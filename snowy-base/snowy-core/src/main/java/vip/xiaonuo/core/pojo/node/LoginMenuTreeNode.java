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
package vip.xiaonuo.core.pojo.node;

import lombok.Data;

/**
 * 登录菜单
 *
 * @author yubaoshan
 * @date 2020/4/17 17:35
 */
@Data
public class LoginMenuTreeNode {

    /**
     * id
     */
    private Long id;

    /**
     * 父id
     */
    private Long pid;

    /**
     * 路由名称, 必须设置,且不能重名
     */
    private String name;

    /**
     * 组件
     */
    private String component;

    /**
     * 重定向地址, 访问这个路由时, 自定进行重定向
     */
    private String redirect;

    /**
     * 路由元信息（路由附带扩展信息）
     */
    private Meta meta;

    /**
     * 路径
     */
    private String path;

    /**
     * 控制路由和子路由是否显示在 sidebar
     */
    private boolean hidden;

    /**
     * 路由元信息内部类
     */
    @Data
    public class Meta {

        /**
         * 路由标题, 用于显示面包屑, 页面标题 *推荐设置
         */
        public String title;

        /**
         * 图标
         */
        public String icon;

        /**
         * 是否可见
         */
        public boolean show;

        /**
         * 如需外部打开，增加：_blank
         */
        public String target;

        /**
         * 内链打开http链接
         */
        public String link;

    }

}
