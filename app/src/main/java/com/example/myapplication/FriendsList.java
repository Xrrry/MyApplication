package com.example.myapplication;

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

import android.os.Build;
import android.widget.Button;
import android.view.*;
import android.widget.*;
import android.app.Activity;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;

import org.w3c.dom.NameList;


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
    private static final String URL = "jdbc:mysql://cd-cdb-fvu4913e.sql.tencentcdb.com:62763/test";
    private static final String USERNAME = "root";
    private static final String PWD = "xiaoruoruo1999";
    String phone = "";
    private Timer mTimer = null;
    private TimerTask mTimerTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏标题栏
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_friendslist);
        MyApplication application = (MyApplication) getApplicationContext();
        phone = application.getPhone();

        listview_1 = (ListView) this.findViewById(R.id.Listview_1);
        list = new ArrayList<Map<String, Object>>();

        startTimer();
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

    private void startTimer() {
        if (mTimer == null) {
            mTimer = new Timer();
        }

        if (mTimerTask == null) {
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    MyThread t = new MyThread();
                    t.run();
                }
            };
        }

        if (mTimer != null && mTimerTask != null)
            mTimer.schedule(mTimerTask, 0, 5000);
    }

    class MyThread implements Runnable {
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
                    String[] form = {"name", "context"};
                    int[] to = {R.id.pic_name, R.id.pic_context};
                    adapter = new SimpleAdapter(getApplicationContext(), list, R.layout.simpleadapter, form, to);
                    listview_1.setAdapter(adapter);
                }
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
    }

}
