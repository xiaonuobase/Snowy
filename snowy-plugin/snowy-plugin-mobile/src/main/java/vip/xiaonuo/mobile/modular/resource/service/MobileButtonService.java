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
package vip.xiaonuo.mobile.modular.resource.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import vip.xiaonuo.mobile.modular.resource.entity.MobileButton;
import vip.xiaonuo.mobile.modular.resource.param.button.MobileButtonAddParam;
import vip.xiaonuo.mobile.modular.resource.param.button.MobileButtonEditParam;
import vip.xiaonuo.mobile.modular.resource.param.button.MobileButtonIdParam;
import vip.xiaonuo.mobile.modular.resource.param.button.MobileButtonPageParam;

import java.util.List;

/**
 * 移动端按钮Service接口
 *
 * @author xuyuxiang
 * @date 2022/6/27 14:01
 **/
public interface MobileButtonService extends IService<MobileButton> {

    /**
     * 获取移动端按钮分页
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:08
     */
    Page<MobileButton> page(MobileButtonPageParam mobileButtonPageParam);

    /**
     * 添加移动端按钮
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:48
     */
    void add(MobileButtonAddParam mobileButtonAddParam);

    /**
     * 编辑移动端按钮
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:13
     */
    void edit(MobileButtonEditParam mobileButtonEditParam);

    /**
     * 删除移动端按钮
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:18
     */
    void delete(List<MobileButtonIdParam> mobileButtonIdParamList);

    /**
     * 获取移动端按钮详情
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:18
     */
    MobileButton detail(MobileButtonIdParam mobileButtonIdParam);

    /**
     * 获取移动端按钮详情
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:18
     */
    MobileButton queryEntity(String id);
}
