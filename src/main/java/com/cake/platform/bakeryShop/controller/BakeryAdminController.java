package com.cake.platform.bakeryShop.controller;

import com.cake.platform.bakeryShop.dto.bakeryShopDTO;
import com.cake.platform.bakeryShop.service.BakeryService;
import com.cake.platform.bakeryShop.vo.BakeryVO;
import com.cake.platform.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/bakery")
@Slf4j
@Tag(name = "商家店铺管理")
public class BakeryAdminController {
    @Resource
    private BakeryService bakeryService;

    @PostMapping
    @Operation(summary = "开通我的店铺")
    public Result addBakery(@RequestBody bakeryShopDTO bakeryShopDTO) {
        log.info("开通店铺: {}", bakeryShopDTO.getName());
        return bakeryService.addBakery(bakeryShopDTO);
    }

    @GetMapping("/my")
    @Operation(summary = "查看我的店铺")
    public Result<BakeryVO> getMyBakery() {
        log.info("获取我的店铺");
        return bakeryService.getMyBakery();
    }

    @PutMapping
    @Operation(summary = "编辑我的店铺")
    public Result updateBakery(@RequestBody bakeryShopDTO bakeryShopDTO) {
        log.info("编辑店铺: {}", bakeryShopDTO.getName());
        return bakeryService.updateBakery(bakeryShopDTO);
    }

    @PutMapping("/status")
    @Operation(summary = "切换营业/歇业")
    public Result updateBakeryStatus(@RequestParam Integer status) {
        log.info("切换店铺营业状态: {}", status);
        return bakeryService.updateBakeryStatus(status);
    }
}
