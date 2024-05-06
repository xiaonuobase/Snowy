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
package vip.xiaonuo.sys.modular.relation.provider;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import vip.xiaonuo.sys.api.SysRelationApi;
import vip.xiaonuo.sys.modular.relation.entity.SysRelation;
import vip.xiaonuo.sys.modular.relation.enums.SysRelationCategoryEnum;
import vip.xiaonuo.sys.modular.relation.service.SysRelationService;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * 关系API接口实现类
 *
 * @author xuyuxiang
 * @date 2022/6/6 15:33
 **/
@Service
public class SysRelationApiProvider implements SysRelationApi {

    @Resource
    private SysRelationService sysRelationService;

    @Override
    public List<String> getUserIdListByRoleIdList(List<String> roleIdList) {
        return sysRelationService.getRelationObjectIdListByTargetIdListAndCategory(roleIdList,
                SysRelationCategoryEnum.SYS_USER_HAS_ROLE.getValue());
    }

    @Override
    public void removeRoleHasMobileMenuRelation(List<String> targetIdList) {
        sysRelationService.remove(new LambdaQueryWrapper<SysRelation>().in(SysRelation::getTargetId, targetIdList)
                .eq(SysRelation::getCategory, SysRelationCategoryEnum.SYS_ROLE_HAS_MOBILE_MENU.getValue()));
    }

    @Override
    public void removeRoleHasMobileButtonRelation(List<String> targetIdList, List<String> buttonIdList) {
        sysRelationService.list(new LambdaQueryWrapper<SysRelation>().in(SysRelation::getTargetId, targetIdList)
                .eq(SysRelation::getCategory, SysRelationCategoryEnum.SYS_ROLE_HAS_MOBILE_MENU.getValue())
                .isNotNull(SysRelation::getExtJson)).forEach(mobileRelation -> {
            JSONObject extJsonObject = JSONUtil.parseObj(mobileRelation.getExtJson());
            List<String> buttonInfoList = extJsonObject.getBeanList("buttonInfo", String.class);
            if (ObjectUtil.isNotEmpty(buttonInfoList)) {
                Set<String> intersectionDistinct = CollectionUtil.intersectionDistinct(buttonIdList, buttonInfoList);
                if(ObjectUtil.isNotEmpty(intersectionDistinct)) {
                    Collection<String> disjunction = CollectionUtil.disjunction(buttonInfoList, intersectionDistinct);
                    extJsonObject.set("buttonInfo", disjunction);
                }
            }
            // 清除对应的角色与移动端菜单信息中的【授权的移动端按钮信息】
            sysRelationService.update(new LambdaUpdateWrapper<SysRelation>().eq(SysRelation::getId, mobileRelation.getId())
                    .set(SysRelation::getExtJson, JSONUtil.toJsonStr(extJsonObject)));
        });
    }
}
