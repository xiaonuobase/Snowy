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

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import vip.xiaonuo.sys.provider.SysTransferResourceProvider;
import vip.xiaonuo.common.enums.CommonTransferModeEnum;
import vip.xiaonuo.sys.modular.user.entity.SysUser;
import vip.xiaonuo.sys.modular.user.service.SysUserService;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 下属资源转移提供者
 *
 * @author yubaoshan
 * @date 2026/05/13
 */
@Component
public class SysUserSubordinateTransferProvider implements SysTransferResourceProvider {

    @Resource
    private SysUserService sysUserService;

    @Override
    public String getResourceType() {
        return "SUBORDINATE";
    }

    @Override
    public String getResourceTypeName() {
        return "下属";
    }

    @Override
    public int getOrder() {
        return 10;
    }

    @Override
    public List<JSONObject> getDetailColumns() {
        return Arrays.asList(
                JSONUtil.createObj().set("title", "姓名").set("dataIndex", "name"),
                JSONUtil.createObj().set("title", "账号").set("dataIndex", "account")
        );
    }

    @Override
    public long getCount(String userId) {
        return sysUserService.count(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getDirectorId, userId));
    }

    @Override
    public List<JSONObject> getDetailList(String userId) {
        List<SysUser> subordinates = sysUserService.list(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getDirectorId, userId)
                .select(SysUser::getId, SysUser::getName, SysUser::getAccount, SysUser::getAvatar));
        return subordinates.stream().map(user -> {
            JSONObject obj = new JSONObject();
            obj.set("id", user.getId());
            obj.set("name", user.getName());
            obj.set("account", user.getAccount());
            obj.set("avatar", user.getAvatar());
            return obj;
        }).collect(Collectors.toList());
    }

    @Override
    public void executeTransfer(String sourceUserId, String targetUserId, String transferMode,
                                boolean transferAll, List<String> selectedIds) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getDirectorId, sourceUserId);
        if (!transferAll && ObjectUtil.isNotEmpty(selectedIds)) {
            queryWrapper.in(SysUser::getId, selectedIds);
        }
        List<SysUser> subordinates = sysUserService.list(queryWrapper);
        if (ObjectUtil.isEmpty(subordinates)) {
            return;
        }
        if (CommonTransferModeEnum.DELETE.getValue().equals(transferMode)) {
            subordinates.forEach(user -> user.setDirectorId(null));
        } else {
            subordinates.forEach(user -> user.setDirectorId(targetUserId));
        }
        sysUserService.updateBatchById(subordinates);
    }
}
