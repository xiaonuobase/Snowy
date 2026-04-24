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
package vip.xiaonuo.sys.modular.org.service;

import com.baomidou.mybatisplus.extension.service.IService;
import vip.xiaonuo.auth.core.pojo.SaBaseLoginUser;
import vip.xiaonuo.sys.modular.org.entity.SysUserDataScope;

import java.util.List;

/**
 * 用户数据范围预计算Service接口
 *
 * @author yubaoshan
 * @date 2026/2/12
 **/
public interface SysUserDataScopeService extends IService<SysUserDataScope> {

    /**
     * 刷新指定用户的数据范围预计算（登录时调用）
     * 按API维度存储，每个API的数据范围独立
     *
     * @param userId 用户ID
     * @param dataScopeList 用户的数据范围集合（per-API）
     */
    void refreshByUserId(String userId, List<SaBaseLoginUser.DataScope> dataScopeList);

    /**
     * 删除指定用户的数据范围预计算
     *
     * @param userId 用户ID
     */
    void deleteByUserId(String userId);

    /**
     * 删除指定机构ID的预计算记录（机构删除时调用）
     * 精准删除，不影响其他机构的数据
     *
     * @param orgIds 被删除的机构ID列表
     */
    void deleteByOrgIds(List<String> orgIds);

    /**
     * 为指定用户的所有API追加机构ID（机构新增时调用）
     *
     * @param userId 用户ID
     * @param orgIds 新增的机构ID列表
     */
    void appendOrgIdsForUser(String userId, List<String> orgIds);
}
