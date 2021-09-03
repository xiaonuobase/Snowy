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
package vip.xiaonuo.sys.modular.user.service;

import cn.hutool.core.lang.Dict;
import com.baomidou.mybatisplus.extension.service.IService;
import me.zhyd.oauth.model.AuthUser;
import vip.xiaonuo.core.pojo.page.PageResult;
import vip.xiaonuo.sys.modular.user.entity.SysUser;
import vip.xiaonuo.sys.modular.user.param.SysUserParam;
import vip.xiaonuo.sys.modular.user.result.SysUserResult;

import java.util.List;
import java.util.Set;

/**
 * 系统用户service接口
 *
 * @author xuyuxiang
 * @date 2020/3/11 17:49
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 根据账号获取用户
     *
     * @param account 账号
     * @return 用户
     * @author xuyuxiang
     * @date 2020/3/11 17:51
     */
    SysUser getUserByCount(String account);

    /**
     * 查询系统用户
     *
     * @param sysUserParam 查询参数
     * @return 查询分页结果
     * @author xuyuxiang
     * @date 2020/3/23 9:23
     */
    PageResult<SysUserResult> page(SysUserParam sysUserParam);

    /**
     * 根据用户账号模糊搜索系统用户列表
     *
     * @param sysUserParam 查询参数
     * @return 增强版hashMap 格式：[{"id:":123, "firstName":"张三"}]
     * @author xuyuxiang
     * @date 2020/4/14 9:21
     */
    List<Dict> list(SysUserParam sysUserParam);

    /**
     * 增加系统用户
     *
     * @param sysUserParam 添加参数
     * @author xuyuxiang
     * @date 2020/3/23 9:26
     */
    void add(SysUserParam sysUserParam);

    /**
     * 删除系统用户
     *
     * @param sysUserParamList 删除集合
     * @author xuyuxiang yubaosahn
     * @date 2020/3/23 9:26
     */
    void delete(List<SysUserParam> sysUserParamList);

    /**
     * 编辑系统用户
     *
     * @param sysUserParam 编辑参数
     * @author xuyuxiang
     * @date 2020/3/23 9:26
     */
    void edit(SysUserParam sysUserParam);

    /**
     * 查看系统用户
     *
     * @param sysUserParam 查看参数
     * @return 用户结果集
     * @author xuyuxiang
     * @date 2020/3/26 9:52
     */
    SysUserResult detail(SysUserParam sysUserParam);

    /**
     * 修改状态
     *
     * @param sysUserParam 修改参数
     * @author xuyuxiang
     * @date 2020/5/25 14:34
     */
    void changeStatus(SysUserParam sysUserParam);

    /**
     * 授权角色
     *
     * @param sysUserParam 授权参数
     * @author xuyuxiang
     * @date 2020/3/28 16:54
     */
    void grantRole(SysUserParam sysUserParam);

    /**
     * 授权数据
     *
     * @param sysUserParam 授权参数
     * @author xuyuxiang
     * @date 2020/3/28 16:54
     */
    void grantData(SysUserParam sysUserParam);

    /**
     * 更新信息
     *
     * @param sysUserParam 更新参数
     * @author xuyuxiang
     * @date 2020/4/1 14:43
     */
    void updateInfo(SysUserParam sysUserParam);

    /**
     * 修改密码
     *
     * @param sysUserParam 修改密码参数
     * @author xuyuxiang
     * @date 2020/4/1 14:44
     */
    void updatePwd(SysUserParam sysUserParam);

    /**
     * 获取用户的数据范围（组织机构id集合）
     *
     * @param userId 用户id
     * @param orgId  组织机构id
     * @return 数据范围id集合（组织机构id集合）
     * @author xuyuxiang
     * @date 2020/4/5 17:23
     */
    List<Long> getUserDataScopeIdList(Long userId, Long orgId);

    /**
     * 根据用户id获取姓名
     *
     * @param userId 用户id
     * @return 用户姓名
     * @author xuyuxiang
     * @date 2020/5/6 15:02
     */
    String getNameByUserId(Long userId);

    /**
     * 拥有角色
     *
     * @param sysUserParam 查询参数
     * @return 角色id集合
     * @author xuyuxiang
     * @date 2020/5/29 14:10
     */
    List<Long> ownRole(SysUserParam sysUserParam);

    /**
     * 拥有数据
     *
     * @param sysUserParam 查询参数
     * @return 数据范围id集合
     * @author xuyuxiang
     * @date 2020/5/29 14:10
     */
    List<Long> ownData(SysUserParam sysUserParam);

    /**
     * 重置密码
     *
     * @param sysUserParam 重置参数
     * @author xuyuxiang
     * @date 2020/5/29 14:57
     */
    void resetPwd(SysUserParam sysUserParam);

    /**
     * 修改头像
     *
     * @param sysUserParam 修改头像参数
     * @author xuyuxiang
     * @date 2020/6/28 15:21
     */
    void updateAvatar(SysUserParam sysUserParam);

    /**
     * 导出用户
     *
     * @param sysUserParam 导出参数
     * @author xuyuxiang
     * @date 2020/6/30 16:08
     */
    void export(SysUserParam sysUserParam);

    /**
     * 用户选择器
     *
     * @param sysUserParam 查询参数
     * @return 用户列表集合，格式[{"id":123,"name":"张三"},{"id":456,"name":"李四"}]
     * @author xuyuxiang
     * @date 2020/7/3 13:17
     */
    List<Dict> selector(SysUserParam sysUserParam);

    /**
     * 根据用户id获取用户
     *
     * @param userId 用户id
     * @return 用户实体
     * @author xuyuxiang
     * @date 2020/7/29 10:07
     **/
    SysUser getUserById(Long userId);

    /**
     * 将授权的用户信息保存到用户表
     *
     * @param authUser 授权的用户信息
     * @param sysUser  用户表信息
     * @return void
     * @author xuyuxiang
     * @date 2020/7/29 10:26
     **/
    void saveAuthUserToUser(AuthUser authUser, SysUser sysUser);

    /**
     * 获取用户id集合
     *
     * @return 用户id集合
     * @author xuyuxiang
     * @date 2020/9/11 17:54
     **/
    List<Long> getAllUserIdList();

    /**
     * 判断集合内用户是否均已删除
     *
     * @author xuyuxiang
     * @date 2021/9/3 13:26
     * @param userIdSet 用户id集合
     * @return boolean
     **/
    boolean hasAllDeletedUser(Set<Long> userIdSet);
}
