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
package vip.xiaonuo.dev.api;

import cn.hutool.json.JSONObject;

import java.util.List;

/**
 * 日志API
 *
 * @author xuyuxiang
 * @date 2022/9/2 15:59
 */
public interface DevLogApi {

    /**
     * 记录登录日志
     *
     * @author xuyuxiang
     * @date 2022/9/2 16:03
     */
    void executeLoginLog(String userName);

    /**
     * 记录登出日志
     *
     * @author xuyuxiang
     * @date 2022/9/2 16:03
     */
    void executeLogoutLog(String userName);

    /**
     * 获取当前用户的访问日志列表
     *
     * @author xuyuxiang
     * @date 2022/9/4 15:12
     */
    List<JSONObject> currentUserVisLogList();

    /**
     * 获取当前用户的操作日志列表
     *
     * @author xuyuxiang
     * @date 2022/9/4 15:12
     */
    List<JSONObject> currentUserOpLogList();
}
