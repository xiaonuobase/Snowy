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
package vip.xiaonuo.sys.modular.pos.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vip.xiaonuo.core.enums.CommonStatusEnum;
import vip.xiaonuo.core.exception.ServiceException;
import vip.xiaonuo.core.factory.PageFactory;
import vip.xiaonuo.core.pojo.page.PageResult;
import vip.xiaonuo.core.util.PoiUtil;
import vip.xiaonuo.sys.modular.emp.service.SysEmpExtOrgPosService;
import vip.xiaonuo.sys.modular.emp.service.SysEmpPosService;
import vip.xiaonuo.sys.modular.pos.entity.SysPos;
import vip.xiaonuo.sys.modular.pos.enums.SysPosExceptionEnum;
import vip.xiaonuo.sys.modular.pos.mapper.SysPosMapper;
import vip.xiaonuo.sys.modular.pos.param.SysPosParam;
import vip.xiaonuo.sys.modular.pos.service.SysPosService;

import javax.annotation.Resource;
import java.util.List;

/**
 * 系统职位service接口实现类
 *
 * @author xuyuxiang yubaoshan
 * @date 2020/3/13 16:01
 */
@Service
public class SysPosServiceImpl extends ServiceImpl<SysPosMapper, SysPos> implements SysPosService {

    @Resource
    private SysEmpPosService sysEmpPosService;

    @Resource
    private SysEmpExtOrgPosService sysEmpExtOrgPosService;

    @Override
    public PageResult<SysPos> page(SysPosParam sysPosParam) {
        LambdaQueryWrapper<SysPos> queryWrapper = new LambdaQueryWrapper<>();
        if (ObjectUtil.isNotNull(sysPosParam)) {
            //根据职位名称模糊查询
            if (ObjectUtil.isNotEmpty(sysPosParam.getName())) {
                queryWrapper.like(SysPos::getName, sysPosParam.getName());
            }
            //根据职位编码模糊查询
            if (ObjectUtil.isNotEmpty(sysPosParam.getCode())) {
                queryWrapper.like(SysPos::getCode, sysPosParam.getCode());
            }
        }
        queryWrapper.eq(SysPos::getStatus, CommonStatusEnum.ENABLE.getCode());
        //根据排序升序排列，序号越小越在前
        queryWrapper.orderByAsc(SysPos::getSort);
        return new PageResult<>(this.page(PageFactory.defaultPage(), queryWrapper));
    }

    @Override
    public List<SysPos> list(SysPosParam sysPosParam) {
        LambdaQueryWrapper<SysPos> queryWrapper = new LambdaQueryWrapper<>();
        if (ObjectUtil.isNotNull(sysPosParam)) {
            //根据职位编码模糊查询
            if (ObjectUtil.isNotEmpty(sysPosParam.getCode())) {
                queryWrapper.eq(SysPos::getCode, sysPosParam.getCode());
            }
        }
        queryWrapper.eq(SysPos::getStatus, CommonStatusEnum.ENABLE.getCode());
        //根据排序升序排列，序号越小越在前
        queryWrapper.orderByAsc(SysPos::getSort);
        return this.list(queryWrapper);
    }

    @Override
    public void add(SysPosParam sysPosParam) {
        //校验参数，检查是否存在相同的名称和编码
        checkParam(sysPosParam, false);
        SysPos sysPos = new SysPos();
        BeanUtil.copyProperties(sysPosParam, sysPos);
        sysPos.setStatus(CommonStatusEnum.ENABLE.getCode());
        this.save(sysPos);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<SysPosParam> sysPosParamList) {
        sysPosParamList.forEach(sysPosParam -> {
            SysPos sysPos = this.querySysPos(sysPosParam);
            Long id = sysPos.getId();
            //该职位下是否有员工
            boolean hasPosEmp = sysEmpPosService.hasPosEmp(id);
            //只要还有，则不能删
            if (hasPosEmp) {
                throw new ServiceException(SysPosExceptionEnum.POS_CANNOT_DELETE);
            }
            //该附属职位下是否有员工
            boolean hasExtPosEmp = sysEmpExtOrgPosService.hasExtPosEmp(id);
            //只要还有，则不能删
            if (hasExtPosEmp) {
                throw new ServiceException(SysPosExceptionEnum.POS_CANNOT_DELETE);
            }
            sysPos.setStatus(CommonStatusEnum.DELETED.getCode());
            this.updateById(sysPos);
        });
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(SysPosParam sysPosParam) {
        SysPos sysPos = this.querySysPos(sysPosParam);
        //校验参数，检查是否存在相同的名称和编码
        checkParam(sysPosParam, true);
        BeanUtil.copyProperties(sysPosParam, sysPos);
        //不能修改状态，用修改状态接口修改状态
        sysPos.setStatus(null);
        this.updateById(sysPos);
    }

    @Override
    public SysPos detail(SysPosParam sysPosParam) {
        return this.querySysPos(sysPosParam);
    }

    /**
     * 校验参数，检查是否存在相同的名称和编码
     *
     * @author xuyuxiang
     * @date 2020/3/25 21:23
     */
    private void checkParam(SysPosParam sysPosParam, boolean isExcludeSelf) {
        Long id = sysPosParam.getId();
        String name = sysPosParam.getName();
        String code = sysPosParam.getCode();

        LambdaQueryWrapper<SysPos> queryWrapperByName = new LambdaQueryWrapper<>();
        queryWrapperByName.eq(SysPos::getName, name)
                .ne(SysPos::getStatus, CommonStatusEnum.DELETED.getCode());

        LambdaQueryWrapper<SysPos> queryWrapperByCode = new LambdaQueryWrapper<>();
        queryWrapperByCode.eq(SysPos::getCode, code)
                .ne(SysPos::getStatus, CommonStatusEnum.DELETED.getCode());

        if (isExcludeSelf) {
            queryWrapperByName.ne(SysPos::getId, id);
            queryWrapperByCode.ne(SysPos::getId, id);
        }

        int countByName = this.count(queryWrapperByName);
        int countByCode = this.count(queryWrapperByCode);

        if (countByName >= 1) {
            throw new ServiceException(SysPosExceptionEnum.POS_NAME_REPEAT);
        }
        if (countByCode >= 1) {
            throw new ServiceException(SysPosExceptionEnum.POS_CODE_REPEAT);
        }
    }

    /**
     * 获取系统职位
     *
     * @author xuyuxiang
     * @date 2020/3/26 9:56
     */
    private SysPos querySysPos(SysPosParam sysPosParam) {
        SysPos sysPos = this.getById(sysPosParam.getId());
        if (ObjectUtil.isNull(sysPos)) {
            throw new ServiceException(SysPosExceptionEnum.POS_NOT_EXIST);
        }
        return sysPos;
    }

    @Override
    public void export(SysPosParam sysPosParam) {
        List<SysPos> list = this.list(sysPosParam);
        PoiUtil.exportExcelWithStream("SnowyPos.xls", SysPos.class, list);
    }
}
