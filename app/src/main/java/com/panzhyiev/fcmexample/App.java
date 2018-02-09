package com.panzhyiev.fcmexample;

import android.app.Application;

import com.panzhyiev.fcmexample.db.SharedPreferencesHelper;


public class App extends Application {

    private static Application INSTANCE;

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferencesHelper.getInstance().initialize(this);
        INSTANCE = this;
    }

    public static Application getInstance() {
        return INSTANCE;
    }
}
