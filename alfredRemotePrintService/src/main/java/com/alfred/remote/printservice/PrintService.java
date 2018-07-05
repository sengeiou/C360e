package com.alfred.remote.printservice;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.alfred.print.jobs.PrintManager;
import com.alfred.printer.ESCPrinter;
import com.alfredbase.PrinterDeviceConfig;
import com.alfredbase.javabean.PrintBean;
import com.alfredbase.store.Store;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.security.auth.callback.Callback;

import static com.alfredbase.javabean.PrintBean.PRINT_TYPE;

public class PrintService extends Service {

    public static final String ACTION_START = "com.alfred.print.ACTION_START";
    public static final String ACTION_SHUT_DOWN = "com.alfred.print.ACTION_SHUT_DOWN";
    public static final String ACTION_DISCOVER_PRINTER = "com.alfred.print.ACTION_DISCOVER_PRINTER";

    static final String TAG = "Alfred Print Service";

    private boolean mShutDown = false;

    public static PrintService instance;
    private PrintManager printJobMgr;
    private PrinterQueueManager pqMgr;

    public static String PRINT_DOLLAR_SIGN_SETTING = "DOLLAR_SIGN_SETTING";
    public static String PRINT_LANG_SETTING = "PRINT_LANG_SETTING";
    public static String PRINT_COUNTRY_SETTING = "PRINT_COUNTRY_SETTING";

    final Object lock = new Object();

    List<IAlfredRemotePrintServiceCallback> mCallbacks;


    private ArrayList<PrintBean> mBluetoothDevicesDatas = new ArrayList<>();
    BluetoothAdapter mBluetoothAdapter;
    private Callback callback;


    //IP Printer Handler
//    static Map<String, WIFIPrinterHandler> printerHandlers = new ConcurrentHashMap<String, WIFIPrinterHandler>();
    private Map<String, ESCPrinter> escPrinterMap = new HashMap<String, ESCPrinter>();

    @Override
    public void onCreate() {
        super.onCreate();
        this.printJobMgr = new PrintManager(this);
        this.pqMgr = new PrinterQueueManager(this);
        instance = this;
        mCallbacks = new ArrayList<IAlfredRemotePrintServiceCallback>();
        //广播注册

        this.printJobMgr.start();
        this.pqMgr.start();



        Log.d(TAG, "Creating Service");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind ");
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if ("alfred.intent.action.bindPrintService".equals(intent.getAction()) &&
                    ("fxxxkprinting").equals((String) extras.get("PRINTERKEY"))) {
//				if (this.printJobMgr!=null)
//					this.printJobMgr.addPingJob();

                return new PrintServiceBinder(this);
            }
        }
        return null;
    }

    @Override
    public void onDestroy() {

        Log.d(TAG, "Destroying Service");
        //this.printJobMgr.clear();
        this.printJobMgr.stop();
        this.mCallbacks.clear();
        //close all sockets
        this.pqMgr.stop();
        closeAllSockets();

            try {
                unregisterReceiver(mReceiver);
            } catch (IllegalArgumentException e) {
            }



        super.onDestroy();
        //this.printJobMgr = null;


    }



    public void closeAllSockets() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Set<String> key = escPrinterMap.keySet();
                for (Iterator it = key.iterator(); it.hasNext(); ) {
                    String ip = (String) it.next();
                    ESCPrinter escp = escPrinterMap.get(ip);
                    if (escp != null) {
                        escp.setConnected(false);
                        escp.close();
                    }
                }
            }
        }).start();

    }

    public PrintManager getPrintMgr() {
        return this.printJobMgr;
    }

    public PrinterQueueManager getPqMgr() {
        return pqMgr;
    }

    public boolean isTMU220(String model) {
        if (model != null && model.length() > 0) {
            if (model.toLowerCase().contains("u220"))
                return true;
        }
        return false;
    }

    public String getDollarSignStr() {
        String sign = "$";
        Integer dollarSign = Store.getInt(this, PrintService.PRINT_DOLLAR_SIGN_SETTING);
        if (dollarSign != null) {
            if (dollarSign.intValue() == PrinterDeviceConfig.PRINT_DOLLAR_CNY)
                sign = "￥";
            if (dollarSign.intValue() == PrinterDeviceConfig.PRINT_DOLLAR_MYM)
                sign = "RM";
        }
        return sign;
    }

    public void configure(int country, int lang, int dollarsign) {
        Store.putInt(this, PrintService.PRINT_COUNTRY_SETTING, country);
        Store.putInt(this, PrintService.PRINT_LANG_SETTING, lang);
        Store.putInt(this, PrintService.PRINT_DOLLAR_SIGN_SETTING, dollarsign);
    }

    public void removePrinterByIP(String ip) {
        this.escPrinterMap.remove(ip);
    }


//	public WIFIPrinterHandlerFQ getPrinterHandler(String ip) {
//		WIFIPrinterHandlerFQ wfpHandler = null;
//		synchronized(lock) {
//		  wfpHandler = PrintService.printerHandlers.get(ip);
//		  if (wfpHandler == null) {
////			  Looper.prepare();
//			  wfpHandler = new WIFIPrinterHandlerFQ(ip);
////			  Looper.loop();
//			  PrintService.printerHandlers.put(ip, wfpHandler);
//		  }
//		}
//		return wfpHandler;
//	}


    public Map<String, ESCPrinter> getEscPrinterMap() {
        return escPrinterMap;
    }

    public void putEscPrinterMap(String id, ESCPrinter eSCPrinter) {
        this.escPrinterMap.put(id, eSCPrinter);
    }


    //搜索蓝牙
    public void SearchBluetooth() {
        Log.d("SearchBluetooth", "start");

        mBluetoothDevicesDatas.clear();
        mBluetoothAdapter.startDiscovery();

    }


    public Boolean registerReceiverBluetooth() {

//        try {
//            unregisterReceiver(mReceiver);
//            return  false;
//        } catch (Exception e) {
//
//            e.printStackTrace();
//            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
//
//            registerReceiver(mReceiver, filter);
//            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//            return  true;
//        }
//        if(mReceiver != null ) {
//
//            Log.e("registerRe", " ----");
//
//            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
//
//            registerReceiver(mReceiver, filter);
//            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//            return false;
//        }else {
//            Log.e("registerReceiverBluetoo", " ----");
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

            registerReceiver(mReceiver, filter);
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            return true;
//        }
      //  return false;

    }

    //	/**
//	 * 通过广播搜索蓝牙设备
//	 */
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // 把搜索的设置添加到集合中
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //已经匹配的设备
                if (device.getBondState() == BluetoothDevice.BOND_BONDED) {

                    Log.d("type", " 已经匹配的设备");
                    addBluetoothDevice(device);

                    //没有匹配的设备
                } else {

                    Log.d("type", " 没有匹配的设备");
                    addBluetoothDevice(device);
                }

                //搜索完成
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {

                Log.e("BluetoothAdapterend", "   " + mBluetoothDevicesDatas.size());

                for(int i=0;i<mBluetoothDevicesDatas.size();i++){
                 callback.getBluetoothDevices(mBluetoothDevicesDatas.get(i));
              //    callback.getBluetoothDevices(mBluetoothDevicesDatas.get(i));
                }
             //   mBluetoothAdapter.cancelDiscovery();
               unregisterReceiver(mReceiver);
         //    mReceiver=null;

            //    abortBroadcast();
            }
        }


    };



    //		/**
//		 * 添加数据
//		 * @param device 蓝牙设置对象
//		 */
    @SuppressLint("NewApi")
    private void addBluetoothDevice(BluetoothDevice device) {


        Log.d("type", " 添加蓝牙");
        for (int i = 0; i < mBluetoothDevicesDatas.size(); i++) {
            if (device.getAddress().equals(mBluetoothDevicesDatas.get(i).getAddress())) {
                mBluetoothDevicesDatas.remove(i);
                Log.d("addBluetoothDevice", " ---remove");
            }
        }
        if (device.getBondState() == BluetoothDevice.BOND_BONDED && device.getBluetoothClass().getDeviceClass() == PRINT_TYPE) {



            mBluetoothDevicesDatas.add(0, new PrintBean(device));
            Log.d("addBluetoothDevice", " -BOND_BONDED--" + mBluetoothDevicesDatas.size());

//                    for (int i = 0; i < mBluetoothDevicesDatas.size(); i++) {
//
//
//                        if (device.getAddress().equals(mBluetoothDevicesDatas.get(i).getAddress())) {
//                            mBluetoothDevicesDatas.remove(i);
//                            Log.d("addBluetoothDevice", " ---remove");
//                        } else {
//                            callback.getBluetoothDevices(mBluetoothDevicesDatas.get(i));
//                        }
//                    }



        } else {


            Log.d("type", " ;;;;" + device.getBluetoothClass().getDeviceClass() + "--" + device.getName() + "---" + device.getAddress()+"---"+ device.getUuids()+""+device.getBluetoothClass().describeContents());

            //  device.getBluetoothClass().getMajorDeviceClass()
            if (device.getBluetoothClass().getDeviceClass() == PRINT_TYPE) {


                mBluetoothDevicesDatas.add(new PrintBean(device));

                for (int i = 0; i < mBluetoothDevicesDatas.size(); i++) {

                    Log.d("addBluetoothDevice", " ==service更新==" + mBluetoothDevicesDatas.size());

//                        if (device.getAddress().equals(mBluetoothDevicesDatas.get(i).getAddress())) {
//                            callback.getBluetoothDevices(mBluetoothDevicesDatas.get(i));
////                            mBluetoothDevicesDatas.remove(i);
////                            Log.d("PRINT_TYPE", " ---mBluetoothDevicesDatas");
//                        }else {
//                            callback.getBluetoothDevices(mBluetoothDevicesDatas.get(i));
//                        }
                }


                // PrintBean pb = new PrintBean(device);

                //
            }

            Log.d("addBluetoothDevice", " ====" + mBluetoothDevicesDatas.size());
        }
    }
    //	**
//			* 提供接口回调方法
//     * @param callback
//     */
    public void setCallback(Callback callback) {
        this.callback = callback;
    }


    public static interface Callback {
        /**
         * 得到实时更新的数据
         *
         * @return
         */
        void getBluetoothDevices(PrintBean pd);
    }
}
