package com.flyn.mydb.Service;

import android.app.Activity;
import android.content.Context;

import com.flyn.mydb.MainActivity;
import com.flyn.mydb.Oper.Alter;
import com.flyn.mydb.Oper.Create;
import com.flyn.mydb.Oper.Delete;
import com.flyn.mydb.Oper.Drop;
import com.flyn.mydb.Oper.Insert;
import com.flyn.mydb.Oper.Operate;
import com.flyn.mydb.Oper.Select;
import com.flyn.mydb.Oper.Show;
import com.flyn.mydb.Oper.Update;
import com.flyn.mydb.Oper.Use;
import com.flyn.mydb.Util.App;
import com.flyn.mydb.bean.OperationEnum;

/**
 * 工厂
 *
 */
class Handler {
    private Operate operate = null;

    public Handler(String account) {
        if(!account.endsWith(";")){
            throw new RuntimeException("please enter an instruction end with semicolon");
        }
        String[] operationArray = account.split("\\s");//按空字符分割输入语句

        OperationEnum op = null;//声明操作符
        try {
            op = OperationEnum.valueOf(operationArray[0].toUpperCase());//将操作字符串转化为枚举对象，方便switch操作
        } catch (Exception e) {
            String s="invalid Instruction";
            App.getManage().outTextView(s);
            return;
        }
        switch (op) {//匹配操作
            case SHOW:
                this.operate = new Show(account);
                break;
            case CREATE:
                this.operate = new Create(account);
                break;
            case INSERT:
                Check.hadUseDatabase();
                this.operate = new Insert(account);
                break;
            case DELETE:
                Check.hadUseDatabase();
                this.operate = new Delete(account);
                break;
            case ALTER:
                Check.hadUseDatabase();
                this.operate = new Alter(account);
                break;
            case UPDATE:
                Check.hadUseDatabase();
                this.operate = new Update(account);
                break;
            case DROP:
                Check.hadUseDatabase();
                this.operate = new Drop(account);
                break;
            case SELECT:
                Check.hadUseDatabase();
                this.operate = new Select(account);
                break;
            case USE:
                this.operate = new Use(account);
                break;
            default:
                break;
        }
    }

    public void start() {
        if(this.operate==null)
            return ;
        try {
            this.operate.start();
        } catch (Exception e) {
            String s=e.getMessage();
            App.getManage().outTextView(s);
        }
    }
}
