package com.alfredbase.utils;

import android.text.TextUtils;

import com.alfredbase.javabean.KotItemDetail;
import com.alfredbase.javabean.KotSummary;
import com.alfredbase.javabean.KotSummaryLog;
import com.alfredbase.javabean.model.KDSDevice;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arif S. on 7/18/19
 */
public class KDSLogUtil {
    public static String setExpectedTime(String ksl, KDSDevice kdsDevice, long time) {
        Gson gson = new Gson();
        List<KotSummaryLog> kotSummaryLogs = getKotSummaryLogs(ksl);
        KotSummaryLog kotSummaryLog = getKotSummaryLog(kdsDevice.getDevice_id(), kotSummaryLogs);
//
//        if (kotSummaryLogs.size() > 0)
//            kotSummaryLog = getKotSummaryLog(kotSummaryLogs);
//
//        kotSummaryLog.expectedTime = time;
        return new Gson().toJson(kotSummaryLog);
    }

    public static String setTimeElapse(String kotSummaryLogStr, long time) {
        if (TextUtils.isEmpty(kotSummaryLogStr)) return "";
        Gson gson = new Gson();
        KotSummaryLog kotSummaryLog = gson.fromJson(kotSummaryLogStr, KotSummaryLog.class);
        kotSummaryLog.timeElapse = time;
        return new Gson().toJson(kotSummaryLog);
    }

    public static String setKds(String kotSummaryLogStr, KDSDevice kdsDevice) {
        if (TextUtils.isEmpty(kotSummaryLogStr)) return "";
        Gson gson = new Gson();
        List<KotSummaryLog> kotSummaryLogs = getKotSummaryLogs(kotSummaryLogStr);
        KotSummaryLog kotSummaryLog = getKotSummaryLog(kdsDevice.getDevice_id(), kotSummaryLogs);

        //Not found kot summary log by kds id
        //assign/create new log
        if (kotSummaryLog == null) {
            KotSummaryLog ksl = getEmptyKdsKotSummaryLog(kotSummaryLogs);
            if (ksl == null)
                ksl = new KotSummaryLog();

            ksl.kdsDevice = kdsDevice;
        }
        return gson.toJson(kotSummaryLogs);
    }

    private static List<KotSummaryLog> getKotSummaryLogs(String kotSL) {
        if (TextUtils.isEmpty(kotSL)) return new ArrayList<>();
        return new Gson().fromJson(kotSL,
                new TypeToken<List<KotSummaryLog>>() {
                }.getType());
    }

    private static KotSummaryLog getKotSummaryLog(int kdsId, List<KotSummaryLog> kotSummaryLogs) {

        for (KotSummaryLog kotSummaryLog : kotSummaryLogs) {
            KDSDevice kdsDevice = kotSummaryLog.kdsDevice;
            if (kdsDevice != null && kdsDevice.getDevice_id() == kdsId) {
                return kotSummaryLog;
            }
        }

        return null;
    }

    private static KotSummaryLog getEmptyKdsKotSummaryLog(List<KotSummaryLog> kotSummaryLogs) {
        for (KotSummaryLog kotSummaryLog : kotSummaryLogs) {
            KDSDevice kdsDevice = kotSummaryLog.kdsDevice;
            if (kdsDevice == null) {
                return kotSummaryLog;
            }
        }
        return null;
    }

    private static KotSummaryLog getEmptyKdsKotSummaryLog(String kotSummaryLogStr) {
        for (KotSummaryLog kotSummaryLog : getKotSummaryLogs(kotSummaryLogStr)) {
            KDSDevice kdsDevice = kotSummaryLog.kdsDevice;
            if (kdsDevice == null) {
                return kotSummaryLog;
            }
        }
        return null;
    }
}
