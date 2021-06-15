package com.flyn.mydb.Oper;

import com.flyn.mydb.Config.Field;
import com.flyn.mydb.Config.Table;
import com.flyn.mydb.Service.DBMS;
import com.flyn.mydb.Service.ParseAccount;
import com.flyn.mydb.Util.App;
import com.flyn.mydb.bean.BTree;
import com.flyn.mydb.bean.DataEntry;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.util.List;

public class Create implements Operate {
    private Table table=null;//创建的关系表
    private String account=null;//创建的语句
    private String name=null;//待创建的关系名或数据库名
    private String indexName=null;//索引名
    private String fieldName=null;//待建立索引的属性名
    public Create(String account) {
        this.account = account;
    }
    @Override
    public void start() throws Exception {
        switch (ParseAccount.parseCreate(this)) {// 解析create语句，若返回1则是创建数据库操作，若返回2则是创建关系操作,返回3则为创建索引。
            case 1:
                parseCreateDatabase();// 创建数据库
                break;
            case 2:
                parseCreateTable();// 创建关系表
                break;
            case 3:
                parseCreateIndex();// 创建索引
                break;
            default:
                throw  new RuntimeException("invalid create Instruction");
        }
    }

    private void parseCreateDatabase() throws Exception {
        createDatabase();// 创建数据库
    }

    private void parseCreateTable() throws Exception {
        this.table = ParseAccount.parseTable(this.account, this.name);// 解析create-table语句，获取Table实例
        this.createTable();// 创建关系
        OperUtil.perpetuateTable(this.table, this.table.getConfigFile());//持久化关系表结构
        OperUtil.perpetuateDatabase(DBMS.dataDictionary, DBMS.dataDictionary.getConfigFile());//持久化数据库结构
    }

    private void parseCreateIndex() throws Exception {
        ParseAccount.parseIndex(this);//解析create-index语句
        this.table = OperUtil.loadTable(this.name);//加载待创建索引的关系表
        this.createIndex();//创建索引
    }

    /**
     * 创建数据库！！！
     *
     * @param
     */
    private void createDatabase() {
        File dir = new File(DBMS.ROOTPATH + File.separator + this.name);
        if (dir.exists()) {
            throw new RuntimeException("database already existed");
        }
        try {
            File file = new File(DBMS.ROOTPATH + File.separator + "database.config");
            if (!file.exists())
                file.createNewFile();
            FileWriter fw=new FileWriter(file,true);
            BufferedWriter bw=new BufferedWriter(fw);
            bw.write(this.name+"\n");
            bw.close();
            fw.close();
            App.getManage().outTextView("Successfully create database "+this.name);
        }catch (Exception e){
            throw new RuntimeException("I/O error");
        }

        dir.mkdirs();//创建这个文件夹
    }

    /**
     * 创建一个table，将配置信息写入configFile，将表添加至数据字典！！！
     */
    private void createTable() throws Exception {
        File file=this.table.getFile();
        file.createNewFile();
        DBMS.dataDictionary.getTables().add(this.table.getTableName());
        DBMS.loadedTables.put(this.table.getTableName(), this.table);
        String success="successfully create a table";
        App.getManage().outTextView(success);
    }

    /**
     * 创建B+树索引
     * @param
     * @throws Exception
     */
    private void createIndex() throws Exception {
        int column = -1;
        String type = null;
        Field field = null;
        for (Field field1 : this.getTable().getAttributes()) {// 找到待创建索引的属性所在列
            if (field1.getFieldName().equals(this.getFieldName())) {
                column = field1.getColumn();
                type = field1.getType();
                field = field1;
            }
        }
        if (column == -1 || field == null) {
            throw new RuntimeException("no such feild in table");
        }
        List<DataEntry> indexList = OperUtil.getIndex(this.getTable().getFile(), column, type);// 获取指定列值和所在行

        field.setIndex(true);// 有索引
        field.setIndexRoot(BTree.buildBT(indexList));// 创建B+树并获取根节点
        DBMS.indexEntry.put(this.indexName, field);//将索引名和索引属性添加到map，方便删除索引
        OperUtil.perpetuateIndex(DBMS.indexEntry,new File(DBMS.currentPath+File.separator+"index.config"));
        BTree.display(BTree.root);//显示创建的B+树
        App.getManage().outTextView("create index successfully");

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }
}
