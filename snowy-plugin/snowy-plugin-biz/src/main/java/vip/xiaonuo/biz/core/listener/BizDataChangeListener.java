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
package vip.xiaonuo.biz.core.listener;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import vip.xiaonuo.biz.core.enums.BizDataTypeEnum;
import vip.xiaonuo.biz.modular.org.service.BizOrgService;
import vip.xiaonuo.common.listener.CommonDataChangeListener;

import java.util.List;

/**
 * 业务模块数据变化侦听器
 * 监听其他模块（如sys）对共享表的变更，同步清除biz模块缓存
 *
 * @author yubaoshan
 * @date 2026/2/12
 **/
@Component
public class BizDataChangeListener implements CommonDataChangeListener {

    @Resource
    private BizOrgService bizOrgService;

    @Override
    public void doAddWithDataId(String dataType, String dataId) {
        if(dataType.equals(BizDataTypeEnum.ORG.getValue())) {
            bizOrgService.clearOrgCache();
        }
    }

    @Override
    public void doAddWithDataIdList(String dataType, List<String> dataIdList) {
        if(dataType.equals(BizDataTypeEnum.ORG.getValue())) {
            bizOrgService.clearOrgCache();
        }
    }

    @Override
    public void doAddWithData(String dataType, JSONObject jsonObject) {
    }

    @Override
    public void doAddWithDataList(String dataType, JSONArray jsonArray) {
    }

    @Override
    public void doUpdateWithDataId(String dataType, String dataId) {
        if(dataType.equals(BizDataTypeEnum.ORG.getValue())) {
            bizOrgService.clearOrgCache();
        }
    }

    @Override
    public void doUpdateWithDataIdList(String dataType, List<String> dataIdList) {
        if(dataType.equals(BizDataTypeEnum.ORG.getValue())) {
            bizOrgService.clearOrgCache();
        }
    }

    @Override
    public void doUpdateWithData(String dataType, JSONObject jsonObject) {
    }

    @Override
    public void doUpdateWithDataList(String dataType, JSONArray jsonArray) {
    }

    @Override
    public void doDeleteWithDataId(String dataType, String dataId) {
    }

    @Override
    public void doDeleteWithDataIdList(String dataType, List<String> dataIdList) {
        if(dataType.equals(BizDataTypeEnum.ORG.getValue())) {
            bizOrgService.clearOrgCache();
        }
    }
}
