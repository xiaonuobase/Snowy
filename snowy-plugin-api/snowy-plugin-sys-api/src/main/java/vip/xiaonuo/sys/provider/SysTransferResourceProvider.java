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
package vip.xiaonuo.sys.provider;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.util.Collections;
import java.util.List;

/**
 * 权限转移资源提供者接口，各插件模块实现此接口注册可转移的资源类型
 *
 * @author yubaoshan
 * @date 2026/05/13
 */
public interface SysTransferResourceProvider {

    /**
     * 获取资源类型标识
     *
     * @author yubaoshan
     * @date 2026/05/13
     */
    String getResourceType();

    /**
     * 获取资源类型名称
     *
     * @author yubaoshan
     * @date 2026/05/13
     */
    String getResourceTypeName();

    /**
     * 获取排序值，值越小越靠前
     *
     * @author yubaoshan
     * @date 2026/05/13
     */
    default int getOrder() {
        return 100;
    }

    /**
     * 获取指定用户拥有的该类型资源总数
     *
     * @author yubaoshan
     * @date 2026/05/13
     */
    long getCount(String userId);

    /**
     * 获取指定用户拥有的该类型资源明细列表
     *
     * @author yubaoshan
     * @date 2026/05/13
     */
    List<JSONObject> getDetailList(String userId);

    /**
     * 执行权限转移
     *
     * @param sourceUserId 源用户ID
     * @param targetUserId 目标用户ID（删除模式下可为null）
     * @param transferMode 转移模式：TRANSFER、COPY、DELETE
     * @param transferAll  是否全部转移
     * @param selectedIds  选中的资源ID列表（transferAll为true时可为空）
     * @author yubaoshan
     * @date 2026/05/13
     */
    void executeTransfer(String sourceUserId, String targetUserId, String transferMode,
                         boolean transferAll, List<String> selectedIds);

    /**
     * 获取资源明细的列定义，前端表格根据此定义动态渲染列
     *
     * @author yubaoshan
     * @date 2026/05/13
     */
    default List<JSONObject> getDetailColumns() {
        return Collections.singletonList(JSONUtil.createObj().set("title", "名称").set("dataIndex", "name"));
    }
}
