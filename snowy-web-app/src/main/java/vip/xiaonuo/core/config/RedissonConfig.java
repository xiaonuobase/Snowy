/*
 * Copyright [2022] [https://www.xiaonuo.vip]
 *
 * Snowy采用APACHE LICENSE 2.0开源协议，您在使用过程中，需要注意以下几点：
 *
 * 1.请不要删除和修改根目录下的LICENSE文件。
 * 2.请不要删除和修改Snowy源码头部的版权声明。
 * 3.本项目代码可免费商业使用，商业使用请保留源码和相关描述文件的项目出处，作者声明等。
 * 4.分发源码时候，请注明软件出处 https://www.xiaonuo.vip
 * 5.不可二次分发开源参与同类竞品，如有想法可联系团队xiaonuobase@qq.com商议合作。
 * 6.若您的项目无法满足以上几点，需要更多功能代码，获取Snowy商业授权许可，请在官网购买授权，地址为 https://www.xiaonuo.vip
 */
package vip.xiaonuo.core.config;

import cn.hutool.core.util.StrUtil;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.dao.SaTokenDaoForRedisson;

/**
 * Redisson配置
 *
 * @author xuyuxiang
 * @date 2026/6/29 0:00
 **/
@Configuration
public class RedissonConfig {

    @Primary
    @Bean(name = "redissonClient", destroyMethod = "shutdown")
    public RedissonClient redissonClient(
            @Value("${spring.data.redis.host}") String host,
            @Value("${spring.data.redis.port}") int port,
            @Value("${spring.data.redis.password}") String password,
            @Value("${spring.data.redis.database}") int database) {

        Config config = new Config();
        config.setCodec(new JsonJacksonCodec());
        String address = "redis://" + host + ":" + port;

        config.useSingleServer()
                .setAddress(address)
                .setPassword(StrUtil.isBlank(password) ? null : password)
                .setDatabase(database)
                .setTimeout(10000)
                .setConnectionPoolSize(200)
                .setConnectionMinimumIdleSize(10)
                .setConnectTimeout(10000)
                .setRetryAttempts(3)
                .setRetryInterval(1500);

        return Redisson.create(config);
    }

    @Bean(name = "aloneRedissonClient", destroyMethod = "shutdown")
    public RedissonClient aloneRedissonClient(
            @Value("${sa-token.alone-redis.host}") String host,
            @Value("${sa-token.alone-redis.port}") int port,
            @Value("${sa-token.alone-redis.password}") String password,
            @Value("${sa-token.alone-redis.database}") int database) {

        Config config = new Config();
        config.setCodec(new JsonJacksonCodec());
        String address = "redis://" + host + ":" + port;

        config.useSingleServer()
                .setAddress(address)
                .setPassword(StrUtil.isBlank(password) ? null : password)
                .setDatabase(database)
                .setTimeout(10000)
                .setConnectionPoolSize(200)
                .setConnectionMinimumIdleSize(10)
                .setConnectTimeout(10000)
                .setRetryAttempts(3)
                .setRetryInterval(1500);

        return Redisson.create(config);
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory(RedissonClient redissonClient) {
        return new RedissonConnectionFactory(redissonClient);
    }

    @Primary
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(RedisSerializer.json());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(RedisSerializer.json());
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Primary
    @Bean
    public SaTokenDao saTokenDao(@Qualifier("aloneRedissonClient") RedissonClient aloneRedissonClient) {
        return new SaTokenDaoForRedisson(aloneRedissonClient);
    }
}
