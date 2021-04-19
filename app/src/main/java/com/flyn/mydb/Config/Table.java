package com.flyn.mydb.Config;

import java.io.File;
import java.io.Serializable;
import java.util.List;

public class Table implements Serializable {
    private static final long serialVersionUID=-530965201790985804L;
    private String tableName;//表名
    private File file;//表数据存储文件
    private File configFile;//表配置文件
    private List<Field> attributes;//字段属性集合

    public Table(String tableName, File file, File configFile, List<Field> attributes) {
        this.tableName = tableName;
        this.file = file;
        this.configFile = configFile;
        this.attributes = attributes;
    }


    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public File getConfigFile() {
        return configFile;
    }

    public void setConfigFile(File configFile) {
        this.configFile = configFile;
    }

    public List<Field> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Field> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        return "Table{" +
                "tableName='" + tableName + '\'' +
                ", file=" + file +
                ", configFile=" + configFile +
                ", attributes=" + attributes +
                '}';
    }
}
