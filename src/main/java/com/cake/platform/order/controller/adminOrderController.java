package com.cake.platform.order.controller;

import com.cake.platform.common.result.Result;
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
@Tag(name = "订单管理接口")
@Slf4j
public class adminOrderController {
    @Resource
    private orderService orderService;
    @GetMapping("/admin/list")
    public Result<List<OrderVO>> listOrder() {
        log.info("商家获取用户订单列表接口");
        return  orderService.listAdminOrder();
    }
    @PutMapping("/admin/{orderId}/accept")
    @Operation(summary = "商家接单（1已支付 → 2制作中）")
    public Result acceptOrder(@PathVariable Long orderId) {
        return orderService.acceptOrder(orderId);
    }

    @PutMapping("/admin/{orderId}/deliver")
    @Operation(summary = "商家发货（2制作中 → 3配送中）")
    public Result deliverOrder(@PathVariable Long orderId) {
        return orderService.deliverOrder(orderId);
    }

    @PutMapping("/admin/{orderId}/complete")
    @Operation(summary = "商家完成（3配送中 → 4已完成）")
    public Result completeOrder(@PathVariable Long orderId) {
        return orderService.completeOrder(orderId);
    }

}
