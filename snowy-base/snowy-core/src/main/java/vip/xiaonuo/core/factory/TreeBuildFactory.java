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
package vip.xiaonuo.core.factory;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import lombok.Data;
import vip.xiaonuo.core.pojo.base.node.BaseTreeNode;

import java.util.ArrayList;
import java.util.List;

/**
 * 默认递归工具类，用于遍历有父子关系的节点，例如菜单树，字典树等等
 *
 * @author xuyuxiang
 * @date 2020/4/5 14:17
 */
@Data
public class TreeBuildFactory<T extends BaseTreeNode> {

    /**
     * 顶级节点的父节点id(默认0)
     */
    private Long rootParentId = 0L;

    /**
     * 树节点构造
     *
     * @author xuyuxiang
     * @date 2020/4/5 14:09
     */
    public List<T> doTreeBuild(List<T> nodes) {

        //具体构建的过程
        List<T> buildComplete = this.executeBuilding(nodes);

        //构建之后的处理工作
        return this.afterBuild(buildComplete);
    }

    /**
     * 查询子节点集合
     *
     * @author xuyuxiang
     * @date 2020/4/5 14:10
     */
    private void buildChildNodes(List<T> totalNodes, T node, List<T> childNodeLists) {
        if (ObjectUtil.hasEmpty(totalNodes, node)) {
            return;
        }
        List<T> nodeSubLists = this.getSubChildLevelOne(totalNodes, node);
        if (ObjectUtil.isNotEmpty(nodeSubLists)) {
            nodeSubLists.forEach(t -> this.buildChildNodes(totalNodes, t, CollectionUtil.newArrayList()));
        }
//        childNodeLists.addAll(nodeSubLists);
        node.setChildren(nodeSubLists);
    }

    /**
     * 获取子一级节点的集合
     *
     * @author xuyuxiang
     * @date 2020/4/5 14:12
     */
    private List<T> getSubChildLevelOne(List<T> list, T node) {
        List<T> nodeList = CollectionUtil.newArrayList();
        if (ObjectUtil.isNotEmpty(list)) {
            list.forEach(t -> {
                if (t.getPid().equals(node.getId())) {
                    nodeList.add(t);
                }
            });
        }
        return nodeList;
    }

    /**
     * 执行构造
     *
     * @author xuyuxiang
     * @date 2020/4/5 14:13
     */
    private List<T> executeBuilding(List<T> nodes) {
        List<T> parentNodes = afterBuild(nodes);
        parentNodes.forEach(t -> this.buildChildNodes(nodes, t, CollectionUtil.newArrayList()));
        return parentNodes;
    }

    /**
     * 构造之后
     *
     * @author xuyuxiang
     * @date 2020/4/5 14:13
     */
    private List<T> afterBuild(List<T> nodes) {
        //去掉所有的二级节点
        ArrayList<T> results = CollectionUtil.newArrayList();
        nodes.forEach(t -> {
            if (rootParentId.equals(t.getPid())) {
                results.add(t);
            }
        });
        return results;
    }
}
