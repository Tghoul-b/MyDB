package com.flyn.mydb.bean;

import com.flyn.mydb.Config.ConditionalExpression;
import com.flyn.mydb.Config.DisplayField;

import java.io.File;
import java.util.List;
import java.util.Set;

public class LeafNode extends Node {
    private String tableName=null;
    private boolean existCondition=false;//存在条件
    private List<ConditionalExpression>  conditions=null;

    public LeafNode(boolean isLeafNode, File file, boolean hasFather, int accountNumbers, Node father, int numberField, Set<DisplayField> saveFields,
                    String tableName, boolean existCondition,List<ConditionalExpression> conditions) {
        super(isLeafNode, file, hasFather, father, numberField, saveFields);
        this.tableName = tableName;
        this.existCondition = existCondition;
        this.conditions=conditions;
    }
    public LeafNode(){
        super();
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public boolean isExistCondition() {
        return existCondition;
    }

    public void setExistCondition(boolean existCondition) {
        this.existCondition = existCondition;
    }

    public List<ConditionalExpression> getConditions() {
        return conditions;
    }

    public void setConditions(List<ConditionalExpression> conditions) {
        this.conditions = conditions;
    }

    public LeafNode(boolean isLeafNode, File file, boolean hasFather, int accountNumbers, Node father, int numberField, Set<DisplayField> saveFields) {
        super(isLeafNode, file, hasFather, father, numberField, saveFields);
    }

    @Override
    public String toString() {
        return "LeafNode [tableName=" + tableName + ", existCondition=" + existCondition + ", conditions=" + conditions
                + ", toString()=" + super.toString() + "]";
    }

    @Override
    public boolean isLeafNode() {
        return super.isLeafNode();
    }

    @Override
    public void setLeafNode(boolean leafNode) {
        super.setLeafNode(leafNode);
    }

    @Override
    public File getFile() {
        return super.getFile();
    }

    @Override
    public void setFile(File file) {
        super.setFile(file);
    }

    @Override
    public boolean isHasFather() {
        return super.isHasFather();
    }

    @Override
    public void setHasFather(boolean hasFather) {
        super.setHasFather(hasFather);
    }

    @Override
    public int getAccountNumbers() {
        return super.getAccountNumbers();
    }

    @Override
    public void setAccountNumbers(int accountNumbers) {
        super.setAccountNumbers(accountNumbers);
    }

    @Override
    public Node getFather() {
        return super.getFather();
    }

    @Override
    public void setFather(Node father) {
        super.setFather(father);
    }

    @Override
    public int getNumberField() {
        return super.getNumberField();
    }

    @Override
    public void setNumberField(int numberField) {
        super.setNumberField(numberField);
    }

    @Override
    public Set<DisplayField> getSaveFields() {
        return super.getSaveFields();
    }

    @Override
    public void setSaveFields(Set<DisplayField> saveFields) {
        super.setSaveFields(saveFields);
    }
}
