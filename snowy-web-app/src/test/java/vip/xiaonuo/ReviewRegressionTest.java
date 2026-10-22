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
package vip.xiaonuo;

import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.json.JSONObject;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;
import vip.xiaonuo.auth.modular.login.prop.AuthThirdClientProperties;
import vip.xiaonuo.auth.modular.login.service.impl.AuthServiceImpl;
import vip.xiaonuo.common.cache.CommonCacheOperator;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.common.listener.CommonDataChangeEventCenter;
import vip.xiaonuo.common.listener.CommonDataChangeListener;
import vip.xiaonuo.common.util.CommonCryptogramUtil;
import vip.xiaonuo.core.config.DataChangeListenerConfiguration;
import vip.xiaonuo.core.config.GlobalConfigure;
import vip.xiaonuo.sys.modular.user.entity.SysUser;
import vip.xiaonuo.sys.modular.user.param.SysUserOpenSafeParam;
import vip.xiaonuo.sys.modular.user.service.impl.SysUserServiceImpl;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 审查修复的独立回归检查，无需数据库及Redis
 *
 * @author bxjyl
 * @date 2026/10/22
 */
public class ReviewRegressionTest {
    @Test
    void listenerRegistration() throws Exception {
        checkListenerRegistration();
    }

    @Test
    void thirdAuthConfiguration() throws Exception {
        checkThirdAuthConfiguration();
    }

    @Test
    void safeAttempts() throws Exception {
        checkSafeAttempts();
    }

    private static void checkListenerRegistration() throws Exception {
        AtomicInteger calls = new AtomicInteger();
        CommonDataChangeListener listener = (CommonDataChangeListener) Proxy.newProxyInstance(
                ReviewRegressionTest.class.getClassLoader(), new Class<?>[]{CommonDataChangeListener.class},
                (proxy, method, arguments) -> {
                    if (method.getName().equals("doAddWithDataId")) calls.incrementAndGet();
                    return null;
                });
        CommonDataChangeEventCenter.clearListener();
        try {
            for (Object configuration : List.of(new GlobalConfigure(), new DataChangeListenerConfiguration())) {
                for (Method method : configuration.getClass().getDeclaredMethods()) {
                    if (method.isAnnotationPresent(Resource.class) && method.getName().equals("registerListenerList")) {
                        method.invoke(configuration, List.of(listener));
                    }
                }
            }
            CommonDataChangeEventCenter.doAddWithData("ORG", new JSONObject().set("id", "test-org"));
            require(calls.get() == 1, "One event must invoke each listener once");
        } finally {
            CommonDataChangeEventCenter.clearListener();
        }
    }

    private static void checkThirdAuthConfiguration() throws Exception {
        require(resolveThirdUrl(Map.of("snowy.third-auth.user-info-url", "https://legacy.example/user"), null)
                .equals("https://legacy.example/user"), "Legacy URL must work with bundled defaults");
        require(resolveThirdUrl(Map.of(
                "snowy.third-auth.enabled", "true",
                "snowy.third-auth.default-provider", "partner",
                "snowy.third-auth.providers.partner.user-info-url", "https://partner.example/user"), null)
                .equals("https://partner.example/user"), "Default provider must work");
        require(resolveThirdUrl(Map.of(
                "snowy.third-auth.enabled", "true",
                "snowy.third-auth.providers.partner.user-info-url", "https://partner.example/user"), "partner")
                .equals("https://partner.example/user"), "Explicit provider must work");
        expectThirdError(Map.of(), null);
        expectThirdError(Map.of("snowy.third-auth.enabled", "false",
                "snowy.third-auth.user-info-url", "https://legacy.example/user"), null);
        expectThirdError(Map.of("snowy.third-auth.user-info-url", "https://legacy.example/user"), "unknown");
    }

    private static String resolveThirdUrl(Map<String, String> overrides, String provider) throws Exception {
        Properties properties = new Properties();
        try (InputStream input = ReviewRegressionTest.class.getResourceAsStream("/application.properties")) {
            properties.load(Objects.requireNonNull(input));
        }
        Map<String, Object> values = new HashMap<>();
        properties.forEach((key, value) -> values.put(key.toString(), value));
        values.putAll(overrides);
        AuthThirdClientProperties config = new Binder(new MapConfigurationPropertySource(values))
                .bind("snowy.third-auth", AuthThirdClientProperties.class).get();
        AuthServiceImpl service = new AuthServiceImpl();
        setField(service, AuthServiceImpl.class, "authThirdClientProperties", config);
        Method method = AuthServiceImpl.class.getDeclaredMethod("resolveThirdUserInfoUrl", String.class);
        method.setAccessible(true);
        try {
            return (String) method.invoke(service, provider);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof CommonException) throw (CommonException) e.getCause();
            throw e;
        }
    }

    private static void expectThirdError(Map<String, String> values, String provider) throws Exception {
        try {
            resolveThirdUrl(values, provider);
            throw new AssertionError("Unconfigured or disabled third-auth must fail closed");
        } catch (CommonException expected) {
            // 预期拒绝
        }
    }

    private static void checkSafeAttempts() throws Exception {
        MemoryCache cache = new MemoryCache();
        ReentrantLock mutex = new ReentrantLock();
        RLock lock = (RLock) Proxy.newProxyInstance(ReviewRegressionTest.class.getClassLoader(),
                new Class<?>[]{RLock.class}, (proxy, method, arguments) -> {
                    if (method.getName().equals("tryLock")) return mutex.tryLock();
                    if (method.getName().equals("unlock")) { mutex.unlock(); return null; }
                    throw new UnsupportedOperationException(method.getName());
                });
        RedissonClient redisson = (RedissonClient) Proxy.newProxyInstance(ReviewRegressionTest.class.getClassLoader(),
                new Class<?>[]{RedissonClient.class}, (proxy, method, arguments) -> {
                    if (method.getName().equals("getLock")) return lock;
                    throw new UnsupportedOperationException(method.getName());
                });
        AtomicInteger passwordReads = new AtomicInteger();
        SysUserServiceImpl service = new SysUserServiceImpl() {
            @Override
            public SysUser queryEntity(String id) {
                passwordReads.incrementAndGet();
                SysUser user = new SysUser();
                user.setPassword(CommonCryptogramUtil.doHashValue("correct-password"));
                return user;
            }
        };
        setField(service, SysUserServiceImpl.class, "commonCacheOperator", cache);
        setField(service, SysUserServiceImpl.class, "redissonClient", redisson);
        List<String> safeTypes = new ArrayList<>();
        StpLogic original = StpUtil.getStpLogic();
        StpUtil.setStpLogic(new StpLogic("review-test") {
            @Override public String getLoginIdAsString() { return "test-user"; }
            @Override public void openSafe(long timeout) { require(timeout == 120, "Safe timeout"); safeTypes.add("default"); }
            @Override public void openSafe(String type, long timeout) { require(timeout == 120, "Safe timeout"); safeTypes.add(type); }
        });
        try {
            SysUserOpenSafeParam wrong = new SysUserOpenSafeParam();
            wrong.setPassword(CommonCryptogramUtil.doSm2Encrypt("wrong-password"));
            SysUserOpenSafeParam correct = new SysUserOpenSafeParam();
            correct.setPassword(CommonCryptogramUtil.doSm2Encrypt("correct-password"));
            for (int i = 0; i < 5; i++) expectSafeError(service, wrong);
            int beforeBlocked = passwordReads.get();
            expectSafeError(service, correct);
            require(passwordReads.get() == beforeBlocked, "Cooldown must reject before checking a password");
            require(safeTypes.isEmpty(), "Failed attempts cannot grant safe auth");
            require(cache.timeout == 1800, "Cooldown must be 30 minutes");
            cache.values.clear(); // 模拟Redis记录到期
            expectSafeError(service, wrong);
            service.openSafe(correct);
            require(cache.values.isEmpty(), "Successful authentication must reset failure count");
            correct.setSafeType("sensitive-operation");
            service.openSafe(correct);
            require(safeTypes.equals(List.of("default", "sensitive-operation")), "Default and named safe auth must work");
            mutex.lock();
            AtomicReference<Throwable> concurrentFailure = new AtomicReference<>();
            Thread concurrent = new Thread(() -> {
                try { expectSafeError(service, wrong); } catch (Throwable e) { concurrentFailure.set(e); }
            });
            try { concurrent.start(); concurrent.join(); } finally { mutex.unlock(); }
            if (concurrentFailure.get() != null) throw new AssertionError(concurrentFailure.get());
            require(cache.values.isEmpty(), "Concurrent rejection must not overwrite failure count");
        } finally {
            StpUtil.setStpLogic(original);
        }
    }

    private static void expectSafeError(SysUserServiceImpl service, SysUserOpenSafeParam param) {
        try {
            service.openSafe(param);
            throw new AssertionError("Expected authentication rejection");
        } catch (CommonException expected) {
            // 预期拒绝
        }
    }

    private static void setField(Object target, Class<?> type, String name, Object value) throws Exception {
        Field field = type.getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }

    private static void require(boolean value, String message) {
        if (!value) throw new AssertionError(message);
    }

    private static class MemoryCache extends CommonCacheOperator {
        final Map<String, Object> values = new HashMap<>();
        long timeout;
        @Override public Object get(String key) { return values.get(key); }
        @Override public void put(String key, Object value, long seconds) { values.put(key, value); timeout = seconds; }
        @Override public void remove(String... keys) { for (String key : keys) values.remove(key); }
    }
}
