package com.alfredselfhelp.activity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.alfredbase.BaseActivity;
import com.alfredbase.global.CoreData;
import com.alfredbase.javabean.ItemMainCategory;
import com.alfredbase.store.sql.ItemMainCategorySQL;
import com.alfredselfhelp.R;
import com.alfredselfhelp.adapter.MainCategoryAdapter;
import com.alfredselfhelp.adapter.RvListener;

import java.util.List;

public class MenuActivity extends BaseActivity {


    private RecyclerView   re_main_category;
    private List<ItemMainCategory> itemMainCategories;
    private LinearLayoutManager mLinearLayoutManager;
    MainCategoryAdapter mainCategoryAdapter;
    protected void initView() {
        super.initView();
        setContentView(R.layout.activity_menu);
        init();
    }

    private void init() {
        re_main_category=(RecyclerView)findViewById(R.id.re_main_category);
       // itemMainCategories = CoreData.getInstance().getItemMainCategories();
       itemMainCategories= ItemMainCategorySQL.getAllItemMainCategory();
      //  getAllItemMainCategory
        re_main_category=(RecyclerView)findViewById(R.id.re_main_category);
        mLinearLayoutManager = new LinearLayoutManager(context);
        mLinearLayoutManager.setOrientation(OrientationHelper. HORIZONTAL);
        re_main_category.setLayoutManager(mLinearLayoutManager);
//        DividerItemDecoration decoration = new DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL);
//        re_main_category.addItemDecoration(decoration);
        mainCategoryAdapter = new MainCategoryAdapter(context, itemMainCategories, new RvListener() {

            @Override
            public void onItemClick(int id, int position) {
                mainCategoryAdapter.setCheckedPosition(position);

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
    }
}
