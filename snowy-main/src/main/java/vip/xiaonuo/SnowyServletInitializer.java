package vip.xiaonuo;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Snowy Web程序启动类
 *
 * @author xuyuxiang
 * @date 2017-05-21 9:43
 */
public class SnowyServletInitializer extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(SnowyApplication.class);
    }

}
