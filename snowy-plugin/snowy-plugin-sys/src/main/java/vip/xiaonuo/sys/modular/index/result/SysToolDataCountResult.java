package vip.xiaonuo.sys.modular.index.result;

import lombok.Getter;
import lombok.Setter;

/**
 * 基础工具数据
 *
 * @author yubaoshan
 * @date 2024/7/18 17:35
 */
@Getter
@Setter
public class SysToolDataCountResult {

    /** 文件数量 */
    private Long fileCount;

    /** 短信数量 */
    private Long smsCount;

    /** 邮件数量 */
    private Long emailCount;

    /** 站内信数量 */
    private Long messageCount;
}
