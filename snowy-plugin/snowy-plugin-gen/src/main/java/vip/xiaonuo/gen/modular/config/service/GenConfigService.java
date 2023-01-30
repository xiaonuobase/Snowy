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
package vip.xiaonuo.gen.modular.config.service;

import com.baomidou.mybatisplus.extension.service.IService;
import vip.xiaonuo.gen.modular.config.entity.GenConfig;
import vip.xiaonuo.gen.modular.config.param.GenConfigEditParam;
import vip.xiaonuo.gen.modular.config.param.GenConfigIdParam;
import vip.xiaonuo.gen.modular.config.param.GenConfigListParam;

import java.util.List;

/**
 * 代码生成详细配置配置Service接口
 *
 * @author yubaoshan
 * @date 2022/10/25 22:33
 **/
public interface GenConfigService extends IService<GenConfig> {

    /**
     * 查询代码生成详细配置列表
     *
     * @author yubaoshan
     * @date 2022/10/25 22:33
     */
    List<GenConfig> list(GenConfigListParam genConfigListParam);

    /**
     * 编辑代码生成详细配置
     *
     * @author yubaoshan
     * @date 2022/10/25 22:33
     */
    void edit(GenConfigEditParam genConfigEditParam);

    /**
     * 删除代码生成详细配置
     *
     * @author yubaoshan
     * @date 2022/10/25 22:33
     */
    void delete(List<GenConfigIdParam> genConfigIdParamList);

    /**
     * 获取代码生成详细配置详情
     *
     * @author yubaoshan
     * @date 2022/10/25 22:33
     */
    GenConfig detail(GenConfigIdParam genConfigIdParam);

    /**
     * 获取代码生成详细配置详情
     *
     * @author yubaoshan
     * @date 2022/10/25 22:33
     **/
    GenConfig queryEntity(String id);

    /**
     * 批量编辑代码生成详细配置
     *
     * @author xuyuxiang
     * @date 2022/10/28 13:49
     **/
    void editBatch(List<GenConfigEditParam> genConfigEditParamList);
}
