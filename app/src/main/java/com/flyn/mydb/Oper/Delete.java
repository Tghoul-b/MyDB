package com.flyn.mydb.Oper;

import com.flyn.mydb.Config.Table;

public class Delete extends Oper implements Operate {
    private Table table=null;
    private String account=null;

    public Delete(String account) {
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
}
