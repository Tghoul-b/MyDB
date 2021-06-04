package com.flyn.mydb.Config;

import com.flyn.mydb.bean.NodeEntry;

import java.util.List;

/**
 * B+树的结点配置
 */
public class IndexNode {
    public static final  int CHIlDNUM=6;//最大孩子数量
    private boolean isLeaf;//是否是叶节点
    private IndexNode parent;//父节点
    private List<NodeEntry> children;//孩子结点

    public IndexNode(boolean isLeaf, IndexNode parent, List<NodeEntry> children) {
        super();
        this.isLeaf = isLeaf;
        this.parent = parent;
        this.children = children;
    }
    public IndexNode() {
        super();
    }
    public boolean isLeaf() {
        return isLeaf;
    }

    public void setLeaf(boolean leaf) {
        isLeaf = leaf;
    }

    public IndexNode getParent() {
        return parent;
    }

    public void setParent(IndexNode parent) {
        this.parent = parent;
    }

    public List<NodeEntry> getChildren() {
        return children;
    }

    public void setChildren(List<NodeEntry> children) {
        this.children = children;
    }

}
