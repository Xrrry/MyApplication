package com.example.myapplication;

import android.app.Application;

public class MyApplication extends Application {
    private String phone;

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
}
