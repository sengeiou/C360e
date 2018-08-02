package com.alfred.callnum.global;

import com.alfredbase.BaseApplication;

public class App extends BaseApplication {
    private static final String TAG = App.class.getSimpleName();
    public static App instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
