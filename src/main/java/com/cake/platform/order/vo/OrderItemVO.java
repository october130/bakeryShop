package com.cake.platform.order.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemVO {//订单项
   private Long cakeId;
    private   String cakeName;
    private   Integer price;       // 单价
    private Integer   amount;// 数量
    private  String customInfo;// 定制信息
}
