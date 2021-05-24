package com.flyn.mydb.Oper;

import com.flyn.mydb.Config.Field;
import com.flyn.mydb.Config.Table;

import java.util.List;

public class Alter implements Operate {
    private Table table=null;
    private String tableName;
    private boolean isAdd;//true表示add操作,false表示drop操作
    private List<Field> fields=null;
    private String account=null;
    public Alter(String account) {
        this.account = account;
    }
    @Override
    public void start() throws Exception {

    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public boolean isAdd() {
        return isAdd;
    }

    public void setAdd(boolean add) {
        isAdd = add;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
}
