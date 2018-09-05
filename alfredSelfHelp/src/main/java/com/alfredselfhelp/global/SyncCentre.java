package com.alfredselfhelp.global;

import android.content.Context;
import android.os.Handler;

import com.alfredbase.APPConfig;
import com.alfredbase.http.APIName;
import com.alfredselfhelp.http.HttpAPI;
import com.loopj.android.http.AsyncHttpClient;

import java.util.Map;

public class SyncCentre {
    private String ip;
    private static AsyncHttpClient httpClient;

    private static SyncCentre instance;

    private SyncCentre() {

    }

    public static SyncCentre getInstance() {
        init();
        return instance;
    }

    private static void init() {
        if (instance == null) {
            instance = new SyncCentre();

            httpClient = new AsyncHttpClient();
            httpClient.addHeader("Connection", "close");
            httpClient.setMaxRetriesAndTimeout(0, 30 * 1000);
            httpClient.setTimeout(30 * 1000);
        }
    }

    public void cancelAllRequests() {
        if (httpClient != null)
            httpClient.cancelAllRequests(true);
    }

    public void login(Context context, Map<String, Object> parameters, final Handler handler) {
        HttpAPI.login(context, parameters, getAbsoluteUrl(APIName.KPMG_LOGIN), httpClient, handler);
    }

    public void updateAllData(Context context, final Handler handler) {
        HttpAPI.updateAllData(context, getAbsoluteUrl(APIName.KPMG_UPDATE_DATA), httpClient, handler);
    }


    public void commitOrder(Context context){
        HttpAPI.commitOrder(context, getAbsoluteUrl(APIName.KPMG_COMMIT_ORDER), httpClient);

    }

    private String getAbsoluteUrl(String url) {
        return "http://" + getIp() + ":" + APPConfig.HTTP_SERVER_PORT + "/"
                + url;
    }

    private String getAbsolutePOSUrlByIp(String ip, String url) {
        return "http://" + ip + ":" + APPConfig.HTTP_SERVER_PORT + "/"
                + url;
    }

    public String getIp() {
        if (ip == null) {
            ip = App.instance.getPairingIp();
        }
        ;
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

}
