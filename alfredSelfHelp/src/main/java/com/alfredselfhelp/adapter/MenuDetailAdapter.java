package com.alfredselfhelp.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alfredbase.javabean.ItemDetail;
import com.alfredbase.utils.BH;
import com.alfredselfhelp.R;
import com.alfredselfhelp.activity.MenuActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;


public class MenuDetailAdapter extends RvAdapter<ItemDetail> {

    public MenuDetailAdapter(Context context, List<ItemDetail> list, RvListener listener) {
        super(context, list, listener);
    }


    @Override
    protected int getLayoutId(int viewType) {
        return viewType == 1 ? R.layout.item_title : R.layout.item_menu_detail;
    }

    @Override
    public int getItemViewType(int position) {
        return  3;
    }

    @Override
    protected RvHolder getHolder(View view, int viewType) {
        return new DetailHolder(view, viewType, listener);
    }

    public class DetailHolder extends RvHolder<ItemDetail> {
        TextView tvPrice;
        ImageView img;
        TextView tvName;

        public DetailHolder(View itemView, int type, RvListener listener) {
            super(itemView, type, listener);


                   tvName = (TextView) itemView.findViewById(R.id.tv_modifier_name);
                   img=(ImageView)itemView.findViewById(R.id.img_modifier);
                    tvPrice=(TextView)itemView.findViewById(R.id.tv_modifier_price);




        }

        @Override
        public void bindHolder(ItemDetail itemDetail, int position) {
            int itemViewType = MenuDetailAdapter.this.getItemViewType(position);

            tvName.setText(itemDetail.getItemName());
            Glide.with(mContext)
                    .load(itemDetail.getImgUrl())
                    .placeholder(R.drawable.logo_icon)
                    .error(R.drawable.logo_icon)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(img);

            tvPrice.setText("S$"+ BH.getBD(itemDetail.getPrice()).toString());
//

        }
    }
}
