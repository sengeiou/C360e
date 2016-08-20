package com.alfredposclient.http;

import android.content.Context;
import android.os.Handler;

import com.alfredbase.ParamConst;
import com.alfredbase.global.CoreData;
import com.alfredbase.http.AsyncHttpResponseHandlerEx;
import com.alfredbase.http.ResultCode;
import com.alfredbase.javabean.MonthlyPLUReport;
import com.alfredbase.javabean.MonthlySalesReport;
import com.alfredbase.javabean.RevenueCenter;
import com.alfredbase.javabean.SyncMsg;
import com.alfredbase.javabean.User;
import com.alfredbase.javabean.model.PushMessage;
import com.alfredbase.store.Store;
import com.alfredbase.store.sql.SyncMsgSQL;
import com.alfredbase.store.sql.UserSQL;
import com.alfredposclient.R;
import com.alfredposclient.activity.BOHSettlementHtml;
import com.alfredposclient.activity.MainPage;
import com.alfredposclient.activity.MonthlyPLUReportHtml;
import com.alfredposclient.activity.MonthlySalesReportHtml;
import com.alfredposclient.activity.NetWorkOrderActivity;
import com.alfredposclient.activity.SyncData;
import com.alfredposclient.activity.SystemSetting;
import com.alfredposclient.activity.XZReportHtml;
import com.alfredposclient.global.App;
import com.alfredposclient.global.SyncCentre;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpAPI {

	// for KDS HTTP Server
	public static final String EOF = "\r\nEOF\r\n";

	/**
	 * 订单数据
	 */
	public static final int ORDER_DATA = 10;
	/**
	 * 支付数据
	 */
	public static final int REPORT_DATA = 20;

	/**
	 * 同步订单记录数据
	 */
	public static final int LOG_DATA = 11;

	/**
	 * 同步开关餐厅开关session的记录数据
	 */
	public static final int OPEN_CLOSE_SESSION_RESTAURANT = 90;


	/**
	 * 用于更新网络订单状态
	 */
	public static final int NETWORK_ORDER_STATUS_UPDATE = 1001;

	public static void login(Context context, Map<String, Object> parameters,
			String url, AsyncHttpClient httpClient, final Handler handler) {
		try {
			StringEntity entity = HttpAssembling.getLoginParam(
					(Integer) parameters.get("userID"),
					(String) parameters.get("password"),
					(String) parameters.get("bizID"));
			httpClient.post(context, url, entity, HttpAssembling.CONTENT_TYPE,
					new AsyncHttpResponseHandlerEx() {
						@Override
						public void onSuccess(int statusCode, Header[] headers,
								byte[] responseBody) {
							super.onSuccess(statusCode, headers, responseBody);
							if (resultCode == ResultCode.SUCCESS) {
								HttpAnalysis.login(statusCode, headers,
										responseBody);
								handler.sendMessage(handler
										.obtainMessage(SyncData.HANDLER_LOGIN));
							} else {
								handler.sendMessage(handler
										.obtainMessage(
												SyncData.HANDLER_ERROR_INFO,
												resultCode));
							}
						}

						@Override
						public void onFailure(int statusCode, Header[] headers,
								byte[] responseBody, Throwable error) {
							handler.sendMessage(handler.obtainMessage(
									ResultCode.CONNECTION_FAILED, error));
							super.onFailure(statusCode, headers, responseBody,
									error);
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void getRestaurantInfo(Context context,
			Map<String, Object> parameters, String url,
			AsyncHttpClient httpClient, final Handler mHandler, int mode) {
		try {
			httpClient.post(context, url, HttpAssembling.getRestaurantParam(),
					HttpAssembling.CONTENT_TYPE,
					new AsyncHttpResponseHandlerEx() {
						@Override
						public void onSuccess(final int statusCode,
								final Header[] headers,
								final byte[] responseBody) {
							super.onSuccess(statusCode, headers, responseBody);
							if (resultCode == ResultCode.SUCCESS) {
								new Thread(new Runnable() {

									@Override
									public void run() {
										HttpAnalysis.getRestaurantInfo(
												statusCode, headers,
												responseBody);
										Map<String, Integer> map = App.instance
												.getPushMsgMap();
										if (!map.isEmpty()) {
											map.remove(map
													.get(PushMessage.PRINTER));
											Store.saveObject(App.instance,
													Store.PUSH_MESSAGE, map);
										}
										if (mHandler != null) {
											mHandler.sendMessage(mHandler
													.obtainMessage(SyncData.HANDLER_GET_RESTAURANT_INFO));
											mHandler.sendMessage(mHandler
													.obtainMessage(ResultCode.SUCCESS));
										}

									}
								}).start();

							} else {
								if (mHandler != null)
									mHandler.sendMessage(mHandler
											.obtainMessage(
													SyncData.HANDLER_ERROR_INFO,
													resultCode));
							}
						}

						@Override
						public void onFailure(int statusCode, Header[] headers,
								byte[] responseBody, Throwable error) {
							if (mHandler != null) {
								mHandler.sendMessage(mHandler.obtainMessage(
										ResultCode.CONNECTION_FAILED, error));
							}
							super.onFailure(statusCode, headers, responseBody,
									error);
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void getBindDeviceIdInfo(final Context context,
			final Map<String, Object> parameters, String url,
			AsyncHttpClient httpClient, final Handler handler) {
		try {
			RevenueCenter revenueCenter = (RevenueCenter) parameters
					.get("revenueCenter");
			StringEntity entity = HttpAssembling
					.getBindDeviceIdInfo(revenueCenter);
			httpClient.post(context, url, entity, HttpAssembling.CONTENT_TYPE,
					new AsyncHttpResponseHandlerEx() {
						@Override
						public void onSuccess(int statusCode, Header[] headers,
								byte[] responseBody) {
							super.onSuccess(statusCode, headers, responseBody);
							if (resultCode == ResultCode.SUCCESS) {
								handler.sendMessage(handler.obtainMessage(
										SyncData.HANDLER_GET_BINDDEVICEID_INFO,
										parameters));
							} else if (resultCode == ResultCode.BINDING_TO_ALREADY) {
								handler.sendMessage(handler.obtainMessage(
										SyncData.HANDLER_BIND_ALREADY,
										context.getResources().getString(
												R.string.binding_to_already)));
							} else if (resultCode == ResultCode.BE_BOUND_ALREADY) {
								handler.sendMessage(handler.obtainMessage(
										SyncData.HANDLER_BIND_ALREADY,
										context.getResources().getString(
												R.string.be_bound_already)));
							} else {
								handler.sendMessage(handler
										.obtainMessage(
												SyncData.HANDLER_ERROR_INFO,
												resultCode));
							}
						}

						@Override
						public void onFailure(int statusCode, Header[] headers,
								byte[] responseBody, Throwable error) {
							handler.sendMessage(handler.obtainMessage(
									ResultCode.CONNECTION_FAILED, error));
							super.onFailure(statusCode, headers, responseBody,
									error);
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void getUser(Context context, String url,
			AsyncHttpClient httpClient, final Handler handler, final int mode) {
		try {
			httpClient.post(context, url, HttpAssembling.getTokenParam(),
					HttpAssembling.CONTENT_TYPE,
					new AsyncHttpResponseHandlerEx() {
						@Override
						public void onSuccess(final int statusCode,
								final Header[] headers,
								final byte[] responseBody) {
							super.onSuccess(statusCode, headers, responseBody);
							if (resultCode == ResultCode.SUCCESS) {
								new Thread(new Runnable() {
									public void run() {
										Map<String, Integer> map = App.instance
												.getPushMsgMap();
										if (!map.isEmpty()) {
											map.remove(map
													.get(PushMessage.USER));
											Store.saveObject(App.instance,
													Store.PUSH_MESSAGE, map);
										}
										HttpAnalysis.getUsers(statusCode,
												headers, responseBody);
										if (mode == SyncCentre.MODE_FIRST_SYNC) {
											handler.sendMessage(handler
													.obtainMessage(
															SyncData.SYNC_DATA_TAG,
															SyncData.SYNC_SUCCEED));
										} else {
											handler.sendEmptyMessage(ResultCode.SUCCESS);
										}

									}
								}).start();
							} else {
								if (mode == SyncCentre.MODE_FIRST_SYNC) {
									handler.sendMessage(handler.obtainMessage(
											SyncData.SYNC_DATA_TAG,
											SyncData.SYNC_FAILURE));
								}
							}
						}

						@Override
						public void onFailure(int statusCode, Header[] headers,
								byte[] responseBody, Throwable error) {
							if (mode == SyncCentre.MODE_PUSH_SYNC) {
								handler.sendMessage(handler.obtainMessage(
										ResultCode.CONNECTION_FAILED, error));
							} else if (mode == SyncCentre.MODE_FIRST_SYNC) {
								handler.sendMessage(handler.obtainMessage(
										SyncData.SYNC_DATA_TAG,
										SyncData.SYNC_FAILURE));
							}
							super.onFailure(statusCode, headers, responseBody,
									error);
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void getUserRestaurant(Context context, String url,
			AsyncHttpClient httpClient) {
		try {
			httpClient.post(context, url, HttpAssembling.getTokenParam(),
					HttpAssembling.CONTENT_TYPE,
					new AsyncHttpResponseHandlerEx() {
						@Override
						public void onSuccess(final int statusCode,
								final Header[] headers,
								final byte[] responseBody) {
							super.onSuccess(statusCode, headers, responseBody);
							if (resultCode == ResultCode.SUCCESS) {
								new Thread(new Runnable() {
									public void run() {
										HttpAnalysis.getUsers(statusCode,
												headers, responseBody);
									}
								}).start();
							}
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void getItemCategory(Context context, String url,
			AsyncHttpClient httpClient, final Handler handler, final int mode) {
		try {
			httpClient.post(context, url, HttpAssembling.getTokenParam(),
					HttpAssembling.CONTENT_TYPE,
					new AsyncHttpResponseHandlerEx() {
						@Override
						public void onSuccess(final int statusCode,
								final Header[] headers,
								final byte[] responseBody) {
							super.onSuccess(statusCode, headers, responseBody);
							if (resultCode == ResultCode.SUCCESS) {
								new Thread(new Runnable() {
									public void run() {
										Map<String, Integer> map = App.instance
												.getPushMsgMap();
										if (!map.isEmpty()) {
											map.remove(map
													.get(PushMessage.ITEM));
											Store.saveObject(App.instance,
													Store.PUSH_MESSAGE, map);
										}
										HttpAnalysis.getItemCategory(
												statusCode, headers,
												responseBody);
										if (mode == SyncCentre.MODE_FIRST_SYNC) {
											handler.sendMessage(handler
													.obtainMessage(
															SyncData.SYNC_DATA_TAG,
															SyncData.SYNC_SUCCEED));
										}
									}
								}).start();

							} else {
								if (mode == SyncCentre.MODE_FIRST_SYNC) {
									handler.sendMessage(handler.obtainMessage(
											SyncData.SYNC_DATA_TAG,
											SyncData.SYNC_FAILURE));
								}
							}
						}

						@Override
						public void onFailure(int statusCode, Header[] headers,
								byte[] responseBody, Throwable error) {
							if (mode == SyncCentre.MODE_FIRST_SYNC) {
								handler.sendMessage(handler.obtainMessage(
										SyncData.SYNC_DATA_TAG,
										SyncData.SYNC_FAILURE));
							}
							super.onFailure(statusCode, headers, responseBody,
									error);
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void getItem(Context context, String url,
			AsyncHttpClient httpClient, final Handler handler, final int mode) {
		try {
			RevenueCenter revenueCenter = (RevenueCenter) App.instance
					.getRevenueCenter();
			StringEntity entity = HttpAssembling.getItemParam(revenueCenter);
			httpClient.post(context, url, entity, HttpAssembling.CONTENT_TYPE,
					new AsyncHttpResponseHandlerEx() {
						@Override
						public void onSuccess(final int statusCode,
								final Header[] headers,
								final byte[] responseBody) {
							super.onSuccess(statusCode, headers, responseBody);
							if (resultCode == ResultCode.SUCCESS) {
								new Thread(new Runnable() {
									public void run() {
										HttpAnalysis.getItemDetail(statusCode,
												headers, responseBody);
										if (mode == SyncCentre.MODE_FIRST_SYNC) {
											handler.sendMessage(handler
													.obtainMessage(
															SyncData.SYNC_DATA_TAG,
															SyncData.SYNC_SUCCEED));
										} else {
											handler.sendEmptyMessage(ResultCode.SUCCESS);
										}
									}
								}).start();
							} else {
								if (mode == SyncCentre.MODE_FIRST_SYNC) {
									handler.sendMessage(handler.obtainMessage(
											SyncData.SYNC_DATA_TAG,
											SyncData.SYNC_FAILURE));
								}
							}
						}

						@Override
						public void onFailure(int statusCode, Header[] headers,
								byte[] responseBody, Throwable error) {
							if (mode == SyncCentre.MODE_FIRST_SYNC) {
								handler.sendMessage(handler.obtainMessage(
										SyncData.SYNC_DATA_TAG,
										SyncData.SYNC_FAILURE));
							} else {
								handler.sendMessage(handler.obtainMessage(
										ResultCode.CONNECTION_FAILED, error));
							}
							super.onFailure(statusCode, headers, responseBody,
									error);
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void getModifier(Context context, String url,
			AsyncHttpClient httpClient, final Handler handler, final int mode) {
		try {
			httpClient.post(context, url, HttpAssembling.getTokenParam(),
					HttpAssembling.CONTENT_TYPE,
					new AsyncHttpResponseHandlerEx() {
						@Override
						public void onSuccess(final int statusCode,
								final Header[] headers,
								final byte[] responseBody) {
							super.onSuccess(statusCode, headers, responseBody);
							if (resultCode == ResultCode.SUCCESS) {
								new Thread(new Runnable() {
									public void run() {
										Map<String, Integer> map = App.instance
												.getPushMsgMap();
										if (!map.isEmpty()) {
											map.remove(map
													.get(PushMessage.MODIFIER));
											Store.saveObject(App.instance,
													Store.PUSH_MESSAGE, map);
										}
										HttpAnalysis.getAllModifier(statusCode,
												headers, responseBody);
										if (mode == SyncCentre.MODE_FIRST_SYNC) {
											handler.sendMessage(handler
													.obtainMessage(
															SyncData.SYNC_DATA_TAG,
															SyncData.SYNC_SUCCEED));
										} else {
											handler.sendEmptyMessage(ResultCode.SUCCESS);
										}
									}
								}).start();
							} else {
								if (mode == SyncCentre.MODE_FIRST_SYNC) {
									handler.sendMessage(handler.obtainMessage(
											SyncData.SYNC_DATA_TAG,
											SyncData.SYNC_FAILURE));
								}
							}
						}

						@Override
						public void onFailure(int statusCode, Header[] headers,
								byte[] responseBody, Throwable error) {
							if (mode == SyncCentre.MODE_FIRST_SYNC) {
								handler.sendMessage(handler.obtainMessage(
										SyncData.SYNC_DATA_TAG,
										SyncData.SYNC_FAILURE));
							} else {
								handler.sendMessage(handler.obtainMessage(
										ResultCode.CONNECTION_FAILED, error));
							}
							super.onFailure(statusCode, headers, responseBody,
									error);
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void getTax(Context context, String url,
			AsyncHttpClient httpClient, final Handler handler, final int mode) {
		try {
			httpClient.post(context, url, HttpAssembling.getTokenParam(),
					HttpAssembling.CONTENT_TYPE,
					new AsyncHttpResponseHandlerEx() {
						@Override
						public void onSuccess(final int statusCode,
								final Header[] headers,
								final byte[] responseBody) {
							super.onSuccess(statusCode, headers, responseBody);
							if (resultCode == ResultCode.SUCCESS) {
								new Thread(new Runnable() {
									public void run() {
										Map<String, Integer> map = App.instance
												.getPushMsgMap();
										if (!map.isEmpty()) {
											map.remove(map.get(PushMessage.TAX));
											Store.saveObject(App.instance,
													Store.PUSH_MESSAGE, map);
										}
										HttpAnalysis.getTax(statusCode,
												headers, responseBody);
										if (mode == SyncCentre.MODE_FIRST_SYNC) {
											handler.sendMessage(handler
													.obtainMessage(
															SyncData.SYNC_DATA_TAG,
															SyncData.SYNC_SUCCEED));
										} else {
											handler.sendEmptyMessage(ResultCode.SUCCESS);
										}
									}
								}).start();
							} else {
								if (mode == SyncCentre.MODE_FIRST_SYNC) {
									handler.sendMessage(handler.obtainMessage(
											SyncData.SYNC_DATA_TAG,
											SyncData.SYNC_FAILURE));
								}
							}
						}

						@Override
						public void onFailure(int statusCode, Header[] headers,
								byte[] responseBody, Throwable error) {
							if (mode == SyncCentre.MODE_FIRST_SYNC) {
								handler.sendMessage(handler.obtainMessage(
										SyncData.SYNC_DATA_TAG,
										SyncData.SYNC_FAILURE));
							} else {
								handler.sendMessage(handler.obtainMessage(
										ResultCode.CONNECTION_FAILED, error));
							}
							super.onFailure(statusCode, headers, responseBody,
									error);
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void getHappyHour(Context context, String url,
			AsyncHttpClient httpClient, final Handler handler, final int mode) {
		try {
			httpClient.post(context, url, HttpAssembling.getTokenParam(),
					HttpAssembling.CONTENT_TYPE,
					new AsyncHttpResponseHandlerEx() {
						@Override
						public void onSuccess(final int statusCode,
								final Header[] headers,
								final byte[] responseBody) {
							super.onSuccess(statusCode, headers, responseBody);
							if (resultCode == ResultCode.SUCCESS) {
								new Thread(new Runnable() {

									@Override
									public void run() {
										HttpAnalysis.getHappyHour(statusCode,
												headers, responseBody);
										Map<String, Integer> map = App.instance
												.getPushMsgMap();
										if (!map.isEmpty()) {
											map.remove(map
													.get(PushMessage.HAPPY_HOURS));
											Store.saveObject(App.instance,
													Store.PUSH_MESSAGE, map);
										}
										if (mode == SyncCentre.MODE_FIRST_SYNC) {
											handler.sendMessage(handler
													.obtainMessage(
															SyncData.SYNC_DATA_TAG,
															SyncData.SYNC_SUCCEED));
										} else {
											handler.sendEmptyMessage(ResultCode.SUCCESS);
										}
									}
								}).start();
							} else {
								if (mode == SyncCentre.MODE_FIRST_SYNC) {
									handler.sendMessage(handler.obtainMessage(
											SyncData.SYNC_DATA_TAG,
											SyncData.SYNC_FAILURE));
								}
							}
						}

						@Override
						public void onFailure(int statusCode, Header[] headers,
								byte[] responseBody, Throwable error) {
							if (mode == SyncCentre.MODE_FIRST_SYNC) {
								handler.sendMessage(handler.obtainMessage(
										SyncData.SYNC_DATA_TAG,
										SyncData.SYNC_FAILURE));
							} else {
								handler.sendMessage(handler.obtainMessage(
										ResultCode.CONNECTION_FAILED, error));
							}
							super.onFailure(statusCode, headers, responseBody,
									error);
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void getPlaceInfo(Context context,
			Map<String, Object> parameters, String url,
			AsyncHttpClient httpClient, final Handler handler, final int mode) {
		try {
			RevenueCenter revenueCenter = (RevenueCenter) parameters
					.get("revenueCenter");
			StringEntity entity = HttpAssembling.getPlaceParam(revenueCenter);
			httpClient.post(context, url, entity, HttpAssembling.CONTENT_TYPE,
					new AsyncHttpResponseHandlerEx() {
						@Override
						public void onSuccess(final int statusCode,
								final Header[] headers,
								final byte[] responseBody) {
							super.onSuccess(statusCode, headers, responseBody);
							if (resultCode == ResultCode.SUCCESS) {
								new Thread(new Runnable() {

									@Override
									public void run() {
										Map<String, Integer> map = App.instance
												.getPushMsgMap();
										if (!map.isEmpty()) {
											map.remove(map
													.get(PushMessage.PLACE_TABLE));
											Store.saveObject(App.instance,
													Store.PUSH_MESSAGE, map);
										}
										HttpAnalysis.getPlaceInfo(statusCode,
												headers, responseBody);
										if (mode == SyncCentre.MODE_FIRST_SYNC) {
											handler.sendMessage(handler
													.obtainMessage(
															SyncData.SYNC_DATA_TAG,
															SyncData.SYNC_SUCCEED));
										} else {
											handler.sendMessage(handler
													.obtainMessage(SyncData.HANDLER_GET_PLACE_INFO));
											handler.sendEmptyMessage(ResultCode.SUCCESS);
										}
									}
								}).start();
							} else {
								if (mode == SyncCentre.MODE_FIRST_SYNC) {
									handler.sendMessage(handler.obtainMessage(
											SyncData.SYNC_DATA_TAG,
											SyncData.SYNC_FAILURE));
								} else {
									handler.sendMessage(handler.obtainMessage(
											SyncData.HANDLER_ERROR_INFO,
											resultCode));
								}
							}
						}

						@Override
						public void onFailure(int statusCode, Header[] headers,
								byte[] responseBody, Throwable error) {
							if (mode == SyncCentre.MODE_FIRST_SYNC) {
								handler.sendMessage(handler.obtainMessage(
										SyncData.SYNC_DATA_TAG,
										SyncData.SYNC_FAILURE));
							} else
								handler.sendMessage(handler.obtainMessage(
										ResultCode.CONNECTION_FAILED, error));
							super.onFailure(statusCode, headers, responseBody,
									error);
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void logout(Context context, String url,
			AsyncHttpClient httpClient, final Handler handler) {
		try {
			httpClient.post(context, url, HttpAssembling.getTokenParam(),
					HttpAssembling.CONTENT_TYPE,
					new AsyncHttpResponseHandlerEx() {
						@Override
						public void onSuccess(final int statusCode,
								final Header[] headers,
								final byte[] responseBody) {
							super.onSuccess(statusCode, headers, responseBody);
							// if (resultCode == ResultCode.SUCCESS) {
							// handler.sendMessage(handler.obtainMessage(MainPage.ACTION_SWITCH_USER));
							// }
							handler.sendMessage(handler
									.obtainMessage(MainPage.ACTION_SWITCH_USER));
						}

						@Override
						public void onFailure(int statusCode, Header[] headers,
								byte[] responseBody, Throwable error) {
							handler.sendMessage(handler.obtainMessage(
									ResultCode.CONNECTION_FAILED, error));
							super.onFailure(statusCode, headers, responseBody,
									error);
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void uploadOrderInfo(Context context, final SyncMsg syncMsg,
			String url, SyncHttpClient httpClient) {
		try {
			StringEntity entity = HttpAssembling
					.getUploadOrderInfoParam(syncMsg);
			httpClient.post(context, url, entity, HttpAssembling.CONTENT_TYPE,
					new AsyncHttpResponseHandlerEx() {
						@Override
						public void onSuccess(int statusCode, Header[] headers,
								byte[] responseBody) {
							super.onSuccess(statusCode, headers, responseBody);
							if (resultCode == ResultCode.SUCCESS) {
								syncMsg.setStatus(ParamConst.SYNC_MSG_SUCCESS);
								SyncMsgSQL.add(syncMsg);
							} else {
								syncMsg.setStatus(ParamConst.SYNC_MSG_MALDATA);
								SyncMsgSQL.add(syncMsg);
								if (resultCode == ResultCode.DEVICE_NO_PERMIT) {
									App.instance
											.getTopActivity()
											.httpRequestAction(
													ResultCode.DEVICE_NO_PERMIT,
													null);
								}
							}
						}

						@Override
						public void onFailure(int statusCode, Header[] headers,
								byte[] responseBody, Throwable error) {
							syncMsg.setStatus(ParamConst.SYNC_MSG_UN_SEND);
							SyncMsgSQL.add(syncMsg);
							super.onFailure(statusCode, headers, responseBody,
									error);
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * Sync order and XZReport data from POS to Cloud
	 */
	public static void cloudSync(Context context, final SyncMsg syncMsg,
			String url, SyncHttpClient httpClient) {
		// try {
		StringEntity entity = null;
		try {
			entity = HttpAssembling.getUploadOrderInfoParam(syncMsg);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (entity != null) {
			httpClient.post(context, url, entity, HttpAssembling.CONTENT_TYPE,
					new AsyncHttpResponseHandlerEx() {
						@Override
						public void onSuccess(int statusCode, Header[] headers,
								byte[] responseBody) {
							super.onSuccess(statusCode, headers, responseBody);
							if (resultCode == ResultCode.SUCCESS) {
								syncMsg.setStatus(ParamConst.SYNC_MSG_SUCCESS);
								SyncMsgSQL.add(syncMsg);
							} else {
								syncMsg.setStatus(ParamConst.SYNC_MSG_MALDATA);
								SyncMsgSQL.add(syncMsg);
								if (resultCode == ResultCode.DEVICE_NO_PERMIT) {
									App.instance
											.getTopActivity()
											.httpRequestAction(
													ResultCode.DEVICE_NO_PERMIT,
													null);
								}
							}
						}

						@Override
						public void onFailure(int statusCode, Header[] headers,
								byte[] responseBody, Throwable error) {
							// no need change status here. JOB will get the
							// exception to rerun job
							// syncMsg.setStatus(ParamConst.SYNC_MSG_UN_SEND);
							// SyncMsgSQL.add(syncMsg);
							super.onFailure(statusCode, headers, responseBody,
									error);
							throw new RuntimeException(error);
						}
					});
		}

		// } catch (Exception e) {
		// e.printStackTrace();
		// }
	}

	public static void getBOHSettlement(Context context, String url,
			AsyncHttpClient httpClient, final Handler handler) {
		try {
			httpClient.post(context, url, HttpAssembling.getTokenParam(),
					HttpAssembling.CONTENT_TYPE,
					new AsyncHttpResponseHandlerEx() {
						@Override
						public void onSuccess(final int statusCode,
								final Header[] headers,
								final byte[] responseBody) {
							super.onSuccess(statusCode, headers, responseBody);
							if (resultCode == ResultCode.SUCCESS) {
								new Thread(new Runnable() {
									public void run() {
										handler.sendMessage(handler
												.obtainMessage(
														BOHSettlementHtml.BOH_GETBOHHOLDUNPAID_INFO,
														HttpAnalysis
																.getBOHSettlement(
																		statusCode,
																		headers,
																		responseBody)));
									}
								}).start();
							} else if (resultCode == ResultCode.DEVICE_NO_PERMIT) {
								App.getTopActivity().httpRequestAction(
										ResultCode.DEVICE_NO_PERMIT, null);
							}
						}

						@Override
						public void onFailure(int statusCode, Header[] headers,
								byte[] responseBody, Throwable error) {
							handler.sendMessage(handler.obtainMessage(
									ResultCode.CONNECTION_FAILED, error));
							super.onFailure(statusCode, headers, responseBody,
									error);
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void uploadBOHPaidInfo(Context context, String url,
			AsyncHttpClient httpClient, Map<String, Object> parameters,
			final Handler handler) {
		try {
			httpClient.post(context, url,
					HttpAssembling.getuploadBOHPaidInfo(parameters),
					HttpAssembling.CONTENT_TYPE,
					new AsyncHttpResponseHandlerEx() {
						@Override
						public void onSuccess(final int statusCode,
								final Header[] headers,
								final byte[] responseBody) {
							super.onSuccess(statusCode, headers, responseBody);
							if (resultCode == ResultCode.SUCCESS) {
								new Thread(new Runnable() {
									public void run() {
										handler.sendEmptyMessage(BOHSettlementHtml.UPLOAD_BOH_PAID);
									}
								}).start();
							} else if (resultCode == ResultCode.DEVICE_NO_PERMIT) {
								App.getTopActivity().httpRequestAction(
										resultCode, null);

							}
						}

						@Override
						public void onFailure(int statusCode, Header[] headers,
								byte[] responseBody, Throwable error) {
							handler.sendMessage(handler.obtainMessage(
									ResultCode.CONNECTION_FAILED, error));
							super.onFailure(statusCode, headers, responseBody,
									error);
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void loadCloudXZReport(Context context, String url,
			AsyncHttpClient httpClient, Map<String, Object> parameters,
			final Handler handler) {
		final Map<String, Object> param = parameters;
		try {
			httpClient.post(context, url,
					HttpAssembling.getloadReportInfo(parameters),
					HttpAssembling.CONTENT_TYPE,
					new AsyncHttpResponseHandlerEx() {
						@Override
						public void onSuccess(final int statusCode,
								final Header[] headers,
								final byte[] responseBody) {
							super.onSuccess(statusCode, headers, responseBody);
							Map<String, Object> zObj = new HashMap<String, Object>();
							if (resultCode == ResultCode.SUCCESS) {
								// write Sales data in DB
								String nettsale = HttpAnalysis
										.getReportDayFromCloud(statusCode,
												headers, responseBody);
								zObj.put("sales", nettsale);
								zObj.put("bizDate", param.get("bizDate"));
								// Notifity UI to show data
								handler.sendMessage(handler
										.obtainMessage(
												XZReportHtml.LOAD_CLOUD_REPORT_COMPLETE,
												zObj));
							}
						}

						@Override
						public void onFailure(int statusCode, Header[] headers,
								byte[] responseBody, Throwable error) {
							handler.sendMessage(handler.obtainMessage(
									ResultCode.CONNECTION_FAILED, error));
							super.onFailure(statusCode, headers, responseBody,
									error);
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//load and print monthly sales report from cloud
	public static void loadCloudMonthlySalesReport(Context context, String url,
			AsyncHttpClient httpClient, Map<String, Object> parameters,
			final Handler handler) {
		final Map<String, Object> param = parameters;
		try {
			httpClient.post(context, url,
					HttpAssembling.getloadMonthlySalesReportInfo(parameters),
					HttpAssembling.CONTENT_TYPE,
					new AsyncHttpResponseHandlerEx() {
						@Override
						public void onSuccess(final int statusCode,
								final Header[] headers,
								final byte[] responseBody) {
							super.onSuccess(statusCode, headers, responseBody);
							Map<String, Object> zObj = new HashMap<String, Object>();
							if (resultCode == ResultCode.SUCCESS) {
								// write Sales data in DB
								List<MonthlySalesReport> sales = HttpAnalysis.getReportMonthlySaleFromCloud(statusCode,
												headers, responseBody);
								zObj.put("date", param.get("month"));
								zObj.put("salesData", sales);
								// Notifity UI to show data
								handler.sendMessage(handler
										.obtainMessage(
												MonthlySalesReportHtml.LOAD_MONTYLY_SALES_REPORT_COMPLETE,
												zObj));
							}
						}

						@Override
						public void onFailure(int statusCode, Header[] headers,
								byte[] responseBody, Throwable error) {
							handler.sendMessage(handler.obtainMessage(
									ResultCode.CONNECTION_FAILED, error));
							super.onFailure(statusCode, headers, responseBody,
									error);
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//load and print monthly PLU report from cloud
	public static void loadCloudMonthlyPLUReport(Context context, String url,
			AsyncHttpClient httpClient, Map<String, Object> parameters,
			final Handler handler) {
		final Map<String, Object> param = parameters;
		try {
			httpClient.post(context, url,
					HttpAssembling.getloadMonthlyPLUReportInfo(parameters),
					HttpAssembling.CONTENT_TYPE,
					new AsyncHttpResponseHandlerEx() {
						@Override
						public void onSuccess(final int statusCode,
								final Header[] headers,
								final byte[] responseBody) {
							super.onSuccess(statusCode, headers, responseBody);
							Map<String, Object> zObj = new HashMap<String, Object>();
							if (resultCode == ResultCode.SUCCESS) {
								// write Sales data in DB
								List<MonthlyPLUReport> pluData = HttpAnalysis.getReportMonthlyPLUFromCloud(statusCode,
												headers, responseBody);
								zObj.put("date", param.get("month"));
								zObj.put("plu", pluData);
								// Notifity UI to show data
								handler.sendMessage(handler
										.obtainMessage(
												MonthlyPLUReportHtml.LOAD_MONTYLY_PLU_REPORT_COMPLETE,
												zObj));
							}
						}

						@Override
						public void onFailure(int statusCode, Header[] headers,
								byte[] responseBody, Throwable error) {
							handler.sendMessage(handler.obtainMessage(
									ResultCode.CONNECTION_FAILED, error));
							super.onFailure(statusCode, headers, responseBody,
									error);
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void getOrderFromApp(Context context, String url,
			AsyncHttpClient httpClient, Map<String, Object> parameters) {
		try {
			httpClient.post(context, url,
					HttpAssembling.encapsulateBaseInfo(parameters),
					HttpAssembling.CONTENT_TYPE,
					new AsyncHttpResponseHandlerEx() {
						@Override
						public void onSuccess(final int statusCode,
								final Header[] headers,
								final byte[] responseBody) {
							super.onSuccess(statusCode, headers, responseBody);
							if (resultCode == ResultCode.SUCCESS) {
								HttpAnalysis.getOrderFromApp(statusCode, headers, responseBody);
							}
						}

						@Override
						public void onFailure(int statusCode, Header[] headers,
								byte[] responseBody, Throwable error) {
							super.onFailure(statusCode, headers, responseBody,
									error);
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void updatePassword(Context context, String url,
			AsyncHttpClient httpClient, final Map<String, Object> parameters, final Handler handler, final User user){
		try {
			httpClient.post(context, url,
					HttpAssembling.encapsulateBaseInfo(parameters),
					HttpAssembling.CONTENT_TYPE,
					new AsyncHttpResponseHandlerEx() {
						@Override
						public void onSuccess(final int statusCode,
								final Header[] headers,
								final byte[] responseBody) {
							super.onSuccess(statusCode, headers, responseBody);
							if(resultCode == ResultCode.SUCCESS){
								UserSQL.updateUserPassword(String.valueOf(parameters.get("newPassword")), user);
								CoreData.getInstance().setUsers(UserSQL.getAllUser());
							}
							handler.sendMessage(handler.obtainMessage(SystemSetting.UPDATE_PASSWORD_TAG, resultCode));
						}

						@Override
						public void onFailure(int statusCode, Header[] headers,
								byte[] responseBody, Throwable error) {
							super.onFailure(statusCode, headers, responseBody,
									error);
							handler.sendMessage(handler.obtainMessage(
									ResultCode.CONNECTION_FAILED, error));
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void getAppOrderById(Context context, String url,
									  AsyncHttpClient httpClient, final Map<String, Object> parameters, final Handler handler){
		try {
			httpClient.post(context, url,
					HttpAssembling.encapsulateBaseInfo(parameters),
					HttpAssembling.CONTENT_TYPE,
					new AsyncHttpResponseHandlerEx() {
						@Override
						public void onSuccess(final int statusCode,
											  final Header[] headers,
											  final byte[] responseBody) {
							super.onSuccess(statusCode, headers, responseBody);
							if(resultCode == ResultCode.SUCCESS){
								HttpAnalysis.getAppOrderById(statusCode, headers, responseBody);
							}
							if(handler != null)
							handler.sendMessage(handler.obtainMessage(SystemSetting.UPDATE_PASSWORD_TAG, resultCode));
						}

						@Override
						public void onFailure(int statusCode, Header[] headers,
											  byte[] responseBody, Throwable error) {
							super.onFailure(statusCode, headers, responseBody,
									error);
							if(handler != null)
							handler.sendMessage(handler.obtainMessage(
									ResultCode.CONNECTION_FAILED, error));
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void getAllAppOrder(Context context, String url,
									   AsyncHttpClient httpClient, final Map<String, Object> parameters, final Handler handler){
		try {
			httpClient.post(context, url,
					HttpAssembling.encapsulateBaseInfo(parameters),
					HttpAssembling.CONTENT_TYPE,
					new AsyncHttpResponseHandlerEx() {
						@Override
						public void onSuccess(final int statusCode,
											  final Header[] headers,
											  final byte[] responseBody) {
							super.onSuccess(statusCode, headers, responseBody);
							if(resultCode == ResultCode.SUCCESS){
								HttpAnalysis.getAllAppOrder(statusCode, headers, responseBody);
							}
							if(handler != null)
								handler.sendMessage(handler.obtainMessage(NetWorkOrderActivity.REFRESH_APPORDER_SUCCESS, resultCode));
						}

						@Override
						public void onFailure(int statusCode, Header[] headers,
											  byte[] responseBody, Throwable error) {
							if(handler != null)
								handler.sendMessage(handler.obtainMessage(
										ResultCode.CONNECTION_FAILED, error));
							super.onFailure(statusCode, headers, responseBody,
									error);

						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void updateAppOrderStatus(Context context, String url,
									  AsyncHttpClient httpClient,  final SyncMsg syncMsg){
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("appOrderId", syncMsg.getAppOrderId());
			parameters.put("orderStatus", syncMsg.getOrderStatus());
			parameters.put("orderNum", syncMsg.getOrderNum());
			httpClient.post(context, url,
					HttpAssembling.encapsulateBaseInfo(parameters),
					HttpAssembling.CONTENT_TYPE,
					new AsyncHttpResponseHandlerEx() {
						@Override
						public void onSuccess(final int statusCode,
											  final Header[] headers,
											  final byte[] responseBody) {
							super.onSuccess(statusCode, headers, responseBody);
							if(resultCode == ResultCode.SUCCESS){
								syncMsg.setStatus(ParamConst.SYNC_MSG_SUCCESS);
								SyncMsgSQL.add(syncMsg);
							}else{
								syncMsg.setStatus(ParamConst.SYNC_MSG_MALDATA);
								SyncMsgSQL.add(syncMsg);
							}
						}

						@Override
						public void onFailure(int statusCode, Header[] headers,
											  byte[] responseBody, Throwable error) {
							super.onFailure(statusCode, headers, responseBody,
									error);
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void callAppNo(final Context context, String url,
								 AsyncHttpClient httpClient, String num){
		try {
			RequestParams requestParams = new RequestParams();
			requestParams.put("callnumber", num);
			httpClient.get(context,url, requestParams,new AsyncHttpResponseHandlerEx(){
				@Override
				public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
					super.onSuccess(statusCode, headers, responseBody);
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
					super.onFailure(statusCode, headers, responseBody, error);
//					UIHelp.showShortToast();
				}
			});
//			httpClient.get(context, url,
//					entity,
//					HttpAssembling.CONTENT_TYPE,
//					new AsyncHttpResponseHandlerEx() {
//						@Override
//						public void onSuccess(final int statusCode,
//											  final Header[] headers,
//											  final byte[] responseBody) {
//							super.onSuccess(statusCode, headers, responseBody);
//							if(resultCode == ResultCode.SUCCESS){
//							}else{
//							}
//						}
//
//						@Override
//						public void onFailure(int statusCode, Header[] headers,
//											  byte[] responseBody, Throwable error) {
//							super.onFailure(statusCode, headers, responseBody,
//									error);
//						}
//					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
