package com.flyn.mydb.Util;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.flyn.mydb.MainActivity;
import com.flyn.mydb.Service.UIManage;

public class App extends Application {
    private static  final Handler handler=new Handler();
    private static Application application;
    private static UIManage manage;
    public static  void runOnUiThread(Runnable runnable){
        handler.post(runnable);
    }
    public static Handler getHandler(){
        return handler;
    }
    public static Application getApplication() {
        return application;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application=this;
    }

    public static UIManage getManage() {
        return manage;
    }

    public static void setManage(UIManage manage) {
        App.manage = manage;
    }
}
