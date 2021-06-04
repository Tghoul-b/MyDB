package com.flyn.mydb.Oper;

import com.flyn.mydb.Config.Table;
import com.flyn.mydb.Service.Check;
import com.flyn.mydb.Service.ParseAccount;
import com.flyn.mydb.Util.App;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class Delete extends Oper implements Operate {
    private Table table=null;
    private String account=null;

    public Delete(String account) {
        this.account = account;
    }
    @Override
    public void start() throws Exception {
        ParseAccount.parseDelete(this);
        this.table=OperUtil.loadTable(this.getTableName());
        if(this.delete()){
            App.getManage().outTextView("delete record(s) successfully!");
        }
    }
    private boolean delete(){
        Table table=this.getTable();
        BufferedReader br = null;
        PrintWriter pw = null;
        String parentPath = table.getFile().getParent();
        File temp = new File(parentPath + File.separator + "temp");
        if (temp.exists()) {
            temp.delete();
        }
        if (!table.getFile().renameTo(temp)) {
            return false;
        }

        table.setFile(new File(parentPath + File.separator + table.getTableName()));

        try {
            table.getFile().createNewFile();

            br = new BufferedReader(new InputStreamReader(new FileInputStream(temp), "GBK"));
            pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(table.getFile())));
            String line = null;

            while ((line = br.readLine()) != null) {
                if (!Check.isSatisfiedOper(line, this)) {//保留不满足删除条件的记录
                    pw.println(line);
                }
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
                if (pw != null) {
                    pw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            temp.delete();
        }
        return false;
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
}
