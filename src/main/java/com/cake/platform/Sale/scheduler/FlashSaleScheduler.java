package com.cake.platform.Sale.scheduler;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cake.platform.Sale.VO.FlashSaleVO;
import com.cake.platform.Sale.entity.FlashSale;
import com.cake.platform.Sale.mapper.FlashSaleMapper;
import com.cake.platform.cake.entity.Cake;
import com.cake.platform.cake.mapper.CakeMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class FlashSaleScheduler {
    @Resource
    private FlashSaleMapper flashSaleMapper;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private CakeMapper cakeMapper;

    /**
     * 每分钟执行一次，预热即将开始或正在进行的秒杀活动
     */
    @Scheduled(cron = "0 * * * * ?")
    public void preheatFlashSale() {
        LocalDateTime now = LocalDateTime.now();
        // 查询: 活动已开始且未结束，状态为"未开始"(status=0)
        List<FlashSale> list = flashSaleMapper.selectList(
                new LambdaQueryWrapper<FlashSale>()
                        .le(FlashSale::getBeginTime, now)   // begin_time <= now (已到达开始时间)
                        .ge(FlashSale::getEndTime, now)     // end_time >= now (还没结束)
                        .eq(FlashSale::getStatus, 0)        // 状态为"未开始"
        );

        for (FlashSale flashSale : list) {
            // 1. 库存预热（key不存在才写入，防止覆盖已扣减的库存）
            String stockKey = "flash:stock:" + flashSale.getId();
            if (!redisTemplate.hasKey(stockKey)) {
                redisTemplate.opsForValue().set(stockKey, flashSale.getStock());
                log.info("库存预热完成: flashSaleId={}, stock={}", flashSale.getId(), flashSale.getStock());
            }
            // 3. 更新状态为"进行中"
            flashSale.setStatus(1);
            flashSaleMapper.updateById(flashSale);
            redisTemplate.opsForValue().set("flashSale:status:" + flashSale.getId(), flashSale.getStatus());//更新缓存状态
            log.info("活动状态更新为进行中: flashSaleId={}", flashSale.getId());

            // 2. 详情预热
            String detailKey = "flashSale:detail:" + flashSale.getId();
            if (!redisTemplate.hasKey(detailKey)) {
                Cake cake = cakeMapper.selectById(flashSale.getCakeId());
                FlashSaleVO vo = toVO(flashSale, cake.getName(), cake.getPrice());
                redisTemplate.opsForValue().set(detailKey, vo, 2, TimeUnit.HOURS);
                log.info("详情预热完成: flashSaleId={}", flashSale.getId());
            }


        }




    }

    private static FlashSaleVO toVO(FlashSale flashSale, String cakeName, Integer originalPrice) {
        return FlashSaleVO.builder()
                .id(flashSale.getId())
                .cakeId(flashSale.getCakeId())
                .cakeName(cakeName)
                .flashPrice(flashSale.getFlashPrice())
                .originalPrice(originalPrice)
                .stock(flashSale.getStock())
                .beginTime(flashSale.getBeginTime())
                .endTime(flashSale.getEndTime())
                .status(flashSale.getStatus())
                .build();
    }
}
