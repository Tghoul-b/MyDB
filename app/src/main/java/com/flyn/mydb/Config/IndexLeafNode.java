package com.flyn.mydb.Config;

import com.flyn.mydb.bean.DataEntry;
import com.flyn.mydb.bean.NodeEntry;

import java.util.List;
/**
 * B+��Ҷ�ӽ��
 */
public class IndexLeafNode extends IndexNode {
	private List<DataEntry> data;
	private IndexLeafNode rightNode;

	public IndexLeafNode(boolean isLeaf,IndexNode parent, List<NodeEntry> childs, List<DataEntry> data,
						 IndexLeafNode rightNode) {
		super(isLeaf, parent, childs);
		this.data = data;
		this.rightNode = rightNode;
	}

	public IndexLeafNode() {
		super();
	}

	public List<DataEntry> getData() {
		return data;
	}

	public void setData(List<DataEntry> data) {
		this.data = data;
	}

	public IndexLeafNode getRightNode() {
		return rightNode;
	}

	public void setRightNode(IndexLeafNode rightNode) {
		this.rightNode = rightNode;
	}

	@Override
	public boolean isLeaf() {
		return super.isLeaf();
	}

	@Override
	public void setLeaf(boolean isLeaf) {
		super.setLeaf(isLeaf);
	}

	@Override
	public IndexNode getParent() {
		return super.getParent();
	}

	@Override
	public void setParent(IndexNode parent) {
		super.setParent(parent);
	}

	@Override
	public List<NodeEntry> getChildren() {
		return super.getChildren();
	}

	@Override
	public void setChildren(List<NodeEntry> childs) {
		super.setChildren(childs);
	}

}
