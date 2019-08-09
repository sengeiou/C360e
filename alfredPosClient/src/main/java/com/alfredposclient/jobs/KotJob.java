
package com.alfredposclient.jobs;

import com.alfredbase.BaseActivity;
import com.alfredbase.ParamConst;
import com.alfredbase.http.APIName;
import com.alfredbase.javabean.KotItemDetail;
import com.alfredbase.javabean.KotItemModifier;
import com.alfredbase.javabean.KotSummary;
import com.alfredbase.javabean.Order;
import com.alfredbase.javabean.OrderBill;
import com.alfredbase.javabean.OrderDetail;
import com.alfredbase.javabean.TableInfo;
import com.alfredbase.javabean.model.KDSDevice;
import com.alfredbase.store.sql.KotItemDetailSQL;
import com.alfredbase.store.sql.KotSummarySQL;
import com.alfredbase.store.sql.OrderBillSQL;
import com.alfredbase.store.sql.OrderDetailSQL;
import com.alfredbase.store.sql.TableInfoSQL;
import com.alfredbase.store.sql.cpsql.CPOrderDetailSQL;
import com.alfredbase.utils.LogUtil;
import com.alfredposclient.activity.MainPage;
import com.alfredposclient.global.App;
import com.alfredposclient.global.SyncCentre;
import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KotJob extends Job {
    private String TAG = KotJob.class.getSimpleName();
    private long localId;
    private String kot;
    private String apiName;
    private KDSDevice kds;
    private Map<String, Object> kotMap = new HashMap<String, Object>();
    private Map<String, Object> data = new HashMap<String, Object>();
    private int failCount = 0;

    public KotJob(KDSDevice kdsDevice, KotSummary kotSummary, String method, String apiName) {
        super(new Params(Priority.MID).requireNetwork().persist().groupBy("kot"));
        this.kds = kdsDevice;
        this.apiName = apiName;
        data.put("kotSummary", kotSummary);
        data.put("method", method);
    }

    public KotJob(KDSDevice kds, KotSummary kotSummary, ArrayList<KotItemDetail> itemDetails,
                  ArrayList<KotItemModifier> modifiers, String method, Map<String, Object> kotMap, String apiName) {
        super(new Params(Priority.MID).requireNetwork().persist().groupBy("kot"));
        //group the order, we don't want to send two in parallel
        //use a negative id so that it cannot collide w/ twitter ids
        long time = System.currentTimeMillis();
        kotSummary.setUpdateTime(time);
        localId = -time;
        this.apiName = apiName;
        data.put("kotItemDetails", itemDetails);
        data.put("kotItemModifiers", modifiers);
        data.put("kotSummary", kotSummary);
        data.put("method", method);
        this.kds = kds;
        this.kotMap = kotMap;
        try {
            KotSummarySQL.updateKotSummaryTimeById(time, kotSummary.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        failCount = 0;
    }

    public KotJob(KDSDevice kds, KotSummary kotSummary, ArrayList<KotItemDetail> itemDetails,
                  ArrayList<KotItemModifier> modifiers, String method, Map<String, Object> kotMap) {
        this(kds, kotSummary, itemDetails, modifiers, method, kotMap, false);
    }

    public KotJob(KDSDevice kds, KotSummary kotSummary, ArrayList<KotItemDetail> itemDetails,
                  ArrayList<KotItemModifier> modifiers, String method, Map<String, Object> kotMap, boolean isFire) {
        super(new Params(Priority.MID).requireNetwork().persist().groupBy("kot"));
        //group the order, we don't want to send two in parallel
        //use a negative id so that it cannot collide w/ twitter ids
        long time = System.currentTimeMillis();
        kotSummary.setUpdateTime(time);
        localId = -time;
        apiName = APIName.SUBMIT_NEW_KOT;
        data.put("kotItemDetails", itemDetails);
        data.put("kotItemModifiers", modifiers);
        data.put("kotSummary", kotSummary);
        data.put("method", method);
        data.put("isFire", isFire);
        this.kds = kds;
        this.kotMap = kotMap;
        try {
            KotSummarySQL.updateKotSummaryTimeById(time, kotSummary.getId().intValue());
        } catch (Exception e) {
            e.printStackTrace();
        }
        failCount = 0;
    }

    public KotJob(KDSDevice kds, String action, KotSummary toKotSummary, KotSummary fromKotSummary, Map<String, Object> kotMap) {
        super(new Params(Priority.MID).requireNetwork().persist().groupBy("kot"));
        long time = System.currentTimeMillis();
        try {
            if (toKotSummary != null) {
                toKotSummary.setUpdateTime(time);
                KotSummarySQL.updateKotSummaryTimeById(time, toKotSummary.getId().intValue());
            } else {
                fromKotSummary.setUpdateTime(time);
                KotSummarySQL.updateKotSummaryTimeById(time, fromKotSummary.getId().intValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        localId = -time;
        data.put("action", action);
        data.put("toKotSummary", toKotSummary);
        data.put("fromKotSummary", fromKotSummary);
        this.kds = kds;
        this.kotMap = kotMap;
        apiName = APIName.TRANSFER_KOT;
        failCount = 0;
    }

    public KotJob(KDSDevice kds, KotSummary toKotSummary, KotSummary fromKotSummary, Map<String, Object> kotMap, KotItemDetail kotItemDetail) {
        super(new Params(Priority.MID).requireNetwork().persist().groupBy("kot"));
        long time = System.currentTimeMillis();
        try {
            if (toKotSummary != null) {
                toKotSummary.setUpdateTime(time);
                KotSummarySQL.updateKotSummaryTimeById(time, toKotSummary.getId().intValue());
            } else {
                fromKotSummary.setUpdateTime(time);
                KotSummarySQL.updateKotSummaryTimeById(time, fromKotSummary.getId().intValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        localId = -time;
        data.put("toKotSummary", toKotSummary);
        data.put("fromKotSummary", fromKotSummary);
        data.put("tansferKotItem", kotItemDetail);
        this.kds = kds;
        this.kotMap = kotMap;
        apiName = APIName.TRANSFER_ITEM_KOT;
        failCount = 0;
    }

    @Override
    public void onAdded() {
        //job has been secured to disk, add item to database
        LogUtil.d(TAG, "onAdded");
    }

    @Override
    public void onRun() throws Throwable {
        LogUtil.d(TAG, data.toString());
        BaseActivity context = App.getTopActivity();
        try {
            if (APIName.SUBMIT_NEW_KOT.equals(apiName)) {
                //region submit new KOT
                /*check the latest KOT summary status*/
                KotSummary kotsmy = (KotSummary) data.get("kotSummary");
                KotSummary updatedkot = KotSummarySQL.getKotSummaryById(kotsmy.getId());

                /*if the kotsummary removed or have been done, no need send it to KDS*/
                if (updatedkot == null)
                    return;
                if (updatedkot.getStatus() == ParamConst.KOTS_STATUS_DONE)
                    return;

                SyncCentre.getInstance().syncSubmitKotToKDS(kds, context, data, null);
                if (kotMap == null) {
                    return;
                }
                List<Integer> orderDetailIds = (List<Integer>) kotMap.get("orderDetailIds");
                if (orderDetailIds.size() != 0) {
                    ArrayList<OrderDetail> orderDetails = new ArrayList<OrderDetail>();
                    ArrayList<KotItemDetail> kotItemDetails = new ArrayList<KotItemDetail>();
                    if (kotMap.containsKey("orderPosType") && (Integer) kotMap.get("orderPosType") == ParamConst.POS_TYPE_SUB) {
                        for (int i = 0; i < orderDetailIds.size(); i++) {
                            OrderDetail orderDetail = CPOrderDetailSQL
                                    .getOrderDetail(orderDetailIds.get(i));
                            orderDetail
                                    .setOrderDetailStatus(ParamConst.ORDERDETAIL_STATUS_KOTPRINTERD);
                            KotItemDetail kotItemDetail = KotItemDetailSQL
                                    .getMainKotItemDetailByOrderDetailId(kotsmy.getId(), orderDetailIds
                                            .get(i));
//						kotItemDetail
//								.setKotStatus(ParamConst.KOT_STATUS_UNDONE);
                            orderDetails.add(orderDetail);
                            kotItemDetails.add(kotItemDetail);
                        }
                        CPOrderDetailSQL.addOrderDetailList(orderDetails);
                        KotItemDetailSQL.addKotItemDetailList(kotItemDetails);
                        context.kotPrintStatus(MainPage.KOT_PRINT_SUCCEED, kotMap.get("orderId"));
                    } else {
                        for (int i = 0; i < orderDetailIds.size(); i++) {
                            OrderDetail orderDetail = OrderDetailSQL
                                    .getOrderDetail(orderDetailIds.get(i));
                            orderDetail
                                    .setOrderDetailStatus(ParamConst.ORDERDETAIL_STATUS_KOTPRINTERD);
                            KotItemDetail kotItemDetail = KotItemDetailSQL
                                    .getMainKotItemDetailByOrderDetailId(kotsmy.getId(), orderDetailIds
                                            .get(i));
//						kotItemDetail
//								.setKotStatus(ParamConst.KOT_STATUS_UNDONE);
                            orderDetails.add(orderDetail);
                            kotItemDetails.add(kotItemDetail);
                        }
                        OrderDetailSQL.addOrderDetailList(orderDetails);
                        KotItemDetailSQL.addKotItemDetailList(kotItemDetails);
                        context.kotPrintStatus(MainPage.KOT_PRINT_SUCCEED, kotMap.get("orderId"));
                    }
                }
                //endregion
            } else if (APIName.TRANSFER_KOT.equals(apiName)) {
                //region transfer KOT
                SyncCentre.getInstance().syncTransferTable(kds, context, data, null);

                String action = (String) kotMap.get("action");
                if (ParamConst.JOB_MERGER_KOT.equals(action)) {
                    KotSummary fromKotSummary = (KotSummary) data.get("fromKotSummary");
                    KotSummary toKotSummary = (KotSummary) data.get("toKotSummary");
                    Order oldOrder = (Order) kotMap.get("fromOrder");
                    OrderBill oldOrderBill = OrderBillSQL.getOrderBillByOrder(oldOrder);
                    TableInfo currentTable = TableInfoSQL.getTableById((Integer) kotMap.get("currentTableId"));
                    List<KotItemDetail> kotItemDetails = KotItemDetailSQL
                            .getKotItemDetailBySummaryId(fromKotSummary.getId());
                    for (KotItemDetail kotItemDetail : kotItemDetails) {
                        kotItemDetail.setKotSummaryId(toKotSummary.getId());
                        kotItemDetail.setOrderId(toKotSummary.getOrderId().intValue());
                        KotItemDetailSQL.update(kotItemDetail);
                    }
                    KotSummarySQL.deleteKotSummary(fromKotSummary);
                    context.kotPrintStatus(ParamConst.JOB_TYPE_POS_MERGER_TABLE, null);
                } else if (ParamConst.JOB_TRANSFER_KOT.equals(action)) {
                    KotSummary fromKotSummary = (KotSummary) data.get("fromKotSummary");

                    KotSummarySQL.update(fromKotSummary);
                    Order order = (Order) kotMap.get("fromOrder");
//		    		OrderSQL.update(order);
                    context.kotPrintStatus(ParamConst.JOB_TYPE_POS_TRANSFER_TABLE, order);
                }

                SyncCentre.getInstance().transferTable(context, kotMap);
                //endregion
            } else if (APIName.TRANSFER_ITEM_KOT.equals(apiName)) {
                SyncCentre.getInstance().syncTransferItem(kds, context, data, null);
            } else if (APIName.SUBMIT_TMP_KOT.equals(apiName)) {
                SyncCentre.getInstance().syncSubmitTmpKotToKDS(kds, context, data, null);
            } else if (APIName.SUBMIT_NEXT_KOT.equals(apiName)) {
                SyncCentre.getInstance().syncSubmitKotToNextKDS(kds, context, data, null);
            } else if (APIName.DELETE_KOT_KDS.equals(apiName)) {
                SyncCentre.getInstance().deleteKotSummary(kds, context, data, null);
            } else if (APIName.SUBMIT_SUMMARY_KDS.equals(apiName)) {
                SyncCentre.getInstance().syncSubmitKotToSummaryKDS(kds, context, data, null);
            }
            LogUtil.d(TAG, "KOT JOB Successful");
        } catch (Throwable e) {
            LogUtil.d(TAG, "KOT JOB Failed:" + e.getMessage());
            if (failCount < 2) {
                failCount++;
                context.kotPrintStatus(MainPage.KOT_PRINT_FAILED, "KDS");
            }
            throw new RuntimeException("KOT failed");
        }
    }

    @Override
    protected void onCancel() {
        LogUtil.d(TAG, "onCancel");
    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        return true;
    }
}
