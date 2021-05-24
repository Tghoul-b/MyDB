package com.flyn.mydb.Oper;

import android.text.TextUtils;
import android.widget.TextView;

import com.flyn.mydb.Service.Check;
import com.flyn.mydb.Service.DBMS;
import com.flyn.mydb.Service.ParseAccount;
import com.flyn.mydb.Util.App;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class Show implements Operate{
    private String account=null;
    public Show(String account) {
        this.account = account;
    }
    @Override
    public void start() throws Exception {
        switch(ParseAccount.parseShow(this.account)) {
            case 1:
                showDatabase();
                break;
            case 2:
                Check.hadUseDatabase();
                showTables();
                break;
            default:
                throw new RuntimeException("Error on show");
        }
    }
    /**
     * 显示所有数据库!!!
     */
    private void showDatabase() {
        File file = new File(DBMS.ROOTPATH + File.separator + "database.config");
        List<String> databaseList=new ArrayList<>();
        int maxLen=8;
        if(!file.exists())
            throw new RuntimeException("Database file missing");
        try {
            BufferedReader bf = new BufferedReader(new FileReader(file));
            String s;
            while(!TextUtils.isEmpty(s=bf.readLine())){
                databaseList.add(s);
                maxLen=Math.max(maxLen,s.length());
            }
            maxLen+=2;
            int len=databaseList.size()+4;
            for(int i=0;i<len;i++){
                if(i==0||i==2||i==len-1){
                    String corner="+";
                    for(int j=0;j<maxLen;j++)  corner+="-";
                    corner+="+";
                    App.getManage().outTextView(corner);
                }
                else if(i==1){
                    String prefix="| Database";
                    while(prefix.length()<maxLen+1)  prefix+=" ";
                    prefix+="|";
                    App.getManage().outTextView(prefix);
                }
                else{
                    String t="| "+databaseList.get(i-3);
                    while(t.length()<maxLen+1) t+=" ";
                    t+="|";
                    App.getManage().outTextView(t);
                }
            }
            bf.close();
        }catch (Exception e){
            throw new RuntimeException("Database file missing");
        }
    }
    /**
     * 显示当前数据库下的所有表!!!
     */
    private void showTables() {
        String prefix="Tables_in_"+DBMS.dataDictionary.getName();
        List<String> tmp=DBMS.dataDictionary.getTables();
        int maxLen=prefix.length();
        for(int i=0;i<tmp.size();i++){
            maxLen=Math.max(maxLen,tmp.get(i).length());
        }
        maxLen+=2;
        int len=tmp.size()+4;
        for(int i=0;i<len;i++){
            if(i==0||i==2||i==len-1){
                String corner="+";
                for(int j=0;j<maxLen;j++)  corner+="-";
                corner+="+";
                App.getManage().outTextView(corner);
            }
            else if(i==1){
                prefix="| "+prefix;
                while(prefix.length()<maxLen+1)  prefix+=" ";
                prefix+="|";
                App.getManage().outTextView(prefix);
            }
            else{
                String t="| "+tmp.get(i-3);
                while(t.length()<maxLen+1) t+=" ";
                t+="|";
                App.getManage().outTextView(t);
            }
        }
    }

}
