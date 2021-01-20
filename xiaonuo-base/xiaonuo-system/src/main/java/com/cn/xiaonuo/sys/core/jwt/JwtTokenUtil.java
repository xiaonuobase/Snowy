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

XiaoNuo采用APACHE LICENSE 2.0开源协议，您在使用过程中，需要注意以下几点：

1.请不要删除和修改根目录下的LICENSE文件。
2.请不要删除和修改XiaoNuo源码头部的版权声明。
3.请保留源码和相关描述文件的项目出处，作者声明等。
4.分发源码时候，请注明软件出处 https://gitee.com/xiaonuobase/xiaonuo-vue
5.在修改包名，模块名称，项目代码等时，请注明软件出处 https://gitee.com/xiaonuobase/xiaonuo-vue
6.若您的项目无法满足以上几点，可申请商业授权，获取XiaoNuo商业授权许可，请在官网购买授权，地址为 https://www.xiaonuo.vip
 */
package com.cn.xiaonuo.sys.core.jwt;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.cn.xiaonuo.core.context.constant.ConstantContextHolder;
import io.jsonwebtoken.*;

import java.util.Date;

/**
 * JwtToken工具类
 *
 * @author xuyuxiang
 * @date 2020/3/12 17:39
 */
public class JwtTokenUtil {

    /**
     * 生成token
     *
     * @author xuyuxiang
     * @date 2020/3/12 17:52
     */
    public static String generateToken(JwtPayLoad jwtPayLoad) {

        DateTime expirationDate = DateUtil.offsetMillisecond(new Date(),
                Convert.toInt(ConstantContextHolder.getTokenExpireSec()) * 1000);
        return Jwts.builder()
                .setClaims(BeanUtil.beanToMap(jwtPayLoad))
                .setSubject(jwtPayLoad.getUserId().toString())
                .setIssuedAt(new Date())
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, ConstantContextHolder.getJwtSecret())
                .compact();
    }

    /**
     * 根据token获取Claims
     *
     * @author xuyuxiang
     * @date 2020/3/13 10:29
     */
    private static Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(ConstantContextHolder.getJwtSecret())
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 获取JwtPayLoad部分
     *
     * @author xuyuxiang
     * @date 2020/3/12 17:53
     */
    public static JwtPayLoad getJwtPayLoad(String token) {
        Claims claims = getClaimsFromToken(token);
        return BeanUtil.mapToBean(claims, JwtPayLoad.class, false);
    }

    /**
     * 校验token是否正确
     *
     * @author xuyuxiang
     * @date 2020/3/13 10:36
     */
    public static Boolean checkToken(String token) {
        try {
            getClaimsFromToken(token);
            return true;
        } catch (JwtException jwtException) {
            return false;
        }
    }

    /**
     * 校验token是否失效
     *
     * @author xuyuxiang
     * @date 2020/3/13 10:30
     */
    public static Boolean isTokenExpired(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            final Date expiration = claims.getExpiration();
            return expiration.before(new Date());
        } catch (ExpiredJwtException expiredJwtException) {
            return true;
        }
    }
}
