package com.cake.platform.cake.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CakeVO {
    private Long id;// 蛋糕ID
    private Long bakeryId;//所属店ID
    private Long categoryId; // 分类ID
    private String name; // 蛋糕名称
    private String image; // 蛋糕图片
    private Integer price; // 蛋糕价格
    private String description; // 蛋糕描述
    private Integer customizable; // 是否可定制
    private Integer status; // 蛋糕状态
    private Integer sold; // 蛋糕销量
}
