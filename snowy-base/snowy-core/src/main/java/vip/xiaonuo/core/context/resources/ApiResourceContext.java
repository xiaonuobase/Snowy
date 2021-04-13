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
package vip.xiaonuo.core.context.resources;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;

import java.util.Set;

/**
 * 存放本系统所有@RequestMapping的Url
 *
 * @author yubaoshan
 * @date 2020/6/21 17:32
 */
public class ApiResourceContext {

    /**
     * 所有资源的url
     */
    private static final Set<String> API_URLS = CollectionUtil.newHashSet();

    /**
     * 添加一批url
     *
     * @author yubaoshan
     * @date 2020/6/21 17:35
     */
    public static void addBatchUrls(Set<String> urls) {
        if (ObjectUtil.isEmpty(urls)) {
            return;
        }
        API_URLS.addAll(urls);
    }

    /**
     * 添加url
     *
     * @author yubaoshan
     * @date 2020/6/21 17:35
     */
    public static void addUrl(String url) {
        if (ObjectUtil.isEmpty(url)) {
            return;
        }
        API_URLS.add(url);
    }

    /**
     * 删除url
     *
     * @author yubaoshan
     * @date 2020/6/21 17:35
     */
    public static void deleteUrl(String url) {
        if (ObjectUtil.isEmpty(url)) {
            return;
        }
        API_URLS.remove(url);
    }

    /**
     * 获取系统的所有url
     *
     * @author yubaoshan
     * @date 2020/6/21 17:36
     */
    public static Set<String> getApiUrls() {
        return API_URLS;
    }
}
