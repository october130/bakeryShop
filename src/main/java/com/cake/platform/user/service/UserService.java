package com.cake.platform.user.service;

import com.cake.platform.common.result.Result;
import com.cake.platform.user.dto.LoginDTO;
import com.cake.platform.user.dto.RegisterDTO;
import com.cake.platform.user.vo.LoginVO;
import com.cake.platform.user.vo.UserVO;

public interface UserService {

    Result<LoginVO> register(RegisterDTO dto);

    Result<LoginVO> login(LoginDTO dto);

    Result<UserVO> getUserInfo(Long userId);
}
