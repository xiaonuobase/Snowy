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
package vip.xiaonuo.biz.modular.user.wrapper;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import vip.xiaonuo.biz.modular.user.entity.BizUser;
import vip.xiaonuo.common.pojo.CommonWrapperInterface;
import vip.xiaonuo.common.util.CommonCryptogramUtil;

/**
 * SysUser类型的包装实现类
 *
 * @author xuyuxiang
 * @date 2022/9/15 21:42
 */
public class BizUserWrapperImpl implements CommonWrapperInterface<BizUser> {

    /**
     * 对手机号和证件号码进行解密
     *
     * @author xuyuxiang
     * @date 2022/9/15 21:45
     */
    @Override
    public JSONObject doWrap(BizUser bizUser) {
        String phone = bizUser.getPhone();
        if(ObjectUtil.isNotEmpty(phone)) {
            bizUser.setPhone(CommonCryptogramUtil.doSm4CbcDecrypt(phone));
        }
        String idCardNumber = bizUser.getIdCardNumber();
        if(ObjectUtil.isNotEmpty(idCardNumber)) {
            bizUser.setIdCardNumber(CommonCryptogramUtil.doSm4CbcDecrypt(idCardNumber));
        }
        String emergencyPhone = bizUser.getEmergencyPhone();
        if(ObjectUtil.isNotEmpty(emergencyPhone)) {
            bizUser.setEmergencyPhone(CommonCryptogramUtil.doSm4CbcDecrypt(emergencyPhone));
        }
        return JSONUtil.parseObj(bizUser);
    }
}
