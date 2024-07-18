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
package vip.xiaonuo.dev.modular.slideshow.service;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import vip.xiaonuo.dev.modular.slideshow.entity.DevSlideshow;
import vip.xiaonuo.dev.modular.slideshow.param.DevSlideshowAddParam;
import vip.xiaonuo.dev.modular.slideshow.param.DevSlideshowEditParam;
import vip.xiaonuo.dev.modular.slideshow.param.DevSlideshowIdParam;
import vip.xiaonuo.dev.modular.slideshow.param.DevSlideshowPageParam;

import java.util.List;

/**
 * 轮播图Service接口
 *
 * @author yubaoshan
 * @date  2024/07/13 00:31
 **/
public interface DevSlideshowService extends IService<DevSlideshow> {

    /**
     * 获取轮播图分页
     *
     * @author yubaoshan
     * @date  2024/07/13 00:31
     */
    Page<DevSlideshow> page(DevSlideshowPageParam devSlideshowPageParam);

    /**
     * 添加轮播图
     *
     * @author yubaoshan
     * @date  2024/07/13 00:31
     */
    void add(DevSlideshowAddParam devSlideshowAddParam);

    /**
     * 编辑轮播图
     *
     * @author yubaoshan
     * @date  2024/07/13 00:31
     */
    void edit(DevSlideshowEditParam devSlideshowEditParam);

    /**
     * 删除轮播图
     *
     * @author yubaoshan
     * @date  2024/07/13 00:31
     */
    void delete(List<DevSlideshowIdParam> devSlideshowIdParamList);

    /**
     * 获取轮播图详情
     *
     * @author yubaoshan
     * @date  2024/07/13 00:31
     **/
    DevSlideshow queryEntity(String id);

    /**
     * 禁用轮播图
     *
     * @author yubaoshan
     * @date  2024/07/13 00:31
     **/
    void disableStatus(DevSlideshowIdParam devSlideshowIdParam);

    /**
     * 启用轮播图
     *
     * @author yubaoshan
     * @date  2024/07/13 00:31
     **/
    void enableStatus(DevSlideshowIdParam devSlideshowIdParam);

    /**
     * 通过位置获得轮播图列表
     *
     * @author yubaoshan
     * @date  2024/07/13 00:31
     **/
    List<JSONObject> getListByPlace(String place);
}
