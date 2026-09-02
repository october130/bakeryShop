package com.cake.platform.bakeryShop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {
    private Long id;
    private String name;//分类名
    private String icon;//图标URL
    private Integer sort;//排序
    private Integer status;//状态(0-禁用 1-启用)
}
