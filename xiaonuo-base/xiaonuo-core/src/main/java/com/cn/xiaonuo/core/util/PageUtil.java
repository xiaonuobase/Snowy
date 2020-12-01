package com.cn.xiaonuo.core.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * 分页工具类针对hutool分页的扩展
 *
 * @author xuyuxiang
 * @date 2020/9/19 10:30
 **/
public class PageUtil<T> extends cn.hutool.core.util.PageUtil{

    /**
     * 逻辑分页
     *
     * @author xuyuxiang
     * @date 2020/9/19 10:36
     **/
    public static <T> List<T> page(Page<T> page, List<T> list) {
        setFirstPageNo(1);
        int start = getStart(Convert.toInt(page.getCurrent()), Convert.toInt(page.getSize()));
        int end = getEnd(Convert.toInt(page.getCurrent()), Convert.toInt(page.getSize()));
        if(start > list.size()) {
            return CollectionUtil.newArrayList();
        }else if(start > end) {
            return CollectionUtil.newArrayList();
        } else if(end > list.size()) {
            return list.subList(start, list.size());
        } else {
            return list.subList(start, end);
        }
    }
}
