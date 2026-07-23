package com.cake.platform.cake.controller;

import com.cake.platform.cake.service.CakeService;
import com.cake.platform.cake.vo.CakeVO;
import com.cake.platform.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/cake")
@Slf4j
@ApiResponse(description = "蛋糕业务接口")
public class CakeController {
    @Resource
    private CakeService cakeService;
    @GetMapping("/list/{bakeryId}")
    @Operation (summary = "获取蛋糕列表")
    public Result<List<CakeVO>> listCakes(@PathVariable Long bakeryId) {
        log.info("获取蛋糕列表: {}", bakeryId);
        return  cakeService.listCakes(bakeryId);
    }

    @GetMapping("/detail/{cakeId}")
    @Operation (summary = "获取蛋糕详情")
    public Result<CakeVO> getCakeDetail(@PathVariable Long cakeId) {
        log.info("获取蛋糕详情: {}", cakeId);
        return cakeService.getCakeDetail(cakeId);
    }
    @GetMapping("/category/{categoryId}")
    @Operation (summary = "获取分类下的蛋糕列表")
    public Result<List<CakeVO>> listCakesByCategory(@PathVariable Integer categoryId) {
        log.info("获取分类下的蛋糕列表: {}", categoryId);
        return cakeService.listCakesByCategory(categoryId);
    }
}
