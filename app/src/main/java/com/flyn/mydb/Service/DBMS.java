package com.flyn.mydb.Service;
import com.flyn.mydb.Config.Field;
import com.flyn.mydb.Config.Table;
import com.flyn.mydb.Util.App;
import com.flyn.mydb.dic.DataDictionary;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBMS implements Serializable {
    public static String ROOTPATH;
    public static String currentPath;
    public static DataDictionary dataDictionary=null;//用来记录各数据库的存储结构
    public static Map<String, Table> loadedTables;//加载的关系表的集合
    public static Map<String, Field> indexEntry;//索引集合
    public static List<String> databases;//数据库名称
    public static  int type;
    public DBMS(){
        DBMS.loadedTables = new HashMap<String, Table>();
        DBMS.indexEntry = new HashMap<String, Field>();
        DBMS.currentPath = ROOTPATH;
    }
    public  void start(String s) throws Exception{
        ROOTPATH=App.getApplication().getExternalFilesDir(null).toString();
        //DBMS.currentPath = ROOTPATH;
        new Handler(s).start();
    }
}
