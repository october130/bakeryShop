package com.cake.platform.review.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cake.platform.common.exception.BusinessException;
import com.cake.platform.common.result.Result;
import com.cake.platform.common.utils.UserIdUtils;
import com.cake.platform.order.entity.Order;
import com.cake.platform.order.mapper.OrderMapper;
import com.cake.platform.review.dto.ReviewDTO;
import com.cake.platform.review.entity.Review;
import com.cake.platform.review.mapper.ReviewMapper;
import com.cake.platform.review.service.ReviewService;
import com.cake.platform.review.vo.ReviewVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    @Resource
    private ReviewMapper reviewMapper;

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    // Redis key 前缀
    private static final String REVIEW_LOCK_KEY = "review:lock:order:";
    private static final String RATING_CACHE_KEY = "rating:bakery:";

    @Override
    @Transactional
    public Result createReview(ReviewDTO reviewDTO) {
        Long userId = UserIdUtils.getUserId();
        Long orderId = reviewDTO.getOrderId();

        // 1. 检查订单是否存在
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 2. 检查是否是自己的订单
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权评价");
        }

        // 3. 检查订单状态是否为已完成(4)
        if (order.getStatus() != 4) {
            throw new BusinessException("只有已完成的订单才能评价");
        }

        // 4. 检查是否已评价过（防重复）
        Long existCount = reviewMapper.selectCount(
                new LambdaQueryWrapper<Review>().eq(Review::getOrderId, orderId)
        );
        if (existCount > 0) {
            throw new BusinessException("该订单已评价");
        }

        // 5. 创建评价
        Review review = Review.builder()
                .userId(userId)
                .orderId(orderId)
                .bakeryId(order.getBakeryId())
                .cakeId(reviewDTO.getCakeId())
                .score(reviewDTO.getScore())
                .content(reviewDTO.getContent())
                .images(reviewDTO.getImages())
                .status(1)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        reviewMapper.insert(review);

        // 6. 删除该店铺评分缓存（下次查询时重新计算）
        redisTemplate.delete(RATING_CACHE_KEY + order.getBakeryId());

        return Result.success("评价成功");
    }

    @Override
    @Transactional
    public Result replyReview(Long reviewId, String replyContent) {
        Review review = reviewMapper.selectById(reviewId);
        if (review == null) {
            throw new BusinessException("评价不存在");
        }
        if (review.getReplyContent() != null) {
            throw new BusinessException("该评价已回复");
        }

        review.setReplyContent(replyContent);
        review.setReplyTime(LocalDateTime.now());
        reviewMapper.updateById(review);

        return Result.success("回复成功");
    }

    @Override
    public Result<List<ReviewVO>> getReviewsByBakery(Long bakeryId, Integer page, Integer size) {
        Page<Review> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getBakeryId, bakeryId)
               .eq(Review::getStatus, 1)
               .orderByDesc(Review::getCreateTime);

        Page<Review> reviewPage = reviewMapper.selectPage(pageParam, wrapper);

        List<ReviewVO> voList = reviewPage.getRecords().stream()
                .map(this::convertToVO)
                .toList();

        return Result.success(voList);
    }

    @Override
    public Result<List<ReviewVO>> getReviewsByCake(Long cakeId, Integer page, Integer size) {
        Page<Review> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getCakeId, cakeId)
               .eq(Review::getStatus, 1)
               .orderByDesc(Review::getCreateTime);

        Page<Review> reviewPage = reviewMapper.selectPage(pageParam, wrapper);

        List<ReviewVO> voList = reviewPage.getRecords().stream()
                .map(this::convertToVO)
                .toList();

        return Result.success(voList);
    }

    @Override
    public Result<ReviewVO> getReviewByOrder(Long orderId) {
        Review review = reviewMapper.selectOne(
                new LambdaQueryWrapper<Review>()
                        .eq(Review::getOrderId, orderId)
                        .eq(Review::getUserId, UserIdUtils.getUserId())
        );
        if (review == null) {
            throw new BusinessException("评价不存在");
        }
        return Result.success(convertToVO(review));
    }

    @Override
    public Result hideReview(Long reviewId) {
        Review review = reviewMapper.selectById(reviewId);
        if (review == null) {
            throw new BusinessException("评价不存在");
        }
        review.setStatus(0);
        reviewMapper.updateById(review);

        // 删除评分缓存
        redisTemplate.delete(RATING_CACHE_KEY + review.getBakeryId());

        return Result.success("隐藏成功");
    }

    private ReviewVO convertToVO(Review review) {
        return ReviewVO.builder()
                .id(review.getId())
                .userId(review.getUserId())
                .orderId(review.getOrderId())
                .bakeryId(review.getBakeryId())
                .cakeId(review.getCakeId())
                .score(review.getScore())
                .content(review.getContent())
                .images(review.getImages())
                .replyContent(review.getReplyContent())
                .replyTime(review.getReplyTime())
                .createTime(review.getCreateTime())
                .build();
    }
}
