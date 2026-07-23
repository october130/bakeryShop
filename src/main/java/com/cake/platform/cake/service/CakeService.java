package com.cake.platform.cake.service;

import com.cake.platform.cake.vo.CakeVO;
import com.cake.platform.common.result.Result;

import java.util.List;

public interface CakeService {
    Result<List<CakeVO>>listCakes(Long bakeryId);

    Result<CakeVO> getCakeDetail(Long cakeId);

    Result<List<CakeVO>> listCakesByCategory(Integer categoryId);
}
