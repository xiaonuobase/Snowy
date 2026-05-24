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
package vip.xiaonuo.biz.modular.org.provider;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import vip.xiaonuo.biz.modular.org.entity.BizOrg;
import vip.xiaonuo.biz.modular.org.service.BizOrgService;
import vip.xiaonuo.sys.provider.SysTransferResourceProvider;
import vip.xiaonuo.common.enums.CommonTransferModeEnum;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 机构主管资源转移提供者
 *
 * @author yubaoshan
 * @date 2026/05/13
 */
@Component
public class BizOrgDirectorTransferProvider implements SysTransferResourceProvider {

    @Resource
    private BizOrgService bizOrgService;

    @Override
    public String getResourceType() {
        return "ORG_DIRECTOR";
    }

    @Override
    public String getResourceTypeName() {
        return "机构主管";
    }

    @Override
    public int getOrder() {
        return 25;
    }

    @Override
    public List<JSONObject> getDetailColumns() {
        return Arrays.asList(
                JSONUtil.createObj().set("title", "机构名称").set("dataIndex", "name"),
                JSONUtil.createObj().set("title", "机构编码").set("dataIndex", "code")
        );
    }

    @Override
    public long getCount(String userId) {
        return bizOrgService.count(new LambdaQueryWrapper<BizOrg>()
                .eq(BizOrg::getDirectorId, userId));
    }

    @Override
    public List<JSONObject> getDetailList(String userId) {
        List<BizOrg> orgList = bizOrgService.list(new LambdaQueryWrapper<BizOrg>()
                .eq(BizOrg::getDirectorId, userId)
                .select(BizOrg::getId, BizOrg::getName, BizOrg::getCode));
        return orgList.stream().map(org -> {
            JSONObject obj = new JSONObject();
            obj.set("id", org.getId());
            obj.set("name", org.getName());
            obj.set("code", org.getCode());
            return obj;
        }).collect(Collectors.toList());
    }

    @Override
    public void executeTransfer(String sourceUserId, String targetUserId, String transferMode,
                                boolean transferAll, List<String> selectedIds) {
        LambdaQueryWrapper<BizOrg> queryWrapper = new LambdaQueryWrapper<BizOrg>()
                .eq(BizOrg::getDirectorId, sourceUserId);
        if (!transferAll && ObjectUtil.isNotEmpty(selectedIds)) {
            queryWrapper.in(BizOrg::getId, selectedIds);
        }
        List<BizOrg> orgList = bizOrgService.list(queryWrapper);
        if (ObjectUtil.isEmpty(orgList)) {
            return;
        }
        if (CommonTransferModeEnum.DELETE.getValue().equals(transferMode)) {
            orgList.forEach(org -> org.setDirectorId(null));
        } else {
            orgList.forEach(org -> org.setDirectorId(targetUserId));
        }
        bizOrgService.updateBatchById(orgList);
    }
}
