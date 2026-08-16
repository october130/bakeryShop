package com.cake.platform.review.service;

import com.cake.platform.common.result.Result;
import com.cake.platform.review.dto.ReviewDTO;
import com.cake.platform.review.vo.ReviewVO;

import java.util.List;

public interface ReviewService {
    Result createReview(ReviewDTO reviewDTO);

    Result replyReview(Long reviewId, String replyContent);

    Result<List<ReviewVO>> getReviewsByBakery(Long bakeryId, Integer page, Integer size);

    Result<List<ReviewVO>> getReviewsByCake(Long cakeId, Integer page, Integer size);

    Result<ReviewVO> getReviewByOrder(Long orderId);

    Result hideReview(Long reviewId);
}
