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
package vip.xiaonuo.dev.modular.password.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import vip.xiaonuo.dev.modular.password.entity.DevWeakPassword;
import vip.xiaonuo.dev.modular.password.param.DevWeakPasswordAddParam;
import vip.xiaonuo.dev.modular.password.param.DevWeakPasswordEditParam;
import vip.xiaonuo.dev.modular.password.param.DevWeakPasswordIdParam;
import vip.xiaonuo.dev.modular.password.param.DevWeakPasswordPageParam;

import java.util.List;

/**
 * 弱密码库Service接口
 *
 * @author yubaoshan
 * @date  2024/12/21 01:25
 **/
public interface DevWeakPasswordService extends IService<DevWeakPassword> {

    /**
     * 获取弱密码库分页
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:08
     */
    Page<DevWeakPassword> page(DevWeakPasswordPageParam devWeakPasswordPageParam);

    /**
     * 添加弱密码库
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:48
     */
    void add(DevWeakPasswordAddParam devWeakPasswordAddParam);

    /**
     * 编辑弱密码库
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:13
     */
    void edit(DevWeakPasswordEditParam devWeakPasswordEditParam);

    /**
     * 删除弱密码库
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:18
     */
    void delete(List<DevWeakPasswordIdParam> devWeakPasswordIdParamList);

    /**
     * 获取弱密码库详情
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:18
     */
    DevWeakPassword detail(DevWeakPasswordIdParam devWeakPasswordIdParam);

    /**
     * 获取弱密码库详情
     *
     * @author xuyuxiang
     * @date 2022/7/25 19:42
     **/
    DevWeakPassword queryEntity(String id);

}
