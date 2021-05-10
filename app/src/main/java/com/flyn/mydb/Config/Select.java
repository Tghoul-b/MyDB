package com.flyn.mydb.Config;

import java.util.List;

public class Select implements Operate{
    private boolean seeAll=false;
    private int numberFeld=0;//显示的属性数量
    private List<DisplayField> attributes=null;//显示的属性集合
    private int numberTable=0;//查询的关系表数量
    private List<String> tableName=null;//查询的表的名字
    private boolean existWhereCondition=true;//是否存在where条件
    private List<List<ConditionalExpression>> conditions=null;//where条件集合
    private String account=null;//select语句

    public Select(String account) {
        this.account = account;
    }

    @Override
    public void start() throws Exception {
        long start=System.currentTimeMillis();

    }
}
