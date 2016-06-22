package com.alfredwaiter.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.alfredbase.BaseActivity;
import com.alfredbase.LoadingDialog;
import com.alfredbase.ParamConst;
import com.alfredbase.global.CoreData;
import com.alfredbase.http.ResultCode;
import com.alfredbase.javabean.ItemCategory;
import com.alfredbase.javabean.ItemDetail;
import com.alfredbase.javabean.ItemMainCategory;
import com.alfredbase.javabean.Order;
import com.alfredbase.javabean.OrderDetail;
import com.alfredbase.javabean.Tables;
import com.alfredbase.store.sql.OrderDetailSQL;
import com.alfredbase.store.sql.OrderModifierSQL;
import com.alfredbase.store.sql.OrderSQL;
import com.alfredbase.utils.CommonUtil;
import com.alfredbase.utils.ObjectFactory;
import com.alfredbase.utils.TextTypeFace;
import com.alfredwaiter.R;
import com.alfredwaiter.adapter.OrderAdapter;
import com.alfredwaiter.global.App;
import com.alfredwaiter.global.SyncCentre;
import com.alfredwaiter.global.UIHelp;
import com.alfredwaiter.javabean.ItemCategoryAndDetails;
import com.alfredwaiter.popupwindow.SearchMenuItemWindow;
import com.alfredwaiter.popupwindow.SetItemCountWindow;
import com.alfredwaiter.utils.WaiterUtils;
import com.alfredwaiter.view.SelectPersonDialog;
import com.alfredwaiter.view.SlidePanelView;

public class MainPage extends BaseActivity {
	public static final int VIEW_EVENT_CLICK_MAIN_CATEGORY = 0;
	public static final int VIEW_EVENT_MODIFY_ITEM_COUNT = 1;
	public static final int VIEW_EVENT_SET_PERSON_INDEX = 2;
	public static final int VIEW_EVENT_SET_QTY = 3;
	public static final int VIEW_ENVENT_KOTNOTIFICATION_LIST = 4;
	public static final int VIEW_EVENT_CLICK_ALL_MAIN_CATEGORY = 5;
	public static final int VIEW_ENVENT_GET_ORDERDETAILS = 101;
	public static final int TRANSFER_TABLE_NOTIFICATION = 102;
	
	public static final int VIEW_EVENT_SHOW_SEARCH = 200;
	public static final int VIEW_EVENT_SEARCH = 201;
	public static final int VIEW_EVENT_DISMISS_SEARCH = 202;
	public static final int VIEW_EVENT_ADD_ORDER_DETAIL = 203;
	
	public static final int VIEW_EVENT_SLIDE = 204;
	public static final int VIEW_EVENT_SLIDE_CLICK = 205;
	
//	public static final int VIEW_EVENT_SET_ITEM_NUM = 103;
	private ExpandableListView expandableListView;
	private OrderAdapter adapter;
	private List<ItemCategoryAndDetails> itemCategoryAndDetailsList = new ArrayList<ItemCategoryAndDetails>();
	private SlidePanelView panel;
	private LinearLayout container;
	private TextView tv_person_index;
	private TextView table_name;
	private SelectPersonDialog dialog;
	private Order currentOrder;
	private SetItemCountWindow setItemCountWindow;

	private int currentGroupId = 0;

	private List<OrderDetail> orderDetails = new ArrayList<OrderDetail>();
	private TextView tv_notification_qty;
	
	private SearchMenuItemWindow searchPopUp;
	
	private Button btn_slide;
	@Override
	protected void initView() {
		super.initView();
		setContentView(R.layout.activity_main_page);
		initTextTypeFace();
		initTitle();
		searchPopUp = new SearchMenuItemWindow(context,handler,findViewById(R.id.rl_root));
//		searchPopUp.setParams(this, handler, findViewById(R.id.rl_root));
//		searchPopUp.setParams(currentOrder,CoreData.getInstance().getItemDetails());
		setItemCountWindow = new SetItemCountWindow(this, findViewById(R.id.rl_root),
				handler);
		dialog = new SelectPersonDialog(context, handler);
		getIntentData();
		expandableListView = (ExpandableListView) findViewById(R.id.expandedListViewEx);
		expandableListView.setDividerHeight(0);
		itemCategoryAndDetailsList.addAll(getItemCategoryAndDetails(null));
		adapter = new OrderAdapter(context, itemCategoryAndDetailsList, handler,setItemCountWindow);
		expandableListView.setGroupIndicator(null);
		expandableListView.setAdapter(adapter);
		for (int i = 0; i < itemCategoryAndDetailsList.size(); i++) {
			expandableListView.expandGroup(i);
		}
		table_name = (TextView)findViewById(R.id.tv_table_name);
		//get table name
		Tables currentTable = CoreData.getInstance().getTables(currentOrder.getTableId());
		if (currentTable!=null)
		  table_name.setText(currentTable.getTableName());
//		expandableListView.setOnChildClickListener(new OnChildClickListener() {
//
//			@Override
//			public boolean onChildClick(ExpandableListView parent, View v,
//					int groupPosition, int childPosition, long id) {
//				ItemDetail itemDetail = (ItemDetail) adapter.getChild(
//						groupPosition, childPosition);
//				OrderDetail orderDetail = OrderDetailSQL.getUnFreeOrderDetail(
//						currentOrder, itemDetail, currentGroupId,
//						ParamConst.ORDERDETAIL_STATUS_WAITER_CREATE);
//				UIHelp.startOrderDetail(context, currentOrder, itemDetail,
//						orderDetail, currentGroupId);
//				return false;
//			}
//		});
		// refreshList();
//		findViewById(R.id.iv_kot_notification).setOnClickListener(this);
		findViewById(R.id.ll_dish_total).setOnClickListener(this);
		findViewById(R.id.iv_person_index).setOnClickListener(this);
//		findViewById(R.id.iv_setting).setOnClickListener(this);
		container = (LinearLayout) findViewById(R.id.ll_container);
		panel = new SlidePanelView(this, expandableListView, 200,
				LayoutParams.MATCH_PARENT, handler);
		container.addView(panel, 0);// 加入Panel控件
		btn_slide = (Button) findViewById(R.id.btn_slide);
		btn_slide.setOnTouchListener(panel.handlerTouchEvent);
//		((TextView) findViewById(R.id.tv_tables_name)).setText(CoreData
//				.getInstance().getTables(currentOrder.getTableId())
//				.getTableName());
		tv_person_index = (TextView) findViewById(R.id.tv_person_index);

		loadingDialog = new LoadingDialog(context);
		loadingDialog.setTitle(context.getResources().getString(R.string.loading));
		loadingDialog.show();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("order", currentOrder);
		SyncCentre.getInstance().handlerGetOrderDetails(context, parameters,
				handler);
	}

	private void getIntentData() {
		Intent intent = getIntent();
		currentOrder = (Order) intent.getExtras().get("order");
	}

	public void initTitle(){
		findViewById(R.id.iv_back).setOnClickListener(this);
		((TextView)findViewById(R.id.tv_title_name)).setText(context.getResources().getString(R.string.menu));
		findViewById(R.id.ll_kot_notification).setVisibility(View.VISIBLE);
		tv_notification_qty = (TextView) findViewById(R.id.tv_notification_qty);
		((ImageView)findViewById(R.id.iv_refresh)).setImageDrawable(getResources().getDrawable(R.drawable.icon_set));
		findViewById(R.id.iv_refresh).setVisibility(View.VISIBLE);
		findViewById(R.id.ll_kot_notification).setOnClickListener(this);
		findViewById(R.id.iv_refresh).setOnClickListener(this);
	}
	
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case VIEW_EVENT_CLICK_MAIN_CATEGORY: {
				ItemMainCategory itemMainCategory = (ItemMainCategory) msg.obj;
				itemCategoryAndDetailsList.clear();
				itemCategoryAndDetailsList
						.addAll(getItemCategoryAndDetails(itemMainCategory));
				for (int i = 0; i < itemCategoryAndDetailsList.size(); i++) {
					expandableListView.expandGroup(i);
				}
				refreshList();
				break;
			}
			case VIEW_EVENT_CLICK_ALL_MAIN_CATEGORY: {
				itemCategoryAndDetailsList.clear();
				itemCategoryAndDetailsList.addAll(getItemCategoryAndDetails(null));
				for (int i = 0; i < itemCategoryAndDetailsList.size(); i++) {
					expandableListView.expandGroup(i);
				}
				refreshList();
				break;
			}
			case VIEW_EVENT_MODIFY_ITEM_COUNT: {
				Map<String, Object> map = (Map<String, Object>) msg.obj;
				updateOrderDetail((ItemDetail) map.get("itemDetail"),
						(Integer) map.get("count"));
				refreshTotal();
				refreshList();
				break;
			}

			case VIEW_EVENT_SET_PERSON_INDEX: {
				currentGroupId = (Integer) msg.obj;
				if (currentGroupId == 0) {//TODO
					tv_person_index.setText("?");
				} else {
					tv_person_index.setText(currentGroupId + "");
				}
				refreshTotal();
				refreshList();
				break;
			}
			case App.VIEW_EVENT_SET_QTY: { 
				int kotNotificationQty = App.instance.getKotNotificationQty();
				if (kotNotificationQty != 0) {
					tv_notification_qty.setVisibility(View.VISIBLE);
					tv_notification_qty.setText(String
							.valueOf(kotNotificationQty));
				} else {
					tv_notification_qty.setVisibility(View.GONE);
				}
			}
				break;
			case VIEW_ENVENT_GET_ORDERDETAILS:
				loadingDialog.dismiss();
				refreshTotal();
				refreshList();
				break;
			case MainPage.TRANSFER_TABLE_NOTIFICATION:
				WaiterUtils.showTransferTableDialog(context);
				break;
			case ResultCode.CONNECTION_FAILED:
				loadingDialog.dismiss();
				UIHelp.showToast(context, ResultCode.getErrorResultStr(context, (Throwable)msg.obj, 
						context.getResources().getString(R.string.revenue_center)));
				break;
//			case VIEW_EVENT_SET_ITEM_NUM:
//				Map<String, Object> map = (Map<String, Object>) msg.obj;
//				updateOrderDetail((ItemDetail) map.get("itemDetail"),
//						Integer.parseInt((String) map.get("count")));
//				refreshTotal();
//				refreshList();
//				break;
			case MainPage.VIEW_EVENT_SHOW_SEARCH:
				searchPopUp.show(currentOrder,CoreData.getInstance().getItemDetails());
				break;
			case MainPage.VIEW_EVENT_SEARCH:
				if (searchPopUp != null && searchPopUp.isShowing()) {
					search((String) msg.obj);
				}
				break;
			case MainPage.VIEW_EVENT_DISMISS_SEARCH:
				dismissSearchView();
				searchPopUp.dismiss();
				searchPopUp.cancelSearch();
				dismissSoftInput();
				break;
			case VIEW_EVENT_SLIDE:
				btn_slide.setBackgroundResource(R.drawable.btn_slide_click);
				break;
			case VIEW_EVENT_SLIDE_CLICK:
				btn_slide.setBackgroundResource(R.drawable.btn_slide);
				break;
			default:
				break;
			}
		};
	};

	protected void onResume() {
		super.onResume();
		refreshTotal();
		refreshList();
		httpRequestAction(App.VIEW_EVENT_SET_QTY, null);
	};

	private void refreshList() {
		adapter.setParams(currentOrder,orderDetails, currentGroupId);
		adapter.notifyDataSetChanged();
	}

	private void updateOrderDetail(ItemDetail itemDetail, int count) {
		OrderDetail orderDetail = OrderDetailSQL.getUnFreeOrderDetail(
				currentOrder, itemDetail, currentGroupId,
				ParamConst.ORDERDETAIL_STATUS_WAITER_CREATE);
		int oldCount = OrderDetailSQL.getUnFreeOrderDetailsNumInKOTOrPOS(
				currentOrder, itemDetail, currentGroupId);
		if ((count - oldCount) <= 0) {// 删除
			OrderDetailSQL.deleteOrderDetail(orderDetail);
			OrderModifierSQL.deleteOrderModifierByOrderDetail(orderDetail);
		} else {// 添加
			count = count - oldCount;
			currentOrder.setOrderStatus(ParamConst.ORDER_STATUS_OPEN_IN_WAITER);
			OrderSQL.update(currentOrder);
			if (orderDetail == null) {
				orderDetail = ObjectFactory.getInstance()
						.createOrderDetailForWaiter(currentOrder, itemDetail,
								currentGroupId, App.instance.getUser());
				orderDetail.setItemNum(count);
				OrderDetailSQL.addOrderDetailETCForWaiter(orderDetail);
			} else {
				orderDetail.setItemNum(count);
				OrderDetailSQL.updateOrderDetailAndOrderForWaiter(orderDetail);
			}
		}
	}

	private void refreshTotal() {
		orderDetails = OrderDetailSQL.getUnFreeOrderDetails(currentOrder);
		List<OrderDetail> orderDetailList = OrderDetailSQL
				.getOrderDetails(currentOrder.getId());
		int itemCount = 0;
		if (!orderDetailList.isEmpty()) {
			for (OrderDetail orderDetail : orderDetailList) {
				itemCount += orderDetail.getItemNum();
			}
		}
//		((TextView) findViewById(R.id.tv_item_count)).setText(itemCount
//				+ " Saved items");
	}

	private void search(String key) {
		List<ItemDetail> itemDetails = CoreData.getInstance()
				.getItemDetails();
		List<ItemDetail> itemDetailList = new ArrayList<ItemDetail>();
		if (key != null) {
			key = key.trim().replaceAll("\\s+","");
			for (ItemDetail itemDtail : itemDetails) {
				String name = CommonUtil.getInitial(itemDtail.getItemName());
				if (name.contains(key) || name.contains(key.toUpperCase())) {
					itemDetailList.add(itemDtail);
					continue;
				}
			}
		}
		searchPopUp.setAdapterData(currentOrder,itemDetailList,currentGroupId);	
	}
	
	private List<ItemCategoryAndDetails> getItemCategoryAndDetails(
			ItemMainCategory itemMainCategory) {
		List<ItemCategoryAndDetails> result = new ArrayList<ItemCategoryAndDetails>();
		ItemCategoryAndDetails itemCategoryAndDetails = null;
		if (itemMainCategory == null) {
//			for(ItemMainCategory mItemMainCategory:CoreData.getInstance()
//					.getItemMainCategories()){
				for (ItemCategory itemCategory : CoreData.getInstance()
						.getItemCategories()) {
					itemCategoryAndDetails = new ItemCategoryAndDetails();
					itemCategoryAndDetails.setItemCategory(itemCategory);
					itemCategoryAndDetails.setItemDetails(CoreData.getInstance()
							.getItemDetails(itemCategory));
					result.add(itemCategoryAndDetails);
				}
//			}
		} else {
			for (ItemCategory itemCategory : CoreData.getInstance()
					.getItemCategories(itemMainCategory)) {
				itemCategoryAndDetails = new ItemCategoryAndDetails();
				itemCategoryAndDetails.setItemCategory(itemCategory);
				itemCategoryAndDetails.setItemDetails(CoreData.getInstance()
						.getItemDetails(itemCategory));
				result.add(itemCategoryAndDetails);
			}
		}
		return result;
	}

	@Override
	public void handlerClickEvent(View v) {
		super.handlerClickEvent(v);
		switch (v.getId()) {
		case R.id.iv_back:
			this.finish();
			break;
		case R.id.ll_kot_notification: {
			UIHelp.startKOTNotification(context);
			break;
		}
		case R.id.ll_dish_total: {
			OpenOrderDetailsTotal();
			break;
		}
		case R.id.iv_person_index: {
			int maxGroupId = OrderDetailSQL.getMaxGroupId(currentOrder);

			dialog.show(currentOrder.getPersons() > maxGroupId ? currentOrder
					.getPersons() : maxGroupId);
			break;
		}
		case R.id.iv_refresh:
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("visibilityMap", View.VISIBLE);
			UIHelp.startSetting(context, map);
			break;
		default:
			break;
		}
	}

	private void OpenOrderDetailsTotal() {
		UIHelp.startOrderDetailsTotal(context, currentOrder);
//		this.finish();
	}
	
	private void dismissSoftInput() {
		// 得到InputMethodManager的实例
		if (getWindow().getAttributes().softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED) {
			// 如果开启
			getWindow().setSoftInputMode(
					WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
			getWindow().getAttributes().softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_UNCHANGED;
		}
	}
	
	private void dismissSearchView() {
		searchPopUp.setAdapterData(currentOrder,CoreData.getInstance().getItemDetails(""),currentGroupId);
	}

	@Override
	public void httpRequestAction(int action, Object obj) {
		super.httpRequestAction(action, obj);
		if(TRANSFER_TABLE_NOTIFICATION == action){
			Order mOrder = (Order) obj;
			if(mOrder.getId().intValue() == currentOrder.getId().intValue()){
				handler.sendEmptyMessage(TRANSFER_TABLE_NOTIFICATION);
			}
		}
		handler.sendEmptyMessage(action);
	}
	
	private void initTextTypeFace() {
		TextTypeFace textTypeFace = TextTypeFace.getInstance();
		textTypeFace.setTrajanProBlod((TextView)findViewById(R.id.tv_title_name));
		textTypeFace.setTrajanProBlod((TextView)findViewById(R.id.tv_notification_qty));
		textTypeFace.setTrajanProBlod((TextView)findViewById(R.id.tv_person_index));
		textTypeFace.setTrajanProBlod((TextView)findViewById(R.id.tv_table_name));
	}
}
