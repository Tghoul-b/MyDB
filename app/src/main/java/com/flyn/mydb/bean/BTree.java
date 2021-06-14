package com.flyn.mydb.bean;

import com.flyn.mydb.Config.IndexLeafNode;
import com.flyn.mydb.Config.IndexNode;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import com.flyn.mydb.Config.IndexNode;
public class BTree {
	public static  IndexNode root;
	public static  IndexNode buildBT(List<DataEntry> indexList) {
		BTree.root = null;
		for (DataEntry de : indexList) {
			IndexLeafNode node = BTree.find(BTree.root, de.getKey());
			BTree.insert(node, de);
		}
		return BTree.root;
	}

	private static void insert(IndexLeafNode node, DataEntry de) {
		if (node == null) {
			List<DataEntry> data = new LinkedList<DataEntry>();
			data.add(de);

			IndexLeafNode leaf = new IndexLeafNode(true, null, null, data, null);
			BTree.root = leaf;
			return;
		}
		boolean isExist = false;
		for (DataEntry e : node.getData()) {
			if (e.getKey() == de.getKey()) {
				e.getIndex().addAll(de.getIndex());
				isExist = true;
				break;
			}
		}
		if (!isExist) {
			node.getData().add(de);
			Collections.sort(node.getData());
			if (node.getData().get(node.getData().size() - 1).equals(de) && node.getRightNode() == null) {
				IndexNode temp = node.getParent();
				while (temp != null) {
					temp.getChildren().get(temp.getChildren().size() - 1).setKey(de.getKey());
					temp = temp.getParent();
				}
			}
			if (node.getData().size() > IndexNode.CHIlDNUM) {
				BTree.split(node);
			}
		}
	}


	private static void split(IndexNode node) {
		long leftkey;
		if (node.isLeaf()) {
			IndexLeafNode tn = (IndexLeafNode) node;
			int n = tn.getData().size() / 2;
			leftkey = tn.getData().get(n).getKey();
			IndexLeafNode newLeaf = new IndexLeafNode(true, null, null, new LinkedList<DataEntry>(), tn.getRightNode());
			while (n-- > 0) {
				newLeaf.getData().add(tn.getData().remove(IndexNode.CHIlDNUM / 2 + 1));
			}
			tn.setRightNode(newLeaf);
			Collections.sort(newLeaf.getData());
			long rightKey = newLeaf.getData().get(newLeaf.getData().size() - 1).getKey();
			if (node.getParent() == null) {
				IndexNode newParent = new IndexNode(false, null, new LinkedList<NodeEntry>());
				NodeEntry ln = new NodeEntry(leftkey, node);
				NodeEntry rn = new NodeEntry(rightKey, newLeaf);
				newParent.getChildren().add(ln);
				newParent.getChildren().add(rn);
				node.setParent(newParent);
				newLeaf.setParent(newParent);
				BTree.root = newParent;
			} else {
				IndexNode parent = node.getParent();
				for (NodeEntry ne : parent.getChildren()) {
					if (ne.getKey() == rightKey) {
						ne.setKey(leftkey);
						break;
					}
				}
				parent.getChildren().add(new NodeEntry(rightKey, newLeaf));
				newLeaf.setParent(parent);
				Collections.sort(parent.getChildren());
				if (parent.getChildren().size() > IndexNode.CHIlDNUM) {
					BTree.split(parent);
				}
			}
		} else {
			int n = node.getChildren().size() / 2;
			leftkey = node.getChildren().get(n).getKey();
			IndexNode newNode = new IndexNode(false, node.getParent(), new LinkedList<NodeEntry>());
			while (n-- > 0) {
				newNode.getChildren().add(node.getChildren().remove(IndexNode.CHIlDNUM / 2 + 1));
				newNode.getChildren().get(newNode.getChildren().size() - 1).getNode().setParent(newNode);
			}
			long rightKey = newNode.getChildren().get(newNode.getChildren().size() - 1).getKey();
			if (node.getParent() == null) {
				IndexNode newParent = new IndexNode(false, null, new LinkedList<NodeEntry>());
				NodeEntry ln = new NodeEntry(leftkey, node);
				NodeEntry rn = new NodeEntry(rightKey, newNode);
				newParent.getChildren().add(ln);
				newParent.getChildren().add(rn);
				node.setParent(newParent);
				newNode.setParent(newParent);
				BTree.root = newParent;
			} else {
				IndexNode parent = node.getParent();
				for (NodeEntry ne : parent.getChildren()) {
					if (ne.getKey() == rightKey) {
						ne.setKey(leftkey);
						break;
					}
				}
				parent.getChildren().add(new NodeEntry(rightKey, newNode));
				newNode.setParent(parent);
				Collections.sort(parent.getChildren());
				if (parent.getChildren().size() > IndexNode.CHIlDNUM) {
					BTree.split(parent);
				}
			}
		}

	}

	public static IndexLeafNode find(IndexNode node, Long key) {
		if (node == null) {
			return null;
		}

		if (node.isLeaf()) {
			return (IndexLeafNode) node;
		}

		for (NodeEntry e : node.getChildren()) {
			if (key <= e.getKey()) {
				return BTree.find(e.getNode(), key);
			}
		}

		return BTree.find(node.getChildren().get(node.getChildren().size() - 1).getNode(), key);

	}
	public static void display(IndexNode node) {
		IndexLeafNode dis = BTree.find(node, Long.MIN_VALUE);
		while (dis != null) {
			System.out.println(dis.getData());
			dis = dis.getRightNode();
		}
	}
}
