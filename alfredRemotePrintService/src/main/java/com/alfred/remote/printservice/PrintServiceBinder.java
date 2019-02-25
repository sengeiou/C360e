package com.alfred.remote.printservice;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.hardware.usb.UsbDevice;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.alfred.print.jobs.PrintManager;
import com.alfred.printer.BillPrint;
import com.alfred.printer.BillTscPrint;
import com.alfred.printer.ComboDetailAnalysisReportPrint;
import com.alfred.printer.DaySalesReportPrint;
import com.alfred.printer.DetailAnalysisReportPrint;
import com.alfred.printer.EntItemReportPrint;
import com.alfred.printer.HourlySalesReportPrint;
import com.alfred.printer.KOTPrint;
import com.alfred.printer.KickDrawerPrint;
import com.alfred.printer.ModifierDetailAnalysisReportPrint;
import com.alfred.printer.MonthlySalesReportPrint;
import com.alfred.printer.StoredCardPrint;
import com.alfred.printer.SummaryAnalysisReportPrint;
import com.alfred.printer.TableQRCodePrint;
import com.alfred.printer.VoidItemReportPrint;
import com.alfredbase.ParamConst;
import com.alfredbase.javabean.CashInOut;
import com.alfredbase.javabean.ItemCategory;
import com.alfredbase.javabean.ItemMainCategory;
import com.alfredbase.javabean.KotItemDetail;
import com.alfredbase.javabean.KotItemModifier;
import com.alfredbase.javabean.KotSummary;
import com.alfredbase.javabean.Modifier;
import com.alfredbase.javabean.MonthlyPLUReport;
import com.alfredbase.javabean.MonthlySalesReport;
import com.alfredbase.javabean.Order;
import com.alfredbase.javabean.OrderDetail;
import com.alfredbase.javabean.PrintBean;
import com.alfredbase.javabean.PrinterTitle;
import com.alfredbase.javabean.ReportDayPayment;
import com.alfredbase.javabean.ReportDaySales;
import com.alfredbase.javabean.ReportDayTax;
import com.alfredbase.javabean.ReportHourly;
import com.alfredbase.javabean.ReportPluDayComboModifier;
import com.alfredbase.javabean.ReportPluDayItem;
import com.alfredbase.javabean.ReportPluDayModifier;
import com.alfredbase.javabean.Restaurant;
import com.alfredbase.javabean.model.PrintOrderItem;
import com.alfredbase.javabean.model.PrintOrderModifier;
import com.alfredbase.javabean.model.PrintReceiptInfo;
import com.alfredbase.javabean.model.PrinterDevice;
import com.alfredbase.javabean.model.ReportEntItem;
import com.alfredbase.javabean.model.ReportSessionSales;
import com.alfredbase.javabean.model.ReportVoidItem;
import com.alfredbase.javabean.temporaryforapp.AppOrder;
import com.alfredbase.javabean.temporaryforapp.ReportUserOpenDrawer;
import com.alfredbase.store.sql.PrintQueueMsgSQL;
import com.alfredbase.store.sql.SettingDataSQL;
import com.alfredbase.utils.BH;
import com.alfredbase.utils.IntegerUtils;
import com.alfredbase.utils.MachineUtil;
import com.alfredbase.utils.TimeUtil;
import com.birbit.android.jobqueue.JobManager;
import com.epson.epos2.discovery.DeviceInfo;
import com.epson.epos2.discovery.Discovery;
import com.epson.epos2.discovery.DiscoveryListener;
import com.epson.epos2.discovery.FilterOption;
import com.epson.epsonio.DevType;
import com.epson.epsonio.EpsonIo;
import com.epson.epsonio.EpsonIoException;
import com.epson.epsonio.IoStatus;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;


public class PrintServiceBinder extends IAlfredRemotePrintService.Stub {
    private String TAG = PrintServiceBinder.class.getSimpleName();
    private final PrintService service;
    ScheduledExecutorService scheduler = null;
    ScheduledFuture<?> future;
    private ArrayList<PrintBean> mBluetoothDevicesDatas = new ArrayList<>();

    private ArrayList<PrinterDevice> pList = new ArrayList<>();
    //get printer name
    static final byte[] CMD_ESC_ENABLE_PRINTER = {
            0x1b, 0x3d, 0x01,    // ESC = 1(Enables printer)
    };

    static final byte[] CMD_GS_I_PRINTER_NAME = {
            0x1d, 0x49, 0x43,    // GS I 67(Printer name)
    };

    static final byte[] CMD_GS_I_ADDITIONAL_FONTS = {
            0x1d, 0x49, 0x45,    // GS I 69(Type of mounted additional fonts)
    };

    static final int RESPONSE_HEADER = 0x5f;
    static final int RESPONSE_TERMINAL = 0x00;
    static final int SEND_RESPONSE_TIMEOUT = 1000;
    static final int RESPONSE_MAXBYTE = 128;


    private FilterOption mFilterOption = null;


    public PrintServiceBinder(PrintService service) {
        super();
        this.service = service;
    }

    @Override
    public void registerCallBack(IAlfredRemotePrintServiceCallback cb)
            throws RemoteException {

        synchronized (this.service.mCallbacks) {
            this.service.mCallbacks.add(cb);
        }
        service.setCallback(new PrintService.Callback() {


            @Override
            public void getBluetoothDevices(PrintBean mBluetoothDevicesDatas) {
                Log.d("搜索", "   " + mBluetoothDevicesDatas.getAddress());

                Map<String, String> ret = new HashMap<String, String>();
                ret.put(mBluetoothDevicesDatas.getAddress(), mBluetoothDevicesDatas.getName());
//
//
                synchronized (service.mCallbacks) {
                    for (IAlfredRemotePrintServiceCallback listener : service.mCallbacks) {
                        try {
                            Gson gson = new Gson();
                            listener.fromService("PRINTS_FOUND", gson.toJson(ret));
                        } catch (RemoteException e) {
                            Log.w("Printer Lookup", "Failed to notify listener " + listener, e);
                        }
                    }
                }
            }

            @Override
            public void getUsbDevices(UsbDevice ud) {
                Gson gson = new Gson();
                String prtStr = ud.getVendorId() + "," + ud.getProductId();

                Map<String, String> ret = new HashMap<String, String>();
                ret.put(prtStr, ud.getDeviceName());
//
//
                synchronized (service.mCallbacks) {
                    for (IAlfredRemotePrintServiceCallback listener : service.mCallbacks) {
                        try {

                            listener.fromService("PRINTS_FOUND", gson.toJson(ret));
                        } catch (RemoteException e) {
                            Log.w("Printer Lookup", "Failed to notify listener " + listener, e);
                        }
                    }
                }
            }

        });
    }

    @Override
    public String getMessage() throws RemoteException {
        return "hello";
    }

    @Override
    public void setMessage(String name) throws RemoteException {
        System.out.println(name);
    }

    @Override
    public void kickCashDrawer(String printer) throws RemoteException {
        Gson gson = new Gson();
        PrinterDevice prtDevice = gson.fromJson(printer, PrinterDevice.class);

        PrintManager printMgr = this.service.getPrintMgr();
        JobManager printJobMgr = printMgr.configureJobManager(prtDevice.getIP());
        PrinterQueueManager pqMgr = this.service.getPqMgr();

        if (printJobMgr != null) {
            String uuid = pqMgr.getDataUUID("drawer");
            KickDrawerPrint kick = new KickDrawerPrint(uuid, 0L);
            kick.setPrinterIp(prtDevice.getIP());
            kick.addKickOut();
            //add queue
            this.service.getPqMgr().queuePrint(kick.getJobForQueue());
            printMgr.addJob(prtDevice.getIP(), kick);
        }
    }

    @Override
    public void printDaySalesReport(String xzType, String printer, String title, String report, String tax, String customPayment, String useropen, String sessionSales)
            throws RemoteException {
        Gson gson = new Gson();
        PrinterDevice prtDevice = gson.fromJson(printer, PrinterDevice.class);
        PrinterTitle prtTitle = gson.fromJson(title, PrinterTitle.class);
        ReportDaySales reportData = gson.fromJson(report, ReportDaySales.class);
        ArrayList<ReportDayTax> taxData = gson.fromJson(tax, new TypeToken<ArrayList<ReportDayTax>>() {
        }.getType());
        List<ReportDayPayment> reportDayPayments = gson.fromJson(customPayment, new TypeToken<List<ReportDayPayment>>() {
        }.getType());
        List<ReportUserOpenDrawer> reportUserOpenDrawers = gson.fromJson(useropen, new TypeToken<List<ReportUserOpenDrawer>>() {
        }.getType());
        List<ReportSessionSales> reportSessionSales = null;
        if (!TextUtils.isEmpty(sessionSales)) {
            reportSessionSales = gson.fromJson(sessionSales, new TypeToken<List<ReportSessionSales>>() {
            }.getType());
        }
        PrintManager printMgr = this.service.getPrintMgr();
        JobManager printJobMgr = printMgr.configureJobManager(prtDevice.getIP());
        PrinterQueueManager pqMgr = this.service.getPqMgr();

        if (printJobMgr != null) {
            //int msgType, String uuid,
            //int msgId, Long bizDate, Long created
            String uuid = pqMgr.getDataUUID(prtTitle.getBill_NO());

            DaySalesReportPrint salesPrint
                    = new DaySalesReportPrint(uuid, TimeUtil.getPrintingLongDate(prtTitle.getBizDate()));

            String name = prtDevice.getName();
            //set page size
            if (this.service.isTMU220(name)) {
                salesPrint.setCharSize(33);
            } else if (this.service.isTM88(name)) {
                salesPrint.setCharSize(42);
            } else {
                salesPrint.setCharSize(48);
            }
            salesPrint.AddReportHeader(prtTitle.getRestaurantName(), xzType, PrintService.instance.getResources().getString(R.string.sales_analysis));
            salesPrint.AddHeader(prtTitle.getOp(), prtTitle.getBill_NO(), prtTitle.getDate() + " " + prtTitle.getTime(), prtTitle.getBizDate());
            salesPrint.AddContentListHeader(PrintService.instance.getResources().getString(R.string.type),
                    PrintService.instance.getResources().getString(R.string.qty_),
                    PrintService.instance.getResources().getString(R.string.amount));
            salesPrint.setPrinterIp(prtDevice.getIP());
            salesPrint.print(reportData, taxData, reportDayPayments, reportUserOpenDrawers, reportSessionSales);
            salesPrint.AddFooter(prtTitle.getDate() + " " + prtTitle.getTime());

            pqMgr.queuePrint(salesPrint.getJobForQueue());

            printMgr.addJob(prtDevice.getIP(), salesPrint);
        }
    }

    @Override
    public void printDetailAnalysisReport(String xzType, String printer,
                                          String title, String daySaleSummary, String plu, String pluMod, String pluCombo, String category, String items)
            throws RemoteException {
        Gson gson = new Gson();

        PrinterDevice prtDevice = gson.fromJson(printer, PrinterDevice.class);
        PrinterTitle prtTitle = gson.fromJson(title, PrinterTitle.class);
        ReportDaySales reportData = gson.fromJson(daySaleSummary, ReportDaySales.class);

        ArrayList<ReportPluDayItem> pluData = gson.fromJson(plu, new TypeToken<ArrayList<ReportPluDayItem>>() {
        }.getType());
        ArrayList<ItemMainCategory> categoryData = gson.fromJson(category, new TypeToken<ArrayList<ItemMainCategory>>() {
        }.getType());
        ArrayList<ItemCategory> itemsData = gson.fromJson(items, new TypeToken<ArrayList<ItemCategory>>() {
        }.getType());
        ArrayList<ReportPluDayModifier> modifier = gson.fromJson(pluMod, new TypeToken<ArrayList<ReportPluDayModifier>>() {
        }.getType());
        ArrayList<ReportPluDayComboModifier> comb = gson.fromJson(pluCombo, new TypeToken<ArrayList<ReportPluDayComboModifier>>() {
        }.getType());

        PrintManager printMgr = this.service.getPrintMgr();
        JobManager printJobMgr = printMgr.configureJobManager(prtDevice.getIP());
        PrinterQueueManager pqMgr = this.service.getPqMgr();

        if (printJobMgr != null) {
            String uuid = pqMgr.getDataUUID(prtTitle.getBill_NO());
            DetailAnalysisReportPrint daPrint
                    = new DetailAnalysisReportPrint(uuid, TimeUtil.getPrintingLongDate(prtTitle.getBizDate()));
            String name = prtDevice.getName();
            //set page size
            if (this.service.isTMU220(name)) {
                daPrint.setCharSize(33);
            } else if (this.service.isTM88(name)) {
                daPrint.setCharSize(42);
            } else {
                daPrint.setCharSize(48);
            }
            daPrint.AddReportHeader(prtTitle.getRestaurantName(), xzType, PrintService.instance.getResources().getString(R.string.detail_analysis));
            daPrint.AddHeader(prtTitle.getOp(), prtTitle.getBill_NO(), prtTitle.getDate() + " " + prtTitle.getTime(), prtTitle.getBizDate());
            if (App.instance.countryCode == ParamConst.CHINA && reportData != null) {
                // Print Sales summary
                daPrint.addSalesSummary(reportData);
            }
            daPrint.AddContentListHeader(PrintService.instance.getResources().getString(R.string.plu_name),
                    PrintService.instance.getResources().getString(R.string.price),
                    PrintService.instance.getResources().getString(R.string.qty),
                    PrintService.instance.getResources().getString(R.string.amount));
            daPrint.print(pluData, modifier, comb, categoryData, itemsData);
            daPrint.setPrinterIp(prtDevice.getIP());
            daPrint.AddFooter(prtTitle.getDate() + " " + prtTitle.getTime());
            pqMgr.queuePrint(daPrint.getJobForQueue());
            printMgr.addJob(prtDevice.getIP(), daPrint);
        }
    }

    @Override
    public void printSummaryAnalysisReport(String xzType, String printer,
                                           String title, String plu, String pluMod, String category, String items)
            throws RemoteException {
        Gson gson = new Gson();

        PrinterDevice prtDevice = gson.fromJson(printer, PrinterDevice.class);
        PrinterTitle prtTitle = gson.fromJson(title, PrinterTitle.class);
        ArrayList<ReportPluDayItem> pluData = gson.fromJson(plu, new TypeToken<ArrayList<ReportPluDayItem>>() {
        }.getType());
        ArrayList<ItemMainCategory> categoryData = gson.fromJson(category, new TypeToken<ArrayList<ItemMainCategory>>() {
        }.getType());
        ArrayList<ItemCategory> itemsData = gson.fromJson(items, new TypeToken<ArrayList<ItemCategory>>() {
        }.getType());
        ArrayList<ReportPluDayModifier> modifier = gson.fromJson(pluMod, new TypeToken<ArrayList<ReportPluDayModifier>>() {
        }.getType());

        PrintManager printMgr = this.service.getPrintMgr();
        JobManager printJobMgr = printMgr.configureJobManager(prtDevice.getIP());
        PrinterQueueManager pqMgr = this.service.getPqMgr();

        if (printJobMgr != null) {
            String uuid = pqMgr.getDataUUID(prtTitle.getBill_NO());
            SummaryAnalysisReportPrint daPrint
                    = new SummaryAnalysisReportPrint(uuid, TimeUtil.getPrintingLongDate(prtTitle.getBizDate()));
            String name = prtDevice.getName();
            //set page size
            if (this.service.isTMU220(name)) {
                daPrint.setCharSize(33);
            } else if (this.service.isTM88(name)) {
                daPrint.setCharSize(42);
            } else {
                daPrint.setCharSize(48);
            }

            daPrint.AddReportHeader(prtTitle.getRestaurantName(), xzType, PrintService.instance.getResources().getString(R.string.summary_analysis));
            daPrint.AddHeader(prtTitle.getOp(), prtTitle.getBill_NO(), prtTitle.getDate() + " " + prtTitle.getTime(), prtTitle.getBizDate());
            daPrint.AddContentListHeader(PrintService.instance.getResources().getString(R.string.plu_name),
                    PrintService.instance.getResources().getString(R.string.qty),
                    PrintService.instance.getResources().getString(R.string.amount));
            daPrint.setPrinterIp(prtDevice.getIP());
            daPrint.print(pluData, modifier, categoryData, itemsData);
            daPrint.AddFooter(prtTitle.getDate() + " " + prtTitle.getTime());
            pqMgr.queuePrint(daPrint.getJobForQueue());
            printMgr.addJob(prtDevice.getIP(), daPrint);
        }
    }

    public void printHourlyAnalysisReport(String xzType, String printer, String title, String hourly)
            throws RemoteException {
        Gson gson = new Gson();

        PrinterDevice prtDevice = gson.fromJson(printer, PrinterDevice.class);
        PrinterTitle prtTitle = gson.fromJson(title, PrinterTitle.class);
        ArrayList<ReportHourly> hourlySales = gson.fromJson(hourly, new TypeToken<ArrayList<ReportHourly>>() {
        }.getType());

        PrintManager printMgr = this.service.getPrintMgr();
        JobManager printJobMgr = printMgr.configureJobManager(prtDevice.getIP());
        PrinterQueueManager pqMgr = this.service.getPqMgr();

        if (printJobMgr != null) {
            String uuid = pqMgr.getDataUUID(prtTitle.getBill_NO());
            HourlySalesReportPrint hsPrint
                    = new HourlySalesReportPrint(uuid, TimeUtil.getPrintingLongDate(prtTitle.getBizDate()));
            String name = prtDevice.getName();
            //set page size
            if (this.service.isTMU220(name)) {
                hsPrint.setCharSize(33);
            } else if (this.service.isTM88(name)) {
                hsPrint.setCharSize(42);
            } else {
                hsPrint.setCharSize(48);
            }
            hsPrint.AddReportHeader(prtTitle.getRestaurantName(), xzType, PrintService.instance.getResources().getString(R.string.hourly_sales));
            hsPrint.AddHeader(prtTitle.getOp(), prtTitle.getBill_NO(), prtTitle.getDate() + " " + prtTitle.getTime(), prtTitle.getBizDate());
            hsPrint.AddContentListHeader(PrintService.instance.getResources().getString(R.string.hour),
                    PrintService.instance.getResources().getString(R.string.tran),
                    PrintService.instance.getResources().getString(R.string.amount));
            hsPrint.setPrinterIp(prtDevice.getIP());
            hsPrint.print(hourlySales);
            hsPrint.AddFooter(prtTitle.getDate() + " " + prtTitle.getTime());
            pqMgr.queuePrint(hsPrint.getJobForQueue());
            printMgr.addJob(prtDevice.getIP(), hsPrint);
        }
    }

    public void printVoidItemAnalysisReport(String xzType, String printer, String title, String voidItems)
            throws RemoteException {
        Gson gson = new Gson();
        PrinterDevice prtDevice = gson.fromJson(printer, PrinterDevice.class);
        PrinterTitle prtTitle = gson.fromJson(title, PrinterTitle.class);
        ArrayList<ReportVoidItem> reportVoidItems = gson.fromJson(voidItems, new TypeToken<ArrayList<ReportVoidItem>>() {
        }.getType());

        PrintManager printMgr = this.service.getPrintMgr();
        JobManager printJobMgr = printMgr.configureJobManager(prtDevice.getIP());
        PrinterQueueManager pqMgr = this.service.getPqMgr();

        if (printJobMgr != null) {
            String uuid = pqMgr.getDataUUID(prtTitle.getBill_NO());
            VoidItemReportPrint viPrint
                    = new VoidItemReportPrint(uuid, TimeUtil.getPrintingLongDate(prtTitle.getBizDate()));

            String name = prtDevice.getName();
            //set page size
            if (this.service.isTMU220(name)) {
                viPrint.setCharSize(33);
            } else if (this.service.isTM88(name)) {
                viPrint.setCharSize(42);
            } else {
                viPrint.setCharSize(48);
            }

            viPrint.AddReportHeader(prtTitle.getRestaurantName(), xzType, PrintService.instance.getResources().getString(R.string.void_plu));
            viPrint.AddHeader(prtTitle.getOp(), prtTitle.getBill_NO(), prtTitle.getDate() + " " + prtTitle.getTime(), prtTitle.getBizDate());
            viPrint.AddContentListHeader(PrintService.instance.getResources().getString(R.string.item_name),
                    PrintService.instance.getResources().getString(R.string.qty),
                    PrintService.instance.getResources().getString(R.string.amount));
            viPrint.setPrinterIp(prtDevice.getIP());
            viPrint.print(reportVoidItems);
            viPrint.AddFooter(prtTitle.getPos());
            pqMgr.queuePrint(viPrint.getJobForQueue());
            printMgr.addJob(prtDevice.getIP(), viPrint);
        }
    }

    public void printEntItemAnalysisReport(String xzType, String printer, String title, String voidItems)
            throws RemoteException {
        Gson gson = new Gson();

        PrinterDevice prtDevice = gson.fromJson(printer, PrinterDevice.class);
        PrinterTitle prtTitle = gson.fromJson(title, PrinterTitle.class);
        ArrayList<ReportEntItem> reportEntItems = gson.fromJson(voidItems, new TypeToken<ArrayList<ReportEntItem>>() {
        }.getType());

        PrintManager printMgr = this.service.getPrintMgr();
        JobManager printJobMgr = printMgr.configureJobManager(prtDevice.getIP());
        PrinterQueueManager pqMgr = this.service.getPqMgr();

        if (printJobMgr != null) {
            String uuid = pqMgr.getDataUUID(prtTitle.getBill_NO());
            EntItemReportPrint eiPrint
                    = new EntItemReportPrint(uuid, TimeUtil.getPrintingLongDate(prtTitle.getBizDate()));
            String name = prtDevice.getName();
            //set page size
            if (this.service.isTMU220(name)) {
                eiPrint.setCharSize(33);
            } else if (this.service.isTM88(name)) {
                eiPrint.setCharSize(42);
            } else {
                eiPrint.setCharSize(48);
            }

            eiPrint.AddReportHeader(prtTitle.getRestaurantName(), xzType, PrintService.instance.getResources().getString(R.string.ent_plu));
            eiPrint.AddHeader(prtTitle.getOp(), prtTitle.getBill_NO(), prtTitle.getDate() + " " + prtTitle.getTime(), prtTitle.getBizDate());
            eiPrint.AddContentListHeader(PrintService.instance.getResources().getString(R.string.item_name),
                    PrintService.instance.getResources().getString(R.string.qty),
                    PrintService.instance.getResources().getString(R.string.amount));
            eiPrint.setPrinterIp(prtDevice.getIP());
            eiPrint.print(reportEntItems);
            eiPrint.AddFooter(prtTitle.getPos());
            pqMgr.queuePrint(eiPrint.getJobForQueue());
            printMgr.addJob(prtDevice.getIP(), eiPrint);
        }
    }

    @Override
    public void printKOT(String printer, String summary,
                         String detail, String modifiers, boolean oneprint, boolean doublePrint, int kotFontSize, boolean isFire) throws RemoteException {
        Gson gson = new Gson();
        PrinterDevice prtDevice = gson.fromJson(printer, PrinterDevice.class);
        KotSummary kotsummary = gson.fromJson(summary, KotSummary.class);
        ArrayList<KotItemDetail> itemDetailsList = gson.fromJson(detail, new TypeToken<ArrayList<KotItemDetail>>() {
        }.getType());
        ArrayList<KotItemModifier> modifiersList = gson.fromJson(modifiers, new TypeToken<ArrayList<KotItemModifier>>() {
        }.getType());

        PrintManager printMgr = this.service.getPrintMgr();
        JobManager printJobMgr = printMgr.configureJobManager(prtDevice.getIP());
        PrinterQueueManager pqMgr = this.service.getPqMgr();

        String name = prtDevice.getName();
        int copies = 1;
        if (doublePrint == true)
            copies = 2;

        for (int i = 0; i < copies; i++) {
            if (oneprint) {
                if (printJobMgr != null) {
                    String uuid = pqMgr.getDataUUID(kotsummary.getKotIdString());

                    KOTPrint kot = new KOTPrint(uuid, kotsummary.getBusinessDate());
                    //set page size
                    if (this.service.isTMU220(name)) {
                        kot.setCharSize(33);
                    } else if (this.service.isTM88(name)) {
                        kot.setCharSize(42);
                    } else {
                        kot.setCharSize(48);
                    }
                    kot.addLineSpace(2);
                    kot.AddTitle(kotsummary.getRevenueCenterName(), kotsummary.getTableName());
                    if (!TextUtils.isEmpty(kotsummary.getDescription())) {
                        kot.addCenterLabel(kotsummary.getDescription(), kotFontSize);
                    }
                    if (itemDetailsList.size() >= 0) {
                        KotItemDetail kotitem = itemDetailsList.get(0);
                        if (kotitem.getKotStatus().intValue() == ParamConst.KOT_STATUS_VOID) {
                            kot.addCenterLabel(PrintService.instance.getResources().getString(R.string.void_), kotFontSize);
                        }
                    }
                    kot.AddHeader(kotsummary);
                    if (isFire) {
                        kot.AddFire();
                    }
                    kot.setPrinterIp(prtDevice.getIP());
                    kot.AddContentListHeader2Cols(PrintService.instance.getResources().getString(R.string.item_name),
                            PrintService.instance.getResources().getString(R.string.qty));
                    boolean canPrint = false;
                    for (KotItemDetail item : itemDetailsList) {
                        kot.AddKotItem(item.getItemName(), item.getItemNum(), kotFontSize);
                        ArrayList<KotItemModifier> modList = getModifiersByDetailId(item.getId().intValue(), modifiersList);
                        int size = getModifierSizehavePrintId(modList);
                        if (size != 0) {
                            canPrint = false;
                            for (KotItemModifier kotItemModifier : modList) {
                                if (IntegerUtils.isEmptyOrZero(kotItemModifier.getPrinterId())
                                        || kotItemModifier.getPrinterId().intValue() == prtDevice.getDevice_id()) {
                                    canPrint = true;
                                    if (!IntegerUtils.isEmptyOrZero(kotItemModifier.getModifierNum()) && kotItemModifier.getModifierNum().intValue() > 1) {
                                        kot.AddModifierItem("-" + kotItemModifier.getModifierName() + "x" + kotItemModifier.getModifierNum().intValue(), 1);
                                    } else {
                                        kot.AddModifierItem("-" + kotItemModifier.getModifierName(), 1);
                                    }


                                }
                            }
                        } else {
                            canPrint = true;
                            List<String> mods = getModifierNameStr(modList);
                            for (String mod : mods) {
                                kot.AddModifierItem("-" + mod, 1);
                            }
                        }
                        if (!TextUtils.isEmpty(item.getSpecialInstractions())) {
                            kot.AddModifierItem("*" + item.getSpecialInstractions() + "*", 1);
                        }
                        kot.addLineSpace(1);
                    }

                    if (i == 1) {
                        kot.AddFooter(PrintService.instance.getResources().getString(R.string.kot_copy), kotsummary.getOrderRemark());
                    } else {
                        kot.AddFooter(TimeUtil.getTime(), kotsummary.getOrderRemark());
                    }
                    if (canPrint) {
                        pqMgr.queuePrint(kot.getJobForQueue());
                        printMgr.addJob(prtDevice.getIP(), kot);
                    }
                }
            } else {
                if (printJobMgr != null) {
                    for (KotItemDetail item : itemDetailsList) {
                        ArrayList<KotItemModifier> modList = getModifiersByDetailId(item.getId().intValue(), modifiersList);
                        int size = getModifierSizehavePrintId(modList);
                        if (size != 0) {
                            List<KotItemModifier> comboItems = getComboItemModifier(prtDevice.getDevice_id(), modList);
                            List<KotItemModifier> comboGeneralModifiers = getComboGeneralModifier(prtDevice.getDevice_id(), modList);
                            for (KotItemModifier kotItemModifier : comboItems) {
                                String uuid = pqMgr.getDataUUID(kotsummary.getKotIdString());

                                KOTPrint kot = new KOTPrint(uuid, kotsummary.getBusinessDate());

                                //set page size
                                if (this.service.isTMU220(name)) {
                                    kot.setCharSize(33);
                                } else if (this.service.isTM88(name)) {
                                    kot.setCharSize(42);
                                } else {
                                    kot.setCharSize(48);
                                }
                                kot.AddTitle(kotsummary.getRevenueCenterName(), kotsummary.getTableName());
                                if (!TextUtils.isEmpty(kotsummary.getDescription())) {
                                    kot.addCenterLabel(kotsummary.getDescription(), kotFontSize);
                                }
                                if (item.getKotStatus().intValue() == ParamConst.KOT_STATUS_VOID) {
                                    kot.addCenterLabel(PrintService.instance.getResources().getString(R.string.void_), kotFontSize);
                                }
                                kot.AddHeader(kotsummary);
                                if (isFire) {
                                    kot.AddFire();
                                }
                                kot.setPrinterIp(prtDevice.getIP());
                                kot.AddContentListHeader2Cols(PrintService.instance.getResources().getString(R.string.item_name),
                                        PrintService.instance.getResources().getString(R.string.qty));

                                kot.AddKotItem(item.getItemName(), item.getItemNum(), kotFontSize);
                                if (!IntegerUtils.isEmptyOrZero(kotItemModifier.getModifierNum()) && kotItemModifier.getModifierNum().intValue() > 1) {
                                    kot.AddModifierItem("-" + kotItemModifier.getModifierName() + "x" + kotItemModifier.getModifierNum().intValue(), 1);
                                } else {
                                    kot.AddModifierItem("-" + kotItemModifier.getModifierName(), 1);
                                }

                                for (KotItemModifier kotGeneralItemModifier : comboGeneralModifiers) {
                                    if (!IntegerUtils.isEmptyOrZero(kotGeneralItemModifier.getModifierNum()) && kotGeneralItemModifier.getModifierNum().intValue() > 1) {
                                        kot.AddModifierItem("-" + kotGeneralItemModifier.getModifierName() + "x" + kotGeneralItemModifier.getModifierNum().intValue(), 1);
                                    } else {
                                        kot.AddModifierItem("-" + kotGeneralItemModifier.getModifierName(), 1);
                                    }
                                }
                                if (!TextUtils.isEmpty(item.getSpecialInstractions())) {
                                    kot.AddModifierItem("*" + item.getSpecialInstractions() + "*", 1);
                                }
                                if (i == 1) {
                                    kot.AddFooter(PrintService.instance.getResources().getString(R.string.kot_copy), kotsummary.getOrderRemark());
                                } else {
                                    kot.AddFooter(TimeUtil.getTime(), kotsummary.getOrderRemark());
                                }
                                pqMgr.queuePrint(kot.getJobForQueue());
                                printMgr.addJob(prtDevice.getIP(), kot);
                            }

                        } else {
                            String uuid = pqMgr.getDataUUID(kotsummary.getKotIdString());

                            KOTPrint kot = new KOTPrint(uuid, kotsummary.getBusinessDate());

                            //set page size
                            if (this.service.isTMU220(name)) {
                                kot.setCharSize(33);
                            } else if (this.service.isTM88(name)) {
                                kot.setCharSize(42);
                            } else {
                                kot.setCharSize(48);
                            }
                            if (!TextUtils.isEmpty(kotsummary.getDescription())) {
                                kot.addCenterLabel(kotsummary.getDescription(), kotFontSize);
                            }
                            if (itemDetailsList.size() >= 0) {
                                KotItemDetail kotitem = itemDetailsList.get(0);
                                if (kotitem.getKotStatus().intValue() == ParamConst.KOT_STATUS_VOID) {
                                    kot.addCenterLabel(PrintService.instance.getResources().getString(R.string.void_), kotFontSize);
                                }
                            }
                            kot.AddTitle(kotsummary.getRevenueCenterName(), kotsummary.getTableName());
                            kot.AddHeader(kotsummary);
                            if (isFire) {
                                kot.AddFire();
                            }
                            kot.setPrinterIp(prtDevice.getIP());
                            kot.AddContentListHeader2Cols(PrintService.instance.getResources().getString(R.string.item_name),
                                    PrintService.instance.getResources().getString(R.string.qty));

                            kot.AddKotItem(item.getItemName(), item.getItemNum(), kotFontSize);
                            List<String> mods = getModifierNameStr(modList);
                            for (String mod : mods) {
                                kot.AddModifierItem("-" + mod, 1);
                            }
                            if (!TextUtils.isEmpty(item.getSpecialInstractions())) {
                                kot.AddModifierItem("*" + item.getSpecialInstractions() + "*", 1);
                            }
                            if (i == 1) {
                                kot.AddFooter(PrintService.instance.getResources().getString(R.string.kot_copy), kotsummary.getOrderRemark());
                            } else {
                                kot.AddFooter(TimeUtil.getTime(), kotsummary.getOrderRemark());
                            }
                            pqMgr.queuePrint(kot.getJobForQueue());
                            printMgr.addJob(prtDevice.getIP(), kot);
                        }

                        //kot.AddFooter(kotsummary.getRevenueCenterName());
//						if (canPrint)

                    }
                }
            }
        }
    }


    @Override
    public void printBill(String printer, String title,
                          String order, String orderDetail,
                          String modifiers, String tax,
                          String payment, boolean doubleprint,
                          boolean doubleReceipts, String rounding,
                          String currencySymbol, boolean openDrawer, boolean isDouble) throws RemoteException {
        BH.initFormart(isDouble);
        Gson gson = new Gson();
        boolean isCashSettlement = false;

        PrinterDevice prtDevice = gson.fromJson(printer, PrinterDevice.class);
        PrinterTitle prtTitle = gson.fromJson(title, PrinterTitle.class);
        Order theOrder = gson.fromJson(order, Order.class);
        Log.d(TAG, prtTitle + "prinjsontBill:" + theOrder + "-----" + orderDetail);
        String name = prtDevice.getName();

        ArrayList<PrintOrderItem> printOrderItemList = gson.fromJson(orderDetail,
                new TypeToken<ArrayList<PrintOrderItem>>() {
                }.getType());

        ArrayList<PrintOrderModifier> orderModifiers = gson.fromJson(modifiers,
                new TypeToken<ArrayList<PrintOrderModifier>>() {
                }.getType());

        List<Map<String, String>> taxes = gson.fromJson(tax,
                new TypeToken<List<Map<String, String>>>() {
                }.getType());

        List<PrintReceiptInfo> settlement = gson.fromJson(payment,
                new TypeToken<List<PrintReceiptInfo>>() {
                }.getType());

        PrintManager printMgr = this.service.getPrintMgr();
        JobManager printJobMgr = printMgr.configureJobManager(prtDevice.getIP());
        PrinterQueueManager pqMgr = this.service.getPqMgr();

        if (settlement.isEmpty()) {
            //double bill print
            if (printJobMgr != null) {
                int printSize = 1;
                if (doubleprint)
                    printSize = 2;
                for (int i = 0; i < printSize; i++) {
                    String uuid = pqMgr.getDataUUID(prtTitle.getBill_NO());
                    BillPrint billPrint = new BillPrint(uuid, Long.valueOf(prtTitle.getBizDate()));
                    billPrint.setPrinterIp(prtDevice.getIP());


                    //set page size
                    if (this.service.isTMU220(name)) {
                        billPrint.setCharSize(33);
                        //U220 cannot support image print
                        billPrint.AddRestaurantInfo(null,
                                prtTitle.getRestaurantName(),
                                prtTitle.getAddressDetail(), prtTitle.getOptions());
                    } else {
                        if (this.service.isTM88(name)) {
                            billPrint.setCharSize(42);
                        } else {
                            billPrint.setCharSize(48);
                        }
                        billPrint.AddRestaurantInfo(prtTitle.getLogo(),
                                prtTitle.getRestaurantName(),
                                prtTitle.getAddressDetail(), prtTitle.getOptions());
                    }

                    String tableName;
                    if (prtTitle.getCopy() == 2) {
                        tableName = prtTitle.getTableName() + "(Reprint Bill Copy)";
                    } else {
                        tableName = prtTitle.getTableName();
                    }

                    billPrint.AddHeader(theOrder.getIsTakeAway(), tableName, theOrder.getPersons(),
                            theOrder.getNumTag() + prtTitle.getBill_NO(), prtTitle.getPos(),
                            prtTitle.getOp(), prtTitle.getDate() + " " + prtTitle.getTime(), theOrder.getNumTag() + theOrder.getOrderNo().toString());
                    billPrint.AddContentListHeader(PrintService.instance.getResources().getString(R.string.item),
                            PrintService.instance.getResources().getString(R.string.price),
                            PrintService.instance.getResources().getString(R.string.qty),
                            PrintService.instance.getResources().getString(R.string.total));//("Item Name", "QTY");


                    if (printOrderItemList != null && printOrderItemList.size() > 0) {
                        LinkedHashMap<String, PrintOrderItem> map = new LinkedHashMap<>();
                        for (int index = 0; index < printOrderItemList.size(); index++) {
                            boolean canMerge = true;
                            PrintOrderItem item = printOrderItemList.get(index).clone();
                            if (orderModifiers != null) {
                                for (int m = 0; m < orderModifiers.size(); m++) {
                                    PrintOrderModifier om = orderModifiers.get(m);
                                    if (om.getOrderDetailId() == item.getOrderDetailId()) {
                                        canMerge = false;
                                        break;
                                    }
                                }
                            } else {
                                canMerge = true;
                            }
                            if (canMerge) {
                                if (map.containsKey(item.getItemDetailId().intValue() + "")) {
                                    PrintOrderItem printOrderItem = map.get(item.getItemDetailId().intValue() + "");
                                    printOrderItem.setQty((Integer.parseInt(printOrderItem.getQty()) + Integer.parseInt(item.getQty())) + "");
                                    printOrderItem.setAmount(BH.add(BH.getBD(printOrderItem.getAmount()), BH.getBD(item.getAmount()), false).toString());
                                    map.put(printOrderItem.getItemDetailId().intValue() + "", printOrderItem);
                                } else {
                                    map.put(item.getItemDetailId().intValue() + "", item);
                                }
                            } else {
                                map.put(item.getItemDetailId().intValue() + "_" + item.getOrderDetailId(), item);
                            }
                        }

                        for (PrintOrderItem item : map.values()) {
                            billPrint.AddOrderItem(item.getItemName(), item.getPrice(),
                                    item.getQty(), item.getAmount(), 1, item.getWeight());
                            //getModifiersByDetailId()
                            ////
                            if (orderModifiers != null) {
                                for (int m = 0; m < orderModifiers.size(); m++) {
                                    PrintOrderModifier om = orderModifiers.get(m);
                                    if (om.getOrderDetailId() == item.getOrderDetailId()) {
                                        if (om.getQty() > 1) {
                                            billPrint.addOrderModifier(om.getItemName() + "x" + om.getQty(), 1, om.getPrice());
                                        } else {
                                            billPrint.addOrderModifier(om.getItemName(), 1, om.getPrice());
                                        }
                                    }
                                }
                            }
                        }

//						for (int index = printOrderItemList.size() - 1;index >= 0;index--) {
//							PrintOrderItem item = printOrderItemList.get(index);
//							billPrint.AddOrderItem(item.getItemName(), item.getPrice(),
//									item.getQty(), item.getAmount(), 1, item.getWeight());
//							//getModifiersByDetailId()
//							////
//							if (orderModifiers != null) {
//								for (int m = 0; m < orderModifiers.size(); m++) {
//									PrintOrderModifier om = orderModifiers.get(m);
//									if (om.getOrderDetailId() == item.getOrderDetailId()) {
//										if (om.getQty() > 1) {
//											billPrint.addOrderModifier(om.getItemName() + "x" + om.getQty(), 1, om.getPrice());
//										} else {
//											billPrint.addOrderModifier(om.getItemName(), 1, om.getPrice());
//										}
//									}
//								}
//							}
//						}
                    }

                    ////////////// Bill Summary
                    String subTotal = BH.getBD(theOrder.getSubTotal()).toString();
                    String discount = BH.getBD(theOrder.getDiscountAmount()).toString();
                    String grandTotal = BH.getBD(theOrder.getTotal()).toString();

                    billPrint.AddBillSummary(subTotal, discount, taxes, grandTotal, rounding, currencySymbol, prtTitle.getSpliteByPax());
                    billPrint.addCustomizedFieldAtFooter(prtTitle.getFooterOptions());
                    billPrint.AddFooter(PrintService.instance.getResources().getString(R.string.powered_by_alfred), true);
                    pqMgr.queuePrint(billPrint.getJobForQueue());
                    printMgr.addJob(prtDevice.getIP(), billPrint);
                }
            }
        } else {
            if (printJobMgr != null) {
                int receiptCopy = 1;

                if (doubleReceipts)
                    receiptCopy = 2;

                for (int i = 0; i < receiptCopy; i++) {
                    String uuid = pqMgr.getDataUUID(prtTitle.getBill_NO());

                    BillPrint billPrint = new BillPrint(uuid, Long.valueOf(prtTitle.getBizDate()));

                    billPrint.setPrinterIp(prtDevice.getIP());
                    //set page size
                    if (this.service.isTMU220(name)) {
                        billPrint.setCharSize(33);
                        billPrint.AddRestaurantInfo(null,
                                prtTitle.getRestaurantName(),
                                prtTitle.getAddressDetail(), prtTitle.getOptions());
                    } else {
                        if (this.service.isTM88(name)) {
                            billPrint.setCharSize(42);
                        } else {
                            billPrint.setCharSize(48);
                        }
                        billPrint.AddRestaurantInfo(prtTitle.getLogo(),
                                prtTitle.getRestaurantName(),
                                prtTitle.getAddressDetail(), prtTitle.getOptions());

                    }

                    String tableName;
                    if (prtTitle.getCopy() == 2) {
                        tableName = prtTitle.getTableName() + "(Reprint Bill Copy)";
                    } else {
                        tableName = prtTitle.getTableName();
                    }

                    billPrint.AddHeader(theOrder.getIsTakeAway(), tableName,
                            theOrder.getPersons(),
                            theOrder.getNumTag() + prtTitle.getBill_NO(), prtTitle.getPos(),
                            prtTitle.getOp(), prtTitle.getDate() + " " + prtTitle.getTime(), theOrder.getNumTag() + theOrder.getOrderNo().toString());
                    billPrint.AddContentListHeader(PrintService.instance.getResources().getString(R.string.item),
                            PrintService.instance.getResources().getString(R.string.price),
                            PrintService.instance.getResources().getString(R.string.qty),
                            PrintService.instance.getResources().getString(R.string.total));
                    if (printOrderItemList != null && printOrderItemList.size() > 0) {
                        LinkedHashMap<String, PrintOrderItem> map = new LinkedHashMap<>();
                        for (int index = 0; index < printOrderItemList.size(); index++) {
                            boolean canMerge = true;
                            PrintOrderItem item = printOrderItemList.get(index).clone();
                            if (orderModifiers != null) {
                                for (int m = 0; m < orderModifiers.size(); m++) {
                                    PrintOrderModifier om = orderModifiers.get(m);
                                    if (om.getOrderDetailId() == item.getOrderDetailId()) {
                                        canMerge = false;
                                        break;
                                    }
                                }
                            } else {
                                canMerge = true;
                            }
                            if (canMerge) {
                                if (map.containsKey(item.getItemDetailId().intValue() + "")) {
                                    PrintOrderItem printOrderItem = map.get(item.getItemDetailId().intValue() + "");
                                    printOrderItem.setQty((Integer.parseInt(printOrderItem.getQty()) + Integer.parseInt(item.getQty())) + "");
                                    printOrderItem.setAmount(BH.add(BH.getBD(printOrderItem.getAmount()), BH.getBD(item.getAmount()), false).toString());
                                    map.put(printOrderItem.getItemDetailId().intValue() + "", printOrderItem);
                                } else {
                                    map.put(item.getItemDetailId().intValue() + "", item);
                                }
                            } else {
                                map.put(item.getItemDetailId().intValue() + "_" + item.getOrderDetailId(), item);
                            }
                        }

                        for (PrintOrderItem item : map.values()) {
                            billPrint.AddOrderItem(item.getItemName(), item.getPrice(),
                                    item.getQty(), item.getAmount(), 1, item.getWeight());
                            //getModifiersByDetailId()
                            ////
                            if (orderModifiers != null) {
                                for (int m = 0; m < orderModifiers.size(); m++) {
                                    PrintOrderModifier om = orderModifiers.get(m);
                                    if (om.getOrderDetailId() == item.getOrderDetailId()) {
                                        if (om.getQty() > 1) {
                                            billPrint.addOrderModifier(om.getItemName() + "x" + om.getQty(), 1, om.getPrice());
                                        } else {
                                            billPrint.addOrderModifier(om.getItemName(), 1, om.getPrice());
                                        }
                                    }
                                }
                            }
                        }

//						for (int index = printOrderItemList.size() - 1;index >= 0;index--) {
//							PrintOrderItem item = printOrderItemList.get(index);
//							billPrint.AddOrderItem(item.getItemName(), item.getPrice(),
//									item.getQty(), item.getAmount(), 1, item.getWeight());
//							//getModifiersByDetailId()
//							////
//							if (orderModifiers != null) {
//								for (int m = 0; m < orderModifiers.size(); m++) {
//									PrintOrderModifier om = orderModifiers.get(m);
//									if (om.getOrderDetailId() == item.getOrderDetailId()) {
//										if (om.getQty() > 1) {
//											billPrint.addOrderModifier(om.getItemName() + "x" + om.getQty(), 1, om.getPrice());
//										} else {
//											billPrint.addOrderModifier(om.getItemName(), 1, om.getPrice());
//										}
//									}
//								}
//							}
//						}
                    }

                    ////////////// Bill Summary
                    String subTotal = BH.getBD(theOrder.getSubTotal()).toString();
                    String discount = BH.getBD(theOrder.getDiscountAmount()).toString();
                    String grandTotal = BH.getBD(theOrder.getTotal()).toString();

                    billPrint.AddBillSummary(subTotal, discount, taxes, grandTotal, rounding, currencySymbol, prtTitle.getSpliteByPax());
                    List<LinkedHashMap<String, String>> stmtList = new ArrayList<LinkedHashMap<String, String>>();
                    if (settlement != null) {
                        // String paymentType = "";
                        String cardNo = null;
                        for (PrintReceiptInfo printReceiptInfo : settlement) {
                            String paymentType = "";
                            LinkedHashMap<String, String> stmt = new LinkedHashMap<String, String>();
                            switch (printReceiptInfo.getPaymentTypeId()) {
                                case ParamConst.SETTLEMENT_TYPE_CASH:
                                    if (!TextUtils.isEmpty(printReceiptInfo.getPaidAmount()) && BH.getBD(printReceiptInfo.getPaidAmount()).compareTo(BH.getBD(ParamConst.DOUBLE_ZERO)) > 0) {
                                        stmt.put(PrintService.instance.getResources().getString(R.string.cash_), BH.add(BH.getBD(printReceiptInfo.getPaidAmount()), BH.getBD(printReceiptInfo.getCashChange()), true).toString());
                                        stmt.put(PrintService.instance.getResources().getString(R.string.changes), BH.getBD(printReceiptInfo.getCashChange()).toString());
                                        isCashSettlement = true;
                                    }
                                    if (isCashSettlement && i == 0) {
                                        if (openDrawer)
                                            this.kickCashDrawer(printer);
                                    }
                                    break;
                                case ParamConst.SETTLEMENT_TYPE_MASTERCARD:
                                    paymentType = PrintService.instance.getResources().getString(R.string.mastercard);
                                    break;
                                case ParamConst.SETTLEMENT_TYPE_UNIPAY:
                                    paymentType = PrintService.instance.getResources().getString(R.string.unionpay);
                                    break;
                                case ParamConst.SETTLEMENT_TYPE_VISA:
                                    paymentType = PrintService.instance.getResources().getString(R.string.visa);
                                    break;
                                case ParamConst.SETTLEMENT_TYPE_AMEX:
                                    paymentType = PrintService.instance.getResources().getString(R.string.amex_);
                                    break;
                                case ParamConst.SETTLEMENT_TYPE_JCB:
                                    paymentType = PrintService.instance.getResources().getString(R.string.jcb);
                                    break;
                                case ParamConst.SETTLEMENT_TYPE_DINNER_INTERMATIONAL:
                                    paymentType = PrintService.instance.getResources().getString(R.string.dinner_intern);
                                    break;
                                case ParamConst.SETTLEMENT_TYPE_BILL_ON_HOLD:
                                    paymentType = PrintService.instance.getResources().getString(R.string.hold_bill);
                                    break;
                                case ParamConst.SETTLEMENT_TYPE_COMPANY:
                                    paymentType = PrintService.instance.getResources().getString(R.string.com_credits);
                                    break;
                                case ParamConst.SETTLEMENT_TYPE_HOURS_CHARGE:
                                    paymentType = PrintService.instance.getResources().getString(R.string.house_charge);
                                    break;
                                case ParamConst.SETTLEMENT_TYPE_VOID:
                                    paymentType = PrintService.instance.getResources().getString(R.string._void);
                                    break;
                                case ParamConst.SETTLEMENT_TYPE_REFUND:
                                    paymentType = PrintService.instance.getResources().getString(R.string._refund);
                                    break;
                                case ParamConst.SETTLEMENT_TYPE_ENTERTAINMENT:
                                    paymentType = PrintService.instance.getResources().getString(R.string.ent);
                                    break;
                                case ParamConst.SETTLEMENT_TYPE_NETS:
                                    paymentType = PrintService.instance.getResources().getString(R.string.nets);
                                    break;
                                case ParamConst.SETTLEMENT_TYPE_ALIPAY:
                                    paymentType = PrintService.instance.getResources().getString(R.string.alipay);
                                    break;
                                case ParamConst.SETTLEMENT_TYPE_EZLINK:
                                    paymentType = "EZ-Link";
                                    break;
                                case ParamConst.SETTLEMENT_TYPE_PAYPAL:
                                    paymentType = PrintService.instance.getResources().getString(R.string.paypal);
                                    break;
                                case ParamConst.SETTLEMENT_TYPE_STORED_CARD:
                                    paymentType = PrintService.instance.getResources().getString(R.string.stored_card);
                                    break;
                                case ParamConst.SETTLEMENT_TYPE_DELIVEROO:
                                    paymentType = PrintService.instance.getResources().getString(R.string.deliveroo);
                                    break;
                                case ParamConst.SETTLEMENT_TYPE_UBEREATS:
                                    paymentType = PrintService.instance.getResources().getString(R.string.ubereats);
                                    break;
                                case ParamConst.SETTLEMENT_TYPE_FOODPANDA:
                                    paymentType = PrintService.instance.getResources().getString(R.string.foodpanda);
                                    break;
                                case ParamConst.SETTLEMENT_TYPE_VOUCHER:
                                    paymentType = PrintService.instance.getResources().getString(R.string.voucher);
                                    break;
                                default:
                                    paymentType = printReceiptInfo.getPaymentTypeName();
                                    break;
                            }
                            if (!TextUtils.isEmpty(paymentType)) {
                                stmt.put(paymentType,
                                        BH.getBD(printReceiptInfo.getPaidAmount()).toString());
                            }
                            if (!TextUtils
                                    .isEmpty(printReceiptInfo.getCardNo())) {
                                stmt.put(PrintService.instance.getResources().getString(R.string.card_no),
                                        "**** " + printReceiptInfo.getCardNo());
                            }
                            stmtList.add(stmt);
                        }
                        billPrint.AddSettlementDetails(stmtList, currencySymbol);
                        billPrint.addCustomizedFieldAtFooter(prtTitle.getFooterOptions());
                        //print check clo
                        billPrint.addCloseBillDate();
                        billPrint.addWelcomeMsg();
                    }
                    /////////
                    billPrint.AddFooter(PrintService.instance.getResources().getString(R.string.powered_by_alfred), false);
                    pqMgr.queuePrint(billPrint.getJobForQueue());
                    printMgr.addJob(prtDevice.getIP(), billPrint);
                }
            }
        }
    }


    @Override
    public void listPrinters(String type) {
        if (type.equals("1")) {
            if (service.registerReceiverBluetooth()) {
                service.SearchBluetooth();
            }
        }
        //   service.SearchUsb();
        final Gson gson = new Gson();
//        Log.e("PrintServiceBinder", " -----1054 listPrinters-----" );
//        if (scheduler == null) {
//            scheduler = Executors.newSingleThreadScheduledExecutor();
//        }

        //stop old finder
//        while (true) {
//            try {
//                Finder.stop();
//                break;
//            } catch (EpsonIoException e) {
//                if (e.getStatus() != IoStatus.ERR_PROCESSING) {
//                    break;
//                }
//            }
//        }

        //stop find thread
//        if (future != null) {
//            future.cancel(false);
//            while (!future.isDone()) {
//                try {
//                    Thread.sleep(500);
//                } catch (Exception e) {
//                    break;
//                }
//            }
//            future = null;
//        }

//        try {
        // Finder.start(this.service.getBaseContext(), DevType.TCP, "255.255.255.255");
//        } catch (EpsonIoException e1) {
//            e1.printStackTrace();
//        }


        mFilterOption = new FilterOption();
        mFilterOption.setDeviceType(Discovery.TYPE_PRINTER);
        mFilterOption.setEpsonFilter(Discovery.FILTER_NAME);
//        mFilterOption.setPortType(Discovery.PORTTYPE_BLUETOOTH);
        try {

            Discovery.start(App.instance, mFilterOption, mDiscoveryListener);
        } catch (Exception e) {
            //    ShowMsg.showException(e, "start", mContext);
//                    e.printStackTrace();
        }
//					//开始搜索蓝牙设备
//					//

        Log.e("PrintServiceBinder", " -----start-----");

        //网络打印机
        Log.e("PrintServiceBinder", " -----devices-----");
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//
        ////
        Map<String, String> ret = new HashMap<String, String>();
        if(MachineUtil.isHisense()){
//            SerialPortFinder serialPortFinder = new SerialPortFinder();
//            String device = serialPortFinder.getHisensePrinterDevice();
//            if(!TextUtils.isEmpty(device)) {
                ret.put("127.0.0.1", "Local Print");
//            }
        }else if (mBluetoothAdapter != null) {
            String innerprinter_address = "00:11:22:33:44:55";
            BluetoothDevice innerprinter_device = null;
            Set<BluetoothDevice> bluetoothDevices = mBluetoothAdapter.getBondedDevices();
            for (BluetoothDevice device : bluetoothDevices) {


                if (device.getAddress().equals(innerprinter_address)) {
                    innerprinter_device = device;

                }
            }


            if (innerprinter_device != null) {
                Log.d("PrintServiceBinder", " ----- deviceList.add-----");
                ret.put("127.0.0.1", "Local Print");

            }
        }
//
        synchronized (service.mCallbacks) {
            for (IAlfredRemotePrintServiceCallback listener : service.mCallbacks) {


                try {
                    listener.fromService("PRINTS_FOUND", gson.toJson(ret));
                    Log.d("PrintServiceBinder", " ----- 1153-----" + gson.toJson(ret));
                } catch (RemoteException e) {
                    Log.w("Printer Lookup", "Failed to notify listener " + listener, e);
                }
            }
        }


//        //start thread
//        future = scheduler.scheduleWithFixedDelay(new Runnable() {
//            @Override
//            public void run() {
////                List<String> deviceList = new ArrayList<String>();
////                boolean hasLocal = false;
//
//
//
//
////                mFilterOption = new FilterOption();
////                mFilterOption.setDeviceType(Discovery.TYPE_PRINTER);
////                mFilterOption.setEpsonFilter(Discovery.FILTER_NAME);
////                try {
////
////                    Discovery.start(App.instance, mFilterOption, mDiscoveryListener);
////                }
////                catch (Exception e) {
////                //    ShowMsg.showException(e, "start", mContext);
//////                    e.printStackTrace();
////                }
//////					//开始搜索蓝牙设备
//////					//
////
////                Log.e("PrintServiceBinder", " -----start-----" );
////                service.SearchBluetooth();
////                //网络打印机
////                Log.e("PrintServiceBinder", " -----devices-----" );
////                BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//////
////                ////
////                Map<String, String> ret = new HashMap<String, String>();
////                if (mBluetoothAdapter != null) {
////                    String innerprinter_address = "00:11:22:33:44:55";
////                    BluetoothDevice innerprinter_device = null;
////                    Set<BluetoothDevice> bluetoothDevices = mBluetoothAdapter.getBondedDevices();
////                    for (BluetoothDevice device : bluetoothDevices) {
////
////
////                        if (device.getAddress().equals(innerprinter_address)) {
////                            innerprinter_device = device;
////
////                        }
////                    }
////
////
////                    if (innerprinter_device != null) {
////                        Log.d("PrintServiceBinder", " ----- deviceList.add-----" );
////                        ret.put("127.0.0.1", "Local Print");
////
////                    }
////                }
//////
////                synchronized (service.mCallbacks) {
////                    for (IAlfredRemotePrintServiceCallback listener : service.mCallbacks) {
////
////
////                        try {
////                            listener.fromService("PRINTS_FOUND", gson.toJson(ret));
////                            Log.d("PrintServiceBinder", " ----- 1153-----" + gson.toJson(ret));
////                        } catch (RemoteException e) {
////                            Log.w("Printer Lookup", "Failed to notify listener " + listener, e);
////                        }
////                    }
////                }
////
//
//                future.cancel(false);
//                future = null;
//                scheduler.shutdown();
//                scheduler = null;
//
//
////                if (deviceList != null && deviceList.size() > 0) {
////                    if (!hasLocal || deviceList.size() > 1) {
////                        future.cancel(false);
////                        future = null;
////                        scheduler.shutdown();
////                        scheduler = null;
////                    }
////                    Map<String, String> ret = new HashMap<String, String>();
////                    Log.d("PrintServiceBinder", " ----- 1141-----" +deviceList.size());
////
////                    for (int i = 0; i < deviceList.size(); i++) {
////                        ret.put(deviceList.get(i), getPrinterName(deviceList.get(i)));
////                        Log.d("PrintServiceBinder", " ----- 1141-----" +deviceList.get(i));
////                    }
////
////                    synchronized (service.mCallbacks) {
////                        for (IAlfredRemotePrintServiceCallback listener : service.mCallbacks) {
////
////
////                            try {
////                                listener.fromService("PRINTS_FOUND", gson.toJson(ret));
////                                Log.d("PrintServiceBinder", " ----- 1153-----" + gson.toJson(ret));
////                            } catch (RemoteException e) {
////                                Log.w("Printer Lookup", "Failed to notify listener " + listener, e);
////                            }
////                        }
////                    }
////
////                }
//            }
//
//
//        }, 0, 500, TimeUnit.MILLISECONDS);
    }


    public void closeDiscovery() {
        try {
            Discovery.stop();
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }

    private DiscoveryListener mDiscoveryListener = new DiscoveryListener() {
        @Override
        public void onDiscovery(final DeviceInfo deviceInfo) {

            Log.e("DeviceInfo", " -----DeviceInfo-----" + deviceInfo.getIpAddress());
            final Gson gson = new Gson();
            Map<String, String> ret = new HashMap<String, String>();

            ret.put(deviceInfo.getIpAddress(), deviceInfo.getDeviceName());
            synchronized (service.mCallbacks) {
                for (IAlfredRemotePrintServiceCallback listener : service.mCallbacks) {


                    try {
                        listener.fromService("PRINTS_FOUND", gson.toJson(ret));
                        Log.d("PrintServiceBinder", " ----- 1153-----" + gson.toJson(ret));
                    } catch (RemoteException e) {
                        Log.w("Printer Lookup", "Failed to notify listener " + listener, e);
                    }
                }
            }

//            future.cancel(false);
//                        future = null;
//                        scheduler.shutdown();
//                        scheduler = null;

        }
    };

    private String getPrinterName(String textIp) {
        if ("127.0.0.1".equals(textIp)) {
            return "Local Print";
        }
        String fontName = null;
        String printerName = null;
        EpsonIo port = null;
        String method = "";
        try {
            byte[] receiveBuffer = new byte[RESPONSE_MAXBYTE];
            int[] receiveSize = new int[1];
            String[] value = new String[1];
            Boolean ret = false;

            //open
            port = new EpsonIo();
            method = "open";
            port.open(DevType.TCP, textIp, null, this.service);

            clearReceiveBuffer(port);

            //send command(esc/pos)
            // enable printer
            method = "write";
            port.write(CMD_ESC_ENABLE_PRINTER, 0, CMD_ESC_ENABLE_PRINTER.length, SEND_RESPONSE_TIMEOUT);

            {    // printe name
                method = "write";
                port.write(CMD_GS_I_PRINTER_NAME, 0, CMD_GS_I_PRINTER_NAME.length, SEND_RESPONSE_TIMEOUT);

                method = "read";
                Arrays.fill(receiveBuffer, (byte) 0);

                ret = receiveResponse(port, receiveBuffer, receiveSize);
                if ((false != ret) && (0 < receiveSize[0])) {
                    byte[] response = Arrays.copyOf(receiveBuffer, receiveSize[0]);

                    analyzeResponse(response, value);

                    printerName = value[0];
                }
            }

            {    // additional fonts
                method = "write";
                port.write(CMD_GS_I_ADDITIONAL_FONTS, 0, CMD_GS_I_ADDITIONAL_FONTS.length, SEND_RESPONSE_TIMEOUT);

                method = "read";
                Arrays.fill(receiveBuffer, (byte) 0);

                ret = receiveResponse(port, receiveBuffer, receiveSize);
                if ((false != ret) && (0 < receiveSize[0])) {
                    byte[] response = Arrays.copyOf(receiveBuffer, receiveSize[0]);

                    analyzeResponse(response, value);

                    fontName = value[0];
                }
            }

            //close
            method = "close";
            port.close();
        } catch (Exception e) {
            try {
                if (port != null) {
                    port.close();
                    port = null;
                }
            } catch (Exception e1) {
                port = null;
            }
            return null;
        }
        return printerName;
    }

    //receive response

    private void clearReceiveBuffer(EpsonIo port) throws EpsonIoException {
        while (true) {
            try {
                byte[] receiveBuffer = new byte[RESPONSE_MAXBYTE];
                int readSize = 0;
                readSize = port.read(receiveBuffer, 0, receiveBuffer.length, 100);
                if (0 == readSize) {
                    break;
                }
            } catch (EpsonIoException e) {
                if (e.getStatus() == IoStatus.ERR_TIMEOUT) {
                    return;
                } else {
                    throw e;
                }
            }
        }
    }

    //receive response
    private Boolean receiveResponse(EpsonIo port, byte[] receiveBuffer, int[] readSize) throws EpsonIoException {
        if ((null == receiveBuffer) || (0 >= receiveBuffer.length)) {
            return false;
        }

        if ((null == readSize) || (0 >= readSize.length)) {
            return false;
        }

        readSize[0] = 0;

        //receive
        try {
            readSize[0] = port.read(receiveBuffer, 0, receiveBuffer.length, SEND_RESPONSE_TIMEOUT);
        } catch (EpsonIoException e) {
            if (e.getStatus() == IoStatus.ERR_TIMEOUT) {
                return false;
            } else {
                throw e;
            }
        }

        return true;
    }

    private ArrayList<KotItemModifier> getModifiersByDetailId(int detailId, ArrayList<KotItemModifier> modsList) {
        ArrayList<KotItemModifier> result = new ArrayList<KotItemModifier>();
        for (KotItemModifier mod : modsList) {
            if (mod.getKotItemDetailId().intValue() == detailId) {
                result.add(mod);
            }
        }
        return result;
    }

    private ArrayList<KotItemModifier> getComboItemModifier(int printId, List<KotItemModifier> modsList) {
        ArrayList<KotItemModifier> result = new ArrayList<KotItemModifier>();
        for (KotItemModifier mod : modsList) {
            if (mod.getPrinterId().intValue() == printId) {
                result.add(mod);
            }
        }
        return result;
    }

    private ArrayList<KotItemModifier> getComboGeneralModifier(int printId, List<KotItemModifier> modsList) {
        ArrayList<KotItemModifier> result = new ArrayList<KotItemModifier>();
        for (KotItemModifier mod : modsList) {
            if (IntegerUtils.isEmptyOrZero(mod.getPrinterId().intValue())) {
                result.add(mod);
            }
        }
        return result;
    }


    private List<String> getModifierNameStr(ArrayList<KotItemModifier> modsList) {
        ArrayList<String> result = new ArrayList<String>();
        for (KotItemModifier mod : modsList) {
            if (!IntegerUtils.isEmptyOrZero(mod.getModifierNum()) && mod.getModifierNum().intValue() > 1) {
                result.add(mod.getModifierName() + "x" + mod.getModifierNum().intValue());
            } else {
                result.add(mod.getModifierName());
            }

        }
        return result;
    }

    private int getModifierSizehavePrintId(ArrayList<KotItemModifier> modsList) {
        int i = 0;
        for (KotItemModifier kotItemModifier : modsList) {
            if (!IntegerUtils.isEmptyOrZero(kotItemModifier.getPrinterId())) {
                i++;
            }
        }
        return i;
    }

    private boolean analyzeResponse(byte[] response, String[] value) {
        int currentPos = 0;

        if ((null == value) || (0 >= value.length)) {
            return false;
        }
        value[0] = "";

        //search 5f header
        for (currentPos = 0; currentPos < response.length; currentPos++) {
            if (response[currentPos] == RESPONSE_HEADER) {
                currentPos++;
                break;
            }
        }

        if (currentPos >= response.length) {
            return false;
        }

        // terminater check
        int endPos = 0;
        for (endPos = currentPos; endPos < response.length; endPos++) {
            if (response[endPos] == RESPONSE_TERMINAL) {
                break;
            }
        }

        if (endPos == currentPos) {
            return true;
        }

        //get response string
        String responseString = null;
        try {
            responseString = new String(response, currentPos, endPos - currentPos, "US-ASCII");
        } catch (UnsupportedEncodingException e) {
            return false;
        }

        value[0] = responseString;

        return true;
    }

    public void onStop() {

        if (future != null) {
            future.cancel(false);
            while (!future.isDone()) {
                try {
                    Thread.sleep(500);
                } catch (Exception e) {
                    break;
                }
            }
            future = null;
        }

        if (scheduler != null)
            scheduler.shutdown();
    }

    @Override
    public void clearPrint() throws RemoteException {
        // TODO Auto-generated method stub
        this.service.getPrintMgr().stop();
        this.service.getPrintMgr().clear();
        this.service.stopSelf();
    }

    @Override
    public void deleteOldPrinterMsg(String businessDate) throws RemoteException {
        if (TextUtils.isEmpty(businessDate))
            return;
        long date = Long.parseLong(businessDate);
        PrintQueueMsgSQL.deletePrintQueueMsgByBusinessDate(date);
    }

    @Override
    public void configure(int country, int lang, int dollarsign) throws RemoteException {
        this.service.configure(country, lang, dollarsign);
    }

    @Override
    public void printKioskKOT(String printer, String summary, String detail,
                              String modifiers, boolean oneprint, boolean doublePrint, String orderNo, int kotFontSize)
            throws RemoteException {
        Gson gson = new Gson();
        PrinterDevice prtDevice = gson.fromJson(printer, PrinterDevice.class);
        KotSummary kotsummary = gson.fromJson(summary, KotSummary.class);
        ArrayList<KotItemDetail> itemDetailsList = gson.fromJson(detail, new TypeToken<ArrayList<KotItemDetail>>() {
        }.getType());
        ArrayList<KotItemModifier> modifiersList = gson.fromJson(modifiers, new TypeToken<ArrayList<KotItemModifier>>() {
        }.getType());
        String name = prtDevice.getName();

        PrintManager printJobMgr = this.service.getPrintMgr();
        PrinterQueueManager pqMgr = this.service.getPqMgr();

        int copies = 1;
        if (doublePrint == true)
            copies = 2;

        for (int i = 0; i < copies; i++) {
            if (oneprint) {
                if (printJobMgr != null) {
                    String uuid = pqMgr.getDataUUID(kotsummary.getKotIdString());

                    KOTPrint kot = new KOTPrint(uuid, kotsummary.getBusinessDate());

                    //set page size
                    if (this.service.isTMU220(name)) {
                        kot.setCharSize(33);
                    } else if (this.service.isTM88(name)) {
                        kot.setCharSize(42);
                    } else {
                        kot.setCharSize(48);
                    }

                    //kot.AddTitle(kotsummary.getRevenueCenterName(),kotsummary.getTableName());
                    if (!TextUtils.isEmpty(orderNo))
                        kot.AddKioskHeader(kotsummary, orderNo);
                    else
                        kot.AddKioskHeader(kotsummary, kotsummary.getOrderNoString());

                    kot.setPrinterIp(prtDevice.getIP());
                    if (!TextUtils.isEmpty(kotsummary.getDescription())) {
                        kot.addCenterLabel(kotsummary.getDescription(), kotFontSize);
                    }
                    if (itemDetailsList.size() >= 0) {
                        KotItemDetail kotitem = itemDetailsList.get(0);
                        if (kotitem.getKotStatus().intValue() == ParamConst.KOT_STATUS_VOID) {
                            kot.addCenterLabel(PrintService.instance.getResources().getString(R.string.void_), kotFontSize);
                        }
                    }

                    kot.AddContentListHeader2Cols(PrintService.instance.getResources().getString(R.string.item_name),
                            PrintService.instance.getResources().getString(R.string.qty));
                    boolean canPrint = false;
                    for (KotItemDetail item : itemDetailsList) {
                        kot.AddKotItem(item.getItemName(), item.getItemNum(), kotFontSize);
                        ArrayList<KotItemModifier> modList = getModifiersByDetailId(item.getId().intValue(), modifiersList);
                        int size = getModifierSizehavePrintId(modList);
                        if (size != 0) {
                            canPrint = false;
                            for (KotItemModifier kotItemModifier : modList) {
                                if (IntegerUtils.isEmptyOrZero(kotItemModifier.getPrinterId())
                                        || kotItemModifier.getPrinterId().intValue() == prtDevice.getDevice_id()) {
                                    canPrint = true;
                                    if (!IntegerUtils.isEmptyOrZero(kotItemModifier.getModifierNum()) && kotItemModifier.getModifierNum().intValue() > 1) {
                                        kot.AddModifierItem("-" + kotItemModifier.getModifierName() + "x" + kotItemModifier.getModifierNum().intValue(), 1);
                                    } else {
                                        kot.AddModifierItem("-" + kotItemModifier.getModifierName(), 1);
                                    }

                                }
                            }
                        } else {
                            canPrint = true;
                            List<String> mods = getModifierNameStr(modList);
                            for (String mod : mods) {
                                kot.AddModifierItem("-" + mod, 1);
                            }
                        }
                        if (!TextUtils.isEmpty(item.getSpecialInstractions())) {
                            kot.AddModifierItem("*" + item.getSpecialInstractions() + "*", 1);
                        }
                        kot.addLineSpace(1);
                    }

                    if (i == 1) {
                        kot.AddFooter(PrintService.instance.getResources().getString(R.string.kot_copy), kotsummary.getOrderRemark());
                    } else {
                        kot.AddFooter(TimeUtil.getTime(), kotsummary.getOrderRemark());
                    }
                    if (canPrint) {
                        pqMgr.queuePrint(kot.getJobForQueue());
                        printJobMgr.addJob(prtDevice.getIP(), kot);
                    }
                }
            } else {
                if (printJobMgr != null) {
                    for (KotItemDetail item : itemDetailsList) {
                        ArrayList<KotItemModifier> modList = getModifiersByDetailId(item.getId().intValue(), modifiersList);
                        int size = getModifierSizehavePrintId(modList);
                        if (size != 0) {
                            List<KotItemModifier> comboItems = getComboItemModifier(prtDevice.getDevice_id(), modList);
                            List<KotItemModifier> comboGeneralModifiers = getComboGeneralModifier(prtDevice.getDevice_id(), modList);
                            for (KotItemModifier kotItemModifier : comboItems) {
                                String uuid = pqMgr.getDataUUID(kotsummary.getKotIdString());

                                KOTPrint kot = new KOTPrint(uuid, kotsummary.getBusinessDate());

                                //set page size
                                if (this.service.isTMU220(name)) {
                                    kot.setCharSize(33);
                                } else if (this.service.isTM88(name)) {
                                    kot.setCharSize(42);
                                } else {
                                    kot.setCharSize(48);
                                }
                                //kot.AddTitle(kotsummary.getRevenueCenterName(),kotsummary.getTableName());
                                if (!TextUtils.isEmpty(kotsummary.getDescription())) {
                                    kot.addCenterLabel(kotsummary.getDescription(), kotFontSize);
                                }
                                if (item.getKotStatus().intValue() == ParamConst.KOT_STATUS_VOID) {
                                    kot.addCenterLabel(PrintService.instance.getResources().getString(R.string.void_), kotFontSize);
                                }
                                if (!TextUtils.isEmpty(orderNo))
                                    kot.AddKioskHeader(kotsummary, orderNo);
                                else
                                    kot.AddKioskHeader(kotsummary, kotsummary.getOrderNoString());
                                kot.setPrinterIp(prtDevice.getIP());
                                kot.AddContentListHeader2Cols(PrintService.instance.getResources().getString(R.string.item_name),
                                        PrintService.instance.getResources().getString(R.string.qty));

                                kot.AddKotItem(item.getItemName(), item.getItemNum(), kotFontSize);
                                if (!IntegerUtils.isEmptyOrZero(kotItemModifier.getModifierNum()) && kotItemModifier.getModifierNum().intValue() > 1) {
                                    kot.AddModifierItem("-" + kotItemModifier.getModifierName() + "x" + kotItemModifier.getModifierNum().intValue(), 1);
                                } else {
                                    kot.AddModifierItem("-" + kotItemModifier.getModifierName(), 1);
                                }

                                for (KotItemModifier kotGeneralItemModifier : comboGeneralModifiers) {
                                    if (!IntegerUtils.isEmptyOrZero(kotGeneralItemModifier.getModifierNum()) && kotGeneralItemModifier.getModifierNum().intValue() > 1) {
                                        kot.AddModifierItem("-" + kotGeneralItemModifier.getModifierName() + "x" + kotGeneralItemModifier.getModifierNum().intValue(), 1);
                                    } else {
                                        kot.AddModifierItem("-" + kotGeneralItemModifier.getModifierName(), 1);
                                    }

                                }
                                if (!TextUtils.isEmpty(item.getSpecialInstractions())) {
                                    kot.AddModifierItem("*" + item.getSpecialInstractions() + "*", 1);
                                }
                                if (i == 1) {
                                    kot.AddFooter(PrintService.instance.getResources().getString(R.string.kot_copy), kotsummary.getOrderRemark());
                                } else {
                                    kot.AddFooter(TimeUtil.getTime(), kotsummary.getOrderRemark());
                                }
                                pqMgr.queuePrint(kot.getJobForQueue());
                                printJobMgr.addJob(prtDevice.getIP(), kot);
                            }

                        } else {
                            String uuid = pqMgr.getDataUUID(kotsummary.getKotIdString());

                            KOTPrint kot = new KOTPrint(uuid, kotsummary.getBusinessDate());
                            //set page size
                            if (this.service.isTMU220(name)) {
                                kot.setCharSize(33);
                            } else if (this.service.isTM88(name)) {
                                kot.setCharSize(42);
                            } else {
                                kot.setCharSize(48);
                            }
                            //kot.AddTitle(kotsummary.getRevenueCenterName(),kotsummary.getTableName());
                            if (!TextUtils.isEmpty(kotsummary.getDescription())) {
                                kot.addCenterLabel(kotsummary.getDescription(), kotFontSize);
                            }
                            if (item.getKotStatus().intValue() == ParamConst.KOT_STATUS_VOID) {
                                kot.addCenterLabel(PrintService.instance.getResources().getString(R.string.void_), kotFontSize);
                            }
                            if (!TextUtils.isEmpty(orderNo))
                                kot.AddKioskHeader(kotsummary, orderNo);
                            else
                                kot.AddKioskHeader(kotsummary, kotsummary.getOrderNoString());
                            kot.setPrinterIp(prtDevice.getIP());
                            kot.AddContentListHeader2Cols(PrintService.instance.getResources().getString(R.string.item_name),
                                    PrintService.instance.getResources().getString(R.string.qty));

                            kot.AddKotItem(item.getItemName(), item.getItemNum(), kotFontSize);
                            List<String> mods = getModifierNameStr(modList);
                            for (String mod : mods) {
                                kot.AddModifierItem(mod);
                            }
                            if (!TextUtils.isEmpty(item.getSpecialInstractions())) {
                                kot.AddModifierItem("*" + item.getSpecialInstractions() + "*", 1);
                            }
                            if (i == 1) {
                                kot.AddFooter(PrintService.instance.getResources().getString(R.string.kot_copy), kotsummary.getOrderRemark());
                            } else {
                                kot.AddFooter(TimeUtil.getTime(), kotsummary.getOrderRemark());
                            }
                            pqMgr.queuePrint(kot.getJobForQueue());
                            printJobMgr.addJob(prtDevice.getIP(), kot);
                        }

                        //kot.AddFooter(kotsummary.getRevenueCenterName());
//						if (canPrint)

                    }
                }
            }
        }


    }


    public void printAppOrderBill(String printer, String title, String order,
                                  String orderDetail, String modifiers, String tax, String payment,
                                  boolean doubleprint, boolean doubleReceipts, String rounding, String orderNo,
                                  String currencySymbol, boolean openDrawer, boolean isDouble, String info, String appOrderlist) throws RemoteException {


        BH.initFormart(isDouble);
        Gson gson = new Gson();
        boolean isCashSettlement = false;

        PrinterDevice prtDevice = gson.fromJson(printer, PrinterDevice.class);
        PrinterTitle prtTitle = gson.fromJson(title, PrinterTitle.class);
        Order theOrder = gson.fromJson(order, Order.class);
        String name = prtDevice.getName();

        ArrayList<PrintOrderItem> printOrderItemList = gson.fromJson(orderDetail,
                new TypeToken<ArrayList<PrintOrderItem>>() {
                }.getType());

        ArrayList<PrintOrderModifier> orderModifiers = gson.fromJson(modifiers,
                new TypeToken<ArrayList<PrintOrderModifier>>() {
                }.getType());

        List<Map<String, String>> taxes = gson.fromJson(tax,
                new TypeToken<List<Map<String, String>>>() {
                }.getType());

        List<PrintReceiptInfo> settlement = gson.fromJson(payment,
                new TypeToken<List<PrintReceiptInfo>>() {
                }.getType());

        List<AppOrder> appOrders = gson.fromJson(appOrderlist,
                new TypeToken<List<AppOrder>>() {
                }.getType());

        PrintManager printMgr = this.service.getPrintMgr();
        JobManager printJobMgr;
//		if (prtDevice.getIP().indexOf(":") != -1) {
//
//		//	printJobMgr = printMgr.configureJobManager("127.0.0.1");
//			Log.d("printJobMgr", " ------"+prtDevice.getIP().replace(":","."));
//			 printJobMgr = printMgr.configureJobManager(prtDevice.getIP().replace(":","."));
//
//
//		}else {
        printJobMgr = printMgr.configureJobManager(prtDevice.getIP());

        PrinterQueueManager pqMgr = this.service.getPqMgr();

        if (doubleprint && settlement == null) {
            //double bill print
            if (printJobMgr != null) {
                for (int i = 0; i < 2; i++) {
                    String uuid = pqMgr.getDataUUID(prtTitle.getBill_NO());

                    BillPrint billPrint = new BillPrint(uuid, Long.valueOf(prtTitle.getBizDate()));

                    billPrint.setPrinterIp(prtDevice.getIP());
                    //set page size
                    if (this.service.isTMU220(name)) {
                        billPrint.setCharSize(33);
                        //U220 cannot support image print
                        billPrint.AddRestaurantInfo(null,
                                prtTitle.getRestaurantName(),
                                prtTitle.getAddressDetail(), null);
                    } else {
                        if (this.service.isTM88(name)) {
                            billPrint.setCharSize(42);
                        } else {
                            billPrint.setCharSize(48);
                        }
                        billPrint.AddRestaurantInfo(SettingDataSQL.getSettingDataByUrl(
                                prtTitle.getLogo()).getLogoString(),
                                prtTitle.getRestaurantName(),
                                prtTitle.getAddressDetail(), null);
                    }

                    String orderNo1;
                    if (prtTitle.getCopy() == 2) {
                        orderNo1 = prtTitle.getOrderNo() + "(Reprint Bill Copy)";
                    } else {
                        orderNo1 = prtTitle.getOrderNo();
                    }

                    if (!TextUtils.isEmpty(orderNo))
                        billPrint.AddOrderNo(orderNo);
                    billPrint.AddKioskHeaderAddress(theOrder.getIsTakeAway(), theOrder.getTableName(), theOrder.getPersons(),
                            theOrder.getNumTag() + prtTitle.getBill_NO(), prtTitle.getPos(),
                            prtTitle.getOp(), prtTitle.getDate() + " " + prtTitle.getTime(), theOrder.getNumTag() + orderNo1, prtTitle.getGroupNum(), info);

                    billPrint.AddContentListHeader(PrintService.instance.getResources().getString(R.string.item),
                            PrintService.instance.getResources().getString(R.string.price),
                            PrintService.instance.getResources().getString(R.string.qty),
                            PrintService.instance.getResources().getString(R.string.total));//("Item Name", "QTY");

                    if (printOrderItemList != null && printOrderItemList.size() > 0) {
                        LinkedHashMap<String, PrintOrderItem> map = new LinkedHashMap<>();
                        for (int index = 0; index < printOrderItemList.size(); index++) {
                            boolean canMerge = true;
                            PrintOrderItem item = printOrderItemList.get(index).clone();
                            if (orderModifiers != null) {
                                for (int m = 0; m < orderModifiers.size(); m++) {
                                    PrintOrderModifier om = orderModifiers.get(m);
                                    if (om.getOrderDetailId() == item.getOrderDetailId()) {
                                        canMerge = false;
                                        break;
                                    }
                                }
                            } else {
                                canMerge = true;
                            }
                            if (canMerge) {
                                if (map.containsKey(item.getItemDetailId().intValue() + "")) {
                                    PrintOrderItem printOrderItem = map.get(item.getItemDetailId().intValue() + "");
                                    printOrderItem.setQty((Integer.parseInt(printOrderItem.getQty()) + Integer.parseInt(item.getQty())) + "");
                                    printOrderItem.setAmount(BH.add(BH.getBD(printOrderItem.getAmount()), BH.getBD(item.getAmount()), false).toString());
                                    map.put(printOrderItem.getItemDetailId().intValue() + "", printOrderItem);
                                } else {
                                    map.put(item.getItemDetailId().intValue() + "", item);
                                }
                            } else {
                                map.put(item.getItemDetailId().intValue() + "_" + item.getOrderDetailId(), item);
                            }
                        }

                        for (PrintOrderItem item : map.values()) {
                            billPrint.AddOrderItem(item.getItemName(), item.getPrice(),
                                    item.getQty(), item.getAmount(), 1, item.getWeight());
                            //getModifiersByDetailId()
                            ////
                            if (orderModifiers != null) {
                                for (int m = 0; m < orderModifiers.size(); m++) {
                                    PrintOrderModifier om = orderModifiers.get(m);
                                    if (om.getOrderDetailId() == item.getOrderDetailId()) {
                                        if (om.getQty() > 1) {
                                            billPrint.addOrderModifier(om.getItemName() + "x" + om.getQty(), 1, om.getPrice());
                                        } else {
                                            billPrint.addOrderModifier(om.getItemName(), 1, om.getPrice());
                                        }
                                    }
                                }
                            }
                        }

//						for (int index = printOrderItemList.size() - 1;index >= 0;index--) {
//							PrintOrderItem item = printOrderItemList.get(index);
//							billPrint.AddOrderItem(item.getItemName(), item.getPrice(),
//									item.getQty(), item.getAmount(), 1, item.getWeight());
//							//getModifiersByDetailId()
//							////
//							if (orderModifiers != null) {
//								for (int m = 0; m < orderModifiers.size(); m++) {
//									PrintOrderModifier om = orderModifiers.get(m);
//									if (om.getOrderDetailId() == item.getOrderDetailId()) {
//										if (om.getQty() > 1) {
//											billPrint.addOrderModifier(om.getItemName() + "x" + om.getQty(), 1, om.getPrice());
//										} else {
//											billPrint.addOrderModifier(om.getItemName(), 1, om.getPrice());
//										}
//									}
//								}
//							}
//						}
                    }

                    ////////////// Bill Summary
                    String subTotal = BH.getBD(theOrder.getSubTotal()).toString();
                    String discount = BH.getBD(theOrder.getDiscountAmount()).toString();
                    String grandTotal = BH.getBD(theOrder.getTotal()).toString();
                    billPrint.AddBillSummary(subTotal, discount, taxes, grandTotal, rounding, currencySymbol);
                    billPrint.addCustomizedFieldAtFooter(prtTitle.getFooterOptions());
                    billPrint.AddFooter(PrintService.instance.getResources().getString(R.string.powered_by_alfred), true);
                    pqMgr.queuePrint(billPrint.getJobForQueue());
                    printMgr.addJob(prtDevice.getIP(), billPrint);
                }
            }
        } else {
            if (printJobMgr != null) {
                int receiptCopy = 1;

                if (doubleReceipts && (settlement != null))
                    receiptCopy = 2;

                for (int i = 0; i < receiptCopy; i++) {
                    String uuid = pqMgr.getDataUUID(prtTitle.getBill_NO());

                    BillPrint billPrint = new BillPrint(uuid, Long.valueOf(prtTitle.getBizDate()));
                    billPrint.setPrinterIp(prtDevice.getIP());


                    //set page size
                    if (this.service.isTMU220(name)) {
                        billPrint.setCharSize(33);
                        if (appOrders==null||appOrders.size()<=0) {
                            billPrint.AddRestaurantInfo(null,
                                    prtTitle.getRestaurantName(),
                                    prtTitle.getAddressDetail(), null);
                        }
                    } else {
                        if (this.service.isTM88(name)) {
                            billPrint.setCharSize(42);
                        } else {
                            billPrint.setCharSize(48);
                        }
                        if (appOrders==null||appOrders.size()<=0) {
                            billPrint.AddRestaurantInfo(prtTitle.getLogo(),
                                    prtTitle.getRestaurantName(),
                                    prtTitle.getAddressDetail(), null);
                        }

                    }
                    if (appOrders==null||appOrders.size()<=0) {
                        String orderNo1;
                        if (prtTitle.getCopy() == 2) {
                            orderNo1 = prtTitle.getOrderNo() + "(Reprint Bill Copy)";
                        } else {
                            orderNo1 = prtTitle.getOrderNo();
                        }

                        if (!TextUtils.isEmpty(orderNo))
                            billPrint.AddOrderNo(orderNo);
                        billPrint.AddKioskHeaderAddress(theOrder.getIsTakeAway(), theOrder.getTableName(), theOrder.getPersons(),
                                theOrder.getNumTag() + prtTitle.getBill_NO(), prtTitle.getPos(),
                                prtTitle.getOp(), prtTitle.getDate() + " " + prtTitle.getTime(), theOrder.getNumTag() + orderNo1, prtTitle.getGroupNum(), info);

                        billPrint.AddContentListHeader(PrintService.instance.getResources().getString(R.string.item),
                                PrintService.instance.getResources().getString(R.string.price),
                                PrintService.instance.getResources().getString(R.string.qty),
                                PrintService.instance.getResources().getString(R.string.total));


                        if (printOrderItemList != null && printOrderItemList.size() > 0) {
                            LinkedHashMap<String, PrintOrderItem> map = new LinkedHashMap<>();
                            for (int index = 0; index < printOrderItemList.size(); index++) {
                                boolean canMerge = true;
                                PrintOrderItem item = printOrderItemList.get(index).clone();
                                if (orderModifiers != null) {
                                    for (int m = 0; m < orderModifiers.size(); m++) {
                                        PrintOrderModifier om = orderModifiers.get(m);
                                        if (om.getOrderDetailId() == item.getOrderDetailId()) {
                                            canMerge = false;
                                            break;
                                        }
                                    }
                                } else {
                                    canMerge = true;
                                }
                                if (canMerge) {
                                    if (map.containsKey(item.getItemDetailId().intValue() + "")) {
                                        PrintOrderItem printOrderItem = map.get(item.getItemDetailId().intValue() + "");
                                        printOrderItem.setQty((Integer.parseInt(printOrderItem.getQty()) + Integer.parseInt(item.getQty())) + "");
                                        printOrderItem.setAmount(BH.add(BH.getBD(printOrderItem.getAmount()), BH.getBD(item.getAmount()), false).toString());
                                        map.put(printOrderItem.getItemDetailId().intValue() + "", printOrderItem);
                                    } else {
                                        map.put(item.getItemDetailId().intValue() + "", item);
                                    }
                                } else {
                                    map.put(item.getItemDetailId().intValue() + "_" + item.getOrderDetailId(), item);
                                }
                            }

                            for (PrintOrderItem item : map.values()) {
                                billPrint.AddOrderItem(item.getItemName(), item.getPrice(),
                                        item.getQty(), item.getAmount(), 1, item.getWeight());
                                //getModifiersByDetailId()
                                ////
                                if (orderModifiers != null) {
                                    for (int m = 0; m < orderModifiers.size(); m++) {
                                        PrintOrderModifier om = orderModifiers.get(m);
                                        if (om.getOrderDetailId() == item.getOrderDetailId()) {
                                            if (om.getQty() > 1) {
                                                billPrint.addOrderModifier(om.getItemName() + "x" + om.getQty(), 1, om.getPrice());
                                            } else {
                                                billPrint.addOrderModifier(om.getItemName(), 1, om.getPrice());
                                            }
                                        }
                                    }
                                }
                            }

//						for (int index = printOrderItemList.size() - 1;index >= 0;index--) {
//							PrintOrderItem item = printOrderItemList.get(index);
//							billPrint.AddOrderItem(item.getItemName(), item.getPrice(),
//									item.getQty(), item.getAmount(), 1, item.getWeight());
//							//getModifiersByDetailId()
//							////
//							if (orderModifiers != null) {
//								for (int m = 0; m < orderModifiers.size(); m++) {
//									PrintOrderModifier om = orderModifiers.get(m);
//									if (om.getOrderDetailId() == item.getOrderDetailId()) {
//										if (om.getQty() > 1) {
//											billPrint.addOrderModifier(om.getItemName() + "x" + om.getQty(), 1, om.getPrice());
//										} else {
//											billPrint.addOrderModifier(om.getItemName(), 1, om.getPrice());
//										}
//									}
//								}
//							}
//						}
                        }
                        ////////////// Bill Summary
                        String subTotal = BH.getBD(theOrder.getSubTotal()).toString();
                        String discount = BH.getBD(theOrder.getDiscountAmount()).toString();
                        String grandTotal = BH.getBD(theOrder.getTotal()).toString();

                        billPrint.AddBillSummary(subTotal, discount, taxes, grandTotal, rounding, currencySymbol);
                        List<LinkedHashMap<String, String>> stmtList = new ArrayList<LinkedHashMap<String, String>>();
                        if (settlement != null) {
                            //   String paymentType = "";
                            String cardNo = null;

                            for (PrintReceiptInfo printReceiptInfo : settlement) {
                                LinkedHashMap<String, String> stmt = new LinkedHashMap<String, String>();
                                String paymentType = "";
                                switch (printReceiptInfo.getPaymentTypeId()) {
                                    case ParamConst.SETTLEMENT_TYPE_CASH:
                                        if (!TextUtils.isEmpty(printReceiptInfo.getPaidAmount()) && BH.getBD(printReceiptInfo.getPaidAmount()).compareTo(BH.getBD(ParamConst.DOUBLE_ZERO)) > 0) {
                                            stmt.put(PrintService.instance.getResources().getString(R.string.cash), BH.add(BH.getBD(printReceiptInfo.getPaidAmount()), BH.getBD(printReceiptInfo.getCashChange()), true).toString());
                                            stmt.put(PrintService.instance.getResources().getString(R.string.changes), BH.getBD(printReceiptInfo.getCashChange()).toString());
                                            isCashSettlement = true;
                                        }
                                        if (isCashSettlement && i == 0) {
                                            if (openDrawer)
                                                this.kickCashDrawer(printer);
                                        }
                                        break;
                                    case ParamConst.SETTLEMENT_TYPE_MASTERCARD:
                                        paymentType = PrintService.instance.getResources().getString(R.string.mastercard);
                                        break;
                                    case ParamConst.SETTLEMENT_TYPE_UNIPAY:
                                        paymentType = PrintService.instance.getResources().getString(R.string.unionpay);
                                        break;
                                    case ParamConst.SETTLEMENT_TYPE_VISA:
                                        paymentType = PrintService.instance.getResources().getString(R.string.visa);
                                        break;
                                    case ParamConst.SETTLEMENT_TYPE_AMEX:
                                        paymentType = PrintService.instance.getResources().getString(R.string.amex_);
                                        break;
                                    case ParamConst.SETTLEMENT_TYPE_JCB:
                                        paymentType = PrintService.instance.getResources().getString(R.string.jcb);
                                        break;
                                    case ParamConst.SETTLEMENT_TYPE_DINNER_INTERMATIONAL:
                                        paymentType = PrintService.instance.getResources().getString(R.string.dinner_intern);
                                        break;
                                    case ParamConst.SETTLEMENT_TYPE_BILL_ON_HOLD:
                                        paymentType = PrintService.instance.getResources().getString(R.string.hold_bill);
                                        break;
                                    case ParamConst.SETTLEMENT_TYPE_COMPANY:
                                        paymentType = PrintService.instance.getResources().getString(R.string.com_credits);
                                        break;
                                    case ParamConst.SETTLEMENT_TYPE_HOURS_CHARGE:
                                        paymentType = PrintService.instance.getResources().getString(R.string.house_charge);
                                        break;
                                    case ParamConst.SETTLEMENT_TYPE_VOID:
                                        paymentType = PrintService.instance.getResources().getString(R.string._void);
                                        break;
                                    case ParamConst.SETTLEMENT_TYPE_REFUND:
                                        paymentType = PrintService.instance.getResources().getString(R.string._refund);
                                        break;
                                    case ParamConst.SETTLEMENT_TYPE_ENTERTAINMENT:
                                        paymentType = PrintService.instance.getResources().getString(R.string.ent);
                                        break;
                                    case ParamConst.SETTLEMENT_TYPE_NETS:
                                        paymentType = PrintService.instance.getResources().getString(R.string.nets);
                                        break;
                                    case ParamConst.SETTLEMENT_TYPE_ALIPAY:
                                        paymentType = PrintService.instance.getResources().getString(R.string.alipay);
                                        break;
                                    case ParamConst.SETTLEMENT_TYPE_EZLINK:
                                        paymentType = "EZ-Link";
                                        break;
                                    case ParamConst.SETTLEMENT_TYPE_PAYPAL:
                                        paymentType = PrintService.instance.getResources().getString(R.string.paypal);
                                        break;
                                    case ParamConst.SETTLEMENT_TYPE_STORED_CARD:
                                        paymentType = PrintService.instance.getResources().getString(R.string.stored_card);
                                        break;
                                    case ParamConst.SETTLEMENT_TYPE_DELIVEROO:
                                        paymentType = PrintService.instance.getResources().getString(R.string.deliveroo);
                                        break;
                                    case ParamConst.SETTLEMENT_TYPE_UBEREATS:
                                        paymentType = PrintService.instance.getResources().getString(R.string.ubereats);
                                        break;
                                    case ParamConst.SETTLEMENT_TYPE_FOODPANDA:
                                        paymentType = PrintService.instance.getResources().getString(R.string.foodpanda);
                                        break;
                                    case ParamConst.SETTLEMENT_TYPE_VOUCHER:
                                        paymentType = PrintService.instance.getResources().getString(R.string.voucher);
                                        break;
                                    default:
                                        paymentType = printReceiptInfo.getPaymentTypeName();
                                        break;
                                }
                                if (!TextUtils.isEmpty(paymentType)) {
                                    stmt.put(paymentType,
                                            BH.getBD(printReceiptInfo.getPaidAmount()).toString());
                                }
                                if (!TextUtils
                                        .isEmpty(printReceiptInfo.getCardNo())) {
                                    stmt.put(PrintService.instance.getResources().getString(R.string.card_no),
                                            "**** " + printReceiptInfo.getCardNo());
                                }
                                stmtList.add(stmt);
                            }

                            billPrint.AddSettlementDetails(stmtList, currencySymbol);
                            billPrint.addCustomizedFieldAtFooter(prtTitle.getFooterOptions());
                            //print check clo
                            billPrint.addCloseBillDate();
                            billPrint.addWelcomeMsg();
                        }

                        billPrint.AddFooter(PrintService.instance.getResources().getString(R.string.powered_by_alfred), false);
                    } else {

                        //  打印appOrder 地址列表
                        printAddress(billPrint, appOrders);
                    }
                    /////////

                    pqMgr.queuePrint(billPrint.getJobForQueue());
                    printMgr.addJob(prtDevice.getIP(), billPrint);
                }
            }
        }


    }

    private void printAddress(BillPrint billPrint, List<AppOrder> appOrders) {
        int size = appOrders.size();
        for (int j = 0; j < appOrders.size(); j++) {
            AppOrder appOrder = appOrders.get(j);
            StringBuffer str = new StringBuffer();
            if (!TextUtils.isEmpty(appOrder.getContact())) {
                str.append(appOrder.getContact());
            }
            if (!TextUtils.isEmpty(appOrder.getMobile())) {
                String space = "";
                if (str.length() > 0) {
                    space = " ";
                }
                str.append(space + appOrder.getMobile());
            }
//            String addressNext = "";
//            if (str.length() > 0) {
//                addressNext = " ";
//            }
//            str.append(addressNext + appOrder.getAddress() + " ");
//            str.append(TimeUtil.getDeliveryDataTime(appOrder.getDeliveryTime()));
            ///    billPrint.addBillOrderStr(appOrder.getOrderNo().toString() + "\n");
//                            if (TextUtils.isEmpty(str.toString())) {
//                                billPrint.printDeliveryList(appOrder.getOrderNo().toString(), appOrder.getAddress(), 1);
//                            } else {
            billPrint.printDeliveryList(PrintService.instance.getResources().getString(R.string.order_no_)+appOrder.getId().toString(), str.toString(), appOrder.getAddress().trim(),TimeUtil.getDeliveryDataTime(appOrder.getDeliveryTime()));
            //  billPrint.printDeliveryList(" ", appOrder.getAddress().toString(), 1);
//                                billPrint.AddAddress(appOrder.getAddress());
//                            }
            if (j != size - 1) {
                billPrint.addHor();
            }

        }
        //   billPrint.addBillOrderStr(orderNoStr);

        billPrint.addBillDate();
    }


    @Override
    public void printKioskBill(String printer, String title, String order,
                               String orderDetail, String modifiers, String tax, String payment,
                               boolean doubleprint, boolean doubleReceipts, String rounding, String orderNo,
                               String currencySymbol, boolean openDrawer, boolean isDouble) throws RemoteException {
        BH.initFormart(isDouble);
        Gson gson = new Gson();
        boolean isCashSettlement = false;

        PrinterDevice prtDevice = gson.fromJson(printer, PrinterDevice.class);
        PrinterTitle prtTitle = gson.fromJson(title, PrinterTitle.class);
        Order theOrder = gson.fromJson(order, Order.class);
        String name = prtDevice.getName();

        ArrayList<PrintOrderItem> printOrderItemList = gson.fromJson(orderDetail,
                new TypeToken<ArrayList<PrintOrderItem>>() {
                }.getType());

        ArrayList<PrintOrderModifier> orderModifiers = gson.fromJson(modifiers,
                new TypeToken<ArrayList<PrintOrderModifier>>() {
                }.getType());

        List<Map<String, String>> taxes = gson.fromJson(tax,
                new TypeToken<List<Map<String, String>>>() {
                }.getType());

        List<PrintReceiptInfo> settlement = gson.fromJson(payment,
                new TypeToken<List<PrintReceiptInfo>>() {
                }.getType());

        PrintManager printMgr = this.service.getPrintMgr();
        JobManager printJobMgr;
//		if (prtDevice.getIP().indexOf(":") != -1) {
//
//		//	printJobMgr = printMgr.configureJobManager("127.0.0.1");
//			Log.d("printJobMgr", " ------"+prtDevice.getIP().replace(":","."));
//			 printJobMgr = printMgr.configureJobManager(prtDevice.getIP().replace(":","."));
//
//
//		}else {
        printJobMgr = printMgr.configureJobManager(prtDevice.getIP());

        PrinterQueueManager pqMgr = this.service.getPqMgr();

        if (doubleprint && settlement == null) {
            //double bill print
            if (printJobMgr != null) {
                for (int i = 0; i < 2; i++) {
                    String uuid = pqMgr.getDataUUID(prtTitle.getBill_NO());

                    BillPrint billPrint = new BillPrint(uuid, Long.valueOf(prtTitle.getBizDate()));

                    billPrint.setPrinterIp(prtDevice.getIP());
                    //set page size
                    if (this.service.isTMU220(name)) {
                        billPrint.setCharSize(33);
                        //U220 cannot support image print
                        billPrint.AddRestaurantInfo(null,
                                prtTitle.getRestaurantName(),
                                prtTitle.getAddressDetail(), null);
                    } else {
                        if (this.service.isTM88(name)) {
                            billPrint.setCharSize(42);
                        } else {
                            billPrint.setCharSize(48);
                        }
                        billPrint.AddRestaurantInfo(SettingDataSQL.getSettingDataByUrl(
                                prtTitle.getLogo()).getLogoString(),
                                prtTitle.getRestaurantName(),
                                prtTitle.getAddressDetail(), null);
                    }

                    String orderNo1;
                    if (prtTitle.getCopy() == 2) {
                        orderNo1 = prtTitle.getOrderNo() + "(Reprint Bill Copy)";
                    } else {
                        orderNo1 = prtTitle.getOrderNo();
                    }

                    if (!TextUtils.isEmpty(orderNo))
                        billPrint.AddOrderNo(orderNo);
                    billPrint.AddKioskHeader(theOrder.getIsTakeAway(), theOrder.getTableName(), theOrder.getPersons(),
                            theOrder.getNumTag() + prtTitle.getBill_NO(), prtTitle.getPos(),
                            prtTitle.getOp(), prtTitle.getDate() + " " + prtTitle.getTime(), theOrder.getNumTag() + orderNo1, prtTitle.getGroupNum());

                    billPrint.AddContentListHeader(PrintService.instance.getResources().getString(R.string.item),
                            PrintService.instance.getResources().getString(R.string.price),
                            PrintService.instance.getResources().getString(R.string.qty),
                            PrintService.instance.getResources().getString(R.string.total));//("Item Name", "QTY");

                    if (printOrderItemList != null && printOrderItemList.size() > 0) {
                        LinkedHashMap<String, PrintOrderItem> map = new LinkedHashMap<>();
                        for (int index = 0; index < printOrderItemList.size(); index++) {
                            boolean canMerge = true;
                            PrintOrderItem item = printOrderItemList.get(index).clone();
                            if (orderModifiers != null) {
                                for (int m = 0; m < orderModifiers.size(); m++) {
                                    PrintOrderModifier om = orderModifiers.get(m);
                                    if (om.getOrderDetailId() == item.getOrderDetailId()) {
                                        canMerge = false;
                                        break;
                                    }
                                }
                            } else {
                                canMerge = true;
                            }
                            if (canMerge) {
                                if (map.containsKey(item.getItemDetailId().intValue() + "")) {
                                    PrintOrderItem printOrderItem = map.get(item.getItemDetailId().intValue() + "");
                                    printOrderItem.setQty((Integer.parseInt(printOrderItem.getQty()) + Integer.parseInt(item.getQty())) + "");
                                    printOrderItem.setAmount(BH.add(BH.getBD(printOrderItem.getAmount()), BH.getBD(item.getAmount()), false).toString());
                                    map.put(printOrderItem.getItemDetailId().intValue() + "", printOrderItem);
                                } else {
                                    map.put(item.getItemDetailId().intValue() + "", item);
                                }
                            } else {
                                map.put(item.getItemDetailId().intValue() + "_" + item.getOrderDetailId(), item);
                            }
                        }

                        for (PrintOrderItem item : map.values()) {
                            billPrint.AddOrderItem(item.getItemName(), item.getPrice(),
                                    item.getQty(), item.getAmount(), 1, item.getWeight());
                            //getModifiersByDetailId()
                            ////
                            if (orderModifiers != null) {
                                for (int m = 0; m < orderModifiers.size(); m++) {
                                    PrintOrderModifier om = orderModifiers.get(m);
                                    if (om.getOrderDetailId() == item.getOrderDetailId()) {
                                        if (om.getQty() > 1) {
                                            billPrint.addOrderModifier(om.getItemName() + "x" + om.getQty(), 1, om.getPrice());
                                        } else {
                                            billPrint.addOrderModifier(om.getItemName(), 1, om.getPrice());
                                        }
                                    }
                                }
                            }
                        }

//						for (int index = printOrderItemList.size() - 1;index >= 0;index--) {
//							PrintOrderItem item = printOrderItemList.get(index);
//							billPrint.AddOrderItem(item.getItemName(), item.getPrice(),
//									item.getQty(), item.getAmount(), 1, item.getWeight());
//							//getModifiersByDetailId()
//							////
//							if (orderModifiers != null) {
//								for (int m = 0; m < orderModifiers.size(); m++) {
//									PrintOrderModifier om = orderModifiers.get(m);
//									if (om.getOrderDetailId() == item.getOrderDetailId()) {
//										if (om.getQty() > 1) {
//											billPrint.addOrderModifier(om.getItemName() + "x" + om.getQty(), 1, om.getPrice());
//										} else {
//											billPrint.addOrderModifier(om.getItemName(), 1, om.getPrice());
//										}
//									}
//								}
//							}
//						}
                    }

                    ////////////// Bill Summary
                    String subTotal = BH.getBD(theOrder.getSubTotal()).toString();
                    String discount = BH.getBD(theOrder.getDiscountAmount()).toString();
                    String grandTotal = BH.getBD(theOrder.getTotal()).toString();

                    billPrint.AddBillSummary(subTotal, discount, taxes, grandTotal, rounding, currencySymbol);
                    billPrint.addCustomizedFieldAtFooter(prtTitle.getFooterOptions());
                    billPrint.AddFooter(PrintService.instance.getResources().getString(R.string.powered_by_alfred), true);
                    pqMgr.queuePrint(billPrint.getJobForQueue());
                    printMgr.addJob(prtDevice.getIP(), billPrint);
                }
            }
        } else {
            if (printJobMgr != null) {
                int receiptCopy = 1;

                if (doubleReceipts && (settlement != null))
                    receiptCopy = 2;

                for (int i = 0; i < receiptCopy; i++) {
                    String uuid = pqMgr.getDataUUID(prtTitle.getBill_NO());

                    BillPrint billPrint = new BillPrint(uuid, Long.valueOf(prtTitle.getBizDate()));
                    billPrint.setPrinterIp(prtDevice.getIP());
                    //set page size
                    if (this.service.isTMU220(name)) {
                        billPrint.setCharSize(33);
                        billPrint.AddRestaurantInfo(null,
                                prtTitle.getRestaurantName(),
                                prtTitle.getAddressDetail(), null);
                    } else {
                        if (this.service.isTM88(name)) {
                            billPrint.setCharSize(42);
                        } else {
                            billPrint.setCharSize(48);
                        }
                        billPrint.AddRestaurantInfo(prtTitle.getLogo(),
                                prtTitle.getRestaurantName(),
                                prtTitle.getAddressDetail(), null);

                    }

                    String orderNo1;
                    if (prtTitle.getCopy() == 2) {
                        orderNo1 = prtTitle.getOrderNo() + "(Reprint Bill Copy)";
                    } else {
                        orderNo1 = prtTitle.getOrderNo();
                    }

                    if (!TextUtils.isEmpty(orderNo))
                        billPrint.AddOrderNo(orderNo);
                    billPrint.AddKioskHeader(theOrder.getIsTakeAway(), theOrder.getTableName(), theOrder.getPersons(),
                            theOrder.getNumTag() + prtTitle.getBill_NO(), prtTitle.getPos(),
                            prtTitle.getOp(), prtTitle.getDate() + " " + prtTitle.getTime(), theOrder.getNumTag() + orderNo1, prtTitle.getGroupNum());

                    billPrint.AddContentListHeader(PrintService.instance.getResources().getString(R.string.item),
                            PrintService.instance.getResources().getString(R.string.price),
                            PrintService.instance.getResources().getString(R.string.qty),
                            PrintService.instance.getResources().getString(R.string.total));


                    if (printOrderItemList != null && printOrderItemList.size() > 0) {
                        LinkedHashMap<String, PrintOrderItem> map = new LinkedHashMap<>();
                        for (int index = 0; index < printOrderItemList.size(); index++) {
                            boolean canMerge = true;
                            PrintOrderItem item = printOrderItemList.get(index).clone();
                            if (orderModifiers != null) {
                                for (int m = 0; m < orderModifiers.size(); m++) {
                                    PrintOrderModifier om = orderModifiers.get(m);
                                    if (om.getOrderDetailId() == item.getOrderDetailId()) {
                                        canMerge = false;
                                        break;
                                    }
                                }
                            } else {
                                canMerge = true;
                            }
                            if (canMerge) {
                                if (map.containsKey(item.getItemDetailId().intValue() + "")) {
                                    PrintOrderItem printOrderItem = map.get(item.getItemDetailId().intValue() + "");
                                    printOrderItem.setQty((Integer.parseInt(printOrderItem.getQty()) + Integer.parseInt(item.getQty())) + "");
                                    printOrderItem.setAmount(BH.add(BH.getBD(printOrderItem.getAmount()), BH.getBD(item.getAmount()), false).toString());
                                    map.put(printOrderItem.getItemDetailId().intValue() + "", printOrderItem);
                                } else {
                                    map.put(item.getItemDetailId().intValue() + "", item);
                                }
                            } else {
                                map.put(item.getItemDetailId().intValue() + "_" + item.getOrderDetailId(), item);
                            }
                        }

                        for (PrintOrderItem item : map.values()) {
                            billPrint.AddOrderItem(item.getItemName(), item.getPrice(),
                                    item.getQty(), item.getAmount(), 1, item.getWeight());
                            //getModifiersByDetailId()
                            ////
                            if (orderModifiers != null) {
                                for (int m = 0; m < orderModifiers.size(); m++) {
                                    PrintOrderModifier om = orderModifiers.get(m);
                                    if (om.getOrderDetailId() == item.getOrderDetailId()) {
                                        if (om.getQty() > 1) {
                                            billPrint.addOrderModifier(om.getItemName() + "x" + om.getQty(), 1, om.getPrice());
                                        } else {
                                            billPrint.addOrderModifier(om.getItemName(), 1, om.getPrice());
                                        }
                                    }
                                }
                            }
                        }

//						for (int index = printOrderItemList.size() - 1;index >= 0;index--) {
//							PrintOrderItem item = printOrderItemList.get(index);
//							billPrint.AddOrderItem(item.getItemName(), item.getPrice(),
//									item.getQty(), item.getAmount(), 1, item.getWeight());
//							//getModifiersByDetailId()
//							////
//							if (orderModifiers != null) {
//								for (int m = 0; m < orderModifiers.size(); m++) {
//									PrintOrderModifier om = orderModifiers.get(m);
//									if (om.getOrderDetailId() == item.getOrderDetailId()) {
//										if (om.getQty() > 1) {
//											billPrint.addOrderModifier(om.getItemName() + "x" + om.getQty(), 1, om.getPrice());
//										} else {
//											billPrint.addOrderModifier(om.getItemName(), 1, om.getPrice());
//										}
//									}
//								}
//							}
//						}
                    }
                    ////////////// Bill Summary
                    String subTotal = BH.getBD(theOrder.getSubTotal()).toString();
                    String discount = BH.getBD(theOrder.getDiscountAmount()).toString();
                    String grandTotal = BH.getBD(theOrder.getTotal()).toString();

                    billPrint.AddBillSummary(subTotal, discount, taxes, grandTotal, rounding, currencySymbol);
                    List<LinkedHashMap<String, String>> stmtList = new ArrayList<LinkedHashMap<String, String>>();
                    if (settlement != null) {
                        //   String paymentType = "";
                        String cardNo = null;

                        for (PrintReceiptInfo printReceiptInfo : settlement) {
                            LinkedHashMap<String, String> stmt = new LinkedHashMap<String, String>();
                            String paymentType = "";
                            switch (printReceiptInfo.getPaymentTypeId()) {
                                case ParamConst.SETTLEMENT_TYPE_CASH:
                                    if (!TextUtils.isEmpty(printReceiptInfo.getPaidAmount()) && BH.getBD(printReceiptInfo.getPaidAmount()).compareTo(BH.getBD(ParamConst.DOUBLE_ZERO)) > 0) {
                                        stmt.put(PrintService.instance.getResources().getString(R.string.cash), BH.add(BH.getBD(printReceiptInfo.getPaidAmount()), BH.getBD(printReceiptInfo.getCashChange()), true).toString());
                                        stmt.put(PrintService.instance.getResources().getString(R.string.changes), BH.getBD(printReceiptInfo.getCashChange()).toString());
                                        isCashSettlement = true;
                                    }
                                    if (isCashSettlement && i == 0) {
                                        if (openDrawer)
                                            this.kickCashDrawer(printer);
                                    }
                                    break;
                                case ParamConst.SETTLEMENT_TYPE_MASTERCARD:
                                    paymentType = PrintService.instance.getResources().getString(R.string.mastercard);
                                    break;
                                case ParamConst.SETTLEMENT_TYPE_UNIPAY:
                                    paymentType = PrintService.instance.getResources().getString(R.string.unionpay);
                                    break;
                                case ParamConst.SETTLEMENT_TYPE_VISA:
                                    paymentType = PrintService.instance.getResources().getString(R.string.visa);
                                    break;
                                case ParamConst.SETTLEMENT_TYPE_AMEX:
                                    paymentType = PrintService.instance.getResources().getString(R.string.amex_);
                                    break;
                                case ParamConst.SETTLEMENT_TYPE_JCB:
                                    paymentType = PrintService.instance.getResources().getString(R.string.jcb);
                                    break;
                                case ParamConst.SETTLEMENT_TYPE_DINNER_INTERMATIONAL:
                                    paymentType = PrintService.instance.getResources().getString(R.string.dinner_intern);
                                    break;
                                case ParamConst.SETTLEMENT_TYPE_BILL_ON_HOLD:
                                    paymentType = PrintService.instance.getResources().getString(R.string.hold_bill);
                                    break;
                                case ParamConst.SETTLEMENT_TYPE_COMPANY:
                                    paymentType = PrintService.instance.getResources().getString(R.string.com_credits);
                                    break;
                                case ParamConst.SETTLEMENT_TYPE_HOURS_CHARGE:
                                    paymentType = PrintService.instance.getResources().getString(R.string.house_charge);
                                    break;
                                case ParamConst.SETTLEMENT_TYPE_VOID:
                                    paymentType = PrintService.instance.getResources().getString(R.string._void);
                                    break;
                                case ParamConst.SETTLEMENT_TYPE_REFUND:
                                    paymentType = PrintService.instance.getResources().getString(R.string._refund);
                                    break;
                                case ParamConst.SETTLEMENT_TYPE_ENTERTAINMENT:
                                    paymentType = PrintService.instance.getResources().getString(R.string.ent);
                                    break;
                                case ParamConst.SETTLEMENT_TYPE_NETS:
                                    paymentType = PrintService.instance.getResources().getString(R.string.nets);
                                    break;
                                case ParamConst.SETTLEMENT_TYPE_ALIPAY:
                                    paymentType = PrintService.instance.getResources().getString(R.string.alipay);
                                    break;
                                case ParamConst.SETTLEMENT_TYPE_EZLINK:
                                    paymentType = "EZ-Link";
                                    break;
                                case ParamConst.SETTLEMENT_TYPE_PAYPAL:
                                    paymentType = PrintService.instance.getResources().getString(R.string.paypal);
                                    break;
                                case ParamConst.SETTLEMENT_TYPE_STORED_CARD:
                                    paymentType = PrintService.instance.getResources().getString(R.string.stored_card);
                                    break;
                                case ParamConst.SETTLEMENT_TYPE_DELIVEROO:
                                    paymentType = PrintService.instance.getResources().getString(R.string.deliveroo);
                                    break;
                                case ParamConst.SETTLEMENT_TYPE_UBEREATS:
                                    paymentType = PrintService.instance.getResources().getString(R.string.ubereats);
                                    break;
                                case ParamConst.SETTLEMENT_TYPE_FOODPANDA:
                                    paymentType = PrintService.instance.getResources().getString(R.string.foodpanda);
                                    break;
                                case ParamConst.SETTLEMENT_TYPE_VOUCHER:
                                    paymentType = PrintService.instance.getResources().getString(R.string.voucher);
                                    break;
                                default:
                                    paymentType = printReceiptInfo.getPaymentTypeName();
                                    break;
                            }
                            if (!TextUtils.isEmpty(paymentType)) {
                                stmt.put(paymentType,
                                        BH.getBD(printReceiptInfo.getPaidAmount()).toString());
                            }
                            if (!TextUtils
                                    .isEmpty(printReceiptInfo.getCardNo())) {
                                stmt.put(PrintService.instance.getResources().getString(R.string.card_no),
                                        "**** " + printReceiptInfo.getCardNo());
                            }
                            stmtList.add(stmt);
                        }

                        billPrint.AddSettlementDetails(stmtList, currencySymbol);
                        billPrint.addCustomizedFieldAtFooter(prtTitle.getFooterOptions());
                        //print check clo
                        billPrint.addCloseBillDate();
                        billPrint.addWelcomeMsg();
                    }
                    /////////
                    billPrint.AddFooter(PrintService.instance.getResources().getString(R.string.powered_by_alfred), false);
                    pqMgr.queuePrint(billPrint.getJobForQueue());
                    printMgr.addJob(prtDevice.getIP(), billPrint);
                }
            }
        }


    }

    @Override
    public void printBillSummary(String printer, String summary, String detail,
                                 String modifiers, int kotFontSize) throws RemoteException {
        // TODO Auto-generated method stub
        Gson gson = new Gson();

        PrinterDevice prtDevice = gson.fromJson(printer, PrinterDevice.class);
        KotSummary kotsummary = gson.fromJson(summary, KotSummary.class);
        ArrayList<KotItemDetail> itemDetailsList = gson.fromJson(detail, new TypeToken<ArrayList<KotItemDetail>>() {
        }.getType());
        ArrayList<KotItemModifier> modifiersList = gson.fromJson(modifiers, new TypeToken<ArrayList<KotItemModifier>>() {
        }.getType());

        PrintManager printMgr = this.service.getPrintMgr();
        JobManager printJobMgr = printMgr.configureJobManager(prtDevice.getIP());
        PrinterQueueManager pqMgr = this.service.getPqMgr();

        String name = prtDevice.getName();
        boolean oneprint = true;

        if (oneprint) {
            if (printJobMgr != null) {
                String uuid = pqMgr.getDataUUID(kotsummary.getOrderNoString());

                KOTPrint kot = new KOTPrint(uuid, kotsummary.getBusinessDate());
                //set page size
                if (this.service.isTMU220(name)) {
                    kot.setCharSize(33);
                } else if (this.service.isTM88(name)) {
                    kot.setCharSize(42);
                } else {
                    kot.setCharSize(48);
                }
                kot.AddTitle(kotsummary.getRevenueCenterName(), kotsummary.getTableName());
                kot.addCenterLabel(PrintService.instance.getResources().getString(R.string.print_order_summary), 1);
                if (!TextUtils.isEmpty(kotsummary.getDescription())) {
                    kot.addCenterLabel(kotsummary.getDescription(), kotFontSize);
                }
                if (itemDetailsList.size() >= 0) {
                    KotItemDetail kotitem = itemDetailsList.get(0);
                    if (kotitem.getKotStatus().intValue() == ParamConst.KOT_STATUS_VOID) {
                        kot.addCenterLabel(PrintService.instance.getResources().getString(R.string.void_), kotFontSize);
                    }
                }
                kot.AddHeader(kotsummary);
                kot.setPrinterIp(prtDevice.getIP());
                kot.AddContentListHeader2Cols(PrintService.instance.getResources().getString(R.string.item_name),
                        PrintService.instance.getResources().getString(R.string.qty));
                boolean canPrint = false;
                for (KotItemDetail item : itemDetailsList) {
                    kot.AddKotItem(item.getItemName(), item.getItemNum(), kotFontSize);
                    ArrayList<KotItemModifier> modList = getModifiersByDetailId(item.getId().intValue(), modifiersList);
//						   int size = getModifierSizehavePrintId(modList);
//						   if (size != 0){
//							   canPrint = false;
//							   for (KotItemModifier kotItemModifier : modList){
//								   if (IntegerUtils.isEmptyOrZero(kotItemModifier.getPrinterId()) 
//										   || kotItemModifier.getPrinterId().intValue() == prtDevice.getDevice_id()){
//									   canPrint = true;
//									   kot.AddModifierItem(kotItemModifier.getModifierName(),kotFontSize);
//									   
//								   }
//							   }
//						   }else{
                    canPrint = true;
                    List<String> mods = getModifierNameStr(modList);
                    for (String mod : mods) {
                        kot.AddModifierItem("-" + mod, 1);
                    }
//						   }
                    if (!TextUtils.isEmpty(item.getSpecialInstractions())) {
                        kot.AddModifierItem("*" + item.getSpecialInstractions() + "*", 1);
                    }
                    kot.addLineSpace(1);
                }

                kot.AddFooter(TimeUtil.getTime(), kotsummary.getOrderRemark());
                if (canPrint) {
                    pqMgr.queuePrint(kot.getJobForQueue());
                    printMgr.addJob(prtDevice.getIP(), kot);
                }
            }
        }
    }

    //  modifier print
    @Override
    public void printModifierDetailAnalysisReport(String xzType, String printer,
                                                  String title, String pluMod, String category)
            throws RemoteException {
        Gson gson = new Gson();

        PrinterDevice prtDevice = gson.fromJson(printer, PrinterDevice.class);
        PrinterTitle prtTitle = gson.fromJson(title, PrinterTitle.class);
        ArrayList<Modifier> categoryData = gson.fromJson(category, new TypeToken<ArrayList<Modifier>>() {
        }.getType());
        ArrayList<ReportPluDayModifier> modifier = gson.fromJson(pluMod, new TypeToken<ArrayList<ReportPluDayModifier>>() {
        }.getType());

        PrintManager printMgr = this.service.getPrintMgr();
        JobManager printJobMgr = printMgr.configureJobManager(prtDevice.getIP());
        PrinterQueueManager pqMgr = this.service.getPqMgr();

        if (printJobMgr != null) {
            String uuid = pqMgr.getDataUUID(prtTitle.getBill_NO());

            ModifierDetailAnalysisReportPrint daPrint = new ModifierDetailAnalysisReportPrint(uuid, TimeUtil.getPrintingLongDate(prtTitle.getBizDate()));
            String name = prtDevice.getName();
            //set page size
            if (this.service.isTMU220(name)) {
                daPrint.setCharSize(33);
            } else if (this.service.isTM88(name)) {
                daPrint.setCharSize(42);
            } else {
                daPrint.setCharSize(48);
            }
            daPrint.AddReportHeader(prtTitle.getRestaurantName(), xzType, PrintService.instance.getResources().getString(R.string.modifier_analysis));
            daPrint.AddHeader(prtTitle.getOp(), prtTitle.getBill_NO(), prtTitle.getDate() + " " + prtTitle.getTime(), prtTitle.getBizDate());
            daPrint.AddContentListHeader(PrintService.instance.getResources().getString(R.string.plu_name),
                    PrintService.instance.getResources().getString(R.string.price),
                    PrintService.instance.getResources().getString(R.string.qty),
                    PrintService.instance.getResources().getString(R.string.amount));
            daPrint.print(modifier, categoryData);
            daPrint.setPrinterIp(prtDevice.getIP());
            daPrint.AddFooter(prtTitle.getDate() + " " + prtTitle.getTime());
            pqMgr.queuePrint(daPrint.getJobForQueue());
            printMgr.addJob(prtDevice.getIP(), daPrint);
        }
    }

    //  modifier print
    @Override
    public void printComboDetailAnalysisReport(String xzType, String printer,
                                               String title, String plu, String pluMod)
            throws RemoteException {
        Gson gson = new Gson();

        PrinterDevice prtDevice = gson.fromJson(printer, PrinterDevice.class);
        PrinterTitle prtTitle = gson.fromJson(title, PrinterTitle.class);
        ArrayList<ReportPluDayItem> item = gson.fromJson(plu, new TypeToken<ArrayList<ReportPluDayItem>>() {
        }.getType());
        ArrayList<ReportPluDayComboModifier> modifier = gson.fromJson(pluMod, new TypeToken<ArrayList<ReportPluDayComboModifier>>() {
        }.getType());

        PrintManager printMgr = this.service.getPrintMgr();
        JobManager printJobMgr = printMgr.configureJobManager(prtDevice.getIP());
        PrinterQueueManager pqMgr = this.service.getPqMgr();

        if (printJobMgr != null) {
            String uuid = pqMgr.getDataUUID(prtTitle.getBill_NO());
            ComboDetailAnalysisReportPrint daPrint = new ComboDetailAnalysisReportPrint(uuid, TimeUtil.getPrintingLongDate(prtTitle.getBizDate()));
            String name = prtDevice.getName();
            //set page size
            if (this.service.isTMU220(name)) {
                daPrint.setCharSize(33);
            } else if (this.service.isTM88(name)) {
                daPrint.setCharSize(42);
            } else {
                daPrint.setCharSize(48);
            }
            daPrint.AddReportHeader(prtTitle.getRestaurantName(), xzType, PrintService.instance.getResources().getString(R.string.modifier_analysis));
            daPrint.AddHeader(prtTitle.getOp(), prtTitle.getBill_NO(), prtTitle.getDate() + " " + prtTitle.getTime(), prtTitle.getBizDate());
            daPrint.AddContentListHeader(PrintService.instance.getResources().getString(R.string.plu_name),
                    PrintService.instance.getResources().getString(R.string.price),
                    PrintService.instance.getResources().getString(R.string.qty),
                    PrintService.instance.getResources().getString(R.string.amount));
            daPrint.print(item, modifier);
            daPrint.setPrinterIp(prtDevice.getIP());
            daPrint.AddFooter(prtTitle.getDate() + " " + prtTitle.getTime());
            pqMgr.queuePrint(daPrint.getJobForQueue());
            printMgr.addJob(prtDevice.getIP(), daPrint);
        }
    }

    @Override
    public void printMonthlySaleReport(String printer, String title, int year, int month,
                                       String saleData) throws RemoteException {
        // TODO Auto-generated method stub
        Gson gson = new Gson();

        PrinterDevice prtDevice = gson.fromJson(printer, PrinterDevice.class);
        PrinterTitle prtTitle = gson.fromJson(title, PrinterTitle.class);
        ArrayList<MonthlySalesReport> items = gson.fromJson(saleData, new TypeToken<ArrayList<MonthlySalesReport>>() {
        }.getType());
        PrintManager printMgr = this.service.getPrintMgr();
        JobManager printJobMgr = printMgr.configureJobManager(prtDevice.getIP());
        PrinterQueueManager pqMgr = this.service.getPqMgr();
        if (printJobMgr != null) {
            int totalDays = TimeUtil.getTotalDaysInMonth(year, month);

            String uuid = pqMgr.getDataUUID(prtTitle.getBill_NO());
            MonthlySalesReportPrint daPrint = new MonthlySalesReportPrint(uuid);
            String name = prtDevice.getName();
            //set page size
            if (this.service.isTMU220(name)) {
                daPrint.setCharSize(33);
            } else if (this.service.isTM88(name)) {
                daPrint.setCharSize(42);
            } else {
                daPrint.setCharSize(48);
            }
            daPrint.AddReportHeader(prtTitle.getRestaurantName(), null, PrintService.instance.getResources().getString(R.string.monthly_sales));
            daPrint.AddHeader(prtTitle.getOp(), year + "-" + month + "-01", year + "-" + month + "-" + totalDays);
            daPrint.print(items);
            daPrint.setPrinterIp(prtDevice.getIP());
            daPrint.AddFooter(prtTitle.getDate() + " " + prtTitle.getTime());
            pqMgr.queuePrint(daPrint.getJobForQueue());
            printMgr.addJob(prtDevice.getIP(), daPrint);
        }

    }


    @Override
    public void printMonthlyPLUReport(String printer, String title, int year, int month, String plu)
            throws RemoteException {
        Gson gson = new Gson();

        PrinterDevice prtDevice = gson.fromJson(printer, PrinterDevice.class);
        PrinterTitle prtTitle = gson.fromJson(title, PrinterTitle.class);
        ArrayList<MonthlyPLUReport> pluData = gson.fromJson(plu, new TypeToken<ArrayList<MonthlyPLUReport>>() {
        }.getType());

        PrintManager printMgr = this.service.getPrintMgr();
        JobManager printJobMgr = printMgr.configureJobManager(prtDevice.getIP());
        PrinterQueueManager pqMgr = this.service.getPqMgr();

        if (printJobMgr != null) {
            int totalDays = TimeUtil.getTotalDaysInMonth(year, month);
            String uuid = pqMgr.getDataUUID(prtTitle.getBill_NO());
            DetailAnalysisReportPrint daPrint
                    = new DetailAnalysisReportPrint(uuid, 0L);
            String name = prtDevice.getName();
            //set page size
            if (this.service.isTMU220(name)) {
                daPrint.setCharSize(33);
            } else if (this.service.isTM88(name)) {
                daPrint.setCharSize(42);
            } else {
                daPrint.setCharSize(48);
            }
            daPrint.AddReportHeader(prtTitle.getRestaurantName(), null, PrintService.instance.getResources().getString(R.string.monthly_plu));
            daPrint.AddHeader(prtTitle.getOp(), year + "-" + month + "-01", year + "-" + month + "-" + totalDays);
            daPrint.AddContentListHeader(PrintService.instance.getResources().getString(R.string.plu_name),
                    PrintService.instance.getResources().getString(R.string.price),
                    PrintService.instance.getResources().getString(R.string.qty),
                    PrintService.instance.getResources().getString(R.string.amount));
            daPrint.print(pluData);
            daPrint.setPrinterIp(prtDevice.getIP());
            daPrint.AddFooter(prtTitle.getDate() + " " + prtTitle.getTime());
            pqMgr.queuePrint(daPrint.getJobForQueue());
            printMgr.addJob(prtDevice.getIP(), daPrint);
        }
    }


    @Override
    public void printStoredCardConsume(String printer, String title, String time, String cardNo, String action, String actionAmount, String balance) throws RemoteException {
        Gson gson = new Gson();
        PrinterDevice prtDevice = gson.fromJson(printer, PrinterDevice.class);
        PrintManager printMgr = this.service.getPrintMgr();
        JobManager printJobMgr = printMgr.configureJobManager(prtDevice.getIP());
        PrinterQueueManager pqMgr = this.service.getPqMgr();
        if (printJobMgr != null) {
            String uuid = pqMgr.getDataUUID(cardNo);
            StoredCardPrint storedCardPrint = new StoredCardPrint(uuid, 0L);
            String name = prtDevice.getName();
            //set page size
            if (this.service.isTMU220(name)) {
                storedCardPrint.setCharSize(33);
            } else if (this.service.isTM88(name)) {
                storedCardPrint.setCharSize(42);
            } else {
                storedCardPrint.setCharSize(48);
            }
            storedCardPrint.AddTitle(title);
            storedCardPrint.AddItem("Time:     ", time);
            storedCardPrint.AddItem("Card no.    ******", cardNo);
            storedCardPrint.AddItem(action + " amount :   $", BH.getBD(actionAmount).toString());
            storedCardPrint.AddItem("Balance amount :   $", BH.getBD(balance).toString());
            storedCardPrint.AddFooter("Powered by Alfred");
            storedCardPrint.setPrinterIp(prtDevice.getIP());
            pqMgr.queuePrint(storedCardPrint.getJobForQueue());
            printMgr.addJob(prtDevice.getIP(), storedCardPrint);
        }
    }

    @Override
    public void printTableQRCode(String printer, String tableId, String title, String qrCodeText) throws RemoteException {
        Gson gson = new Gson();
        PrinterDevice prtDevice = gson.fromJson(printer, PrinterDevice.class);
        PrintManager printMgr = this.service.getPrintMgr();
        JobManager printJobMgr = printMgr.configureJobManager(prtDevice.getIP());
        PrinterQueueManager pqMgr = this.service.getPqMgr();
        if (printJobMgr != null) {
            PrinterTitle prtTitle = gson.fromJson(title, PrinterTitle.class);
            String uuid = pqMgr.getDataUUID("TableQRCode" + tableId);
            TableQRCodePrint tableQRCodePrint = new TableQRCodePrint(uuid, 0l);
            String name = prtDevice.getName();
            //set page size
            if (this.service.isTMU220(name)) {
                tableQRCodePrint.setCharSize(33);
            } else if (this.service.isTM88(name)) {
                tableQRCodePrint.setCharSize(42);
            } else {
                tableQRCodePrint.setCharSize(48);
            }
            tableQRCodePrint.AddRestaurantInfo(prtTitle.getLogo(), prtTitle.getRestaurantName(), "", TimeUtil.getTime());
            tableQRCodePrint.AddTitle("Scan QR_code by ServedByAlfred to \nselect and send order by yourself");
            String table = "";
            if (!TextUtils.isEmpty(prtTitle.getTableName())) {
                table = "Tabel:" + prtTitle.getTableName();
            }
            tableQRCodePrint.AddQRCode(table, qrCodeText);
            tableQRCodePrint.AddFooter("Powered by Alfred");
            tableQRCodePrint.setPrinterIp(prtDevice.getIP());
            pqMgr.queuePrint(tableQRCodePrint.getJobForQueue());
            printMgr.addJob(prtDevice.getIP(), tableQRCodePrint);
        }
    }

    @Override
    public void printTscBill(String printer, String title, String order, String orderdetail, String modifiers, String currencySymbol, String direction) throws RemoteException {
        String name;

        BillTscPrint b = null;
        Gson gson = new Gson();
        Boolean isTian = false;
        PrinterDevice prtDevice = gson.fromJson(printer, PrinterDevice.class);
        PrinterTitle prtitle = gson.fromJson(title, PrinterTitle.class);
        Order prorder = gson.fromJson(order, Order.class);
        ArrayList<OrderDetail> prOrderDetail = gson.fromJson(orderdetail,
                new TypeToken<ArrayList<OrderDetail>>() {
                }.getType());
        ArrayList<PrintOrderModifier> orderModifiers = gson.fromJson(modifiers,
                new TypeToken<ArrayList<PrintOrderModifier>>() {
                }.getType());

        PrintManager printMgr = this.service.getPrintMgr();
        JobManager printJobMgr;
        if (prtDevice.getIP().contains(",")) {
            printJobMgr = printMgr.configureJobManager("12:12:12");
        } else {
            printJobMgr = printMgr.configureJobManager(prtDevice.getIP());
        }

        PrinterQueueManager pqMgr = this.service.getPqMgr();
        Log.d(TAG, "setIsLablePrinter--:" + printer);

        name = prtitle.getRestaurantName();

        if (TextUtils.isEmpty(name)) {
            name = "AAA";
        }
        String uuid = pqMgr.getDataUUID(prtitle.getBill_NO());

        if (isTian) {
            b = new BillTscPrint(uuid, Long.valueOf(prtitle.getBizDate()), 25);
        } else {
            b = new BillTscPrint(uuid, Long.valueOf(prtitle.getBizDate()), 26);
        }
        List<OrderDetail> lableOrderDetail = new ArrayList<OrderDetail>();

        for (int i = 0; i < prOrderDetail.size(); i++) {
            b.setPrinterIp(prtDevice.getIP());
            b.setIsLablePrinter(prtDevice.getIsLablePrinter(), Integer.valueOf(direction).intValue(), isTian);
            if (prOrderDetail.get(i).getItemNum() > 1) {
                String price = BH.getBD(Double.parseDouble(prOrderDetail.get(i).getRealPrice()) / prOrderDetail.get(i).getItemNum() + "").toString();
                for (Integer j = 0; j < prOrderDetail.get(i).getItemNum(); j++) {
                    if (price.equals("0.00") || price.equals("0")) {
                        prOrderDetail.get(i).setRealPrice("");
                    } else {
                        prOrderDetail.get(i).setRealPrice(price);
                    }
                    lableOrderDetail.add(prOrderDetail.get(i));
                }
            } else {
                lableOrderDetail.add(prOrderDetail.get(i));
            }
        }
        StringBuilder modbuf = new StringBuilder();


        for (int i = 0; i < lableOrderDetail.size(); i++) {

            modbuf.setLength(0);
            if (orderModifiers != null) {
                for (int m = 0; m < orderModifiers.size(); m++) {
                    PrintOrderModifier om = orderModifiers.get(m);
                    if (om.getOrderDetailId() == lableOrderDetail.get(i).getId()) {
//                        if (om.getQty() > 1) {
                        if (om.getPrice().toString().equals("0")) {
                            modbuf.append(om.getItemName() + "" + "/");
                        } else {
                            modbuf.append(om.getItemName() + "" + currencySymbol + BH.getBD(om.getPrice()).toString() + "/");
                        }
                        //    billPrint.addOrderModifier(om.getItemName() + "x" + om.getQty(), 1, om.getPrice());
//                        } else {
//                         //   billPrint.addOrderModifier(om.getItemName(), 1, om.getPrice());
//                        }

                        // App.instance.getLocalRestaurantConfig().getCurrencySymbol()
                    }
                }
            }
            b.AddRestaurantInfo(null,
                    prtitle.getRevName(),
                    prtitle.getOrderNo(), i, lableOrderDetail.size() + "", lableOrderDetail.get(i).getItemName() + "", modbuf.toString(), currencySymbol + lableOrderDetail.get(i).getRealPrice(), true);

        }
        pqMgr.queuePrint(b.getJobForQueue());
        if (prtDevice.getIP().contains(",")) {
            printMgr.addJob("12:12:12", b);
        } else {
            printMgr.addJob(prtDevice.getIP(), b);
        }
    }

    @Override
    public void printCashInOut(String printer, String cashinout, String title) throws RemoteException {
        Gson gson = new Gson();
        PrinterDevice prtDevice = gson.fromJson(printer, PrinterDevice.class);
        Restaurant prtitle = gson.fromJson(title, Restaurant.class);
        CashInOut cashInOut = gson.fromJson(cashinout, CashInOut.class);

        PrintManager printMgr = this.service.getPrintMgr();
        JobManager printJobMgr = printMgr.configureJobManager(prtDevice.getIP());
        PrinterQueueManager pqMgr = this.service.getPqMgr();
        String uuid = pqMgr.getDataUUID("drawer");
        BillPrint billPrint = new BillPrint(uuid, Long.valueOf(cashInOut.getBusinessDate()));
        billPrint.setPrinterIp(prtDevice.getIP());
        String name = prtDevice.getName();
        //set page size

//   String   url=SettingDataSQL.getSettingDataByUrl(
//           prtitle.getLogoUrl().toString()).getLogoString();
        if (this.service.isTMU220(name)) {
            billPrint.setCharSize(33);
            //U220 cannot support image print
            billPrint.AddRestaurantInfo(null,
                    prtitle.getRestaurantName(),
                    prtitle.getAddressPrint(), null);
        } else {
            if (this.service.isTM88(name)) {
                billPrint.setCharSize(42);
            } else {
                billPrint.setCharSize(48);
            }
            billPrint.AddRestaurantInfo(prtitle.getLogoUrl(),
                    prtitle.getRestaurantName(),
                    prtitle.getAddressPrint(), null);
        }

        String time = TimeUtil.getPrintDate(Long.parseLong(cashInOut.getCreateTime()));
        billPrint.AddHeaderCash(cashInOut.getEmpId(), time);
        billPrint.AddCashIn(cashInOut.getCash(), cashInOut.getComment(), cashInOut.getType());
        billPrint.AddFooter(PrintService.instance.getResources().getString(R.string.powered_by_alfred), false);
        pqMgr.queuePrint(billPrint.getJobForQueue());
        printMgr.addJob(prtDevice.getIP(), billPrint);
    }

}
