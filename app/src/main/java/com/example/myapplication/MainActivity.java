package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static MainActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        Launcher.instance.finish();
        // 隐藏标题栏
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        Button bt1 = (Button) findViewById(R.id.button);
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Signin.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            }
        });
        String[] permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.READ_CONTACTS};
        List<String> mPermissionList = new ArrayList<>();

        if (Build.VERSION.SDK_INT>=23) {
            mPermissionList.clear();//清空已经允许的没有通过的权限
            //逐个判断是否还有未通过的权限
            for (int i = 0;i<permissions.length;i++){
                if (ContextCompat.checkSelfPermission(this,permissions[i])!=
                        PackageManager.PERMISSION_GRANTED){
                    mPermissionList.add(permissions[i]);//添加还未授予的权限到mPermissionList中
                }
            }
            //申请权限
            if (mPermissionList.size()>0){//有权限没有通过，需要申请
                ActivityCompat.requestPermissions(this,permissions,123);
            }else {
            }
        }
        else {

        }
    }
}
