package com.alfred.callnum.global;

import com.alfred.callnum.utils.TvPref;
import com.alfredbase.BaseApplication;

public class App extends BaseApplication {
    private static final String TAG = App.class.getSimpleName();
    public static App instance;
    boolean mbPlayIMG;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        TvPref.init();
        mbPlayIMG = TvPref.readPlayIMGEn();
    }

    public boolean getPlayIMGEn() {
        return mbPlayIMG;
    }

    public void setPlayIMGEn(boolean enable) {
        mbPlayIMG = enable;
        TvPref.savePlayIMGEn(enable);
    }
}
