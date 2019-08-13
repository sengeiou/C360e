package com.alfredkds.global;

import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;

import com.alfredbase.BaseActivity;
import com.alfredbase.BaseApplication;
import com.alfredbase.ParamConst;
import com.alfredbase.UnCEHandler;
import com.alfredbase.global.BugseeHelper;
import com.alfredbase.javabean.KotItemDetail;
import com.alfredbase.javabean.KotItemModifier;
import com.alfredbase.javabean.KotSummary;
import com.alfredbase.javabean.Printer;
import com.alfredbase.javabean.User;
import com.alfredbase.javabean.model.KDSDevice;
import com.alfredbase.javabean.model.MainPosInfo;
import com.alfredbase.javabean.model.SessionStatus;
import com.alfredbase.store.SQLExe;
import com.alfredbase.store.Store;
import com.alfredbase.store.sql.KotItemDetailSQL;
import com.alfredbase.store.sql.KotItemModifierSQL;
import com.alfredbase.store.sql.KotSummarySQL;
import com.alfredbase.utils.CommonUtil;
import com.alfredbase.utils.LogUtil;
import com.alfredbase.utils.RingUtil;
import com.alfredkds.R;
import com.alfredkds.activity.Welcome;
import com.alfredkds.http.server.KdsHttpServer;
import com.alfredkds.javabean.Kot;
import com.alfredkds.view.SystemSettings;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class App extends BaseApplication {

    public static final int HANDLER_RECONNECT_POS = 10;
    public static final int HANDLER_SEND_FAILURE = 11;
    public static final int HANDLER_SEND_FAILURE_SHOW = 12;
    public static final int HANDLER_REFRESH_KOT = 13;
    public static final int HANDLER_VERIFY_MAINPOS = 14;
    public static final int HANDLER_RETURN_ERROR = 15;
    public static final int HANDLER_RETURN_ERROR_SHOW = 16;
    public static final int HANDLER_KOTSUMMARY_IS_UNREAL = 17;
    public static final int HANDLER_KOT_COMPLETE_USER_FAILED = 18;
    public static final int HANDLER_KOT_ITEM_CALL = 19;

    public static final int HANDLER_KOT_CALL_NUM = 101;
    public static final int HANDLER_KOT_CALL_NUM_OLD = 100;
    public static final int HANDLER_KOT_COMPLETE_ALL = 102;
    public static final int HANDLER_KOT_COMPLETE = 103;

    public static final int HANDLER_NEW_KOT = 20;
    public static final int HANDLER_UPDATE_KOT = 8;
    public static final int HANDLER_DELETE_KOT = 2;
    private static final String DATABASE_NAME = "com.alfredkds";
    public static final int HANDLER_RELOAD_KOT = 21;
    public static App instance;
    //for pairing
    private ArrayList<String> pairingIps = new ArrayList<>();
    private User user;
    private KDSDevice kdsDevice;
    private Map<Integer, MainPosInfo> mainPosInfos = new HashMap<Integer, MainPosInfo>();
    public RingUtil ringUtil;
    private Printer printer;
    public static String TAG = App.class.getName();

    private SessionStatus sessionStatus;
    private Long businessDate;
    private SystemSettings systemSettings;
    public String VERSION = "0.0.0";

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        BugseeHelper.init(this, "856f76df-ba87-4e0a-9049-cfcd31a33a42");
        SQLExe.init(this, DATABASE_NAME, DATABASE_VERSION);

        systemSettings = new SystemSettings(this);
        update15to16();
        if (kdsDevice == null) {
            kdsDevice = new KDSDevice();
        }

        VERSION = getAppVersionName();


        KdsHttpServer kdssrv = new KdsHttpServer();
        try {
            if (!kdssrv.isAlive()) {
                kdssrv.start();
            }
        } catch (IOException e) {
            kdssrv.stop();
        }
        UnCEHandler catchExcep = new UnCEHandler(this, Welcome.class);
        Thread.setDefaultUncaughtExceptionHandler(catchExcep);
        CrashReport.initCrashReport(getApplicationContext(), "900043724", isOpenLog);
        wifiPolicyNever();
        kdsDevice = Store.getObject(this, Store.KDS_DEVICE, KDSDevice.class);
    }

    public void setRing() {
        ringUtil = new RingUtil();
        ringUtil.init(this, R.raw.desk_bell_ringing);
    }

//	protected void updateKDSNetworkInfo(String ip, String mac) {
//		
//		this.kdsDevice.setIP(ip);
//		this.kdsDevice.setMac(mac);
//		Store.saveObject(this, Store.KDS_DEVICE, this.kdsDevice);
//	}

    @Override
    protected void onIPChanged() {
        super.onIPChanged();
        if (getCurrentConnectedMainPos() == null || Store.getObject(App.instance, Store.KDS_USER, User.class) == null) {
            return;
        }
        if (getKdsDevice() != null) {
            this.kdsDevice.setIP(CommonUtil.getLocalIpAddress());
            this.setKdsDevice(kdsDevice);
            LogUtil.d(TAG, "onIPChanged: " + this.kdsDevice.toString());
            SyncCentre.getInstance().kdsIpChange(this.getApplicationContext(),
                    this.kdsDevice.toMap(), null);
        }

    }

    @Override
    protected void onNetworkConnectionUpdate() {
        super.onNetworkConnectionUpdate();
        SyncCentre.getInstance().setNetworkStatus(this.network_connected);
    }


    public User getUser() {
        if (user == null) {
            user = Store.getObject(instance, Store.KDS_USER, User.class);
        }
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public KDSDevice getKdsDevice() {
        if (kdsDevice == null) {
            kdsDevice = Store.getObject(this, Store.KDS_DEVICE, KDSDevice.class);
        }
        return kdsDevice;
    }

    public void setKdsDevice(KDSDevice kdsDevice) {
        this.kdsDevice = kdsDevice;
        Store.saveObject(this, Store.KDS_DEVICE, kdsDevice);
    }


    public SystemSettings getSystemSettings() {
        return systemSettings;
    }

    public synchronized Map<Integer, MainPosInfo> getMainPosInfos() {

        if (mainPosInfos.isEmpty()) {
            Map<Integer, MainPosInfo> mainPosInfoMap = Store.getObject(instance, Store.MAINPOSINFO_MAP, new TypeToken<Map<Integer, MainPosInfo>>() {
            }.getType());
            if (mainPosInfoMap != null) {
                mainPosInfos = mainPosInfoMap;
            }
        }
        return mainPosInfos;
    }

    public synchronized void addMainPosInfo(MainPosInfo mainPosInfo) {
        getMainPosInfos();
        this.mainPosInfos.put(mainPosInfo.getRevenueId(), mainPosInfo);
        Store.saveObject(instance, Store.MAINPOSINFO_MAP, this.mainPosInfos);
        ArrayList<Integer> posId = new ArrayList<>();
        for (Map.Entry<Integer, MainPosInfo> entry : mainPosInfos.entrySet()) {
            MainPosInfo posInfo = entry.getValue();
            posId.add(posInfo.getRevenueId());
        }
        Store.putString(this, Store.CURRENT_MAIN_POS_ID_CONNECTED, new Gson().toJson(posId));
    }

    public List<MainPosInfo> getCurrentConnectedMainPos() {
        getMainPosInfos();

        String data = Store.getString(this, Store.CURRENT_MAIN_POS_ID_CONNECTED);
        ArrayList<Integer> posId = new ArrayList<>();
        if (!TextUtils.isEmpty(data)) {
            try {
                ArrayList<Integer> list = new Gson().fromJson(data, new TypeToken<ArrayList<Integer>>() {
                }.getType());
                if (list != null) {
                    posId.addAll(list);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (posId.size() > 0) {
            List<MainPosInfo> mainPosInfos = new ArrayList<>();
            for (Integer cid : posId) {
                mainPosInfos.add(this.mainPosInfos.get(cid));
            }
            return mainPosInfos;
        } else {
            return null;
        }
    }

    public MainPosInfo getCurrentConnectedMainPosByRevenueCenterId(Integer revenueCenterId) {
        List<MainPosInfo> list = getCurrentConnectedMainPos();
        for (MainPosInfo pos : list) {
            if(pos.getRevenueId().equals(revenueCenterId)){
                return pos;
            }
        }
        return null;

    }

    public Printer getPrinter() {
        if (printer == null) {
            printer = Store.getObject(this, Store.KDS_PRINTER, Printer.class);
        }
        return printer;
    }

    public void setPrinter(Printer printer) {
        this.printer = printer;
        Store.saveObject(this, Store.KDS_PRINTER, printer);
    }

    public ArrayList<String> getPairingIp() {
        return pairingIps;
    }

    public void setPairingIp(String pairingIp) {
        if (!pairingIps.contains(pairingIp)) {
            this.pairingIps.add(pairingIp);
        }
    }

    public SessionStatus getSessionStatus() {
        if (sessionStatus == null) {
            sessionStatus = Store.getObject(instance, Store.SESSION_STATUS, SessionStatus.class);
        }
        return sessionStatus;
    }

    public void setSessionStatus(SessionStatus sessionStatus) {
        this.sessionStatus = sessionStatus;
    }

    public Long getBusinessDate() {
        if (businessDate == null) {
            businessDate = Store.getLong(this, Store.BUSINESS_DATE);
        }
        return businessDate;
    }

    public void setBusinessDate(Long businessDate) {
        this.businessDate = businessDate;
    }

    /**
     * 每次刷新KitchenOrder，从数据库取出数据
     */
    public List<Kot> getRefreshKots() {
        List<Kot> kotList = new ArrayList<Kot>();
        Kot kot = null;
        boolean flag = false;
        List<KotSummary> kotSummaries = new ArrayList<KotSummary>();
        List<KotItemDetail> kotItemDetails = new ArrayList<KotItemDetail>();
        kotSummaries = KotSummarySQL.getUndoneKotSummary();
        List<KotItemModifier> kotItemModifiers = new ArrayList<KotItemModifier>();
        for (int i = 0; i < kotSummaries.size(); i++) {
            kot = new Kot();
            kotItemDetails = KotItemDetailSQL.getKotItemDetailBySummaryIdandOrderIdForMainPage(kotSummaries.get(i).getId(),
                    kotSummaries.get(i).getOrderId());
            for (int j = 0; j < kotItemDetails.size(); j++) {
                if (kotSummaries.get(i).getStatus() == ParamConst.KOTS_STATUS_UNDONE) {
                    flag = true;
                }
                kotItemModifiers.addAll(KotItemModifierSQL.getKotItemModifiersByKotItemDetail(kotItemDetails.get(j).getId()));
            }
            if (flag) {
                kot.setKotItemDetails(kotItemDetails);
                kot.setKotItemModifiers(kotItemModifiers);
                kot.setKotSummary(kotSummaries.get(i));
                kotList.add(kot);
                flag = false;
            }
        }
        return kotList;
    }

    /**
     * KitchenOrder--Login初始化数据填充Adapter
     *
     * @return
     */
    public List<Kot> getInitKots() {
        List<Kot> kotList = new ArrayList<Kot>();
        Kot kot = null;
        boolean flag = false;
        List<KotSummary> kotSummaries = new ArrayList<KotSummary>();
        List<KotItemDetail> kotItemDetails = new ArrayList<KotItemDetail>();
        kotSummaries = KotSummarySQL.getAllKotSummary();
        List<KotItemModifier> kotItemModifiers = new ArrayList<KotItemModifier>();
        for (int i = 0; i < kotSummaries.size(); i++) {
            kot = new Kot();
            kotItemDetails = KotItemDetailSQL.getKotItemDetailBySummaryIdandOrderId(kotSummaries.get(i).getId(),
                    kotSummaries.get(i).getOrderId());
            for (int j = 0; j < kotItemDetails.size(); j++) {
                if (kotItemDetails.get(j).getKotStatus() < ParamConst.KOT_STATUS_DONE
                        || kotSummaries.get(i).getStatus() == ParamConst.KOTS_STATUS_UNDONE) {
                    flag = true;
                }
                kotItemModifiers.addAll(KotItemModifierSQL.getKotItemModifiersByKotItemDetail(kotItemDetails.get(j).getId()));
            }
            if (flag) {
                kot.setKotItemDetails(kotItemDetails);
                kot.setKotItemModifiers(kotItemModifiers);
                kot.setKotSummary(kotSummaries.get(i));
                kotList.add(kot);
                flag = false;
            }
        }
        return kotList;
    }

    /**
     * 查询出所有的kot信息
     * <p>
     * added by
     *
     * @return
     */
    public List<Kot> getAllKots() {
        List<Kot> kotList = new ArrayList<Kot>();
        Kot kot = null;
        List<KotSummary> kotSummaries = new ArrayList<KotSummary>();
        List<KotItemDetail> kotItemDetails = new ArrayList<KotItemDetail>();
        kotSummaries = KotSummarySQL.getAllKotSummary();
        List<KotItemModifier> kotItemModifiers = new ArrayList<KotItemModifier>();
        for (int i = 0; i < kotSummaries.size(); i++) {
            kot = new Kot();
            kotItemDetails = KotItemDetailSQL.getKotItemDetailBySummaryId(kotSummaries.get(i).getId());
            for (int j = 0; j < kotItemDetails.size(); j++) {
                kotItemModifiers.addAll(KotItemModifierSQL.getKotItemModifiersByKotItemDetail(kotItemDetails.get(j).getId()));
            }
            kot.setKotItemDetails(kotItemDetails);
            kot.setKotItemModifiers(kotItemModifiers);
            kot.setKotSummary(kotSummaries.get(i));
            kotList.add(kot);
        }
        return kotList;
    }

    /**
     * 数据库取数据，初始化KotHistory及refresh页面
     * <p>
     * added by
     *
     * @return
     */
    public List<Object[]> getKotHistoryData() {
        List<Object[]> kotHistoryItems = new ArrayList<Object[]>();
//		List<KotItemDetail> kotItemDetails = KotItemDetailSQL.getAllKotItemDetail();
        List<KotItemDetail> kotItemDetails = KotItemDetailSQL.getHistoryKotItemDetail();
        List<KotItemModifier> kotItemModifiers = null;
        for (int i = 0; i < kotItemDetails.size(); i++) {
            KotSummary kotSummary = KotSummarySQL.getKotSummaryById(kotItemDetails.get(i).getKotSummaryId());
            kotItemModifiers = KotItemModifierSQL.getKotItemModifiersByKotItemDetail(kotItemDetails.get(i).getId());
            String kotItem = "";
            if (kotItemModifiers.size() == 0) {
                kotItem = kotItemDetails.get(i).getItemName();
            } else {
                kotItem = kotItemDetails.get(i).getItemName() + "(";
                for (int j = 0; j < kotItemModifiers.size(); j++) {
                    kotItem += kotItemModifiers.get(j).getModifierName() + "、";
                }
                kotItem = kotItem.substring(0, kotItem.length() - 1) + ")";
            }
            Object[] obj = new Object[3];
            obj[0] = kotSummary;
            obj[1] = kotItemDetails.get(i);
            obj[2] = kotItem;
            kotHistoryItems.add(obj);
        }
        return kotHistoryItems;
    }

    /**
     * Summary ：菜名初始化
     * <p>
     * added by
     *
     * @return
     */
    public List<String> getDishNames() {
        List<String> result = new ArrayList<String>();
        List<KotItemDetail> kotItemDetails = KotItemDetailSQL.getAllKotItemDetail();
        for (int i = 0; i < kotItemDetails.size(); i++) {
            result.add(kotItemDetails.get(i).getItemName());
        }
        Set<String> kotHashSet = new HashSet<String>(result);//排序
        result.clear();
        result.addAll(kotHashSet);
        return result;
    }

    /**
     * Summary ： 每个菜有多少个及详细信息
     * <p>
     * added by XieJF, 2014-8-14
     *
     * @param dishName
     * @return
     */
    public List<Object[]> getKotDishDetail(String dishName) {
        List<Object[]> result = new ArrayList<Object[]>();
        List<KotItemDetail> kotItemDetails = KotItemDetailSQL.getKotItemDetailByDishName(dishName);
        List<KotItemModifier> kotItemModifiers = null;
        for (int i = 0; i < kotItemDetails.size(); i++) {
            kotItemModifiers = KotItemModifierSQL.getKotItemModifiersByKotItemDetail(kotItemDetails.get(i).getId());
            String kotItem = "";
            if (kotItemModifiers.size() == 0) {
                kotItem = kotItemDetails.get(i).getItemName();
            } else {
                kotItem = kotItemDetails.get(i).getItemName() + "(";
                for (int j = 0; j < kotItemModifiers.size(); j++) {
                    kotItem += kotItemModifiers.get(j).getModifierName() + "、";
                }
                kotItem = kotItem.substring(0, kotItem.length() - 1) + ")";
            }
            KotSummary kotSummary = KotSummarySQL.getKotSummaryById(kotItemDetails.get(i).getKotSummaryId());
            Object[] obj = new Object[3];
            obj[0] = kotSummary;
            obj[1] = kotItemDetails.get(i);
            obj[2] = kotItem;
            result.add(obj);
        }
        return result;
    }

    /**
     * Summary中popupWindow数据
     *
     * @param kotSummary
     * @return
     */
    public Kot getKot(KotSummary kotSummary) {
        Kot kot = new Kot();
        List<KotItemDetail> kotItemDetails = KotItemDetailSQL.getKotItemDetailBySummaryId(kotSummary.getId());
        List<KotItemModifier> kotItemModifiers = new ArrayList<KotItemModifier>();
        for (int j = 0; j < kotItemDetails.size(); j++) {
            kotItemModifiers.addAll(KotItemModifierSQL.getKotItemModifiersByKotItemDetail(kotItemDetails.get(j).getId()));
        }
        kot.setKotSummary(kotSummary);
        kot.setKotItemDetails(kotItemDetails);
        kot.setKotItemModifiers(kotItemModifiers);
        return kot;
    }

    public void reload(BaseActivity context, Handler handler) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("employee_ID", Store.getString(context, Store.EMPLOYEE_ID));
        parameters.put("password", Store.getString(context, Store.PASSWORD));
        parameters.put("type", ParamConst.USER_TYPE_KOT);
        parameters.put("device", App.instance.getKdsDevice());

        List<MainPosInfo> datas = App.instance.getCurrentConnectedMainPos();
        if (datas != null) {
            for (MainPosInfo data : datas) {
                SyncCentre.getInstance().login(context, data.getIP(), parameters, handler);
            }
        } else {
            UIHelp.showToast(context, context.getResources().getString(R.string.reconn_pos));
        }
    }

    /**
     * 判断是否使用的是商米设备  商米副屏设置是否展示
     */
    public boolean isSUNMIShow() {
        String brand = Build.BRAND;
        String model = Build.MODEL;
        String manufacturer = Build.MANUFACTURER;
        LogUtil.d(TAG, brand + "**************" + model);
        if ("SUNMI".equals(brand.toUpperCase())) {
            return true;
        }
        return false;
    }
}