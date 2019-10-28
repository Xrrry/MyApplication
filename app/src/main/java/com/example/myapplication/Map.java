package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.xujiaji.happybubble.BubbleDialog;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

class User {
    String phone;
    String name;
    Marker marker;
    List<LatLng> list;
}


public class Map extends AppCompatActivity implements BDLocationListener {
    MapView mMapView;
    private BaiduMap mBaiduMap;
    LocationClient mLocationClient;
    private boolean isFirstLoc = true;
    BDLocation location = new BDLocation();
    LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
    Connection c = null;
    PreparedStatement s = null;
    ResultSet rs = null;
    private static final String URL = "jdbc:mysql://cdb-hecbapbe.cd.tencentcdb.com:10013/mainDB";
    private static final String USERNAME = "root";
    private static final String PWD = "xiaoruoruo1999";
    private Timer mTimer = null;
    private Timer mTimer1 = null;
    private Timer mTimer2 = null;
    private TimerTask mTimerTask = null;
    private TimerTask mTimerTask1 = null;
    private TimerTask mTimerTask2 = null;
    String phone = "";
    String name = "";
    String la = null;
    String ln = null;
    Marker mymarker = null;
    List<LatLng> points = new ArrayList<LatLng>();
    Overlay mPolyline;
    Boolean isOnSend = false;
    Boolean isOnReceive = false;
    Date date = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    public static Map instance;
    List<User> users = new ArrayList<User>();
    List<Overlay> polyLineList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        instance = this;
        if (Launcher.instance != null) {
            Launcher.instance.finish();
        }
        if (MainActivity.instance != null) {
            MainActivity.instance.finish();
        }
        if (Signin.instance != null) {
            Signin.instance.finish();
        }
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

        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        SDKInitializer.initialize(getApplicationContext());
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.GCJ02);

        setContentView(R.layout.activity_map);
        mMapView = (MapView) findViewById(R.id.bmapView);
        mMapView.removeViewAt(1);
        mMapView.showZoomControls(false);
        mLocationClient = new LocationClient(getApplicationContext()); //声明LocationClient类
        mLocationClient.registerLocationListener(this);//注册监听函数
        initLocation();
        // 开启定位图层
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);//显示定位层并且可以触发定位,默认是flase
        mLocationClient.start();//开启定位


        final MyApplication application = (MyApplication) getApplicationContext();
        phone = application.getPhone();
        new Thread() {
            @Override
            public void run() {
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    c = DriverManager.getConnection(URL, USERNAME, PWD);
                    String sql = "select Name from user where Phone='" + phone + "'";
                    s = c.prepareStatement(sql);
                    rs = s.executeQuery();
                    rs.next();
                    application.setName(rs.getString("Name"));
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

        Button bt1 = (Button) findViewById(R.id.button6);


        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//
                Intent i1 = new Intent(Map.this, My.class);

//                i1.setData(Uri.parse("baidumap://map/direction?origin=name:对外经贸大学|latlng:39.98871,116.43234&destination=西直门&coord_type=bd09ll&mode=transit&sy=3&index=0&target=1&src=andr.baidu.openAPIdemo"));
                startActivity(i1);

            }
        });

        Button bt2 = (Button) findViewById(R.id.button7);
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent i = new Intent(Map.this, Friend.class);
//                startActivity(i);

//                Intent sendIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + "17638591897"));//跳转到拨号界面，同时传递电话号码
//                sendIntent.putExtra("sms_body", "test");
//                startActivity(sendIntent);
                Intent i = new Intent(Map.this, FriendsList.class);
                startActivity(i);
            }
        });

        Button bt3 = (Button) findViewById(R.id.button4);
        bt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent i = new Intent(Map.this, Friend.class);
//                startActivity(i);

//                Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "17638591897"));//跳转到拨号界面，同时传递电话号码
//                startActivity(dialIntent);
                if (isOnSend == false) {
                    isOnSend = true;
                    startTimer();
                    Toast.makeText(getApplicationContext(), "开始发送定位", Toast.LENGTH_SHORT).show();
                } else {
                    isOnSend = false;
                    mTimer.cancel();
                    mTimer = null;
                    mTimerTask.cancel();
                    mTimerTask = null;
                    Toast.makeText(getApplicationContext(), "停止发送定位", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button bt4 = (Button) findViewById(R.id.button3);
        bt4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapStatusUpdate update = MapStatusUpdateFactory.newLatLngZoom(ll, 16);//设置地图中心及缩放级别
                mBaiduMap.animateMapStatus(update);
            }
        });

        Button bt5 = (Button) findViewById(R.id.button5);
//        bt5.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MyLocationData locData = new MyLocationData.Builder()
//                        .accuracy(location.getRadius())
//                        // 此处设置开发者获取到的方向信息，顺时针0-360
//                        .direction(100).latitude(location.getLatitude())
//                        .longitude(location.getLongitude()).build();
//                // 设置定位数据
//                mBaiduMap.setMyLocationData(locData);
//            }
//        });
        bt5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i2 = new Intent(Map.this, StartShare.class);
                startActivity(i2);
            }
        });

        Button bt6 = (Button) findViewById(R.id.button11);

        Button bt7 = (Button) findViewById(R.id.button12);
        bt7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
            }
        });

        Button bt8 = (Button) findViewById(R.id.button13);
        bt8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
            }
        });
        Button bt11 = (Button) findViewById(R.id.button11);
        bt11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOnReceive == false) {
                    isOnReceive = true;
                    startTimer1();
                    Toast.makeText(getApplicationContext(), "开始接收定位", Toast.LENGTH_SHORT).show();
                } else {
                    isOnReceive = false;
                    mTimer1.cancel();
                    mTimer1 = null;
                    mTimerTask1.cancel();
                    mTimerTask1 = null;
                    Toast.makeText(getApplicationContext(), "停止接收定位", Toast.LENGTH_SHORT).show();
                }
            }
        });


        final BubbleDialog bd = new BubbleDialog(this)
                .addContentView(LayoutInflater.from(this).inflate(R.layout.activity_bubble1, null))
                .setClickedView(bt6)
                .setPosition(BubbleDialog.Position.TOP)
                .setOffsetX(100)
                .setOffsetY(250)
                .calBar(true);
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            //marker被点击时回调的方法
            //若响应点击事件，返回true，否则返回false
            //默认返回false
            @Override
            public boolean onMarkerClick(Marker marker) {
                bd.show();
                return false;
            }
        });

        BaiduMap.OnMapClickListener listener = new BaiduMap.OnMapClickListener() {
            /**
             * 地图单击事件回调函数
             *
             * @param point 点击的地理坐标
             */
            @Override
            public void onMapClick(LatLng point) {

            }

            /**
             * 地图内 Poi 单击事件回调函数
             *
             * @param mapPoi 点击的 poi 信息
             */
            @Override
            public void onMapPoiClick(MapPoi mapPoi) {

            }
        };
//设置地图单击事件监听
        mBaiduMap.setOnMapClickListener(listener);

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
                String values = "(" + phone + "," + la + "," + ln + ",'" + dateFormat.format(date) + "')";
                String sql = "INSERT INTO location (Phone, Lat, Lng , CTime) VALUES " + values;
                if (la != null) {
                    s = c.prepareStatement(sql);
                    s.executeUpdate();
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

    private void startTimer1() {
        if (mTimer1 == null) {
            mTimer1 = new Timer();
        }

        if (mTimerTask1 == null) {
            mTimerTask1 = new TimerTask() {
                @Override
                public void run() {
                    MyThread1 t1 = new MyThread1();
                    t1.run();
                }
            };
        }

        if (mTimer1 != null && mTimerTask1 != null)
            mTimer1.schedule(mTimerTask1, 0, 5000);
    }

    class MyThread1 implements Runnable {
        public void run() {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                c = DriverManager.getConnection(URL, USERNAME, PWD);
                String sql = "select * from location where Phone = '17638591897' order by CTime desc limit 1";
                s = c.prepareStatement(sql);
                rs = s.executeQuery();
                rs.next();
                if (mymarker != null) {
                    LatLng p = new LatLng(rs.getDouble("Lat"), rs.getDouble("Lng"));
                    mymarker.setPosition(p);
                    if (points.size() == 0) {
                        points.add(p);
                    } else if (points.get(points.size() - 1) != p) {
                        points.add(p);
                        if (points.size() == 2) {
                            OverlayOptions mOverlayOptions = new PolylineOptions()
                                    .width(30)
                                    .color(0xAA59C9A5)
                                    .points(points);

                            mPolyline = mBaiduMap.addOverlay(mOverlayOptions);
                        } else {
                            OverlayOptions mOverlayOptions = new PolylineOptions()
                                    .width(30)
                                    .color(0xAA59C9A5)
                                    .points(points);
                            mPolyline.remove();
                            mPolyline = mBaiduMap.addOverlay(mOverlayOptions);
                        }
                    }
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

    private void startTimer2() {
        if (mTimer2 == null) {
            mTimer2 = new Timer();
        }

        if (mTimerTask2 == null) {
            mTimerTask2 = new TimerTask() {
                @Override
                public void run() {
                    MyThread2 t2 = new MyThread2();
                    t2.run();
                }
            };
        }

        if (mTimer2 != null && mTimerTask2 != null)
            mTimer2.schedule(mTimerTask2, 0, 5000);
    }

    class MyThread2 implements Runnable {
        public void run() {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                c = DriverManager.getConnection(URL, USERNAME, PWD);
                for (int i = 0; i < users.size(); i++) {
                    User user = users.get(i);
                    String sql = "select * from location where Phone = '" + user.phone + "' order by CTime desc limit 1";
                    s = c.prepareStatement(sql);
                    rs = s.executeQuery();
                    if (rs.next()) {
                        if (user.marker != null) {
                            LatLng p = new LatLng(rs.getDouble("Lat"), rs.getDouble("Lng"));
                            user.marker.setPosition(p);
                            if (user.list.size() == 0) {
                                user.list.add(p);
                            } else if (user.list.get(user.list.size() - 1) != p) {
                                user.list.add(p);
                                if (user.list.size() == 2) {
                                    OverlayOptions mOverlayOptions = new PolylineOptions()
                                            .width(30)
                                            .color(0xAA59C9A5)
                                            .points(user.list);

                                    polyLineList.remove(i);
                                    polyLineList.add(i, mBaiduMap.addOverlay(mOverlayOptions));
                                } else {
                                    OverlayOptions mOverlayOptions = new PolylineOptions()
                                            .width(30)
                                            .color(0xAA59C9A5)
                                            .points(user.list);
                                    polyLineList.get(i).remove();
                                    polyLineList.remove(i);
                                    polyLineList.add(i, mBaiduMap.addOverlay(mOverlayOptions));
                                }
                            }
                        }
                    }
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


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123) {            //当然权限多了，建议使用Switch，不必纠结于此
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "权限申请成功", Toast.LENGTH_SHORT).show();
                setContentView(R.layout.activity_map);
                mMapView = (MapView) findViewById(R.id.bmapView);
                mMapView.removeViewAt(1);
                mMapView.showZoomControls(false);
                mLocationClient = new LocationClient(getApplicationContext()); //声明LocationClient类
                mLocationClient.registerLocationListener(this);//注册监听函数
                initLocation();
                // 开启定位图层
                mBaiduMap = mMapView.getMap();
                mBaiduMap.setMyLocationEnabled(true);//显示定位层并且可以触发定位,默认是flase
                mLocationClient.start();//开启定位
            } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "权限申请失败，用户拒绝权限", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        int span = 1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }

    @Override
    public void onReceiveLocation(BDLocation location) {

        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(location.getRadius())
                // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(100).latitude(location.getLatitude())
                .longitude(location.getLongitude()).build();
        // 设置定位数据
        la = String.valueOf(location.getLatitude());
        ln = String.valueOf(location.getLongitude());
        mBaiduMap.setMyLocationData(locData);
        if (isFirstLoc) {
            ll = new LatLng(location.getLatitude(), location.getLongitude());
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLngZoom(ll, 16);//设置地图中心及缩放级别
            mBaiduMap.animateMapStatus(update);
            isFirstLoc = false;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
        final MyApplication application = (MyApplication) getApplicationContext();
        if (application.getStartShare()) {
            LatLng point = new LatLng(0, 0);
            //构建Marker图标
            BitmapDescriptor bitmap = BitmapDescriptorFactory
                    .fromResource(R.drawable.circle2);
            //构建MarkerOption，用于在地图上添加Marker
            final OverlayOptions option = new MarkerOptions()
                    .position(point) //必传参数
                    .icon(bitmap) //必传参数
                    //设置平贴地图，在地图中双指下拉查看效果
                    .flat(true);
            for (int i = 0; i < application.getPhones().size(); i++) {
                users.get(i).phone = application.getPhones().get(i);
                users.get(i).name = application.getNames().get(i);
                //在地图上添加Marker，并显示
                users.get(i).marker = (Marker) mBaiduMap.addOverlay(option);
            }
            startTimer2();
            Toast.makeText(getApplicationContext(), "开始接受定位", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }
}
