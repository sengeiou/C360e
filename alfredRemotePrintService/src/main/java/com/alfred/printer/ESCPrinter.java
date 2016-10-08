package com.alfred.printer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.alfred.print.jobs.WifiCommunication;
import com.alfred.remote.printservice.PrintService;
import com.alfred.remote.printservice.WIFIPrintCallback;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;


public class ESCPrinter implements WIFIPrintCallback{

	/**
	 */
//	public static final int FONT_A = ESCPOSPrinter.FONT_A;
//	public static final int FONT_B = ESCPOSPrinter.FONT_B;
//	public static final int FONT_C = ESCPOSPrinter.FONT_C;
//
//
//	public final static int MSG_PRINTER_FOUND = 1;
//	public final static boolean TXT_UNDERLINE = true;

	Context context;
	
	private String ip;
	ESCPOSPrinter printer;

	//data
//	private ArrayList<PrintData> data;

	private boolean connected = false;

	WifiCommunication wfComm = null;

	public static final int DEFAULT_PORT = 9100;

	public ESCPrinter(String ip) {
		
//		hdl.setPrinterCbk(this);
		this.ip = ip;
		this.printer = new ESCPOSPrinter((PrintService) context);
	}
	
	public ArrayList<String> discovery() {
		
		return null;
	}
	
	public boolean reconnect() {
		return open();
	}

	public boolean open() {
		if (wfComm == null) {
			wfComm = new WifiCommunication();
		}
		return wfComm.initSocket(ip,DEFAULT_PORT);
	}
	public boolean isConnected() {
		boolean ret = false;
		if (wfComm != null) {
			ret = wfComm.isConnected();
		}
		return ret;
	}


	//tested
	public synchronized void close() {
		Log.d(TAG, "printer ("+ip+") close");
		if (wfComm !=null) {
			wfComm.close();
			wfComm = null;
		}
		this.connected = false;
	}


//	public boolean ping(){
//		Boolean connected = NetUtil.ping(ip);
//		return connected;
//	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	@Override
	public void onConnected() {
		this.connected = true;
	}

//	public  void checkStatus()  throws Exception{
//		byte[] tcmd = new byte[3];
//		tcmd[0] = 0x10;
//		tcmd[1] = 0x04;
//		tcmd[2] = 0x04;
//		boolean result = wfComm.checkStatus(tcmd);
//		this.printer.getOut().reset();
////		wfComm.close();
//		if (!result) {
//			throw new RuntimeException("Print action Failed");
//		}
//	}

	public boolean setData(List<PrintData> data) {
		boolean result = true;
		boolean isKickDrawer = false;
		try {
//			checkStatus();
			this.printer.reset();

			for (int i=0; i<data.size(); i++) {
				PrintData toPrint = data.get(i);
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
			sendData();
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

			if (data.size()<2 && isKickDrawer) {
				Thread.sleep(100);
			}else if (data.size()<40) {
				Thread.sleep(1000);
			}else {
				Thread.sleep(data.size()*40);
			}

			close();
			Thread.sleep(200);

		} catch (InterruptedException e) {
			e.printStackTrace();
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public void sendData() {
		boolean result = wfComm.sndByte(this.printer.getOut().toByteArray());
		this.printer.getOut().reset();
//		wfComm.close();
		if (!result) {
			throw new RuntimeException("Print action Failed");
		}
	}

	@Override
	public void onDisconnected() {
		close();
		PrintService.instance.removePrinterByIP(this.ip);
	}

	@Override
	public void onSendFailed() {
		close();
		PrintService.instance.removePrinterByIP(this.ip);
	}

}
