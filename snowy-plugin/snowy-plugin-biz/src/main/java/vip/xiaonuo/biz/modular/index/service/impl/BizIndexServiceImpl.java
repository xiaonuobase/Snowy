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
package vip.xiaonuo.biz.modular.index.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import vip.xiaonuo.biz.modular.index.param.BizIndexNoticeIdParam;
import vip.xiaonuo.biz.modular.index.param.BizIndexNoticeListParam;
import vip.xiaonuo.biz.modular.index.param.BizIndexSlideshowListParam;
import vip.xiaonuo.biz.modular.index.result.BizIndexNoticeListResult;
import vip.xiaonuo.biz.modular.index.result.BizIndexSlideshowDetailResult;
import vip.xiaonuo.biz.modular.index.result.BizIndexSlideshowListResult;
import vip.xiaonuo.biz.modular.index.service.BizIndexService;
import vip.xiaonuo.biz.modular.notice.entity.BizNotice;
import vip.xiaonuo.biz.modular.notice.enums.BizNoticeStatusEnum;
import vip.xiaonuo.biz.modular.notice.service.BizNoticeService;
import vip.xiaonuo.dev.api.DevSlideshowApi;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 业务首页Service接口实现类
 *
 * @author yubaoshan
 * @date  2024/07/13 00:31
 */
@Service
public class BizIndexServiceImpl implements BizIndexService {


    @Resource
    private DevSlideshowApi devSlideshowApi;

    @Resource
    private BizNoticeService bizNoticeService;

    @Override
    public List<BizIndexSlideshowListResult> slideshowListByPlace(BizIndexSlideshowListParam bizIndexSlideshowListParam) {
        return devSlideshowApi.getListByPlace(bizIndexSlideshowListParam.getPlace()).stream()
                .map(jsonObject -> JSONUtil.toBean(jsonObject, BizIndexSlideshowListResult.class)).collect(Collectors.toList());
    }

    @Override
    public List<BizIndexNoticeListResult> noticeListByLimit(BizIndexNoticeListParam bizIndexNoticeListParam) {
        return bizNoticeService.list(new LambdaQueryWrapper<BizNotice>()
                    .eq(BizNotice::getStatus, BizNoticeStatusEnum.ENABLE.getValue())
                    .orderByAsc(BizNotice::getCreateTime)
                )
                .stream()
                .limit(ObjectUtil.isNotEmpty(bizIndexNoticeListParam.getLimit())? bizIndexNoticeListParam.getLimit().longValue() : 10)
                .map(notice -> JSONUtil.toBean(JSONUtil.toJsonStr(notice), BizIndexNoticeListResult.class)).collect(Collectors.toList());
    }

    @Override
    public BizIndexSlideshowDetailResult noticeDetailById(BizIndexNoticeIdParam bizIndexNoticeIdParam) {
        BizIndexSlideshowDetailResult result = new BizIndexSlideshowDetailResult();
        BeanUtil.copyProperties(bizNoticeService.getById(bizIndexNoticeIdParam.getId()), result);
        return result;
    }
}
