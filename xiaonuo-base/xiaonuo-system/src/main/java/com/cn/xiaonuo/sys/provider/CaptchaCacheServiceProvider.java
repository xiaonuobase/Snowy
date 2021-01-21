package com.cn.xiaonuo.sys.provider;

import com.anji.captcha.service.CaptchaCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName CaptchaCacheServiceProvider
 * @Description 验证码 分布式部署 需要使用redis
 * @Author Jax
 * @Date 2021/1/21 16:27
 **/
//public class CaptchaCacheServiceProvider implements CaptchaCacheService {
//
//    private static final String REDIS = "redis";
//
//    @Autowired
//    private StringRedisTemplate stringRedisTemplate;
//
//    @Override
//    public void set(String key, String value, long expiresInSeconds) {
//        stringRedisTemplate.opsForValue().set(key, value, expiresInSeconds, TimeUnit.SECONDS);
//    }
//
//    @Override
//    public boolean exists(String key) {
//        return stringRedisTemplate.hasKey(key);
//    }
//
//    @Override
//    public void delete(String key) {
//        stringRedisTemplate.delete(key);
//    }
//
//    @Override
//    public String get(String key) {
//        return stringRedisTemplate.opsForValue().get(key);
//    }
//
//    @Override
//    public String type() {
//        return REDIS;
//    }
//
//}
