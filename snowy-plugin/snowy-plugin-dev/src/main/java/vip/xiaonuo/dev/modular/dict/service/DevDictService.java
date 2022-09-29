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
package vip.xiaonuo.dev.modular.dict.service;

import cn.hutool.core.lang.tree.Tree;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import vip.xiaonuo.dev.modular.dict.entity.DevDict;
import vip.xiaonuo.dev.modular.dict.param.*;

import java.util.List;

/**
 * 字典Service接口
 *
 * @author xuyuxiang
 * @date 2022/4/22 10:41
 **/
public interface DevDictService extends IService<DevDict> {

    /**
     * 获取字典分页
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:08
     */
    Page<DevDict> page(DevDictPageParam devDictPageParam);

    /**
     * 获取字典列表
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:08
     */
    List<DevDict> list(DevDictListParam devDictListParam);

    /**
     * 获取字典树
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:08
     */
    List<Tree<String>> tree(DevDictTreeParam devDictTreeParam);

    /**
     * 添加字典
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:48
     */
    void add(DevDictAddParam devDictAddParam);

    /**
     * 编辑字典
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:13
     */
    void edit(DevDictEditParam devDictEditParam);

    /**
     * 删除字典
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:18
     */
    void delete(List<DevDictIdParam> devDictIdParamList);

    /**
     * 获取字典详情
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:18
     */
    DevDict detail(DevDictIdParam devDictIdParam);

    /**
     * 获取字典详情
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:18
     */
    DevDict queryEntity(String id);
}
