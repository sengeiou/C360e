package com.alfredselfhelp.global;

import com.alfredbase.BaseApplication;
import com.alfredbase.ParamConst;
import com.alfredbase.javabean.RestaurantConfig;
import com.alfredbase.javabean.RevenueCenter;
import com.alfredbase.javabean.User;
import com.alfredbase.javabean.model.LocalRestaurantConfig;
import com.alfredbase.javabean.model.MainPosInfo;
import com.alfredbase.javabean.model.SessionStatus;
import com.alfredbase.javabean.model.WaiterDevice;
import com.alfredbase.store.SQLExe;
import com.alfredbase.store.Store;
import com.alfredbase.utils.BH;
import com.alfredbase.utils.TimeUtil;
import com.alfredselfhelp.utils.TvPref;

import java.util.List;


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

    private Long businessDate;
    private Long lastBusinessDate;
    private int indexOfRevenueCenter;

    public static boolean isleftMoved;
    private String currencySymbol = "$";
    private LocalRestaurantConfig localRestaurantConfig;

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

    public void setLocalRestaurantConfig(
            List<RestaurantConfig> restaurantConfigs) {
        this.localRestaurantConfig = getLocalRestaurantConfig();
        for (RestaurantConfig restaurantConfig : restaurantConfigs) {
            switch (restaurantConfig.getParaType().intValue()) {
                case ParamConst.SALE_SESSION_TYPE:
                    this.localRestaurantConfig
                            .setSessionConfigType(restaurantConfig);
                    break;
                case ParamConst.PRICE_TAX_INCLUSIVE:
                    this.localRestaurantConfig.setIncludedTax(restaurantConfig);
                    break;
                case ParamConst.ROUND_RULE_TYPE:
                    this.localRestaurantConfig.setRoundType(restaurantConfig);
                    break;
                case ParamConst.CURRENCY_TYPE:
                    this.localRestaurantConfig.setCurrencySymbol(restaurantConfig);
                    this.localRestaurantConfig.setCurrencySymbolType(restaurantConfig);
                    break;
                case ParamConst.DEF_DISCOUNT_TYPE:
                    this.localRestaurantConfig.setDiscountOption(restaurantConfig);
                    break;
                case ParamConst.SEND_FOOD_CARD_NUM:
                    this.localRestaurantConfig.setSendFoodCardNumList(restaurantConfig);
                    break;
                default:
                    break;
            }
        }
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

    public LocalRestaurantConfig getLocalRestaurantConfig() {
        return localRestaurantConfig;
    }

    public Long getBusinessDate() {
        return businessDate;
    }

    public void setBusinessDate(Long businessDate) {
        this.businessDate = businessDate;
    }

    public Long getLastBusinessDate() {
        if (lastBusinessDate == null) {
            lastBusinessDate = Store.getLong(this, Store.LAST_BUSINESSDATE);
        }
        // if all data is null, set today is businessDate
        if (lastBusinessDate == null
                || lastBusinessDate == Store.DEFAULT_LONG_TYPE) {
            lastBusinessDate = TimeUtil.getNewBusinessDate();
        }
        return lastBusinessDate;
    }

    public void setLastBusinessDate(Long lastBusinessDate) {
        this.lastBusinessDate = lastBusinessDate;
    }

    public int getIndexOfRevenueCenter() {
        if (revenueCenter != null) {
            indexOfRevenueCenter = revenueCenter.getId();
        }
        return indexOfRevenueCenter;
    }

    @Override
    public void onTerminate() {
        RfidApiCentre.getInstance().stopRFIDScan(null);
        super.onTerminate();
    }
}
