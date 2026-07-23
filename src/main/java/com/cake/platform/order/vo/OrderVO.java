package com.cake.platform.order.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderVO {
    private Long id;//订单id
    private String orderNo;
    private Integer totalPrice;
    private String status;
    private LocalDateTime createTime;
    private List<OrderItemVO> items;
}
