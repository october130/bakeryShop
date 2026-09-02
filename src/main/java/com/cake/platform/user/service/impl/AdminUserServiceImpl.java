package com.cake.platform.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cake.platform.common.exception.BusinessException;
import com.cake.platform.common.result.Result;
import com.cake.platform.common.utils.JwtUtils;
import com.cake.platform.user.dto.LoginDTO;
import com.cake.platform.user.entity.User;
import com.cake.platform.user.mapper.UserMapper;
import com.cake.platform.user.service.AdminUserService;
import com.cake.platform.user.vo.LoginVO;
import com.cake.platform.user.vo.UserVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AdminUserServiceImpl implements AdminUserService {
    @Resource
    private JwtUtils jwtUtils;
    @Resource
    private UserMapper userMapper;
    @Override
    public Result<LoginVO> login(LoginDTO dto) {
        User user = userMapper.selectOne(
                new QueryWrapper<User>().eq("phone", dto.getPhone()));
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (!dto.getPassword().equals(user.getPassword())) {
            throw new BusinessException("密码错误");
        }
        if (!"ADMIN".equals(user.getRole())) {
            throw new BusinessException("无管理员权限");
        }
        String token = jwtUtils.generateToken(user.getId(), user.getRole());
        log.info("管理员登录成功: userId={}", user.getId());
        return Result.success(LoginVO.builder()
                .token(token)
                .userInfo(toVO(user))
                .build());
    }

    @Override
    public Result<UserVO> getUserInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        log.info("获取用户信息成功: userId={}", user.getId());
        return Result.success(toVO(user));
    }

    private UserVO toVO(User user) {
        return UserVO.builder()
                .id(user.getId())
                .phone(user.getPhone())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .role(user.getRole())
                .createTime(user.getCreateTime())
                .build();
    }

}
