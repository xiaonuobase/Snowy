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
package vip.xiaonuo.dev.modular.monitor.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.system.JvmInfo;
import cn.hutool.system.OsInfo;
import cn.hutool.system.RuntimeInfo;
import cn.hutool.system.SystemUtil;
import org.springframework.stereotype.Service;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.FileSystem;
import oshi.software.os.OperatingSystem;
import oshi.util.Util;
import vip.xiaonuo.common.util.CommonNetWorkInfoUtil;
import vip.xiaonuo.dev.modular.monitor.result.DevMonitorServerResult;
import vip.xiaonuo.dev.modular.monitor.service.DevMonitorService;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 监控Service接口实现类
 *
 * @author xuyuxiang
 * @date 2022/9/1 15:59
 */
@Service
public class DevMonitorServiceImpl implements DevMonitorService {

    @Override
    public DevMonitorServerResult serverInfo() {
        DevMonitorServerResult devMonitorServerResult = new DevMonitorServerResult();
        SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hal = si.getHardware();
        CentralProcessor cpu = hal.getProcessor();

        // CPU信息
        DevMonitorServerResult.DevMonitorCpuInfo devMonitorCpuInfo = new DevMonitorServerResult.DevMonitorCpuInfo();
        devMonitorCpuInfo.setCpuName(StrUtil.trim(cpu.getProcessorIdentifier().getName()));
        devMonitorCpuInfo.setCpuNum(cpu.getPhysicalPackageCount() + "颗物理CPU");
        devMonitorCpuInfo.setCpuPhysicalCoreNum(cpu.getPhysicalProcessorCount() + "个物理核心");
        devMonitorCpuInfo.setCpuLogicalCoreNum(cpu.getLogicalProcessorCount() + "个逻辑核心");
        long[] prevTicks = cpu.getSystemCpuLoadTicks();
        Util.sleep(1000);
        long[] ticks = cpu.getSystemCpuLoadTicks();
        long nice = ticks[CentralProcessor.TickType.NICE.getIndex()]
                - prevTicks[CentralProcessor.TickType.NICE.getIndex()];
        long irq = ticks[CentralProcessor.TickType.IRQ.getIndex()]
                - prevTicks[CentralProcessor.TickType.IRQ.getIndex()];
        long softIrq = ticks[CentralProcessor.TickType.SOFTIRQ.getIndex()]
                - prevTicks[CentralProcessor.TickType.SOFTIRQ.getIndex()];
        long steal = ticks[CentralProcessor.TickType.STEAL.getIndex()]
                - prevTicks[CentralProcessor.TickType.STEAL.getIndex()];
        long sys = ticks[CentralProcessor.TickType.SYSTEM.getIndex()]
                - prevTicks[CentralProcessor.TickType.SYSTEM.getIndex()];
        long user = ticks[CentralProcessor.TickType.USER.getIndex()]
                - prevTicks[CentralProcessor.TickType.USER.getIndex()];
        long ioWait = ticks[CentralProcessor.TickType.IOWAIT.getIndex()]
                - prevTicks[CentralProcessor.TickType.IOWAIT.getIndex()];
        long idle = ticks[CentralProcessor.TickType.IDLE.getIndex()]
                - prevTicks[CentralProcessor.TickType.IDLE.getIndex()];
        long totalCpu = user + nice + sys + idle + ioWait + irq + softIrq + steal;
        devMonitorCpuInfo.setCpuSysUseRate(NumberUtil.div(NumberUtil.mul(sys, 100), totalCpu, 2) + "%");
        devMonitorCpuInfo.setCpuUserUseRate(NumberUtil.div(NumberUtil.mul(user, 100), totalCpu, 2) + "%");
        devMonitorCpuInfo.setCpuTotalUseRate(NumberUtil.div(NumberUtil.mul(NumberUtil.add(sys, user), 100), totalCpu, 2));
        devMonitorCpuInfo.setCpuWaitRate(NumberUtil.div(NumberUtil.mul(ioWait, 100), totalCpu, 2) + "%");
        devMonitorCpuInfo.setCpuFreeRate(NumberUtil.div(NumberUtil.mul(idle, 100), totalCpu, 2) + "%");
        devMonitorServerResult.setDevMonitorCpuInfo(devMonitorCpuInfo);

        // 内存信息
        GlobalMemory memory = hal.getMemory();
        DevMonitorServerResult.DevMonitorMemoryInfo devMonitorMemoryInfo = new DevMonitorServerResult.DevMonitorMemoryInfo();
        long used = memory.getTotal() - memory.getAvailable();
        devMonitorMemoryInfo.setMemoryTotal(FileUtil.readableFileSize(memory.getTotal()));
        devMonitorMemoryInfo.setMemoryUsed(FileUtil.readableFileSize(used));
        devMonitorMemoryInfo.setMemoryFree(FileUtil.readableFileSize(memory.getAvailable()));
        devMonitorMemoryInfo.setMemoryUseRate(NumberUtil.mul(NumberUtil.div(used, memory.getTotal(), 4), 100));
        devMonitorServerResult.setDevMonitorMemoryInfo(devMonitorMemoryInfo);

        // 存储信息
        DevMonitorServerResult.DevMonitorStorageInfo devMonitorStorageInfo = new DevMonitorServerResult.DevMonitorStorageInfo();
        OperatingSystem operatingSystem = si.getOperatingSystem();
        FileSystem fileSystem = operatingSystem.getFileSystem();
        AtomicLong storageTotal = new AtomicLong();
        AtomicLong storageUsed = new AtomicLong();
        AtomicLong storageFree = new AtomicLong();
        fileSystem.getFileStores().forEach(osFileStore -> {
            long totalSpace = osFileStore.getTotalSpace();
            long usableSpace = osFileStore.getUsableSpace();
            long freeSpace = osFileStore.getFreeSpace();
            long usedSpace = totalSpace - usableSpace;
            storageTotal.addAndGet(totalSpace);
            storageUsed.addAndGet(usedSpace);
            storageFree.addAndGet(freeSpace);
        });
        devMonitorStorageInfo.setStorageTotal(FileUtil.readableFileSize(storageTotal.get()));
        devMonitorStorageInfo.setStorageUsed(FileUtil.readableFileSize(storageUsed.get()));
        devMonitorStorageInfo.setStorageFree(FileUtil.readableFileSize(storageFree.get()));
        devMonitorStorageInfo.setStorageUseRate(NumberUtil.mul(NumberUtil.div(storageUsed.doubleValue(), storageTotal.doubleValue(), 4), 100));
        devMonitorServerResult.setDevMonitorStorageInfo(devMonitorStorageInfo);

        // 服务器信息
        OsInfo osInfo = SystemUtil.getOsInfo();
        DevMonitorServerResult.DevMonitorServerInfo devMonitorServerInfo = new DevMonitorServerResult.DevMonitorServerInfo();
        devMonitorServerInfo.setServerName(NetUtil.getLocalHostName());
        devMonitorServerInfo.setServerOs(osInfo.getName());
        devMonitorServerInfo.setServerIp(NetUtil.getLocalhostStr());
        devMonitorServerInfo.setServerArchitecture(osInfo.getArch());
        devMonitorServerResult.setDevMonitorServerInfo(devMonitorServerInfo);

        // JVM信息
        DevMonitorServerResult.DevMonitorJvmInfo devMonitorJvmInfo = new DevMonitorServerResult.DevMonitorJvmInfo();
        RuntimeInfo runtimeInfo = SystemUtil.getRuntimeInfo();
        JvmInfo jvmInfo = SystemUtil.getJvmInfo();
        devMonitorJvmInfo.setJvmName(jvmInfo.getName());
        devMonitorJvmInfo.setJvmVersion(jvmInfo.getVersion());
        long totalMemory = runtimeInfo.getTotalMemory();
        devMonitorJvmInfo.setJvmMemoryTotal(FileUtil.readableFileSize(totalMemory));
        devMonitorJvmInfo.setJvmMemoryFree(FileUtil.readableFileSize(runtimeInfo.getFreeMemory()));
        long jvmMemoryUsed = NumberUtil.sub(new BigDecimal(runtimeInfo
                .getTotalMemory()), new BigDecimal(runtimeInfo.getFreeMemory())).longValue();
        devMonitorJvmInfo.setJvmMemoryUsed(FileUtil.readableFileSize(jvmMemoryUsed));
        double jvmUseRate = NumberUtil.mul(NumberUtil.div(jvmMemoryUsed, totalMemory, 4), 100);
        devMonitorJvmInfo.setJvmUseRate(jvmUseRate);
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        DateTime startTime = DateUtil.date(runtimeMXBean.getStartTime());
        devMonitorJvmInfo.setJvmStartTime(DateUtil.formatDateTime(startTime));
        devMonitorJvmInfo.setJvmRunTime(DateUtil.formatBetween(startTime, DateTime.now()));
        devMonitorJvmInfo.setJavaVersion(SystemUtil.get("java.version", false));
        devMonitorJvmInfo.setJavaPath(SystemUtil.get("java.home", false));
        devMonitorServerResult.setDevMonitorJvmInfo(devMonitorJvmInfo);
        return devMonitorServerResult;
    }

    /**
     * 获取服务器网络情况
     *
     * @author diantu
     * @date 2023/7/27
     */
    @Override
    public DevMonitorServerResult networkInfo(){
        DevMonitorServerResult devMonitorServerResult = new DevMonitorServerResult();
        // 网络信息
        DevMonitorServerResult.DevMonitorNetworkInfo devMonitorNetworkInfo = new DevMonitorServerResult.DevMonitorNetworkInfo();
        Map<String, String> networkUpRate = CommonNetWorkInfoUtil.getNetworkUpRate();
        devMonitorNetworkInfo.setUpLinkRate(networkUpRate.get("UP"));
        devMonitorNetworkInfo.setDownLinkRate(networkUpRate.get("DOWN"));
        devMonitorServerResult.setDevMonitorNetworkInfo(devMonitorNetworkInfo);
        return devMonitorServerResult;
    }
}
