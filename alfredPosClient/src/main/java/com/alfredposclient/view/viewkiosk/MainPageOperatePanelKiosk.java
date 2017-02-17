package com.alfredposclient.view.viewkiosk;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alfredbase.ParamConst;
import com.alfredbase.ParamHelper;
import com.alfredbase.SureDialog;
import com.alfredbase.global.CoreData;
import com.alfredbase.javabean.ItemDetail;
import com.alfredbase.javabean.KotItemDetail;
import com.alfredbase.javabean.KotItemModifier;
import com.alfredbase.javabean.KotSummary;
import com.alfredbase.javabean.Order;
import com.alfredbase.javabean.OrderBill;
import com.alfredbase.javabean.OrderDetail;
import com.alfredbase.javabean.OrderModifier;
import com.alfredbase.javabean.OrderSplit;
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
import com.alfredbase.store.sql.OrderSplitSQL;
import com.alfredbase.store.sql.PaymentSQL;
import com.alfredbase.store.sql.PaymentSettlementSQL;
import com.alfredbase.store.sql.TableInfoSQL;
import com.alfredbase.utils.BH;
import com.alfredbase.utils.ButtonClickTimer;
import com.alfredbase.utils.CommonUtil;
import com.alfredbase.utils.DialogFactory;
import com.alfredbase.utils.ObjectFactory;
import com.alfredbase.utils.OrderHelper;
import com.alfredbase.utils.RoundUtil;
import com.alfredbase.utils.TextTypeFace;
import com.alfredposclient.R;
import com.alfredposclient.activity.MainPage;
import com.alfredposclient.activity.kioskactivity.MainPageKiosk;
import com.alfredposclient.global.App;
import com.alfredposclient.global.UIHelp;
import com.alfredposclient.popupwindow.DiscountWindow.ResultCall;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainPageOperatePanelKiosk extends LinearLayout implements
		OnClickListener {
//	private BaseActivity parent;
	private Handler handler;
	private TextView tv_order_no;
//	private TextView tv_pax;
//	private TextView tv_grand_total;
	private Order order;
	private List<OrderDetail> orderDetails;
	private MainPageKiosk parent;

	private EditText et_bar_code;
	
	public MainPageOperatePanelKiosk(Context context) {
		super(context);
		init(context);
	}

	public MainPageOperatePanelKiosk(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public void setParams(MainPageKiosk parent, Order order,
			List<OrderDetail> orderDetails, Handler handler) {
		this.parent = parent;
		this.handler = handler;
		this.order = order;
		this.orderDetails = orderDetails;
		setData();
	}

	private void init(Context context) {
		
		View.inflate(context, R.layout.operate_panel_kiosk, this);
//		findViewById(R.id.tv_close_bill).setOnClickListener(this);
//		findViewById(R.id.tv_tables).setOnClickListener(this);
		findViewById(R.id.tv_discount).setOnClickListener(this);
		findViewById(R.id.tv_open_item).setOnClickListener(this);
		findViewById(R.id.tv_delete_order).setOnClickListener(this);
//		findViewById(R.id.tv_print_bill).setOnClickListener(this);
//		findViewById(R.id.tv_transfer_table).setOnClickListener(this);
		findViewById(R.id.tv_kick_cashdrawer).setOnClickListener(this);
		findViewById(R.id.tv_take_away).setOnClickListener(this);
		findViewById(R.id.tv_table_name).setOnClickListener(this);
		findViewById(R.id.tv_cash_close).setOnClickListener(this);
//		findViewById(R.id.rl_pax).setOnClickListener(this);
		tv_order_no = (TextView) findViewById(R.id.tv_order_no);
//		tv_pax = (TextView) findViewById(R.id.tv_pax);
		et_bar_code = (EditText) findViewById(R.id.et_bar_code);
		et_bar_code.setFocusable(true);
		et_bar_code.requestFocus();
		et_bar_code.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
				if (KeyEvent.KEYCODE_ENTER == arg1 && arg2.getAction() == KeyEvent.ACTION_DOWN) {
					String barCode = new String(et_bar_code.getText().toString());
					if(TextUtils.isEmpty(barCode)){
						UIHelp.showToast(parent, "Barcode cannot be empty");
						return false;
					}
					ItemDetail itemDetail = CoreData.getInstance().getItemDetailByBarCode(barCode);
					OrderDetail orderDetail = null;
					SureDialog sureDialog = new SureDialog(parent);
					et_bar_code.postDelayed(new Runnable() {

						@Override
						public void run() {
							et_bar_code.setText("");
							et_bar_code.requestFocus();
						}
					}, 500);
					if (itemDetail != null) {
						orderDetail = ObjectFactory.getInstance()
								.getOrderDetail(order, itemDetail, 0);
					}
					if (orderDetail == null) {
						sureDialog.show(false);
						return false;
					}
					Message msg = handler.obtainMessage();
					msg.what = MainPage.VIEW_EVENT_ADD_ORDER_DETAIL;
					msg.obj = orderDetail;
					handler.sendMessage(msg);
					sureDialog.show(true);
					return true;
				}
				return false;
			}
		});
		initTextTypeFace();
	}

	private void initTextTypeFace() {
		TextTypeFace textTypeFace = TextTypeFace.getInstance();
//		textTypeFace.setTrajanProBlod((TextView) findViewById(R.id.tv_close_bill));
//		textTypeFace.setTrajanProBlod((TextView) findViewById(R.id.tv_tables));
		textTypeFace.setTrajanProBlod((TextView) findViewById(R.id.tv_discount));
		textTypeFace.setTrajanProBlod((TextView) findViewById(R.id.tv_open_item));
		textTypeFace.setTrajanProBlod((TextView) findViewById(R.id.tv_delete_order));
		textTypeFace.setTrajanProBlod((TextView) findViewById(R.id.tv_take_away));
		textTypeFace.setTrajanProBlod((TextView) findViewById(R.id.tv_table_name));
//		textTypeFace.setTrajanProBlod((TextView) findViewById(R.id.tv_print_bill));
//		textTypeFace.setTrajanProBlod((TextView) findViewById(R.id.tv_transfer_table));
		textTypeFace.setTrajanProBlod((TextView) findViewById(R.id.tv_kick_cashdrawer));
		textTypeFace.setTrajanProBlod((TextView) findViewById(R.id.tv_order_id));
		textTypeFace.setTrajanProBlod((TextView) findViewById(R.id.tv_order_no));
//		textTypeFace.setTrajanProBlod((TextView) findViewById(R.id.tv_pax_title));
//		textTypeFace.setTrajanProBlod((TextView) findViewById(R.id.tv_pax));
		textTypeFace.setTrajanProBlod((TextView) findViewById(R.id.tv_bill_content));
//		textTypeFace.setTrajanProBlod((TextView) findViewById(R.id.tv_split));
//		textTypeFace.setTrajanProBlod((TextView) findViewById(R.id.tv_table_content));
		textTypeFace.setTrajanProBlod((TextView) findViewById(R.id.tv_misc));
//		textTypeFace.setTrajanProBlod((TextView) findViewById(R.id.tv_edit_kot));
//		textTypeFace.setTrajanProBlod((TextView) findViewById(R.id.tv_take_away));
	}
	
	private void setData() {
		tv_order_no.setText(ParamHelper.getPrintOrderNO(order.getId()));
//		tv_pax.setText(order.getPersons() + "");
	}

	
	@Override
	public void onClick(View v) {
		if (ButtonClickTimer.canClick(v)) {
			switch (v.getId()) {
//			case R.id.tv_close_bill: {
//				Message msg = handler.obtainMessage();
//				msg.what = MainPage.VIEW_EVENT_SHOW_CLOSE_ORDER_WINDOW;
//				handler.sendMessage(msg);
//				break;
//			}
//			case R.id.tv_tables: {
//				Message msg = handler.obtainMessage();
//				msg.what = MainPage.VIEW_EVENT_SHOW_TABLES;
//				handler.sendMessage(msg);
//				break;
//			}
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
			case R.id.tv_delete_order:
				PaymentSettlement paymentSettlement = PaymentSettlementSQL.getPaymentSettlementsByOrderId(order.getId());
				if (paymentSettlement != null) {
					return;
				}
				DialogFactory.commonTwoBtnDialog(parent, parent.getResources().getString(R.string.warning), 
						parent.getResources().getString(R.string.discard_current_order), 
						parent.getResources().getString(R.string.no), 
						parent.getResources().getString(R.string.yes), null, new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						handler.sendEmptyMessage(MainPageKiosk.KIOSK_VIEW_EVENT_DELETE_ORDER);
					}
				});
				
				break;
			case R.id.tv_open_item:
				Message msg = handler.obtainMessage();
				msg.what = MainPage.VIEW_EVENT_SHOW_OPEN_ITEM_WINDOW;
				handler.sendMessage(msg);
				break;
//			case R.id.tv_print_bill:
//				handler.sendEmptyMessage(MainPage.VIEW_EVENT_OPERATEPANEL);
//				break;
			case R.id.tv_transfer_table:
				handler.sendEmptyMessage(MainPage.VIEW_EVENT_TANSFER_TABLE);
				break;
			case R.id.tv_kick_cashdrawer:
				handler.sendEmptyMessage(MainPage.VIEW_EVENT_KICK_CASHDRAWER);
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
//			case R.id.rl_pax:
//				handler.sendMessage(handler.obtainMessage(MainPage.VIEW_EVENT_TANSFER_PAX,(String)tv_pax.getText().toString()));
//				break;
			case R.id.tv_table_name:
				parent.openCustomNoteView();
				break;
			case R.id.tv_cash_close:
				if (!ButtonClickTimer.canClick(v)) {
					return;
				}
				cashPay();
				break;
			default:
				break;
			}

		}
	}


	private void cashPay() {

		if (orderDetails.isEmpty()) {
			UIHelp.showShortToast(parent, parent.getResources().getString(R.string.no_order_detail));
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
		handler.sendMessage(handler
				.obtainMessage(MainPage.VIEW_EVENT_PLACE_ORDER));
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
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("orderId", String.valueOf(paidOrderId));
				map.put("paymentId", String.valueOf(payment.getId().intValue()));
				map.put("isPrint", App.instance.getSystemSettings().isCashClosePrint() + "");
				handler.sendMessage(handler.obtainMessage(
						MainPage.VIEW_EVENT_CLOSE_BILL, map));
				handler.sendEmptyMessage(MainPage.VIEW_EVENT_SHOW_TABLES_AFTER_CLOSE_BILL);
			}
		}).start();
	}
	
	private Map<String, Object> discountShow(){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("order", order);
		map.put("orderDetail", null);
		map.put("resultCall", new ResultCall() {

			@Override
			public void call(final String discount_rate, final String discount_price,final boolean discountByCategory) {
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
							if(discountByCategory){
								order.setDiscountType(ParamConst.ORDER_DISCOUNT_TYPE_RATE_BY_CATEGORY);
							}else{
								order.setDiscountType(ParamConst.ORDER_DISCOUNT_TYPE_RATE_BY_ORDER);
							}

						} else {
							order.setDiscountPrice(discount_price);
//							order.setDiscountRate(BH.div(
//									BH.getBD(order.getDiscountAmount()),
//									BH.getBD(order.getSubTotal()), true)
//									.toString());
							if(discountByCategory){
								order.setDiscountType(ParamConst.ORDER_DISCOUNT_TYPE_SUB_BY_CATEGORY);
							}else{
								order.setDiscountType(ParamConst.ORDER_DISCOUNT_TYPE_SUB_BY_ORDER);
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
