/*
Copyright [2020] [https://www.xiaonuo.vip]

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

XiaoNuo采用APACHE LICENSE 2.0开源协议，您在使用过程中，需要注意以下几点：

1.请不要删除和修改根目录下的LICENSE文件。
2.请不要删除和修改XiaoNuo源码头部的版权声明。
3.请保留源码和相关描述文件的项目出处，作者声明等。
4.分发源码时候，请注明软件出处 https://gitee.com/xiaonuobase/xiaonuo-vue
5.在修改包名，模块名称，项目代码等时，请注明软件出处 https://gitee.com/xiaonuobase/xiaonuo-vue
6.若您的项目无法满足以上几点，可申请商业授权，获取XiaoNuo商业授权许可，请在官网购买授权，地址为 https://www.xiaonuo.vip
 */
package com.cn.xiaonuo.sys.modular.user.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cn.xiaonuo.sys.modular.role.service.SysRoleService;
import com.cn.xiaonuo.sys.modular.user.entity.SysUserRole;
import com.cn.xiaonuo.sys.modular.user.mapper.SysUserRoleMapper;
import com.cn.xiaonuo.sys.modular.user.param.SysUserParam;
import com.cn.xiaonuo.sys.modular.user.service.SysUserRoleService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 系统用户角色service接口实现类
 *
 * @author xuyuxiang
 * @date 2020/3/13 15:48
 */
@Service
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole> implements SysUserRoleService {

    @Resource
    private SysRoleService sysRoleService;

    @Override
    public List<Long> getUserRoleIdList(Long userId) {
        List<Long> roleIdList = CollectionUtil.newArrayList();
        LambdaQueryWrapper<SysUserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUserRole::getUserId, userId);
        this.list(queryWrapper).forEach(sysUserRole -> roleIdList.add(sysUserRole.getRoleId()));
        return roleIdList;
    }

    @Override
    public void grantRole(SysUserParam sysUserParam) {
        Long userId = sysUserParam.getId();
        //删除所拥有角色
        LambdaQueryWrapper<SysUserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUserRole::getUserId, userId);
        this.remove(queryWrapper);
        //授权角色
        sysUserParam.getGrantRoleIdList().forEach(roleId -> {
            SysUserRole sysUserRole = new SysUserRole();
            sysUserRole.setUserId(userId);
            sysUserRole.setRoleId(roleId);
            this.save(sysUserRole);
        });
    }

    @Override
    public List<Long> getUserRoleDataScopeIdList(Long userId, Long orgId) {
        List<Long> roleIdList = CollectionUtil.newArrayList();

        // 获取用户所有角色
        LambdaQueryWrapper<SysUserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUserRole::getUserId, userId);
        this.list(queryWrapper).forEach(sysUserRole -> roleIdList.add(sysUserRole.getRoleId()));

        // 获取这些角色对应的数据范围
        if (ObjectUtil.isNotEmpty(roleIdList)) {
            return sysRoleService.getUserDataScopeIdList(roleIdList, orgId);
        }
        return CollectionUtil.newArrayList();
    }

    @Override
    public void deleteUserRoleListByRoleId(Long roleId) {
        LambdaQueryWrapper<SysUserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUserRole::getRoleId, roleId);
        this.remove(queryWrapper);
    }

    @Override
    public void deleteUserRoleListByUserId(Long userId) {
        LambdaQueryWrapper<SysUserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUserRole::getUserId, userId);
        this.remove(queryWrapper);
    }
}
