package com.alfredposclient.http;

import android.content.Context;
import android.os.Handler;

import com.alfredbase.http.AsyncHttpResponseHandlerEx;
import com.alfredbase.http.ResultCode;
import com.alfredbase.javabean.model.KDSDevice;
import com.alfredbase.utils.LogUtil;
import com.alfredposclient.global.App;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.SyncHttpClient;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;

import java.net.ConnectException;
import java.util.Map;

public class HTTPKDSRequest {

	public static void syncSubmitTmpKot(Context context, Map<String, Object> parameters, String url, final KDSDevice kds,
									 SyncHttpClient syncHttpClient, final Handler handler) throws Exception {

		parameters.put("mainpos", App.instance.getMainPosInfo());
		syncHttpClient.post(context,url,
				new StringEntity(new Gson().toJson(parameters) + HttpAPI.EOF,
						"UTF-8"),HttpAssembling.CONTENT_TYPE,
				new AsyncHttpResponseHandlerEx() {
					@Override
					public void onSuccess(final int statusCode, final Header[] headers,
										  final byte[] responseBody) {
						super.onSuccess(statusCode, headers, responseBody);
						if(resultCode==ResultCode.INVALID_DEVICE) {
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

	public static void syncSubmitKot(Context context, Map<String, Object> parameters, String url, final KDSDevice kds,
			SyncHttpClient syncHttpClient, final Handler handler) throws Exception {
			
			parameters.put("mainpos", App.instance.getMainPosInfo());						
			syncHttpClient.post(context,url, 
					 new StringEntity(new Gson().toJson(parameters) + HttpAPI.EOF, 
							 "UTF-8"),HttpAssembling.CONTENT_TYPE,
						new AsyncHttpResponseHandlerEx() {
							@Override
							public void onSuccess(final int statusCode, final Header[] headers,
									final byte[] responseBody) {
								super.onSuccess(statusCode, headers, responseBody);
								if(resultCode==ResultCode.INVALID_DEVICE) {
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
	
	public static void sendSessionClose(Context context, Map<String, Object> parameters, String url,final KDSDevice kds,
			AsyncHttpClient httpClient){
    		parameters.put("mainpos", App.instance.getMainPosInfo());
		    try {
				httpClient.post(context,url, 
						 new StringEntity(new Gson().toJson(parameters), "UTF-8"),HttpAssembling.CONTENT_TYPE,
							new AsyncHttpResponseHandlerEx() {
								@Override
								public void onSuccess(int statusCode, Header[] headers,
										byte[] responseBody) {
									super.onSuccess(statusCode, headers, responseBody);
									if (resultCode == ResultCode.SUCCESS) {
										LogUtil.i("sendSessionClose", "SUCCESS");
									}else if(resultCode==ResultCode.INVALID_DEVICE) {
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
			AsyncHttpClient httpClient){
    	parameters.put("mainpos", App.instance.getMainPosInfo());
	    try {
			httpClient.post(context,url, 
					 new StringEntity(new Gson().toJson(parameters), "UTF-8"),HttpAssembling.CONTENT_TYPE,
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
