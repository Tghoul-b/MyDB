package com.flyn.mydb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyn.mydb.Util.BaseApi;
import com.flyn.mydb.databinding.ActivityMainBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private Typeface typeface;
    private ViewGroup rootView;
    private TextView prefixView;
    private String device_name;
    private Handler mHandler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case  1:
                    ChangeRam();
                    break;
                case 2:
                    ChangeCharge();
                    getDate();
                    break;
            }
            return false;
        }
    });
    public  final  long RAM_DELAY=3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        typeface = Typeface.createFromAsset(getAssets(),"lucida_console.ttf");
        rootView=findViewById(R.id.mainview);
        InitWidget();
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
        timerRam.schedule(new MyTaskRam(),0,3000);
        Timer timeCharge=new Timer();
        timeCharge.scheduleAtFixedRate(new MyTaskCharge(),0,1000);
        getStorage();
        int layoutId=R.layout.input_down;
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View inputOutputView = inflater.inflate(layoutId, null);
        rootView.addView(inputOutputView);
        initTools();
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
        EditText inputView=findViewById(R.id.input_view);
        String strHint="user@"+device_name+"~";
        inputView.setTextColor(getResources().getColor(R.color.input_color));
        inputView.setHighlightColor(Color.TRANSPARENT);
        inputView.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        SpannableString span = new SpannableString(strHint);
        span.setSpan(new RelativeSizeSpan(0.8f), 0, strHint.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        inputView.setHint(span);
        inputView.setTypeface(typeface);
        inputView.requestFocus();
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
    private void getDate(){
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

}