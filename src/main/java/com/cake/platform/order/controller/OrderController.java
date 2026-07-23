package com.cake.platform.order.controller;

import com.cake.platform.common.result.Result;
import com.cake.platform.order.dto.OrderDTO;
import com.cake.platform.order.service.orderService;
import com.cake.platform.order.vo.OrderVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
