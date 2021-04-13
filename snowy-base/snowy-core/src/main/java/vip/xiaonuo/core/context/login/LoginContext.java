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
package vip.xiaonuo.core.context.login;

import vip.xiaonuo.core.pojo.login.SysLoginUser;

import java.util.List;

/**
 * 登录用户上下文
 *
 * @author xuyuxiang
 * @date 2020/3/13 12:16
 */
public interface LoginContext {

    /**
     * 获取当前登录用户
     *
     * @return 当前登录用户信息
     * @author xuyuxiang
     * @date 2020/3/13 14:40
     */
    SysLoginUser getSysLoginUser();

    /**
     * 获取当前登录用户，如未登录，则返回null，不抛异常
     *
     * @return 当前登录用户信息
     * @author xuyuxiang
     * @date 2020/3/13 14:40
     */
    SysLoginUser getSysLoginUserWithoutException();

    /**
     * 获取当前登录用户的id
     *
     * @return 当前登录用户的id
     * @author xuyuxiang
     * @date 2020/3/18 19:25
     */
    Long getSysLoginUserId();

    /**
     * 判断用户是否登录
     *
     * @return 是否登录，true是，false否
     * @author xuyuxiang
     * @date 2020/3/18 19:22
     */
    boolean hasLogin();

    /**
     * 获取当前登录用户的账户
     *
     * @return 当前登陆用户的账户account
     * @author xuyuxiang
     * @date 2020/3/19 20:37
     */
    String getSysLoginUserAccount();

    /**
     * 判断当前登录用户是否有某资源的访问权限
     *
     * @param requestUri 请求的url
     * @return 是否有访问权限，true是，false否
     * @author xuyuxiang
     * @date 2020/3/23 8:48
     */
    boolean hasPermission(String requestUri);

    /**
     * 判断当前登录用户是否包含某个角色
     *
     * @param roleCode 角色编码
     * @return 是否包含该角色，true是，false否
     * @author xuyuxiang
     * @date 2020/3/23 8:53
     */
    boolean hasRole(String roleCode);

    /**
     * 判断当前登录用户是否包含任意一个角色
     *
     * @param roleCodes 角色集合，逗号拼接
     * @return 是否包含任一角色，true是，false否
     * @author xuyuxiang
     * @date 2020/3/23 8:54
     */
    boolean hasAnyRole(String roleCodes);

    /**
     * 判断当前登录用户是否是超级管理员
     *
     * @return 当前登录用户是否是超级管理员
     * @author xuyuxiang
     * @date 2020/3/23 17:50
     */
    boolean isSuperAdmin();

    /**
     * 判断当前登录用户是否包含所有角色
     *
     * @param roleCodes 角色集合，逗号拼接
     * @return 是否包含所有角色，true是，false否
     * @author xuyuxiang
     * @date 2020/4/5 10:28
     */
    boolean hasAllRole(String roleCodes);

    /**
     * 获取当前登录用户的数据范围集合（组织机构id集合）
     *
     * @return 数据范围集合（组织机构id集合）
     * @author xuyuxiang
     * @date 2020/4/5 17:20
     */
    List<Long> getLoginUserDataScopeIdList();

    /**
     * 获取当前登录用户的组织机构id
     *
     * @return 当前登录用户的组织机构id
     * @author xuyuxiang
     * @date 2020/4/5 18:31
     */
    Long getSysLoginUserOrgId();

    /**
     * 获取当前登录用户的角色id集合
     *
     * @return 当前登录用户角色id集合
     * @author xuyuxiang
     * @date 2020/4/20 16:04
     */
    List<String> getLoginUserRoleIds();

    /**
     * 获取最新的用户信息，用于修改之后前端获取
     *
     * @return 最新的用户信息
     * @author xuyuxiang
     * @date 2020/9/20 15:18
     **/
    SysLoginUser getSysLoginUserUpToDate();
}
