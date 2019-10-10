package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

public class signin2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Button bt2 = findViewById(R.id.button2);
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i2 = new Intent(signin2.this, Main2Activity.class);
                EditText ed1 = (EditText) findViewById(R.id.editText);
                EditText ed2 = (EditText) findViewById(R.id.editText2);
                String name = ed1.getText().toString();
                String passwd = ed2.getText().toString();
                i2.putExtra("name",name);
                i2.putExtra("passwd",passwd);
                startActivity(i2);
            }
        });

        setContentView(R.layout.activity_signin2);
    }
}
