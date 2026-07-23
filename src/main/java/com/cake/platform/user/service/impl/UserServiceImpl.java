package com.cake.platform.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cake.platform.common.exception.BusinessException;
import com.cake.platform.common.result.Result;
import com.cake.platform.common.utils.JwtUtils;
import com.cake.platform.user.dto.LoginDTO;
import com.cake.platform.user.dto.RegisterDTO;
import com.cake.platform.user.entity.User;
import com.cake.platform.user.mapper.UserMapper;
import com.cake.platform.user.service.UserService;
import com.cake.platform.user.vo.LoginVO;
import com.cake.platform.user.vo.UserVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private JwtUtils jwtUtils;

    @Override
    public Result<LoginVO> register(RegisterDTO dto) {
        // 检查手机号是否已注册
        User exist = userMapper.selectOne(
                new QueryWrapper<User>().eq("phone", dto.getPhone()));
        if (exist != null) {
            throw new BusinessException("该手机号已注册");
        }

        // 创建用户
        User user = User.builder()
                .phone(dto.getPhone())
                .password(dto.getPassword())
                .nickname(dto.getNickname() != null ? dto.getNickname() : "用户" + dto.getPhone().substring(7))
                .role("USER")
                .build();
        userMapper.insert(user);
        log.info("用户注册成功: phone={}", dto.getPhone());

        // 生成 token
        String token = jwtUtils.generateToken(user.getId(), user.getRole());
        return Result.success("注册成功", LoginVO.builder()
                .token(token)
                .userInfo(toVO(user))
                .build());
    }

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

        String token = jwtUtils.generateToken(user.getId(), user.getRole());
        log.info("用户登录成功: userId={}", user.getId());
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
