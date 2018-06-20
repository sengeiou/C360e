package com.alfred.remote.printservice;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.os.Handler;
import android.os.RemoteException;

import com.alfredbase.javabean.model.PrinterDevice;
import com.alfredbase.utils.LogUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class RemotePrintServiceCallback extends IAlfredRemotePrintServiceCallback.Stub{
    private Handler handler = null;
    final public static int PRINTERS_DISCOVERIED = 0x30;

    final public static int B_RINTERS_DISCOVERIED = 0x31;
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

       else if("B_PRINTS_FOUND".equals(type))
       {
           List<PrinterDevice> pList=null;
           LogUtil.d("Prints found", data);
           pList =  gson.fromJson(data, new TypeToken<List<PrinterDevice>>(){}.getType());
           if (handler != null)
               handler.sendMessage(handler.obtainMessage(
                       RemotePrintServiceCallback.B_RINTERS_DISCOVERIED,pList)
               );
       }
    }
	public void setHandler(Handler handler) {
		this.handler = handler;
	}


}
