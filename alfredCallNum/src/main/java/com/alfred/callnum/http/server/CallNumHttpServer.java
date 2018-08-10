package com.alfred.callnum.http.server;

import com.alfredbase.APPConfig;
import com.alfredbase.BaseActivity;
import com.alfredbase.http.APIName;
import com.alfredbase.http.AlfredHttpServer;
import com.alfredbase.http.ResultCode;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class CallNumHttpServer extends AlfredHttpServer {
	private BaseActivity context;
	public CallNumHttpServer() {
		super(APPConfig.CALLNUM_HTTP_SERVER_PORT);
	}
	
	@Override
	public Response doGet(String uri, Method mothod, Map<String, String> params, String body) {

    	Response resp;
    	
    	if (uri == null) {
    		resp = getNotFoundResponse();
		} else {
			if(uri.equals(APIName.CALL_POS_NUM)) {
				// TODO 在这边解析 pos 放过来的号码，开始叫号。
				/*
					叫号内容{"type" : 1, "callnumber":"A1235"}
				 */

				/**
				 * 返回成功
				 */
				Map<String, Object> map = new HashMap<>();
				map.put("resultCode", ResultCode.SUCCESS);
				return getJsonResponse(new Gson().toJson(map));
			}
			else{
				resp = getNotFoundResponse();
			}
		}
   	
    	return resp;
	}


}
