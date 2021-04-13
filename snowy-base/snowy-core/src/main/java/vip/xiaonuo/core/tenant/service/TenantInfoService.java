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
package vip.xiaonuo.core.tenant.service;

import com.baomidou.mybatisplus.extension.service.IService;
import vip.xiaonuo.core.pojo.page.PageResult;
import vip.xiaonuo.core.tenant.entity.TenantInfo;
import vip.xiaonuo.core.tenant.params.TenantInfoParam;

/**
 * 租户表 服务类
 *
 * @author yubaoshan
 * @since 2019-06-16
 */
public interface TenantInfoService extends IService<TenantInfo> {

    /**
     * 新增租户
     *
     * @param param 添加参数
     * @author yubaoshan
     * @date 2019-06-16
     */
    void add(TenantInfoParam param);

    /**
     * 删除租户
     *
     * @param param 删除参数
     * @author yubaoshan
     * @date 2019-06-16
     */
    void delete(TenantInfoParam param);

    /**
     * 更新租户
     *
     * @param param 更新参数
     * @author yubaoshan
     * @date 2019-06-16
     */
    void update(TenantInfoParam param);

    /**
     * 分页查询租户列表
     *
     * @param param 查询参数
     * @return 查询结果
     * @author xuyuxiang
     * @date 2020/9/3
     */
    PageResult<TenantInfo> page(TenantInfoParam param);

    /**
     * 获取租户信息，通过租户编码
     *
     * @param code 租户编码
     * @return 租户信息
     * @author xuyuxiang
     * @date 2019-06-19 14:17
     */
    TenantInfo getByCode(String code);

}
