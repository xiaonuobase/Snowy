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

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vip.xiaonuo.auth.core.pojo.SaBaseLoginUser;
import vip.xiaonuo.sys.modular.org.entity.SysUserDataScope;
import vip.xiaonuo.sys.modular.org.entity.SysUserDataScopeMap;
import vip.xiaonuo.sys.modular.org.mapper.SysUserDataScopeMapMapper;
import vip.xiaonuo.sys.modular.org.mapper.SysUserDataScopeMapper;
import vip.xiaonuo.sys.modular.org.service.SysUserDataScopeService;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户数据范围预计算Service实现类
 * <p>
 * 优化策略：
 * 1. 作用域去重：将orgId集合相同的API分组，共享同一个SCOPE_KEY，避免重复存储
 * 2. 增量刷新：对比新旧数据，只执行必要的增删操作，减少数据库IO
 * </p>
 *
 * @author yubaoshan
 * @date 2026/2/12
 **/
@Slf4j
@Service
public class SysUserDataScopeServiceImpl extends ServiceImpl<SysUserDataScopeMapper, SysUserDataScope>
        implements SysUserDataScopeService {

    @Resource
    private SysUserDataScopeMapMapper sysUserDataScopeMapMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void refreshByUserId(String userId, List<SaBaseLoginUser.DataScope> dataScopeList) {
        if (ObjectUtil.isEmpty(dataScopeList)) {
            // 无数据范围，清空该用户所有记录
            this.deleteByUserId(userId);
            return;
        }
        // 1. 按orgId集合分组，计算SCOPE_KEY
        // key=scopeKey, value=orgId集合
        Map<String, List<String>> scopeKeyToOrgIds = new LinkedHashMap<>();
        // key=apiUrl, value=scopeKey
        Map<String, String> apiToScopeKey = new LinkedHashMap<>();
        for (SaBaseLoginUser.DataScope dataScope : dataScopeList) {
            if (dataScope.isScopeAll() || ObjectUtil.isEmpty(dataScope.getDataScope())) {
                continue;
            }
            List<String> sortedOrgIds = dataScope.getDataScope().stream()
                    .distinct().sorted().collect(Collectors.toList());
            String scopeKey = SecureUtil.md5(String.join(",", sortedOrgIds));
            apiToScopeKey.put(dataScope.getApiUrl(), scopeKey);
            scopeKeyToOrgIds.putIfAbsent(scopeKey, sortedOrgIds);
        }
        if (apiToScopeKey.isEmpty()) {
            this.deleteByUserId(userId);
            return;
        }
        // 2. 增量刷新MAP表
        Map<String, String> existingMap = sysUserDataScopeMapMapper.selectList(
                new LambdaQueryWrapper<SysUserDataScopeMap>()
                        .eq(SysUserDataScopeMap::getUserId, userId)
        ).stream().collect(Collectors.toMap(SysUserDataScopeMap::getApiUrl, SysUserDataScopeMap::getScopeKey));

        // 需要删除的MAP记录（旧的有，新的没有）
        Set<String> apiToDelete = new HashSet<>(existingMap.keySet());
        apiToDelete.removeAll(apiToScopeKey.keySet());
        if (!apiToDelete.isEmpty()) {
            sysUserDataScopeMapMapper.delete(new LambdaQueryWrapper<SysUserDataScopeMap>()
                    .eq(SysUserDataScopeMap::getUserId, userId)
                    .in(SysUserDataScopeMap::getApiUrl, apiToDelete));
        }
        // 需要新增或更新的MAP记录
        List<SysUserDataScopeMap> mapToInsert = new ArrayList<>();
        List<SysUserDataScopeMap> mapToUpdate = new ArrayList<>();
        for (Map.Entry<String, String> entry : apiToScopeKey.entrySet()) {
            String apiUrl = entry.getKey();
            String newScopeKey = entry.getValue();
            String oldScopeKey = existingMap.get(apiUrl);
            if (oldScopeKey == null) {
                // 新增
                SysUserDataScopeMap map = new SysUserDataScopeMap();
                map.setUserId(userId);
                map.setApiUrl(apiUrl);
                map.setScopeKey(newScopeKey);
                mapToInsert.add(map);
            } else if (!oldScopeKey.equals(newScopeKey)) {
                // scope变了，更新
                SysUserDataScopeMap map = new SysUserDataScopeMap();
                map.setUserId(userId);
                map.setApiUrl(apiUrl);
                map.setScopeKey(newScopeKey);
                mapToUpdate.add(map);
            }
            // scope没变的跳过
        }
        for (SysUserDataScopeMap map : mapToInsert) {
            sysUserDataScopeMapMapper.insert(map);
        }
        for (SysUserDataScopeMap map : mapToUpdate) {
            SysUserDataScopeMap updateEntity = new SysUserDataScopeMap();
            updateEntity.setScopeKey(map.getScopeKey());
            sysUserDataScopeMapMapper.update(updateEntity, new LambdaQueryWrapper<SysUserDataScopeMap>()
                    .eq(SysUserDataScopeMap::getUserId, map.getUserId())
                    .eq(SysUserDataScopeMap::getApiUrl, map.getApiUrl()));
        }

        // 3. 增量刷新SCOPE表
        // 查询该用户当前所有scope记录
        Set<String> existingScopeKeys = this.list(
                new LambdaQueryWrapper<SysUserDataScope>()
                        .select(SysUserDataScope::getScopeKey)
                        .eq(SysUserDataScope::getUserId, userId)
                        .groupBy(SysUserDataScope::getScopeKey)
        ).stream().map(SysUserDataScope::getScopeKey).collect(Collectors.toSet());

        // 新的需要的scopeKey集合
        Set<String> neededScopeKeys = new HashSet<>(scopeKeyToOrgIds.keySet());

        // 删除不再需要的scope记录
        Set<String> scopeKeysToDelete = new HashSet<>(existingScopeKeys);
        scopeKeysToDelete.removeAll(neededScopeKeys);
        if (!scopeKeysToDelete.isEmpty()) {
            this.remove(new LambdaQueryWrapper<SysUserDataScope>()
                    .eq(SysUserDataScope::getUserId, userId)
                    .in(SysUserDataScope::getScopeKey, scopeKeysToDelete));
        }

        // 新增缺失的scope记录
        Set<String> scopeKeysToAdd = new HashSet<>(neededScopeKeys);
        scopeKeysToAdd.removeAll(existingScopeKeys);
        if (!scopeKeysToAdd.isEmpty()) {
            List<SysUserDataScope> newRecords = new ArrayList<>();
            for (String scopeKey : scopeKeysToAdd) {
                for (String orgId : scopeKeyToOrgIds.get(scopeKey)) {
                    SysUserDataScope record = new SysUserDataScope();
                    record.setUserId(userId);
                    record.setScopeKey(scopeKey);
                    record.setOrgId(orgId);
                    newRecords.add(record);
                }
            }
            if (!newRecords.isEmpty()) {
                this.saveBatch(newRecords);
            }
        }

        int totalScopeRecords = scopeKeyToOrgIds.values().stream().mapToInt(List::size).sum();
        log.debug(">>> 刷新用户数据范围预计算，userId={}，API数={}，去重后scope组={}，scope记录数={}",
                userId, apiToScopeKey.size(), scopeKeyToOrgIds.size(), totalScopeRecords);
    }

    @Override
    public void deleteByUserId(String userId) {
        sysUserDataScopeMapMapper.delete(new LambdaQueryWrapper<SysUserDataScopeMap>()
                .eq(SysUserDataScopeMap::getUserId, userId));
        this.remove(new LambdaQueryWrapper<SysUserDataScope>()
                .eq(SysUserDataScope::getUserId, userId));
    }

    @Override
    public void deleteByOrgIds(List<String> orgIds) {
        if (ObjectUtil.isEmpty(orgIds)) {
            return;
        }
        this.remove(new LambdaQueryWrapper<SysUserDataScope>()
                .in(SysUserDataScope::getOrgId, orgIds));
        log.info(">>> 删除机构数据范围预计算，orgIds数量={}", orgIds.size());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void appendOrgIdsForUser(String userId, List<String> orgIds) {
        if (ObjectUtil.isEmpty(orgIds)) {
            return;
        }
        // 查询该用户所有的scopeKey（去重）
        Set<String> existingScopeKeys = this.list(
                new LambdaQueryWrapper<SysUserDataScope>()
                        .select(SysUserDataScope::getScopeKey)
                        .eq(SysUserDataScope::getUserId, userId)
                        .groupBy(SysUserDataScope::getScopeKey)
        ).stream().map(SysUserDataScope::getScopeKey).collect(Collectors.toSet());

        if (ObjectUtil.isEmpty(existingScopeKeys)) {
            return;
        }
        // 查询已存在的(scopeKey, orgId)组合，避免重复插入
        Set<String> existingKeys = this.list(
                new LambdaQueryWrapper<SysUserDataScope>()
                        .select(SysUserDataScope::getScopeKey, SysUserDataScope::getOrgId)
                        .eq(SysUserDataScope::getUserId, userId)
                        .in(SysUserDataScope::getOrgId, orgIds)
        ).stream().map(r -> r.getScopeKey() + "|" + r.getOrgId()).collect(Collectors.toSet());

        // 为每个scopeKey追加新的机构ID
        List<SysUserDataScope> newRecords = new ArrayList<>();
        for (String scopeKey : existingScopeKeys) {
            for (String orgId : orgIds) {
                if (!existingKeys.contains(scopeKey + "|" + orgId)) {
                    SysUserDataScope record = new SysUserDataScope();
                    record.setUserId(userId);
                    record.setScopeKey(scopeKey);
                    record.setOrgId(orgId);
                    newRecords.add(record);
                }
            }
        }
        if (ObjectUtil.isNotEmpty(newRecords)) {
            this.saveBatch(newRecords);
        }
    }
}
