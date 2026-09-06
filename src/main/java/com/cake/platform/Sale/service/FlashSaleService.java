package com.cake.platform.Sale.service;

import com.cake.platform.Sale.VO.FlashSaleOrderVO;
import com.cake.platform.Sale.VO.FlashSaleVO;
import com.cake.platform.common.result.Result;

import java.util.List;

public interface FlashSaleService {
    Result<FlashSaleVO> getFlashSaleDetail(Long id);

    Result<String> seckill(Long id, Long userId);

    Result<List<FlashSaleVO>> getActiveFlashSaleList();

    Result<List<FlashSaleOrderVO>> getUserFlashSaleOrders(Long userId);

    Result<String> payFlashSaleOrder(Long orderId, Long userId);
}
