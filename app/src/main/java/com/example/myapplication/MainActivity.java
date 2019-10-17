package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏标题栏
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

//        MyApplication application = (MyApplication) getApplicationContext();
//        if (application.getPhone() != null) {
//            Intent intent = new Intent(getApplicationContext(), Map.class);
//            startActivity(intent);
//        }

//        SharedPreferences sp = getSharedPreferences("login", getApplicationContext().MODE_PRIVATE);
//        String phone = sp.getString("phone", null);
//        if (phone != null) {
//            MyApplication application = (MyApplication) getApplicationContext();
//            application.setPhone(phone);
//            Intent intent = new Intent(getApplicationContext(), Map.class);
//            startActivity(intent);
//        }

        Button bt1 = (Button) findViewById(R.id.button);
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Signin.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            }
        });
    }
}
