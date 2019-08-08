package com.alfredposclient.http;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import com.alfredbase.ParamConst;
import com.alfredbase.http.AsyncHttpResponseHandlerEx;
import com.alfredbase.http.ResultCode;
import com.alfredbase.javabean.KotItemDetail;
import com.alfredbase.javabean.KotItemModifier;
import com.alfredbase.javabean.KotSummary;
import com.alfredbase.javabean.Printer;
import com.alfredbase.javabean.model.KDSDevice;
import com.alfredbase.store.sql.CommonSQL;
import com.alfredbase.store.sql.KotItemDetailSQL;
import com.alfredbase.store.sql.KotItemModifierSQL;
import com.alfredbase.store.sql.KotSummarySQL;
import com.alfredbase.utils.KDSLogUtil;
import com.alfredbase.utils.LogUtil;
import com.alfredposclient.global.App;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.SyncHttpClient;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HTTPKDSRequest {

    public static void syncSubmitTmpKot(Context context, Map<String, Object> parameters, String url, final KDSDevice kds,
                                        SyncHttpClient syncHttpClient, final Handler handler) throws Exception {

        parameters.put("mainpos", App.instance.getMainPosInfo());

        syncHttpClient.post(context, url,
                new StringEntity(new Gson().toJson(parameters) + HttpAPI.EOF,
                        "UTF-8"), HttpAssembling.CONTENT_TYPE,
                new AsyncHttpResponseHandlerEx() {
                    @Override
                    public void onSuccess(final int statusCode, final Header[] headers,
                                          final byte[] responseBody) {
                        super.onSuccess(statusCode, headers, responseBody);
                        if (resultCode == ResultCode.INVALID_DEVICE) {
                            // if kds device is invadate, POS need remove it.
//									App.instance.removeKDSDevice(kds.getDevice_id());
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] responseBody, Throwable error) {
                        if (error.getCause() instanceof ConnectException) {
                            throw new RuntimeException(error);
                        }
                    }
                });
    }

    public static void syncSubmitKotToNextKDS(Context context, Map<String, Object> parameters, String url, final KDSDevice kds,
                                              SyncHttpClient syncHttpClient, final Handler handler) throws Exception {

        parameters.put("mainpos", App.instance.getMainPosInfo());
        final KotSummary kotSummary = (KotSummary) parameters.get("kotSummary");
        final List<KotItemDetail> kotItemDetails = (List<KotItemDetail>) parameters.get("kotItemDetails");

        syncHttpClient.post(context, url,
                new StringEntity(new Gson().toJson(parameters) + HttpAPI.EOF,
                        "UTF-8"), HttpAssembling.CONTENT_TYPE,
                new AsyncHttpResponseHandlerEx() {
                    @Override
                    public void onSuccess(final int statusCode, final Header[] headers,
                                          final byte[] responseBody) {
                        super.onSuccess(statusCode, headers, responseBody);
                        if (resultCode == ResultCode.INVALID_DEVICE) {
                            // if kds device is invadate, POS need remove it.
//									App.instance.removeKDSDevice(kds.getDevice_id());
                        } else if (resultCode == ResultCode.SUCCESS) {
                            if (kotSummary != null) {
                                int kotSummaryId = kotSummary.getId() <= 0 ? kotSummary.getOriginalId() : kotSummary.getId();

                                KotSummary kotSummaryLocal = KotSummarySQL.getKotSummaryById(kotSummaryId);

                                if (kotSummaryLocal == null) return;

                                List<KotItemDetail> kotItemDetailsCopy = new ArrayList<>();
                                if (kotItemDetails != null) {

                                    for (KotItemDetail kotItemDetail : kotItemDetails) {
                                        KotItemDetail kidLocal = KotItemDetailSQL.getKotItemDetailById(kotItemDetail.getId());
                                        if (kidLocal == null) continue;

                                        kidLocal.setStartTime(System.currentTimeMillis());

                                        if (kds.getKdsType() == Printer.KDS_EXPEDITER ||
                                                kds.getKdsType() == Printer.KDS_SUMMARY) {
                                            kidLocal.setEndTime(System.currentTimeMillis());
                                        }
                                        KotItemDetailSQL.update(kidLocal);
                                        kotItemDetailsCopy.add(kidLocal);
                                    }
                                }

                                kotSummaryLocal.setKotSummaryLog(KDSLogUtil.putKdsLog(kotSummaryLocal, kotItemDetailsCopy, kds));
                                KotSummarySQL.updateKotSummaryLog(kotSummaryLocal);

                                sendKOTToSummaryKDS(kotSummaryLocal);
                            }
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] responseBody, Throwable error) {
                        if (error.getCause() instanceof ConnectException) {
                            throw new RuntimeException(error);
                        }
                    }
                });
    }

    public static void syncSubmitKotToSummaryKDS(Context context, Map<String, Object> parameters, String url, final KDSDevice kds,
                                                 SyncHttpClient syncHttpClient, final Handler handler) throws Exception {

        parameters.put("mainpos", App.instance.getMainPosInfo());

        syncHttpClient.post(context, url,
                new StringEntity(new Gson().toJson(parameters) + HttpAPI.EOF,
                        "UTF-8"), HttpAssembling.CONTENT_TYPE,
                new AsyncHttpResponseHandlerEx() {
                    @Override
                    public void onSuccess(final int statusCode, final Header[] headers,
                                          final byte[] responseBody) {
                        super.onSuccess(statusCode, headers, responseBody);
                        if (resultCode == ResultCode.INVALID_DEVICE) {
                        } else if (resultCode == ResultCode.SUCCESS) {
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] responseBody, Throwable error) {
                        if (error.getCause() instanceof ConnectException) {
                            throw new RuntimeException(error);
                        }
                    }
                });
    }

    public static void deleteKotSummary(Context context, Map<String, Object> parameters, String url, final KDSDevice kds,
                                        SyncHttpClient syncHttpClient, final Handler handler) throws Exception {

        parameters.put("mainpos", App.instance.getMainPosInfo());

        syncHttpClient.post(context, url,
                new StringEntity(new Gson().toJson(parameters) + HttpAPI.EOF,
                        "UTF-8"), HttpAssembling.CONTENT_TYPE,
                new AsyncHttpResponseHandlerEx() {
                    @Override
                    public void onSuccess(final int statusCode, final Header[] headers,
                                          final byte[] responseBody) {
                        super.onSuccess(statusCode, headers, responseBody);
                        if (resultCode == ResultCode.INVALID_DEVICE) {
                            // if kds device is invadate, POS need remove it.
//									App.instance.removeKDSDevice(kds.getDevice_id());
                        } else if (resultCode == ResultCode.SUCCESS) {
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] responseBody, Throwable error) {
                        if (error.getCause() instanceof ConnectException) {
                            throw new RuntimeException(error);
                        }
                    }
                });
    }

    public static void syncSubmitKot(Context context, final Map<String, Object> parameters, String url, final KDSDevice kds,
                                     SyncHttpClient syncHttpClient, final Handler handler) throws Exception {

        parameters.put("mainpos", App.instance.getMainPosInfo());
        final KotSummary kotSummary = (KotSummary) parameters.get("kotSummary");
        final List<KotItemDetail> kotItemDetails = (List<KotItemDetail>) parameters.get("kotItemDetails");

        syncHttpClient.post(context, url,
                new StringEntity(new Gson().toJson(parameters) + HttpAPI.EOF,
                        "UTF-8"), HttpAssembling.CONTENT_TYPE,
                new AsyncHttpResponseHandlerEx() {
                    @Override
                    public void onSuccess(final int statusCode, final Header[] headers,
                                          final byte[] responseBody) {
                        super.onSuccess(statusCode, headers, responseBody);
                        if (resultCode == ResultCode.SUCCESS) {
                            boolean isFire = false;
                            if (parameters.containsKey("isFire"))
                                isFire = (boolean) parameters.get("isFire");

                            if (isFire) return;

                            if (kotSummary != null) {

                                List<KotItemDetail> kotItemDetailsCopy = new ArrayList<>();

                                if (kotItemDetails != null) {
                                    for (KotItemDetail kotItemDetail : kotItemDetails) {
                                        kotItemDetail.setStartTime(System.currentTimeMillis());
                                        KotItemDetailSQL.update(kotItemDetail);
                                        kotItemDetailsCopy.add(kotItemDetail);
                                    }
                                }

                                int kotSummaryId = CommonSQL.isFakeId(kotSummary.getId()) ? kotSummary.getOriginalId() : kotSummary.getId();

                                KotSummary kotSummaryLocal = KotSummarySQL.getKotSummaryById(kotSummaryId);
                                if (!TextUtils.isEmpty(kotSummaryLocal.getKotSummaryLog())) {
                                    kotSummary.setKotSummaryLog(kotSummaryLocal.getKotSummaryLog());
                                }

                                kotSummary.setKotSummaryLog(KDSLogUtil.putKdsLog(kotSummary, kotItemDetailsCopy, kds));
                                KotSummarySQL.updateKotSummaryLog(kotSummary);

                                sendKOTToSummaryKDS(kotSummary);

                            }
                        } else if (resultCode == ResultCode.INVALID_DEVICE) {
                            // if kds device is invadate, POS need remove it.
//									App.instance.removeKDSDevice(kds.getDevice_id());
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] responseBody, Throwable error) {
                        if (error.getCause() instanceof ConnectException) {
                            throw new RuntimeException(error);
                        }
                    }
                });
    }

    private static void sendKOTToSummaryKDS(KotSummary kotSummary) {
        ArrayList<KotItemDetail> kotItemDetails = KotItemDetailSQL.getKotItemDetailBySummaryId(kotSummary.getId());
        ArrayList<KotItemModifier> kotItemModifiers = new ArrayList<>();

        for (KotItemDetail kotItemDetail : kotItemDetails) {
            kotItemModifiers.addAll(KotItemModifierSQL.getKotItemModifiersByKotItemDetail(kotItemDetail.getId()));
        }

        App.instance.getKdsJobManager().sendKOTToKDSSummary(
                kotSummary, kotItemDetails,
                kotItemModifiers, ParamConst.JOB_KOT_SUMMARY);
    }

//	public static void asyncSubmitKot(Context context, Map<String, Object> parameters, String url,
//			AsyncHttpClient httpClient, final Handler handler) throws Exception {
//						
//		httpClient.post(context,url, 
//					 new StringEntity(new Gson().toJson(parameters) + HttpAPI.EOF, 
//							 "UTF-8"),HttpAssembling.CONTENT_TYPE,
//						new AsyncHttpResponseHandlerEx() {
//							@Override
//							public void onSuccess(int statusCode, Header[] headers,
//									byte[] responseBody) {
//								super.onSuccess(statusCode, headers, responseBody);
//								if (resultCode == ResultCode.SUCCESS) {
//									
//								}
//							}
//							@Override
//							public void onFailure(int statusCode, Header[] headers,
//									byte[] responseBody, Throwable error) {
//								if (error.getCause() instanceof ConnectException) {
//								  throw new RuntimeException(error);
//								}
//							}			
//			});
//	}

    public static void sendSessionClose(Context context, Map<String, Object> parameters, String url, final KDSDevice kds,
                                        AsyncHttpClient httpClient) {
        parameters.put("mainpos", App.instance.getMainPosInfo());
        try {
            httpClient.post(context, url,
                    new StringEntity(new Gson().toJson(parameters), "UTF-8"), HttpAssembling.CONTENT_TYPE,
                    new AsyncHttpResponseHandlerEx() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers,
                                              byte[] responseBody) {
                            super.onSuccess(statusCode, headers, responseBody);
                            if (resultCode == ResultCode.SUCCESS) {
                                LogUtil.i("sendSessionClose", "SUCCESS");
                            } else if (resultCode == ResultCode.INVALID_DEVICE) {
                                //: if waiter device is invadate, POS need remove it.
                                App.instance.removeKDSDevice(kds.getDevice_id());
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers,
                                              byte[] responseBody, Throwable error) {
                            LogUtil.e("sendSessionClose", "FAILURE");
                        }
                    });
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void transferTable(Context context, Map<String, Object> parameters, String url, final KDSDevice kds,
                                     AsyncHttpClient httpClient) {
        parameters.put("mainpos", App.instance.getMainPosInfo());
        try {
            httpClient.post(context, url,
                    new StringEntity(new Gson().toJson(parameters), "UTF-8"), HttpAssembling.CONTENT_TYPE,
                    new AsyncHttpResponseHandlerEx() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers,
                                              byte[] responseBody) {
                            super.onSuccess(statusCode, headers, responseBody);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers,
                                              byte[] responseBody, Throwable error) {
                            if (error.getCause() instanceof ConnectException) {
                                throw new RuntimeException(error);
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
