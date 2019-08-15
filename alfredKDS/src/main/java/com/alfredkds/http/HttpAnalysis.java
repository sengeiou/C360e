package com.alfredkds.http;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.alfredbase.ParamConst;
import com.alfredbase.global.CoreData;
import com.alfredbase.http.ResultCode;
import com.alfredbase.javabean.KotItemDetail;
import com.alfredbase.javabean.KotItemModifier;
import com.alfredbase.javabean.KotSummary;
import com.alfredbase.javabean.Printer;
import com.alfredbase.javabean.User;
import com.alfredbase.javabean.model.MainPosInfo;
import com.alfredbase.javabean.model.SessionStatus;
import com.alfredbase.store.Store;
import com.alfredbase.store.sql.KotItemDetailSQL;
import com.alfredbase.store.sql.KotItemModifierSQL;
import com.alfredbase.store.sql.KotSummarySQL;
import com.alfredkds.global.App;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/* Parsing data in HTTP response */
public class HttpAnalysis {

    public static String login(int statusCode, Header[] headers,
                               byte[] responseBody, Context context) {
        try {
            Gson gson = new Gson();
            JSONObject object = new JSONObject(new String(responseBody));
            String userKey = object.optString("userKey");
            SessionStatus receiveSessionStatus = gson.fromJson(
                    object.optString("session"), SessionStatus.class);
            Long receiveBusinessDate = object.optLong("businessDate");
            int trainType = object.optInt("trainType");

            MainPosInfo mainPosInfo = gson.fromJson(
                    object.optString("mainPosInfo"), MainPosInfo.class);


            // SessionStatus oldSessionStatus = (SessionStatus)
            // Store.getObject(context, Store.SESSION_STATUS);
            // Long oldBusinessDate = (Long) Store.getObject(context,
            // Store.BUSINESS_DATE);
            // if (oldSessionStatus == null && oldBusinessDate == null) {
            // Store.saveObject(context, Store.SESSION_STATUS,
            // receiveSessionStatus);
            // Store.saveObject(context, Store.BUSINESS_DATE,
            // receiveBusinessDate);
            // }else {
            // if (oldSessionStatus.getSession_status() !=
            // receiveSessionStatus.getSession_status()
            // && oldBusinessDate != receiveBusinessDate) {
            List<KotSummary> kotSummaryList = gson.fromJson(
                    object.optString("kotSummaryList"),
                    new TypeToken<ArrayList<KotSummary>>() {
                    }.getType());
            List<KotItemDetail> kotItemDetails = gson.fromJson(
                    object.optString("kotItemDetails"),
                    new TypeToken<ArrayList<KotItemDetail>>() {
                    }.getType());
            List<KotItemModifier> kotItemModifiers = gson.fromJson(
                    object.optString("kotItemModifiers"),
                    new TypeToken<ArrayList<KotItemModifier>>() {
                    }.getType());
            Integer revId = null;
            if (kotSummaryList != null && kotItemDetails != null
                    && kotItemModifiers != null) {

                if (kotSummaryList.size() > 0) {
                    revId = kotSummaryList.get(0).getRevenueCenterId();
                    CoreData.getInstance().setUserKey(revId,userKey);
                }
                KotSummarySQL.deleteAllKotSummary(revId);
                KotItemDetailSQL.deleteAllKotItemDetail(revId);
                KotItemModifierSQL.deleteAllKotItemModifier(revId);

                for (int i = 0; i < kotSummaryList.size(); i++) {


                    boolean flag = false;
                    KotSummary kotSummary = kotSummaryList.get(i);
                    ;

                    for (int j = 0; j < kotItemDetails.size(); j++) {
                        KotItemDetail kotItemDetail = kotItemDetails.get(j);
                        //

                        //对未完成的item和完成了一部分但status未done的item做处理
                        if (kotSummary.getId().intValue() == kotItemDetail.getKotSummaryId().intValue()
                                && kotItemDetail.getUnFinishQty().intValue() > 0) {
                            flag = true;
                        }
//						if (kotItemDetail.getKotStatus() < ParamConst.KOT_STATUS_DONE
//								 && kotSummary.getId().intValue() == kotItemDetail
//										.getKotSummaryId().intValue()) {
//							flag = true;
//						}else if (kotItemDetail.getKotStatus() >= ParamConst.KOT_STATUS_DONE
//								&& kotSummary.getId().intValue() == kotItemDetail.getKotSummaryId().intValue()
//							&& kotItemDetail.getUnFinishQty().intValue() > 0
//								 ) {
//							kotItemDetail.setFinishQty(kotItemDetail.getItemNum()-kotItemDetail.getUnFinishQty());
//							kotItemDetail.setKotStatus(ParamConst.KOT_STATUS_UNDONE);
//							flag = true;
//						}
                    }
                    if (!flag) {
                        kotSummary.setStatus(ParamConst.KOTS_STATUS_DONE);
                    }
                    KotSummarySQL.update(kotSummary);
                }

                // KotSummarySQL.addKotSummaryList(kotSummaryList);
                KotItemDetailSQL.addKotItemDetailList(kotItemDetails);
                KotItemModifierSQL.addKotItemModifierList(kotItemModifiers);


            }
            Store.saveObject(context, Store.SESSION_STATUS,
                    receiveSessionStatus);
            Store.putLong(context, Store.BUSINESS_DATE, receiveBusinessDate);
            Store.putInt(context, Store.TRAIN_TYPE, trainType);
            // }
            // }
            App.instance.addMainPosInfo(mainPosInfo);
            // check business data and session
            // Long bizDate = object.optLong("businessDate");
            // SessionStatus session =
            // gson.fromJson(object.optString("session"), SessionStatus.class);
            return userKey;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Printer> getPrinterList(int statusCode,
                                               Header[] headers, byte[] responseBody) {
        List<Printer> printers = new ArrayList<Printer>();
        try {
            JSONObject object = new JSONObject(new String(responseBody));
            Gson gson = new Gson();
            printers = gson.fromJson(
                    object.getString("printers"),
                    new TypeToken<ArrayList<Printer>>() {
                    }.getType());
            User user = gson.fromJson(object.getString("user"), User.class);
            Store.saveObject(App.instance, Store.KDS_USER, user);
            return printers;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return printers;
    }

    public static void getMainPosInfo(int statusCode, Header[] headers,
                                      byte[] responseBody, Handler handler) {
        try {
            Gson gson = new Gson();
            JSONObject object = new JSONObject(new String(responseBody));
            MainPosInfo mainPosInfo = gson.fromJson(
                    object.optString("mainPosInfo"), MainPosInfo.class);
            App.instance.addMainPosInfo(mainPosInfo);
            handler.sendEmptyMessage(ResultCode.SUCCESS);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void getKotItemDetail(int statusCode, Header[] headers,
                                        byte[] responseBody, Handler handler) {
        try {
            Gson gson = new Gson();
            JSONObject object = new JSONObject(new String(responseBody));
            List<KotItemDetail> subKotItemDetails = gson.fromJson(
                    object.optString("resultKotItemDetails"),
                    new TypeToken<ArrayList<KotItemDetail>>() {
                    }.getType());
            KotItemDetailSQL.addKotItemDetailList(subKotItemDetails);
            if (object.has("kotSummaryId")) {
                int id = object.getInt("kotSummaryId");
                int count = KotItemDetailSQL.getKotItemDetailCountBySummaryId(id);
                if (count == 0) {
                    KotSummarySQL.updateKotSummaryStatusById(ParamConst.KOTS_STATUS_DONE, id);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void getNewKotItemDetail(int statusCode, Header[] headers,
                                           byte[] responseBody, Handler handler) {
        try {
            Gson gson = new Gson();
            JSONObject object = new JSONObject(new String(responseBody));
            List<KotItemDetail> newKotItemDetails = gson.fromJson(
                    object.optString("newKotItemDetails"),
                    new TypeToken<ArrayList<KotItemDetail>>() {
                    }.getType());
            if (newKotItemDetails == null) {
                return;
            }
            KotItemDetailSQL.addKotItemDetailList(newKotItemDetails);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
