package com.alfredwaiter.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

import com.alfredbase.BaseActivity;
import com.alfredbase.LoadingDialog;
import com.alfredbase.ParamConst;
import com.alfredbase.global.CoreData;
import com.alfredbase.http.ResultCode;
import com.alfredbase.javabean.Order;
import com.alfredbase.javabean.OrderDetail;
import com.alfredbase.javabean.Places;
import com.alfredbase.javabean.Tables;
import com.alfredbase.store.sql.OrderDetailSQL;
import com.alfredbase.store.sql.OrderSQL;
import com.alfredwaiter.R;
import com.alfredwaiter.adapter.TablesAdapter;
import com.alfredwaiter.global.App;
import com.alfredwaiter.global.SyncCentre;
import com.alfredwaiter.global.UIHelp;
import com.alfredwaiter.popupwindow.SetPAXWindow;
import com.viewpagerindicator.TabPageIndicator;

public class TablesPage extends BaseActivity {
	public static final int VIEW_EVENT_SET_PAX = 0;
	public static final int VIEW_EVENT_SELECT_TABLES = 1;
	public static final int HANDLER_GET_PLACE_INFO = 2;
	public static final int HANDLER_ERROR_INFO = 3;
	
//	private static final String TITLE_NAME = "Table";
//	private TitleBar titleBar;
	private SetPAXWindow setPAXWindow;
	private List<Places> placesList;
	private Tables currenTables;
	private TabPageIndicator indicator;
	private ViewPager pager;
	private Adapter adapter;
	private TextView tv_kot_notification_qty;
	private boolean doubleBackToExitPressedOnce = false;
	
	@Override
	protected void initView() {
		super.initView();
		setContentView(R.layout.activity_tables);
		loadingDialog = new LoadingDialog(context);
		initTitle();
		placesList = CoreData.getInstance().getPlaceList();
		setPAXWindow = new SetPAXWindow(this, findViewById(R.id.rl_root),
				handler);
		adapter = new Adapter(this);
		pager = (ViewPager) findViewById(R.id.viewPager);
		pager.setAdapter(adapter);
		indicator = (TabPageIndicator) findViewById(R.id.tabPageIndicator);
		indicator.setViewPager(pager);
		tv_kot_notification_qty = (TextView) findViewById(R.id.tv_notification_qty);
	}

	public void initTitle(){
		findViewById(R.id.iv_back).setOnClickListener(this);
		((TextView)findViewById(R.id.tv_title_name)).setText(context.getResources().getString(R.string.table));
		findViewById(R.id.ll_kot_notification).setVisibility(View.VISIBLE);
		findViewById(R.id.iv_refresh).setVisibility(View.VISIBLE);
		findViewById(R.id.ll_kot_notification).setOnClickListener(this);
		findViewById(R.id.iv_refresh).setOnClickListener(this);
	}
	
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case VIEW_EVENT_SET_PAX: {
				int persons = 0;
				try {
					persons = Integer.parseInt((String) msg.obj);
				} catch (Exception e) {
					e.printStackTrace();
				}
				currenTables.setTablePacks(persons);
				currenTables.setTableStatus(ParamConst.TABLE_STATUS_DINING);
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("tables", currenTables);
				loadingDialog.setTitle(context.getResources().getString(R.string.loading));
				loadingDialog.show();
				SyncCentre.getInstance().selectTables(context, parameters,
						handler);
				break;
			}
			case VIEW_EVENT_SELECT_TABLES: {
				loadingDialog.dismiss();
				Order order = (Order) msg.obj;
				OrderSQL.update(order);
				ArrayList<OrderDetail> orderDetails = OrderDetailSQL.getOrderDetails(order.getId());
				for (OrderDetail orderDetail : orderDetails) {
					if (orderDetail.getOrderDetailStatus() == ParamConst.ORDERDETAIL_STATUS_WAITER_CREATE) {
						order.setOrderStatus(ParamConst.ORDER_STATUS_OPEN_IN_WAITER);
						OrderSQL.update(order);
						break;
					}
				}
				if (orderDetails.isEmpty()) {
					UIHelp.startMainPage(context, order);
				} else {
					OrderSQL.updateOrder(order);
					Order currentOrder = OrderSQL.getOrder(order.getId());
					UIHelp.startOrderDetailsTotal(context, currentOrder);
				}
				break;
			}
			case SelectRevenue.SYNC_DATA_TAG :
				loadingDialog.dismiss();
				placesList = CoreData.getInstance().getPlaceList();
				adapter.notifyDataSetChanged();
				indicator.notifyDataSetChanged();
				break;
//			case HANDLER_ERROR_INFO:
//				loadingDialog.dismiss();
//				UIHelp.showToast(context, "Refresh Defeat!");
//				break;
//			case SelectRevenue.HANDLER_CONN_REFUSED:
//				dialog.dismiss();
//				UIHelp.showToast(TablesPage.this,"Cannot connect the main POS in the reveue center");
//				break;
			case ResultCode.CONNECTION_FAILED:
				loadingDialog.dismiss();
				UIHelp.showToast(context, ResultCode.getErrorResultStr(context, (Throwable)msg.obj, 
						context.getResources().getString(R.string.revenue_center)));
				break;
			case App.VIEW_EVENT_SET_QTY: 
				int kotNotificationQty = App.instance.getKotNotificationQty();
				if (kotNotificationQty != 0) {
					tv_kot_notification_qty.setVisibility(View.VISIBLE);
					tv_kot_notification_qty.setText(String
							.valueOf(kotNotificationQty));
				} else {
					tv_kot_notification_qty.setVisibility(View.GONE);
				}
				break;
			default:
				break;
			}

		};
	};

	@Override
	public void handlerClickEvent(View v) {
		super.handlerClickEvent(v);
		switch (v.getId()) {
		case R.id.iv_refresh:
			getPlaces();
			break;
		case R.id.ll_kot_notification:
			UIHelp.startKOTNotification(context);
			break;
		case R.id.iv_back:
			this.finish();
			break;
		default:
			break;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		doubleBackToExitPressedOnce = false;
		getPlaces();
		httpRequestAction(App.VIEW_EVENT_SET_QTY, null);
	}

	class Adapter extends PagerAdapter {
		private List<View> views;
		private List<TablesAdapter> tablesAdapters;
		public Adapter(Context context) {
			LayoutInflater inflater = LayoutInflater.from(context);
			views = new ArrayList<View>();
			tablesAdapters = new ArrayList<TablesAdapter>();
			View view = null;
			for (int i = 0; i < placesList.size(); i++) {
				Places places = placesList.get(i);
				view = inflater.inflate(R.layout.item_table, null);
				GridView gv_tables = (GridView) view
						.findViewById(R.id.gv_tables);
				TablesAdapter tablesAdapter = new TablesAdapter(context, CoreData
						.getInstance().getTableList(
								App.instance.getRevenueCenter().getId(),
								places.getId()), places);
				tablesAdapters.add(tablesAdapter);
				gv_tables.setAdapter(tablesAdapter);
				gv_tables.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {

						currenTables = (Tables) arg0.getItemAtPosition(arg2);
						if (currenTables != null
								&& currenTables.getTableStatus() != ParamConst.TABLE_STATUS_IDLE) {
							handler.sendMessage(handler.obtainMessage(
									TablesPage.VIEW_EVENT_SET_PAX, currenTables.getTablePacks().toString()));
						} else {
							setPAXWindow.show();
						}
					}
				});
				views.add(view);
			}
		}

		@Override
		public int getCount() {
			return placesList.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			View view = views.get(position);
			((ViewPager) container).addView(view, 0);
			return view;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(views.get(position));
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return placesList.get(position).getPlaceName();
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}
		@Override
		public void notifyDataSetChanged() {
			super.notifyDataSetChanged();
			if(tablesAdapters != null && !tablesAdapters.isEmpty()){
				for(int i = 0; i < tablesAdapters.size(); i++){
					TablesAdapter tablesAdapter = tablesAdapters.get(i);
					tablesAdapter.setTableList();
					tablesAdapter.notifyDataSetChanged();
				}
			}
		}
	}

	private void getPlaces() {
		loadingDialog.setTitle(context.getResources().getString(R.string.loading));
		loadingDialog.show();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("revenueId", App.instance.getRevenueCenter().getId());
		SyncCentre.getInstance().getPlaceInfo(context, App.instance.getMainPosInfo().getIP(), parameters, handler);

	}
	
	
	
	@Override
	public void httpRequestAction(int action, Object obj) {
		super.httpRequestAction(action, obj);
		handler.sendEmptyMessage(action);
	}
	
	@Override
	public void onBackPressed() {
		if (doubleBackToExitPressedOnce) {
	        super.onBackPressed();
	        return;
	    }

	    this.doubleBackToExitPressedOnce = true;
	    UIHelp.showToast(this, context.getResources().getString(R.string.exit_program));

	    new Handler().postDelayed(new Runnable() {

	        @Override
	        public void run() {
	            doubleBackToExitPressedOnce=false;                       
	        }
	    }, 2000);
	}
}
