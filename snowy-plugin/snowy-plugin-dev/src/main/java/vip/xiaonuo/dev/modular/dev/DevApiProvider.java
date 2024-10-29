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
package vip.xiaonuo.dev.modular.dev;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import vip.xiaonuo.dev.api.DevApi;
import vip.xiaonuo.dev.modular.dict.entity.DevDict;
import vip.xiaonuo.dev.modular.dict.enums.DevDictCategoryEnum;
import vip.xiaonuo.dev.modular.dict.service.DevDictService;
import vip.xiaonuo.dev.modular.email.service.DevEmailService;
import vip.xiaonuo.dev.modular.file.service.DevFileService;
import vip.xiaonuo.dev.modular.job.service.DevJobService;
import vip.xiaonuo.dev.modular.message.service.DevMessageService;
import vip.xiaonuo.dev.modular.sms.service.DevSmsService;

/**
 * 开发工具模块综合API接口实现类
 *
 * @author xuyuxiang
 * @date 2022/9/26 14:30
 **/
@Service
public class DevApiProvider implements DevApi {

    @Resource
    private DevDictService devDictService;

    @Resource
    private DevJobService devJobService;

    @Resource
    private DevFileService devFileService;

    @Resource
    private DevSmsService devSmsService;

    @Resource
    private DevEmailService devEmailService;

    @Resource
    private DevMessageService devMessageService;

    @Override
    public JSONObject getDevOpCount() {
        Long sysDictCount = devDictService.count(new LambdaQueryWrapper<DevDict>().eq(DevDict::getCategory,
                DevDictCategoryEnum.FRM.getValue()));
        Long bizDictCount = devDictService.count(new LambdaQueryWrapper<DevDict>().eq(DevDict::getCategory,
                DevDictCategoryEnum.BIZ.getValue()));
        Long jobCount = 0L;
//        Long jobCount = devJobService.count(new LambdaQueryWrapper<DevJob>().eq(DevJob::getJobStatus,
//                DevJobStatusEnum.RUNNING.getValue()));
        JSONObject json = new JSONObject();
        json.set("sysDictCount", sysDictCount);
        json.set("bizDictCount", bizDictCount);
        json.set("jobCount", jobCount);
        return json;
    }

    @Override
    public JSONObject getToolDataCount() {
        JSONObject json = new JSONObject();
        json.set("fileCount", devFileService.count());
        json.set("smsCount", devSmsService.count());
        json.set("emailCount", devEmailService.count());
        json.set("messageCount", devMessageService.count());
        return json;
    }
}
