package com.cake.platform.ai.tool;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cake.platform.cake.entity.Cake;
import com.cake.platform.cake.mapper.CakeMapper;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class CakeTools {
    @Resource
    private CakeMapper cakeMapper;


    @Tool( description = "根据蛋糕口味,最高价格,场景搜索蛋糕")
    public List<Cake> searchCakes(
            @ToolParam(description = "口味") String flavor,
            @ToolParam(description = "价格") Integer maxPrice,
            @ToolParam(description = "场景") String occasion
    ){

        log.info("AI调用搜索函数：口味={}, 价格={}, 场景={}", flavor, maxPrice, occasion);
            LambdaQueryWrapper<Cake> wrapper = new LambdaQueryWrapper<>();
           if  ( flavor!=null){
               wrapper.like(Cake::getName, flavor)
                       .or()
                       .like(Cake::getDescription,flavor);
           }
           if (maxPrice!=null){
               wrapper.le(Cake::getPrice, maxPrice);
           }
           wrapper.eq(Cake::getStatus, 1);
            List<Cake> cakes = cakeMapper.selectList(wrapper);
            log.info("找到 {} 个蛋糕", cakes.size());
            return cakes;
        };
@Data
public static class DeliveryInfo{
        private boolean available;//是否可配送
        private  String[] timeSlots; //可配送时段
        private String message;//提示信息
    }

    @Tool(description = "时间段判断蛋糕是否可配送")
    public DeliveryInfo checkDelivery( @ToolParam(description = "日期") String  date){

            log.info("AI调用配送函数，日期：{}",date);
            //TODO: 根据场景,地址,时间段判断蛋糕是否可配送
            DeliveryInfo deliveryInfo = new DeliveryInfo();
            deliveryInfo.setAvailable(true);
            deliveryInfo.setTimeSlots(new String[]{"09:00-13:00","15:00-20:00"});
            deliveryInfo.setMessage(date + "蛋糕可配送");
            return deliveryInfo;
        };

    @Tool(description = "获取热销蛋糕排行榜")
    public List<Cake> getPopularCakes(
            @ToolParam(description = "返回数量，默认5") Integer limit) {

        log.info("AI调用热销蛋糕函数：数量={}", limit);

        LambdaQueryWrapper<Cake> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cake::getStatus, 1)
               .orderByDesc(Cake::getSold)
               .last("LIMIT " + (limit != null ? limit : 5));

        List<Cake> cakes = cakeMapper.selectList(wrapper);
        log.info("找到 {} 个热销蛋糕", cakes.size());
        return cakes;
    }


}
