package com.cn.xiaonuo.core.file.common.enums;

/**
 * 桶的权限策略枚举
 *
 * @author xuyuxiang
 * @date 2020-05-23-3:03 下午
 */
public enum BucketAuthEnum {

    /**
     * 私有的（仅有 owner 可以读写）
     */
    PRIVATE,

    /**
     * 公有读，私有写（ owner 可以读写， 其他客户可以读）
     */
    PUBLIC_READ,

    /**
     * 公共读写（即所有人都可以读写，慎用）
     */
    PUBLIC_READ_WRITE

}
