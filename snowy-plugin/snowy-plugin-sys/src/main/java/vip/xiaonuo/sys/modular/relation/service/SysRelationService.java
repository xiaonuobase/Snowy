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
package vip.xiaonuo.sys.modular.relation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import vip.xiaonuo.sys.modular.relation.entity.SysRelation;

import java.util.List;

/**
 * 关系Service接口
 *
 * @author xuyuxiang
 * @date 2022/4/21 18:35
 **/
public interface SysRelationService extends IService<SysRelation> {

    /**
     * 追加关系
     *
     * @author xuyuxiang
     * @date 2022/4/24 22:29
     */
    void saveRelationWithAppend(String objectId, String targetId, String category);

    /**
     * 追加关系
     *
     * @author xuyuxiang
     * @date 2022/4/24 22:29
     */
    void saveRelationWithAppend(String objectId, String targetId, String category, String extJson);

    /**
     * 批量追加关系
     *
     * @author xuyuxiang
     * @date 2022/4/24 22:29
     */
    void saveRelationBatchWithAppend(String objectId, List<String> targetIdList, String category);

    /**
     * 批量追加关系
     *
     * @author xuyuxiang
     * @date 2022/4/24 22:29
     */
    void saveRelationBatchWithAppend(String objectId, List<String> targetIdList, String category, List<String> extJsonList);

    /**
     * 清空原关系并保存关系
     *
     * @author xuyuxiang
     * @date 2022/4/24 22:29
     */
    void saveRelationWithClear(String objectId, String targetId, String category);

    /**
     * 清空原关系并保存关系
     *
     * @author xuyuxiang
     * @date 2022/4/24 22:29
     */
    void saveRelationWithClear(String objectId, String targetId, String category, String extJson);

    /**
     * 清空原关系并批量保存关系
     *
     * @author xuyuxiang
     * @date 2022/4/24 22:29
     */
    void saveRelationBatchWithClear(String objectId, List<String> targetIdList, String category);

    /**
     * 清空原关系并批量保存关系
     *
     * @author xuyuxiang
     * @date 2022/4/24 22:29
     */
    void saveRelationBatchWithClear(String objectId, List<String> targetIdList, String category, List<String> extJsonList);

    /**
     * 根据对象id获取关系列表
     *
     * @author xuyuxiang
     * @date 2022/4/27 22:06
     */
    List<SysRelation> getRelationListByObjectId(String objectId);

    /**
     * 根据对象id集合获取关系列表
     *
     * @author xuyuxiang
     * @date 2022/4/27 22:06
     */
    List<SysRelation> getRelationListByObjectIdList(List<String> objectIdList);

    /**
     * 根据对象id和关系分类获取关系列表
     *
     * @author xuyuxiang
     * @date 2022/4/27 22:06
     */
    List<SysRelation> getRelationListByObjectIdAndCategory(String objectId, String category);

    /**
     * 根据对象id集合和关系分类获取关系列表
     *
     * @author xuyuxiang
     * @date 2022/4/27 22:06
     */
    List<SysRelation> getRelationListByObjectIdListAndCategory(List<String> objectIdList, String category);

    /**
     * 根据目标id获取关系列表
     *
     * @author xuyuxiang
     * @date 2022/4/27 22:06
     */
    List<SysRelation> getRelationListByTargetId(String targetId);

    /**
     * 根据目标id集合获取关系列表
     *
     * @author xuyuxiang
     * @date 2022/4/27 22:06
     */
    List<SysRelation> getRelationListByTargetIdList(List<String> targetIdList);

    /**
     * 根据目标id和关系分类获取关系列表
     *
     * @author xuyuxiang
     * @date 2022/4/27 22:06
     */
    List<SysRelation> getRelationListByTargetIdAndCategory(String targetId, String category);

    /**
     * 根据目标id集合和关系分类获取关系列表
     *
     * @author xuyuxiang
     * @date 2022/4/27 22:06
     */
    List<SysRelation> getRelationListByTargetIdListAndCategory(List<String> targetIdList, String category);

    /**
     * 根据对象id获取目标id列表
     *
     * @author xuyuxiang
     * @date 2022/4/27 22:07
     */
    List<String> getRelationTargetIdListByObjectId(String objectId);

    /**
     * 根据对象id集合获取目标id列表
     *
     * @author xuyuxiang
     * @date 2022/4/27 22:07
     */
    List<String> getRelationTargetIdListByObjectIdList(List<String> objectIdList);

    /**
     * 根据对象id和关系分类获取目标id列表
     *
     * @author xuyuxiang
     * @date 2022/4/27 22:07
     */
    List<String> getRelationTargetIdListByObjectIdAndCategory(String objectId, String category);

    /**
     * 根据对象id集合和关系分类获取目标id列表
     *
     * @author xuyuxiang
     * @date 2022/4/27 22:07
     */
    List<String> getRelationTargetIdListByObjectIdListAndCategory(List<String> objectIdList, String category);

    /**
     * 根据目标id获取对象id列表
     *
     * @author xuyuxiang
     * @date 2022/4/27 22:07
     */
    List<String> getRelationObjectIdListByTargetId(String targetId);

    /**
     * 根据目标id集合获取对象id列表
     *
     * @author xuyuxiang
     * @date 2022/4/27 22:07
     */
    List<String> getRelationObjectIdListByTargetIdList(List<String> targetIdList);

    /**
     * 根据目标id和关系分类获取对象id列表
     *
     * @author xuyuxiang
     * @date 2022/4/27 22:08
     */
    List<String> getRelationObjectIdListByTargetIdAndCategory(String targetId, String category);

    /**
     * 根据目标id集合和关系分类获取对象id列表
     *
     * @author xuyuxiang
     * @date 2022/4/27 22:08
     */
    List<String> getRelationObjectIdListByTargetIdListAndCategory(List<String> targetIdList, String category);
}
