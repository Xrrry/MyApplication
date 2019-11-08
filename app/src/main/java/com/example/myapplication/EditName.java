package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.service.autofill.OnClickAction;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EditName extends AppCompatActivity {
    Connection c = null;
    PreparedStatement s = null;
    ResultSet rs = null;
    private static final String URL = "jdbc:mysql://cdb-hecbapbe.cd.tencentcdb.com:10013/mainDB";
    private static final String USERNAME = "root";
    private static final String PWD = "xiaoruoruo1999";
    String phone = "";
    String name = "";
    Handler myHandler = new Handler();
    public static EditName instance;

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
        setContentView(R.layout.activity_edit_name);
        final MyApplication application = (MyApplication) getApplicationContext();
        phone = application.getPhone();
        name = application.getName();

        ((EditText)findViewById(R.id.edittext1)).setHint(name);
    }
    public void onClick(View view) {
        new Thread() {
            @Override
            public void run() {
                try {
                    String a = ((EditText)findViewById(R.id.edittext1)).getText().toString();
                    Class.forName("com.mysql.jdbc.Driver");
                    c = DriverManager.getConnection(URL, USERNAME, PWD);
                    String sql = "update user set Name='"+ a + "' where Phone='"+ phone + "'";
                    s = c.prepareStatement(sql);
                    s.executeUpdate();
                    myHandler.post(mUpdateResults);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (rs != null) rs.close();
                        if (s != null) s.close();
                        if (c != null) c.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }
    final Runnable mUpdateResults = new Runnable() {
        public void run() {
            String a = ((EditText)findViewById(R.id.edittext1)).getText().toString();
            ((MyApplication)getApplicationContext()).setName(a);
            Toast.makeText(getApplicationContext(), "修改成功", Toast.LENGTH_SHORT).show();
            final MyApplication application = (MyApplication) getApplicationContext();
            if(application.getNewUser()) {
                application.setNewUser(false);
                Intent intent = new Intent(getApplicationContext(), Map.class);
                startActivity(intent);
            }
            else {
                finish();
            }
        }
    };
}
