package com.alfredselfhelp.global;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.alfredbase.BaseActivity;
import com.alfredbase.utils.CallBack;

import java.util.HashMap;

public class VtintApiCentre {
    private PendingIntent mPermissionIntent;
    private UsbManager mUsbManager;
    private UsbDeviceConnection mUsbDeviceConnection;
    private UsbDevice mUsbDevice;
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
        int vendorId = 1234;
        int productId = 12;
        if(deviceList.size()>0) {
            for (UsbDevice device : deviceList.values()) {
                if (vendorId == device.getVendorId()
                        && device.getProductId() == productId) {
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

    public void startPay(CallBack callBack){

    }
}
