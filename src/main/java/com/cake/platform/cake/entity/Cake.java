package com.cake.platform.cake.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("cake")
public class Cake {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long bakeryId;
    private Long categoryId;
    private String name;
    private String image;
    private Integer price;
    private String description;
    private Integer customizable;
    private Integer status;
    private Integer sold;

    @TableLogic // 逻辑删除
    private Integer deleted;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

}
