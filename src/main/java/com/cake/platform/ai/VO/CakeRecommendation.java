package com.cake.platform.ai.VO;

import com.cake.platform.cake.entity.Cake;
import lombok.Data;

import java.util.List;

@Data
public class CakeRecommendation {
    private List<Cake> recommendations;//推荐的蛋糕列表
    private String reason;//推荐理由
    private String deliverMessage;//配送信息
}
