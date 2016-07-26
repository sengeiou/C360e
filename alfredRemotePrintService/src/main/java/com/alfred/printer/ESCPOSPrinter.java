package com.alfred.printer;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.alfred.print.jobs.WifiCommunication;
import com.alfred.remote.printservice.PrintService;

public class ESCPOSPrinter {
	static private String TAG = "ESCPOSPrinter";
	/* ASCII codes */
	static byte  NUL = 0x00;
	static byte  LF = 0x0A;
	static byte  ESC = 0x1B;
	static byte  FS =  0x1C;
	static byte  GS =  0x1D;
	
	//barcode type
	static byte BARCODE_UPCA = 0;
	static byte  BARCODE_UPCE = 1;
	static byte  BARCODE_JAN13 = 2;
	static byte  BARCODE_JAN8 = 3;
	static byte  BARCODE_CODE39 = 4;
	static byte  BARCODE_ITF = 5;
	static byte  BARCODE_CODABAR = 6;

	/* Cut types */
	static byte CUT_FULL = 65;
	static byte CUT_PARTIAL = 66;
	
	/* Fonts */
	static public byte FONT_A = 0;
	static public byte FONT_B = 1;
	static public byte FONT_C = 2;
	
	/* Image sizing options */
	static byte IMG_DEFAULT = 0;
	static byte IMG_DOUBLE_WIDTH = 1;
	static byte IMG_DOUBLE_HEIGHT = 2;
	
	/* Justifications */
	static public byte JUSTIFY_LEFT = 0;
	static public byte JUSTIFY_CENTER = 1;
	static public byte JUSTIFY_RIGHT = 2;
	
	/* Print mode constants */
	static byte MODE_FONT_A = 0;
	static byte MODE_FONT_B = 1;
	static byte MODE_EMPHASIZED = 8;
	static byte MODE_DOUBLE_HEIGHT = 16;
	static byte MODE_DOUBLE_WIDTH = 32;
	static int  MODE_UNDERLINE = 128;
	
	/* Underline */
	static public byte UNDERLINE_NONE = 0;
	static public byte UNDERLINE_SINGLE = 1;
	static public byte UNDERLINE_DOUBLE = 2;

	
	WifiCommunication wfComm = null;
	int  connFlag = 0;
//	revMsgThread revThred = null;
	Context mContext;
	//checkPrintThread cheThread = null;
	//alignment
	static int ALIGN_LEFT = -1;
	static int ALIGN_CENTER = 0;
	static int ALIGN_RIGHT = 1;
	Handler parent = null;
	private static final int WFPRINTER_REVMSG = 0x06;
	
	private   Handler mHandler;
	private String ip = null;
	
    public ESCPOSPrinter(PrintService context,Handler parent) {
		super();
		this.mContext = context;
		this.parent = parent;
    }
    
    public void open(String ip, int port) {
    	this.ip = ip;
    	if (wfComm == null) {
    	  wfComm = new WifiCommunication(this.parent);
    	  wfComm.initSocket(ip,9100);
    	}
    }
    public boolean isConnected() {
    	boolean ret = false;
    	if (wfComm != null) {
    		ret = wfComm.isConnected();
    	}
    	return ret;
    }
    public void reset()  throws Exception{
    	boolean result = true;

    	byte[] tcmd = new byte[2];
        tcmd[0] = ESCPOSPrinter.ESC;
   	    tcmd[1] = 0x40;
   	    result =  wfComm.sndByte(tcmd);
    	if (result == false) {
    		throw new RuntimeException("reset Failed"); 
    	}   	    
    }
    
    //tested
    public synchronized void close() {
    	Log.d(TAG, "printer ("+ip+") close");
    	if (wfComm !=null) {
    	  wfComm.close();
    	  wfComm = null;
    	}
    }
    
    
    public  void checkStatus()  throws Exception{
    	boolean result = true;
		byte[] tcmd = new byte[3];
		tcmd[0] = 0x10;
		tcmd[1] = 0x04;
		tcmd[2] = 0x04;  
		result =   wfComm.sndByte(tcmd);  
    	if (result == false) {
    		throw new RuntimeException("Send Failed"); 
    	}		
    }
    
    //tested
    public void setJustification (byte just) throws Exception{
    	boolean result = true;
    	byte[] cmd = new byte[3];        
	    cmd[0] = ESCPOSPrinter.ESC;
	    cmd[1] = 0x61;
	    cmd[2] = just; 
	    result =  wfComm.sndByte(cmd);
    	if (result == false) {
    		throw new RuntimeException("Send Failed"); 
    	}	    
    }
    
	/**
	 * Add text to the buffer.
	 *
	 * Text should either be followed by a line-break, or feed() should be called
	 * after this to clear the print buffer.
	 *
	 * @param data $str Text to print
	 */    
    public void printText(String data) throws Exception{
    	boolean result = true;
    	checkStatus();
    	if (data!=null&& data.length()>0) {
    		result = wfComm.sendMsg(data,"gbk");   
    	}
    	if (result == false) {
    		throw new RuntimeException("Send Failed"); 
    	}
    }
    
    /*
     * Print buffer and next line
     * */
    public void printLF() throws Exception {
        byte[] tail = new byte[2];
        tail[0] = 0x0A;
        tail[1] = 0x0D;
        boolean result =  wfComm.sndByte(tail);     
    	if (result == false) {
    		throw new RuntimeException("Send Failed"); 
    	}
    }
    
    /* tested
     * FONT_A and FONT_B
     * */
    public void setFont(byte font) throws Exception {
        byte[] tcmd = new byte[3];
        tcmd[0] = ESCPOSPrinter.ESC;
   	    tcmd[1] = 0x4D;
   	    tcmd[2] = font;   //0.125mm line height
   	    boolean result =  wfComm.sndByte(tcmd); 
    	if (result == false) {
    		throw new RuntimeException("Send Failed"); 
    	}   	    
    }
    
    /*
     * Tested 
     * */
    public  void setFontSize(int size)  throws Exception {
    	byte[] definedSize = {0x00, 0x11, 0x22, 0x33,0x44,0x55,0x66,0x77};
    	byte[] cmd = new byte[3];        
	    cmd[0] = 0x1D;
	    cmd[1] = 0x21;
	    if (size>0&&size<definedSize.length) {
	       cmd[2] = definedSize[size-1];
	    }else {
	       cmd[2] = 0x00;
	    }
	    boolean result =  wfComm.sndByte(cmd);  
    	if (result == false) {
    		throw new RuntimeException("Set Font Size Failed"); 
    	} 	    
    }
    
    //tested
    public  void setUnderline(byte underline) throws Exception {
        byte[] tcmd = new byte[3];
        tcmd[0] = ESCPOSPrinter.ESC;
   	    tcmd[1] = 0x2D;
   	    tcmd[2] = underline;   //0.125mm line height
   	    boolean result = wfComm.sndByte(tcmd);   
        
   	    if (result) {
	        byte[] chunderline = new byte[3];
	        tcmd[0] = ESCPOSPrinter.FS;
	   	    tcmd[1] = 0x21;
	   	    tcmd[2] |= 0x80;   
	   	    result = wfComm.sndByte(tcmd);  
   	    }
    	if (result == false) {
    		throw new RuntimeException("Set Underline Failed"); 
    	}   	    
    }
    
    public  void setBold(byte bold)  throws Exception{
        byte[] tcmd = new byte[3];
        tcmd[0] = ESCPOSPrinter.ESC;
   	    tcmd[1] = 0x45;
   	    tcmd[2] = bold;   
   	    boolean result = wfComm.sndByte(tcmd);    
    	if (result == false) {
    		throw new RuntimeException("Set Bold Text Failed"); 
    	}    	    
    }
    
    //tested
    public  void feed(byte lines) throws Exception {
    	boolean result = true;
    	if (lines <=1) {
	         byte[] tcmd = new byte[1];
	         tcmd[0] = ESCPOSPrinter.LF;
	         result = wfComm.sndByte(tcmd);
    	}else{
	         byte[] tcmd = new byte[3];
	         tcmd[0] = ESCPOSPrinter.ESC;
	    	 tcmd[1] = 0x64;
	    	 tcmd[2] = lines;   //0.125mm line height
	    	 result =  wfComm.sndByte(tcmd);
    	}
    	if (result == false) {
    		throw new RuntimeException("Feed Failed"); 
    	}      	
    }
    
    //tested
//    public  boolean margin(byte mm) {
//	       byte[] tcmd = new byte[3];
//	       tcmd[0] = ESCPOSPrinter.ESC;
//	       tcmd[1] = 0x4A;
//	       tcmd[2] = 0;   //0.125mm line height
//	       //wfComm.sndByte(tcmd);
//	       return true;
//    }
    
    //tested
    public  void cut()  throws Exception  {
      byte[] cutcmd = new byte[4];
      cutcmd[0]=ESCPOSPrinter.GS;
      cutcmd[1]=0x56;
      cutcmd[2]=0x42;
      cutcmd[3]=90;   
      boolean result =  wfComm.sndByte(cutcmd);   	
	      try {
			this.feed((byte) 1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  	if (result == false) {
			throw new RuntimeException("Cut Failed"); 
		}         
    }
    
    //tested
    public  void kickDrawer()  throws Exception {
		byte[] tcmd = new byte[5];
		tcmd[0] = ESCPOSPrinter.ESC;
		tcmd[1] = 0x70;
		tcmd[2] = 0x00;     
		tcmd[3] = 0x40;   
		tcmd[4] = 0x50;   
		boolean result =   wfComm.sndByte(tcmd); 
	  	if (result == false) {
			throw new RuntimeException("Kick Drawer Failed"); 
		} 		
    }
    
	public  void printImage(int width, int height, byte[]bitmap) throws Exception {
		byte[] sendData = null;
    	int i = 0,s = 0,j = 0,index = 0,lines = 0;
    	byte[] temp = new byte[(width / 8)*5];
    	byte[] dHeader = new byte[8];
    	boolean result = true;
    	if(bitmap.length!=0){
    		dHeader[0] = 0x1D;
        	dHeader[1] = 0x76;
        	dHeader[2] = 0x30;
        	dHeader[3] = 0x00;
        	dHeader[4] = (byte)(width/8);
        	dHeader[5] = 0x00;
        	dHeader[6] = (byte)(bitmap.length%256);
        	dHeader[7] = (byte)(bitmap.length/256);
        	result = wfComm.sndByte(dHeader); 	
	    	for( i = 0 ; i < (bitmap.length/5)+1 ; i++ ){    
	    		s = 0;
	    		if( i < bitmap.length/5 ){
	    			lines = 5;
	    		}else{
	    			lines = bitmap.length%5;
	    		}
	    		for( j = 0 ; j < lines*(width / 8) ; j++ ){
	    			temp[s++] = sendData[index++];
	    		}
	    		result = wfComm.sndByte(temp); 
	    		try {
					Thread.sleep(60);                    
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
	    		for(j = 0 ; j <(width/8)*5 ; j++ ){         
				    temp[j] = 0;
			    }
	    	}
    	}
	  	if (result == false) {
			throw new RuntimeException("Print Image Failed"); 
		} 
    }
	
    //=
//	class revMsgThread extends Thread {	
//		@Override
//		public void run() {            
//			try {
//				Message msg = new Message();
//				int revData;
//				while(true)
//	            {
//					revData = wfComm.revByte();               //read data from WIFI Printer
//					if(revData != -1){
//						
//						msg = mHandler.obtainMessage(WFPRINTER_REVMSG);
//		                msg.obj = revData;
//		                mHandler.sendMessage(msg);
//					}    
//				    Thread.sleep(20);
//	            }
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//				Log.d("POS Printer Receiver","Failed to read data");
//			}
//		}
//	}
}

