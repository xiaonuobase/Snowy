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
package vip.xiaonuo.gen.modular.basic.service;

import cn.hutool.core.lang.tree.Tree;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletResponse;
import vip.xiaonuo.gen.modular.basic.entity.GenBasic;
import vip.xiaonuo.gen.modular.basic.param.*;
import vip.xiaonuo.gen.modular.basic.result.*;

import java.io.IOException;
import java.util.List;

/**
 * 代码生成基础Service接口
 *
 * @author yubaoshan
 * @date 2022/10/25 22:33
 **/
public interface GenBasicService extends IService<GenBasic> {

    /**
     * 查询代码生成基础分页
     *
     * @author yubaoshan
     * @date 2022/10/25 22:33
     */
    Page<GenBasic> page(GenBasicPageParam genBasicPageParam);

    /**
     * 添加代码生成基础
     *
     * @author yubaoshan
     * @date 2022/10/25 22:33
     */
    GenBasic add(GenBasicAddParam genBasicAddParam);

    /**
     * 编辑代码生成基础
     *
     * @author yubaoshan
     * @date 2022/10/25 22:33
     */
    GenBasic edit(GenBasicEditParam genBasicEditParam);

    /**
     * 删除代码生成基础
     *
     * @author yubaoshan
     * @date 2022/10/25 22:33
     */
    void delete(List<GenBasicIdParam> genBasicIdParamList);

    /**
     * 获取代码生成基础详情
     *
     * @author yubaoshan
     * @date 2022/10/25 22:33
     */
    GenBasic detail(GenBasicIdParam genBasicIdParam);

    /**
     * 获取代码生成基础详情
     *
     * @author yubaoshan
     * @date 2022/10/25 22:33
     **/
    GenBasic queryEntity(String id);

    /**
     * 获取所有表信息
     *
     * @author yubaoshan
     * @date 2022/10/25 22:33
     **/
    List<GenBasicTableResult> tables();

    /**
     * 获取表内所有字段信息
     *
     * @author yubaoshan
     * @date 2022/10/25 22:33
     **/
    List<GenBasicTableColumnResult> tableColumns(GenBasicTableColumnParam genBasicTableColumnParam);

    /**
     * 执行代码生成
     *
     * @author xuyuxiang yubaoshan
     * @date 2022/10/28 9:37
     **/
    void execGenZip(GenBasicIdParam genBasicIdParam, HttpServletResponse response) throws IOException;

    /**
     * 执行代码生成
     *
     * @author xuyuxiang
     * @date 2022/10/28 9:37
     **/
    void execGenPro(GenBasicIdParam genBasicIdParam, HttpServletResponse response) throws IOException;

    /**
     * 预览代码生成
     *
     * @author xuyuxiang
     * @date 2022/10/28 17:08
     **/
    GenBasicPreviewResult previewGen(GenBasicIdParam genBasicIdParam);

    /**
     * 获取移动端模块
     *
     * @author 每天一点
     * @date 2023/7/15 22:28
     **/
    List<GenBasicMobileModuleSelectorResult> mobileModuleSelector();

    /**
     * 获取模块
     *
     * @author yubaoshan
     * @date 2024/9/6 01:24
     **/
    List<GenBasicModuleSelectorResult> moduleSelector();

    /**
     * 代码生成获取所有菜单树包括未授权的
     *
     * @author yubaoshan
     * @date 2024/9/6 01:24
     **/
    List<Tree<String>> menuTreeSelector(GenBasicSelectorMenuParam genBasicSelectorMenuParam);
}
