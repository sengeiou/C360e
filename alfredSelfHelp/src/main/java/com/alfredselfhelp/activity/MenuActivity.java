package com.alfredselfhelp.activity;

import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextClock;

import com.alfredbase.BaseActivity;
import com.alfredbase.global.CoreData;
import com.alfredbase.javabean.ItemCategory;
import com.alfredbase.javabean.ItemDetail;
import com.alfredbase.javabean.ItemMainCategory;
import com.alfredbase.store.sql.ItemCategorySQL;
import com.alfredbase.store.sql.ItemDetailSQL;
import com.alfredbase.store.sql.ItemMainCategorySQL;
import com.alfredselfhelp.R;
import com.alfredselfhelp.adapter.ClassAdapter;
import com.alfredselfhelp.adapter.MainCategoryAdapter;
import com.alfredselfhelp.adapter.MenuDetailAdapter;
import com.alfredselfhelp.adapter.RvListener;
import com.alfredselfhelp.global.App;
import com.alfredselfhelp.utils.CheckListener;
import com.alfredselfhelp.utils.ItemHeaderDecoration;

import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends BaseActivity implements CheckListener {


    private RecyclerView re_main_category;
    private List<ItemMainCategory> itemMainCategories;
    private LinearLayoutManager mLinearLayoutManager;
    MainCategoryAdapter mainCategoryAdapter;
    private LinearLayout ll_grab, ll_menu_details, ll_video;
    private RecyclerView re_menu_classify, re_menu_details;

    private ClassAdapter mClassAdapter;

    private MenuDetailAdapter mDetailAdapter;


    ItemHeaderDecoration mDecoration;

    List<ItemDetail> itemDetails = new ArrayList<ItemDetail>();

    List<ItemCategory> itemCategorys = new ArrayList<ItemCategory>();
    private CheckListener checkListener;

    private Boolean isMoved = false;
    private int targetPosition;
    private int mIndex;
    private GridLayoutManager mManager;
    private Boolean move = false;

    protected void initView() {
        super.initView();
        setContentView(R.layout.activity_menu);
        init();
    }

    private void init() {
        ll_grab = (LinearLayout) findViewById(R.id.ll_grab);
        ll_video = (LinearLayout) findViewById(R.id.ll_video);
        ll_menu_details = (LinearLayout) findViewById(R.id.ll_menu_details);
        re_menu_classify = (RecyclerView) findViewById(R.id.re_menu_classify);
        re_menu_details = (RecyclerView) findViewById(R.id.re_menu_details);
        re_main_category = (RecyclerView) findViewById(R.id.re_main_category);
        ll_video.setVisibility(View.VISIBLE);
//        ll_menu_details.setVisibility(View.VISIBLE);
//        ll_video.setVisibility(View.GONE);
        ll_grab.setOnClickListener(this);


        // itemMainCategories = CoreData.getInstance().getItemMainCategories();
        itemMainCategories = ItemMainCategorySQL.getAllItemMainCategory();
        //

        menuDetail();
        re_main_category = (RecyclerView) findViewById(R.id.re_main_category);
        mLinearLayoutManager = new LinearLayoutManager(context);
        mLinearLayoutManager.setOrientation(OrientationHelper.HORIZONTAL);
        re_main_category.setLayoutManager(mLinearLayoutManager);
//        DividerItemDecoration decoration = new DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL);
//        re_main_category.addItemDecoration(decoration);
        mainCategoryAdapter = new MainCategoryAdapter(context, itemMainCategories, new RvListener() {

            @Override
            public void onItemClick(int id, int position) {

                ItemMainCategory itemMainCategory = itemMainCategories.get(position);
                mainCategoryAdapter.setCheckedPosition(position);
                ll_grab.setBackgroundResource(R.drawable.main_btn_g);
                ll_menu_details.setVisibility(View.VISIBLE);
                ll_video.setVisibility(View.GONE);

                getItemCategory(itemMainCategory.getId());
                getItemDetail(itemMainCategory.getMainCategoryName(), itemMainCategory.getId().intValue());


            }
        });
        re_main_category.setAdapter(mainCategoryAdapter);

    }


    private void menuDetail() {

        mLinearLayoutManager = new LinearLayoutManager(context);
        mLinearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        re_menu_classify.setLayoutManager(mLinearLayoutManager);
        mClassAdapter = new ClassAdapter(context, itemCategorys, new RvListener() {
            @Override
            public void onItemClick(int id, int position) {
                isMoved = true;
                App.isleftMoved = true;
                targetPosition = position;
                setChecked(position, true, 0);

            }
        });

        re_menu_classify.setAdapter(mClassAdapter);


        // mDetailAdapter.notifyDataSetChanged();

//        for (int i = 0; i < 10; i++) {
//
//            if (i == 0) {
//                ItemDetail itemDetail = new ItemDetail();
//                itemDetail.setViewType(1);
//                itemDetails.add(itemDetail);
//            }
//            ItemDetail itemDetail = new ItemDetail();
//            itemDetail.setViewType(3);
//            itemDetails.add(itemDetail);
//        }
//
//        GridLayoutManager mManager = new GridLayoutManager(context, 3);
//        //通过isTitle的标志来判断是否是title
////        mManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
////            @Override
////            public int getSpanSize(int position) {
////                return  1;
////            }
////        });1
//        re_menu_details.setLayoutManager(mManager);
//        mDetailAdapter = new MenuDetailAdapter(context, itemDetails, new RvListener() {
//            @Override
//            public void onItemClick(int id, int position) {
//
//            }
//        });
//       re_menu_details.setAdapter(mDetailAdapter);
//        mDecoration = new ItemHeaderDecoration(context, itemDetails);
//        re_menu_details.addItemDecoration(mDecoration);
        // mDecoration.setCheckListener(checkListener);
        //  itemDetails = getItemDetail("aaaaaa",1);
    }


    private List<ItemCategory> getItemCategory(int id) {

        List<ItemCategory> itemCategorielist = new ArrayList<ItemCategory>();

        List<ItemCategory> itemCategorylist = ItemCategorySQL.getAllItemCategory();
        itemCategorys.clear();
        for (int i = 0; i < itemCategorylist.size(); i++) {
            ItemCategory itemCategory = itemCategorylist.get(i);
            int cid;
            cid = itemCategorylist.get(i).getItemMainCategoryId();
            if (id == cid) {
//
                itemCategorys.add(itemCategory);

            }
        }

        mClassAdapter.notifyDataSetChanged();
        return itemCategorielist;

    }

    private List<ItemDetail> getItemDetail(String mainCategoryName, int id
    ) {

        re_menu_details.addOnScrollListener(new RecyclerViewListener());
        mManager = new GridLayoutManager(context, 3);
        //通过isTitle的标志来判断是否是title
//        mManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//            @Override
//            public int getSpanSize(int position) {
//                return  1;
//            }
//        });1
        re_menu_details.setLayoutManager(mManager);
        mDetailAdapter = new MenuDetailAdapter(context, itemDetails, new RvListener() {
            @Override
            public void onItemClick(int id, int position) {

            }
        });
        re_menu_details.setAdapter(mDetailAdapter);
        mDecoration = new ItemHeaderDecoration(context, itemDetails);
        re_menu_details.addItemDecoration(mDecoration);
        mDecoration.setCheckListener(this);
        List<ItemDetail> itemDetaillist = new ArrayList<ItemDetail>();
        //itemDetaillist=CoreData.getInstance().getItemDetails();
        List<ItemDetail> itemDetailandCate = new ArrayList<ItemDetail>();
//		itemDetaillist=CoreData.getInstance().getItemDetails();
//		List<ItemCategory> list=new ArrayList<ItemCategory>();
        List<ItemCategory> itemCategorylist = ItemCategorySQL.getAllItemCategory();
        int tag = 0;

        for (int j = 0; j < itemCategorylist.size(); j++) {
            ItemCategory itemCategory = itemCategorylist.get(j);
            int cid;
            cid = itemCategorylist.get(j).getItemMainCategoryId();
            if (id == cid) {
//                ItemDetail itemCateDetail = new ItemDetail();
//                itemCateDetail.setItemCategoryName(mainCategoryName);
//                // detail.setId(list.get(j).getId());
//                itemCateDetail.setItemName(itemCategory.getItemCategoryName());
//                itemCateDetail.setTag(String.valueOf(j));
//                itemCateDetail.setViewType(1);
//                itemDetailandCate.add(itemCateDetail);
                itemDetaillist.clear();
                //  itemDetaillist = CoreData.getInstance().getItemDetails(itemCategory);
                itemDetaillist = CoreData.getInstance().getItemDetails(itemCategorylist.get(j));
                // itemDetaillist  = ItemDetailSQL.getAllItemDetail();
                for (int d = 0; d < itemDetaillist.size(); d++) {
                    itemDetaillist.get(d).setItemCategoryName(mainCategoryName);
                    itemDetaillist.get(d).setTag(String.valueOf(tag));
                    itemDetaillist.get(d).setViewType(3);
                    itemDetails.add(itemDetaillist.get(d));
                    // }
                }

                tag++;
            }
        }


//		else {
//			ItemCategory=CoreData.getInstance()
//					.getItemCategories(itemMainCategory);
//		}

        mDetailAdapter.notifyDataSetChanged();
        mDecoration.setData(itemDetails);
        return itemDetailandCate;
    }

    @Override
    protected void handlerClickEvent(View v) {
        super.handlerClickEvent(v);

        switch (v.getId()) {
            case R.id.ll_grab:
                mainCategoryAdapter.setCheckedPosition(-1);
                ll_grab.setBackgroundResource(R.drawable.main_btn_b);
                ll_menu_details.setVisibility(View.GONE);
                ll_video.setVisibility(View.VISIBLE);

                break;
        }
    }


    private void setChecked(int position, boolean isLeft, int id) {
        Log.d("setChecked-------->", String.valueOf(position));
        if (isLeft) {
            mClassAdapter.setCheckedPosition(position);
            //mSortAdapter.setCheckedPosition(position);
            //此处的位置需要根据每个分类的集合来进行计算
            Log.d("1111111-------->", String.valueOf(position));
            List<ItemDetail> itemDetaillist = new ArrayList<ItemDetail>();
            int count = 0;
            List<ItemCategory> itemCategorylist = ItemCategorySQL.getAllItemCategory();
            int tag = 0;


            for (int i = 0; i < position; i++) {
                itemDetaillist.clear();
                //  itemDetaillist = CoreData.getInstance().getItemDetails(itemCategory);
                itemDetaillist = CoreData.getInstance().getItemDetails(itemCategorys.get(i));
                // itemDetaillist  = ItemDetailSQL.getAllItemDetail();
                for (int d = 0; d < itemDetaillist.size(); d++) {
                    count++;
                    // }
                }
            }

//
            count += position;
            Log.d("count-------->", String.valueOf(count));
            move(count);
            mDecoration.setCurrentTag(String.valueOf(targetPosition));//凡是点击左边，将左边点击的位置作为当前的tag
        } else {

            if (isMoved) {
                isMoved = false;
            } else {
                if (!App.isleftMoved) {
                    mClassAdapter.setCheckedPosition(position);
                }

            }
            ItemHeaderDecoration.setCurrentTag(String.valueOf(position));//如果是滑动右边联动左边，则按照右边传过来的位置作为tag


        }
        if (!App.isleftMoved) {
            moveToCenter(position);
        }

    }

    private void moveToCenter(int position) {
        //将点击的position转换为当前屏幕上可见的item的位置以便于计算距离顶部的高度，从而进行移动居中
        View childAt = re_main_category.getChildAt(position - mLinearLayoutManager.findFirstVisibleItemPosition());
        if (childAt != null) {
            int y = (childAt.getTop() - re_main_category.getHeight() / 2);
            re_main_category.smoothScrollBy(0, y);
        }

    }

    @Override
    public void check(int position, boolean isScroll) {

        setChecked(position, isScroll, 0);
    }

    public void move(int n) {
        mIndex = n;
        re_menu_details.stopScroll();
        smoothMoveToPosition(n);
    }


    private void smoothMoveToPosition(int n) {
        int firstItem = mManager.findFirstVisibleItemPosition();
        int lastItem = mManager.findLastVisibleItemPosition();
        Log.d("first--->", String.valueOf(firstItem));
        Log.d("last--->", String.valueOf(lastItem));
        if (n <= firstItem) {
            re_menu_details.smoothScrollToPosition(n);
            // ((LinearLayoutManager)mRv.getLayoutManager()).scrollToPositionWithOffset(n,0);
        } else if (n <= lastItem) {
            Log.d("pos---->", String.valueOf(n) + "VS" + firstItem);
            int top = re_menu_details.getChildAt(n - firstItem).getTop();
            Log.d("top---->", String.valueOf(top));
            re_menu_details.scrollBy(0, top);
        } else {
            //((LinearLayoutManager)mRv.getLayoutManager()).scrollToPositionWithOffset(n,0);
            re_menu_details.smoothScrollToPosition(n);
            move = true;
        }
    }


    private class RecyclerViewListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);

            if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                Log.d("SCROLL--->", "拖拽中");
                App.isleftMoved = false;
            }
            if (move && newState == RecyclerView.SCROLL_STATE_IDLE) {
                App.isleftMoved = false;
                move = false;
                int n = mIndex - mManager.findFirstVisibleItemPosition();
                Log.d("n---->", String.valueOf(n));
                if (0 <= n && n < re_menu_details.getChildCount()) {
                    int top = re_menu_details.getChildAt(n).getTop();
                    Log.d("top---Changed>", String.valueOf(top));
                    re_menu_details.smoothScrollBy(0, top);
                }
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

//            if (move) {
//                move = false;
//                int n = mIndex - mManager.findFirstVisibleItemPosition();
//                if (0 <= n && n < mRv.getChildCount()) {
//                    int top = mRv.getChildAt(n).getTop();
//                    mRv.scrollBy(0, top);
//                }
//            }
        }
    }
}

