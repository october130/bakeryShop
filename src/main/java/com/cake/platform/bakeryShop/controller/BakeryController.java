package com.cake.platform.bakeryShop.controller;

import com.cake.platform.bakeryShop.service.BakeryService;
import com.cake.platform.bakeryShop.vo.BakeryVO;
import com.cake.platform.bakeryShop.vo.CategoryVO;
import com.cake.platform.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bakery")
@Tag(name = "烘焙店接口")
public class BakeryController {

    @Resource
    private BakeryService bakeryService;

    @GetMapping("/categories")
    @Operation(summary = "获取蛋糕分类列表")
    public Result<List<CategoryVO>> listCategories() {
        return bakeryService.listCategories();
    }

    @GetMapping("/list")
    @Operation(summary = "获取烘焙店列表（支持 LBS 距离排序）")
    public Result<List<BakeryVO>> listBakeries(
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam(required = false) Integer categoryId) {
        return bakeryService.listBakeries(latitude, longitude, categoryId);
    }

    @GetMapping("/detail/{id}")
    @Operation(summary = "获取烘焙店详情")
    public Result<BakeryVO> getBakeryDetail(@PathVariable Long id) {
        return bakeryService.getBakeryDetail(id);
    }
}
