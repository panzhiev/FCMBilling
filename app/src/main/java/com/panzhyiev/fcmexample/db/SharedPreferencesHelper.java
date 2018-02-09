package com.panzhyiev.fcmexample.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SharedPreferencesHelper {

    private static final String TAG = "SharedPreferencesHelper";
    private static final String SP_NAME = "SP_CRYPTO_INFO";
    private static SharedPreferencesHelper INSTANCE;
    private SharedPreferences mSharedPreferences;

    public static SharedPreferencesHelper getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SharedPreferencesHelper();
        }
        return INSTANCE;
    }

    private SharedPreferencesHelper() {

    }

    public void initialize(Context context) {
        mSharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        Log.d(TAG, " initialize() SharedPreferencesHelper");
    }

    public void putStringValue(String key, String value) {
        mSharedPreferences.edit().putString(key, value).apply();
    }

    public String getStringValue(String key) {
        return mSharedPreferences.getString(key, "");
    }

    public void putIntValue(String key, int value) {
        mSharedPreferences.edit().putInt(key, value).apply();
    }

    public int getIntValue(String key) {
        return mSharedPreferences.getInt(key, -1);
    }


//    public void putToken(String token) {
//        mSharedPreferences.edit().putString(Config.TOKEN, token).apply();
//    }
//
//    public String getToken() {
//        return mSharedPreferences.getString(Config.TOKEN, "");
//    }


    public SharedPreferences getSharedPreferencesLink() {
        return mSharedPreferences;
    }
}
