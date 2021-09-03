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
package vip.xiaonuo.sys.modular.emp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vip.xiaonuo.core.pojo.login.LoginEmpInfo;
import vip.xiaonuo.sys.modular.emp.entity.SysEmp;
import vip.xiaonuo.sys.modular.emp.mapper.SysEmpMapper;
import vip.xiaonuo.sys.modular.emp.param.SysEmpParam;
import vip.xiaonuo.sys.modular.emp.result.SysEmpInfo;
import vip.xiaonuo.sys.modular.emp.service.SysEmpExtOrgPosService;
import vip.xiaonuo.sys.modular.emp.service.SysEmpPosService;
import vip.xiaonuo.sys.modular.emp.service.SysEmpService;
import vip.xiaonuo.sys.modular.user.service.SysUserService;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 员工service接口实现类
 *
 * @author xuyuxiang
 * @date 2020/3/13 15:10
 */
@Service
public class SysEmpServiceImpl extends ServiceImpl<SysEmpMapper, SysEmp> implements SysEmpService {

    @Resource
    private SysEmpExtOrgPosService sysEmpExtOrgPosService;

    @Resource
    private SysEmpPosService sysEmpPosService;

    @Resource
    private SysUserService sysUserService;

    @Override
    public LoginEmpInfo getLoginEmpInfo(Long empId) {

        LoginEmpInfo loginEmpInfo = new LoginEmpInfo();
        //获取员工信息
        SysEmp sysEmp = this.getById(empId);
        if (ObjectUtil.isNotNull(sysEmp)) {
            BeanUtil.copyProperties(sysEmp, loginEmpInfo);
            //获取附属机构和职位信息
            List<Dict> empExtOrgPosDictList = sysEmpExtOrgPosService.getEmpExtOrgPosDictList(sysEmp.getId(), false);
            loginEmpInfo.setExtOrgPos(empExtOrgPosDictList);

            //获取所属职位信息
            List<Dict> empEmpPosDictList = sysEmpPosService.getEmpPosDictList(sysEmp.getId(), false);
            loginEmpInfo.setPositions(empEmpPosDictList);
        } else {
            loginEmpInfo.setExtOrgPos(CollectionUtil.newArrayList());
            loginEmpInfo.setPositions(CollectionUtil.newArrayList());
        }
        return loginEmpInfo;
    }

    @Override
    public SysEmpInfo getSysEmpInfo(Long empId) {
        SysEmpInfo sysEmpInfo = new SysEmpInfo();
        //获取员工信息
        SysEmp sysEmp = this.getById(empId);
        if (ObjectUtil.isNotNull(sysEmp)) {
            BeanUtil.copyProperties(sysEmp, sysEmpInfo);
            //获取附属机构和职位信息
            List<Dict> empExtOrgPosDictList = sysEmpExtOrgPosService.getEmpExtOrgPosDictList(sysEmp.getId(), true);
            sysEmpInfo.setExtOrgPos(empExtOrgPosDictList);

            //获取所属职位信息
            List<Dict> empEmpPosDictList = sysEmpPosService.getEmpPosDictList(sysEmp.getId(), true);
            sysEmpInfo.setPositions(empEmpPosDictList);
        } else {
            sysEmpInfo.setExtOrgPos(CollectionUtil.newArrayList());
            sysEmpInfo.setPositions(CollectionUtil.newArrayList());
        }
        return sysEmpInfo;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addOrUpdate(SysEmpParam sysEmpParam) {
        Long empId = sysEmpParam.getId();
        SysEmp sysEmp = this.getById(empId);
        if (ObjectUtil.isNull(sysEmp)) {
            sysEmp = new SysEmp();
        }
        BeanUtil.copyProperties(sysEmpParam, sysEmp);
        this.saveOrUpdate(sysEmp);
        //编辑附属机构职位相关信息
        List<Dict> extIdList = sysEmpParam.getExtIds();
        sysEmpExtOrgPosService.addOrEdit(empId, extIdList);
        //编辑职位相关信息
        List<Long> posIdList = sysEmpParam.getPosIdList();
        sysEmpPosService.addOrEdit(empId, posIdList);
    }

    @Override
    public void updateEmpOrgInfo(Long orgId, String orgName) {
        LambdaQueryWrapper<SysEmp> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysEmp::getOrgId, orgId);
        this.list(queryWrapper).forEach(sysEmp -> {
            sysEmp.setOrgName(orgName);
            this.updateById(sysEmp);
        });
    }

    @Override
    public boolean hasOrgEmp(Long orgId) {
        LambdaQueryWrapper<SysEmp> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysEmp::getOrgId, orgId);
        List<SysEmp> list = this.list(queryWrapper);
        if(ObjectUtil.isEmpty(list)) {
            return false;
        } else {
            Set<Long> collect = list.stream().map(SysEmp::getId).collect(Collectors.toSet());
            return sysUserService.hasAllDeletedUser(collect);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteEmpInfoByUserId(Long empId) {
        //删除员工信息
        this.removeById(empId);

        //级联删除对应的员工-附属信息
        sysEmpExtOrgPosService.deleteEmpExtInfoByUserId(empId);

        //级联删除对用的员工-职位信息
        sysEmpPosService.deleteEmpPosInfoByUserId(empId);
    }
}
