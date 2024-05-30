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
package vip.xiaonuo.sys.modular.position.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vip.xiaonuo.common.enums.CommonSortOrderEnum;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.common.listener.CommonDataChangeEventCenter;
import vip.xiaonuo.common.page.CommonPageRequest;
import vip.xiaonuo.sys.core.enums.SysDataTypeEnum;
import vip.xiaonuo.sys.modular.org.entity.SysOrg;
import vip.xiaonuo.sys.modular.org.service.SysOrgService;
import vip.xiaonuo.sys.modular.position.entity.SysPosition;
import vip.xiaonuo.sys.modular.position.enums.SysPositionCategoryEnum;
import vip.xiaonuo.sys.modular.position.mapper.SysPositionMapper;
import vip.xiaonuo.sys.modular.position.param.*;
import vip.xiaonuo.sys.modular.position.service.SysPositionService;
import vip.xiaonuo.sys.modular.user.entity.SysUser;
import vip.xiaonuo.sys.modular.user.service.SysUserService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 职位Service接口实现类
 *
 * @author xuyuxiang
 * @date 2022/2/23 18:43
 **/
@Service
public class SysPositionServiceImpl extends ServiceImpl<SysPositionMapper, SysPosition> implements SysPositionService {

    @Resource
    private SysOrgService sysOrgService;

    @Resource
    private SysUserService sysUserService;

    @Override
    public Page<SysPosition> page(SysPositionPageParam sysPositionPageParam) {
        QueryWrapper<SysPosition> queryWrapper = new QueryWrapper<SysPosition>().checkSqlInjection();
        // 查询部分字段
        queryWrapper.lambda().select(SysPosition::getId, SysPosition::getOrgId, SysPosition::getName,
                SysPosition::getCategory, SysPosition::getSortCode);
        if(ObjectUtil.isNotEmpty(sysPositionPageParam.getOrgId())) {
            queryWrapper.lambda().eq(SysPosition::getOrgId, sysPositionPageParam.getOrgId());
        }
        if(ObjectUtil.isNotEmpty(sysPositionPageParam.getCategory())) {
            queryWrapper.lambda().eq(SysPosition::getCategory, sysPositionPageParam.getCategory());
        }
        if(ObjectUtil.isNotEmpty(sysPositionPageParam.getSearchKey())) {
            queryWrapper.lambda().like(SysPosition::getName, sysPositionPageParam.getSearchKey());
        }
        if(ObjectUtil.isAllNotEmpty(sysPositionPageParam.getSortField(), sysPositionPageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(sysPositionPageParam.getSortOrder());
            queryWrapper.orderBy(true, sysPositionPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(sysPositionPageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(SysPosition::getSortCode);
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(SysPositionAddParam sysPositionAddParam) {
        SysPositionCategoryEnum.validate(sysPositionAddParam.getCategory());
        SysPosition sysPosition = BeanUtil.toBean(sysPositionAddParam, SysPosition.class);
        boolean repeatName = this.count(new LambdaQueryWrapper<SysPosition>().eq(SysPosition::getOrgId, sysPosition.getOrgId())
                .eq(SysPosition::getName, sysPosition.getName())) > 0;
        if(repeatName) {
            throw new CommonException("同组织下存在重复的职位，名称为：{}", sysPosition.getName());
        }
        sysPosition.setCode(RandomUtil.randomString(10));
        this.save(sysPosition);

        // 发布增加事件
        CommonDataChangeEventCenter.doAddWithData(SysDataTypeEnum.POSITION.getValue(), JSONUtil.createArray().put(sysPosition));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(SysPositionEditParam sysPositionEditParam) {
        SysPositionCategoryEnum.validate(sysPositionEditParam.getCategory());
        SysPosition sysPosition = this.queryEntity(sysPositionEditParam.getId());
        BeanUtil.copyProperties(sysPositionEditParam, sysPosition);
        boolean repeatName = this.count(new LambdaQueryWrapper<SysPosition>().eq(SysPosition::getOrgId, sysPosition.getOrgId())
                .eq(SysPosition::getName, sysPosition.getName()).ne(SysPosition::getId, sysPosition.getId())) > 0;
        if(repeatName) {
            throw new CommonException("同组织下存在重复的职位，名称为：{}", sysPosition.getName());
        }
        this.updateById(sysPosition);

        // 发布更新事件
        CommonDataChangeEventCenter.doUpdateWithData(SysDataTypeEnum.POSITION.getValue(), JSONUtil.createArray().put(sysPosition));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<SysPositionIdParam> sysPositionIdParamList) {
        List<String> positionIdList = CollStreamUtil.toList(sysPositionIdParamList, SysPositionIdParam::getId);
        if(ObjectUtil.isNotEmpty(positionIdList)) {
            // 职位下有人不能删除（直属职位）
            boolean hasOrgUser = sysUserService.count(new LambdaQueryWrapper<SysUser>().in(SysUser::getPositionId, positionIdList)) > 0;
            if(hasOrgUser) {
                throw new CommonException("请先删除职位下的用户");
            }
            // 职位下有人不能删除（兼任职位）
            List<String> positionJsonList = sysUserService.list(new LambdaQueryWrapper<SysUser>()
                    .isNotNull(SysUser::getPositionJson)).stream().map(SysUser::getPositionJson).collect(Collectors.toList());
            if(ObjectUtil.isNotEmpty(positionJsonList)) {
                List<String> extPositionIdList = CollectionUtil.newArrayList();
                positionJsonList.forEach(positionJson -> JSONUtil.toList(JSONUtil.parseArray(positionJson), JSONObject.class)
                        .forEach(jsonObject -> extPositionIdList.add(jsonObject.getStr("positionId"))));
                boolean hasPositionUser = CollectionUtil.intersectionDistinct(positionIdList, CollectionUtil.removeNull(extPositionIdList)).size() > 0;
                if(hasPositionUser) {
                    throw new CommonException("请先删除职位下的用户");
                }
            }
            // 执行删除
            this.removeByIds(positionIdList);

            // 发布删除事件
            CommonDataChangeEventCenter.doDeleteWithDataId(SysDataTypeEnum.POSITION.getValue(), positionIdList);
        }
    }

    @Override
    public SysPosition detail(SysPositionIdParam sysPositionIdParam) {
        return this.queryEntity(sysPositionIdParam.getId());
    }

    @Override
    public SysPosition queryEntity(String id) {
        SysPosition sysPosition = this.getById(id);
        if(ObjectUtil.isEmpty(sysPosition)) {
            throw new CommonException("职位不存在，id值为：{}", id);
        }
        return sysPosition;
    }

    @Override
    public SysPosition getById(List<SysPosition> originDataList, String id) {
        int index = CollStreamUtil.toList(originDataList, SysPosition::getId).indexOf(id);
        return index == -1?null:originDataList.get(index);
    }

    @Override
    public String getPositionIdByPositionNameWithCreate(String orgId, String positionName) {
        SysPosition sysPosition = this.getOne(new LambdaQueryWrapper<SysPosition>().eq(SysPosition::getOrgId, orgId).eq(SysPosition::getName, positionName));
        if(ObjectUtil.isNotEmpty(sysPosition)) {
            return sysPosition.getId();
        } else {
            sysPosition = new SysPosition();
            sysPosition.setOrgId(orgId);
            sysPosition.setName(positionName);
            sysPosition.setCode(RandomUtil.randomString(10));
            sysPosition.setCategory(SysPositionCategoryEnum.LOW.getValue());
            sysPosition.setSortCode(99);
            this.save(sysPosition);
            // 发布增加事件
            CommonDataChangeEventCenter.doAddWithData(SysDataTypeEnum.POSITION.getValue(), JSONUtil.createArray().put(sysPosition));
            return sysPosition.getId();
        }
    }

    /* ====职位部分所需要用到的选择器==== */

    @Override
    public List<Tree<String>> orgTreeSelector() {
        List<SysOrg> sysOrgList = sysOrgService.getAllOrgList();
        List<TreeNode<String>> treeNodeList = sysOrgList.stream().map(sysOrg ->
                new TreeNode<>(sysOrg.getId(), sysOrg.getParentId(), sysOrg.getName(), sysOrg.getSortCode()))
                .collect(Collectors.toList());
        return TreeUtil.build(treeNodeList, "0");
    }

    @Override
    public Page<SysPosition> positionSelector(SysPositionSelectorPositionParam sysPositionSelectorPositionParam) {
        LambdaQueryWrapper<SysPosition> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 查询部分字段
        lambdaQueryWrapper.select(SysPosition::getId, SysPosition::getOrgId, SysPosition::getName,
                SysPosition::getCategory, SysPosition::getSortCode);
        if(ObjectUtil.isNotEmpty(sysPositionSelectorPositionParam.getOrgId())) {
            lambdaQueryWrapper.eq(SysPosition::getOrgId, sysPositionSelectorPositionParam.getOrgId());
        }
        if(ObjectUtil.isNotEmpty(sysPositionSelectorPositionParam.getSearchKey())) {
            lambdaQueryWrapper.like(SysPosition::getName, sysPositionSelectorPositionParam.getSearchKey());
        }
        lambdaQueryWrapper.orderByAsc(SysPosition::getSortCode);
        return this.page(CommonPageRequest.defaultPage(), lambdaQueryWrapper);
    }
}
