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
package vip.xiaonuo.sys.modular.consts.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import vip.xiaonuo.core.context.constant.ConstantContext;
import vip.xiaonuo.core.enums.CommonStatusEnum;
import vip.xiaonuo.core.enums.YesOrNotEnum;
import vip.xiaonuo.core.exception.ServiceException;
import vip.xiaonuo.core.factory.PageFactory;
import vip.xiaonuo.core.pojo.page.PageResult;
import vip.xiaonuo.sys.modular.consts.entity.SysConfig;
import vip.xiaonuo.sys.modular.consts.enums.SysConfigExceptionEnum;
import vip.xiaonuo.sys.modular.consts.mapper.SysConfigMapper;
import vip.xiaonuo.sys.modular.consts.param.SysConfigParam;
import vip.xiaonuo.sys.modular.consts.service.SysConfigService;

import java.util.List;


/**
 * 系统参数配置service接口实现类
 *
 * @author xuyuxiang
 * @date 2020/4/14 11:16
 */
@Service
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig> implements SysConfigService {

    @Override
    public PageResult<SysConfig> page(SysConfigParam sysConfigParam) {

        //构造查询条件
        LambdaQueryWrapper<SysConfig> queryWrapper = new LambdaQueryWrapper<>();

        if (ObjectUtil.isNotNull(sysConfigParam)) {
            //如果名称不为空，则带上名称搜素搜条件
            if (ObjectUtil.isNotEmpty(sysConfigParam.getName())) {
                queryWrapper.like(SysConfig::getName, sysConfigParam.getName());
            }
            //如果常量编码不为空，则带上常量编码搜素搜条件
            if (ObjectUtil.isNotEmpty(sysConfigParam.getCode())) {
                queryWrapper.like(SysConfig::getCode, sysConfigParam.getCode());
            }
            //如果分类编码不为空，则带上分类编码
            if (ObjectUtil.isNotEmpty(sysConfigParam.getGroupCode())) {
                queryWrapper.eq(SysConfig::getGroupCode, sysConfigParam.getGroupCode());
            }
        }

        //查询未删除的
        queryWrapper.ne(SysConfig::getStatus, CommonStatusEnum.DELETED.getCode());

        //按类型升序排列，同类型的排在一起
        queryWrapper.orderByDesc(SysConfig::getGroupCode);

        //查询分页结果
        return new PageResult<>(this.page(PageFactory.defaultPage(), queryWrapper));
    }

    @Override
    public List<SysConfig> list(SysConfigParam sysConfigParam) {

        //构造条件
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();

        //查询未删除的
        wrapper.ne(SysConfig::getStatus, CommonStatusEnum.DELETED.getCode());

        return this.list(wrapper);
    }

    @Override
    public SysConfig detail(SysConfigParam sysConfigParam) {
        return this.querySysConfig(sysConfigParam);
    }

    @Override
    public void add(SysConfigParam sysConfigParam) {

        //1.校验参数，是否有重复的名称和编码，不排除当前记录
        checkRepeatParam(sysConfigParam, false);

        //2.构造实体
        SysConfig sysConfig = new SysConfig();
        BeanUtil.copyProperties(sysConfigParam, sysConfig);
        sysConfig.setStatus(CommonStatusEnum.ENABLE.getCode());

        //3.保存到库中
        this.save(sysConfig);

        //4.添加对应context
        ConstantContext.putConstant(sysConfigParam.getCode(), sysConfigParam.getValue());
    }

    @Override
    public void delete(SysConfigParam sysConfigParam) {

        //1.根据id获取常量
        SysConfig sysConfig = this.querySysConfig(sysConfigParam);

        //2.不能删除系统参数
        if (YesOrNotEnum.Y.getCode().equals(sysConfig.getSysFlag())) {
            throw new ServiceException(SysConfigExceptionEnum.CONFIG_SYS_CAN_NOT_DELETE);
        }

        //3.设置状态为已删除
        sysConfig.setStatus(CommonStatusEnum.DELETED.getCode());
        this.updateById(sysConfig);

        //4.删除对应context
        ConstantContext.deleteConstant(sysConfigParam.getCode());
    }

    @Override
    public void edit(SysConfigParam sysConfigParam) {

        //1.根据id获取常量信息
        SysConfig sysConfig = this.querySysConfig(sysConfigParam);

        //2.校验参数，是否有重复的名称和编码，排除本条记录
        checkRepeatParam(sysConfigParam, true);

        //请求参数转化为实体
        BeanUtil.copyProperties(sysConfigParam, sysConfig);
        //不能修改状态，用修改状态接口修改状态
        sysConfig.setStatus(null);

        //3.更新记录
        this.updateById(sysConfig);

        //4.更新对应常量context
        ConstantContext.putConstant(sysConfigParam.getCode(), sysConfigParam.getValue());
    }

    /**
     * 校验参数，是否有重复的名称和编码
     *
     * @author xuyuxiang
     * @date 2020/4/14 11:18
     */
    private void checkRepeatParam(SysConfigParam sysConfigParam, boolean isExcludeSelf) {
        Long id = sysConfigParam.getId();
        String name = sysConfigParam.getName();
        String code = sysConfigParam.getCode();

        //构建带name和code的查询条件
        LambdaQueryWrapper<SysConfig> queryWrapperByName = new LambdaQueryWrapper<>();
        queryWrapperByName.eq(SysConfig::getName, name);

        LambdaQueryWrapper<SysConfig> queryWrapperByCode = new LambdaQueryWrapper<>();
        queryWrapperByCode.eq(SysConfig::getCode, code);

        //如果排除自己，则增加查询条件主键id不等于本条id
        if (isExcludeSelf) {
            queryWrapperByName.ne(SysConfig::getId, id);
            queryWrapperByCode.ne(SysConfig::getId, id);
        }
        int countByName = this.count(queryWrapperByName);
        int countByCode = this.count(queryWrapperByCode);

        //如果存在重复的记录，抛出异常，直接返回前端
        if (countByName >= 1) {
            throw new ServiceException(SysConfigExceptionEnum.CONFIG_NAME_REPEAT);
        }
        if (countByCode >= 1) {
            throw new ServiceException(SysConfigExceptionEnum.CONFIG_CODE_REPEAT);
        }
    }

    /**
     * 获取系统参数配置
     *
     * @author xuyuxiang
     * @date 2020/4/14 11:19
     */
    private SysConfig querySysConfig(SysConfigParam sysConfigParam) {
        SysConfig sysConfig = this.getById(sysConfigParam.getId());
        if (ObjectUtil.isEmpty(sysConfig)) {
            throw new ServiceException(SysConfigExceptionEnum.CONFIG_NOT_EXIST);
        }
        return sysConfig;
    }
}
