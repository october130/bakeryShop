package com.cake.platform.cake.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CakeAddDTO {
        private Long bakeryId;        // 所属店铺
        private Long categoryId;      // 分类
        private String name;          // 名称
        private String image;         // 图片URL
        private Integer price;        // 价格
        private String description;   // 描述
        private Integer customizable; // 是否可定制 0/1
}
