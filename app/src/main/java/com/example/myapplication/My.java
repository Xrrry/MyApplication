package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class My extends AppCompatActivity {
    String name = "";
    String phone = "";
    public static My instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        // 隐藏标题栏
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        // 设置状态栏字体颜色 黑色
        Window window = getWindow();
        if (window != null) {
            Class clazz = window.getClass();
            try {
                int darkModeFlag = 0;
                Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                extraFlagField.invoke(window, darkModeFlag, darkModeFlag);//状态栏透明且黑色字体

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //开发版 7.7.13 及以后版本采用了系统API，旧方法无效但不会报错，所以两个方式都要加上
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                }
            } catch (Exception e) {

            }
        }
        setContentView(R.layout.activity_my);

        final MyApplication application = (MyApplication) getApplicationContext();
        phone = application.getPhone();
        name = application.getName();
        Button bt1 = (Button) findViewById(R.id.Button1);
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i1 = new Intent(My.this,EditName.class);

                startActivity(i1);
            }
        });

        Button bt3 = (Button) findViewById(R.id.Button3);
        bt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MyApplication application = (MyApplication) getApplicationContext();
                application.setPhone("");
                SharedPreferences sp = getSharedPreferences("login", getApplicationContext().MODE_PRIVATE);
                sp.edit()
                        .clear()
                        .apply();
                Intent i1 = new Intent(My.this, MainActivity.class);
                startActivity(i1);
                Map.instance.finish();
                finish();
            }
        });
        ((TextView)findViewById(R.id.Text3)).setText(name);
        ((TextView)findViewById(R.id.Text5)).setText(phone);

    }

    @Override
    protected void onResume() {
        super.onResume();
        final MyApplication application = (MyApplication) getApplicationContext();
        name = application.getName();
        ((TextView)findViewById(R.id.Text3)).setText(name);
    }
}

