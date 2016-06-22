package com.alfred.printer;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.alfred.remote.printservice.PrintService;
import com.alfred.remote.printservice.WIFIPrintCallback;
import com.alfred.remote.printservice.WIFIPrinterHandler;
import com.alfredbase.utils.NetUtil;


public class ESCPrinter implements WIFIPrintCallback{

	/**
	 */
	public static final int FONT_A = ESCPOSPrinter.FONT_A;
	public static final int FONT_B = ESCPOSPrinter.FONT_B;
	public static final int FONT_C = ESCPOSPrinter.FONT_C;
	
	
	public final static int MSG_PRINTER_FOUND = 1;
	public final static boolean TXT_UNDERLINE = true;

	Context context;
	
	private int deviceType; //0: Thermal, 1: others
	private String ip;
	private String printerName;
	private int language;
	ESCPOSPrinter printer;

	//data
	private ArrayList<PrintData> data;

	private boolean connected = false;
	
	WIFIPrinterHandler hdl=null;
	
	public ESCPrinter(Context context, int deviceType, String ip, String printerName) {
		
		hdl = ((PrintService) context).getPrinterHandler(ip);
		hdl.setPrinterCbk(this);
		
		this.deviceType = deviceType;
		this.ip = ip;
		this.printerName = printerName;
		
		this.context = context;
		this.data = new ArrayList<PrintData>();
		
		this.printer = new ESCPOSPrinter((PrintService) context, hdl);
        this.printer.open(this.ip, 9100);
	}
	
	public ArrayList<String> discovery() {
		
		return null;
	}
	
	public void reconnect() {
			if (this.printer!=null) {

			  this.printer.open(this.ip, 9100);
			}
	}
	
	public boolean open() {
        boolean result = false;
        if (connected) {
           result = addPrintJob();
        }        
		return result;
	}
	
	public int checkStatus() {
		
		return 0;
	}

	public void close(){
		connected = false;
		printer.close();
	}
	
	public boolean addPrintJob() {
		boolean result = true;
        boolean isKickDrawer = false;
		try {
			this.printer.reset();

			for (int i=0; i<this.data.size(); i++) {
				PrintData toPrint = this.data.get(i);
					byte isUnderline = ESCPOSPrinter.UNDERLINE_NONE;
					if (toPrint.isUnderline())
						isUnderline = ESCPOSPrinter.UNDERLINE_SINGLE;
					
					if (toPrint.getTextBold() != -1) {
						this.printer.setBold((byte) 1);
					}else {
						this.printer.setBold((byte) 0);
					}
	
					if (toPrint.getFontsize() == -1)
						this.printer.setFontSize(1);
					else
						this.printer.setFontSize(toPrint.getFontsize());
					
					if (toPrint.getTextAlign() == PrintData.ALIGN_RIGHT)
						 this.printer.setJustification(ESCPOSPrinter.JUSTIFY_RIGHT);
					else if (toPrint.getTextAlign() == PrintData.ALIGN_CENTRE)
						this.printer.setJustification(ESCPOSPrinter.JUSTIFY_CENTER);
					else
						this.printer.setJustification(ESCPOSPrinter.JUSTIFY_LEFT);
	
					if (toPrint.getDataFormat() == PrintData.FORMAT_FEED)  {
					   if(toPrint.getMarginTop() != -1)
						   this.printer.feed((byte) toPrint.getMarginTop());
					}
	
					//content
					if (toPrint.getDataFormat() == PrintData.FORMAT_TXT) {
						this.printer.printText(toPrint.getText());
						
					}else if(toPrint.getDataFormat() == PrintData.FORMAT_IMG) {
						byte bitmapBytes[] = toPrint.getImage();
				        Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length); 					
					}else if (toPrint.getDataFormat() == PrintData.FORMAT_CUT) {
						this.printer.cut();
					}else if (toPrint.getDataFormat() == PrintData.FORMAT_QR) {
			            
			        }else if(toPrint.getDataFormat() == PrintData.FORMAT_DRAWER) {
			        	this.printer.kickDrawer();
			        	isKickDrawer = true;
			        }
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			this.close();
			result = false;
			return result;
		}		
	    //printer.close();
	    try {
	    	//kickdrawer no need wait for a long time

	    	if (this.data.size()<2 && isKickDrawer) {
	    		Thread.sleep(100);
	    	}else if (this.data.size()<40) {
			    Thread.sleep(1000);
	    	}else {
	    		Thread.sleep(this.data.size()*40);
	    	}
	    	
	    	printer.close();
	    	Thread.sleep(200);
	    	
		} catch (InterruptedException e) {
			e.printStackTrace();
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public int getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(int deviceType) {
		this.deviceType = deviceType;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getPrinterName() {
		return printerName;
	}

	public void setPrinterName(String printerName) {
		this.printerName = printerName;
	}
    
	public void addContent(PrintData data) {
		if (data != null)
		  this.data.add(data);
	}

	public ArrayList<PrintData> getData() {
		return data;
	}

	public void setData(ArrayList<PrintData> data) {
		this.data = data;
	}
	
	public boolean isConnected() {
	   return connected && this.printer.isConnected();
	}
	
	public boolean ping(){
		Boolean connected = NetUtil.ping(ip); 
		return connected;
	}
	@Override
	public void onConnected() {
		this.connected = true;
	}

	@Override
	public void onDisconnected() {
		this.connected = false;
		((PrintService)(this.context)).removePrinterHandlerByIP(this.ip);
		
	}

	@Override
	public void onSendFailed() {
			this.connected = false;
			this.printer.close();
			((PrintService)(this.context)).removePrinterHandlerByIP(this.ip);
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}
}
