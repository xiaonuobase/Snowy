package com.cn.xiaonuo;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * XiaoNuo Web程序启动类
 *
 * @author xuyuxiang
 * @date 2017-05-21 9:43
 */
public class XiaoNuoServletInitializer extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(XiaoNuoApplication.class);
    }

}
