package com.alfred.print.jobs;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.Set;
import java.util.UUID;

import static android.content.ContentValues.TAG;

public class WifiCommunication {
    public static final int WFPRINTER_CONNECTED = 110;
    public static final int WFPRINTER_DISCONNECTED = -110;
    public static final int SEND_FAILED = -100;
    public static final int SEND_SUCCESS = 100;
    public static final int WFPRINTER_CONNECTEDERR = -111;
    public static final int DATA_EMPTY = -99;
    private Socket socket;
    private BluetoothSocket mSocket;
    //	private SerialPort mSerialPort;
    public static final String localIPAddress = "127.0.0.1";
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
    }

    ;

    private boolean clientStart() {

        Log.d("WifiCommunication", "printer (" + ipAddress + ")");
        if ((ipAddress.indexOf(":") != -1)) {
            return clientStartBluetooth(ipAddress);
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

    private boolean clientStartSocket() {
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

    private boolean clientStartBluetooth(String address) {

        Log.d("clientStartBluetooth", "printer (" + address + ")");
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (setConnect(mBluetoothAdapter.getRemoteDevice(address))) {


            boolean isStart;
            try {


                if (mBluetoothAdapter != null) {
//					String innerprinter_address = "00:11:22:33:44:55";
//					BluetoothDevice innerprinter_device = null;
//					Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
//					for (BluetoothDevice device : devices) {
//						if (device.getAddress().equals(innerprinter_address)) {
//							innerprinter_device = device;
//						}
//					}
                    UUID PRINTER_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
                    mSocket = mBluetoothAdapter.getRemoteDevice(address).createRfcommSocketToServiceRecord(PRINTER_UUID);
                    mBluetoothAdapter.cancelDiscovery();

                    mSocket.connect();
                    out = mSocket.getOutputStream();
                    Log.d("clientStartBluetooth", "printer 连接成功(" + address + ")");
                    isStart = true;

                } else {

                    isStart = false;
                }

            } catch (Exception e) {
                isStart = false;
                e.printStackTrace();
            }
            return isStart;
        } else {

            Log.d("clientStartBluetooth", "失败printer (" + address + ")");
            return false;
        }
    }


    /**
     * 匹配设备
     *
     * @param device 设备
     */
    private Boolean setConnect(BluetoothDevice device) {
        try {
            Method createBondMethod = BluetoothDevice.class.getMethod("createBond");
            createBondMethod.invoke(device);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void close() {
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//		if (localIPAddress.equals(ipAddress)) {
//			return;
//		}
        try {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (socket != null) {
                socket.close();

            }
            if (mSocket != null) {
                mSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        in = null;
        out = null;
        socket = null;
        mSocket = null;
//			}
//		}).start();
    }

    private boolean sndByteBluetooth(byte[] data) {
        boolean result;
        if (data == null) {
            return false;
        }
        if (mSocket == null || out == null) {
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
        if (data == null) {
            return false;
        }
        if (socket == null || out == null) {
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

            if ((ipAddress.indexOf(":") != -1))
                return sndByteBluetooth(data);
            else
                return sndByteSocket(data);

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

//	public boolean sendMsg(String msg, String charset) {
//		boolean result = false;
//		if(TextUtils.isEmpty(msg)){
////			handler.sendEmptyMessage(DATA_EMPTY);
//			return false;
//		}
//
//		String msgData = msg;
//		if(App.countryCode == ParamConst.CHINA)
//			msgData = msg.replace("$", "￥");
//		try {
//			if(TextUtils.isEmpty(charset))
//				charset = "gbk";
//			byte[] data = msgData.getBytes(charset);
//			result = sndByte(data);
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
//		return result;
//	}

    private boolean isConnectedBluetooth() {
        if (this.mSocket == null) {
            return false;
        }
        return this.mSocket.isConnected();
    }

    private boolean isConnectedSocket() {
        if (this.socket == null)
            return false;
        else
            return (!this.socket.isClosed()) && this.socket.isConnected();
    }

    public boolean isConnected() {
//        if (localIPAddress.equals(ipAddress)) {
            return isConnectedBluetooth();
//        } else {
//            return isConnectedSocket();
//        }
    }

}
