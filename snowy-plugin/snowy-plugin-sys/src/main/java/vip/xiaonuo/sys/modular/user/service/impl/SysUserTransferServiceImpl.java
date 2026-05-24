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
package vip.xiaonuo.sys.modular.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.sys.api.SysTransferApi;
import vip.xiaonuo.sys.provider.SysTransferResourceProvider;
import vip.xiaonuo.common.enums.CommonTransferModeEnum;
import vip.xiaonuo.sys.modular.org.param.SysOrgSelectorTreeParam;
import vip.xiaonuo.sys.modular.org.service.SysOrgService;
import vip.xiaonuo.sys.modular.user.entity.SysUser;
import vip.xiaonuo.sys.modular.user.param.SysUserSelectorUserParam;
import vip.xiaonuo.sys.modular.user.param.SysUserTransferExecuteParam;
import vip.xiaonuo.sys.modular.user.param.SysUserTransferItemParam;
import vip.xiaonuo.sys.modular.user.param.SysUserTransferResourceDetailParam;
import vip.xiaonuo.sys.modular.user.param.SysUserTransferResourceListParam;
import vip.xiaonuo.sys.modular.user.result.SysUserTransferResourceResult;
import vip.xiaonuo.sys.modular.user.service.SysUserService;
import vip.xiaonuo.sys.modular.user.service.SysUserTransferService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 权限转移Service实现类
 *
 * @author yubaoshan
 * @date 2026/05/13
 */
@Service
public class SysUserTransferServiceImpl implements SysUserTransferService, SysTransferApi {

    @Resource
    private SysUserService sysUserService;

    @Resource
    private SysOrgService sysOrgService;

    @Resource
    private List<SysTransferResourceProvider> transferResourceProviders;

    @Override
    public List<SysUserTransferResourceResult> resourceList(SysUserTransferResourceListParam sysUserTransferResourceListParam) {
        String sourceUserId = sysUserTransferResourceListParam.getSourceUserId();
        SysUser sysUser = sysUserService.getById(sourceUserId);
        if (ObjectUtil.isEmpty(sysUser)) {
            throw new CommonException("用户不存在，id值为：{}", sourceUserId);
        }
        List<SysUserTransferResourceResult> resultList = new ArrayList<>();
        List<SysTransferResourceProvider> sortedProviders = transferResourceProviders.stream()
                .sorted(Comparator.comparingInt(SysTransferResourceProvider::getOrder))
                .toList();
        for (SysTransferResourceProvider provider : sortedProviders) {
            SysUserTransferResourceResult result = new SysUserTransferResourceResult();
            result.setResourceType(provider.getResourceType());
            result.setResourceTypeName(provider.getResourceTypeName());
            try {
                result.setTotalCount(provider.getCount(sourceUserId));
            } catch (Exception e) {
                result.setTotalCount(0L);
            }
            if (result.getTotalCount() <= 0) {
                continue;
            }
            result.setDetailColumns(provider.getDetailColumns());
            resultList.add(result);
        }
        return resultList;
    }

    @Override
    public List<JSONObject> resourceDetail(SysUserTransferResourceDetailParam sysUserTransferResourceDetailParam) {
        String sourceUserId = sysUserTransferResourceDetailParam.getSourceUserId();
        String resourceType = sysUserTransferResourceDetailParam.getResourceType();
        SysTransferResourceProvider provider = getProviderByType(resourceType);
        return provider.getDetailList(sourceUserId);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void execute(SysUserTransferExecuteParam sysUserTransferExecuteParam) {
        String sourceUserId = sysUserTransferExecuteParam.getSourceUserId();
        String targetUserId = sysUserTransferExecuteParam.getTargetUserId();
        String transferMode = sysUserTransferExecuteParam.getTransferMode();
        CommonTransferModeEnum.validate(transferMode);

        if (!CommonTransferModeEnum.DELETE.getValue().equals(transferMode)) {
            if (ObjectUtil.isEmpty(targetUserId)) {
                throw new CommonException("转移和复制模式下目标用户不能为空");
            }
            if (sourceUserId.equals(targetUserId)) {
                throw new CommonException("被调整人和目标人不能是同一个人");
            }
            SysUser targetUser = sysUserService.getById(targetUserId);
            if (ObjectUtil.isEmpty(targetUser)) {
                throw new CommonException("目标用户不存在");
            }
        }

        SysUser sourceUser = sysUserService.getById(sourceUserId);
        if (ObjectUtil.isEmpty(sourceUser)) {
            throw new CommonException("被调整用户不存在");
        }

        for (SysUserTransferItemParam item : sysUserTransferExecuteParam.getTransferItems()) {
            SysTransferResourceProvider provider = getProviderByType(item.getResourceType());
            provider.executeTransfer(sourceUserId, targetUserId, transferMode,
                    Boolean.TRUE.equals(item.getTransferAll()),
                    item.getSelectedIds() != null ? item.getSelectedIds() : new ArrayList<>());
        }
    }

    private SysTransferResourceProvider getProviderByType(String resourceType) {
        return transferResourceProviders.stream()
                .filter(p -> p.getResourceType().equals(resourceType))
                .findFirst()
                .orElseThrow(() -> new CommonException("不支持的资源类型：{}", resourceType));
    }

    @Override
    public List<JSONObject> getTransferResourceList(String sourceUserId) {
        SysUserTransferResourceListParam param = new SysUserTransferResourceListParam();
        param.setSourceUserId(sourceUserId);
        return resourceList(param).stream().map(r -> JSONUtil.parseObj(JSONUtil.toJsonStr(r))).toList();
    }

    @Override
    public List<JSONObject> getTransferResourceDetail(String sourceUserId, String resourceType) {
        SysUserTransferResourceDetailParam param = new SysUserTransferResourceDetailParam();
        param.setSourceUserId(sourceUserId);
        param.setResourceType(resourceType);
        return resourceDetail(param);
    }

    @Override
    public void executeTransfer(JSONObject executeParam) {
        SysUserTransferExecuteParam param = BeanUtil.toBean(executeParam, SysUserTransferExecuteParam.class);
        execute(param);
    }

    @Override
    public List<JSONObject> getTransferOrgTreeSelector(JSONObject param) {
        SysOrgSelectorTreeParam sysOrgSelectorTreeParam = BeanUtil.toBean(param, SysOrgSelectorTreeParam.class);
        return sysOrgService.orgTreeSelector(sysOrgSelectorTreeParam);
    }

    @Override
    public Page<JSONObject> getTransferUserSelector(JSONObject param) {
        SysUserSelectorUserParam sysUserSelectorUserParam = BeanUtil.toBean(param, SysUserSelectorUserParam.class);
        Page<SysUser> page = sysUserService.userSelector(sysUserSelectorUserParam);
        Page<JSONObject> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        result.setRecords(page.getRecords().stream().map(u -> JSONUtil.parseObj(JSONUtil.toJsonStr(u))).toList());
        return result;
    }
}
