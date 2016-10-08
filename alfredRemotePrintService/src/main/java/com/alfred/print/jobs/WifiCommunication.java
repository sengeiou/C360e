package com.alfred.print.jobs;

import android.text.TextUtils;

import com.alfred.remote.printservice.App;
import com.alfredbase.ParamConst;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

import android_serialport_api.SerialPort;

public class WifiCommunication {
	public static final int WFPRINTER_CONNECTED = 110;
	public static final int WFPRINTER_DISCONNECTED = -110;
	public static final int SEND_FAILED = -100;
	public static final int SEND_SUCCESS = 100;
	public static final int WFPRINTER_CONNECTEDERR = -111;
	public static final int DATA_EMPTY = -99;
	private Socket socket;
	private SerialPort mSerialPort;
	private String localIPAddress = "127.0.0.1";
	private OutputStream out;
//	private DataOutputStream dos;
	private InputStream in;
	
	private String ipAddress;
	private int port = 9100;

	public WifiCommunication() {
	}

	public boolean initSocket(String ipAddress, int port) {
		if (!TextUtils.isEmpty(ipAddress)) {
			this.ipAddress = ipAddress;
			this.port = port;
		}
		return clientStart();
	};

	private boolean clientStart() {
		if (localIPAddress.equals(ipAddress)) {
			return clientStartSerial();
		}

		return clientStartSocket();
//		new Thread(new Runnable() {
//
//			@Override
//			public void run() {

//			}
//		}
// ).start();
		
	}

	private boolean clientStartSocket(){
		boolean isStart;
		try {
			socket = new Socket(ipAddress, port);
			socket.setKeepAlive(true);
			socket.setReuseAddress(true);
			socket.setTcpNoDelay(true);
			socket.setSoLinger(true, 10);
			out = socket.getOutputStream();
//					dos = new DataOutputStream(out);
			in = socket.getInputStream();
			isStart = true;
		} catch (Exception e) {
			isStart = false;
			e.printStackTrace();
		}
		return isStart;
	}
	private boolean clientStartSerial(){
		boolean isStart;

		try {
//			 mSerialPort = new SerialPort(new File("/dev/ttyS1"), 19200, 0,
//			 true); // For leo
			mSerialPort = new SerialPort(new File("/dev/ttyS1"), 115200, 0,
					true);
			out = mSerialPort.getOutputStream();
			isStart = true;
		} catch (Exception e) {
			isStart = false;
			e.printStackTrace();
		}
		return isStart;
	}

	public void close() {
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
		if (localIPAddress.equals(ipAddress))
			return;
				try {
					if(in != null){
						in.close();
					}
					if(out != null){
						out.close();
					}
					if (socket != null) {
						socket.close();

					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				in = null;
				out = null;
				socket = null;

//			}
//		}).start();
	}

	private boolean sndByteSerial(byte[] data) {
		boolean result = true;
		if (data == null) {
			return false;
		}
		if (mSerialPort == null || out == null) {
			return false;
		}
		try {
			out.write(data);
			out.flush();
			result = true;
		} catch (IOException e) {
			result = false;
			e.printStackTrace();
		}
		return result;
	}
	private boolean sndByteSocket(byte[] data) {
		boolean result;
		if(data == null){
			return false;
		}
		if (socket == null || out == null){
			return false;
		}
		try {
			out.write(data);
			out.flush();
			result = true;
		} catch (IOException e) {
			result = false;
			e.printStackTrace();
		}
		return result;
	}

	public boolean sndByte(byte[] data) {
		if (localIPAddress.equals(this.ipAddress))
			return  sndByteSerial(data);
		else
			return  sndByteSocket(data);
	}

//	public boolean checkStatus(byte[] data) {
//		boolean result;
//		if(data == null){
//			return false;
//		}
//		if (socket == null || out == null){
//			return false;
//		}
//		try {
//			out.write(data);
//			Log.e("Print", in.read() + "");
//			out.flush();
//			result = true;
//		} catch (IOException e) {
//			result = false;
//			e.printStackTrace();
//		}
//		return result;
//	}

	public boolean sendMsg(String msg, String charset) {
		boolean result = false;
		if(TextUtils.isEmpty(msg)){
//			handler.sendEmptyMessage(DATA_EMPTY);
			return false;
		}

		String msgData = msg;
		if(App.countryCode == ParamConst.CHINA)
			msgData = msg.replace("$", "￥");
		try {
			if(TextUtils.isEmpty(charset))
				charset = "gbk";
			byte[] data = msgData.getBytes(charset);
			result = sndByte(data);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}

	private boolean isConnectedSerial() {
		boolean mbStatus = false;
		if (mSerialPort != null) {
			mbStatus = true;
		}
		return mbStatus;
	}
	
	private boolean isConnectedSocket() {
		boolean ret = false;
		if (this.socket == null)
			return false;
		else
		  return (!this.socket.isClosed()) && this.socket.isConnected();
	}

	public boolean isConnected() {
		if (localIPAddress.equals(ipAddress)) {
			return isConnectedSerial();
		} else {
			return isConnectedSocket();
		}
	}
	
}
