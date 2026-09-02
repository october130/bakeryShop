package com.cake.platform.cake.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cake.platform.cake.dto.CakeAddDTO;
import com.cake.platform.cake.entity.Cake;
import com.cake.platform.cake.mapper.CakeMapper;
import com.cake.platform.cake.service.AdminCakeService;
import com.cake.platform.cake.vo.CakeVO;
import com.cake.platform.common.result.Result;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminCakeServiceImpl implements AdminCakeService {
    @Resource
    private CakeMapper cakeMapper;
    @Resource
    private RedisTemplate redisTemplate;
    public Result<List<CakeVO>> listCakes(Long bakeryId) {
        List<Cake> cakes = cakeMapper.selectList(new QueryWrapper<Cake>().eq("bakery_id", bakeryId)
                .eq("status", 1).orderByAsc("price"));
        if (cakes.isEmpty()){
            return Result.error("蛋糕列表为空");
        }
        List<CakeVO> cakeVO = cakes.stream().map(cake -> toVO(cake)).toList();
        return Result.success(cakeVO);
    }
    @Override
    public Result<CakeVO> getCakeDetail(Long cakeId) {

        Cake cake = cakeMapper.selectById(cakeId);
        if (cake == null) {
            return Result.error("蛋糕不存在");
        }
        CakeVO  cakeVo = toVO(cake);
        return Result.success(cakeVo);
    }
    @Override
    public Result<List<CakeVO>> listCakesByCategory(Integer categoryId) {
        List<Cake> cakes = cakeMapper.selectList(new QueryWrapper<Cake>().eq("category_id", categoryId)
                .eq("status", 1).orderByAsc("price"));
        if ( cakes.isEmpty()){
            return Result.error("该风格下蛋糕列表为空");
        }
        List<CakeVO> cakeVO = cakes.stream().map(cake -> toVO(cake)).toList();
        return Result.success(cakeVO);
    }

    @Override
    public Result updateCakeStatus(Long id, Integer status) {
        Cake cake = cakeMapper.selectById(id);
        if (cake == null) {
            return Result.error("蛋糕不存在");
        }
        cake.setStatus(status);
        cakeMapper.updateById(cake);
        clearCakeCache(cake.getBakeryId(), null);
        return Result.success("更新成功");

    }

    @Override
    @Transactional
    public Result addCake(CakeAddDTO cakeAddDTO) {
       if (cakeAddDTO.getBakeryId() == null || cakeAddDTO.getName() == null){
           return Result.error(" bakeryId or name 为空");
       }
        Cake cake = new Cake().builder()
                .bakeryId(cakeAddDTO.getBakeryId())
                .categoryId(cakeAddDTO.getCategoryId())
                .name(cakeAddDTO.getName())
                .image(cakeAddDTO.getImage())
                .price(cakeAddDTO.getPrice())
                .description(cakeAddDTO.getDescription())
                .customizable(cakeAddDTO.getCustomizable())
                .status(1)
                .sold(0)
                .build();
        cakeMapper.insert(cake);
        clearCakeCache(cakeAddDTO.getBakeryId(), null);
        return Result.success("添加成功");

    }

    @Override
    public Result deleteCake(Long id) {
        Cake cake = cakeMapper.selectById(id);
        if (cake == null) {
            return Result.error("商品不存在");
        }
        cakeMapper.deleteById(id);


        clearCakeCache(cake.getBakeryId(), id);   // 删除要清详情+列表缓存
        return Result.success("删除成功");
    }
    private void clearCakeCache(Long bakeryId, Long cakeId) {
        if (cakeId != null) {
            redisTemplate.delete("cake:detail:" + cakeId);
        }
        if (bakeryId != null) {
            redisTemplate.delete("cake:list:bakery:" + bakeryId);
        }
    }

    private CakeVO toVO(Cake cake) {
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
