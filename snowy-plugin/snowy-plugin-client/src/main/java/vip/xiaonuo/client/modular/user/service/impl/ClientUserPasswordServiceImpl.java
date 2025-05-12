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
package vip.xiaonuo.client.modular.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import vip.xiaonuo.client.modular.user.entity.ClientUserPassword;
import vip.xiaonuo.client.modular.user.mapper.ClientUserPasswordMapper;
import vip.xiaonuo.client.modular.user.service.ClientUserPasswordService;
import vip.xiaonuo.common.util.CommonCryptogramUtil;

import java.util.List;
import java.util.stream.Collectors;

/**
 * C端用户密码Service接口实现类
 *
 * @author yubaoshan
 * @date  2024/12/21 01:25
 **/
@Service
public class ClientUserPasswordServiceImpl extends ServiceImpl<ClientUserPasswordMapper, ClientUserPassword> implements ClientUserPasswordService {

    @Override
    public void insertUserPasswordHistory(String userId, String newPassword) {
        ClientUserPassword clientUserPassword = new ClientUserPassword();
        clientUserPassword.setUserId(userId);
        clientUserPassword.setPassword(CommonCryptogramUtil.doHashValue(newPassword));
        this.save(clientUserPassword);
    }

    @Override
    public List<String> getUserPasswordHistoryLimit(String userId, int limitValue) {
        return this.page(new Page<>(1, limitValue), new LambdaQueryWrapper<ClientUserPassword>()
                        .eq(ClientUserPassword::getUserId, userId).orderByDesc(ClientUserPassword::getCreateTime))
                .getRecords().stream().map(ClientUserPassword::getPassword).collect(Collectors.toList());
    }
}
