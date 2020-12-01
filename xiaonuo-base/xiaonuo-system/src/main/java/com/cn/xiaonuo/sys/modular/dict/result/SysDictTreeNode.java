package com.cn.xiaonuo.sys.modular.dict.result;

import com.cn.xiaonuo.core.pojo.base.node.BaseTreeNode;
import lombok.Data;

import java.util.List;

/**
 * 系统字典树
 *
 * @author xuyuxiang
 * @date 2020/3/11 12:08
 */
@Data
public class SysDictTreeNode implements BaseTreeNode {

    /**
     * id
     */
    private Long id;

    /**
     * 父id
     */
    private Long pid;

    /**
     * 编码-对应字典值的编码
     */
    private String code;

    /**
     * 名称-对应字典值的value
     */
    private String name;

    /**
     * 子节点集合
     */
    private List<SysDictTreeNode> children;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public Long getPid() {
        return this.pid;
    }

    @Override
    public void setChildren(List children) {
        this.children = children;
    }
}
