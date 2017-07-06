package com.alfredposclient.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alfredbase.BaseActivity;
import com.alfredbase.global.CoreData;
import com.alfredbase.javabean.ItemCategory;
import com.alfredbase.javabean.ItemDetail;
import com.alfredbase.javabean.ItemMainCategory;
import com.alfredbase.javabean.ItemModifier;
import com.alfredbase.javabean.Order;
import com.alfredbase.javabean.OrderDetail;
import com.alfredbase.utils.AnimatorListenerImpl;
import com.alfredbase.utils.BitmapUtil;
import com.alfredbase.utils.ButtonClickTimer;
import com.alfredbase.utils.ObjectFactory;
import com.alfredbase.utils.ScreenSizeUtil;
import com.alfredbase.utils.TextTypeFace;
import com.alfredposclient.R;
import com.alfredposclient.activity.MainPage;
import com.alfredposclient.adapter.ItemDetailAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainPageMenuView extends LinearLayout {
	private static final int WIDTH = (int) (ScreenSizeUtil.width*(1 - (700+300)/ScreenSizeUtil.WIDTH_POS));
	private static final int OPEN_DELAY = 300;
	private BaseActivity parent;
	private Handler handler;
	private GestureDetector detector;
	private RelativeLayout ll_menu;
	private HorizontalScrollViewEx oneLevelMenu;
	private HorizontalScrollViewEx twoLevelMenu;
	private HorizontalScrollViewEx threeLevelMenu;
	private ImageView iv_up;
	private ImageView iv_down;
	private ImageView iv_modifier_up;
	private ImageView iv_modifier_down;
	private ImageView iv_done;
	private LinearLayout ll_item_detail;
	private LinearLayout ll_sub_menu;
	private ScrollView sv_sub_menu;
	private ImageView iv_sub_menu_index;
	private Button btn_more_sub_menu;
	private int current_index = 0;
	private Order order;
	private TextTypeFace textTypeFace;
	private int height;
	private boolean flag;
	private ItemDetailAdapter itemAdp;
	
	private List<ItemMainCategory> listMainCategorys = CoreData.getInstance()
			.getItemMainCategories();
	
	public MainPageMenuView(Context context) {
		super(context);
		init(context);
		initTextTypeFace();
	}

	public MainPageMenuView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
		initTextTypeFace();
	}
	
	public void setParent(BaseActivity parent){
		this.parent = parent;
		initItemMainCategory();
		initItemDetail();
//		initItemCategory(current_index);
	}

	public void setParam(Order order, Handler handler) {
		this.handler = handler;
		this.order = order;
		listMainCategorys = CoreData.getInstance()
				.getItemMainCategories();
		// ll_menu.setVisibility(View.VISIBLE);
	}

	private void initTextTypeFace(){
		textTypeFace = TextTypeFace.getInstance();
		textTypeFace.setTrajanProBlod((TextView) findViewById(R.id.tv_item_name));
	}

	public void refreshItemOrderDetail(){
		itemAdp.notifyDataSetInvalidated();
	}
	
	private void initItemDetail() {
		List<View> itemDetailViews = new ArrayList<View>();
		LayoutInflater inflater = LayoutInflater.from(parent);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(WIDTH,
				LayoutParams.WRAP_CONTENT);
		List<ItemDetail> currentItemDetails = null;
		for (ItemMainCategory itemMainCategory : CoreData.getInstance()
				.getItemMainCategories()) {
			currentItemDetails = new ArrayList<ItemDetail>();
			for (ItemDetail itemDetail : CoreData.getInstance()
					.getItemDetails()) {
				if (itemDetail.getItemMainCategoryId().intValue() == itemMainCategory
						.getId().intValue()) {
					currentItemDetails.add(itemDetail);
				}
			}
			View view = inflater.inflate(R.layout.item_menu_detial, null);
			view.setLayoutParams(params);
			final GridView gv_menu_detail = (GridView) view
					.findViewById(R.id.gv_menu_detail);
			gv_menu_detail.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
				
				@SuppressLint("NewApi")
				@Override
				public void onGlobalLayout() {
					int numColumns = (int)Math.floor(gv_menu_detail.getWidth()/(gv_menu_detail.getVerticalSpacing() + ScreenSizeUtil.dip2px(parent, ItemDetailAdapter.ITEM_WIDTH_HEIGHT)));
					gv_menu_detail.setNumColumns(numColumns);
				}
			});
			itemAdp = new ItemDetailAdapter(parent,
					currentItemDetails);
			gv_menu_detail.setAdapter(itemAdp);
			
			// get the view tree observer of the grid and set the height and numcols dynamically
//			gv_menu_detail.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//				@Override
//				public void onGlobalLayout() {
//					if (itemAdp.getNumColumns() == 0) {
//						final int numColumns = (int) Math.floor(gv_menu_detail.getWidth() / (80 + 15));
//						if (numColumns > 0) {
//							final int columnWidth = (gv_menu_detail.getWidth() / numColumns) - 15;
//							itemAdp.setNumColumns(numColumns);
//							itemAdp.setItemHeight(columnWidth);
//						}
//					}
//				 
//				}
//			});
				
			gv_menu_detail.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					ItemDetail itemDetail = (ItemDetail) arg0
							.getItemAtPosition(arg2);
					OrderDetail orderDetail = ObjectFactory.getInstance()
							.getOrderDetail(order, itemDetail, 0);
					Message msg = handler.obtainMessage();
					msg.what = MainPage.VIEW_EVENT_ADD_ORDER_DETAIL;
					msg.obj = orderDetail;
					handler.sendMessage(msg);
				}
			});
			itemDetailViews.add(view);
		}

		LinearLayout ll_two_level_menu = (LinearLayout) findViewById(R.id.ll_two_level_menu);
		ll_two_level_menu.removeAllViews();
		for (View view : itemDetailViews) {
			ll_two_level_menu.addView(view);
		}
	}

	List<View> itemMainCategoryViews = new ArrayList<View>();
	private void initItemMainCategory() {
		TextView textView = new TextView(parent);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				WIDTH / 3, LayoutParams.MATCH_PARENT);
		textView.setLayoutParams(params);
//		textView.setBackgroundColor(getResources().getColor(
//				android.R.color.white));
//		textView.setBackgroundResource(R.drawable.box_left);
		itemMainCategoryViews.add(textView);
		//Category name is from 1...n
		int startindx = 0;
		for (ItemMainCategory itemMainCategory : CoreData.getInstance()
				.getItemMainCategories()) {
			textView = new TextView(parent);
			textView.setLayoutParams(params);
//			textView.setBackgroundColor(Color.parseColor("#00FFFFFF"));
//			textView.setBackgroundResource(R.drawable.box_center);
			textView.setGravity(Gravity.CENTER);
			textView.setText(itemMainCategory.getMainCategoryName());
			if (startindx ==1 )
				textView.setTextColor(Color.parseColor("#000000"));
			else
			   textView.setTextColor(Color.parseColor("#ffffff"));
			textTypeFace.setTrajanProBlod(textView);
			itemMainCategoryViews.add(textView);
			startindx ++;
		}
		textView = new TextView(parent);
		textView.setLayoutParams(params);
//		textView.setBackgroundColor(getResources().getColor(
//				android.R.color.darker_gray));
//		textView.setBackgroundResource(R.drawable.box_right);
		itemMainCategoryViews.add(textView);
        
		final LinearLayout ll_one_level_menu = (LinearLayout) findViewById(R.id.ll_one_level_menu);
		ll_one_level_menu.removeAllViews();
		for (int i = 0; i < itemMainCategoryViews.size(); i++) {
			ll_one_level_menu.addView(itemMainCategoryViews.get(i));
			if (i > 0 && i < itemMainCategoryViews.size() - 1) {
				final int index = i;
				itemMainCategoryViews.get(i).setOnClickListener(
						new OnClickListener() {
							@Override
							public void onClick(View v) {
								if(!ButtonClickTimer.canClick(v)){
									return;
								}
								//
								if (index-1 == current_index) {//选中点击
									if (!flag) {//点击切换
										openSubMenu();
										flag = true;
									}else {
										closeSubMenu(listMainCategorys.get(current_index).getId());
									}
									
								}else {
									scrollToIndex(oneLevelMenu, index - 1);
									scrollToIndex(twoLevelMenu, index - 1);
									List<ItemMainCategory> listMainCategorys = CoreData.getInstance()
											.getItemMainCategories();
									closeSubMenu(listMainCategorys.get(current_index).getId());
								}
							}
						});
			}
		}
	}
    private void hightlightMainCategoryLabel(int index) {
    	Resources res = this.getResources();
    	TextView currentLabel =  (TextView) itemMainCategoryViews.get(index+1);
    	for (View txtview: itemMainCategoryViews) {
    		if (txtview != currentLabel) {
    			((TextView) txtview).setTextColor(res.getColor(R.color.black));
    		}else {
    			((TextView) txtview).setTextColor(res.getColor(R.color.white));
    		}
    	}
    }
//	private void initItemCategory(int index) {
//		final List<View> oneLevelMenus = new ArrayList<View>();
//		Button button;
//		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//				(int) getResources().getDimension(R.dimen.dp60),
//				(int) getResources().getDimension(R.dimen.dp60));
//		List<ItemMainCategory> listMainCategorys = CoreData.getInstance()
//				.getItemMainCategories();
//		
//		ItemMainCategory itemMainCategory = null;
//		if (listMainCategorys==null) return;
//		itemMainCategory = listMainCategorys.get(index);
//		List<ItemCategory> currentItemCategories = new ArrayList<ItemCategory>();
//		
//		for (ItemCategory itemCategory : CoreData.getInstance()
//				.getItemCategories()) {
//			if (itemCategory.getItemMainCategoryId().intValue() == itemMainCategory
//					.getId().intValue()) {
//				currentItemCategories.add(itemCategory);
//			}
//		}
//		//show all items in main category
//		filterItemsInSubCategory(itemMainCategory.getId());
//		
//		for (ItemCategory itemCategory : currentItemCategories) {
//			button = new Button(parent);
//			button.setLayoutParams(params);
//			button.setGravity(Gravity.CENTER);
//			button.setText(itemCategory.getItemCategoryName());
//			button.setTag(itemCategory);
//			//button.setTag(itemCategory.getId());
//			button.setTextColor(Color.parseColor("#000000"));
//			oneLevelMenus.add(button);
//			
//			//Filter item in subcategory
//			button.setOnClickListener(new OnClickListener(){
//
//				@Override
//				public void onClick(View arg0) {

//					Button self = (Button)arg0;
//					ItemCategory subcate = (ItemCategory) self.getTag();
//					
//					filterItemsInSubCategory(subcate.getItemMainCategoryId(), subcate.getId());
//					Map<String, Object> paramters = new HashMap<String, Object>();
//					paramters.put("width", WIDTH);
//					paramters.put("itemCategory", getItemCategory(current_index));
//					handler.sendMessage(handler.obtainMessage(111111111,paramters));
//				}});
//		}

//		LinearLayout ll_three_level_menu = (LinearLayout) findViewById(R.id.ll_three_level_menu);
//
//		ll_three_level_menu.removeAllViews();
//
//		for (View view : oneLevelMenus) {
//			ll_three_level_menu.addView(view);
//		}
//		ll_three_level_menu.setVisibility(View.GONE);
//		btn_more_sub_menu = (Button) findViewById(R.id.btn_more_sub_menu);
//		btn_more_sub_menu.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				if (!ButtonClickTimer.canClick()) {
//					return;
//				}
//				LinearLayout ll_three_level_menu = (LinearLayout) findViewById(R.id.ll_three_level_menu);
//				if (ll_three_level_menu.getVisibility() == View.GONE) {
//					ll_three_level_menu.setVisibility(View.VISIBLE);
//					opensubMenuDetail(ll_three_level_menu, oneLevelMenus.size());
//				} else {
//					closesubMenuDetail(ll_three_level_menu,
//							oneLevelMenus.size());
//				}
//			}
//		});
//	}
	
	private List<ItemCategory> getItemCategory(int index){
    	List<ItemMainCategory> listMainCategorys = CoreData.getInstance()
				.getItemMainCategories();
    	ItemMainCategory itemMainCategory = null;
		if (listMainCategorys==null){
			return null;
		}
		itemMainCategory = listMainCategorys.get(index);
		final List<ItemCategory> currentItemCategories = new ArrayList<ItemCategory>();
		
		for (ItemCategory itemCategory : CoreData.getInstance()
				.getItemCategories()) {
			if (itemCategory.getItemMainCategoryId().intValue() == itemMainCategory
					.getId().intValue()) {
				currentItemCategories.add(itemCategory);
			}
		}
		return currentItemCategories;
    }
	
	private void filterItemsInSubCategory (int maincategoryid) {
		LinearLayout ll_two_level_menu = (LinearLayout) findViewById(R.id.ll_two_level_menu);
		View view = ll_two_level_menu.getChildAt(current_index);
		GridView gv_menu_detail = (GridView)view.findViewById(R.id.gv_menu_detail);	
		ItemDetailAdapter subadp = (ItemDetailAdapter) gv_menu_detail.getAdapter();

		List<ItemDetail>  currentItemDetails = new ArrayList<ItemDetail>();
		for (ItemDetail itemDetail : CoreData.getInstance()
				.getItemDetails()) {
			if ((itemDetail.getItemMainCategoryId().intValue() == maincategoryid)) {
				currentItemDetails.add(itemDetail);
			}
		}
		subadp.setItemDetails(currentItemDetails);
	}   
	public void filterItemsInSubCategory (int maincategoryid, int subcategoryid) {
		LinearLayout ll_two_level_menu = (LinearLayout) findViewById(R.id.ll_two_level_menu);
		View view = ll_two_level_menu.getChildAt(current_index);
		GridView gv_menu_detail = (GridView)view.findViewById(R.id.gv_menu_detail);	
		ItemDetailAdapter subadp = (ItemDetailAdapter) gv_menu_detail.getAdapter();
		
		List<ItemDetail>  currentItemDetails = new ArrayList<ItemDetail>();
		for (ItemDetail itemDetail : CoreData.getInstance()
				.getItemDetails()) {
			if ((itemDetail.getItemMainCategoryId().intValue() == maincategoryid) 
					&& (itemDetail.getItemCategoryId().intValue() == subcategoryid)) {
				currentItemDetails.add(itemDetail);
			}
		}

		subadp.setItemDetails(currentItemDetails);		
	}
	
	private void init(Context context) {
		View.inflate(context, R.layout.main_page_menu_view, this);
		ll_menu = (RelativeLayout) findViewById(R.id.rl_menu);
		iv_done = (ImageView) findViewById(R.id.iv_done);
		iv_done.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!ButtonClickTimer.canClick(v)){
					return;
				}
				closeModifiers();
			}
		});
		ll_item_detail = (LinearLayout) findViewById(R.id.ll_item_detail);
		ll_item_detail.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
			@Override
			public void onGlobalLayout() {
				height = ll_item_detail.getMeasuredHeight();
			}
		});
		sv_sub_menu = (ScrollView) findViewById(R.id.sv_sub_menu);
		iv_sub_menu_index = (ImageView) findViewById(R.id.iv_sub_menu_index);
		ll_sub_menu = (LinearLayout) findViewById(R.id.ll_sub_menu);
		iv_up = (ImageView) findViewById(R.id.iv_up);
		iv_down = (ImageView) findViewById(R.id.iv_down);
		iv_modifier_up = (ImageView) findViewById(R.id.iv_up);
		iv_modifier_down = (ImageView) findViewById(R.id.iv_down);
		detector = new GestureDetector(context, new CustomGestureListener(
				new OnFling() {
					@Override
					public void fling(int direction) {
						if (direction == OnFling.FLING_TO_LEFT) {
							scrollToIndex(oneLevelMenu,
									getIndex(oneLevelMenu) + 1);
							scrollToIndex(twoLevelMenu,
									getIndex(twoLevelMenu) + 1);
						} else {
							scrollToIndex(oneLevelMenu,
									getIndex(oneLevelMenu) - 1);
							scrollToIndex(twoLevelMenu,
									getIndex(twoLevelMenu) - 1);
						}
					}
				}));
		oneLevelMenu = (HorizontalScrollViewEx) findViewById(R.id.hsv_one_level_menu);
		oneLevelMenu.itemW = WIDTH / 3;
		twoLevelMenu = (HorizontalScrollViewEx) findViewById(R.id.hsv_two_level_menu);
		twoLevelMenu.itemW = WIDTH;
//		threeLevelMenu = (HorizontalScrollViewEx) findViewById(R.id.hsv_three_level_menu);
//		threeLevelMenu.itemW = (int) getResources().getDimension(R.dimen.dp60);
		oneLevelMenu.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				boolean handled = detector.onTouchEvent(event);
				if (!handled) {
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						break;
					case MotionEvent.ACTION_MOVE:
						twoLevelMenu.scrollTo(
								(int) (twoLevelMenu.getX() + oneLevelMenu
										.getScrollX() * 3), (int) twoLevelMenu
										.getY());
						break;
					case MotionEvent.ACTION_UP:
						scrollToIndex(oneLevelMenu, getIndex(oneLevelMenu));
						scrollToIndex(twoLevelMenu, getIndex(oneLevelMenu));
						break;
					default:
						break;
					}
				}
				return handled;
			}
		});

		twoLevelMenu.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				boolean handled = detector.onTouchEvent(event);
				if (!handled) {
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						break;
					case MotionEvent.ACTION_MOVE:
						oneLevelMenu.scrollTo(
								(int) (oneLevelMenu.getX() + twoLevelMenu
										.getScrollX() / 3), (int) oneLevelMenu
										.getY());
						break;
					case MotionEvent.ACTION_UP:
						scrollToIndex(oneLevelMenu, getIndex(twoLevelMenu));
						scrollToIndex(twoLevelMenu, getIndex(twoLevelMenu));
						break;

					default:
						break;
					}
				}
				return handled;
			}
		});

	}

	public void opensubMenuDetail(final View view, int number) {
		if (AnimatorListenerImpl.isRunning) {
			return;
		}
		ObjectAnimator animator1 = ObjectAnimator.ofFloat(threeLevelMenu,
				"translationX", -number * threeLevelMenu.itemW, 0f);
		animator1.setDuration(300);
		AnimatorSet animSet = new AnimatorSet();
		animSet.play(animator1);
		animSet.addListener(new AnimatorListenerImpl() {
			@Override
			public void onAnimationStart(Animator animation) {
				super.onAnimationStart(animation);
			}
		});
		animSet.start();
	}

	public void closesubMenuDetail(final View view, int number) {
		if (AnimatorListenerImpl.isRunning) {
			return;
		}
		TranslateAnimation trans = new TranslateAnimation(0f, -number
				* threeLevelMenu.itemW, 0f, 0f);
		trans.setFillAfter(false);
		trans.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				view.setVisibility(View.GONE);
			}
		});
		threeLevelMenu.setAnimation(trans);
		threeLevelMenu.startAnimation(trans);
	}

	private void initMenuDetail(Order order, OrderDetail orderDetail,
			List<ItemModifier> itemModifiers) {
		ll_item_detail.removeAllViews();
		for (ItemModifier itemModifier : itemModifiers) {
			ModifierView modifierView = new ModifierView(parent);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
//			params.rightMargin = 10;
			modifierView.setLayoutParams(params);
			if (itemModifiers.indexOf(itemModifier) % 2 == 1) {//判断为奇数时，背景色改变
				modifierView.setBackgroundResource(R.color.modifier_odd);
			}
			modifierView.setParams(order, orderDetail, itemModifier, handler,height);
			ll_item_detail.addView(modifierView);
		}
	}

	private void initSubMenu(List<ItemCategory> itemCategories){
		ll_sub_menu.removeAllViews();
		SubMenuView subMenuView = new SubMenuView(parent);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT,LayoutParams.MATCH_PARENT);
		params.topMargin = 20;
		subMenuView.setLayoutParams(params);
		subMenuView.setParams(WIDTH, itemCategories, handler);
		ll_sub_menu.addView(subMenuView);
	}
	
	public void openSubMenu() {
		initSubMenu(getItemCategory(current_index));
		sv_sub_menu.setVisibility(View.VISIBLE);
		iv_sub_menu_index.setVisibility(View.VISIBLE);
		if (AnimatorListenerImpl.isRunning) {
			return;
		}
	}
	public void closeSubMenu(int maincategoryid,int subcategoryid){
		filterItemsInSubCategory(maincategoryid, subcategoryid);
		if (AnimatorListenerImpl.isRunning) {
			return;
		}
		sv_sub_menu.setVisibility(View.GONE);
		iv_sub_menu_index.setVisibility(View.GONE);
	}
	public void closeSubMenu(int maincategoryid){
		flag = false;
		filterItemsInSubCategory(maincategoryid);
		if (AnimatorListenerImpl.isRunning ) {
			return;
		}
		sv_sub_menu.setVisibility(View.GONE);
		iv_sub_menu_index.setVisibility(View.GONE);
	}
	
	public void openModifiers(Order order, OrderDetail orderDetail,
			List<ItemModifier> itemModifiers) {
		initMenuDetail(order, orderDetail, itemModifiers);
		if (AnimatorListenerImpl.isRunning) {
			return;
		}
		iv_up.setVisibility(View.VISIBLE);
		iv_down.setVisibility(View.VISIBLE);
		Bitmap bitmap = BitmapUtil.convertViewToBitmap(ll_menu);
		iv_up.setImageBitmap(Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight() / 2));
		iv_down.setImageBitmap(Bitmap.createBitmap(bitmap, 0,
				bitmap.getHeight() / 2, bitmap.getWidth(),
				bitmap.getHeight() / 2));
		
		if (!bitmap.isRecycled())
			bitmap.recycle();

		ll_menu.setVisibility(View.GONE);
		((TextView) findViewById(R.id.tv_item_name)).setText(CoreData.getInstance().getItemDetailById(orderDetail.getItemId()).getItemName());
		ObjectAnimator animator1 = ObjectAnimator.ofFloat(iv_up, "y",
				iv_up.getY(), iv_up.getY() - iv_up.getHeight()).setDuration(
				OPEN_DELAY);
		ObjectAnimator animator2 = ObjectAnimator.ofFloat(iv_down, "y",
				iv_down.getY(), iv_down.getY() + iv_down.getHeight())
				.setDuration(OPEN_DELAY);
		final ObjectAnimator animator3 = ObjectAnimator.ofFloat(iv_up, "y",
				iv_up.getY() - iv_up.getHeight(), iv_up.getY()).setDuration(
				1);
		final ObjectAnimator animator4 = ObjectAnimator.ofFloat(iv_down, "y",
				iv_down.getY() + iv_down.getHeight(), iv_down.getY())
				.setDuration(1);
		AnimatorSet animSet = new AnimatorSet();
		animSet.playTogether(animator1, animator2);
		animSet.addListener(new AnimatorListenerImpl(){
			public void onAnimationEnd(Animator animation) {
				super.onAnimationEnd(animation);
				ll_menu.post(new Runnable() {

					@Override
					public void run() {
						System.out.println("==========donghua");
						AnimatorSet animSet1 = new AnimatorSet();
						animSet1.playTogether(animator3, animator4);
						animSet1.start();
						iv_up.setVisibility(View.GONE);
						iv_down.setVisibility(View.GONE);
						
					}
				});
			};
		});
		animSet.start();
		
	}

	public boolean isModifierOpen(){
		if(ll_menu != null && ll_menu.getVisibility() != View.VISIBLE)
			return true;
		else
			return false;
	}

	public void closeModifiers() {
		if (AnimatorListenerImpl.isRunning || ll_menu.getVisibility() == View.VISIBLE) {
			return;
		}
		iv_up.setVisibility(View.VISIBLE);
		iv_down.setVisibility(View.VISIBLE);
		ObjectAnimator animator3 = ObjectAnimator.ofFloat(iv_up, "y",
				iv_up.getY(), iv_up.getY() - iv_up.getHeight()).setDuration(
				0);
		ObjectAnimator animator4 = ObjectAnimator.ofFloat(iv_down, "y",
				iv_down.getY(), iv_down.getY() + iv_down.getHeight())
				.setDuration(0);
		
		AnimatorSet animSet = new AnimatorSet();
		animSet.playTogether(animator3, animator4);
		animSet.addListener(new AnimatorListenerImpl() {
			public void onAnimationEnd(Animator animation) {
				AnimatorSet animSet = new AnimatorSet();
				ObjectAnimator animator1 = ObjectAnimator.ofFloat(iv_up, "y",
						iv_up.getY(), iv_up.getY() + iv_up.getHeight()).setDuration(
						OPEN_DELAY);
				ObjectAnimator animator2 = ObjectAnimator.ofFloat(iv_down, "y",
						iv_down.getY(), iv_down.getY() - iv_down.getHeight())
						.setDuration(OPEN_DELAY);
				animSet.playTogether(animator1, animator2);
				animSet.start();
				animSet.addListener(new AnimatorListenerImpl(){
					@Override
					public void onAnimationEnd(Animator animation) {
						super.onAnimationEnd(animation);
						ll_menu.post(new Runnable() {

							@Override
							public void run() {
								ll_menu.setVisibility(View.VISIBLE);
							}
						});
					}
				});
			};
		});
		animSet.start();
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return detector.onTouchEvent(ev);
	}

	private int getIndex(HorizontalScrollViewEx hsv) {
		return (int) Math.rint(((double) hsv.getScrollX()) / hsv.itemW);
	}

	/**
	 * 
	 * @param hsv
	 * @param index
//	 * @param itemWidth
	 */
	private void scrollToIndex(final HorizontalScrollViewEx hsv, final int index) {
		if (index < 0
				|| index >= CoreData.getInstance().getItemMainCategories()
						.size())
			return;
		hsv.post(new Runnable() {
			@Override
			public void run() {
				hsv.smoothScrollTo((int) (hsv.getX() + index * hsv.itemW),
						(int) hsv.getY());
				hightlightMainCategoryLabel(index);
				if (hsv.getId() == R.id.hsv_one_level_menu) {
					closeSubMenu(listMainCategorys.get(current_index).getId());
//					initItemCategory(current_index);
//					bgChang(itemMainCategoryViews, index+1);
		}
			}
		});
		if (hsv.getId() == R.id.hsv_two_level_menu) {
			current_index = index;
//			postDelayed(new Runnable() {
//
//				@Override
//				public void run() {
////					initItemCategory(current_index);
//				}
//			}, 200);
		}
		
	}

	public void bgChang(List<View> views,int index){
//		for (int i = 0; i < views.size(); i++) {
//			views.get(i).setBackgroundResource(R.drawable.box_center);
//			if (views.size() >= index && i == index) {
//				views.get(i).setBackgroundResource(R.drawable.box_center_checked);
//			}
//		}
		if (views.size() >= index + 1 && index != 0){
				View view = views.get(index);
				view.setBackgroundResource(R.drawable.box_center_checked);
				views.get(index-1).setBackgroundResource(R.drawable.box_center);
				views.get(index+1).setBackgroundResource(R.drawable.box_center);
		}
	}
	
	private class CustomGestureListener extends SimpleOnGestureListener {
		private static final float FLING_MIN_DISTANCE = 50;
		private static final float FLING_MIN_VELOCITY = 200;

		private OnFling onFling;

		public CustomGestureListener(OnFling onFling) {
			this.onFling = onFling;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			if (e1 == null || e2 == null) {
				return false;
			}
			if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE
					&& Math.abs(velocityX) > FLING_MIN_VELOCITY) {
				if (onFling != null) {
					onFling.fling(OnFling.FLING_TO_LEFT);
				}
				return true;
			} else if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE
					&& Math.abs(velocityX) > FLING_MIN_VELOCITY) {
				if (onFling != null) {
					onFling.fling(OnFling.FLING_TO_RIGHT);
				}
				return true;
			}
			return false;
		}
	}

	public interface OnFling {
		public static final int FLING_TO_LEFT = -1;
		public static final int FLING_TO_RIGHT = 1;

		void fling(int direction);
	}
}
