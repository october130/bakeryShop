package com.cake.platform.shoppingCart.service;

import com.cake.platform.common.result.Result;
import com.cake.platform.shoppingCart.VO.CartItemVO;
import com.cake.platform.shoppingCart.VO.CheckoutVO;
import com.cake.platform.shoppingCart.dto.CartAddDTO;

import java.util.List;

public interface CartService {
    Result addCart(CartAddDTO cartAddDTO);

    void removeCart(Long cakeId);

    List<CartItemVO> listCart();

    Result<CheckoutVO> checkout();
    void clearCart();
}
