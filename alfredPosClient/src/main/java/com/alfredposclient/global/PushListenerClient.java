package com.alfredposclient.global;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.text.TextUtils;

import com.alfredbase.ParamConst;
import com.alfredbase.global.CoreData;
import com.alfredbase.javabean.model.AlipayPushMsgDto;
import com.alfredbase.javabean.model.PushMessage;
import com.alfredbase.javabean.model.ThirdpartyPayPushMsgDto;
import com.alfredbase.javabean.temporaryforapp.TempOrder;
import com.alfredbase.store.Store;
import com.alfredbase.store.sql.temporaryforapp.TempOrderSQL;
import com.alfredbase.utils.LogUtil;
import com.alfredbase.utils.MD5Util;
import com.alfredposclient.service.PushService.PushListener;
import com.google.gson.Gson;

public class PushListenerClient implements PushListener {
	private String TAG = PushListenerClient.class.getSimpleName();
	Context context;
	
	public PushListenerClient(Context context) {
		super();
		this.context = context;
	}
	
	@Override
	public void onPushMessageReceived(PushMessage msg) {
		LogUtil.d(TAG, msg.toString());
		switch (msg.getType()){
		case PushMessage.MESSAGE_TYPE_HEART_BEAT:
			break;
		case PushMessage.MESSAGE_TYPE_UPDATE:
			if (msg != null && PushMessage.PUSH_ORDER.equals(msg.getMsg())) {
				String [] contentArray = msg.getContent().split(PushMessage.CONTENT_DELIMITER);
				String restaurantKeyMd5 = contentArray[0];
//				String QRCodeStr = contentArray[1];
				String localRestaurantKeyMd5 = MD5Util.md5(CoreData.getInstance().getLoginResult().getRestaurantKey());
				if(localRestaurantKeyMd5.equals(restaurantKeyMd5)){
					Map<String, Object> parameters = new HashMap<String, Object>();
					parameters.put("QRCodeStr",msg.getContent());
					SyncCentre.getInstance().getOrderFromApp(App.instance.getTopActivity(), parameters);
				}
			} else if (msg != null && PushMessage.ALIPAY_RESULT.equals(msg.getMsg())) {
				if(!TextUtils.isEmpty(msg.getContent())){
					Gson gson = new Gson();
					AlipayPushMsgDto alipayPushMsgDto = gson.fromJson(msg.getContent(), AlipayPushMsgDto.class);
					App.instance.addAlipayPushMessage(alipayPushMsgDto);
					LogUtil.d(TAG, alipayPushMsgDto.toString());
				}
			} else if (msg != null && PushMessage.THIRDPARTYPAY_RESULT.equals(msg.getMsg())) {
				if(!TextUtils.isEmpty(msg.getContent())){
					Gson gson = new Gson();
					ThirdpartyPayPushMsgDto thirdpartyPayPushMsgDto = gson.fromJson(msg.getContent(), ThirdpartyPayPushMsgDto.class);
					TempOrder tempOrder = TempOrderSQL.getTempOrderByAppOrderId(Integer.parseInt(thirdpartyPayPushMsgDto.getSysOrderId()));
					if(tempOrder == null)
						return;
					tempOrder.setPaied(ParamConst.TEMPORDER_PAIED);
					TempOrderSQL.updateTempOrder(tempOrder);
					LogUtil.d(TAG, thirdpartyPayPushMsgDto.toString());
				}
			} else {
				Map<String, Integer> pushMsgMap = App.instance.getPushMsgMap();
				if(pushMsgMap.containsKey(msg.getMsg())){
					if(pushMsgMap.get(pushMsgMap) != null){
						pushMsgMap.put(msg.getMsg(),  pushMsgMap.get(pushMsgMap) + 1);
					}else{
						pushMsgMap.put(msg.getMsg(), 1);
					}
					
				}else{
					pushMsgMap.put(msg.getMsg(), 1);
				}
				App.instance.setPushMsgMap(pushMsgMap);
				Store.saveObject(context, Store.PUSH_MESSAGE, App.instance.getPushMsgMap());
		}
			break;
		
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
