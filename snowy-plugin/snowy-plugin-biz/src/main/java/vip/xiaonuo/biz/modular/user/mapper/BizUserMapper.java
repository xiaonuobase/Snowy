/*
 * Copyright [2022] [https://www.xiaonuo.vip]
 *
 * Snowy采用APACHE LICENSE 2.0开源协议，您在使用过程中，需要注意以下几点：
 *
 * 1.请不要删除和修改根目录下的LICENSE文件。
 * 2.请不要删除和修改Snowy源码头部的版权声明。
 * 3.本项目代码可免费商业使用，商业使用请保留源码和相关描述文件的项目出处，作者声明等。
 * 4.分发源码时候，请注明软件出处 https://www.xiaonuo.vip
 * 5.不可二次分发开源参与同类竞品，如有想法可联系团队xiaonuobase@qq.com商议合作。
 * 6.若您的项目无法满足以上几点，需要更多功能代码，获取Snowy商业授权许可，请在官网购买授权，地址为 https://www.xiaonuo.vip
 */
package vip.xiaonuo.biz.modular.user.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import vip.xiaonuo.biz.modular.user.entity.BizUser;
import vip.xiaonuo.biz.modular.user.result.BizUserExportResult;
import vip.xiaonuo.biz.modular.user.result.BizUserResult;

import java.util.List;

/**
 * 人员Mapper接口
 *
 * @author xuyuxiang
 * @date 2022/4/21 18:37
 **/
public interface BizUserMapper extends BaseMapper<BizUser> {

    /**
     * 获取人员分页列表
     *
     * @author xuyuxiang
     * @date 2022/7/8 13:27
     **/
    Page<BizUserResult> page(@Param("page") Page<BizUserResult> page, @Param("ew") QueryWrapper<BizUserResult> queryWrapper);

    /**
     * 获取要导出的用户列表
     *
     * @author xuyuxiang
     * @date 2022/7/8 13:27
     **/
    List<BizUserExportResult> exportList(@Param("ew") QueryWrapper<BizUserExportResult> queryWrapper);
}
