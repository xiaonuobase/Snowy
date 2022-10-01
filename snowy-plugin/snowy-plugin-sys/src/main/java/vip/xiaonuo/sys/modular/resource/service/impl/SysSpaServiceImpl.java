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
package vip.xiaonuo.sys.modular.resource.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import vip.xiaonuo.common.enums.CommonSortOrderEnum;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.common.page.CommonPageRequest;
import vip.xiaonuo.sys.core.enums.SysBuildInEnum;
import vip.xiaonuo.sys.modular.resource.entity.SysSpa;
import vip.xiaonuo.sys.modular.resource.enums.SysResourceCategoryEnum;
import vip.xiaonuo.sys.modular.resource.enums.SysResourceMenuTypeEnum;
import vip.xiaonuo.sys.modular.resource.mapper.SysSpaMapper;
import vip.xiaonuo.sys.modular.resource.param.spa.SysSpaAddParam;
import vip.xiaonuo.sys.modular.resource.param.spa.SysSpaEditParam;
import vip.xiaonuo.sys.modular.resource.param.spa.SysSpaIdParam;
import vip.xiaonuo.sys.modular.resource.param.spa.SysSpaPageParam;
import vip.xiaonuo.sys.modular.resource.service.SysSpaService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 单页面Service接口实现类
 *
 * @author xuyuxiang
 * @date 2022/6/27 14:25
 **/
@Service
public class SysSpaServiceImpl extends ServiceImpl<SysSpaMapper, SysSpa> implements SysSpaService {

    @Override
    public Page<SysSpa> page(SysSpaPageParam sysSpaPageParam) {
        QueryWrapper<SysSpa> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysSpa::getCategory, SysResourceCategoryEnum.SPA.getValue());
        if(ObjectUtil.isNotEmpty(sysSpaPageParam.getSearchKey())) {
            queryWrapper.lambda().like(SysSpa::getTitle, sysSpaPageParam.getSearchKey());
        }
        if(ObjectUtil.isAllNotEmpty(sysSpaPageParam.getSortField(), sysSpaPageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(sysSpaPageParam.getSortOrder());
            queryWrapper.orderBy(true, sysSpaPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(sysSpaPageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(SysSpa::getSortCode);
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Override
    public void add(SysSpaAddParam sysSpaAddParam) {
        checkParam(sysSpaAddParam);
        SysSpa sysSpa = BeanUtil.toBean(sysSpaAddParam, SysSpa.class);
        boolean repeatTitle = this.count(new LambdaQueryWrapper<SysSpa>().eq(SysSpa::getCategory,
                SysResourceCategoryEnum.SPA.getValue()).eq(SysSpa::getTitle, sysSpa.getTitle())) > 0;
        if(repeatTitle) {
            throw new CommonException("存在重复的单页面，名称为：{}", sysSpa.getTitle());
        }
        sysSpa.setCode(RandomUtil.randomString(10));
        sysSpa.setCategory(SysResourceCategoryEnum.SPA.getValue());
        this.save(sysSpa);
    }

    @SuppressWarnings("all")
    private void checkParam(SysSpaAddParam sysSpaAddParam) {
        SysResourceMenuTypeEnum.validate(sysSpaAddParam.getMenuType());
        if(SysResourceMenuTypeEnum.MENU.getValue().equals(sysSpaAddParam.getMenuType())) {
            if(ObjectUtil.isEmpty(sysSpaAddParam.getName())) {
                throw new CommonException("name不能为空");
            }
            if(ObjectUtil.isEmpty(sysSpaAddParam.getComponent())) {
                throw new CommonException("component不能为空");
            }
        } else if(SysResourceMenuTypeEnum.IFRAME.getValue().equals(sysSpaAddParam.getMenuType()) ||
                SysResourceMenuTypeEnum.LINK.getValue().equals(sysSpaAddParam.getMenuType())) {
            sysSpaAddParam.setName(RandomUtil.randomNumbers(10));
            sysSpaAddParam.setComponent(null);
        } else {
            sysSpaAddParam.setName(null);
            sysSpaAddParam.setComponent(null);
        }
    }

    @Override
    public void edit(SysSpaEditParam sysSpaEditParam) {
        SysSpa sysSpa = this.queryEntity(sysSpaEditParam.getId());
        checkParam(sysSpaEditParam);
        BeanUtil.copyProperties(sysSpaEditParam, sysSpa);
        boolean repeatTitle = this.count(new LambdaQueryWrapper<SysSpa>().eq(SysSpa::getCategory,
                SysResourceCategoryEnum.SPA.getValue()).eq(SysSpa::getTitle, sysSpa.getTitle())
                .ne(SysSpa::getId, sysSpa.getId())) > 0;
        if(repeatTitle) {
            throw new CommonException("存在重复的单页面，名称为：{}", sysSpa.getTitle());
        }
        this.updateById(sysSpa);
    }

    @SuppressWarnings("all")
    private void checkParam(SysSpaEditParam sysSpaEditParam) {
        SysResourceMenuTypeEnum.validate(sysSpaEditParam.getMenuType());
        if(SysResourceMenuTypeEnum.MENU.getValue().equals(sysSpaEditParam.getMenuType())) {
            if(ObjectUtil.isEmpty(sysSpaEditParam.getName())) {
                throw new CommonException("name不能为空");
            }
            if(ObjectUtil.isEmpty(sysSpaEditParam.getComponent())) {
                throw new CommonException("component不能为空");
            }
        } else if(SysResourceMenuTypeEnum.IFRAME.getValue().equals(sysSpaEditParam.getMenuType()) ||
                SysResourceMenuTypeEnum.LINK.getValue().equals(sysSpaEditParam.getMenuType())) {
            sysSpaEditParam.setName(RandomUtil.randomNumbers(10));
            sysSpaEditParam.setComponent(null);
        } else {
            sysSpaEditParam.setName(null);
            sysSpaEditParam.setComponent(null);
        }
    }

    @Override
    public void delete(List<SysSpaIdParam> sysSpaIdParamList) {
        List<String> sysSpaIdList = CollStreamUtil.toList(sysSpaIdParamList, SysSpaIdParam::getId);
        if(ObjectUtil.isNotEmpty(sysSpaIdList)) {
            boolean containsSystemSpa = this.listByIds(sysSpaIdList).stream().map(SysSpa::getCode)
                    .collect(Collectors.toSet()).contains(SysBuildInEnum.BUILD_IN_SPA_CODE.getValue());
            if(containsSystemSpa) {
                throw new CommonException("不可删除系统内置单页面");
            }
            // 删除
            this.removeBatchByIds(sysSpaIdList);
        }
    }

    @Override
    public SysSpa detail(SysSpaIdParam sysSpaIdParam) {
        return this.queryEntity(sysSpaIdParam.getId());
    }

    @Override
    public SysSpa queryEntity(String id) {
        SysSpa sysSpa = this.getById(id);
        if(ObjectUtil.isEmpty(sysSpa)) {
            throw new CommonException("单页面不存在，id值为：{}", id);
        }
        return sysSpa;
    }
}
