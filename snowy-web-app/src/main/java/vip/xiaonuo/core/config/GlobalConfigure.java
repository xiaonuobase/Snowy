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

import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.model.SaResponse;
import cn.dev33.satoken.filter.SaServletFilter;
import cn.dev33.satoken.router.SaHttpMethod;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.*;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.ReflectionException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import vip.xiaonuo.auth.core.util.StpClientUtil;
import vip.xiaonuo.common.annotation.CommonNoRepeat;
import vip.xiaonuo.common.annotation.CommonWrapper;
import vip.xiaonuo.common.cache.CommonCacheOperator;
import vip.xiaonuo.common.enums.CommonDeleteFlagEnum;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.common.interceptor.CommonTraceInterceptor;
import vip.xiaonuo.common.listener.CommonDataChangeEventCenter;
import vip.xiaonuo.common.listener.CommonDataChangeListener;
import vip.xiaonuo.common.pojo.CommonResult;
import vip.xiaonuo.common.pojo.CommonWrapperInterface;
import vip.xiaonuo.common.util.CommonIpAddressUtil;
import vip.xiaonuo.common.util.CommonJoinPointUtil;
import vip.xiaonuo.common.util.CommonServletUtil;
import vip.xiaonuo.common.util.CommonTimeFormatUtil;
import vip.xiaonuo.core.handler.GlobalExceptionUtil;
import vip.xiaonuo.sys.core.enums.SysBuildInEnum;

import javax.sql.DataSource;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Snowy配置
 *
 * @author xuyuxiang
 * @date 2021/10/9 14:24
 **/
@Configuration
@MapperScan(basePackages = {"vip.xiaonuo.**.mapper"})
public class GlobalConfigure implements WebMvcConfigurer {

    @Resource
    private SaTokenConfig saTokenConfig;

    private static final String COMMON_REPEAT_SUBMIT_CACHE_KEY = "common-repeatSubmit:";

    /**
     * 无需登录的接口地址集合
     */
    public static final String[] NO_LOGIN_PATH_ARR = {
            /* 主入口 */
            "/",
            /* 静态资源 */
            "/favicon.ico",
            "/doc.html",
            "/webjars/**",
            "/v3/api-docs/**",
            "/druid/**",
            /* 移动端静态资源 */
            "/mobile/**",

            /* 认证相关 */
            "/auth/c/getPicCaptcha",
            "/auth/c/getPhoneValidCode",
            "/auth/c/doLogin",
            "/auth/c/doLoginByPhone",
            "/auth/c/register",
            "/auth/c/getEmailValidCode",
            "/auth/c/doLoginByEmail",

            "/auth/b/getPicCaptcha",
            "/auth/b/getPhoneValidCode",
            "/auth/b/doLogin",
            "/auth/b/doLoginByPhone",
            "/auth/b/register",
            "/auth/b/getEmailValidCode",
            "/auth/b/doLoginByEmail",

            /* 三方登录相关 */
            "/auth/third/render",
            "/auth/third/callback",

            /* 系统基础配置 */
            "/dev/config/sysBaseList",

            /* 系统字典树 */
            "/dev/dict/tree",

            /* B端用户个人中心相关 */
            "/sys/userCenter/getPicCaptcha",
            "/sys/userCenter/findPasswordGetPhoneValidCode",
            "/sys/userCenter/findPasswordGetEmailValidCode",
            "/sys/userCenter/findPasswordByPhone",
            "/sys/userCenter/findPasswordByEmail",

            /* C端用户个人中心相关 */
            "/client/userCenter/getPicCaptcha",
            "/client/userCenter/findPasswordGetPhoneValidCode",
            "/client/userCenter/findPasswordGetEmailValidCode",
            "/client/userCenter/findPasswordByPhone",
            "/client/userCenter/findPasswordByEmail",

            /* 文件下载 */
            "/dev/file/download"
    };

    /**
     * 仅超管使用的接口地址集合
     */
    private static final String[] SUPER_PERMISSION_PATH_ARR = {
            "/auth/session/**",
            "/auth/third/page",
            "/client/user/**",
            "/sys/org/**",
            "/sys/group/**",
            "/sys/position/**",
            "/sys/button/**",
            "/sys/menu/**",
            "/sys/module/**",
            "/sys/role/**",
            "/sys/user/**",
            "/sys/index/bizDataCount",
            "/sys/index/opDataCount",
            "/sys/index/toolDataCount",
            "/dev/config/**",
            "/dev/dict/**",
            "/dev/email/page",
            "/dev/email/delete",
            "/dev/email/detail",
            "/dev/file/page",
            "/dev/file/list",
            "/dev/file/delete",
            "/dev/file/detail",
            "/dev/job/**",
            "/dev/log/**",
            "/dev/message/page",
            "/dev/message/delete",
            "/dev/message/detail",
            "/dev/monitor/**",
            "/dev/sms/page",
            "/dev/sms/delete",
            "/dev/sms/detail",
            "/dev/slideshow/**",
            "/gen/basic/**",
            "/gen/config/**",
            "/mobile/menu/**",
            "/mobile/module/**",
    };

    /**
     * B端要排除的，相当于C端要认证的
     */
    private static final String[] CLIENT_USER_PERMISSION_PATH_ARR = {
            "/auth/c/**",
            "/client/c/**"
    };

    /**
     * 注册跨域过滤器
     */
    @Bean
    public SaServletFilter getSaServletFilter() {
        return new SaServletFilter()
                // 指定拦截路由
                .addInclude("/**")

                // 设置鉴权的接口
                .setAuth(r -> {
                    // B端的接口校验B端登录
                    SaRouter.match("/**")
                            // 排除无需登录接口
                            .notMatch(CollectionUtil.newArrayList(NO_LOGIN_PATH_ARR))
                            // 排除C端认证接口
                            .notMatch(CollectionUtil.newArrayList(CLIENT_USER_PERMISSION_PATH_ARR))
                            // 校验B端登录
                            .check(r1 -> {
                                StpUtil.checkLogin();
                                // 更新过期时间
                                StpUtil.renewTimeout(saTokenConfig.getTimeout());
                            });

                    // C端的接口校验C端登录
                    SaRouter.match("/**")
                            // 排除无需登录接口
                            .notMatch(CollectionUtil.newArrayList(NO_LOGIN_PATH_ARR))
                            // 匹配C端认证接口
                            .match(CollectionUtil.newArrayList(CLIENT_USER_PERMISSION_PATH_ARR))
                            // 校验C端登录
                            .check(r1 -> StpClientUtil.checkLogin());

                    // B端的超管接口校验B端超管角色
                    SaRouter.match("/**")
                            // 排除无需登录接口
                            .notMatch(CollectionUtil.newArrayList(NO_LOGIN_PATH_ARR))
                            // 匹配超管接口
                            .match(CollectionUtil.newArrayList(SUPER_PERMISSION_PATH_ARR))
                            // 校验B端超管角色
                            .check(r1 -> StpUtil.checkRole(SysBuildInEnum.BUILD_IN_ROLE_CODE.getValue()));
                })

                // 前置函数：在每次认证函数之前执行
                .setBeforeAuth(obj -> {

                    // ---------- 设置跨域响应头 ----------
                    SaHolder.getResponse()

                            // 是否可以在iframe显示视图： DENY=不可以 | SAMEORIGIN=同域下可以 | ALLOW-FROM uri=指定域名下可以
                            // .setHeader("X-Frame-Options", "SAMEORIGIN")

                            // 是否启用浏览器默认XSS防护： 0=禁用 | 1=启用 | 1; mode=block 启用, 并在检查到XSS攻击时，停止渲染页面
                            .setHeader("X-XSS-Protection", "1; mode=block")
                            // 禁用浏览器内容嗅探
                            .setHeader("X-Content-Type-Options", "nosniff")
                            // 允许指定域访问跨域资源
                            .setHeader("Access-Control-Allow-Origin", "*")
                            // 允许所有请求方式
                            .setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE")
                            // 有效时间
                            .setHeader("Access-Control-Max-Age", "3600")
                            // 允许的header参数
                            .setHeader("Access-Control-Allow-Headers", "*");

                    // 如果是预检请求，则立即返回到前端
                    SaRouter.match(SaHttpMethod.OPTIONS)
                            // OPTIONS预检请求，不做处理
                            .free(r -> {
                            })
                            .back();
                })

                // 异常处理
                .setError(e -> {
                    // 由于过滤器中抛出的异常不进入全局异常处理，所以必须提供[异常处理函数]来处理[认证函数]里抛出的异常
                    // 在[异常处理函数]里的返回值，将作为字符串输出到前端，此处统一转为JSON输出前端
                    SaResponse saResponse = SaHolder.getResponse();
                    saResponse.setHeader(Header.CONTENT_TYPE.getValue(), ContentType.JSON + ";charset=" + CharsetUtil.UTF_8);
                    // result是字符串，需要手动转为json格式。全局异常处理中的字符串返回信息（ommonResult<String>）已经被@ResponseBody进行json格式化了
                    CommonResult<String> result = GlobalExceptionUtil.getCommonResult((Exception) e);
                    Gson g = new GsonBuilder().serializeNulls().create();
                    return g.toJson(result);
                });
    }

    /**
     * RedisTemplate序列化
     *
     * @author xuyuxiang
     * @date 2022/6/21 17:01
     **/
    @SuppressWarnings("ALL")
    @Primary
    @Bean
    public RedisTemplate<String, Object> redisTemplate(@Autowired(required = false) RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        Jackson2JsonRedisSerializer<?> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    /**
     * 静态资源映射
     *
     * @author xuyuxiang
     * @date 2022/7/25 15:16
     **/
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("doc.html").addResourceLocations(ResourceUtils.CLASSPATH_URL_PREFIX + "/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations(ResourceUtils.CLASSPATH_URL_PREFIX + "/META-INF/resources/webjars/");
    }

    /**
     * 节流防抖的AOP
     *
     * @author xuyuxiang
     * @date 2022/9/15 21:24
     */
    @Component
    @Aspect
    public static class CommonNoRepeatAop {

        /**
         * 切入点
         *
         * @author xuyuxiang
         * @date 2022/9/15 21:27
         */
        @Pointcut("@annotation(vip.xiaonuo.common.annotation.CommonNoRepeat)")
        private void noRepeatPointcut() {

        }

        /**
         * 执行校验
         *
         * @author xuyuxiang
         * @date 2022/9/15 21:27
         */
        @Before("noRepeatPointcut()")
        public void doBefore(JoinPoint joinPoint) {
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            Method method = methodSignature.getMethod();
            CommonNoRepeat commonNoRepeat = method.getAnnotation(CommonNoRepeat.class);
            HttpServletRequest request = CommonServletUtil.getRequest();
            String url = request.getRequestURI();
            CommonCacheOperator commonCacheOperator = SpringUtil.getBean(CommonCacheOperator.class);
            JSONObject jsonObject = JSONUtil.createObj();
            jsonObject.set("repeatParam", CommonJoinPointUtil.getArgsJsonString(joinPoint));
            jsonObject.set("repeatTime", DateUtil.current());
            // 获取该接口缓存的限流数据，跟当前ip以及登录用户有关
            String cacheKey = COMMON_REPEAT_SUBMIT_CACHE_KEY + CommonIpAddressUtil.getIp(request) + StrUtil.COLON;
            Object loginId = StpUtil.getLoginIdDefaultNull();
            if (ObjectUtil.isNotEmpty(loginId)) {
                cacheKey = cacheKey + Convert.toStr(loginId) + StrUtil.COLON + url;
            } else {
                cacheKey = cacheKey + url;
            }
            Object cacheObj = commonCacheOperator.get(cacheKey);
            if (ObjectUtil.isNotEmpty(cacheObj)) {
                JSONObject cacheJsonObject = JSONUtil.parseObj(cacheObj);
                if (cacheJsonObject.containsKey(url)) {
                    JSONObject existRepeatJsonObject = cacheJsonObject.getJSONObject(url);
                    // 如果与上次参数一致，且时间间隔小于要求的限流时长，则判定为重复提交
                    if (jsonObject.getStr("repeatParam").equals(existRepeatJsonObject.getStr("repeatParam"))) {
                        long interval = jsonObject.getLong("repeatTime") - existRepeatJsonObject.getLong("repeatTime");
                        if (interval < commonNoRepeat.interval()) {
                            long secondsParam = (commonNoRepeat.interval() - interval) / 1000;
                            if (secondsParam > 0) {
                                throw new CommonException("请求过于频繁，请" + CommonTimeFormatUtil.formatSeconds(secondsParam) + "后再试");
                            }
                        }
                    }
                }
            }
            // 缓存最新的该接口的限流数据，跟当前ip以及登录用户有关，为防止缓存的数据过多，缓存时效为1小时
            commonCacheOperator.put(cacheKey, JSONUtil.createObj().set(url, jsonObject), 60 * 60);
        }
    }

    /**
     * 通用Wrapper的AOP
     *
     * @author xuyuxiang
     * @date 2022/9/15 21:24
     */
    @Component
    @Aspect
    public static class CommonWrapperAop {

        /**
         * 切入点
         *
         * @author xuyuxiang
         * @date 2022/9/15 21:27
         */
        @Pointcut("@annotation(vip.xiaonuo.common.annotation.CommonWrapper)")
        private void wrapperPointcut() {

        }

        /**
         * 执行包装
         *
         * @author xuyuxiang
         * @date 2022/9/15 21:27
         */
        @Around("wrapperPointcut()")
        public Object doWrapper(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
            // 直接执行原有业务逻辑
            Object proceedResult = proceedingJoinPoint.proceed();
            return processWrapping(proceedingJoinPoint, proceedResult);
        }

        /**
         * 具体包装过程
         *
         * @author xuyuxiang
         * @date 2022/9/15 21:27
         */
        @SuppressWarnings("ALL")
        private Object processWrapping(ProceedingJoinPoint proceedingJoinPoint, Object originResult) throws IllegalAccessException, InstantiationException {
            MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
            Method method = methodSignature.getMethod();
            CommonWrapper commonWrapper = method.getAnnotation(CommonWrapper.class);
            Class<? extends CommonWrapperInterface<?>>[] baseWrapperClasses = commonWrapper.value();
            if (ObjectUtil.isEmpty(baseWrapperClasses)) {
                return originResult;
            }
            if (!(originResult instanceof CommonResult)) {
                return originResult;
            }
            CommonResult commonResult = (CommonResult) originResult;
            Object beWrapped = commonResult.getData();
            if (ObjectUtil.isBasicType(beWrapped)) {
                throw new CommonException("被包装的值不能是基本类型");
            }
            if (beWrapped instanceof Page) {
                Page page = (Page) beWrapped;
                ArrayList<Map<String, Object>> maps = new ArrayList<>();
                for (Object wrappedItem : page.getRecords()) {
                    maps.add(this.wrapPureObject(wrappedItem, baseWrapperClasses));
                }
                page.setRecords(maps);
                commonResult.setData(page);
            } else if (beWrapped instanceof Collection) {
                Collection collection = (Collection) beWrapped;
                List<Map<String, Object>> maps = new ArrayList<>();
                for (Object wrappedItem : collection) {
                    maps.add(this.wrapPureObject(wrappedItem, baseWrapperClasses));
                }
                commonResult.setData(maps);
            } else if (ArrayUtil.isArray(beWrapped)) {
                Object[] objects = this.objToArray(beWrapped);
                ArrayList<Map<String, Object>> maps = new ArrayList<>();
                for (Object wrappedItem : objects) {
                    maps.add(this.wrapPureObject(wrappedItem, baseWrapperClasses));
                }
                commonResult.setData(maps);
            } else {
                commonResult.setData(this.wrapPureObject(beWrapped, baseWrapperClasses));
            }
            return commonResult;
        }

        /**
         * 原始对象包装JSONObject
         *
         * @author xuyuxiang
         * @date 2022/9/15 21:36
         */
        @SuppressWarnings("ALL")
        private JSONObject wrapPureObject(Object originModel, Class<? extends CommonWrapperInterface<?>>[] baseWrapperClasses) {
            JSONObject jsonObject = JSONUtil.parseObj(originModel);
            try {
                for (Class<? extends CommonWrapperInterface<?>> commonWrapperClass : baseWrapperClasses) {
                    CommonWrapperInterface commonWrapperInterface = commonWrapperClass.newInstance();
                    Map<String, Object> incrementFieldsMap = commonWrapperInterface.doWrap(originModel);
                    jsonObject.putAll(incrementFieldsMap);
                }
            } catch (Exception e) {
                throw new CommonException("原始对象包装过程，字段转化异常：{}", e.getMessage());
            }
            return jsonObject;
        }

        /**
         * Object转array
         *
         * @author xuyuxiang
         * @date 2022/9/15 21:34
         */
        private Object[] objToArray(Object object) {
            int length = Array.getLength(object);
            Object[] result = new Object[length];
            for (int i = 0; i < result.length; i++) {
                result[i] = Array.get(object, i);
            }
            return result;
        }
    }

    /**
     * 分页插件
     *
     * @author xuyuxiang
     * @date 2022/3/11 10:59
     **/
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        return mybatisPlusInterceptor;
    }

    /**
     * 数据库id选择器，用于Mapper.xml中
     * MyBatis可以根据不同的数据库厂商执行不同的语句
     *
     * @author xuyuxiang
     * @date 2022/1/8 2:16
     */
    @Component
    public static class CustomDbIdProvider implements DatabaseIdProvider {

        @Override
        public String getDatabaseId(DataSource dataSource) throws SQLException {
            Connection conn = null;
            try {
                conn = dataSource.getConnection();
                String url = conn.getMetaData().getURL().toLowerCase();
                if (url.contains("jdbc:oracle")) {
                    return "oracle";
                } else if (url.contains("jdbc:postgresql")) {
                    return "pgsql";
                } else if (url.contains("jdbc:mysql")) {
                    return "mysql";
                } else if (url.contains("jdbc:dm")) {
                    return "dm";
                } else if (url.contains("jdbc:kingbase")) {
                    return "kingbase";
                } else {
                    return "mysql";
                }
            } finally {
                JdbcUtils.closeConnection(conn);
            }
        }
    }

    /**
     * 自定义公共字段自动注入
     *
     * @author xuyuxiang
     * @date 2020/3/31 15:42
     */
    @Component
    public static class CustomMetaObjectHandler implements MetaObjectHandler {

        /** 删除标志 */
        private static final String DELETE_FLAG = "deleteFlag";

        /** 创建人 */
        private static final String CREATE_USER = "createUser";

        /** 创建时间 */
        private static final String CREATE_TIME = "createTime";

        /** 更新人 */
        private static final String UPDATE_USER = "updateUser";

        /** 更新时间 */
        private static final String UPDATE_TIME = "updateTime";

        @Override
        public void insertFill(MetaObject metaObject) {
            try {
                //为空则设置deleteFlag
                Object deleteFlag = metaObject.getValue(DELETE_FLAG);
                if (ObjectUtil.isNull(deleteFlag)) {
                    setFieldValByName(DELETE_FLAG, EnumUtil.toString(CommonDeleteFlagEnum.NOT_DELETE), metaObject);
                }
            } catch (ReflectionException ignored) { }
            try {
                //为空则设置createUser
                Object createUser = metaObject.getValue(CREATE_USER);
                if (ObjectUtil.isNull(createUser)) {
                    setFieldValByName(CREATE_USER, this.getUserId(), metaObject);
                }
            } catch (ReflectionException ignored) { }
            try {
                //为空则设置createTime
                Object createTime = metaObject.getValue(CREATE_TIME);
                if (ObjectUtil.isNull(createTime)) {
                    setFieldValByName(CREATE_TIME, DateTime.now(), metaObject);
                }
            } catch (ReflectionException ignored) { }
        }

        @Override
        public void updateFill(MetaObject metaObject) {
            try {
                //设置updateUser
                setFieldValByName(UPDATE_USER, this.getUserId(), metaObject);
            } catch (ReflectionException ignored) {
            }
            try {
                //设置updateTime
                setFieldValByName(UPDATE_TIME, DateTime.now(), metaObject);
            } catch (ReflectionException ignored) {
            }
        }

        /**
         * 获取用户id
         */
        private String getUserId() {
            try {
                try {
                    String loginId = StpUtil.getLoginIdAsString();
                    if (ObjectUtil.isNotEmpty(loginId)) {
                        return loginId;
                    } else {
                        return "-1";
                    }
                } catch (Exception e) {
                    String clientLoginId = StpClientUtil.getLoginIdAsString();
                    if (ObjectUtil.isNotEmpty(clientLoginId)) {
                        return clientLoginId;
                    } else {
                        return "-1";
                    }
                }
            } catch (Exception e) {
                return "-1";
            }
        }
    }

    /**
     *  物理删除
     *  @author xuyuxiang
     *  @date 2025/4/06 1:20
     */
    public static class DeleteAbsoluteById extends AbstractMethod {
        /**
         * @param methodName 方法名
         * @since 3.5.0
         */
        protected DeleteAbsoluteById(String methodName) {
            super(methodName);
        }

        @Override
        public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
            String method = "deleteAbsoluteById";
            SqlMethod sqlMethod = SqlMethod.DELETE_BY_ID;
            String sql = String.format(sqlMethod.getSql(), tableInfo.getTableName(), tableInfo.getKeyColumn(), tableInfo.getKeyProperty());
            SqlSource sqlSource = this.languageDriver.createSqlSource(this.configuration, sql, Object.class);
            return this.addDeleteMappedStatement(mapperClass, method, sqlSource);
        }
    }


    public static class MpSqlInjector extends DefaultSqlInjector {
        @Override
        public List<AbstractMethod> getMethodList(Class<?> mapperClass, TableInfo tableInfo) {
            List<AbstractMethod> methodList = super.getMethodList(mapperClass, tableInfo);
            methodList.add(new DeleteAbsoluteById("deleteAbsoluteById"));
            return methodList;
        }
    }

    @Bean
    public ISqlInjector sqlInjector() {
        return new MpSqlInjector();
    }

    /**
     * 注册数据变化事件中心 事件发布器
     *
     * @author xuyuxiang
     * @date 2023/3/3 14:27
     **/
    @Resource
    public void registerListenerList(List<CommonDataChangeListener> dataChangeListenerList) {
        CommonDataChangeEventCenter.registerListenerList(dataChangeListenerList);
    }

    /**
     * 添加应用拦截器
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册注解拦截器，并排除不需要注解鉴权的接口地址 (与登录拦截器无关，只是说明哪些接口不需要被拦截器拦截，此处都拦截)
        registry.addInterceptor(new CommonTraceInterceptor()).addPathPatterns("/**");
    }
}
