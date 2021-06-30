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
package vip.xiaonuo.sys.core.cache;

import cn.hutool.cache.impl.TimedCache;
import vip.xiaonuo.core.cache.base.AbstractMemoryCacheOperator;

import java.util.Map;

/**
 * mapping是映射，指业务id和业务名称的映射，或字典code和字典值的映射
 * <p>
 * mapping的含义：
 * 指对接口响应字段转化（或完善）过程
 * <p>
 * 为什么要映射：
 * 一般在查询类的方法，响应结果如果需要返回详细的一些名称信息
 * 则需要通过left join关联对应明细表或字典表，通过id或者code查到对应的中文名称，这样做效率不高
 * <p>
 * 结论：
 * 利用缓存，将常用的字典或者业务id映射，保存起来
 * 一方面保证了查询速度，一方面简化了代码开发，不用写一些left join之类的sql
 *
 * @author xuyuxiang
 * @date 2020/7/24 11:59
 */
public class MappingCache extends AbstractMemoryCacheOperator<Map<String, Object>> {

    /**
     * 缓存的前缀标识
     */
    public static final String MAPPING_CACHE_PREFIX = "MAPPINGS_";

    public MappingCache(TimedCache<String, Map<String, Object>> timedCache) {
        super(timedCache);
    }

    @Override
    public String getCommonKeyPrefix() {
        return MAPPING_CACHE_PREFIX;
    }

}
