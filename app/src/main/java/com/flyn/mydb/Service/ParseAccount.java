package com.flyn.mydb.Service;

import com.flyn.mydb.Config.ConditionalExpression;

public class ParseAccount {
    //解析条件表达式
    private static  void parseCondition(String left, String right, ConditionalExpression condition) throws Exception{
        String []sp=null;
        String table=null;
        String name=null;
        condition.setLeft(left);
        condition.setRight(right);
        if(left.contains(".")&&!left.matches("^\".+\"$")){
            boolean flag=true;
            sp=left.split("\\.");
            table=sp[0];
            name=sp[1];
            if(!DBMS.loadedTables.containsKey(table)){
                System.out.println("属性非法!"+sp);
                return ;
            }
        }
    }

}
