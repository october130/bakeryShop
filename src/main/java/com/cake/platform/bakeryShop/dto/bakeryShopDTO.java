package com.cake.platform.bakeryShop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class bakeryShopDTO {
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
}
