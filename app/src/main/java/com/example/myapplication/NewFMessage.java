package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

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

public class NewFMessage extends AppCompatActivity {
    private ListView listview_2;
    private ArrayList Friendslist = new ArrayList();
    private SimpleAdapter adapter;
    MyAdapter myadapter;
    private List<java.util.Map<String, Object>> list;
    private Map<String, Object> map;
    Connection c = null;
    PreparedStatement s = null;
    PreparedStatement s2 = null;
    ResultSet rs = null;
    ResultSet rs2 = null;
    private static final String URL = "jdbc:mysql://cdb-hecbapbe.cd.tencentcdb.com:10013/mainDB";
    private static final String USERNAME = "root";
    private static final String PWD = "xiaoruoruo1999";
    final Handler myHandler = new Handler();
    Handler handler = new Handler();
    String phone = "";
    public static String tel[] = new String[1000];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏标题栏
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
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
        setContentView(R.layout.activity_new_fmessage);

        listview_2 = (ListView) this.findViewById(R.id.Listview_2);
        list = new ArrayList<Map<String, Object>>();
        final MyApplication application = (MyApplication) getApplicationContext();
        phone = application.getPhone();
        new Thread() {
            @Override
            public void run() {
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    c = DriverManager.getConnection(URL, USERNAME, PWD);
                    String sql = "select Phone1,Name from newfriends,user where newfriends.Phone2='" + phone + "' and newfriends.Phone1 = user.Phone order by newfriends.ID desc";
                    s = c.prepareStatement(sql);
                    rs = s.executeQuery();
                    int i=0;
                    while (rs.next()) {
                        map = new HashMap<String, Object>();
                        map.put("name", rs.getString("Name"));
                        map.put("tel", rs.getString("Phone1"));
                        tel[i] = rs.getString("Phone1");
                        i++;
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
    }

    final Runnable mUpdateResults = new Runnable() {
        public void run() {
            String[] form = {"name", "tel"};
            int[] to = {R.id.name, R.id.tel};
            myadapter = new MyAdapter(getApplicationContext(), list, R.layout.newshareitem, form, to);
            listview_2.setAdapter(myadapter);
        }
    };

    public class MyAdapter extends SimpleAdapter {
        //上下文
        Context context;
        //private LayoutInflater mInflater;

        public MyAdapter(Context context,
                         List<? extends Map<String, ?>> data, int resource, String[] from,
                         int[] to) {
            super(context, data, resource, from, to);
            this.context = context;
            //this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(final int i, View convertView, ViewGroup viewGroup) {
            View view = super.getView(i, convertView, viewGroup);
            final Button bt1 = (Button) view.findViewById(R.id.agree);
            final Button bt2 = (Button) view.findViewById(R.id.refuse);
            bt1.setTag(i);//设置标签
            bt1.setOnClickListener(new android.view.View.OnClickListener() {

                @Override
                public void onClick(View v) {
//                    handler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            btn.setText("已发送");
//                        }
//                    });
                    final int a = (Integer) v.getTag();
                    final MyApplication application = (MyApplication) getApplicationContext();
                    {
                        new Thread() {
                            @Override
                            public void run() {
                                try {
                                    Class.forName("com.mysql.jdbc.Driver");
                                    c = DriverManager.getConnection(URL, USERNAME, PWD);
                                    String values = "(" + application.getPhone() + "," + tel[a].replaceAll(" ","") + "),(" + tel[a].replaceAll(" ","") + "," + application.getPhone() + ")";
                                    String sql = "INSERT INTO relationship (Phone1, Phone2) VALUES " + values;
                                    s = c.prepareStatement(sql);
                                    s.executeUpdate();
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
                        new Thread() {
                            @Override
                            public void run() {
                                try {
                                    Class.forName("com.mysql.jdbc.Driver");
                                    c = DriverManager.getConnection(URL, USERNAME, PWD);
                                    String sql2 = "delete from newfriends where Phone1='" + tel[a].replaceAll(" ","") + "' and Phone2='" + application.getPhone() + "'";
                                    s2 = c.prepareStatement(sql2);
                                    s2.executeUpdate();
                                } catch (ClassNotFoundException e) {
                                    e.printStackTrace();
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    try {
                                        if (rs != null) rs.close();
                                        if (s2 != null) s2.close();
                                        if (c != null) c.close();
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }.start();
                        Toast.makeText(getApplicationContext(), "已同意", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            return view;
        }
    }
}
