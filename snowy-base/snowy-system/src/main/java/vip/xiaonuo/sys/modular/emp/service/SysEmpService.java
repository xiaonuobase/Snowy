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
package vip.xiaonuo.sys.modular.emp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import vip.xiaonuo.core.pojo.login.LoginEmpInfo;
import vip.xiaonuo.sys.modular.emp.entity.SysEmp;
import vip.xiaonuo.sys.modular.emp.param.SysEmpParam;
import vip.xiaonuo.sys.modular.emp.result.SysEmpInfo;

/**
 * 员工service接口
 *
 * @author xuyuxiang
 * @date 2020/3/13 15:08
 */
public interface SysEmpService extends IService<SysEmp> {

    /**
     * 获取登录用户员工相关信息
     *
     * @param empId 员工id（用户id）
     * @return 登录用户员工相关信息
     * @author xuyuxiang
     * @date 2020/3/13 15:23
     */
    LoginEmpInfo getLoginEmpInfo(Long empId);

    /**
     * 获取用户员工相关信息
     *
     * @param empId 员工id（用户id）
     * @return 用户员工相关信息
     * @author xuyuxiang
     * @date 2020/4/2 20:33
     */
    SysEmpInfo getSysEmpInfo(Long empId);

    /**
     * 增加或编辑员工相关信息
     *
     * @param sysEmpParam 增加或编辑参数
     * @author xuyuxiang
     * @date 2020/4/2 8:44
     */
    void addOrUpdate(SysEmpParam sysEmpParam);

    /**
     * 修改员工相关机构信息
     *
     * @param orgId   机构id
     * @param orgName 机构名称
     * @author xuyuxiang
     * @date 2020/6/23 9:57
     */
    void updateEmpOrgInfo(Long orgId, String orgName);

    /**
     * 根据机构id判断该机构下是否有员工
     *
     * @param orgId 机构id
     * @return 该机构下是否有员工，true是，false否
     * @author xuyuxiang
     * @date 2020/6/23 10:30
     */
    boolean hasOrgEmp(Long orgId);

    /**
     * 根据员工id删除对应的员工表信息
     *
     * @param empId 员工id（用户id）
     * @author xuyuxiang
     * @date 2020/6/28 14:51
     */
    void deleteEmpInfoByUserId(Long empId);
}
