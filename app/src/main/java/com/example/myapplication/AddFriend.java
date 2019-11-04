package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddFriend<adapter> extends AppCompatActivity {
    MyAdapter adapter;
    ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    public static String name[] = new String[1000];
    public static String tel[] = new String[1000];
    Map<String, Object> map;
    ListView listview;
    Connection c = null;
    PreparedStatement s = null;
    ResultSet rs = null;
    private static final String URL = "jdbc:mysql://cdb-hecbapbe.cd.tencentcdb.com:10013/mainDB";
    private static final String USERNAME = "root";
    private static final String PWD = "xiaoruoruo1999";
    Handler handler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {

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
        setContentView(R.layout.activity_add_friend);

        adapter = new MyAdapter(this, readContacts(), R.layout.newfrienditem,
                new String[]{"name", "tel", "button"},
                new int[]{R.id.name, R.id.tel, R.id.button});
        listview = (ListView) findViewById(R.id.listview);
        listview.setAdapter(adapter);
        //授权
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    AddFriend.this, new String[]{Manifest.permission.READ_CONTACTS}, 1);
        } else {
            // readContacts();
        }


        Button btn0 = (Button) findViewById(R.id.search_button);
        btn0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et = (EditText) findViewById(R.id.editText);
                final String str = et.getText().toString();
                if (str.equals("")) {
                    Toast.makeText(AddFriend.this, "手机号码格式错误", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (str.length() < 11) {
                    Toast.makeText(AddFriend.this, "手机号码格式错误", Toast.LENGTH_SHORT).show();
                    et.setText(null);
                    return;
                } else {
                    final MyApplication application = (MyApplication) getApplicationContext();
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                Class.forName("com.mysql.jdbc.Driver");
                                c = DriverManager.getConnection(URL, USERNAME, PWD);
                                String values = "(" + application.getPhone() + "," + str + ")";
                                String sql = "INSERT INTO newfriends (Phone1, Phone2) VALUES " + values;
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
                    Toast.makeText(getApplicationContext(), "已发送请求", Toast.LENGTH_SHORT).show();
                    et.setText(null);
                }


            }
        });

    }

    private ArrayList<Map<String, Object>> readContacts() {

        Cursor cursor = null;
        int i = 0;
        try {
            //查询联系人数据
            cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {

                    //获取联系人姓名
                    String names = cursor.getString(cursor.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

                    //获取联系人电话
                    String tels = cursor.getString(cursor.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.NUMBER));

                    map = new HashMap<String, Object>();
                    map.put("name", names);
                    map.put("tel", tels);
                    name[i] = names;
                    tel[i] = tels;
                    i++;
                    map.put("button", "添加");
                    list.add(map);
                }
                //刷新
                adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    //回调方法，无论哪种结果，最终都会回调该方法，之后在判断用户是否授权，
    // 用户同意则调用readContacts（）方法，失败则会弹窗提示失败
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    readContacts();
                } else {
                    Toast.makeText(this, "获取联系人权限失败", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

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
            final Button btn = (Button) view.findViewById(R.id.button);
            btn.setTag(i);//设置标签
            btn.setOnClickListener(new android.view.View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            btn.setText("已发送");
                        }
                    });
                    final int a = (Integer) v.getTag();
                    final MyApplication application = (MyApplication) getApplicationContext();
                    {
                        new Thread() {
                            @Override
                            public void run() {
                                try {
                                    Class.forName("com.mysql.jdbc.Driver");
                                    c = DriverManager.getConnection(URL, USERNAME, PWD);
                                    String values = "(" + application.getPhone() + "," + tel[a].replaceAll(" ","") + ")";
                                    String sql = "INSERT INTO newfriends (Phone1, Phone2) VALUES " + values;
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
                        Toast.makeText(getApplicationContext(), "已发送请求", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            return view;
        }
    }
}




