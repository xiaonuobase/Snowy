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
package vip.xiaonuo.generate.modular.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vip.xiaonuo.core.enums.YesOrNotEnum;
import vip.xiaonuo.core.exception.ServiceException;
import vip.xiaonuo.generate.core.consts.GenConstant;
import vip.xiaonuo.generate.core.enums.QueryTypeEnum;
import vip.xiaonuo.generate.core.enums.TableFilteredFieldsEnum;
import vip.xiaonuo.generate.core.tool.JavaEffTool;
import vip.xiaonuo.generate.core.tool.JavaSqlTool;
import vip.xiaonuo.generate.core.tool.NamingConTool;
import vip.xiaonuo.generate.modular.entity.CodeGenerate;
import vip.xiaonuo.generate.modular.entity.SysCodeGenerateConfig;
import vip.xiaonuo.generate.modular.enums.SysCodeGenerateConfigExceptionEnum;
import vip.xiaonuo.generate.modular.mapper.SysCodeGenerateConfigMapper;
import vip.xiaonuo.generate.modular.param.SysCodeGenerateConfigParam;
import vip.xiaonuo.generate.modular.result.InforMationColumnsResult;
import vip.xiaonuo.generate.modular.service.SysCodeGenerateConfigService;

import java.util.List;

/**
 * 代码生成详细配置service接口实现类
 *
 * @author yubaoshan
 * @date 2021-02-06 20:19:49
 */
@Service
public class SysCodeGenerateConfigServiceImpl extends ServiceImpl<SysCodeGenerateConfigMapper, SysCodeGenerateConfig> implements SysCodeGenerateConfigService {

    @Override
    public List<SysCodeGenerateConfig> list(SysCodeGenerateConfigParam sysCodeGenerateConfigParam) {
        LambdaQueryWrapper<SysCodeGenerateConfig> queryWrapper = new LambdaQueryWrapper<>();
        if (ObjectUtil.isNotNull(sysCodeGenerateConfigParam)) {

            // 根据代码生成主表ID 模糊查询
            if (ObjectUtil.isNotEmpty(sysCodeGenerateConfigParam.getCodeGenId())) {
                queryWrapper.eq(SysCodeGenerateConfig::getCodeGenId, sysCodeGenerateConfigParam.getCodeGenId());
            }
        }
        return this.list(queryWrapper);
    }

    @Override
    public void add(SysCodeGenerateConfigParam sysCodeGenerateConfigParam) {
        SysCodeGenerateConfig sysCodeGenerateConfig = new SysCodeGenerateConfig();
        BeanUtil.copyProperties(sysCodeGenerateConfigParam, sysCodeGenerateConfig);
        this.save(sysCodeGenerateConfig);
    }

    @Override
    public void addList(List<InforMationColumnsResult> inforMationColumnsResultList, CodeGenerate codeGenerate) {
        for (InforMationColumnsResult inforMationColumnsResult : inforMationColumnsResultList) {
            SysCodeGenerateConfig sysCodeGenerateConfig = new SysCodeGenerateConfig();

            String YesOrNo = YesOrNotEnum.Y.getCode();
            if (ObjectUtil.isNotNull(inforMationColumnsResult.getColumnKey())
                    && inforMationColumnsResult.getColumnKey().equals(GenConstant.DB_TABLE_COM_KRY) ||
                    TableFilteredFieldsEnum.contains(inforMationColumnsResult.getColumnName())) {
                YesOrNo = YesOrNotEnum.N.getCode();
            }
            if (TableFilteredFieldsEnum.contains(inforMationColumnsResult.getColumnName())) {
                sysCodeGenerateConfig.setWhetherCommon(YesOrNotEnum.Y.getCode());
            } else {
                sysCodeGenerateConfig.setWhetherCommon(YesOrNotEnum.N.getCode());
            }

            sysCodeGenerateConfig.setCodeGenId(codeGenerate.getId());
            sysCodeGenerateConfig.setColumnName(inforMationColumnsResult.getColumnName());
            sysCodeGenerateConfig.setColumnComment(inforMationColumnsResult.getColumnComment());
            sysCodeGenerateConfig.setJavaName(NamingConTool.UnderlineToHump(inforMationColumnsResult.getColumnName(), codeGenerate.getTablePrefix()));
            sysCodeGenerateConfig.setJavaType(JavaSqlTool.sqlToJava(inforMationColumnsResult.getDataType()));
            sysCodeGenerateConfig.setWhetherRetract(YesOrNotEnum.N.getCode());

            sysCodeGenerateConfig.setWhetherRequired(YesOrNo);
            sysCodeGenerateConfig.setQueryWhether(YesOrNo);
            sysCodeGenerateConfig.setWhetherAddUpdate(YesOrNo);
            sysCodeGenerateConfig.setWhetherTable(YesOrNo);

            sysCodeGenerateConfig.setColumnKey(inforMationColumnsResult.getColumnKey());

            // 设置get set方法使用的名称
            String columnName = NamingConTool.UnderlineToHump(sysCodeGenerateConfig.getColumnName(),"");
            sysCodeGenerateConfig.setColumnKeyName(columnName.substring(0,1).toUpperCase() + columnName.substring(1,columnName.length()));

            sysCodeGenerateConfig.setDataType(inforMationColumnsResult.getDataType());
            sysCodeGenerateConfig.setEffectType(JavaEffTool.javaToEff(sysCodeGenerateConfig.getJavaType()));
            sysCodeGenerateConfig.setQueryType(QueryTypeEnum.eq.getCode());

            this.save(sysCodeGenerateConfig);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(SysCodeGenerateConfigParam sysCodeGenerateConfigParam) {
        LambdaQueryWrapper<SysCodeGenerateConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysCodeGenerateConfig::getCodeGenId, sysCodeGenerateConfigParam.getCodeGenId());
        this.remove(queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(List<SysCodeGenerateConfigParam> sysCodeGenerateConfigParamList) {
        for (SysCodeGenerateConfigParam sysCodeGenerateConfigParam : sysCodeGenerateConfigParamList) {
            SysCodeGenerateConfig sysCodeGenerateConfig = this.querySysCodeGenerateConfig(sysCodeGenerateConfigParam);
            BeanUtil.copyProperties(sysCodeGenerateConfigParam, sysCodeGenerateConfig);
            this.updateById(sysCodeGenerateConfig);
        }
    }

    @Override
    public SysCodeGenerateConfig detail(SysCodeGenerateConfigParam sysCodeGenerateConfigParam) {
        return this.querySysCodeGenerateConfig(sysCodeGenerateConfigParam);
    }

    /**
     * 获取代码生成详细配置
     *
     * @author yubaoshan
     * @date 2021-02-06 20:19:49
     */
    private SysCodeGenerateConfig querySysCodeGenerateConfig(SysCodeGenerateConfigParam sysCodeGenerateConfigParam) {
        SysCodeGenerateConfig sysCodeGenerateConfig = this.getById(sysCodeGenerateConfigParam.getId());
        if (ObjectUtil.isNull(sysCodeGenerateConfig)) {
            throw new ServiceException(SysCodeGenerateConfigExceptionEnum.NOT_EXIST);
        }
        return sysCodeGenerateConfig;
    }
}
