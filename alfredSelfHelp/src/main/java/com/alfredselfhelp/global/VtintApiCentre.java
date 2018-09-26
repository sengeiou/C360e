package com.alfredselfhelp.global;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.alfredbase.BaseActivity;

import java.util.HashMap;

public class VtintApiCentre {
    private static final byte STX = 0x02;
    private static final byte ETX = 0x03;
    private static final byte EOT = 0x04;
    private static final byte ENQ = 0x05;
    private static final byte ACK = 0x06;
    private static final byte NAK = 0x15;
    private static final String sales = "C200";
    private static final String amount = "0412%012d";
    private static final String identifier = "5706%06d";
    private static final String trace = "612000109040112121200001";




    private PendingIntent mPermissionIntent;
    private UsbManager mUsbManager;
    private UsbDeviceConnection mUsbDeviceConnection;
    private UsbDevice mUsbDevice;
    //代表一个接口的某个节点的类:写数据节点
    private UsbEndpoint usbEpOut;
    //代表一个接口的某个节点的类:读数据节点
    private UsbEndpoint usbEpIn;

    public static VtintApiCentre instance;
    private BaseActivity context;
    private VtintApiCentre(){}

    public static VtintApiCentre getInstance() {
        if (instance == null){
            instance = new VtintApiCentre();

        }
        return instance;
    }

    public void init(BaseActivity context){
        this.context = context;
    }

    public void SearchUsb() {
        mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        // mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        mPermissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), 0);
        //注册USB设备权限管理广播
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        context.registerReceiver(mUsbDeviceReceiver, filter);

        // 列出所有的USB设备，并且都请求获取USB权限
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        Log.d("typeUsb", " 33333333--"+deviceList.size());
        int productId = 72800009;
        if(deviceList.size()>0) {
            for (UsbDevice device : deviceList.values()) {
                if (device.getProductId() == productId) {
                    mUsbManager.requestPermission(device, mPermissionIntent);
                }
            }
        }
    }

    private static final String ACTION_USB_PERMISSION = "com.usb.printer.USB_PERMISSION";
    private final BroadcastReceiver mUsbDeviceReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d("typeUsb", " 444444444444" +action);
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false) && usbDevice != null) {
                        Log.d("typeUsb", usbDevice.getProductId()+" --111111111111111--"+usbDevice.getVendorId());
                        // 获取USBDevice
                        mUsbDevice = usbDevice;
                    }
                }
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                Log.d("typeUsb", " 222222222");
                if (mUsbDevice != null) {
                    if (mUsbDeviceConnection != null) {
                        mUsbDeviceConnection.close();
                    }
                }
            }
        }
    };

    public void onDestory(){
        context.unregisterReceiver(mUsbDeviceReceiver);
    }

    public boolean startPay(String msg){
        boolean status = false;
        status = checkEDC();
        status = sendMsg(msg);
        status = endEDC();
        return status;
    }
    private int i=0;

    private boolean sendMsg(String msg){
        boolean status = false;
        try {
            StringBuffer str = new StringBuffer(sales);
            str.append(String.format(amount, Integer.parseInt(msg)));
            str.append(String.format(identifier, i++));
            msg = str.append(trace).toString();
            byte[] msgB = msg.getBytes("UTF8");
            byte LRC = getBCC(msgB);
            byte[] msgG = new byte[msgB.length + 3];
            for(int i = 0; i < msgG.length; i++){
                if(i == 0){
                    msgG[i] = STX;
                }else if (i == msgG.length -2){
                    msgG[i] = ETX;
                }else if(i == msgG.length - 1){
                    msgG[i] = LRC;
                }else{
                    msgG[i] = msgB[i-1];
                }
            }
            int ret = -1;
            ret = mUsbDeviceConnection.bulkTransfer(usbEpOut, msgG, msgG.length, 1500);
            if(ret != -1){
                byte[] receiveytes = new byte[1];
                ret = mUsbDeviceConnection.bulkTransfer(usbEpIn, receiveytes, receiveytes.length, 2000);
                if(ret != -1 && receiveytes[0] == ACK){
                    status = true;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return status;
    }

    private boolean checkEDC(){
        boolean canSendMsg = false;
        byte[] data = new byte[1];
        data[0] = ENQ;
        int ret = -1;
        ret = mUsbDeviceConnection.bulkTransfer(usbEpOut, data, data.length, 1500);
        if(ret != -1){
            byte[] receiveytes = new byte[1];
            ret = mUsbDeviceConnection.bulkTransfer(usbEpIn, receiveytes, receiveytes.length, 2000);
            if(ret != -1 && receiveytes[0] == ACK){
                canSendMsg = true;
            }
        }
        return canSendMsg;
    }
    private boolean endEDC(){
        boolean canSendMsg = false;
        byte[] data = new byte[1];
        data[0] = EOT;
        int ret = -1;
        ret = mUsbDeviceConnection.bulkTransfer(usbEpOut, data, data.length, 5000);
        if(ret != -1){
            byte[] receiveytes = new byte[1];
            ret = mUsbDeviceConnection.bulkTransfer(usbEpIn, receiveytes, receiveytes.length, 15*60*1000);
            if(ret != -1 && receiveytes[0] == ENQ){
                canSendMsg = true;
            }
        }
        return canSendMsg;
    }

    public void initUsb(){
        if(mUsbDevice != null){
            UsbInterface usbInterface = mUsbDevice.getInterface(0);
            //用UsbDeviceConnection 与 UsbInterface 进行端点设置和通讯
            if (usbInterface.getEndpoint(1) != null) {
                usbEpOut = usbInterface.getEndpoint(1);
            }
            if (usbInterface.getEndpoint(0) != null) {
                usbEpIn = usbInterface.getEndpoint(0);
            }
            mUsbDeviceConnection = mUsbManager.openDevice(mUsbDevice);
        }
    }

    private byte getBCC(byte[] data) {

//        String ret = "";
        byte BCC[]= new byte[1];
        for(int i=0;i<data.length;i++)
        {
            BCC[0]=(byte) (BCC[0] ^ data[i]);
        }
        BCC[0]=(byte) (BCC[0] ^ ETX);
//        String hex = Integer.toHexString(BCC[0] & 0xFF);
//        if (hex.length() == 1) {
//            hex = '0' + hex;
//        }
//        ret += hex.toUpperCase();
        return BCC[0];
    }
}
