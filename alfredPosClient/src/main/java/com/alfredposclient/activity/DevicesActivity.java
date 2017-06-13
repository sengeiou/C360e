package com.alfredposclient.activity;

import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alfred.remote.printservice.RemotePrintServiceCallback;
import com.alfredbase.BaseActivity;
import com.alfredbase.ParamConst;
import com.alfredbase.global.CoreData;
import com.alfredbase.javabean.LocalDevice;
import com.alfredbase.javabean.Printer;
import com.alfredbase.javabean.model.KDSDevice;
import com.alfredbase.javabean.model.PrinterDevice;
import com.alfredbase.javabean.model.WaiterDevice;
import com.alfredbase.store.sql.PrinterSQL;
import com.alfredbase.utils.BarcodeUtil;
import com.alfredbase.utils.CommonUtil;
import com.alfredbase.utils.ObjectFactory;
import com.alfredbase.view.CustomListView;
import com.alfredbase.view.HorizontalListView;
import com.alfredposclient.R;
import com.alfredposclient.adapter.DeviceGroupAdapter;
import com.alfredposclient.adapter.DivicesAdapter;
import com.alfredposclient.global.App;
import com.alfredposclient.global.JavaConnectJS;
import com.alfredposclient.global.UIHelp;
import com.alfredposclient.popupwindow.SelectPrintWindow;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 设备页
 */
public class DevicesActivity extends BaseActivity {
    public static final int ASSIGN_PRINTER_DEVICE = 1;
    public static final int UNASSIGN_PRINTER_DEVICE = -1;

    final static String TAG = "DevicesActivity";

    private ImageButton btn_back;
    private TextView tv_title_name;
    private LinearLayout ll_print;

    private ImageView device_code_img;
    private TextView devices_ip_tv;
    private TextView devices_revenueCenter_tv;

    //打印机
    private LinearLayout devices_printe_lyt;

    //KDS
    private LinearLayout devices_transfer_lyt;

    //手动添加打印机
    private LinearLayout devices_add_ll;

    //waiter
    private LinearLayout devices_waiter_lyt;
    private DivicesAdapter adapter;

    private HorizontalListView hv_printer_group;

    private CustomListView devices_customlistview;

    private DeviceGroupAdapter deviceGroupAdapter;

    private View view;
    private List<PrinterDevice> printerDBModelList = new ArrayList<PrinterDevice>();
    private List<Printer> printerDeptModelList = new ArrayList<Printer>();

    private SelectPrintWindow selectPrintWindow;
    private int dex = 0;

    @Override
    protected void initView() {
        super.initView();
        setContentView(R.layout.devices_layout);
        initUI();
        initData();
    }

    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case UNASSIGN_PRINTER_DEVICE: //解绑打印机
                    PrinterDevice device = (PrinterDevice) msg.obj;
                    unassignDevice(device);
                    break;
                case JavaConnectJS.ACTION_ASSIGN_PRINTER_DEVICE: // 手动添加打印机
                    Gson gson = new Gson();
                    @SuppressWarnings("unchecked")
                    Map<String, String> printer = (Map<String, String>) gson
                            .fromJson((String) msg.obj,
                                    new TypeToken<Map<String, String>>() {
                                    }.getType());
                            if (printer != null) {
                                String assignToName = printer.get("assignTo");
                                if (assignToName != null && assignToName.length()>0) {
                                        String printerModel = printer.get("printerName");

                                        LocalDevice localDevice = ObjectFactory
                                                .getInstance().getLocalDevice(assignToName,
                                                        printerModel,
                                                        ParamConst.DEVICE_TYPE_PRINTER,
                                                        printerDeptModelList.get(dex).getId(),
                                                        printer.get("printerIp"), "");
                                        CoreData.getInstance().addLocalDevice(localDevice);

                                        PrinterDevice prtDev = new PrinterDevice();
                                        prtDev.setDevice_id(printerDeptModelList.get(dex).getId());
                                        prtDev.setIP(printer.get("printerIp"));
                                        prtDev.setIsCahierPrinter(printerDeptModelList.get(dex).getIsCashdrawer());
                                        prtDev.setMac(localDevice.getMacAddress());
                                        prtDev.setModel(printerModel);
                                        prtDev.setName(assignToName);
                                        App.instance.setPrinterDevice(printerDeptModelList.get(dex).getId(), prtDev);
                                        refreshPrinterDevices(null);
                                        App.instance.discoverPrinter(handler);
                                }
                            }
                    break;
                case JavaConnectJS.ACTION_NEW_KDS_ADDED: // 连接KDS设备
                    KDSDevice kds = (KDSDevice)msg.obj;
                    Map<Integer, KDSDevice> map1 = new HashMap<Integer, KDSDevice>();
                    map1.put(kds.getDevice_id(), kds);
                    App.instance.addKDSDevice(kds.getDevice_id(), kds);
                    break;
                case JavaConnectJS.ACTION_NEW_WAITER_ADDED: // 连接Waiter设备
                    WaiterDevice waiter = (WaiterDevice)msg.obj;
                    Map<Integer, WaiterDevice> map2 = new HashMap<Integer, WaiterDevice>();
                    map2.put(waiter.getWaiterId(), waiter);
                    App.instance.setWaiterDevices(map2);
                    break;
                case RemotePrintServiceCallback.PRINTERS_DISCOVERIED: // 同一局域网内搜索可绑定的打印机
                    refreshPrinterDevices((Map<String, String>) msg.obj);
                    break;
                case ASSIGN_PRINTER_DEVICE: // 绑定打印机
                    PrinterDevice printerDevice = (PrinterDevice) msg.obj;
                    printerDevice.setDevice_id(printerDeptModelList.get(dex).getId());
                    List<PrinterDevice> list = new ArrayList<PrinterDevice>();
                    list.add(printerDevice);
                    adapter.setList(list, 1);
                    App.instance.setPrinterDevice(printerDeptModelList.get(dex).getId(), printerDevice);
                    LocalDevice localDevice = ObjectFactory.getInstance().getLocalDevice(printerDevice.getName(), printerDevice.getModel(),
                            ParamConst.DEVICE_TYPE_PRINTER,
                            printerDeptModelList.get(dex).getId(),
                            printerDevice.getIP(),
                            printerDevice.getMac());
                    CoreData.getInstance().addLocalDevice(localDevice);
                    App.instance.discoverPrinter(handler);
                    break;
                default:
                    break;
            }
        }
    };

    // 搜索可连接的打印机
    private void refreshPrinterDevices(Map<String, String> printersDiscovered){
        Map<Integer, PrinterDevice> data = App.instance.getPrinterDevices();
        Map<Integer, List<PrinterDevice>> map = new HashMap<Integer, List<PrinterDevice>>();
        for (Map.Entry<Integer, PrinterDevice> entry : data.entrySet()) {
            List<PrinterDevice> devices = new ArrayList<PrinterDevice>();
            devices.add(entry.getValue());
            map.put(entry.getKey(), devices);
        }

        if (printersDiscovered != null) {
            for (Map.Entry<String, String> entry : printersDiscovered.entrySet()) {
                PrinterDevice tmppt = new PrinterDevice();
                tmppt.setIP(entry.getKey());
                tmppt.setName(entry.getValue());
                tmppt.setDevice_id(-1);
                if (!map.containsKey(tmppt.getDevice_id())) {
                    List<PrinterDevice> list = new ArrayList<PrinterDevice>();
                    list.add(tmppt);
                    map.put(tmppt.getDevice_id(), list);
                } else {
                    List<PrinterDevice> printerDevices = map.get(tmppt.getDevice_id());
                    printerDevices.add(tmppt);
                }
            }
        }

        App.instance.setMap(map);

        Printer printer1 = printerDeptModelList.get(dex);
        if(map.containsKey(printer1.getId())) {
            printerDBModelList = map.get(printer1.getId());
        }else{
            printerDBModelList = map.get(-1);
        }
        if (printerDBModelList != null && printerDBModelList.size() > 0) {
            devices_customlistview.setVisibility(View.VISIBLE);
            devices_add_ll.setVisibility(View.GONE);
            if (adapter != null) {
                adapter.setList(printerDBModelList, 1);
            }else {
                adapter = new DivicesAdapter(this, printerDBModelList, handler);
                devices_customlistview.setAdapter(adapter);
            }
        }else {
            devices_customlistview.setVisibility(View.GONE);
            devices_add_ll.setVisibility(View.VISIBLE);
        }
    }

    private void unassignDevice(PrinterDevice device) {
        if (device == null)
            return;
        int localDevId = device.getDevice_id();
        if (localDevId > 0) {
            LocalDevice ldItem = CoreData.getInstance()
                    .getLocalDeviceByDeviceIdAndIP(localDevId, device.getIP());
            App.instance.removePrinterDevice(localDevId);
            if (ldItem != null) {
                CoreData.getInstance().removeLocalDeviceByDeviceIdAndIP(
                        localDevId, device.getIP());
            }
            refreshPrinterDevices(null);
            App.instance.discoverPrinter(handler);
        }
    }

    @Override
    public void httpRequestAction(int action, Object obj) {
        switch (action) {
            case ParamConst.HTTP_REQ_CALLBACK_KDS_PAIRED:
                if (obj != null) {
                    UIHelp.showToast(this, context.getResources().getString(R.string.kds_paired));
                    KDSDevice kds = (KDSDevice) obj;
                    handler.sendMessage(handler.obtainMessage(JavaConnectJS.ACTION_NEW_KDS_ADDED, kds));
                }
                break;
            case ParamConst.HTTP_REQ_CALLBACK_WAITER_PAIRED:
                if (obj != null) {
                    UIHelp.showToast(this, context.getResources().getString(R.string.waiter_paired));
                    WaiterDevice wds = (WaiterDevice)obj;
                    handler.sendMessage(handler.obtainMessage(JavaConnectJS.ACTION_NEW_WAITER_ADDED, wds));
                }
                break;
            }
    }

    @Override
    public void handlerClickEvent(View v) {
        super.handlerClickEvent(v);
        switch (v.getId()) {
            case R.id.btn_back:
                DevicesActivity.this.finish();
                break;
            case R.id.devices_printe_lyt:
                float f1 = devices_printe_lyt.getY();
                initAnimation(f1);
                MViews(R.id.devices_printe_lyt);
                break;
            case R.id.devices_transfer_lyt:
                float f5 = devices_transfer_lyt.getY();
                initAnimation(f5);
                MViews(R.id.devices_transfer_lyt);
                break;
            case R.id.devices_waiter_lyt:
                float f9 = devices_waiter_lyt.getY();
                initAnimation(f9);
                MViews(R.id.devices_waiter_lyt);
                break;
            case R.id.devices_add_ll:
                selectPrintWindow = new SelectPrintWindow(DevicesActivity.this, devices_customlistview, handler);
                selectPrintWindow.show("TM-T81", "TM-T81");
                break;
            default:
                break;
        }
    }

    private void MViews(int id){
        switch (id){
            case R.id.devices_printe_lyt:
                hv_printer_group.setVisibility(View.VISIBLE);
                devices_customlistview.setVisibility(View.VISIBLE);
                devices_add_ll.setVisibility(View.GONE);
                List<PrinterDevice> devices = new ArrayList<PrinterDevice>();
                Map<Integer, List<PrinterDevice>> hash = App.instance.getMap();
                if (hash.containsKey(printerDeptModelList.get(dex).getId())) {
                    devices = hash.get(printerDeptModelList.get(dex).getId());
                }else {
                    List<PrinterDevice> list = hash.get(-1);
                    for (int i = 0; i < list.size(); i++) {
                        PrinterDevice device = list.get(i);
                        PrinterDevice printerDevice = new PrinterDevice();
                        printerDevice.setDevice_id(-1);
                        printerDevice.setName(device.getName());
                        printerDevice.setGroupId(device.getGroupId());
                        printerDevice.setIP(device.getIP());
                        printerDevice.setMac(device.getMac());
                        printerDevice.setModel(device.getModel());
                        devices.add(printerDevice);
                    }
                }
                    if (devices.size() > 0) {
                        if (adapter != null) {
                            adapter.setList(devices, 1);
                        }else {
                            adapter = new DivicesAdapter(DevicesActivity.this, devices, handler);
                            devices_customlistview.setAdapter(adapter);
                        }
                    } else {
                        devices_customlistview.setVisibility(View.GONE);
                        devices_add_ll.setVisibility(View.VISIBLE);
                    }
                break;
            case R.id.devices_transfer_lyt:
                hv_printer_group.setVisibility(View.GONE);
                devices_add_ll.setVisibility(View.GONE);
                Map<Integer, KDSDevice> map = App.instance.getKDSDevices();
                List<PrinterDevice> KDSDevices = new ArrayList<PrinterDevice>();
                if (map.size() > 0){
                    List<KDSDevice> kdsDevices = new ArrayList<KDSDevice>(map.values());
                    for (KDSDevice device : kdsDevices){
                        PrinterDevice printerDevice = new PrinterDevice();
                        printerDevice.setDevice_id(device.getDevice_id());
                        printerDevice.setModel(device.getName());
                        printerDevice.setName(device.getName());
                        printerDevice.setIP(device.getIP());
                        printerDevice.setMac(device.getMac());
                        KDSDevices.add(printerDevice);
                    }
                    if (adapter != null){
                        adapter.setList(KDSDevices, 2);
                    }else {
                        adapter = new DivicesAdapter(DevicesActivity.this, KDSDevices, handler);
                        devices_customlistview.setAdapter(adapter);
                    }
                }else {
                    devices_customlistview.setVisibility(View.GONE);
                }
                break;
            case R.id.devices_waiter_lyt:
                hv_printer_group.setVisibility(View.GONE);
                devices_add_ll.setVisibility(View.GONE);
                Map<Integer, WaiterDevice> wmap = App.instance.getWaiterDevices();
                List<PrinterDevice> WaiterDevices = new ArrayList<PrinterDevice>();
                if (wmap.size() > 0){
                    List<WaiterDevice> waiterDevices = new ArrayList<WaiterDevice>(wmap.values());
                    for (WaiterDevice device : waiterDevices){
                        PrinterDevice printerDevice = new PrinterDevice();
                        printerDevice.setDevice_id(device.getWaiterId());
                        printerDevice.setIP(device.getIP());
                        printerDevice.setMac(device.getMac());
                        WaiterDevices.add(printerDevice);
                    }
                    if (adapter != null){
                        adapter.setList(WaiterDevices, 2);
                    }else {
                        adapter = new DivicesAdapter(DevicesActivity.this, WaiterDevices, handler);
                        devices_customlistview.setAdapter(adapter);
                    }
                }else {
                    devices_customlistview.setVisibility(View.GONE);
                }
                break;
            }
    }

    private void initData() {

        //本机IP地址和MAC地址
        if (!TextUtils.isEmpty(CommonUtil.getLocalIpAddress()) && !TextUtils.isEmpty(CommonUtil.getLocalMacAddress(context))) {
            devices_ip_tv.setText(CommonUtil.getLocalIpAddress() + "\n"
                    + CommonUtil.getLocalMacAddress(context));
            Bitmap bitmap = BarcodeUtil.createQRImage(CommonUtil.getLocalIpAddress());
            Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.scanner_logo);
            Bitmap mBitmap = BarcodeUtil.addLogo(bitmap, logo);
            device_code_img.setImageBitmap(mBitmap);
        } else {
            devices_ip_tv.setVisibility(View.GONE);
            device_code_img.setVisibility(View.GONE);
        }
        printerDBModelList = new ArrayList<PrinterDevice>(App.instance.getPrinterDevices().values());
        printerDeptModelList = PrinterSQL.getAllPrinterByType(1);
        if (printerDeptModelList.size() > 0) {
            deviceGroupAdapter = new DeviceGroupAdapter(this, printerDeptModelList);
            hv_printer_group.setAdapter(deviceGroupAdapter);
        }
        refreshPrinterDevices(null);
        App.instance.discoverPrinter(handler);
        deviceGroupAdapter.setSelectIndex(dex);
        registEvent();

    }

    private void initUI() {
        btn_back = (ImageButton) findViewById(R.id.btn_back);
        ll_print = (LinearLayout) findViewById(R.id.ll_print);
        ll_print.setVisibility(View.GONE);

        tv_title_name = (TextView) findViewById(R.id.tv_title_name);
        tv_title_name.setText(getString(R.string.devices_));
        hv_printer_group = (HorizontalListView) findViewById(R.id.hv_printer_group);

        device_code_img = (ImageView) findViewById(R.id.device_code_img);

        devices_ip_tv = (TextView) findViewById(R.id.devices_ip_tv);

        devices_revenueCenter_tv = (TextView) findViewById(R.id.devices_revenueCenter_tv);
        devices_revenueCenter_tv.setText(App.instance.getMainPosInfo().getName());

        devices_printe_lyt = (LinearLayout) findViewById(R.id.devices_printe_lyt);

        devices_transfer_lyt = (LinearLayout) findViewById(R.id.devices_transfer_lyt);

        devices_waiter_lyt = (LinearLayout) findViewById(R.id.devices_waiter_lyt);

        devices_customlistview = (CustomListView) findViewById(R.id.devices_customlistview);

        devices_add_ll = (LinearLayout) findViewById(R.id.devices_add_ll);
        view = findViewById(R.id.device_view);
        devices_customlistview.setDividerWidth(20);
        devices_customlistview.setDividerHeight(20);
    }

    private void registEvent() {

        btn_back.setOnClickListener(this);
        ll_print.setOnClickListener(this);
        devices_printe_lyt.setOnClickListener(this);
        devices_transfer_lyt.setOnClickListener(this);
        devices_waiter_lyt.setOnClickListener(this);
        devices_add_ll.setOnClickListener(this);

        hv_printer_group.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int position, long id) {
                dex = position;
                deviceGroupAdapter.setSelectIndex(position);
                deviceGroupAdapter.notifyDataSetChanged();
                devices_customlistview.removeAllViews();
                List<PrinterDevice> devices = new ArrayList<PrinterDevice>();
                Map<Integer, List<PrinterDevice>> map = App.instance.getMap();
                if (map.size() > 0) {
                    if (map.containsKey(printerDeptModelList.get(position).getId())) {
                        devices = map.get(printerDeptModelList.get(position).getId());
                    } else {
                        List<PrinterDevice> list = map.get(-1);
                        if (list != null) {
                            for (int i = 0; i < list.size(); i++) {
                                PrinterDevice device = list.get(i);
                                PrinterDevice printerDevice = new PrinterDevice();
                                printerDevice.setDevice_id(-1);
                                printerDevice.setName(device.getName());
                                printerDevice.setGroupId(device.getGroupId());
                                printerDevice.setIP(device.getIP());
                                printerDevice.setMac(device.getMac());
                                printerDevice.setModel(device.getModel());
                                devices.add(printerDevice);
                            }
                        }else {
                            devices_customlistview.setVisibility(View.GONE);
                            devices_add_ll.setVisibility(View.VISIBLE);
                        }
                    }
                    if (devices.size() > 0) {
                        devices_customlistview.setVisibility(View.VISIBLE);
                        devices_add_ll.setVisibility(View.GONE);
                        if (adapter != null) {
                            adapter.setList(devices, 1);
                            } else {
                                adapter = new DivicesAdapter(DevicesActivity.this, devices, handler);
                                devices_customlistview.setAdapter(adapter);
                            }
                        }else {
                        devices_customlistview.setVisibility(View.GONE);
                        devices_add_ll.setVisibility(View.VISIBLE);
                    }
                }else {
                    devices_customlistview.setVisibility(View.GONE);
                    devices_add_ll.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void initAnimation(float values) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY",
                values);
        animator.setDuration(300);
        animator.start();
    }
}
