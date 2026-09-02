package com.cake.platform.order.controller;

import com.cake.platform.common.result.Result;
import com.cake.platform.order.dto.OrderDTO;
import com.cake.platform.order.service.orderService;
import com.cake.platform.order.vo.OrderVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/order")
@Tag(name = "订单业务接口")
@Slf4j
public class OrderController {
    @Resource
    private orderService orderService;

    @PostMapping("/checkout")
    @Operation(summary = "订单创建接口")
    public Result<OrderVO> creatCheckout(@RequestBody OrderDTO orderDTO) {
        return orderService.createOrder(orderDTO);
    }
    @PostMapping("/pay")
    @Operation(summary = "订单支付接口")
    public Result<OrderVO> payOrder(@RequestBody OrderDTO orderDTO) {
        return orderService.payOrder(orderDTO);
    }

    @GetMapping("/list")
    @Operation(summary = "我的订单列表")
    public Result<List<OrderVO>> listOrder() {
        return orderService.listOrder();
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "订单详情")
    public Result<OrderVO> getOrderDetail(@PathVariable Long orderId) {
        return orderService.getOrderDetail(orderId);
    }

    @PutMapping("/cancel/{orderId}")
    @Operation(summary = "取消订单")
    public Result cancelOrder(@PathVariable Long orderId) {
        return orderService.cancelOrder(orderId);
    }

}
