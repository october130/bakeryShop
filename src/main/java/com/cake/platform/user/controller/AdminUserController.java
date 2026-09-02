package com.cake.platform.user.controller;

import com.cake.platform.common.result.Result;
import com.cake.platform.user.dto.LoginDTO;
import com.cake.platform.user.service.AdminUserService;
import com.cake.platform.user.vo.LoginVO;
import com.cake.platform.user.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@Slf4j
@Tag(name = "管理员接口")
public class AdminUserController {
    @Autowired
    private AdminUserService userService;
    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        return userService.login(dto);
    }
    @GetMapping("/info")
    @Operation(summary = "获取当前用户信息")
    public Result<UserVO> getUserInfo(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return userService.getUserInfo(userId);
    }

}
