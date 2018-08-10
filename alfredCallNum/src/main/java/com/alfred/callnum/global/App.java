package com.alfred.callnum.global;

import com.alfred.callnum.http.server.CallNumHttpServer;
import com.alfred.callnum.utils.TvPref;
import com.alfredbase.BaseApplication;

import java.io.IOException;

public class App extends BaseApplication {
    private static final String TAG = App.class.getSimpleName();
    public static App instance;
    boolean mbPlayIMG;
    public String VERSION;
    private String posIp;
    private int mainPageType = 1;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        TvPref.init();
        mbPlayIMG = TvPref.readPlayIMGEn();
        VERSION = getAppVersionName();
        CallNumHttpServer callNumHttpServer = new CallNumHttpServer();
        try {
            if (!callNumHttpServer.isAlive()) {
                callNumHttpServer.start();
            }
        } catch (IOException e) {
            callNumHttpServer.stop();
        }
    }

    public boolean getPlayIMGEn() {
        return mbPlayIMG;
    }

    public void setPlayIMGEn(boolean enable) {
        mbPlayIMG = enable;
        TvPref.savePlayIMGEn(enable);
    }

    public String getPosIp() {
        return posIp;
    }

    public void setPosIp(String posIp) {
        this.posIp = posIp;
    }

    public int getMainPageType() {
        return mainPageType;
    }

    public void setMainPageType(int mainPageType) {
        this.mainPageType = mainPageType;
    }
}
