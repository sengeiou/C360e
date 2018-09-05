package com.alfredselfhelp.global;

import com.alfredbase.BaseApplication;
import com.alfredbase.javabean.RevenueCenter;
import com.alfredbase.javabean.User;
import com.alfredbase.javabean.model.MainPosInfo;
import com.alfredbase.javabean.model.SessionStatus;
import com.alfredbase.javabean.model.WaiterDevice;
import com.alfredbase.store.SQLExe;
import com.alfredbase.store.Store;
import com.alfredbase.utils.BH;
import com.alfredselfhelp.utils.TvPref;


public class App extends BaseApplication {
    private static final String TAG = App.class.getSimpleName();
    public static App instance;
    boolean mbPlayIMG;
    public String VERSION;
    private String posIp;
    private MainPosInfo mainPosInfo;
    public static final int HANDLER_REFRESH_CALL=1;
    public static final int HANDLER_REFRESH_CALL_ON=2;
    private String pairingIp,ip;
    private WaiterDevice waiterdev;
    private User user;
    private RevenueCenter revenueCenter;
    // RFID API
    public static boolean isleftMoved;
    private String currencySymbol = "$";

    private static final String DATABASE_NAME = "com.alfredselfhelp";
    private SessionStatus sessionStatus;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        VERSION = getAppVersionName();
        SQLExe.init(this, DATABASE_NAME, DATABASE_VERSION);
        RfidApiCentre.getInstance().initApi();
        TvPref.init();
        mbPlayIMG = TvPref.readPlayIMGEn();

    }




    public String getPairingIp() {
        pairingIp = Store.getString(App.instance,"kip");
        return pairingIp;
    }

    public void setPairingIp(String pairingIp) {

        Store.putString(App.instance, "kip", pairingIp);

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
            user = Store.getObject(App.instance, Store.KPM_USER, User.class);
        }
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        Store.saveObject(App.instance, Store.KPM_USER, user);
    }

    public boolean getPlayIMGEn() {
        return mbPlayIMG;
    }


    public String getPosIp() {

        ip = Store.getString(App.instance,Store.KPM_IP);

        return ip;
    }

    public void setPosIp(String posIp) {
           Store.putString(App.instance, Store.KPM_IP, posIp);

    }

    public SessionStatus getSessionStatus() {
        if(sessionStatus == null){
            sessionStatus = Store.getObject(instance, Store.SESSION_STATUS, SessionStatus.class);
        }
        return sessionStatus;
    }

    public void setSessionStatus(SessionStatus sessionStatus) {
        this.sessionStatus = sessionStatus;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol, boolean isDouble) {
        this.currencySymbol = currencySymbol;
        BH.initFormart(isDouble);
    }


    @Override
    public void onTerminate() {
        RfidApiCentre.getInstance().stopRFIDScan(null);
        super.onTerminate();
    }
}
