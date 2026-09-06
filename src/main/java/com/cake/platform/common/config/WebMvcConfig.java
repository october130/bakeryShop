package com.cake.platform.common.config;

import com.cake.platform.common.interceptor.AdminInterceptor;
import com.cake.platform.common.interceptor.AuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;
    private final AdminInterceptor adminInterceptor;

    public WebMvcConfig(AdminInterceptor adminInterceptor, AuthInterceptor authInterceptor) {
        this.adminInterceptor = adminInterceptor;
        this.authInterceptor = authInterceptor;
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**")// 拦截所有请求
                .excludePathPatterns(//这个表示不拦截的请求
                        "/api/user/login",
                        "/api/user/register",
                        "/api/bakery/list",
                        "/api/bakery/detail/**",
                        "/api/cake/list/**",
                        "/api/cake/detail/**",
                        "/api/cake/category/**",
                        "/api/admin/login",
                        "/api/upload",
                        "/api/flashSale/list",
                        "/api/flashSale/detail/**"
                );
        registry.addInterceptor(adminInterceptor)// 添加拦截器
                .addPathPatterns("/api/admin/**")// 拦截所有请求
                .excludePathPatterns("/api/admin/login");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 映射本地图片目录到 /images/** URL
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:E:/wechat_morgan_yun/miniprogram/images/");
    }
}
