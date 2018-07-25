package com.alfredwaiter.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alfredbase.global.CoreData;
import com.alfredbase.javabean.ItemCategory;
import com.alfredbase.javabean.ItemDetail;
import com.alfredbase.javabean.ItemMainCategory;
import com.alfredbase.utils.TextTypeFace;
import com.alfredwaiter.R;
import com.alfredwaiter.activity.CallBackMove;
import com.alfredwaiter.activity.CheckListener;
import com.alfredwaiter.activity.ItemDetailFragment;
import com.alfredwaiter.activity.MainPage;
import com.alfredwaiter.adapter.ItemHeaderDecoration;
import com.alfredwaiter.adapter.ItemHeaderDetailDecoration;

/**
 * 参考http://blog.csdn.net/hellogv/article/details/6828584
 * 
 * @author 冯小卫 2014-4-30
 * 
 */
public class SlidePanelView extends LinearLayout implements
		GestureDetector.OnGestureListener ,CheckListener {

	private Boolean isMoved;
	public void check(int position, boolean isScroll) {
		setChecked(position,isScroll);
		Log.d("滑动事件--->", position + "---" + isScroll);
	}

	public interface PanelClosedEvent {
		void onPanelClosed(View panel);
	}

	public interface PanelOpenedEvent {
		void onPanelOpened(View panel);
	}

	private final static int MOVE_WIDTH = -20;
	private ListView lv_main_category;
	private int mLeftMargin = 0;
	private Context mContext;
	private GestureDetector mGestureDetector;
	private boolean mIsScrolling = false;
	private float mScrollX;
	private PanelClosedEvent panelClosedEvent = null;
	private PanelOpenedEvent panelOpenedEvent = null;

	private List<ItemMainCategory> itemMainCategories;

	private LayoutInflater inflater;

	private Handler handler;
	private View searchBtn;
	
	private TextTypeFace textTypeFace;

	private boolean isPressed = true;
	private int selectItem = 0;
	private static CallBackMove callBackMove;

	public SlidePanelView(Context context, View otherView, int width,
			int height,  final Handler handler) {
		super(context);
		this.mContext = context;
		this.handler = handler;

		inflater = LayoutInflater.from(context);
		View.inflate(context, R.layout.order_slide, this);
		searchBtn = (View)findViewById(R.id.iv_search);
		searchBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				handler.sendMessage(handler.obtainMessage(
						MainPage.VIEW_EVENT_SHOW_SEARCH,null));				
			}});

		initList();
		// 定义手势识别
		mGestureDetector = new GestureDetector(mContext, this);
		mGestureDetector.setIsLongpressEnabled(false);

		// 设置Panel本身的属性
		LayoutParams lp = new LayoutParams(width, height);
		lp.leftMargin = -lp.width;
		mLeftMargin = Math.abs(lp.leftMargin);
		this.setLayoutParams(lp);
		initTextTypeFace();
	}

	private void initList() {
		lv_main_category = (ListView) findViewById(R.id.lv_main_category);

		itemMainCategories = CoreData.getInstance().getItemMainCategories();
		MainCategoryAdapter mAdapter = new MainCategoryAdapter();
		mAdapter.setmItemMainCategories(itemMainCategories);

		lv_main_category.setAdapter(mAdapter);

	//	setChecked(0,true);
		ItemDetailFragment.setListener(this);
		lv_main_category.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Log.d("onItemClick-------->", String.valueOf(arg2));
				isMoved=true;
					setChecked(arg2,true);
				if (selectItem == arg2){
					isPressed = true;
				}else {
					isPressed = false;
					selectItem = arg2;
				}

				if (!isPressed) {
					if (arg2 == 0) {
						((MainCategoryAdapter) lv_main_category.getAdapter()).setSelectItem(arg2);
						((MainCategoryAdapter) lv_main_category.getAdapter()).refresh();
						handler.sendMessage(handler.obtainMessage(
								MainPage.VIEW_EVENT_CLICK_ALL_MAIN_CATEGORY, null));
					}
					((MainCategoryAdapter) lv_main_category.getAdapter()).setSelectItem(arg2);
					((MainCategoryAdapter) lv_main_category.getAdapter()).refresh();
					ItemMainCategory itemMainCategory = (ItemMainCategory) arg0
							.getItemAtPosition(arg2);
					handler.sendMessage(handler.obtainMessage(
							MainPage.VIEW_EVENT_CLICK_MAIN_CATEGORY,
							itemMainCategory));
				}else if (isPressed){
					selectItem = -1 ;
					if (arg2 == 0) {
						((MainCategoryAdapter) lv_main_category.getAdapter()).setSelectItem(arg2);
						((MainCategoryAdapter) lv_main_category.getAdapter()).refresh();
						handler.sendMessage(handler.obtainMessage(
								MainPage.VIEW_EVENT_FIRST_COLLAPSE, null));
					}
					((MainCategoryAdapter) lv_main_category.getAdapter()).setSelectItem(arg2);
					((MainCategoryAdapter) lv_main_category.getAdapter()).refresh();
					ItemMainCategory itemMainCategory = (ItemMainCategory) arg0
							.getItemAtPosition(arg2);
					handler.sendMessage(handler.obtainMessage(
							MainPage.VIEW_EVENT_COLLAPSE,
							itemMainCategory));
				}
			}
		});
	}

	public View.OnTouchListener handlerTouchEvent = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			LayoutParams lp = (LayoutParams) SlidePanelView.this
					.getLayoutParams();
			if (lp.leftMargin < 0 && event.getAction() == MotionEvent.ACTION_DOWN) {
				handler.sendMessage(handler.obtainMessage(MainPage.VIEW_EVENT_SLIDE));
			}
			if (lp.leftMargin >= 0 && event.getAction() == MotionEvent.ACTION_DOWN) {
				handler.sendMessage(handler.obtainMessage(MainPage.VIEW_EVENT_SLIDE_CLICK));
			}
			if (event.getAction() == MotionEvent.ACTION_UP && // onScroll时的ACTION_UP
					mIsScrolling == true) {
				if (lp.leftMargin >= (-mLeftMargin / 2)) {// 往右超过一半
					handler.sendMessage(handler.obtainMessage(MainPage.VIEW_EVENT_SLIDE));
					new AsynMove().execute(new Integer[] { MOVE_WIDTH });// 正数展开
				} else if (lp.leftMargin < (-mLeftMargin / 2)) {
					handler.sendMessage(handler.obtainMessage(MainPage.VIEW_EVENT_SLIDE_CLICK));
					new AsynMove().execute(new Integer[] { -MOVE_WIDTH });// 负数收缩
				}
			}
			return mGestureDetector.onTouchEvent(event);
		}
	};

	/**
	 * 定义收缩时的回调函数
	 * 
	 * @param event
	 */
	public void setPanelClosedEvent(PanelClosedEvent event) {
		this.panelClosedEvent = event;
	}

	/**
	 * 定义展开时的回调函数
	 * 
	 * @param event
	 */
	public void setPanelOpenedEvent(PanelOpenedEvent event) {
		this.panelOpenedEvent = event;
	}

	/**
	 * 异步移动Panel
	 * 
	 * @author hellogv
	 */
	class AsynMove extends AsyncTask<Integer, Integer, Void> {

		@Override
		protected Void doInBackground(Integer... params) {
			int times;
			if (mLeftMargin % Math.abs(params[0]) == 0)// 整除
				times = mLeftMargin / Math.abs(params[0]);
			else
				// 有余数
				times = mLeftMargin / Math.abs(params[0]) + 1;

			for (int i = 0; i < times; i++) {
				publishProgress(params);
				try {
					Thread.sleep(Math.abs(params[0]));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... params) {
			LayoutParams lp = (LayoutParams) SlidePanelView.this
					.getLayoutParams();
			if (params[0] < 0)
				lp.leftMargin = Math.min((lp.leftMargin - params[0]), 0);
			else
				lp.leftMargin = Math.max((lp.leftMargin - params[0]),
						-mLeftMargin);

			if (lp.leftMargin == 0 && panelOpenedEvent != null) {// 展开之后
				panelOpenedEvent.onPanelOpened(SlidePanelView.this);// 调用OPEN回调函数
			} else if (lp.leftMargin == -(mLeftMargin)
					&& panelClosedEvent != null) {// 收缩之后
				panelClosedEvent.onPanelClosed(SlidePanelView.this);// 调用CLOSE回调函数
			}
			SlidePanelView.this.setLayoutParams(lp);
		}
	}

	@Override
	public boolean onDown(MotionEvent e) {
		mScrollX = 0;
		mIsScrolling = false;
		return false;
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		LayoutParams lp = (LayoutParams) SlidePanelView.this.getLayoutParams();
		if (lp.leftMargin < 0)// CLOSE的状态
		{
			new AsynMove().execute(new Integer[] { MOVE_WIDTH });// 正数展开
		} else if (lp.leftMargin >= 0)// OPEN的状态
		{
			new AsynMove().execute(new Integer[] { -MOVE_WIDTH });// 负数收缩
		}
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		mIsScrolling = true;
		mScrollX += distanceX;

		LayoutParams lp = (LayoutParams) SlidePanelView.this.getLayoutParams();
		if (lp.leftMargin > -(mLeftMargin) && mScrollX > 0) {// 往左拖拉
			lp.leftMargin = Math.max((lp.leftMargin - (int) mScrollX),
					-mLeftMargin);
			SlidePanelView.this.setLayoutParams(lp);
		} else if (lp.leftMargin < 0 && mScrollX < 0) {// 往右拖
			lp.leftMargin = Math.min((lp.leftMargin - (int) mScrollX), 0);
			SlidePanelView.this.setLayoutParams(lp);
		}

		if (lp.leftMargin == 0 && panelOpenedEvent != null) {// 展开之后
			panelOpenedEvent.onPanelOpened(SlidePanelView.this);// 调用OPEN回调函数
		} else if (lp.leftMargin == -(mLeftMargin) && panelClosedEvent != null) {// 收缩之后
			panelClosedEvent.onPanelClosed(SlidePanelView.this);// 调用CLOSE回调函数
		}
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
	}

	@Override
	public void onShowPress(MotionEvent e) {
	}
	public static void setCallBackMove( CallBackMove callBack) {
		callBackMove = callBack;
	}
	class MainCategoryAdapter extends BaseAdapter {
		private List<ItemMainCategory> mItemMainCategories = new ArrayList<ItemMainCategory>();
		private int selectItem = 0;
		
		public List<ItemMainCategory> getmItemMainCategories() {
			return mItemMainCategories;
		}

		public void setmItemMainCategories(List<ItemMainCategory> mItemMainCategories) {
			if (mItemMainCategories == null) {
				return;
			}else {
				this.mItemMainCategories.clear();
				//this.mItemMainCategories.add(null);
				this.mItemMainCategories.addAll(mItemMainCategories);
			}
		}

		public void setSelectItem(int selectItem) { 
			this.selectItem = selectItem;
		}
		
		public int getSelectItem(){
			return selectItem;
		}
		@Override
		public int getCount() {
			return mItemMainCategories.size();
		}

		@Override
		public Object getItem(int position) {
			return mItemMainCategories.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
		
		public void refresh() {
			notifyDataSetChanged();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_main_category, null);
				holder = new ViewHolder();
				holder.tv_text = (TextView) convertView.findViewById(R.id.tv_text);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
//			if (position == 0) {
//				holder.tv_text.setText(mContext.getResources().getString(R.string.all));
//			}else {
				holder.tv_text.setText(mItemMainCategories.get(position).getMainCategoryName());
		//	}
			textTypeFace.setTrajanProBlod(holder.tv_text);
			if (position == selectItem) {
				convertView.setBackgroundResource(R.color.bg_item_category);
			}else {
				convertView.setBackgroundResource(android.R.color.transparent);
			}
			return convertView;

		}

		class ViewHolder {
			public TextView tv_text;
		}
	}

	private void initTextTypeFace() {
		textTypeFace = TextTypeFace.getInstance();
	}

	private void setChecked(int position, boolean isLeft) {
		Log.d("setChecked-------->", String.valueOf(position));
		if (isLeft) {
			((MainCategoryAdapter) lv_main_category.getAdapter()).setSelectItem(position);
			((MainCategoryAdapter) lv_main_category.getAdapter()).refresh();
			//mSortAdapter.setCheckedPosition(position);
			//此处的位置需要根据每个分类的集合来进行计算
			List<ItemCategory>	itemCategorylist=  CoreData.getInstance().getItemCategoriesorDetail();
			List<ItemDetail> itemDetaillist =new ArrayList<ItemDetail>();
			int count = 0;
			for (int i = 0; i < position; i++) {

				for (int j = 0; j < itemCategorylist.size(); j++) {

					int itemMainid=itemMainCategories.get(i).getId();
					int categoryId=itemCategorylist.get(j).getItemMainCategoryId();
				if(itemMainid==categoryId){
					count++;
					itemDetaillist.clear();
					itemDetaillist= CoreData.getInstance().getItemDetails(itemCategorylist.get(j));
					for(int d=0;d<itemDetaillist.size();d++){
					count++;

					}
				}

				}

			}
			count += position;
//			ItemDetailFragment fragment=new ItemDetailFragment();
			Log.d("count-------->", String.valueOf(count));
//			fragment.setData(count);
			callBackMove.move(count);
			ItemHeaderDetailDecoration.setCurrentTag(String.valueOf(position));//凡是点击左边，将左边点击的位置作为当前的tag
		} else {


			if (isMoved) {
				isMoved = false;
			} else {
				((MainCategoryAdapter) lv_main_category.getAdapter()).setSelectItem(position);
				((MainCategoryAdapter) lv_main_category.getAdapter()).refresh();
			}
			ItemHeaderDecoration.setCurrentTag(String.valueOf(position));//如果是滑动右边联动左边，则按照右边传过来的位置作为tag

		}
	//	moveToCenter(position);

	}
}
