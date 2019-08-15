package com.alfredbase.utils;

import android.text.TextUtils;

import com.alfredbase.javabean.KDSHistory;
import com.alfredbase.javabean.KDSTracking;
import com.alfredbase.javabean.KotItemDetail;
import com.alfredbase.javabean.KotSummary;
import com.alfredbase.javabean.KotSummaryLog;
import com.alfredbase.javabean.Printer;
import com.alfredbase.javabean.model.KDSDevice;
import com.alfredbase.store.sql.KotItemDetailSQL;
import com.alfredbase.store.sql.PrinterSQL;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arif S. on 7/18/19
 */
public class KDSLogUtil {

    public static String initLog(List<KDSDevice> kdsDeviceList) {
        KotSummaryLog kotSummaryLog = new KotSummaryLog();
        List<KDSHistory> kdsHistoryList = new ArrayList<>();
        List<KDSTracking> kdsTrackingList = new ArrayList<>();

        for (KDSDevice kdsDevice : kdsDeviceList) {
            KDSHistory kdsHistory = new KDSHistory();
            kdsHistory.kdsDevice = kdsDevice;
            kdsHistoryList.add(kdsHistory);

            KDSTracking kdsTracking = new KDSTracking();
            kdsTracking.kdsDevice = kdsDevice;
            kdsTrackingList.add(kdsTracking);
        }

        kotSummaryLog.kdsHistoryList = kdsHistoryList;
        kotSummaryLog.kdsTrackingList = kdsTrackingList;

        return new Gson().toJson(kotSummaryLog);
    }

    public static String setEndTime(KotSummary kotSummary, List<KotItemDetail> kotItemDetails, KDSDevice kdsDevice) {
        List<KotSummaryLog> kotSummaryLogs = getKotSummaryLogs(kotSummary.getKotSummaryLog());
        KotSummaryLog kotSummaryLog = getKotSummaryLog(kdsDevice.getDevice_id(), kotSummaryLogs);

        if (kotSummaryLog != null) {
            kotSummaryLog.endTime = System.currentTimeMillis();//parent

            List<KotItemDetail> tmpKotItemDetails = new ArrayList<>(kotSummaryLog.kotItemDetails);
            int position = 0;
            for (KotItemDetail kotItemDetail : tmpKotItemDetails) {
                for (KotItemDetail kid : kotItemDetails) {
                    if (kid.getId().equals(kotItemDetail.getId())) {
                        //replace data
                        kotSummaryLog.kotItemDetails.remove(position);

                        if (position < kotSummaryLog.kotItemDetails.size())
                            kotSummaryLog.kotItemDetails.add(position, kid);
                        else
                            kotSummaryLog.kotItemDetails.add(kid);

                        kotItemDetails.remove(kid);
                        break;
                    }
                }
                position++;
            }

            if (kotItemDetails.size() > 0)
                kotSummaryLog.kotItemDetails.addAll(kotItemDetails);
        }
        return createLogs(kotSummaryLogs, kotSummaryLog);
    }

    public static String putLog(String kotSummaryLogStr, List<KotItemDetail> kotItemDetails, KDSDevice kdsDevice) {
        KotSummaryLog kotSummaryLog = new Gson().fromJson(kotSummaryLogStr, KotSummaryLog.class);
        KDSHistory kdsHistory = getKdsHistory(kotSummaryLog.kdsHistoryList, kdsDevice.getDevice_id());
        KDSTracking kdsTracking = getKdsTracking(kotSummaryLog.kdsTrackingList, kdsDevice.getDevice_id());

        return "";
    }

    private static KDSHistory getKdsHistory(List<KDSHistory> kdsHistoryList, int id) {
        for (KDSHistory kdsHistory : kdsHistoryList) {
            if (kdsHistory.kdsDevice.getDevice_id() == id)
                return kdsHistory;
        }

        return null;
    }

    private static KDSTracking getKdsTracking(List<KDSTracking> kdsTrackingList, int id) {
        for (KDSTracking kdsTracking : kdsTrackingList) {
            if (kdsTracking.kdsDevice.getDevice_id() == id)
                return kdsTracking;
        }

        return null;
    }

    public static String putKdsLog(KotSummary kotSummary, List<KotItemDetail> kotItemDetails, KDSDevice kdsDevice) {
        List<KotSummaryLog> kotSummaryLogs = getKotSummaryLogs(kotSummary.getKotSummaryLog());
        KotSummaryLog kotSummaryLog = getKotSummaryLog(kdsDevice.getDevice_id(), kotSummaryLogs);

        //Not found kot summary log by kds id
        //create new log
        if (kotSummaryLog == null) {
            kotSummaryLog = new KotSummaryLog();
        }

        kotSummaryLog.kdsDevice = kdsDevice;
        kotSummaryLog.startTime = System.currentTimeMillis();

        List<KotItemDetail> tmpKotItemDetails = new ArrayList<>(kotSummaryLog.kotItemDetails);
        int position = 0;
        for (KotItemDetail kotItemDetail : tmpKotItemDetails) {
            for (KotItemDetail kid : kotItemDetails) {
                if (kid.getId().equals(kotItemDetail.getId())) {
                    //replace data
                    kotSummaryLog.kotItemDetails.remove(position);

                    if (position < kotSummaryLog.kotItemDetails.size())
                        kotSummaryLog.kotItemDetails.add(position, kid);
                    else
                        kotSummaryLog.kotItemDetails.add(kid);

                    kotItemDetails.remove(kid);
                    break;
                }
            }
            position++;
        }

        if (kotItemDetails.size() > 0)
            kotSummaryLog.kotItemDetails.addAll(kotItemDetails);

        return createLogs(kotSummaryLogs, kotSummaryLog);
    }

    private static String createLogs(List<KotSummaryLog> kotSummaryLogs, KotSummaryLog newKotSummaryLog) {
        int position = 0;
        boolean isFound = false;
        for (KotSummaryLog ksl : kotSummaryLogs) {
            if (newKotSummaryLog == null) break;
            if (ksl.kdsDevice.getDevice_id() == newKotSummaryLog.kdsDevice.getDevice_id()) {
                isFound = true;
                break;
            }

            position++;
        }

        if (isFound) {
            kotSummaryLogs.remove(position);

            if (position < kotSummaryLogs.size())
                kotSummaryLogs.add(position, newKotSummaryLog);
            else
                kotSummaryLogs.add(newKotSummaryLog);
        } else {
            if (newKotSummaryLog != null)
                kotSummaryLogs.add(newKotSummaryLog);
        }

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
