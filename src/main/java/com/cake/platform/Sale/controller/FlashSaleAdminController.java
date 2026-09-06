package com.cake.platform.Sale.controller;

import com.cake.platform.Sale.VO.FlashSaleVO;
import com.cake.platform.Sale.dto.SaleDTO;
import com.cake.platform.Sale.service.FlashSaleAdminService;
import com.cake.platform.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/flashSale")
@Slf4j
@ApiResponse(description = "秒杀蛋糕业务管理员端开放模块")
public class FlashSaleAdminController {
    @Resource
    private FlashSaleAdminService flashSaleAdminService;
    @PostMapping
    @Operation(description = "创建秒杀活动")
    public Result createFlashSale(@RequestBody SaleDTO saleDTO) {
        return flashSaleAdminService.createFlashSale(saleDTO);

    }
    @GetMapping("/list")
    @Operation(description = "获取秒杀活动列表")
    public Result<List<FlashSaleVO>> getFlashSaleList() {
        List<FlashSaleVO> flashSaleList = flashSaleAdminService.getFlashSaleList();
        return Result.success(flashSaleList);
    }
    @PutMapping("/{id}/status")
    @Operation(description = "修改秒杀活动状态")
    public Result updateFlashSaleStatus(@PathVariable Long id, @RequestParam("status") Integer status) {
        flashSaleAdminService.updateFlashSaleStatus(id, status);
        return Result.success("修改秒杀活动状态成功");
    }
}
