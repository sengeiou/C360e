package com.alfred.print.jobs;

import android.text.TextUtils;
import android.util.Log;

import com.alfred.remote.printservice.App;
import com.alfredbase.ParamConst;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

public class WifiCommunication {
	public static final int WFPRINTER_CONNECTED = 110;
	public static final int WFPRINTER_DISCONNECTED = -110;
	public static final int SEND_FAILED = -100;
	public static final int SEND_SUCCESS = 100;
	public static final int WFPRINTER_CONNECTEDERR = -111;
	public static final int DATA_EMPTY = -99;
	private Socket socket;
	private OutputStream out;
//	private DataOutputStream dos;
	private InputStream in;
	
	private String ipAddress;
	private int port = 9100;

	public WifiCommunication() {
	}

	public boolean  initSocket(String ipAddress, int port) {
		if (!TextUtils.isEmpty(ipAddress)) {
			this.ipAddress = ipAddress;
			this.port = port;
		}
		return clientStart();
	};

	private boolean clientStart() {
//		new Thread(new Runnable() {
//
//			@Override
//			public void run() {
		boolean isStart = false;
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
//			}
//		}
// ).start();
		
	}

	public void close() {
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
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
	
	public boolean sndByte(byte[] data) {
		boolean result;
		if(data == null){
			return false;
		}
		if (socket == null || out == null){
			return false;
		}
		try {
			out.write(data);
			Log.e("TESTTEST", in.read() + "");
			out.flush();
			result = true;
		} catch (IOException e) {
			result = false;
			e.printStackTrace();
		}
		return result;
	}

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
	
	public boolean isConnected() {
		boolean ret = false;
		if (this.socket == null)
			return false;
		else
		  return (!this.socket.isClosed()) && this.socket.isConnected();
	}
	
}
