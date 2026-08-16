package com.cake.platform.shoppingcar.service;

import com.cake.platform.cake.entity.Cake;
import com.cake.platform.cake.mapper.CakeMapper;
import com.cake.platform.common.result.Result;
import com.cake.platform.common.utils.UserIdUtils;
import com.cake.platform.shoppingcar.VO.CartItemVO;
import com.cake.platform.shoppingcar.VO.CheckoutVO;
import com.cake.platform.shoppingcar.dto.CartAddDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class CartServiceImpl  implements CartService{
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private CakeMapper cakeMapper;

    @Override
    public Result addCart(CartAddDTO cartAddDTO) {
        Long cakeId = cartAddDTO.getCakeId();
        if (cakeId == null) {
            return Result.error("蛋糕ID不能为空");
        }
        String key = "cart:" + UserIdUtils.getUserId();
        stringRedisTemplate.opsForHash().increment(key, cakeId.toString(), cartAddDTO.getAmount());
        return Result.success("添加成功");
    }

    @Override
    public void removeCart(Long cakeId) {
        Long userId = UserIdUtils.getUserId();
        String key = "cart:" + userId;
        stringRedisTemplate.opsForHash().delete(key, cakeId.toString());
    }

    @Override
    public List<CartItemVO> listCart() {
        String key = "cart:" + UserIdUtils.getUserId();
        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(key);//用法
        if (entries.isEmpty()){
            return new ArrayList<>();
        }

      List<CartItemVO> list = new ArrayList<>();
        for (Map.Entry<Object, Object> entry : entries.entrySet()) {
            Long cakeId = Long.valueOf(entry.getKey().toString());
            Integer amount = Integer.valueOf(entry.getValue().toString());
            Cake cake = cakeMapper.selectById(cakeId);
            CartItemVO cartItemVO = CartItemVO.builder()
                    .cakeId(cakeId)
                    .bakeryId(cake.getBakeryId())
                    .cakeName(cake.getName())
                    .price(cake.getPrice())
                    .image(cake.getImage())
                    .amount(amount)
                    .build();
            list.add(cartItemVO);
        }
        log.info("获取购物车列表成功");
        return list;
    }

    @Override
    public Result<CheckoutVO> checkout() {
        String key = "cart:" + UserIdUtils.getUserId();
        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(key);
        if (entries.isEmpty()) {
            return Result.error("购物车为空");
        }
        List<CartItemVO> cartItemVOList = new ArrayList<>();
        Integer totalPrice = 0;
        for (Map.Entry<Object, Object> entry : entries.entrySet()) {
            Long cakeId = Long.valueOf(entry.getKey().toString());
            Integer amount = Integer.valueOf(entry.getValue().toString());
            Cake cake = cakeMapper.selectById(cakeId);
            if (cake == null) {
                return Result.error(" 蛋糕不存在");
            }
            CartItemVO cartItemVO = CartItemVO.builder()
                    .cakeId(cakeId)
                    .bakeryId(cake.getBakeryId())
                    .cakeName(cake.getName())
                    .price(cake.getPrice())
                    .image(cake.getImage())
                    .amount(amount)
                    .build();
            cartItemVOList.add(cartItemVO);
         totalPrice  = totalPrice + cake.getPrice() * amount;
        }
        CheckoutVO checkoutVO = CheckoutVO.builder()
                .totalPrice(totalPrice)
                .items(cartItemVOList)
                .build();
        return Result.success("获取成功", checkoutVO);

    }

    @Override
    public void clearCart() {
        String key = "cart:" + UserIdUtils.getUserId();
        stringRedisTemplate.delete(key);
        log.info("清空购物车成功");
    }


}
