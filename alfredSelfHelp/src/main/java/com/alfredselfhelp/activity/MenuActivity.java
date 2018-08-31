package com.alfredselfhelp.activity;

import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
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
import com.alfredselfhelp.utils.CheckListener;
import com.alfredselfhelp.utils.ItemHeaderDecoration;

import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends BaseActivity {


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
    private CheckListener checkListener;


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
        ll_grab.setOnClickListener(this);


        // itemMainCategories = CoreData.getInstance().getItemMainCategories();
        itemMainCategories = ItemMainCategorySQL.getAllItemMainCategory();
        //  getAllItemMainCategory
        re_main_category = (RecyclerView) findViewById(R.id.re_main_category);
        mLinearLayoutManager = new LinearLayoutManager(context);
        mLinearLayoutManager.setOrientation(OrientationHelper.HORIZONTAL);
        re_main_category.setLayoutManager(mLinearLayoutManager);
//        DividerItemDecoration decoration = new DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL);
//        re_main_category.addItemDecoration(decoration);
        mainCategoryAdapter = new MainCategoryAdapter(context, itemMainCategories, new RvListener() {

            @Override
            public void onItemClick(int id, int position) {
                mainCategoryAdapter.setCheckedPosition(position);
                ll_grab.setBackgroundResource(R.drawable.main_btn_g);
                ll_menu_details.setVisibility(View.VISIBLE);
                ll_video.setVisibility(View.GONE);
                getItemDetail(itemMainCategories.get(position).getMainCategoryName(), itemMainCategories.get(position).getId().intValue());
            }

//            public void onItemClick(View v, int position) {
//                isMoved = true;
//                App.isleftMoved = true;
//                targetPosition = position;
//                setChecked(position, true);
//
//
//            }


        });
        re_main_category.setAdapter(mainCategoryAdapter);


        //   menuDetail();
    }

    private void menuDetail() {


        List<String> list = new ArrayList<>();
        //初始化左侧列表数据
        for (int i = 0; i < 10; i++) {
            list.add("aaaaa" + i);
        }

        mLinearLayoutManager = new LinearLayoutManager(context);
        mLinearLayoutManager.setOrientation(OrientationHelper.HORIZONTAL);
        re_menu_classify.setLayoutManager(mLinearLayoutManager);
        mClassAdapter = new ClassAdapter(context, list, new RvListener() {
            @Override
            public void onItemClick(int id, int position) {

            }
        });
        re_menu_classify.setAdapter(mClassAdapter);


        GridLayoutManager mManager = new GridLayoutManager(context, 3);
        //通过isTitle的标志来判断是否是title
        mManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return itemDetails.get(position).getViewType();
            }
        });
        re_menu_details.setLayoutManager(mManager);
        mDetailAdapter = new MenuDetailAdapter(context, itemDetails, new RvListener() {
            @Override
            public void onItemClick(int id, int position) {

            }
        });
        re_menu_details.setAdapter(mDetailAdapter);
        mDecoration = new ItemHeaderDecoration(context, itemDetails);
        re_menu_details.addItemDecoration(mDecoration);
        mDecoration.setCheckListener(checkListener);


    }


    private List<ItemDetail> getItemDetail(String mainCategoryName, int id
    ) {
        List<ItemDetail> itemDetaillist = new ArrayList<ItemDetail>();
        //itemDetaillist=CoreData.getInstance().getItemDetails();
        List<ItemDetail> itemDetailandCate = new ArrayList<ItemDetail>();
//		itemDetaillist=CoreData.getInstance().getItemDetails();
//		List<ItemCategory> list=new ArrayList<ItemCategory>();
        List<ItemCategory> itemCategorylist = ItemCategorySQL.getAllItemCategory();
        //    List<ItemMainCategory> itemMainCategorielist = CoreData.getInstance().getItemMainCategories();

//        for (int i = 0; i < itemMainCategorielist.size(); i++) {
//
//            ItemDetail detail = new ItemDetail();
//            detail.setItemCategoryName(itemMainCategorielist.get(i).getMainCategoryName());
//            // detail.setId(list.get(j).getId());
//            detail.setTag(String.valueOf(i));
//            detail.setViewType(1);
//            itemDetailandCate.add(detail);

        for (int j = 0; j < itemCategorylist.size(); j++) {
            ItemCategory itemCategory = itemCategorylist.get(j);
            int cid;
            cid = itemCategorylist.get(j).getItemMainCategoryId();
            if (id == cid) {
                ItemDetail itemCateDetail = new ItemDetail();
                itemCateDetail.setItemCategoryName(mainCategoryName);
                // detail.setId(list.get(j).getId());
                itemCateDetail.setItemName(itemCategory.getItemCategoryName());
                itemCateDetail.setTag(String.valueOf(j));
                itemCateDetail.setViewType(1);
                itemDetailandCate.add(itemCateDetail);
                itemDetaillist.clear();
              //  itemDetaillist = CoreData.getInstance().getItemDetails(itemCategory);
                itemDetaillist  = ItemDetailSQL.getAllItemDetail();
                    for (int d = 0; d < itemDetaillist.size(); d++) {
                        if (itemCateDetail.getItemCategoryId().intValue() == itemCategory
                                .getId().intValue()) {
                        itemDetaillist.get(d).setItemCategoryName(mainCategoryName);
                        itemDetaillist.get(d).setTag(String.valueOf(j));
                        itemDetaillist.get(d).setViewType(3);
                        itemDetailandCate.add(itemDetaillist.get(d));
                    }
                }
            }
        }


//		else {
//			ItemCategory=CoreData.getInstance()
//					.getItemCategories(itemMainCategory);
//		}
        return itemDetailandCate;
    }

    @Override
    protected void handlerClickEvent(View v) {
        super.handlerClickEvent(v);

        switch (v.getId()) {
            case R.id.ll_grab:
                ll_grab.setBackgroundResource(R.drawable.main_btn_b);
                ll_video.setVisibility(View.VISIBLE);

                break;
        }
    }
}
