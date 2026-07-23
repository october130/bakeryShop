package com.cake.platform.shoppingcar.VO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemVO {
    private Long cakeId;//蛋糕ID
    private Long bakeryId;//烘焙店ID
    private String cakeName;//蛋糕名称
    private Integer price; // 蛋糕价格
    private String image;//蛋糕图片url
    private Integer amount; // 蛋糕数量

}
