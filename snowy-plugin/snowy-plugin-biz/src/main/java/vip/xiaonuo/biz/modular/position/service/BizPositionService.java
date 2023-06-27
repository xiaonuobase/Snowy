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
package vip.xiaonuo.biz.modular.position.service;

import cn.hutool.core.lang.tree.Tree;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import vip.xiaonuo.biz.modular.position.entity.BizPosition;
import vip.xiaonuo.biz.modular.position.param.*;

import java.util.List;

/**
 * 岗位Service接口
 *
 * @author xuyuxiang
 * @date 2022/4/21 18:35
 **/
public interface BizPositionService extends IService<BizPosition> {

    /**
     * 获取岗位分页
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:08
     */
    Page<BizPosition> page(BizPositionPageParam bizPositionPageParam);

    /**
     * 添加岗位
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:48
     */
    void add(BizPositionAddParam bizPositionAddParam);

    /**
     * 编辑岗位
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:13
     */
    void edit(BizPositionEditParam bizPositionEditParam);

    /**
     * 删除岗位
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:18
     */
    void delete(List<BizPositionIdParam> bizPositionIdParamList);

    /**
     * 获取岗位详情
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:18
     */
    BizPosition detail(BizPositionIdParam bizPositionIdParam);

    /**
     * 获取岗位详情
     *
     * @author xuyuxiang
     * @date 2022/7/25 19:42
     **/
    BizPosition queryEntity(String id);

    /**
     * 根据机构id和岗位名称获取岗位id，有则返回，无则创建
     *
     * @author xuyuxiang
     * @date 2022/8/15 14:55
     **/
    String getPositionIdByPositionNameWithCreate(String orgId, String positionName);

    /* ====岗位部分所需要用到的选择器==== */

    /**
     * 获取机构树选择器
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:08
     */
    List<Tree<String>> orgTreeSelector();

    /**
     * 获取岗位选择器
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:08
     */
    Page<BizPosition> positionSelector(BizPositionSelectorPositionParam bizPositionSelectorPositionParam);
}
