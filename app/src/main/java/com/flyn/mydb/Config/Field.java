package com.flyn.mydb.Config;

import java.io.Serializable;
import java.util.Objects;

public class Field implements Serializable {
    protected static final long serialVersionUID = -1607589038195638512L;//方便反序列化
    protected String fieldName;//字段名
    protected String type;//字段类型
    protected int length;//字段长度
    protected int column;//字段所在列
    public boolean isNull=true;//字段是否为空,默认为空
    public boolean isPrimary=false;//字段是否可以为主键,默认不是
    public transient boolean isIndex = false;// 是否是索引字段,默认不参与序列化的过程
    public transient IndexNode indexRoot = null;// 索引树的根节点

    public Field() {
    }

    public Field(String fieldName, String type, int length, int column, boolean isNull, boolean isPrimary, boolean isIndex, IndexNode indexRoot) {
        this.fieldName = fieldName;
        this.type = type;
        this.length = length;
        this.column = column;
        this.isNull = isNull;
        this.isPrimary = isPrimary;
        this.isIndex = isIndex;
        this.indexRoot = indexRoot;
    }

    public Field(Field field) {   //这个是相当于一个Field字段进行赋值
        this.fieldName = field.fieldName;
        this.type = field.type;
        this.length = field.length;
        this.column = field.column;
        this.isNull = field.isNull;
        this.isPrimary = field.isPrimary;
        this.isIndex = field.isIndex;
        this.indexRoot = field.indexRoot;
    }

    @Override
    public String toString() {
        return "Field{" +
                "fieldName='" + fieldName + '\'' +
                ", type='" + type + '\'' +
                ", length=" + length +
                ", column=" + column +
                ", isNull=" + isNull +
                ", isPrimary=" + isPrimary +
                ", isIndex=" + isIndex +
                ", indexRoot=" + indexRoot +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Field)) return false;
        Field field = (Field) o;
        return length == field.length &&
                column == field.column &&
                isNull == field.isNull &&
                isPrimary == field.isPrimary &&
                isIndex == field.isIndex &&
                Objects.equals(fieldName, field.fieldName) &&
                Objects.equals(type, field.type) &&
                Objects.equals(indexRoot, field.indexRoot);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fieldName, type, length, column, isNull, isPrimary, isIndex, indexRoot);
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public boolean isNull() {
        return isNull;
    }

    public void setNull(boolean aNull) {
        isNull = aNull;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public void setPrimary(boolean primary) {
        isPrimary = primary;
    }

    public boolean isIndex() {
        return isIndex;
    }

    public void setIndex(boolean index) {
        isIndex = index;
    }

    public IndexNode getIndexRoot() {
        return indexRoot;
    }

    public void setIndexRoot(IndexNode indexRoot) {
        this.indexRoot = indexRoot;
    }
}
