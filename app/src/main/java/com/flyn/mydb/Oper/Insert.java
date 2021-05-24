package com.flyn.mydb.Oper;

import com.flyn.mydb.Config.Table;

public class Insert implements Operate{
    private Table table=null;
    private String account=null;
    private String values=null;
    private String tableName=null;
    public Insert(String account) {
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

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getValues() {
        return values;
    }

    public void setValues(String values) {
        this.values = values;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
