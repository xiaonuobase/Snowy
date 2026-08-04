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

import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import vip.xiaonuo.common.listener.CommonDataChangeEventCenter;
import vip.xiaonuo.common.listener.CommonDataChangeListener;

import java.util.List;

/**
 * 数据变化监听器注册配置
 * 从 GlobalConfigure 拆分出来，避免与 MyBatis 初始化产生循环依赖
 *
 * @author xuyuxiang
 * @date 2026/7/23
 **/
@Configuration
public class DataChangeListenerConfiguration {

    /**
     * 注册数据变化事件中心监听器
     * 在独立的配置类中注册，延迟初始化顺序，打破循环依赖链
     *
     * @author xuyuxiang
     * @date 2023/3/3 14:27
     **/
    @Resource
    public void registerListenerList(List<CommonDataChangeListener> dataChangeListenerList) {
        CommonDataChangeEventCenter.registerListenerList(dataChangeListenerList);
    }
}
