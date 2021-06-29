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
package vip.xiaonuo.sys.modular.app.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vip.xiaonuo.core.consts.CommonConstant;
import vip.xiaonuo.core.enums.CommonStatusEnum;
import vip.xiaonuo.core.enums.YesOrNotEnum;
import vip.xiaonuo.core.exception.ServiceException;
import vip.xiaonuo.core.factory.PageFactory;
import vip.xiaonuo.core.pojo.page.PageResult;
import vip.xiaonuo.sys.core.enums.AdminTypeEnum;
import vip.xiaonuo.sys.modular.app.entity.SysApp;
import vip.xiaonuo.sys.modular.app.enums.SysAppExceptionEnum;
import vip.xiaonuo.sys.modular.app.mapper.SysAppMapper;
import vip.xiaonuo.sys.modular.app.param.SysAppParam;
import vip.xiaonuo.sys.modular.app.service.SysAppService;
import vip.xiaonuo.sys.modular.menu.service.SysMenuService;
import vip.xiaonuo.sys.modular.user.entity.SysUser;
import vip.xiaonuo.sys.modular.user.service.SysUserService;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 系统应用service接口实现类
 *
 * @author xuyuxiang
 * @date 2020/3/13 16:15
 */
@Service
public class SysAppServiceImpl extends ServiceImpl<SysAppMapper, SysApp> implements SysAppService {

    @Resource
    private SysUserService sysUserService;

    @Resource
    private SysMenuService sysMenuService;

    @Override
    public List<Dict> getLoginApps(Long userId, List<Long> roleIdList) {
        List<Dict> userAppDictList = CollectionUtil.newArrayList();

        LambdaQueryWrapper<SysApp> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysApp::getStatus, CommonStatusEnum.ENABLE.getCode());


        SysUser sysUser = sysUserService.getById(userId);
        Integer adminType = sysUser.getAdminType();

        //如果不是超级管理员则有自己的菜单对应的应用编码
        if (!AdminTypeEnum.SUPER_ADMIN.getCode().equals(adminType)) {
            //获取用户菜单对应的应用编码集合
            List<String> appCodeList = sysMenuService.getUserMenuAppCodeList(userId, roleIdList);
            //当应用编码不为空时，则限制查询范围
            if (ObjectUtil.isNotEmpty(appCodeList)) {
                queryWrapper.in(SysApp::getCode, appCodeList);
            } else {
                //没查到应用编码则直接返回
                return userAppDictList;
            }
        }
        //定义是否有默认激活的应用标志
        AtomicBoolean hasDefaultActive = new AtomicBoolean(false);
        //遍历
        this.list(queryWrapper).forEach(sysApp -> {
            Dict dict = Dict.create();
            dict.put(CommonConstant.CODE, sysApp.getCode());
            dict.put(CommonConstant.NAME, sysApp.getName());
            //如果有默认激活的
            if (YesOrNotEnum.Y.getCode().equals(sysApp.getActive())) {
                hasDefaultActive.set(true);
                dict.put("active", true);
                //将其放在第一个
                userAppDictList.add(0, dict);
            } else {
                dict.put("active", false);
                userAppDictList.add(dict);
            }

        });
        if (ObjectUtil.isNotEmpty(userAppDictList)) {
            //如果默认激活的系统没有，则第一个为默认激活的系统
            if (!hasDefaultActive.get()) {
                Dict dict = userAppDictList.get(0);
                dict.put("active", true);
            }
        }
        return userAppDictList;
    }

    @Override
    public PageResult<SysApp> page(SysAppParam sysAppParam) {
        LambdaQueryWrapper<SysApp> queryWrapper = new LambdaQueryWrapper<>();
        if (ObjectUtil.isNotNull(sysAppParam)) {
            //根据名称模糊查询
            if (ObjectUtil.isNotEmpty(sysAppParam.getName())) {
                queryWrapper.like(SysApp::getName, sysAppParam.getName());
            }
            //根据编码模糊查询
            if (ObjectUtil.isNotEmpty(sysAppParam.getCode())) {
                queryWrapper.like(SysApp::getCode, sysAppParam.getCode());
            }
        }
        queryWrapper.eq(SysApp::getStatus, CommonStatusEnum.ENABLE.getCode());
        return new PageResult<>(this.page(PageFactory.defaultPage(), queryWrapper));
    }

    @Override
    public void add(SysAppParam sysAppParam) {
        //校验参数，检查是否存在相同的名称和编码，以及默认激活的系统的数量是否合理
        checkParam(sysAppParam, false);
        SysApp sysApp = new SysApp();
        BeanUtil.copyProperties(sysAppParam, sysApp);
        sysApp.setStatus(CommonStatusEnum.ENABLE.getCode());
        this.save(sysApp);
    }

    @Override
    public void delete(SysAppParam sysAppParam) {
        SysApp sysApp = this.querySysApp(sysAppParam);
        String code = sysApp.getCode();
        //该应用下是否有状态为正常的菜单
        boolean hasMenu = sysMenuService.hasMenu(code);
        //只要有，则不能删
        if (hasMenu) {
            throw new ServiceException(SysAppExceptionEnum.APP_CANNOT_DELETE);
        }
        sysApp.setStatus(CommonStatusEnum.DELETED.getCode());
        this.updateById(sysApp);
    }

    @Override
    public void edit(SysAppParam sysAppParam) {
        SysApp sysApp = this.querySysApp(sysAppParam);
        //校验参数，检查是否存在相同的名称和编码，以及默认激活的系统的数量是否合理
        checkParam(sysAppParam, true);
        BeanUtil.copyProperties(sysAppParam, sysApp);
        //不能修改状态，用修改状态接口修改状态
        sysApp.setStatus(null);
        this.updateById(sysApp);
    }

    @Override
    public SysApp detail(SysAppParam sysAppParam) {
        return this.querySysApp(sysAppParam);
    }

    @Override
    public List<SysApp> list(SysAppParam sysAppParam) {
        LambdaQueryWrapper<SysApp> appQueryWrapper = new LambdaQueryWrapper<>();
        appQueryWrapper.eq(SysApp::getStatus, CommonStatusEnum.ENABLE.getCode());
        return this.list(appQueryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void setAsDefault(SysAppParam sysAppParam) {
        SysApp currentApp = this.querySysApp(sysAppParam);
        //所有已激活的改为未激活
        LambdaQueryWrapper<SysApp> appQueryWrapper = new LambdaQueryWrapper<>();
        appQueryWrapper.eq(SysApp::getStatus, CommonStatusEnum.ENABLE.getCode())
                .eq(SysApp::getActive, YesOrNotEnum.Y.getCode());
        this.list(appQueryWrapper).forEach(sysApp -> {
            sysApp.setActive(YesOrNotEnum.N.getCode());
            this.updateById(sysApp);
        });
        //当前的设置为已激活
        currentApp.setActive(YesOrNotEnum.Y.getCode());
        this.updateById(currentApp);
    }

    /**
     * 校验参数，检查是否存在相同的名称和编码，以及默认激活的系统的数量是否合理
     *
     * @author xuyuxiang
     * @date 2020/3/25 21:23
     */
    private void checkParam(SysAppParam sysAppParam, boolean isExcludeSelf) {
        Long id = sysAppParam.getId();
        String name = sysAppParam.getName();
        String code = sysAppParam.getCode();
        String active = sysAppParam.getActive();

        // 查询名称有无重复
        LambdaQueryWrapper<SysApp> appQueryWrapperByName = new LambdaQueryWrapper<>();
        appQueryWrapperByName.eq(SysApp::getName, name)
                .ne(SysApp::getStatus, CommonStatusEnum.DELETED.getCode());

        // 查询编码有无重复
        LambdaQueryWrapper<SysApp> appQueryWrapperByCode = new LambdaQueryWrapper<>();
        appQueryWrapperByCode.eq(SysApp::getCode, code)
                .ne(SysApp::getStatus, CommonStatusEnum.DELETED.getCode());

        // 查询激活状态有无已经有Y的，也就是激活的
        LambdaQueryWrapper<SysApp> appQueryWrapperByActive = new LambdaQueryWrapper<>();
        appQueryWrapperByActive.eq(SysApp::getActive, active)
                .ne(SysApp::getStatus, CommonStatusEnum.DELETED.getCode());

        if (isExcludeSelf) {
            appQueryWrapperByName.ne(SysApp::getId, id);
            appQueryWrapperByCode.ne(SysApp::getId, id);
            appQueryWrapperByActive.ne(SysApp::getId, id);
        }
        int countByName = this.count(appQueryWrapperByName);
        int countByCode = this.count(appQueryWrapperByCode);
        int countByActive = this.count(appQueryWrapperByActive);

        if (countByName >= 1) {
            throw new ServiceException(SysAppExceptionEnum.APP_NAME_REPEAT);
        }
        if (countByCode >= 1) {
            throw new ServiceException(SysAppExceptionEnum.APP_CODE_REPEAT);
        }

        // 只判断激活状态为Y时候数量是否大于1了
        if (countByActive >= 1 && YesOrNotEnum.Y.getCode().equals(sysAppParam.getActive())) {
            throw new ServiceException(SysAppExceptionEnum.APP_ACTIVE_REPEAT);
        }
    }

    /**
     * 获取系统应用
     *
     * @author xuyuxiang
     * @date 2020/3/26 9:56
     */
    private SysApp querySysApp(SysAppParam sysAppParam) {
        SysApp sysApp = this.getById(sysAppParam.getId());
        if (ObjectUtil.isNull(sysApp)) {
            throw new ServiceException(SysAppExceptionEnum.APP_NOT_EXIST);
        }
        return sysApp;
    }
}
