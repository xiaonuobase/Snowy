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
package vip.xiaonuo.sys.modular.role.service;

import cn.hutool.core.lang.Dict;
import com.baomidou.mybatisplus.extension.service.IService;
import vip.xiaonuo.core.pojo.page.PageResult;
import vip.xiaonuo.sys.modular.role.entity.SysRole;
import vip.xiaonuo.sys.modular.role.param.SysRoleParam;

import java.util.List;

/**
 * 系统角色service接口
 *
 * @author xuyuxiang
 * @date 2020/3/13 15:47
 */
public interface SysRoleService extends IService<SysRole> {

    /**
     * 获取用户角色相关信息
     *
     * @param userId 用户id
     * @return 增强版hashMap，格式：[{"id":456, "code":"zjl", "name":"总经理"}]
     * @author xuyuxiang
     * @date 2020/3/13 16:26
     */
    List<Dict> getLoginRoles(Long userId);

    /**
     * 查询系统角色
     *
     * @param sysRoleParam 查询参数
     * @return 查询分页结果
     * @author xuyuxiang
     * @date 2020/3/28 14:53
     */
    PageResult<SysRole> page(SysRoleParam sysRoleParam);

    /**
     * 根据角色名模糊搜索系统角色列表
     *
     * @param sysRoleParam 查询参数
     * @return 增强版hashMap，格式：[{"id":456, "name":"总经理(zjl)"}]
     * @author xuyuxiang
     * @date 2020/4/14 9:21
     */
    List<Dict> list(SysRoleParam sysRoleParam);

    /**
     * 系统角色下拉（用于授权角色时选择）
     *
     * @return 增强版hashMap，格式：[{"id":456, "code":"zjl", "name":"总经理"}]
     * @author xuyuxiang
     * @date 2020/4/5 16:47
     */
    List<Dict> dropDown();

    /**
     * 添加系统角色
     *
     * @param sysRoleParam 添加参数
     * @author xuyuxiang
     * @date 2020/3/28 14:54
     */
    void add(SysRoleParam sysRoleParam);

    /**
     * 删除系统角色
     *
     * @param sysRoleParam 删除参数
     * @author xuyuxiang
     * @date 2020/3/28 14:54
     */
    void delete(SysRoleParam sysRoleParam);

    /**
     * 编辑系统角色
     *
     * @param sysRoleParam 编辑参数
     * @author xuyuxiang
     * @date 2020/3/28 14:54
     */
    void edit(SysRoleParam sysRoleParam);

    /**
     * 查看系统角色
     *
     * @param sysRoleParam 查看参数
     * @return 系统角色
     * @author xuyuxiang
     * @date 2020/3/26 9:50
     */
    SysRole detail(SysRoleParam sysRoleParam);

    /**
     * 授权菜单
     *
     * @param sysRoleParam 授权参数
     * @author xuyuxiang
     * @date 2020/3/28 16:19
     */
    void grantMenu(SysRoleParam sysRoleParam);

    /**
     * 授权数据
     *
     * @param sysRoleParam 授权参数
     * @author xuyuxiang
     * @date 2020/3/28 16:20
     */
    void grantData(SysRoleParam sysRoleParam);

    /**
     * 根据角色id集合获取数据范围id集合
     *
     * @param roleIdList 角色id集合
     * @param orgId      机构id
     * @return 数据范围id集合
     * @author xuyuxiang
     * @date 2020/4/5 17:41
     */
    List<Long> getUserDataScopeIdList(List<Long> roleIdList, Long orgId);

    /**
     * 根据角色id获取角色名称
     *
     * @param roleId 角色id
     * @return 角色名称
     * @author xuyuxiang
     * @date 2020/5/22 16:15
     */
    String getNameByRoleId(Long roleId);

    /**
     * 查询角色拥有菜单
     *
     * @param sysRoleParam 查询参数
     * @return 菜单id集合
     * @author xuyuxiang
     * @date 2020/5/29 14:03
     */
    List<Long> ownMenu(SysRoleParam sysRoleParam);

    /**
     * 查询角色拥有数据
     *
     * @param sysRoleParam 查询参数
     * @return 数据范围id集合
     * @author xuyuxiang
     * @date 2020/5/29 14:04
     */
    List<Long> ownData(SysRoleParam sysRoleParam);
}
