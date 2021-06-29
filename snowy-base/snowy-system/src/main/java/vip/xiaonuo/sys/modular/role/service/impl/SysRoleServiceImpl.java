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

Snowy采用APACHE LICENSE 2.0开源协议，您在使用过程中，需要注意以下几点：

1.请不要删除和修改根目录下的LICENSE文件。
2.请不要删除和修改Snowy源码头部的版权声明。
3.请保留源码和相关描述文件的项目出处，作者声明等。
4.分发源码时候，请注明软件出处 https://gitee.com/xiaonuobase/snowy
5.在修改包名，模块名称，项目代码等时，请注明软件出处 https://gitee.com/xiaonuobase/snowy
6.若您的项目无法满足以上几点，可申请商业授权，获取Snowy商业授权许可，请在官网购买授权，地址为 https://www.xiaonuo.vip
 */
package vip.xiaonuo.sys.modular.role.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vip.xiaonuo.core.consts.CommonConstant;
import vip.xiaonuo.core.consts.SymbolConstant;
import vip.xiaonuo.core.context.login.LoginContextHolder;
import vip.xiaonuo.core.enums.CommonStatusEnum;
import vip.xiaonuo.core.exception.PermissionException;
import vip.xiaonuo.core.exception.ServiceException;
import vip.xiaonuo.core.exception.enums.PermissionExceptionEnum;
import vip.xiaonuo.core.factory.PageFactory;
import vip.xiaonuo.core.pojo.page.PageResult;
import vip.xiaonuo.sys.core.enums.DataScopeTypeEnum;
import vip.xiaonuo.sys.modular.org.service.SysOrgService;
import vip.xiaonuo.sys.modular.role.entity.SysRole;
import vip.xiaonuo.sys.modular.role.enums.SysRoleExceptionEnum;
import vip.xiaonuo.sys.modular.role.mapper.SysRoleMapper;
import vip.xiaonuo.sys.modular.role.param.SysRoleParam;
import vip.xiaonuo.sys.modular.role.service.SysRoleDataScopeService;
import vip.xiaonuo.sys.modular.role.service.SysRoleMenuService;
import vip.xiaonuo.sys.modular.role.service.SysRoleService;
import vip.xiaonuo.sys.modular.user.service.SysUserRoleService;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 系统角色service接口实现类
 *
 * @author xuyuxiang
 * @date 2020/3/13 15:55
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    @Resource
    private SysUserRoleService sysUserRoleService;

    @Resource
    private SysRoleMenuService sysRoleMenuService;

    @Resource
    private SysRoleDataScopeService sysRoleDataScopeService;

    @Resource
    private SysOrgService sysOrgService;

    @Override
    public List<Dict> getLoginRoles(Long userId) {
        List<Dict> dictList = CollectionUtil.newArrayList();
        //获取用户角色id集合
        List<Long> roleIdList = sysUserRoleService.getUserRoleIdList(userId);
        if (ObjectUtil.isNotEmpty(roleIdList)) {
            LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(SysRole::getId, roleIdList).eq(SysRole::getStatus, CommonStatusEnum.ENABLE.getCode());
            //根据角色id集合查询并返回结果
            dictList = this.list(queryWrapper).stream().map(sysRole -> {
                Dict dict = Dict.create();
                dict.put(CommonConstant.ID, sysRole.getId());
                dict.put(CommonConstant.CODE, sysRole.getCode());
                dict.put(CommonConstant.NAME, sysRole.getName());
                return dict;
            }).collect(Collectors.toList());
        }
        return dictList;
    }

    @Override
    public PageResult<SysRole> page(SysRoleParam sysRoleParam) {
        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
        if (ObjectUtil.isNotNull(sysRoleParam)) {
            //根据名称模糊查询
            if (ObjectUtil.isNotEmpty(sysRoleParam.getName())) {
                queryWrapper.like(SysRole::getName, sysRoleParam.getName());
            }
            //根据编码模糊查询
            if (ObjectUtil.isNotEmpty(sysRoleParam.getCode())) {
                queryWrapper.like(SysRole::getCode, sysRoleParam.getCode());
            }
        }

        queryWrapper.eq(SysRole::getStatus, CommonStatusEnum.ENABLE.getCode());
        //根据排序升序排列，序号越小越在前
        queryWrapper.orderByAsc(SysRole::getSort);
        return new PageResult<>(this.page(PageFactory.defaultPage(), queryWrapper));
    }

    @Override
    public List<Dict> list(SysRoleParam sysRoleParam) {
        List<Dict> dictList = CollectionUtil.newArrayList();
        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
        if (ObjectUtil.isNotNull(sysRoleParam)) {
            //根据角色名称或编码模糊查询
            if (ObjectUtil.isNotEmpty(sysRoleParam.getName())) {
                queryWrapper.and(i -> i.like(SysRole::getName, sysRoleParam.getName())
                        .or().like(SysRole::getCode, sysRoleParam.getName()));
            }
        }
        //只查询正常状态
        queryWrapper.eq(SysRole::getStatus, CommonStatusEnum.ENABLE.getCode());
        //根据排序升序排列，序号越小越在前
        queryWrapper.orderByAsc(SysRole::getSort);
        this.list(queryWrapper).forEach(sysRole -> {
            Dict dict = Dict.create();
            dict.put(CommonConstant.ID, sysRole.getId());
            dict.put(CommonConstant.NAME, sysRole.getName() + SymbolConstant.LEFT_SQUARE_BRACKETS
                    + sysRole.getCode() + SymbolConstant.RIGHT_SQUARE_BRACKETS);
            dictList.add(dict);
        });
        return dictList;
    }

    @Override
    public List<Dict> dropDown() {
        List<Dict> dictList = CollectionUtil.newArrayList();
        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
        //如果当前登录用户不是超级管理员，则查询自己拥有的
        if (!LoginContextHolder.me().isSuperAdmin()) {

            //查询自己拥有的
            List<String> loginUserRoleIds = LoginContextHolder.me().getLoginUserRoleIds();
            if (ObjectUtil.isEmpty(loginUserRoleIds)) {
                return dictList;
            }
            queryWrapper.in(SysRole::getId, loginUserRoleIds);
        }
        //只查询正常状态
        queryWrapper.eq(SysRole::getStatus, CommonStatusEnum.ENABLE.getCode());
        this.list(queryWrapper)
                .forEach(sysRole -> {
                    Dict dict = Dict.create();
                    dict.put(CommonConstant.ID, sysRole.getId());
                    dict.put(CommonConstant.CODE, sysRole.getCode());
                    dict.put(CommonConstant.NAME, sysRole.getName());
                    dictList.add(dict);
                });
        return dictList;
    }

    @Override
    public void add(SysRoleParam sysRoleParam) {
        //校验参数，检查是否存在相同的名称和编码
        checkParam(sysRoleParam, false);
        SysRole sysRole = new SysRole();
        BeanUtil.copyProperties(sysRoleParam, sysRole);
        sysRole.setStatus(CommonStatusEnum.ENABLE.getCode());
        this.save(sysRole);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(SysRoleParam sysRoleParam) {
        SysRole sysRole = this.querySysRole(sysRoleParam);
        sysRole.setStatus(CommonStatusEnum.DELETED.getCode());
        this.updateById(sysRole);
        Long id = sysRole.getId();
        //级联删除该角色对应的角色-数据范围关联信息
        sysRoleDataScopeService.deleteRoleDataScopeListByRoleId(id);

        //级联删除该角色对应的用户-角色表关联信息
        sysUserRoleService.deleteUserRoleListByRoleId(id);

        //级联删除该角色对应的角色-菜单表关联信息
        sysRoleMenuService.deleteRoleMenuListByRoleId(id);
    }

    @Override
    public void edit(SysRoleParam sysRoleParam) {
        SysRole sysRole = this.querySysRole(sysRoleParam);
        //校验参数，检查是否存在相同的名称和编码
        checkParam(sysRoleParam, true);
        BeanUtil.copyProperties(sysRoleParam, sysRole);
        //不能修改状态，用修改状态接口修改状态
        sysRole.setStatus(null);
        this.updateById(sysRole);
    }

    @Override
    public SysRole detail(SysRoleParam sysRoleParam) {
        return this.querySysRole(sysRoleParam);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void grantMenu(SysRoleParam sysRoleParam) {
        this.querySysRole(sysRoleParam);
        sysRoleMenuService.grantMenu(sysRoleParam);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void grantData(SysRoleParam sysRoleParam) {
        SysRole sysRole = this.querySysRole(sysRoleParam);
        boolean superAdmin = LoginContextHolder.me().isSuperAdmin();
        //如果登录用户不是超级管理员，则进行数据权限校验
        if (!superAdmin) {
            Integer dataScopeType = sysRoleParam.getDataScopeType();
            //如果授权的角色的数据范围类型为全部，则没权限，只有超级管理员有
            if (DataScopeTypeEnum.ALL.getCode().equals(dataScopeType)) {
                throw new PermissionException(PermissionExceptionEnum.NO_PERMISSION_OPERATE);
            }
            //如果授权的角色数据范围类型为自定义，则要判断授权的数据范围是否在自己的数据范围内
            if (DataScopeTypeEnum.DEFINE.getCode().equals(dataScopeType)) {
                List<Long> dataScope = sysRoleParam.getDataScope();
                //要授权的数据范围列表
                List<Long> grantOrgIdList = sysRoleParam.getGrantOrgIdList();
                if (ObjectUtil.isNotEmpty(grantOrgIdList)) {
                    //数据范围为空
                    if (ObjectUtil.isEmpty(dataScope)) {
                        throw new PermissionException(PermissionExceptionEnum.NO_PERMISSION_OPERATE);
                    } else if (!dataScope.containsAll(grantOrgIdList)) {
                        //所要授权的数据不在自己的数据范围内
                        throw new PermissionException(PermissionExceptionEnum.NO_PERMISSION_OPERATE);
                    }
                }
            }
        }
        sysRole.setDataScopeType(sysRoleParam.getDataScopeType());
        this.updateById(sysRole);
        sysRoleDataScopeService.grantDataScope(sysRoleParam);
    }

    @Override
    public List<Long> getUserDataScopeIdList(List<Long> roleIdList, Long orgId) {
        Set<Long> resultList = CollectionUtil.newHashSet();

        //定义角色中最大数据范围的类型，目前系统按最大范围策略来，如果你同时拥有ALL和SELF的权限，系统最后按ALL返回
        Integer strongerDataScopeType = DataScopeTypeEnum.SELF.getCode();

        //获取用户自定义数据范围的角色集合
        List<Long> customDataScopeRoleIdList = CollectionUtil.newArrayList();
        if (ObjectUtil.isNotEmpty(roleIdList)) {
            List<SysRole> sysRoleList = this.listByIds(roleIdList);
            for (SysRole sysRole : sysRoleList) {
                if (DataScopeTypeEnum.DEFINE.getCode().equals(sysRole.getDataScopeType())) {
                    customDataScopeRoleIdList.add(sysRole.getId());
                } else {
                    if (sysRole.getDataScopeType() <= strongerDataScopeType) {
                        strongerDataScopeType = sysRole.getDataScopeType();
                    }
                }
            }
        }

        //自定义数据范围的角色对应的数据范围
        List<Long> roleDataScopeIdList = sysRoleDataScopeService.getRoleDataScopeIdList(customDataScopeRoleIdList);

        //角色中拥有最大数据范围类型的数据范围
        List<Long> dataScopeIdList = sysOrgService.getDataScopeListByDataScopeType(strongerDataScopeType, orgId);

        resultList.addAll(dataScopeIdList);
        resultList.addAll(roleDataScopeIdList);
        return CollectionUtil.newArrayList(resultList);
    }

    @Override
    public String getNameByRoleId(Long roleId) {
        SysRole sysRole = this.getById(roleId);
        if (ObjectUtil.isEmpty(sysRole)) {
            throw new ServiceException(SysRoleExceptionEnum.ROLE_NOT_EXIST);
        }
        return sysRole.getName();
    }

    @Override
    public List<Long> ownMenu(SysRoleParam sysRoleParam) {
        SysRole sysRole = this.querySysRole(sysRoleParam);
        return sysRoleMenuService.getRoleMenuIdList(CollectionUtil.newArrayList(sysRole.getId()));
    }

    @Override
    public List<Long> ownData(SysRoleParam sysRoleParam) {
        SysRole sysRole = this.querySysRole(sysRoleParam);
        return sysRoleDataScopeService.getRoleDataScopeIdList(CollectionUtil.newArrayList(sysRole.getId()));
    }

    /**
     * 校验参数，检查是否存在相同的名称和编码
     *
     * @author xuyuxiang
     * @date 2020/3/28 14:59
     */
    private void checkParam(SysRoleParam sysRoleParam, boolean isExcludeSelf) {
        Long id = sysRoleParam.getId();
        String name = sysRoleParam.getName();
        String code = sysRoleParam.getCode();

        LambdaQueryWrapper<SysRole> queryWrapperByName = new LambdaQueryWrapper<>();
        queryWrapperByName.eq(SysRole::getName, name)
                .ne(SysRole::getStatus, CommonStatusEnum.DELETED.getCode());

        LambdaQueryWrapper<SysRole> queryWrapperByCode = new LambdaQueryWrapper<>();
        queryWrapperByCode.eq(SysRole::getCode, code)
                .ne(SysRole::getStatus, CommonStatusEnum.DELETED.getCode());

        //是否排除自己，如果排除自己则不查询自己的id
        if (isExcludeSelf) {
            queryWrapperByName.ne(SysRole::getId, id);
            queryWrapperByCode.ne(SysRole::getId, id);
        }
        int countByName = this.count(queryWrapperByName);
        int countByCode = this.count(queryWrapperByCode);

        if (countByName >= 1) {
            throw new ServiceException(SysRoleExceptionEnum.ROLE_NAME_REPEAT);
        }
        if (countByCode >= 1) {
            throw new ServiceException(SysRoleExceptionEnum.ROLE_CODE_REPEAT);
        }
    }

    /**
     * 获取系统角色
     *
     * @author xuyuxiang
     * @date 2020/3/28 14:59
     */
    private SysRole querySysRole(SysRoleParam sysRoleParam) {
        SysRole sysRole = this.getById(sysRoleParam.getId());
        if (ObjectUtil.isNull(sysRole)) {
            throw new ServiceException(SysRoleExceptionEnum.ROLE_NOT_EXIST);
        }
        return sysRole;
    }
}
