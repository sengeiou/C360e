package com.alfredposclient.push;
import android.content.Context;
import android.text.TextUtils;

import com.alfredbase.ParamConst;
import com.alfredbase.javabean.model.AlipayPushMsgDto;
import com.alfredbase.javabean.model.PushMessage;
import com.alfredbase.javabean.model.ThirdpartyPayPushMsgDto;
import com.alfredbase.javabean.temporaryforapp.AppOrder;
import com.alfredbase.javabean.temporaryforapp.TempOrder;
import com.alfredbase.store.Store;
import com.alfredbase.store.sql.temporaryforapp.AppOrderSQL;
import com.alfredbase.store.sql.temporaryforapp.TempOrderSQL;
import com.alfredbase.utils.LogUtil;
import com.alfredposclient.activity.NetWorkOrderActivity;
import com.alfredposclient.global.App;
import com.alfredposclient.global.SyncCentre;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PushListenerClient implements PushServer.PushListener {
	private String TAG = PushListenerClient.class.getSimpleName();
	Context context;
	
	public PushListenerClient(Context context) {
		super();
		this.context = context;
	}
	
	@Override
	public void onPushMessageReceived(PushMessage msg, boolean canCheck) {
		LogUtil.d(TAG, msg.toString());
		if (msg == null)
			return;
		if(msg.getRevenueId() == 0 || msg.getRevenueId() == App.instance.getRevenueCenter().getId().intValue()) {
			if (msg != null && PushMessage.PUSH_ORDER.equals(msg.getMsg())
					&& !TextUtils.isEmpty(msg.getContent())) {
				try {
					JSONObject jsonObject = new JSONObject(msg.getContent());
					int type = 0;
					if(jsonObject.has("refundType")){
						type = jsonObject.getInt("refundType");
					}
					int appOrderId = jsonObject.getInt("appOrderId");

					if(type == -2016){
						AppOrderSQL.updateAppOrderStatusById(appOrderId, ParamConst.APP_ORDER_STATUS_REFUND);
						App.instance.setAppOrderNum(AppOrderSQL.getNewAppOrderCountByTime(App.instance.getBusinessDate()));
						if(App.getTopActivity() instanceof NetWorkOrderActivity){
							App.getTopActivity().httpRequestAction(type, null);
						}
					}else {
						AppOrder appOrder = AppOrderSQL.getAppOrderById(appOrderId);
						if (appOrder == null) {
							Map<String, Object> map = new HashMap<String, Object>();
							map.put("appOrderId", appOrderId);
							SyncCentre.getInstance().getAppOrderById(context, map, null, canCheck);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

//				String [] contentArray = msg.getContent().split(PushMessage.CONTENT_DELIMITER);
//				String restaurantKeyMd5 = contentArray[0];
////				String QRCodeStr = contentArray[1];
//				String localRestaurantKeyMd5 = MD5Util.md5(CoreData.getInstance().getLoginResult().getRestaurantKey());
//				if(localRestaurantKeyMd5.equals(restaurantKeyMd5)){
//					Map<String, Object> parameters = new HashMap<String, Object>();
//					parameters.put("QRCodeStr",msg.getContent());
//					SyncCentre.getInstance().getOrderFromApp(App.instance.getTopActivity(), parameters);
//				}
			} else if (msg != null && PushMessage.ALIPAY_RESULT.equals(msg.getMsg())) {
				if (!TextUtils.isEmpty(msg.getContent())) {
					Gson gson = new Gson();
					AlipayPushMsgDto alipayPushMsgDto = gson.fromJson(msg.getContent(), AlipayPushMsgDto.class);
					App.instance.addAlipayPushMessage(alipayPushMsgDto);
					LogUtil.d(TAG, alipayPushMsgDto.toString());
				}
			} else if (msg != null && PushMessage.THIRDPARTYPAY_RESULT.equals(msg.getMsg())) {
				if (!TextUtils.isEmpty(msg.getContent())) {
					Gson gson = new Gson();
					ThirdpartyPayPushMsgDto thirdpartyPayPushMsgDto = gson.fromJson(msg.getContent(), ThirdpartyPayPushMsgDto.class);
					TempOrder tempOrder = TempOrderSQL.getTempOrderByAppOrderId(Integer.parseInt(thirdpartyPayPushMsgDto.getSysOrderId()));
					if (tempOrder == null)
						return;
					tempOrder.setPaied(ParamConst.TEMPORDER_PAIED);
					TempOrderSQL.updateTempOrder(tempOrder);
					LogUtil.d(TAG, thirdpartyPayPushMsgDto.toString());
				}
			} else {
				Map<String, Integer> pushMsgMap = App.instance.getPushMsgMap();
				if (pushMsgMap.containsKey(msg.getMsg())) {
					if (pushMsgMap.get(pushMsgMap) != null) {
						pushMsgMap.put(msg.getMsg(), pushMsgMap.get(pushMsgMap) + 1);
					} else {
						pushMsgMap.put(msg.getMsg(), 1);
					}

				} else {
					pushMsgMap.put(msg.getMsg(), 1);
				}
				App.instance.setPushMsgMap(pushMsgMap);
				Store.saveObject(context, Store.PUSH_MESSAGE, App.instance.getPushMsgMap());
			}
		}
	}

	@Override
	public void onNetworkError() {
		// TODO Auto-generated method stub
		//Toast.makeText(context, "Push Server ERROR", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onNetworkDisconnected() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNetworkConnected() {
		// TODO Auto-generated method stub
		
	}

}
