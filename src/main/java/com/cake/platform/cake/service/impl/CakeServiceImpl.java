package com.cake.platform.cake.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cake.platform.cake.entity.Cake;
import com.cake.platform.cake.mapper.CakeMapper;
import com.cake.platform.cake.service.CakeService;
import com.cake.platform.cake.vo.CakeVO;
import com.cake.platform.common.result.Result;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CakeServiceImpl  implements CakeService {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private CakeMapper cakeMapper;
    @Override
    public Result<List<CakeVO>> listCakes(Long bakeryId) {
        String key = "cake:list:bakery:" + bakeryId;
        List<CakeVO> cakeVOS = (List<CakeVO>) redisTemplate.opsForValue().get(key);
        if (cakeVOS != null) {
            log.info("蛋糕列表缓存命中: {}", key);
            return Result.success(cakeVOS);
        }
        List<Cake> cakes = cakeMapper.selectList(new QueryWrapper<Cake>().eq("bakery_id", bakeryId)
                .eq("status", 1).orderByAsc("price"));
         if (cakes.isEmpty()){
             return Result.error("蛋糕列表为空");
         }
         List<CakeVO> cakeVO = cakes.stream().map(cake -> toVO(cake)).collect(Collectors.toList());
         redisTemplate.opsForValue().set(key, cakeVO, 60, TimeUnit.MINUTES);
         log.info("蛋糕列表缓存未命中: {}", key);
         return Result.success(cakeVO);

    }

    @Override
    public Result<CakeVO> getCakeDetail(Long cakeId) {
        String  cacheKey = "cake:detail:" + cakeId;
          CakeVO cakeVO = (CakeVO) redisTemplate.opsForValue().get( cacheKey);
          if (cakeVO != null) {
              log.info("蛋糕详情缓存命中: {}", cacheKey);
              return Result.success(cakeVO);
          }
          Cake cake = cakeMapper.selectById(cakeId);
          if (cake == null) {
              return Result.error("蛋糕不存在");
          }
        CakeVO  cakeVo = toVO(cake);
          redisTemplate.opsForValue().set(cacheKey, cakeVo, 30, TimeUnit.MINUTES);
          log.info("蛋糕详情缓存未命中: {}", cacheKey);
          return Result.success(cakeVo);
    }

    @Override
    public Result<List<CakeVO>> listCakesByCategory(Integer categoryId) {

         List<Cake> cakes = cakeMapper.selectList(new QueryWrapper<Cake>().eq("category_id", categoryId)
                 .eq("status", 1).orderByAsc("price"));
         if ( cakes.isEmpty()){
             return Result.error("该风格下蛋糕列表为空");
         }
         List<CakeVO> cakeVO = cakes.stream().map(cake -> toVO(cake)).collect(Collectors.toList());
         return Result.success(cakeVO);
    }

    private static CakeVO toVO(Cake cake) {//封装方法，将cake对象转换为CakeVO对象
        return CakeVO.builder()
                .id(cake.getId())
                .bakeryId(cake.getBakeryId())
                .categoryId(cake.getCategoryId())
                .name(cake.getName())
                .image(cake.getImage())
                .price(cake.getPrice())
                .description(cake.getDescription())
                .customizable(cake.getCustomizable())
                .status(cake.getStatus())
                .sold(cake.getSold())
                .build();
    }
}
