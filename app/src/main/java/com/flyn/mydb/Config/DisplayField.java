package com.flyn.mydb.Config;

import java.io.Serializable;
import java.util.Objects;

public class DisplayField extends Field {
    private static final long serialVersionUID = 2760633084059905299L;

    private String name;// 全名
    private String table;// 所属关系表名
    public DisplayField(){
        super();
    }
    public DisplayField(Field field, String name, String table) {
        super(field);
        this.name = name;
        this.table = table;
    }

    public DisplayField(DisplayField df) {
        super(df.fieldName, df.type, df.length, df.column, df.isNull, df.isPrimary, df.isIndex, df.indexRoot);
        this.name = df.name;
        this.table = df.table;
    }

    public DisplayField(String fieldName, String type, int length, int column, boolean isNull, boolean isPrimary,
                        boolean isIndex, IndexNode indexRoot, String name, String table) {
        super(fieldName, type, length, column, isNull, isPrimary, isIndex, indexRoot);
        this.name = name;
        this.table = table;
    }

    @Override
    public String toString() {
        return "DisplayField{" +
                "name='" + name + '\'' +
                ", table='" + table + '\'' +
                '}'+
                super.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DisplayField)) return false;
        if (!super.equals(o)) return false;
        DisplayField that = (DisplayField) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(table, that.table);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, table);
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }
}
