package com.alfredselfhelp.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alfredbase.javabean.ItemDetail;
import com.alfredselfhelp.R;
import com.alfredselfhelp.activity.MenuActivity;

import java.util.List;


public class MenuDetailAdapter extends RvAdapter<ItemDetail> {

    public MenuDetailAdapter(Context context, List<ItemDetail> list, RvListener listener) {
        super(context, list, listener);
    }


    @Override
    protected int getLayoutId(int viewType) {
        return viewType == 0 ? R.layout.item_title : R.layout.item_menu_detail;
    }

    @Override
    public int getItemViewType(int position) {
        return  1;
    }

    @Override
    protected RvHolder getHolder(View view, int viewType) {
        return new DetailHolder(view, viewType, listener);
    }

    public class DetailHolder extends RvHolder<ItemDetail> {
        TextView tvCity;
        ImageView avatar;
        TextView tvTitle;

        public DetailHolder(View itemView, int type, RvListener listener) {
            super(itemView, type, listener);
            switch (type) {
                case 0:
                    tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
                    break;
                case 1:
                    tvCity = (TextView) itemView.findViewById(R.id.tvCity);
                    avatar = (ImageView) itemView.findViewById(R.id.ivAvatar);
                    break;
            }

        }

        @Override
        public void bindHolder(ItemDetail temDetail, int position) {
            int itemViewType = MenuDetailAdapter.this.getItemViewType(position);
            switch (itemViewType) {
                case 0:
                  //  tvTitle.setText(sortBean.getName());
                    break;
                case 1:
                 //   tvCity.setText(sortBean.getName());
                    break;
            }

        }
    }
}
