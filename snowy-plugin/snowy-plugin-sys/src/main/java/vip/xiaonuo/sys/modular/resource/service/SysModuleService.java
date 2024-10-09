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
package vip.xiaonuo.sys.modular.resource.service;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import vip.xiaonuo.sys.modular.resource.entity.SysModule;
import vip.xiaonuo.sys.modular.resource.param.module.SysModuleAddParam;
import vip.xiaonuo.sys.modular.resource.param.module.SysModuleEditParam;
import vip.xiaonuo.sys.modular.resource.param.module.SysModuleIdParam;
import vip.xiaonuo.sys.modular.resource.param.module.SysModulePageParam;

import java.util.List;

/**
 * 模块Service接口
 *
 * @author xuyuxiang
 * @date 2022/6/27 14:03
 **/
public interface SysModuleService extends IService<SysModule> {

    /**
     * 获取模块分页
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:08
     */
    Page<SysModule> page(SysModulePageParam sysModulePageParam);

    /**
     * 添加模块
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:48
     */
    void add(SysModuleAddParam sysModuleAddParam);

    /**
     * 编辑模块
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:13
     */
    void edit(SysModuleEditParam sysModuleEditParam);

    /**
     * 删除模块
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:18
     */
    void delete(List<SysModuleIdParam> sysModuleIdParamList);

    /**
     * 获取所有模块
     *
     * @author yubaoshan
     * @date 2024/9/6 01:24
     */
    List<JSONObject> moduleSelector();

    /**
     * 获取模块详情
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:18
     */
    SysModule detail(SysModuleIdParam sysModuleIdParam);

    /**
     * 获取模块详情
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:18
     */
    SysModule queryEntity(String id);
}
