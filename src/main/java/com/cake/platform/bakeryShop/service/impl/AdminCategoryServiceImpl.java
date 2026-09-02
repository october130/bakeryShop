package com.cake.platform.bakeryShop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cake.platform.bakeryShop.dto.CategoryDTO;
import com.cake.platform.bakeryShop.entity.Category;
import com.cake.platform.bakeryShop.mapper.CategoryMapper;
import com.cake.platform.bakeryShop.service.AdminCategoryService;
import com.cake.platform.cake.entity.Cake;
import com.cake.platform.cake.mapper.CakeMapper;
import com.cake.platform.common.result.Result;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class AdminCategoryServiceImpl implements AdminCategoryService {
    @Resource
    private CategoryMapper categoryMapper;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private CakeMapper cakeMapper;

    @Override
    public Result addCategory(CategoryDTO categoryDTO) {
        if (categoryDTO.getName() == null){
            return Result.error("分类名称不能为空");
        }
        Long count = categoryMapper.selectCount(new QueryWrapper<Category>()
                .eq("name", categoryDTO.getName())
        );
        if (count > 0) {
            return Result.error("分类已存在");
        }
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);
        categoryMapper.insert(category);
        redisTemplate.delete("bakery:categories");
        log.info("新增分类成功，ID: {}", category.getId());
        return Result.success("分类添加成功");
    }

    @Override
    public Result<List<Category>> listCategory() {
        List<Category> categories = categoryMapper.selectList(
                new QueryWrapper<Category>().orderByAsc("sort"));
        log.info("查询分类列表成功");
        return Result.success(categories);
    }

    @Override
    public Result updateCategory(CategoryDTO categoryDTO) {
        if (categoryDTO.getId() == null) {
            return Result.error("分类id不能为空");
        }
        // 改了名字才需要查重，且要排除自己
        if (categoryDTO.getName() != null) {
            Long count = categoryMapper.selectCount(new QueryWrapper<Category>()
                    .eq("name", categoryDTO.getName())
                    .ne("id", categoryDTO.getId()));
            if (count > 0) {
                return Result.error("分类已存在");
            }
        }
        Category category = categoryMapper.selectById(categoryDTO.getId());
        if (category == null) {
            return Result.error("分类不存在");
        }
        // 局部更新：传了哪个字段改哪个
        if (categoryDTO.getName() != null) category.setName(categoryDTO.getName());
        if (categoryDTO.getIcon() != null) category.setIcon(categoryDTO.getIcon());
        if (categoryDTO.getSort() != null) category.setSort(categoryDTO.getSort());
        if (categoryDTO.getStatus() != null) category.setStatus(categoryDTO.getStatus());
        categoryMapper.updateById(category);
        redisTemplate.delete("bakery:categories");
        log.info("编辑分类成功，ID: {}", category.getId());
        return Result.success("分类修改成功");
    }

    @Override
    public Result deleteCategory(Long id) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            return Result.error("分类不存在");
        }
        // 该分类下还有蛋糕则不允许删除，防止商品悬空
        Long cakeCount = cakeMapper.selectCount(new QueryWrapper<Cake>().eq("category_id", id));
        if (cakeCount > 0) {
            return Result.error("该分类下还有蛋糕，无法删除");
        }
        categoryMapper.deleteById(id);
        redisTemplate.delete("bakery:categories");
        log.info("删除分类成功，ID: {}", id);
        return Result.success("分类删除成功");
    }
}
