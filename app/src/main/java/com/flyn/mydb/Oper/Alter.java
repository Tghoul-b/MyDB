package com.flyn.mydb.Oper;

import com.flyn.mydb.Config.Field;
import com.flyn.mydb.Config.Table;
import com.flyn.mydb.Service.Check;
import com.flyn.mydb.Service.ParseAccount;
import com.flyn.mydb.Util.App;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;

public class Alter implements Operate {
    private Table table=null;
    private String tableName;
    private boolean isAdd;//true表示add操作,false表示drop操作
    private List<Field> fields=null;
    private String account=null;
    public Alter(String account) {
        this.account = account;
    }
    @Override
    public void start() throws Exception {
        ParseAccount.parseAlter(this);
        this.table=OperUtil.loadTable(tableName);
        ParseAccount.parseAlterAddAndDrop(this);
        if(this.isAdd){
            this.alterAdd();
            App.getManage().outTextView("add field(s) successfully");
        }
        else{
            this.alterDrop();
            App.getManage().outTextView("delete field(s) successfully");
        }
        OperUtil.perpetuateTable(this.table, this.table.getConfigFile());
    }
    private void alterAdd() throws Exception {
        Table table = this.getTable();

        if (Check.isRepeated(table.getAttributes(), this.getFields())) {
            throw new RuntimeException("添加属性与已有属性名重复！");
        }
        BufferedReader br = null;
        PrintWriter pw = null;
        String parentPath = table.getFile().getParent();
        File temp = new File(parentPath + File.separator + "temp");
        if (temp.exists()) {
            temp.delete();
        }
        table.getFile().renameTo(temp);
        table.setFile(new File(parentPath + File.separator + table.getTableName()));

        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(temp), "UTF-8"));
            pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(table.getFile()), "UTF-8"));
            String line = null;
            String add = "";
            for (int i = 0; i < this.getFields().size(); i++) {
                add += ",null";
            }
            while ((line = br.readLine()) != null) {
                line += add;
                pw.println(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        } finally {
            try {
                if (br != null) {
                    br.close();
                    temp.delete();
                }
                if (pw != null) {
                    pw.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        table.getAttributes().addAll(this.getFields());
    }

    private void alterDrop() {
        Table table = this.getTable();

        BufferedReader br = null;
        PrintWriter pw = null;
        String parentPath = table.getFile().getParent();
        File temp = new File(parentPath + File.separator + "temp");
        if (temp.exists()) {
            temp.delete();
        }
        table.getFile().renameTo(temp);
        table.setFile(new File(parentPath + File.separator + table.getTableName()));
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(temp), "UTF-8"));
            pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(table.getFile()), "UTF-8"), true);

            String line = null;
            String[] fields = null;

            while ((line = br.readLine()) != null) {
                String newLine = "";
                fields = line.split(",");

                for (int i = 0; i < fields.length; i++) {// 遍历一条记录中的每个字段
                    boolean flag = true;
                    for (Field field : this.getFields()) {// 遍历所有要删除的属性
                        if (field.getColumn() == i) {// 删除待删除属性对应的字段
                            flag = false;
                            break;
                        }
                    }
                    if (flag == true) {
                        newLine = newLine + fields[i] + ",";
                    }
                }
                if (newLine.length() != 0) {
                    if (newLine.endsWith(",")) {
                        newLine = newLine.substring(0, newLine.length() - 1);
                    }
                    pw.println(newLine);
                }
            }
            for (Field field : this.getFields()) {
                table.getAttributes().remove(field);
            }
            int column = 0;
            for (Field field : table.getAttributes()) {
                field.setColumn(column);
                column += 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        } finally {
            try {
                if (br != null) {
                    br.close();
                    temp.delete();
                }
                if (pw != null) {
                    pw.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public boolean isAdd() {
        return isAdd;
    }

    public void setAdd(boolean add) {
        isAdd = add;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
}
