package com.cake.platform.shoppingCart.controller;

import com.cake.platform.common.result.Result;
import com.cake.platform.shoppingCart.VO.CartItemVO;
import com.cake.platform.shoppingCart.VO.CheckoutVO;
import com.cake.platform.shoppingCart.dto.CartAddDTO;
import com.cake.platform.shoppingCart.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@Slf4j
@ApiResponse(description = "购物车接口")
public class CartController {
    @Resource
    private CartService cartService;
    @PostMapping("/add")
    @Operation(summary = "添加购物车")
    public Result addCart(@RequestBody CartAddDTO cartAddDTO) {
        log.info("添加购物车");
        cartService.addCart(cartAddDTO);
        return Result.success("添加成功");
    }
    @GetMapping("/list")
    @Operation(summary = "获取购物车列表")
    public List<CartItemVO> listCart() {
        log.info("获取购物车列表");
        List<CartItemVO> cartItemVO = cartService.listCart();
        return cartItemVO;
    }
    @DeleteMapping("/remove/{cakeId}")
    @Operation(summary = "删除购物车")
    public Result removeCart(@PathVariable Long cakeId) {
        log.info("删除购物车");
        cartService.removeCart( cakeId);
        return Result.success("删除成功");
    }
    @PostMapping("/checkout")
    @Operation(summary = "购物车结算")
    public Result<CheckoutVO> checkout() {
        log.info("购物车结算");
      return  cartService.checkout();

    }


}
