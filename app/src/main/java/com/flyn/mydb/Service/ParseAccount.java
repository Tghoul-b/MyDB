package com.flyn.mydb.Service;

import android.text.TextUtils;

import com.flyn.mydb.Config.ConditionalExpression;
import com.flyn.mydb.Oper.Alter;
import com.flyn.mydb.Oper.Create;
import com.flyn.mydb.Config.Field;
import com.flyn.mydb.Config.Table;
import com.flyn.mydb.Oper.Delete;
import com.flyn.mydb.Oper.Insert;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            for(Field field:DBMS.loadedTables.get(table).getAttributes()){  //遍历这个表的属性集合
                if(field.getFieldName().equals(name)){//这个表含有条件表达式左部的属性，利用表中的属性给条件表达式中的属性赋值
                    condition.setLeftType(field.getType());
                    condition.setLeftcolumn(field.getColumn());
                    condition.setLeftIsIndex(field.isIndex());
                    condition.setLeftRoot(field.getIndexRoot());
                    flag=false;
                    break;
                }
            }
            if(!flag){
                condition.setLeftConstant(flag);  //条件表达式左部是属性
                condition.setLeftTableName(table);//条件表达式左部所属表名
                condition.setLeftAttribute(name);
            }
            else{
                throw new RuntimeException("关系"+table+"没有"+name+"这个属性!");
            }
        }
        if(right.contains(".")&&!right.matches("^\".+\"$")){
            boolean flag=true;
            sp=right.split("\\.");
            table=sp[0];
            name=sp[1];
            if(!DBMS.loadedTables.containsKey(table)){
                System.out.println("属性非法!"+sp);
                return ;
            }
            for(Field field:DBMS.loadedTables.get(table).getAttributes()){  //遍历这个表的属性集合
                if(field.getFieldName().equals(name)){//这个表含有条件表达式左部的属性，利用表中的属性给条件表达式中的属性赋值
                    condition.setRightType(field.getType());
                    condition.setRightcolumn(field.getColumn());
                    condition.setRightIsIndex(field.isIndex());
                    condition.setRightRoot(field.getIndexRoot());
                    flag=false;
                    break;
                }
            }
            if(!flag){
                condition.setRightConstant(flag);  //条件表达式左部是属性
                condition.setRightTableName(table);//条件表达式左部所属表名
                condition.setRightAttribute(name);
            }
            else{
                throw new RuntimeException("关系"+table+"没有"+name+"这个属性!");
            }
        }
    }

    public static  Table parseTable(String account, String name) throws Exception{
        String attr=account.substring(account.indexOf('(')+1,account.lastIndexOf(')')).trim();
        String[] attrs=account.split(",");
        if(attrs.length==0){
            throw  new RuntimeException("非法语句!");
        }
        List<Field> fieldList=new ArrayList<>();
        Field field=null;
        String []infos=null;
        for(int i=0;i<attrs.length;i++){
            infos=attrs[i].trim().split("//s");//空白符作为分隔符
            field=new Field();
            field.setColumn(i);  //第几列
            field.setFieldName(infos[0]);//设置字段名
            String t=infos[i].toLowerCase();
            if(t.equals("int")){
                field.setType("INT");
                field.setLength(4);
            }else if(t.startsWith("char")){
                String d=(t.substring(t.indexOf('(')+1,t.lastIndexOf(')')));
                int length=0;
                if(TextUtils.isEmpty(d))
                    length=32;//默认长度32
                else length=Integer.parseInt(d);
                field.setType("CHAR");
                field.setLength(length);
            }
            else if(t.equals("date")){
                field.setType("DATE");
                field.setLength(8);//日期类型数据8位长
            }
            if(infos.length==4){
                if(infos[2].equalsIgnoreCase("not")){
                    field.setNull(false);
                }
                else{
                    field.setPrimary(true);//主键
                }
            }
            fieldList.add(field);
        }
        File file=new File(DBMS.currentPath+File.separator+name);//数据文件
        File configFile=new File(DBMS.currentPath+File.separator+name+".config");//数据表配置文件
        return new Table(name,file,configFile,fieldList);//表名，数据文件，配置文件,字段集合
    }

    /**
     * 获取use database中数据库的名称
     * @param account
     * @return
     */
    public static String parseUse(String account){
        String pattern="(?i)^(use)\\s(database)\\s\\S+\\s?;$";  //\s匹配空白字符,\S匹配非空白字符,^定义以use开头  (?i)表示忽略大小写
        if(!account.matches(pattern)){
            throw new RuntimeException("invalid use Instruction");
        }
        return account.split("\\s|;")[2];//该操作可以去除数据库名后面可能跟的;
    }
    public static int parseCreate(Create create){
        String tableP="(?i)^(create)\\s+(table)\\s+(\\w+)\\s*\\((\\s*(\\w+\\s*INT)|(\\w+\\s*CHAR(\\(\\d+\\))?)|(\\w+\\s*DATE))((\\s*PRIMARY\\s*KEY)|(\\s*Not\\s*Null))?"
                +"(\\s*,\\s*((\\w+\\s*INT)|(\\w+\\s*CHAR(\\(\\w+\\))?)|(\\w+\\s*DATE))((\\s*PRIMARY\\s*KEY)|(\\s*Not\\s*Null))?)*\\);$";//创建数据表的正则表达式
        String databaseP="(?i)^(create)\\s+(database)\\s+\\w+\\s*?;$";//创建数据库的正则表达式
        String indexP="(?i)^(create)\\s+(index)\\s+(\\w+)\\s+(on)\\s*(\\w+)\\s*(\\w+)\\s*;$";
        String []arr=create.getAccount().split("\\s|;|\\(");
        if(create.getAccount().matches(tableP))//建表命令
        {
            create.setName(arr[2]);//设置表名
            if(DBMS.dataDictionary==null) {
                throw new RuntimeException("please choose a database");
            }
            if(DBMS.dataDictionary.getTables().contains(create.getName())){
                throw new RuntimeException("table already existed");
            }else{
                return 2;//表不存在，则建表
            }
        }else if(create.getAccount().matches(databaseP)){
            create.setName(arr[2]);
            return 1;//建数据库
        }
        else if(create.getAccount().matches(indexP)){
            create.setName(arr[2]);
            return 3;//建立索引
        }
        else{
            throw new RuntimeException("invalid create Instruction");
        }
    }
    public static int parseShow(String account){
        String showDatabase="(?i)(show)\\s+(databases)\\s*;";
        String showTables="(?i)(show)\\s+(tables)\\s*;";
        if(account.matches(showDatabase)){
            return 1;//展示数据库
        }
        else if(account.matches(showTables)){
            return 2;
        }
        else{
            throw new RuntimeException("invalid show Instruction");
        }
    }
    public static void parseInsert(Insert insert) throws Exception{
        String account=insert.getAccount();
        String pattern="(?i)^(insert)\\s+(into)\\s+\\w+\\s+(values)\\s*"
                +"\\(\\s*(\\S+)\\s*(,\\s*\\S+\\s*)*\\);$";
        if(!account.matches(pattern)){
            throw new RuntimeException("非法Insert语句");
        }
        String tableName=account.split("\\s")[2];
        insert.setTableName(tableName);
        insert.setValues(ParseAccount.subValues(account));
    }
    public static void parseDelete(Delete delete){
        String account=delete.getAccount();
        String pattern="(?i)^(delete)\\s+(from)\\s+\\w+(\\s+(where)\\s+\\w+[(<)(>)(=)(<=)(>=)]\\w+(\\s*((and)|(or))\\s+\\w+"+
                "[(<)(>)(=)(<=)(>=)]\\w+)*)?;";
        if(!account.matches(pattern)){
            throw new RuntimeException("非法delete语句");
        }
        String []arr=account.split("\\s|;");
        delete.setTableName(arr[2]);
        if(!account.contains("where") &&!account.contains("WHERE")){
            delete.setExistWhereCondition(false);
        }
        else{
            List<List<String>>  list=ParseAccount.subOper(account,"where",";");
            List<List<ConditionalExpression>> lc=new ArrayList<>();
            List<ConditionalExpression> conditions=null;
            ConditionalExpression condition=null;
            String []lr=null;
            try{
                for(List<String>lor:list){
                    conditions=new ArrayList<>();
                    for(String str:lor){
                        condition=new ConditionalExpression();
                        if(str.contains("<=")){
                            lr=str.split("<=");
                            condition.setOper("<=");
                            parseCondition(lr[0],lr[1],condition);
                        }else if(str.contains(">=")){
                            lr=str.split(">=");
                            condition.setOper(">=");
                            parseCondition(lr[0],lr[1],condition);
                        }
                        else if(str.contains("<")){
                            lr=str.split("<");
                            condition.setOper("<");
                            parseCondition(lr[0],lr[1],condition);
                        }else if(str.contains(">")){
                            lr=str.split(">");
                            condition.setOper(">");
                            parseCondition(lr[0],lr[1],condition);
                        }else if(str.contains("=")){
                            lr=str.split("=");
                            condition.setOper("=");
                            parseCondition(lr[0],lr[1],condition);
                        }
                        conditions.add(condition);
                    }
                    lc.add(conditions);
                }
            }catch (Exception e){
                System.out.println(e.getMessage());
                e.printStackTrace();
                return ;
            }
            delete.setConditions(lc);
        }
    }

    public static  void parseAlter(Alter alter){

    }
    public static int parseDrop(String account){
        String tableP="(?i)^(drop)\\s+(table)\\s+\\w+\\s*;";
        String IndexP="(?i)^(drop)\\s+(index)\\s+\\w+\\s*;";
        if(account.matches(tableP))
            return 1;
        else if(account.matches(IndexP))
            return 2;
        else
            return -1;

    }
    public static String parseTableName(String account){
        return account.split("\\s|;")[2];
    }
    public static String subValues(String account){
        String []values=account.substring(account.indexOf('(')+1,account.lastIndexOf(')')).split(",");
        String newLine="";
        for(String value:values){
            newLine+=value.trim()+",";//以，分割
        }
        return newLine.substring(0,newLine.length()-1);//删除最后一个","
    }

    /**
     * 解析条件语句
     * @param account
     * @param start
     * @param end
     * @return
     */
    public static List<List<String>>subOper(String account,String start,String end){
        List<List<String>> list=new ArrayList<>();
        List<String> tlist=null;
        Pattern pattern=Pattern.compile("(?i)(?="+start+").+(?<="+end+")");  //得到start和end之间的所有语句
        String temp=null;
        Matcher matcher=pattern.matcher(account);
        if(matcher.find())
            temp=matcher.group(0);
        temp=temp.split("(?i)(where)|;")[1].trim();//删除where和;
        String []arr1=temp.split("(?i)(or)");//or连接的语句
        for(String arr2:arr1){
            tlist=new ArrayList<>();
            for(String arr3:arr2.trim().split("(?i)(and)"))
                tlist.add(arr3.trim());
            list.add(tlist);
        }
        return list;
    }
}
