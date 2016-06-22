package com.alfred.remote.printservice;

import java.util.Map;

import android.os.Handler;
import android.os.RemoteException;

import com.alfredbase.utils.LogUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class RemotePrintServiceCallback extends IAlfredRemotePrintServiceCallback.Stub{
    private Handler handler = null;
    final public static int PRINTERS_DISCOVERIED = 0x30;
    @Override
    public void fromService(String type, String data) throws RemoteException {
       Gson gson = new Gson();

       if ("PRINTS_FOUND".equals(type)) {
    	  //list all physical printers
          Map<String, String> printers = null;    	   
    	  LogUtil.d("Prints found", data);
    	  printers =  gson.fromJson(data, new TypeToken< Map<String, String>>(){}.getType());
    	  if (handler != null) 
    		  handler.sendMessage(handler.obtainMessage(
    						  				RemotePrintServiceCallback.PRINTERS_DISCOVERIED,printers)
    				  					);
       }
    }
	public void setHandler(Handler handler) {
		this.handler = handler;
	}
}
