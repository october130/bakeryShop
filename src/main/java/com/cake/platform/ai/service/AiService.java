package com.cake.platform.ai.service;
import com.cake.platform.ai.VO.CakeRecommendation;
import com.cake.platform.ai.tool.CakeTools;
import com.cake.platform.cake.entity.Cake;
import com.cake.platform.cake.mapper.CakeMapper;
import com.cake.platform.common.result.Result;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@Slf4j
public class AiService {
@Resource
private ChatModel chatModel;
@Resource
private CakeTools cakeTools;
@Resource
private ChatMemory chatMemory;

public Result< CakeRecommendation> CakeRecommend(String userMsg,String sessionId){
    try {
        String CakeKnowLedge = loadKnowLedgeBase();
        String systemPrompt = new String(
                getClass().getResourceAsStream("/prompt/cake-recommend_system.txt").readAllBytes(),
                StandardCharsets.UTF_8
        );

        systemPrompt = systemPrompt.replace("{cakeContext}",CakeKnowLedge);
        String userPrompt = new String(
                getClass().getResourceAsStream("/prompt/cake-recommend_user.txt").readAllBytes(),
                StandardCharsets.UTF_8
        );
        userPrompt = userPrompt.replace("{userQuestion}", userMsg);


        CakeRecommendation AIResponse = callDeepseek(systemPrompt, userPrompt, sessionId);



        log.info("AI返回结果：{}", AIResponse);
        return  Result.success(AIResponse);
    } catch (IOException e) {
        throw new RuntimeException(e);
    }
}
private CakeRecommendation callDeepseek( String systemPrompt, String userPrompt, String sessionId){
    ChatClient chatClient =  ChatClient.builder(chatModel)
            .defaultSystem(systemPrompt)
            .defaultAdvisors(
                    MessageChatMemoryAdvisor.builder(chatMemory)
                           .build()
            )
            .defaultTools(cakeTools)
            .build();
        CakeRecommendation  aiAnswer = chatClient.prompt()
            .user(userPrompt)
            .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, sessionId))
            .call()
            .entity(CakeRecommendation.class);


    if (aiAnswer == null) {
        throw new RuntimeException("AI评估返回为空");
    }
    return aiAnswer;
}

    private String loadKnowLedgeBase(){//加载静态蛋糕知识库
    StringBuilder sb = new StringBuilder();
        try {
            sb.append( new String(
                    getClass().getResourceAsStream("/knowledge/cake-details.md")
                    .readAllBytes(),
                    StandardCharsets.UTF_8)
                );
            sb.append( new String(
                    getClass().getResourceAsStream("/knowledge/occasion-guide.md")
                    .readAllBytes(),
                    StandardCharsets.UTF_8)
                );
            sb.append( new String(
                    getClass().getResourceAsStream("/knowledge/ordering-faq.md")
                    .readAllBytes(),
                    StandardCharsets.UTF_8)
                );
            log.info("加载静态蛋糕知识库成功");
            return  sb.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

//        String cakeNews = getCakeContext();//调用方法获取蛋糕信息
//        systemPrompt = systemPrompt.replace("{cakeContext}", cakeNews);//替换系统提示中的蛋糕信息

//    public String getCakeContext(){
//        List<Cake> cakes = cakeMapper.selectList(null);// 查询所有蛋糕信息
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < cakes.size(); i++) {
//            Cake cake = cakes.get(i);
//            sb.append(String.format("蛋糕名称：%s\n蛋糕价格：%d\n蛋糕描述：%s\n", cake.getName(), cake.getPrice(), cake.getDescription()));
//        }
//        return  sb.toString();
//    }