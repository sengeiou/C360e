package com.alfredposclient.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alfredbase.ParamConst;
import com.alfredbase.ParamHelper;
import com.alfredbase.javabean.Order;
import com.alfredbase.javabean.OrderDetail;
import com.alfredbase.javabean.OrderSplit;
import com.alfredbase.store.sql.OrderDetailSQL;
import com.alfredbase.store.sql.OrderSQL;
import com.alfredbase.store.sql.OrderSplitSQL;
import com.alfredbase.utils.ButtonClickTimer;
import com.alfredbase.utils.CommonUtil;
import com.alfredbase.utils.DialogFactory;
import com.alfredbase.utils.IntegerUtils;
import com.alfredbase.utils.TextTypeFace;
import com.alfredposclient.R;
import com.alfredposclient.activity.MainPage;
import com.alfredposclient.global.UIHelp;
import com.alfredposclient.popupwindow.DiscountWindow.ResultCall;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainPageOperatePanel extends LinearLayout implements
		OnClickListener {
//	private BaseActivity parent;
	private Handler handler;
	private TextView tv_order_no;
	private TextView tv_pax;
	private TextView tv_grand_total;
	private Order order;
	private List<OrderDetail> orderDetails;
	private MainPage parent;
//	private TableInfo tables;
	
	public MainPageOperatePanel(Context context) {
		super(context);
		init(context);
	}

	public MainPageOperatePanel(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public void setParams(MainPage parent, Order order,
						  List<OrderDetail> orderDetails, Handler handler) {
		this.parent = parent;
//		this.tables = tables;
		this.handler = handler;
		this.order = order;
		this.orderDetails = orderDetails;
		setData();
	}

	private void init(Context context) {
		
		View.inflate(context, R.layout.operate_panel, this);
		findViewById(R.id.tv_close_bill).setOnClickListener(this);
		findViewById(R.id.tv_tables).setOnClickListener(this);
		findViewById(R.id.tv_discount).setOnClickListener(this);
		findViewById(R.id.tv_unseat).setOnClickListener(this);
		findViewById(R.id.tv_open_item).setOnClickListener(this);
		findViewById(R.id.tv_print_bill).setOnClickListener(this);
		findViewById(R.id.tv_transfer_table).setOnClickListener(this);
		findViewById(R.id.tv_kick_cashdrawer).setOnClickListener(this);
		findViewById(R.id.rl_pax).setOnClickListener(this);
		findViewById(R.id.tv_take_away).setOnClickListener(this);
		findViewById(R.id.tv_fire).setOnClickListener(this);
		findViewById(R.id.tv_split_by_pax).setOnClickListener(this);
		tv_order_no = (TextView) findViewById(R.id.tv_order_no);
		tv_pax = (TextView) findViewById(R.id.tv_pax);
		
		initTextTypeFace();
	}

	private void initTextTypeFace() {
		TextTypeFace textTypeFace = TextTypeFace.getInstance();
		textTypeFace.setTrajanProBlod((TextView) findViewById(R.id.tv_close_bill));
		textTypeFace.setTrajanProBlod((TextView) findViewById(R.id.tv_tables));
		textTypeFace.setTrajanProBlod((TextView) findViewById(R.id.tv_discount));
		textTypeFace.setTrajanProBlod((TextView) findViewById(R.id.tv_unseat));
		textTypeFace.setTrajanProBlod((TextView) findViewById(R.id.tv_open_item));
		textTypeFace.setTrajanProBlod((TextView) findViewById(R.id.tv_print_bill));
		textTypeFace.setTrajanProBlod((TextView) findViewById(R.id.tv_transfer_table));
		textTypeFace.setTrajanProBlod((TextView) findViewById(R.id.tv_kick_cashdrawer));
		textTypeFace.setTrajanProBlod((TextView) findViewById(R.id.tv_order_id));
		textTypeFace.setTrajanProBlod((TextView) findViewById(R.id.tv_order_no));
		textTypeFace.setTrajanProBlod((TextView) findViewById(R.id.tv_pax_title));
		textTypeFace.setTrajanProBlod((TextView) findViewById(R.id.tv_pax));
		textTypeFace.setTrajanProBlod((TextView) findViewById(R.id.tv_bill_content));
//		textTypeFace.setTrajanProBlod((TextView) findViewById(R.id.tv_split));
		textTypeFace.setTrajanProBlod((TextView) findViewById(R.id.tv_table_content));
		textTypeFace.setTrajanProBlod((TextView) findViewById(R.id.tv_misc));
//		textTypeFace.setTrajanProBlod((TextView) findViewById(R.id.tv_edit_kot));
		textTypeFace.setTrajanProBlod((TextView) findViewById(R.id.tv_take_away));
		textTypeFace.setTrajanProBlod((TextView) findViewById(R.id.tv_fire));
		textTypeFace.setTrajanProBlod((TextView) findViewById(R.id.tv_split_by_pax));
	}
	
	private void setData() {
		tv_order_no.setText(ParamHelper.getPrintOrderNO(order.getOrderNo()));
		tv_pax.setText(order.getPersons() + "");
	}

	
	@Override
	public void onClick(View v) {
		if (ButtonClickTimer.canClick(v)) {
			switch (v.getId()) {
			case R.id.tv_close_bill: {
				Message msg = handler.obtainMessage();
				msg.what = MainPage.VIEW_EVENT_SHOW_CLOSE_ORDER_WINDOW;
				handler.sendMessage(msg);
				break;
			}
			case R.id.tv_tables: {
				Message msg = handler.obtainMessage();
				msg.what = MainPage.VIEW_EVENT_SHOW_TABLES;
				handler.sendMessage(msg);
				break;
			}
			case R.id.tv_discount: {
				if (orderDetails.isEmpty()) {
					UIHelp.showToast(parent, parent.getResources().getString(R.string.order_first));
					return;
				}
				boolean canDiscount = true;
				for(OrderDetail orderDetail : orderDetails){
					if(orderDetail.getDiscountType().intValue() != ParamConst.ORDERDETAIL_DISCOUNT_TYPE_RATE 
							&& orderDetail.getDiscountType().intValue() != ParamConst.ORDERDETAIL_DISCOUNT_TYPE_SUB
							&& orderDetail.getIsItemDiscount() == ParamConst.ITEM_DISCOUNT
							&& orderDetail.getIsFree() == ParamConst.NOT_FREE){
						canDiscount = true;
						break;
					}else{
						canDiscount = false;
					}
				}
				if(canDiscount)
					handler.sendMessage(handler.obtainMessage(MainPage.VIEW_EVENT_SHOW_DISCOUNT_WINDOW,discountShow()));
				else{
					UIHelp.showToast(parent, parent.getResources().getString(R.string.order_first));
					return;
				}
				break;
			}
			case R.id.tv_unseat:
//				PaymentSettlement paymentSettlement = PaymentSettlementSQL.getPaymentSettlementsByOrderId(order.getId());
//				if (paymentSettlement != null) {
//					return;
//				}

				int placeOrderCount = OrderDetailSQL.getOrderDetailPlaceOrderCountByOrder(order);
				if(placeOrderCount > 0) {
					DialogFactory.showOneButtonCompelDialog(parent,"", parent.getResources().getString(R.string.cannot_unseat), null);
				}else{
					DialogFactory.commonTwoBtnDialog(parent, parent.getResources().getString(R.string.warning),
							parent.getResources().getString(R.string.unseat_table),
							parent.getResources().getString(R.string.no),
							parent.getResources().getString(R.string.yes), null, new OnClickListener() {

								@Override
								public void onClick(View arg0) {
									handler.sendEmptyMessage(MainPage.VIEW_EVENT_UNSEAT_ORDER);
								}
							});
				}
				break;
			case R.id.tv_open_item:
				Message msg = handler.obtainMessage();
				msg.what = MainPage.VIEW_EVENT_SHOW_OPEN_ITEM_WINDOW;
				handler.sendMessage(msg);
				break;
			case R.id.tv_print_bill:
				handler.sendEmptyMessage(MainPage.VIEW_EVENT_OPERATEPANEL);
				break;
			case R.id.tv_transfer_table:
				if(!IntegerUtils.isEmptyOrZero(order.getAppOrderId())){
					UIHelp.showShortToast(parent, "Orders from Diner App Cannot be Transferred");
					return;
				}
				handler.sendEmptyMessage(MainPage.VIEW_EVENT_TANSFER_TABLE);
				break;
			case R.id.tv_kick_cashdrawer:
				handler.sendEmptyMessage(MainPage.VIEW_EVENT_KICK_CASHDRAWER);
				break;	
			case R.id.rl_pax:
				handler.sendMessage(handler.obtainMessage(MainPage.VIEW_EVENT_TANSFER_PAX,tv_pax.getText().toString()));
				break;
			case R.id.tv_take_away:
				if(order.getIsTakeAway().intValue() == ParamConst.TAKE_AWAY){
					order.setIsTakeAway(ParamConst.NOT_TAKE_AWAY);
				}else{
					order.setIsTakeAway(ParamConst.TAKE_AWAY);
				}
				OrderSQL.updateOrder(order);
				handler.sendEmptyMessage(MainPage.VIEW_EVENT_SET_DATA);
				break;
			case R.id.tv_fire:{
				handler.sendEmptyMessage(MainPage.VIEW_EVENT_FIRE);
			}
				break;
			case R.id.tv_split_by_pax:{
				handler.sendEmptyMessage(MainPage.VIEW_EVENT_SPLIT_BY_PAX);
			}
				break;
			default:
				break;
			}

		}
	}
	
	private Map<String, Object> discountShow(){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("order", order);
		map.put("orderDetail", null);
		map.put("resultCall", new ResultCall() {

			@Override
			public void call(final String discount_rate, final String discount_price, final String discountByCategory) {
//				parent.loadingDialog.show();
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						Message msg = handler.obtainMessage();
						msg.what = MainPage.VIEW_EVENT_SET_DATA;
						if (CommonUtil.isNull(discount_rate)
								&& CommonUtil.isNull(discount_price)) {
							handler.sendMessage(msg);
							return;
						}

						if (!CommonUtil.isNull(discount_rate)) {
							order.setDiscountRate(discount_rate);
//							order.setDiscountPrice(BH.mul(
//									BH.getBD(order.getSubTotal()),
//									BH.getBDNoFormat(order.getDiscountRate()), true)
//									.toString());
							if(!TextUtils.isEmpty(discountByCategory)){
								order.setDiscountType(ParamConst.ORDER_DISCOUNT_TYPE_RATE_BY_CATEGORY);
								order.setDiscountCategoryId(discountByCategory);
							}else{
								order.setDiscountType(ParamConst.ORDER_DISCOUNT_TYPE_RATE_BY_ORDER);
								order.setDiscountCategoryId("");
							}
						} else {
							order.setDiscountPrice(discount_price);
//							order.setDiscountRate(BH.div(
//									BH.getBD(order.getDiscountAmount()),
//									BH.getBD(order.getSubTotal()), true)
//									.toString());
							if(!TextUtils.isEmpty(discountByCategory)){
								order.setDiscountType(ParamConst.ORDER_DISCOUNT_TYPE_SUB_BY_CATEGORY);
								order.setDiscountCategoryId(discountByCategory);
							}else{
								order.setDiscountType(ParamConst.ORDER_DISCOUNT_TYPE_SUB_BY_ORDER);
								order.setDiscountCategoryId("");
							}
						}
						OrderSQL.updateOrderAndOrderDetailByDiscount(order);
						List<OrderSplit> orderSplits = OrderSplitSQL.getOrderSplits(order);
						if(!orderSplits.isEmpty()){
							for(OrderSplit orderSplit : orderSplits){
								OrderSplitSQL.updateOrderSplitByOrder(order, orderSplit);
							}
						}
						handler.sendMessage(msg);
					}
				}).start();
				
			}
		});
		return map;
	}
}
