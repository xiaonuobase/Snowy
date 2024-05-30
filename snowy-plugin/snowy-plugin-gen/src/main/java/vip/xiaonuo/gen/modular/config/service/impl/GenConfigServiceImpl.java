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
package vip.xiaonuo.gen.modular.config.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vip.xiaonuo.common.enums.CommonSortOrderEnum;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.gen.modular.config.entity.GenConfig;
import vip.xiaonuo.gen.modular.config.mapper.GenConfigMapper;
import vip.xiaonuo.gen.modular.config.param.GenConfigEditParam;
import vip.xiaonuo.gen.modular.config.param.GenConfigIdParam;
import vip.xiaonuo.gen.modular.config.param.GenConfigListParam;
import vip.xiaonuo.gen.modular.config.service.GenConfigService;

import java.util.List;

/**
 * 代码生成详情配置Service接口实现类
 *
 * @author yubaoshan
 * @date 2022/10/25 22:33
 **/
@Service
public class GenConfigServiceImpl extends ServiceImpl<GenConfigMapper, GenConfig> implements GenConfigService {

    @Override
    public List<GenConfig> list(GenConfigListParam genConfigListParam) {
        QueryWrapper<GenConfig> queryWrapper = new QueryWrapper<GenConfig>().checkSqlInjection();

        queryWrapper.lambda().eq(GenConfig::getBasicId, genConfigListParam.getBasicId());
        if(ObjectUtil.isAllNotEmpty(genConfigListParam.getSortField(), genConfigListParam.getSortOrder())) {
            CommonSortOrderEnum.validate(genConfigListParam.getSortOrder());
            queryWrapper.orderBy(true, genConfigListParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(genConfigListParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(GenConfig::getSortCode);
        }
        return this.list(queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(GenConfigEditParam genConfigEditParam) {
        GenConfig genConfig = this.queryEntity(genConfigEditParam.getId());
        BeanUtil.copyProperties(genConfigEditParam, genConfig);
        this.updateById(genConfig);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<GenConfigIdParam> genConfigIdParamList) {
        List<String> basicIdIdList = CollStreamUtil.toList(genConfigIdParamList, GenConfigIdParam::getId);
        if(ObjectUtil.isNotEmpty(basicIdIdList)) {
            // 执行删除
            this.removeByIds(basicIdIdList);
        }
    }

    @Override
    public GenConfig detail(GenConfigIdParam genConfigIdParam) {
        return this.queryEntity(genConfigIdParam.getId());
    }

    @Override
    public GenConfig queryEntity(String id) {
        GenConfig genConfig = this.getById(id);
        if(ObjectUtil.isEmpty(genConfig)) {
            throw new CommonException("代码生成详情配置不存在，id值为：{}", id);
        }
        return genConfig;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void editBatch(List<GenConfigEditParam> genConfigEditParamList) {
        genConfigEditParamList.forEach(this::edit);
    }
}
