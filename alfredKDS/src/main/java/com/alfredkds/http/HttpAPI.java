package com.alfredkds.http;

import android.content.Context;
import android.os.Handler;

import com.alfredbase.ParamConst;
import com.alfredbase.global.CoreData;
import com.alfredbase.http.AsyncHttpResponseHandlerEx;
import com.alfredbase.http.ResultCode;
import com.alfredbase.javabean.KotItemDetail;
import com.alfredbase.javabean.KotSummary;
import com.alfredbase.store.sql.KotItemDetailSQL;
import com.alfredbase.store.sql.KotSummarySQL;
import com.alfredkds.activity.KotHistory;
import com.alfredkds.activity.Login;
import com.alfredkds.activity.SelectKitchen;
import com.alfredkds.activity.Setting;
import com.alfredkds.global.App;
import com.alfredkds.global.UIHelp;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/* KDS Requests main POS */
public class HttpAPI {
	/**
	 * 用于pos server判断数据结束
	 */
	private static final String EOF = "\r\nEOF\r\n";
	private static final String CONTENT_TYPE = "text/plain;charset=UTF-8";

	public static void login(final Context context, Map<String, Object> parameters,
			String url, AsyncHttpClient httpClient, final Handler handler) {
		if (parameters != null) {
			parameters.put("appVersion", App.instance.VERSION);
		}
		try {
			httpClient.post(context, url,
					new StringEntity(new Gson().toJson(parameters) + EOF,
							"UTF-8"), CONTENT_TYPE,
					new AsyncHttpResponseHandlerEx() {
						@Override
						public void onSuccess(int statusCode, Header[] headers,
								byte[] responseBody) {
							super.onSuccess(statusCode, headers, responseBody);
							if (resultCode == ResultCode.SUCCESS) {
								HttpAnalysis.login(statusCode, headers,
										responseBody,context);
								handler.sendMessage(handler
										.obtainMessage(Login.HANDLER_LOGIN));
							} else {
								elseResultCodeAction(resultCode, statusCode, headers, responseBody);
							}
						}
						@Override
						public void onFailure(int statusCode, Header[] headers,
								byte[] responseBody, Throwable error) {
							// TODO Auto-generated method stub
							handler.sendMessage(handler.obtainMessage(ResultCode.CONNECTION_FAILED,error));
							super.onFailure(statusCode, headers, responseBody, error);
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void getPrinters(final Context context,
			Map<String, Object> parameters, String url,
			AsyncHttpClient httpClient, final Handler handler) {
		if (parameters != null) {
			parameters.put("appVersion", App.instance.VERSION);
		}
		try {
			httpClient.post(context, url,
					new StringEntity(new Gson().toJson(parameters) + EOF,
							"UTF-8"), CONTENT_TYPE,
					new AsyncHttpResponseHandlerEx() {
						@Override
						public void onSuccess(int statusCode, Header[] headers,
								byte[] responseBody) {
							super.onSuccess(statusCode, headers, responseBody);
							if (resultCode == ResultCode.SUCCESS) {
								handler.sendMessage(handler.obtainMessage(ResultCode.SUCCESS,
										HttpAnalysis.getPrinterList(statusCode, headers, responseBody)));
							}else if(resultCode == ResultCode.USER_NO_PERMIT) {
								handler.sendEmptyMessage(ResultCode.USER_NO_PERMIT);
							} else {
								elseResultCodeAction(resultCode, statusCode, headers, responseBody);
							}
						}
						@Override
						public void onFailure(int statusCode, Header[] headers,
								byte[] responseBody, Throwable error) {
							error.printStackTrace();
							handler.sendMessage(handler.obtainMessage(ResultCode.CONNECTION_FAILED,error));
							super.onFailure(statusCode, headers, responseBody, error);
						}						
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/* Send KDS device details to POS when pairing completes */
	public static void pairingComplete(final Context context,
			Map<String, Object> parameters, String url,AsyncHttpClient httpClient, final Handler handler) {
		if (parameters != null) {
			parameters.put("appVersion", App.instance.VERSION);
		}
		try {
			httpClient.post(context, url,
					new StringEntity(new Gson().toJson(parameters) + EOF,
							"UTF-8"), CONTENT_TYPE,
					new AsyncHttpResponseHandlerEx() {
						@Override
						public void onSuccess(int statusCode, Header[] headers,
								byte[] responseBody) {
							super.onSuccess(statusCode, headers, responseBody);
							if (resultCode == ResultCode.SUCCESS) {
								HttpAnalysis.getMainPosInfo(statusCode, headers, responseBody, handler);
							} else {
								handler.sendMessage(handler.obtainMessage(SelectKitchen.HANDLER_ERROR,resultCode));
								elseResultCodeAction(resultCode, statusCode, headers, responseBody);
							}
						}
						@Override
						public void onFailure(int statusCode, Header[] headers,
								byte[] responseBody, Throwable error) {
							error.printStackTrace();
							handler.sendMessage(handler.obtainMessage(ResultCode.CONNECTION_FAILED,error));
							super.onFailure(statusCode, headers, responseBody, error);
						}							
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*Report KDS Dynamic IP change*/
	public static void kdsIpChange(final Context context,
			Map<String, Object> parameters, String url,
			AsyncHttpClient httpClient, final Handler handler) {
		if (parameters != null) {
			parameters.put("userKey", CoreData.getInstance().getUserKey());			
			parameters.put("appVersion", App.instance.VERSION);
		}
		try {
			httpClient.post(context, url,
					new StringEntity(new Gson().toJson(parameters) + EOF,
							"UTF-8"), CONTENT_TYPE,
					new AsyncHttpResponseHandlerEx() {
						@Override
						public void onSuccess(int statusCode, Header[] headers,
								byte[] responseBody) {
							super.onSuccess(statusCode, headers, responseBody);
							if (resultCode == ResultCode.SUCCESS) {
							} else {
//								elseResultCodeAction(resultCode, statusCode, headers, responseBody);
							}
						}
						
						@Override
						public void onFailure(int statusCode, Header[] headers,
								byte[] responseBody, Throwable error) {
							error.printStackTrace();
//							UIHelp.showToast((BaseApplication)context, context.getResources().getString(R.string.network_error));
						}						
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*Complete items in KOT*/
	public static void KotComplete(final Context context,
			final Map<String, Object> parameters, String url,
			AsyncHttpClient httpClient, final Handler handler) {
		if (parameters != null) {
			parameters.put("userKey", CoreData.getInstance().getUserKey());
			parameters.put("appVersion", App.instance.VERSION);
		}
		try {
			httpClient.post(context, url,
					new StringEntity(new Gson().toJson(parameters) + EOF,
							"UTF-8"), CONTENT_TYPE,
					new AsyncHttpResponseHandlerEx() {
						@Override
						public void onSuccess(int statusCode, Header[] headers,
								byte[] responseBody) {
							super.onSuccess(statusCode, headers, responseBody);
							if (resultCode == ResultCode.SUCCESS) {
								List<KotItemDetail> kotItemDetails = (List<KotItemDetail>) parameters.get("kotItemDetails");
								HttpAnalysis.getKotItemDetail(statusCode,headers,responseBody, handler);
								handler.sendMessage(handler.obtainMessage(App.HANDLER_REFRESH_KOT,kotItemDetails));
							}else if (resultCode == ResultCode.USER_NO_PERMIT) {
								handler.sendMessage(handler.obtainMessage(App.HANDLER_RECONNECT_POS));
							}else if (resultCode == ResultCode.KOT_COMPLETE_USER_FAILED) {
								handler.sendMessage(handler.obtainMessage(App.HANDLER_KOT_COMPLETE_USER_FAILED));
							}else if (resultCode == ResultCode.KOT_COMPLETE_FAILED) {
								List<KotItemDetail> kotItemDetails = (List<KotItemDetail>) parameters.get("kotItemDetails");
								for (int i = 0; i < kotItemDetails.size(); i++) {
									//将数据库的数据变为原来的数据
									KotItemDetail kotItemDetail = kotItemDetails.get(i);
									KotItemDetail sqlKotItemDetail = KotItemDetailSQL.getKotItemDetailById(kotItemDetail.getId());
									sqlKotItemDetail.setUnFinishQty(sqlKotItemDetail.getUnFinishQty() + kotItemDetail.getFinishQty());
									sqlKotItemDetail.setFinishQty(sqlKotItemDetail.getFinishQty()-kotItemDetail.getFinishQty());
									sqlKotItemDetail.setKotStatus(ParamConst.KOT_STATUS_UNDONE);
									KotItemDetailSQL.update(sqlKotItemDetail);
								}
								handler.sendMessage(handler.obtainMessage(App.HANDLER_RETURN_ERROR,resultCode));
								handler.sendMessage(handler.obtainMessage(App.HANDLER_RETURN_ERROR_SHOW,kotItemDetails));
							}else if (resultCode == ResultCode.KOTSUMMARY_IS_UNREAL) {
								KotSummary kotSummary = (KotSummary) parameters.get("kotSummary");
								KotSummarySQL.deleteKotSummary(kotSummary);
								KotItemDetailSQL.deleteAllKotItemDetailByKotSummary(kotSummary);
								handler.sendMessage(handler.obtainMessage(App.HANDLER_KOTSUMMARY_IS_UNREAL));
							} else {
								elseResultCodeAction(resultCode, statusCode, headers, responseBody);
							}
						}
						@SuppressWarnings("unchecked")
						@Override
						public void onFailure(int statusCode, Header[] headers,
								byte[] responseBody, Throwable error) {
							error.printStackTrace();
							List<KotItemDetail> kotItemDetails = (List<KotItemDetail>) parameters.get("kotItemDetails");;
							for (int i = 0; i < kotItemDetails.size(); i++) {
								KotItemDetail kotItemDetail = kotItemDetails.get(i);
								KotItemDetail sqlKotItemDetail = KotItemDetailSQL.getKotItemDetailById(kotItemDetail.getId());
								sqlKotItemDetail.setUnFinishQty(sqlKotItemDetail.getUnFinishQty() + kotItemDetail.getFinishQty());
								sqlKotItemDetail.setFinishQty(sqlKotItemDetail.getFinishQty()-kotItemDetail.getFinishQty());
								sqlKotItemDetail.setKotStatus(ParamConst.KOT_STATUS_UNDONE);
								KotItemDetailSQL.update(sqlKotItemDetail);
							}
							handler.sendMessage(handler.obtainMessage(App.HANDLER_SEND_FAILURE,kotItemDetails));
							handler.sendMessage(handler.obtainMessage(ResultCode.CONNECTION_FAILED,error));
							super.onFailure(statusCode, headers, responseBody, error);
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/*cancel complete items in KOT*/
	public static void CancelComplete(Context context,
			final Map<String, Object> parameters, String url,
			AsyncHttpClient httpClient, final Handler handler){
		if (parameters != null) {
			parameters.put("userKey", CoreData.getInstance().getUserKey());
			parameters.put("appVersion", App.instance.VERSION);
		}
		try {
			httpClient.post(context, url,
					new StringEntity(new Gson().toJson(parameters) + EOF,
							"UTF-8"), CONTENT_TYPE,
					new AsyncHttpResponseHandlerEx() {
						@Override
						public void onSuccess(int statusCode, Header[] headers,
								byte[] responseBody) {
							super.onSuccess(statusCode, headers, responseBody);
							if (resultCode == ResultCode.SUCCESS) {
								HttpAnalysis.getNewKotItemDetail(statusCode,headers,responseBody, handler);
								KotItemDetail kotItemDetail = (KotItemDetail) parameters.get("kotItemDetail");
								KotItemDetailSQL.deleteKotItemDetail(kotItemDetail);
								handler.sendMessage(handler.obtainMessage(KotHistory.HANDLER_SUCCEED));
							}else if (resultCode == ResultCode.USER_NO_PERMIT) {
								handler.sendMessage(handler.obtainMessage(KotHistory.HANDLER_RECONNECT_POS));
							}else if (resultCode == ResultCode.KOT_COMPLETE_USER_FAILED) {
								handler.sendMessage(handler.obtainMessage(App.HANDLER_KOT_COMPLETE_USER_FAILED));
							}else if (resultCode == ResultCode.KOTSUMMARY_IS_UNREAL) {
								KotItemDetail kotItemDetail = (KotItemDetail) parameters.get("kotItemDetail");
								KotItemDetailSQL.deleteKotItemDetail(kotItemDetail);
								KotSummary kotSummary = (KotSummary) parameters.get("kotSummary");
								KotSummarySQL.deleteKotSummary(kotSummary);
								KotItemDetailSQL.deleteAllKotItemDetailByKotSummary(kotSummary);
								handler.sendMessage(handler.obtainMessage(App.HANDLER_KOTSUMMARY_IS_UNREAL));
							}else{
								elseResultCodeAction(resultCode, statusCode, headers, responseBody);
							}
						}
						@Override
						public void onFailure(int statusCode, Header[] headers,
								byte[] responseBody, Throwable error) {
//							KotItemDetail kotItemDetail = (KotItemDetail) parameters.get("kotItemDetail");
//							kotItemDetail.setKotStatus(ParamConst.KOT_STATUS_DONE);
//							KotItemDetailSQL.update(kotItemDetail);	
							handler.sendMessage(handler.obtainMessage(KotHistory.HANDLER_SEND_FAILURE));
							handler.sendMessage(handler.obtainMessage(ResultCode.CONNECTION_FAILED,error));
							super.onFailure(statusCode, headers, responseBody, error);
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void Logout(Context context,
			final Map<String, Object> parameters, String url,
			AsyncHttpClient httpClient, final Handler handler){
		if (parameters != null) {
			parameters.put("userKey", CoreData.getInstance().getUserKey());
			parameters.put("appVersion", App.instance.VERSION);
		}
		try {
			httpClient.post(context, url,
					new StringEntity(new Gson().toJson(parameters) + EOF,
							"UTF-8"), CONTENT_TYPE,
					new AsyncHttpResponseHandlerEx() {
						@Override
						public void onSuccess(int statusCode, Header[] headers,
								byte[] responseBody) {
							super.onSuccess(statusCode, headers, responseBody);
							if (resultCode == ResultCode.SUCCESS) {
								handler.sendMessage(handler.obtainMessage(Setting.HANDLER_LOGOUT_SUCCESS));
							} else {
								elseResultCodeAction(resultCode, statusCode, headers, responseBody);
							}
						}
						@Override
						public void onFailure(int statusCode, Header[] headers,
								byte[] responseBody, Throwable error) {
							handler.sendMessage(handler.obtainMessage(Setting.HANDLER_LOGOUT_FAILED,error));
							super.onFailure(statusCode, headers, responseBody, error);
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// 返回码不需要特殊处理的提醒
	private static  void elseResultCodeAction(final int resultCode, int statusCode,Header[] headers, final byte[] responseBody){
		App.getTopActivity().runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				String information = null;
				try {
					JSONObject object = new JSONObject(new String(responseBody));
					information = object.optString("posVersion");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				App.getTopActivity().dismissLoadingDialog();
				UIHelp.showToast(App.getTopActivity(), ResultCode.getErrorResultStrByCode(App.instance, resultCode, information));
			}
		});
	}
}
