package com.alfredselfhelp.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alfredbase.utils.BH;
import com.alfredselfhelp.R;
import com.alfredselfhelp.javabean.ItemDetailDto;

import java.util.List;


public class NurDetailAdapter extends RvAdapter<ItemDetailDto> {

    public NurDetailAdapter(Context context, List<ItemDetailDto> list, RvListener listener) {
        super(context, list, listener);
    }


    @Override
    protected int getLayoutId(int viewType) {
        return R.layout.item_nur_detail;
    }

    @Override
    public int getItemViewType(int position) {
        return 3;
    }

    @Override
    protected RvHolder getHolder(View view, int viewType) {
        return new DetailHolder(view, viewType, listener);
    }

    public class DetailHolder extends RvHolder<ItemDetailDto> {
        TextView tvPrice;
        ImageView img, add;
        TextView tvName, num;

        RelativeLayout re_modifier_num;

        public DetailHolder(View itemView, int type, RvListener listener) {
            super(itemView, type, listener);
            tvName = (TextView) itemView.findViewById(R.id.tv_nur_name);
           // img = (ImageView) itemView.findViewById(R.id.img_modifier);
            tvPrice = (TextView) itemView.findViewById(R.id.tv_nur_price);
          //  add = (ImageView) itemView.findViewById(R.id.img_modifier_add);
            //    re_modifier_num=(RelativeLayout)itemView.findViewById(R.id.re_modifier_num);
        //    num = (TextView) itemView.findViewById(R.id.tv_modifier_num);


        }


        @Override
        public void bindHolder(ItemDetailDto itemDetail, final int position) {
            int itemViewType = NurDetailAdapter.this.getItemViewType(position);

            tvName.setText(itemDetail.getItemName());
//            Glide.with(mContext)
//                    .load(itemDetail.getImgUrl())
//                    .placeholder(R.drawable.logo_icon)
//                    .error(R.drawable.logo_icon)
//                    .diskCacheStrategy(DiskCacheStrategy.NONE)
//                    .into(img);

            tvPrice.setText("S$" + BH.getBD(itemDetail.getItemPrice()).toString());
//            add.setImageResource(R.drawable.icon_add);
//            add.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    notifyItemChanged(position, "aaaa");
//
//                }
//            });
//



        }

        @Override
        public void bindHolderItem(ItemDetailDto itemDetail, int position) {

//            add.setImageResource(R.drawable.mod_num);
//
//            num.setVisibility(View.VISIBLE);

        }
    }
}
