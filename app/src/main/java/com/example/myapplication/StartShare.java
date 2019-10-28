package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

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
//调用百度API选择一个位置之后，我们需要返回一个经纬度的地理坐标，我们可以通过方法
//startActivityForResult(Intent intent,int RequestCode)来实现
public class StartShare extends AppCompatActivity implements View.OnClickListener {
    private ViewPager viewPager;
    private ArrayList<View> pageView;   //一个用来存放布局的ArrayList
    private ImageView scrollbar;
    private int offset=0;//滚动条初始偏移量
    private int currIndex = 0; //当前页编号
    private int bmpW; //滚动条宽度
    private int one; //一倍滚动量

    private ListView contacts;  //第一个页面的listview
    private ListView contacts1; //第二个页面的listview
    private SimpleAdapter adapter;
    //存放数据
    private List<Map<String,Object>> contactsList;
    private Map<String,Object> map;
    private int image = R.drawable.head;
    private HashMap<Integer,Boolean> hashMap;
    private HashMap<Integer,Boolean> hashMap1;
    Connection c = null;
    PreparedStatement s = null;
    ResultSet rs = null;
    private static final String URL = "jdbc:mysql://cdb-hecbapbe.cd.tencentcdb.com:10013/mainDB";
    private static final String USERNAME = "root";
    private static final String PWD = "xiaoruoruo1999";
    String phone = "";
    final Handler myHandler = new Handler();
    private List<Map<String, Object>> list;
    private ListView listview_1;



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

        setContentView(R.layout.activity_start_share);

        MyApplication application = (MyApplication) getApplicationContext();
        phone = application.getPhone();

        hashMap = new HashMap<Integer, Boolean>();
        for(int i=0;i<100;i++){
            hashMap.put(i,false);
        }
        hashMap1 = new HashMap<Integer, Boolean>();
        for(int i=0;i<100;i++){
            hashMap1.put(i,false);
        }
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        //查找布局文件用LayoutInflater.inflate,进行实例化，对于非直接本页面的必须先实例化，否则在调用页面元素时会返回空指针
        LayoutInflater inflater =getLayoutInflater();
        View view1 = inflater.inflate(R.layout.location_share,null);
        View view2 = inflater.inflate(R.layout.destination,null);
        contacts = (ListView) view1.findViewById(R.id.contacts_view);
        contacts1 = (ListView) view2.findViewById(R.id.contacts_view1);
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
                        map.put("img",R.drawable.head);
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


        Button btn_share = view1.findViewById(R.id.share);
        Button btn_destine = view2.findViewById(R.id.confirm);
        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i=0;i<list.size();i++){
                    if(hashMap.get(i)==true){
                        System.out.println(i);
                    }
                }
            }
        });
        btn_destine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i=0;i<list.size();i++){
                    if(hashMap1.get(i)==true){
                        System.out.println(i);
                    }
                }
            }
        });
        contacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                parent.getAdapter().getItem(position);
                View v = view;
                CheckBox checkBox = view.findViewById(R.id.checkbox);
                if(checkBox.isSelected()){
                    checkBox.setSelected(false);
                    view.setBackgroundColor(Color.parseColor("#ADD8E6"));
                }else{
                    checkBox.setSelected(true);
                    view.setBackgroundColor(Color.parseColor("#25AC66"));
                }
                if(hashMap.get(position)==false)
                    hashMap.put(position,true);
                else hashMap.put(position,false);
                System.out.println(position);
            }
        });
        contacts1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                parent.getAdapter().getItem(position);
                View v = view;
                CheckBox checkBox = view.findViewById(R.id.checkbox);
                if(checkBox.isSelected()){
                    checkBox.setSelected(false);
                    view.setBackgroundColor(Color.parseColor("#ADD8E6"));
                }else{
                    checkBox.setSelected(true);
                    view.setBackgroundColor(Color.parseColor("#25AC66"));
                }
                if(hashMap1.get(position)==false)
                    hashMap1.put(position,true);
                else hashMap1.put(position,false);
                System.out.println(position);

            }
        });

        TextView locationShare = (TextView)findViewById(R.id.location_share);
        TextView destination = (TextView)findViewById(R.id.destination);
        scrollbar = (ImageView)findViewById(R.id.scrollbar);
        locationShare.setOnClickListener(this);
        destination.setOnClickListener(this);
        pageView = new ArrayList<View>();
        //添加想要切换的界面
        pageView.add(view1);
        pageView.add(view2);
        //数据适配器
        PagerAdapter mPagerAdapter = new PagerAdapter() {
            @Override
            //获取当前窗体界面数
            public int getCount() {
                return pageView.size();
            }

            @Override
            //判断是否由对象生成界面
            public boolean isViewFromObject(@NonNull View arg0, @NonNull Object arg1) {
                return arg0 == arg1;
            }

            //使从ViewGroup中移出当前View
            public void destroyItem(View arg0, int arg1, Object arg2) {
                ((ViewPager) arg0).removeView(pageView.get(arg1));
            }

            //返回一个对象，这个对象表明了PagerAdapter适配器选择哪个对象放在当前的ViewPager中
            public Object instantiateItem(View arg0, int arg1){
                ((ViewPager)arg0).addView(pageView.get(arg1));
                return pageView.get(arg1);
            }
        };
        //绑定适配器
        viewPager.setAdapter(mPagerAdapter);
        //设置viewPager的初始界面为第一个界面
        viewPager.setCurrentItem(0);
        //添加切换界面的监听器
        viewPager.addOnPageChangeListener(new MyOnPageChangeListener());
        // 获取滚动条的宽度
        bmpW = BitmapFactory.decodeResource(getResources(),R.drawable.scrollbar).getWidth();
        //为了获取屏幕宽度，新建一个DisplayMetrics对象
        DisplayMetrics displayMetrics = new DisplayMetrics();
        //将当前窗口的一些信息放在DisplayMetrics类中
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        //得到屏幕的宽度
        int screenW = displayMetrics.widthPixels;
        //计算出滚动条初始的偏移量
        offset = (screenW / 2 - bmpW) / 2;
        //计算出切换一个界面时，滚动条的位移量
        one = offset * 2 + bmpW;
        Matrix matrix = new Matrix();
        matrix.postTranslate(offset,0);
        //将滚动条的初始位置设置成与左边界间隔一个offset
        scrollbar.setImageMatrix(matrix);

    }


    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener{

        @Override
        public void onPageSelected(int arg0){
            Animation animation = null;
            switch (arg0){
                case 0:
                    /**
                     * TranslateAnimation的四个属性分别为
                     * float fromXDelta 动画开始的点离当前View X坐标上的差值
                     * float toXDelta 动画结束的点离当前View X坐标上的差值
                     * float fromYDelta 动画开始的点离当前View Y坐标上的差值
                     * float toYDelta 动画开始的点离当前View Y坐标上的差值
                     **/
                    animation = new TranslateAnimation(one, 0, 0, 0);
                    break;
                case 1:
                    animation = new TranslateAnimation(offset, one, 0, 0);
                    break;
            }
            //arg0为切换到的页的编码
            currIndex = arg0;
            // 将此属性设置为true可以使得图片停在动画结束时的位置
            animation.setFillAfter(true);
            //动画持续时间，单位为毫秒
            animation.setDuration(200);
            //滚动条开始动画
            scrollbar.startAnimation(animation);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.location_share:
                //点击“位置共享”时切换到第一页
                viewPager.setCurrentItem(0);
                break;
            case R.id.destination:
                //点击“目的地”时切换的第二页
                viewPager.setCurrentItem(1);
                break;
        }
    }

    final Runnable mUpdateResults = new Runnable() {
        public void run() {
            String[] form = {"img","name", "context"};
            int[] to = {R.id.image,R.id.name, R.id.tel};
            adapter = new SimpleAdapter(getApplicationContext(), list, R.layout.list_item, form, to);
            contacts.setAdapter(adapter);
        }
    };
}
