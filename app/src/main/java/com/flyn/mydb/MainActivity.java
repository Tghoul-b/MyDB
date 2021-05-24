package com.flyn.mydb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.flyn.mydb.Service.DBMS;
import com.flyn.mydb.Service.UIManage;
import com.flyn.mydb.Util.App;
import com.flyn.mydb.Util.BaseApi;
import com.flyn.mydb.Util.PermissionsChecker;
import com.flyn.mydb.databinding.ActivityMainBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_STORAGE = 1;
    private static int WAIT_INTERVAL = 2000;
    static final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private PermissionsChecker mPermissionsChecker;
    public static final int CATEGORY_INPUT = 10, CATEGORY_OUTPUT = 11, CATEGORY_NO_COLOR = 20;
    private ActivityMainBinding binding;
    private Typeface typeface;
    private ViewGroup rootView;
    private TextView prefixView;
    private String device_name;
    private String curAccount;
    private TextView terminalTv;
    private EditText mInputView;
    private int clearCmdsCount= 0;
    private ScrollView mScrollView;
    private List<String> cmdList;
    private int curCmdCnt=-1;
    private Handler mHandler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case  1:
                    ChangeRam();
                    break;
                case 2:
                    ChangeCharge();
                    changeDate();
                    break;
            }
            return false;
        }
    });
    private Runnable mScrollRunnable = new Runnable() {
        @Override
        public void run() {
            mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            mInputView.requestFocus();
        }
    };
    public  final  long RAM_DELAY=3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        typeface = Typeface.createFromAsset(getAssets(),"lucida_console.ttf");
        rootView=findViewById(R.id.mainview);
        InitWidget();
        initClick();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
            mPermissionsChecker = new PermissionsChecker(this);
            requestPermission();
        }
    }
    private void initData(){
        terminalTv=findViewById(R.id.terminal_view);
        UIManage manage=new UIManage(MainActivity.this,terminalTv);
        App.setManage(manage);
        mInputView=findViewById(R.id.input_view);
        cmdList=new ArrayList<>();
        mScrollView=findViewById(R.id.input_scroll);
        RelativeLayout.LayoutParams layoutParams=(RelativeLayout.LayoutParams)mScrollView.getLayoutParams();
        layoutParams.setMargins(20,0,20,0);
        mScrollView.setLayoutParams(layoutParams);
    }

    private void initClick(){

        ImageView imageView=findViewById(R.id.submit_tv);
        imageView.setOnClickListener(v -> onNewInput());
        ImageView imageView1=findViewById(R.id.delete_view);
        imageView1.setOnClickListener(v->setupNewInput());
        ImageView imageView2=findViewById(R.id.back_view);
        imageView2.setOnClickListener(v->{
            changeIns(-1);
        });
        ImageView imageView3=findViewById(R.id.next_view);
        imageView3.setOnClickListener(v->{
            changeIns(1);
        });
    }
    public void changeIns(int v){
        if(curCmdCnt==-1){  //第一次调用切换的命令
            curCmdCnt=cmdList.size()-1;
            mInputView.setText(cmdList.get(curCmdCnt));
            mInputView.setSelection(cmdList.get(curCmdCnt).length());
            return ;
        }
        else if(v<0&&curCmdCnt==0){//第一条指令
            mInputView.setText(cmdList.get(curCmdCnt));
            mInputView.setSelection(cmdList.get(curCmdCnt).length());
            return ;
        }else if(v>0&&curCmdCnt==cmdList.size()-1){
            mInputView.setText(cmdList.get(curCmdCnt));
            mInputView.setSelection(cmdList.get(curCmdCnt).length());
            return ;
        }
        else{
            curCmdCnt+=v;
            mInputView.setText(cmdList.get(curCmdCnt));
            mInputView.setSelection(cmdList.get(curCmdCnt).length());
        }
    }
    public void setOutput(String s){
        if(TextUtils.isEmpty(s))  return ;
        writeToView(s,CATEGORY_OUTPUT);
    }

    private boolean onNewInput() {
        if (mInputView == null) {
            return false;
        }

        CharSequence input = mInputView.getText();

        String cmd = input.toString().trim();

        if(input.length() > 0) {
            clearCmdsCount++;
            writeToView(input,CATEGORY_INPUT);
            cmdList.add(cmd);
        }
        String s=input.toString();
        setupNewInput();
        try {
            new DBMS().start(s);
        }catch (Exception e){
            String err=e.getMessage();
            App.getManage().outTextView(err);
        }
        curCmdCnt=-1;
        return true;
    }

    private void writeToView(final CharSequence text) {
        terminalTv.post(() -> {
            terminalTv.append(text);
            scrollToEnd();
        });
    }
    private void writeToView(CharSequence text,int type){
        text=getFinalText(text,type);
        text=TextUtils.concat("\n", text);
        writeToView(text);
    }
    private SpannableString getFinalText(CharSequence t, int type) {
        SpannableString s=null;
        switch (type){
            case CATEGORY_INPUT:
                t=t.toString();
                String date=getDate();
                String final_str="["+date+"]"+" $ "+t;
                s=new SpannableString(final_str);
                int InputColor=getResources().getColor(R.color.input_color);
                int timeColor=getResources().getColor(R.color.time);
                s.setSpan(new ForegroundColorSpan(timeColor),1,date.length()+1,SpannableString.SPAN_INCLUSIVE_INCLUSIVE);
                s.setSpan(new ForegroundColorSpan(InputColor),final_str.indexOf("$"),final_str.indexOf("$")+1,SpannableString.SPAN_INCLUSIVE_INCLUSIVE);
                s.setSpan(new ForegroundColorSpan(InputColor),final_str.indexOf("$")+2,final_str.length(),SpannableString.SPAN_INCLUSIVE_INCLUSIVE);
                s.setSpan(new ForegroundColorSpan(InputColor),0,1,SpannableString.SPAN_INCLUSIVE_INCLUSIVE);
                s.setSpan(new ForegroundColorSpan(InputColor),final_str.indexOf("]"),final_str.indexOf("]")+1,SpannableString.SPAN_INCLUSIVE_INCLUSIVE);
                break;
            case CATEGORY_OUTPUT:
                t=t.toString();
                s=new SpannableString(t);
                s.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.white)),0,t.length(),SpannableString.SPAN_INCLUSIVE_INCLUSIVE);
        }
        return s;
    }
    public void scrollToEnd() {
        mScrollView.postDelayed(mScrollRunnable, 200);
    }
    private void setupNewInput() {
        mInputView.setText("");
        String strHint="user@"+device_name+"~";
        mInputView.setTextColor(getResources().getColor(R.color.input_color));
        mInputView.setHighlightColor(Color.TRANSPARENT);
        mInputView.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        SpannableString span = new SpannableString(strHint);
        span.setSpan(new RelativeSizeSpan(0.8f), 0, strHint.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mInputView.setHint(span);
        mInputView.setTypeface(typeface);
        terminalTv.setTypeface(typeface);
    }
    private void initTools(){
        prefixView=findViewById(R.id.prefix_view);
        prefixView.setText("$");
        prefixView.setTypeface(typeface);
        int color=getResources().getColor(R.color.delete_view);
        ImageButton deleteView=findViewById(R.id.delete_view);
        deleteView.setColorFilter(color);
        ImageButton backView=findViewById(R.id.back_view);
        backView.setColorFilter(color);
        ImageButton forwardView=findViewById(R.id.next_view);
        forwardView.setColorFilter(color);
        ImageButton paste_view=findViewById(R.id.paste_view);
        paste_view.setColorFilter(color);;
        ImageView imageView=findViewById(R.id.submit_tv);
        imageView.setColorFilter(getResources().getColor(R.color.enter_color));
        setupNewInput();
        mInputView.requestFocus();
    }
    private void ChangeRam(){
        ActivityManager manager;
        ActivityManager.MemoryInfo memory;
        memory = new ActivityManager.MemoryInfo();
        manager = (ActivityManager) getApplicationContext().getSystemService(Activity.ACTIVITY_SERVICE);
        double freeRam= BaseApi.freeRam(manager,memory);
        double totalRam=BaseApi.totalRam(manager,memory);
        String s=String.format("Available RAM: %.2f GB of %.2f GB（%.2f%%）",freeRam/1024/1024/1024,totalRam/1024/1024/1024,freeRam/totalRam*100);
        TextView tv1=findViewById(R.id.tv1);
        tv1.setTypeface(typeface);
        tv1.setText(s);
    }
    private void ChangeCharge(){
        BatteryManager batteryManager = (BatteryManager)getSystemService(BATTERY_SERVICE);
        int battery = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        TextView tv2=findViewById(R.id.tv2);
        if(battery<30){
            tv2.setTextColor(getResources().getColor(R.color.battery_color_low));
        }
        else if(battery<60){
            tv2.setTextColor(getResources().getColor(R.color.battery_color_medium));
        }

        tv2.setText("Charging: "+battery+"%");
        tv2.setTypeface(typeface);
    }
    private String getDate(){
        Date date=new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-d HH:mm:ss");
        String s=formatter.format(date);
        return s;
    }
    private void changeDate(){
        Date date=new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-d HH:mm:ss");
        String s=formatter.format(date);
        TextView tv3=findViewById(R.id.tv3);
        tv3.setText(s);
        tv3.setTypeface(typeface);
    }
    private void getStorage(){
        double AvailStorage=BaseApi.getAvailableSpace(Environment.getDataDirectory(),4);
        double totalStorage=BaseApi.getTotaleSpace(Environment.getDataDirectory(),4);
        String s=String.format("Internal Storage: %.2f GB / %.2f GB (%.2f%%)",AvailStorage/1024/1024/1024,totalStorage/1024/1024/1024,AvailStorage/totalStorage*100);
        TextView tv4=findViewById(R.id.tv4);
        tv4.setText(s);
        tv4.setTypeface(typeface);
    }
    private class MyTaskRam extends TimerTask{
        @Override
        public void run() {
            Message message=new Message();
            message.what=1;
            mHandler.sendMessage(message);
        }
    }
    private class MyTaskCharge extends  TimerTask{
        @Override
        public void run() {
            Message message=new Message();
            message.what=2;
            mHandler.sendMessage(message);
        }
    }
    private void InitWidget(){
        String device_name= Build.DEVICE;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        TextView tv0=findViewById(R.id.tv0);
        tv0.setText(device_name+": user");
        tv0.setTypeface(typeface);
        this.device_name=device_name;
        Timer timerRam=new Timer();
        timerRam.schedule(new MyTaskRam(),0,RAM_DELAY);
        Timer timeCharge=new Timer();
        timeCharge.scheduleAtFixedRate(new MyTaskCharge(),0,1000);
        getStorage();
        int layoutId=R.layout.input_down;
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View inputOutputView = inflater.inflate(layoutId, null);
        rootView.addView(inputOutputView);
        initData();
        initTools();
    }
    private static void applyBgRect(View v, String strokeColor, String bgColor, int strokeWidth, int cornerRadius) {
        try {
            GradientDrawable d = new GradientDrawable();
            d.setShape(GradientDrawable.RECTANGLE);
            d.setCornerRadius(cornerRadius);

            if(!(strokeColor.startsWith("#00") && strokeColor.length() == 9)) {
                d.setStroke(strokeWidth, Color.parseColor(strokeColor));
            }


            d.setColor(Color.parseColor(bgColor));
            v.setBackgroundDrawable(d);
        } catch (Exception e) {
        e.printStackTrace();
        }
    }
    private void requestPermission(){
        //获取读取和写入SD卡的权限
        if (mPermissionsChecker.lacksPermissions(PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSIONS_REQUEST_STORAGE);
        }
    }
}