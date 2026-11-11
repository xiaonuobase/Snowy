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
package vip.xiaonuo.common.cache;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RKeys;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.redisson.api.options.KeysScanOptions;
import org.redisson.client.codec.StringCodec;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * 通用Redis缓存操作器
 *
 * @author xuyuxiang
 * @date 2022/6/21 16:00
 **/
@Slf4j
@Component
public class CommonCacheOperator {

    private static final String CACHE_KEY_PREFIX = "Cache:";

    /** 计数器使用独立前缀，避免与普通缓存的序列化方式混用 */
    private static final String COUNTER_KEY_PREFIX = "Counter:";

    @Resource
    private RedissonClient redissonClient;

    public void put(String key, Object value) {
        RBucket<Object> bucket = redissonClient.getBucket(CACHE_KEY_PREFIX + key);
        bucket.set(value);
    }

    public void put(String key, Object value, long timeoutSeconds) {
        RBucket<Object> bucket = redissonClient.getBucket(CACHE_KEY_PREFIX + key);
        bucket.set(value, Duration.ofSeconds(timeoutSeconds));
    }

    /**
     * 键不存在时写入缓存，返回是否写入成功（原子操作）
     *
     * @author yubaoshan
     * @date 2026/11/09 15:00
     */
    public boolean putIfAbsent(String key, Object value, long timeoutSeconds) {
        RBucket<Object> bucket = redissonClient.getBucket(CACHE_KEY_PREFIX + key);
        return bucket.setIfAbsent(value, Duration.ofSeconds(timeoutSeconds));
    }

    /**
     * 计数器原子自增并返回自增后的值，首次计数时设置过期时间
     *
     * @author yubaoshan
     * @date 2026/11/09 15:00
     */
    public long incrementCounter(String key, long timeoutSeconds) {
        // 原子计数并设置过期时间，同时补齐历史无过期计数器的时效
        return redissonClient.getScript(StringCodec.INSTANCE).eval(RScript.Mode.READ_WRITE,
                "local count = redis.call('INCR', KEYS[1]); "
                        + "if redis.call('TTL', KEYS[1]) == -1 then "
                        + "redis.call('EXPIRE', KEYS[1], ARGV[1]); end; return count;",
                RScript.ReturnType.INTEGER, Collections.singletonList(COUNTER_KEY_PREFIX + key), timeoutSeconds);
    }

    /**
     * 删除计数器
     *
     * @author yubaoshan
     * @date 2026/11/09 15:00
     */
    public void removeCounter(String key) {
        redissonClient.getAtomicLong(COUNTER_KEY_PREFIX + key).delete();
    }

    public Object get(String key) {
        String fullKey = CACHE_KEY_PREFIX + key;
        try {
            RBucket<Object> bucket = redissonClient.getBucket(fullKey);
            return bucket.get();
        } catch (Exception e) {
            // 反序列化失败通常意味着缓存内容与当前类结构不再兼容（如实体新增了 setterless 属性），
            // 此时删掉该 key 让调用方回源重建；否则每次请求都会失败且无法自愈
            log.warn("缓存读取失败，已删除该 key 以便回源重建：{}", fullKey, e);
            deleteQuietly(fullKey);
            return null;
        }
    }

    /**
     * 删除失效缓存失败不应影响降级流程
     */
    private void deleteQuietly(String fullKey) {
        try {
            redissonClient.getBucket(fullKey).delete();
        } catch (Exception e) {
            log.warn("删除失效缓存失败，需人工清理：{}", fullKey, e);
        }
    }

    /**
     * 获取并删除缓存（原子操作），适用于验证码等只能使用一次的数据
     *
     * @author yubaoshan
     * @date 2026/11/09 15:00
     */
    public Object getAndRemove(String key) {
        RBucket<Object> bucket = redissonClient.getBucket(CACHE_KEY_PREFIX + key);
        return bucket.getAndDelete();
    }

    public void remove(String... key) {
        ArrayList<String> keys = CollectionUtil.toList(key);
        List<String> withPrefixKeys = keys.stream().map(i -> CACHE_KEY_PREFIX + i).toList();
        redissonClient.getKeys().delete(withPrefixKeys.toArray(new String[0]));
    }

    public Collection<String> getAllKeys() {
        RKeys keys = redissonClient.getKeys();
        Iterable<String> keysByPattern = keys.getKeysStream(KeysScanOptions.defaults().pattern(CACHE_KEY_PREFIX + "*")).collect(Collectors.toList());
        return StreamSupport.stream(keysByPattern.spliterator(), false)
                .map(key -> StrUtil.removePrefix(key, CACHE_KEY_PREFIX))
                .collect(Collectors.toSet());
    }

    public Collection<Object> getAllValues() {
        Collection<String> allKeys = this.getAllKeys();
        return allKeys.stream()
                .map(this::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public Map<String, Object> getAllKeyValues() {
        Collection<String> allKeys = this.getAllKeys();
        HashMap<String, Object> results = MapUtil.newHashMap();
        for (String key : allKeys) {
            results.put(key, this.get(key));
        }
        return results;
    }

    public void removeBatch(String pattern) {
        RKeys keys = redissonClient.getKeys();
        Iterable<String> keysByPattern = keys.getKeysStream(KeysScanOptions.defaults().pattern(CACHE_KEY_PREFIX + pattern)).collect(Collectors.toList());
        List<String> keyList = StreamSupport.stream(keysByPattern.spliterator(), false)
                .toList();
        if (!keyList.isEmpty()) {
            keys.delete(keyList.toArray(new String[0]));
        }
    }
}
