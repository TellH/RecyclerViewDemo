package com.example.tellh.recyclerviewdemo;

import android.app.Application;

/**
 * Created by tlh on 2017/1/20 :)
 */
public class AndroidApplication extends Application {
    private static AndroidApplication instance;

    public static AndroidApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}