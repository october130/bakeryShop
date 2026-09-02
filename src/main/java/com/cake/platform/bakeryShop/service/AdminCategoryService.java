package com.cake.platform.bakeryShop.service;

import com.cake.platform.bakeryShop.dto.CategoryDTO;
import com.cake.platform.bakeryShop.entity.Category;
import com.cake.platform.common.result.Result;

import java.util.List;

public interface AdminCategoryService {
    Result addCategory(CategoryDTO categoryDTO);

    Result<List<Category>> listCategory();

    Result updateCategory(CategoryDTO categoryDTO);

    Result deleteCategory(Long id);
}
