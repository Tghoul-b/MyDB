package com.flyn.mydb.Oper;

import com.flyn.mydb.Config.Table;
import com.flyn.mydb.Service.DBMS;
import com.flyn.mydb.Service.ParseAccount;
import com.flyn.mydb.Util.App;

import java.io.File;

public class Drop implements Operate{
    private String account=null;
    private String name=null;

    public Drop(String account) {
        this.account = account;
    }

    @Override
    public void start() throws Exception {
        switch (ParseAccount.parseDrop(this.account)) {// 解析drop语句，若返回1则是删除关系表，若返回2则是删除索引。
            case 1:
                parseDropTable();// 删除关系表
                break;
        }
    }
    public void parseDropTable() throws Exception {
        this.name = ParseAccount.parseTableName(this.account);//解析drop-table语句
        this.dropTable();//执行drop-table操作
        OperUtil.perpetuateDatabase(DBMS.dataDictionary, DBMS.dataDictionary.getConfigFile());//持久化数据库结构
    }
    private void dropTable() throws Exception{

        File file = new File(DBMS.currentPath + File.separator + this.name);
        DBMS.dataDictionary.getTables().remove(this.name);//从关系表集合中删除此表
        if (file.exists()) {
            Table table = OperUtil.loadTable(this.name);
            File dataFile = table.getFile();
            File configFile = table.getConfigFile();
            if (dataFile.delete() && configFile.delete()) {//删除关系表数据文件和数据字典文件
                String d="delete table "+this.name+" successfully";
                App.getManage().outTextView(d);
                if (DBMS.loadedTables.containsKey(this.name)) {
                    DBMS.loadedTables.remove(this.name, table);
                }
                return;
            }
            throw new RuntimeException("其他程序正在占用，删除文件失败！");

        } else {
            throw new RuntimeException("table "+this.name+" not exist");
        }
    }
    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
