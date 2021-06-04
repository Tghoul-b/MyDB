package com.flyn.mydb;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void testCreate(){
        String tableP="(?i)^(create)\\s+(table)\\s+(\\w+)\\s*\\((\\s*(\\w+\\s*INT)|(\\w+\\s*CHAR(\\(\\d+\\))?)|(\\w+\\s*DATE))((\\s*PRIMARY\\s*KEY)|(\\s*Not\\s*Null))?"
                +"(\\s*,\\s*((\\w+\\s*INT)|(\\w+\\s*CHAR(\\(\\w+\\))?)|(\\w+\\s*DATE))((\\s*PRIMARY\\s*KEY)|(\\s*Not\\s*Null))?)*\\);$";
        String p="create table add;";
        System.out.println(p.matches(tableP));
    }
    @Test
    public void testInsert() {
        String s="INSERT INTO employee VALUES(\"李白\", 370783190000000000, \"中国\", \"男\");";
        String pattern="(?i)^(insert)\\s+(into)\\s+\\w+\\s+(values)\\s*"
                +"\\(\\s*(\\S+)\\s*(,\\s*\\S+\\s*)*\\);$";
        System.out.println(s.matches(pattern));
    }
    @Test
    public void testDelete(){
        String pattern = "(?i)^(delete)\\s(from)\\s[\\S]+(\\s(where)\\s[\\S]+[(<)(>)(=)(<=)(>=)][\\S]+(\\s((and)|(or))\\s[\\S]+[(<)(>)(=)(<=)(>=)][\\S]+)*)?;$";
        String s="delete from table;";
        System.out.println(s.matches(pattern));
    }
    @Test
    public void testSubOper(){
        String account="DELETE FROM employee WHERE employee.sex=\"woman\" AND employee.country=\"美国\" OR employee.sex=\"女\" AND employee.country=\"中国\";";
        String start="where";
        String end=";";
        Pattern pattern=Pattern.compile("(?i)(?="+start+").+(?<="+end+")");
        String temp=null;
        Matcher matcher=pattern.matcher(account);
        if(matcher.find())
            temp=matcher.group(0);
        temp=temp.split("(?i)(where)|;")[1];
        System.out.println(temp);
    }
    @Test
    public void testInt(){
        String pattern="^(-?)[1-9]+[0-9]*$";
        String s="-1";
        System.out.println(s.matches(pattern));
    }
}