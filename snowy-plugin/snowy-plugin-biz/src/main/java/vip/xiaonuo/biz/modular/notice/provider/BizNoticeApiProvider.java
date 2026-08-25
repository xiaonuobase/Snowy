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
package vip.xiaonuo.biz.modular.notice.provider;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import vip.xiaonuo.biz.api.BizNoticeApi;
import vip.xiaonuo.biz.modular.notice.entity.BizNotice;
import vip.xiaonuo.biz.modular.notice.enums.BizNoticeStatusEnum;
import vip.xiaonuo.biz.modular.notice.mapper.BizNoticeMapper;
import vip.xiaonuo.common.exception.CommonException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 通知公告API接口提供者
 *
 * @author yubaoshan
 * @date 2026/8/23 10:00
 **/
@Service
public class BizNoticeApiProvider implements BizNoticeApi {

    /** 默认取多少条 */
    private static final int DEFAULT_LIMIT = 5;

    /** 最多取多少条，防止调用方传入过大值 */
    private static final int MAX_LIMIT = 20;

    @Resource
    private BizNoticeMapper bizNoticeMapper;

    @Override
    public List<JSONObject> latestNoticeList(Integer limit) {
        int realLimit = DEFAULT_LIMIT;
        if (ObjectUtil.isNotNull(limit) && limit > 0) {
            realLimit = Math.min(limit, MAX_LIMIT);
        }
        // 用MP分页取前N条，由分页插件按数据库方言生成语句，不写死LIMIT以兼容金仓等国产库
        List<BizNotice> noticeList = bizNoticeMapper.selectPage(new Page<>(1, realLimit, false),
                new LambdaQueryWrapper<BizNotice>()
                        .eq(BizNotice::getStatus, BizNoticeStatusEnum.ENABLE.getValue())
                        .orderByAsc(BizNotice::getSortCode)
                        .orderByDesc(BizNotice::getCreateTime)).getRecords();
        // 跨插件调用不返回实体类，统一转成JSONObject，且只暴露门户展示需要的字段
        return noticeList.stream().map(notice -> {
            JSONObject item = new JSONObject();
            item.set("id", notice.getId());
            item.set("title", notice.getTitle());
            item.set("digest", notice.getDigest());
            item.set("type", notice.getType());
            item.set("createTime", notice.getCreateTime());
            return item;
        }).collect(Collectors.toList());
    }

    @Override
    public JSONObject noticeDetail(String id) {
        if (ObjectUtil.isEmpty(id)) {
            throw new CommonException("公告id不能为空");
        }
        // 必须再校验一次已发布状态：门户详情是免登录接口，只按id查会让未发布公告被猜到id后读到
        BizNotice notice = bizNoticeMapper.selectOne(new LambdaQueryWrapper<BizNotice>()
                .eq(BizNotice::getId, id)
                .eq(BizNotice::getStatus, BizNoticeStatusEnum.ENABLE.getValue()));
        if (ObjectUtil.isEmpty(notice)) {
            throw new CommonException("公告不存在或已下架，id值为：{}", id);
        }
        JSONObject result = new JSONObject();
        result.set("id", notice.getId());
        result.set("title", notice.getTitle());
        result.set("content", notice.getContent());
        result.set("type", notice.getType());
        result.set("createTime", notice.getCreateTime());
        return result;
    }
}
