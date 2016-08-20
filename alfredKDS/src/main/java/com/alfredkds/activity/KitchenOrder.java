package com.alfredkds.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alfredbase.BaseActivity;
import com.alfredbase.LoadingDialog;
import com.alfredbase.ParamConst;
import com.alfredbase.http.ResultCode;
import com.alfredbase.javabean.KotItemDetail;
import com.alfredbase.javabean.KotSummary;
import com.alfredbase.javabean.model.MainPosInfo;
import com.alfredbase.store.sql.KotItemDetailSQL;
import com.alfredbase.utils.DialogFactory;
import com.alfredbase.utils.ScreenSizeUtil;
import com.alfredbase.utils.TextTypeFace;
import com.alfredbase.utils.TimeUtil;
import com.alfredkds.R;
import com.alfredkds.global.App;
import com.alfredkds.global.SyncCentre;
import com.alfredkds.global.UIHelp;
import com.alfredkds.javabean.Kot;
import com.alfredkds.view.FinishQtyWindow;
import com.alfredkds.view.HorizontalListView;
import com.alfredkds.view.KOTArrayAdapter;
import com.alfredkds.view.PopItemAdapter;
import com.alfredkds.view.PopItemListView;
import com.alfredkds.view.PopItemListView.RemoveDirection;
import com.alfredkds.view.PopItemListView.RemoveListener;
import com.alfredkds.view.TopBarView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KitchenOrder extends BaseActivity {
	public static final int HANDLER_TRANSFER_KOT = 3;
	public static final int HANDLER_MERGER_KOT = 4;
	
	private LinearLayout ll_progress_list; 
	private HorizontalListView ll_orders;	//水平列表
	public KOTArrayAdapter adapter;
	private List<Kot> kots = new ArrayList<Kot>();
	
	private TopBarView topBarView;	//页面顶部view 
	
	private IntentFilter filter;
	private KotSummary kotSummary;
	private TextTypeFace textTypeFace;
	private FinishQtyWindow finishQtyPop;
	private MainPosInfo mainPosInfo;
	private boolean doubleBackToExitPressedOnce = false;
	
	public Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case App.HANDLER_NEW_KOT:
				kots = App.instance.getRefreshKots();
				adapter.setKots(kots);
				adapter.setAddFirstItem(true);
				adapter.notifyDataSetChanged();
				if(itemPopupWindow != null && itemPopupWindow.isShowing()){
					popItemAdapter.setKot(App.instance.getKot(kotSummary));
					popItemAdapter.notifyDataSetChanged();
				}
				break;
			case App.HANDLER_UPDATE_KOT:
				kots = App.instance.getRefreshKots();
				adapter.setKots(kots);
				adapter.notifyDataSetChanged();
				if(itemPopupWindow != null && itemPopupWindow.isShowing()){
					popItemAdapter.setKot(App.instance.getKot(kotSummary));
					popItemAdapter.notifyDataSetChanged();
				}
				break;
			case App.HANDLER_DELETE_KOT:
				refresh();
				break;
			case App.HANDLER_RECONNECT_POS:
				loadingDialog.dismiss();
				DialogFactory.commonTwoBtnDialog(context, "", getString(R.string.reconnect_pos),
						getString(R.string.cancel), getString(R.string.ok), null, 
						new OnClickListener() {
							@Override
							public void onClick(View v) {
								UIHelp.startConnectPOS(context);
								finish();
							}
						});
				break;
			case App.HANDLER_SEND_FAILURE:
				loadingDialog.dismiss();
				adapter.notifyDataSetChanged();
				refresh();
				break;
			case App.HANDLER_RETURN_ERROR:
				UIHelp.showToast(context,ResultCode.getErrorResultStrByCode(context,(Integer)msg.obj, null));
				refresh();
				break;
			case App.HANDLER_SEND_FAILURE_SHOW:
				UIHelp.showToast(context,context.getResources().getString(R.string.send_failed));
				break;
			case App.HANDLER_REFRESH_KOT:
				dismissLoadingDialog();
				refresh();
				break;
			case HANDLER_TRANSFER_KOT:
				refresh();
				break;
			case HANDLER_MERGER_KOT:
				refresh();
				break;
			case ResultCode.CONNECTION_FAILED:
				loadingDialog.dismiss();
				UIHelp.showToast(context,ResultCode.getErrorResultStr(context,(Throwable)msg.obj,
						context.getResources().getString(R.string.revenue_center)));
				break;
			case App.HANDLER_VERIFY_MAINPOS:
				UIHelp.showToast(context,context.getResources().getString(R.string.other_pos_data));
				break;
			case App.HANDLER_KOTSUMMARY_IS_UNREAL:
				loadingDialog.dismiss();
				UIHelp.showToast(context,context.getResources().getString(R.string.order_discarded));
				List<Kot> kots = App.instance.getRefreshKots();
				adapter.setKots(kots);
				adapter.notifyDataSetChanged();
				if(kots.isEmpty()){
					itemPopupWindow.dismiss();
				}
				if(itemPopupWindow != null && itemPopupWindow.isShowing()){
					itemPopupWindow.dismiss();
				}
				break;
			case App.HANDLER_KOT_COMPLETE_USER_FAILED:
				App.instance.reload(context,handler);
				break;
			case Login.HANDLER_LOGIN:
				refresh();	
				break;
			default:
				break;
			}
		};
	};
	
	public void refresh(){
		if (loadingDialog.isShowing()) {
			loadingDialog.dismiss();
		}
		List<Kot> kots = App.instance.getRefreshKots();
		adapter.setKots(kots);
		adapter.notifyDataSetChanged();
		if(kots.isEmpty()){
			itemPopupWindow.dismiss();
		}
		if(itemPopupWindow != null && itemPopupWindow.isShowing()){
			popItemAdapter.setKot(App.instance.getKot(kotSummary));
			popItemAdapter.notifyDataSetChanged();
		}
	}
	
	@Override
	public void httpRequestAction(int action, Object obj) {
		handler.sendMessage(handler.obtainMessage(action,null));
	}
	
	@Override
	protected void initView() {
		super.initView();
		setContentView(R.layout.activity_kitchen_order);
		loadingDialog = new LoadingDialog(context);
		loadingDialog.setTitle(context.getResources().getString(R.string.loading));
		mainPosInfo = App.instance.getCurrentConnectedMainPos();
		App.instance.setRing();
		filter = new IntentFilter();
		filter.addAction(Intent.ACTION_TIME_TICK);
		registerReceiver(receiver, filter);
		
		//ll_progress_list = (LinearLayout) findViewById(R.id.ll_progress_list);
		//initProgressList();
		ll_orders = (HorizontalListView) findViewById(R.id.ll_orders);
		finishQtyPop = new FinishQtyWindow(context, findViewById(R.id.rl_root), handler);
		initKOTList();
		initTopBarView();
		initTextTypeFace();
	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			adapter.notifyDataSetChanged();
		}
	};
	
	@Override
	protected void onResume() {
		doubleBackToExitPressedOnce = false;
		adapter.setKots(App.instance.getRefreshKots());
		adapter.notifyDataSetChanged();
		if(itemPopupWindow != null && itemPopupWindow.isShowing()){
			popItemAdapter.setKot(App.instance.getKot(kotSummary));
			popItemAdapter.notifyDataSetChanged();
		}
		super.onStart();
	}
	private void initTopBarView(){
		topBarView = (TopBarView) findViewById(R.id.tv_title);
	}
	@Override
	protected void onDestroy() {
		unregisterReceiver(receiver);
		super.onDestroy();
	}
	private void initKOTList() {
		// Assign adapter to the HorizontalListView
		adapter = new KOTArrayAdapter(context);
		kots = App.instance.getInitKots();
		adapter.setKots(kots);
		ll_orders.setAdapter(adapter);
	}
	
	private void initProgressList() {
		View view = null;
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(80, 80);
		params.rightMargin = 5;
		params.leftMargin = 5;
		params.gravity = Gravity.CENTER_VERTICAL;
		for (int i = 0; i < 10; i++) {
			view = getLayoutInflater().inflate(R.layout.order_progress_view,
					null);
			view.setLayoutParams(params);
			TextView tv_progress = (TextView) view
					.findViewById(R.id.tv_progress);
			tv_progress.setText(((10 - i) * 10 - 1) + "%");
			ll_progress_list.addView(view);
		}
	}

	@Override
	public void handlerClickEvent(View v) {
		super.handlerClickEvent(v);
		switch (v.getId()) {
		case R.id.iv_complete:
			if(itemPopupWindow != null && itemPopupWindow.isShowing()){
				kots = App.instance.getRefreshKots();
				adapter.setKots(kots);
				adapter.notifyDataSetChanged();
				itemPopupWindow.dismiss();
//				refresh();
			}
			break;
		case R.id.iv_back:
			if(itemPopupWindow != null && itemPopupWindow.isShowing()){
				adapter.notifyDataSetChanged();
				itemPopupWindow.dismiss();
			}
		break;
		case R.id.ll_bottom: {
			String title = getResources().getString(R.string.warning);
			String content = getResources().getString(R.string.complete_all_item);
			String left = getResources().getString(R.string.no);
			String right = getResources().getString(R.string.yes);
			DialogFactory.commonTwoBtnDialog(this, title, content, left, right, null, new OnClickListener() {
				@Override
				public void onClick(View view) {
					List<KotItemDetail> itemDetails = new ArrayList<KotItemDetail>();
					for(KotItemDetail kotItemDetail : App.instance.getKot(kotSummary).getKotItemDetails()){
						kotItemDetail.setFinishQty(kotItemDetail.getUnFinishQty());
						kotItemDetail.setUnFinishQty(0);
						kotItemDetail.setKotStatus(ParamConst.KOT_STATUS_DONE);
						KotItemDetailSQL.update(kotItemDetail);
						itemDetails.add(kotItemDetail);
					}
					Map<String, Object> parameters = new HashMap<String, Object>();
					parameters.put("kotSummary", kotSummary);
					parameters.put("kotItemDetails", itemDetails);
					parameters.put("type", 1);
					SyncCentre.getInstance().kotComplete(KitchenOrder.this,
							App.instance.getCurrentConnectedMainPos(),parameters, handler);
					loadingDialog.show();
				}
			});




		}
		break;
		default:
			break;
		}
	}
	
	private PopupWindow itemPopupWindow;
	private PopItemListView popItemListView;
	private PopItemAdapter popItemAdapter;
	public void showOrderItem(final KotSummary kotSummary) {
		Kot kot = App.instance.getKot(kotSummary);
		this.kotSummary = kotSummary;
		View view = getLayoutInflater().inflate(R.layout.kitche_order_item_popupwindow, null);
		view.findViewById(R.id.iv_back).setOnClickListener(this);
		view.findViewById(R.id.iv_complete).setOnClickListener(this);
		view.findViewById(R.id.ll_bottom).setOnClickListener(this);
		//kotTop
		TextView kotId = (TextView) view.findViewById(R.id.tv_kot_id);
		TextView orderId = (TextView) view.findViewById(R.id.tv_order_id);
		TextView table = (TextView) view.findViewById(R.id.tv_table);
		TextView posName = (TextView) view.findViewById(R.id.tv_pos);
		TextView date = (TextView) view.findViewById(R.id.tv_date);
		TextView time = (TextView) view.findViewById(R.id.tv_time);
		TextView tv_kiosk_order_id = (TextView) view.findViewById(R.id.tv_kiosk_order_id);
		if (mainPosInfo.getIsKiosk() == ParamConst.MAINPOSINFO_IS_KIOSK) {
			tv_kiosk_order_id.setVisibility(View.VISIBLE);
			orderId.setVisibility(View.GONE);
		}else {
			tv_kiosk_order_id.setVisibility(View.GONE);
			orderId.setVisibility(View.VISIBLE);
		}
		kotId.setText(kot.getKotSummary().getId()+"");
		orderId.setText(context.getResources().getString(R.string.order_id_) + kot.getKotSummary().getOrderId()+"");
		tv_kiosk_order_id.setText(context.getResources().getString(R.string.order_id_) + kot.getKotSummary().getOrderId() + "");
		table.setText(context.getResources().getString(R.string.table_) + kot.getKotSummary().getTableName()+"");
		posName.setText(kot.getKotSummary().getRevenueCenterName()+"");
		date.setText(TimeUtil.getPrintDate(kot.getKotSummary().getCreateTime()));
		time.setText(TimeUtil.getPrintTime(kot.getKotSummary().getCreateTime()));
		textTypeFace.setTrajanProBlod(kotId);
		textTypeFace.setTrajanProRegular(orderId);
		textTypeFace.setTrajanProRegular(table);
		textTypeFace.setTrajanProRegular(posName);
		textTypeFace.setTrajanProRegular(date);
		textTypeFace.setTrajanProRegular(time);
		textTypeFace.setTrajanProRegular(tv_kiosk_order_id);
		
		popItemListView = (PopItemListView) view.findViewById(R.id.lv_kot);
		popItemAdapter = new PopItemAdapter(context);
		popItemAdapter.setKot(kot);
		popItemListView.setAdapter(popItemAdapter);
		popItemListView.setRemoveListener(new RemoveListener() {
			@Override
			public void removeItem(RemoveDirection direction, int position) {
				PopItemAdapter currentPopItems = (PopItemAdapter) popItemListView.getAdapter();
				Kot popKot = currentPopItems.getKot();
				/*
				kotItemDetail.setFinishQty(kotItemDetail.getUnFinishQty());
				kotItemDetail.setUnFinishQty(0);
				kotItemDetail.setKotStatus(ParamConst.KOT_STATUS_DONE);
				KotItemDetailSQL.update(kotItemDetail);
				 */
				switch (direction) {
				case LEFT:
					if (position >= popKot.getKotItemDetails().size()) {
//						return;
					}
					if(popKot.getKotItemDetails().get(position).getKotStatus()<ParamConst.KOT_STATUS_DONE
							&& popKot.getKotItemDetails().get(position).getCategoryId() == ParamConst.KOTITEMDETAIL_CATEGORYID_MAIN){
						KotItemDetail kotItemDetail = popKot.getKotItemDetails().get(position);
						if (kotItemDetail.getUnFinishQty() > 1 ) {
							finishQtyPop.show(kotItemDetail.getUnFinishQty()+"",kotSummary, kotItemDetail,loadingDialog);
							return;
						}
						loadingDialog.show();
						kotItemDetail.setKotStatus(ParamConst.KOT_STATUS_DONE);
						kotItemDetail.setUnFinishQty(0);
						kotItemDetail.setFinishQty(1);
						KotItemDetailSQL.update(kotItemDetail);
						List<KotItemDetail> itemDetails = new ArrayList<KotItemDetail>();
						itemDetails.add(kotItemDetail);
						Map<String, Object> parameters = new HashMap<String, Object>();
						parameters.put("kotSummary", popKot.getKotSummary());
						parameters.put("kotItemDetails", itemDetails);
						SyncCentre.getInstance().kotComplete(context, 
								App.instance.getCurrentConnectedMainPos(),parameters, handler);
					}
					
					break;
				default:
					break;
				}
			}
		});
		
//		TextTypeFace.setTypeface(context, (TextView) view.findViewById(R.id.tv_order_item_name));
		itemPopupWindow = new PopupWindow(view,
				LinearLayout.LayoutParams.MATCH_PARENT,
				(int) (ScreenSizeUtil.height - ScreenSizeUtil
						.getStatusBarHeight(context)));
		itemPopupWindow.setFocusable(true);
		if (itemPopupWindow != null && !itemPopupWindow.isShowing())
			itemPopupWindow.showAtLocation(findViewById(R.id.ll_orders),
					Gravity.TOP | Gravity.LEFT, 0,
					ScreenSizeUtil.getStatusBarHeight(context));
	}

	private void initTextTypeFace(){
		textTypeFace = TextTypeFace.getInstance();
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
