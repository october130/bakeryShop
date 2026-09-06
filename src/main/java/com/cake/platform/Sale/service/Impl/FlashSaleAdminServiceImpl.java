package com.cake.platform.Sale.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.cake.platform.Sale.VO.FlashSaleVO;
import com.cake.platform.Sale.dto.SaleDTO;
import com.cake.platform.Sale.entity.FlashSale;
import com.cake.platform.Sale.mapper.FlashSaleMapper;
import com.cake.platform.Sale.service.FlashSaleAdminService;
import com.cake.platform.cake.entity.Cake;
import com.cake.platform.cake.mapper.CakeMapper;
import com.cake.platform.common.result.Result;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class FlashSaleAdminServiceImpl implements FlashSaleAdminService {
    @Resource
    private CakeMapper cakeMapper;
    @Resource
    private FlashSaleMapper flashSaleMapper;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public Result<Object> createFlashSale(SaleDTO saleDTO) {
        Cake cake = cakeMapper.selectById(saleDTO.getCakeId());
        if (cake == null)
            return Result.error("Cake not found");
        if (cake.getStatus() != 1){
            return Result.error("为上架蛋糕无法作为限时抢购商品");
        }
        FlashSale flashSale = FlashSale.builder()
                .cakeId(saleDTO.getCakeId())
                .flashPrice(saleDTO.getFlashPrice())
                .stock(saleDTO.getStock())
                .beginTime(saleDTO.getBeginTime())
                .endTime(saleDTO.getEndTime())
                .status(0)
                .build();
        flashSaleMapper.insert(flashSale);
        return Result.success("限时抢购创建成功");
    }

    @Override
    public List<FlashSaleVO> getFlashSaleList() {

        List<FlashSale> flashSales = flashSaleMapper.selectList(
                new QueryWrapper<FlashSale>().orderByDesc("create_time"));
        List<FlashSaleVO> flashSaleVOS = flashSales.stream().map(flashSale -> FlashSaleVO.builder()
                .id(flashSale.getId())
                .cakeId(flashSale.getCakeId())
                .flashPrice(flashSale.getFlashPrice())
                .stock(flashSale.getStock())
                .beginTime(flashSale.getBeginTime())
                .endTime(flashSale.getEndTime())
                .status(flashSale.getStatus())
                .build()).toList();
        return flashSaleVOS;
    }

    @Override
    public Result updateFlashSaleStatus(Long id, Integer status) {
        String key = "flashSale:detail:" + id;



        int rows = flashSaleMapper.update(null,
                new UpdateWrapper<FlashSale>()
                        .eq("id", id)
                        .set("status", status));
        if (rows == 0){
            log.warn("限时抢购更新失败，cakeId={} 不存在或状态已变更", id);
            return Result.error("限时抢购更新失败，cakeId=" + id + " 不存在或状态已变更");
        }
        redisTemplate.delete(key);
        log.info("限时抢购更新成功，id={},需要删除缓存", id);
        return Result.success("限时抢购更新成功");



    }
}
//
//-- 限时抢购表
//CREATE TABLE `flash_sale` (
//        `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
//                              `cake_id` BIGINT NOT NULL COMMENT '蛋糕ID',
//        `flash_price` INT NOT NULL COMMENT '抢购价(分)',
//        `stock` INT NOT NULL COMMENT '库存',
//        `begin_time` DATETIME NOT NULL COMMENT '开始时间',
//        `end_time` DATETIME NOT NULL COMMENT '结束时间',
//        `status` TINYINT DEFAULT 0 COMMENT '状态(0-未开始 1-进行中 2-已结束)',
//        `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
//KEY `idx_cake` (`cake_id`)
//        ) COMMENT '限时抢购表';
