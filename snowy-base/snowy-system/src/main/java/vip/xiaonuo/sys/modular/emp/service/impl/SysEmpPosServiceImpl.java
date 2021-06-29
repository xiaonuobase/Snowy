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
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Dict;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import vip.xiaonuo.sys.modular.emp.entity.SysEmpPos;
import vip.xiaonuo.sys.modular.emp.mapper.SysEmpPosMapper;
import vip.xiaonuo.sys.modular.emp.service.SysEmpPosService;
import vip.xiaonuo.sys.modular.pos.entity.SysPos;
import vip.xiaonuo.sys.modular.pos.service.SysPosService;

import javax.annotation.Resource;
import java.util.List;

/**
 * 员工职位service接口实现类
 *
 * @author xuyuxiang
 * @date 2020/3/13 15:11
 */
@Service
public class SysEmpPosServiceImpl extends ServiceImpl<SysEmpPosMapper, SysEmpPos> implements SysEmpPosService {

    @Resource
    private SysPosService sysPosService;

    /**
     * 职位id参数键
     */
    private static final String POS_ID_DICT_KEY = "posId";

    /**
     * 职位编码参数键
     */
    private static final String POS_CODE_DICT_KEY = "posCode";

    /**
     * 职位名称参数键
     */
    private static final String POS_NAME_DICT_KEY = "posName";

    @Override
    public void addOrEdit(Long empId, List<Long> posIdList) {
        LambdaQueryWrapper<SysEmpPos> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysEmpPos::getEmpId, empId);

        //删除职位信息
        this.remove(queryWrapper);

        //保存职位信息
        posIdList.forEach(posId -> {
            SysEmpPos sysEmpPos = new SysEmpPos();
            sysEmpPos.setEmpId(empId);
            sysEmpPos.setPosId(Convert.toLong(posId));
            this.save(sysEmpPos);
        });
    }

    @Override
    public List<Dict> getEmpPosDictList(Long empId, boolean isFillId) {
        List<Dict> dictList = CollectionUtil.newArrayList();

        LambdaQueryWrapper<SysEmpPos> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysEmpPos::getEmpId, empId);

        this.list(queryWrapper).forEach(sysEmpPos -> {
            Dict dict = Dict.create();
            Long posId = sysEmpPos.getPosId();
            SysPos sysPos = sysPosService.getById(posId);
            if (isFillId) {
                dict.put(POS_ID_DICT_KEY, posId);
            }
            dict.put(POS_CODE_DICT_KEY, sysPos.getCode());
            dict.put(POS_NAME_DICT_KEY, sysPos.getName());
            dictList.add(dict);
        });
        return dictList;
    }

    @Override
    public boolean hasPosEmp(Long posId) {
        LambdaQueryWrapper<SysEmpPos> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysEmpPos::getPosId, posId);
        List<SysEmpPos> list = this.list(queryWrapper);
        return list.size() != 0;
    }

    @Override
    public void deleteEmpPosInfoByUserId(Long empId) {
        LambdaQueryWrapper<SysEmpPos> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysEmpPos::getEmpId, empId);
        this.remove(queryWrapper);
    }
}
