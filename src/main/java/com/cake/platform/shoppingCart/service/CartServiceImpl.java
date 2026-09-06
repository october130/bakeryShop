package com.cake.platform.shoppingCart.service;

import com.cake.platform.cake.entity.Cake;
import com.cake.platform.cake.mapper.CakeMapper;
import com.cake.platform.common.result.Result;
import com.cake.platform.common.utils.UserIdUtils;
import com.cake.platform.shoppingCart.VO.CartItemVO;
import com.cake.platform.shoppingCart.VO.CheckoutVO;
import com.cake.platform.shoppingCart.dto.CartAddDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class CartServiceImpl  implements CartService{
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private CakeMapper cakeMapper;


    @Override
    public Result addCart(CartAddDTO cartAddDTO) {

        Long cakeId = cartAddDTO.getCakeId();

        String key = "cart:" + UserIdUtils.getUserId();//redis拼接key

        redisTemplate.opsForHash().increment(key, String.valueOf(cakeId), cartAddDTO.getAmount());//之后用hash结构存储购物车数据，
        // 其中key是用户id，field是蛋糕id，value是蛋糕数量
            return Result.success("添加成功");
        }

    @Override
    public void removeCart(Long cakeId) {
        Long userId = UserIdUtils.getUserId();
        String key = "cart:" + userId;
        redisTemplate.opsForHash().delete(key, String.valueOf(cakeId));
        log.info("删除购物车成功");
    }

    @Override
    public List<CartItemVO> listCart() {
        String key = "cart:" + UserIdUtils.getUserId();
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);//用法
        if (entries.isEmpty()){
            return new ArrayList<>();
        }

      List<CartItemVO> list = new ArrayList<>();
        for (Map.Entry<Object, Object> entry : entries.entrySet()) {
            Long cakeId = Long.valueOf(entry.getKey().toString());
            Integer amount = (Integer) entry.getValue();
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
    public Result<CheckoutVO> checkout() {//结算
        String key = "cart:" + UserIdUtils.getUserId();//购物车key
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);//用来获取购物车数据
        if (entries.isEmpty()) {
            return Result.error("购物车为空");
        }
        List<CartItemVO> cartItemVOList = new ArrayList<>();
        Long firstBakeryId = null;
        Integer totalPrice = 0;
        for (Map.Entry<Object, Object> entry : entries.entrySet()) {

            Long cakeId = Long.valueOf(entry.getKey().toString());
            Integer amount = (Integer) entry.getValue();
            Cake cake = cakeMapper.selectById(cakeId);


            if (cake == null) {
                redisTemplate.opsForHash().delete(key, String.valueOf(cakeId));
                continue;
            }
            if (firstBakeryId == null) {
                firstBakeryId = cake.getBakeryId();
            }else if (!firstBakeryId.equals(cake.getBakeryId())){
                return Result.error("购物车商品来自多个 bakery，请分开结算");
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
        String key = "cart:" + UserIdUtils.getUserId();//购物车key
        redisTemplate.delete(key);
        log.info("清空购物车成功");
    }


}
