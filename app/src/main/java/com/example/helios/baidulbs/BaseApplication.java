package com.example.helios.baidulbs;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

/**
 * Created by Bonus Liu on 1/8/16.
 * email : wumumawl@163.com
 */
public class BaseApplication extends Application{


    @Override
    public void onCreate() {
        super.onCreate();
        //for application
        SDKInitializer.initialize(getApplicationContext());
    }
}
