package com.cake.platform.bakeryShop.controller;

import com.cake.platform.bakeryShop.dto.CategoryDTO;
import com.cake.platform.bakeryShop.entity.Category;
import com.cake.platform.bakeryShop.service.AdminCategoryService;
import com.cake.platform.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/category")
@Tag(name = "蛋糕分类管理接口")
@Slf4j
public class AdminCategoryController {
    @Resource
    private AdminCategoryService adminCategoryService;

    @PostMapping
    @Operation(summary = "新增分类")
    public Result addCategory(@RequestBody CategoryDTO categoryDTO) {
        log.info("新增分类: {}", categoryDTO.getName());
        return adminCategoryService.addCategory(categoryDTO);
    }

    @GetMapping("/list")
    @Operation(summary = "分类列表（含禁用的）")
    public Result<List<Category>> listCategory() {
        log.info("查询分类列表");
        return adminCategoryService.listCategory();
    }

    @PutMapping("/{id}")
    @Operation(summary = "修改分类")
    public Result updateCategory(@PathVariable Long id, @RequestBody CategoryDTO categoryDTO) {
        log.info("修改分类: {}", id);
        categoryDTO.setId(id);
        return adminCategoryService.updateCategory(categoryDTO);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除分类")
    public Result deleteCategory(@PathVariable Long id) {
        log.info("删除分类: {}", id);
        return adminCategoryService.deleteCategory(id);
    }
}
