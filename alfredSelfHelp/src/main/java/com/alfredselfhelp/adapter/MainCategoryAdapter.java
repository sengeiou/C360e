package com.alfredselfhelp.adapter;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alfredbase.javabean.ItemMainCategory;
import com.alfredbase.utils.ScreenSizeUtil;
import com.alfredbase.utils.TextTypeFace;
import com.alfredselfhelp.R;
import java.util.List;

public class MainCategoryAdapter extends RvAdapter<ItemMainCategory> {

    private int checkedPosition=-1;
    private TextTypeFace textTypeFace = TextTypeFace.getInstance();
    private Context mContext;
    int WIDTH;
    public void setCheckedPosition(int checkedPosition) {
        this.checkedPosition = checkedPosition;
        notifyDataSetChanged();
    }

    public MainCategoryAdapter(Context context, List<ItemMainCategory> itemMainCategories, RvListener listener) {
        super(context, itemMainCategories, listener);
        this.mContext=context;

        WIDTH= (int) (ScreenSizeUtil.width-ScreenSizeUtil.dip2px((Activity) context, 120));
    }

    @Override
    protected int getLayoutId(int viewType) {
        return R.layout.item_main_category;
    }

    @Override
    protected RvHolder getHolder(View view, int viewType) {
        return new CateHolder(view, viewType, listener);
    }

    private class CateHolder extends RvHolder<ItemMainCategory> {

        private TextView tv_text;
        private View mView;

       CateHolder(View itemView, int type, RvListener listener) {
            super(itemView, type, listener);
            this.mView = itemView;
            tv_text = (TextView) itemView.findViewById(R.id.tv_main_text);
           LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                   WIDTH / 3, ScreenSizeUtil.dip2px((Activity) mContext, 130));
           params.setMargins(5,0,5,0);
           tv_text.setLayoutParams(params);

        }

        @Override
        public void bindHolder(ItemMainCategory item, int position) {
            tv_text.setText(item.getMainCategoryName());
            textTypeFace.setTrajanProRegular(tv_text);

            if (position == checkedPosition) {
                tv_text.setBackgroundResource(R.drawable.main_btn_b);

            } else {
                tv_text.setBackgroundResource(R.drawable.btn_main_g);

            }
        }

        @Override
        public void bindHolderItem(ItemMainCategory itemMainCategory, int position) {

        }

    }
}
