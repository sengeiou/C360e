package com.alfredposclient.global;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import com.alfred.remote.printservice.IAlfredRemotePrintService;
import com.alfred.remote.printservice.RemotePrintServiceCallback;
import com.alfredbase.BaseActivity;
import com.alfredbase.BaseApplication;
import com.alfredbase.ParamConst;
import com.alfredbase.UnCEHandler;
import com.alfredbase.global.CoreData;
import com.alfredbase.javabean.ConsumingRecords;
import com.alfredbase.javabean.ItemCategory;
import com.alfredbase.javabean.ItemDetail;
import com.alfredbase.javabean.ItemMainCategory;
import com.alfredbase.javabean.KotItemDetail;
import com.alfredbase.javabean.KotItemModifier;
import com.alfredbase.javabean.KotSummary;
import com.alfredbase.javabean.LocalDevice;
import com.alfredbase.javabean.Modifier;
import com.alfredbase.javabean.MonthlyPLUReport;
import com.alfredbase.javabean.MonthlySalesReport;
import com.alfredbase.javabean.Order;
import com.alfredbase.javabean.OrderBill;
import com.alfredbase.javabean.OrderDetail;
import com.alfredbase.javabean.OrderDetailTax;
import com.alfredbase.javabean.OrderModifier;
import com.alfredbase.javabean.Payment;
import com.alfredbase.javabean.PaymentSettlement;
import com.alfredbase.javabean.Printer;
import com.alfredbase.javabean.PrinterTitle;
import com.alfredbase.javabean.ReportDaySales;
import com.alfredbase.javabean.ReportDayTax;
import com.alfredbase.javabean.ReportHourly;
import com.alfredbase.javabean.ReportPluDayComboModifier;
import com.alfredbase.javabean.ReportPluDayItem;
import com.alfredbase.javabean.ReportPluDayModifier;
import com.alfredbase.javabean.RestaurantConfig;
import com.alfredbase.javabean.RevenueCenter;
import com.alfredbase.javabean.RoundAmount;
import com.alfredbase.javabean.TableInfo;
import com.alfredbase.javabean.Tax;
import com.alfredbase.javabean.TaxCategory;
import com.alfredbase.javabean.User;
import com.alfredbase.javabean.UserTimeSheet;
import com.alfredbase.javabean.model.AlipayPushMsgDto;
import com.alfredbase.javabean.model.KDSDevice;
import com.alfredbase.javabean.model.LocalRestaurantConfig;
import com.alfredbase.javabean.model.MainPosInfo;
import com.alfredbase.javabean.model.PrintOrderItem;
import com.alfredbase.javabean.model.PrintOrderModifier;
import com.alfredbase.javabean.model.PrintReceiptInfo;
import com.alfredbase.javabean.model.PrinterDevice;
import com.alfredbase.javabean.model.ReportEntItem;
import com.alfredbase.javabean.model.ReportSessionSales;
import com.alfredbase.javabean.model.ReportVoidItem;
import com.alfredbase.javabean.model.SessionStatus;
import com.alfredbase.javabean.model.WaiterDevice;
import com.alfredbase.javabean.temporaryforapp.AppOrder;
import com.alfredbase.javabean.temporaryforapp.AppOrderDetail;
import com.alfredbase.javabean.temporaryforapp.AppOrderDetailTax;
import com.alfredbase.javabean.temporaryforapp.AppOrderModifier;
import com.alfredbase.javabean.temporaryforapp.ReportUserOpenDrawer;
import com.alfredbase.store.SQLExe;
import com.alfredbase.store.Store;
import com.alfredbase.store.sql.CardsSettlementSQL;
import com.alfredbase.store.sql.KotItemDetailSQL;
import com.alfredbase.store.sql.KotItemModifierSQL;
import com.alfredbase.store.sql.KotSummarySQL;
import com.alfredbase.store.sql.NetsSettlementSQL;
import com.alfredbase.store.sql.OrderDetailSQL;
import com.alfredbase.store.sql.OrderDetailTaxSQL;
import com.alfredbase.store.sql.OrderModifierSQL;
import com.alfredbase.store.sql.OrderSQL;
import com.alfredbase.store.sql.PaymentSQL;
import com.alfredbase.store.sql.PaymentSettlementSQL;
import com.alfredbase.store.sql.RoundAmountSQL;
import com.alfredbase.store.sql.TableInfoSQL;
import com.alfredbase.store.sql.UserSQL;
import com.alfredbase.store.sql.UserTimeSheetSQL;
import com.alfredbase.store.sql.temporaryforapp.AppOrderSQL;
import com.alfredbase.utils.BH;
import com.alfredbase.utils.ButtonClickTimer;
import com.alfredbase.utils.CommonUtil;
import com.alfredbase.utils.DialogFactory;
import com.alfredbase.utils.IntegerUtils;
import com.alfredbase.utils.LogUtil;
import com.alfredbase.utils.ObjectFactory;
import com.alfredbase.utils.RxBus;
import com.alfredbase.utils.TimeUtil;
import com.alfredposclient.R;
import com.alfredposclient.activity.MainPage;
import com.alfredposclient.activity.NetWorkOrderActivity;
import com.alfredposclient.activity.OpenRestaruant;
import com.alfredposclient.activity.Welcome;
import com.alfredposclient.http.server.MainPosHttpServer;
import com.alfredposclient.javabean.SecondScreenBean;
import com.alfredposclient.javabean.SecondScreenTotal;
import com.alfredposclient.jobs.CloudSyncJobManager;
import com.alfredposclient.jobs.KotJobManager;
import com.alfredposclient.utils.T1SecondScreen.DataModel;
import com.alfredposclient.utils.T1SecondScreen.UPacketFactory;
import com.alfredposclient.view.ReloginDialog;
import com.alfredposclient.xmpp.XmppThread;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.moonearly.model.GoodsModel;
import com.moonearly.model.OrderModel;
import com.moonearly.utils.service.TcpSendCallBack;
import com.moonearly.utils.service.TcpUdpFactory;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.tencent.bugly.crashreport.CrashReport;
import com.zebra.scannercontrol.DCSSDKDefs;
import com.zebra.scannercontrol.DCSScannerInfo;
import com.zebra.scannercontrol.FirmwareUpdateEvent;
import com.zebra.scannercontrol.IDcsSdkApiDelegate;
import com.zebra.scannercontrol.SDKHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cn.finalteam.galleryfinal.CoreConfig;
import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.ImageLoader;
import cn.finalteam.galleryfinal.ThemeConfig;
import cn.finalteam.galleryfinal.widget.GFImageView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import sunmi.ds.DSKernel;
import sunmi.ds.callback.IConnectionCallback;
import sunmi.ds.callback.IReceiveCallback;
import sunmi.ds.callback.ISendCallback;
import sunmi.ds.callback.ISendFilesCallback;
import sunmi.ds.data.DSData;
import sunmi.ds.data.DSFile;
import sunmi.ds.data.DSFiles;
import sunmi.ds.data.DataPacket;

//import com.alfredposclient.push.PushServer;

public class App extends BaseApplication {
    private static final  String TAG = App.class.getSimpleName();
    public static App instance;
    private RevenueCenter revenueCenter;
    private MainPosInfo mainPosInfo;
    public String VERSION = "1.0.8";
    private static final String DATABASE_NAME = "com.alfredposclient";

    private String callAppIp;

    private int appOrderNum;
    /**
     * User: Current cashier logged in
     */
    private User user;
    private Map<String, User> activeUsers = new ConcurrentHashMap<String, User>();
    private Map<Integer, KDSDevice> kdsDevices = new ConcurrentHashMap<Integer, KDSDevice>();
    private Map<Integer, PrinterDevice> printerDevices = new ConcurrentHashMap<Integer, PrinterDevice>();
    private Map<Integer, WaiterDevice> waiterDevices = new ConcurrentHashMap<Integer, WaiterDevice>();


    // push message
    public Map<String, Integer> pushMsgMap = new HashMap<String, Integer>();

    public Map<Integer, AlipayPushMsgDto> alipayPush = new HashMap<Integer, AlipayPushMsgDto>();

    private List<TableInfo> notificationsOfGettingBill = Collections
            .synchronizedList(new ArrayList<TableInfo>());

    // Job Manager
    private KotJobManager kdsJobManager;

    private SessionStatus sessionStatus;
    private Long businessDate;
    private Long lastBusinessDate;

    private int indexOfRevenueCenter;
    private MainPosHttpServer httpServer;

    // system settings
    private SystemSettings systemSettings;

    // Remote Print Service
    private IAlfredRemotePrintService mRemoteService = null;

    // Sync sale data to cloud
    private CloudSyncJobManager syncJob = null;

    // 全局变量用于表示正在结账的 Order
    public Order orderInPayment = null;

    // public String roundType;

    // price include tax;
    // public int taxIncluded = 0;

//    private PushThread pushThread;

    public boolean kot_print;

    private DSKernel mDSKernel = null;

    private IConnectionCallback.ConnState connState;

    private boolean hasSecondScreen = false;

    private Map<Integer, List<PrinterDevice>> map = new HashMap<Integer, List<PrinterDevice>>();
    private XmppThread xmppThread;
    // // 动态session类型
    // private List<Integer> sessionConfigType;
    //
    // // 货币符号
    // private String currencySymbol = "$";

    private LocalRestaurantConfig localRestaurantConfig;
    /*
     * Print Service call back
     */
    private RemotePrintServiceCallback mCallback = new RemotePrintServiceCallback();


    /*
     * Remote Print Service Connection
     */
    private ServiceConnection mRemoteConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mRemoteService = null;
            autoConnectRemotePrintService();
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            mRemoteService = IAlfredRemotePrintService.Stub
                    .asInterface(service);
            try {
                mRemoteService.registerCallBack(mCallback);
                String txt = mRemoteService.getMessage();
                mRemoteService.setMessage("Alfred Main POS");
            } catch (RemoteException e1) {
                e1.printStackTrace();
            }
        }
    };

    /*
     * Alfred PUSH Service
     */
//    private RabbitMqPushService rabbitMqPushService;
//    private ServiceConnection rabbitMqPushConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            rabbitMqPushService = ((RabbitMqPushService.Binder) service).getService();
////            rabbitMqPushService.setListener(new PushListenerClient(App.instance));
//
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            rabbitMqPushService = null;
//        }
//    };

//    private PushService pushService;
//    private ServiceConnection pushConnection = new ServiceConnection() {
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            pushService = null;
//        }
//
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            pushService = ((PushService.Binder) service).getService();
//            pushService.setListener(new PushListenerClient(App.instance));
//            if (revenueCenter != null) {
//                pushService
//                        .sentMessage(PushMessage.registClient(
//                                revenueCenter.getRestaurantId(),
//                                revenueCenter.getId()));
//            }
//        }
//    };
    private IntentFilter intentFilter;

    private Observable<Object> observable;
//    private PushServer pushServer;
    private SDKHandler sdkHandler;
    private boolean  isUsbScannerLink = false;
    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        SQLExe.init(this, DATABASE_NAME, DATABASE_VERSION);

        systemSettings = new SystemSettings(this);

        kdsJobManager = new KotJobManager(this);
        httpServer = new MainPosHttpServer();

        // Init remote print service
        tryConnectRemotePrintService();
        if (getRevenueCenter() != null) {
            bindSyncService();
            migration();
        }
        VERSION = getAppVersionName();
        UnCEHandler catchExcep = new UnCEHandler(this, Welcome.class);
        Thread.setDefaultUncaughtExceptionHandler(catchExcep);
        CrashReport.initCrashReport(getApplicationContext(), "900043720", isOpenLog);
        mDSKernel = DSKernel.newInstance();
        if(mDSKernel != null){
            mDSKernel.init(instance, new IConnectionCallback() {
                @Override
                public void onDisConnect() {
                    hasSecondScreen = false;
                    LogUtil.d(TAG, "副屏连接失败");
                    if(isOpenLog)
                    UIHelp.showToast(getTopActivity(), "副屏连接失败");
                }

                @Override
                public void onConnected(ConnState state) {
                    hasSecondScreen = true;
                    connState = state;
                    LogUtil.d(TAG, "副屏连接成功,副屏状态:" + state);
                    if(getTopActivity() != null) {
                        if(isOpenLog)
                        UIHelp.showToast(getTopActivity(), "副屏连接成功,副屏状态:" + state);
                    }
                }
            });

            mDSKernel.addReceiveCallback(new IReceiveCallback() {
                @Override
                public void onReceiveData(DSData data) {}

                @Override
                public void onReceiveFile(DSFile file) {}

                @Override
                public void onReceiveFiles(DSFiles files) {}

                @Override
                public void onReceiveCMD(DSData cmd) {}
            });
        }


        observable = RxBus.getInstance().register("showRelogin");
        observable.observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Object>() {
            @Override
            public void call(Object object) {
                boolean isScreenLock = systemSettings.isScreenLock();
                if(isScreenLock) {
                    BaseActivity activity = getTopActivity();
                    if(activity != null && getIndexOfActivity(OpenRestaruant.class) != -1){
                        ReloginDialog reloginDialog = new ReloginDialog(activity);
                        reloginDialog.show();
                    }
                }
            }
        });

//        TcpUdpFactory.getServiceIp(5, new UdpSendCallBack() {
//            @Override
//            public void call(boolean isSucceed) {
//
//            }
//        });
//        TcpUdpFactory.tcpSend(5, "{}", new TcpSendCallBack() {
//            @Override
//            public void call(boolean isSucceed) {
//
//            }
//        });

//        pushThread = new PushThread();
//        pushThread.start();
//        pushServer = new PushServer();
//        checkoutVersion();

        // 设置主题
        ThemeConfig theme = new ThemeConfig.Builder().build();
        // 配置功能
        FunctionConfig functionConfig = new FunctionConfig.Builder()
                .setEnableCamera(true)
                .setEnableEdit(true)
                .setEnableCrop(true)
                .setEnableRotate(true)
                .setCropSquare(true)
                .setEnablePreview(true)
                .build();

        // 配置ImageLoader
        ImageLoader imageloader = new PicassoImageLoader();
        CoreConfig coreConfig = new CoreConfig.Builder(this, imageloader, theme)
                .setFunctionConfig(functionConfig)
                .build();
        GalleryFinal.init(coreConfig);

        if(isSUNMIShow()){
            try {
                sdkHandler = new SDKHandler(this);
                sdkHandler.dcssdkSetDelegate(iDcsSdkApiDelegate);
                initializeDcsSdk();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        xmppThread = new XmppThread();
        xmppThread.start();
        wifiPolicyNever();
        update15to16();
    }

    public XmppThread getXmppThread() {
        return xmppThread;
    }


    public Map<Integer, List<PrinterDevice>> getMap() {
        return map;
    }

    public void setMap(Map<Integer, List<PrinterDevice>> map) {
        this.map = map;
    }

    public boolean isUsbScannerLink() {
        return isUsbScannerLink;
    }

    private void initializeDcsSdk(){
        sdkHandler.dcssdkEnableAvailableScannersDetection(true);
        sdkHandler.dcssdkSetOperationalMode(DCSSDKDefs.DCSSDK_MODE.DCSSDK_OPMODE_BT_NORMAL);
        sdkHandler.dcssdkSetOperationalMode(DCSSDKDefs.DCSSDK_MODE.DCSSDK_OPMODE_SNAPI);
        int notifications_mask = 0;
        notifications_mask |= (DCSSDKDefs.DCSSDK_EVENT.DCSSDK_EVENT_SCANNER_APPEARANCE.value | DCSSDKDefs.DCSSDK_EVENT.DCSSDK_EVENT_SCANNER_DISAPPEARANCE.value);
        notifications_mask |= (DCSSDKDefs.DCSSDK_EVENT.DCSSDK_EVENT_SESSION_ESTABLISHMENT.value | DCSSDKDefs.DCSSDK_EVENT.DCSSDK_EVENT_SESSION_TERMINATION.value);
        notifications_mask |= (DCSSDKDefs.DCSSDK_EVENT.DCSSDK_EVENT_BARCODE.value);
        sdkHandler.dcssdkSubsribeForEvents(notifications_mask);
    }
    private DCSSDKDefs.DCSSDK_RESULT connect(int scannerId) {
        if (sdkHandler != null) {
            return sdkHandler.dcssdkEstablishCommunicationSession(scannerId);
        } else {
            return DCSSDKDefs.DCSSDK_RESULT.DCSSDK_RESULT_FAILURE;
        }
    }
    private IDcsSdkApiDelegate iDcsSdkApiDelegate = new IDcsSdkApiDelegate() {
        @Override
        public void dcssdkEventScannerAppeared(final DCSScannerInfo dcsScannerInfo) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    DCSSDKDefs.DCSSDK_RESULT result =connect(dcsScannerInfo.getScannerID());
                    if(result== DCSSDKDefs.DCSSDK_RESULT.DCSSDK_RESULT_SUCCESS){
//                        UIHelp.showShortToast(getTopActivity(), "Scanner linked");
                        isUsbScannerLink = true;
                    }
                    else if(result== DCSSDKDefs.DCSSDK_RESULT.DCSSDK_RESULT_FAILURE){
//                        Log.e(TAG, "连接失败");
                        isUsbScannerLink = false;
                    }
                }
            }).start();
        }

        @Override
        public void dcssdkEventScannerDisappeared(int i) {
            isUsbScannerLink = false;
        }

        @Override
        public void dcssdkEventCommunicationSessionEstablished(DCSScannerInfo dcsScannerInfo) {

        }

        @Override
        public void dcssdkEventCommunicationSessionTerminated(int i) {

        }

        @Override
        public void dcssdkEventBarcode(byte[] bytes, int i, int i1) {
//            handleDecode(new Result(new String(bytes), bytes, null, null));
            RxBus.getInstance().post(RxBus.RX_MSG_2, new String(bytes));
        }

        @Override
        public void dcssdkEventImage(byte[] bytes, int i) {

        }

        @Override
        public void dcssdkEventVideo(byte[] bytes, int i) {

        }

        @Override
        public void dcssdkEventFirmwareUpdate(FirmwareUpdateEvent firmwareUpdateEvent) {

        }
    };

//    public PushServer getPushServer(){
//        return  pushServer;
//    }
    @Override
    public void onTerminate() {
        if(observable != null){
            RxBus.getInstance().unregister("showRelogin", observable);
        }
        TcpUdpFactory.stopUdpServer();
        super.onTerminate();
    }

    // for APP data migration
    private void migration() {
        if (DATABASE_VERSION <= 2)
            syncJob.syncAllUnsentMsg_migration();
    }

    public DSKernel getmDSKernel() {
        return mDSKernel;
    }

    public IConnectionCallback.ConnState getConnState() {
        return connState;
    }

    public boolean isHasSecondScreen() {
        return hasSecondScreen;
    }

    public void  showWelcomeToSecondScreen(){
        if (App.instance.isHasSecondScreen() && App.instance.getConnState() == IConnectionCallback.ConnState.VICE_APP_CONN) {
            long id = Store.getLong(App.instance, Store.WELCOME_IMAGE_ID);
            if(id != Store.DEFAULT_LONG_TYPE){
                showImg(id);
            }else{
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String path = Environment
                                .getExternalStorageDirectory().getAbsolutePath() + "/welcome.png";
                        File file = new File(path);
                        if (!file.exists()) {
    //                        UIHelp.showToast(getTopActivity(), "文件不存在");
                            boolean isOK = copyApkFromAssets(instance, "sunmiImage/welcome.png", path);
    //                        if(!isOK){
    //                            UIHelp.showToast(getTopActivity(), "文件拷贝失败");
    //                            return;
    //                        }else{
    //                            UIHelp.showToast(getTopActivity(), "文件拷贝成功" + (new File(path)).exists());
    //                        }
                        }
                        sendImageToSecondScreen(path, new ISendCallback() {
                            @Override
                            public void onSendSuccess(long taskId) {
                                Store.putLong(App.instance, Store.WELCOME_IMAGE_ID, taskId);
                                showImg(taskId);
                            }

                            @Override
                            public void onSendFail(int errorId, String errorInfo) {

                            }

                            @Override
                            public void onSendProcess(long totle, long sended) {

                            }
                        });
                    }
                }).start();

            }
        }else{
            sendImageToSecondScreenByMoonearly();
        }
    }

    private void sendImageToSecondScreenByMoonearly(){
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("type", 0);
        Gson gson = new Gson();
        String json = gson.toJson(map);
        TcpUdpFactory.tcpSend(5, json, new TcpSendCallBack() {
            @Override
            public void call(boolean isSucceed) {
                if(!isSucceed){
                    LogUtil.d(TAG, "请确认 副屏已经连接上了");
                }
            }
        });
    }

    private  void sendImageToSecondScreen(String path, ISendCallback iSendCallback){
        if (App.instance.isHasSecondScreen()) {
            if (App.instance.getConnState() == IConnectionCallback.ConnState.VICE_APP_CONN) {
                mDSKernel.sendFile(DSKernel.getDSDPackageName(), path, iSendCallback);
            }
        }
    }

    private void showImg(long fileId){
        if (App.instance.isHasSecondScreen()) {
            if (App.instance.getConnState() == IConnectionCallback.ConnState.VICE_APP_CONN) {
                String json = UPacketFactory.createJson(DataModel.SHOW_IMG_WELCOME, "def");
                mDSKernel.sendCMD(DSKernel.getDSDPackageName(), json, fileId, null);//该命令会让副屏显示图片
            }
        }
    }

    public void sendDataToSecondScreen(Order order, List<OrderDetail> orderDetails){

        if(orderDetails != null  && order != null && orderDetails.size() > 0) {
            if (App.instance.isHasSecondScreen() && App.instance.getConnState() == IConnectionCallback.ConnState.VICE_APP_CONN) {

                sendViceScreenData(order, orderDetails);


            }else{
                GoodsModel goodsModelTitle = new GoodsModel();
                goodsModelTitle.setIndex(getResources().getString(R.string.index));
                goodsModelTitle.setName(getResources().getString(R.string.name));
                goodsModelTitle.setPrice(App.instance.getResources().getString(R.string.price));
                goodsModelTitle.setQty(App.instance.getResources().getString(R.string.qty));
                goodsModelTitle.setTotal(App.instance.getResources().getString(R.string.total));

                List<GoodsModel> goodsModels = new ArrayList<GoodsModel>();
                for (int i = 0; i < orderDetails.size(); i++) {
                    OrderDetail orderDetail = orderDetails.get(i);
                    GoodsModel goodsModel = new GoodsModel();
                    goodsModel.setIndex((orderDetails.size() - i)+"");
                    goodsModel.setName(orderDetail.getItemName());
                    goodsModel.setPrice(getLocalRestaurantConfig().getCurrencySymbol() + BH.getBD(orderDetail.getItemPrice()).toString());
                    goodsModel.setQty(orderDetail.getItemNum() + "");
                    goodsModel.setTotal(getLocalRestaurantConfig().getCurrencySymbol() + BH.getBD(orderDetail.getRealPrice()).toString());
                    goodsModels.add(goodsModel);
                }

                OrderModel orderModel = new OrderModel();
                orderModel.setRestaurantName("Welcome to " + CoreData.getInstance().getRestaurant().getRestaurantName());
                orderModel.setSubTotal(App.instance.getResources().getString(R.string.sub_total) + ":" + getLocalRestaurantConfig().getCurrencySymbol() + BH.getBD(order.getSubTotal()).toString());
                orderModel.setDiscount(App.instance.getResources().getString(R.string.discount) + ":" + getLocalRestaurantConfig().getCurrencySymbol() + BH.getBD(order.getDiscountAmount()).toString());
                orderModel.setTaxes(App.instance.getResources().getString(R.string.taxes) + ":" + getLocalRestaurantConfig().getCurrencySymbol() + BH.getBD(order.getTaxAmount()).toString());
                orderModel.setGrandTotal(App.instance.getResources().getString(R.string.grand_total) + ":" + getLocalRestaurantConfig().getCurrencySymbol() + BH.getBD(order.getTotal()).toString());
                orderModel.setGoodsModel(goodsModelTitle);
                orderModel.setGoodsModelList(goodsModels);
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("type", 1);
                map.put("orderModel",orderModel);
                TcpUdpFactory.tcpSend(5, new Gson().toJson(map), new TcpSendCallBack() {
                    @Override
                    public void call(boolean isSucceed) {
                        if(!isSucceed){
                            LogUtil.d(TAG, "请确认 副屏已经连接上了");
                        }
                    }
                });
            }
        }else{
            if (App.instance.isHasSecondScreen() && App.instance.getConnState() == IConnectionCallback.ConnState.VICE_APP_CONN) {
                    if (isDebug)
                        UIHelp.showToast(App.getTopActivity(), "准备发送数据:" + App.instance.getConnState());
                    showWelcomeToSecondScreen();
                }else{
                sendImageToSecondScreenByMoonearly();
            }
        }
    }

    /**
     * 商米 副屏发送数据
     * @param order
     * @param orderDetails
     */
    public void sendViceScreenData(Order order, List<OrderDetail> orderDetails){
        int styleType = Store.getInt(App.instance, Store.SUNMI_STYLE);
        if(isOpenLog) {
            UIHelp.showShortToast(getTopActivity(), styleType + "");
        }
        if (styleType == Store.SUNMI_TEXT){
            showBigScreenData(order, orderDetails);
        }else {
            List<String> imgPath = Store.getStrListValue(App.instance, Store.SUNMI_DATA);
            if(isOpenLog)
                UIHelp.showShortToast(getTopActivity(), imgPath.toString());
            if (imgPath != null && imgPath.size() != 0) {
                if (styleType == Store.SUNMI_IMG) {
                    showSunmiImg(imgPath);
                } else if (styleType == Store.SUNMI_IMG_TEXT) {
                    showBigScreenImgText(order, orderDetails, imgPath);
                } else if (styleType == Store.SUNMI_VIDEO_TEXT) {
                    showBigScreenVideoText(order, orderDetails, imgPath);
                } else if (styleType == Store.SUNMI_VIDEO) {
                    showBigScreenVideo(imgPath);
                }
            }
        }
//        if (isSmallOrBigScreen()){

//    }else {
//        if (styleType == Store.SUNMI_IMG){
//            showSunmiImg(imgPath);
//        }else if (styleType == Store.SUNMI_TEXT){
//            showSunmiText("Total Price:", order.getSubTotal());
//        }else if (styleType == Store.SUNMI_IMG_TEXT){
//            showSunmiTextAndImg("Total Price:", order.getSubTotal(), imgPath);
//        }
//    }
}

    /**
     * 商米 全屏显示视频(14寸屏)
     * @param path
     */
    private void showBigScreenVideo(List<String> path){
        mDSKernel.sendFile(DSKernel.getDSDPackageName(), path.get(0), new ISendCallback() {
            @Override
            public void onSendSuccess(long taskId) {
                String json = UPacketFactory.createJson(DataModel.VIDEO, "");
                mDSKernel.sendCMD(DSKernel.getDSDPackageName(), json, taskId, null);
            }
            @Override
            public void onSendFail(int errorId, String errorInfo) {
            }
            @Override
            public void onSendProcess(long totle, long sended) {
            }
        });
    }

    /**
     * 商米 屏幕左边显示视频，右边显示复杂的表格数据(14寸屏)
     * @param order
     * @param orderDetails
     * @param path
     */
    private void showBigScreenVideoText(final Order order, final List<OrderDetail> orderDetails, List<String> path){
        mDSKernel.sendFile(DSKernel.getDSDPackageName(), path.get(0), new ISendCallback() {
            @Override
            public void onSendSuccess(long taskId) {
                String jsonStr = UPacketFactory.createJson(DataModel.SHOW_VIDEO_LIST, getSendData(order, orderDetails));
                mDSKernel.sendCMD(DSKernel.getDSDPackageName(), jsonStr, taskId,null);
            }
            @Override
            public void onSendFail(int errorId, String errorInfo) {
            }
            @Override
            public void onSendProcess(long totle, long sended) {
            }
        });
    }

    /**
     * 商米 .屏幕左边显示幻灯片或者图片，右边显示复杂的表格数据(14寸屏)
     * @param order
     * @param orderDetails
     * @param path
     */
    private void showBigScreenImgText(final Order order, final List<OrderDetail> orderDetails, List<String> path){
        if (path.size() == 1){
            mDSKernel.sendFile(DSKernel.getDSDPackageName(), path.get(0), new ISendCallback() {
                public void onSendSuccess(long fileId) {
                    String jsonStr = UPacketFactory.createJson(DataModel.SHOW_IMG_LIST, getSendData(order, orderDetails));
                    //第一个参数DataModel.SHOW_IMG_LIST为显示布局模式，jsonStr为要显示的内容字符
                    mDSKernel.sendCMD(DSKernel.getDSDPackageName(), jsonStr, fileId,null);
                }
                public void onSendFail(int errorId, String errorInfo) {}
                public void onSendProcess(long total, long sended) {}
            });
        }else if (path.size() > 1){
            mDSKernel.sendFiles(DSKernel.getDSDPackageName(), "", path, new ISendFilesCallback() {
                @Override
                public void onAllSendSuccess(long fileid) {
                    String jsonStr= UPacketFactory.createJson(DataModel.SHOW_IMGS_LIST, getSendData(order, orderDetails));
                    mDSKernel.sendCMD(DSKernel.getDSDPackageName(), jsonStr, fileid,null);
                }
                @Override
                public void onSendSuccess(String path, long taskId) {
                }
                @Override
                public void onSendFaile(int errorId, String errorInfo) {
                }
                @Override
                public void onSendFileFaile(String path, int errorId, String errorInfo) {
                }
                @Override
                public void onSendProcess(String path, long totle, long sended) {
                }
            });
        }
    }

    /**
     * 商米 全屏只显示复杂的表格字符数据(14寸屏)
     * @param order
     * @param orderDetails
     */
    private void showBigScreenData(Order order, List<OrderDetail> orderDetails){

        final String jsonStr = getSendData(order, orderDetails);

        DataPacket dsPacket = UPacketFactory.buildShowText(DSKernel.getDSDPackageName(), jsonStr, new ISendCallback() {
            @Override
            public void onSendSuccess(long taskId) {
                if(isOpenLog)
                    UIHelp.showToast(App.getTopActivity(), "发送数据:成功");
            }
            @Override
            public void onSendFail(int errorId, String errorInfo) {
                if(isOpenLog)
                    UIHelp.showToast(App.getTopActivity(), "发送数据:失败,\n失败信息" + errorInfo);
            }
            @Override
            public void onSendProcess(long totle, long sended) {
                if(isOpenLog)
                    UIHelp.showToast(App.getTopActivity(), "发送数据:中"+jsonStr);
            }
        });
        App.instance.getmDSKernel().sendData(dsPacket);
    }

    /**
     * 组合要发送到商米副屏的表格数据
     * @param order
     * @param orderDetails
     * @return
     */
    private String getSendData(Order order, List<OrderDetail> orderDetails){
        String title = "Welcome to " + CoreData.getInstance().getRestaurant().getRestaurantName();
        SecondScreenBean secondScreenDataHead = new SecondScreenBean();
        List<SecondScreenBean> secondScreenBeans = new ArrayList<SecondScreenBean>();
        for (int i = 0; i < orderDetails.size(); i++) {
            OrderDetail orderDetail = orderDetails.get(i);
            secondScreenBeans.add(
                    new SecondScreenBean(
                            (orderDetails.size() - 1)+"",
                            orderDetail.getItemName(),
                            getLocalRestaurantConfig().getCurrencySymbol() + BH.getBD(orderDetail.getItemPrice()).toString(),
                            orderDetail.getItemNum() + "",
                            getLocalRestaurantConfig().getCurrencySymbol() + BH.getBD(orderDetail.getRealPrice()).toString()));
        }
        List<SecondScreenTotal> secondScreenTotals = new ArrayList<SecondScreenTotal>();
        secondScreenTotals.add(new SecondScreenTotal(App.instance.getResources().getString(R.string.sub_total), getLocalRestaurantConfig().getCurrencySymbol() + BH.getBD(order.getSubTotal()).toString()));
        secondScreenTotals.add(new SecondScreenTotal(App.instance.getResources().getString(R.string.discount), getLocalRestaurantConfig().getCurrencySymbol() + BH.getBD(order.getDiscountAmount()).toString()));
        secondScreenTotals.add(new SecondScreenTotal(App.instance.getResources().getString(R.string.taxes), getLocalRestaurantConfig().getCurrencySymbol() + BH.getBD(order.getTaxAmount()).toString()));
        secondScreenTotals.add(new SecondScreenTotal(App.instance.getResources().getString(R.string.grand_total), getLocalRestaurantConfig().getCurrencySymbol() + BH.getBD(order.getTotal()).toString()));

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("KVPList", secondScreenTotals);
        map.put("list", secondScreenBeans);
        map.put("head", secondScreenDataHead);
        map.put("title", title);
        Gson gson = new Gson();
        final String jsonStr = gson.toJson(map);
        return jsonStr;
    }

    /**
     * 商米 全屏显示幻灯片或者图片
     * @param list
     */
    private void showSunmiImg(List<String> list){
        if (list.size() == 1){
            mDSKernel.sendFile(DSKernel.getDSDPackageName(), list.get(0), new ISendCallback() {
                @Override
                public void onSendSuccess(long taskId) {
                    String json = UPacketFactory.createJson(DataModel.SHOW_IMG_WELCOME, "def");
                    mDSKernel.sendCMD(DSKernel.getDSDPackageName(), json, taskId, null);//该命令会让副屏显示图片
                }
                @Override
                public void onSendFail(int errorId, String errorInfo) {
                    if(isOpenLog) {
                        UIHelp.showShortToast(getTopActivity(), "发送数据失败：" + errorInfo);
                    }
                }
                @Override
                public void onSendProcess(long totle, long sended) {
                }
            });
        }else if (list.size() > 1){
            JSONObject object = new JSONObject();
            try {
                object.put("rotation_time",3000); //幻灯片的切换时间，用毫秒计算，如果不传默认是10000毫秒
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mDSKernel.sendFiles(DSKernel.getDSDPackageName(), object.toString(), list, new ISendFilesCallback() {
                @Override
                public void onAllSendSuccess(long fileid) {
                    String json = UPacketFactory.createJson(DataModel.IMAGES, "def");
                    mDSKernel.sendCMD(DSKernel.getDSDPackageName(), json, fileid, null);//该命令会让副屏显示图片
                }
                @Override
                public void onSendSuccess(String path, long taskId) {
                }
                @Override
                public void onSendFaile(int errorId, String errorInfo) {
                    if(isOpenLog) {
                        UIHelp.showShortToast(getTopActivity(), "发送数据失败：" + errorInfo);
                    }
                }
                @Override
                public void onSendFileFaile(String path, int errorId, String errorInfo) {
                    if(isOpenLog) {
                        UIHelp.showShortToast(getTopActivity(), path + "发送失败：" + errorInfo);
                    }
                }
                @Override
                public void onSendProcess(String path, long totle, long sended) {
                }
            });
        }
    }

    /**
     * 商米 全屏只显示简单文字(7寸屏)
     * @param title
     * @param content
     */
    private void showSunmiText(String title, String content){
        try {
            JSONObject json = new JSONObject();
            json.put("title", title);//title为上面一行的标题内容
            json.put("content", content);//content为下面一行的内容
            String jsonStr = json.toString();
            //构建DataPacket类
            DataPacket packet = UPacketFactory.buildShowText(
                    DSKernel.getDSDPackageName(), jsonStr, new ISendCallback() {
                        @Override
                        public void onSendSuccess(long taskId) {
                        }
                        @Override
                        public void onSendFail(int errorId, String errorInfo) {
                            if(isOpenLog) {
                                UIHelp.showShortToast(getTopActivity(), "发送数据失败：" + errorInfo);
                            }
                        }
                        @Override
                        public void onSendProcess(long totle, long sended) {
                        }
                    });
            mDSKernel.sendData(packet);//调用sendData方法发送文本
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 商米 在屏幕部分区域显示图片+文字(7寸屏)
     * @param title
     * @param content
     * @param list
     */
    private void showSunmiTextAndImg(String title, String content, List<String> list){
        try {
            JSONObject json = new JSONObject();
            json.put("title", title);//title为上面一行的标题内容
            json.put("content", content);//content为下面一行的内容
            String jsonStr = json.toString();
            if (list.size() == 1){
                mDSKernel.sendFile(DSKernel.getDSDPackageName(), jsonStr, list.get(0), new ISendCallback() {
                    @Override
                    public void onSendSuccess(long taskId) {
                        String json = UPacketFactory.createJson(DataModel.IMAGE, "def");
                        mDSKernel.sendCMD(DSKernel.getDSDPackageName(), json, taskId, null);//该命令会让副屏显示图片
                    }
                    @Override
                    public void onSendFail(int errorId, String errorInfo) {
                        if(isOpenLog) {
                            UIHelp.showShortToast(getTopActivity(), "发送数据失败：" + errorInfo);
                        }
                    }
                    @Override
                    public void onSendProcess(long totle, long sended) {
                    }
                });
            }else if (list.size() > 1){
                mDSKernel.sendFiles(DSKernel.getDSDPackageName(), jsonStr, list, new ISendFilesCallback() {
                    @Override
                    public void onAllSendSuccess(long fileid) {
                        String json = UPacketFactory.createJson(DataModel.IMAGES, "def");
                        mDSKernel.sendCMD(DSKernel.getDSDPackageName(), json, fileid, null);//该命令会让副屏显示图片
                    }
                    @Override
                    public void onSendSuccess(String path, long taskId) {
                    }
                    @Override
                    public void onSendFaile(int errorId, String errorInfo) {
                        if(isOpenLog) {
                            UIHelp.showShortToast(getTopActivity(), "发送数据失败：" + errorInfo);
                        }
                    }
                    @Override
                    public void onSendFileFaile(String path, int errorId, String errorInfo) {
                        if(isOpenLog) {
                            UIHelp.showShortToast(getTopActivity(), path + "发送失败：" + errorInfo);
                        }
                    }
                    @Override
                    public void onSendProcess(String path, long totle, long sended) {
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onIPChanged() {
        super.onIPChanged();
        if (this.getMainPosInfo() != null) {
            this.mainPosInfo.setIP(CommonUtil.getLocalIpAddress());
            this.setMainPosInfo(mainPosInfo);
            Store.saveObject(getBaseContext(), Store.MAINPOSINFO, mainPosInfo);
        }
    }

    @Override
    protected void onNetworkConnectionUpdate() {
        super.onNetworkConnectionUpdate();
    }

    /*
     * Generate Printer and KDS device from DB after the first setup
     */
    public void initKdsAndPrinters() {
        List<LocalDevice> devices = CoreData.getInstance().getLocalDevices();

        for (LocalDevice item : devices) {
            int devid = item.getDeviceId();
            String ip = item.getIp();
            String mac = item.getMacAddress();
            String name = item.getDeviceName();
            String model = item.getDeviceMode();
            String printerName = item.getPrinterName();
            int type = item.getDeviceType();

            // load physical printer
            if (type == ParamConst.DEVICE_TYPE_PRINTER) {
                PrinterDevice pdev = new PrinterDevice();
                pdev.setDevice_id(devid);
                pdev.setIP(ip);
                pdev.setMac(mac);
                pdev.setName(name);
                pdev.setModel(model);
                pdev.setPrinterName(printerName);
                pdev.setIsCahierPrinter(CoreData.getInstance()
                        .isCashierPrinter(devid));
                printerDevices.put(devid, pdev);
            } else if (type == ParamConst.DEVICE_TYPE_KDS) {
                KDSDevice kdev = new KDSDevice();
                kdev.setDevice_id(devid);
                kdev.setIP(ip);
                kdev.setMac(mac);
                kdev.setName(name);
                kdsDevices.put(devid, kdev);
            } else if (type == ParamConst.DEVICE_TYPE_WAITER) {
                WaiterDevice wdev = new WaiterDevice();
                wdev.setIP(ip);
                wdev.setMac(mac);
                wdev.setWaiterId(devid);
                waiterDevices.put(devid, wdev);
            }
        }
    }

    public void loadPrinters() {
        List<LocalDevice> devices = CoreData.getInstance().getLocalDevices();
        for (LocalDevice item : devices) {
            int type = item.getDeviceType();
            // load physical printer
            if (type == ParamConst.DEVICE_TYPE_PRINTER) {
                int devid = item.getDeviceId();
                String ip = item.getIp();
                String mac = item.getMacAddress();
                String name = item.getDeviceName();
                String model = item.getDeviceMode();
                String printerName = item.getPrinterName();
                PrinterDevice pdev = new PrinterDevice();
                pdev.setDevice_id(devid);
                pdev.setIP(ip);
                pdev.setMac(mac);
                pdev.setName(name);
                pdev.setModel(model);
                pdev.setPrinterName(printerName);
                pdev.setIsCahierPrinter(CoreData.getInstance()
                        .isCashierPrinter(devid));
                printerDevices.put(devid, pdev);
            }
        }
    }

    /* HTTP Server */
    public void startHttpServer() {
        try {
            if (!httpServer.isAlive())
                this.httpServer.start();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }
    }

//    public void startPushServer(String key){
//        try {
//            if(!pushServer.isAlive()){
//                this.pushServer.start(key);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public RevenueCenter getRevenueCenter() {
        if (revenueCenter == null)
            revenueCenter = Store.getObject(this, Store.CURRENT_REVENUE_CENTER,
                    RevenueCenter.class);
        return revenueCenter;
    }

    public void setRevenueCenter(RevenueCenter revenueCenter) {
        this.revenueCenter = revenueCenter;
    }

    public String getCallAppIp() {
        if(callAppIp == null)
            callAppIp = Store.getString(this, Store.CALL_APP_IP);
        return callAppIp;
    }

    public void setCallAppIp(String callAppIp) {
        this.callAppIp = callAppIp;
        Store.putString(this, Store.CALL_APP_IP, callAppIp);
    }

    /* Get all users currently connected to POS */
    public void addActiveUser(String userKey, User user) {
        activeUsers.put(userKey, user);
        Store.saveObject(this, Store.USER_AND_KEY, activeUsers);
    }

    public Map<String, User> getActiveUser() {
        if (activeUsers.isEmpty()) {
            if (Store.getObject(this, Store.USER_AND_KEY,
                    new TypeToken<Map<String, User>>() {
                    }.getType()) != null) {
                activeUsers = Store.getObject(this, Store.USER_AND_KEY,
                        new TypeToken<ConcurrentHashMap<String, User>>() {
                        }.getType());
            }

        }
        return activeUsers;
    }

    public User getUserByKey(String userKey) {
        return getActiveUser().get(userKey);
    }

    public void addKDSDevice(int deviceid, KDSDevice device) {
        this.kdsDevices.put(deviceid, device);
    }

    public void removeKDSDevice(int deviceid) {
        this.kdsDevices.remove(deviceid);
    }

    public KDSDevice getKDSDevice(int deviceid) {
        return this.kdsDevices.get(deviceid);
    }

    public Map<Integer, KDSDevice> getKDSDevices() {
        return this.kdsDevices;
    }

    public User getUser() {
        if (user == null) {
            user = Store.getObject(instance, Store.MAINPOS_USER, User.class);
        }
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        Store.saveObject(instance, Store.MAINPOS_USER, user);
    }

    public SessionStatus getSessionStatus() {
        return sessionStatus;
    }

    public void setSessionStatus(SessionStatus sessionStatus) {
        this.sessionStatus = sessionStatus;
    }

    public Long getBusinessDate() {
        if (businessDate == null) {
            businessDate = (Long) Store.getLong(this, Store.BUSINESS_DATE);
        }
        // if all data is null, set today is businessDate
        if (businessDate == Store.DEFAULT_LONG_TYPE) {
            businessDate = TimeUtil.getNewBusinessDate();
        }
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

    public void setIndexOfRevenueCenter(int indexOfRevenueCenter) {
        this.indexOfRevenueCenter = indexOfRevenueCenter;
    }

    public KotJobManager getKdsJobManager() {
        return this.kdsJobManager;
    }

    public Map<Integer, WaiterDevice> getWaiterDevices() {
        return waiterDevices;
    }

    public void setWaiterDevices(Map<Integer, WaiterDevice> waiterDevices) {
        this.waiterDevices = waiterDevices;
    }

    public void addWaiterDevice(WaiterDevice waiterDevice) {
        this.waiterDevices.put(waiterDevice.getWaiterId(), waiterDevice);

        // timesheet: Login
        boolean login = false;
        ArrayList<UserTimeSheet> mUserTimeSheets = UserTimeSheetSQL
                .getUserTimeSheetsByEmpId(waiterDevice.getWaiterId(),
                        App.instance.getLastBusinessDate());
        User user = UserSQL.getUserById(waiterDevice.getWaiterId());
        for (UserTimeSheet userTimeSheet : mUserTimeSheets) {
            if (userTimeSheet != null
                    && userTimeSheet.getStatus() == ParamConst.USERTIMESHEET_STATUS_LOGIN) {
                login = true;
            }
        }
        if (!login) {
            UserTimeSheet userTimeSheet = ObjectFactory.getInstance()
                    .getUserTimeSheet(App.instance.getLastBusinessDate(), user,
                            App.instance.getRevenueCenter());
            UserTimeSheetSQL.addUser(userTimeSheet);
        }
    }

    public void removeWaiterDevice(WaiterDevice waiterDevice) {
        this.waiterDevices.remove(waiterDevice.getWaiterId());
        boolean login = true;

        UserTimeSheet userTimeSheet = UserTimeSheetSQL.getUserTimeSheetByEmpId(
                waiterDevice.getWaiterId(), App.instance.getLastBusinessDate());
        if (userTimeSheet == null) {
            login = false;
        }
        if (login) {
            userTimeSheet.setLogoutTime(System.currentTimeMillis());
            userTimeSheet.setStatus(ParamConst.USERTIMESHEET_STATUS_LOGOUT);
            UserTimeSheetSQL.addUser(userTimeSheet);
        }
    }

    public boolean isWaiterLoginAllowed(WaiterDevice dev) {
        boolean ret = false;
        WaiterDevice waiterDevice = this.waiterDevices.get(dev.getWaiterId());
        if (waiterDevice != null)
            if (waiterDevice.getMac().equals(dev.getMac()))
                ret = true;
            else
                ret = false;
        else
            ret = true;
        return ret;
    }

    public PrinterDevice getPrinterDeviceById(int deviceId) {
        return printerDevices.get(deviceId);
    }

    public void setPrinterDevice(Integer deviceid, PrinterDevice device) {
        this.printerDevices.put(deviceid, device);
    }

    public void removePrinterDevice(Integer deviceid) {
        for (Iterator<Map.Entry<Integer, PrinterDevice>> it = printerDevices
                .entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Integer, PrinterDevice> entry = it.next();
            if (entry.getKey().intValue() == deviceid.intValue()) {
                it.remove();
            }
        }
    }

    public Map<Integer, PrinterDevice> getPrinterDevices() {
        return this.printerDevices;
    }

    public PrinterDevice getCahierPrinter() {
        // PrinterDevice dummy = new PrinterDevice();
        // dummy.setIP("192.168.0.11");
        // return dummy;

        for (Map.Entry<Integer, PrinterDevice> dev : printerDevices.entrySet()) {
            Integer key = dev.getKey();
            PrinterDevice devPrinter = dev.getValue();
            if (devPrinter.getIsCahierPrinter() > 0)
                return devPrinter;
        }
        return null;
    }

    /**
     * @return the mainPosInfo
     */
    public MainPosInfo getMainPosInfo() {
        return mainPosInfo;
    }

    public void setMainPosInfo(MainPosInfo mainPosInfo) {
        this.mainPosInfo = mainPosInfo;
    }

    /* Print service */
    public void connectRemotePrintService() {
        if (mRemoteService == null) {
            Intent bindIntent = new Intent(
                    "alfred.intent.action.bindPrintService");
            bindIntent.putExtra("PRINTERKEY", "fxxxkprinting");
            bindIntent.setClassName("com.alfred.remote.printservice",
                    "com.alfred.remote.printservice.PrintService");

            bindService(bindIntent, mRemoteConnection, Context.BIND_AUTO_CREATE);
        }
    }

    public void reconnectRemotePrintService() {
        Intent bindIntent = new Intent("alfred.intent.action.bindPrintService");
        bindIntent.putExtra("PRINTERKEY", "fxxxkprinting");
        bindIntent.setClassName("com.alfred.remote.printservice",
                "com.alfred.remote.printservice.PrintService");

        this.bindService(bindIntent, mRemoteConnection,
                Context.BIND_AUTO_CREATE);
    }

    public void disconnectRemotePrintService() {
        if (mRemoteConnection != null)
            unbindService(mRemoteConnection);
    }

    public boolean isKotPrint() {
        return kot_print = Store.getBoolean(instance, Store.KOT_PRINT, true);
    }

    public boolean remoteKotPrint(PrinterDevice printer, KotSummary kotsummary,
                                  ArrayList<KotItemDetail> itemDetailsList,
                                  ArrayList<KotItemModifier> modifiersList,
                                  boolean isFire) {
        if (!isKotPrint()) {
            return true;
        }
        if (printer == null) {
            return false;
        }
        if (mRemoteService == null) {
            printerDialog();
            return false;
        }
        try {
            Gson gson = new Gson();
            String printstr = gson.toJson(printer);
            String kotsumStr = gson.toJson(kotsummary);
            String kdlstr = gson.toJson(itemDetailsList);
            String modstr = gson.toJson(modifiersList);
            if (isRevenueKiosk()) {
                if (countryCode == ParamConst.CHINA)
                    mRemoteService.printKioskKOT(printstr, kotsumStr, kdlstr,
                            modstr, this.systemSettings.isKotPrintTogether(),
                            this.systemSettings.isKotDoublePrint(), getPrintOrderNo(kotsummary.getOrderId().intValue()), 2);
                else
                    mRemoteService.printKioskKOT(printstr, kotsumStr, kdlstr,
                            modstr, this.systemSettings.isKotPrintTogether(),
                            this.systemSettings.isKotDoublePrint(), null, 3);
            } else {
                int size = 2;
                if (countryCode == ParamConst.CHINA)
                    size = 2;
                mRemoteService.printKOT(printstr, kotsumStr, kdlstr, modstr,
                        this.systemSettings.isKotPrintTogether(),
                        this.systemSettings.isKotDoublePrint(), size, isFire);
            }
            return true;
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean remoteOrderSummaryPrint(PrinterDevice printer, KotSummary kotsummary,
                                           ArrayList<KotItemDetail> itemDetailsList,
                                           ArrayList<KotItemModifier> modifiersList) {
        if (printer == null) {
            return false;
        }
        if (isRevenueKiosk()) {
            return false;
        }
        if (mRemoteService == null) {
            printerDialog();
            return false;
        }
        try {
            Gson gson = new Gson();

            //Simple Hack: 客户联需要显示餐厅
            KotSummary fakeKotSummary = new KotSummary();
            fakeKotSummary.setBusinessDate(kotsummary.getBusinessDate());
            fakeKotSummary.setCreateTime(kotsummary.getCreateTime());
            fakeKotSummary.setId(kotsummary.getId());
            fakeKotSummary.setOrderId(kotsummary.getOrderId());
            fakeKotSummary.setOrderNo(kotsummary.getOrderNo());
            fakeKotSummary.setRevenueCenterId(kotsummary.getRevenueCenterId());
            fakeKotSummary.setRevenueCenterName(CoreData.getInstance().getRestaurant().getRestaurantName());
            fakeKotSummary.setTableId(kotsummary.getTableId());
            fakeKotSummary.setTableName(kotsummary.getTableName());
            fakeKotSummary.setUpdateTime(kotsummary.getUpdateTime());
            fakeKotSummary.setIsTakeAway(kotsummary.getIsTakeAway());

            String printstr = gson.toJson(printer);
            String kotsumStr = gson.toJson(fakeKotSummary);
            String kdlstr = gson.toJson(itemDetailsList);
            String modstr = gson.toJson(modifiersList);

            int size = 3;
            if (countryCode == ParamConst.CHINA)
                size = 2;
            mRemoteService.printBillSummary(printstr, kotsumStr, kdlstr, modstr, size);
            return true;
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

    // public void remoteBillPrint(PrinterDevice printer, PrinterTitle title,
    // Order order, ArrayList<PrintOrderItem> orderItems,
    // ArrayList<PrintOrderModifier> orderModifiers,
    // Map<String,ArrayList<String>> taxes, Map<String, String> settlement ) {
    //
    // if (mRemoteService == null) {
    // printerDialog();
    // return ;
    // }
    // try {
    // Gson gson = new Gson();
    // String prtStr = gson.toJson(printer);
    // String prtTitle = gson.toJson(title);
    // String orderStr = gson.toJson(order);
    // String details = gson.toJson(orderItems);
    // String mods = gson.toJson(orderModifiers);
    // String tax = gson.toJson(taxes);
    // String payment = gson.toJson(settlement);
    // if (isRevenueKiosk())
    // mRemoteService.printKioskBill(prtStr,prtTitle,orderStr,details,mods,
    // tax, payment, this.systemSettings.isDoubleBillPrint(),
    // this.systemSettings.isDoubleReceiptPrint());
    // else
    // mRemoteService.printBill(prtStr,prtTitle,orderStr,details,mods,
    // tax, payment, this.systemSettings.isDoubleBillPrint(),
    // this.systemSettings.isDoubleReceiptPrint());
    // } catch (RemoteException e) {
    // e.printStackTrace();
    // }
    // }

    public void remoteStoredCard(PrinterDevice printer, ConsumingRecords consumingRecords, String balance){
        if(mRemoteService == null){
            printerDialog();
            return;
        }
        String action = "Top-up";
        if(consumingRecords.getConsumingType().intValue() == ParamConst.STORED_CARD_ACTION_TOP_UP){
            action = "Top-up";
        }else if(consumingRecords.getConsumingType().intValue() == ParamConst.STORED_CARD_ACTION_PAY){
            action = "Pay";
        }else if(consumingRecords.getConsumingType().intValue() == ParamConst.STORED_CARD_ACTION_REFUND){
            action = "Refund";
        }
        try {
            Gson gson = new Gson();
            String prtStr = gson.toJson(printer);
            mRemoteService.printStoredCardConsume(prtStr, "StoredCard Amount", TimeUtil.getTimeFormat(consumingRecords.getConsumingTime()), consumingRecords.getCardId()+"", action, consumingRecords.getConsumingAmount(), balance);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void remoteBillPrint(PrinterDevice printer, PrinterTitle title,
                                Order order, ArrayList<PrintOrderItem> orderItems,
                                ArrayList<PrintOrderModifier> orderModifiers,
                                List<Map<String, String>> taxes,
                                List<PaymentSettlement> settlement, RoundAmount roundAmount){
        remoteBillPrint(printer, title, order, orderItems, orderModifiers, taxes, settlement, roundAmount, true);
    }

    public void remoteBillPrint(PrinterDevice printer, PrinterTitle title,
                                Order order, ArrayList<PrintOrderItem> orderItems,
                                ArrayList<PrintOrderModifier> orderModifiers,
                                List<Map<String, String>> taxes,
                                List<PaymentSettlement> settlement, RoundAmount roundAmount,
                                boolean openDrawer) {

        if (mRemoteService == null) {
            printerDialog();
            return;
        }
        List<PrintReceiptInfo> printReceiptInfos = new ArrayList<PrintReceiptInfo>();
        if (settlement != null) {
            for (PaymentSettlement paymentSettlement : settlement) {
                PrintReceiptInfo printReceiptInfo = new PrintReceiptInfo();
                printReceiptInfo.setPaidAmount(paymentSettlement
                        .getPaidAmount());
                printReceiptInfo.setPaymentTypeId(paymentSettlement
                        .getPaymentTypeId());
                switch (paymentSettlement.getPaymentTypeId().intValue()) {
                    case ParamConst.SETTLEMENT_TYPE_CASH:
                        printReceiptInfo.setCashChange(paymentSettlement
                                .getCashChange());
                        break;
                    case ParamConst.SETTLEMENT_TYPE_MASTERCARD:
                    case ParamConst.SETTLEMENT_TYPE_UNIPAY:
                    case ParamConst.SETTLEMENT_TYPE_VISA:
                    case ParamConst.SETTLEMENT_TYPE_DINNER_INTERMATIONAL:
                    case ParamConst.SETTLEMENT_TYPE_AMEX:
                    case ParamConst.SETTLEMENT_TYPE_JCB:
                        printReceiptInfo
                                .setCardNo(CardsSettlementSQL
                                        .getCardNoByPaymentIdAndPaymentSettlementId(
                                                paymentSettlement.getPaymentId()
                                                        .intValue(),
                                                paymentSettlement.getId()
                                                        .intValue()));
                        break;
                    case ParamConst.SETTLEMENT_TYPE_NETS:
                        printReceiptInfo
                                .setCardNo(NetsSettlementSQL
                                        .getNetsSettlementByPament(
                                                paymentSettlement.getPaymentId()
                                                        .intValue(),
                                                paymentSettlement.getId()
                                                        .intValue())
                                        .getReferenceNo()
                                        + "");
                        break;
                    default:
                        break;
                }
                printReceiptInfos.add(printReceiptInfo);
            }
        }
        try {
            Map<String, String> roundingMap = new HashMap<String, String>();
            String total = order.getTotal();
            String rounding = "0.00";
            if (roundAmount != null) {
                total = BH.sub(BH.getBD(order.getTotal()),
                        BH.getBD(roundAmount.getRoundBalancePrice()), true)
                        .toString();
                rounding = BH.getBD(roundAmount.getRoundBalancePrice())
                        .toString();
            }
            roundingMap.put("Total", total);
            roundingMap.put("Rounding", rounding);
            Gson gson = new Gson();
            String prtStr = gson.toJson(printer);
            String prtTitle = gson.toJson(title);
            String orderStr = gson.toJson(order);
            String details = gson.toJson(orderItems);
            String mods = gson.toJson(orderModifiers);
            String tax = gson.toJson(taxes);
            String payment = gson.toJson(printReceiptInfos);
            String roundStr = gson.toJson(roundingMap);
            // gson.toJson(roundingMap);
            if (isRevenueKiosk())
                if (countryCode == ParamConst.CHINA)
                    mRemoteService.printKioskBill(prtStr, prtTitle, orderStr,
                            details, mods, tax, payment,
                            this.systemSettings.isDoubleBillPrint(),
                            this.systemSettings.isDoubleReceiptPrint(), roundStr,
                            getPrintOrderNo(order.getId().intValue()), getLocalRestaurantConfig().getCurrencySymbol(),
                            true);
                else
                    mRemoteService.printKioskBill(prtStr, prtTitle, orderStr,
                            details, mods, tax, payment,
                            this.systemSettings.isDoubleBillPrint(),
                            this.systemSettings.isDoubleReceiptPrint(), roundStr,
                            null, getLocalRestaurantConfig().getCurrencySymbol(),
                            openDrawer);
            else
                mRemoteService.printBill(prtStr, prtTitle, orderStr, details,
                        mods, tax, payment,
                        this.systemSettings.isDoubleBillPrint(),
                        this.systemSettings.isDoubleReceiptPrint(), roundStr,
                        getLocalRestaurantConfig().getCurrencySymbol(),
                        openDrawer);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void kickOutCashDrawer(PrinterDevice printer) {
        Gson gson = new Gson();
        String prtStr = gson.toJson(printer);
        if (mRemoteService == null) {
            printerDialog();
            return;
        }
        try {
            mRemoteService.kickCashDrawer(prtStr);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void deleteOldPrinterMsg(long businessDate){

        try {
            mRemoteService.deleteOldPrinterMsg(businessDate+"");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    public void remitePrintTableQRCode(PrinterDevice printer, PrinterTitle title,String tableId, String qrCodeText){
        if(!TextUtils.isEmpty(tableId) && IntegerUtils.isInteger(tableId)){
            if(mRemoteService == null){
                printerDialog();
                return;
            }
            Gson gson = new Gson();
            String prtStr = gson.toJson(printer);
            String prtTitle = gson.toJson(title);
            try {
                mRemoteService.printTableQRCode(prtStr,tableId+"",prtTitle, qrCodeText);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /*
     * Discover all physical printer devices Result is retured from callback
     * Param: Handler: callback when printers are found
     */
    public void discoverPrinter(Handler handler) {
        if (mRemoteService == null) {
            printerDialog();
            return;
        }
        try {
            mCallback.setHandler(handler);
            mRemoteService.listPrinters();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void remotePrintDaySalesReport(String xzType, PrinterDevice printer,
                                          PrinterTitle title, ReportDaySales reportData,
                                          List<ReportDayTax> taxData, List<ReportUserOpenDrawer> reportUserOpenDrawers,
                                          List<ReportSessionSales> reportSessionSalesList) {
        if (mRemoteService == null) {
            printerDialog();
            return;
        }
        try {
            Gson gson = new Gson();
            String prtStr = gson.toJson(printer);
            String prtTitle = gson.toJson(title);
            String report = gson.toJson(reportData);
            String tax = gson.toJson(taxData);
            String useropen = gson.toJson(reportUserOpenDrawers);
            String reportSessionSaless = gson.toJson(reportSessionSalesList);
            mRemoteService.printDaySalesReport(xzType, prtStr, prtTitle,
                    report, tax, useropen, reportSessionSaless);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void remotePrintDetailAnalysisReport(String xzType,
                                                PrinterDevice printer, PrinterTitle title, ReportDaySales reportData,
                                                List<ReportPluDayItem> plu, List<ReportPluDayModifier> modifier, List<ReportPluDayComboModifier> combData,
                                                List<ItemMainCategory> category, List<ItemCategory> items) {

        if (mRemoteService == null) {
            printerDialog();
            return;
        }
        try {
            Gson gson = new Gson();
            String prtStr = gson.toJson(printer);
            String prtTitle = gson.toJson(title);
            String pluStr = gson.toJson(plu);
            String catStr = gson.toJson(category);
            String itmStr = gson.toJson(items);
            String modStr = gson.toJson(modifier);
            String combStr = gson.toJson(combData);
            String sales = gson.toJson(reportData);
            mRemoteService.printDetailAnalysisReport(xzType, prtStr, prtTitle, sales,
                    pluStr, modStr, combStr, catStr, itmStr);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    public void remotePrintSummaryAnalysisReport(String xzType,
                                                 PrinterDevice printer, PrinterTitle title,
                                                 List<ReportPluDayItem> plu, List<ReportPluDayModifier> modifier,
                                                 List<ItemMainCategory> category, List<ItemCategory> items) {

        if (mRemoteService == null) {
            printerDialog();
            return;
        }
        try {
            Gson gson = new Gson();
            String prtStr = gson.toJson(printer);
            String prtTitle = gson.toJson(title);
            String pluStr = gson.toJson(plu);
            String catStr = gson.toJson(category);
            String itmStr = gson.toJson(items);
            String modStr = gson.toJson(modifier);
            mRemoteService.printSummaryAnalysisReport(xzType, prtStr, prtTitle,
                    pluStr, modStr, catStr, itmStr);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void remotePrintHourlyReport(String xzType, PrinterDevice printer,
                                        PrinterTitle title, List<ReportHourly> hourly) {
        if (mRemoteService == null) {
            // Toast.makeText(this, "Printer service is not started yet",
            // 1000).show();
            printerDialog();
            return;
        }
        try {
            Gson gson = new Gson();
            String prtStr = gson.toJson(printer);
            String prtTitle = gson.toJson(title);
            String hourStr = gson.toJson(hourly);
            mRemoteService.printHourlyAnalysisReport(xzType, prtStr, prtTitle,
                    hourStr);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    public void remotePrintVoidItemReport(String xzType, PrinterDevice printer,
                                          PrinterTitle title, ArrayList<ReportVoidItem> reportVoidItems) {
        if (mRemoteService == null) {
            // Toast.makeText(this, "Printer service is not started yet",
            // 1000).show();
            printerDialog();
            return;
        }
        try {
            Gson gson = new Gson();
            String prtStr = gson.toJson(printer);
            String prtTitle = gson.toJson(title);
            String voidItemStr = gson.toJson(reportVoidItems);
            mRemoteService.printVoidItemAnalysisReport(xzType, prtStr,
                    prtTitle, voidItemStr);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    public void remotePrintEntItemReport(String xzType, PrinterDevice printer,
                                         PrinterTitle title, ArrayList<ReportEntItem> reportEntItems) {
        if (mRemoteService == null) {
            // Toast.makeText(this, "Printer service is not started yet",
            // 1000).show();
            printerDialog();
            return;
        }
        try {
            Gson gson = new Gson();
            String prtStr = gson.toJson(printer);
            String prtTitle = gson.toJson(title);
            String entItemStr = gson.toJson(reportEntItems);
            mRemoteService.printEntItemAnalysisReport(xzType, prtStr, prtTitle,
                    entItemStr);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }




    public void remotePrintModifierDetailAnalysisReport(String xzType,
                                                        PrinterDevice printer, PrinterTitle title, List<ReportPluDayModifier> modifier,
                                                        List<Modifier> category) {

        if (mRemoteService == null) {
            printerDialog();
            return;
        }
        try {
            Gson gson = new Gson();
            String prtStr = gson.toJson(printer);
            String prtTitle = gson.toJson(title);
            String catStr = gson.toJson(category);
            String modStr = gson.toJson(modifier);
            mRemoteService.printModifierDetailAnalysisReport(xzType, prtStr, prtTitle, modStr, catStr);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    public void remotePrintComboDetailAnalysisReport(String xzType,
                                                     PrinterDevice printer, PrinterTitle title, List<ReportPluDayItem> plu, List<ReportPluDayComboModifier> modifier) {

        if (mRemoteService == null) {
            printerDialog();
            return;
        }
        try {
            Gson gson = new Gson();
            String prtStr = gson.toJson(printer);
            String prtTitle = gson.toJson(title);
            String itemStr = gson.toJson(plu);
            String modStr = gson.toJson(modifier);
            mRemoteService.printComboDetailAnalysisReport(xzType, prtStr, prtTitle, itemStr, modStr);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    public void remotePrintMonthlySalesReport(PrinterDevice printer, PrinterTitle title, int year, int month, List<MonthlySalesReport> sales) {

        if (mRemoteService == null) {
            printerDialog();
            return;
        }
        try {
            Gson gson = new Gson();
            String prtStr = gson.toJson(printer);
            String prtTitle = gson.toJson(title);
            String data = gson.toJson(sales);
            mRemoteService.printMonthlySaleReport(prtStr, prtTitle, year, month, data);

        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    public void remotePrintMonthlyPLUReport(PrinterDevice printer, PrinterTitle title, int year, int month, List<MonthlyPLUReport> plu) {

        if (mRemoteService == null) {
            printerDialog();
            return;
        }
        try {
            Gson gson = new Gson();
            String prtStr = gson.toJson(printer);
            String prtTitle = gson.toJson(title);
            String data = gson.toJson(plu);
            mRemoteService.printMonthlyPLUReport(prtStr, prtTitle, year, month, data);

        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    public void printerDialog() {
        BaseActivity context = App.getTopActivity();
        DialogFactory.commonTwoBtnDialog(context, context.getResources()
                        .getString(R.string.print_down), context.getResources()
                        .getString(R.string.reconnect_print), context.getResources()
                        .getString(R.string.no),
                context.getResources().getString(R.string.yes), null,
                new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        // TODO Auto-generated method stub
                        App.instance.tryConnectRemotePrintService();
                    }
                });
    }

    /**
     * WebSocket PUSH Service
     **/
    public void bindSyncService() {
//        this.bindService(PushService.startIntent(this.getApplicationContext()),
//                this.pushConnection, Context.BIND_IMPORTANT);
//        this.startService(PushService.startIntent(this.getApplicationContext()));
        syncJob = new CloudSyncJobManager(this);
    }
//    public void bindPushWebSocketService(int restId) {
//        this.bindService(RabbitMqPushService.startIntent(this.getApplicationContext(), restId),
//                this.rabbitMqPushConnection, Context.BIND_IMPORTANT);
//        this.startService(RabbitMqPushService.startIntent(this.getApplicationContext(), restId));
//    }

//    public void unBindPushWebSocketService() {
//        if(this.rabbitMqPushService != null){
//            this.stopService(new Intent(this.getApplicationContext(), RabbitMqPushService.class));
//            this.unbindService(this.rabbitMqPushConnection);
//            this.rabbitMqPushService = null;
//        }
//    }

//    public void unBindPushWebSocketSerive() {
//        if (this.pushService != null) {
//            this.unbindService(this.pushConnection);
//            this.pushService = null;
//        }
//    }

    // Getting bill notification
    public void addGettingBillNotification(TableInfo tables) {
        this.notificationsOfGettingBill.add(tables);
        Store.saveObject(instance, Store.NOTIFICATIONS_OF_GETTING_BILL,
                notificationsOfGettingBill);
    }

    public void removeGettingBillNotification(TableInfo tables) {
        this.notificationsOfGettingBill.remove(tables);
        Store.saveObject(instance, Store.NOTIFICATIONS_OF_GETTING_BILL,
                notificationsOfGettingBill);
    }

    public List<TableInfo> getGetTingBillNotifications() {
        if (notificationsOfGettingBill.isEmpty()) {
            List<TableInfo> tabls = Store.getObject(instance,
                    Store.NOTIFICATIONS_OF_GETTING_BILL,
                    new TypeToken<List<TableInfo>>() {
                    }.getType());
            if (tabls != null) {
                notificationsOfGettingBill = tabls;
            }
        }
        return this.notificationsOfGettingBill;
    }

    public SystemSettings getSystemSettings() {
        return systemSettings;
    }

    public Map<String, Integer> getPushMsgMap() {
        if (pushMsgMap.isEmpty()) {
            Map<String, Integer> map = Store.getObject(instance,
                    Store.PUSH_MESSAGE, new TypeToken<Map<String, Integer>>() {
                    }.getType());
            if (map != null) {
                pushMsgMap = map;
            }
        }
        return pushMsgMap;
    }

    public void setPushMsgMap(Map<String, Integer> pushMsgMap) {
        this.pushMsgMap = pushMsgMap;
    }

    public void addPushMsgItem(String msg, int msgValue) {
        pushMsgMap.put(msg, msgValue);
    }

    public boolean isRevenueKiosk() {
        if (revenueCenter != null
                && revenueCenter.getIsKiosk() == ParamConst.REVENUECENTER_IS_KIOSK) {
            return true;
        } else {
            return false;
        }
    }

    /*
     * Cloud Sync manager: Sync Sales data to cloud
     */
    public CloudSyncJobManager getSyncJob() {
        return syncJob;
    }

    public int height;

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    // // round的配置移到RestaurantConfig里面
    // public String getRoundType() {
    // if(TextUtils.isEmpty(roundType)){
    // setRoundType(CoreData.getInstance().getRestaurantConfigs());
    // }
    // if(TextUtils.isEmpty(roundType)){
    // roundType = ParamConst.ROUND_NONE;
    // }
    // return roundType;
    // }
    //
    // public void setRoundType(List<RestaurantConfig> restaurantConfigs) {
    // for(RestaurantConfig restaurantConfig : restaurantConfigs){
    // if(restaurantConfig.getParaType().intValue() ==
    // ParamConst.ROUND_RULE_TYPE){
    // this.roundType = restaurantConfig.getParaValue1();
    // }
    // }
    // }
    //
    // public List<Integer> getSessionConfigType() {
    // if(sessionConfigType == null){
    // this.sessionConfigType = Arrays.asList(new Integer[]{
    // ParamConst.SESSION_STATUS_BREAKFAST, ParamConst.SESSION_STATUS_LUNCH,
    // ParamConst.SESSION_STATUS_DINNER});
    // }
    // return sessionConfigType;
    // }
    //
    // public void setSessionConfigType(List<RestaurantConfig>
    // restaurantConfigs) {
    // for(RestaurantConfig restaurantConfig : restaurantConfigs){
    // if(restaurantConfig.getParaType().intValue() ==
    // ParamConst.SALE_SESSION_TYPE){
    // if(TextUtils.isEmpty(restaurantConfig.getParaValue1())){
    // this.sessionConfigType = Arrays.asList(new Integer[]{
    // ParamConst.SESSION_STATUS_BREAKFAST, ParamConst.SESSION_STATUS_LUNCH,
    // ParamConst.SESSION_STATUS_DINNER});
    // }else{
    // String[]
    // strArray=restaurantConfig.getParaValue1().split(ParamConst.PARA_VALUE_SPLIT);
    // if(strArray.length == 0){
    // this.sessionConfigType = Arrays.asList(new Integer[]{
    // ParamConst.SESSION_STATUS_BREAKFAST, ParamConst.SESSION_STATUS_LUNCH,
    // ParamConst.SESSION_STATUS_DINNER});
    // }else{
    // Integer[] intArray=new Integer[strArray.length];
    // for(int i=0;i<strArray.length;i++){
    // intArray[i]=Integer.parseInt(strArray[i]);
    // }
    // Comparator comparator=new Comparator<Integer>() {
    //
    // @Override
    // public int compare(Integer arg0, Integer arg1) {
    // return arg0.compareTo(arg1);
    // }
    // };
    //
    // this.sessionConfigType = Arrays.asList(intArray);
    // Collections.sort(this.sessionConfigType, comparator);
    // }
    // }
    //
    //
    // }
    // }
    //
    // }

    // public String getCurrencySymbol() {
    // return currencySymbol;
    // }
    //
    // public void setCurrencySymbol(List<RestaurantConfig> restaurantConfigs) {
    // for(RestaurantConfig restaurantConfig : restaurantConfigs){
    // if(restaurantConfig.getParaType().intValue() ==
    // ParamConst.CURRENCY_TYPE){
    // this.currencySymbol = restaurantConfig.getParaValue1();
    // }
    // }
    // }
    //
    // public int getTaxInclusiveTag() {
    // return taxIncluded;
    // }
    //
    // public void setTaxInclusiveTag(List<RestaurantConfig> restaurantConfigs)
    // {
    // for(RestaurantConfig restaurantConfig : restaurantConfigs){
    // if(restaurantConfig.getParaType().intValue() ==
    // ParamConst.PRICE_TAX_INCLUSIVE){
    // if (!TextUtils.isEmpty(restaurantConfig.getParaValue1()))
    // this.taxIncluded = Integer.valueOf(restaurantConfig.getParaValue1());
    // }
    // }
    // }

    public LocalRestaurantConfig getLocalRestaurantConfig() {
        if (localRestaurantConfig == null) {
            this.localRestaurantConfig = LocalRestaurantConfig.getInstance();
        }
        return localRestaurantConfig;
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
                    if (CoreData.getInstance().getTaxs() != null)
                        this.localRestaurantConfig.getIncludedTax().setTax(
                                CoreData.getInstance().getTax(
                                        Integer.parseInt(restaurantConfig
                                                .getParaValue1())));
                    break;
                case ParamConst.ROUND_RULE_TYPE:
                    this.localRestaurantConfig.setRoundType(restaurantConfig);
                    break;
                case ParamConst.CURRENCY_TYPE:
                    this.localRestaurantConfig.setCurrencySymbol(restaurantConfig);
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

    public void addAlipayPushMessage(AlipayPushMsgDto alipayPushMsgDto) {
        if (alipayPushMsgDto == null)
            return;
        this.alipayPush.put(alipayPushMsgDto.getPosBillNo(), alipayPushMsgDto);
    }

    public Map<Integer, AlipayPushMsgDto> getAlipayPushMessage() {
        if (this.alipayPush == null)
            return new HashMap<Integer, AlipayPushMsgDto>();
        else
            return this.alipayPush;
    }

    public String getPrintOrderNo(int orderId) {
        int No = orderId % this.systemSettings.getMaxPrintOrderNo();
        if (No == 0) {
            No = 100;
        }
        if (No > 3 && No < 13) {
            No += 1;
        } else if (No > 3 && No < this.systemSettings.getMaxPrintOrderNo()) {
            No += 2;
        }
        return No + "";
    }

    public void appOrderShowDialog(boolean isPush, final AppOrder appOrder, final List<AppOrderDetail> appOrderDetailList, final List<AppOrderModifier> appOrderModifierList, List<AppOrderDetailTax> appOrderDetailTaxList) throws Exception {
        BaseActivity activity = getTopActivity();
        if ((!(activity instanceof NetWorkOrderActivity)) && activity != null
                && (!activity.isFinishing())) {
//            activity.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    netOrderDialogShow(appOrder);
//                }
//            });
        }
        appOrderTransforOrder(appOrder, appOrderDetailList, appOrderModifierList, appOrderDetailTaxList);
    }


    private void netOrderDialogShow(final AppOrder appOrder) {
        final BaseActivity context = App.getTopActivity();
        DialogFactory.topDialogOrder(context, appOrder,
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (ButtonClickTimer.canClick(v))
                            if (!(context instanceof NetWorkOrderActivity)) {
                                Intent intent = new Intent(context,
                                        NetWorkOrderActivity.class);
                                context.startActivity(intent);
                            }
                    }
                }, new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (ButtonClickTimer.canClick(v))
                            if (!(context instanceof NetWorkOrderActivity)) {
                                Intent intent = new Intent(context,
                                        NetWorkOrderActivity.class);
                                intent.putExtra("appOrderId", appOrder
                                        .getId().toString());
                                context.startActivity(intent);
                            }
                    }
                });

    }



    public void appOrderTransforOrder(final AppOrder appOrder, final List<AppOrderDetail> appOrderDetailList, final List<AppOrderModifier> appOrderModifierList, List<AppOrderDetailTax> appOrderDetailTaxList) {
        synchronized (instance) {
//			new Thread(new Runnable() {
//				@Override
//				public void run() {
            TableInfo tables = null;
            if (App.instance.isRevenueKiosk())
                tables = TableInfoSQL.getKioskTable();
            else {
                if (appOrder.getTableId().intValue() > 0) {
                    tables = TableInfoSQL.getTableById(appOrder.getTableId().intValue());
                } else {
//                    tables = TableInfoSQL.getAllUsedOneTables();
                    return;
                }
//                if (tables != null && tables.getStatus().intValue() != ParamConst.TABLE_STATUS_IDLE) {
////                    appOrder.setTableType(ParamConst.APP_ORDER_TABLE_STATUS_USED);
////                    AppOrderSQL.updateAppOrder(appOrder);
//                    Order order = OrderSQL.getUnfinishedOrderAtTable(tables.getPosId(), getBusinessDate());
//                    if(OrderDetailSQL.getOrderDetails(order.getId().intValue()).size() > 0){
//                        return;
//                    }
//                }
                if(tables == null){
                    return;
                }
            }


//            appOrder.setTableType(ParamConst.APP_ORDER_TABLE_STATUS_NOT_USE);
//            AppOrderSQL.updateAppOrder(appOrder);

            Order order = ObjectFactory.getInstance().getOrderFromAppOrder(appOrder, getUser(),
                    getSessionStatus(), getRevenueCenter(), tables, getBusinessDate(), CoreData.getInstance().getRestaurant(),
                    App.instance.getLocalRestaurantConfig().getIncludedTax().getTax(), App.instance.isRevenueKiosk());
//            OrderHelper.setOrderInclusiveTaxPrice(order);
//            tables.setStatus(ParamConst.TABLE_STATUS_DINING);

//            TablesSQL.updateTables(tables);
//            TableInfoSQL.updateTables(tables);
            order.setOrderStatus(ParamConst.ORDER_STATUS_FINISHED);
            OrderSQL.update(order);
            OrderBill orderBill = ObjectFactory.getInstance()
                    .getOrderBill(order, getRevenueCenter());
            Payment payment = ObjectFactory.getInstance().getPayment(order, orderBill);
            PaymentSettlement paymentSettlement = ObjectFactory.getInstance().getPaymentSettlement(payment, ParamConst.SETTLEMENT_TYPE_PAYPAL, payment.getPaymentAmount());
            for (AppOrderDetail appOrderDetail : appOrderDetailList) {
                if (CoreData.getInstance().getItemDetailById(appOrderDetail.getItemId().intValue()) == null)
                    continue;
                OrderDetail orderDetail = ObjectFactory
                        .getInstance()
                        .getOrderDetailFromTempAppOrderDetail(
                                order, appOrderDetail);
                OrderDetailSQL.updateOrderDetail(orderDetail);
                for (AppOrderDetailTax appOrderDetailTax : appOrderDetailTaxList){
                     if(appOrderDetailTax.getOrderDetailId().intValue() != appOrderDetail.getId().intValue()){
                         continue;
                     }
                    Tax tax = CoreData.getInstance().getTax(appOrderDetailTax.getTaxId().intValue());
                    TaxCategory taxCategory = CoreData.getInstance().getTaxCategoryByTaxId(tax.getId().intValue());
                    OrderDetailTax orderDetailTax = ObjectFactory.getInstance().getOrderDetailTaxByOnline(order, orderDetail, appOrderDetailTax, taxCategory.getIndex());
                }
                for (AppOrderModifier appOrderModifier : appOrderModifierList) {
                    if(appOrderModifier.getOrderDetailId().intValue() != appOrderDetail.getId().intValue())
                        continue;

                    ItemDetail printItemDetail = CoreData
                            .getInstance()
                            .getItemDetailByTemplateId(
                                    appOrderModifier
                                            .getItemId());
                    int printId = 0;
                    if (printItemDetail != null) {
                        ArrayList<Printer> prints =
                                CoreData.getInstance().getPrintersInGroup(printItemDetail.getPrinterId().intValue());
                        if (prints.size() == 0) {
                            printId = 0;
                        } else {
                            printId = prints.get(0).getId().intValue();
                        }
                    }
                   ObjectFactory
                            .getInstance()
                            .getOrderModifierFromTempAppOrderModifier(
                                    order, orderDetail, printId,
                                    appOrderModifier);
                }
            }
            List<OrderDetail> placedOrderDetails
                    = OrderDetailSQL.getOrderDetailsForPrint(order.getId());
            KotSummary kotSummary = ObjectFactory.getInstance()
                    .getKotSummary(
                            TableInfoSQL.getTableById(
                                    order.getTableId()).getName(), order,
                            App.instance.getRevenueCenter(),
                            App.instance.getBusinessDate());
            ArrayList<KotItemDetail> kotItemDetails = new ArrayList<KotItemDetail>();
            List<Integer> orderDetailIds = new ArrayList<Integer>();
            ArrayList<KotItemModifier> kotItemModifiers = new ArrayList<KotItemModifier>();
            String kotCommitStatus = ParamConst.JOB_NEW_KOT;
            for (OrderDetail orderDetail : placedOrderDetails) {
                if (orderDetail.getOrderDetailStatus() >= ParamConst.ORDERDETAIL_STATUS_PREPARED) {
                    continue;
                }
                if (orderDetail.getOrderDetailStatus() == ParamConst.ORDERDETAIL_STATUS_KOTPRINTERD) {
                    kotCommitStatus = ParamConst.JOB_UPDATE_KOT;
                } else {
                    KotItemDetail kotItemDetail = ObjectFactory
                            .getInstance()
                            .getKotItemDetail(
                                    order,
                                    orderDetail,
                                    CoreData.getInstance()
                                            .getItemDetailById(
                                                    orderDetail
                                                            .getItemId()),
                                    kotSummary,
                                    App.instance.getSessionStatus(), ParamConst.KOTITEMDETAIL_CATEGORYID_MAIN);
                    kotItemDetail.setItemNum(orderDetail
                            .getItemNum());
                    if (kotItemDetail.getKotStatus() == ParamConst.KOT_STATUS_UNDONE) {
                        kotCommitStatus = ParamConst.JOB_UPDATE_KOT;
                        kotItemDetail
                                .setKotStatus(ParamConst.KOT_STATUS_UPDATE);
                    }
                    KotItemDetailSQL.update(kotItemDetail);
                    kotItemDetails.add(kotItemDetail);
                    orderDetailIds.add(orderDetail.getId());
                    ArrayList<OrderModifier> orderModifiers = OrderModifierSQL
                            .getOrderModifiers(order, orderDetail);
                    for (OrderModifier orderModifier : orderModifiers) {
                        if (orderModifier.getStatus().intValue() == ParamConst.ORDER_MODIFIER_STATUS_NORMAL) {
                            KotItemModifier kotItemModifier = ObjectFactory
                                    .getInstance()
                                    .getKotItemModifier(
                                            kotItemDetail,
                                            orderModifier,
                                            CoreData.getInstance()
                                                    .getModifier(
                                                            orderModifier
                                                                    .getModifierId()));
                            KotItemModifierSQL.update(kotItemModifier);
                            kotItemModifiers.add(kotItemModifier);
                        }
                    }
                }
            }
            KotSummarySQL.update(kotSummary);
            if (!kotItemDetails.isEmpty()) {

                if (!isRevenueKiosk() && getSystemSettings().isOrderSummaryPrint()) {
                    PrinterDevice printer = getCahierPrinter();
                    if (printer != null) {
                        remoteOrderSummaryPrint(printer, kotSummary, kotItemDetails, kotItemModifiers);
                    }
                }
                // check system has KDS or printer devices
                if (getKDSDevices().size() == 0
                        && getPrinterDevices().size() == 0) {
                } else {
                    Map<String, Object> orderMap = new HashMap<String, Object>();
                    orderMap.put("orderId", order.getId());
                    orderMap.put("orderDetailIds", orderDetailIds);
                    getKdsJobManager().tearDownKot(
                            kotSummary, kotItemDetails,
                            kotItemModifiers, kotCommitStatus,
                            orderMap);
                }
                appOrder.setOrderStatus(ParamConst.APP_ORDER_STATUS_PREPARING);
                appOrder.setOrderNo(order.getOrderNo());
                AppOrderSQL.addAppOrder(appOrder);
                getSyncJob().checkAppOrderStatus(
                        App.instance.getRevenueCenter().getId().intValue(),
                        appOrder.getId().intValue(),
                        appOrder.getOrderStatus().intValue(), "",
                        App.instance.getBusinessDate().longValue(),appOrder.getOrderNo());
                if(App.getTopActivity() instanceof NetWorkOrderActivity){
                    App.getTopActivity().httpRequestAction(Activity.RESULT_OK, "");
                }
            }
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                App.instance.setAppOrderNum(AppOrderSQL.getNewAppOrderCountByTime(App.instance.getBusinessDate()), 3);
            }
        }).start();
        if(getTopActivity() instanceof MainPage){
            getTopActivity().httpRequestAction(MainPage.REFRESH_TABLES_STATUS, null);
        }
//				}
//			}).start();
    }

    public void printerAppOrder(final AppOrder appOrder) {
        try {

            Order paidOrder = OrderSQL.getOrderByAppOrderId(appOrder
                    .getId().intValue());
            PrinterDevice printer = App.instance.getCahierPrinter();
            TableInfo tableInfo = TableInfoSQL.getTableById(paidOrder.getTableId().intValue());
            PrinterTitle title = ObjectFactory.getInstance().getPrinterTitle(
                    getRevenueCenter(),
                    paidOrder,
                    getUser().getFirstName()
                            + getUser().getLastName(),
                    tableInfo.getName(), 1);

            ArrayList<PrintOrderItem> orderItems = ObjectFactory.getInstance()
                    .getItemList(
                            OrderDetailSQL.getOrderDetails(paidOrder.getId()));
            List<Map<String, String>> taxMap = OrderDetailTaxSQL
                    .getTaxPriceSUMForPrint(App.instance.getLocalRestaurantConfig()
                            .getIncludedTax().getTax(), paidOrder);

            ArrayList<PrintOrderModifier> orderModifiers = ObjectFactory
                    .getInstance().getItemModifierList(paidOrder,
                            OrderDetailSQL.getOrderDetails(paidOrder.getId()));
            if (orderItems.size() > 0 && printer != null) {
                List<PaymentSettlement> paymentSettlements = PaymentSettlementSQL
                        .getAllPaymentSettlementByPaymentId(PaymentSQL
                                .getPaymentByOrderId(
                                        paidOrder.getId().intValue()).getId()
                                .intValue());
                RoundAmount roundAmount = RoundAmountSQL
                        .getRoundAmount(paidOrder);
                App.instance.remoteBillPrint(printer, title, paidOrder,
                        orderItems, orderModifiers, taxMap, paymentSettlements,
                        roundAmount);
            }
        } catch (Exception e) {
            CrashReport.postCatchedException(e);
        }
    }

    public void tryConnectRemotePrintService() {
        int printVersionCode = 0;
        int posVersionCode = 0;
        try {
            posVersionCode = instance.getPackageManager().getPackageInfo(instance.getPackageName(), 0).versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<PackageInfo> packageinfo = this.getPackageManager()
                .getInstalledPackages(0);

        int count = packageinfo.size();
        for (int i = 0; i < count; i++) {

            PackageInfo pinfo = packageinfo.get(i);
            ApplicationInfo appInfo = pinfo.applicationInfo;
            if (!((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0)) {
                String name = pinfo.applicationInfo.packageName;

                if (name.equals("com.alfred.remote.printservice")) {
                    printVersionCode = pinfo.versionCode;
                }
            }
        }
        boolean hasApk = copyApkFromAssets(this, "printServiceApk/Print.apk", Environment
                .getExternalStorageDirectory().getAbsolutePath()
                + "/Print.apk");
        if (printVersionCode < posVersionCode && hasApk) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(
                        Uri.parse("file://"
                                + Environment.getExternalStorageDirectory()
                                .getAbsolutePath() + "/Print.apk"),
                        "application/vnd.android.package-archive");
                intentFilter = new IntentFilter();
                intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
                intentFilter.addDataScheme("package");
                registerReceiver(receiver, intentFilter);
                this.startActivity(intent);
        }else{
            connectRemotePrintService();
        }

    }


    public void autoConnectRemotePrintService() {
        int printVersionCode = 0;
        int posVersionCode = 0;
        try {
            posVersionCode = instance.getPackageManager().getPackageInfo(instance.getPackageName(), 0).versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<PackageInfo> packageinfo = this.getPackageManager()
                .getInstalledPackages(0);

        int count = packageinfo.size();
        for (int i = 0; i < count; i++) {

            PackageInfo pinfo = packageinfo.get(i);
            ApplicationInfo appInfo = pinfo.applicationInfo;
            if (!((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0)) {
                String name = pinfo.applicationInfo.packageName;

                if (name.equals("com.alfred.remote.printservice")) {
                    printVersionCode = pinfo.versionCode;
                }
            }
        }
        if (printVersionCode < posVersionCode) {
            printerDialog();
        }else{
            connectRemotePrintService();
        }

    }



    public boolean copyApkFromAssets(Context context, String fileName, String path) {
        boolean copyIsFinish = false;
        try {
            InputStream is = context.getAssets().open(fileName);
            File file = new File(path);
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            byte[] temp = new byte[1024];
            int i = 0;
            while ((i = is.read(temp)) > 0) {
                fos.write(temp, 0, i);
            }
            fos.close();
            is.close();
            copyIsFinish = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return copyIsFinish;
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String packName = intent.getDataString().substring(8);
            //packName为所安装的程序的包名
            String name = context.getResources().getString(R.string.printer_app_name);
            if(packName.equals("com.alfred.remote.printservice")){
                connectRemotePrintService();
                unregisterReceiver(receiver);
            }
        }
    };

    public int getAppOrderNum() {
        return appOrderNum;
    }

    public void setAppOrderNum(int appOrderNum, int type) {
        this.appOrderNum = appOrderNum;
        RxBus.getInstance().post(RxBus.RX_MSG_1, type);
    }

    /**
     * 判断是否使用的是商米设备  商米副屏设置是否展示
     */
    public boolean isSUNMIShow(){
        String brand = Build.BRAND;
        String model = Build.MODEL;
        String manufacturer = Build.MANUFACTURER;
        LogUtil.d(TAG, brand + "**************" + model);
        if (brand.equals("SUNMI") && manufacturer.equals("SUNMI")){
            return true;
        }
        return false;
    }

    /**
     * 判断显示的商米富屏是大屏（14寸）还是小屏（7寸）
     * @return 返回false为小屏  反之为大屏
     */
    public boolean isSmallOrBigScreen(){
        try {
            String sn = Build.SERIAL;
            String str = sn.substring(0, 4);
            if (str.equals("T103") || str.equals("T104")){
                return true;
            }else if (str.equals("T105") || str.equals("T106")){
                return false;
            }else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 图片加载框架
     */
    private class PicassoImageLoader implements ImageLoader {

        private Bitmap.Config mConfig;

        public PicassoImageLoader() {
            this(Bitmap.Config.RGB_565);
        }

        public PicassoImageLoader(Bitmap.Config config) {
            this.mConfig = config;
        }

        @Override
        public void displayImage(Activity activity, String path, GFImageView imageView, Drawable defaultDrawable, int width, int height) {
            Picasso.with(activity)
                    .load(new File(path))
                    .placeholder(defaultDrawable)
                    .error(defaultDrawable)
                    .config(mConfig)
                    .resize(width, height)
                    .centerInside()
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(imageView);
        }

        @Override
        public void clearMemoryCache() {
        }
    }
}
