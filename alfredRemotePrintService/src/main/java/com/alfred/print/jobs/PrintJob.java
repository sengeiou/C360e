package com.alfred.print.jobs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.alfred.printer.ESCPrinter;
import com.alfred.printer.PrintData;
import com.alfred.printer.StringUtil;
import com.alfred.remote.printservice.PrintService;
import com.alfredbase.ParamConst;
import com.alfredbase.javabean.PrintQueueMsg;
import com.alfredbase.store.sql.PrintQueueMsgSQL;
import com.alfredbase.utils.LogUtil;
import com.alfredbase.utils.NetUtil;
import com.birbit.android.jobqueue.CancelReason;
import com.birbit.android.jobqueue.RetryConstraint;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;

import java.util.ArrayList;

public class PrintJob  extends Job{
    private String printerIp;
    private long localId;
    static final String TAG = "PrintJob";
	public static String reNext = "\n";
    
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
			 PrintQueueMsgSQL.updatePrintQueueMsgStatus(ParamConst.PRINTQUEUE_MSG_QUEUED, this.msgUUID, this.created);
	     }
	}

	@Override
	public void onRun() throws Throwable {
        boolean isPrintLink;
        boolean printed = false;
		boolean pingSuccess;
        Log.d(TAG, "onRun:"+this.printerIp);
		Log.i(TAG, "onRun:this uuid is " + this.msgUUID + ", then this object is" + this);
        Gson gson = new Gson();
        PrintQueueMsg content = PrintQueueMsgSQL.getQueuedMsgById(this.msgUUID, this.created);
        if (content == null){
			return ;
		}
//		Subscriber<ESCPrinter> subscriber = new Subscriber<ESCPrinter>() {
//			@Override
//			public void onCompleted() {
//
//			}
//
//			@Override
//			public void onError(Throwable e) {
//
//			}
//
//			@Override
//			public void onNext(ESCPrinter escPrinter) {
//				if(escPrinter != null){
//
//				}
//			}
//
//		};
//
//		Observable.just(printerIp) // 输入类型 String
//				.map(new Func1<String, ESCPrinter>() {
//					@Override
//					public ESCPrinter call(String ip) { // 参数类型 String
//						return getESCPrinter(ip); // 返回类型 ESCPrinter
//					}
//				})
//				.subscribe(subscriber);

        this.data = gson.fromJson(content.getData(), new TypeToken<ArrayList<PrintData>>(){}.getType());
    	ESCPrinter printer = PrintService.instance.getEscPrinterMap().get(this.printerIp);
		//ping printer first
		pingSuccess = NetUtil.ping(printerIp);
    	if (pingSuccess) {
			if(printer == null){
				printer= new ESCPrinter(this.printerIp);
				isPrintLink = printer.open();
				PrintService.instance.putEscPrinterMap(this.printerIp, printer);
			}else {
				if (!printer.isConnected()) {
					isPrintLink = printer.reconnect();
				}else{
					isPrintLink = true;
				}
			}
			if(isPrintLink)
				printed = printer.setData(this.data);
		}else{
			onCancel(CancelReason.CANCELLED_WHILE_RUNNING, null);
//			PrintQueueMsgSQL.updatePrintQueueMsgStatus(ParamConst.PRINTQUEUE_MSG_UN_SEND, this.msgUUID, this.created);
			if(printer != null)
				printer.onSendFailed();

			Log.e(TAG, "onRun: this ip is failing ping, waiting next check PrintQueueMsg");
			return;
		}
		if (printed && isPrintLink && pingSuccess) {
			PrintQueueMsgSQL.updatePrintQueueMsgStatus(ParamConst.PRINTQUEUE_MSG_SUCCESS, this.msgUUID, this.created);
		}
		if(!isPrintLink) {
			throw new RuntimeException("Printer unLink run next time");
		}
		if (!printed) {
			throw new RuntimeException("Print Error");
		}
	}

	@Override
	protected void onCancel(int cancelReason, @Nullable Throwable throwable) {
		LogUtil.d(TAG, "onCancel:"+this.printerIp);
		PrintQueueMsgSQL.updatePrintQueueMsgStatus(ParamConst.SYNC_MSG_UN_SEND, this.msgUUID, this.created);
	}

	@Override
	protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
		return RetryConstraint.RETRY;
	}

//	private ESCPrinter getESCPrinter(String ip){
//		boolean isPrintLink = NetUtil.ping(printerIp);
//		ESCPrinter printer = null;
//		if (isPrintLink) {
//			printer = PrintService.instance.getEscPrinterMap().get(ip);
//			if(printer == null){
//				printer = new ESCPrinter(this.printerIp);
//				PrintService.instance.putEscPrinterMap(this.printerIp, printer);
//			}else if (printer.isConnected()) {
//				//need reconnect
//				printer.reconnect();
//			}
//		}
//		return printer;
//	}


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
		String lstr = new String(new char[charSize]).replace('\0', '-').concat(reNext);
		PrintData line = new PrintData();
		line.setDataFormat(PrintData.FORMAT_TXT);
		line.setMarginTop(10);
		line.setText(lstr);
		this.data.add(line);		
	}

	protected void addHortionaDoublelLine(int charSize) {
		String lstr = new String(new char[charSize]).replace('\0', '=').concat(reNext);
		PrintData line = new PrintData();
		line.setDataFormat(PrintData.FORMAT_TXT);
		line.setMarginTop(10);
		line.setTextBold(-1);
		line.setText(lstr);
		this.data.add(line);		
	}

	protected void addBlankLine(){
		StringBuilder sbr = new StringBuilder();
		sbr.append(reNext);
		PrintData line = new PrintData();
		line.setDataFormat(PrintData.FORMAT_TXT);
		line.setMarginTop(20);
		line.setText(sbr.toString());
		this.data.add(line);		
	}
	protected void AddSing() {
		PrintData sing = new PrintData();
		sing.setDataFormat(PrintData.FORMAT_SING);
		this.data.add(sing);
	}

	protected void addSingleLineCenteredText(int charSize, String txt,int height) {
		StringBuilder sbr = new StringBuilder();
		sbr.append(txt).append(reNext);
		PrintData line = new PrintData();
		line.setDataFormat(PrintData.FORMAT_TXT);
		line.setTextAlign(PrintData.ALIGN_CENTRE);
		line.setText(sbr.toString());
		line.setMarginTop(height);
		this.data.add(line);		
	}
	
	protected void addSingleLineCenteredTextPaddingWithDash(int charSize, String txt,int height) {
		StringBuilder sbr = new StringBuilder();
		sbr.append(StringUtil.padCenterWithDash(txt, charSize)).append(reNext);
		PrintData line = new PrintData();
		line.setDataFormat(PrintData.FORMAT_TXT);
		line.setTextAlign(PrintData.ALIGN_CENTRE);
		line.setText(sbr.toString());
		line.setMarginTop(height);
		this.data.add(line);		
	}
	
	protected void addSingleLineText(int charSize, String txt,int height) {
		StringBuilder sbr = new StringBuilder();
		sbr.append(StringUtil.padLeft(txt, charSize)).append(reNext);
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
