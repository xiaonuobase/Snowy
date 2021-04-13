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

import cn.hutool.core.lang.Dict;
import com.baomidou.mybatisplus.extension.service.IService;
import vip.xiaonuo.sys.modular.emp.entity.SysEmpPos;

import java.util.List;

/**
 * 员工职位service接口
 *
 * @author xuyuxiang
 * @date 2020/3/13 15:10
 */
public interface SysEmpPosService extends IService<SysEmpPos> {

    /**
     * 保存职位相关信息
     *
     * @param empId     员工id（用户id）
     * @param posIdList 职位id集合
     * @author xuyuxiang
     * @date 2020/4/2 9:00
     */
    void addOrEdit(Long empId, List<Long> posIdList);

    /**
     * 获取所属职位信息
     *
     * @param empId    员工id（用户id）
     * @param isFillId 是否需要返回id信息
     * @return 增强版hashMap，格式：[{"posId":456, "posCode":"zjl", "posName":"总经理"}]
     * @author xuyuxiang
     * @date 2020/4/2 20:07
     */
    List<Dict> getEmpPosDictList(Long empId, boolean isFillId);

    /**
     * 根据职位id判断该职位下是否有员工
     *
     * @param posId 职位id
     * @return 该职位下是否有员工，true是，false否
     * @author xuyuxiang
     * @date 2020/6/23 10:40
     */
    boolean hasPosEmp(Long posId);

    /**
     * 根据员工id删除对用的员工-职位信息
     *
     * @param empId 员工id（用户id）
     * @author xuyuxiang
     * @date 2020/6/28 14:58
     */
    void deleteEmpPosInfoByUserId(Long empId);
}
