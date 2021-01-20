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

XiaoNuo采用APACHE LICENSE 2.0开源协议，您在使用过程中，需要注意以下几点：

1.请不要删除和修改根目录下的LICENSE文件。
2.请不要删除和修改XiaoNuo源码头部的版权声明。
3.请保留源码和相关描述文件的项目出处，作者声明等。
4.分发源码时候，请注明软件出处 https://gitee.com/xiaonuobase/xiaonuo-vue
5.在修改包名，模块名称，项目代码等时，请注明软件出处 https://gitee.com/xiaonuobase/xiaonuo-vue
6.若您的项目无法满足以上几点，可申请商业授权，获取XiaoNuo商业授权许可，请在官网购买授权，地址为 https://www.xiaonuo.vip
 */
package com.cn.xiaonuo.sys.modular.pos.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cn.xiaonuo.core.pojo.page.PageResult;
import com.cn.xiaonuo.sys.modular.pos.entity.SysPos;
import com.cn.xiaonuo.sys.modular.pos.param.SysPosParam;

import java.util.List;

/**
 * 系统职位service接口
 *
 * @author xuyuxiang
 * @date 2020/3/13 16:00
 */
public interface SysPosService extends IService<SysPos> {

    /**
     * 查询系统职位
     *
     * @param sysPosParam 查询参数
     * @return 查询分页结果
     * @author xuyuxiang
     * @date 2020/3/26 10:19
     */
    PageResult<SysPos> page(SysPosParam sysPosParam);

    /**
     * 系统职位列表
     *
     * @param sysPosParam 查询参数
     * @return 职位列表
     * @author yubaoshan
     * @date 2020/6/21 23:44
     */
    List<SysPos> list(SysPosParam sysPosParam);

    /**
     * 添加系统职位
     *
     * @param sysPosParam 添加参数
     * @author xuyuxiang
     * @date 2020/3/25 14:57
     */
    void add(SysPosParam sysPosParam);

    /**
     * 删除系统职位
     *
     * @param sysPosParam 删除参数
     * @author xuyuxiang
     * @date 2020/3/25 14:57
     */
    void delete(SysPosParam sysPosParam);

    /**
     * 编辑系统职位
     *
     * @param sysPosParam 编辑参数
     * @author xuyuxiang
     * @date 2020/3/25 14:58
     */
    void edit(SysPosParam sysPosParam);

    /**
     * 查看系统职位
     *
     * @param sysPosParam 查看参数
     * @return 系统职位
     * @author xuyuxiang
     * @date 2020/3/26 9:50
     */
    SysPos detail(SysPosParam sysPosParam);
}
