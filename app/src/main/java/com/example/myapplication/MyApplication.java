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
    private List<String> phones;
    private List<String> names;
    private boolean isStartShare = false;
    private String ShareID = "";
    private boolean isFirstTo = true;
    private boolean isNewUser = false;


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

    public boolean getStartShare () {
        return isStartShare;
    }
    public void setStartShare(boolean i) {
        this.isStartShare = i;
    }
    public String getShareID() {
        return ShareID;
    }
    public void setShareID(String i) {
        this.ShareID = i;
    }
    public boolean getFirstTo () {
        return isFirstTo;
    }
    public void setFirstTo (boolean i) {
        this.isFirstTo = i;
    }
    public boolean getNewUser () {
        return isNewUser;
    }
    public void setNewUser(boolean i) {
        this.isNewUser = i;
    }

}
