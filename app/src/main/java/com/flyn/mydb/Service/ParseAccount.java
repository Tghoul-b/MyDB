package com.flyn.mydb.Service;

import android.text.TextUtils;

import com.flyn.mydb.Config.ConditionalExpression;
import com.flyn.mydb.Config.DisplayField;
import com.flyn.mydb.Oper.Alter;
import com.flyn.mydb.Oper.Create;
import com.flyn.mydb.Config.Field;
import com.flyn.mydb.Config.Table;
import com.flyn.mydb.Oper.Delete;
import com.flyn.mydb.Oper.Insert;
import com.flyn.mydb.Oper.OperUtil;
import com.flyn.mydb.Oper.Select;
import com.flyn.mydb.Oper.Update;
import com.flyn.mydb.Util.App;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseAccount {
    //解析条件表达式
    private static  void parseCondition(String left, String right, ConditionalExpression condition) throws Exception {
        String[] sp = null;
        String table = null;
        String name = null;

        condition.setLeft(left);
        condition.setRight(right);

        if (left.contains(".") && !left.matches("^\".+\"$")) {//如果符合xx.xx格式则为属性
            boolean flag = true;
            sp = left.split("\\.");
            table = sp[0];
            name = sp[1];
            Table table1=OperUtil.loadTable(table);
            for (Field field : DBMS.loadedTables.get(table).getAttributes()) {//遍历这个表的属性集合
                if (field.getFieldName().equals(name)) {
                    condition.setLeftType(field.getType());//左属性类型
                    condition.setLeftcolumn(field.getColumn());//左属性所在列
                    condition.setLeftIsIndex(field.isIndex());//左属性是否为索引列
                    condition.setLeftRoot(field.getIndexRoot());//左属性索引列树根，若不是索引列，则为null
                    flag = false;
                    break;
                }
            }
            if (flag == false) {
                condition.setLeftConstant(flag);//条件表达式的左部是属性
                condition.setLeftTableName(table);//左部所属表名
                condition.setLeftAttribute(name);//左部属性名
            } else {
                throw new RuntimeException("table "+table+" has no such field");//将异常抛出
            }

        }
        if (right.contains(".") && !right.matches("^\".+\"$")) {//如果符合XX.XX格式，则为属性
            boolean flag = true;
            sp = right.split("\\.");
            table = sp[0];
            name = sp[1];
            Table table1=OperUtil.loadTable(table);
            try{
                if((table1=DBMS.loadedTables.get(table))==null){
                    table1= OperUtil.loadTable(table);
                    DBMS.loadedTables.put(table,table1);
                }
            }catch (Exception e){
                e.printStackTrace();
                return ;
            }
            for (Field field : DBMS.loadedTables.get(table).getAttributes()) {//遍历这个表的属性集合
                if (field.getFieldName().equals(name)) {//这个表含有条件表达式右部的属性，利用表中的属性给条件表达式中的属性赋值
                    condition.setRightType(field.getType());//右属性类型
                    condition.setRightcolumn(field.getColumn());//右属性所在列
                    condition.setRightIsIndex(field.isIndex());//右属性是否为索引列
                    condition.setRightRoot(field.getIndexRoot());//右属性索引列树根，若不是索引列，则为null
                    flag = false;
                    break;
                }
            }
            if (flag == false) {
                condition.setRightConstant(flag);//条件表达式的又部是属性
                condition.setRightTableName(table);//又部所属表名
                condition.setRightAttribute(name);//又部属性名
            } else {
                throw new RuntimeException("table "+table+" has no such field");//将异常抛出
            }
        }
        if (!left.contains(".") && !right.contains(".")) {
            System.out.println("语句非法！常量比较");
        }
    }

    public static  Table parseTable(String account, String name) throws Exception{
        String attr=account.substring(account.indexOf('(')+1,account.lastIndexOf(')')).trim();
        String[] attrs=attr.split(",");
        if(attrs.length==0){
            throw  new RuntimeException("invalid field!");
        }
        List<Field> fieldList=new ArrayList<>();
        Field field=null;
        String []infos=null;
        for(int i=0;i<attrs.length;i++){
            infos=attrs[i].trim().split("\\s+");//空白符作为分隔符
            field=new Field();
            field.setColumn(i);  //第几列
            field.setFieldName(infos[0]);//设置字段名
            String t=infos[1].toLowerCase();
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
    public static void parseSelect(Select select,String account){
        String pattern="(?i)^(select)\\s([\\S]+)\\s?(,\\s?[\\S]+)*\\s(from)\\s[\\S]+\\s?(,\\s?[\\S]+)*"
                + "(\\s(where)\\s[\\S]+[(<)(>)(=)(<=)(>=)][\\S]+(\\s((and)|(or))\\s[\\S]+[(<)(>)(=)(<=)(>=)][\\S]+)*)?;$";
        if(!account.matches(pattern)){
            throw  new RuntimeException("invalid select Instruction");
        }
        String end="where";
        if(!account.toLowerCase().contains("where"))  end=";";
        String []fromwhere=ParseAccount.subAccount(account,"from",end);
        List<String> list=new ArrayList<>();
        for(String s:fromwhere){
            Table table=null;
            try{
                if((table=DBMS.loadedTables.get(s))==null){
                    table= OperUtil.loadTable(s);
                    DBMS.loadedTables.put(s,table);
                }
            }catch (Exception e){
                e.printStackTrace();
                return ;
            }
            list.add(s);
            select.setNumberTable(select.getNumberTable()+1);
        }
        select.setTableName(list);
        String []selectFrom=ParseAccount.subAccount(account,"select","from");
        if(selectFrom[0].equals("*")){
            select.setSeeAll(true);
        }else{
            List<DisplayField> attributes = new ArrayList<DisplayField>();//用于存储select后的字段信息
            DisplayField df = null;//显示字段
            String tableName = null;//要显示字段所涉及的表名
            String fieldName = null;//字段名
            for (String fullName : selectFrom) {
                try {
                    tableName = fullName.split("\\.")[0];
                    fieldName = fullName.split("\\.")[1];
                } catch (Exception e) {
                    return;
                }
                if (DBMS.loadedTables.get(tableName) == null) {
                    return;
                }
                for (Field field : DBMS.loadedTables.get(tableName).getAttributes()) {//遍历tableName表的字段集合
                    if (field.getFieldName().equals(fieldName)) {//找到该字段
                        df = new DisplayField(field, fullName, tableName);
                        break;
                    }
                }
                if (df == null) {
                    return;
                }
                attributes.add(df);
                select.setNumberFeld(select.getNumberFeld() + 1);
            }
            select.setAttributes(attributes);
        }
        if (!account.contains("where") && !account.contains("WHERE")) {//没有where
            select.setExistWhereCondition(false);
        } else {//有where，则处理条件表达式
            List<List<String>> arr = ParseAccount.subOper(account, "where", ";");

            List<List<ConditionalExpression>> lc = new ArrayList<List<ConditionalExpression>>();//or表达式
            List<ConditionalExpression> conditions = null;//and表达式
            ConditionalExpression condition = null;//单个表达式

            String[] lr = null;
            String left = null;
            String right = null;
            try {
                for (List<String> lor : arr) {
                    conditions = new ArrayList<ConditionalExpression>();
                    for (String str : lor) {
                        condition = new ConditionalExpression();
                        if (str.contains("<")) {
                            lr = str.split("<");
                            left = lr[0];
                            right = lr[1];
                            condition.setOper("<");
                            parseCondition(left, right, condition);
                        } else if (str.contains(">")) {
                            lr = str.split(">");
                            left = lr[0];
                            right = lr[1];
                            parseCondition(left, right, condition);
                            condition.setOper(">");
                        } else if (str.contains("<=")) {
                            lr = str.split("<=");
                            left = lr[0];
                            right = lr[1];
                            parseCondition(left, right, condition);
                            condition.setOper("<=");
                        } else if (str.contains(">=")) {
                            lr = str.split(">=");
                            left = lr[0];
                            right = lr[1];
                            parseCondition(left, right, condition);
                            condition.setOper(">=");
                        } else {
                            lr = str.split("=");
                            left = lr[0];
                            right = lr[1];
                            parseCondition(left, right, condition);
                            condition.setOper("=");
                        }
                        conditions.add(condition);
                    }
                    lc.add(conditions);
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
                return;
            }
            select.setConditions(lc);
        }
    }
    public static void parseUpdate(Update update, String account) {
        String pattern = "(?i)^(update)\\s\\w+\\s(set)(\\s\\w+(=)\\S+)(\\s?,\\s?\\w+(=)\\S+)*(\\s(where)\\s[\\S]+[(<)(>)(=)(<=)(>=)][\\S]+(\\s((and)|(or))\\s[\\S]+[(<)(>)(=)(<=)(>=)][\\S]+)*)?;$";
        if (!account.matches(pattern)) {
            System.out.println("invalid update Instruction");
            return;
        }
        String tableName = account.split("\\s")[1];

        update.setTableName(tableName);

        String end = "where";
        if (!account.contains("WHERE") && !account.contains("where")) {//语句不含有where则表名没有where条件
            update.setExistWhereCondition(false);//没有where条件
            end = ";";
        }
        String[] setValues = ParseAccount.subAccount(account, "set", end);

        List<String[]> set =  new ArrayList<String[]>();
        String[] kv = null;
        for (String string : setValues) {
            kv = new String[2];
            kv[0] = string.split("=")[0];
            kv[1] = string.split("=")[1];
            set.add(kv);
        }
        update.setSet(set);

        if (update.isExistWhereCondition()) {//有where条件，
            List<List<String>> cs = ParseAccount.subOper(account, "where", ";");
            List<List<ConditionalExpression>> lo = new ArrayList<List<ConditionalExpression>>();
            List<ConditionalExpression> conditions = null;
            ConditionalExpression condition = null;
            String[] lr = null;
            String left = null;
            String right = null;
            try {
                for (List<String> arr : cs) {
                    conditions = new ArrayList<ConditionalExpression>();
                    for (String str : arr) {
                        condition = new ConditionalExpression();
                        if (str.contains("<")) {
                            lr = str.split("<");
                            left = lr[0];
                            right = lr[1];
                            condition.setOper("<");
                            parseCondition(left, right, condition);
                        } else if (str.contains(">")) {
                            lr = str.split(">");
                            left = lr[0];
                            right = lr[1];
                            parseCondition(left, right, condition);
                            condition.setOper(">");
                        } else if (str.contains("<=")) {
                            lr = str.split("<=");
                            left = lr[0];
                            right = lr[1];
                            parseCondition(left, right, condition);
                            condition.setOper("<=");
                        } else if (str.contains(">=")) {
                            lr = str.split(">=");
                            left = lr[0];
                            right = lr[1];
                            parseCondition(left, right, condition);
                            condition.setOper(">=");
                        } else {
                            lr = str.split("=");
                            left = lr[0];
                            right = lr[1];
                            parseCondition(left, right, condition);
                            condition.setOper("=");
                        }
                        conditions.add(condition);
                    }
                    lo.add(conditions);
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
                return;
            }
            update.setConditions(lo);
        }

    }
    public static String[] subAccount(String account, String start, String end) {
        String temp = null;
        Pattern pattern = null;
        if (start == null) {
            pattern = Pattern.compile("(?i)(?=(add)|(drop)).+(?<=" + end + ")");
        } else {
            pattern = Pattern.compile("(?i)(?=" + start + ").+(?<=" + end + ")");
        }
        Matcher matcher = pattern.matcher(account);
        if (matcher.find()) {
            temp = matcher.group(0);
        }
        temp = temp.split("(?i)(select)|(set)|(add)|(drop)|(from)|(where)|;")[1];// 语句中不能出现关键字，即使单词中含有也不行
        String[] fields = temp.split("(?i),|(and)");// 要查看的字段
        for (int i = 0; i < fields.length; i++) {
            fields[i] = fields[i].trim();
        }

        return fields;
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
        String pattern = "(?i)^(delete)\\s(from)\\s[\\S]+(\\s(where)\\s[\\S]+[(<)(>)(=)(<=)(>=)][\\S]+(\\s((and)|(or))\\s[\\S]+[(<)(>)(=)(<=)(>=)][\\S]+)*)?;$";
        if(!account.matches(pattern)){
            throw new RuntimeException("invalid delete Instruction");
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

    public static void parseAlter(Alter alter) throws Exception {
        String account = alter.getAccount();

        String patternAdd = "(?i)^(alter)\\s(table)\\s\\S+\\s(add)\\s((\\S+\\sint)|(\\S+\\schar\\(\\w+\\))|(\\S+\\sdate))\\s?;$";
        String patternDrop = "(?i)^(alter)\\s(table)\\s\\w+\\s(drop)(\\s\\w+)(\\s?,\\s?\\w+)*\\s?;$";

        if (account.matches(patternAdd)) {//alter-add操作
            alter.setAdd(true);
        } else if (account.matches(patternDrop)) {//alter-drop操作
            alter.setAdd(false);
        } else {
            throw new RuntimeException("invalid alter Instruction");
        }

        String[] arr = account.split("\\s");
        String tableName = arr[2];//获取语句中涉及的表名
        alter.setTableName(tableName);
    }
    public static void parseAlterAddAndDrop(Alter alter) {
        String account = alter.getAccount();
        Table table = alter.getTable();
        String[] attrs = ParseAccount.subAccount(account, null, ";");

        List<Field> fields = new ArrayList<Field>();
        alter.setFields(fields);
        if (alter.isAdd()) {//是alter-add操作，新建属性，添加至待添加关系的属性集合
            String name = null;
            String type = null;
            int step = 0;

            for (String str : attrs) {//添加属性，新建字段
                int length = 4;//Integer 4字节
                name = str.split("\\s")[0];
                if (str.contains("(")) {//char类型
                    type = str.split("\\s")[1].split("\\(|\\)")[0];
                    length = Integer.parseInt(str.split("\\s")[1].split("\\(|\\)")[1]);//设置最大长度
                } else {//Integer或date类型
                    type = str.split("\\s")[1];
                }
                alter.getFields().add(new Field(name, type, length, table.getAttributes().size() + step, true, false, false, null));
                step += 1;
            }
        }
        if (!alter.isAdd()) {//是alter-drop操作
            for (String name : attrs) {
                for (Field field : table.getAttributes()) {//找到对应的属性，添加至alter
                    if (field.getFieldName().equals(name)) {//名字对应
                        alter.getFields().add(field);
                    }
                }
            }

        }
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
