package com.cake.platform.Sale.service;

import com.cake.platform.Sale.VO.FlashSaleVO;
import com.cake.platform.common.result.Result;

public interface FlashSaleService {
    Result<FlashSaleVO> getFlashSaleDetail(Long id);

    Result<String> seckill(Long id, Long userId);
}
