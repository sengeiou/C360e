package com.alfredkds.http.server;

import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import com.alfredbase.APPConfig;
import com.alfredbase.BaseActivity;
import com.alfredbase.ParamConst;
import com.alfredbase.http.APIName;
import com.alfredbase.http.AlfredHttpServer;
import com.alfredbase.http.ResultCode;
import com.alfredbase.javabean.KotItemDetail;
import com.alfredbase.javabean.KotItemModifier;
import com.alfredbase.javabean.KotSummary;
import com.alfredbase.javabean.Printer;
import com.alfredbase.javabean.model.MainPosInfo;
import com.alfredbase.javabean.model.SessionStatus;
import com.alfredbase.store.sql.CommonSQL;
import com.alfredbase.store.sql.KotItemDetailSQL;
import com.alfredbase.store.sql.KotItemModifierSQL;
import com.alfredbase.store.sql.KotSummarySQL;
import com.alfredkds.R;
import com.alfredkds.activity.KitchenOrder;
import com.alfredkds.activity.Login;
import com.alfredkds.global.App;
import com.alfredkds.global.UIHelp;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KdsHttpServer extends AlfredHttpServer {
    private BaseActivity context;

    public KdsHttpServer() {
        super(APPConfig.KDS_HTTP_SERVER_PORT);
    }

    @Override
    public Response doPost(String uri, Method mothod, Map<String, String> params, String body) {

        Response resp;

        if (uri == null) {
            resp = getNotFoundResponse();
        } else {
            if (!validMessageFromConnectedPOS(body)) {
                return invalidDeviceResponse();
            }
            if (uri.equals(APIName.SUBMIT_NEW_KOT)) {
                //handlerSubmitNewKot
                resp = handlerSubmitNewKot(body);
            } else if (uri.equals(APIName.TRANSFER_KOT)) {
                App.instance.ringUtil.playRingOnce();
                resp = handlerTransferKot(body);
            } else if (uri.equals(APIName.TRANSFER_ITEM_KOT)) {
                App.instance.ringUtil.playRingOnce();
                resp = handlerTransferKotItem(body);
            } else if (uri.equals(APIName.CLOSE_SESSION)) {
                App.instance.ringUtil.playRingOnce();
                resp = handlerSessionClose(body);
            } else if (uri.equals(APIName.SUBMIT_TMP_KOT)) {
                resp = handlerTmpKot(body);
            } else if (uri.equals(APIName.SUBMIT_NEXT_KOT)) {
                App.instance.ringUtil.playRingOnce();
                resp = handlerNextKot(body);
            } else if (uri.equals(APIName.DELETE_KOT_KDS)) {
                resp = handlerDeleteKot(body);
            } else if (uri.equals(APIName.SUBMIT_SUMMARY_KDS)) {
                resp = handlerSubmitSummary(body);
            } else if (uri.equals(APIName.UPDATE_ORDER_COUNT)) {
                resp = handlerUpdateOrderCount(body);
            } else {
                resp = getNotFoundResponse();
            }
        }

        return resp;
    }

    private Response handlerUpdateOrderCount(String params) {
        Response resp;
        Map<String, Object> result = new HashMap<>();
        int revenueCenterId = App.instance.getCurrentConnectedMainPos().getRevenueId();

        try {
            JSONObject jsonObject = new JSONObject(params);
            final Gson gson = new Gson();
            String method = jsonObject.optString("method");

            //region parameter validation
            if (TextUtils.isEmpty(method)) {
                resp = this.getInternalErrorResponse(App.getTopActivity().getResources().getString(R.string.kot_submit_failed));
                return resp;
            }

            final KotSummary kotSummary = gson.fromJson(jsonObject.optString("kotSummary"), KotSummary.class);

            if (kotSummary == null) {
                resp = this.getInternalErrorResponse(App.getTopActivity().getResources().getString(R.string.kot_submit_failed));
                return resp;
            } else {
                if (revenueCenterId != kotSummary.getRevenueCenterId()) {
                    App.getTopActivity().httpRequestAction(App.HANDLER_VERIFY_MAINPOS, null);
                    result.put("resultCode", ResultCode.CONNECTION_FAILED);
                    resp = getJsonResponse(new Gson().toJson(result));
                    return resp;
                }
            }
            //endregion

            new Thread(new Runnable() {
                @Override
                public void run() {

                    KotSummarySQL.updateKotSummaryOrderCountById(kotSummary.getOrderDetailCount(), kotSummary.getId());

                    if (App.getTopActivity() != null)
                        App.getTopActivity().httpRequestAction(App.HANDLER_REFRESH_KOT, null);
                }
            }).start();

            result.put("resultCode", ResultCode.SUCCESS);
            result.put("method", method);
            result.put("kotSummary", kotSummary);
            resp = getJsonResponse(new Gson().toJson(result));

        } catch (JSONException e) {
            e.printStackTrace();
            resp = this.getInternalErrorResponse(App.getTopActivity().getResources().getString(R.string.kot_submit_failed));
        }

        return resp;
    }

    private Response handlerSubmitSummary(String params) {
        Response resp;
        Map<String, Object> result = new HashMap<>();
        int revenueCenterId = App.instance.getCurrentConnectedMainPos().getRevenueId();

        try {
            JSONObject jsonObject = new JSONObject(params);
            final Gson gson = new Gson();
            String method = jsonObject.optString("method");

            //region parameter validation
            if (TextUtils.isEmpty(method)) {
                resp = this.getInternalErrorResponse(App.getTopActivity().getResources().getString(R.string.kot_submit_failed));
                return resp;
            }

            final KotSummary kotSummary = gson.fromJson(jsonObject.optString("kotSummary"), KotSummary.class);

            if (kotSummary == null) {
                resp = this.getInternalErrorResponse(App.getTopActivity().getResources().getString(R.string.kot_submit_failed));
                return resp;
            } else {
                if (revenueCenterId != kotSummary.getRevenueCenterId()) {
                    App.getTopActivity().httpRequestAction(App.HANDLER_VERIFY_MAINPOS, null);
                    result.put("resultCode", ResultCode.CONNECTION_FAILED);
                    resp = getJsonResponse(new Gson().toJson(result));
                    return resp;
                }
            }
            //endregion

            final List<KotItemDetail> kotItemDetails = gson.fromJson(jsonObject.optString("kotItemDetails"), new TypeToken<List<KotItemDetail>>() {
            }.getType());
            final List<KotItemModifier> kotItemModifiers = gson.fromJson(jsonObject.optString("kotItemModifiers"), new TypeToken<List<KotItemModifier>>() {
            }.getType());

            new Thread(new Runnable() {
                @Override
                public void run() {

                    KotSummarySQL.update(kotSummary);
                    if (kotItemDetails != null) {
                        KotItemDetailSQL.addKotItemDetailList(kotItemDetails);
                    }
                    if (kotItemModifiers != null) {
                        KotItemModifierSQL.addKotItemModifierList(kotItemModifiers);
                    }

                    if (App.getTopActivity() != null)
                        App.getTopActivity().httpRequestAction(App.HANDLER_UPDATE_KOT, null);
                }
            }).start();

            result.put("resultCode", ResultCode.SUCCESS);
            result.put("method", method);
            result.put("kotSummary", kotSummary);
            result.put("orderId", kotSummary.getOrderId());
            resp = getJsonResponse(new Gson().toJson(result));

        } catch (JSONException e) {
            e.printStackTrace();
            resp = this.getInternalErrorResponse(App.getTopActivity().getResources().getString(R.string.kot_submit_failed));
        }

        return resp;
    }

    private Response handlerDeleteKot(String params) {
        Response resp;
        Map<String, Object> result = new HashMap<>();
        try {
            JSONObject jsonObject = new JSONObject(params);
            final Gson gson = new Gson();
            KotSummary kotSummary = gson.fromJson(jsonObject.optString("kotSummary"), KotSummary.class);
            if (kotSummary == null) {
                resp = this.getInternalErrorResponse(App.getTopActivity().getResources().getString(R.string.kot_submit_failed));
                return resp;
            }

            int revenueCenterId = App.instance.getCurrentConnectedMainPos().getRevenueId();
            if (revenueCenterId != kotSummary.getRevenueCenterId()) {
                if (App.getTopActivity() != null)
                    App.getTopActivity().httpRequestAction(App.HANDLER_VERIFY_MAINPOS, null);
                result.put("resultCode", ResultCode.CONNECTION_FAILED);
                resp = getJsonResponse(new Gson().toJson(result));
                return resp;
            }

            ArrayList<KotItemDetail> kotItemDetails = KotItemDetailSQL.getKotItemDetailByOrderId(kotSummary.getOrderId());

            for (KotItemDetail kotItemDetail : kotItemDetails) {
                List<KotItemModifier> kotItemModifierList =
                        KotItemModifierSQL.getKotItemModifiersByKotItemDetail(kotItemDetail.getId());

                for (KotItemModifier kotItemModifier : kotItemModifierList) {
                    KotItemModifierSQL.deleteKotItemModifier(kotItemModifier);
                }
            }

            KotItemDetailSQL.deleteAllKotItemDetailByKotSummary(kotSummary);
            KotSummarySQL.deleteKotSummary(kotSummary);

            if (App.getTopActivity() != null)
                App.getTopActivity().httpRequestAction(App.HANDLER_DELETE_KOT, null);
            result.put("resultCode", ResultCode.SUCCESS);
            resp = getJsonResponse(new Gson().toJson(result));
        } catch (JSONException ex) {
            resp = this.getInternalErrorResponse(App.getTopActivity().getResources().getString(R.string.kot_submit_failed));
        }
        return resp;
    }

    private Response handlerNextKot(String params) {
        Response resp;
        Map<String, Object> result = new HashMap<>();
        int revenueCenterId = App.instance.getCurrentConnectedMainPos().getRevenueId();

        try {
            JSONObject jsonObject = new JSONObject(params);
            final Gson gson = new Gson();
            String method = jsonObject.optString("method");

            //region parameter validation
            if (TextUtils.isEmpty(method)) {
                resp = this.getInternalErrorResponse(App.getTopActivity().getResources().getString(R.string.kot_submit_failed));
                return resp;
            }

            final KotSummary kotSummary = gson.fromJson(jsonObject.optString("kotSummary"), KotSummary.class);

            if (kotSummary == null) {
                resp = this.getInternalErrorResponse(App.getTopActivity().getResources().getString(R.string.kot_submit_failed));
                return resp;
            } else {
                if (revenueCenterId != kotSummary.getRevenueCenterId()) {
                    if (App.getTopActivity() != null)
                        App.getTopActivity().httpRequestAction(App.HANDLER_VERIFY_MAINPOS, null);
                    result.put("resultCode", ResultCode.CONNECTION_FAILED);
                    resp = getJsonResponse(new Gson().toJson(result));
                    return resp;
                }
            }
            //endregion

            final List<KotItemDetail> kotItemDetails = gson.fromJson(jsonObject.optString("kotItemDetails"), new TypeToken<List<KotItemDetail>>() {
            }.getType());
            final List<KotItemModifier> kotItemModifiers = gson.fromJson(jsonObject.optString("kotItemModifiers"), new TypeToken<List<KotItemModifier>>() {
            }.getType());

            new Thread(new Runnable() {
                @Override
                public void run() {

                    if (App.instance.getKdsDevice().getKdsType() == Printer.KDS_SUMMARY ||
                            App.instance.getKdsDevice().getKdsType() == Printer.KDS_EXPEDITER ||
                            App.instance.getKdsDevice().getKdsType() == Printer.KDS_NORMAL) {
                        if (CommonSQL.isFakeId(kotSummary.getId())) {
                            kotSummary.setId(kotSummary.getOriginalId());
                        }
                    }

                    KotSummarySQL.update(kotSummary);
                    if (kotItemDetails != null) {
                        if (App.instance.getKdsDevice().getKdsType() == Printer.KDS_EXPEDITER) {
                            for (KotItemDetail kotItemDetail : kotItemDetails) {
                                int kotSummaryId = CommonSQL.isFakeId(kotSummary.getId()) ? kotSummary.getOriginalId() : kotSummary.getId();
                                kotItemDetail.setKotSummaryId(kotSummaryId);//set original id
                            }
                        }

                        KotItemDetailSQL.addKotItemDetailList(kotItemDetails);
                    }
                    if (kotItemModifiers != null) {
                        KotItemModifierSQL.addKotItemModifierList(kotItemModifiers);
                    }

                    if (App.getTopActivity() != null)
                        App.getTopActivity().httpRequestAction(App.HANDLER_NEXT_KOT, null);
                }
            }).start();

            result.put("resultCode", ResultCode.SUCCESS);
            result.put("method", method);
            result.put("kotSummary", kotSummary);
            result.put("orderId", kotSummary.getOrderId());
            resp = getJsonResponse(new Gson().toJson(result));

        } catch (JSONException e) {
            e.printStackTrace();
            resp = this.getInternalErrorResponse(App.getTopActivity().getResources().getString(R.string.kot_submit_failed));
        }

        return resp;
    }

    private Response handlerTmpKot(String params) {
        Response resp;
        Map<String, Object> result = new HashMap<>();
        int revenueCenterId = App.instance.getCurrentConnectedMainPos().getRevenueId();

        try {
            JSONObject jsonObject = new JSONObject(params);
            final Gson gson = new Gson();
            final String method = jsonObject.optString("method");

            //region parameter validation
            if (TextUtils.isEmpty(method)) {
                resp = this.getInternalErrorResponse(App.getTopActivity().getResources().getString(R.string.kot_submit_failed));
                return resp;
            }

            final KotSummary kotSummary = gson.fromJson(jsonObject.optString("kotSummary"), KotSummary.class);
            if (kotSummary == null) {
                resp = this.getInternalErrorResponse(App.getTopActivity().getResources().getString(R.string.kot_submit_failed));
                return resp;
            } else {
                if (revenueCenterId != kotSummary.getRevenueCenterId()) {
                    if (App.getTopActivity() != null)
                        App.getTopActivity().httpRequestAction(App.HANDLER_VERIFY_MAINPOS, null);
                    result.put("resultCode", ResultCode.CONNECTION_FAILED);
                    resp = getJsonResponse(new Gson().toJson(result));
                    return resp;
                }
            }
            //endregion

            final List<KotItemDetail> kotItemDetails = gson.fromJson(jsonObject.optString("kotItemDetails"), new TypeToken<List<KotItemDetail>>() {
            }.getType());
            final List<KotItemModifier> kotItemModifiers = gson.fromJson(jsonObject.optString("kotItemModifiers"), new TypeToken<List<KotItemModifier>>() {
            }.getType());

            new Thread(new Runnable() {
                @Override
                public void run() {

                    if (method.equals(ParamConst.JOB_DELETE_TMP_ITEM_KOT)) {
                        KotItemDetailSQL.deleteKotItemDetail(kotItemDetails);
                        KotItemModifierSQL.deleteKotItemModifiers(kotItemModifiers);
                    } else {
                        //region update to db
                        ArrayList<KotSummary> kotSummariesLocal = KotSummarySQL.getKotSummaryByOriginalId(kotSummary.getOriginalId());

                        if (kotSummariesLocal.size() > 0) {

                            KotSummary kotSumSelected = null;
                            for (KotSummary kotSLocal : kotSummariesLocal) {

                                List<KotItemDetail> kotDetailLocal = KotItemDetailSQL.getKotItemDetailBySummaryId(kotSLocal.getId());
                                boolean isPlaceOrder = false;
                                for (KotItemDetail kotItemDetail : kotDetailLocal) {
                                    if (kotItemDetail.getKotStatus() > ParamConst.KOT_STATUS_TMP) {
                                        isPlaceOrder = true;
                                        break;
                                    }
                                }

                                if (!isPlaceOrder) {
                                    kotSumSelected = kotSLocal;
                                    break;
                                }
                            }

                            if (kotItemDetails != null) {
                                if (kotSumSelected == null) {
                                    int fakeId = CommonSQL.getFakeId();
                                    kotSummary.setId(fakeId);
                                    KotSummarySQL.addKotSummary(kotSummary);

                                    for (int i = 0; i < kotItemDetails.size(); i++) {
                                        kotItemDetails.get(i).setKotSummaryId(fakeId);//assign to fake id
                                        KotItemDetailSQL.update(kotItemDetails.get(i));
                                    }
                                } else {
                                    for (int i = 0; i < kotItemDetails.size(); i++) {
                                        kotItemDetails.get(i).setKotSummaryId(kotSumSelected.getId());//assign to fake id
                                        KotItemDetailSQL.update(kotItemDetails.get(i));
                                    }
                                    KotSummarySQL.update(kotSumSelected);
                                }
                            }
                        } else {
                            KotItemDetailSQL.addKotItemDetailList(kotItemDetails);
                            KotSummarySQL.update(kotSummary);
                        }

                        if (kotItemModifiers != null) {
                            KotItemModifierSQL.addKotItemModifierList(kotItemModifiers);
                        }
                        //endregion
                    }

                    App.getTopActivity().httpRequestAction(App.HANDLER_TMP_KOT, kotSummary);
                }
            }).start();

            result.put("resultCode", ResultCode.SUCCESS);
            result.put("method", method);
            result.put("kotSummary", kotSummary);
            result.put("orderId", kotSummary.getOrderId());
            resp = getJsonResponse(new Gson().toJson(result));

        } catch (JSONException e) {
            e.printStackTrace();
            resp = this.getInternalErrorResponse(App.getTopActivity().getResources().getString(R.string.kot_submit_failed));
        }

        return resp;
    }

    private Response handlerTransferKot(String params) {
        Response resp = null;
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            JSONObject jsonObject = new JSONObject(params);
            Gson gson = new Gson();
            String action = jsonObject.optString("action");
            KotSummary toKotSummary = null;
            KotSummary fromKotSummary = null;
            if (!TextUtils.isEmpty(action)) {
                resp = this.getInternalErrorResponse(App.getTopActivity().getResources().getString(R.string.transfer_table_failed));
            }
            if (action.equals(ParamConst.JOB_TRANSFER_KOT)) {
                fromKotSummary = gson.fromJson(jsonObject.optString("fromKotSummary"), KotSummary.class);
                if (fromKotSummary != null) {
                    KotSummarySQL.update(fromKotSummary);
                } else {
                    resp = this.getInternalErrorResponse(App.getTopActivity().getResources().getString(R.string.transfer_table_failed));
                }

                if (App.getTopActivity() != null)
                    App.getTopActivity().httpRequestAction(KitchenOrder.HANDLER_TRANSFER_KOT, null);
                result.put("resultCode", ResultCode.SUCCESS);
                resp = getJsonResponse(new Gson().toJson(result));
            }
            if (action.equals(ParamConst.JOB_MERGER_KOT)) {
                toKotSummary = gson.fromJson(jsonObject.optString("toKotSummary"), KotSummary.class);
                fromKotSummary = gson.fromJson(jsonObject.optString("fromKotSummary"), KotSummary.class);
                List<KotItemDetail> kotItemDetails = KotItemDetailSQL.getKotItemDetailBySummaryId(fromKotSummary.getId());
                KotSummarySQL.update(toKotSummary);
                if (fromKotSummary != null) {
                    for (int i = 0; i < kotItemDetails.size(); i++) {
                        KotItemDetail kotItemDetail = kotItemDetails.get(i);
                        kotItemDetail.setKotSummaryId(toKotSummary.getId());
                        kotItemDetail.setOrderId(toKotSummary.getOrderId());
                        KotItemDetailSQL.update(kotItemDetail);
                    }
                    KotSummarySQL.deleteKotSummary(fromKotSummary);
                }

                if (App.getTopActivity() != null)
                    App.getTopActivity().httpRequestAction(KitchenOrder.HANDLER_MERGER_KOT, null);
                result.put("resultCode", ResultCode.SUCCESS);
                resp = getJsonResponse(new Gson().toJson(result));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resp;
    }

    private Response handlerTransferKotItem(String params) {
        Response resp = null;
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            JSONObject jsonObject = new JSONObject(params);
            Gson gson = new Gson();
            KotSummary toKotSummary = gson.fromJson(jsonObject.optString("toKotSummary"), KotSummary.class);
            KotSummary fromKotSummary = gson.fromJson(jsonObject.optString("fromKotSummary"), KotSummary.class);
            KotItemDetail kotItemDetail = gson.fromJson(jsonObject.optString("tansferKotItem"), KotItemDetail.class);

            KotItemDetailSQL.update(kotItemDetail);
            KotSummarySQL.update(toKotSummary);
            KotSummarySQL.update(fromKotSummary);
            if (App.getTopActivity() != null)
                App.getTopActivity().httpRequestAction(KitchenOrder.HANDLER_MERGER_KOT, null);
            result.put("resultCode", ResultCode.SUCCESS);
            resp = getJsonResponse(new Gson().toJson(result));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resp;
    }

    private Response handlerSubmitNewKot(String params) {
        Response resp = null;
        Map<String, Object> result = new HashMap<>();
        int revenueCenterId = App.instance.getCurrentConnectedMainPos().getRevenueId();
        try {
            JSONObject jsonObject = new JSONObject(params);
            final Gson gson = new Gson();
            String method = jsonObject.optString("method");
            final List<Integer> orderDetailIds = new ArrayList<Integer>();
            if (TextUtils.isEmpty(method)) {
                resp = this.getInternalErrorResponse(App.getTopActivity().getResources().getString(R.string.kot_submit_failed));
                return resp;
            }

            final KotSummary kotSummary = gson.fromJson(jsonObject.optString("kotSummary"), KotSummary.class);
            if (kotSummary == null) {
                resp = this.getInternalErrorResponse(App.getTopActivity().getResources().getString(R.string.kot_submit_failed));
                return resp;
            } else {
                if (revenueCenterId != kotSummary.getRevenueCenterId()) {
                    if (App.getTopActivity() != null)
                        App.getTopActivity().httpRequestAction(App.HANDLER_VERIFY_MAINPOS, null);
                    result.put("resultCode", ResultCode.CONNECTION_FAILED);
                    resp = getJsonResponse(new Gson().toJson(result));
                    return resp;
                }
            }

            if (App.instance.getKdsDevice().getKdsType() != Printer.KDS_SUMMARY &&
                    !ParamConst.JOB_VOID_KOT.equals(method)) {
                App.instance.ringUtil.playRingOnce();
            }

            //region new kot
            if (method.equals(ParamConst.JOB_NEW_KOT)) {
                final List<KotItemDetail> kotItemDetails = gson.fromJson(jsonObject.optString("kotItemDetails"), new TypeToken<List<KotItemDetail>>() {
                }.getType());
                final List<KotItemModifier> kotItemModifiers = gson.fromJson(jsonObject.optString("kotItemModifiers"), new TypeToken<List<KotItemModifier>>() {
                }.getType());
                new Thread(new Runnable() {
                    @Override
                    public void run() {

//                        kotSummary.setOrderDetailCount(kotItemDetails.size());
                        KotSummarySQL.update(kotSummary);
                        if (kotItemDetails != null) {
                            KotItemDetailSQL.addKotItemDetailList(kotItemDetails);
                        }
                        if (kotItemModifiers != null) {
                            KotItemModifierSQL.addKotItemModifierList(kotItemModifiers);
                        }

                        if (App.getTopActivity() != null)
                            App.getTopActivity().httpRequestAction(App.HANDLER_NEW_KOT, null);
                    }
                }).start();

                result.put("resultCode", ResultCode.SUCCESS);
                result.put("method", method);
                result.put("kotSummary", kotSummary);
                result.put("orderId", kotSummary.getOrderId());
                resp = getJsonResponse(new Gson().toJson(result));
            }
            //endregion

            //region update kot
            if (method.equals(ParamConst.JOB_UPDATE_KOT)) {
//				kotSummary = gson.fromJson(jsonObject.optString("kotSummary"), KotSummary.class);
                final List<KotItemDetail> kotItemDetails = gson.fromJson(jsonObject.optString("kotItemDetails"), new TypeToken<List<KotItemDetail>>() {
                }.getType());
                final List<KotItemModifier> kotItemModifiers = gson.fromJson(jsonObject.optString("kotItemModifiers"), new TypeToken<List<KotItemModifier>>() {
                }.getType());
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        if (App.instance.getKdsDevice().getKdsType() == Printer.KDS_SUMMARY ||
                                App.instance.getKdsDevice().getKdsType() == Printer.KDS_EXPEDITER ||
                                App.instance.getKdsDevice().getKdsType() == Printer.KDS_NORMAL) {
                            KotSummarySQL.update(kotSummary);
                            if (kotItemDetails != null) {
                                KotItemDetailSQL.addKotItemDetailList(kotItemDetails);
                                for (int i = 0; i < kotItemDetails.size(); i++) {
                                    orderDetailIds.add(kotItemDetails.get(i).getOrderDetailId());
                                }
                            }
                            if (kotItemModifiers != null) {
                                KotItemModifierSQL.addKotItemModifierList(kotItemModifiers);
                            }

                        } else {//sub kds with fake id
                            int KotSummaryId = CommonSQL.isFakeId(kotSummary.getId()) ? kotSummary.getOriginalId() : kotSummary.getId();
                            KotSummary kotSummaryLocal = KotSummarySQL.getKotSummaryById(KotSummaryId);

                            if (kotSummaryLocal != null) {
                                KotSummarySQL.updateKotSummaryOrderCountById(kotSummary.getOrderDetailCount(),
                                        kotSummaryLocal.getId());
                            }

                            int fakeId = CommonSQL.getFakeId();

                            if (kotItemModifiers != null) {
                                KotItemModifierSQL.addKotItemModifierList(kotItemModifiers);
                            }

                            boolean isFire = false;
                            for (int i = 0; i < kotItemDetails.size(); i++) {
                                if (kotItemDetails.get(i).getFireStatus() == 1) {
                                    KotItemDetail kidLocal = KotItemDetailSQL.
                                            getKotItemDetailById(kotItemDetails.get(i).getId());

                                    if (kidLocal != null) {
                                        kidLocal.setFireStatus(1);
                                        KotItemDetailSQL.update(kidLocal);
                                    }

                                    isFire = true;
                                } else {
                                    kotItemDetails.get(i).setKotSummaryId(fakeId);//assign to fake id
                                    KotItemDetailSQL.update(kotItemDetails.get(i));
                                    orderDetailIds.add(kotItemDetails.get(i).getOrderDetailId());
                                }
                            }

                            if (!isFire) {
                                KotSummarySQL.deleteKotSummaryTmp(kotSummary);
                                kotSummary.setId(fakeId);
//                                kotSummary.setOrderDetailCount(kotItemDetails.size());
                                KotSummarySQL.addKotSummary(kotSummary);
                            }
                        }

                        if (App.getTopActivity() != null) {
                            App.getTopActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (App.getTopActivity() != null)
                                        App.getTopActivity().httpRequestAction(App.HANDLER_UPDATE_KOT, null);
                                }
                            });
                        }

                    }
                }).start();
                result.put("resultCode", ResultCode.SUCCESS);
                result.put("method", method);
                result.put("kotSummary", kotSummary);
                result.put("orderDetailIds", orderDetailIds);
                resp = getJsonResponse(new Gson().toJson(result));
            }
            //endregion

            if (method.equals(ParamConst.JOB_DELETE_KOT)) {
                final List<KotItemDetail> kotItemDetails = gson.fromJson(jsonObject.optString("kotItemDetails"), new TypeToken<List<KotItemDetail>>() {
                }.getType());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        KotItemDetailSQL.deleteKotItemDetail(kotItemDetails);
                        if (App.getTopActivity() != null)
                            App.getTopActivity().httpRequestAction(App.HANDLER_DELETE_KOT, null);
                    }
                }).start();
                result.put("resultCode", ResultCode.SUCCESS);
                resp = getJsonResponse(new Gson().toJson(result));
            }
            if (method.equals(ParamConst.JOB_VOID_KOT)) {
                final List<KotItemDetail> kotItemDetails = gson.fromJson(jsonObject.optString("kotItemDetails"), new TypeToken<List<KotItemDetail>>() {
                }.getType());
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        boolean isFound = false;

                        for (KotItemDetail kotItemDetail : kotItemDetails) {
                            KotItemDetail kotItemDetailLocal = KotItemDetailSQL.getKotItemDetailById(kotItemDetail.getId());
                            if (kotItemDetailLocal != null) {
                                isFound = true;
                                kotItemDetail.setKotStatus(ParamConst.KOT_STATUS_VOID);//状态改变为void
                                KotItemDetailSQL.update(kotItemDetail);
                            }
                        }

                        KotSummarySQL.update(kotSummary);//update total order count

                        if (isFound) {
                            App.instance.ringUtil.playRingOnce();

                            if (App.getTopActivity() != null)
                                App.getTopActivity().httpRequestAction(App.HANDLER_REFRESH_KOT, null);
                        }
                    }
                }).start();
                result.put("resultCode", ResultCode.SUCCESS);
                resp = getJsonResponse(new Gson().toJson(result));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            resp = this.getInternalErrorResponse(App.getTopActivity().getResources().getString(R.string.kot_submit_failed));
        }
        return resp;
    }

    private Response respond(Map<String, String> headers, IHTTPSession session, String uri) {
        // Remove URL arguments
        uri = uri.trim().replace(File.separatorChar, '/');
        if (uri.indexOf('?') >= 0) {
            uri = uri.substring(0, uri.indexOf('?'));
        }

        // Prohibit getting out of current directory
        if (uri.startsWith("src/main") || uri.endsWith("src/main") || uri.contains("../")) {
            return getForbiddenResponse("Won't serve ../ for security reasons.");
        }

//        boolean canServeUri = false;
//        File f = new File(homeDir, uri);
//        if (f.isDirectory() && !uri.endsWith("/")) {
//            uri += "/";
//            Response res = createResponse(Response.Status.REDIRECT, NanoHTTPD.MIME_HTML, "<html><body>Redirected: <a href=\"" +
//                uri + "\">" + uri + "</a></body></html>");
//            res.addHeader("Location", uri);
//            return res;
//        }

        //return createResponse(Response.Status.OK, NanoHTTPD.MIME_HTML, listDirectory(uri, f));

        return getNotFoundResponse();
    }

    private boolean validMessageFromConnectedPOS(String params) {
        boolean ret = false;
        try {
            Gson gson = new Gson();
            JSONObject jsonObject = new JSONObject(params);
            MainPosInfo pos = gson.fromJson(
                    jsonObject.optString("mainpos"), MainPosInfo.class);
            MainPosInfo connectedPos = App.instance.getCurrentConnectedMainPos();

            //old POS version(<1.0.1) POS dont have mainpos object in request
            if (pos == null)
                return true;
            if (connectedPos != null &&
                    pos.getRestId().intValue() == connectedPos.getRestId().intValue()
                    && pos.getRevenueId().intValue() == connectedPos.getRevenueId().intValue()) {
                ret = true;
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ret = false;
        }

        return ret;
    }

    private Response handlerSessionClose(String params) {
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            Gson gson = new Gson();
            JSONObject jsonObject = new JSONObject(params);
            SessionStatus sessionStatus = gson.fromJson(jsonObject.optString("session"), SessionStatus.class);
            if (App.getTopActivity() != null) {
                App.getTopActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        App.getTopActivity().showOneButtonCompelDialog(App.getTopActivity().getResources().getString(R.string.session_change),
                                App.getTopActivity().getResources().getString(R.string.relogin_pos),
                                new OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        KotSummarySQL.deleteAllKotSummary();
                                        KotItemDetailSQL.deleteAllKotItemDetail();
                                        KotItemModifierSQL.deleteAllKotItemModifier();
//							App.instance.popAllActivityExceptOne(EmployeeID.class);
                                        UIHelp.startWelcome(App.getTopActivity());
                                        App.instance.popAllActivityExceptOne(Login.class);
                                    }
                                });

                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        result.put("resultCode", ResultCode.SUCCESS);
        Response resp = this.getJsonResponse(new Gson().toJson(result));
        return resp;
    }

    private Response invalidDeviceResponse() {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("resultCode", ResultCode.INVALID_DEVICE);
        Response resp = this.getJsonResponse(new Gson().toJson(result));
        return resp;
    }
}
