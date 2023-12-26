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
package vip.xiaonuo.dev.modular.config.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import vip.xiaonuo.dev.modular.config.entity.DevConfig;
import vip.xiaonuo.dev.modular.config.param.*;

import java.util.List;

/**
 * 配置Service接口
 *
 * @author xuyuxiang
 * @date 2022/4/22 10:41
 **/
public interface DevConfigService extends IService<DevConfig> {

    /**
     * 根据键获取值
     *
     * @author xuyuxiang
     * @date 2022/4/22 14:52
     **/
    String getValueByKey(String key);

    /**
     * 获取配置分页
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:08
     */
    Page<DevConfig> page(DevConfigPageParam devConfigPageParam);

    /**
     * 获取基础配置列表
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:08
     */
    List<DevConfig> sysBaseList();

    /**
     * 获取配置列表
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:08
     */
    List<DevConfig> list(DevConfigListParam devConfigListParam);

    /**
     * 添加配置
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:48
     */
    void add(DevConfigAddParam devConfigAddParam);

    /**
     * 编辑配置
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:13
     */
    void edit(DevConfigEditParam devConfigEditParam);

    /**
     * 删除配置
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:18
     */
    void delete(List<DevConfigIdParam> devConfigIdParamList);

    /**
     * 获取配置详情
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:18
     */
    DevConfig detail(DevConfigIdParam devConfigIdParam);

    /**
     * 获取配置详情
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:18
     */
    DevConfig queryEntity(String id);

    /**
     * 配置批量更新
     *
     * @author xuyuxiang
     * @date 2022/6/28 11:09
     **/
    void editBatch(List<DevConfigBatchParam> devConfigBatchParamList);
}
