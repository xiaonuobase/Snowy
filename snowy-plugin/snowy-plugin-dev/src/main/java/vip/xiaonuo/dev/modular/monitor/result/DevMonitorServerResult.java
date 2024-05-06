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
package vip.xiaonuo.dev.modular.monitor.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * 服务器监控结果
 *
 * @author xuyuxiang
 * @date 2022/9/1 16:00
 */
@Getter
@Setter
public class DevMonitorServerResult {

    /* ==============概览数据============ */
    /** CPU信息 */
    @Schema(description = "CPU信息")
    private DevMonitorCpuInfo devMonitorCpuInfo;

    /** 内存信息 */
    @Schema(description = "内存信息")
    private DevMonitorMemoryInfo devMonitorMemoryInfo;

    /** 存储信息 */
    @Schema(description = "存储信息")
    private DevMonitorStorageInfo devMonitorStorageInfo;

    /** 网络信息 */
    @Schema(description = "网络信息")
    private DevMonitorNetworkInfo devMonitorNetworkInfo;

    /* ==============服务器数据============ */
    /** 服务器信息 */
    @Schema(description = "服务器信息")
    private DevMonitorServerInfo devMonitorServerInfo;

    /* ==============JVM数据============ */
    /** JVM信息 */
    @Schema(description = "JVM信息")
    private DevMonitorJvmInfo devMonitorJvmInfo;

    /**
     * CPU信息类
     *
     * @author xuyuxiang
     * @date 2022/7/31 16:42
     */
    @Getter
    @Setter
    public static class DevMonitorCpuInfo {

        /** CPU名称 */
        @Schema(description = "CPU名称")
        private String cpuName;

        /** CPU数量 */
        @Schema(description = "CPU数量")
        private String cpuNum;

        /** CPU物理核心数 */
        @Schema(description = "CPU物理核心数")
        private String cpuPhysicalCoreNum;

        /** CPU逻辑核心数 */
        @Schema(description = "CPU逻辑核心数")
        private String cpuLogicalCoreNum;

        /** CPU系统使用率 */
        @Schema(description = "CPU系统使用率")
        private String cpuSysUseRate;

        /** CPU用户使用率 */
        @Schema(description = "CPU用户使用率")
        private String cpuUserUseRate;

        /** CPU当前总使用率 */
        @Schema(description = "CPU当前总使用率")
        private Double cpuTotalUseRate;

        /** CPU当前等待率 */
        @Schema(description = "CPU当前等待率")
        private String cpuWaitRate;

        /** CPU当前空闲率 */
        @Schema(description = "CPU当前空闲率")
        private String cpuFreeRate;
    }

    /**
     * 内存信息类
     *
     * @author xuyuxiang
     * @date 2022/7/31 16:42
     */
    @Getter
    @Setter
    public static class DevMonitorMemoryInfo {

        /** 内存总量 */
        @Schema(description = "内存总量")
        private String memoryTotal;

        /** 内存已用 */
        @Schema(description = "内存已用")
        private String memoryUsed;

        /** 内存剩余 */
        @Schema(description = "内存剩余")
        private String memoryFree;

        /** 内存使用率 */
        @Schema(description = "内存使用率")
        private Double memoryUseRate;
    }

    /**
     * 存储信息
     *
     * @author xuyuxiang
     * @date 2022/7/31 16:42
     */
    @Getter
    @Setter
    public static class DevMonitorStorageInfo {

        /** 存储总量 */
        @Schema(description = "存储总量")
        private String storageTotal;

        /** 存储已用 */
        @Schema(description = "存储已用")
        private String storageUsed;

        /** 存储剩余 */
        @Schema(description = "存储剩余")
        private String storageFree;

        /** 存储使用率 */
        @Schema(description = "存储使用率")
        private Double storageUseRate;
    }

    /**
     * 网络信息类
     *
     * @author xuyuxiang
     * @date 2022/7/31 16:42
     */
    @Getter
    @Setter
    public static class DevMonitorNetworkInfo {

        /** 上行速率 */
        @Schema(description = "上行速率")
        private String upLinkRate;

        /** 下行速率 */
        @Schema(description = "下行速率")
        private String downLinkRate;

    }

    /**
     * 服务器信息类
     *
     * @author xuyuxiang
     * @date 2022/7/31 16:42
     */
    @Getter
    @Setter
    public static class DevMonitorServerInfo {

        /** 服务器名称 */
        @Schema(description = "服务器名称")
        private String serverName;

        /** 服务器操作系统 */
        @Schema(description = "服务器操作系统")
        private String serverOs;

        /** 服务器IP */
        @Schema(description = "服务器IP")
        private String serverIp;

        /** 服务器架构 */
        @Schema(description = "服务器架构")
        private String serverArchitecture;
    }

    /**
     * JVM信息类
     *
     * @author xuyuxiang
     * @date 2022/7/31 16:42
     */
    @Getter
    @Setter
    public static class DevMonitorJvmInfo {

        /** JVM名称 */
        @Schema(description = "JVM名称")
        private String jvmName;

        /** JVM版本 */
        @Schema(description = "JVM版本")
        private String jvmVersion;

        /** JVM总分配内存 */
        @Schema(description = "JVM总分配内存")
        private String jvmMemoryTotal;

        /** JVM已用内存 */
        @Schema(description = "JVM已用内存")
        private String jvmMemoryUsed;

        /** JVM剩余内存 */
        @Schema(description = "JVM剩余内存")
        private String jvmMemoryFree;

        /** JVM内存使用率 */
        @Schema(description = "JVM内存使用率")
        private Double jvmUseRate;

        /** JVM启动时间 */
        @Schema(description = "JVM启动时间")
        private String jvmStartTime;

        /** JVM运行时长 */
        @Schema(description = "JVM运行时长")
        private String jvmRunTime;

        /** Java版本 */
        @Schema(description = "Java版本")
        private String javaVersion;

        /** Java安装路径 */
        @Schema(description = "Java安装路径")
        private String javaPath;
    }
}
