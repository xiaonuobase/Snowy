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
import org.redisson.api.RBucket;
import org.redisson.api.RKeys;
import org.redisson.api.RedissonClient;
import org.redisson.api.options.KeysScanOptions;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * 通用Redis缓存操作器
 *
 * @author xuyuxiang
 * @date 2022/6/21 16:00
 **/
@Component
public class CommonCacheOperator {

    private static final String CACHE_KEY_PREFIX = "Cache:";

    @Resource
    private RedissonClient redissonClient;

    public void put(String key, Object value) {
        RBucket<Object> bucket = redissonClient.getBucket(CACHE_KEY_PREFIX + key);
        bucket.set(value);
    }

    public void put(String key, Object value, long timeoutSeconds) {
        RBucket<Object> bucket = redissonClient.getBucket(CACHE_KEY_PREFIX + key);
        bucket.set(value, java.time.Duration.ofSeconds(timeoutSeconds));
    }

    public Object get(String key) {
        RBucket<Object> bucket = redissonClient.getBucket(CACHE_KEY_PREFIX + key);
        return bucket.get();
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
