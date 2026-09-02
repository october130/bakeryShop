package com.cake.platform.bakeryShop.service;

import com.cake.platform.bakeryShop.dto.bakeryShopDTO;
import com.cake.platform.bakeryShop.vo.BakeryVO;
import com.cake.platform.bakeryShop.vo.CategoryVO;
import com.cake.platform.common.result.Result;

import java.util.List;

public interface BakeryService {

    Result<List<CategoryVO>> listCategories();

    Result<List<BakeryVO>> listBakeries(Double latitude, Double longitude, Integer categoryId);

    Result<BakeryVO> getBakeryDetail(Long bakeryId);

    Result addBakery(bakeryShopDTO bakeryShopDTO);

    Result<BakeryVO> getMyBakery();

    Result updateBakery(bakeryShopDTO bakeryShopDTO);

    Result updateBakeryStatus(Integer status);
}
