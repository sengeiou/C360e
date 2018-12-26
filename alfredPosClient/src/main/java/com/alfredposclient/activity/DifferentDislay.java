package com.alfredposclient.activity;

import android.app.Presentation;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alfredbase.ParamConst;
import com.alfredbase.javabean.Order;
import com.alfredbase.javabean.OrderDetail;
import com.alfredbase.javabean.OrderSplit;
import com.alfredbase.store.sql.OrderSplitSQL;
import com.alfredbase.store.sql.TableInfoSQL;
import com.alfredbase.utils.BH;
import com.alfredbase.utils.TextTypeFace;
import com.alfredposclient.R;
import com.alfredposclient.adapter.DifferentAdapter;
import com.alfredposclient.global.App;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

public class DifferentDislay extends Presentation {

    private static List<OrderDetail> orderDetails;
    private static Order order;

    private TextView tv_diff;
    private RecyclerView recyclerView;
    LinearLayoutManager mLinearLayoutManager;
    private static TextTypeFace textTypeFace;
    private static TextView tv_item_count;
    private static TextView tv_sub_total;
    private static TextView tv_discount;
    private static TextView tv_taxes;
    private static TextView tv_grand_total,tv_table_name_ontop,tv_different;
    private Button btn_place_order;

  //  private List<OrderDetail> orderDetails = Collections.emptyList();

    private static DifferentAdapter  differentAdapter;
    private static LinearLayout ll_order_list;
    private LinearLayout ll_foot_total;
    public DifferentDislay(Context outerContext, Display display) {
        super(outerContext, display);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_different);
        init();
        initTextTypeFace();
    }


    private void initTextTypeFace() {
        textTypeFace = TextTypeFace.getInstance();

        textTypeFace
                .setTrajanProRegular((TextView) findViewById(R.id.tv_name_title));
        textTypeFace
                .setTrajanProRegular((TextView) findViewById(R.id.tv_price_title));
        textTypeFace
                .setTrajanProRegular((TextView) findViewById(R.id.tv_qry_title));
        textTypeFace
                .setTrajanProRegular((TextView) findViewById(R.id.tv_sutotal_title));
        textTypeFace
                .setTrajanProRegular((TextView) findViewById(R.id.tv_discount_title));
        textTypeFace
                .setTrajanProRegular((TextView) findViewById(R.id.tv_total_title));
        textTypeFace
                .setTrajanProRegular((TextView) findViewById(R.id.tv_grand_total));
        textTypeFace
                .setTrajanProRegular((TextView) findViewById(R.id.tv_item_title));
        textTypeFace
                .setTrajanProRegular((TextView) findViewById(R.id.tv_sub_total_title));
        textTypeFace
                .setTrajanProRegular((TextView) findViewById(R.id.tv_discount_totle_title));
        textTypeFace
                .setTrajanProRegular((TextView) findViewById(R.id.tv_taxes_title));
        textTypeFace.setTrajanProRegular(tv_item_count);
        textTypeFace.setTrajanProRegular(tv_sub_total);
        textTypeFace.setTrajanProRegular(tv_discount);
        textTypeFace.setTrajanProRegular(tv_taxes);
    }
    private void init() {
        tv_diff=(TextView)findViewById(R.id.tv_different);
        tv_table_name_ontop = (TextView) findViewById(R.id.tv_table_name_ontop);
        tv_item_count = (TextView) findViewById(R.id.tv_item_count);
        tv_sub_total = (TextView) findViewById(R.id.tv_sub_total);
        tv_discount = (TextView) findViewById(R.id.tv_discount);
        tv_taxes = (TextView) findViewById(R.id.tv_taxes);
        ll_order_list=(LinearLayout)findViewById(R.id.ll_order_list);
        ll_foot_total=(LinearLayout)findViewById(R.id.ll_foot_total);
        recyclerView=(RecyclerView)findViewById(R.id.recyc_different);
        tv_different=(TextView)findViewById(R.id.tv_different);
        mLinearLayoutManager = new LinearLayoutManager(App.instance);
        mLinearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        recyclerView.addItemDecoration(new DividerItemDecoration(App.instance, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(mLinearLayoutManager);
        differentAdapter=new DifferentAdapter(orderDetails);
        recyclerView.setAdapter(differentAdapter);

    }


    public static void setVisibility(Boolean  v){
       if(v){
           ll_order_list.setVisibility(View.VISIBLE);
           ll_order_list.setVisibility(View.VISIBLE);
           tv_different.setVisibility(View.GONE);
       }else {
           ll_order_list.setVisibility(View.GONE);
           ll_order_list.setVisibility(View.GONE);
           tv_different.setVisibility(View.VISIBLE);
       }
    }


    public static void setParam(List<OrderDetail> orderDetail,Order order){
        orderDetails=orderDetail;
        differentAdapter.notifyDataSetChanged();
        order=order;
        refresh();
    }


    private static void refresh() {

        int itemCount = 0;
        if (!orderDetails.isEmpty()) {
            for (OrderDetail orderDetail : orderDetails) {
                itemCount += orderDetail.getItemNum();
            }
        } else {
            // bob add:
            // if there is no order detail, complete all KOT if the order has
            // KotSummary kotSummary =
            // KotSummarySQL.getKotSummary(order.getId());
            // if(kotSummary != null){
            // kotSummary.setStatus(ParamConst.KOTS_STATUS_DONE);
            // KotSummarySQL.update(kotSummary);
            // }
        }
        String orderNoStr = App.instance.getResources().getString(R.string.table)
                + " - "
                + TableInfoSQL.getTableById(order.getTableId())
                .getName();
        if(order.getIsTakeAway() == ParamConst.TAKE_AWAY){
            orderNoStr = orderNoStr + "("+ App.instance.getResources().getString(R.string.take_away) + ")";
        }
        tv_table_name_ontop.setText(orderNoStr);
        tv_item_count.setText("" + itemCount);
        List<OrderSplit> orderSplits = OrderSplitSQL.getFinishedOrderSplits(order.getId().intValue());
        if(orderSplits != null && orderSplits.size() > 0){
            BigDecimal subtotal = BH.getBD(ParamConst.DOUBLE_ZERO);
            BigDecimal taxAmount = BH.getBD(ParamConst.DOUBLE_ZERO);
            BigDecimal discountAmount = BH.getBD(ParamConst.DOUBLE_ZERO);
            BigDecimal total = BH.getBD(ParamConst.DOUBLE_ZERO);
            for(OrderSplit orderSplit : orderSplits){
                subtotal = BH.add(subtotal, BH.getBD(orderSplit.getSubTotal()), false);
                taxAmount = BH.add(taxAmount, BH.getBD(orderSplit.getTaxAmount()), false);
                discountAmount = BH.add(discountAmount, BH.getBD(orderSplit.getDiscountAmount()), false);
                total = BH.add(total, BH.getBD(orderSplit.getTotal()), false);
            }
            subtotal = BH.sub(BH.getBD(order.getSubTotal()), subtotal, true);
            taxAmount = BH.sub(BH.getBD(order.getTaxAmount()), taxAmount, true);
            discountAmount = BH.sub(BH.getBD(order.getDiscountAmount()), discountAmount, true);
            total = BH.sub(BH.getBD(order.getTotal()), total, true);
            tv_sub_total.setText(App.instance.getLocalRestaurantConfig().getCurrencySymbol() + subtotal.toString());
            tv_discount.setText("-" + App.instance.getLocalRestaurantConfig().getCurrencySymbol() + discountAmount.toString());
            tv_taxes.setText(App.instance.getLocalRestaurantConfig().getCurrencySymbol() + taxAmount.toString());
            tv_grand_total.setText(App.instance.getResources().getString(R.string.grand_total) + ": " +
                    App.instance.getLocalRestaurantConfig().getCurrencySymbol() + total.toString());

        }else {
            tv_sub_total.setText(App.instance.getLocalRestaurantConfig().getCurrencySymbol() + BH.getBD(order.getSubTotal()).toString());
            tv_discount.setText("-" + App.instance.getLocalRestaurantConfig().getCurrencySymbol() + BH.getBD(order.getDiscountAmount()).toString());
            tv_taxes.setText(App.instance.getLocalRestaurantConfig().getCurrencySymbol() + BH.getBD(order.getTaxAmount()).toString());
            tv_grand_total.setText(App.instance.getResources().getString(R.string.grand_total) + ": " +
                    App.instance.getLocalRestaurantConfig().getCurrencySymbol() + BH.getBD(order.getTotal()).toString());
        }
    }
}
