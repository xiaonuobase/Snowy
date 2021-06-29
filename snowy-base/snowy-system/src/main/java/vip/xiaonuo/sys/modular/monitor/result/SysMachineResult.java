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
package vip.xiaonuo.sys.modular.monitor.result;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 系统属性结果集
 *
 * @author xuyuxiang
 * @date 2020/6/5 15:02
 */
@Data
public class SysMachineResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 系统信息
     */
    private SysOsInfo sysOsInfo;

    /**
     * Java信息
     */
    private SysJavaInfo sysJavaInfo;

    /**
     * JVM内存信息
     */
    private SysJvmMemInfo sysJvmMemInfo;

    /**
     * 系统信息内部类
     *
     * @author xuyuxiang
     * @date 2020/6/5 15:19
     */
    @NoArgsConstructor
    @Data
    public static class SysOsInfo {

        /**
         * 系统名称
         */
        private String osName;

        /**
         * 系统架构
         */
        private String osArch;

        /**
         * 系统版本
         */
        private String osVersion;

        /**
         * 主机名称
         */
        private String osHostName;

        /**
         * 主机ip地址
         */
        private String osHostAddress;

    }

    /**
     * JVM信息内部类
     *
     * @author xuyuxiang
     * @date 2020/6/5 15:19
     */
    @NoArgsConstructor
    @Data
    public static class SysJavaInfo {

        /**
         * 虚拟机名称
         */
        private String jvmName;

        /**
         * 虚拟机版本
         */
        private String jvmVersion;

        /**
         * 虚拟机供应商
         */
        private String jvmVendor;

        /**
         * java名称
         */
        private String javaName;

        /**
         * java版本
         */
        private String javaVersion;

    }

    /**
     * JVM内存信息
     *
     * @author xuyuxiang
     * @date 2020/6/5 15:19
     */
    @NoArgsConstructor
    @Data
    public static class SysJvmMemInfo {

        /**
         * 最大内存
         */
        private String jvmMaxMemory;

        /**
         * 可用内存
         */
        private String jvmUsableMemory;

        /**
         * 总内存
         */
        private String jvmTotalMemory;

        /**
         * 已使用内存
         */
        private String jvmUsedMemory;

        /**
         * 空余内存
         */
        private String jvmFreeMemory;

        /**
         * 使用率
         */
        private String jvmMemoryUsedRate;
    }
}
