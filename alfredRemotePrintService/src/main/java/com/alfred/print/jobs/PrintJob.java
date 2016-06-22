package com.alfred.print.jobs;

import java.util.ArrayList;

import android.util.Log;

import com.alfred.printer.ESCPrinter;
import com.alfred.printer.PrintData;
import com.alfred.printer.StringUtil;
import com.alfred.remote.printservice.PrintService;
import com.alfred.remote.printservice.WIFIPrinterHandler;
import com.alfredbase.ParamConst;
import com.alfredbase.javabean.PrintQueueMsg;
import com.alfredbase.store.sql.PrintQueueMsgSQL;
import com.alfredbase.utils.LogUtil;
import com.alfredbase.utils.NetUtil;
import com.epson.eposprint.Print;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

public class PrintJob  extends Job{
    private String printerIp;
    private long localId;
    static final String TAG = "PrintJob";
    
	protected int charSize = 33;  //tm81: 48 tm-u220:33  
    //Print Data
	protected ArrayList<PrintData> data;    
    
	//Print QUEUE
	private String msgUUID; 
    private String msgType;
    private int msgId; //all 0
    private Long created;
    private Long bizDate;
    
	public PrintJob(Params params,String msgType, String uuid, Long bizDate) {
		
		super(params);
		this.data = new ArrayList<PrintData>();
		
		this.msgType = msgType;
		this.msgUUID = uuid;
		this.msgId = msgId;
		this.bizDate = bizDate;
		this.created = System.currentTimeMillis();
	}
	public PrintJob(Params params,String msgType, String uuid, Long bizDate, Long created) {
		
		super(params);
		this.data = new ArrayList<PrintData>();
		
		this.msgType = msgType;
		this.msgUUID = uuid;
		this.msgId = msgId;
		this.bizDate = bizDate;
		this.created = created;
	}
	
	public String getPrinterIp() {
		return printerIp;
	}

	public void setPrinterIp(String printerIp) {
		this.printerIp = printerIp;
	}
	
	public void setCharSize(int size) {
		this.charSize = size;
	}
	
	
	//Bob: for Print Queue
	public PrintQueueMsg getJobForQueue() {
		PrintQueueMsg msg = new PrintQueueMsg();
		Gson gson = new Gson();
		
		msg.setPrinterIp(this.printerIp);
		msg.setCharSize(this.charSize);
		msg.setMsgUUID(this.msgUUID);
		msg.setMsgType(this.msgType);
		msg.setCreated(created);
		msg.setBizDate(bizDate);
		msg.setData(gson.toJson(data));
		
		return msg;
	}
	
	@Override
	public void onAdded() {
		 Log.d(TAG, "onAdded:"+this.printerIp);
		 PrintQueueMsg content = PrintQueueMsgSQL.getUnsentMsgById(this.msgUUID, this.created);
	     if (content != null) {
	    	    this.printerIp = content.getPrinterIp().trim();
	    		content.setStatus(ParamConst.PRINTQUEUE_MSG_QUEUED);
	    		PrintQueueMsgSQL.add(content);
	     }		 
	}

	@Override
	public void onRun() throws Throwable {
        boolean result = false;
        
//        if (false) {
        	//EPC/POS commands compatiable printer
        boolean printed = false;
        
        Log.d(TAG, "onRun:"+this.printerIp);
        //
        Gson gson = new Gson();
        PrintQueueMsg content = PrintQueueMsgSQL.getQueuedMsgById(this.msgUUID, this.created);
        if (content == null)
        	return ;
        
        this.data = gson.fromJson(content.getData(), new TypeToken<ArrayList<PrintData>>(){}.getType());
        
    	WIFIPrinterHandler hdl = ((PrintService)PrintService.instance).getPrinterHandler(this.printerIp);
    	ESCPrinter printer = hdl.getPrinter();
    	
    	//ping printer first
    	if (printer != null) {
            result = printer.ping();
        	if (result) {
	    		if (!printer.isConnected()) {
	    			//need reconnect
	    			printer.reconnect();
	    			result = false;
	    		} else {
	    			printer.setData(this.data);
	    			result = printer.open();
	    			if (result)
	    			   printed = true;
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

		if (printed && result) {
			content.setStatus(ParamConst.PRINTQUEUE_MSG_SUCCESS);
			PrintQueueMsgSQL.add(content);		
		}
		if (!result) {
			throw new RuntimeException("Print Error");
		}
	}

    @Override
    protected void onCancel() {
    	LogUtil.d(TAG, "onCancel:"+this.printerIp);
    	PrintQueueMsg content = PrintQueueMsgSQL.getQueuedMsgById(this.msgUUID, this.created);
    	if (content != null) {
    		content.setStatus(ParamConst.SYNC_MSG_UN_SEND);
    		PrintQueueMsgSQL.add(content);
    	}    	
    }
    
	@Override
	protected boolean shouldReRunOnThrowable(Throwable throwable) {
		// TODO Auto-generated method stub
		return true;
	}
	
	protected void AddCut() {
		PrintData cut = new PrintData();
		cut.setDataFormat(PrintData.FORMAT_CUT);
		this.data.add(cut);		
	}
	
	protected void AddKickDrawer() {
		PrintData kick = new PrintData();
		kick.setDataFormat(PrintData.FORMAT_DRAWER);
		this.data.add(kick);			
	}
	
	protected void addHortionalLine(int charSize) {
		String lstr = new String(new char[charSize]).replace('\0', '-').concat("\r\n");
		PrintData line = new PrintData();
		line.setDataFormat(PrintData.FORMAT_TXT);
		line.setMarginTop(10);
		line.setText(lstr);
		this.data.add(line);		
	}
	protected void addHortionaDoublelLine(int charSize) {
		String lstr = new String(new char[charSize]).replace('\0', '=').concat("\r\n");
		PrintData line = new PrintData();
		line.setDataFormat(PrintData.FORMAT_TXT);
		line.setMarginTop(10);
		line.setTextBold(-1);
		line.setText(lstr);
		this.data.add(line);		
	}

	protected void addBlankLine(){
		StringBuilder sbr = new StringBuilder();
		sbr.append("\r\n");
		PrintData line = new PrintData();
		line.setDataFormat(PrintData.FORMAT_TXT);
		line.setMarginTop(20);
		line.setText(sbr.toString());
		this.data.add(line);		
	}

	protected void addSingleLineCenteredText(int charSize, String txt,int height) {
		StringBuilder sbr = new StringBuilder();
		sbr.append(txt).append("\r\n");
		PrintData line = new PrintData();
		line.setDataFormat(PrintData.FORMAT_TXT);
		line.setTextAlign(PrintData.ALIGN_CENTRE);
		line.setText(sbr.toString());
		line.setMarginTop(height);
		this.data.add(line);		
	}
	
	protected void addSingleLineCenteredTextPaddingWithDash(int charSize, String txt,int height) {
		StringBuilder sbr = new StringBuilder();
		sbr.append(StringUtil.padCenterWithDash(txt, charSize)).append("\r\n");
		PrintData line = new PrintData();
		line.setDataFormat(PrintData.FORMAT_TXT);
		line.setTextAlign(PrintData.ALIGN_CENTRE);
		line.setText(sbr.toString());
		line.setMarginTop(height);
		this.data.add(line);		
	}
	
	protected void addSingleLineText(int charSize, String txt,int height) {
		StringBuilder sbr = new StringBuilder();
		sbr.append(StringUtil.padLeft(txt, charSize)).append("\r\n");
		PrintData line = new PrintData();
		line.setDataFormat(PrintData.FORMAT_TXT);
		line.setText(sbr.toString());
		line.setMarginTop(height);
		this.data.add(line);		
	}

	public ArrayList<PrintData> getData() {
		return data;
	}

	public void setData(ArrayList<PrintData> data) {
		this.data = data;
	}
}
