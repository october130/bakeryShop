package com.cake.platform.common.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminInterceptor implements HandlerInterceptor {
    private static final String ADMIN_ROLE = "ADMIN";
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
      String role =(String) request.getAttribute("role");
      if (role != null && role.equals(ADMIN_ROLE)) {
          return true;
      } else {
          response.setStatus(HttpServletResponse.SC_FORBIDDEN);
          return false;
      }
    }
}
