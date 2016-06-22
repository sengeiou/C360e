package com.alfred.print.jobs;

import android.util.Log;

import com.alfred.printer.ESCPrinter;
import com.alfred.remote.printservice.PrintService;
import com.alfred.remote.printservice.WIFIPrinterHandler;
import com.alfredbase.utils.NetUtil;
import com.epson.eposprint.Print;
import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

public class PingJob  extends Job{
    private String printerIp;
    private long localId;
    static final String TAG = "Ping Job";
	
	protected PingJob() {
		super(new Params(Priority.MID).requireNetwork().persist().groupBy("pingjob"));
	}
	public void setPrinterIp(String printerIp) {
		this.printerIp = printerIp;
	}
	
	@Override
	public void onAdded() {
		 Log.d(TAG, "Ping Job onAdded:"+this.printerIp);
	}

	@Override
	public void onRun() throws Throwable {
        boolean result = false;
        
//        if (false) {
        	//EPC/POS commands compatiable printer
        Log.d(TAG, "onRun:"+this.printerIp);
        
        	WIFIPrinterHandler hdl = ((PrintService)PrintService.instance).getPrinterHandler(this.printerIp);
        	ESCPrinter printer = hdl.getPrinter();
        	
        	//ping printer first
        	if (printer != null) {
	            result = printer.ping();
	        	if (result) {
		    		if (!printer.isConnected()) {
		    			Log.d(TAG, "reconnect socket:"+this.printerIp);
		    			printer.reconnect();
		    			result = false;
		    		}
	        	}else{
	        		printer.onSendFailed();
	        		//hdl.sendEmptyMessage(WifiCommunication.SEND_FAILED);
	        	}
        	}else {
        		//printer is null, need add new printer
        		boolean networked = NetUtil.ping(printerIp); 
        		if (networked) {
		    	   ESCPrinter newPrinter = new ESCPrinter(((PrintService)PrintService.instance), Print.DEVTYPE_TCP, this.printerIp, "P80");
		    	   hdl.setPrinter(newPrinter);
        		}
        	}
		if (!result)
			throw new RuntimeException("Ping Print Error");
	}

	@Override
	protected void onCancel() {
		// TODO Auto-generated method stub
		Log.d(TAG, "onRun:"+this.printerIp);
	}

	@Override
	protected boolean shouldReRunOnThrowable(Throwable throwable) {
		// TODO Auto-generated method stub
		return true;
	}
}
