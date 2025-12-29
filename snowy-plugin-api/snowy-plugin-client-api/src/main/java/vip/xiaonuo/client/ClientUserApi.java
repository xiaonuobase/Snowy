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
package vip.xiaonuo.client;

import cn.hutool.json.JSONObject;

import java.util.List;

/**
 * 用户Api
 *
 * @author xuyuxiang
 * @date 2022/6/6 11:33
 **/
public interface ClientUserApi {

    /**
     * 根据用户id获取用户对象，没有则返回null
     *
     * @author xuyuxiang
     * @date 2022/6/20 18:19
     **/
    JSONObject getUserByIdWithoutException(String userId);

    /**
     * 根据用户id获取用户对象列表，没有的则为空，都没有则返回空集合
     *
     * @author xuyuxiang
     * @date 2022/6/20 18:19
     **/
    List<JSONObject> getUserListByIdListWithoutException(List<String> userIdList);

    /**
     * 根据用户id获取用户对象，没有则抛出异常
     *
     * @author xuyuxiang
     * @date 2022/6/20 18:19
     **/
    JSONObject getUserByIdWithException(String userId);

    /**
     * 根据用户id获取用户对象列表，只要有一个没有则抛出异常
     *
     * @author xuyuxiang
     * @date 2022/6/20 18:19
     **/
    List<JSONObject> getUserListByIdWithException(List<String> userIdList);

    /**
     * 获取用户列表（排除当前用户）
     *
     * @author chengchuanyao
     * @date 2024/7/19 9:54
     */
    List<JSONObject> listUserWithoutCurrent();

    /**
     * 获取用户扩展信息，没有则创建
     *
     * @author xuyuxiang
     * @date 2022/7/8 9:26
     **/
    JSONObject getOrCreateClientUserExt(String userId);
}
