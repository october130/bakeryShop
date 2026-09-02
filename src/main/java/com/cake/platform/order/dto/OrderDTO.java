package com.cake.platform.order.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Long userId;
    @NotBlank(message = "订单号不能为空")
    private String orderNo;// 订单号
    private Integer totalAmount;// 总金额
    private String address;      // 配送地址
    private String phone;        // 联系电话
    private String remark;       // 备注
    private LocalDateTime deliverTime; // 期望送达时间
}
