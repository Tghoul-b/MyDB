package com.flyn.mydb.Oper;

import com.flyn.mydb.Config.Table;

import java.util.List;

public class Update implements Operate {
    private List<String[]> set;// 要更改的属性和值
    private Table table = null;
    private String account = null;

    public Update(String account) {
        this.account = account;
    }

    @Override
    public void start() throws Exception {

    }

    public List<String[]> getSet() {
        return set;
    }

    public void setSet(List<String[]> set) {
        this.set = set;
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
