package com.cake.platform.Sale.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("flash_sale_order")
public class FlashSaleOrder {
    private Long id;
    private Long flashSaleId;//抢购活动ID
    private Long userId;
    private String orderNo;//订单编号,用UUID生成
    private Integer status;//订单状态
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
