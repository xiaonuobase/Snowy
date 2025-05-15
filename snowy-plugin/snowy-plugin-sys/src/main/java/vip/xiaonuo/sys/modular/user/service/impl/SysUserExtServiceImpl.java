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

import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import vip.xiaonuo.sys.modular.user.entity.SysUserExt;
import vip.xiaonuo.sys.modular.user.enums.SysUserSourceFromTypeEnum;
import vip.xiaonuo.sys.modular.user.mapper.SysUserExtMapper;
import vip.xiaonuo.sys.modular.user.service.SysUserExtService;

/**
 * 用户扩展Service接口实现类
 *
 * @author yubaoshan
 * @date  2024/12/21 01:25
 **/
@Service
public class SysUserExtServiceImpl extends ServiceImpl<SysUserExtMapper, SysUserExt> implements SysUserExtService {

    @Override
    public void updatePasswordLastTime(String userId) {
        SysUserExt sysUserExt = this.getOne(new LambdaQueryWrapper<SysUserExt>().eq(SysUserExt::getUserId, userId));
        if(ObjectUtil.isEmpty(sysUserExt)){
            sysUserExt = new SysUserExt();
            sysUserExt.setUserId(userId);
            sysUserExt.setSourceFromType(SysUserSourceFromTypeEnum.SYSTEM_ADD.getValue());
            sysUserExt.setPasswordUpdateTime(DateTime.now());
            this.save(sysUserExt);
        } else {
            sysUserExt.setPasswordUpdateTime(DateTime.now());
            this.updateById(sysUserExt);
        }
    }

    @Override
    public void createExtInfo(String userId, String sourceFromType) {
        SysUserExt sysUserExt = new SysUserExt();
        sysUserExt.setUserId(userId);
        sysUserExt.setSourceFromType(sourceFromType);
        sysUserExt.setPasswordUpdateTime(DateTime.now());
        this.save(sysUserExt);
    }
}
