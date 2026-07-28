package com.cake.platform.shoppingcar.service;

import com.cake.platform.common.result.Result;
import com.cake.platform.shoppingcar.VO.CartItemVO;
import com.cake.platform.shoppingcar.VO.CheckoutVO;
import com.cake.platform.shoppingcar.dto.CartAddDTO;

import java.util.List;

public interface CartService {
    Result addCart(CartAddDTO cartAddDTO);

    void removeCart(Long cakeId);

    List<CartItemVO> listCart();

    Result<CheckoutVO> checkout();

    void clearCart();
}
