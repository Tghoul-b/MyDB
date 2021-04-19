package com.flyn.mydb.dic;

import com.flyn.mydb.Config.Table;
import com.flyn.mydb.Service.DBMS;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DataDictionary implements Serializable {
    private static final long serialVersionUID = -4590671050174187697L;
    private File configFile;
    private String path;//数据库路径
    private String name;//数据库名称
    private List<String>  tables;//数据库下的表名

    public DataDictionary(String name) throws Exception {
        this.name = name;
        this.path = DBMS.ROOTPATH + File.separator + this.name;//路径+分隔符+文件名
        this.configFile=new File(this.name+".config");
        this.tables=new ArrayList<>();
    }

    public File getConfigFile() {
        return configFile;
    }

    public void setConfigFile(File configFile) {
        this.configFile = configFile;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getTables() {
        return tables;
    }

    public void setTables(List<String> tables) {
        this.tables = tables;
    }
}
