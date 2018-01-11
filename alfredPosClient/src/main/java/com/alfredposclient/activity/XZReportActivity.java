package com.alfredposclient.activity;

import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alfredbase.BaseActivity;
import com.alfredbase.LoadingDialog;
import com.alfredbase.ParamConst;
import com.alfredbase.ParamHelper;
import com.alfredbase.VerifyDialog;
import com.alfredbase.http.ResultCode;
import com.alfredbase.javabean.ItemCategory;
import com.alfredbase.javabean.ItemMainCategory;
import com.alfredbase.javabean.PrinterTitle;
import com.alfredbase.javabean.ReportDaySales;
import com.alfredbase.javabean.ReportDayTax;
import com.alfredbase.javabean.ReportHourly;
import com.alfredbase.javabean.ReportPluDayComboModifier;
import com.alfredbase.javabean.ReportPluDayItem;
import com.alfredbase.javabean.ReportPluDayModifier;
import com.alfredbase.javabean.RevenueCenter;
import com.alfredbase.javabean.model.PrinterDevice;
import com.alfredbase.javabean.model.SessionStatus;
import com.alfredbase.javabean.temporaryforapp.ReportUserOpenDrawer;
import com.alfredbase.store.sql.ItemCategorySQL;
import com.alfredbase.store.sql.ItemMainCategorySQL;
import com.alfredbase.store.sql.ReportDaySalesSQL;
import com.alfredbase.store.sql.ReportDayTaxSQL;
import com.alfredbase.store.sql.ReportHourlySQL;
import com.alfredbase.store.sql.ReportPluDayComboModifierSQL;
import com.alfredbase.store.sql.ReportPluDayItemSQL;
import com.alfredbase.store.sql.ReportPluDayModifierSQL;
import com.alfredbase.store.sql.UserOpenDrawerRecordSQL;
import com.alfredbase.utils.BH;
import com.alfredbase.utils.CommonUtil;
import com.alfredbase.utils.DialogFactory;
import com.alfredbase.utils.ObjectFactory;
import com.alfredbase.utils.TimeUtil;
import com.alfredposclient.Calendar.CalendarCard;
import com.alfredposclient.Calendar.CardGridItem;
import com.alfredposclient.Calendar.OnCellItemClick;
import com.alfredposclient.R;
import com.alfredposclient.adapter.XZReportDetailAdapter;
import com.alfredposclient.adapter.XZReportHourlyAdapter;
import com.alfredposclient.adapter.XZReportSumaryAdapter;
import com.alfredposclient.global.App;
import com.alfredposclient.global.ReportObjectFactory;
import com.alfredposclient.global.SyncCentre;
import com.alfredposclient.global.UIHelp;
import com.alfredposclient.javabean.ReportDetailAnalysisItem;
import com.alfredposclient.utils.AlertToDeviceSetting;
import com.alfredposclient.utils.DialogSelectReportPrint;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XZReportActivity extends BaseActivity {

    private CalendarCard calendarCard;
    private TextView tv_title_name;
    private LinearLayout ll_print;
    private ImageButton btn_back;
    private LinearLayout ll_xz_analsis;
    private long businessDate;
    private SessionStatus session;
    private Calendar calendar;
    private Map<String, Object> data = new HashMap<String, Object>();
    private RevenueCenter revenueCenter;
    private LoadingDialog loadingDialog;
    private ListView lv_hourly_analsis;
    private SimpleDateFormat yearMonthDayFormater = new SimpleDateFormat("yyyy年MM月dd日");
    private String curStr;
    private long date;
    private ReportDaySales reportDaySales;
    private List<ReportDayTax> reportDayTaxs;
    private List<ReportPluDayItem> reportPluDayItems;
    private List<ReportDetailAnalysisItem> reportDetailAnalysisItems;
    private List<ReportDetailAnalysisItem> reportDetailAnalysisItemList;
    private List<ReportPluDayModifier> reportPluDayModifiers;
    private List<ReportHourly> reportHourlys;
    private List<ItemCategory> itemCategorys;
    private List<ItemMainCategory> itemMainCategorys;
    private List<ReportPluDayComboModifier> reportPluDayComboModifiers;
    private Map<String, Object> map = new HashMap<String, Object>();
    private long showBusinessDate;
    private BigDecimal detailTotalAmount;
    private int detailTotalQty;
    private BigDecimal hourlyTotalAmount;
    private int hourlyTotalQty;

    @Override
    protected void initView() {
        super.initView();
        setContentView(R.layout.activity_xzrerort);
        VerifyDialog verifyDialog = new VerifyDialog(context, handler);
        verifyDialog.show("initData",null);
        findViewById(R.id.tv_print).setOnClickListener(this);
        showBusinessDate = 0;
        reportDetailAnalysisItems = new ArrayList<>();
        reportDetailAnalysisItemList = new ArrayList<>();
        detailTotalAmount = BH.getBD(ParamConst.DOUBLE_ZERO);
        hourlyTotalAmount = BH.getBD(ParamConst.DOUBLE_ZERO);
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case VerifyDialog.DIALOG_RESPONSE:
                    init();
                    break;
                case VerifyDialog.DIALOG_DISMISS:
                    XZReportActivity.this.finish();
                    break;
                case XZReportHtml.LOAD_CLOUD_REPORT_COMPLETE:
                    Map<String, Object> param = (Map<String, Object>) msg.obj;
                    Long date = (Long) param.get("bizDate");
                    showBusinessDate = date.longValue();
                    loadOldReport(date);
                    if(loadingDialog != null && loadingDialog.isShowing())
                        loadingDialog.dismiss();
                    initData(businessDate);

                    break;
                case ResultCode.CONNECTION_FAILED:
                    if (loadingDialog!= null && loadingDialog.isShowing()) {
                        loadingDialog.dismiss();
                    }
                    UIHelp.showShortToast(XZReportActivity.this, "No Data");
                    break;
            }
        }
    };

    private void loadNewReport(long businessDate){
        reportDaySales = ReportObjectFactory.getInstance().loadShowReportDaySales(businessDate);
        if(reportDaySales != null) {
            itemCategorys = ItemCategorySQL.getAllItemCategoryForReport();
            itemMainCategorys = ItemMainCategorySQL
                    .getAllItemMainCategoryForReport();
            reportPluDayItems = ReportObjectFactory.getInstance().loadShowReportPluDayItem(businessDate);
            Map<String, Object> map = ReportObjectFactory.getInstance().loadShowReportPluDayModifierInfo(businessDate);
            reportPluDayModifiers = (List<ReportPluDayModifier>) map.get("reportPluDayModifiers");
            reportPluDayComboModifiers = (List<ReportPluDayComboModifier>) map.get("reportPluDayComboModifiers");
            reportHourlys = ReportObjectFactory.getInstance().loadShowReportHourlys(businessDate);
            reportDayTaxs = ReportObjectFactory.getInstance().loadShowReportDayTax(reportDaySales,businessDate);
            loadModel();
        }else{
            reportDayTaxs = Collections.emptyList();
            reportPluDayItems = new ArrayList<ReportPluDayItem>();
            reportPluDayModifiers = Collections.emptyList();
            reportHourlys = Collections.emptyList();
            itemCategorys = Collections.emptyList();
            itemMainCategorys = Collections.emptyList();
            reportDetailAnalysisItems = Collections.emptyList();
            reportDetailAnalysisItemList = Collections.emptyList();

        }

    }
    private void loadOldReport(long businessDate){
        reportDaySales = ReportDaySalesSQL.getReportDaySalesByTime(businessDate);
        if(reportDaySales != null) {
            itemCategorys = ItemCategorySQL.getAllItemCategoryForReport();
            itemMainCategorys = ItemMainCategorySQL
                    .getAllItemMainCategoryForReport();
            reportPluDayItems = ReportPluDayItemSQL.getReportPluDayItemsByTime(businessDate);
            reportPluDayComboModifiers = ReportPluDayComboModifierSQL.getReportPluDayComboModifiersByTime(businessDate);
            reportPluDayModifiers = ReportPluDayModifierSQL.getReportPluDayModifiersByTime(businessDate);
            reportHourlys = ReportHourlySQL.getReportHourlysByTime(businessDate);
            reportDayTaxs = ReportDayTaxSQL.getReportDayTaxsByNowTime(businessDate);
            loadModel();
        }else{
            reportDayTaxs = Collections.emptyList();
            reportPluDayItems = new ArrayList<ReportPluDayItem>();
            reportPluDayModifiers = Collections.emptyList();
            reportHourlys = Collections.emptyList();
            itemCategorys = Collections.emptyList();
            itemMainCategorys = Collections.emptyList();
            reportDetailAnalysisItems = Collections.emptyList();
            reportDetailAnalysisItemList = Collections.emptyList();
        }
    }

    private void loadModel(){
        reportDetailAnalysisItems.clear();
        reportDetailAnalysisItemList.clear();
        detailTotalAmount = BH.getBD(ParamConst.DOUBLE_ZERO);
        hourlyTotalAmount = BH.getBD(ParamConst.DOUBLE_ZERO);
        detailTotalQty = 0;
        hourlyTotalQty = 0;
        if(reportPluDayItems != null && reportPluDayItems.size() > 0) {
            Collections.sort(reportPluDayItems);
            int mainId = 0;
            int id = 0;
            for(ReportPluDayItem reportPluDayItem : reportPluDayItems){
                if(mainId == reportPluDayItem.getItemMainCategoryId().intValue()){
                    ReportDetailAnalysisItem reportDetailAnalysisItem = new ReportDetailAnalysisItem();
                    reportDetailAnalysisItem.setName(reportPluDayItem.getItemName());
                    reportDetailAnalysisItem.setQty(reportPluDayItem.getItemCount());
                    reportDetailAnalysisItem.setAmount(BH.getBD(reportPluDayItem.getItemAmount()));
                    reportDetailAnalysisItem.setShowOther(true);
                    reportDetailAnalysisItems.add(reportDetailAnalysisItem);
                }else{
                    mainId = reportPluDayItem.getItemMainCategoryId().intValue();
                    ReportDetailAnalysisItem reportDetailAnalysisItem1 = new ReportDetailAnalysisItem();
                    reportDetailAnalysisItem1.setName(reportPluDayItem.getItemMainCategoryName());
                    reportDetailAnalysisItem1.setShowOther(false);

                    ReportDetailAnalysisItem reportDetailAnalysisItem = new ReportDetailAnalysisItem();
                    reportDetailAnalysisItem.setName(reportPluDayItem.getItemName());
                    reportDetailAnalysisItem.setQty(reportPluDayItem.getItemCount());
                    reportDetailAnalysisItem.setAmount(BH.getBD(reportPluDayItem.getItemAmount()));
                    reportDetailAnalysisItem.setShowOther(true);

                    reportDetailAnalysisItems.add(reportDetailAnalysisItem1);
                    reportDetailAnalysisItems.add(reportDetailAnalysisItem);
                }
                if(id == reportPluDayItem.getItemCategoryId().intValue()){
                    ReportDetailAnalysisItem reportDetailAnalysisItem = reportDetailAnalysisItemList.get(reportDetailAnalysisItemList.size() - 1);
                    reportDetailAnalysisItem.setQty(reportDetailAnalysisItem.getQty() + reportPluDayItem.getItemCount());
                    reportDetailAnalysisItem.setAmount(BH.add(BH.getBD(reportPluDayItem.getItemAmount()), reportDetailAnalysisItem.getAmount(), true));
                }else{
                    mainId = reportPluDayItem.getItemMainCategoryId().intValue();
                    ReportDetailAnalysisItem reportDetailAnalysisItem1 = new ReportDetailAnalysisItem();
                    reportDetailAnalysisItem1.setName(reportPluDayItem.getItemMainCategoryName());
                    reportDetailAnalysisItem1.setShowOther(false);

                    ReportDetailAnalysisItem reportDetailAnalysisItem = new ReportDetailAnalysisItem();
                    reportDetailAnalysisItem.setName(reportPluDayItem.getItemCategoryName());
                    reportDetailAnalysisItem.setQty(reportPluDayItem.getItemCount());
                    reportDetailAnalysisItem.setAmount(BH.getBD(reportPluDayItem.getItemAmount()));
                    reportDetailAnalysisItem.setShowOther(true);

                    reportDetailAnalysisItemList.add(reportDetailAnalysisItem1);
                    reportDetailAnalysisItemList.add(reportDetailAnalysisItem);
                }
                detailTotalAmount = BH.add(detailTotalAmount, BH.getBD(reportPluDayItem.getItemAmount()), true);
                detailTotalQty += reportPluDayItem.getItemCount().intValue();
            }
        }
        if(reportHourlys != null && reportHourlys.size() > 0){
            for (ReportHourly reportHourly : reportHourlys){
                hourlyTotalAmount = BH.add(hourlyTotalAmount, BH.getBD(reportHourly.getAmountPrice()), true);
                hourlyTotalQty += reportHourly.getAmountQty().intValue();
            }
        }
    }
    private void init(){
        calendarCard = (CalendarCard) findViewById(R.id.calendarCard);
        tv_title_name = (TextView) findViewById(R.id.tv_title_name);
        ll_print = (LinearLayout) findViewById(R.id.ll_print);
        btn_back = (ImageButton) findViewById(R.id.btn_back);
        ll_xz_analsis = (LinearLayout) findViewById(R.id.ll_xz_analsis);
        lv_hourly_analsis = (ListView) findViewById(R.id.lv_hourly_analsis);
        tv_title_name.setText(getString(R.string.day_sales_report));
        ll_print.setVisibility(View.INVISIBLE);
        loadingDialog = new LoadingDialog(this);

        calendar = Calendar.getInstance();
        businessDate = App.instance.getBusinessDate();
        Date date = new Date(businessDate);
        curStr = yearMonthDayFormater.format(date);
//        session = App.instance.getSessionStatus();
        revenueCenter = App.instance.getRevenueCenter();
        reportDaySales = ReportObjectFactory.getInstance().loadShowReportDaySales(businessDate);
        if (reportDaySales != null) {
            String nettsSales = reportDaySales.getNettSales();
            calendarCard.setAmount(nettsSales);
        }
        calendarCard.setDateDisplay(calendar);
        calendarCard.notifyChanges();
        calendarCard.setOnCellItemClick(onCellItemClick);
        btn_back.setOnClickListener(onClickListener);
    }


    private void initData(long time){
        if (reportDaySales != null) {
            calendarCard.setVisibility(View.GONE);
            ll_xz_analsis.setVisibility(View.VISIBLE);
            ll_print.setVisibility(View.VISIBLE);
            // Item Sales
            ((TextView) findViewById(R.id.tv_sales_total_num)).setText(reportDaySales.getItemSalesQty() + "");
            ((TextView) findViewById(R.id.tv_sales_total)).setText("$" + reportDaySales.getItemSales());
            // Stored-Card Sales
            ((TextView) findViewById(R.id.tv_free_menu_num)).setText(reportDaySales.getTopUpsQty() + "");
            ((TextView) findViewById(R.id.tv_free_menu)).setText("$" + reportDaySales.getTopUps());
            // ENT Items
            ((TextView) findViewById(R.id.tv_free_item_num)).setText(reportDaySales.getFocItemQty() + "");
            ((TextView) findViewById(R.id.tv_free_item)).setText("$" + reportDaySales.getFocItem());
            // ENT Bills
            ((TextView) findViewById(R.id.tv_ent_bills_item_num)).setText(reportDaySales.getFocBillQty() + "");
            ((TextView) findViewById(R.id.tv_ent_bills_item)).setText("$" + reportDaySales.getFocBill());
            // VOID/Items
            ((TextView) findViewById(R.id.tv_void_item_num)).setText(reportDaySales.getItemVoidQty() + "");
            ((TextView) findViewById(R.id.tv_void_item)).setText("$" + reportDaySales.getItemVoid());
            // VOID Bills
            ((TextView) findViewById(R.id.tv_void_bills_num)).setText(reportDaySales.getBillVoidQty() + "");
            ((TextView) findViewById(R.id.tv_void_bills_item)).setText("$" + reportDaySales.getBillVoid());
            // REFUND Bills
            ((TextView) findViewById(R.id.tv_refund_bills_item_num)).setText(reportDaySales.getBillRefundQty() + "");
            ((TextView) findViewById(R.id.tv_refund_bills_item)).setText("$" + reportDaySales.getBillRefund());
            // REFUND Taxes
            ((TextView) findViewById(R.id.tv_refund_taxes_item_num)).setText("");
            ((TextView) findViewById(R.id.tv_refund_taxes_item)).setText("$" + reportDaySales.getRefundTax());
            // Discount on%
            ((TextView) findViewById(R.id.tv_discount_on_item_num)).setText(reportDaySales.getDiscountPerQty() + "");
            ((TextView) findViewById(R.id.tv_discount_on_item)).setText("$" + reportDaySales.getDiscountPer());
            // Discount on$
            ((TextView) findViewById(R.id.tv_discount_item_num)).setText(reportDaySales.getDiscountQty() + "");
            ((TextView) findViewById(R.id.tv_discount_item)).setText("$" + reportDaySales.getDiscount());
            // Exlusive Tax
            ((TextView) findViewById(R.id.tv_exlusive_tax_item_num)).setText("");
            ((TextView) findViewById(R.id.tv_exlusive_tax_item)).setText("$" + reportDaySales.getTotalTax());
            // Inclusive Tax
            ((TextView) findViewById(R.id.tv_inclusive_tax_item_num)).setText("");
            ((TextView) findViewById(R.id.tv_inclusive_tax_item)).setText("$" + reportDaySales.getInclusiveTaxAmt());
            // Total/sales
            ((TextView) findViewById(R.id.tv_total_sales_item_num)).setText("");
            ((TextView) findViewById(R.id.tv_total_sales_item)).setText("$" + reportDaySales.getTotalSales());
            // CASH
            ((TextView) findViewById(R.id.tv_cash_item_num)).setText(reportDaySales.getCashQty() + "");
            ((TextView) findViewById(R.id.tv_cash_item)).setText("$" + reportDaySales.getCash());
            // Diner App
            ((TextView) findViewById(R.id.tv_diner_app_item_num)).setText(reportDaySales.getPaypalpayQty() + "");
            ((TextView) findViewById(R.id.tv_diner_app_item)).setText("$" + reportDaySales.getPaypalpay());
            // Stored-Card Use
            ((TextView) findViewById(R.id.tv_storedcard_use_item_num)).setText(reportDaySales.getStoredCardQty() + "");
            ((TextView) findViewById(R.id.tv_storedcard_use_item)).setText("$" + reportDaySales.getStoredCard());
            // Stored-Card Charge
            ((TextView) findViewById(R.id.tv_storedcard_charge_item_num)).setText(reportDaySales.getTopUpsQty() + "");
            ((TextView) findViewById(R.id.tv_storedcard_charge_item)).setText("$" + reportDaySales.getTopUps());
            // NETS
            ((TextView) findViewById(R.id.tv_nets_item_num)).setText(reportDaySales.getNetsQty() + "");
            ((TextView) findViewById(R.id.tv_nets_item)).setText("$" + reportDaySales.getNets());
            // VISA
            ((TextView) findViewById(R.id.tv_visa_item_num)).setText(reportDaySales.getVisaQty() + "");
            ((TextView) findViewById(R.id.tv_visa_item)).setText("$" + reportDaySales.getVisa());
            // MC
            ((TextView) findViewById(R.id.tv_mc_item_num)).setText(reportDaySales.getMcQty() + "");
            ((TextView) findViewById(R.id.tv_mc_item)).setText("$" + reportDaySales.getMc());
            // AMEX
            ((TextView) findViewById(R.id.tv_amex_item_num)).setText(reportDaySales.getAmexQty() + "");
            ((TextView) findViewById(R.id.tv_amex_item)).setText("$" + reportDaySales.getAmex());
            // JBL
            ((TextView) findViewById(R.id.tv_jbl_item_num)).setText(reportDaySales.getJblQty() + "");
            ((TextView) findViewById(R.id.tv_jbl_item)).setText("$" + reportDaySales.getJbl());
            // unionPAYPayQty
            ((TextView) findViewById(R.id.tv_unionpayqty_item_num)).setText(reportDaySales.getUnionPayQty() + "");
            ((TextView) findViewById(R.id.tv_unionpayqty_item)).setText("$" + reportDaySales.getUnionPay());
            // Diner
            ((TextView) findViewById(R.id.tv_diner_item_num)).setText(reportDaySales.getDinerQty() + "");
            ((TextView) findViewById(R.id.tv_diner_item)).setText("$" + reportDaySales.getDiner());
            // BOH
            ((TextView) findViewById(R.id.tv_boh_item_num)).setText(reportDaySales.getHoldldQty() + "");
            ((TextView) findViewById(R.id.tv_boh_item)).setText("$" + reportDaySales.getHoldld());
            // DELIVEROO
            ((TextView) findViewById(R.id.tv_deliveoo_item_num)).setText(reportDaySales.getDeliverooQty() + "");
            ((TextView) findViewById(R.id.tv_deliveoo_item)).setText("$" + reportDaySales.getDeliveroo());
            // UBEREATS
            ((TextView) findViewById(R.id.tv_ubereats_item_num)).setText(reportDaySales.getUbereatsQty() + "");
            ((TextView) findViewById(R.id.tv_ubereats_item)).setText("$" + reportDaySales.getUbereats());
            // FOODPANDA
            ((TextView) findViewById(R.id.tv_foodpanda_item_num)).setText(reportDaySales.getFoodpandaQty() + "");
            ((TextView) findViewById(R.id.tv_foodpanda_item)).setText("$" + reportDaySales.getFoodpanda());
            // VOUCHER
            ((TextView) findViewById(R.id.tv_voucher_item_num)).setText(reportDaySales.getVoucherQty() + "");
            ((TextView) findViewById(R.id.tv_voucher_item)).setText("$" + reportDaySales.getVoucher());
            // TOTAL DELIVERY
            ((TextView) findViewById(R.id.tv_total_delivery_item_num)).setText(reportDaySales.getDeliverooQty().intValue() + reportDaySales.getUbereatsQty().intValue() + reportDaySales.getFoodpandaQty().intValue() + "");
            float f = Float.parseFloat(reportDaySales.getDeliveroo()) + Float.parseFloat(reportDaySales.getUbereats()) + Float.parseFloat(reportDaySales.getFoodpanda());
            ((TextView) findViewById(R.id.tv_total_delivery_item)).setText("$" + f + "");
            // TOTAL/CARD
            ((TextView) findViewById(R.id.tv_total_card_item_num)).setText(reportDaySales.getTotalCardQty() + "");
            ((TextView) findViewById(R.id.tv_total_card_item)).setText("$" + reportDaySales.getTotalCard());
            // TOTAL/NETS
            ((TextView) findViewById(R.id.tv_total_nets_item_num)).setText(reportDaySales.getNetsQty() + "");
            ((TextView) findViewById(R.id.tv_total_nets_item)).setText("$" + reportDaySales.getNets());
            // TOTAL/CASH
            ((TextView) findViewById(R.id.tv_total_cash_item_num)).setText(reportDaySales.getTotalCashQty() + "");
            ((TextView) findViewById(R.id.tv_total_cash_item)).setText("$" + reportDaySales.getTotalCash());
            // TOTAL/BOH
            ((TextView) findViewById(R.id.tv_total_boh_item_num)).setText(reportDaySales.getHoldldQty() + "");
            ((TextView) findViewById(R.id.tv_total_boh_item)).setText("$" + reportDaySales.getHoldld());
            // Netts/Sales
            ((TextView) findViewById(R.id.tv_nett_sales_item_num)).setText("");
            ((TextView) findViewById(R.id.tv_nett_sales_item)).setText("$" + reportDaySales.getNettSales());
            // VOID/Items
            ((TextView) findViewById(R.id.tv_summary_voiditems_item_num)).setText(reportDaySales.getItemVoidQty() + "");
            ((TextView) findViewById(R.id.tv_summary_voiditems_item)).setText("$" + reportDaySales.getItemVoid());
            // VOID Bills
            ((TextView) findViewById(R.id.tv_summary_voidbills_item_num)).setText(reportDaySales.getBillVoidQty() + "");
            ((TextView) findViewById(R.id.tv_summary_voidbills_item)).setText("$" + reportDaySales.getBillVoid());
            // TOTAL VOID
            ((TextView) findViewById(R.id.tv_summary_totalvoids_item_num)).setText("");
            float f1 = Float.parseFloat(reportDaySales.getItemVoid()) + Float.parseFloat(reportDaySales.getBillVoid());
            ((TextView) findViewById(R.id.tv_summary_totalvoids_item)).setText("$" + f1 + "");
            // REFUND Bills
            ((TextView) findViewById(R.id.tv_summary_refundbills_num)).setText(reportDaySales.getBillRefundQty() + "");
            ((TextView) findViewById(R.id.tv_summary_refundbills_item)).setText("$" + reportDaySales.getBillRefund());
            // REFUND Taxes
            ((TextView) findViewById(R.id.tv_summary_refundtaxes_item_num)).setText("");
            ((TextView) findViewById(R.id.tv_summary_refundtaxes_item)).setText("$" + reportDaySales.getRefundTax());
            // TOTAL REFUND
            ((TextView) findViewById(R.id.tv_summary_totalrefund_item_num)).setText("");
            float f2 = Float.parseFloat(reportDaySales.getBillRefund()) + Float.parseFloat(reportDaySales.getRefundTax());
            ((TextView) findViewById(R.id.tv_summary_totalrefund_item)).setText("$" + f2 + "");
            // Discount on%
            ((TextView) findViewById(R.id.tv_discounts_discounton_item_num)).setText(reportDaySales.getDiscountPerQty() + "");
            ((TextView) findViewById(R.id.tv_discounts_discounton_item)).setText("$" + reportDaySales.getDiscountPer());
            // Discount on$
            ((TextView) findViewById(R.id.tv_discounts_discount_item_num)).setText(reportDaySales.getDiscountQty() + "");
            ((TextView) findViewById(R.id.tv_discounts_discount_item)).setText("$" + reportDaySales.getDiscount());
            // Total Discount
            ((TextView) findViewById(R.id.tv_discounts_totaldiscount_item_num)).setText("");
            float f3 = Float.parseFloat(reportDaySales.getDiscountPer()) + Float.parseFloat(reportDaySales.getDiscount());
            ((TextView) findViewById(R.id.tv_discounts_totaldiscount_item)).setText("$" + f3 + "");
            // Svc Charge
//        ((TextView)findViewById(R.id.tv_svc_charge_item_num)).setText(reportDaySales.getDiscountPerQty() + "");
//        ((TextView)findViewById(R.id.tv_svc_charge_item)).setText("$" + reportDaySales.getDiscountPer());
            // GST
//        ((TextView)findViewById(R.id.tv_gst_item_num)).setText(reportDaySales.getDiscountPerQty() + "");
//        ((TextView)findViewById(R.id.tv_gst_item)).setText("$" + reportDaySales.getDiscountPer());
            // Inclusive Tax
            ((TextView) findViewById(R.id.tv_tax_inclusivetax_item_num)).setText("");
            ((TextView) findViewById(R.id.tv_tax_inclusivetax_item)).setText("$" + reportDaySales.getInclusiveTaxAmt());
            // Total Tax
            ((TextView) findViewById(R.id.tv_tax_totaltax_item_num)).setText("");
            float f4 = Float.parseFloat(reportDaySales.getTotalTax()) + Float.parseFloat(reportDaySales.getInclusiveTaxAmt());
            ((TextView) findViewById(R.id.tv_tax_totaltax_item)).setText("$" + f4 + "");
            // Start Drawer
            ((TextView) findViewById(R.id.tv_start_drawer_item_num)).setText("");
            ((TextView) findViewById(R.id.tv_start_drawer_item)).setText("$" + reportDaySales.getStartDrawerAmount());
            // TOTAL CASH
            ((TextView) findViewById(R.id.tv_drawer_totalcash_item_num)).setText("");
            ((TextView) findViewById(R.id.tv_drawer_totalcash_item)).setText("$" + reportDaySales.getTotalCash());
            // Stored-card Cash Charge
            ((TextView) findViewById(R.id.tv_drawer_storedcharge_item_num)).setText("");
            ((TextView) findViewById(R.id.tv_drawer_storedcharge_item)).setText("$" + reportDaySales.getCashTopUp());
            // Cash In
            ((TextView) findViewById(R.id.tv_cashIn_item_num)).setText("");
            ((TextView) findViewById(R.id.tv_cashIn_item)).setText("$" + reportDaySales.getCashInAmt());
            // Cash Out
            ((TextView) findViewById(R.id.tv_cashout_item_num)).setText("");
            ((TextView) findViewById(R.id.tv_cashout_item)).setText("$" + reportDaySales.getCashOutAmt());
            // Expected In Drawer
            ((TextView) findViewById(R.id.tv_expectedindrawer_item_num)).setText("");
            ((TextView) findViewById(R.id.tv_expectedindrawer_item)).setText("$" + reportDaySales.getExpectedAmount());
            // Actual In Drawer
            ((TextView) findViewById(R.id.tv_actualindrawer_item_num)).setText("");
            ((TextView) findViewById(R.id.tv_actualindrawer_item)).setText("$" + reportDaySales.getWaiterAmount());
            // Difference
            ((TextView) findViewById(R.id.tv_difference_item_num)).setText("");
            ((TextView) findViewById(R.id.tv_difference_item)).setText("$" + reportDaySales.getDifference());

            ((TextView) findViewById(R.id.tv_total_bill)).setText(reportDaySales.getTotalBills().toString());
            ((TextView) findViewById(R.id.tv_total_temp_menu)).setText(reportDaySales.getOpenCount().toString());
            ((TextView)findViewById(R.id.tv_detail_total_num)).setText(detailTotalQty + "");
            ((TextView)findViewById(R.id.tv_detail_total)).setText(detailTotalAmount.toString());
            ((TextView)findViewById(R.id.tv_summary_total_num)).setText(detailTotalQty + "");
            ((TextView)findViewById(R.id.tv_summary_total)).setText(detailTotalAmount.toString());
            ((TextView)findViewById(R.id.tv_hourly_total_num)).setText(hourlyTotalQty + "");
            ((TextView)findViewById(R.id.tv_hourly_total)).setText(hourlyTotalAmount.toString());
            if (reportHourlys != null && reportHourlys.size() > 0) {
                XZReportHourlyAdapter hourlyAdapter = new XZReportHourlyAdapter(reportHourlys, XZReportActivity.this);
                lv_hourly_analsis.setAdapter(hourlyAdapter);
            }
            if(reportPluDayItems != null && reportPluDayItems.size() > 0){
                XZReportDetailAdapter xzReportDetailAdapter = new XZReportDetailAdapter(reportDetailAnalysisItems, context);
                ((ListView) findViewById(R.id.lv_detail_analsis)).setAdapter(xzReportDetailAdapter);
                XZReportSumaryAdapter xzReportSumaryAdapter = new XZReportSumaryAdapter(reportDetailAnalysisItemList, context);
                ((ListView) findViewById(R.id.lv_summary_sales)).setAdapter(xzReportSumaryAdapter);
            }

        }else {
            UIHelp.showShortToast(XZReportActivity.this, "No Data");
            calendarCard.setVisibility(View.VISIBLE);
            ll_xz_analsis.setVisibility(View.GONE);
            ll_print.setVisibility(View.GONE);
        }

    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_back:
                if (calendarCard.getVisibility() == View.VISIBLE && ll_xz_analsis.getVisibility() == View.GONE) {
                    XZReportActivity.this.finish();
                } else if (calendarCard.getVisibility() == View.GONE && ll_xz_analsis.getVisibility() == View.VISIBLE) {
                    ll_xz_analsis.setVisibility(View.GONE);
                    calendarCard.setVisibility(View.VISIBLE);
                    ll_print.setVisibility(View.INVISIBLE);
                    tv_title_name.setText(getString(R.string.day_sales_report));
                }
                    break;
                case R.id.ll_print:

                    break;
            }
        }
    };

    // 打印
//    private void print(){
//        try {
//            jsonObject = new JSONObject(str);
//            final Long bizDate = jsonObject.optLong("bizDate");
//            final boolean zPrint = jsonObject.optBoolean("z");
//            if (reportDaySales != null) {
//                DialogSelectReportPrint.show(context,
//                        new View.OnClickListener() {
//
//                            @Override
//                            public void onClick(View v) {
//                                switch (v.getId()) {
//                                    case R.id.btn_report_all: {
//                                        sendPrintData(
//                                                XZReportHtml.REPORT_PRINT_ALL,
//                                                zPrint, bizDate);
//                                    }
//                                    break;
//                                    case R.id.btn_report_sales: {
//                                        sendPrintData(
//                                                XZReportHtml.REPORT_PRINT_SALES,
//                                                zPrint, bizDate);
//                                    }
//                                    break;
//                                    case R.id.btn_report_detail_analysis: {
//                                        sendPrintData(
//                                                XZReportHtml.REPORT_PRINT_DETAILS,
//                                                zPrint, bizDate);
//                                    }
//                                    break;
//                                    case R.id.btn_report_summary_analysis: {
//                                        sendPrintData(
//                                                XZReportHtml.REPORT_PRINT_SUMMARY,
//                                                zPrint, bizDate);
//                                    }
//                                    break;
//                                }
//                            }
//                        });
//            } else {
//                DialogFactory.alertDialog(
//                        XZRerortActivity.this,
//                        context.getResources().getString(
//                                R.string.warning),
//                        context.getResources().getString(
//                                R.string.no_sales_print));
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }

    OnCellItemClick onCellItemClick = new OnCellItemClick() {
        @Override
        public void onCellClick(View v, CardGridItem item) {
            date = item.getDate().getTimeInMillis();
            Date date1 = new Date(item.getDate().getTimeInMillis());
            SimpleDateFormat yearMonthDayFormater = new SimpleDateFormat("yyyy年MM月dd日");
            String str = yearMonthDayFormater.format(date1);
            loadingDialog.setTitle("Loading");
            loadingDialog.show();
            if (str.equals(curStr)) {
                date = businessDate;
                showBusinessDate = date;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        loadNewReport(showBusinessDate);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(loadingDialog != null && loadingDialog.isShowing())
                                    loadingDialog.dismiss();
                                initData(businessDate);
                            }
                        });
                    }
                }).start();


            }else if (revenueCenter != null) {

                Map<String, Object> param = new HashMap<String, Object>();
                param.put("businessDate", TimeUtil.getMDY(date));
                param.put("bizDate", TimeUtil.getBusinessDateByDay(date, 0));
                param.put("revenueId", revenueCenter.getId().intValue());
                SyncCentre.getInstance().loadCloudDaySalesReport(
                        context, param, handler);
            }
        }
    };


    @Override
    protected void handlerClickEvent(View v) {
        switch (v.getId()){
            case R.id.tv_print:
                showPrintDialog(showBusinessDate, showBusinessDate == businessDate ? true:false);
                break;
        }
        super.handlerClickEvent(v);
    }


    private void showPrintDialog(final Long bizDate, final boolean zPrint){
            if (reportDaySales != null) {
                DialogSelectReportPrint.show(context,
                        new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                switch (v.getId()) {
                                    case R.id.btn_report_all: {
                                        sendPrintData(
                                                XZReportHtml.REPORT_PRINT_ALL,
                                                zPrint, bizDate);
                                    }
                                    break;
                                    case R.id.btn_report_sales: {
                                        sendPrintData(
                                                XZReportHtml.REPORT_PRINT_SALES,
                                                zPrint, bizDate);
                                    }
                                    break;
                                    case R.id.btn_report_detail_analysis: {
                                        sendPrintData(
                                                XZReportHtml.REPORT_PRINT_DETAILS,
                                                zPrint, bizDate);
                                    }
                                    break;
                                    case R.id.btn_report_summary_analysis: {
                                        sendPrintData(
                                                XZReportHtml.REPORT_PRINT_SUMMARY,
                                                zPrint, bizDate);
                                    }
                                    break;
                                }
                            }
                        });
            } else {
                DialogFactory.alertDialog(
                        context,
                        context.getResources().getString(
                                R.string.warning),
                        context.getResources().getString(
                                R.string.no_sales_print));
            }
    }

    private void sendPrintData(int type, boolean zPrint, long bzDate) {
        String bizDate = TimeUtil.getPrintingDate(bzDate);
        String rptType = CommonUtil.getReportType(context, 999);

        SessionStatus session = App.instance.getSessionStatus();

        String label = "YX";
        if (zPrint) {
            label = "YZ";
        } else {
            rptType = CommonUtil.getReportType(context,
                    session.getSession_status());
            bizDate = TimeUtil.getPrintingDate(businessDate);
        }

        PrinterTitle title = ObjectFactory.getInstance()
                .getPrinterTitleForReport(
                        App.instance.getRevenueCenter().getId(),
                        label
                                + ParamHelper.getPrintOrderBillNo(
                                App.instance.getIndexOfRevenueCenter(),
                                0),
                        App.instance.getUser().getFirstName()
                                + App.instance.getUser().getLastName(), null,
                        bizDate);

        PrinterDevice cashierPrinter = App.instance.getCahierPrinter();
        List<ReportUserOpenDrawer> reportUserOpenDrawers = new ArrayList<ReportUserOpenDrawer>();
        if(zPrint){
            reportUserOpenDrawers = UserOpenDrawerRecordSQL.getReportUserOpenDrawerByTime(businessDate);
        }else{
            reportUserOpenDrawers = UserOpenDrawerRecordSQL.getReportUserOpenDrawer(session.getSession_status(), bzDate);
        }
        if (cashierPrinter == null) {
            AlertToDeviceSetting.noKDSorPrinter(context, context.getResources()
                    .getString(R.string.no_printer_devices));
        } else {
            if (type == XZReportHtml.REPORT_PRINT_ALL) {

                // sales report
                App.instance.remotePrintDaySalesReport(rptType, cashierPrinter,
                        title, reportDaySales, reportDayTaxs, reportUserOpenDrawers, null);
                // detail analysis
                App.instance.remotePrintDetailAnalysisReport(rptType,
                        cashierPrinter, title, reportDaySales,
                        reportPluDayItems, reportPluDayModifiers,
                        reportPluDayComboModifiers, itemMainCategorys,
                        itemCategorys);
                // summary
                App.instance
                        .remotePrintSummaryAnalysisReport(rptType,
                                cashierPrinter, title, reportPluDayItems,
                                reportPluDayModifiers, itemMainCategorys,
                                itemCategorys);
                // hourly sales
                App.instance.remotePrintHourlyReport(rptType, cashierPrinter,
                        title, reportHourlys);
            }
            if (type == XZReportHtml.REPORT_PRINT_SALES) {
                // sales report
                App.instance.remotePrintDaySalesReport(rptType, cashierPrinter,
                        title, reportDaySales, reportDayTaxs, reportUserOpenDrawers, null);
            }
            if (type == XZReportHtml.REPORT_PRINT_DETAILS) {
                if (zPrint)
                    App.instance.remotePrintDetailAnalysisReport(rptType,
                            cashierPrinter, title, reportDaySales,
                            reportPluDayItems, reportPluDayModifiers,
                            reportPluDayComboModifiers, itemMainCategorys,
                            itemCategorys);
                else
                    App.instance.remotePrintDetailAnalysisReport(rptType,
                            cashierPrinter, title, null, reportPluDayItems,
                            reportPluDayModifiers, reportPluDayComboModifiers,
                            itemMainCategorys, itemCategorys);

                // if(reportPluDayComboModifiers != null &&
                // reportPluDayComboModifiers.size() > 0) {
                // App.instance.remotePrintComboDetailAnalysisReport(rptType,
                // cashierPrinter, title,
                // filteredPluDayItems, reportPluDayComboModifiers);
                // }

            }
            if (type == XZReportHtml.REPORT_PRINT_SUMMARY) {
                // sales report
                App.instance
                        .remotePrintSummaryAnalysisReport(rptType,
                                cashierPrinter, title, reportPluDayItems,
                                reportPluDayModifiers, itemMainCategorys,
                                itemCategorys);
            }
            if (type == XZReportHtml.REPORT_PRINT_HOURLY) {
                // hourly sales
                App.instance.remotePrintHourlyReport(rptType, cashierPrinter,
                        title, reportHourlys);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (calendarCard.getVisibility() == View.GONE && ll_xz_analsis.getVisibility() == View.VISIBLE) {
                calendarCard.setVisibility(View.VISIBLE);
                ll_xz_analsis.setVisibility(View.GONE);
                ll_print.setVisibility(View.INVISIBLE);
                tv_title_name.setText(getString(R.string.day_sales_report));
            } else {
                this.finish();
            }
        }
        return false;
    }
}
