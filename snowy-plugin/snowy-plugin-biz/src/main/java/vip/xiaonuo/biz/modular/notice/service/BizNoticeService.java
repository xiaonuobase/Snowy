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
package vip.xiaonuo.biz.modular.notice.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import vip.xiaonuo.biz.modular.notice.entity.BizNotice;
import vip.xiaonuo.biz.modular.notice.param.BizNoticeAddParam;
import vip.xiaonuo.biz.modular.notice.param.BizNoticeEditParam;
import vip.xiaonuo.biz.modular.notice.param.BizNoticeIdParam;
import vip.xiaonuo.biz.modular.notice.param.BizNoticePageParam;

import java.util.List;

/**
 * 通知公告Service接口
 *
 * @author yubaoshan
 * @date  2024/07/11 14:46
 **/
public interface BizNoticeService extends IService<BizNotice> {

    /**
     * 获取通知公告分页
     *
     * @author yubaoshan
     * @date  2024/07/11 14:46
     */
    Page<BizNotice> page(BizNoticePageParam bizNoticePageParam);

    /**
     * 添加通知公告
     *
     * @author yubaoshan
     * @date  2024/07/11 14:46
     */
    void add(BizNoticeAddParam bizNoticeAddParam);

    /**
     * 编辑通知公告
     *
     * @author yubaoshan
     * @date  2024/07/11 14:46
     */
    void edit(BizNoticeEditParam bizNoticeEditParam);

    /**
     * 删除通知公告
     *
     * @author yubaoshan
     * @date  2024/07/11 14:46
     */
    void delete(List<BizNoticeIdParam> bizNoticeIdParamList);

    /**
     * 获取通知公告详情
     *
     * @author yubaoshan
     * @date  2024/07/11 14:46
     */
    BizNotice detail(BizNoticeIdParam bizNoticeIdParam);

    /**
     * 获取通知公告详情
     *
     * @author yubaoshan
     * @date  2024/07/11 14:46
     **/
    BizNotice queryEntity(String id);

    /**
     * 禁用通知公告
     *
     * @author yubaoshan
     * @date  2024/07/11 14:46
     */
    void disableStatus(BizNoticeIdParam bizNoticeIdParam);

    /**
     * 启用通知公告
     *
     * @author yubaoshan
     * @date  2024/07/11 14:46
     */
    void enableStatus(BizNoticeIdParam bizNoticeIdParam);
}
