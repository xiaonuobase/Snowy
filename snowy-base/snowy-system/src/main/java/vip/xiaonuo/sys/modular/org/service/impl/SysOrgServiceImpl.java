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
package vip.xiaonuo.sys.modular.org.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vip.xiaonuo.core.consts.SymbolConstant;
import vip.xiaonuo.core.context.login.LoginContextHolder;
import vip.xiaonuo.core.enums.CommonStatusEnum;
import vip.xiaonuo.core.exception.PermissionException;
import vip.xiaonuo.core.exception.ServiceException;
import vip.xiaonuo.core.exception.enums.PermissionExceptionEnum;
import vip.xiaonuo.core.factory.PageFactory;
import vip.xiaonuo.core.factory.TreeBuildFactory;
import vip.xiaonuo.core.pojo.node.AntdBaseTreeNode;
import vip.xiaonuo.core.pojo.page.PageResult;
import vip.xiaonuo.core.util.PoiUtil;
import vip.xiaonuo.sys.core.enums.DataScopeTypeEnum;
import vip.xiaonuo.sys.modular.auth.service.AuthService;
import vip.xiaonuo.sys.modular.emp.service.SysEmpExtOrgPosService;
import vip.xiaonuo.sys.modular.emp.service.SysEmpService;
import vip.xiaonuo.sys.modular.org.entity.SysOrg;
import vip.xiaonuo.sys.modular.org.enums.SysOrgExceptionEnum;
import vip.xiaonuo.sys.modular.org.mapper.SysOrgMapper;
import vip.xiaonuo.sys.modular.org.param.SysOrgParam;
import vip.xiaonuo.sys.modular.org.service.SysOrgService;
import vip.xiaonuo.sys.modular.role.service.SysRoleDataScopeService;
import vip.xiaonuo.sys.modular.user.service.SysUserDataScopeService;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

/**
 * 系统组织机构service接口实现类
 *
 * @author xuyuxiang
 * @date 2020/3/13 16:02
 */
@Service
public class SysOrgServiceImpl extends ServiceImpl<SysOrgMapper, SysOrg> implements SysOrgService {

    @Resource
    private SysEmpService sysEmpService;

    @Resource
    private SysEmpExtOrgPosService sysEmpExtOrgPosService;

    @Resource
    private SysRoleDataScopeService sysRoleDataScopeService;

    @Resource
    private SysUserDataScopeService sysUserDataScopeService;

    @Resource
    private AuthService authService;

    @Override
    public PageResult<SysOrg> page(SysOrgParam sysOrgParam) {
        LambdaQueryWrapper<SysOrg> queryWrapper = new LambdaQueryWrapper<>();
        if (ObjectUtil.isNotNull(sysOrgParam)) {

            // 根据机构名称模糊查询
            if (ObjectUtil.isNotEmpty(sysOrgParam.getName())) {
                queryWrapper.like(SysOrg::getName, sysOrgParam.getName());
            }

            // 根据机构id查询
            if (ObjectUtil.isNotEmpty(sysOrgParam.getId())) {
                queryWrapper.eq(SysOrg::getId, sysOrgParam.getId());
            }

            // 根据父机构id查询
            if (ObjectUtil.isNotEmpty(sysOrgParam.getPid())) {
                queryWrapper
                        .eq(SysOrg::getId, sysOrgParam.getPid())
                        .or()
                        .like(SysOrg::getPids, sysOrgParam.getPid());
            }
        }

        boolean superAdmin = LoginContextHolder.me().isSuperAdmin();

        // 如果是超级管理员则获取所有组织机构，否则只获取其数据范围的机构数据
        if (!superAdmin) {
            List<Long> dataScope = sysOrgParam.getDataScope();
            if (ObjectUtil.isEmpty(dataScope)) {
                return new PageResult<>(new Page<>());
            } else {
                Set<Long> dataScopeSet = CollectionUtil.newHashSet(dataScope);
                dataScope.forEach(orgId -> {
                    //此处获取所有的上级节点，放入set，用于构造完整树
                    List<Long> parentAndChildIdListWithSelf = this.getParentIdListById(orgId);
                    dataScopeSet.addAll(parentAndChildIdListWithSelf);
                });
                queryWrapper.in(SysOrg::getId, dataScopeSet);
            }
        }

        // 查询启用状态的
        queryWrapper.eq(SysOrg::getStatus, CommonStatusEnum.ENABLE.getCode());
        //根据排序升序排列，序号越小越在前
        queryWrapper.orderByAsc(SysOrg::getSort);
        return new PageResult<>(this.page(PageFactory.defaultPage(), queryWrapper));
    }

    @Override
    public List<SysOrg> list(SysOrgParam sysOrgParam) {
        LambdaQueryWrapper<SysOrg> queryWrapper = new LambdaQueryWrapper<>();
        if (ObjectUtil.isNotNull(sysOrgParam)) {
            //根据父机构id查询
            if (ObjectUtil.isNotEmpty(sysOrgParam.getPid())) {
                queryWrapper.eq(SysOrg::getPid, sysOrgParam.getPid());
            }
        }
        //如果是超级管理员则获取所有组织机构，否则只获取其数据范围的机构数据
        boolean superAdmin = LoginContextHolder.me().isSuperAdmin();
        if (!superAdmin) {
            List<Long> dataScope = sysOrgParam.getDataScope();
            if (ObjectUtil.isEmpty(dataScope)) {
                return CollectionUtil.newArrayList();
            } else {
                Set<Long> dataScopeSet = CollectionUtil.newHashSet(dataScope);
                dataScope.forEach(orgId -> {
                    //此处获取所有的上级节点，放入set，用于构造完整树
                    List<Long> parentAndChildIdListWithSelf = this.getParentIdListById(orgId);
                    dataScopeSet.addAll(parentAndChildIdListWithSelf);
                });
                queryWrapper.in(SysOrg::getId, dataScopeSet);
            }
        }
        queryWrapper.eq(SysOrg::getStatus, CommonStatusEnum.ENABLE.getCode());
        //根据排序升序排列，序号越小越在前
        queryWrapper.orderByAsc(SysOrg::getSort);
        return this.list(queryWrapper);
    }

    @Override
    public void add(SysOrgParam sysOrgParam) {
        //校验参数，检查是否存在相同的名称和编码
        checkParam(sysOrgParam, false);
        //获取父id
        Long pid = sysOrgParam.getPid();
        boolean superAdmin = LoginContextHolder.me().isSuperAdmin();
        //如果登录用户不是超级管理员
        if (!superAdmin) {
            //如果新增的机构父id不是0，则进行数据权限校验
            if (!pid.equals(0L)) {
                List<Long> dataScope = sysOrgParam.getDataScope();
                //数据范围为空
                if (ObjectUtil.isEmpty(dataScope)) {
                    throw new PermissionException(PermissionExceptionEnum.NO_PERMISSION_OPERATE);
                } else if (!dataScope.contains(pid)) {
                    //所添加的组织机构的父机构不在自己的数据范围内
                    throw new PermissionException(PermissionExceptionEnum.NO_PERMISSION_OPERATE);
                }
            } else {
                //如果新增的机构父id是0，则根本没权限，只有超级管理员能添加父id为0的节点
                throw new PermissionException(PermissionExceptionEnum.NO_PERMISSION_OPERATE);
            }
        }

        SysOrg sysOrg = new SysOrg();
        BeanUtil.copyProperties(sysOrgParam, sysOrg);
        this.fillPids(sysOrg);
        sysOrg.setStatus(CommonStatusEnum.ENABLE.getCode());
        this.save(sysOrg);
        this.authService.refreshUserDataScope(sysOrg.getId());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<SysOrgParam> sysOrgParamList) {
        sysOrgParamList.forEach(sysOrgParam -> {
            SysOrg sysOrg = this.querySysOrg(sysOrgParam);
            Long id = sysOrg.getId();
            boolean superAdmin = LoginContextHolder.me().isSuperAdmin();

            // 级联删除子节点
            List<Long> childIdList = this.getChildIdListById(id);
            childIdList.add(id);

            childIdList.forEach(item ->{
                if (!superAdmin) {
                    List<Long> dataScope = LoginContextHolder.me().getLoginUserDataScopeIdList();
                    //数据范围为空
                    if (ObjectUtil.isEmpty(dataScope)) {
                        throw new PermissionException(PermissionExceptionEnum.NO_PERMISSION_OPERATE);
                    } else if (!dataScope.contains(item)) {
                        //所操作的数据不在自己的数据范围内
                        throw new PermissionException(PermissionExceptionEnum.NO_PERMISSION_OPERATE);
                    }
                }
                // 该机构下有员工，则不能删
                boolean hasOrgEmp = sysEmpService.hasOrgEmp(item);
                if (hasOrgEmp) {
                    throw new ServiceException(SysOrgExceptionEnum.ORG_CANNOT_DELETE);
                }

                // 该附属机构下若有员工，则不能删除
                boolean hasExtOrgEmp = sysEmpExtOrgPosService.hasExtOrgEmp(item);
                if (hasExtOrgEmp) {
                    throw new ServiceException(SysOrgExceptionEnum.ORG_CANNOT_DELETE);
                }
            });

            LambdaUpdateWrapper<SysOrg> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.in(SysOrg::getId, childIdList)
                    .set(SysOrg::getStatus, CommonStatusEnum.DELETED.getCode());
            this.update(updateWrapper);

            // 级联删除该机构及子机构对应的角色-数据范围关联信息
            sysRoleDataScopeService.deleteRoleDataScopeListByOrgIdList(childIdList);

            // 级联删除该机构子机构对应的用户-数据范围关联信息
            sysUserDataScopeService.deleteUserDataScopeListByOrgIdList(childIdList);
        });
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(SysOrgParam sysOrgParam) {

        SysOrg sysOrg = this.querySysOrg(sysOrgParam);
        Long id = sysOrg.getId();

        // 检测此人数据范围能不能操作这个公司
        boolean superAdmin = LoginContextHolder.me().isSuperAdmin();
        if (!superAdmin) {
            List<Long> dataScope = sysOrgParam.getDataScope();
            //数据范围为空
            if (ObjectUtil.isEmpty(dataScope)) {
                throw new PermissionException(PermissionExceptionEnum.NO_PERMISSION_OPERATE);
            }
            //数据范围中不包含本公司
            else if (!dataScope.contains(id)) {
                throw new PermissionException(PermissionExceptionEnum.NO_PERMISSION_OPERATE);
            }
        }

        //校验参数，检查是否存在相同的名称和编码
        checkParam(sysOrgParam, true);

        //如果名称有变化，则修改对应员工的机构相关信息
        if (!sysOrg.getName().equals(sysOrgParam.getName())) {
            sysEmpService.updateEmpOrgInfo(sysOrg.getId(), sysOrg.getName());
        }

        BeanUtil.copyProperties(sysOrgParam, sysOrg);
        this.fillPids(sysOrg);

        //不能修改状态，用修改状态接口修改状态
        sysOrg.setStatus(null);
        this.updateById(sysOrg);
        //将所有子的父id进行更新
        List<Long> childIdListById = this.getChildIdListById(sysOrg.getId());
        childIdListById.forEach(subChildId -> {
            SysOrg child = this.getById(subChildId);
            SysOrgParam childParam = new SysOrgParam();
            BeanUtil.copyProperties(child, childParam);
            this.edit(childParam);
        });
    }

    @Override
    public SysOrg detail(SysOrgParam sysOrgParam) {
        return this.querySysOrg(sysOrgParam);
    }

    @Override
    public List<AntdBaseTreeNode> tree(SysOrgParam sysOrgParam) {
        List<AntdBaseTreeNode> treeNodeList = CollectionUtil.newArrayList();
        LambdaQueryWrapper<SysOrg> queryWrapper = new LambdaQueryWrapper<>();

        // 如果是超级管理员则获取所有组织机构，否则只获取其数据范围的机构数据
        boolean superAdmin = LoginContextHolder.me().isSuperAdmin();
        if (!superAdmin) {
            List<Long> dataScope = sysOrgParam.getDataScope();
            if (ObjectUtil.isEmpty(dataScope)) {
                return treeNodeList;
            } else {
                Set<Long> dataScopeSet = CollectionUtil.newHashSet(dataScope);
                dataScope.forEach(orgId -> {
                    //此处获取所有的上级节点，放入set，用于构造完整树
                    List<Long> parentAndChildIdListWithSelf = this.getParentIdListById(orgId);
                    dataScopeSet.addAll(parentAndChildIdListWithSelf);
                });
                queryWrapper.in(SysOrg::getId, dataScopeSet);
            }
        }

        // 只查询未删除的
        queryWrapper.eq(SysOrg::getStatus, CommonStatusEnum.ENABLE.getCode());
        //根据排序升序排列，序号越小越在前
        queryWrapper.orderByAsc(SysOrg::getSort);
        this.list(queryWrapper).forEach(sysOrg -> {
            AntdBaseTreeNode orgTreeNode = new AntdBaseTreeNode();
            orgTreeNode.setId(sysOrg.getId());
            orgTreeNode.setParentId(sysOrg.getPid());
            orgTreeNode.setTitle(sysOrg.getName());
            orgTreeNode.setValue(String.valueOf(sysOrg.getId()));
            orgTreeNode.setWeight(sysOrg.getSort());
            treeNodeList.add(orgTreeNode);
        });

        return new TreeBuildFactory<AntdBaseTreeNode>().doTreeBuild(treeNodeList);
    }

    @Override
    public List<Long> getDataScopeListByDataScopeType(Integer dataScopeType, Long orgId) {
        List<Long> resultList = CollectionUtil.newArrayList();

        if (ObjectUtil.isEmpty(orgId)) {
            return CollectionUtil.newArrayList();
        }

        // 如果是范围类型是全部数据，则获取当前系统所有的组织架构id
        if (DataScopeTypeEnum.ALL.getCode().equals(dataScopeType)) {
            resultList = this.getOrgIdAll();
        }
        // 如果范围类型是本部门及以下部门，则查询本节点和子节点集合，包含本节点
        else if (DataScopeTypeEnum.DEPT_WITH_CHILD.getCode().equals(dataScopeType)) {
            resultList = this.getChildIdListWithSelfById(orgId);
        }
        // 如果数据范围是本部门，不含子节点，则直接返回本部门
        else if (DataScopeTypeEnum.DEPT.getCode().equals(dataScopeType)) {
            resultList.add(orgId);
        }

        return resultList;
    }

    /**
     * 根据条件获取组织机构id集合
     *
     * @author xuyuxiang
     * @date 2020/4/5 18:35
     */
    private List<Long> getOrgIdAll() {
        List<Long> resultList = CollectionUtil.newArrayList();

        LambdaQueryWrapper<SysOrg> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(SysOrg::getStatus, CommonStatusEnum.ENABLE.getCode());

        this.list(queryWrapper).forEach(sysOrg -> resultList.add(sysOrg.getId()));
        return resultList;
    }

    /**
     * 校验参数，检查是否存在相同的名称和编码
     *
     * @author xuyuxiang
     * @date 2020/3/25 21:23
     */
    private void checkParam(SysOrgParam sysOrgParam, boolean isExcludeSelf) {
        Long id = sysOrgParam.getId();
        String name = sysOrgParam.getName();
        String code = sysOrgParam.getCode();
        Long pid = sysOrgParam.getPid();

        //如果父id不是根节点
        if (!pid.equals(0L)) {
            SysOrg pOrg = this.getById(pid);
            if (ObjectUtil.isNull(pOrg)) {
                //父机构不存在
                throw new ServiceException(SysOrgExceptionEnum.ORG_NOT_EXIST);
            }
        }

        // 如果是编辑，父id和自己的id不能一致
        if (isExcludeSelf) {
            if (sysOrgParam.getId().equals(sysOrgParam.getPid())) {
                throw new ServiceException(SysOrgExceptionEnum.ID_CANT_EQ_PID);
            }

            // 如果是编辑，父id不能为自己的子节点
            List<Long> childIdListById = this.getChildIdListById(sysOrgParam.getId());
            if(ObjectUtil.isNotEmpty(childIdListById)) {
                if(childIdListById.contains(sysOrgParam.getPid())) {
                    throw new ServiceException(SysOrgExceptionEnum.PID_CANT_EQ_CHILD_ID);
                }
            }
        }

        LambdaQueryWrapper<SysOrg> queryWrapperByName = new LambdaQueryWrapper<>();
        queryWrapperByName.eq(SysOrg::getName, name)
                .ne(SysOrg::getStatus, CommonStatusEnum.DELETED.getCode());

        LambdaQueryWrapper<SysOrg> queryWrapperByCode = new LambdaQueryWrapper<>();
        queryWrapperByCode.eq(SysOrg::getCode, code)
                .ne(SysOrg::getStatus, CommonStatusEnum.DELETED.getCode());

        if (isExcludeSelf) {
            queryWrapperByName.ne(SysOrg::getId, id);
            queryWrapperByCode.ne(SysOrg::getId, id);
        }
        int countByName = this.count(queryWrapperByName);
        int countByCode = this.count(queryWrapperByCode);

        if (countByName >= 1) {
            throw new ServiceException(SysOrgExceptionEnum.ORG_NAME_REPEAT);
        }
        if (countByCode >= 1) {
            throw new ServiceException(SysOrgExceptionEnum.ORG_CODE_REPEAT);
        }
    }

    /**
     * 获取系统组织机构
     *
     * @author xuyuxiang
     * @date 2020/3/26 9:56
     */
    private SysOrg querySysOrg(SysOrgParam sysOrgParam) {
        SysOrg sysOrg = this.getById(sysOrgParam.getId());
        if (ObjectUtil.isNull(sysOrg)) {
            throw new ServiceException(SysOrgExceptionEnum.ORG_NOT_EXIST);
        }
        return sysOrg;
    }

    /**
     * 填充父ids
     *
     * @author xuyuxiang
     * @date 2020/3/26 11:28
     */
    private void fillPids(SysOrg sysOrg) {
        if (sysOrg.getPid().equals(0L)) {
            sysOrg.setPids(SymbolConstant.LEFT_SQUARE_BRACKETS +
                    0 +
                    SymbolConstant.RIGHT_SQUARE_BRACKETS +
                    SymbolConstant.COMMA);
        } else {
            //获取父组织机构
            SysOrg pSysOrg = this.getById(sysOrg.getPid());
            sysOrg.setPids(pSysOrg.getPids() +
                    SymbolConstant.LEFT_SQUARE_BRACKETS + pSysOrg.getId() +
                    SymbolConstant.RIGHT_SQUARE_BRACKETS +
                    SymbolConstant.COMMA);
        }
    }

    /**
     * 根据节点id获取所有子节点id集合
     *
     * @author xuyuxiang
     * @date 2020/3/26 11:31
     */
    private List<Long> getChildIdListById(Long id) {
        List<Long> childIdList = CollectionUtil.newArrayList();
        LambdaQueryWrapper<SysOrg> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.like(SysOrg::getPids, SymbolConstant.LEFT_SQUARE_BRACKETS + id +
                SymbolConstant.RIGHT_SQUARE_BRACKETS);

        this.list(queryWrapper).forEach(sysOrg -> childIdList.add(sysOrg.getId()));

        return childIdList;
    }

    /**
     * 根据节点id获取所有父节点id集合，不包含自己
     *
     * @author xuyuxiang
     * @date 2020/4/6 14:53
     */
    private List<Long> getParentIdListById(Long id) {
        List<Long> resultList = CollectionUtil.newArrayList();
        SysOrg sysOrg = this.getById(id);
        String pids = sysOrg.getPids();
        String pidsWithRightSymbol = StrUtil.removeAll(pids, SymbolConstant.LEFT_SQUARE_BRACKETS);
        String pidsNormal = StrUtil.removeAll(pidsWithRightSymbol, SymbolConstant.RIGHT_SQUARE_BRACKETS);
        String[] pidsNormalArr = pidsNormal.split(SymbolConstant.COMMA);
        for (String pid : pidsNormalArr) {
            resultList.add(Convert.toLong(pid));
        }
        return resultList;
    }

    /**
     * 根据节点id获取所有子节点id集合，包含自己
     *
     * @author xuyuxiang
     * @date 2020/4/6 14:54
     */
    private List<Long> getChildIdListWithSelfById(Long id) {
        List<Long> childIdListById = this.getChildIdListById(id);
        List<Long> resultList = CollectionUtil.newArrayList(childIdListById);
        resultList.add(id);
        return resultList;
    }

    /**
     * 根据节点id获取父节点和子节点id集合，包含自己
     *
     * @author xuyuxiang
     * @date 2020/4/7 16:50
     */
    private List<Long> getParentAndChildIdListWithSelfById(Long id) {
        Set<Long> resultSet = CollectionUtil.newHashSet();
        List<Long> parentIdListById = this.getParentIdListById(id);
        List<Long> childIdListById = this.getChildIdListWithSelfById(id);
        resultSet.addAll(parentIdListById);
        resultSet.addAll(childIdListById);
        return CollectionUtil.newArrayList(resultSet);
    }

    @Override
    public void export(SysOrgParam sysOrgParam) {
        List<SysOrg> list = this.list(sysOrgParam);
        PoiUtil.exportExcelWithStream("SysOrg.xls", SysOrg.class, list);
    }
}
