package com.alfredbase.utils;

import android.text.TextUtils;

import com.alfredbase.javabean.KotItemDetail;
import com.alfredbase.javabean.KotSummary;
import com.alfredbase.javabean.KotSummaryLog;
import com.alfredbase.javabean.model.KDSDevice;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Created by Arif S. on 7/18/19
 */
public class KDSLogUtil {
    public static String setExpectedTime(String kotSummaryLogStr, long time) {
        if (TextUtils.isEmpty(kotSummaryLogStr)) return "";
        Gson gson = new Gson();
        KotSummaryLog kotSummaryLog = getKotSummaryLog(kdsDevice.getDevice_id(), getKotSummaryLogs(kotSummaryLogStr));

        KotSummaryLog kotSummaryLog = new KotSummaryLog();
        kotSummaryLog.expectedTime = time;
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
        KotSummaryLog kotSummaryLog = getKotSummaryLog(kdsDevice.getDevice_id(), getKotSummaryLogs(kotSummaryLogStr));
        if (kotSummaryLog != null) {
            kotSummaryLog.kdsDevice = kdsDevice;
        } else {
            KotSummaryLog ksl = new KotSummaryLog();
            ksl.kdsDevice = kdsDevice;
        }
        return gson.toJson(kotSummaryLog);
    }

    private static List<KotSummaryLog> getKotSummaryLogs(String kotSL) {
        return new Gson().fromJson(kotSL,
                new TypeToken<List<KotSummaryLog>>() {
                }.getType());
    }

    private static KotSummaryLog getKotSummaryLog(int kdsId, List<KotSummaryLog> kotSummaryLogs) {
        KotSummaryLog ksl = null;

        for (KotSummaryLog kotSummaryLog : kotSummaryLogs) {
            KDSDevice kdsDevice = kotSummaryLog.kdsDevice;
            if (kdsDevice != null && kdsDevice.getDevice_id() == kdsId) {
                return kotSummaryLog;
            } else {
                if (kdsDevice == null) {
                    ksl = kotSummaryLog;
                }
            }
        }

        if (ksl == null) {
            ksl = new KotSummaryLog();
            ksl.
        }
        return new KotSummaryLog();
    }
}
