package com.cn.xiaonuo.sys.provider;

import com.anji.captcha.service.CaptchaCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import java.util.concurrent.TimeUnit;

/**
 * 对于分布式部署的应用，我们建议应用自己实现CaptchaCacheService，比如用Redis
 * 如果应用是单点的，也没有使用redis，那默认使用内存。内存缓存只适合单节点部署的应用，否则验证码生产与验证在节点之间信息不同步，导致失败
 *
 * @Author Jax
 * @Date 2021/1/21 16:27
 */
/*public class CaptchaCacheServiceProvider implements CaptchaCacheService {

    private static final String REDIS = "redis";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void set(String key, String value, long expiresInSeconds) {
        stringRedisTemplate.opsForValue().set(key, value, expiresInSeconds, TimeUnit.SECONDS);
    }

    @Override
    public boolean exists(String key) {
        return stringRedisTemplate.hasKey(key);
    }

    @Override
    public void delete(String key) {
        stringRedisTemplate.delete(key);
    }

    @Override
    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    @Override
    public String type() {
        return REDIS;
    }

}*/
