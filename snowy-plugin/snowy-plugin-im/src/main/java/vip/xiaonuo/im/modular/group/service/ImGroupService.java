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
package vip.xiaonuo.im.modular.group.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;
import vip.xiaonuo.im.modular.group.entity.ImGroup;
import vip.xiaonuo.im.modular.group.param.ImGroupAddParam;
import vip.xiaonuo.im.modular.group.param.ImGroupEditParam;
import vip.xiaonuo.im.modular.group.param.ImGroupIdParam;
import vip.xiaonuo.im.modular.group.param.ImGroupPageParam;

import java.util.List;

/**
 * IM-群组Service接口
 *
 * @author liuchunming
 * @date  2024/05/27 16:40
 **/
public interface ImGroupService extends IService<ImGroup> {

    /**
     * 获取IM-群组分页
     *
     * @author liuchunming
     * @date  2024/05/27 16:40
     */
    Page<ImGroup> page(ImGroupPageParam imGroupPageParam);

    /**
     * 添加IM-群组
     *
     * @author liuchunming
     * @date  2024/05/27 16:40
     */
    void add(ImGroupAddParam imGroupAddParam);

    /**
     * 编辑IM-群组
     *
     * @author liuchunming
     * @date  2024/05/27 16:40
     */
    void edit(ImGroupEditParam imGroupEditParam);

    /**
     * 删除IM-群组
     *
     * @author liuchunming
     * @date  2024/05/27 16:40
     */
    void delete(List<ImGroupIdParam> imGroupIdParamList);

    /**
     * 获取IM-群组详情
     *
     * @author liuchunming
     * @date  2024/05/27 16:40
     */
    ImGroup detail(ImGroupIdParam imGroupIdParam);

    /**
     * 获取IM-群组详情
     *
     * @author liuchunming
     * @date  2024/05/27 16:40
     **/
    ImGroup queryEntity(String id);

    /**
     * 查询当前用户的群组列表
     *
     * @author chengchuanyao
     * @date 2024/7/27 14:28
     */
    List<ImGroup> listByUser();

    /**
     * 上传群组头像
     *
     * @author chengchuanyao
     * @date 2024/8/31 14:04
     */
    String uploadAvatar(MultipartFile file);
}
