package com.flyn.mydb.Oper;

import com.flyn.mydb.Config.ConditionalExpression;
import com.flyn.mydb.Config.IndexNode;
import com.flyn.mydb.Config.Table;
import com.flyn.mydb.Service.DBMS;
import com.flyn.mydb.Config.IndexLeafNode;
import com.flyn.mydb.bean.BTree;
import com.flyn.mydb.bean.DataEntry;
import com.flyn.mydb.bean.Node;
import com.flyn.mydb.bean.Tree;
import com.flyn.mydb.dic.DataDictionary;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    public static void garbageClear(Collection<Node> rootNodes) {
        for (Node node : rootNodes) {
            node.getFile().delete();
        }
        Tree.leaves = null;
        Tree.inners = null;
    }
    public static List<DataEntry> getIndex(File file, int column, String type) {
        List<DataEntry> indexList = new ArrayList<DataEntry>();
        DataEntry de = null;

        BufferedReader br = null;
        String line = null;
        int index = 0;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
            long key;
            if ("int".equalsIgnoreCase(type)) {
                while ((line = br.readLine()) != null) {
                    key = Long.parseLong(line.split(",")[column]);
                    de = new DataEntry(key, new HashSet<Integer>());
                    de.getIndex().add(index++);
                    indexList.add(de);
                }
            } else if ("char".equalsIgnoreCase(type)||"date".equalsIgnoreCase(type)) {
                while ((line = br.readLine()) != null) {
                    key = line.split(",")[column].hashCode();
                    de = new DataEntry(key, new HashSet<Integer>());
                    de.getIndex().add(index++);
                    indexList.add(de);
                }
            } else {
                throw new RuntimeException("no such type feild");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return indexList;
    }
    public static Set<Integer> getIndexOfBT(ConditionalExpression c) {

        Set<Integer> indexs = null;
        String cons = c.isLeftConstant() ? c.getLeft() : c.getRight();//获取待比较常量
        String type = c.isLeftConstant() ? c.getRightType() : c.getLeftType();
        long constant = 0;
        if ("int".equalsIgnoreCase(type)) {
            constant = Integer.parseInt(cons);
        } else if ("char".equalsIgnoreCase(type)||"date".equalsIgnoreCase(type)) {
            constant = cons.hashCode();
        } else {
            throw new RuntimeException("no such type");
        }
        IndexNode root = c.isLeftIsIndex() ? c.getLeftRoot() : c.getRightRoot();//获取索引树的根节点
        IndexLeafNode now = BTree.find(root, constant);//查找要比较的常量所在的叶节点

        if ("<".equals(c.getOper()) && c.isLeftIsIndex() || ">".equals(c.getOper()) && c.isRightIsIndex()) {
            indexs = new HashSet<Integer>();
            IndexLeafNode minNode = BTree.find(c.getLeftRoot(), Long.MIN_VALUE);//查找最小关键字所在节点
            while (minNode != null && !minNode.equals(now)) {//获取当前叶结点所存的数据（即记录所在行）
                for (DataEntry de : minNode.getData()) {//遍历当前叶结点下的数据集合
                    indexs.addAll(de.getIndex());
                }
                minNode = minNode.getRightNode();
            }
            for (DataEntry de : now.getData()) {//遍历当前叶结点下的数据集合
                if (de.getKey() >= constant) {//当前关键字大于等于待比较常量，则不添加直接跳出循环
                    break;
                }
                indexs.addAll(de.getIndex());
            }
        } else if ("<=".equals(c.getOper()) && c.isLeftIsIndex() || ">=".equals(c.getOper()) && c.isRightIsIndex()) {
            indexs = new HashSet<Integer>();
            IndexLeafNode minNode = BTree.find(c.getLeftRoot(), Long.MIN_VALUE);//查找最小关键字所在节点
            while (minNode != null && !minNode.equals(now)) {//获取当前叶结点所存的数据（即记录所在行）
                for (DataEntry de : minNode.getData()) {//遍历当前叶结点下的数据集合
                    indexs.addAll(de.getIndex());
                }
                minNode = minNode.getRightNode();
            }
            for (DataEntry de : now.getData()) {//遍历当前叶结点下的数据集合
                if (de.getKey() > constant) {//当前关键字大于待比较常量，则不添加直接跳出循环
                    break;
                }
                indexs.addAll(de.getIndex());
            }
        } else if ("=".equals(c.getOper()) && (c.isLeftIsIndex() || c.isRightIsIndex())) {
            indexs = new HashSet<Integer>();
            for (DataEntry de : now.getData()) {
                if (de.getKey() == constant) {
                    indexs.addAll(de.getIndex());
                }
            }
        } else if (">".equals(c.getOper())  && c.isLeftIsIndex() || "<".equals(c.getOper()) && c.isRightIsIndex()) {
            indexs = new HashSet<Integer>();
            for (DataEntry de : now.getData()) {//遍历当前叶结点下的数据集合
                if (de.getKey() <= constant) {//当前关键字小于等于待比较常量，则不添加继续下一次循环
                    continue;
                }
                indexs.addAll(de.getIndex());
            }
            now = now.getRightNode();
            while (now != null) {//获取当前叶结点所存的数据（即记录所在行）
                for (DataEntry de : now.getData()) {//遍历当前叶结点下的数据集合
                    indexs.addAll(de.getIndex());
                }
                now = now.getRightNode();
            }
        } else if (">=".equals(c.getOper())  && c.isLeftIsIndex() || "<=".equals(c.getOper()) && c.isRightIsIndex()) {
            indexs = new HashSet<Integer>();
            for (DataEntry de : now.getData()) {//遍历当前叶结点下的数据集合
                if (de.getKey() < constant) {//当前关键字小于待比较常量，则不添加继续下一次循环
                    continue;
                }
                indexs.addAll(de.getIndex());
            }
            now = now.getRightNode();
            while (now != null) {//获取当前叶结点所存的数据（即记录所在行）
                for (DataEntry de : now.getData()) {//遍历当前叶结点下的数据集合
                    indexs.addAll(de.getIndex());
                }
                now = now.getRightNode();
            }
        }

        return indexs;
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
    public static  int getLen(String s){
        byte[] b = null;
        try {
            b = s.getBytes("gb2312");
        } catch (
            UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return  b.length;
    }
}
