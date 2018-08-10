package com.alfredposclient.http.subpos;

import android.app.DownloadManager;
import android.content.Context;
import android.os.Handler;
import android.view.View;

import com.alfredbase.http.AsyncHttpResponseHandlerEx;
import com.alfredbase.http.DownloadFactory;
import com.alfredbase.http.ResultCode;
import com.alfredbase.javabean.system.VersionUpdate;
import com.alfredbase.store.Store;
import com.alfredbase.utils.DialogFactory;
import com.alfredposclient.global.App;
import com.alfredposclient.global.UIHelp;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SubPosHttpAPI {

    // for KDS HTTP Server
    public static final String EOF = "\r\nEOF\r\n";
    private static final String CONTENT_TYPE = "text/plain;charset=UTF-8";

    /**
     * 订单数据
     */
    public static final int ORDER_DATA = 10;
    /**
     * 支付数据
     */
    public static final int REPORT_DATA = 20;

    /**
     * 同步订单记录数据
     */
    public static final int LOG_DATA = 11;

    /**
     * 同步开关餐厅开关session的记录数据
     */
    public static final int OPEN_CLOSE_SESSION_RESTAURANT = 90;


    /**
     * 用于更新网络订单状态
     */
    public static final int NETWORK_ORDER_STATUS_UPDATE = 1001;



    public static void login(final Context context, Map<String, Object> parameters,
                             String url, AsyncHttpClient httpClient, final Handler handler) {
        if (parameters != null) {
            parameters.put("appVersion", App.instance.VERSION);
        }
        try {
            httpClient.post(context, url,
                    new StringEntity(new Gson().toJson(parameters) + EOF,
                            "UTF-8"), CONTENT_TYPE,
                    new AsyncHttpResponseHandlerEx() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers,
                                              byte[] responseBody) {
                            super.onSuccess(statusCode, headers, responseBody);
                            String body = new String(responseBody);
                            if (resultCode == ResultCode.SUCCESS) {
                                SubPosHttpAnalysis.login(body, handler);
                            } else {
                                elseResultCodeAction(resultCode, body);
                            }
                        }
                        @Override
                        public void onFailure(int statusCode, Header[] headers,
                                              byte[] responseBody, Throwable error) {
                            // TODO Auto-generated method stub
                            handler.sendMessage(handler.obtainMessage(ResultCode.CONNECTION_FAILED,error));
                            super.onFailure(statusCode, headers, responseBody, error);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void chooseRevenue(final Context context,
                             String url, AsyncHttpClient httpClient, final Handler handler) {
        Map<String, Object> parameters = new HashMap<>();
        if (parameters != null) {
            parameters.put("appVersion", App.instance.VERSION);
        }
        try {
            httpClient.post(context, url,
                    new StringEntity(new Gson().toJson(parameters) + EOF,
                            "UTF-8"), CONTENT_TYPE,
                    new AsyncHttpResponseHandlerEx() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers,
                                              byte[] responseBody) {
                            super.onSuccess(statusCode, headers, responseBody);
                            String body = new String(responseBody);
                            if (resultCode == ResultCode.SUCCESS) {
                                handler.sendEmptyMessage(ResultCode.SUCCESS);
                            } else {
                                elseResultCodeAction(resultCode, body);
                            }
                        }
                        @Override
                        public void onFailure(int statusCode, Header[] headers,
                                              byte[] responseBody, Throwable error) {
                            // TODO Auto-generated method stub
                            handler.sendMessage(handler.obtainMessage(ResultCode.CONNECTION_FAILED,error));
                            super.onFailure(statusCode, headers, responseBody, error);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 返回码不需要特殊处理的提醒
    private static  void elseResultCodeAction(final int resultCode, final String responseBody){
        App.getTopActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                String information = null;
                try {
                    JSONObject object = new JSONObject(responseBody);
                    information = object.optString("posVersion");
                    if(object.has("versionUpdate")){
                        final VersionUpdate versionUpdate = new Gson().fromJson(object.getString("versionUpdate"), VersionUpdate.class);
                        if (versionUpdate != null && App.instance.getAppVersionCode() < versionUpdate.getVersionCode()) {
                            if (versionUpdate != null && App.instance.getAppVersionCode() < versionUpdate.getVersionCode()) {
                                DialogFactory.showUpdateVersionDialog(App.getTopActivity(), versionUpdate, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        long posUpdateId = DownloadFactory.downloadApk(App.getTopActivity(), (DownloadManager) App.getTopActivity().getSystemService(Context.DOWNLOAD_SERVICE), versionUpdate.getKdsDownload(), Store.getLong(App.getTopActivity(), "posUpdateId"));
                                        Store.putLong(App.getTopActivity(), "posUpdateId", posUpdateId);
                                    }
                                }, null);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                App.getTopActivity().dismissLoadingDialog();
                UIHelp.showToast(App.getTopActivity(), ResultCode.getErrorResultStrByCode(App.instance, resultCode, information));
            }
        });
    }
}
