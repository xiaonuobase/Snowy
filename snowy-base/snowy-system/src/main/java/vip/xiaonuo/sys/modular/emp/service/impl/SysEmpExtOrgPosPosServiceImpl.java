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
package vip.xiaonuo.sys.modular.emp.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Dict;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import vip.xiaonuo.sys.modular.emp.entity.SysEmpExtOrgPos;
import vip.xiaonuo.sys.modular.emp.mapper.SysEmpExtOrgPosMapper;
import vip.xiaonuo.sys.modular.emp.service.SysEmpExtOrgPosService;
import vip.xiaonuo.sys.modular.org.entity.SysOrg;
import vip.xiaonuo.sys.modular.org.service.SysOrgService;
import vip.xiaonuo.sys.modular.pos.entity.SysPos;
import vip.xiaonuo.sys.modular.pos.service.SysPosService;

import javax.annotation.Resource;
import java.util.List;

/**
 * 员工附属机构service接口实现类
 *
 * @author xuyuxiang
 * @date 2020/3/13 15:11
 */
@Service
public class SysEmpExtOrgPosPosServiceImpl extends ServiceImpl<SysEmpExtOrgPosMapper, SysEmpExtOrgPos> implements SysEmpExtOrgPosService {

    @Resource
    private SysOrgService sysOrgService;

    @Resource
    private SysPosService sysPosService;

    /**
     * 附属机构id参数键
     */
    private static final String EXT_ORG_ID_DICT_KEY = "orgId";

    /**
     * 附属机构编码参数键
     */
    private static final String EXT_ORG_CODE_DICT_KEY = "orgCode";

    /**
     * 附属机构名称参数键
     */
    private static final String EXT_ORG_NAME_DICT_KEY = "orgName";

    /**
     * 附属职位id参数键
     */
    private static final String EXT_POS_ID_DICT_KEY = "posId";

    /**
     * 附属职位编码参数键
     */
    private static final String EXT_POS_CODE_DICT_KEY = "posCode";

    /**
     * 附属职位名称参数键
     */
    private static final String EXT_POS_NAME_DICT_KEY = "posName";

    @Override
    public void addOrEdit(Long empId, List<Dict> extIdList) {
        LambdaQueryWrapper<SysEmpExtOrgPos> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysEmpExtOrgPos::getEmpId, empId);
        //删除附属信息
        this.remove(queryWrapper);
        //保存附属信息
        extIdList.forEach(dict -> {
            Long orgId = dict.getLong(EXT_ORG_ID_DICT_KEY);
            Long posId = dict.getLong(EXT_POS_ID_DICT_KEY);
            SysEmpExtOrgPos sysEmpExtOrgPos = new SysEmpExtOrgPos();
            sysEmpExtOrgPos.setEmpId(empId);
            sysEmpExtOrgPos.setOrgId(orgId);
            sysEmpExtOrgPos.setPosId(posId);
            this.save(sysEmpExtOrgPos);
        });
    }

    @Override
    public List<Dict> getEmpExtOrgPosDictList(Long empId, boolean isFillId) {
        List<Dict> dictList = CollectionUtil.newArrayList();

        LambdaQueryWrapper<SysEmpExtOrgPos> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysEmpExtOrgPos::getEmpId, empId);

        this.list(queryWrapper).forEach(sysEmpExtOrgPos -> {
            Dict dict = Dict.create();
            Long orgId = sysEmpExtOrgPos.getOrgId();
            SysOrg sysOrg = sysOrgService.getById(orgId);
            dict.put(EXT_ORG_CODE_DICT_KEY, sysOrg.getCode());
            dict.put(EXT_ORG_NAME_DICT_KEY, sysOrg.getName());

            Long posId = sysEmpExtOrgPos.getPosId();
            SysPos sysPos = sysPosService.getById(posId);
            dict.put(EXT_POS_CODE_DICT_KEY, sysPos.getCode());
            dict.put(EXT_POS_NAME_DICT_KEY, sysPos.getName());

            if (isFillId) {
                dict.put(EXT_ORG_ID_DICT_KEY, orgId);
                dict.put(EXT_POS_ID_DICT_KEY, posId);
            }
            dictList.add(dict);
        });

        return dictList;
    }

    @Override
    public boolean hasExtOrgEmp(Long orgId) {
        LambdaQueryWrapper<SysEmpExtOrgPos> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysEmpExtOrgPos::getOrgId, orgId);
        List<SysEmpExtOrgPos> list = this.list(queryWrapper);
        return list.size() != 0;
    }

    @Override
    public boolean hasExtPosEmp(Long posId) {
        LambdaQueryWrapper<SysEmpExtOrgPos> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysEmpExtOrgPos::getPosId, posId);
        List<SysEmpExtOrgPos> list = this.list(queryWrapper);
        return list.size() != 0;
    }

    @Override
    public void deleteEmpExtInfoByUserId(Long empId) {
        LambdaQueryWrapper<SysEmpExtOrgPos> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysEmpExtOrgPos::getEmpId, empId);
        this.remove(queryWrapper);
    }
}
