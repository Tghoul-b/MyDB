package com.flyn.mydb.Oper;

import com.flyn.mydb.Config.ConditionalExpression;
import com.flyn.mydb.Config.Field;
import com.flyn.mydb.Config.IOUtil;
import com.flyn.mydb.Config.Table;
import com.flyn.mydb.Service.Check;
import com.flyn.mydb.Service.DBMS;
import com.flyn.mydb.Service.ParseAccount;
import com.flyn.mydb.Util.App;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;

public class Update extends Oper implements Operate {
    private List<String[]> set;// 要更改的属性和值
    private Table table = null;
    private String account = null;

    public Update(String account) {
        this.account = account;
    }

    @Override
    public void start() throws Exception {
        ParseAccount.parseUpdate(this,account);
        this.table=OperUtil.loadTable(this.getTableName());
        if(this.update()){
            App.getManage().outTextView("update successfully");
        }else{
            App.getManage().outTextView("update failed");
        }
    }
    private boolean update(){
        String parentPath = this.table.getFile().getParent();
        File temp = new File(parentPath + File.separator + "temp");
        if (temp.exists()) {
            temp.delete();
        }
        this.table.getFile().renameTo(temp);
        this.table.setFile(new File(parentPath + File.separator + this.table.getTableName()));
        List<String> list = IOUtil.readFile(temp);//将文件中的数据读到内存


        PrintWriter pw = null;
        boolean updated=false;
        try {
            pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(this.table.getFile()), "GBK"));
            String[] fields = null;
            for (String line : list) {
                if (Check.isSatisfiedOper(line, this)) {//如果满足更新条件
                    updated=true;
                    String newLine = "";//新字符串
                    fields = line.split(",");//字段数组
                    boolean isUpdatePrimary = false;
                    for (String[] arr : this.getSet()) {
                        for (Field field : DBMS.loadedTables.get(this.getTableName()).getAttributes()) {
                            if (field.getFieldName().equals(arr[0]) && field.isPrimary) {
                                isUpdatePrimary = true;
                                break;
                            }
                        }
                    }
                    for (int i = 0; i < fields.length; i++) {//遍历一条记录中的每个字段
                        for (String[] kvs: this.getSet()) {//遍历所有要更新的属性
                            if (DBMS.loadedTables.get(this.getTableName()).getAttributes().get(i).getFieldName().equals(kvs[0])){//更新待更新属性对应的字段
                                fields[i] = kvs[1];
                            }
                        }
                        newLine = newLine + fields[i] + ",";
                    }
                    if (newLine.endsWith(",")) {
                        newLine = newLine.substring(0, newLine.length()-1);
                    }
                    //		System.out.println(newLine);
                    if (Check.checkValues(temp, this.getTable(), newLine, isUpdatePrimary)) {
                        pw.println(newLine);
                    } else {
                        pw.println(line);
                    }
                } else {
                    pw.println(line);//不满足更新条件直接写出到文件
                }
            }
        } catch (Exception e) {
           e.printStackTrace();
        } finally {
            if (pw != null) {
                pw.close();
            }
            temp.delete();//删除临时文件
        }
        return updated;
    }
    public List<String[]> getSet() {
        return set;
    }

    public void setSet(List<String[]> set) {
        this.set = set;
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

    public Update(String tableName, boolean existWhereCondition, List<List<ConditionalExpression>> conditions) {
        super(tableName, existWhereCondition, conditions);
    }

    public Update() {
        super();
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public String getTableName() {
        return super.getTableName();
    }

    @Override
    public void setTableName(String tableName) {
        super.setTableName(tableName);
    }

    @Override
    public boolean isExistWhereCondition() {
        return super.isExistWhereCondition();
    }

    @Override
    public void setExistWhereCondition(boolean existWhereCondition) {
        super.setExistWhereCondition(existWhereCondition);
    }

    @Override
    public List<List<ConditionalExpression>> getConditions() {
        return super.getConditions();
    }

    @Override
    public void setConditions(List<List<ConditionalExpression>> conditions) {
        super.setConditions(conditions);
    }
}
