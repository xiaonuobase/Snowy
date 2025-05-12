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
package vip.xiaonuo.sys.modular.org.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import vip.xiaonuo.sys.modular.org.entity.SysOrgExt;
import vip.xiaonuo.sys.modular.org.mapper.SysOrgExtMapper;
import vip.xiaonuo.sys.modular.org.service.SysOrgExtService;

/**
 * 组织扩展Service接口实现类
 *
 * @author yubaoshan
 * @date  2024/12/21 01:25
 **/
@Service
public class SysOrgExtServiceImpl extends ServiceImpl<SysOrgExtMapper, SysOrgExt> implements SysOrgExtService {

    @Override
    public void createExtInfo(String orgId, String sourceFromType) {
        SysOrgExt sysOrgExt = new SysOrgExt();
        sysOrgExt.setOrgId(orgId);
        sysOrgExt.setSourceFromType(sourceFromType);
        this.save(sysOrgExt);
    }
}
