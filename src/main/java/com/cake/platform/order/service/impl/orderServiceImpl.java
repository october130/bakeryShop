package com.cake.platform.order.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cake.platform.common.result.Result;
import com.cake.platform.common.utils.UserIdUtils;
import com.cake.platform.order.dto.OrderDTO;
import com.cake.platform.order.entity.Order;
import com.cake.platform.order.entity.OrderDetail;
import com.cake.platform.order.mapper.OrderDetailMapper;
import com.cake.platform.order.mapper.OrderMapper;
import com.cake.platform.order.service.orderService;
import com.cake.platform.order.vo.OrderItemVO;
import com.cake.platform.order.vo.OrderVO;
import com.cake.platform.shoppingCart.VO.CartItemVO;
import com.cake.platform.shoppingCart.VO.CheckoutVO;
import com.cake.platform.shoppingCart.service.CartService;
import com.cake.platform.bakeryShop.entity.Bakery;
import com.cake.platform.bakeryShop.mapper.BakeryMapper;
import com.cake.platform.common.config.RabbitMQConfig;
import com.cake.platform.common.exception.BusinessException;
import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class orderServiceImpl  implements orderService {
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private CartService cartService;
    @Resource
    private OrderMapper orderMapper;
    @Resource
    private OrderDetailMapper orderDetailMapper;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private BakeryMapper bakeryMapper;

    @Override
    @Transactional
    public Result<OrderVO> createOrder(OrderDTO orderDTO) {

        Result<CheckoutVO> checkVO = cartService.checkout();// 调用购物车服务的checkout方法，获取结算信息
        if (checkVO.getCode() != 200) {
            return Result.error("购物车为空");
        }
        CheckoutVO checkoutVO = checkVO.getData();
        Integer totalPrice = checkoutVO.getTotalPrice();
        List<CartItemVO> cartItems = checkoutVO.getItems();
        String  orderId = "order_" + UUID.randomUUID().toString(); // UUid生成订单编号

        // 从购物车第一项获取bakeryId
        Long bakeryId = cartItems.get(0).getBakeryId();

        Order order = Order.builder()
                .orderNo(orderId)
                .userId(UserIdUtils.getUserId())
                .bakeryId(bakeryId)
                .totalAmount(totalPrice)
                .address(orderDTO.getAddress())
                .phone(orderDTO.getPhone())
                .remark(orderDTO.getRemark())
                .deliverTime(orderDTO.getDeliverTime())
                .status(0)
                .updateTime(LocalDateTime.now())
                .build();

        // 插入订单表
        orderMapper.insert(order);

        // 遍历购物车，插入订单详情表
        List<OrderItemVO> orderItems = new ArrayList<>();
        for (CartItemVO item : cartItems) {
            OrderDetail detail = OrderDetail.builder()
                    .orderId(order.getId())
                    .cakeId(item.getCakeId())
                    .cakeName(item.getCakeName())
                    .price(item.getPrice())
                    .quantity(item.getAmount())
                    .customInfo("")
                    .build();
            orderDetailMapper.insert(detail);

            orderItems.add(OrderItemVO.builder()
                    .cakeId(item.getCakeId())
                    .cakeName(item.getCakeName())
                    .price(item.getPrice())
                    .amount(item.getAmount())
                    .customInfo("")
                    .build());
        }

        // 返回订单信息（购物车不清空，等支付成功后再清）
        OrderVO orderVO = OrderVO.builder()
                .id(order.getId())
                .orderNo(order.getOrderNo())
                .totalPrice(totalPrice)
                .status("待支付")
                .createTime(order.getCreateTime())
                .items(orderItems)
                .build();

        // 发送延迟消息：15分钟未支付自动取消（超时走死信队列）
        Map<String, Object> delayMsg = new HashMap<>();
        delayMsg.put("orderId", order.getId());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.DELAY_EXCHANGE,
                RabbitMQConfig.DELAY_ROUTING_KEY,
                delayMsg);

        return Result.success("下单成功", orderVO);
    }

    @Override
    @Transactional
    public Result<OrderVO> payOrder(OrderDTO orderDTO) {

        Order order = orderMapper.selectOne(
                new QueryWrapper<Order>()
                        .eq("order_no", orderDTO.getOrderNo())
        );
        if (order==null){
            return Result.error("订单不存在");
        }
        if (!order.getUserId().equals(orderDTO.getUserId())){
            return Result.error("无权限支付该订单");}


        if (order.getStatus()== 1){
            return Result.success("订单已支付", null);
        }
        if (order.getStatus() == 5) {
            return Result.error("订单已取消，无法支付");
        }
        if (!order.getTotalAmount().equals(orderDTO.getTotalAmount())){
            return Result.error("支付金额异常，请检查");
        }
        order.setStatus(1);
        order.setPayTime(LocalDateTime.now());
        order.setPayChannel("虚拟账户余额支付");
        String translation_id = order.getOrderNo();
        order.setTranslationId(translation_id);
        orderMapper.updateById(order);
        cartService.clearCart(); // 清空购物车
        return Result.success("支付成功", null);
    }

    /* ---------- 用户端：订单查询/详情/取消 ---------- */

    @Override
    public Result<List<OrderVO>> listOrder() {
        Long userId = UserIdUtils.getUserId();
        List<Order> orders = orderMapper.selectList(
                new QueryWrapper<Order>().eq("user_id", userId).orderByDesc("create_time"));
        List<OrderVO> orderVOS = orders.stream().map(order -> OrderVO.builder()
                .id(order.getId())
                .orderNo(order.getOrderNo())
                .totalPrice(order.getTotalAmount())
                .status(statusText(order.getStatus()))
                .createTime(order.getCreateTime())
                .build()).toList();
        return Result.success(orderVOS);
    }

    @Override
    public Result<OrderVO> getOrderDetail(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            return Result.error("订单不存在");
        }
        if (!order.getUserId().equals(UserIdUtils.getUserId())) {
            return Result.error("无权查看该订单");
        }
        List<OrderDetail> details = orderDetailMapper.selectList(
                new QueryWrapper<OrderDetail>().eq("order_id", orderId));
        List<OrderItemVO> items = details.stream().map(d -> OrderItemVO.builder()
                .cakeId(d.getCakeId())
                .cakeName(d.getCakeName())
                .price(d.getPrice())
                .amount(d.getQuantity())
                .customInfo(d.getCustomInfo())
                .build()).toList();
        OrderVO vo = OrderVO.builder()
                .id(order.getId())
                .orderNo(order.getOrderNo())
                .totalPrice(order.getTotalAmount())
                .status(statusText(order.getStatus()))
                .createTime(order.getCreateTime())
                .items(items)
                .build();
        return Result.success(vo);
    }

    @Override
    public Result cancelOrder(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            return Result.error("订单不存在");
        }
        if (!order.getUserId().equals(UserIdUtils.getUserId())) {
            return Result.error("无权操作该订单");
        }
        // 制作中及之后不可取消
        if (order.getStatus() >= 2) {
            return Result.error("订单已开始制作，无法取消");
        }
        order.setStatus(5);
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);
        return Result.success("取消成功");
    }

    /* ---------- 商家端：订单列表/接单/发货/完成 ---------- */

    @Override
    public Result<List<OrderVO>> listAdminOrder() {
        Bakery bakery = getMyBakery();
        List<Order> orders = orderMapper.selectList(
                new QueryWrapper<Order>().eq("bakery_id", bakery.getId()).orderByDesc("create_time"));
        List<OrderVO> orderVOS = orders.stream().map(order -> OrderVO.builder()
                .id(order.getId())
                .orderNo(order.getOrderNo())
                .totalPrice(order.getTotalAmount())
                .status(statusText(order.getStatus()))
                .createTime(order.getCreateTime())
                .build()).toList();
        return Result.success(orderVOS);
    }

    @Override
    public Result acceptOrder(Long orderId) {
        Order order = requireMyBakeryOrder(orderId);
        if (order.getStatus() != 1) {
            return Result.error("只有已支付的订单才能接单");
        }
        order.setStatus(2);
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);
        return Result.success("接单成功");
    }

    @Override
    public Result deliverOrder(Long orderId) {
        Order order = requireMyBakeryOrder(orderId);
        if (order.getStatus() != 2) {
            return Result.error("只有制作中的订单才能发货");
        }
        order.setStatus(3);
        order.setDeliverStartTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);
        return Result.success("发货成功");
    }

    @Override
    public Result completeOrder(Long orderId) {
        Order order = requireMyBakeryOrder(orderId);
        if (order.getStatus() != 3) {
            return Result.error("只有配送中的订单才能确认完成");
        }
        order.setStatus(4);
        order.setFinishTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);
        return Result.success("订单已完成");
    }

    @Override
    public Result<OrderVO> getOrderAdminDetail(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            return Result.error("订单不存在");
        }
        if (!order.getBakeryId().equals(getMyBakery().getId())){
            return Result.error("无权查看该店铺下订单");
        }
        List<OrderDetail> details = orderDetailMapper.selectList(
                new QueryWrapper<OrderDetail>().eq("order_id", orderId));
        List<OrderItemVO> items = details.stream().map(d -> OrderItemVO.builder()
                .cakeId(d.getCakeId())
                .cakeName(d.getCakeName())
                .price(d.getPrice())
                .amount(d.getQuantity())
                .customInfo(d.getCustomInfo())
                .build()).toList();
        OrderVO vo = OrderVO.builder()
                .id(order.getId())
                .orderNo(order.getOrderNo())
                .totalPrice(order.getTotalAmount())
                .status(statusText(order.getStatus()))
                .createTime(order.getCreateTime())
                .items(items)
                .build();
        return Result.success(vo);
    }

    /** 获取当前登录商家（店铺主）的店铺 */
    private Bakery getMyBakery() {
        Bakery bakery = bakeryMapper.selectOne(
                new QueryWrapper<Bakery>().eq("owner_id", UserIdUtils.getUserId()));
        if (bakery == null) {
            throw new BusinessException("商家不存在,您还未开通商铺");
        }
        return bakery;
    }

    /** 校验订单属于当前商家的店铺，返回订单 */
    private Order requireMyBakeryOrder(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getBakeryId().equals(getMyBakery().getId())) {
            throw new BusinessException("无权操作该店铺订单");
        }
        return order;
    }

    /** DB 数字状态 → 中文文案（与 OrderVO.status 的 String 类型一致） */
    private String statusText(Integer status) {
        if (status == null) {
            return "";
        }
        return switch (status) {
            case 0 -> "待支付";
            case 1 -> "已支付";
            case 2 -> "制作中";
            case 3 -> "配送中";
            case 4 -> "已完成";
            case 5 -> "已取消";
            default -> String.valueOf(status);
        };
    }
}
