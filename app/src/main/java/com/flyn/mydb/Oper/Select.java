package com.flyn.mydb.Oper;

import com.flyn.mydb.Config.ConditionalExpression;
import com.flyn.mydb.Config.DisplayField;
import com.flyn.mydb.Oper.Operate;
import com.flyn.mydb.Service.ParseAccount;
import com.flyn.mydb.bean.Node;
import com.flyn.mydb.bean.Tree;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Select implements Operate {
    private boolean seeAll=false;
    private int numberFeld=0;//显示的属性数量
    private List<DisplayField> attributes=null;//显示的属性集合
    private int numberTable=0;//查询的关系表数量
    private List<String> tableName=null;//查询的表的名字
    private boolean existWhereCondition=true;//是否存在where条件
    private List<List<ConditionalExpression>> conditions=null;//where条件集合
    private String account=null;//select语句

    public Select(String account) {
        this.account = account;
    }

    @Override
    public void start() throws Exception {
        long start=System.currentTimeMillis();
        ParseAccount.parseSelect(this, this.account);
        List<Node> rootNodes= Tree.buildTree(this);
        for(Node node:rootNodes){
            Tree.traverseTree(node);
        }
        Set<String> displayAccounts=Tree.getDisplayAccounts(rootNodes);//从文件中读出所有记录，包括字段名
        Tree.display(this,displayAccounts,rootNodes.get(0).getSaveFields());
        OperUtil.garbageClear(rootNodes);
    }

    public boolean isSeeAll() {
        return seeAll;
    }

    public void setSeeAll(boolean seeAll) {
        this.seeAll = seeAll;
    }

    public int getNumberFeld() {
        return numberFeld;
    }

    public void setNumberFeld(int numberFeld) {
        this.numberFeld = numberFeld;
    }

    public List<DisplayField> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<DisplayField> attributes) {
        this.attributes = attributes;
    }

    public int getNumberTable() {
        return numberTable;
    }

    public void setNumberTable(int numberTable) {
        this.numberTable = numberTable;
    }

    public List<String> getTableName() {
        return tableName;
    }

    public void setTableName(List<String> tableName) {
        this.tableName = tableName;
    }

    public boolean isExistWhereCondition() {
        return existWhereCondition;
    }

    public void setExistWhereCondition(boolean existWhereCondition) {
        this.existWhereCondition = existWhereCondition;
    }

    public List<List<ConditionalExpression>> getConditions() {
        return conditions;
    }

    public void setConditions(List<List<ConditionalExpression>> conditions) {
        this.conditions = conditions;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
}
