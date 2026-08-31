package com.cake.platform.Sale.service.Impl;

import com.cake.platform.Sale.mapper.FlashSaleMapper;
import com.cake.platform.Sale.VO.FlashSaleVO;
import com.cake.platform.Sale.entity.FlashSale;
import com.cake.platform.Sale.service.FlashSaleService;
import com.cake.platform.cake.entity.Cake;
import com.cake.platform.cake.mapper.CakeMapper;
import com.cake.platform.common.config.RabbitMQConfig;
import com.cake.platform.common.result.Result;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class FlashSaleServiceImpl implements FlashSaleService {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private FlashSaleMapper flashSaleMapper;
    @Resource
    private CakeMapper cakeMapper;
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private RabbitTemplate rabbitTemplate;

    private  static  final DefaultRedisScript<Long> SECKILL_SCRIPT;
    static {//初始化lua脚本，加载秒杀脚本
        SECKILL_SCRIPT = new DefaultRedisScript<>();
     SECKILL_SCRIPT.setLocation(new ClassPathResource("seckill.lua"));
     SECKILL_SCRIPT.setResultType(Long.class);
    }

    @Override
    public Result<FlashSaleVO> getFlashSaleDetail(Long id) {
        String key = "flashSale:detail:" + id;
        FlashSaleVO flashRedisSaleVO = (FlashSaleVO) redisTemplate.opsForValue().get(key);
        if (flashRedisSaleVO != null) {
            log.info("秒杀蛋糕详情缓存命中: {}", key);
            return Result.success(flashRedisSaleVO);
        }
        FlashSale flashSale = flashSaleMapper.selectById(id);
        if (flashSale == null) {
//            //缓存一个空值，防止下缓存穿透
//            redisTemplate.opsForValue().set(key, null, 5, TimeUnit.MINUTES);
            return Result.error("优惠蛋糕不存在");
        }
       Cake cake = cakeMapper.selectById(flashSale.getCakeId());
        String cakeName = cake.getName();
        Integer originalPrice = cake.getPrice();
        FlashSaleVO flashSaleVO = toVO(flashSale, cakeName, originalPrice);
        redisTemplate.opsForValue().set(key, flashSaleVO, 30, TimeUnit.MINUTES);
        log.info("秒杀缓存成功");
        return Result.success(flashSaleVO);

    }

    @Override
    public Result<String> seckill(Long id, Long userId) {
        String key = "flashSale:detail:" + id;
        FlashSaleVO VO = (FlashSaleVO) redisTemplate.opsForValue().get(key);
        if (VO == null) {
            return Result.error("秒杀蛋糕不存在");
        }
        if (VO.getStatus() == 2) {
            return Result.error("秒杀蛋糕已结束");
        }
        if (VO.getStatus() == 0) {
            return Result.error("秒杀蛋糕未开始");
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(VO.getBeginTime()) || now.isAfter(VO.getEndTime())) {
            return Result.error("不在秒杀活动时间内");
        }
        RLock lock = redissonClient.getLock("flash:lock:" + id + ":" + userId);
        boolean isLock = lock.tryLock();
        if (!isLock) {
            return Result.error("秒杀失败,请稍后重试");
        }
        String LimitKey = "limit" + userId;
        Long count = redisTemplate.opsForValue().increment(LimitKey);
      if (count!=null&&count>3){
          return Result.error("操作频率过高，请稍后重试");
      }
        try {
            Long result = redisTemplate.execute(
                    SECKILL_SCRIPT,
                    List.of("flash:stock:" + id, "flash:bought:" + id),
                    userId.toString()
            );
            if (result == 0) {
                return Result.error("秒杀失败,库存不足");
            }
            if (result == 2) {
                return Result.error("秒杀失败,您已参与过此秒杀");
            }

            Map<String, Object> msg = new HashMap<>();
            msg.put("flashSaleId", id);
            msg.put("userId", userId);
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE_NAME,//生产者将消息发送给交换机，交换机将消息发送给队列
                    "seckill",
                    msg

            );
        } finally {
            lock.unlock();//解锁
        }
        return Result.success("抢购成功，订单生成中");
    }
    private static FlashSaleVO toVO(FlashSale flashSale, String cakeName, Integer originalPrice) {
        return FlashSaleVO.builder()
                .id(flashSale.getId())
                .cakeId(flashSale.getCakeId())
                .cakeName(cakeName)
                .flashPrice(flashSale.getFlashPrice())
                .originalPrice(originalPrice)//原价
                .stock(flashSale.getStock())
                .beginTime(flashSale.getStartTime())
                .endTime(flashSale.getEndTime())
                .status(flashSale.getStatus())
                .build();
    }
}
