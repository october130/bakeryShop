package com.cake.platform.Sale.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.cake.platform.Sale.mapper.FlashSaleMapper;
import com.cake.platform.Sale.mapper.FlashSaleOrderMapper;
import com.cake.platform.Sale.VO.FlashSaleVO;
import com.cake.platform.Sale.VO.FlashSaleOrderVO;
import com.cake.platform.Sale.entity.FlashSale;
import com.cake.platform.Sale.entity.FlashSaleOrder;
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
import java.util.stream.Collectors;

@Service
@Slf4j
public class FlashSaleServiceImpl implements FlashSaleService {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private FlashSaleMapper flashSaleMapper;
    @Resource
    private FlashSaleOrderMapper flashSaleOrderMapper;
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
        String cakeImage = cake.getImage();
        FlashSaleVO flashSaleVO = toVO(flashSale, cakeName, cakeImage, originalPrice);
        redisTemplate.opsForValue().set(key, flashSaleVO, 30, TimeUnit.MINUTES);
        log.info("秒杀缓存成功");
        return Result.success(flashSaleVO);

    }

    @Override
    public Result<String> seckill(Long id, Long userId) {
        // 先检查活动是否存在（从数据库，不依赖缓存）
        FlashSale flashSale = flashSaleMapper.selectById(id);
        if (flashSale == null) {
            return Result.error("秒杀活动不存在");
        }

        // 检查时间
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(flashSale.getBeginTime())) {
            return Result.error("秒杀活动未开始");
        }
        if (now.isAfter(flashSale.getEndTime())) {
            return Result.error("秒杀活动已结束");
        }

        // 自动预热库存（如果 Redis 里没有）
        String stockKey = "flash:stock:" + id;
        if (!Boolean.TRUE.equals(redisTemplate.hasKey(stockKey))) {
            redisTemplate.opsForValue().set(stockKey, flashSale.getStock());
            log.info("自动预热库存: flashSaleId={}, stock={}", id, flashSale.getStock());
        }

        // 分布式锁
        RLock lock = redissonClient.getLock("flash:lock:" + id + ":" + userId);
        boolean isLock = lock.tryLock();
        if (!isLock) {
            return Result.error("系统繁忙，请稍后重试");
        }
        try {
            // 执行 Lua 脚本扣库存
            Long result = redisTemplate.execute(
                    SECKILL_SCRIPT,
                    List.of(stockKey, "flash:bought:" + id),
                    userId.toString()
            );
            if (result == 0) {
                return Result.error("商品已抢光");
            }
            if (result == 2) {
                return Result.error("您已经抢到了，请勿重复购买");
            }

            // 发送 MQ 创建订单
            Map<String, Object> msg = new HashMap<>();
            msg.put("flashSaleId", id);
            msg.put("userId", userId);
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE_NAME,
                    "seckill",
                    msg
            );
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return Result.success("抢购成功，请前往订单支付");
    }
    @Override
    public Result<List<FlashSaleVO>> getActiveFlashSaleList() {
        LocalDateTime now = LocalDateTime.now();
        List<FlashSale> list = flashSaleMapper.selectList(
                new QueryWrapper<FlashSale>()
                        .eq("status", 1)
                        .le("begin_time", now)
                        .ge("end_time", now)
                        .orderByAsc("end_time")
        );
        List<FlashSaleVO> voList = list.stream().map(fs -> {
            Cake cake = cakeMapper.selectById(fs.getCakeId());
            return FlashSaleVO.builder()
                    .id(fs.getId())
                    .cakeId(fs.getCakeId())
                    .cakeName(cake != null ? cake.getName() : "")
                    .cakeImage(cake != null ? cake.getImage() : "")
                    .flashPrice(fs.getFlashPrice())
                    .originalPrice(cake != null ? cake.getPrice() : 0)
                    .stock(fs.getStock())
                    .beginTime(fs.getBeginTime())
                    .endTime(fs.getEndTime())
                    .status(fs.getStatus())
                    .build();
        }).collect(Collectors.toList());
        return Result.success(voList);
    }

    @Override
    public Result<List<FlashSaleOrderVO>> getUserFlashSaleOrders(Long userId) {
        List<FlashSaleOrder> orders = flashSaleOrderMapper.selectList(
                new QueryWrapper<FlashSaleOrder>()
                        .eq("user_id", userId)
                        .orderByDesc("create_time")
        );
        List<FlashSaleOrderVO> voList = orders.stream().map(order -> {
            FlashSale fs = flashSaleMapper.selectById(order.getFlashSaleId());
            Cake cake = fs != null ? cakeMapper.selectById(fs.getCakeId()) : null;
            return FlashSaleOrderVO.builder()
                    .id(order.getId())
                    .flashSaleId(order.getFlashSaleId())
                    .orderNo(order.getOrderNo())
                    .status(order.getStatus())
                    .createTime(order.getCreateTime())
                    .cakeName(cake != null ? cake.getName() : "")
                    .cakeImage(cake != null ? cake.getImage() : "")
                    .flashPrice(fs != null ? fs.getFlashPrice() : 0)
                    .build();
        }).collect(Collectors.toList());
        return Result.success(voList);
    }

    @Override
    public Result<String> payFlashSaleOrder(Long orderId, Long userId) {
        FlashSaleOrder order = flashSaleOrderMapper.selectById(orderId);
        if (order == null) {
            return Result.error("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            return Result.error("无权操作此订单");
        }
        if (order.getStatus() != 0) {
            return Result.error("订单状态异常，无法支付");
        }
        // 更新状态为已支付
        int rows = flashSaleOrderMapper.update(null,
                new UpdateWrapper<FlashSaleOrder>()
                        .eq("id", orderId)
                        .set("status", 1)
        );
        if (rows > 0) {
            return Result.success("支付成功");
        }
        return Result.error("支付失败");
    }

    private static FlashSaleVO toVO(FlashSale flashSale, String cakeName, String cakeImage, Integer originalPrice) {
        return FlashSaleVO.builder()
                .id(flashSale.getId())
                .cakeId(flashSale.getCakeId())
                .cakeName(cakeName)
                .cakeImage(cakeImage)
                .flashPrice(flashSale.getFlashPrice())
                .originalPrice(originalPrice)//原价
                .stock(flashSale.getStock())
                .beginTime(flashSale.getBeginTime())
                .endTime(flashSale.getEndTime())
                .status(flashSale.getStatus())
                .build();
    }
}
