package com.barracchia.inventario;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {

    private static final String TAG = MyApplication.class.getSimpleName();

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Context getContext(){
        return mContext;
    }
}