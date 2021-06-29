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
package vip.xiaonuo.sys.modular.notice.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vip.xiaonuo.core.context.login.LoginContextHolder;
import vip.xiaonuo.core.exception.ServiceException;
import vip.xiaonuo.core.factory.PageFactory;
import vip.xiaonuo.core.pojo.login.SysLoginUser;
import vip.xiaonuo.core.pojo.page.PageResult;
import vip.xiaonuo.sys.core.enums.NoticeStatusEnum;
import vip.xiaonuo.sys.core.enums.NoticeUserStatusEnum;
import vip.xiaonuo.sys.modular.notice.entity.SysNotice;
import vip.xiaonuo.sys.modular.notice.entity.SysNoticeUser;
import vip.xiaonuo.sys.modular.notice.enums.SysNoticeExceptionEnum;
import vip.xiaonuo.sys.modular.notice.mapper.SysNoticeMapper;
import vip.xiaonuo.sys.modular.notice.param.SysNoticeParam;
import vip.xiaonuo.sys.modular.notice.result.SysNoticeDetailResult;
import vip.xiaonuo.sys.modular.notice.result.SysNoticeReceiveResult;
import vip.xiaonuo.sys.modular.notice.service.SysNoticeService;
import vip.xiaonuo.sys.modular.notice.service.SysNoticeUserService;
import vip.xiaonuo.sys.modular.user.service.SysUserService;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 系统通知公告service接口实现类
 *
 * @author xuyuxiang
 * @date 2020/6/28 17:20
 */
@Service
public class SysNoticeServiceImpl extends ServiceImpl<SysNoticeMapper, SysNotice> implements SysNoticeService {

    @Resource
    private SysNoticeUserService sysNoticeUserService;

    @Resource
    private SysUserService sysUserService;

    @Override
    public PageResult<SysNotice> page(SysNoticeParam sysNoticeParam) {
        LambdaQueryWrapper<SysNotice> queryWrapper = new LambdaQueryWrapper<>();
        if (ObjectUtil.isNotNull(sysNoticeParam)) {
            //根据标题或内容模糊查询
            if (ObjectUtil.isNotEmpty(sysNoticeParam.getSearchValue())) {
                queryWrapper.and(q -> q.like(SysNotice::getTitle, sysNoticeParam.getSearchValue())
                        .or().like(SysNotice::getContent, sysNoticeParam.getSearchValue()));
            }
            //根据类型查询
            if (ObjectUtil.isNotEmpty(sysNoticeParam.getType())) {
                queryWrapper.eq(SysNotice::getType, sysNoticeParam.getType());
            }
        }
        //查询未删除的
        queryWrapper.ne(SysNotice::getStatus, NoticeStatusEnum.DELETED.getCode());
        return new PageResult<>(this.page(PageFactory.defaultPage(), queryWrapper));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(SysNoticeParam sysNoticeParam) {
        //校验参数，检查状态是否正确
        checkParam(sysNoticeParam, true);
        SysNotice sysNotice = new SysNotice();
        BeanUtil.copyProperties(sysNoticeParam, sysNotice);
        SysLoginUser sysLoginUser = LoginContextHolder.me().getSysLoginUser();
        sysNotice.setPublicUserId(sysLoginUser.getId());
        sysNotice.setPublicUserName(sysLoginUser.getName());
        sysNotice.setPublicOrgId(sysLoginUser.getLoginEmpInfo().getOrgId());
        sysNotice.setPublicOrgName(sysLoginUser.getLoginEmpInfo().getOrgName());
        //如果是发布，则设置发布时间
        if (NoticeStatusEnum.PUBLIC.getCode().equals(sysNotice.getStatus())) {
            sysNotice.setPublicTime(new Date());
        }
        this.save(sysNotice);
        //通知到的人
        List<Long> noticeUserIdList = sysNoticeParam.getNoticeUserIdList();
        Integer noticeUserStatus = NoticeUserStatusEnum.UNREAD.getCode();
        sysNoticeUserService.add(sysNotice.getId(), noticeUserIdList, noticeUserStatus);
    }

    @Override
    public void delete(SysNoticeParam sysNoticeParam) {
        SysNotice sysNotice = this.querySysNotice(sysNoticeParam);
        Integer status = sysNotice.getStatus();
        if (!NoticeStatusEnum.DRAFT.getCode().equals(status) && !NoticeStatusEnum.CANCEL.getCode().equals(status)) {
            throw new ServiceException(SysNoticeExceptionEnum.NOTICE_CANNOT_DELETE);
        }
        sysNotice.setStatus(NoticeStatusEnum.DELETED.getCode());
        this.updateById(sysNotice);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(SysNoticeParam sysNoticeParam) {
        SysNotice sysNotice = this.querySysNotice(sysNoticeParam);
        //校验参数，检查状态是否正确
        checkParam(sysNoticeParam, true);
        //非草稿状态
        Integer status = sysNotice.getStatus();
        if (!NoticeStatusEnum.DRAFT.getCode().equals(status)) {
            throw new ServiceException(SysNoticeExceptionEnum.NOTICE_CANNOT_EDIT);
        }
        BeanUtil.copyProperties(sysNoticeParam, sysNotice);
        //如果是发布，则设置发布时间
        if (NoticeStatusEnum.PUBLIC.getCode().equals(sysNotice.getStatus())) {
            sysNotice.setPublicTime(new Date());
        }
        this.updateById(sysNotice);
        //通知到的人
        List<Long> noticeUserIdList = sysNoticeParam.getNoticeUserIdList();
        Integer noticeUserStatus = NoticeUserStatusEnum.UNREAD.getCode();
        sysNoticeUserService.edit(sysNotice.getId(), noticeUserIdList, noticeUserStatus);
    }

    @Override
    public SysNoticeDetailResult detail(SysNoticeParam sysNoticeParam) {
        SysNotice sysNotice = this.querySysNotice(sysNoticeParam);
        Long id = sysNotice.getId();
        //获取通知到的用户
        List<SysNoticeUser> sysNoticeUserList = sysNoticeUserService.getSysNoticeUserListByNoticeId(id);
        List<Long> noticeUserIdList = CollectionUtil.newArrayList();
        List<Dict> noticeUserReadInfoList = CollectionUtil.newArrayList();
        SysNoticeDetailResult sysNoticeResult = new SysNoticeDetailResult();
        BeanUtil.copyProperties(sysNotice, sysNoticeResult);
        if (ObjectUtil.isNotEmpty(sysNoticeUserList)) {
            sysNoticeUserList.forEach(sysNoticeUser -> {
                //遍历通知到的用户，并构造
                noticeUserIdList.add(sysNoticeUser.getUserId());
                Dict dict = Dict.create();
                dict.put("userId", sysNoticeUser.getUserId());
                dict.put("userName", sysUserService.getNameByUserId(sysNoticeUser.getUserId()));
                dict.put("readStatus", sysNoticeUser.getStatus());
                dict.put("readTime", sysNoticeUser.getReadTime());
                noticeUserReadInfoList.add(dict);
            });
        }
        sysNoticeResult.setNoticeUserIdList(noticeUserIdList);
        sysNoticeResult.setNoticeUserReadInfoList(noticeUserReadInfoList);
        //如果该条通知公告为已发布，则将当前用户的该条通知公告设置为已读
        if (sysNotice.getStatus().equals(NoticeStatusEnum.PUBLIC.getCode())) {
            sysNoticeUserService.read(sysNotice.getId(),
                    LoginContextHolder.me().getSysLoginUserId(), NoticeUserStatusEnum.READ.getCode());
        }
        return sysNoticeResult;
    }

    @Override
    public void changeStatus(SysNoticeParam sysNoticeParam) {
        SysNotice sysNotice = this.querySysNotice(sysNoticeParam);
        //校验参数，检查状态是否正确
        checkParam(sysNoticeParam, false);
        sysNotice.setStatus(sysNoticeParam.getStatus());
        //如果是撤回，则设置撤回时间
        if (NoticeStatusEnum.CANCEL.getCode().equals(sysNotice.getStatus())) {
            sysNotice.setCancelTime(new Date());
        } else if (NoticeStatusEnum.PUBLIC.getCode().equals(sysNotice.getStatus())) {
            //如果是发布，则设置发布时间
            sysNotice.setPublicTime(new Date());
        }
        this.updateById(sysNotice);
    }

    @Override
    public PageResult<SysNoticeReceiveResult> received(SysNoticeParam sysNoticeParam) {
        QueryWrapper<SysNoticeReceiveResult> queryWrapper = new QueryWrapper<>();
        //查询当前用户的
        queryWrapper.eq("sys_notice_user.user_id", LoginContextHolder.me().getSysLoginUserId());
        if (ObjectUtil.isNotNull(sysNoticeParam)) {
            //根据标题或内容模糊查询
            if (ObjectUtil.isNotEmpty(sysNoticeParam.getSearchValue())) {
                queryWrapper.and(q -> q.like("sys_notice.title", sysNoticeParam.getSearchValue())
                        .or().like("sys_notice.content", sysNoticeParam.getSearchValue()));
            }
            //根据类型查询
            if (ObjectUtil.isNotEmpty(sysNoticeParam.getType())) {
                queryWrapper.eq("sys_notice.type", sysNoticeParam.getType());
            }
        }
        //查询未删除的
        queryWrapper.ne("sys_notice.status", NoticeStatusEnum.DELETED.getCode());
        return new PageResult<>(this.baseMapper.page(PageFactory.defaultPage(), queryWrapper));
    }

    /**
     * 校验参数，判断状态是否正确
     *
     * @author xuyuxiang
     * @date 2020/6/29 10:06
     */
    private void checkParam(SysNoticeParam sysNoticeParam, boolean isAddOrEdit) {
        //保存或编辑时，传递的状态参数应为草稿，或发布
        Integer status = sysNoticeParam.getStatus();
        if (isAddOrEdit) {
            if (!NoticeStatusEnum.DRAFT.getCode().equals(status) &&
                    !NoticeStatusEnum.PUBLIC.getCode().equals(status)) {
                throw new ServiceException(SysNoticeExceptionEnum.NOTICE_STATUS_ERROR);
            }
        } else {
            //修改状态时，传递的状态参数应为撤回或删除或发布
            if (!NoticeStatusEnum.CANCEL.getCode().equals(status) &&
                    !NoticeStatusEnum.DELETED.getCode().equals(status) &&
                    !NoticeStatusEnum.PUBLIC.getCode().equals(status)) {
                throw new ServiceException(SysNoticeExceptionEnum.NOTICE_STATUS_ERROR);
            }
        }

    }

    /**
     * 获取系统通知公告
     *
     * @author xuyuxiang
     * @date 2020/6/29 9:58
     */
    private SysNotice querySysNotice(SysNoticeParam sysNoticeParam) {
        SysNotice sysNotice = this.getById(sysNoticeParam.getId());
        if (ObjectUtil.isNull(sysNotice)) {
            throw new ServiceException(SysNoticeExceptionEnum.NOTICE_NOT_EXIST);
        }
        return sysNotice;
    }

}
