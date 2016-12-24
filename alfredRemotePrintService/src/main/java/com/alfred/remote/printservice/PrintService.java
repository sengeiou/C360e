package com.alfred.remote.printservice;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.alfred.print.jobs.PrintManager;
import com.alfred.printer.ESCPrinter;
import com.alfredbase.PrinterDeviceConfig;
import com.alfredbase.store.Store;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PrintService extends Service {

	public static final String ACTION_START = "com.alfred.print.ACTION_START";
	public static final String ACTION_SHUT_DOWN = "com.alfred.print.ACTION_SHUT_DOWN";
	public static final String ACTION_DISCOVER_PRINTER = "com.alfred.print.ACTION_DISCOVER_PRINTER";

	static final String TAG = "Alfred Print Service";
	
	private boolean mShutDown = false;

    public static PrintService instance;
    private PrintManager printJobMgr;  
    private PrinterQueueManager pqMgr;
    
    public static String PRINT_DOLLAR_SIGN_SETTING = "DOLLAR_SIGN_SETTING";
    public static String PRINT_LANG_SETTING = "PRINT_LANG_SETTING";
    public static String PRINT_COUNTRY_SETTING = "PRINT_COUNTRY_SETTING";

    final Object lock = new Object();
    
    final List<IAlfredRemotePrintServiceCallback> mCallbacks = new ArrayList<IAlfredRemotePrintServiceCallback>();
    
    //IP Printer Handler
//    static Map<String, WIFIPrinterHandler> printerHandlers = new ConcurrentHashMap<String, WIFIPrinterHandler>();
    private Map<String, ESCPrinter> escPrinterMap = new HashMap<String, ESCPrinter>();
	@Override
	public void onCreate() {
		super.onCreate();
		this.printJobMgr = new PrintManager(this);
		this.pqMgr = new PrinterQueueManager(this);
		instance = this;		

		this.printJobMgr.start();
        this.pqMgr.start();

		Log.d(TAG, "Creating Service");
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, "onBind ");
		Bundle extras = intent.getExtras();
		if (extras != null) {
			if ("alfred.intent.action.bindPrintService".equals(intent.getAction()) &&
					("fxxxkprinting").equals((String) extras.get("PRINTERKEY"))) {
//				if (this.printJobMgr!=null)
//					this.printJobMgr.addPingJob();
				
				return new PrintServiceBinder(this);
			}
		}
		return null;
	}

	@Override
	public void onDestroy() {

		Log.d(TAG, "Destroying Service");
		//this.printJobMgr.clear();
		this.printJobMgr.stop();
        this.mCallbacks.clear();
        //close all sockets
		this.pqMgr.stop();
        closeAllSockets();
		super.onDestroy();
		//this.printJobMgr = null;

		
	}

	public void closeAllSockets() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				Set<String> key = escPrinterMap.keySet();
				for (Iterator it = key.iterator(); it.hasNext();) {
					String ip = (String) it.next();
					ESCPrinter escp = escPrinterMap.get(ip);
					if (escp != null) {
						escp.setConnected(false);
						escp.close();
					}
				}
			}
		}).start();

	}
	
	public PrintManager getPrintMgr() {
		return this.printJobMgr;
	}
	
	public PrinterQueueManager getPqMgr() {
		return pqMgr;
	}
	
	public boolean isTMU220(String model) {
		if (model!=null && model.length()>0) {
			if (model.toLowerCase().contains("u220"))
				return true;
		}
		return false;
	}
	
	public String getDollarSignStr() {		
		String sign = "$";
		Integer dollarSign = Store.getInt(this, PrintService.PRINT_DOLLAR_SIGN_SETTING);
		if (dollarSign != null) {
			if (dollarSign.intValue() == PrinterDeviceConfig.PRINT_DOLLAR_CNY)
				sign = "￥";
			if (dollarSign.intValue() == PrinterDeviceConfig.PRINT_DOLLAR_MYM)
				sign = "RM";
		}
		return sign;
	}
    
	public void configure(int country,int lang, int dollarsign) {
		Store.putInt(this, PrintService.PRINT_COUNTRY_SETTING, country);
		Store.putInt(this, PrintService.PRINT_LANG_SETTING, lang);
		Store.putInt(this, PrintService.PRINT_DOLLAR_SIGN_SETTING, dollarsign);
	}
	
	public void removePrinterByIP(String ip) {
		this.escPrinterMap.remove(ip);
	}

	
//	public WIFIPrinterHandlerFQ getPrinterHandler(String ip) {
//		WIFIPrinterHandlerFQ wfpHandler = null;
//		synchronized(lock) {
//		  wfpHandler = PrintService.printerHandlers.get(ip);
//		  if (wfpHandler == null) {
////			  Looper.prepare();
//			  wfpHandler = new WIFIPrinterHandlerFQ(ip);
////			  Looper.loop();
//			  PrintService.printerHandlers.put(ip, wfpHandler);
//		  }
//		}
//		return wfpHandler;
//	}


	public Map<String, ESCPrinter> getEscPrinterMap() {
		return escPrinterMap;
	}

	public void putEscPrinterMap(String id, ESCPrinter eSCPrinter) {
		this.escPrinterMap.put(id, eSCPrinter);
	}

}
