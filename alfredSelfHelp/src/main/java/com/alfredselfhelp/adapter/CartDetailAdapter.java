package com.alfredselfhelp.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alfredbase.javabean.OrderDetail;
import com.alfredbase.utils.BH;
import com.alfredselfhelp.R;
import com.alfredselfhelp.global.App;
import com.alfredselfhelp.popuwindow.SetItemCountWindow;
import com.alfredselfhelp.view.CountView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;


public class CartDetailAdapter extends RvAdapter<OrderDetail> {

    private CountView.OnCountChange onCountChange;
    private SetItemCountWindow setItemCountWindow;

    public CartDetailAdapter(Context context, List<OrderDetail> list, SetItemCountWindow setItemCountWindow, RvListener listener, CountView.OnCountChange onCountChange) {
        super(context, list, listener);

        this.setItemCountWindow = setItemCountWindow;
        this.onCountChange = onCountChange;
    }


    @Override
    protected int getLayoutId(int viewType) {
        return R.layout.item_cart_detail;
    }


    @Override
    protected RvHolder getHolder(View view, int viewType) {
        return new DetailHolder(view, viewType, listener);
    }

    public class DetailHolder extends RvHolder<OrderDetail> {
        TextView tvPrice;
        ImageView img, add;
        TextView tvName, num;

        RelativeLayout re_modifier_num;

        CountView count_view;

        public DetailHolder(View itemView, int type, RvListener listener) {
            super(itemView, type, listener);
            tvName = (TextView) itemView.findViewById(R.id.tv_modifier_name);
            img = (ImageView) itemView.findViewById(R.id.img_cart_modifier);
            tvPrice = (TextView) itemView.findViewById(R.id.tv_modifier_price);

            count_view = (CountView) itemView
                    .findViewById(R.id.count_view);

//            add = (ImageView) itemView.findViewById(R.id.img_modifier_add);
            //    re_modifier_num=(RelativeLayout)itemView.findViewById(R.id.re_modifier_num);
//            num = (TextView) itemView.findViewById(R.id.tv_modifier_num);


        }


        @Override
        public void bindHolder(OrderDetail orderDetail, final int position) {
            int itemViewType = CartDetailAdapter.this.getItemViewType(position);
            tvName.setText(orderDetail.getItemName());
            Glide.with(mContext)
                    .load(orderDetail.getItemUrl())
                    .placeholder(R.drawable.logo_icon)
                    .error(R.drawable.logo_icon)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(img);
            tvPrice.setText("S" + App.instance.getCurrencySymbol() + BH.getBD(orderDetail.getItemPrice()).toString());
            if(TextUtils.isEmpty(orderDetail.getBarCode())){
                count_view.setIsCanClick(true);
            }else{
                count_view.setIsCanClick(false);
            }
            count_view.setInitCount(orderDetail.getItemNum());
            count_view.setTag(orderDetail);
            count_view.setParam(orderDetail, setItemCountWindow);
            count_view.setOnCountChange(onCountChange);


//

        }

        @Override
        public void bindHolderItem(OrderDetail orderDetail, int position) {

        }


    }
}
