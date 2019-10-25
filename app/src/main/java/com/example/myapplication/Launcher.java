package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.baidu.mapapi.model.LatLng;

public class Launcher extends AppCompatActivity {
    public static Launcher instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        // 隐藏标题栏
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setTheme(R.style.AppTheme);//恢复原有的样式
        setContentView(R.layout.activity_launcher);

        SharedPreferences sp = getSharedPreferences("login", getApplicationContext().MODE_PRIVATE);
        String phone = sp.getString("phone", null);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (phone != null) {
            MyApplication application = (MyApplication) getApplicationContext();
            application.setPhone(phone);
            Intent intent = new Intent(Launcher.this, Map.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        }
        else {
            Intent i = new Intent(Launcher.this, MainActivity.class);
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        }
    }

}
