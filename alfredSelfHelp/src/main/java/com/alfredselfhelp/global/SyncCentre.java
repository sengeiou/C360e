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

    public void employeeId(Context context, String ip, Map<String, Object> parameters,
                           Handler handler) {
        HttpAPI.employeeId(context, parameters,
                getAbsolutePOSUrlByIp(ip, APIName.KPM_EMPLOYEE_ID), httpClient, handler);


    }

    public void login(Context context, Map<String, Object> parameters,
                      Handler handler) {
        HttpAPI.getItem(context, getAbsolutePOSUrlByIp(App.instance.getPairingIp(), APIName.ITEM_GETITEM),
                parameters, httpClient);
        HttpAPI.login(context, parameters,
                getAbsoluteUrl(APIName.LOGIN_LOGINVERIFY), httpClient, handler);
    }

//	public void getRestaurantInfo(Context context,
//			Map<String, Object> parameters, Handler handler) {
//		HttpAPI.getRestaurantInfo(context, parameters,
//				getAbsoluteUrl(APIName.RESTAURANT_GETRESTAURANTINFO),
//				httpClient, handler);
//	}

    public void syncCommonData(Context context, String ip, Map<String, Object> parameters, Handler handler) {

        HttpAPI.getUser(context, getAbsolutePOSUrlByIp(ip, APIName.USER_GETUSER),
                parameters, httpClient, handler);

        HttpAPI.getItemCategory(context,
                getAbsolutePOSUrlByIp(ip, APIName.ITEM_GETITEMCATEGORY), parameters,
                httpClient, handler);

        HttpAPI.getModifier(context, getAbsolutePOSUrlByIp(ip, APIName.ITEM_GETMODIFIER),
                parameters, httpClient, handler);

        HttpAPI.getTax(context, getAbsolutePOSUrlByIp(ip, APIName.TAX_GETTAX), parameters,
                httpClient, handler);

        HttpAPI.getHappyHour(context,
                getAbsolutePOSUrlByIp(ip, APIName.HAPPYHOUR_GETHAPPYHOUR), parameters,
                httpClient, handler);
//
//		HttpAPI.getItem(context, getAbsolutePOSUrlByIp(ip, APIName.ITEM_GETITEM),
//				parameters, httpClient);
    }

    public void pairingComplete(Context context, String ip,
                                Map<String, Object> parameters, Handler handler) {
        HttpAPI.pairingComplete(context, parameters, getAbsolutePOSUrlByIp(ip, APIName.PAIRING_COMPLETE), httpClient, handler);
    }

    public void getPlaceInfo(Context context, String ip, Map<String, Object> parameters, Handler handler) {
        HttpAPI.getPlaceInfo(context, parameters,
                getAbsolutePOSUrlByIp(ip, APIName.RESTAURANT_GETPLACEINFO), httpClient, handler);
    }

    /*
     * select tables and get order ID*/
    public void selectTables(Context context, Map<String, Object> parameters,
                             Handler handler) {
        HttpAPI.selectTables(context, parameters,
                getAbsoluteUrl(APIName.SELECT_TABLES), httpClient, handler);
    }

    /*
     * Get bill print */
    public void getBillPrint(Context context, Map<String, Object> parameters,
                             Handler handler) {
        HttpAPI.getBillPrint(context, parameters,
                getAbsoluteUrl(APIName.GET_BILL), httpClient, handler);
    }

    /*
    print Bill
     */
    public void printBill(Context context, Map<String, Object> parameters,
                          Handler handler) {
        HttpAPI.printBill(context, parameters,
                getAbsoluteUrl(APIName.PRINT_BILL), httpClient, handler);
    }

    // get OrderDetail Un Waiter Create
    public void handlerGetOrderDetails(Context context, Map<String, Object> parameters,
                                       Handler handler) {
        HttpAPI.handlerGetOrderDetails(context, parameters, getAbsoluteUrl(APIName.GET_ORDERDETAILS), httpClient, handler);
    }

    private String getAbsoluteUrl(String url) {
        return "http://" + getIp() + ":" + APPConfig.HTTP_SERVER_PORT + "/"
                + url;
    }

    private String getAbsolutePOSUrlByIp(String ip, String url) {
        return "http://" + ip + ":" + APPConfig.HTTP_SERVER_PORT + "/"
                + url;
    }

    public void commitOrderAndOrderDetails(Context context, Map<String, Object> parameters,
                                           Handler handler) {
        HttpAPI.commitOrderAndOrderDetails(context, parameters, getAbsoluteUrl(APIName.COMMIT_ORDER), httpClient, handler);
    }

    public void getKotNotifications(Context context, Map<String, Object> parameters,
                                    Handler handler) {
        HttpAPI.getKotNotification(context, parameters, getAbsoluteUrl(APIName.GET_KOT_NOTIFICATION), httpClient, handler);
    }

    public void handlerCollectKotItem(Context context, Map<String, Object> parameters,
                                      Handler handler) {
        HttpAPI.handlerCollectKotItem(context, parameters, getAbsoluteUrl(APIName.COLLECT_KOT_ITEM), httpClient, handler);
    }

    public void logout(Context context, Map<String, Object> parameters,
                       Handler handler) {
        HttpAPI.logout(context, parameters, getAbsoluteUrl(APIName.LOGIN_LOGOUT), httpClient, handler);
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
