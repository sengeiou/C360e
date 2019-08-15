package com.alfredbase.utils;

import com.alfredbase.javabean.KDSHistory;
import com.alfredbase.javabean.KDSTracking;
import com.alfredbase.javabean.KotItemDetail;
import com.alfredbase.javabean.KotSummaryLog;
import com.alfredbase.javabean.model.KDSDevice;
import com.google.gson.Gson;

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

    public static String putLog(String kotSummaryLogStr, List<KotItemDetail> kotItemDetails, KDSDevice kdsDevice) {
        Gson gson = new Gson();
        KotSummaryLog kotSummaryLog = gson.fromJson(kotSummaryLogStr, KotSummaryLog.class);
        KDSHistory kdsHistory = getKdsHistory(kotSummaryLog.kdsHistoryList, kdsDevice.getDevice_id());
        KDSTracking kdsTracking = getKdsTracking(kotSummaryLog.kdsTrackingList, kdsDevice.getDevice_id());

        if (kdsHistory != null) {
            List<KotItemDetail> kotItemDetailListCopy = new ArrayList<>(kdsHistory.kotItemDetails);

            if (kotItemDetailListCopy.size() > 0) {
                for (KotItemDetail kotItemDetailUpdate : kotItemDetails) {
                    KotItemDetail kotItemDetailOld = null;

                    for (KotItemDetail kotItemDetail : kotItemDetailListCopy) {
                        if (kotItemDetailUpdate.getId().equals(kotItemDetail.getId())) {
                            kotItemDetailOld = kotItemDetail;
                            break;
                        }
                    }

                    if (kotItemDetailOld != null) {
                        kdsHistory.kotItemDetails.remove(kotItemDetailOld);
                        kdsHistory.kotItemDetails.add(kotItemDetailUpdate);
                        kotItemDetailListCopy.remove(kotItemDetailOld);
                    } else {
                        kdsHistory.kotItemDetails.add(kotItemDetailUpdate);
                    }
                }
            } else {
                kdsHistory.kotItemDetails.addAll(kotItemDetailListCopy);
            }
        } else {
            kdsHistory = new KDSHistory();
            kdsHistory.kdsDevice = kdsDevice;
            kdsHistory.kotItemDetails = new ArrayList<>();
            kotSummaryLog.kdsHistoryList.add(kdsHistory);
        }

        if (kdsTracking != null) {

            List<KotItemDetail> kotItemDetailListCopy = new ArrayList<>(kdsTracking.kotItemDetails);

            if (kotItemDetailListCopy.size() > 0) {
                for (KotItemDetail kotItemDetailUpdate : kotItemDetails) {
                    KotItemDetail kotItemDetailOld = null;

                    for (KotItemDetail kotItemDetail : kotItemDetailListCopy) {
                        if (kotItemDetailUpdate.getId().equals(kotItemDetail.getId())) {
                            kotItemDetailOld = kotItemDetail;
                            break;
                        }
                    }

                    if (kotItemDetailOld != null) {
                        kdsTracking.kotItemDetails.remove(kotItemDetailOld);
                        kdsTracking.kotItemDetails.add(kotItemDetailUpdate);
                        kotItemDetailListCopy.remove(kotItemDetailOld);
                    } else {
                        kdsTracking.kotItemDetails.add(kotItemDetailUpdate);
                    }
                }
            } else {
                kdsTracking.kotItemDetails.addAll(kotItemDetailListCopy);
            }
        } else {
            kdsTracking = new KDSTracking();
            kdsTracking.kdsDevice = kdsDevice;
            kdsTracking.kotItemDetails = new ArrayList<>();
            kotSummaryLog.kdsTrackingList.add(kdsTracking);
        }

        return gson.toJson(kotSummaryLog);
    }

    public static String removeTrackerLog(String kotSummaryLogStr, List<KotItemDetail> kotItemDetails, KDSDevice kdsDevice) {
        Gson gson = new Gson();
        KotSummaryLog kotSummaryLog = gson.fromJson(kotSummaryLogStr, KotSummaryLog.class);
        KDSTracking kdsTracking = getKdsTracking(kotSummaryLog.kdsTrackingList, kdsDevice.getDevice_id());

        if (kdsTracking != null) {
            for (KotItemDetail kotItemDetail : kotItemDetails) {
                for (KotItemDetail kid : kdsTracking.kotItemDetails) {
                    if (kotItemDetail.getId().equals(kid.getId())) {
                        kdsTracking.kotItemDetails.remove(kid);
                        break;
                    }
                }
            }
        }

        return gson.toJson(kotSummaryLog);
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

}
