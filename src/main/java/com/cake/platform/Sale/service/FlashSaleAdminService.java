package com.cake.platform.Sale.service;

import com.cake.platform.Sale.VO.FlashSaleVO;
import com.cake.platform.Sale.dto.SaleDTO;
import com.cake.platform.common.result.Result;

import java.util.List;

public interface FlashSaleAdminService {

    Result<Object> createFlashSale(SaleDTO saleDTO);

    List<FlashSaleVO> getFlashSaleList();

    Result updateFlashSaleStatus(Long id, Integer status);
}
