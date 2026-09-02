package com.cake.platform.cake.controller;

import com.cake.platform.cake.dto.CakeAddDTO;
import com.cake.platform.cake.service.AdminCakeService;
import com.cake.platform.cake.vo.CakeVO;
import com.cake.platform.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/cake")
@Slf4j
@ApiResponse(description = "蛋糕业务接口")
public class AdminCakeController {
    @Resource
    private AdminCakeService adminCakeService;
    @GetMapping("/list/{bakeryId}")
    @Operation(summary = "获取蛋糕列表")
    public Result<List<CakeVO>> listCakes(@PathVariable Long bakeryId) {
        log.info("获取蛋糕列表: {}", bakeryId);
        return  adminCakeService.listCakes(bakeryId);
    }

    @GetMapping("/detail/{cakeId}")
    @Operation (summary = "获取蛋糕详情")
    public Result<CakeVO> getCakeDetail(@PathVariable Long cakeId) {
        log.info("获取蛋糕详情: {}", cakeId);
        return adminCakeService.getCakeDetail(cakeId);
    }
    @GetMapping("/category/{categoryId}")
    @Operation (summary = "获取分类下的蛋糕列表")
    public Result<List<CakeVO>> listCakesByCategory(@PathVariable Integer categoryId) {
        log.info("获取分类下的蛋糕列表: {}", categoryId);
        return adminCakeService.listCakesByCategory(categoryId);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "更新蛋糕状态")
    public Result updateCakeStatus(@PathVariable Long id, @RequestParam Integer status) {
        log.info("更新蛋糕状态: {}", id);
        return adminCakeService.updateCakeStatus(id, status);
    }
    @PostMapping
    @Operation(summary = "新增蛋糕")
    public Result addCake(@RequestBody CakeAddDTO cakeAddDTO) {
        log.info("新增蛋糕: {}", cakeAddDTO);
        return adminCakeService.addCake(cakeAddDTO);
    }
    @DeleteMapping("/{id}")
    @Operation(summary = "删除蛋糕")
    public Result deleteCake(@PathVariable Long id) {
        log.info("删除蛋糕: {}", id);
        return adminCakeService.deleteCake(id);
    }

}
