package com.flyn.mydb.Oper;

import com.flyn.mydb.Config.Table;
import com.flyn.mydb.Service.DBMS;
import com.flyn.mydb.dic.DataDictionary;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class OperUtil {
    /**
     * 将配置信息写入配置文件
     * @param table
     * @param file
     * @throws Exception
     */
    public static void perpetuateTable(Table table, File file)throws  Exception{
        file.delete();
        ObjectOutputStream oos=new ObjectOutputStream(new FileOutputStream(file));
        oos.writeObject(table);//将关系表结构信息持久化
        oos.close();
    }
    public static void perpetuateDatabase(DataDictionary dd, File file) throws Exception {
        file.delete();
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
        oos.writeObject(dd);
        oos.close();
    }
    public static void persistence() {
        ObjectOutputStream oos = null;
        try {
            /*
             * 持久化数据库结构定义对象
             */
            if (DBMS.dataDictionary != null) {
                oos = new ObjectOutputStream(new FileOutputStream(DBMS.dataDictionary.getConfigFile()));
                oos.writeObject(DBMS.dataDictionary);
            }
            /*
             * 持久化关系表结构定义对象
             */
            if (DBMS.loadedTables != null) {
                for (Table table : DBMS.loadedTables.values()) {
                    table.getConfigFile().delete();
                    oos = new ObjectOutputStream(new FileOutputStream(table.getConfigFile()));
                    oos.writeObject(table);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("关系表持久化失败！");
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    public static Table loadTable(String tableName) throws Exception {
        File file = new File(DBMS.currentPath + File.separator + tableName + ".config");
        if (!file.exists()) {
            throw new RuntimeException("Invalid！table "+tableName+" not exist");
        }
        if (DBMS.loadedTables.containsKey(tableName)) {//如果已加载关系表集合中含有此关系表则返回此关系表
            return DBMS.loadedTables.get(tableName);
        }
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
        Table table = (Table) ois.readObject();
        if (ois != null) {
            ois.close();
        }
        if (table != null) {
            DBMS.loadedTables.put(tableName, table);// 将加载的关系表添加至已加载关系表map
            return table;
        } else {
            throw new RuntimeException("read table Error");
        }
    }
}
