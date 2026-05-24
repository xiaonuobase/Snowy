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
package vip.xiaonuo.sys.modular.user.provider;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import vip.xiaonuo.sys.provider.SysTransferResourceProvider;
import vip.xiaonuo.sys.modular.relation.entity.SysRelation;
import vip.xiaonuo.sys.modular.relation.enums.SysRelationCategoryEnum;
import vip.xiaonuo.sys.modular.relation.service.SysRelationService;
import vip.xiaonuo.sys.modular.role.entity.SysRole;
import vip.xiaonuo.sys.modular.role.service.SysRoleService;
import vip.xiaonuo.common.enums.CommonTransferModeEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色资源转移提供者
 *
 * @author yubaoshan
 * @date 2026/05/13
 */
@Component
public class SysUserRoleTransferProvider implements SysTransferResourceProvider {

    @Resource
    private SysRelationService sysRelationService;

    @Resource
    private SysRoleService sysRoleService;

    @Override
    public String getResourceType() {
        return "ROLE";
    }

    @Override
    public String getResourceTypeName() {
        return "角色";
    }

    @Override
    public int getOrder() {
        return 20;
    }

    @Override
    public List<JSONObject> getDetailColumns() {
        return List.of(JSONUtil.createObj().set("title", "角色名称").set("dataIndex", "name"));
    }

    @Override
    public long getCount(String userId) {
        List<String> roleIdList = sysRelationService.getRelationTargetIdListByObjectIdAndCategory(
                userId, SysRelationCategoryEnum.SYS_USER_HAS_ROLE.getValue());
        return roleIdList.size();
    }

    @Override
    public List<JSONObject> getDetailList(String userId) {
        List<String> roleIdList = sysRelationService.getRelationTargetIdListByObjectIdAndCategory(
                userId, SysRelationCategoryEnum.SYS_USER_HAS_ROLE.getValue());
        if (ObjectUtil.isEmpty(roleIdList)) {
            return new ArrayList<>();
        }
        List<SysRole> roles = sysRoleService.listByIds(roleIdList);
        return roles.stream().map(role -> {
            JSONObject obj = new JSONObject();
            obj.set("id", role.getId());
            obj.set("name", role.getName());
            return obj;
        }).collect(Collectors.toList());
    }

    @Override
    public void executeTransfer(String sourceUserId, String targetUserId, String transferMode,
                                boolean transferAll, List<String> selectedIds) {
        List<SysRelation> sourceRoleRelations = sysRelationService.getRelationListByObjectIdAndCategory(
                sourceUserId, SysRelationCategoryEnum.SYS_USER_HAS_ROLE.getValue());
        if (ObjectUtil.isEmpty(sourceRoleRelations)) {
            return;
        }
        if (!transferAll && ObjectUtil.isNotEmpty(selectedIds)) {
            sourceRoleRelations = sourceRoleRelations.stream()
                    .filter(r -> selectedIds.contains(r.getTargetId()))
                    .collect(Collectors.toList());
        }
        if (ObjectUtil.isEmpty(sourceRoleRelations)) {
            return;
        }
        List<String> roleIdList = CollStreamUtil.toList(sourceRoleRelations, SysRelation::getTargetId);

        if (CommonTransferModeEnum.TRANSFER.getValue().equals(transferMode)) {
            sysRelationService.remove(new LambdaQueryWrapper<SysRelation>()
                    .eq(SysRelation::getObjectId, sourceUserId)
                    .eq(SysRelation::getCategory, SysRelationCategoryEnum.SYS_USER_HAS_ROLE.getValue())
                    .in(SysRelation::getTargetId, roleIdList));
            appendRolesToUser(targetUserId, roleIdList);
        } else if (CommonTransferModeEnum.COPY.getValue().equals(transferMode)) {
            appendRolesToUser(targetUserId, roleIdList);
        } else if (CommonTransferModeEnum.DELETE.getValue().equals(transferMode)) {
            sysRelationService.remove(new LambdaQueryWrapper<SysRelation>()
                    .eq(SysRelation::getObjectId, sourceUserId)
                    .eq(SysRelation::getCategory, SysRelationCategoryEnum.SYS_USER_HAS_ROLE.getValue())
                    .in(SysRelation::getTargetId, roleIdList));
        }
    }

    private void appendRolesToUser(String userId, List<String> roleIdList) {
        List<String> existingRoleIds = sysRelationService.getRelationTargetIdListByObjectIdAndCategory(
                userId, SysRelationCategoryEnum.SYS_USER_HAS_ROLE.getValue());
        List<String> newRoleIds = roleIdList.stream()
                .filter(id -> !existingRoleIds.contains(id))
                .collect(Collectors.toList());
        if (ObjectUtil.isNotEmpty(newRoleIds)) {
            List<SysRelation> newRelations = newRoleIds.stream().map(roleId -> {
                SysRelation sysRelation = new SysRelation();
                sysRelation.setObjectId(userId);
                sysRelation.setTargetId(roleId);
                sysRelation.setCategory(SysRelationCategoryEnum.SYS_USER_HAS_ROLE.getValue());
                return sysRelation;
            }).collect(Collectors.toList());
            sysRelationService.saveBatch(newRelations);
        }
    }
}
