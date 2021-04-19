package com.flyn.mydb.bean;

import com.flyn.mydb.Config.IndexNode;

import java.util.Objects;

/**
 * B+树内部节点
 */
public class NodeEntry implements Comparable<NodeEntry> {
    private long key;//数值
    private IndexNode node;

    public NodeEntry(long key, IndexNode node) {
        super();
        this.key = key;
        this.node = node;
    }

    public long getKey() {
        return key;
    }

    public void setKey(long key) {
        this.key = key;
    }

    public IndexNode getNode() {
        return node;
    }

    public void setNode(IndexNode node) {
        this.node = node;
    }

    @Override
    public int compareTo(NodeEntry o) {
        return this.getKey()>o.getKey()?1:-1;
    }

    @Override
    public String toString() {
        return "NodeEntry{" +
                "key=" + key +
                ", node=" + node +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NodeEntry)) return false;
        NodeEntry nodeEntry = (NodeEntry) o;
        return key == nodeEntry.key &&
                Objects.equals(node, nodeEntry.node);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, node);
    }

}
