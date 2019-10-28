package com.example.myapplication;

import android.Manifest;
import android.app.Application;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.List;

public class MyApplication extends Application {
    private String phone;
    private String name;
    private double la = 0;
    private double ln = 0;
    @Override
    public void onCreate() {
        super.onCreate();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public double getLa() {
        return la;
    }
    public void setLa(double la){
        this.la = la;
    }

    public double getLn() {
        return ln;
    }
    public void setLn(double ln){
        this.ln = ln;
    }

}
