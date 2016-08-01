package com.alfred.print.jobs;

import android.os.Handler;
import android.text.TextUtils;

import com.alfred.remote.printservice.App;
import com.alfredbase.ParamConst;

import java.io.DataOutputStream;
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
	private DataOutputStream dos;
	private InputStream in;
	
	private String ipAddress;
	private int port = 9100;
	public Handler handler;

	public WifiCommunication(Handler handler) {
		this.handler = handler;
	}

	public void initSocket(String ipAddress, int port) {
		if (!TextUtils.isEmpty(ipAddress)) {
			this.ipAddress = ipAddress;
			this.port = port;
		}
		clientStart();
	};

	private void clientStart() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					socket = new Socket(ipAddress, port);
					socket.setKeepAlive(true);
					socket.setReuseAddress(true);
					socket.setTcpNoDelay(true);
					socket.setSoLinger(true, 10);
					out = socket.getOutputStream();
					dos = new DataOutputStream(out);
					in = socket.getInputStream();
					handler.sendEmptyMessage(WFPRINTER_CONNECTED);
				} catch (Exception e) {
					e.printStackTrace();
					handler.sendEmptyMessage(WFPRINTER_CONNECTEDERR);
				}
			}
		}).start();
		
	}

	public void close() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					if (socket != null) {
						//socket.shutdownOutput();

								socket.close();

					}
					if(out != null){
						out.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}

				socket = null;
				out = null;
			}
		}).start();
	}
	
	public boolean sndByte(byte[] data) {
		boolean result = true;
		if(data == null){
			handler.sendEmptyMessage(DATA_EMPTY);
		}
		if (socket == null || out == null){
			handler.sendEmptyMessage(SEND_FAILED);
			return false;
		}
		try {
			out.write(data);
			out.flush();
			handler.sendEmptyMessage(SEND_SUCCESS);
		} catch (IOException e) {
			result = false;
			e.printStackTrace();
			handler.sendEmptyMessage(SEND_FAILED);
		}
		return result;
	}

	public boolean sendMsg(String msg, String charset) {
		boolean result = false;
		if(TextUtils.isEmpty(msg)){
			handler.sendEmptyMessage(DATA_EMPTY);
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
