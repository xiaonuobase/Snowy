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
package vip.xiaonuo.dev.modular.push.service.impl;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vip.xiaonuo.common.enums.CommonSortOrderEnum;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.common.page.CommonPageRequest;
import vip.xiaonuo.dev.api.DevConfigApi;
import vip.xiaonuo.dev.modular.push.entity.DevPush;
import vip.xiaonuo.dev.modular.push.enums.DevPushEngineTypeEnum;
import vip.xiaonuo.dev.modular.push.enums.DevPushMessageTypeEnum;
import vip.xiaonuo.dev.modular.push.enums.DevPushNoticeTypeEnum;
import vip.xiaonuo.dev.modular.push.mapper.DevPushMapper;
import vip.xiaonuo.dev.modular.push.param.*;
import vip.xiaonuo.dev.modular.push.service.DevPushService;
import vip.xiaonuo.dev.modular.push.util.DevPushDingTalkUtil;
import vip.xiaonuo.dev.modular.push.util.DevPushFeiShuUtil;
import vip.xiaonuo.dev.modular.push.util.DevPushWorkWechatUtil;

import java.util.List;

/**
 * 邮件Service接口实现类
 *
 * @author xuyuxiang
 * @date 2022/2/23 18:43
 **/
@Service
public class DevPushServiceImpl extends ServiceImpl<DevPushMapper, DevPush> implements DevPushService {

    /** 默认消息引擎 */
    private static final String SNOWY_SYS_DEFAULT_PUSH_ENGINE_KEY = "SNOWY_SYS_DEFAULT_PUSH_ENGINE";

    @Resource
    private DevConfigApi devConfigApi;

    @Override
    public void pushDynamicText(String engine, String content, boolean noticeAll) {
        if(engine.equals(DevPushEngineTypeEnum.DINGTALK.getValue())) {
            DevPushDingTalkTextParam devPushDingTalkTextParam = new DevPushDingTalkTextParam();
            devPushDingTalkTextParam.setContent(content);
            devPushDingTalkTextParam.setNoticeType(noticeAll?DevPushNoticeTypeEnum.ALL.getValue():
                    DevPushNoticeTypeEnum.NONE.getValue());
            this.pushDingTalkText(devPushDingTalkTextParam);
        } else if (engine.equals(DevPushEngineTypeEnum.FEISHU.getValue())) {
            DevPushFeiShuTextParam devPushFeiShuTextParam = new DevPushFeiShuTextParam();
            devPushFeiShuTextParam.setContent(content);
            devPushFeiShuTextParam.setNoticeType(noticeAll?DevPushNoticeTypeEnum.ALL.getValue():
                    DevPushNoticeTypeEnum.NONE.getValue());
            this.pushFeiShuText(devPushFeiShuTextParam);
        } else if (engine.equals(DevPushEngineTypeEnum.WORKWECHAT.getValue())) {
            DevPushWorkWechatTextParam devPushWorkWechatTextParam = new DevPushWorkWechatTextParam();
            devPushWorkWechatTextParam.setContent(content);
            devPushWorkWechatTextParam.setNoticeType(noticeAll?DevPushNoticeTypeEnum.ALL.getValue():
                    DevPushNoticeTypeEnum.NONE.getValue());
            this.pushWorkWechatText(devPushWorkWechatTextParam);
        } else {
            throw new CommonException("不支持的消息推送引擎：{}", engine);
        }
    }

    @Override
    public void pushDynamicText(DevPushDynamicTextParam devPushDynamicTextParam) {
        String defaultEmailEngine = devConfigApi.getValueByKey(SNOWY_SYS_DEFAULT_PUSH_ENGINE_KEY);
        if(ObjectUtil.isEmpty(defaultEmailEngine)) {
            throw new CommonException("请联系管理员配置默认消息推送引擎");
        }
        String content = devPushDynamicTextParam.getContent();
        Boolean noticeAll = devPushDynamicTextParam.getNoticeAll();
        this.pushDynamicText(defaultEmailEngine, content, noticeAll);
    }

    @Override
    public void pushFeiShuText(DevPushFeiShuTextParam devPushFeiShuTextParam) {
        String noticeType = devPushFeiShuTextParam.getNoticeType();
        String receiptInfo;
        if(DevPushNoticeTypeEnum.NONE.getValue().equals(noticeType)){
            receiptInfo = DevPushFeiShuUtil.pushFeiShuText(devPushFeiShuTextParam.getContent(), false);
        } else if(DevPushNoticeTypeEnum.ALL.getValue().equals(noticeType)) {
            receiptInfo = DevPushFeiShuUtil.pushFeiShuText(devPushFeiShuTextParam.getContent(), true);
        } else {
            throw new CommonException("不支持的通知类型：{}", noticeType);
        }
        DevPush devPush = new DevPush();
        devPush.setEngine(DevPushEngineTypeEnum.FEISHU.getValue());
        devPush.setType(DevPushMessageTypeEnum.TEXT.getValue());
        devPush.setTitle(devPushFeiShuTextParam.getContent());
        devPush.setContent(devPushFeiShuTextParam.getContent());
        devPush.setReceiptInfo(receiptInfo);
        this.save(devPush);
    }

    @Override
    public void pushDingTalkText(DevPushDingTalkTextParam devPushDingTalkTextParam) {
        String noticeType = devPushDingTalkTextParam.getNoticeType();
        String receiptInfo;
        if(DevPushNoticeTypeEnum.NONE.getValue().equals(noticeType)){
            receiptInfo = DevPushDingTalkUtil.pushDingTalkText(devPushDingTalkTextParam.getContent(),
                    false, null);
        } else if(DevPushNoticeTypeEnum.ALL.getValue().equals(noticeType)) {
            receiptInfo = DevPushDingTalkUtil.pushDingTalkText(devPushDingTalkTextParam.getContent(),
                    true, null);
        } else if(DevPushNoticeTypeEnum.PHONE.getValue().equals(noticeType)) {
            if(ObjectUtil.isEmpty(devPushDingTalkTextParam.getPhones())) {
                throw new CommonException("手机号不能为空");
            } else {
                receiptInfo = DevPushDingTalkUtil.pushDingTalkText(devPushDingTalkTextParam.getContent(),
                        false, devPushDingTalkTextParam.getPhones());
            }
        } else {
            throw new CommonException("不支持的通知类型：{}", noticeType);
        }
        DevPush devPush = new DevPush();
        devPush.setEngine(DevPushEngineTypeEnum.DINGTALK.getValue());
        devPush.setType(DevPushMessageTypeEnum.TEXT.getValue());
        devPush.setTitle(devPushDingTalkTextParam.getContent());
        devPush.setContent(devPushDingTalkTextParam.getContent());
        devPush.setReceiptInfo(receiptInfo);
        this.save(devPush);
    }

    @Override
    public void pushDingTalkMarkdown(DevPushDingTalkMarkdownParam devPushDingTalkMarkdownParam) {
        String noticeType = devPushDingTalkMarkdownParam.getNoticeType();
        String receiptInfo;
        if(DevPushNoticeTypeEnum.NONE.getValue().equals(noticeType)){
            receiptInfo = DevPushDingTalkUtil.pushDingTalkMarkdown(devPushDingTalkMarkdownParam.getTitle(),
                    devPushDingTalkMarkdownParam.getContent(), false);
        } else if(DevPushNoticeTypeEnum.ALL.getValue().equals(noticeType)) {
            receiptInfo = DevPushDingTalkUtil.pushDingTalkMarkdown(devPushDingTalkMarkdownParam.getTitle(),
                    devPushDingTalkMarkdownParam.getContent(), true);
        } else {
            throw new CommonException("不支持的通知类型：{}", noticeType);
        }
        DevPush devPush = new DevPush();
        devPush.setEngine(DevPushEngineTypeEnum.DINGTALK.getValue());
        devPush.setType(DevPushMessageTypeEnum.MARKDOWN.getValue());
        devPush.setTitle(devPushDingTalkMarkdownParam.getTitle());
        devPush.setContent(devPushDingTalkMarkdownParam.getContent());
        devPush.setReceiptInfo(receiptInfo);
        this.save(devPush);
    }

    @Override
    public void pushDingTalkLink(DevPushDingTalkLinkParam devPushDingTalkLinkParam) {
        String receiptInfo = DevPushDingTalkUtil.pushDingTalkLink(devPushDingTalkLinkParam.getTitle(),
                devPushDingTalkLinkParam.getContent(),
                devPushDingTalkLinkParam.getPicUrl(),
                devPushDingTalkLinkParam.getMessageUrl());
        DevPush devPush = new DevPush();
        devPush.setEngine(DevPushEngineTypeEnum.DINGTALK.getValue());
        devPush.setType(DevPushMessageTypeEnum.LINK.getValue());
        devPush.setTitle(devPushDingTalkLinkParam.getTitle());
        devPush.setContent(devPushDingTalkLinkParam.getContent());
        devPush.setReceiptInfo(receiptInfo);
        this.save(devPush);
    }

    @Override
    public void pushWorkWechatText(DevPushWorkWechatTextParam devPushWorkWechatTextParam) {
        String noticeType = devPushWorkWechatTextParam.getNoticeType();
        String receiptInfo;
        if(DevPushNoticeTypeEnum.NONE.getValue().equals(noticeType)){
            receiptInfo = DevPushWorkWechatUtil.pushWorkWechatText(devPushWorkWechatTextParam.getContent(),
                    false, null);
        } else if(DevPushNoticeTypeEnum.ALL.getValue().equals(noticeType)) {
            receiptInfo = DevPushWorkWechatUtil.pushWorkWechatText(devPushWorkWechatTextParam.getContent(),
                    true, null);
        } else if(DevPushNoticeTypeEnum.PHONE.getValue().equals(noticeType)) {
            if(ObjectUtil.isEmpty(devPushWorkWechatTextParam.getPhones())) {
                throw new CommonException("手机号不能为空");
            } else {
                receiptInfo = DevPushWorkWechatUtil.pushWorkWechatText(devPushWorkWechatTextParam.getContent(),
                        false, devPushWorkWechatTextParam.getPhones());
            }
        } else {
            throw new CommonException("不支持的通知类型：{}", noticeType);
        }
        DevPush devPush = new DevPush();
        devPush.setEngine(DevPushEngineTypeEnum.WORKWECHAT.getValue());
        devPush.setType(DevPushMessageTypeEnum.TEXT.getValue());
        devPush.setTitle(devPushWorkWechatTextParam.getContent());
        devPush.setContent(devPushWorkWechatTextParam.getContent());
        devPush.setReceiptInfo(receiptInfo);
        this.save(devPush);
    }

    @Override
    public void pushWorkWechatMarkdown(DevPushWorkWechatMarkdownParam devPushWorkWechatMarkdownParam) {
        String receiptInfo = DevPushWorkWechatUtil.pushWorkWechatMarkdown(devPushWorkWechatMarkdownParam.getTitle(),
                devPushWorkWechatMarkdownParam.getContent());
        DevPush devPush = new DevPush();
        devPush.setEngine(DevPushEngineTypeEnum.WORKWECHAT.getValue());
        devPush.setType(DevPushMessageTypeEnum.MARKDOWN.getValue());
        devPush.setTitle(devPushWorkWechatMarkdownParam.getTitle());
        devPush.setContent(devPushWorkWechatMarkdownParam.getContent());
        devPush.setReceiptInfo(receiptInfo);
        this.save(devPush);
    }

    @Override
    public void pushWorkWechatNews(DevPushWorkWechatNewsParam devPushWorkWechatNewsParam) {
        String receiptInfo = DevPushWorkWechatUtil.pushWorkWechatNews(devPushWorkWechatNewsParam.getTitle(),
                devPushWorkWechatNewsParam.getContent(),
                devPushWorkWechatNewsParam.getPicUrl(),
                devPushWorkWechatNewsParam.getMessageUrl());
        DevPush devPush = new DevPush();
        devPush.setEngine(DevPushEngineTypeEnum.WORKWECHAT.getValue());
        devPush.setType(DevPushMessageTypeEnum.NEWS.getValue());
        devPush.setTitle(devPushWorkWechatNewsParam.getTitle());
        devPush.setContent(devPushWorkWechatNewsParam.getContent());
        devPush.setReceiptInfo(receiptInfo);
        this.save(devPush);
    }

    @Override
    public Page<DevPush> page(DevPushPageParam devPushPageParam) {
        QueryWrapper<DevPush> queryWrapper = new QueryWrapper<DevPush>().checkSqlInjection();
        if(ObjectUtil.isNotEmpty(devPushPageParam.getEngine())) {
            queryWrapper.lambda().eq(DevPush::getEngine, devPushPageParam.getEngine());
        }
        if(ObjectUtil.isNotEmpty(devPushPageParam.getSearchKey())) {
            queryWrapper.lambda().like(DevPush::getTitle, devPushPageParam.getSearchKey());
        }
        if(ObjectUtil.isAllNotEmpty(devPushPageParam.getSortField(), devPushPageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(devPushPageParam.getSortOrder());
            queryWrapper.orderBy(true, devPushPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(devPushPageParam.getSortField()));
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<DevPushIdParam> devPushIdParamList) {
        this.removeByIds(CollStreamUtil.toList(devPushIdParamList, DevPushIdParam::getId));
    }

    @Override
    public DevPush detail(DevPushIdParam devPushIdParam) {
        return this.queryEntity(devPushIdParam.getId());
    }

    @Override
    public DevPush queryEntity(String id) {
        DevPush devPush = this.getById(id);
        if(ObjectUtil.isEmpty(devPush)) {
            throw new CommonException("消息推送记录不存在，id值为：{}", id);
        }
        return devPush;
    }
}
