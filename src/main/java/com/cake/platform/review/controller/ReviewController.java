package com.cake.platform.review.controller;

import com.cake.platform.common.result.Result;
import com.cake.platform.review.dto.ReviewDTO;
import com.cake.platform.review.service.ReviewService;
import com.cake.platform.review.vo.ReviewVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/review")
@Tag(name = "评价模块接口")
@Slf4j
public class ReviewController {

    @Resource
    private ReviewService reviewService;

    @PostMapping("/create")
    @Operation(summary = "用户提交评价")
    public Result createReview(@Valid @RequestBody ReviewDTO reviewDTO) {
        return reviewService.createReview(reviewDTO);
    }

    @PutMapping("/reply/{reviewId}")
    @Operation(summary = "商家回复评价")
    public Result replyReview(@PathVariable Long reviewId,
                              @RequestParam String replyContent) {
        return reviewService.replyReview(reviewId, replyContent);
    }

    @GetMapping("/bakery/{bakeryId}")
    @Operation(summary = "查看店铺评价列表（分页）")
    public Result<List<ReviewVO>> getReviewsByBakery(
            @PathVariable Long bakeryId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return reviewService.getReviewsByBakery(bakeryId, page, size);
    }

    @GetMapping("/cake/{cakeId}")
    @Operation(summary = "查看蛋糕评价列表（分页）")
    public Result<List<ReviewVO>> getReviewsByCake(
            @PathVariable Long cakeId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return reviewService.getReviewsByCake(cakeId, page, size);
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "查看订单评价详情")
    public Result<ReviewVO> getReviewByOrder(@PathVariable Long orderId) {
        return reviewService.getReviewByOrder(orderId);
    }

    @PutMapping("/hide/{reviewId}")
    @Operation(summary = "隐藏评价（管理员）")
    public Result hideReview(@PathVariable Long reviewId) {
        return reviewService.hideReview(reviewId);
    }
}
