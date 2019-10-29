package com.example.myapplication;


import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Changetest extends Activity implements OnClickListener {

    //定义UPDATE_TEXT这个整型敞亮，用于表示更新TextView这个动作
    public static final int UPDATE_TEXT = 1;

    private TextView text;
    private Button changeText;

    //创建一个Handler
    private Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_TEXT:
                    //在这里可以进行UI操作
                    //对msg.obj进行String强制转换
                    String string=(String)msg.obj;
                    text.setText(string);
                    break;
                default:
                    break;
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changetest);
        text = (TextView) findViewById(R.id.text);
        changeText = (Button) findViewById(R.id.change_text);
        changeText.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.change_text:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //创建一个message
                        //设置what字段的值为UPDATE_TEXT,主要是为了区分不同的message
                        //设置message.obj的内容
                        //调用Handler的message对象
                        //handler中的handlermessage对象是在主线程中运行的
                        String string="Nice to meet you";
                        Message message = new Message();
                        message.what = UPDATE_TEXT;
                        message.obj=string;
                        handler.sendMessage(message);
                    }
                }).start();
                break;
            default:
                break;
        }
    }

}