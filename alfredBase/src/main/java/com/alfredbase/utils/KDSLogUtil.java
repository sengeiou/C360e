package com.alfredbase.utils;

import android.text.TextUtils;

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

    public static String setStartTime(String kotSummaryLogStr, KDSDevice kdsDevice) {
        if (TextUtils.isEmpty(kotSummaryLogStr)) return "";
        List<KotSummaryLog> kotSummaryLogs = getKotSummaryLogs(kotSummaryLogStr);
        KotSummaryLog kotSummaryLog = getKotSummaryLog(kdsDevice.getDevice_id(), kotSummaryLogs);

        if (kotSummaryLog != null) {
            kotSummaryLog.startTime = System.currentTimeMillis();
            kotSummaryLogs.add(kotSummaryLog);
        }
        return new Gson().toJson(kotSummaryLogs);
    }

    public static String setEndTime(String kotSummaryLogStr, KDSDevice kdsDevice) {
        if (TextUtils.isEmpty(kotSummaryLogStr)) return "";
        List<KotSummaryLog> kotSummaryLogs = getKotSummaryLogs(kotSummaryLogStr);
        KotSummaryLog kotSummaryLog = getKotSummaryLog(kdsDevice.getDevice_id(), kotSummaryLogs);

        if (kotSummaryLog != null) {
            kotSummaryLog.endTime = System.currentTimeMillis();
            kotSummaryLogs.add(kotSummaryLog);
        }
        return new Gson().toJson(kotSummaryLogs);
    }

    public static String putKdsLog(String kotSummaryLogStr, KDSDevice kdsDevice) {
        List<KotSummaryLog> kotSummaryLogs = getKotSummaryLogs(kotSummaryLogStr);
        KotSummaryLog kotSummaryLog = getKotSummaryLog(kdsDevice.getDevice_id(), kotSummaryLogs);

        //Not found kot summary log by kds id
        //create new log
        if (kotSummaryLog == null) {
            kotSummaryLog = new KotSummaryLog();
        }

        kotSummaryLog.kdsDevice = kdsDevice;
        kotSummaryLog.startTime = System.currentTimeMillis();
        kotSummaryLogs.add(kotSummaryLog);

        return new Gson().toJson(kotSummaryLogs);
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

}
