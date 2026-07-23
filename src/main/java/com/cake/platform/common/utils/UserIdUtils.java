package com.cake.platform.common.utils;

import org.springframework.stereotype.Component;

@Component
public class UserIdUtils {//用来提取token中的用户id
    private  static  ThreadLocal<Long> threadLocal = new ThreadLocal<>();//用来存储用户id
    public static void setUserId(Long userId){
        threadLocal.set(userId);//设置用户id
    }
    public static Long getUserId(){
        return threadLocal.get();//获取用户id
    }
    public static void remove(){
        threadLocal.remove();//移除用户id
    }
}
