package com.cake.platform.bakeryShop.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BakeryVO {
    private Long id;
    private String name;
    private String address;
    private String phone;
    private String image;
    private String description;
    private Double latitude;
    private Double longitude;
    private Integer avgPrice;
    private Integer status;
    private Double distance; // 距离(公里)，查询时计算
}
