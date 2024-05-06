package vip.xiaonuo.mobile.modular.resource.provider;

import cn.hutool.json.JSONObject;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import vip.xiaonuo.mobile.api.MobileModuleApi;
import vip.xiaonuo.mobile.modular.resource.service.MobileModuleService;

import java.util.List;

/**
 * 移动端模块API接口提供者
 *
 * @author 每天一点
 * @date 2023/7/15 22:38
 */
@Service
public class MobileModuleApiProvider implements MobileModuleApi {
    @Resource
    private MobileModuleService mobileModuleService;

    @Override
    public List<JSONObject> mobileModuleSelector() {
        return mobileModuleService.mobileModuleSelector();
    }
}
