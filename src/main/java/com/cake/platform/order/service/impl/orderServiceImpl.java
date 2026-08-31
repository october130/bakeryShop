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
import com.cake.platform.shoppingcar.VO.CartItemVO;
import com.cake.platform.shoppingcar.VO.CheckoutVO;
import com.cake.platform.shoppingcar.service.CartService;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
                .status("待支付")
                .createTime(order.getCreateTime())
                .items(orderItems)
                .build();

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
        if (order.getStatus()== 1){
            return Result.success("订单已支付", null);
        }
        if (order.getStatus() == 4) {
            return Result.error("订单已取消，无法支付");
        }
        if (!order.getTotalAmount().equals(orderDTO.getTotalAmount())){
            return Result.error("支付金额异常，请检查");
        }
        order.setStatus(1);
        order.setPayTime(LocalDateTime.now());
        order.setPay_channel("虚拟账户余额支付");
        String translation_id = System.currentTimeMillis()+order.getOrderNo();
        order.setTranslation_id(translation_id);
        orderMapper.updateById(order);
        return Result.success("支付成功", null);
    }
}
