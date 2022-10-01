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
package vip.xiaonuo.biz.modular.user.service.impl;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.PhoneUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vip.xiaonuo.auth.core.util.StpLoginUserUtil;
import vip.xiaonuo.biz.core.enums.BizBuildInEnum;
import vip.xiaonuo.biz.modular.org.entity.BizOrg;
import vip.xiaonuo.biz.modular.org.service.BizOrgService;
import vip.xiaonuo.biz.modular.position.entity.BizPosition;
import vip.xiaonuo.biz.modular.position.service.BizPositionService;
import vip.xiaonuo.biz.modular.user.entity.BizUser;
import vip.xiaonuo.biz.modular.user.enums.BizUserStatusEnum;
import vip.xiaonuo.biz.modular.user.mapper.BizUserMapper;
import vip.xiaonuo.biz.modular.user.param.*;
import vip.xiaonuo.biz.modular.user.result.BizUserExportResult;
import vip.xiaonuo.biz.modular.user.result.BizUserRoleResult;
import vip.xiaonuo.biz.modular.user.service.BizUserService;
import vip.xiaonuo.common.enums.CommonSortOrderEnum;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.common.page.CommonPageRequest;
import vip.xiaonuo.common.util.*;
import vip.xiaonuo.dev.api.DevConfigApi;
import vip.xiaonuo.sys.api.SysRoleApi;
import vip.xiaonuo.sys.api.SysUserApi;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 人员Service接口实现类
 *
 * @author xuyuxiang
 * @date 2022/2/23 18:43
 **/
@Service
public class BizUserServiceImpl extends ServiceImpl<BizUserMapper, BizUser> implements BizUserService {

    private static final String SNOWY_SYS_DEFAULT_PASSWORD_KEY = "SNOWY_SYS_DEFAULT_PASSWORD";

    @Resource
    private DevConfigApi devConfigApi;

    @Resource
    private SysUserApi sysUserApi;

    @Resource
    private SysRoleApi sysRoleApi;

    @Resource
    private BizOrgService bizOrgService;

    @Resource
    private BizPositionService bizPositionService;

    @Override
    public Page<BizUser> page(BizUserPageParam bizUserPageParam) {
        QueryWrapper<BizUser> queryWrapper = new QueryWrapper<>();
        if (ObjectUtil.isNotEmpty(bizUserPageParam.getSearchKey())) {
            queryWrapper.lambda().like(BizUser::getAccount, bizUserPageParam.getSearchKey()).or()
                    .like(BizUser::getName, bizUserPageParam.getSearchKey());
        }
        if (ObjectUtil.isNotEmpty(bizUserPageParam.getOrgId())) {
            queryWrapper.lambda().eq(BizUser::getOrgId, bizUserPageParam.getOrgId());
        }
        if (ObjectUtil.isNotEmpty(bizUserPageParam.getUserStatus())) {
            queryWrapper.lambda().eq(BizUser::getUserStatus, bizUserPageParam.getUserStatus());
        }
        if (ObjectUtil.isAllNotEmpty(bizUserPageParam.getSortField(), bizUserPageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(bizUserPageParam.getSortOrder());
            queryWrapper.orderBy(true, bizUserPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(bizUserPageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(BizUser::getSortCode);
        }
        // 排除超管
        queryWrapper.lambda().ne(BizUser::getAccount, BizBuildInEnum.BUILD_IN_USER_ACCOUNT.getValue());
        // 校验数据范围
        List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
        if(ObjectUtil.isNotEmpty(loginUserDataScope)) {
            queryWrapper.lambda().in(BizUser::getOrgId, loginUserDataScope);
        } else {
            queryWrapper.lambda().eq(BizUser::getId, StpUtil.getLoginIdAsString());
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(BizUserAddParam bizUserAddParam) {
        checkParam(bizUserAddParam);
        BizUser bizUser = BeanUtil.toBean(bizUserAddParam, BizUser.class);
        if(ObjectUtil.isEmpty(bizUser.getAvatar())) {
            // 设置默认头像
            bizUser.setAvatar(CommonAvatarUtil.generateImg(bizUser.getName()));
        }
        // 设置密码
        bizUser.setPassword(CommonCryptogramUtil.doHashValue(devConfigApi.getValueByKey(SNOWY_SYS_DEFAULT_PASSWORD_KEY)));
        // 设置状态
        bizUser.setUserStatus(BizUserStatusEnum.ENABLE.getValue());
        this.save(bizUser);
    }

    private void checkParam(BizUserAddParam bizUserAddParam) {
        // 校验数据范围
        List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
        if(ObjectUtil.isNotEmpty(loginUserDataScope)) {
            if(!loginUserDataScope.contains(bizUserAddParam.getOrgId())) {
                throw new CommonException("您没有权限在该机构下增加人员，机构id：{}", bizUserAddParam.getOrgId());
            }
        } else {
            throw new CommonException("您没有权限在该机构下增加人员，机构id：{}", bizUserAddParam.getOrgId());
        }
        if (this.count(new LambdaQueryWrapper<BizUser>()
                .eq(BizUser::getAccount, bizUserAddParam.getAccount())) > 0) {
            throw new CommonException("存在重复的账号，账号为：{}", bizUserAddParam.getAccount());
        }
        if(ObjectUtil.isNotEmpty(bizUserAddParam.getPhone())) {
            if(!PhoneUtil.isMobile(bizUserAddParam.getPhone())) {
                throw new CommonException("手机号码：{}格式错误", bizUserAddParam.getPhone());
            }
            if (this.count(new LambdaQueryWrapper<BizUser>()
                    .eq(BizUser::getPhone, bizUserAddParam.getPhone())) > 0) {
                throw new CommonException("存在重复的手机号，手机号为：{}", bizUserAddParam.getPhone());
            }
        }
        if(ObjectUtil.isNotEmpty(bizUserAddParam.getEmail())) {
            if(!CommonEmailUtil.isEmail(bizUserAddParam.getEmail())) {
                throw new CommonException("邮箱：{}格式错误", bizUserAddParam.getPhone());
            }
            if (this.count(new LambdaQueryWrapper<BizUser>()
                    .eq(BizUser::getEmail, bizUserAddParam.getEmail())) > 0) {
                throw new CommonException("存在重复的邮箱，邮箱为：{}", bizUserAddParam.getEmail());
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(BizUserEditParam bizUserEditParam) {
        BizUser bizUser = this.queryEntity(bizUserEditParam.getId());
        checkParam(bizUserEditParam);
        boolean updateSuperAdminAccount = bizUser.getAccount().equals(BizBuildInEnum.BUILD_IN_USER_ACCOUNT.getValue()) &&
                !bizUserEditParam.getAccount().equals(BizBuildInEnum.BUILD_IN_USER_ACCOUNT.getValue());
        if(updateSuperAdminAccount) {
            throw new CommonException("不可修改系统内置超管用户账号");
        }
        BeanUtil.copyProperties(bizUserEditParam, bizUser);
        this.updateById(bizUser);
    }

    private void checkParam(BizUserEditParam bizUserEditParam) {
        // 校验数据范围
        List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
        if(ObjectUtil.isNotEmpty(loginUserDataScope)) {
            if(!loginUserDataScope.contains(bizUserEditParam.getOrgId())) {
                throw new CommonException("您没有权限编辑该机构下的人员，机构id：{}", bizUserEditParam.getOrgId());
            }
        } else {
            if(!bizUserEditParam.getId().equals(StpUtil.getLoginIdAsString())) {
                throw new CommonException("您没有权限编辑该机构下的人员，机构id：{}", bizUserEditParam.getOrgId());
            }
        }
        if (this.count(new LambdaQueryWrapper<BizUser>()
                .eq(BizUser::getAccount, bizUserEditParam.getAccount())
                .ne(BizUser::getId, bizUserEditParam.getId())) > 0) {
            throw new CommonException("存在重复的账号，账号为：{}", bizUserEditParam.getAccount());
        }
        if(ObjectUtil.isNotEmpty(bizUserEditParam.getPhone())) {
            if(!PhoneUtil.isMobile(bizUserEditParam.getPhone())) {
                throw new CommonException("手机号码：{}格式错误", bizUserEditParam.getPhone());
            }
            if (this.count(new LambdaQueryWrapper<BizUser>()
                    .eq(BizUser::getPhone, bizUserEditParam.getPhone())
                    .ne(BizUser::getId, bizUserEditParam.getId())) > 0) {
                throw new CommonException("存在重复的手机号，手机号为：{}", bizUserEditParam.getPhone());
            }
        }
        if(ObjectUtil.isNotEmpty(bizUserEditParam.getEmail())) {
            if(!CommonEmailUtil.isEmail(bizUserEditParam.getEmail())) {
                throw new CommonException("邮箱：{}格式错误", bizUserEditParam.getPhone());
            }
            if (this.count(new LambdaQueryWrapper<BizUser>()
                    .eq(BizUser::getEmail, bizUserEditParam.getEmail())
                    .ne(BizUser::getId, bizUserEditParam.getId())) > 0) {
                throw new CommonException("存在重复的邮箱，邮箱为：{}", bizUserEditParam.getEmail());
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<BizUserIdParam> bizUserIdParamList) {
        List<String> bizUserIdList = CollStreamUtil.toList(bizUserIdParamList, BizUserIdParam::getId);
        if(ObjectUtil.isNotEmpty(bizUserIdList)) {
            boolean containsSuperAdminAccount = this.listByIds(bizUserIdList).stream().map(BizUser::getAccount)
                    .collect(Collectors.toSet()).contains(BizBuildInEnum.BUILD_IN_USER_ACCOUNT.getValue());
            if(containsSuperAdminAccount) {
                throw new CommonException("不可删除系统内置超管用户");
            }
            // 获取这些人员的的机构id集合
            Set<String> userOrgIdList = this.listByIds(bizUserIdList).stream().map(BizUser::getOrgId).collect(Collectors.toSet());
            // 校验数据范围
            List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
            if(ObjectUtil.isNotEmpty(loginUserDataScope)) {
                if(!loginUserDataScope.containsAll(userOrgIdList)) {
                    throw new CommonException("您没有权限删除这些机构下的人员，机构id：{}",
                            CollectionUtil.subtract(userOrgIdList, loginUserDataScope));
                }
            } else {
                if(bizUserIdList.size() != 1 || !bizUserIdList.get(0).equals(StpUtil.getLoginIdAsString())) {
                    throw new CommonException("您没有权限删除这些机构下的人员，机构id：{}", userOrgIdList);
                }
            }
            // 清除【将这些人员作为主管】的信息
            this.update(new LambdaUpdateWrapper<BizUser>().in(BizUser::getDirectorId, bizUserIdList).set(BizUser::getDirectorId, null));
            // 清除【将这些人员作为兼任岗位的主管】的信息
            this.list(new LambdaQueryWrapper<BizUser>() .isNotNull(BizUser::getPositionJson)).forEach(sysUser -> {
                List<JSONObject> handledJsonObjectList = JSONUtil.toList(JSONUtil.parseArray(sysUser.getPositionJson()),
                        JSONObject.class).stream().peek(jsonObject -> {
                    String directorId = jsonObject.getStr("directorId");
                    if (ObjectUtil.isNotEmpty(directorId) && bizUserIdList.contains(directorId)) {
                        jsonObject.remove("directorId");
                    }
                }).collect(Collectors.toList());
                this.update(new LambdaUpdateWrapper<BizUser>().eq(BizUser::getId, sysUser.getId())
                        .set(BizUser::getPositionJson, JSONUtil.toJsonStr(handledJsonObjectList)));
            });
            // 执行删除
            this.removeBatchByIds(bizUserIdList);

            // TODO 此处需要将这些人员踢下线，并永久注销这些人员
        }
    }

    @Override
    public BizUser detail(BizUserIdParam bizUserIdParam) {
        return this.queryEntity(bizUserIdParam.getId());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void disableUser(BizUserIdParam bizUserIdParam) {
        BizUser bizUser = this.detail(bizUserIdParam);
        // 校验数据范围
        List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
        if(ObjectUtil.isNotEmpty(loginUserDataScope)) {
            if(!loginUserDataScope.contains(bizUser.getOrgId())) {
                throw new CommonException("您没有权限禁用该机构下的人员：{}，机构id：{}", bizUser.getName(), bizUser.getOrgId());
            }
        } else {
            if(!bizUser.getId().equals(StpUtil.getLoginIdAsString())) {
                throw new CommonException("您没有权限禁用该机构下的人员：{}，机构id：{}", bizUser.getName(), bizUser.getOrgId());
            }
        }
        this.update(new LambdaUpdateWrapper<BizUser>().eq(BizUser::getId,
                bizUserIdParam.getId()).set(BizUser::getUserStatus, BizUserStatusEnum.DISABLED.getValue()));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void enableUser(BizUserIdParam bizUserIdParam) {
        BizUser bizUser = this.detail(bizUserIdParam);
        // 校验数据范围
        List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
        if(ObjectUtil.isNotEmpty(loginUserDataScope)) {
            if(!loginUserDataScope.contains(bizUser.getOrgId())) {
                throw new CommonException("您没有权限启用该机构下的人员：{}，机构id：{}", bizUser.getName(), bizUser.getOrgId());
            }
        } else {
            if(!bizUser.getId().equals(StpUtil.getLoginIdAsString())) {
                throw new CommonException("您没有权限启用该机构下的人员：{}，机构id：{}", bizUser.getName(), bizUser.getOrgId());
            }
        }
        this.update(new LambdaUpdateWrapper<BizUser>().eq(BizUser::getId,
                bizUserIdParam.getId()).set(BizUser::getUserStatus, BizUserStatusEnum.ENABLE.getValue()));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void resetPassword(BizUserIdParam bizUserIdParam) {
        BizUser bizUser = this.detail(bizUserIdParam);
        // 校验数据范围
        List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
        if(ObjectUtil.isNotEmpty(loginUserDataScope)) {
            if(!loginUserDataScope.contains(bizUser.getOrgId())) {
                throw new CommonException("您没有权限为该机构下的人员：{}重置密码，机构id：{}", bizUser.getName(), bizUser.getOrgId());
            }
        } else {
            if(!bizUser.getId().equals(StpUtil.getLoginIdAsString())) {
                throw new CommonException("您没有权限为该机构下的人员：{}重置密码，机构id：{}", bizUser.getName(), bizUser.getOrgId());
            }
        }
        this.update(new LambdaUpdateWrapper<BizUser>().eq(BizUser::getId,
                bizUserIdParam.getId()).set(BizUser::getPassword,
                CommonCryptogramUtil.doHashValue(devConfigApi.getValueByKey(SNOWY_SYS_DEFAULT_PASSWORD_KEY))));
    }

    @Override
    public List<String> ownRole(BizUserIdParam bizUserIdParam) {
        return sysUserApi.ownRole(bizUserIdParam.getId());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void grantRole(BizUserGrantRoleParam bizUserGrantRoleParam) {
        BizUser bizUser = this.queryEntity(bizUserGrantRoleParam.getId());
        // 校验数据范围
        List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
        if(ObjectUtil.isNotEmpty(loginUserDataScope)) {
            if(!loginUserDataScope.contains(bizUser.getOrgId())) {
                throw new CommonException("您没有权限为该机构下的人员：{}授权角色，机构id：{}", bizUser.getName(), bizUser.getOrgId());
            }
        } else {
            if(!bizUser.getId().equals(StpUtil.getLoginIdAsString())) {
                throw new CommonException("您没有权限为该机构下的人员：{}授权角色，机构id：{}", bizUser.getName(), bizUser.getOrgId());
            }
        }
        sysUserApi.grantRole(bizUserGrantRoleParam.getId(), bizUserGrantRoleParam.getRoleIdList());
    }

    @Override
    public void importUser(MultipartFile file) {
        // TODO 待完善
    }

    @Override
    public void exportUser(BizUserExportParam bizUserExportParam, HttpServletResponse response) throws IOException {
        File tempFile = null;
        try {
            QueryWrapper<BizUser> queryWrapper = new QueryWrapper<>();
            if(ObjectUtil.isNotEmpty(bizUserExportParam.getSearchKey())) {
                queryWrapper.lambda().like(BizUser::getAccount, bizUserExportParam.getSearchKey()).or()
                        .like(BizUser::getName, bizUserExportParam.getSearchKey());
            }
            if(ObjectUtil.isNotEmpty(bizUserExportParam.getUserStatus())) {
                queryWrapper.lambda().eq(BizUser::getUserStatus, bizUserExportParam.getUserStatus());
            }
            String fileName = "SNOWY2.0系统B端人员信息清单";
            List<BizUserExportResult> bizUserExportResultList =this.list(queryWrapper).stream()
                    .map(bizUser -> BeanUtil.copyProperties(bizUser, BizUserExportResult.class)).peek(bizUserExportResult -> {
                if (ObjectUtil.isNotEmpty(bizUserExportResult.getAvatar())) {
                    bizUserExportResult.setAvatarByte(ImgUtil.toBytes(ImgUtil.toImage(StrUtil
                            .split(bizUserExportResult.getAvatar(), StrUtil.COMMA).get(1)), ImgUtil.IMAGE_TYPE_PNG));
                }
            }).collect(Collectors.toList());
            Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams(fileName, "B端人员"),
                    BizUserExportResult.class, bizUserExportResultList);
            tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName + ".xls");
            BufferedOutputStream outputStream = FileUtil.getOutputStream(tempFile);
            workbook.write(outputStream);
            outputStream.close();
            CommonDownloadUtil.download(tempFile, response);
        } catch (Exception e) {
            e.printStackTrace();
            CommonResponseUtil.renderError(response, "导出失败");
        } finally {
            FileUtil.del(tempFile);
        }
    }

    @Override
    public BizUser queryEntity(String id) {
        BizUser bizUser = this.getById(id);
        if(ObjectUtil.isEmpty(bizUser)) {
            throw new CommonException("人员不存在，id值为：{}", id);
        }
        return bizUser;
    }

    /* ====人员部分所需要用到的选择器==== */

    @Override
    public List<Tree<String>> orgTreeSelector() {
        LambdaQueryWrapper<BizOrg> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 校验数据范围
        List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
        if(ObjectUtil.isNotEmpty(loginUserDataScope)) {
            lambdaQueryWrapper.in(BizOrg::getId, loginUserDataScope);
        } else {
            return CollectionUtil.newArrayList();
        }
        lambdaQueryWrapper.orderByAsc(BizOrg::getSortCode);
        List<BizOrg> bizOrgList = bizOrgService.list(lambdaQueryWrapper);
        List<TreeNode<String>> treeNodeList = bizOrgList.stream().map(bizOrg ->
                new TreeNode<>(bizOrg.getId(), bizOrg.getParentId(), bizOrg.getName(), bizOrg.getSortCode()))
                .collect(Collectors.toList());
        return TreeUtil.build(treeNodeList, "0");
    }

    @Override
    public List<BizOrg> orgListSelector(BizUserSelectorOrgListParam bizUserSelectorOrgListParam) {
        LambdaQueryWrapper<BizOrg> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 校验数据范围
        List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
        if(ObjectUtil.isNotEmpty(loginUserDataScope)) {
            lambdaQueryWrapper.in(BizOrg::getId, loginUserDataScope);
        } else {
            return CollectionUtil.newArrayList();
        }
        // 查询部分字段
        lambdaQueryWrapper.select(BizOrg::getId, BizOrg::getParentId, BizOrg::getName,
                BizOrg::getCategory, BizOrg::getSortCode);
        if(ObjectUtil.isNotEmpty(bizUserSelectorOrgListParam.getParentId())) {
            lambdaQueryWrapper.eq(BizOrg::getParentId, bizUserSelectorOrgListParam.getParentId());
        }
        if(ObjectUtil.isNotEmpty(bizUserSelectorOrgListParam.getSearchKey())) {
            lambdaQueryWrapper.like(BizOrg::getName, bizUserSelectorOrgListParam.getSearchKey());
        }
        lambdaQueryWrapper.orderByAsc(BizOrg::getSortCode);
        return bizOrgService.list(lambdaQueryWrapper);
    }

    @Override
    public List<BizPosition> positionSelector(BizUserSelectorPositionParam bizUserSelectorPositionParam) {
        LambdaQueryWrapper<BizPosition> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 校验数据范围
        List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
        if(ObjectUtil.isNotEmpty(loginUserDataScope)) {
            lambdaQueryWrapper.in(BizPosition::getOrgId, loginUserDataScope);
        } else {
            return CollectionUtil.newArrayList();
        }
        // 查询部分字段
        lambdaQueryWrapper.select(BizPosition::getId, BizPosition::getOrgId, BizPosition::getName,
                BizPosition::getCategory, BizPosition::getSortCode);
        if(ObjectUtil.isNotEmpty(bizUserSelectorPositionParam.getOrgId())) {
            lambdaQueryWrapper.eq(BizPosition::getOrgId, bizUserSelectorPositionParam.getOrgId());
        }
        if(ObjectUtil.isNotEmpty(bizUserSelectorPositionParam.getSearchKey())) {
            lambdaQueryWrapper.like(BizPosition::getName, bizUserSelectorPositionParam.getSearchKey());
        }
        lambdaQueryWrapper.orderByAsc(BizPosition::getSortCode);
        return bizPositionService.list(lambdaQueryWrapper);
    }

    @Override
    public List<BizUserRoleResult> roleSelector(BizUserSelectorRoleParam bizUserSelectorRoleParam) {
        // 校验数据范围
        List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
        if(ObjectUtil.isNotEmpty(loginUserDataScope)) {
            if(ObjectUtil.isNotEmpty(bizUserSelectorRoleParam.getOrgId())) {
                if(loginUserDataScope.contains(bizUserSelectorRoleParam.getOrgId())) {
                    return sysRoleApi.roleSelector(bizUserSelectorRoleParam.getOrgId(), bizUserSelectorRoleParam.getCategory(),
                            bizUserSelectorRoleParam.getSearchKey()).stream().map(jsonObject ->
                            JSONUtil.toBean(jsonObject, BizUserRoleResult.class)).collect(Collectors.toList());
                } else {
                    return CollectionUtil.newArrayList();
                }
            } else {
                return sysRoleApi.roleSelector(bizUserSelectorRoleParam.getOrgId(), bizUserSelectorRoleParam.getCategory(),
                        bizUserSelectorRoleParam.getSearchKey()).stream().map(jsonObject ->
                        JSONUtil.toBean(jsonObject, BizUserRoleResult.class)).filter(bizUserRoleResult -> ObjectUtil
                        .isNotEmpty(bizUserRoleResult.getOrgId()) && loginUserDataScope.contains(bizUserRoleResult.getOrgId()))
                        .collect(Collectors.toList());
            }
        } else {
            return CollectionUtil.newArrayList();
        }
    }

    @Override
    public List<BizUser> userSelector(BizUserSelectorUserParam bizUserSelectorUserParam) {
        LambdaQueryWrapper<BizUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 校验数据范围
        List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
        if(ObjectUtil.isNotEmpty(loginUserDataScope)) {
            lambdaQueryWrapper.in(BizUser::getOrgId, loginUserDataScope);
        } else {
            return CollectionUtil.newArrayList();
        }
        // 只查询部分字段
        lambdaQueryWrapper.select(BizUser::getId, BizUser::getOrgId, BizUser::getAccount, BizUser::getName, BizUser::getSortCode);
        if(ObjectUtil.isNotEmpty(bizUserSelectorUserParam.getOrgId())) {
            lambdaQueryWrapper.eq(BizUser::getOrgId, bizUserSelectorUserParam.getOrgId());
        }
        if(ObjectUtil.isNotEmpty(bizUserSelectorUserParam.getSearchKey())) {
            lambdaQueryWrapper.like(BizUser::getName, bizUserSelectorUserParam.getSearchKey());
        }
        lambdaQueryWrapper.orderByAsc(BizUser::getSortCode);
        return this.list(lambdaQueryWrapper);
    }
}
