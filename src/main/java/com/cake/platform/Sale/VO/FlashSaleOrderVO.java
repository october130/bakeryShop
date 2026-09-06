package com.cake.platform.Sale.VO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlashSaleOrderVO {
    private Long id;
    private Long flashSaleId;
    private String orderNo;
    private Integer status;
    private LocalDateTime createTime;
    // 关联信息
    private String cakeName;
    private String cakeImage;
    private Integer flashPrice;
}
