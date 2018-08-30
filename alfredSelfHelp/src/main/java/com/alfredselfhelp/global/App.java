package com.alfredselfhelp.global;

import android.text.TextUtils;

import com.alfredbase.BaseApplication;
import com.alfredbase.javabean.RevenueCenter;
import com.alfredbase.javabean.User;
import com.alfredbase.javabean.model.MainPosInfo;
import com.alfredbase.javabean.model.WaiterDevice;
import com.alfredbase.store.SQLExe;
import com.alfredbase.store.Store;
import com.alfredbase.utils.LogUtil;
import com.alfredselfhelp.utils.TvPref;
import com.nordicid.nurapi.NurApi;
import com.nordicid.nurapi.NurApiListener;
import com.nordicid.nurapi.NurEventClientInfo;
import com.nordicid.nurapi.NurEventDeviceInfo;
import com.nordicid.nurapi.NurEventFrequencyHop;
import com.nordicid.nurapi.NurEventIOChange;
import com.nordicid.nurapi.NurEventInventory;
import com.nordicid.nurapi.NurEventNxpAlarm;
import com.nordicid.nurapi.NurEventProgrammingProgress;
import com.nordicid.nurapi.NurEventTraceTag;
import com.nordicid.nurapi.NurEventTriggeredRead;


public class App extends BaseApplication {
    private static final String TAG = App.class.getSimpleName();
    public static App instance;
    boolean mbPlayIMG;
    public String VERSION;
    private String posIp;
    private int mainPageType = 1;
    private MainPosInfo mainPosInfo;
    public static final int HANDLER_REFRESH_CALL=1;
    public static final int HANDLER_REFRESH_CALL_ON=2;
    private String pairingIp;
    private WaiterDevice waiterdev;
    private User user;
    private RevenueCenter revenueCenter;
    // RFID API
    private NurApi nurApi;


    private static final String DATABASE_NAME = "com.alfredselfhelp";
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        VERSION = getAppVersionName();
        SQLExe.init(this, DATABASE_NAME, DATABASE_VERSION);

        TvPref.init();
        nurApi = new NurApi();
        nurApi.setListener(new NurApiListener() {
            @Override
            public void logEvent(int i, String s) {

            }

            @Override
            public void connectedEvent() {
                LogUtil.d(TAG, "NurApi connected");
                // TODO enableItems(true);
            }

            @Override
            public void disconnectedEvent() {
                LogUtil.d(TAG, "NurApi disconnected");
                // TODO enableItems(false);
            }

            @Override
            public void bootEvent(String s) {

            }

            @Override
            public void inventoryStreamEvent(NurEventInventory nurEventInventory) {

            }

            @Override
            public void IOChangeEvent(NurEventIOChange nurEventIOChange) {

            }

            @Override
            public void traceTagEvent(NurEventTraceTag nurEventTraceTag) {

            }

            @Override
            public void triggeredReadEvent(NurEventTriggeredRead nurEventTriggeredRead) {

            }

            @Override
            public void frequencyHopEvent(NurEventFrequencyHop nurEventFrequencyHop) {

            }

            @Override
            public void debugMessageEvent(String s) {

            }

            @Override
            public void inventoryExtendedStreamEvent(NurEventInventory nurEventInventory) {

            }

            @Override
            public void programmingProgressEvent(NurEventProgrammingProgress nurEventProgrammingProgress) {

            }

            @Override
            public void deviceSearchEvent(NurEventDeviceInfo nurEventDeviceInfo) {

            }

            @Override
            public void clientConnectedEvent(NurEventClientInfo nurEventClientInfo) {

            }

            @Override
            public void clientDisconnectedEvent(NurEventClientInfo nurEventClientInfo) {

            }

            @Override
            public void nxpEasAlarmEvent(NurEventNxpAlarm nurEventNxpAlarm) {

            }
        });
        mbPlayIMG = TvPref.readPlayIMGEn();

    }




    public String getPairingIp() {
        return pairingIp;
    }

    public void setPairingIp(String pairingIp) {
        this.pairingIp = pairingIp;
    }


    public MainPosInfo getMainPosInfo() {
        if(mainPosInfo == null){
            mainPosInfo = Store.getObject(this.getApplicationContext(), Store.MAINPOSINFO, MainPosInfo.class);
        }
        return mainPosInfo;
    }

    public void setMainPosInfo(MainPosInfo mainPosInfo) {
        this.mainPosInfo = mainPosInfo;
        Store.saveObject(this.getApplicationContext(),Store.MAINPOSINFO, mainPosInfo);
    }

    public RevenueCenter getRevenueCenter() {
        if (revenueCenter == null)
            revenueCenter = Store.getObject(this,
                    Store.CURRENT_REVENUE_CENTER, RevenueCenter.class);
        return revenueCenter;
    }

    public void setRevenueCenter(RevenueCenter revenueCenter) {
        this.revenueCenter = revenueCenter;
    }
    public WaiterDevice getWaiterdev() {
        if(waiterdev == null){
            waiterdev = Store.getObject(this, Store.WAITER_DEVICE, WaiterDevice.class);
        }
        return waiterdev;
    }

    public void setWaiterdev(WaiterDevice waiterdev) {
        this.waiterdev = waiterdev;
        Store.saveObject(this, Store.WAITER_DEVICE,waiterdev);
    }


    public User getUser() {
        if (user == null) {
            user = Store.getObject(App.instance, Store.WAITER_USER, User.class);
        }
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        Store.saveObject(App.instance, Store.WAITER_USER, user);
    }

    public boolean getPlayIMGEn() {
        return mbPlayIMG;
    }


    public String getPosIp() {

        pairingIp = Store.getString(App.instance,Store.KPM_IP);

        return pairingIp;
    }

    public void setPosIp(String posIp) {


           Store.putString(App.instance, Store.KPM_IP, posIp);

    }

    public int getMainPageType() {
        return mainPageType;
    }

    public void setMainPageType(int mainPageType) {
        this.mainPageType = mainPageType;
    }
}
