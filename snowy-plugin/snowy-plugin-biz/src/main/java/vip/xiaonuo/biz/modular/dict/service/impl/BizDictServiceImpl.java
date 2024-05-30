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
package vip.xiaonuo.biz.modular.dict.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fhs.trans.service.impl.DictionaryTransService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import vip.xiaonuo.biz.modular.dict.entity.BizDict;
import vip.xiaonuo.biz.modular.dict.enums.BizDictCategoryEnum;
import vip.xiaonuo.biz.modular.dict.mapper.BizDictMapper;
import vip.xiaonuo.biz.modular.dict.param.BizDictEditParam;
import vip.xiaonuo.biz.modular.dict.param.BizDictPageParam;
import vip.xiaonuo.biz.modular.dict.service.BizDictService;
import vip.xiaonuo.common.enums.CommonSortOrderEnum;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.common.page.CommonPageRequest;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 字典Service接口实现类
 *
 * @author xuyuxiang
 * @date 2022/4/22 10:41
 **/
@Service
public class BizDictServiceImpl extends ServiceImpl<BizDictMapper, BizDict> implements BizDictService, InitializingBean {

    private static final String ROOT_PARENT_ID = "0";

    @Resource
    private DictionaryTransService dictionaryTransService;

    @Override
    public Page<BizDict> page(BizDictPageParam bizDictPageParam) {
        QueryWrapper<BizDict> queryWrapper = new QueryWrapper<BizDict>().checkSqlInjection();
        // 查询部分字段
        queryWrapper.lambda().select(BizDict::getId, BizDict::getParentId, BizDict::getCategory, BizDict::getDictLabel,
                BizDict::getDictValue, BizDict::getSortCode).eq(BizDict::getCategory, BizDictCategoryEnum.BIZ.getValue());
        if (ObjectUtil.isNotEmpty(bizDictPageParam.getParentId())) {
            queryWrapper.lambda().and(q -> q.eq(BizDict::getParentId, bizDictPageParam.getParentId())
                    .or().eq(BizDict::getId, bizDictPageParam.getParentId()));
        }
        if (ObjectUtil.isNotEmpty(bizDictPageParam.getSearchKey())) {
            queryWrapper.lambda().like(BizDict::getDictLabel, bizDictPageParam.getSearchKey());
        }
        if (ObjectUtil.isAllNotEmpty(bizDictPageParam.getSortField(), bizDictPageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(bizDictPageParam.getSortOrder());
            queryWrapper.orderBy(true, bizDictPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(bizDictPageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(BizDict::getSortCode);
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Override
    public List<Tree<String>> tree() {
        LambdaQueryWrapper<BizDict> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(BizDict::getCategory, BizDictCategoryEnum.BIZ.getValue()).orderByAsc(BizDict::getSortCode);
        List<BizDict> bizDictList = this.list(lambdaQueryWrapper);
        List<TreeNode<String>> treeNodeList = bizDictList.stream().map(bizDict ->
                new TreeNode<>(bizDict.getId(), bizDict.getParentId(),
                        bizDict.getDictLabel(), bizDict.getSortCode()).setExtra(JSONUtil.parseObj(bizDict)))
                .collect(Collectors.toList());
        return TreeUtil.build(treeNodeList, "0");
    }

    @Override
    public List<Tree<String>> treeAll() {
        List<BizDict> bizDictList = this.list();
        List<TreeNode<String>> treeNodeList = bizDictList.stream().map(bizDict ->
                        new TreeNode<>(bizDict.getId(), bizDict.getParentId(),
                                bizDict.getDictLabel(), bizDict.getSortCode()).setExtra(JSONUtil.parseObj(bizDict)))
                .collect(Collectors.toList());
        return TreeUtil.build(treeNodeList, "0");
    }

    @Override
    public void edit(BizDictEditParam bizDictEditParam) {
        BizDict bizDict = this.queryEntity(bizDictEditParam.getId());
        checkParam(bizDictEditParam);
        BeanUtil.copyProperties(bizDictEditParam, bizDict);
        this.updateById(bizDict);
        refreshTransCache();
    }

    private void checkParam(BizDictEditParam bizDictEditParam) {
        boolean hasSameDictLabel = this.count(new LambdaQueryWrapper<BizDict>()
                .eq(BizDict::getCategory, BizDictCategoryEnum.BIZ.getValue())
                .eq(BizDict::getDictLabel, bizDictEditParam.getDictLabel())
                .ne(BizDict::getId, bizDictEditParam.getId())) > 0;
        if (hasSameDictLabel) {
            throw new CommonException("存在重复的字典文字，名称为：{}", bizDictEditParam.getDictLabel());
        }
    }

    @Override
    public BizDict queryEntity(String id) {
        BizDict bizDict = this.getById(id);
        if (ObjectUtil.isEmpty(bizDict)) {
            throw new CommonException("字典不存在，id值为：{}", id);
        }
        return bizDict;
    }

    @Override
    public void afterPropertiesSet() {
        refreshTransCache();
    }

    private void refreshTransCache() {
        // 异步不阻塞主线程，不会 增加启动用时
        CompletableFuture.supplyAsync(() -> {
            // 使用redis能解决共享问题，但是性能没有直接取缓存的好。
            dictionaryTransService.makeUseRedis();
            List<BizDict> bizDictList = super.list(new LambdaQueryWrapper<>());
            // 非root级别的字典根据ParentId分组
            Map<String,List<BizDict>> bizDictGroupByPIDMap = bizDictList.stream().filter(dict -> !ROOT_PARENT_ID
                    .equals(dict.getParentId())).collect(Collectors.groupingBy(BizDict::getParentId));
            Map<String,String> parentDictIdValMap = bizDictList.stream().filter(dict -> ROOT_PARENT_ID
                    .equals(dict.getParentId())).collect(Collectors.toMap(BizDict::getId, BizDict::getDictValue));
            for (String parentId : parentDictIdValMap.keySet()) {
                if(bizDictGroupByPIDMap.containsKey(parentId)){
                    dictionaryTransService.refreshCache(parentDictIdValMap.get(parentId), bizDictGroupByPIDMap.get(parentId).stream()
                            .collect(Collectors.toMap(BizDict::getDictValue, BizDict::getDictLabel)));
                }
            }
            return null;
        });
    }
}
