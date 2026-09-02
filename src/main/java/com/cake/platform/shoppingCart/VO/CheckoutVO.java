package com.cake.platform.shoppingCart.VO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutVO {
    List<CartItemVO> items;
    private Integer totalPrice;

}
