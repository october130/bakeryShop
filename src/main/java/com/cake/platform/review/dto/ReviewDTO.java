package com.cake.platform.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewDTO {
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    private Long cakeId;  // 可选，针对某个蛋糕的评价

    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分最低1分")
    @Max(value = 5, message = "评分最高5分")
    private Integer score;

    @NotBlank(message = "评价内容不能为空")
    private String content;

    private String images;  // 图片URL，逗号分隔
}
