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

import cn.afterturn.easypoi.cache.manager.POICacheManager;
import cn.afterturn.easypoi.entity.ImageEntity;
import cn.afterturn.easypoi.word.WordExportUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
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
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.handler.context.CellWriteHandlerContext;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.alibaba.excel.write.style.row.AbstractRowHeightStyleStrategy;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fhs.trans.service.impl.TransService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vip.xiaonuo.auth.core.util.StpLoginUserUtil;
import vip.xiaonuo.biz.core.enums.BizBuildInEnum;
import vip.xiaonuo.biz.core.enums.BizDataTypeEnum;
import vip.xiaonuo.biz.modular.org.entity.BizOrg;
import vip.xiaonuo.biz.modular.org.service.BizOrgService;
import vip.xiaonuo.biz.modular.position.entity.BizPosition;
import vip.xiaonuo.biz.modular.position.service.BizPositionService;
import vip.xiaonuo.biz.modular.user.entity.BizUser;
import vip.xiaonuo.biz.modular.user.enums.BizRoleCategoryEnum;
import vip.xiaonuo.biz.modular.user.enums.BizUserStatusEnum;
import vip.xiaonuo.biz.modular.user.mapper.BizUserMapper;
import vip.xiaonuo.biz.modular.user.param.*;
import vip.xiaonuo.biz.modular.user.result.BizUserExportResult;
import vip.xiaonuo.biz.modular.user.result.BizUserRoleResult;
import vip.xiaonuo.biz.modular.user.service.BizUserService;
import vip.xiaonuo.common.enums.CommonSortOrderEnum;
import vip.xiaonuo.common.excel.CommonExcelCustomMergeStrategy;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.common.listener.CommonDataChangeEventCenter;
import vip.xiaonuo.common.page.CommonPageRequest;
import vip.xiaonuo.common.util.*;
import vip.xiaonuo.dev.api.DevConfigApi;
import vip.xiaonuo.sys.api.SysRoleApi;
import vip.xiaonuo.sys.api.SysUserApi;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
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
    private TransService transService;

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
        QueryWrapper<BizUser> queryWrapper = new QueryWrapper<BizUser>().checkSqlInjection();
        if (ObjectUtil.isNotEmpty(bizUserPageParam.getSearchKey())) {
            queryWrapper.lambda().and(q -> q.like(BizUser::getAccount, bizUserPageParam.getSearchKey())
                    .or().like(BizUser::getName, bizUserPageParam.getSearchKey()));
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

        // 发布增加事件
        CommonDataChangeEventCenter.doAddWithData(BizDataTypeEnum.USER.getValue(), JSONUtil.createArray().put(bizUser));
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
                    .eq(BizUser::getPhone, CommonCryptogramUtil.doSm4CbcEncrypt(bizUserAddParam.getPhone()))) > 0) {
                throw new CommonException("存在重复的手机号，手机号为：{}", bizUserAddParam.getPhone());
            }
        }
        if(ObjectUtil.isNotEmpty(bizUserAddParam.getEmail())) {
            if(!CommonEmailUtil.isEmail(bizUserAddParam.getEmail())) {
                throw new CommonException("邮箱：{}格式错误", bizUserAddParam.getEmail());
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
            throw new CommonException("不可修改系统内置超管人员账号");
        }
        BeanUtil.copyProperties(bizUserEditParam, bizUser);
        this.updateById(bizUser);

        // 发布更新事件
        CommonDataChangeEventCenter.doUpdateWithData(BizDataTypeEnum.USER.getValue(), JSONUtil.createArray().put(bizUser));
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
                    .eq(BizUser::getPhone, CommonCryptogramUtil.doSm4CbcEncrypt(bizUserEditParam.getPhone()))
                    .ne(BizUser::getId, bizUserEditParam.getId())) > 0) {
                throw new CommonException("存在重复的手机号，手机号为：{}", bizUserEditParam.getPhone());
            }
        }
        if(ObjectUtil.isNotEmpty(bizUserEditParam.getEmail())) {
            if(!CommonEmailUtil.isEmail(bizUserEditParam.getEmail())) {
                throw new CommonException("邮箱：{}格式错误", bizUserEditParam.getEmail());
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
                throw new CommonException("不可删除系统内置超管人员");
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
            this.list(new LambdaQueryWrapper<BizUser>() .isNotNull(BizUser::getPositionJson)).forEach(bizUser -> {
                List<JSONObject> handledJsonObjectList = JSONUtil.toList(JSONUtil.parseArray(bizUser.getPositionJson()),
                        JSONObject.class).stream().peek(jsonObject -> {
                    String directorId = jsonObject.getStr("directorId");
                    if (ObjectUtil.isNotEmpty(directorId) && bizUserIdList.contains(directorId)) {
                        jsonObject.remove("directorId");
                    }
                }).collect(Collectors.toList());
                this.update(new LambdaUpdateWrapper<BizUser>().eq(BizUser::getId, bizUser.getId())
                        .set(BizUser::getPositionJson, JSONUtil.toJsonStr(handledJsonObjectList)));
            });

            // 清除【将这些人员作为主管】的机构的主管信息
            bizOrgService.update(new LambdaUpdateWrapper<BizOrg>().in(BizOrg::getDirectorId, bizUserIdList).set(BizOrg::getDirectorId, null));

            // 执行删除
            this.removeByIds(bizUserIdList);

            // 发布删除事件
            CommonDataChangeEventCenter.doDeleteWithDataId(BizDataTypeEnum.USER.getValue(), bizUserIdList);
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
    public void exportUser(BizUserExportParam bizUserExportParam, HttpServletResponse response) throws IOException {
        File tempFile = null;
        try {
            QueryWrapper<BizUser> queryWrapper = new QueryWrapper<BizUser>().checkSqlInjection();
            // 排除超管
            queryWrapper.lambda().ne(BizUser::getAccount, BizBuildInEnum.BUILD_IN_USER_ACCOUNT.getValue());
            // 校验数据范围
            List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
            if(ObjectUtil.isNotEmpty(loginUserDataScope)) {
                queryWrapper.lambda().in(BizUser::getOrgId, loginUserDataScope);
            } else {
                queryWrapper.lambda().eq(BizUser::getId, StpUtil.getLoginIdAsString());
            }
            if(ObjectUtil.isNotEmpty(bizUserExportParam.getUserIds())) {
                queryWrapper.lambda().in(BizUser::getId, StrUtil.split(bizUserExportParam.getUserIds(), StrUtil.COMMA));
            } else {
                if (ObjectUtil.isNotEmpty(bizUserExportParam.getSearchKey())) {
                    queryWrapper.lambda().and(q -> q.like(BizUser::getAccount, bizUserExportParam.getSearchKey())
                            .or().like(BizUser::getName, bizUserExportParam.getSearchKey())
                            .or().like(BizUser::getPhone, bizUserExportParam.getSearchKey()));
                }
                if (ObjectUtil.isNotEmpty(bizUserExportParam.getUserStatus())) {
                    queryWrapper.lambda().eq(BizUser::getUserStatus, bizUserExportParam.getUserStatus());
                }
            }
            String fileName = "SNOWY系统B端人员信息清单.xlsx";
            List<BizUser> bizUserList = this.list(queryWrapper);
            if(ObjectUtil.isEmpty(bizUserList)) {
                throw new CommonException("无数据可导出");
            }
            transService.transBatch(bizUserList);
            bizUserList = CollectionUtil.sort(bizUserList, Comparator.comparing(BizUser::getOrgId));
            List<BizUserExportResult> bizUserExportResultList = bizUserList.stream()
                    .map(bizUser -> {
                        BizUserExportResult bizUserExportResult = new BizUserExportResult();
                        BeanUtil.copyProperties(bizUser, bizUserExportResult);
                        bizUserExportResult.setGroupName(ObjectUtil.isNotEmpty(bizUserExportResult.getOrgName())?
                                bizUserExportResult.getOrgName():"无组织");
                        // 状态枚举转为文字
                        bizUserExportResult.setUserStatus(bizUserExportResult.getUserStatus()
                                .equalsIgnoreCase(BizUserStatusEnum.ENABLE.getValue())?"正常":"停用");
                        // 将base64转为byte数组
                        if (ObjectUtil.isNotEmpty(bizUser.getAvatar())) {
                            bizUserExportResult.setAvatar(ImgUtil.toBytes(ImgUtil.toImage(StrUtil
                                    .split(bizUser.getAvatar(), StrUtil.COMMA).get(1)), ImgUtil.IMAGE_TYPE_PNG));
                        }
                        return bizUserExportResult;
                    }).collect(Collectors.toList());
            // 创建临时文件
            tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);

            // 头的策略
            WriteCellStyle headWriteCellStyle = new WriteCellStyle();
            WriteFont headWriteFont = new WriteFont();
            headWriteFont.setFontHeightInPoints((short) 14);
            headWriteCellStyle.setWriteFont(headWriteFont);
            // 水平垂直居中
            headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
            headWriteCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            // 内容的策略
            WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
            // 这里需要指定 FillPatternType 为FillPatternType.SOLID_FOREGROUND 不然无法显示背景颜色.头默认了 FillPatternType所以可以不指定
            contentWriteCellStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
            // 内容背景白色
            contentWriteCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
            WriteFont contentWriteFont = new WriteFont();

            // 内容字体大小
            contentWriteFont.setFontHeightInPoints((short) 12);
            contentWriteCellStyle.setWriteFont(contentWriteFont);

            //设置边框样式，细实线
            contentWriteCellStyle.setBorderLeft(BorderStyle.THIN);
            contentWriteCellStyle.setBorderTop(BorderStyle.THIN);
            contentWriteCellStyle.setBorderRight(BorderStyle.THIN);
            contentWriteCellStyle.setBorderBottom(BorderStyle.THIN);

            // 水平垂直居中
            contentWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.LEFT);
            contentWriteCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            // 这个策略是 头是头的样式 内容是内容的样式 其他的策略可以自己实现
            HorizontalCellStyleStrategy horizontalCellStyleStrategy = new HorizontalCellStyleStrategy(headWriteCellStyle,
                    contentWriteCellStyle);

            // 写excel
            EasyExcel.write(tempFile.getPath(), BizUserExportResult.class)
                    // 自定义样式
                    .registerWriteHandler(horizontalCellStyleStrategy)
                    // 自动列宽
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    // 机构分组合并单元格
                    .registerWriteHandler(new CommonExcelCustomMergeStrategy(bizUserExportResultList.stream().map(BizUserExportResult::getGroupName)
                            .collect(Collectors.toList()), 0))
                    // 设置第一行字体
                    .registerWriteHandler(new CellWriteHandler() {
                        @Override
                        public void afterCellDispose(CellWriteHandlerContext context) {
                            WriteCellData<?> cellData = context.getFirstCellData();
                            WriteCellStyle writeCellStyle = cellData.getOrCreateStyle();
                            Integer rowIndex = context.getRowIndex();
                            if(rowIndex == 0) {
                                WriteFont headWriteFont = new WriteFont();
                                headWriteFont.setFontName("宋体");
                                headWriteFont.setBold(true);
                                headWriteFont.setFontHeightInPoints((short) 16);
                                writeCellStyle.setWriteFont(headWriteFont);
                            }
                        }
                    })
                    // 设置表头行高
                    .registerWriteHandler(new AbstractRowHeightStyleStrategy() {
                        @Override
                        protected void setHeadColumnHeight(Row row, int relativeRowIndex) {
                            if(relativeRowIndex == 0) {
                                // 表头第一行
                                row.setHeightInPoints(34);
                            } else {
                                // 表头其他行
                                row.setHeightInPoints(30);
                            }
                        }
                        @Override
                        protected void setContentColumnHeight(Row row, int relativeRowIndex) {
                            // 内容行
                            row.setHeightInPoints(20);
                        }
                    })
                    .sheet("人员信息")
                    .doWrite(bizUserExportResultList);
            CommonDownloadUtil.download(tempFile, response);
        } catch (Exception e) {
            log.error(">>> 人员导出异常：", e);
            CommonResponseUtil.renderError(response, "导出失败");
        } finally {
            FileUtil.del(tempFile);
        }
    }

    @Override
    public void exportUserInfo(BizUserIdParam bizUserIdParam, HttpServletResponse response) throws IOException {
        File destTemplateFile = null;
        File resultFile = null;
        try {
            BizUser bizUser = this.queryEntity(bizUserIdParam.getId());
            transService.transOne(bizUser);
            // 读取模板流
            InputStream inputStream = POICacheManager.getFile("userExportTemplate.docx");
            // 创建一个临时模板
            destTemplateFile = FileUtil.writeFromStream(inputStream, FileUtil.file(FileUtil.getTmpDir() +
                    File.separator + "userExportTemplate.docx"));
            // 构造填充的参数
            Map<String, Object> map = BeanUtil.beanToMap(bizUser);
            String avatarBase64;
            if(ObjectUtil.isNotEmpty(bizUser.getAvatar())) {
                avatarBase64 = bizUser.getAvatar();
            } else {
                avatarBase64 = CommonAvatarUtil.generateImg(bizUser.getName());
            }
            // 头像
            ImageEntity imageEntity = new ImageEntity(ImgUtil.toBytes(ImgUtil.toImage(StrUtil
                    .split(avatarBase64, StrUtil.COMMA).get(1)), ImgUtil.IMAGE_TYPE_PNG), 120, 160);
            map.put("avatar", imageEntity);
            if(ObjectUtil.isNotEmpty(bizUser.getBirthday())) {
                try {
                    // 年龄
                    long age = cn.hutool.core.date.DateUtil.betweenYear(cn.hutool.core.date.DateUtil.parseDate(bizUser.getBirthday()), DateTime.now(), true);
                    if(age != 0) {
                        map.put("age", age + "岁");
                    }
                } catch (Exception ignored) {
                }
            }
            // 导出时间
            map.put("exportDateTime", DateUtil.format(DateTime.now(), DatePattern.CHINESE_DATE_PATTERN));
            // 生成doc
            XWPFDocument doc = WordExportUtil.exportWord07(destTemplateFile.getAbsolutePath(), map);
            // 生成临时导出文件
            resultFile = FileUtil.file(FileUtil.getTmpDir() + File.separator + "SNOWY系统B端人员信息_" + bizUser.getName() + ".docx");
            // 写入
            BufferedOutputStream outputStream = FileUtil.getOutputStream(resultFile);
            doc.write(outputStream);
            outputStream.close();
            // 下载
            CommonDownloadUtil.download(resultFile, response);
        } catch (Exception e) {
            log.error(">>> 导出人员个人信息异常：", e);
            CommonResponseUtil.renderError(response, "导出失败");
        } finally {
            // 删除临时文件
            if(ObjectUtil.isNotEmpty(destTemplateFile)) {
                FileUtil.del(destTemplateFile);
            }
            if(ObjectUtil.isNotEmpty(resultFile)) {
                FileUtil.del(resultFile);
            }
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
        // 定义机构集合
        Set<BizOrg> bizOrgSet = CollectionUtil.newHashSet();
        if(ObjectUtil.isNotEmpty(loginUserDataScope)) {
            // 获取所有机构
            List<BizOrg> allOrgList = bizOrgService.list();
            loginUserDataScope.forEach(orgId -> bizOrgSet.addAll(bizOrgService.getParentListById(allOrgList, orgId, true)));
            List<String> loginUserDataScopeFullList = bizOrgSet.stream().map(BizOrg::getId).collect(Collectors.toList());
            lambdaQueryWrapper.in(BizOrg::getId, loginUserDataScopeFullList);
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
    public Page<BizOrg> orgListSelector(BizUserSelectorOrgListParam bizUserSelectorOrgListParam) {
        LambdaQueryWrapper<BizOrg> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 校验数据范围
        List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
        if(ObjectUtil.isNotEmpty(loginUserDataScope)) {
            lambdaQueryWrapper.in(BizOrg::getId, loginUserDataScope);
        } else {
            return new Page<>();
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
        return bizOrgService.page(CommonPageRequest.defaultPage(), lambdaQueryWrapper);
    }

    @Override
    public Page<BizPosition> positionSelector(BizUserSelectorPositionParam bizUserSelectorPositionParam) {
        LambdaQueryWrapper<BizPosition> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 校验数据范围
        List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
        if(ObjectUtil.isNotEmpty(loginUserDataScope)) {
            lambdaQueryWrapper.in(BizPosition::getOrgId, loginUserDataScope);
        } else {
            return new Page<>();
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
        return bizPositionService.page(CommonPageRequest.defaultPage(), lambdaQueryWrapper);
    }

    @SuppressWarnings("ALL")
    @Override
    public Page<BizUserRoleResult> roleSelector(BizUserSelectorRoleParam bizUserSelectorRoleParam) {
        // 校验数据范围
        List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
        if(ObjectUtil.isNotEmpty(loginUserDataScope)) {
            if(ObjectUtil.isNotEmpty(bizUserSelectorRoleParam.getOrgId())) {
                if(loginUserDataScope.contains(bizUserSelectorRoleParam.getOrgId())) {
                    return BeanUtil.toBean(sysRoleApi.roleSelector(bizUserSelectorRoleParam.getOrgId(), bizUserSelectorRoleParam.getCategory(),
                            bizUserSelectorRoleParam.getSearchKey(), loginUserDataScope, true), Page.class);
                } else {
                    return new Page<>();
                }
            } else {
                if (ObjectUtil.isNotEmpty(bizUserSelectorRoleParam.getCategory()) & BizRoleCategoryEnum.GLOBAL.getValue().equals(bizUserSelectorRoleParam.getCategory())) {
                    // 查询系统级别的
                    return BeanUtil.toBean(sysRoleApi.roleSelector(null, bizUserSelectorRoleParam.getCategory(),
                            bizUserSelectorRoleParam.getSearchKey(), null, true), Page.class);
                } else {
                    return BeanUtil.toBean(sysRoleApi.roleSelector(null, bizUserSelectorRoleParam.getCategory(),
                            bizUserSelectorRoleParam.getSearchKey(), loginUserDataScope, true), Page.class);
                }
            }
        } else {
            return new Page<>();
        }
    }

    @Override
    public Page<BizUser> userSelector(BizUserSelectorUserParam bizUserSelectorUserParam) {
        LambdaQueryWrapper<BizUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 校验数据范围
        List<String> loginUserDataScope = StpLoginUserUtil.getLoginUserDataScope();
        if(ObjectUtil.isNotEmpty(loginUserDataScope)) {
            lambdaQueryWrapper.in(BizUser::getOrgId, loginUserDataScope);
        } else {
            return new Page<>();
        }
        // 只查询部分字段
        lambdaQueryWrapper.select(BizUser::getId, BizUser::getAvatar, BizUser::getOrgId, BizUser::getPositionId, BizUser::getAccount,
                BizUser::getName, BizUser::getSortCode, BizUser::getGender, BizUser::getEntryDate);
        if (ObjectUtil.isNotEmpty(bizUserSelectorUserParam.getOrgId())) {
            // 如果机构id不为空，则查询该机构及其子机构下的所有人
            List<String> childOrgIdList = CollStreamUtil.toList(bizOrgService.getChildListById(bizOrgService
                    .getAllOrgList(), bizUserSelectorUserParam.getOrgId(), true), BizOrg::getId);
            if (ObjectUtil.isNotEmpty(childOrgIdList)) {
                lambdaQueryWrapper.in(BizUser::getOrgId, childOrgIdList);
            } else {
                return new Page<>();
            }
        }
        if(ObjectUtil.isNotEmpty(bizUserSelectorUserParam.getSearchKey())) {
            lambdaQueryWrapper.like(BizUser::getName, bizUserSelectorUserParam.getSearchKey());
        }
        lambdaQueryWrapper.orderByAsc(BizUser::getSortCode);
        return this.page(CommonPageRequest.defaultPage(), lambdaQueryWrapper);
    }
}
