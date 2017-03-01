package com.alfredwaiter.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alfredbase.BaseActivity;
import com.alfredbase.BaseApplication;
import com.alfredbase.LoadingDialog;
import com.alfredbase.ParamConst;
import com.alfredbase.global.CoreData;
import com.alfredbase.http.ResultCode;
import com.alfredbase.javabean.ItemDetail;
import com.alfredbase.javabean.Order;
import com.alfredbase.javabean.OrderDetail;
import com.alfredbase.javabean.OrderModifier;
import com.alfredbase.javabean.Printer;
import com.alfredbase.store.sql.OrderDetailSQL;
import com.alfredbase.store.sql.OrderModifierSQL;
import com.alfredbase.store.sql.OrderSQL;
import com.alfredbase.store.sql.TableInfoSQL;
import com.alfredbase.utils.BH;
import com.alfredbase.utils.DialogFactory;
import com.alfredbase.utils.VibrationUtil;
import com.alfredwaiter.R;
import com.alfredwaiter.global.App;
import com.alfredwaiter.global.SyncCentre;
import com.alfredwaiter.global.UIHelp;
import com.alfredwaiter.popupwindow.SelectGroupWindow;
import com.alfredwaiter.utils.WaiterUtils;
import com.alfredwaiter.view.MoneyKeyboard;
import com.alfredwaiter.view.MoneyKeyboard.KeyBoardClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderDetailsTotal extends BaseActivity implements KeyBoardClickListener{
	public static final int VIEW_EVENT_SELECT_GROUP = 0;
	public static final int PLACE_ORDER_RESULT_CODE = 1;
	public static final int VIEW_EVENT_GET_BILL = 2;
	public static final int VIEW_EVENT_GET_BILL_FAILED = 3;
	public static final int VIEW_EVENT_PRINT_BILL = 4;
	public static final int VIEW_EVENT_PRINT_BILL_FAILED = 5;
	public static final int VIEW_EVENT_GET_PRINT_LIST = 6;
	public static final int VIEW_EVENT_GET_PRINT_LIST_FAILED = 7;

	private static final int DURATION_1 = 300;
	private ListView lv_dishes;
	private Order currentOrder;
	private List<OrderDetail> orderDetails = new ArrayList<OrderDetail>();
	private RelativeLayout rl_content;
	private RelativeLayout rl_countKeyboard;
	private TextView tv_group;
	private TextView tv_place_order;
	private Button btn_get_bill;
	private Button btn_print_bill;
	private SelectGroupWindow selectGroupWindow;
	private int groupId = -1;
	private OrderDetailListAdapter adapter;
	private TextView tv_item_count;
	private TextView tv_sub_total;
	private TextView tv_discount;
	private TextView tv_taxes;
	private ImageView iv_add;
	private List<OrderDetail> newOrderDetails = new ArrayList<OrderDetail>();
	private List<OrderDetail> oldOrderDetails = new ArrayList<OrderDetail>();
	private StringBuffer buffer = new StringBuffer();
	private DismissCall dismissCall;
	private LinearLayout ll_bill_action;
	@Override
	protected void initView() {
		super.initView();
		setContentView(R.layout.activity_order_detail_total);
		getIntentData();
		lv_dishes = (ListView) findViewById(R.id.lv_dishes);
		tv_place_order = (TextView) findViewById(R.id.tv_place_order);
		btn_get_bill = (Button) findViewById(R.id.btn_get_bill);
		btn_print_bill = (Button) findViewById(R.id.btn_print_bill);
		loadingDialog = new LoadingDialog(context);
		adapter = new OrderDetailListAdapter(context);
		lv_dishes.setAdapter(adapter);
		lv_dishes.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
			}
		});

		tv_item_count = (TextView) findViewById(R.id.tv_item_count);
		tv_sub_total = (TextView) findViewById(R.id.tv_sub_total);
		tv_discount = (TextView) findViewById(R.id.tv_discount);
		tv_taxes = (TextView) findViewById(R.id.tv_taxes);
		iv_add = (ImageView) findViewById(R.id.iv_add);
		iv_add.setOnClickListener(this);
		tv_group = (TextView) findViewById(R.id.tv_group);
		tv_group.setOnClickListener(this);
		tv_place_order.setOnClickListener(this);
		btn_get_bill.setOnClickListener(this);
		btn_print_bill.setOnClickListener(this);
		ll_bill_action = (LinearLayout) findViewById(R.id.ll_bill_action);
		selectGroupWindow = new SelectGroupWindow(context, tv_group, handler);
		((TextView) findViewById(R.id.tv_tables_name)).setText(TableInfoSQL.getTableById(currentOrder.getTableId())
				.getName());
		rl_content = (RelativeLayout) findViewById(R.id.rl_content);
		rl_countKeyboard = (RelativeLayout) findViewById(R.id.rl_countKeyboard);
		rl_countKeyboard.setVisibility(View.GONE);
		MoneyKeyboard moneyKeyboard = (MoneyKeyboard)findViewById(R.id.countKeyboard);
		moneyKeyboard.setMoneyPanel(View.GONE);
		moneyKeyboard.setMoneyRoot(Color.BLACK);
		moneyKeyboard.setKeyBoardClickListener(this);
		refreshOrderTotal();
		refreshOrder();
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case VIEW_EVENT_SELECT_GROUP:
				groupId = (Integer) msg.obj;
				if(groupId < 0){
					tv_group.setText("Group:All");
				}else if(groupId == 0){
					tv_group.setText("Group:?");
				}else{
					tv_group.setText("Group:" + groupId);
				}
				
				refreshList();
				break;
			case ResultCode.SUCCESS:
				loadingDialog.dismiss();
				if (currentOrder != null) {
					currentOrder
							.setOrderStatus(ParamConst.ORDER_STATUS_OPEN_IN_POS);
					OrderSQL.update(currentOrder);
				}
				// if(orderDetails != null && !orderDetails.isEmpty()){
				// for(int i = 0; i < orderDetails.size(); i++){
				// orderDetails.get(i).setOrderDetailStatus(ParamConst.ORDERDETAIL_STATUS_KOTPRINTERD);
				// }
				// OrderDetailSQL.addOrderDetailList(orderDetails);
				// }
				orderDetails = OrderDetailSQL.getOrderDetails(currentOrder.getId());
				
				refreshOrder();
				refreshList();
				UIHelp.showToast(context, context.getResources().getString(R.string.place_succ));

				break;
			case ResultCode.CONNECTION_FAILED:
				loadingDialog.dismiss();
				UIHelp.showToast(context, context.getResources().getString(R.string.place_failed));
				break;
			case VIEW_EVENT_GET_BILL:
				loadingDialog.dismiss();
				UIHelp.startOrderReceiptDetails(context, currentOrder);
				break;
			case VIEW_EVENT_GET_BILL_FAILED:
				loadingDialog.dismiss();
				UIHelp.showToast(context, context.getResources().getString(R.string.network_errors));
				break;
			case MainPage.TRANSFER_TABLE_NOTIFICATION:
				WaiterUtils.showTransferTableDialog(context);
				break;
			case ResultCode.ORDER_FINISHED:
				loadingDialog.dismiss();
				DialogFactory.showOneButtonCompelDialog(context, 
						context.getResources().getString(R.string.warn), 
						context.getResources().getString(R.string.order_closed), null);
				break;
			case ResultCode.NONEXISTENT_ORDER:
				loadingDialog.dismiss();
				DialogFactory.showOneButtonCompelDialog(context, 
						context.getResources().getString(R.string.warn), 
						context.getResources().getString(R.string.order_not_edited), null);
				break;
			case ResultCode.ORDER_SPLIT_IS_SETTLED:
				loadingDialog.dismiss();
				int groupId = (Integer) msg.obj;
				if (groupId == 0) {
					DialogFactory.showOneButtonCompelDialog(context, 
							context.getResources().getString(R.string.warn), 
							context.getResources().getString(R.string.order_split_settled), null);
				} else {
					DialogFactory.showOneButtonCompelDialog(context, 
							context.getResources().getString(R.string.warn), 
							context.getResources().getString(R.string.order_split) + groupId + 
							context.getResources().getString(R.string.settled), null);
				}
				
				break;
			case VIEW_EVENT_PRINT_BILL:
				UIHelp.showToast(context, context.getResources().getString(R.string.print_bill_succ));
				break;
			case VIEW_EVENT_PRINT_BILL_FAILED:
				UIHelp.showToast(context, context.getResources().getString(R.string.print_bill_failed));
				break;
			default:
				break;
			}
		};
	};

	private void refreshList() {
		orderDetails.clear();
		newOrderDetails.clear();
		oldOrderDetails.clear();
		if (groupId < 0) {
			newOrderDetails = OrderDetailSQL
					.getOrderDetailByOrderIdAndOrderDetailStatus(
							currentOrder.getId(),
							ParamConst.ORDERDETAIL_STATUS_WAITER_CREATE);
			oldOrderDetails = OrderDetailSQL
					.getOrderDetailByOrderIdAndOrderDetailStatus(currentOrder
							.getId());
		} else {
			newOrderDetails = OrderDetailSQL.getOrderDetails(currentOrder,
					groupId, ParamConst.ORDERDETAIL_STATUS_WAITER_CREATE);
			oldOrderDetails = OrderDetailSQL.getOrderDetailsUnZero(
					currentOrder, groupId);
		}
		orderDetails.addAll(newOrderDetails);
		orderDetails.addAll(oldOrderDetails);
		adapter.notifyDataSetChanged();
		refreshOrderTotal();
	}

	private void refreshOrderTotal() {
		List<OrderDetail> orderDetailList = OrderDetailSQL
				.getOrderDetails(currentOrder.getId());
		int itemCount = 0;
		if (!orderDetailList.isEmpty()) {
			for (OrderDetail orderDetail : orderDetailList) {
				itemCount += orderDetail.getItemNum();
			}
		}
		tv_item_count.setText(context.getResources().getString(R.string.item_count) + itemCount);
		tv_sub_total.setText(context.getResources().getString(R.string.sub_total)+ App.instance.getCurrencySymbol()
				+ BH.getBD(currentOrder.getSubTotal()));
		tv_discount.setText(context.getResources().getString(R.string.discount_) +  App.instance.getCurrencySymbol()
				+ BH.getBD(currentOrder.getDiscountAmount()));
		tv_taxes.setText(context.getResources().getString(R.string.taxes) + App.instance.getCurrencySymbol() + BH.getBD(currentOrder.getTaxAmount()));
	}

	private void getIntentData() {
		Intent intent = getIntent();
		currentOrder = (Order) intent.getExtras().get("order");
		currentOrder = OrderSQL.getOrder(currentOrder.getId());
		newOrderDetails = OrderDetailSQL
				.getOrderDetailByOrderIdAndOrderDetailStatus(
						currentOrder.getId(),
						ParamConst.ORDERDETAIL_STATUS_WAITER_CREATE);
		oldOrderDetails = OrderDetailSQL
				.getOrderDetailByOrderIdAndOrderDetailStatus(currentOrder
						.getId());
		// orderDetails = OrderDetailSQL.getOrderDetails(currentOrder);
		orderDetails.addAll(newOrderDetails);
		orderDetails.addAll(oldOrderDetails);
	}

	@Override
	public void handlerClickEvent(View v) {
		super.handlerClickEvent(v);
		switch (v.getId()) {
		case R.id.tv_group: {
			int maxGroupId = OrderDetailSQL.getMaxGroupId(currentOrder);
			selectGroupWindow
					.show(currentOrder.getPersons() > maxGroupId ? currentOrder
							.getPersons() : maxGroupId);
			break;
		}
		case R.id.tv_place_order:
			// VibrationUtil.init(context);
			// VibrationUtil.playVibratorTwice();
			commitOrderToPOS();
			break;
		case R.id.btn_get_bill: {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters
					.put("table",
							TableInfoSQL.getTableById(
									currentOrder.getTableId()));
			SyncCentre.getInstance().getBillPrint(context, parameters, handler);
		}
			break;
		case R.id.btn_print_bill: {
			DialogFactory.commonTwoBtnDialog(context, "Waring", "Use the default Cashier Printer ?", "Other", "OK",
					null,
					new OnClickListener() {
						@Override
						public void onClick(View v) {
							printBill(null);
						}
			});

		}
			break;
		case R.id.iv_add:
			OpenMainPage();
			break;
		default:
			break;
		}
	}

	private void printBill(Printer printer){
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("orderId",currentOrder.getId().intValue());
		parameters.put("tableName",TableInfoSQL.getTableById(
				currentOrder.getTableId()).getName());
		SyncCentre.getInstance().printBill(context, parameters, handler);
	}
	@Override
	public void onBackPressed() {
		//OpenMainPage();
		//this.
	    super.onBackPressed();
	}
	private void OpenMainPage() {
//		UIHelp.startMainPage(context, currentOrder);
		if (BaseApplication.instance.getIndexOfActivity(MainPage.class) != -1) {
			this.finish();
		}else {
			UIHelp.startMainPage(context, currentOrder);
			this.finish();
		}
		
	}

	private void updateOrderDetail(OrderDetail orderDetail, int count) {
		if (count <= 0) {// 删除
			OrderDetailSQL.deleteOrderDetail(orderDetail);
			OrderModifierSQL.deleteOrderModifierByOrderDetail(orderDetail);
		} else {// 添加
			orderDetail.setItemNum(count);
			OrderDetailSQL.updateOrderDetailAndOrderForWaiter(orderDetail);
		}
		currentOrder = OrderSQL.getOrder(orderDetail.getOrderId());
	}

	private void commitOrderToPOS() {
		if (newOrderDetails == null || newOrderDetails.isEmpty()) {
			return;
		}
		Map<String, Object> parameters = new HashMap<String, Object>();
		List<OrderDetail> orderDetails = new ArrayList<OrderDetail>();
		List<OrderModifier> orderModifiers = OrderModifierSQL
				.getAllOrderModifier(currentOrder);
		for (OrderDetail orderDetail : newOrderDetails) {
			if (orderDetail.getIsFree() != ParamConst.FREE) {
				orderDetails.add(orderDetail);
			}
		}
		parameters.put("order", currentOrder);
		parameters.put("orderDetails", orderDetails);
		parameters.put("orderModifiers", orderModifiers);
		loadingDialog.setTitle(context.getResources().getString(R.string.loading));
		loadingDialog.show();
		SyncCentre.getInstance().commitOrderAndOrderDetails(context,
				parameters, handler);
	}

	private void refreshOrder() {
		if (currentOrder.getOrderStatus() == ParamConst.ORDER_STATUS_OPEN_IN_POS) {
			tv_place_order.setVisibility(View.GONE);
			ll_bill_action.setVisibility(View.VISIBLE);
		} else {
			tv_place_order.setVisibility(View.VISIBLE);
			ll_bill_action.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		Order mOrder = OrderSQL.getOrder(currentOrder.getId());
		if (mOrder == null) {
			WaiterUtils.showTransferTableDialog(context);
		}
		// refreshList();
		// refreshOrder();
	}

	public void httpRequestAction(int action, Object obj) {

		if (MainPage.TRANSFER_TABLE_NOTIFICATION == action) {
			Order mOrder = (Order) obj;
			if (mOrder.getId().intValue() == currentOrder.getId().intValue()) {
				handler.sendEmptyMessage(MainPage.TRANSFER_TABLE_NOTIFICATION);
			}
		}

	};

	@Override
	protected void onDestroy() {
		VibrationUtil.cancel();
		super.onDestroy();
	}

	class OrderDetailListAdapter extends BaseAdapter {
		private LayoutInflater inflater;

		public OrderDetailListAdapter() {
		}

		public OrderDetailListAdapter(Context context) {
			inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return orderDetails.size();
		}

		@Override
		public Object getItem(int arg0) {
			return orderDetails.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(final int position, View arg1, ViewGroup arg2) {
			ViewHolder holder = null;
			if (arg1 == null) {
				arg1 = inflater.inflate(R.layout.item_order_detail, null);
				holder = new ViewHolder();
				holder.ll_title = (LinearLayout) arg1
						.findViewById(R.id.ll_title);
				holder.name = (TextView) arg1.findViewById(R.id.name);
				holder.price = (TextView) arg1.findViewById(R.id.price);
				holder.tv_qty = (TextView) arg1.findViewById(R.id.tv_qty);
				holder.subtotal = (TextView) arg1.findViewById(R.id.subtotal);
				arg1.setTag(holder);
			} else {
				holder = (ViewHolder) arg1.getTag();
			}

			final OrderDetail orderDetail = orderDetails.get(position);
			ItemDetail itemDetail = CoreData.getInstance().getItemDetailById(
					orderDetail.getItemId());
			if (orderDetail.getOrderDetailStatus() == ParamConst.ORDERDETAIL_STATUS_WAITER_CREATE) {
				holder.ll_title.setBackgroundColor(context.getResources()
						.getColor(R.color.white));
				holder.tv_qty.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						final OrderDetail tag = (OrderDetail) arg0.getTag();
						if (tag.getIsFree().intValue() == ParamConst.FREE) {
							return;
						}
						final TextView textView = (TextView) arg0;
						textView.setBackgroundColor(context.getResources()
								.getColor(R.color.gray));
						startAnimation(position);
						buffer.delete(0, buffer.length());
						dismissCall = new DismissCall() {
							
							@Override
							public void call(String key, int num) {
								if (num == orderDetail.getItemNum()) {
									return;
								}
								if (num < 0)
									num = 0;
								if (num > 999)
									num = 999;
								textView.setText(num + "");
								if (key.equals("Enter")) {
									textView.setBackgroundColor(context
											.getResources().getColor(
													R.color.white));
									updateOrderDetail(tag, num);
									refreshList();
								}
							}
						};
					}
				});
			} else {
				holder.tv_qty.setOnClickListener(null);
				holder.tv_qty.setBackgroundColor(context.getResources()
						.getColor(R.color.gray));
				holder.ll_title.setBackgroundColor(context.getResources()
						.getColor(R.color.gray));
			}
			holder.name.setText(itemDetail.getItemName());
			holder.price.setText( App.instance.getCurrencySymbol()
					+ BH.getBD(orderDetail.getItemPrice()).toString());
			holder.tv_qty.setText(orderDetail.getItemNum() + "");
			holder.tv_qty.setTag(orderDetail);
			holder.subtotal.setText( App.instance.getCurrencySymbol()
					+ BH.getBD(orderDetail.getRealPrice()).toString());
			return arg1;
		}

		class ViewHolder {
			public TextView name;
			public TextView price;
			public TextView tv_qty;
			public TextView subtotal;
			public LinearLayout ll_title;
		}
	}
	
	public void startAnimation(final int position){
		rl_countKeyboard.setVisibility(View.VISIBLE);
		lv_dishes.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				lv_dishes.setSelection(position);
			}
		});
//		rl_content.post(new Runnable() {
//			@Override
//			public void run() {
//				if (AnimatorListenerImpl.isRunning) {
//					return;
//				}
//				AnimatorSet set = new AnimatorSet();
//				ObjectAnimator animator = ObjectAnimator.ofFloat(rl_content,
//						"y",rl_content.getY() + rl_countKeyboard.getHeight(),rl_content.getY())
//						.setDuration(2000);
//				set.play(animator); 
//				set.setInterpolator(new DecelerateInterpolator());
//				set.addListener(new AnimatorListenerImpl(){
//					@Override
//					public void onAnimationStart(Animator animation) {
//						// TODO Auto-generated method stub
//						super.onAnimationStart(animation);
//						
//					}
//				});
//				set.start();
//			}
//		});
	}

	public void endAnimation(){
		rl_countKeyboard.setVisibility(View.GONE);
//		rl_countKeyboard.post(new Runnable() {
//			@Override
//			public void run() {
//				if (AnimatorListenerImpl.isRunning) {
//					return;
//				}
//				AnimatorSet set = new AnimatorSet();
//				ObjectAnimator animator = ObjectAnimator.ofFloat(rl_content,
//						"y",rl_countKeyboard.getY(),rl_countKeyboard.getY() + rl_countKeyboard.getHeight())
//						.setDuration(2000);
//				set.play(animator);
//				set.setInterpolator(new DecelerateInterpolator());
//				set.addListener(new AnimatorListenerImpl(){
//					@Override
//					public void onAnimationEnd(Animator animation) {
//						// TODO Auto-generated method stub
//						super.onAnimationEnd(animation);
//						
//					}
//				});
//				set.start();
//			}
//		});
	}
	
	@Override
	public void onKeyBoardClick(String key) {
		if ("Cancel".equals(key)) {
			endAnimation();
		} else if ("Enter".equals(key)) {
			endAnimation();
			if (buffer.length() > 0) {
				dismissCall.call(key, Integer.parseInt(buffer.toString()));
			} else {
				return;
			}
		} else if ("Clear".equals(key)) {
			buffer.delete(0, buffer.length());
			dismissCall.call(key, 0);
		} else {
			if (buffer.length() < 4) {
				buffer.append(key);
			}
			dismissCall.call(key, Integer.parseInt(buffer.toString()));
		}
	}
	
	public interface DismissCall {
		public void call(String key, int num);
	}
}
