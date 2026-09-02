package com.cake.platform.cake.service;

import com.cake.platform.cake.dto.CakeAddDTO;
import com.cake.platform.cake.vo.CakeVO;
import com.cake.platform.common.result.Result;

import java.util.List;

public interface AdminCakeService {
    Result<List<CakeVO>> listCakes(Long bakeryId);

    Result<CakeVO> getCakeDetail(Long cakeId);

    Result<List<CakeVO>> listCakesByCategory(Integer categoryId);

    Result updateCakeStatus(Long id, Integer status);

    Result addCake(CakeAddDTO cakeAddDTO);

    Result deleteCake(Long id);
}
