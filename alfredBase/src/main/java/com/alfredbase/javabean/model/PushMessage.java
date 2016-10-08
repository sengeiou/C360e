package com.alfredbase.javabean.model;

import com.alfredbase.utils.LogUtil;
import com.google.gson.Gson;

public class PushMessage {
	public final static String HAPPY_HOURS = "happyhour";

	public final static String PRINTER = "printer";

	public final static String ITEM = "item";

	public final static String MODIFIER = "modifier";

	public final static String USER = "user";

	public final static String RESTAURANT = "restaurant";

	public final static String PLACE_TABLE = "place_table";

	public final static String TAX = "tax";
	
	public final static String REST_CONFIG = "rest_config";
	
	public final static String PUSH_ORDER = "push_order";
	
	public final static String ALIPAY_RESULT = "alipay_result";
	
	public final static String THIRDPARTYPAY_RESULT = "thirdpartyPay_result";
	
	public final static int MESSAGE_TYPE_HEART_BEAT = -2;
	public final static int MESSAGE_TYPE_REGISTER = 0;
	public final static int MESSAGE_TYPE_UPDATE=1;
	
	public final static String CONTENT_DELIMITER = ",";
	
	private String msg; // 事件类型
	private int type;//  -2心跳, 0:register, 1: Data Update, 
	private Integer restId;
	private Integer revenueId;
	private String content; // json 数据
	private Integer appOrderId;
	
	private String deviceId;
	
	public PushMessage() {
	}

	public PushMessage(int type, String msg) {
		this.type = type;
		this.msg = msg;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	
	public Integer getRestId() {
		return restId;
	}

	
	public void setRestId(Integer restId) {
		this.restId = restId;
	}

	
	public Integer getRevenueId() {
		return revenueId;
	}

	
	public void setRevenueId(Integer revenueId) {
		this.revenueId = revenueId;
	}

	public Integer getAppOrderId() {
		return appOrderId;
	}

	public void setAppOrderId(Integer appOrderId) {
		this.appOrderId = appOrderId;
	}

	public static String getPingMsg(int restId, int revenueId) {
		PushMessage msg = new PushMessage();
		msg.setType(-2);
		msg.setMsg("PING");
		msg.setRestId(restId);
		msg.setRevenueId(revenueId);
		String msgtxt = new Gson().toJson(msg);
		LogUtil.v("PushMessage","sendPing==>" + msgtxt);
		return msgtxt;
	}
	
	public static String registClient(int restId, int revenueId) {
		PushMessage msg = new PushMessage();
		msg.setType(MESSAGE_TYPE_REGISTER);
		msg.setRestId(restId);
		msg.setRevenueId(revenueId);
		return new Gson().toJson(msg);
	}
	public static PushMessage deserializeList(String json){
			return new Gson().fromJson(json, PushMessage.class);
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "PushMessage{" +
				"msg='" + msg + '\'' +
				", type=" + type +
				", restId=" + restId +
				", revenueId=" + revenueId +
				", content='" + content + '\'' +
				", appOrderId=" + appOrderId +
				", deviceId='" + deviceId + '\'' +
				'}';
	}

}