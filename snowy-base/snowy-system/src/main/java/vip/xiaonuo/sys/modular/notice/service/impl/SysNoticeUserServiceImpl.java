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

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vip.xiaonuo.sys.modular.notice.entity.SysNoticeUser;
import vip.xiaonuo.sys.modular.notice.mapper.SysNoticeUserMapper;
import vip.xiaonuo.sys.modular.notice.service.SysNoticeUserService;

import java.util.Date;
import java.util.List;

/**
 * 系统通知公告用户service接口实现类
 *
 * @author xuyuxiang
 * @date 2020/6/29 10:53
 */
@Service
public class SysNoticeUserServiceImpl extends ServiceImpl<SysNoticeUserMapper, SysNoticeUser> implements SysNoticeUserService {

    @Override
    public void add(Long noticeId, List<Long> noticeUserIdList, Integer noticeUserStatus) {
        noticeUserIdList.forEach(userId -> {
            SysNoticeUser sysNoticeUser = new SysNoticeUser();
            sysNoticeUser.setNoticeId(noticeId);
            sysNoticeUser.setUserId(userId);
            sysNoticeUser.setStatus(noticeUserStatus);
            this.save(sysNoticeUser);
        });
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(Long noticeId, List<Long> noticeUserIdList, Integer noticeUserStatus) {
        LambdaQueryWrapper<SysNoticeUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysNoticeUser::getNoticeId, noticeId);
        //先删除
        this.remove(queryWrapper);
        //再增加
        this.add(noticeId, noticeUserIdList, noticeUserStatus);
    }

    @Override
    public List<SysNoticeUser> getSysNoticeUserListByNoticeId(Long noticeId) {
        LambdaQueryWrapper<SysNoticeUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysNoticeUser::getNoticeId, noticeId);
        return this.list(queryWrapper);
    }

    @Override
    public void read(Long noticeId, Long userId, Integer status) {
        LambdaQueryWrapper<SysNoticeUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysNoticeUser::getNoticeId, noticeId)
                .eq(SysNoticeUser::getUserId, userId);
        SysNoticeUser sysNoticeUser = this.getOne(queryWrapper);
        if (ObjectUtil.isNotNull(sysNoticeUser)) {
            sysNoticeUser.setStatus(status);
            sysNoticeUser.setReadTime(new Date());
            this.updateById(sysNoticeUser);
        }
    }
}
