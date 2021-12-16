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
package vip.xiaonuo.core.cryptogram;

/**
 * 基于SM2的秘钥对
 * （本项目中配置的，自己使用可根据自己的需求进行更换）
 *
 * @author yubaoshan
 */
public class keypair {

    /**
     * 公钥
     */
    public static String PUBLIC_KEY = "04298364ec840088475eae92a591e01284d1abefcda348b47eb324bb521bb03b0b2a5bc393f6b71dabb8f15c99a0050818b56b23f31743b93df9cf8948f15ddb54";

    /**
     * 私钥
     */
    public static String PRIVATE_KEY = "3037723d47292171677ec8bd7dc9af696c7472bc5f251b2cec07e65fdef22e25";

    /**
     * SM4的对称秘钥（生产环境需要改成自己使用的）
     * 16 进制字符串，要求为 128 比特
     */
    public static String KEY = "0123456789abcdeffedcba9876543210";

}
