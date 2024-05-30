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
package vip.xiaonuo.mobile.modular.resource.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import vip.xiaonuo.common.enums.CommonSortOrderEnum;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.common.page.CommonPageRequest;
import vip.xiaonuo.mobile.modular.resource.entity.MobileButton;
import vip.xiaonuo.mobile.modular.resource.entity.MobileMenu;
import vip.xiaonuo.mobile.modular.resource.enums.MobileResourceCategoryEnum;
import vip.xiaonuo.mobile.modular.resource.mapper.MobileButtonMapper;
import vip.xiaonuo.mobile.modular.resource.param.button.MobileButtonAddParam;
import vip.xiaonuo.mobile.modular.resource.param.button.MobileButtonEditParam;
import vip.xiaonuo.mobile.modular.resource.param.button.MobileButtonIdParam;
import vip.xiaonuo.mobile.modular.resource.param.button.MobileButtonPageParam;
import vip.xiaonuo.mobile.modular.resource.service.MobileButtonService;
import vip.xiaonuo.mobile.modular.resource.service.MobileMenuService;
import vip.xiaonuo.sys.api.SysRelationApi;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 移动端按钮Service接口实现类
 *
 * @author xuyuxiang
 * @date 2022/6/27 14:25
 **/
@Service
public class MobileButtonServiceImpl extends ServiceImpl<MobileButtonMapper, MobileButton> implements MobileButtonService {

    @Resource
    private SysRelationApi sysRelationApi;

    @Resource
    private MobileMenuService mobileMenuService;

    @Override
    public Page<MobileButton> page(MobileButtonPageParam mobileButtonPageParam) {
        QueryWrapper<MobileButton> queryWrapper = new QueryWrapper<MobileButton>().checkSqlInjection();
        queryWrapper.lambda().eq(MobileButton::getCategory, MobileResourceCategoryEnum.BUTTON.getValue());
        if(ObjectUtil.isNotEmpty(mobileButtonPageParam.getParentId())) {
            queryWrapper.lambda().eq(MobileButton::getParentId, mobileButtonPageParam.getParentId());
        }
        if(ObjectUtil.isNotEmpty(mobileButtonPageParam.getSearchKey())) {
            queryWrapper.lambda().like(MobileButton::getTitle, mobileButtonPageParam.getSearchKey());
        }
        if(ObjectUtil.isAllNotEmpty(mobileButtonPageParam.getSortField(), mobileButtonPageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(mobileButtonPageParam.getSortOrder());
            queryWrapper.orderBy(true, mobileButtonPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(mobileButtonPageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(MobileButton::getSortCode);
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Override
    public void add(MobileButtonAddParam mobileButtonAddParam) {
        MobileButton mobileButton = BeanUtil.toBean(mobileButtonAddParam, MobileButton.class);
        boolean repeatCode = this.count(new LambdaQueryWrapper<MobileButton>()
                .eq(MobileButton::getCategory, MobileResourceCategoryEnum.BUTTON.getValue())
                .eq(MobileButton::getCode, mobileButton.getCode())) > 0;
        if(repeatCode) {
            throw new CommonException("存在重复的移动端按钮，编码为：{}", mobileButton.getCode());
        }
        mobileButton.setCategory(MobileResourceCategoryEnum.BUTTON.getValue());
        this.save(mobileButton);
    }

    @Override
    public void edit(MobileButtonEditParam mobileButtonEditParam) {
        MobileButton mobileButton = this.queryEntity(mobileButtonEditParam.getId());
        BeanUtil.copyProperties(mobileButtonEditParam, mobileButton);
        boolean repeatCode = this.count(new LambdaQueryWrapper<MobileButton>()
                .eq(MobileButton::getCategory, MobileResourceCategoryEnum.BUTTON.getValue())
                .eq(MobileButton::getCode, mobileButton.getCode())
                .ne(MobileButton::getId, mobileButtonEditParam.getId())) > 0;
        if(repeatCode) {
            throw new CommonException("存在重复的移动端按钮，编码为：{}", mobileButton.getCode());
        }
        this.updateById(mobileButton);
    }

    @Override
    public void delete(List<MobileButtonIdParam> mobileButtonIdParamList) {
        List<String> buttonIdList = CollStreamUtil.toList(mobileButtonIdParamList, MobileButtonIdParam::getId);
        if(ObjectUtil.isNotEmpty(buttonIdList)) {
            // 获取移动端按钮的父菜单id集合
            List<String> parentMenuIdList = mobileMenuService.list(new LambdaQueryWrapper<MobileMenu>().in(MobileMenu::getId, buttonIdList)
                    .eq(MobileMenu::getCategory, MobileResourceCategoryEnum.BUTTON.getValue())).stream().map(MobileMenu::getParentId)
                    .collect(Collectors.toList());
            if(ObjectUtil.isNotEmpty(parentMenuIdList)) {
                // 清除对应的角色与移动端菜单信息中的【授权的移动端按钮信息】
                sysRelationApi.removeRoleHasMobileButtonRelation(parentMenuIdList, buttonIdList);
                // 执行删除
                this.removeByIds(buttonIdList);
            }
        }
    }

    @Override
    public MobileButton detail(MobileButtonIdParam mobileButtonIdParam) {
        return this.queryEntity(mobileButtonIdParam.getId());
    }

    @Override
    public MobileButton queryEntity(String id) {
        MobileButton mobileButton = this.getById(id);
        if(ObjectUtil.isEmpty(mobileButton)) {
            throw new CommonException("移动端按钮不存在，id值为：{}", id);
        }
        return mobileButton;
    }
}
