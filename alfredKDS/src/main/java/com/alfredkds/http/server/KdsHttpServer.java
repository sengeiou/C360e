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
import com.alfredbase.javabean.model.MainPosInfo;
import com.alfredbase.javabean.model.SessionStatus;
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
                App.instance.ringUtil.playRingOnce();
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
            } else {
                resp = getNotFoundResponse();
            }
        }

        return resp;
    }

    private Response handlerTmpKot(String params) {
        Response resp = null;
        Map<String, Object> result = new HashMap<>();
        int revenueCenterId = App.instance.getCurrentConnectedMainPos().getRevenueId();

        try {
            JSONObject jsonObject = new JSONObject(params);
            final Gson gson = new Gson();
            String method = jsonObject.optString("method");
            final List<Integer> orderDetailIds = new ArrayList<>();
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

            if (method.equals(ParamConst.JOB_NEW_KOT)) {
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

                        App.getTopActivity().httpRequestAction(App.HANDLER_NEW_KOT, null);
                    }
                }).start();

                result.put("resultCode", ResultCode.SUCCESS);
                result.put("method", method);
                result.put("kotSummary", kotSummary);
                result.put("orderId", kotSummary.getOrderId());
                resp = getJsonResponse(new Gson().toJson(result));
            }

            if (method.equals(ParamConst.JOB_UPDATE_KOT)) {
                final List<KotItemDetail> kotItemDetails = gson.fromJson(jsonObject.optString("kotItemDetails"), new TypeToken<List<KotItemDetail>>() {
                }.getType());
                final List<KotItemModifier> kotItemModifiers = gson.fromJson(jsonObject.optString("kotItemModifiers"), new TypeToken<List<KotItemModifier>>() {
                }.getType());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        KotSummarySQL.update(kotSummary);
                        if (kotItemModifiers != null) {
                            KotItemModifierSQL.addKotItemModifierList(kotItemModifiers);
                        }
                        for (int i = 0; i < kotItemDetails.size(); i++) {
                            KotItemDetail kotItemDetail = KotItemDetailSQL
                                    .getMainKotItemDetailByOrderDetailId(kotSummary.getId(),
                                            kotItemDetails.get(i).getOrderDetailId());

                            if (kotItemDetail != null) {
                                if (kotItemDetails.get(i).getKotStatus() < ParamConst.KOT_STATUS_DONE) {
                                    kotItemDetails.get(i).setKotStatus(ParamConst.KOT_STATUS_UPDATE);
                                    KotItemDetailSQL.update(kotItemDetails.get(i));
                                    orderDetailIds.add(kotItemDetails.get(i).getOrderDetailId());
                                } else {
                                    //TODO
                                }
                            } else {
                                KotItemDetailSQL.update(kotItemDetails.get(i));
                                orderDetailIds.add(kotItemDetails.get(i).getOrderDetailId());
                            }
                        }


                        App.getTopActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                App.getTopActivity().httpRequestAction(App.HANDLER_UPDATE_KOT, null);
                            }
                        });

                    }
                }).start();
                result.put("resultCode", ResultCode.SUCCESS);
                result.put("method", method);
                result.put("kotSummary", kotSummary);
                result.put("orderDetailIds", orderDetailIds);
                resp = getJsonResponse(new Gson().toJson(result));
            }

            if (method.equals(ParamConst.JOB_DELETE_KOT)) {
                final List<KotItemDetail> kotItemDetails = gson.fromJson(jsonObject.optString("kotItemDetails"), new TypeToken<List<KotItemDetail>>() {
                }.getType());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        KotItemDetailSQL.deleteKotItemDetail(kotItemDetails);
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
                        for (KotItemDetail kotItemDetail : kotItemDetails) {
                            kotItemDetail.setKotStatus(ParamConst.KOT_STATUS_VOID);//状态改变为void
                            KotItemDetailSQL.update(kotItemDetail);
                        }
                        App.getTopActivity().httpRequestAction(App.HANDLER_REFRESH_KOT, null);
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
            KotItemDetail kotItemDetail = gson.fromJson(jsonObject.optString("tansferKotItem"), KotItemDetail.class);
            KotItemDetailSQL.update(kotItemDetail);
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
        Map<String, Object> result = new HashMap<String, Object>();
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
                    App.getTopActivity().httpRequestAction(App.HANDLER_VERIFY_MAINPOS, null);
                    result.put("resultCode", ResultCode.CONNECTION_FAILED);
                    resp = getJsonResponse(new Gson().toJson(result));
                    return resp;
                }
            }

            if (method.equals(ParamConst.JOB_NEW_KOT)) {
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

                        App.getTopActivity().httpRequestAction(App.HANDLER_NEW_KOT, null);
                    }
                }).start();

                result.put("resultCode", ResultCode.SUCCESS);
                result.put("method", method);
                result.put("kotSummary", kotSummary);
                result.put("orderId", kotSummary.getOrderId());
                resp = getJsonResponse(new Gson().toJson(result));
            }

            if (method.equals(ParamConst.JOB_UPDATE_KOT)) {
//				kotSummary = gson.fromJson(jsonObject.optString("kotSummary"), KotSummary.class);
                final List<KotItemDetail> kotItemDetails = gson.fromJson(jsonObject.optString("kotItemDetails"), new TypeToken<List<KotItemDetail>>() {
                }.getType());
                final List<KotItemModifier> kotItemModifiers = gson.fromJson(jsonObject.optString("kotItemModifiers"), new TypeToken<List<KotItemModifier>>() {
                }.getType());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        KotSummarySQL.update(kotSummary);
                        if (kotItemModifiers != null) {
                            KotItemModifierSQL.addKotItemModifierList(kotItemModifiers);
                        }
                        for (int i = 0; i < kotItemDetails.size(); i++) {
//					KotItemDetail kotItemDetail = KotItemDetailSQL.getKotItemDetailByOrderDetailId(
//							kotItemDetails.get(i).getOrderDetailId(),kotItemDetails.get(i).getCategoryId());
                            KotItemDetail kotItemDetail = KotItemDetailSQL.getMainKotItemDetailByOrderDetailId(kotSummary.getId(), kotItemDetails.get(i).getOrderDetailId());
                            if (kotItemDetail != null) {
                                if (kotItemDetails.get(i).getKotStatus() < ParamConst.KOT_STATUS_DONE) {
                                    kotItemDetails.get(i).setKotStatus(ParamConst.KOT_STATUS_UPDATE);
                                    KotItemDetailSQL.update(kotItemDetails.get(i));
                                    orderDetailIds.add(kotItemDetails.get(i).getOrderDetailId());
                                } else {
                                    //TODO
                                }
                            } else {
                                KotItemDetailSQL.update(kotItemDetails.get(i));
                                orderDetailIds.add(kotItemDetails.get(i).getOrderDetailId());
                            }
                        }


                        App.getTopActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                App.getTopActivity().httpRequestAction(App.HANDLER_UPDATE_KOT, null);
                            }
                        });

                    }
                }).start();
                result.put("resultCode", ResultCode.SUCCESS);
                result.put("method", method);
                result.put("kotSummary", kotSummary);
                result.put("orderDetailIds", orderDetailIds);
                resp = getJsonResponse(new Gson().toJson(result));
            }

            if (method.equals(ParamConst.JOB_DELETE_KOT)) {
                final List<KotItemDetail> kotItemDetails = gson.fromJson(jsonObject.optString("kotItemDetails"), new TypeToken<List<KotItemDetail>>() {
                }.getType());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        KotItemDetailSQL.deleteKotItemDetail(kotItemDetails);
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
                        for (KotItemDetail kotItemDetail : kotItemDetails) {
                            kotItemDetail.setKotStatus(ParamConst.KOT_STATUS_VOID);//状态改变为void
                            KotItemDetailSQL.update(kotItemDetail);
                        }
                        App.getTopActivity().httpRequestAction(App.HANDLER_REFRESH_KOT, null);
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
