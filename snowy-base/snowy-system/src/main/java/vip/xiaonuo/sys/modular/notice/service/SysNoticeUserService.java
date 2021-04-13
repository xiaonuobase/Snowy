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
package vip.xiaonuo.sys.modular.notice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import vip.xiaonuo.sys.modular.notice.entity.SysNoticeUser;

import java.util.List;

/**
 * 系统通知公告用户service接口
 *
 * @author xuyuxiang
 * @date 2020/6/29 10:51
 */
public interface SysNoticeUserService extends IService<SysNoticeUser> {

    /**
     * 添加
     *
     * @param noticeId         通知公告id
     * @param noticeUserIdList 通知到的用户id集合
     * @param noticeUserStatus 阅读状态
     * @author xuyuxiang
     * @date 2020/6/29 11:06
     */
    void add(Long noticeId, List<Long> noticeUserIdList, Integer noticeUserStatus);

    /**
     * 编辑
     *
     * @param noticeId         通知公告id
     * @param noticeUserIdList 通知到的用户id集合
     * @param noticeUserStatus 阅读状态
     * @author xuyuxiang
     * @date 2020/6/29 11:40
     */
    void edit(Long noticeId, List<Long> noticeUserIdList, Integer noticeUserStatus);

    /**
     * 根据通知公告id查询通知人员信息集合
     *
     * @param noticeId 通知公告id
     * @return 通知用户列表
     * @author xuyuxiang
     * @date 2020/6/29 11:50
     */
    List<SysNoticeUser> getSysNoticeUserListByNoticeId(Long noticeId);

    /**
     * 设为已读
     *
     * @param noticeId 通知公告id
     * @param userId   用户id
     * @param status   阅读状态
     * @author xuyuxiang
     * @date 2020/6/29 12:05
     */
    void read(Long noticeId, Long userId, Integer status);
}
