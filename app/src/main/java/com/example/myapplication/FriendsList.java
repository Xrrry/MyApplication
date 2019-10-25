package com.example.myapplication;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.widget.Button;
import android.view.*;
import android.widget.*;
import android.app.Activity;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;



public class FriendsList extends AppCompatActivity {
    private ListView listview_1;
    private ArrayList Friendslist = new ArrayList();
    private SimpleAdapter adapter;
    private List<Map<String, Object>> list;
    private Map<String, Object> map;
    private String[] names = {};
    private String[] number = {};
    private Button button_1;
    private Button button_2;
    Connection c = null;
    PreparedStatement s = null;
    ResultSet rs = null;
    private static final String URL = "jdbc:mysql://cdb-hecbapbe.cd.tencentcdb.com:10013/mainDB";    private static final String USERNAME = "root";
    private static final String PWD = "xiaoruoruo1999";
    String phone = "";
    final Handler myHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        setContentView(R.layout.activity_friendslist);
        MyApplication application = (MyApplication) getApplicationContext();
        phone = application.getPhone();

        listview_1 = (ListView) this.findViewById(R.id.Listview_1);
        list = new ArrayList<Map<String, Object>>();

        new Thread() {
            @Override
            public void run() {
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    c = DriverManager.getConnection(URL, USERNAME, PWD);
                    String sql = "select Phone2,Name from relationship,user where relationship.Phone1='" + phone + "' and user.Phone=relationship.Phone2";
                    s = c.prepareStatement(sql);
                    rs = s.executeQuery();
                    while (rs.next()) {
                        System.out.println(rs.getString("Phone2") + "   " + rs.getString("Name"));
                        map = new HashMap<String, Object>();
                        map.put("name", rs.getString("Name"));
                        map.put("context", rs.getString("Phone2"));
                        list.add(map);
                    }
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
        button_1 = (Button) this.findViewById(R.id.Image_button_1);
        button_2 = (Button) this.findViewById(R.id.add_button);
        button_1.setOnClickListener(new View.OnClickListener()//请求按钮跳转
        {
            @Override
            public void onClick(View v) {
                //Intent intent=new Intent(MainActivity.this,*.class);
                //startActivity(intent);
            }
        });
        button_2.setOnClickListener(new View.OnClickListener()//添加按钮跳转
        {
            @Override
            public void onClick(View v) {
                //Intent intent=new Intent(MainActivity.this,*.class)
                //startActivity(intent);
            }
        });
    }

    final Runnable mUpdateResults = new Runnable() {
        public void run() {
            String[] form = {"name", "context"};
            int[] to = {R.id.pic_name, R.id.pic_context};
            adapter = new SimpleAdapter(getApplicationContext(), list, R.layout.simpleadapter, form, to);
            listview_1.setAdapter(adapter);
        }
    };
}

