package com.alfredposclient.jobs;

import android.content.Context;
import android.util.Log;

import com.alfredbase.BaseActivity;
import com.alfredbase.ParamConst;
import com.alfredbase.global.CoreData;
import com.alfredbase.javabean.KotItemDetail;
import com.alfredbase.javabean.KotItemModifier;
import com.alfredbase.javabean.KotSummary;
import com.alfredbase.javabean.Order;
import com.alfredbase.javabean.OrderDetail;
import com.alfredbase.javabean.Printer;
import com.alfredbase.javabean.model.KDSDevice;
import com.alfredbase.javabean.model.PrinterDevice;
import com.alfredbase.store.sql.KotItemDetailSQL;
import com.alfredbase.store.sql.KotItemModifierSQL;
import com.alfredbase.store.sql.KotSummarySQL;
import com.alfredbase.store.sql.OrderDetailSQL;
import com.alfredbase.store.sql.cpsql.CPOrderDetailSQL;
import com.alfredbase.utils.LogUtil;
import com.alfredposclient.R;
import com.alfredposclient.activity.MainPage;
import com.alfredposclient.global.App;
import com.google.gson.Gson;
import com.path.android.jobqueue.JobManager;
import com.path.android.jobqueue.config.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KotJobManager {

    private JobManager kotJobManager;
    private Context context;

    public KotJobManager(Context mContext) {
        super();
        this.context = mContext;
        configureJobManager();
    }

    private void configureJobManager() {
        Configuration kotconfiguration = new Configuration.Builder(context)
                .customLogger(new AlfredJobLogger("KOT_JOBS")).id("kot_jobs")
                .minConsumerCount(1) // always keep at least one consumer alive
                .maxConsumerCount(3) // up to 3 consumers at a time
                .loadFactor(3) // 3 jobs per consumer
                .consumerKeepAlive(120) // wait 2 minute
                .build();
        this.kotJobManager = new JobManager(this.context, kotconfiguration);
    }

    /* convert KOT to kot jobs */
    public void tearDownKot(KotSummary kotSummary,
                            ArrayList<KotItemDetail> kot, ArrayList<KotItemModifier> modifiers,
                            String method, Map<String, Object> orderMap) {
        LogUtil.d("开始时间", "时间");
        ArrayList<Integer> printerGrougIds = new ArrayList<Integer>();
        // map printergroudId to Kot: Group ID --> Details
        Map<Integer, ArrayList<KotItemDetail>> kots = new HashMap<Integer, ArrayList<KotItemDetail>>();
        // map printerGroudId to Modifiers
        Map<Integer, ArrayList<KotItemModifier>> mods = new HashMap<Integer, ArrayList<KotItemModifier>>();
        BaseActivity context = App.getTopActivity();
        for (KotItemDetail items : kot) {
            Integer pgid = items.getPrinterGroupId();
            if (pgid.intValue() == 0) {
                context.kotPrintStatus(MainPage.KOT_ITEM_PRINT_NULL,
                        items.getItemName());
                return;
            }
            int kotItemDetailId = items.getId().intValue();

            // Get all Group ids that KOT blongs to
            if (!printerGrougIds.contains(pgid))
                printerGrougIds.add(pgid);

            // kot
            if (kots.containsKey(pgid)) {
                ArrayList<KotItemDetail> tmp = kots.get(pgid);
                tmp.add(items);
            } else {
                ArrayList<KotItemDetail> tmp = new ArrayList<KotItemDetail>();
                tmp.add(items);
                kots.put(pgid, tmp);
            }

            // modifier
            if (mods.containsKey(pgid)) {
                ArrayList<KotItemModifier> tmp = mods.get(pgid);
                for (KotItemModifier mof : modifiers) {
                    if (mof.getKotItemDetailId().intValue() == kotItemDetailId) {
                        tmp.add(mof);
                    }
                }
            } else {
                ArrayList<KotItemModifier> tmp = new ArrayList<KotItemModifier>();
                for (KotItemModifier mof : modifiers) {
                    if (mof.getKotItemDetailId().intValue() == kotItemDetailId) {
                        tmp.add(mof);
                    }
                }
                mods.put(items.getPrinterGroupId(), tmp);
            }
        }

        if (printerGrougIds != null && printerGrougIds.size() > 0 && kotSummary != null) {
            kotSummary.setStatus(ParamConst.KOTS_STATUS_UNDONE);
            KotSummarySQL.updateKotSummaryStatusById(ParamConst.KOTS_STATUS_UNDONE, kotSummary.getId().intValue());
        }

//        List<Integer> doStockMap = new ArrayList<>();
        // add job to send it to KDS

        for (Integer prgid : printerGrougIds) {
            ArrayList<Printer> printers = CoreData.getInstance()
                    .getPrintersInGroup(prgid.intValue());
            for (Printer prnt : printers) {
                // KDS device
                KDSDevice kds1 = App.instance.getKDSDevice(prnt.getId());
                // physical printer
                PrinterDevice prntd = App.instance.getPrinterDeviceById(prnt
                        .getId());
                if (kds1 == null && prntd == null) {
                    context.kotPrintStatus(MainPage.KOT_PRINT_NULL, null);
                    return;
                }
                if (kds1 != null) {
                    KotJob kotjob = new KotJob(kds1, kotSummary,
                            kots.get(prgid), mods.get(prgid), method, orderMap);
                    kotJobManager.addJob(kotjob);
                }
                if (prntd != null) {
                    prntd.setGroupId(prgid.intValue());

                    boolean printed = false;

                    if ((!prntd.getIP().contains(":") && !prntd.getIP().contains(",")) || prntd.getIsLablePrinter() != 1) {
                        printed = App.instance.remoteKotPrint(prntd,
                                kotSummary, kots.get(prgid), mods.get(prgid), false);

                        if (printed) {
                            List<Integer> orderDetailIds = (List<Integer>) orderMap
                                    .get("orderDetailIds");
//                            boolean refreshStock = false;
                            if (orderDetailIds != null && orderDetailIds.size() != 0) {
                                ArrayList<OrderDetail> orderDetails = new ArrayList<OrderDetail>();
                                synchronized (orderDetails) {
                                    for (int i = 0; i < orderDetailIds.size(); i++) {
                                        OrderDetail orderDetail = OrderDetailSQL
                                                .getOrderDetail(orderDetailIds
                                                        .get(i));
                                        orderDetail
                                                .setOrderDetailStatus(ParamConst.ORDERDETAIL_STATUS_KOTPRINTERD);
                                        orderDetails.add(orderDetail);

//                                        if(!doStockMap.contains(orderDetail.getId())){
//                                            boolean refresh = RemainingStockHelper.updateRemainingStockNumByOrderDetail(orderDetail);
//                                            if(!refreshStock) {
//                                                refreshStock = refresh;
//                                            }
//                                            doStockMap.add(orderDetail.getId());
//                                        }
                                    }
                                }
                                OrderDetailSQL.addOrderDetailList(orderDetails);
                                LogUtil.e("成功时间", "时间");
                                context.kotPrintStatus(MainPage.KOT_PRINT_SUCCEED,
                                        orderMap.get("orderId"));
//                                App.instance.getSyncJob().updateRemainingStock((Integer) orderMap.get("orderId"));
//                                if(refreshStock){
//                                    App.getTopActivity().httpRequestAction(MainPage.REFRESH_STOCK_NUM, null);
//                                }
                            }
                        } else {
                            context.kotPrintStatus(MainPage.KOT_PRINT_FAILED,
                                    orderMap.get("orderId"));
                        }
                    }
//
//                    }
                }
            }

        }
    }

    public void refreshAllKDS(ArrayList<KotItemDetail> kot, String method) {
        LogUtil.d("开始时间", "时间");
        ArrayList<Integer> printerGrougIds = new ArrayList<Integer>();
        Map<Integer, ArrayList<KotItemDetail>> kots = new HashMap<Integer, ArrayList<KotItemDetail>>();
        BaseActivity context = App.getTopActivity();
        for (KotItemDetail items : kot) {
            Integer pgid = items.getPrinterGroupId();
            if (pgid.intValue() == 0) {
                context.kotPrintStatus(MainPage.KOT_ITEM_PRINT_NULL,
                        items.getItemName());
                return;
            }
            // Get all Group ids that KOT blongs to
            if (!printerGrougIds.contains(pgid))
                printerGrougIds.add(pgid);

            // kot
            if (kots.containsKey(pgid)) {
                ArrayList<KotItemDetail> tmp = kots.get(pgid);
                tmp.add(items);
            } else {
                ArrayList<KotItemDetail> tmp = new ArrayList<KotItemDetail>();
                tmp.add(items);
                kots.put(pgid, tmp);
            }

        }

//        List<Integer> doStockMap = new ArrayList<>();
        // add job to send it to KDS

        for (Integer prgid : printerGrougIds) {
            ArrayList<Printer> printers = CoreData.getInstance()
                    .getPrintersInGroup(prgid.intValue());
            for (Printer prnt : printers) {
                // KDS device
                KDSDevice kds1 = App.instance.getKDSDevice(prnt.getId());
                if (kds1 != null) {
                    KotJob kotjob = new KotJob(kds1, kots.get(prgid), method);
                    kotJobManager.addJob(kotjob);
                }

            }

        }
    }

    public void tearDownKotForSub(KotSummary kotSummary,
                                  ArrayList<KotItemDetail> kot, ArrayList<KotItemModifier> modifiers,
                                  String method, Map<String, Object> orderMap) {
        LogUtil.d("开始时间", "时间");
        ArrayList<Integer> printerGrougIds = new ArrayList<Integer>();
        // map printergroudId to Kot: Group ID --> Details
        Map<Integer, ArrayList<KotItemDetail>> kots = new HashMap<Integer, ArrayList<KotItemDetail>>();
        // map printerGroudId to Modifiers
        Map<Integer, ArrayList<KotItemModifier>> mods = new HashMap<Integer, ArrayList<KotItemModifier>>();
        BaseActivity context = App.getTopActivity();
        for (KotItemDetail items : kot) {
            Integer pgid = items.getPrinterGroupId();
            if (pgid.intValue() == 0) {
                context.kotPrintStatus(MainPage.KOT_ITEM_PRINT_NULL,
                        items.getItemName());
                return;
            }
            int kotItemDetailId = items.getId().intValue();

            // Get all Group ids that KOT blongs to
            if (!printerGrougIds.contains(pgid))
                printerGrougIds.add(pgid);

            // kot
            if (kots.containsKey(pgid)) {
                ArrayList<KotItemDetail> tmp = kots.get(pgid);
                tmp.add(items);
            } else {
                ArrayList<KotItemDetail> tmp = new ArrayList<KotItemDetail>();
                tmp.add(items);
                kots.put(pgid, tmp);
            }

            // modifier
            if (mods.containsKey(pgid)) {
                ArrayList<KotItemModifier> tmp = mods.get(pgid);
                for (KotItemModifier mof : modifiers) {
                    if (mof.getKotItemDetailId().intValue() == kotItemDetailId) {
                        tmp.add(mof);
                    }
                }
            } else {
                ArrayList<KotItemModifier> tmp = new ArrayList<KotItemModifier>();
                for (KotItemModifier mof : modifiers) {
                    if (mof.getKotItemDetailId().intValue() == kotItemDetailId) {
                        tmp.add(mof);
                    }
                }
                mods.put(items.getPrinterGroupId(), tmp);
            }
        }

        if (printerGrougIds != null && printerGrougIds.size() > 0 && kotSummary != null) {
            kotSummary.setStatus(ParamConst.KOTS_STATUS_UNDONE);
            KotSummarySQL.updateKotSummaryStatusById(ParamConst.KOTS_STATUS_UNDONE, kotSummary.getId().intValue());
        }


        // add job to send it to KDS
        for (Integer prgid : printerGrougIds) {
            ArrayList<Printer> printers = CoreData.getInstance()
                    .getPrintersInGroup(prgid.intValue());
            for (Printer prnt : printers) {
                // KDS device
                KDSDevice kds1 = App.instance.getKDSDevice(prnt.getId());
                // physical printer
                PrinterDevice prntd = App.instance.getPrinterDeviceById(prnt
                        .getId());
                if (kds1 == null && prntd == null) {
                    context.kotPrintStatus(MainPage.KOT_PRINT_NULL, null);
                    return;
                }
                if (kds1 != null) {
                    KotJob kotjob = new KotJob(kds1, kotSummary,
                            kots.get(prgid), mods.get(prgid), method, orderMap);
                    kotJobManager.addJob(kotjob);
                }
                if (prntd != null) {
                    prntd.setGroupId(prgid.intValue());

                    boolean printed = false;

                    if ((!prntd.getIP().contains(":") && !prntd.getIP().contains(",")) || prntd.getIsLablePrinter() != 1) {
                        printed = App.instance.remoteKotPrint(prntd,
                                kotSummary, kots.get(prgid), mods.get(prgid), false);

                        if (printed) {
                            List<Integer> orderDetailIds = (List<Integer>) orderMap
                                    .get("orderDetailIds");
                            if (orderDetailIds != null && orderDetailIds.size() != 0) {
                                ArrayList<OrderDetail> orderDetails = new ArrayList<OrderDetail>();
                                synchronized (orderDetails) {
                                    for (int i = 0; i < orderDetailIds.size(); i++) {
                                        OrderDetail orderDetail = CPOrderDetailSQL
                                                .getOrderDetail(orderDetailIds
                                                        .get(i));
                                        orderDetail
                                                .setOrderDetailStatus(ParamConst.ORDERDETAIL_STATUS_KOTPRINTERD);
                                        orderDetails.add(orderDetail);
                                    }
                                }
                                CPOrderDetailSQL.addOrderDetailList(orderDetails);
                                LogUtil.e("成功时间", "时间");
                                context.kotPrintStatus(MainPage.KOT_PRINT_SUCCEED,
                                        orderMap.get("orderId"));
                            }
                        } else {
                            context.kotPrintStatus(MainPage.KOT_PRINT_FAILED,
                                    orderMap.get("orderId"));
                        }
                    }
                }
            }

        }
    }


    public void tearDownKotFire(KotSummary kotSummary,
                                ArrayList<KotItemDetail> kot, ArrayList<KotItemModifier> modifiers,
                                String method, Map<String, Object> orderMap) {
        LogUtil.d("开始时间", "时间");
        ArrayList<Integer> printerGrougIds = new ArrayList<Integer>();
        // map printergroudId to Kot: Group ID --> Details
        Map<Integer, ArrayList<KotItemDetail>> kots = new HashMap<Integer, ArrayList<KotItemDetail>>();
        // map printerGroudId to Modifiers
        Map<Integer, ArrayList<KotItemModifier>> mods = new HashMap<Integer, ArrayList<KotItemModifier>>();
        BaseActivity context = App.getTopActivity();
        for (KotItemDetail items : kot) {
            Integer pgid = items.getPrinterGroupId();
            if (pgid.intValue() == 0) {
                context.kotPrintStatus(MainPage.KOT_ITEM_PRINT_NULL,
                        items.getItemName());
                return;
            }
            int kotItemDetailId = items.getId().intValue();

            // Get all Group ids that KOT blongs to
            if (!printerGrougIds.contains(pgid))
                printerGrougIds.add(pgid);

            // kot
            if (kots.containsKey(pgid)) {
                ArrayList<KotItemDetail> tmp = kots.get(pgid);
                tmp.add(items);
            } else {
                ArrayList<KotItemDetail> tmp = new ArrayList<KotItemDetail>();
                tmp.add(items);
                kots.put(pgid, tmp);
            }

            // modifier
            if (mods.containsKey(pgid)) {
                ArrayList<KotItemModifier> tmp = mods.get(pgid);
                for (KotItemModifier mof : modifiers) {
                    if (mof.getKotItemDetailId().intValue() == kotItemDetailId) {
                        tmp.add(mof);
                    }
                }
            } else {
                ArrayList<KotItemModifier> tmp = new ArrayList<KotItemModifier>();
                for (KotItemModifier mof : modifiers) {
                    if (mof.getKotItemDetailId().intValue() == kotItemDetailId) {
                        tmp.add(mof);
                    }
                }
                mods.put(items.getPrinterGroupId(), tmp);
            }
        }


        // add job to send it to KDS
        for (Integer prgid : printerGrougIds) {
            ArrayList<Printer> printers = CoreData.getInstance()
                    .getPrintersInGroup(prgid.intValue());
            for (Printer prnt : printers) {
                // KDS device
                KDSDevice kds1 = App.instance.getKDSDevice(prnt.getId());
                // physical printer
                PrinterDevice prntd = App.instance.getPrinterDeviceById(prnt
                        .getId());
                if (kds1 == null && prntd == null) {
                    context.kotPrintStatus(MainPage.KOT_PRINT_NULL, null);
                    return;
                }
                if (kds1 != null) {
                    KotJob kotjob = new KotJob(kds1, kotSummary,
                            kots.get(prgid), mods.get(prgid), method, orderMap);
                    kotJobManager.addJob(kotjob);
                }
                if (prntd != null) {
                    prntd.setGroupId(prgid.intValue());


                    boolean printed = App.instance.remoteKotPrint(prntd,
                            kotSummary, kots.get(prgid), mods.get(prgid), true);

                    if (printed) {
                        List<Integer> orderDetailIds = (List<Integer>) orderMap
                                .get("orderDetailIds");
                        if (orderDetailIds != null && orderDetailIds.size() != 0) {
                            ArrayList<OrderDetail> orderDetails = new ArrayList<OrderDetail>();
                            synchronized (orderDetails) {
                                for (int i = 0; i < orderDetailIds.size(); i++) {
                                    OrderDetailSQL.updateOrderDetailFireStatus(1, orderDetailIds.get(i));
                                }
                            }
                            LogUtil.e("成功时间", "时间");
                            context.kotPrintStatus(MainPage.KOT_PRINT_SUCCEED,
                                    orderMap.get("orderId"));
                        }
                    } else {
                        context.kotPrintStatus(MainPage.KOT_PRINT_FAILED,
                                orderMap.get("orderId"));
                    }
                }
            }

        }
    }

    /* convert KOT to kot jobs */
    public void transferTableDownKot(String action, KotSummary toKotSummary,
                                     KotSummary fromKotSummary, Map<String, Object> orderMap) {

        List<Integer> printerGrougIds = KotItemDetailSQL
                .getPrinterGroupIds(fromKotSummary);
        // add job to send it to KDS
        for (Integer prgid : printerGrougIds) {
            ArrayList<Printer> printers = CoreData.getInstance()
                    .getPrintersInGroup(prgid.intValue());
            for (Printer prnt : printers) {
                KDSDevice kds1 = App.instance.getKDSDevice(prnt.getId());
                if (kds1 != null) {
                    KotJob kotjob = new KotJob(kds1, action, toKotSummary,
                            fromKotSummary, orderMap);
                    kotJobManager.addJob(kotjob);
                }
            }
        }
        boolean ret = transferTableItemToPrinter(fromKotSummary,
                toKotSummary, orderMap);
        if (!ret) {
            App.getTopActivity().kotPrintStatus(
                    MainPage.KOT_PRINT_FAILED, null);
        }
    }

    /* transfer table`s item to printer */
    private boolean transferTableItemToPrinter(KotSummary fromKotSummary,
                                               KotSummary toKotSummary, Map<String, Object> orderMap) {
        BaseActivity context = App.getTopActivity();
        ArrayList<Integer> printerGrougIds = new ArrayList<Integer>();
        KotSummary printKotSummary = null;
        // map printergroudId to Kot: Group ID --> Details
        Map<Integer, ArrayList<KotItemDetail>> kots = new HashMap<Integer, ArrayList<KotItemDetail>>();
        // map printerGroudId to Modifiers
        Map<Integer, ArrayList<KotItemModifier>> mods = new HashMap<Integer, ArrayList<KotItemModifier>>();
        List<KotItemModifier> kotItemModifiers = new ArrayList<KotItemModifier>();
        List<KotItemDetail> kotItemDetails = new ArrayList<KotItemDetail>();
        String transferAction = (String) orderMap.get("action");
        if (ParamConst.JOB_MERGER_KOT.equals(transferAction)) {

//			Order oldOrder = (Order) orderMap.get("fromOrder");

//			TableInfo currentTable = TableInfoSQL.getTableById((Integer) orderMap
//					.get("currentTableId"));
            kotItemDetails = KotItemDetailSQL
                    .getKotItemDetailBySummaryId(fromKotSummary.getId());
            kotItemModifiers = new ArrayList<KotItemModifier>();
            for (KotItemDetail kotItemDetail : kotItemDetails) {
                kotItemDetail.setKotSummaryId(toKotSummary.getId().intValue());
                kotItemDetail.setOrderId(toKotSummary.getOrderId().intValue());
                KotItemDetailSQL.update(kotItemDetail);
                kotItemModifiers.addAll(KotItemModifierSQL
                        .getKotItemModifiersByKotItemDetail(kotItemDetail
                                .getId()));
            }
            KotSummarySQL.deleteKotSummary(fromKotSummary);
//			Order newOrder = OrderSQL.getUnfinishedOrderAtTable(currentTable.getPosId(), oldOrder.getBusinessDate());
//			OrderBill newOrderBill = ObjectFactory.getInstance().getOrderBill(
//					newOrder, App.instance.getRevenueCenter());
//			List<OrderDetail> orderDetails = OrderDetailSQL
//					.getUnFreeOrderDetails(oldOrder);
//			if (!orderDetails.isEmpty()) {
//				for (OrderDetail orderDetail : orderDetails) {
//					OrderDetail newOrderDetail = ObjectFactory.getInstance()
//							.getOrderDetailForTransferTable(newOrder,
//									orderDetail);
////					if(!IntegerUtils.isEmptyOrZero(orderDetail.getAppOrderDetailId())){
//						OrderDetailTaxSQL.updateOrderDetailTaxForTransation(newOrderDetail, orderDetail);
////					}
//					OrderDetailSQL.addOrderDetailETC(newOrderDetail);
//					List<OrderModifier> orderModifiers = OrderModifierSQL
//							.getOrderModifiers(orderDetail);
//					if (orderModifiers.isEmpty()) {
//						continue;
//					}
//					for (OrderModifier orderModifier : orderModifiers) {
//						OrderModifier newOrderModifier = ObjectFactory
//								.getInstance().getOrderModifier(
//										newOrder,
//										newOrderDetail,
//										CoreData.getInstance().getModifier(
//												orderModifier.getModifierId()),
//										orderModifier.getPrinterId().intValue());
//						OrderModifierSQL.addOrderModifier(newOrderModifier);
//					}
//				}
//			}
////			if(!IntegerUtils.isEmptyOrZero(oldOrder.getAppOrderId()){
////				OrderDetailTaxSQL.updateOrderDetailTaxForTransation(newOrderDetail, orderDetail);
////			}
//			OrderDetailSQL.deleteOrderDetailByOrder(oldOrder);
//			OrderModifierSQL.deleteOrderModifierByOrder(oldOrder);
//			OrderBillSQL.deleteOrderBillByOrder(oldOrder);
//			OrderBillSQL.add(newOrderBill);
//			OrderSQL.deleteOrder(oldOrder);
            context.kotPrintStatus(ParamConst.JOB_TYPE_POS_MERGER_TABLE, null);
            printKotSummary = toKotSummary;
        } else if (ParamConst.JOB_TRANSFER_KOT.equals(transferAction)) {
            KotSummarySQL.update(fromKotSummary);
            Order order = (Order) orderMap.get("fromOrder");
//			OrderSQL.update(order);
            kotItemDetails = KotItemDetailSQL
                    .getKotItemDetailBySummaryId(fromKotSummary.getId());
            for (KotItemDetail kotItemDetail : kotItemDetails) {
                kotItemModifiers.addAll(KotItemModifierSQL
                        .getKotItemModifiersByKotItemDetail(kotItemDetail
                                .getId()));
            }
            context.kotPrintStatus(ParamConst.JOB_TYPE_POS_TRANSFER_TABLE,
                    order);
            printKotSummary = KotSummarySQL.getKotSummary(fromKotSummary.getOrderId(), fromKotSummary.getNumTag());
        }
        boolean printed = false;
        if (App.instance.getSystemSettings().isTransferPrint()) {
            for (KotItemDetail items : kotItemDetails) {
                Integer pgid = items.getPrinterGroupId();
                int kotItemDetailId = items.getId().intValue();

                // Get all Group ids that KOT blongs to
                if (!printerGrougIds.contains(pgid))
                    printerGrougIds.add(pgid);

                // kot
                if (kots.containsKey(pgid)) {
                    ArrayList<KotItemDetail> tmp = kots.get(pgid);
                    tmp.add(items);
                } else {
                    ArrayList<KotItemDetail> tmp = new ArrayList<KotItemDetail>();
                    tmp.add(items);
                    kots.put(pgid, tmp);
                }

                // modifier
                if (mods.containsKey(pgid)) {
                    ArrayList<KotItemModifier> tmp = mods.get(pgid);
                    for (KotItemModifier mof : kotItemModifiers) {
                        if (mof.getKotItemDetailId().intValue() == kotItemDetailId) {
                            tmp.add(mof);
                        }
                    }
                } else {
                    ArrayList<KotItemModifier> tmp = new ArrayList<KotItemModifier>();
                    for (KotItemModifier mof : kotItemModifiers) {
                        if (mof.getKotItemDetailId().intValue() == kotItemDetailId) {
                            tmp.add(mof);
                        }
                    }
                    mods.put(items.getPrinterGroupId(), tmp);
                }
            }


            if (App.countryCode == ParamConst.CHINA) {
                return true;
            }
            for (Integer prgid : printerGrougIds) {
                ArrayList<Printer> printers = CoreData.getInstance()
                        .getPrintersInGroup(prgid.intValue());
                for (Printer prnt : printers) {
                    // physical printer
                    PrinterDevice prntd = App.instance.getPrinterDeviceById(prnt
                            .getId());

                    if (prntd != null) {
                        prntd.setGroupId(prgid.intValue());
                        String fromTableName = (String) orderMap.get("fromTableName");
                        printKotSummary.setDescription(String.format(context.getResources().getString(R.string.table_transfer_from), fromTableName));
                        printed = App.instance.remoteKotPrint(prntd, printKotSummary,
                                kots.get(prgid), mods.get(prgid), false);

                    } else {
                        printed = true;
                    }
                }
            }
        } else {
            printed = true;
        }
        return printed;
    }

    /* convert KOT to kot jobs */
    public void transferItemDownKot(KotSummary toKotSummary,
                                    KotSummary fromKotSummary, Map<String, Object> orderMap, KotItemDetail kotItemDetail) {

        List<Integer> printerGrougIds = KotItemDetailSQL
                .getPrinterGroupIds(fromKotSummary);
        // add job to send it to KDS
        for (Integer prgid : printerGrougIds) {
            ArrayList<Printer> printers = CoreData.getInstance()
                    .getPrintersInGroup(prgid.intValue());
            for (Printer prnt : printers) {
                KDSDevice kds1 = App.instance.getKDSDevice(prnt.getId());
                if (kds1 != null) {
                    KotJob kotjob = new KotJob(kds1, toKotSummary,
                            fromKotSummary, orderMap, kotItemDetail);
                    kotJobManager.addJob(kotjob);
                }
            }
        }
        boolean ret = transferItemToPrinter(fromKotSummary,
                toKotSummary, orderMap, kotItemDetail);
        if (!ret) {
            App.getTopActivity().kotPrintStatus(
                    MainPage.KOT_PRINT_FAILED, null);
        }
    }

    /* transfer item to printer */
    private boolean transferItemToPrinter(KotSummary fromKotSummary,
                                          KotSummary toKotSummary, Map<String, Object> orderMap, KotItemDetail kotItemDetail) {
        BaseActivity context = App.getTopActivity();
        ArrayList<Integer> printerGrougIds = new ArrayList<Integer>();
        KotSummary printKotSummary = null;
        // map printergroudId to Kot: Group ID --> Details
        Map<Integer, ArrayList<KotItemDetail>> kots = new HashMap<Integer, ArrayList<KotItemDetail>>();
        // map printerGroudId to Modifiers
        Map<Integer, ArrayList<KotItemModifier>> mods = new HashMap<Integer, ArrayList<KotItemModifier>>();
        List<KotItemModifier> kotItemModifiers = new ArrayList<KotItemModifier>();
        kotItemModifiers.addAll(KotItemModifierSQL
                .getKotItemModifiersByKotItemDetail(kotItemDetail
                        .getId()));
        List<KotItemDetail> kotItemDetails = KotItemDetailSQL.getKotItemDetailBySummaryId(fromKotSummary.getId());
        if (kotItemDetails == null || kotItemDetails.size() == 0) {
            KotSummarySQL.deleteKotSummary(fromKotSummary);
        }
        context.kotPrintStatus(ParamConst.JOB_TYPE_POS_MERGER_TABLE, null);
        printKotSummary = toKotSummary;
        boolean printed = false;
        if (App.instance.getSystemSettings().isTransferPrint()) {
            Integer pgid = kotItemDetail.getPrinterGroupId();
            int kotItemDetailId = kotItemDetail.getId().intValue();

            // Get all Group ids that KOT blongs to
            if (!printerGrougIds.contains(pgid))
                printerGrougIds.add(pgid);

            // kot
            if (kots.containsKey(pgid)) {
                ArrayList<KotItemDetail> tmp = kots.get(pgid);
                tmp.add(kotItemDetail);
            } else {
                ArrayList<KotItemDetail> tmp = new ArrayList<KotItemDetail>();
                tmp.add(kotItemDetail);
                kots.put(pgid, tmp);
            }

            // modifier
            if (mods.containsKey(pgid)) {
                ArrayList<KotItemModifier> tmp = mods.get(pgid);
                for (KotItemModifier mof : kotItemModifiers) {
                    if (mof.getKotItemDetailId().intValue() == kotItemDetailId) {
                        tmp.add(mof);
                    }
                }
            } else {
                ArrayList<KotItemModifier> tmp = new ArrayList<KotItemModifier>();
                for (KotItemModifier mof : kotItemModifiers) {
                    if (mof.getKotItemDetailId().intValue() == kotItemDetailId) {
                        tmp.add(mof);
                    }
                }
                mods.put(kotItemDetail.getPrinterGroupId(), tmp);
            }
            if (App.countryCode == ParamConst.CHINA) {
                return true;
            }
            for (Integer prgid : printerGrougIds) {
                ArrayList<Printer> printers = CoreData.getInstance()
                        .getPrintersInGroup(prgid.intValue());
                for (Printer prnt : printers) {
                    // physical printer
                    PrinterDevice prntd = App.instance.getPrinterDeviceById(prnt
                            .getId());
                    if (prntd != null) {
                        prntd.setGroupId(prgid.intValue());
                        String fromTableName = (String) orderMap.get("fromTableName");
                        printKotSummary.setDescription(String.format(context.getResources().getString(R.string.table_transfer_from), fromTableName));
                        printed = App.instance.remoteKotPrint(prntd, printKotSummary,
                                kots.get(prgid), mods.get(prgid), false);

                    } else {
                        printed = true;
                    }
                }
            }
        } else {
            printed = true;
        }
        return printed;
    }

    public void clear() {
        this.kotJobManager.clear();
    }

    public void stop() {
        this.kotJobManager.stop();
    }
}
