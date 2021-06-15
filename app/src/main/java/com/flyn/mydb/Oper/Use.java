package com.flyn.mydb.Oper;

import com.flyn.mydb.Service.DBMS;
import com.flyn.mydb.Service.ParseAccount;
import com.flyn.mydb.Util.App;
import com.flyn.mydb.dic.DataDictionary;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Use implements Operate{
    private String account = null;
    private String databaseName = null;
    public static List<String> databases;
    public Use(String account){
        this.account=account;
    }
    @Override
    public void start() throws Exception {
        this.databaseName = ParseAccount.parseUse(this.account);
        this.use();
    }
    private void use(){
        OperUtil.persistence();
        DBMS.loadedTables=new HashMap<>();
        List<String> ds=DBMS.databases;
        File databaseDir = new File(DBMS.ROOTPATH, this.databaseName);
        if (!databaseDir.exists()) {
            App.getManage().outTextView("database not exist");
            return;
        }
        DBMS.currentPath = DBMS.ROOTPATH + File.separator + this.databaseName;

        File file = new File(DBMS.ROOTPATH + File.separator + this.databaseName + ".config");
        ObjectInputStream ois = null;//利用对象输入流读取配置文件
        ObjectOutputStream oos = null;
        try {
            DBMS.dataDictionary = null;
            if (file.exists()) {//如果数据库的结构定义文件存在，则读入内存
                FileInputStream fileInputStream=new FileInputStream(file);
                if(file.length()==0){
                    fileInputStream.close();
                    DBMS.dataDictionary = new DataDictionary(this.databaseName);
                    App.getManage().outTextView("database changed");
                    return ;
                }
                ois = new ObjectInputStream(fileInputStream);
                DBMS.dataDictionary = (DataDictionary) ois.readObject();
            } else {//如果不存在则创建一个数据库结构定义文件
                file.createNewFile();//创建数据库结构定义文件
                DBMS.dataDictionary = new DataDictionary(this.databaseName);//创建数据库结构对象
            }
            App.getManage().outTextView("database changed");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        } finally {
            try {
                if (ois != null) {
                    ois.close();
                }
                if (oos != null) {
                    oos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public static List<String> getDatabases() {
        return databases;
    }

    public static void setDatabases(List<String> databases) {
        Use.databases = databases;
    }
}
