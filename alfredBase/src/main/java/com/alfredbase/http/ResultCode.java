package com.alfredbase.http;

import android.content.Context;
import android.text.TextUtils;

import com.alfredbase.R;

import java.net.SocketTimeoutException;

/**
 * 
 * @author 冯小卫 2014-5-15
 * 
 */
public class ResultCode {

	public static final String resultKey = "resultCode";
	
	public static final int CONNECTION_FAILED = -1;
	
	/**
	 * 成功
	 */
	public static final int SUCCESS = 1;
	

	/**
	 * 未知错误
	 */
	public static final int UNKNOW_ERROR = -9999;
	
	public static final int INVALID_DEVICE = -9000;
	/**
	 * 参数为空
	 */
	public static final int CLIENT_PARAM_EMPTY = -1001;
	/**
	 * 参数解析异常
	 */
	public static final int PARSE_JSON_ERROR = -1002;
	/**
	 * 参数数据有误
	 */
	public static final int JSON_DATA_ERROR = -1003;
	
	
	/**
	 * 插入出错
	 */
	public static final int INSERT_ERROR = -1004;
	/**
	 * 查询为空
	 */
	public static final int QUERY_EMPTY = -1005;
	/**
	 * 更新失败
	 */
	public static final int UPDATE_FAILED = -1006;
	
	
	/**
	 * 餐厅key有误
	 */
	public static final int RESTAURANT_EMPTY = -2001;
	/**
	 * 员工不存在
	 */
	public static final int USER_EMPTY = -2002;
	/**
	 * 密码不正确
	 */
	public static final int USER_PASSWORD_ERROR = -2003;
	/**
	 * userKey不对或过期
	 */
	public static final int USER_KEY_ERROR = -2004;
	/**
	 * 已登录状态已存在
	 */
	public static final int USER_LOGIN_EXIST = -2005;
	
	/**
	 * 已绑定(revenueCeter 绑定 pos)
	 */
	public static final int BINDING_TO_ALREADY = -2006;
	public static final int BE_BOUND_ALREADY = -2007;
	/**
	 * 已绑定，运行过程中被解绑
	 */
	public static final int DEVICE_NO_PERMIT = -2008;

	/**
	 * 同步的数据已经存在
	 */
	public static final int RECEIVE_MSG_EXIST = -2009;
	/**
	 *	no  permission	  用户权限不足
	 */
	public static final int USER_NO_PERMIT = -3001;


	/**
	 * 会员卡二维码为空
	 */
	public static final int QRCODE_EMPTY = -4001;

	/**
	 * 会员卡二维码信息有误
	 */
	public static final int QRCODE_ERROR = -4002;

	/**
	 * 会员卡不存在
	 */
	public static final int QRCODE_NOT_EXISTS = -4003;

	/**
	 * 会员卡状态异常
	 */
	public static final int CARD_STATUS_ABNORMAL = -4004;

	/**
	 * 用户暂无绑定会员卡信息
	 */
	public static final int CUSTOMER_NO_CARD = -4005;

	/**
	 * 用户二维码失效
	 */
	public static final int CUSTOMER_QRCODE_INVALIDATE = -4006;

	/**
	 * 该会员卡绑定了手机.请使用手机支付
	 */
	public static final int QRCODE_TOBIND_PHONE_USE_PHONE_PAY = -2011;


	/**
	 * 余额不足
	 */
	public static final int CARD_BALANCE_NOT_ENOUGH = -2012;
	
	/**
	 * 用于POS作为服务器的时候，session还没有打开的错误提醒
	 */
	public static final int SESSION_IS_CLOSED = -1000;
	
	public static final int NONEXISTENT_ORDER = -999;
	
	public static final int KOTSUMMARY_IS_UNREAL = -998;
	
	public static final int APP_VERSION_UNREAL = -997;
	
	public static final int ORDER_FINISHED = -996;
	
	/**
	 * waiter提交的拆单在POS机上已经结账的返回
	 */
	public static final int ORDER_SPLIT_IS_SETTLED = -1998;

	/*
	 * POS to KDS response Error
	 * */
	public static final int KOT_COMPLETE_FAILED = -2;
	public static final int KOT_COMPLETE_USER_FAILED = -3;
	
	public static final String getErrorResultStrByCode(Context context, int resultCode, String information){
		switch (resultCode) {
		case UNKNOW_ERROR:
			return context.getResources().getString(R.string.unknown_error);
		case CLIENT_PARAM_EMPTY:
			return context.getResources().getString(R.string.param_empty);
		case PARSE_JSON_ERROR:
			return context.getResources().getString(R.string.param_error);
		case RESTAURANT_EMPTY:
			return context.getResources().getString(R.string.invalid_rest_key);
		case USER_EMPTY:
			return context.getResources().getString(R.string.no_user);
		case USER_PASSWORD_ERROR:
			return context.getResources().getString(R.string.incorrect_pwd);
		case USER_KEY_ERROR:
			return context.getResources().getString(R.string.invalid_user_key);
		case USER_LOGIN_EXIST:
			return context.getResources().getString(R.string.login_other_devices);
		case USER_NO_PERMIT:
			return context.getResources().getString(R.string.user_no_permission);
		case SESSION_IS_CLOSED:
			return context.getResources().getString(R.string.not_start_session);
		case DEVICE_NO_PERMIT:
			return context.getResources().getString(R.string.no_permissions);
		case QRCODE_EMPTY:
			return context.getResources().getString(R.string.stored_card_null);
		case QRCODE_NOT_EXISTS:
		case QRCODE_ERROR:
			return context.getResources().getString(R.string.qrcode_error);
		case CUSTOMER_NO_CARD:
			return context.getResources().getString(R.string.customer_no_card);
		case CARD_STATUS_ABNORMAL:
			return context.getResources().getString(R.string.card_status_abnormal);
		case QRCODE_TOBIND_PHONE_USE_PHONE_PAY:
			return context.getResources().getString(R.string.card_binding_mobile);
		case CUSTOMER_QRCODE_INVALIDATE:
			return context.getResources().getString(R.string.qrcode_expire);
		case CARD_BALANCE_NOT_ENOUGH:
			return "The stored card balance is not enough";
		case APP_VERSION_UNREAL:
			if(TextUtils.isEmpty(information)){
				return context.getResources().getString(R.string.upgrade_new_version);
			} else {
				return context.getResources().getString(R.string.upgrade_app_version) + information + 
						context.getResources().getString(R.string.comatible_pos);
			}
			
		}
		return "";
	}
	
	public static final String getErrorResultStr(Context context, Throwable error, String server){
		if(error == null){
			return context.getResources().getString(R.string.failed_connect_server) + server + 
					context.getResources().getString(R.string.try_later);
		}
		if (error.getClass().equals(
				SocketTimeoutException.class)) {
			return context.getResources().getString(R.string.conn_time_out);
//		} else if (error.getClass().equals(
//				HttpHostConnectException.class)) {
//			return "Failed to connect to main POS, Please try later";
//		} else if (error.getClass().equals(
//				NoHttpResponseException.class)) {
//			return "Cannot connect to main POS now";
		} else {
			return context.getResources().getString(R.string.failed_connect_server) + server +
					context.getResources().getString(R.string.try_later);
		}
	}
	
}
