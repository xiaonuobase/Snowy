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
package vip.xiaonuo.sys.modular.user.provider;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.sys.api.SysUserApi;
import vip.xiaonuo.sys.modular.user.entity.SysUser;
import vip.xiaonuo.sys.modular.user.param.SysUserGrantRoleParam;
import vip.xiaonuo.sys.modular.user.param.SysUserIdParam;
import vip.xiaonuo.sys.modular.user.param.SysUserSelectorUserParam;
import vip.xiaonuo.sys.modular.user.service.SysUserService;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * 用户API接口提供者
 *
 * @author xuyuxiang
 * @date 2022/6/20 18:24
 **/
@Service
public class SysUserApiProvider implements SysUserApi {

    @Resource
    private SysUserService sysUserService;

    @Override
    public JSONObject getUserByIdWithoutException(String userId) {
        SysUser sysUser = sysUserService.getById(userId);
        if(ObjectUtil.isNotEmpty(sysUser)) {
            return JSONUtil.parseObj(sysUser);
        }
        return null;
    }

    @Override
    public List<JSONObject> getUserListByIdListWithoutException(List<String> userIdList) {
        return sysUserService.listByIds(userIdList).stream().map(JSONUtil::parseObj).collect(Collectors.toList());
    }

    @Override
    public JSONObject getUserByIdWithException(String userId) {
        return JSONUtil.parseObj(sysUserService.queryEntity(userId));
    }

    @Override
    public List<JSONObject> getUserListByIdWithException(List<String> userIdList) {
        HashSet<String> userIdSet = CollectionUtil.newHashSet(userIdList);
        List<SysUser> sysUserList = sysUserService.listByIds(userIdSet);
        if(sysUserList.size() != userIdSet.size()) {
            throw new CommonException("某用户不存在，id值集合为：{}", userIdSet);
        }
        return sysUserList.stream().map(JSONUtil::parseObj).collect(Collectors.toList());
    }

    @Override
    public List<String> ownRole(String userId) {
        SysUserIdParam sysUserIdParam = new SysUserIdParam();
        sysUserIdParam.setId(userId);
        return sysUserService.ownRole(sysUserIdParam);
    }

    @Override
    public void grantRole(String userId, List<String> roleIdList) {
        SysUserGrantRoleParam sysUserGrantRoleParam = new SysUserGrantRoleParam();
        sysUserGrantRoleParam.setId(userId);
        sysUserGrantRoleParam.setRoleIdList(roleIdList);
        sysUserService.grantRole(sysUserGrantRoleParam);
    }

    @Override
    public List<String> getUserIdListByOrgIdList(List<String> orgIdList) {
        if(ObjectUtil.isNotEmpty(orgIdList)) {
            String orConditionSql = orgIdList.stream().map(orgId -> "POSITION_JSON LIKE '%" + orgId + "%'")
                    .collect(Collectors.joining(" OR "));
            return sysUserService.list(new LambdaQueryWrapper<SysUser>().in(SysUser::getOrgId, orgIdList)
                    .or(q-> q.apply(orConditionSql))).stream().map(SysUser::getId).collect(Collectors.toList());

        }
        return CollectionUtil.newArrayList();
    }

    @Override
    public List<String> getUserIdListByPositionIdList(List<String> positionIdList) {
        if(ObjectUtil.isNotEmpty(positionIdList)) {
            String orConditionSql = positionIdList.stream().map(positionId -> "POSITION_JSON LIKE '%" + positionId + "%'")
                    .collect(Collectors.joining(" OR "));
            return sysUserService.list(new LambdaQueryWrapper<SysUser>().in(SysUser::getPositionId, positionIdList)
                    .or(q-> q.apply(orConditionSql))).stream().map(SysUser::getId).collect(Collectors.toList());
        }
        return CollectionUtil.newArrayList();
    }

    @Override
    public JSONObject getSupervisorIdBySupervisorLevel(List<String> userIdList, String userId, String orgId, String supervisorLevel) {
        SysUser sysUser = sysUserService.queryEntity(userId);
        String userOrgId = sysUser.getOrgId();
        String positionJson = sysUser.getPositionJson();
        AtomicReference<String> result = new AtomicReference<>();
        // 先查主职位主管
        if(ObjectUtil.isAllNotEmpty(userOrgId)) {
            if(userOrgId.equals(orgId)) {
                String directorId = sysUser.getDirectorId();
                if(ObjectUtil.isNotEmpty(directorId)) {
                    result.set(directorId);
                }
            }
        }
        // 再查兼职职位主管，需排除userOrgId的兼职主管
        if(ObjectUtil.isEmpty(result.get())) {
            if(ObjectUtil.isNotEmpty(positionJson)) {
                JSONUtil.parseArray(positionJson).forEach(object -> {
                    JSONObject jsonObject = JSONUtil.parseObj(object);
                    String partTimeOrgId = jsonObject.getStr("orgId");
                    if(ObjectUtil.isNotEmpty(partTimeOrgId) && !partTimeOrgId.equals(userOrgId) && orgId.equals(partTimeOrgId)) {
                        String partTimDirectorId = jsonObject.getStr("directorId");
                        if(ObjectUtil.isNotEmpty(partTimDirectorId)) {
                            // 存在多个相同兼职职位，后者覆盖前者
                            result.set(partTimDirectorId);
                        }
                    }
                });
            }
        }
        // 查询结果
        String resultUserId = result.get();
        // 如果要求查最高层级主管
        if(supervisorLevel.equals("-1")) {
            if(ObjectUtil.isEmpty(resultUserId)) {
                // 查不到，则当前用户就是最高层级主管
                if(ObjectUtil.isEmpty(userIdList)) {
                    return JSONUtil.createObj().set("id", userId).set("idList", CollectionUtil.newArrayList(userId));
                } else {
                    return JSONUtil.createObj().set("id", userId).set("idList", userIdList);
                }
            } else {
                if(ObjectUtil.isNotEmpty(userIdList)) {
                    if(userIdList.contains(resultUserId)) {
                        // 如果查出的结果已经出现过，意味着出现了循环主管，则当前的userId就是最高级主管
                        return JSONUtil.createObj().set("id", userId).set("idList", userIdList);
                    }
                }
                // 如果结果为空，则将查询的结果放入集合
                userIdList.add(resultUserId);
                // 继续查询
                return this.getSupervisorIdBySupervisorLevel(userIdList, resultUserId, orgId, supervisorLevel);
            }
        } else {
            // 如果要求查指定层级主管
            if(ObjectUtil.isEmpty(resultUserId)) {
                // 最直接主管查不到，则没有上级之说
                return JSONUtil.createObj().set("id", null).set("idList", CollectionUtil.newArrayList());
            } else {
                // 由最低级向高级查，首先主管级别减1
                String nextLevel = Convert.toStr(Convert.toInt(supervisorLevel) - 1);
                // 如果减1后级别为0，表示查一级直属主管
                if(nextLevel.equals("0")) {
                    // 则当前结果就是一级直属主管
                    return JSONUtil.createObj().set("id", resultUserId).set("idList", CollectionUtil.newArrayList(resultUserId));
                } else {
                    if(ObjectUtil.isNotEmpty(userIdList)) {
                        if(userIdList.contains(resultUserId)) {
                            // 如果查出的结果已经出现过，意味着出现了循环主管，则当前的userId就暂定是满足层级的主管
                            return JSONUtil.createObj().set("id", userId).set("idList", userIdList);
                        }
                    }
                    // 如果查出的结果没有出现过，则将查询的结果放入集合
                    userIdList.add(resultUserId);
                    // 继续查更高层级主管
                    return this.getSupervisorIdBySupervisorLevel(userIdList, resultUserId, orgId, nextLevel);
                }
            }
        }
    }

    @Override
    public List<String> getMulSupervisorIdListByEndLevel(String userId, String orgId, String endLevel) {
        List<String> resultList = CollectionUtil.newArrayList();
        if(endLevel.equals("-1")) {
            List<String> idList = this.getSupervisorIdBySupervisorLevel(CollectionUtil.newArrayList(), userId, orgId, endLevel)
                    .getBeanList("idList", String.class);
            if(ObjectUtil.isNotEmpty(idList)) {
                resultList.addAll(idList);
            }
        } else {
            Integer levelValue = Convert.toInt(endLevel);
            for (int i = 1; i < levelValue; i++) {
                String supervisorId = this.getSupervisorIdBySupervisorLevel(CollectionUtil.newArrayList(), userId, orgId,
                        Convert.toStr(i)).getStr("id");
                if(ObjectUtil.isNotEmpty(supervisorId)) {
                    resultList.add(supervisorId);
                } else {
                    break;
                }
            }
        }
        return resultList;
    }

    @SuppressWarnings("ALL")
    @Override
    public Page<JSONObject> userSelector(String orgId, String searchKey) {
        SysUserSelectorUserParam sysUserSelectorUserParam = new SysUserSelectorUserParam();
        sysUserSelectorUserParam.setOrgId(orgId);
        sysUserSelectorUserParam.setSearchKey(searchKey);
        return BeanUtil.toBean(sysUserService.userSelector(sysUserSelectorUserParam), Page.class);
    }

    @Override
    public List<JSONObject> listUserWithoutCurrent() {
        return sysUserService.list(new LambdaQueryWrapper<SysUser>()
                .select(SysUser::getId, SysUser::getAccount, SysUser::getName, SysUser::getAvatar)
                .ne(SysUser::getId, StpUtil.getLoginId()))
                .stream().map(JSONUtil::parseObj).collect(Collectors.toList());
    }
}
