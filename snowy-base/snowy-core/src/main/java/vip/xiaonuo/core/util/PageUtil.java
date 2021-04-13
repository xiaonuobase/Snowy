/*
Copyright [2020] [https://www.xiaonuo.vip]

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Snowy采用APACHE LICENSE 2.0开源协议，您在使用过程中，需要注意以下几点：

1.请不要删除和修改根目录下的LICENSE文件。
2.请不要删除和修改Snowy源码头部的版权声明。
3.请保留源码和相关描述文件的项目出处，作者声明等。
4.分发源码时候，请注明软件出处 https://gitee.com/xiaonuobase/snowy
5.在修改包名，模块名称，项目代码等时，请注明软件出处 https://gitee.com/xiaonuobase/snowy
6.若您的项目无法满足以上几点，可申请商业授权，获取Snowy商业授权许可，请在官网购买授权，地址为 https://www.xiaonuo.vip
 */
package vip.xiaonuo.core.util;

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
