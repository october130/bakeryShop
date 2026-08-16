package com.cake.platform.order.controller;

import com.cake.platform.common.result.Result;
import com.cake.platform.order.dto.OrderDTO;
import com.cake.platform.order.service.orderService;
import com.cake.platform.order.vo.OrderVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
@Tag(name = "订单业务接口")
@Slf4j
public class orderController {
    @Resource
    private orderService orderService;

    @PostMapping("/checkout")
    @Operation(summary = "订单创建接口")
    public Result<OrderVO> creatCheckout(@RequestBody OrderDTO orderDTO) {
        return orderService.createOrder(orderDTO);
    }
    @PutMapping("/pay/{orderId}")
    @Operation(summary = "订单支付接口")
    public Result payOrder(@PathVariable Long orderId) {
        log.info("订单支付接口");
        return orderService.payOrder(orderId);

    }
    @GetMapping("/list")
    @Operation(summary = "订单列表接口")
    public Result<List<OrderVO>>  listOrder() {
        log.info("订单列表接口");
        return orderService.listOrder();
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "订单详情接口")
    public Result<OrderVO> getOrderDetail(@PathVariable Long orderId) {
        return orderService.getOrderDetail(orderId);
    }

    @PutMapping("/cancel/{orderId}")
    @Operation(summary = "取消订单接口")
    public Result cancelOrder(@PathVariable Long orderId) {
        return orderService.cancelOrder(orderId);
    }


}
