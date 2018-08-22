package com.alfred.callnum.http;

import android.content.Context;
import android.os.Handler;

import com.alfred.callnum.global.App;
import com.alfred.callnum.utils.UIHelp;
import com.alfredbase.APPConfig;
import com.alfredbase.ParamConst;
import com.alfredbase.global.CoreData;
import com.alfredbase.http.APIName;
import com.alfredbase.http.AsyncHttpResponseHandlerEx;
import com.alfredbase.http.ResultCode;
import com.alfredbase.javabean.KotItemDetail;
import com.alfredbase.javabean.KotItemModifier;
import com.alfredbase.javabean.KotSummary;
import com.alfredbase.javabean.model.MainPosInfo;
import com.alfredbase.javabean.model.SessionStatus;
import com.alfredbase.store.Store;
import com.alfredbase.store.sql.KotItemDetailSQL;
import com.alfredbase.store.sql.KotItemModifierSQL;
import com.alfredbase.store.sql.KotSummarySQL;
import com.alfredbase.utils.CommonUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* Wrapper of all HTTP Requests (KDS -> Main POS) */
public class SyncCentre {
    private String ip;
    private boolean connected = false;

    private static AsyncHttpClient httpClient;

    private static SyncCentre instance;
    private static final String EOF = "\r\nEOF\r\n";
    private static final String CONTENT_TYPE = "text/plain;charset=UTF-8";

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
            httpClient.setMaxRetriesAndTimeout(1, 5 * 1000);
            httpClient.setTimeout(25 * 1000);
        }
    }


    public static void assignRevenue(final Context context, String ip, final Handler handler) {
        String url = "http://" + ip + ":" + APPConfig.HTTP_SERVER_PORT + "/" + APIName.CALLNUM_ASSIGNREVENUE;
        Map<String, String> parameters = new HashMap<>();
        parameters.put("ip", CommonUtil.getLocalIpAddress());
        try {
            httpClient.post(context, url,
                    new StringEntity(new Gson().toJson(parameters) + EOF,
                            "UTF-8"),
                    CONTENT_TYPE,
                    new AsyncHttpResponseHandlerEx() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            super.onSuccess(statusCode, headers, responseBody);
                            if (resultCode == ResultCode.SUCCESS) {

                                revenue(statusCode, headers,
                                        responseBody, context);
                                handler.sendMessage(handler
                                        .obtainMessage(resultCode));
                            } else {
                                elseResultCodeAction(resultCode, statusCode, headers, responseBody);
                                App.instance.setPosIp("");
                            }

                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            handler.sendMessage(handler.obtainMessage(ResultCode.CONNECTION_FAILED, error));
                            super.onFailure(statusCode, headers, responseBody, error);
                        }
                    });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    // 返回码不需要特殊处理的提醒
    private static void elseResultCodeAction(final int resultCode, int statusCode, Header[] headers, final byte[] responseBody) {
        App.getTopActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                App.getTopActivity().dismissLoadingDialog();
                UIHelp.showToast(App.getTopActivity(), ResultCode.getErrorResultStrByCode(App.instance, resultCode, ""));
            }
        });
    }


    public static String revenue(int statusCode, Header[] headers,
                                 byte[] responseBody, Context context) {
        try {
            Gson gson = new Gson();
            JSONObject object = new JSONObject(new String(responseBody));

            int callType = object.optInt("calltype");
            App.instance.setMainPageType(callType);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
