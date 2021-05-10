package com.flyn.mydb.bean;

import com.flyn.mydb.Config.DisplayField;

import java.io.File;
import java.util.Set;

public class Node {
    protected boolean isLeafNode;//是否为叶子结点
    protected File file;//临时中间文件
    protected boolean hasFather=false;//是否有父节点
    protected int accountNumbers=0;//记录条数
    private Node father=null;
    private int numberField;//保留的字段个数
    private Set<DisplayField>  saveFields=null;//保留的字段集合

    public Node(boolean isLeafNode, File file, boolean hasFather, Node father, int numberField, Set<DisplayField> saveFields) {
        this.isLeafNode = isLeafNode;
        this.file = file;
        this.hasFather = hasFather;
        this.father = father;
        this.numberField = numberField;
        this.saveFields = saveFields;
    }
    public  Node(){
        super();
    }
    @Override
    public String toString() {
        return "Node{" +
                "isLeafNode=" + isLeafNode +
                ", file=" + file +
                ", hasFather=" + hasFather +
                ", accountNumbers=" + accountNumbers +
                ", father=" + father +
                ", numberField=" + numberField +
                ", saveFields=" + saveFields +
                '}';
    }

    public boolean isLeafNode() {
        return isLeafNode;
    }

    public void setLeafNode(boolean leafNode) {
        isLeafNode = leafNode;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public boolean isHasFather() {
        return hasFather;
    }

    public void setHasFather(boolean hasFather) {
        this.hasFather = hasFather;
    }

    public int getAccountNumbers() {
        return accountNumbers;
    }

    public void setAccountNumbers(int accountNumbers) {
        this.accountNumbers = accountNumbers;
    }

    public Node getFather() {
        return father;
    }

    public void setFather(Node father) {
        this.father = father;
    }

    public int getNumberField() {
        return numberField;
    }

    public void setNumberField(int numberField) {
        this.numberField = numberField;
    }

    public Set<DisplayField> getSaveFields() {
        return saveFields;
    }

    public void setSaveFields(Set<DisplayField> saveFields) {
        this.saveFields = saveFields;
    }
}
