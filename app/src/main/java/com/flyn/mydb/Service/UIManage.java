package com.flyn.mydb.Service;

import android.content.Context;
import android.widget.TextView;

import com.flyn.mydb.MainActivity;

public class UIManage {
    private Context mContext;
    private TextView mTextView;
    public UIManage(Context context,TextView textView){
        this.mContext=context;
        mTextView=textView;
    }
    public void outTextView(String s){
        ((MainActivity)mContext).setOutput(s);
    }
}
