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
package vip.xiaonuo.dev.modular.message.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vip.xiaonuo.common.enums.CommonSortOrderEnum;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.common.page.CommonPageRequest;
import vip.xiaonuo.dev.modular.message.entity.DevMessage;
import vip.xiaonuo.dev.modular.message.enums.DevMessageCategoryEnum;
import vip.xiaonuo.dev.modular.message.mapper.DevMessageMapper;
import vip.xiaonuo.dev.modular.message.param.DevMessageIdParam;
import vip.xiaonuo.dev.modular.message.param.DevMessageListParam;
import vip.xiaonuo.dev.modular.message.param.DevMessagePageParam;
import vip.xiaonuo.dev.modular.message.param.DevMessageSendParam;
import vip.xiaonuo.dev.modular.message.result.DevMessageResult;
import vip.xiaonuo.dev.modular.message.service.DevMessageService;
import vip.xiaonuo.dev.modular.relation.entity.DevRelation;
import vip.xiaonuo.dev.modular.relation.enums.DevRelationCategoryEnum;
import vip.xiaonuo.dev.modular.relation.service.DevRelationService;
import vip.xiaonuo.sys.api.SysUserApi;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 站内信Service接口实现类
 *
 * @author xuyuxiang
 * @date 2022/6/21 14:56
 **/
@Service
public class DevMessageServiceImpl extends ServiceImpl<DevMessageMapper, DevMessage> implements DevMessageService {

    @Resource
    private SysUserApi sysUserApi;

    @Resource
    private DevRelationService devRelationService;


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void send(DevMessageSendParam devMessageSendParam) {
        devMessageSendParam.setContent(ObjectUtil.isEmpty(devMessageSendParam.getContent())?
                devMessageSendParam.getSubject():devMessageSendParam.getContent());
        devMessageSendParam.setCategory(ObjectUtil.isEmpty(devMessageSendParam.getCategory())?
                DevMessageCategoryEnum.SYS.getValue() :devMessageSendParam.getCategory());
        DevMessage devMessage = BeanUtil.toBean(devMessageSendParam, DevMessage.class);
        this.save(devMessage);
        List<String> receiverIdList = devMessageSendParam.getReceiverIdList();
        if(ObjectUtil.isNotEmpty(receiverIdList)) {
            List<String> extJsonList = receiverIdList.stream().map(userId -> JSONUtil.toJsonStr(JSONUtil.createObj()
                    .set("read", false))).collect(Collectors.toList());
            devRelationService.saveRelationBatchWithAppend(devMessage.getId(), receiverIdList,
                    DevRelationCategoryEnum.MSG_TO_USER.getValue(), extJsonList);
        }
    }

    @Override
    public Page<DevMessage> page(DevMessagePageParam devMessagePageParam) {
        QueryWrapper<DevMessage> queryWrapper = new QueryWrapper<DevMessage>().checkSqlInjection();
        if(ObjectUtil.isNotEmpty(devMessagePageParam.getSearchKey())) {
            queryWrapper.lambda().like(DevMessage::getSubject, devMessagePageParam.getSearchKey());
        }
        if(ObjectUtil.isNotEmpty(devMessagePageParam.getReceiveUserId())) {
            queryWrapper.lambda().in(DevMessage::getId, devRelationService.list(new LambdaQueryWrapper<DevRelation>()
                    .eq(DevRelation::getCategory, DevRelationCategoryEnum.MSG_TO_USER.getValue())
                    .eq(DevRelation::getTargetId, devMessagePageParam.getReceiveUserId())).stream()
                    .map(DevRelation::getObjectId).collect(Collectors.toList()));
        }
        if(ObjectUtil.isAllNotEmpty(devMessagePageParam.getSortField(), devMessagePageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(devMessagePageParam.getSortOrder());
            queryWrapper.orderBy(true, devMessagePageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(devMessagePageParam.getSortField()));
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Override
    public Page<JSONObject> page(List<String> receiverIdList, String category) {
        if(ObjectUtil.isNotEmpty(receiverIdList)) {
            List<DevRelation> messageRelationList = devRelationService
                    .getRelationListByTargetIdListAndCategory(receiverIdList, DevRelationCategoryEnum.MSG_TO_USER.getValue());
            List<String> messageIdList = messageRelationList.stream().map(DevRelation::getObjectId).collect(Collectors.toList());
            if(ObjectUtil.isNotEmpty(messageIdList)) {
                LambdaQueryWrapper<DevMessage> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.in(DevMessage::getId, messageIdList).orderByDesc(DevMessage::getCreateTime);
                if(ObjectUtil.isNotEmpty(category)) {
                    lambdaQueryWrapper.eq(DevMessage::getCategory, category);
                }
                Page<JSONObject> resultPage = new Page<>();
                Page<DevMessage> devMessagePage = this.page(CommonPageRequest.defaultPage(), lambdaQueryWrapper);
                BeanUtil.copyProperties(devMessagePage, resultPage);
                List<String> relationMessageIdList = CollStreamUtil.toList(messageRelationList, DevRelation::getObjectId);
                List<JSONObject> jsonObjectList = devMessagePage.getRecords().stream().map(obj -> {
                    JSONObject jsonObject = JSONUtil.parseObj(obj);
                    jsonObject.set("read", JSONUtil.parseObj(messageRelationList.get(relationMessageIdList
                            .indexOf(jsonObject.getStr("id"))).getExtJson()).getBool("read"));
                    return jsonObject;
                }).collect(Collectors.toList());
                CollectionUtil.sort(jsonObjectList, Comparator.comparingInt(jsonObject -> Convert.toInt(jsonObject.getBool("read"))));
                resultPage.setRecords(jsonObjectList);
                return resultPage;
            }
            return CommonPageRequest.defaultPage();
        }
        return CommonPageRequest.defaultPage();
    }

    @Override
    public List<DevMessage> list(DevMessageListParam devMessageListParam) {
        if(ObjectUtil.isNotEmpty(devMessageListParam.getReceiverIdList())) {
            List<String> messageIdList = devRelationService
                    .getRelationListByTargetIdListAndCategory(devMessageListParam.getReceiverIdList(),
                    DevRelationCategoryEnum.MSG_TO_USER.getValue()).stream().filter(devRelation -> JSONUtil
                            .parseObj(devRelation.getExtJson()).getBool("read").equals(false))
                    .map(DevRelation::getObjectId).collect(Collectors.toList());
            if(ObjectUtil.isNotEmpty(messageIdList)) {
                LambdaQueryWrapper<DevMessage> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.in(DevMessage::getId, messageIdList).orderByDesc(DevMessage::getCreateTime);
                return this.page(CommonPageRequest.defaultPage(),lambdaQueryWrapper).getRecords();
            }
            return CollectionUtil.newArrayList();
        }
        return CollectionUtil.newArrayList();
    }

    @Override
    public Long unreadCount(String loginId){
        return devRelationService.getRelationListByTargetIdAndCategory(loginId,
                DevRelationCategoryEnum.MSG_TO_USER.getValue()).stream().filter(devRelation -> JSONUtil
                .parseObj(devRelation.getExtJson()).getBool("read").equals(false)).count();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<DevMessageIdParam> devMessageIdParamList) {
        List<String> devMessageIdList = CollStreamUtil.toList(devMessageIdParamList, DevMessageIdParam::getId);
        if(ObjectUtil.isNotEmpty(devMessageIdList)) {
            // 清除站内信与用户关联关系
            devRelationService.remove(new LambdaUpdateWrapper<DevRelation>().eq(DevRelation::getCategory, DevRelationCategoryEnum.MSG_TO_USER.getValue())
                    .in(DevRelation::getObjectId, devMessageIdList));
            // 执行删除
            this.removeByIds(devMessageIdList);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public DevMessageResult detail(DevMessageIdParam devMessageIdParam) {
        DevMessageResult devMessageResult = new DevMessageResult();
        DevMessage devMessage = this.queryEntity(devMessageIdParam.getId());
        BeanUtil.copyProperties(devMessage, devMessageResult);
        DevRelation myMessage = devRelationService.getOne(new LambdaQueryWrapper<DevRelation>()
                .eq(DevRelation::getObjectId, devMessage.getId()).eq(DevRelation::getTargetId, StpUtil.getLoginIdAsString())
                .eq(DevRelation::getCategory, DevRelationCategoryEnum.MSG_TO_USER.getValue()));
        if(ObjectUtil.isNotEmpty(myMessage)) {
            // 设置为已读
            String myMessageExtJson = JSONUtil.toJsonStr(JSONUtil.parseObj(myMessage.getExtJson()).set("read", true));
            devRelationService.update(new LambdaUpdateWrapper<DevRelation>()
                    .eq(DevRelation::getObjectId, devMessage.getId()).eq(DevRelation::getTargetId, StpUtil.getLoginIdAsString())
                    .eq(DevRelation::getCategory, DevRelationCategoryEnum.MSG_TO_USER.getValue()).set(DevRelation::getExtJson, myMessageExtJson));
        }
        List<DevMessageResult.DevReceiveInfo> receiveInfoList = devRelationService.getRelationListByObjectIdAndCategory(devMessage.getId(),
                DevRelationCategoryEnum.MSG_TO_USER.getValue()).stream().map(devRelation -> {
            DevMessageResult.DevReceiveInfo devReceiveInfo = new DevMessageResult.DevReceiveInfo();
            JSONObject userObj = null;
            try {
                userObj = sysUserApi.getUserByIdWithException(devRelation.getTargetId());
            }
            catch (Exception e) {
                // 收件人中包含删除用户 在此处做处理
            }
            String userName = "未知用户";
            if(ObjectUtil.isNotEmpty(userObj)) {
                userName = userObj.getStr("name");
            }
            devReceiveInfo.setReceiveUserId(devRelation.getTargetId());
            devReceiveInfo.setReceiveUserName(userName);
            devReceiveInfo.setRead(JSONUtil.parseObj(devRelation.getExtJson()).getBool("read"));
            return devReceiveInfo;
        }).collect(Collectors.toList());
        devMessageResult.setReceiveInfoList(receiveInfoList);
        return devMessageResult;
    }

    @Override
    public DevMessage queryEntity(String id) {
        DevMessage devMessage = this.getById(id);
        if(ObjectUtil.isEmpty(devMessage)) {
            throw new CommonException("站内信不存在，id值为：{}", id);
        }
        return devMessage;
    }

}
