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
package vip.xiaonuo.biz.modular.org.service.impl;

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
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vip.xiaonuo.auth.core.util.StpLoginUserUtil;
import vip.xiaonuo.biz.core.enums.BizDataTypeEnum;
import vip.xiaonuo.biz.modular.org.entity.BizOrg;
import vip.xiaonuo.biz.modular.org.enums.BizOrgCategoryEnum;
import vip.xiaonuo.biz.modular.org.enums.BizOrgSourceFromTypeEnum;
import vip.xiaonuo.biz.modular.org.mapper.BizOrgMapper;
import vip.xiaonuo.biz.modular.org.param.*;
import vip.xiaonuo.biz.modular.org.service.BizOrgExtService;
import vip.xiaonuo.biz.modular.org.service.BizOrgService;
import vip.xiaonuo.biz.modular.position.entity.BizPosition;
import vip.xiaonuo.biz.modular.position.service.BizPositionService;
import vip.xiaonuo.biz.modular.user.entity.BizUser;
import vip.xiaonuo.biz.modular.user.service.BizUserService;
import vip.xiaonuo.common.cache.CommonCacheOperator;
import vip.xiaonuo.common.util.CommonServletUtil;
import vip.xiaonuo.common.util.CommonSqlUtil;
import vip.xiaonuo.common.enums.CommonSortOrderEnum;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.common.listener.CommonDataChangeEventCenter;
import vip.xiaonuo.common.page.CommonPageRequest;
import vip.xiaonuo.sys.api.SysOrgApi;
import vip.xiaonuo.sys.api.SysRoleApi;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 机构Service接口实现类
 *
 * @author xuyuxiang
 * @date 2022/2/23 18:43
 **/
@Service
public class BizOrgServiceImpl extends ServiceImpl<BizOrgMapper, BizOrg> implements BizOrgService {

    private static final String ORG_ALL_LIST_CACHE_KEY = "biz-org:all-list";

    /** 本地内存缓存，避免每次从Redis取出后JSON反序列化大量记录（与SysOrg对齐） */
    private volatile List<BizOrg> localOrgListCache;

    /** 机构数据版本号缓存key，机构增删改时递增，用于使 visibleOrgIds 缓存自动失效 */
    private static final String ORG_CACHE_VERSION_KEY = "biz-org:cache-version";

    /** 可见机构ID集合缓存key前缀，完整key = 前缀 + 版本号 + : + 数据范围hash */
    private static final String VISIBLE_ORG_IDS_CACHE_KEY_PREFIX = "biz-org:visible-ids:";

    /** visibleOrgIds 缓存TTL（秒），用于自动清理旧版本的缓存条目 */
    private static final long VISIBLE_ORG_IDS_CACHE_TTL = 300;

    @Resource
    private CommonCacheOperator commonCacheOperator;

    @Resource
    private SysOrgApi sysOrgApi;

    @Resource
    private SysRoleApi sysRoleApi;

    @Resource
    private BizOrgExtService bizOrgExtService;

    @Resource
    private BizPositionService bizPositionService;

    @Resource
    private BizUserService bizUserService;

    @Override
    public Page<BizOrg> page(BizOrgPageParam bizOrgPageParam) {
        QueryWrapper<BizOrg> queryWrapper = new QueryWrapper<BizOrg>().checkSqlInjection();
        // 查询部分字段
        queryWrapper.lambda().select(BizOrg::getId, BizOrg::getParentId, BizOrg::getName,
                BizOrg::getCategory, BizOrg::getSortCode);
        if(ObjectUtil.isNotEmpty(bizOrgPageParam.getParentId())) {
            queryWrapper.lambda().eq(BizOrg::getParentId, bizOrgPageParam.getParentId());
        }
        if(ObjectUtil.isNotEmpty(bizOrgPageParam.getSearchKey())) {
            queryWrapper.lambda().like(BizOrg::getName, bizOrgPageParam.getSearchKey());
        }
        if(ObjectUtil.isAllNotEmpty(bizOrgPageParam.getSortField(), bizOrgPageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(bizOrgPageParam.getSortOrder());
            queryWrapper.orderBy(true, bizOrgPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(bizOrgPageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(BizOrg::getSortCode);
        }
        // 校验数据范围
        List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
        if(loginUserDataScope != null && loginUserDataScope.isEmpty()) {
            return new Page<>();
        }
        if(ObjectUtil.isNotEmpty(loginUserDataScope)) {
            CommonSqlUtil.scopeIn(queryWrapper.lambda(), BizOrg::getId, StpUtil.getLoginIdAsString(), CommonServletUtil.getRequest().getServletPath());
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    /**
     * 查询指定父节点下的直接子机构列表，并为每个子机构设置 isLeaf 标志。
     * visibleOrgIds 为 null 时表示 SCOPE_ALL，不做数据范围过滤（与 SysOrg 行为一致）；
     * 非 null 时仅返回集合内可见的子机构，且 isLeaf 判断也限制在可见范围内。
     */
    private List<JSONObject> fetchChildrenWithLeafFlag(String parentId, Set<String> visibleOrgIds) {
        // 查询当前父级下的（可见）子机构
        LambdaQueryWrapper<BizOrg> childQuery = new LambdaQueryWrapper<BizOrg>()
                .eq(BizOrg::getParentId, parentId)
                .orderByAsc(BizOrg::getSortCode)
                .orderByAsc(BizOrg::getId);
        if (visibleOrgIds != null) {
            CommonSqlUtil.safeIn(childQuery, BizOrg::getId, visibleOrgIds);
        }
        List<BizOrg> childList = this.list(childQuery);
        if (ObjectUtil.isEmpty(childList)) {
            return CollectionUtil.newArrayList();
        }
        // 批量判断哪些子机构还有（可见的）下级
        List<String> childIds = childList.stream().map(BizOrg::getId).collect(Collectors.toList());
        LambdaQueryWrapper<BizOrg> hasChildrenQuery = new LambdaQueryWrapper<BizOrg>()
                .select(BizOrg::getParentId)
                .in(BizOrg::getParentId, childIds);
        if (visibleOrgIds != null) {
            CommonSqlUtil.safeIn(hasChildrenQuery, BizOrg::getId, visibleOrgIds);
        }
        Set<String> hasChildrenParentIds = this.list(hasChildrenQuery)
                .stream().map(BizOrg::getParentId).collect(Collectors.toSet());
        return childList.stream().map(bizOrg -> {
            JSONObject jsonObject = JSONUtil.parseObj(bizOrg);
            jsonObject.set("isLeaf", !hasChildrenParentIds.contains(bizOrg.getId()));
            return jsonObject;
        }).collect(Collectors.toList());
    }

    /**
     * 全量搜索模式，返回嵌套树结构的JSONObject列表
     * searchKey为空字符串时返回全量树，非空时按关键字过滤
     * 保留数据范围过滤逻辑
     */
    private List<JSONObject> treeSearch(String searchKey) {
        List<BizOrg> allOrgList = this.getAllOrgList();
        Set<BizOrg> bizOrgSet = CollectionUtil.newHashSet();
        List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
        if (loginUserDataScope != null && loginUserDataScope.isEmpty()) {
            return CollectionUtil.newArrayList();
        }
        if (loginUserDataScope == null) {
            bizOrgSet.addAll(allOrgList);
        } else {
            loginUserDataScope.forEach(orgId -> bizOrgSet.addAll(this.getParentListById(allOrgList, orgId, true)));
        }
        List<BizOrg> bizOrgArrayList = new ArrayList<>(bizOrgSet);
        if (ObjectUtil.isNotEmpty(searchKey)) {
            Set<BizOrg> filteredSet = CollectionUtil.newLinkedHashSet();
            bizOrgArrayList.stream()
                    .filter(org -> StrUtil.containsIgnoreCase(org.getName(), searchKey))
                    .forEach(org -> filteredSet.addAll(this.getParentListById(allOrgList, org.getId(), true)));
            bizOrgArrayList = new ArrayList<>(filteredSet);
            bizOrgArrayList.retainAll(bizOrgSet);
        }
        bizOrgArrayList.sort(Comparator.comparingInt(BizOrg::getSortCode)
                .thenComparing(BizOrg::getId));
        List<TreeNode<String>> treeNodeList = bizOrgArrayList.stream().map(bizOrg ->
                        new TreeNode<>(bizOrg.getId(), bizOrg.getParentId(),
                                bizOrg.getName(), bizOrg.getSortCode()).setExtra(JSONUtil.parseObj(bizOrg)))
                .collect(Collectors.toList());
        List<Tree<String>> treeList = TreeUtil.build(treeNodeList, "0");
        return JSONUtil.toList(JSONUtil.parseArray(treeList), JSONObject.class);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(BizOrgAddParam bizOrgAddParam, String sourceFromType) {
        BizOrgCategoryEnum.validate(bizOrgAddParam.getCategory());
        // 校验数据范围
        List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
        if(loginUserDataScope != null && loginUserDataScope.isEmpty()) {
            throw new CommonException("您没有权限增加机构");
        }
        if(ObjectUtil.isNotEmpty(loginUserDataScope)) {
            if(!loginUserDataScope.contains(bizOrgAddParam.getParentId())) {
                throw new CommonException("您没有权限在该机构下增加机构，机构id：{}", bizOrgAddParam.getParentId());
            }
        }
        BizOrg bizOrg = BeanUtil.toBean(bizOrgAddParam, BizOrg.class);

        // 重复名称
        boolean repeatName = this.count(new LambdaQueryWrapper<BizOrg>().eq(BizOrg::getParentId, bizOrg.getParentId())
                .eq(BizOrg::getName, bizOrg.getName())) > 0;
        if(repeatName) {
            throw new CommonException("存在重复的同级机构，名称为：{}", bizOrg.getName());
        }
        bizOrg.setCode(RandomUtil.randomString(10));
        // 保存机构
        this.save(bizOrg);
        // 插入扩展信息
        bizOrgExtService.createExtInfo(bizOrg.getId(), sourceFromType);
        // 发布增加事件（BizDataChangeListener 和 SysDataChangeListener 监听后自动清缓存）
        CommonDataChangeEventCenter.doAddWithData(BizDataTypeEnum.ORG.getValue(), JSONUtil.createArray().put(bizOrg));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(BizOrgEditParam bizOrgEditParam) {
        BizOrgCategoryEnum.validate(bizOrgEditParam.getCategory());
        BizOrg bizOrg = this.queryEntity(bizOrgEditParam.getId());
        // 校验数据范围
        List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
        if(loginUserDataScope != null && loginUserDataScope.isEmpty()) {
            throw new CommonException("您没有权限编辑该机构，机构id：{}", bizOrg.getId());
        }
        if(ObjectUtil.isNotEmpty(loginUserDataScope)) {
            if(!loginUserDataScope.contains(bizOrg.getId())) {
                throw new CommonException("您没有权限编辑该机构，机构id：{}", bizOrg.getId());
            }
            if(!loginUserDataScope.contains(bizOrg.getParentId())) {
                throw new CommonException("您没有权限编辑该机构下的机构，机构id：{}", bizOrg.getParentId());
            }
        }
        BeanUtil.copyProperties(bizOrgEditParam, bizOrg);
        boolean repeatName = this.count(new LambdaQueryWrapper<BizOrg>().eq(BizOrg::getParentId, bizOrg.getParentId())
                .eq(BizOrg::getName, bizOrg.getName()).ne(BizOrg::getId, bizOrg.getId())) > 0;
        if(repeatName) {
            throw new CommonException("存在重复的同级机构，名称为：{}", bizOrg.getName());
        }
        List<BizOrg> originDataList = this.getAllOrgList();
        boolean errorLevel = this.getChildListById(originDataList, bizOrg.getId(), true).stream()
                .map(BizOrg::getId).collect(Collectors.toList()).contains(bizOrg.getParentId());
        if(errorLevel) {
            throw new CommonException("不可选择上级机构：{}", this.getById(originDataList, bizOrg.getParentId()).getName());
        }
        // 更新机构
        this.updateById(bizOrg);
        // 发布更新事件（BizDataChangeListener 和 SysDataChangeListener 监听后自动清缓存）
        CommonDataChangeEventCenter.doUpdateWithData(BizDataTypeEnum.ORG.getValue(), JSONUtil.createArray().put(bizOrg));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<BizOrgIdParam> bizOrgIdParamList) {
        List<String> orgIdList = CollStreamUtil.toList(bizOrgIdParamList, BizOrgIdParam::getId);
        if(ObjectUtil.isNotEmpty(orgIdList)) {
            // 校验数据范围
            List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
            if(loginUserDataScope != null && loginUserDataScope.isEmpty()) {
                throw new CommonException("您没有权限删除这些机构，机构id：{}", orgIdList);
            }
            if(ObjectUtil.isNotEmpty(loginUserDataScope)) {
                if(!new HashSet<>(loginUserDataScope).containsAll(orgIdList)) {
                    throw new CommonException("您没有权限删除这些机构，机构id：{}", orgIdList);
                }
            }
            List<BizOrg> allOrgList = this.getAllOrgList();
            // 获取所有子机构
            List<String> toDeleteOrgIdList = CollectionUtil.newArrayList();
            orgIdList.forEach(orgId -> toDeleteOrgIdList.addAll(this.getChildListById(allOrgList, orgId, true).stream()
                    .map(BizOrg::getId).collect(Collectors.toList())));
            // 机构下有人不能删除（直属机构）
            boolean hasOrgUser = bizUserService.count(new LambdaQueryWrapper<BizUser>().in(BizUser::getOrgId, toDeleteOrgIdList)) > 0;
            if(hasOrgUser) {
                throw new CommonException("请先删除机构下的人员");
            }
            // 机构下有人不能删除（兼任机构）
            List<String> positionJsonList = bizUserService.list(new LambdaQueryWrapper<BizUser>()
                    .isNotNull(BizUser::getPositionJson)).stream().map(BizUser::getPositionJson).collect(Collectors.toList());
            if(ObjectUtil.isNotEmpty(positionJsonList)) {
                List<String> positionOrgIdList = CollectionUtil.newArrayList();
                positionJsonList.forEach(positionJson -> JSONUtil.toList(JSONUtil.parseArray(positionJson), JSONObject.class)
                        .forEach(jsonObject -> positionOrgIdList.add(jsonObject.getStr("orgId"))));
                boolean hasPositionUser = !CollectionUtil.intersectionDistinct(toDeleteOrgIdList, CollectionUtil.removeNull(positionOrgIdList)).isEmpty();
                if(hasPositionUser) {
                    throw new CommonException("请先删除机构下的人员");
                }
            }
            // 机构下有角色不能删除
            boolean hasRole = sysRoleApi.orgHasRole(toDeleteOrgIdList);
            if(hasRole) {
                throw new CommonException("请先删除机构下的角色");
            }
            // 机构下有岗位不能删除
            boolean hasPosition = bizPositionService.count(new LambdaQueryWrapper<BizPosition>().in(BizPosition::getOrgId, toDeleteOrgIdList)) > 0;
            if(hasPosition) {
                throw new CommonException("请先删除机构下的岗位");
            }
            // 执行删除
            this.removeByIds(toDeleteOrgIdList);

            // 发布删除事件（BizDataChangeListener 和 SysDataChangeListener 监听后自动清缓存）
            CommonDataChangeEventCenter.doDeleteWithDataIdList(BizDataTypeEnum.ORG.getValue(), toDeleteOrgIdList);
        }
    }

    @Override
    public BizOrg detail(BizOrgIdParam bizOrgIdParam) {
        return this.queryEntity(bizOrgIdParam.getId());
    }

    @Override
    public BizOrg queryEntity(String id) {
        BizOrg bizOrg = this.getById(id);
        if(ObjectUtil.isEmpty(bizOrg)) {
            throw new CommonException("机构不存在，id值为：{}", id);
        }
        return bizOrg;
    }

    @Override
    public List<BizOrg> getAllOrgList() {
        // 优先从本地内存缓存获取，避免每次JSON反序列化
        List<BizOrg> localCache = localOrgListCache;
        if (localCache != null) {
            return localCache;
        }
        // 其次从Redis缓存获取
        Object cached = commonCacheOperator.get(ORG_ALL_LIST_CACHE_KEY);
        if (cached != null) {
            List<BizOrg> list = JSONUtil.toList(JSONUtil.parseArray(cached), BizOrg.class);
            localOrgListCache = list;
            return list;
        }
        // 最后从数据库查询
        List<BizOrg> list = this.list(new LambdaQueryWrapper<BizOrg>().orderByAsc(BizOrg::getSortCode));
        commonCacheOperator.put(ORG_ALL_LIST_CACHE_KEY, list);
        localOrgListCache = list;
        return list;
    }

    /**
     * 使机构相关缓存失效（版本号递增 + 清除全量列表缓存）
     * 机构增删改时调用，visibleOrgIds 缓存通过版本号自动失效，旧条目由 TTL 自动清理
     */
    private void invalidateOrgCaches() {
        localOrgListCache = null;
        commonCacheOperator.remove(ORG_ALL_LIST_CACHE_KEY);
        commonCacheOperator.put(ORG_CACHE_VERSION_KEY, String.valueOf(System.currentTimeMillis()));
    }

    @Override
    public void clearOrgCache() {
        this.invalidateOrgCaches();
    }

    /**
     * 获取当前用户可见的机构ID集合（带版本化缓存）
     * 缓存命中时直接返回，无需加载全量机构数据；缓存未命中时计算并缓存
     *
     * @param loginUserDataScope 当前用户的数据范围（直接授权的机构ID列表）
     * @return 可见机构ID集合（包含授权机构及其所有父级）
     */
    private Set<String> getVisibleOrgIds(List<String> loginUserDataScope) {
        if (ObjectUtil.isEmpty(loginUserDataScope)) {
            return Collections.emptySet();
        }
        // 获取当前缓存版本号
        Object versionObj = commonCacheOperator.get(ORG_CACHE_VERSION_KEY);
        String version = versionObj != null ? versionObj.toString() : "0";

        // 构建缓存key：前缀 + 版本号 + : + 数据范围hash
        int scopeHash = loginUserDataScope.stream().sorted().collect(Collectors.joining(",")).hashCode();
        String cacheKey = VISIBLE_ORG_IDS_CACHE_KEY_PREFIX + version + ":" + scopeHash;

        // 尝试从缓存获取
        Object cached = commonCacheOperator.get(cacheKey);
        if (cached != null) {
            return new HashSet<>(JSONUtil.toList(JSONUtil.parseArray(cached), String.class));
        }

        // 缓存未命中，计算可见机构ID集合
        List<BizOrg> allOrgList = this.getAllOrgList();
        Map<String, BizOrg> orgById = allOrgList.stream()
                .collect(Collectors.toMap(BizOrg::getId, org -> org, (a, b) -> a));

        Set<String> visibleOrgIds = new HashSet<>();
        for (String orgId : loginUserDataScope) {
            if (orgById.containsKey(orgId)) {
                visibleOrgIds.add(orgId);
                // 向上遍历添加所有父级
                String currentId = orgById.get(orgId).getParentId();
                while (ObjectUtil.isNotEmpty(currentId) && !"0".equals(currentId) && orgById.containsKey(currentId)) {
                    if (!visibleOrgIds.add(currentId)) {
                        break; // 已经添加过，停止遍历
                    }
                    currentId = orgById.get(currentId).getParentId();
                }
            }
        }

        // 写入缓存（带TTL，旧版本条目自动过期）
        commonCacheOperator.put(cacheKey, JSONUtil.toJsonStr(visibleOrgIds), VISIBLE_ORG_IDS_CACHE_TTL);
        return visibleOrgIds;
    }

    @Override
    public String getOrgIdByOrgFullNameWithCreate(String orgFullName) {
        List<BizOrg> allOrgList = this.getAllOrgList();
        List<Tree<String>> treeList = TreeUtil.build(allOrgList.stream().map(bizOrg ->
                new TreeNode<>(bizOrg.getId(), bizOrg.getParentId(), bizOrg.getName(), bizOrg.getSortCode()))
                .collect(Collectors.toList()), "0");
        return findOrgIdByOrgName("0", StrUtil.split(orgFullName, StrUtil.DASHED).iterator(), allOrgList, treeList);
    }

    public String findOrgIdByOrgName(String parentId, Iterator<String> iterator, List<BizOrg> allOrgList, List<Tree<String>> treeList) {
        String orgName = iterator.next();
        if(ObjectUtil.isNotEmpty(treeList)) {
            List<Tree<String>> findList = treeList.stream().filter(tree -> tree.getName().equals(orgName)).collect(Collectors.toList());
            if(ObjectUtil.isNotEmpty(findList)) {
                if(iterator.hasNext()) {
                    return findOrgIdByOrgName(findList.get(0).getId(), iterator, allOrgList, findList.get(0).getChildren());
                } else {
                    return findList.get(0).getId();
                }
            }
        }
        String orgId = this.doCreateOrg(parentId, orgName, allOrgList);
        if(iterator.hasNext()) {
            return findOrgIdByOrgName(orgId, iterator, allOrgList, CollectionUtil.newArrayList());
        } else {
            return orgId;
        }
    }

    /**
     * 执行创建机构
     *
     * @author xuyuxiang
     * @date 2023/3/8 9:38
     **/
    public String doCreateOrg(String parentId, String orgName, List<BizOrg> allOrgList) {
        //创建该机构
        BizOrg bizOrg = new BizOrg();
        bizOrg.setName(orgName);
        bizOrg.setCode(RandomUtil.randomString(10));
        bizOrg.setParentId(parentId);
        bizOrg.setCategory("0".equals(parentId)?BizOrgCategoryEnum.COMPANY.getValue():BizOrgCategoryEnum.DEPT.getValue());
        bizOrg.setSortCode(99);
        this.save(bizOrg);
        // 发布增加事件（BizDataChangeListener 和 SysDataChangeListener 监听后自动清缓存）
        CommonDataChangeEventCenter.doAddWithData(BizDataTypeEnum.ORG.getValue(), JSONUtil.createArray().put(bizOrg));
        return bizOrg.getId();
    }

    /* ====机构部分所需要用到的选择器==== */

    @Override
    public List<JSONObject> orgTreeSelector(BizOrgSelectorTreeParam bizOrgSelectorTreeParam) {
        // searchKey不为null时，走全量搜索模式，返回嵌套树结构
        if (bizOrgSelectorTreeParam.getSearchKey() != null) {
            return this.treeSearch(bizOrgSelectorTreeParam.getSearchKey());
        }
        String parentId = ObjectUtil.isNotEmpty(bizOrgSelectorTreeParam.getParentId()) ? bizOrgSelectorTreeParam.getParentId() : "0";
        // 校验数据范围
        List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
        if (loginUserDataScope != null && ObjectUtil.isEmpty(loginUserDataScope)) {
            return CollectionUtil.newArrayList();
        }
        // loginUserDataScope == null 时为 SCOPE_ALL，不做数据范围过滤；否则取可见机构ID集合
        Set<String> visibleOrgIds = null;
        if (loginUserDataScope != null) {
            visibleOrgIds = this.getVisibleOrgIds(loginUserDataScope);
            if (ObjectUtil.isEmpty(visibleOrgIds)) {
                return CollectionUtil.newArrayList();
            }
        }
        return this.fetchChildrenWithLeafFlag(parentId, visibleOrgIds);
    }

    @Override
    public Page<BizOrg> orgListSelector(BizOrgSelectorOrgListParam bizOrgSelectorOrgListParam) {
        QueryWrapper<BizOrg> queryWrapper = new QueryWrapper<BizOrg>().checkSqlInjection();
        // 校验数据范围
        List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
        if(loginUserDataScope != null && loginUserDataScope.isEmpty()) {
            return new Page<>();
        }
        if(ObjectUtil.isNotEmpty(loginUserDataScope)) {
            CommonSqlUtil.scopeIn(queryWrapper.lambda(), BizOrg::getId, StpUtil.getLoginIdAsString(), CommonServletUtil.getRequest().getServletPath());
        }
        // 查询部分字段
        queryWrapper.lambda().select(BizOrg::getId, BizOrg::getParentId, BizOrg::getName,
                BizOrg::getCategory, BizOrg::getSortCode);
        if(ObjectUtil.isNotEmpty(bizOrgSelectorOrgListParam.getParentId())) {
            queryWrapper.lambda().eq(BizOrg::getParentId, bizOrgSelectorOrgListParam.getParentId());
        }
        if(ObjectUtil.isNotEmpty(bizOrgSelectorOrgListParam.getSearchKey())) {
            queryWrapper.lambda().like(BizOrg::getName, bizOrgSelectorOrgListParam.getSearchKey());
        }
        queryWrapper.lambda().orderByAsc(BizOrg::getSortCode);
        return this.page(CommonPageRequest.defaultPage(), queryWrapper.lambda());
    }

    @Override
    public Page<BizUser> userSelector(BizOrgSelectorUserParam bizOrgSelectorUserParam) {
        QueryWrapper<BizUser> queryWrapper = new QueryWrapper<BizUser>().checkSqlInjection();
        // 校验数据范围
        List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
        if(loginUserDataScope != null && loginUserDataScope.isEmpty()) {
            return new Page<>();
        }
        if(ObjectUtil.isNotEmpty(loginUserDataScope)) {
            CommonSqlUtil.scopeIn(queryWrapper.lambda(), BizUser::getOrgId, StpUtil.getLoginIdAsString(), CommonServletUtil.getRequest().getServletPath());
        }
        // 只查询部分字段
        queryWrapper.lambda().select(BizUser::getId, BizUser::getAvatar, BizUser::getOrgId, BizUser::getPositionId, BizUser::getAccount,
                BizUser::getName, BizUser::getSortCode, BizUser::getGender, BizUser::getEntryDate);
        if (ObjectUtil.isNotEmpty(bizOrgSelectorUserParam.getOrgId())) {
            // 如果机构id不为空，则查询该机构及其子机构下的所有人
            List<String> childOrgIdList = CollStreamUtil.toList(this.getChildListById(this
                    .getAllOrgList(), bizOrgSelectorUserParam.getOrgId(), true), BizOrg::getId);
            if (ObjectUtil.isNotEmpty(childOrgIdList)) {
                queryWrapper.lambda().in(BizUser::getOrgId, childOrgIdList);
            } else {
                return new Page<>();
            }
        }
        if(ObjectUtil.isNotEmpty(bizOrgSelectorUserParam.getSearchKey())) {
            queryWrapper.lambda().like(BizUser::getName, bizOrgSelectorUserParam.getSearchKey());
        }
        queryWrapper.lambda().orderByAsc(BizUser::getSortCode);
        return bizUserService.page(CommonPageRequest.defaultPage(), queryWrapper.lambda());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void copy(BizOrgCopyParam bizOrgCopyParam) {
        // 获取目标父id
        String targetParentId = bizOrgCopyParam.getTargetParentId();
        // 获取机构id集合
        List<String> orgIdList = bizOrgCopyParam.getIds();
        if(ObjectUtil.isNotEmpty(orgIdList)) {
            // 校验数据范围
            List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
            if(loginUserDataScope != null && loginUserDataScope.isEmpty()) {
                throw new CommonException("您没有权限复制机构");
            }
            if(ObjectUtil.isNotEmpty(loginUserDataScope)) {
                // 如果有数据范围限制，则校验目标父id是否有权限
                if(!loginUserDataScope.contains(targetParentId)) {
                    throw new CommonException("您没有权限在该机构下增加机构，机构id：{}", targetParentId);
                }
                // 再校验源ID权限
                if(!new HashSet<>(loginUserDataScope).containsAll(orgIdList)) {
                    throw new CommonException("您没有权限复制这些机构，机构id：{}", orgIdList);
                }
            }

            // 遍历复制
            orgIdList.forEach(orgId -> {
                BizOrg bizOrg = this.getById(orgId);
                if(ObjectUtil.isNotEmpty(bizOrg)) {
                    // 查询是否有重复名称
                    boolean repeatName = this.count(new LambdaQueryWrapper<BizOrg>()
                            .eq(BizOrg::getParentId, targetParentId)
                            .eq(BizOrg::getName, bizOrg.getName())) > 0;
                    // 如果有重复名称则跳过
                    if(!repeatName) {
                        BizOrg copyBizOrg = new BizOrg();
                        // 复制部分字段
                        copyBizOrg.setName(bizOrg.getName());
                        copyBizOrg.setCategory(bizOrg.getCategory());
                        copyBizOrg.setSortCode(bizOrg.getSortCode());
                        copyBizOrg.setExtJson(bizOrg.getExtJson());
                        // 设置父id
                        copyBizOrg.setParentId(targetParentId);
                        // 重新生成code
                        copyBizOrg.setCode(RandomUtil.randomString(10));
                        // 主管置空
                        copyBizOrg.setDirectorId(null);
                        // 保存
                        this.save(copyBizOrg);
                        // 插入扩展信息
                        bizOrgExtService.createExtInfo(copyBizOrg.getId(), BizOrgSourceFromTypeEnum.SYSTEM_ADD.getValue());
                        // 发布增加事件（BizDataChangeListener 和 SysDataChangeListener 监听后自动清缓存）
                        CommonDataChangeEventCenter.doAddWithData(BizDataTypeEnum.ORG.getValue(), JSONUtil.createArray().put(copyBizOrg));
                    }
                }
            });
        }
    }

    /* ====以下为各种递归方法==== */

    @Override
    public List<BizOrg> getParentAndChildListById(List<BizOrg> originDataList, String id, boolean includeSelf) {
        List<BizOrg> parentListById = this.getParentListById(originDataList, id, false);
        List<BizOrg> childListById = this.getChildListById(originDataList, id, true);
        parentListById.addAll(childListById);
        return parentListById;
    }

    @Override
    public List<BizOrg> getChildListById(List<BizOrg> originDataList, String id, boolean includeSelf) {
        List<BizOrg> resultList = CollectionUtil.newArrayList();
        execRecursionFindChild(originDataList, id, resultList);
        if(includeSelf) {
            BizOrg self = this.getById(originDataList, id);
            if(ObjectUtil.isNotEmpty(self)) {
                resultList.add(self);
            }
        }
        return resultList;
    }

    @Override
    public List<BizOrg> getParentListById(List<BizOrg> originDataList, String id, boolean includeSelf) {
        List<BizOrg> resultList = CollectionUtil.newArrayList();
        execRecursionFindParent(originDataList, id, resultList);
        if(includeSelf) {
            BizOrg self = this.getById(originDataList, id);
            if(ObjectUtil.isNotEmpty(self)) {
                resultList.add(self);
            }
        }
        return resultList;
    }


    public void execRecursionFindChild(List<BizOrg> originDataList, String id, List<BizOrg> resultList) {
        originDataList.forEach(item -> {
            if(item.getParentId().equals(id)) {
                resultList.add(item);
                execRecursionFindChild(originDataList, item.getId(), resultList);
            }
        });
    }

    public void execRecursionFindParent(List<BizOrg> originDataList, String id, List<BizOrg> resultList) {
        originDataList.forEach(item -> {
            if(item.getId().equals(id)) {
                BizOrg parent = this.getById(originDataList, item.getParentId());
                if(ObjectUtil.isNotEmpty(parent)) {
                    resultList.add(parent);
                }
                execRecursionFindParent(originDataList, item.getParentId(), resultList);
            }
        });
    }

    @Override
    public BizOrg getById(List<BizOrg> originDataList, String id) {
        int index = CollStreamUtil.toList(originDataList, BizOrg::getId).indexOf(id);
        return index == -1?null:originDataList.get(index);
    }

    @Override
    public BizOrg getParentById(List<BizOrg> originDataList, String id) {
        BizOrg self = this.getById(originDataList, id);
        return ObjectUtil.isNotEmpty(self)?self:this.getById(originDataList, self.getParentId());
    }

    @Override
    public BizOrg getChildById(List<BizOrg> originDataList, String id) {
        int index = CollStreamUtil.toList(originDataList, BizOrg::getParentId).indexOf(id);
        return index == -1?null:originDataList.get(index);
    }

    @Override
    public List<JSONObject> getAncestorNodes(List<String> orgIdList) {
        if (ObjectUtil.isEmpty(orgIdList)) {
            return CollectionUtil.newArrayList();
        }
        List<BizOrg> allOrgList = this.getAllOrgList();
        Set<String> neededIdSet = new LinkedHashSet<>();
        for (String orgId : orgIdList) {
            List<BizOrg> ancestorList = this.getParentListById(allOrgList, orgId, true);
            for (BizOrg org : ancestorList) {
                neededIdSet.add(org.getId());
            }
        }
        if (ObjectUtil.isEmpty(neededIdSet)) {
            return CollectionUtil.newArrayList();
        }
        List<BizOrg> resultOrgList = allOrgList.stream()
                .filter(org -> neededIdSet.contains(org.getId()))
                .sorted(Comparator.comparingInt(BizOrg::getSortCode).thenComparing(BizOrg::getId))
                .collect(Collectors.toList());
        List<String> resultIds = resultOrgList.stream().map(BizOrg::getId).collect(Collectors.toList());
        Set<String> hasChildrenParentIds = this.list(new LambdaQueryWrapper<BizOrg>()
                        .select(BizOrg::getParentId)
                        .in(BizOrg::getParentId, resultIds))
                .stream().map(BizOrg::getParentId).collect(Collectors.toSet());
        return resultOrgList.stream().map(bizOrg -> {
            JSONObject jsonObject = JSONUtil.parseObj(bizOrg);
            jsonObject.set("isLeaf", !hasChildrenParentIds.contains(bizOrg.getId()));
            return jsonObject;
        }).collect(Collectors.toList());
    }
}
