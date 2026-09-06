package com.cake.platform.Sale.controller;

import com.cake.platform.Sale.VO.FlashSaleOrderVO;
import com.cake.platform.Sale.VO.FlashSaleVO;
import com.cake.platform.Sale.service.FlashSaleService;
import com.cake.platform.common.result.Result;
import com.cake.platform.common.utils.UserIdUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flashSale")
@Slf4j
@ApiResponse(description = "秒杀蛋糕业务接口")
public class FlashSaleController {
    @Resource
    private FlashSaleService flashSaleService;

    @GetMapping("/list")
    @Operation(summary = "获取进行中的限时抢购列表")
    public Result<List<FlashSaleVO>> getActiveFlashSaleList() {
        log.info("获取进行中的限时抢购列表");
        return flashSaleService.getActiveFlashSaleList();
    }

    @GetMapping("/detail/{id}")
   @Operation(summary = "获取秒杀蛋糕详情")
    public Result<FlashSaleVO> getFlashSaleDetail(@PathVariable Long id){
        log.info("获取秒杀蛋糕详情: {}", id);
        return flashSaleService.getFlashSaleDetail(id);
    }

@PostMapping("/{id}/seckill")
    @Operation(summary = "秒杀蛋糕抢购")
    public Result<String> seckill(@PathVariable Long id){
        log.info("秒杀蛋糕抢购: {}", id);
    Long userId = UserIdUtils.getUserId();
    return flashSaleService.seckill(id, userId);
}

    @GetMapping("/order/list")
    @Operation(summary = "获取我的秒杀订单列表")
    public Result<List<FlashSaleOrderVO>> getMyFlashSaleOrders() {
        Long userId = UserIdUtils.getUserId();
        log.info("获取用户 {} 的秒杀订单列表", userId);
        return flashSaleService.getUserFlashSaleOrders(userId);
    }

    @PostMapping("/order/pay/{orderId}")
    @Operation(summary = "支付秒杀订单")
    public Result<String> payFlashSaleOrder(@PathVariable Long orderId) {
        Long userId = UserIdUtils.getUserId();
        log.info("用户 {} 支付秒杀订单 {}", userId, orderId);
        return flashSaleService.payFlashSaleOrder(orderId, userId);
    }

}
