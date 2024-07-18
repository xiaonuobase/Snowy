package vip.xiaonuo.sys.modular.index.result;

import lombok.Getter;
import lombok.Setter;

/**
 * 运维一览数据
 *
 * @author yubaoshan
 * @date 2024/7/18 17:35
 */
@Getter
@Setter
public class SysOpDataCountResult {

    /** 定时任务数量 */
    private Long jobCount;

    /** 系统字典数量 */
    private Long sysDictCount;

    /** 业务字典数量 */
    private Long bizDictCount;

    /** B端用户在线数量 */
    private Long backUserSessionCount;

    /** C端用户在线数量 */
    private Long clientUserSessionCount;

    /** 三方用户数量 */
    private Long thirdUserCount;
}
