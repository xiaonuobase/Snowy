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
package vip.xiaonuo.client.modular.relation.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vip.xiaonuo.client.modular.relation.entity.ClientRelation;
import vip.xiaonuo.client.modular.relation.mapper.ClientRelationMapper;
import vip.xiaonuo.client.modular.relation.service.ClientRelationService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * C端关系Service接口实现类
 *
 * @author xuyuxiang
 * @date 2022/2/23 18:43
 **/
@Service
public class ClientRelationServiceImpl extends ServiceImpl<ClientRelationMapper, ClientRelation> implements ClientRelationService {

    @Transactional(rollbackFor = Exception.class)
    public void saveRelation(String objectId, String targetId, String category, String extJson, boolean clear) {
        // 是否需要先删除关系
        if(clear) {
            this.remove(new LambdaQueryWrapper<ClientRelation>().eq(ClientRelation::getObjectId, objectId)
                    .eq(ClientRelation::getCategory, category));
        }
        ClientRelation clientRelation = new ClientRelation();
        clientRelation.setObjectId(objectId);
        clientRelation.setTargetId(targetId);
        clientRelation.setCategory(category);
        clientRelation.setExtJson(extJson);
        this.save(clientRelation);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveRelationBatch(String objectId, List<String> targetIdList, String category, List<String> extJsonList, boolean clear) {
        // 是否需要先删除关系
        if(clear) {
            this.remove(new LambdaQueryWrapper<ClientRelation>().eq(ClientRelation::getObjectId, objectId)
                    .eq(ClientRelation::getCategory, category));
        }
        List<ClientRelation> clientRelationList = CollectionUtil.newArrayList();
        for(int i = 0; i < targetIdList.size(); i++) {
            ClientRelation clientRelation = new ClientRelation();
            clientRelation.setObjectId(objectId);
            clientRelation.setTargetId(targetIdList.get(i));
            clientRelation.setCategory(category);
            if(ObjectUtil.isNotEmpty(extJsonList)) {
                clientRelation.setExtJson(extJsonList.get(i));
            }
            clientRelationList.add(clientRelation);
        }
        if(ObjectUtil.isNotEmpty(clientRelationList)) {
            this.saveBatch(clientRelationList);
        }
    }

    @Override
    public void saveRelationWithAppend(String objectId, String targetId, String category) {
        this.saveRelation(objectId, targetId, category, null, false);
    }

    @Override
    public void saveRelationWithAppend(String objectId, String targetId, String category, String extJson) {
        this.saveRelation(objectId, targetId, category, extJson, false);
    }

    @Override
    public void saveRelationBatchWithAppend(String objectId, List<String> targetIdList, String category) {
        this.saveRelationBatch(objectId, targetIdList, category, null, false);
    }

    @Override
    public void saveRelationBatchWithAppend(String objectId, List<String> targetIdList, String category, List<String> extJsonList) {
        this.saveRelationBatch(objectId, targetIdList, category, extJsonList, false);
    }

    @Override
    public void saveRelationWithClear(String objectId, String targetId, String category) {
        this.saveRelation(objectId, targetId, category, null, true);
    }

    @Override
    public void saveRelationWithClear(String objectId, String targetId, String category, String extJson) {
        this.saveRelation(objectId, targetId, category, extJson, true);
    }

    @Override
    public void saveRelationBatchWithClear(String objectId, List<String> targetIdList, String category) {
        this.saveRelationBatch(objectId, targetIdList, category, null, true);
    }

    @Override
    public void saveRelationBatchWithClear(String objectId, List<String> targetIdList, String category, List<String> extJsonList) {
        this.saveRelationBatch(objectId, targetIdList, category, extJsonList, true);
    }

    @Override
    public List<ClientRelation> getRelationListByObjectId(String objectId) {
        return this.getRelationListByObjectIdAndCategory(objectId, null);
    }

    @Override
    public List<ClientRelation> getRelationListByObjectIdList(List<String> objectIdList) {
        return this.getRelationListByObjectIdListAndCategory(objectIdList, null);
    }

    @Override
    public List<ClientRelation> getRelationListByObjectIdAndCategory(String objectId, String category) {
        LambdaQueryWrapper<ClientRelation> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ClientRelation::getObjectId, objectId);
        if(ObjectUtil.isNotEmpty(category)) {
            lambdaQueryWrapper.eq(ClientRelation::getCategory, category);
        }
        return this.list(lambdaQueryWrapper);
    }

    @Override
    public List<ClientRelation> getRelationListByObjectIdListAndCategory(List<String> objectIdList, String category) {
        LambdaQueryWrapper<ClientRelation> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(ClientRelation::getObjectId, objectIdList);
        if(ObjectUtil.isNotEmpty(category)) {
            lambdaQueryWrapper.eq(ClientRelation::getCategory, category);
        }
        return this.list(lambdaQueryWrapper);
    }

    @Override
    public List<ClientRelation> getRelationListByTargetId(String targetId) {
        return this.getRelationListByTargetIdAndCategory(targetId, null);
    }

    @Override
    public List<ClientRelation> getRelationListByTargetIdList(List<String> targetIdList) {
        return this.getRelationListByTargetIdListAndCategory(targetIdList, null);
    }

    @Override
    public List<ClientRelation> getRelationListByTargetIdAndCategory(String targetId, String category) {
        LambdaQueryWrapper<ClientRelation> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ClientRelation::getTargetId, targetId);
        if(ObjectUtil.isNotEmpty(category)) {
            lambdaQueryWrapper.eq(ClientRelation::getCategory, category);
        }
        return this.list(lambdaQueryWrapper);
    }

    @Override
    public List<ClientRelation> getRelationListByTargetIdListAndCategory(List<String> targetIdList, String category) {
        LambdaQueryWrapper<ClientRelation> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(ClientRelation::getTargetId, targetIdList);
        if(ObjectUtil.isNotEmpty(category)) {
            lambdaQueryWrapper.eq(ClientRelation::getCategory, category);
        }
        return this.list(lambdaQueryWrapper);
    }

    @Override
    public List<String> getRelationTargetIdListByObjectId(String objectId) {
        return this.getRelationTargetIdListByObjectIdAndCategory(objectId, null);
    }

    @Override
    public List<String> getRelationTargetIdListByObjectIdList(List<String> objectIdList) {
        return this.getRelationTargetIdListByObjectIdListAndCategory(objectIdList, null);
    }

    @Override
    public List<String> getRelationTargetIdListByObjectIdAndCategory(String objectId, String category) {
        return this.getRelationListByObjectIdAndCategory(objectId, category).stream()
                .map(ClientRelation::getTargetId).collect(Collectors.toList());
    }

    @Override
    public List<String> getRelationTargetIdListByObjectIdListAndCategory(List<String> objectIdList, String category) {
        return this.getRelationListByObjectIdListAndCategory(objectIdList, category).stream()
                .map(ClientRelation::getTargetId).collect(Collectors.toList());
    }

    @Override
    public List<String> getRelationObjectIdListByTargetId(String targetId) {
        return this.getRelationObjectIdListByTargetIdAndCategory(targetId, null);
    }

    @Override
    public List<String> getRelationObjectIdListByTargetIdList(List<String> targetIdList) {
        return this.getRelationObjectIdListByTargetIdListAndCategory(targetIdList, null);
    }

    @Override
    public List<String> getRelationObjectIdListByTargetIdAndCategory(String targetId, String category) {
        return this.getRelationListByTargetIdAndCategory(targetId, category).stream()
                .map(ClientRelation::getObjectId).collect(Collectors.toList());
    }

    @Override
    public List<String> getRelationObjectIdListByTargetIdListAndCategory(List<String> targetIdList, String category) {
        return this.getRelationListByTargetIdListAndCategory(targetIdList, category).stream()
                .map(ClientRelation::getObjectId).collect(Collectors.toList());
    }
}
