package com.alfredposclient.activity.kioskactivity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alfredbase.BaseActivity;
import com.alfredbase.LoadingDialog;
import com.alfredbase.ParamConst;
import com.alfredbase.global.CoreData;
import com.alfredbase.javabean.KotItemDetail;
import com.alfredbase.javabean.KotItemModifier;
import com.alfredbase.javabean.KotSummary;
import com.alfredbase.javabean.Order;
import com.alfredbase.javabean.OrderBill;
import com.alfredbase.javabean.OrderDetail;
import com.alfredbase.javabean.OrderModifier;
import com.alfredbase.javabean.Payment;
import com.alfredbase.javabean.PaymentSettlement;
import com.alfredbase.javabean.RoundAmount;
import com.alfredbase.store.sql.KotItemDetailSQL;
import com.alfredbase.store.sql.KotItemModifierSQL;
import com.alfredbase.store.sql.KotSummarySQL;
import com.alfredbase.store.sql.OrderBillSQL;
import com.alfredbase.store.sql.OrderDetailSQL;
import com.alfredbase.store.sql.OrderModifierSQL;
import com.alfredbase.store.sql.OrderSQL;
import com.alfredbase.store.sql.PaymentSQL;
import com.alfredbase.store.sql.PaymentSettlementSQL;
import com.alfredbase.store.sql.TableInfoSQL;
import com.alfredbase.utils.BH;
import com.alfredbase.utils.ObjectFactory;
import com.alfredbase.utils.OrderHelper;
import com.alfredbase.utils.RoundUtil;
import com.alfredbase.utils.TextTypeFace;
import com.alfredbase.utils.TimeUtil;
import com.alfredbase.utils.ToastUtils;
import com.alfredposclient.R;
import com.alfredposclient.global.App;
import com.alfredposclient.global.UIHelp;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Alex on 2018/4/16.
 */

public class KioskHoldActivity extends BaseActivity {
    private boolean hasOrder;
    private LinearLayout ll_orderdetail_layout;
    private ListView lv_order_list;
    private ListView lv_orderdetail_list;
    private LayoutInflater inflater;
    private List<Order> orderList = new ArrayList<>();
    private List<OrderDetail> orderDetails = new ArrayList<>();
    private int selectOrderItem = 0;
    private KioskHoldOderAdapter kioskHoldOderAdapter;
    private KioskHoldOderDetailAdapter kioskHoldOderDetailAdapter;
    private TextTypeFace textTypeFace = TextTypeFace.getInstance();
    private int selectViewId;
    private TextView tv_hold_order;
    private TextView tv_kiosk_order;
    private TextView tv_remarks;
    private TextView tv_eat_type;
    private Button btn_get_order;
    private Button btn_close_w_cash;
    private Button btn_refresh;
    private Order currentOrder;
    private LinearLayout ll_remarks;
    public static final int CHECK_REQUEST_CODE = 100;
    public static final int CHECK_RESULT_CODE = 100;
    @Override
    protected void initView() {
        super.initView();
        setContentView(R.layout.activity_kiosk_hold);
        Intent intent = getIntent();
        hasOrder = intent.getBooleanExtra("hasOrder", true);
        if(intent.hasExtra("currentOrder")) {
            currentOrder = (Order) intent.getExtras().get("currentOrder");
        }
//        orderList =
        ll_orderdetail_layout = (LinearLayout) findViewById(R.id.ll_orderdetail_layout);
        loadingDialog = new LoadingDialog(this);
        loadingDialog.setTitle("Loading");
        inflater = LayoutInflater.from(this);
        selectOrderItem = 0;
        selectViewId = R.id.tv_hold_order;
        lv_order_list = (ListView) findViewById(R.id.lv_order_list);
        lv_orderdetail_list = (ListView) findViewById(R.id.lv_orderdetail_list);
        tv_hold_order = (TextView) findViewById(R.id.tv_hold_order);
        tv_kiosk_order = (TextView) findViewById(R.id.tv_kiosk_order);
        btn_close_w_cash = (Button) findViewById(R.id.btn_close_w_cash);
        btn_get_order = (Button) findViewById(R.id.btn_get_order);
        btn_refresh = (Button) findViewById(R.id.btn_refresh);
        ll_remarks = (LinearLayout) findViewById(R.id.ll_remarks);
        tv_remarks = (TextView) findViewById(R.id.tv_remarks);
        tv_eat_type = (TextView) findViewById(R.id.tv_eat_type);
        lv_order_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                selectOrderItem = arg2;
                refreshDataView();
            }
        });
        initData();
        kioskHoldOderAdapter = new KioskHoldOderAdapter();
        lv_order_list.setAdapter(kioskHoldOderAdapter);
        kioskHoldOderDetailAdapter = new KioskHoldOderDetailAdapter();
        lv_orderdetail_list.setAdapter(kioskHoldOderDetailAdapter);
        tv_hold_order.setOnClickListener(this);
        tv_kiosk_order.setOnClickListener(this);
        btn_close_w_cash.setOnClickListener(this);
        btn_get_order.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        hasOrder = true;
        currentOrder = null;
        super.onDestroy();
    }

    private void initData(){
        tv_hold_order.setBackgroundColor(getResources().getColor(R.color.white));
        tv_hold_order.setTextColor(getResources().getColor(R.color.black));
        tv_kiosk_order.setBackgroundColor(getResources().getColor(R.color.white));
        tv_kiosk_order.setTextColor(getResources().getColor(R.color.black));
        int orderStatus = ParamConst.ORDER_STATUS_HOLD;
        switch (selectViewId){
            case R.id.tv_hold_order:
                orderStatus = ParamConst.ORDER_STATUS_HOLD;
                tv_hold_order.setBackgroundColor(getResources().getColor(R.color.brownness));
                tv_hold_order.setTextColor(getResources().getColor(R.color.white));
                break;
            case R.id.tv_kiosk_order:
                orderStatus = ParamConst.ORDER_STATUS_KIOSK;
                tv_kiosk_order.setBackgroundColor(getResources().getColor(R.color.brownness));
                tv_kiosk_order.setTextColor(getResources().getColor(R.color.white));
                break;
        }
        orderList = OrderSQL.getOrderByStatus(orderStatus, App.instance.getSessionStatus());
        if(orderList != null && orderList.size() > 0){
            if(selectOrderItem >= orderList.size() || selectOrderItem < 0){
                selectOrderItem = 0;
            }
            Order order = orderList.get(selectOrderItem);
            orderDetails = OrderDetailSQL.getOrderDetails(order.getId().intValue());
            if(orderDetails != null && orderDetails.size() > 0){
                ll_orderdetail_layout.setVisibility(View.VISIBLE);
            }else{
                ll_orderdetail_layout.setVisibility(View.INVISIBLE);
            }
            if(TextUtils.isEmpty(order.getOrderRemark())){
                ll_remarks.setVisibility(View.GONE);
            }else{
                ll_remarks.setVisibility(View.VISIBLE);
                tv_remarks.setText(order.getOrderRemark());
            }
            if(order.getIsTakeAway().intValue() == ParamConst.TAKE_AWAY){
                tv_eat_type.setText("Take Away");
            }else{
                tv_eat_type.setText("Eat In");
            }
        }else{
            ll_orderdetail_layout.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    protected void handlerClickEvent(View v) {
        super.handlerClickEvent(v);
        switch (v.getId()){
            case R.id.tv_hold_order:
            case R.id.tv_kiosk_order:
                selectViewId = v.getId();
                refreshDataView();
                break;
            case R.id.btn_close_w_cash: {
                Order order = orderList.get(selectOrderItem);
                if (order != null && order.getId() != null) {
                    cashPay(order);

                }
            }
                break;
            case R.id.btn_get_order: {
                if (hasOrder) {
                    ToastUtils.showToast(context, "Please close the last order");
                } else {
                    Order order = orderList.get(selectOrderItem);
                    if (order != null && order.getId() != null) {
                        if (currentOrder != null) {
                            OrderSQL.deleteOrder(currentOrder);
                        }
                        OrderSQL.updateOrderStatus(ParamConst.ORDER_STATUS_OPEN_IN_POS, order.getId().intValue());
                        this.finish();
                    }
                }
            }
                break;
            case R.id.btn_refresh:
                refreshDataView();
                break;
        }

    }

    private void  refreshDataView(){
        initData();
        kioskHoldOderAdapter.notifyDataSetChanged();
        kioskHoldOderDetailAdapter.notifyDataSetChanged();
    }

    private void cashPay(final Order order) {
        loadingDialog.show();
        List<OrderDetail> orderDetailList = OrderDetailSQL.getAllOrderDetailsByOrder(order);
        if (orderDetailList.isEmpty()) {
            UIHelp.showShortToast(context, getResources().getString(R.string.no_order_detail));
            return;
        }
        OrderBill orderBill = ObjectFactory.getInstance().getOrderBill(
                order, App.instance.getRevenueCenter());
        OrderBillSQL.add(orderBill);
        final Payment payment = ObjectFactory.getInstance().getPayment(order, orderBill);
        BigDecimal remainTotalAfterRound = RoundUtil.getPriceAfterRound(App.instance.getLocalRestaurantConfig().getRoundType(), BH.getBD(order.getTotal()));
        RoundAmount roundAmount = ObjectFactory.getInstance().getRoundAmount(order, orderBill, BH.getBD(order.getTotal()), App.instance.getLocalRestaurantConfig().getRoundType());
        order.setOrderStatus(ParamConst.ORDER_STATUS_FINISHED);
        OrderHelper.setOrderTotalAlfterRound(order, roundAmount);
        OrderSQL.update(order);
        PaymentSettlement paymentSettlement = ObjectFactory.getInstance()
                .getPaymentSettlement(payment, ParamConst.SETTLEMENT_TYPE_CASH, remainTotalAfterRound.toString());
        PaymentSQL.updatePaymentAmount(order.getTotal(), order.getId().intValue());
        PaymentSettlementSQL.addPaymentSettlement(paymentSettlement);
        final int paidOrderId = order.getId();
        new Thread(new Runnable() {

            @Override
            public void run() {
                Order placedOrder = OrderSQL.getOrder(order.getId());
                List<OrderDetail> placedOrderDetails
                        = OrderDetailSQL.getOrderDetailsForPrint(placedOrder.getId());
                KotSummary kotSummary = ObjectFactory.getInstance()
                        .getKotSummary(
                                TableInfoSQL.getTableById(
                                        placedOrder.getTableId()).getName(), placedOrder,
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
                                        placedOrder,
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
                                .getOrderModifiers(placedOrder, orderDetail);
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
                PaymentSettlementSQL.deleteAllNoActiveSettlement(payment);
                final HashMap<String, String> map = new HashMap<String, String>();
                map.put("orderId", String.valueOf(paidOrderId));
                map.put("paymentId", String.valueOf(payment.getId().intValue()));
                map.put("isPrint", App.instance.getSystemSettings().isCashClosePrint() + "");
//                handler.sendMessage(handler.obtainMessage(
//                        MainPage.VIEW_EVENT_CLOSE_BILL, map));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissLoadingDialog();
                        Intent intent = new Intent();
                        intent.putExtra("map", map);
                        setResult(CHECK_RESULT_CODE, intent);
                        KioskHoldActivity.this.finish();
                    }
                });
            }
        }).start();
    }

    class KioskHoldOderAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return orderList.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return orderList.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return arg0;
        }

        @Override
        public View getView(int arg0, View arg1, ViewGroup arg2) {
            HolderView holder = null;
            if (arg1 == null) {
                arg1 = inflater.inflate(R.layout.temp_order_item, null);
                holder = new HolderView();
                holder.tv_order_id = (TextView) arg1.findViewById(R.id.tv_order_id);
                holder.tv_order_status = (TextView) arg1.findViewById(R.id.tv_order_status);
                holder.tv_order_type = (TextView) arg1.findViewById(R.id.tv_order_type);
                holder.tv_place_time = (TextView) arg1.findViewById(R.id.tv_place_time);
                textTypeFace.setTrajanProRegular(holder.tv_order_id);
                textTypeFace.setTrajanProRegular(holder.tv_order_type);
                textTypeFace.setTrajanProRegular(holder.tv_place_time);
                textTypeFace.setTrajanProRegular(holder.tv_order_status);
                arg1.setTag(holder);
            } else {
                holder = (HolderView) arg1.getTag();
            }

            Order order = orderList.get(arg0);
            if(arg0 == selectOrderItem){
                arg1.setBackgroundColor(getResources().getColor(R.color.brownness));
//                btn_check.setTag(order);
//                btn_cancel.setTag(order);
//				if(appOrder.getTableType().intValue() == ParamConst.APP_ORDER_TABLE_STATUS_USED){
//					btn_check.setVisibility(View.VISIBLE);
//				}else{
//					btn_check.setVisibility(View.INVISIBLE);
//				}
                holder.tv_order_id.setTextColor(getResources().getColor(R.color.white));
                holder.tv_order_status.setTextColor(getResources().getColor(R.color.white));
                holder.tv_order_type.setTextColor(getResources().getColor(R.color.white));
                holder.tv_place_time.setTextColor(getResources().getColor(R.color.white));
            }else{
                arg1.setBackgroundColor(getResources().getColor(R.color.white));
                holder.tv_order_id.setTextColor(getResources().getColor(R.color.black));
                holder.tv_order_status.setTextColor(getResources().getColor(R.color.black));
                holder.tv_order_type.setTextColor(getResources().getColor(R.color.black));
                holder.tv_place_time.setTextColor(getResources().getColor(R.color.black));
            }
            holder.tv_order_id.setText(order.getOrderNo() + "");
            String statusStr = "";
            switch (order.getOrderStatus().intValue()) {
                case ParamConst.ORDER_STATUS_HOLD:
                    statusStr = "Hold Order";
                    break;
                case ParamConst.ORDER_STATUS_KIOSK:
                    statusStr = "Kiosk Order";
                    break;
                default:
                    break;
            }
            holder.tv_order_type.setText(statusStr);
            holder.tv_order_status.setText(TimeUtil.getCloseBillDataTime(order.getCreateTime()));
            holder.tv_place_time.setText(App.instance.getLocalRestaurantConfig().getCurrencySymbol() + BH.getBD(order.getTotal()));
            return arg1;
        }
        class HolderView {
            public TextView tv_order_id;
            public TextView tv_order_status;
            public TextView tv_order_type;
            public TextView tv_place_time;
        }
    }

    class KioskHoldOderDetailAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return orderDetails.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return orderDetails.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return arg0;
        }

        @Override
        public View getView(int arg0, View arg1, ViewGroup arg2) {
            HolderView holder = null;
            if (arg1 == null) {
                arg1 = inflater.inflate(R.layout.temp_orderdetail_item, null);
                holder = new HolderView();
                holder.tv_orderdetail_name = (TextView) arg1.findViewById(R.id.tv_orderdetail_name);
                holder.tv_orderdetail_qty = (TextView) arg1.findViewById(R.id.tv_orderdetail_qty);
                holder.tv_temp_modifier = (TextView) arg1.findViewById(R.id.tv_temp_modifier);
                textTypeFace.setTrajanProRegular(holder.tv_orderdetail_name);
                textTypeFace.setTrajanProRegular(holder.tv_orderdetail_qty);
                textTypeFace.setTrajanProRegular(holder.tv_temp_modifier);
                arg1.setTag(holder);
            } else {
                holder = (HolderView) arg1.getTag();
            }
            OrderDetail orderDetail = orderDetails.get(arg0);
            holder.tv_orderdetail_name.setText(orderDetail.getItemName());
            holder.tv_orderdetail_qty.setText(orderDetail.getItemNum() + "");
            List<OrderModifier> orderModifiers = OrderModifierSQL.getOrderModifiersByOrderDetailId(orderDetail.getId().intValue());
            StringBuffer modifierNames = new StringBuffer();
            if(!TextUtils.isEmpty(orderDetail.getReason())) {
                modifierNames.append(orderDetail.getReason());
            }
            for(OrderModifier orderModifier : orderModifiers){
                if(modifierNames.length() != 0){
                    modifierNames.append(",");
                }
                String modifierName = CoreData.getInstance().getModifier(orderModifier.getModifierId().intValue()).getModifierName();
                modifierNames.append(modifierName);
            }
            holder.tv_temp_modifier.setText(modifierNames.toString());
            return arg1;
        }
        class HolderView {
            public TextView tv_orderdetail_name;
            public TextView tv_orderdetail_qty;
            public TextView tv_temp_modifier;
        }
    }

}
