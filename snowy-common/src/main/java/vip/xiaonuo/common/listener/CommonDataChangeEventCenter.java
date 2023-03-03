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
package vip.xiaonuo.common.listener;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import vip.xiaonuo.common.exception.CommonException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 通用数据变化事件中心 事件发布器
 *
 * @author xuyuxiang
 * @date 2023/3/3 10:14
 **/
public class CommonDataChangeEventCenter {

    // --------- 注册侦听器 

    private static List<CommonDataChangeListener> listenerList = new ArrayList<>();

    /**
     * 获取已注册的所有侦听器
     * @return / 
     */
    public static List<CommonDataChangeListener> getListenerList() {
        return listenerList;
    }

    /**
     * 重置侦听器集合
     * @param listenerList / 
     */
    public static void setListenerList(List<CommonDataChangeListener> listenerList) {
        if(listenerList == null) {
            throw new CommonException("重置的侦听器集合不可以为空");
        }
        CommonDataChangeEventCenter.listenerList = listenerList;
    }

    /**
     * 注册一个侦听器 
     * @param listener / 
     */
    public static void registerListener(CommonDataChangeListener listener) {
        if(listener == null) {
            throw new CommonException("注册的侦听器不可以为空");
        }
        listenerList.add(listener);
    }

    /**
     * 注册一组侦听器 
     * @param listenerList / 
     */
    public static void registerListenerList(List<CommonDataChangeListener> listenerList) {
        if(listenerList == null) {
            throw new CommonException("注册的侦听器不可以为空");
        }
        for (CommonDataChangeListener listener : listenerList) {
            if(listener == null) {
                throw new CommonException("注册的侦听器不可以为空");
            }
        }
        CommonDataChangeEventCenter.listenerList.addAll(listenerList);
    }

    /**
     * 移除一个侦听器 
     * @param listener / 
     */
    public static void removeListener(CommonDataChangeListener listener) {
        listenerList.remove(listener);
    }

    /**
     * 移除指定类型的所有侦听器 
     * @param cls / 
     */
    public static void removeListener(Class<? extends CommonDataChangeListener> cls) {
        ArrayList<CommonDataChangeListener> listenerListCopy = new ArrayList<>(listenerList);
        for (CommonDataChangeListener listener : listenerListCopy) {
            if(cls.isAssignableFrom(listener.getClass())) {
                listenerList.remove(listener);
            }
        }
    }

    /**
     * 清空所有已注册的侦听器 
     */
    public static void clearListener() {
        listenerList.clear();
    }

    /**
     * 判断是否已经注册了指定侦听器 
     * @param listener / 
     * @return / 
     */
    public static boolean hasListener(CommonDataChangeListener listener) {
        return listenerList.contains(listener);
    }

    /**
     * 判断是否已经注册了指定类型的侦听器 
     * @param cls / 
     * @return / 
     */
    public static boolean hasListener(Class<? extends CommonDataChangeListener> cls) {
        for (CommonDataChangeListener listener : listenerList) {
            if(cls.isAssignableFrom(listener.getClass())) {
                return true;
            }
        }
        return false;
    }

    // --------- 事件发布-添加 --------- //

    /**
     * 执行添加事件发布，数据集合
     *
     * @author xuyuxiang
     * @date 2023/3/3 10:22
     **/
    public static void doAddWithData(String dataType, JSONArray jsonArray) {
        for (CommonDataChangeListener listener : listenerList) {
            listener.doAddWithDataIdList(dataType, jsonArray.stream().map(o -> JSONUtil.parseObj(o).getStr("id")).collect(Collectors.toList()));
            listener.doAddWithDataList(dataType, jsonArray);
        }
    }

    // --------- 事件发布-更新 --------- //

    /**
     * 执行更新事件发布，数据集合
     *
     * @author xuyuxiang
     * @date 2023/3/3 10:22
     **/
    public static void doUpdateWithData(String dataType, JSONArray jsonArray) {
        for (CommonDataChangeListener listener : listenerList) {
            listener.doUpdateWithDataIdList(dataType, jsonArray.stream().map(o -> JSONUtil.parseObj(o).getStr("id")).collect(Collectors.toList()));
            listener.doUpdateWithDataList(dataType, jsonArray);
        }
    }

    // --------- 事件发布-删除 --------- //

    /**
     * 执行删除事件发布，ID集合
     *
     * @author xuyuxiang
     * @date 2023/3/3 10:22
     **/
    public static void doDeleteWithDataId(String dataType, List<String> dataIdList) {
        for (CommonDataChangeListener listener : listenerList) {
            listener.doDeleteWithDataIdList(dataType, dataIdList);
        }
    }
}
