package com.cake.platform.review.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewVO {
    private Long id;
    private Long userId;
    private Long orderId;
    private Long bakeryId;
    private Long cakeId;
    private Integer score;
    private String content;
    private String images;
    private String replyContent;
    private LocalDateTime replyTime;
    private LocalDateTime createTime;
}
