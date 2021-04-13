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
package vip.xiaonuo.core.pojo.base.param;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 通用基础参数，相关实体参数校验可继承此类
 *
 * @author xuyuxiang
 * @date 2020/3/10 16:02
 */
@Data
public class BaseParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 搜索值
     */
    private String searchValue;

    /**
     * 数据权限
     */
    private List<Long> dataScope;

    /**
     * 开始时间
     */
    private String searchBeginTime;

    /**
     * 结束时间
     */
    private String searchEndTime;

    /**
     * 状态（字典 0正常 1停用 2删除）
     */
    private Integer searchStatus;

    /**
     * 参数校验分组：分页
     */
    public @interface page {
    }

    /**
     * 参数校验分组：列表
     */
    public @interface list {
    }

    /**
     * 参数校验分组：下拉
     */
    public @interface dropDown {
    }

    /**
     * 参数校验分组：增加
     */
    public @interface add {
    }

    /**
     * 参数校验分组：编辑
     */
    public @interface edit {
    }

    /**
     * 参数校验分组：更新信息
     */
    public @interface updateInfo {
    }

    /**
     * 参数校验分组：修改密码
     */
    public @interface updatePwd {
    }

    /**
     * 参数校验分组：重置密码
     */
    public @interface resetPwd {
    }

    /**
     * 参数校验分组：修改头像
     */
    public @interface updateAvatar {
    }

    /**
     * 参数校验分组：删除
     */
    public @interface delete {
    }

    /**
     * 参数校验分组：详情
     */
    public @interface detail {
    }

    /**
     * 参数校验分组：授权角色
     */
    public @interface grantRole {
    }

    /**
     * 参数校验分组：授权菜单
     */
    public @interface grantMenu {
    }

    /**
     * 参数校验分组：授权数据
     */
    public @interface grantData {
    }

    /**
     * 参数校验分组：强退
     */
    public @interface force {
    }

    /**
     * 参数校验分组：停用
     */
    public @interface stop {
    }

    /**
     * 参数校验分组：启用
     */
    public @interface start {
    }

    /**
     * 参数校验分组：部署
     */
    public @interface deploy {
    }

    /**
     * 参数校验分组：挂起
     */
    public @interface suspend {
    }

    /**
     * 参数校验分组：激活
     */
    public @interface active {
    }

    /**
     * 参数校验分组：委托
     */
    public @interface entrust {
    }

    /**
     * 参数校验分组：转办
     */
    public @interface turn {
    }

    /**
     * 参数校验分组：追踪
     */
    public @interface trace {
    }

    /**
     * 参数校验分组：跳转
     */
    public @interface jump {
    }

    /**
     * 参数校验分组：提交
     */
    public @interface submit {
    }

    /**
     * 参数校验分组：退回
     */
    public @interface back {
    }

    /**
     * 参数校验分组：终止
     */
    public @interface end {
    }

    /**
     * 参数校验分组：导出
     */
    public @interface export {
    }

    /**
     * 参数校验分组：映射
     */
    public @interface mapping {
    }

    /**
     * 参数校验分组：切换
     */
    public @interface change {
    }

    /**
     * 参数校验分组：历史审批记录
     */
    public @interface commentHistory {
    }

    /**
     * 参数校验分组：修改状态
     */
    public @interface changeStatus {
    }

    /**
     * 参数校验分组：加签
     */
    public @interface addSign {
    }

    /**
     * 参数校验分组：减签
     */
    public @interface deleteSign {
    }

}
