package com.cake.platform.ai.controller;

import com.cake.platform.ai.VO.CakeRecommendation;
import com.cake.platform.ai.service.AiService;
import com.cake.platform.common.result.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "Ai蛋糕推荐")
@RestController
@RequestMapping("/ai")
public class AiCommendController {
    @Resource
    private AiService aiService;
    @GetMapping ("/recommend")
    public Result<CakeRecommendation> recommend(
            @RequestParam String userMsg,
            @RequestParam(defaultValue = "default")
            String sessionId
    ){
        Result<CakeRecommendation> cakeRecommendationResult = aiService.CakeRecommend(userMsg, sessionId);
        log.info("用户输入：{},现在立即启动AI服务", userMsg);
        return cakeRecommendationResult;
    }


}
