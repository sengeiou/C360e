package com.alfredposclient.view.viewkiosk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.alfredbase.BaseActivity;
import com.alfredbase.javabean.ItemDetail;
import com.alfredbase.javabean.Order;
import com.alfredbase.javabean.OrderDetail;
import com.alfredbase.utils.CommonUtil;
import com.alfredbase.utils.ObjectFactory;
import com.alfredbase.utils.ScreenSizeUtil;
import com.alfredbase.utils.TextTypeFace;
import com.alfredposclient.R;
import com.alfredposclient.activity.MainPage;
import com.alfredposclient.activity.kioskactivity.MainPageKiosk;
import com.alfredposclient.adapter.ItemDetailAdapter;

public class MainPageSearchViewKiosk extends LinearLayout implements OnClickListener{
	private static final String TAG = MainPageSearchViewKiosk.class.getSimpleName();
	private BaseActivity parent;
	private Context context;
	private EditText et_search;
	private ImageView iv_cancel;
	private Order order;
	private Handler handler;
	private GridView gv_items;
	private ItemDetailAdapter itemDetailAdapter;
	private InputMethodManager imm;
	private List<ItemDetail> itemDetails = new ArrayList<ItemDetail>();

	public MainPageSearchViewKiosk(Context context) {
		super(context);
		this.context = context;
		init(context);
	}

	public MainPageSearchViewKiosk(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init(context);
	}

	private void init(Context context) {
		View.inflate(context, R.layout.main_page_search_view, this);
		LinearLayout ll_blank_left = (LinearLayout) findViewById(R.id.ll_blank_left);
		LinearLayout ll_blank_right = (LinearLayout) findViewById(R.id.ll_blank_right);
		ll_blank_left.setOnClickListener(null);
		ll_blank_right.setOnClickListener(null);
		gv_items = (GridView) findViewById(R.id.gv_items);
		et_search = (EditText) findViewById(R.id.et_search);
		TextTypeFace textTypeFace = TextTypeFace.getInstance();
		textTypeFace.setTrajanProBlod(et_search);
		et_search.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				search();
			}
		});
		findViewById(R.id.ll_search).setOnClickListener(null);
		iv_cancel = (ImageView) findViewById(R.id.iv_cancel);
		iv_cancel.setOnClickListener(this);
		itemDetailAdapter = new ItemDetailAdapter(context, itemDetails);
		gv_items.setAdapter(itemDetailAdapter);

		gv_items.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				CommonUtil.hideSoftkeyBoard(parent);
				
				ItemDetail itemDetail = (ItemDetail) arg0
						.getItemAtPosition(arg2);
				OrderDetail orderDetail = ObjectFactory.getInstance()
						.getOrderDetail(order, itemDetail, 0);
				Message msg = null;

//				msg = handler.obtainMessage();
//				msg.what = MainPage.DISMISS_SOFT_INPUT;
//				handler.sendMessage(msg);

				msg = handler.obtainMessage();
				msg.what = MainPageKiosk.VIEW_EVENT_ADD_ORDER_DETAIL;
				msg.obj = orderDetail;
				handler.sendMessage(msg);
				
			}
		});
		gv_items.setOnScrollListener(new OnScrollListener() {
					
					@Override
					public void onScrollStateChanged(AbsListView arg0, int arg1) {
						if(arg1 == OnScrollListener.SCROLL_STATE_IDLE){
							if (imm != null)
								imm.hideSoftInputFromWindow(MainPageSearchViewKiosk.this.parent.getCurrentFocus().getWindowToken()
										,InputMethodManager.HIDE_NOT_ALWAYS);
						}
						
					}
					
					@Override
					public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
						
					}
				});
	}

	public void setParam(BaseActivity parent, Order order,
			List<ItemDetail> itemDetails, Handler handler) {
		if (itemDetails == null)
			itemDetails = Collections.emptyList();
		this.parent = parent;
		this.order = order;
		this.itemDetails.clear();
		this.itemDetails.addAll(itemDetails);
		this.handler = handler;
		et_search.setFocusable(true);
		et_search.setFocusableInTouchMode(true);
		et_search.requestFocus();
		imm = (InputMethodManager)parent.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null)
			imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
		gv_items.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {

					@SuppressLint("NewApi")
					@Override
					public void onGlobalLayout() {
						 int numColumns =
						 (int)Math.floor((gv_items.getWidth() - gv_items.getPaddingLeft() - gv_items.getPaddingRight())/(gv_items.getVerticalSpacing()
						 + ScreenSizeUtil.dip2px(MainPageSearchViewKiosk.this.parent,
						 ItemDetailAdapter.ITEM_WIDTH_HEIGHT)));
						gv_items.setNumColumns(numColumns);
					}
				});
		refresh();
	}

	private void search() {
		String key = et_search.getText().toString();
		Message msg = handler.obtainMessage();
		msg.what = MainPageKiosk.VIEW_EVENT_SEARCH;
		msg.obj = key;
		handler.sendMessage(msg);
	}
	
	public void cancelSearch() {
		et_search.setText("");
		et_search.clearFocus();
	}
	
	private void refresh() {
		if (itemDetailAdapter != null)
			itemDetailAdapter.notifyDataSetChanged();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_cancel: {
			handler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					if (imm != null)
						imm.hideSoftInputFromWindow(parent.getCurrentFocus().getWindowToken()
							,InputMethodManager.HIDE_NOT_ALWAYS);
				}
			}, 200);
			Message msg = handler.obtainMessage();
			msg.what = MainPageKiosk.VIEW_EVENT_DISMISS_SEARCH;
			handler.sendMessage(msg);
			break;
		}
		default:
			break;
		}
	}

}
