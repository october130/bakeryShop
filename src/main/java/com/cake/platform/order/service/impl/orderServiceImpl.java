package com.cake.platform.order.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cake.platform.common.config.RabbitMQConfig;
import com.cake.platform.common.exception.BusinessException;
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
import com.cake.platform.shoppingcar.VO.CartItemVO;
import com.cake.platform.shoppingcar.VO.CheckoutVO;
import com.cake.platform.shoppingcar.service.CartService;
import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Service
public class orderServiceImpl  implements orderService {
    @Resource
  private RabbitTemplate rabbitTemplate;
    @Resource
    private CartService cartService;
    @Resource
    private OrderMapper orderMapper;
    @Resource
    private OrderDetailMapper orderDetailMapper;

    @Override
    @Transactional
    public Result<OrderVO> createOrder(OrderDTO orderDTO) {
        Result<CheckoutVO> checkVO = cartService.checkout();
        CheckoutVO checkoutVO = checkVO.getData();
        Integer totalPrice = checkoutVO.getTotalPrice();
        List<CartItemVO> cartItems = checkoutVO.getItems();
        String  orderId = "order_" + System.currentTimeMillis();//雪花 id生成订单编号

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
                .status( 0)
                .createTime(order.getCreateTime())
                .items(orderItems)
                .build();
        rabbitTemplate.convertAndSend(RabbitMQConfig.DELAY_EXCHANGE,  RabbitMQConfig.DELAY_ROUTING_KEY, order.getId());//发送消息给延迟队列



        return Result.success("下单成功", orderVO);
    }

    @Override
    @Transactional
    public Result payOrder(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (order.getUserId() != UserIdUtils.getUserId()) {
            throw new  BusinessException(403,"无权操作");
        }
        if (order.getStatus() != 0) {
            throw new  BusinessException("订单已支付");
        }
        order.setStatus(1);
        order.setPayTime(LocalDateTime.now());
        orderMapper.updateById(order);
        cartService.clearCart();
        return Result.success("支付成功");
    }

    @Override
    public Result<List<OrderVO>> listOrder() {
        Long userId = UserIdUtils.getUserId();
         List<Order> orders = orderMapper.selectList(
                 new QueryWrapper<Order>().eq("user_id", userId)
         );
         List<OrderVO> orderVOS = orders.stream().map(order -> OrderVO.builder()
                 .id(order.getId())
                 .orderNo(order.getOrderNo())
                 .totalPrice(order.getTotalAmount())
                 .status(order.getStatus())
                 .createTime(order.getCreateTime())
                 .build()).toList();
         return Result.success(orderVOS);

    }

    @Override
    public Result<OrderVO> getOrderDetail(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getUserId().equals(UserIdUtils.getUserId())) {
            throw new BusinessException(403, "无权查看");
        }
        List<OrderDetail> details = orderDetailMapper.selectList(
                new QueryWrapper<OrderDetail>().eq("order_id", orderId)
        );
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
                .status(order.getStatus())
                .createTime(order.getCreateTime())
                .items(items)
                .build();
        return Result.success(vo);
    }

    @Override
    @Transactional
    public Result cancelOrder(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getUserId().equals(UserIdUtils.getUserId())) {
            throw new BusinessException(403, "无权操作");
        }
        // 制作中及之后不可取消
        if (order.getStatus() >= 2) {
            throw new BusinessException("订单已开始制作，无法取消");
        }
        order.setStatus(5);
        orderMapper.updateById(order);
        return Result.success("取消成功");
    }

    @Override
    @Transactional
    public Result acceptOrder(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() != 1) {
            throw new BusinessException("只有已支付的订单才能接单");
        }
        order.setStatus(2);
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);
        return Result.success("接单成功");
    }

    @Override
    @Transactional
    public Result deliverOrder(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() != 2) {
            throw new BusinessException("只有制作中的订单才能发货");
        }
        order.setStatus(3);
        order.setDeliverStartTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);
        return Result.success("发货成功");
    }

    @Override
    @Transactional
    public Result completeOrder(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() != 3) {
            throw new BusinessException("只有配送中的订单才能确认完成");
        }
        order.setStatus(4);
        order.setUpdateTime(LocalDateTime.now());
        order.setFinishTime(LocalDateTime.now());
        orderMapper.updateById(order);
        return Result.success("订单已完成");
    }
}
