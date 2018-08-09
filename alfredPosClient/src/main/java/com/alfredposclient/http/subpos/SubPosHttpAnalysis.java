package com.alfredposclient.http.subpos;

import android.os.Handler;

import com.alfredbase.global.CoreData;
import com.alfredbase.javabean.LoginResult;
import com.alfredbase.store.Store;
import com.alfredposclient.global.App;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class SubPosHttpAnalysis {
	public static final String TAG = SubPosHttpAnalysis.class.getSimpleName();
	public static LoginResult login(String responseBody, Handler handler) {
		LoginResult result = null;
		try {
			JSONObject object = new JSONObject(responseBody);
			Gson gson = new Gson();
			result = gson.fromJson(object.toString(), LoginResult.class);
			Store.saveObject(App.instance, Store.LOGIN_RESULT, result);
			CoreData.getInstance().setLoginResult(result);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}


}
