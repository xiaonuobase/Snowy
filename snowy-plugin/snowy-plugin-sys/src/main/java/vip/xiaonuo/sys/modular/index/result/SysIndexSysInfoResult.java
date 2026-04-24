package vip.xiaonuo.sys.modular.index.result;

import lombok.Getter;
import lombok.Setter;

/**
 * 系统运行信息
 *
 * @author yubaoshan
 * @date 2026/4/21
 */
@Getter
@Setter
public class SysIndexSysInfoResult {

    /** Java版本 */
    private String javaVersion;

    /** 操作系统 */
    private String osName;

    /** 服务器IP */
    private String serverIp;

    /** 运行时长 */
    private String runTime;
}
