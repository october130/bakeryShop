package com.cake.platform.Sale.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleDTO {
    private Long cakeId;
    private  Integer flashPrice;
    private Integer stock;
    private LocalDateTime beginTime;
    private LocalDateTime endTime;
}
