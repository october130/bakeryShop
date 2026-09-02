package com.cake.platform.user.service;

import com.cake.platform.common.result.Result;
import com.cake.platform.user.dto.LoginDTO;
import com.cake.platform.user.vo.LoginVO;
import com.cake.platform.user.vo.UserVO;
import jakarta.validation.Valid;

public interface AdminUserService {
    Result<LoginVO> login(@Valid LoginDTO dto);

    Result<UserVO> getUserInfo(Long userId);
}
