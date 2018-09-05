package com.alfredselfhelp.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alfredbase.ParamConst;
import com.alfredbase.javabean.ItemDetail;
import com.alfredbase.javabean.Order;
import com.alfredbase.javabean.OrderDetail;
import com.alfredbase.utils.BH;
import com.alfredselfhelp.R;
import com.alfredselfhelp.activity.MenuActivity;
import com.alfredselfhelp.view.CountView;
import com.alfredselfhelp.view.CountViewMod;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;


public class MenuDetailAdapter extends RvAdapter<ItemDetail> {

    CountView.OnCountChange onCountChange;
    private List<OrderDetail> orderDetails;
    private int currentGroupId;
    private Order currentOrder;
    public MenuDetailAdapter(Context context, List<ItemDetail> list, RvListener listener, CountViewMod.OnCountChange countViewMod) {
        super(context, list, listener);
        this.onCountChange = (CountView.OnCountChange) countViewMod;
    }


    public void setParams(Order currentOrder, List<OrderDetail> orderDetails, int currentGroupId) {
        this.currentOrder = currentOrder;
        this.orderDetails = orderDetails;
        this.currentGroupId = currentGroupId;
    }
    @Override
    protected int getLayoutId(int viewType) {
        return R.layout.item_menu_detail;
    }

    @Override
    public int getItemViewType(int position) {
        return 3;
    }

    @Override
    protected RvHolder getHolder(View view, int viewType) {
        return new DetailHolder(view, viewType, listener);
    }

    public class DetailHolder extends RvHolder<ItemDetail> {
        TextView tvPrice;
        ImageView img;
        TextView tvName, num;
         CountViewMod count_view;
        RelativeLayout re_modifier_num;

        public DetailHolder(View itemView, int type, RvListener listener) {
            super(itemView, type, listener);
            tvName = (TextView) itemView.findViewById(R.id.tv_modifier_name);
            img = (ImageView) itemView.findViewById(R.id.img_modifier);
            tvPrice = (TextView) itemView.findViewById(R.id.tv_modifier_price);
            count_view = (CountViewMod) itemView.findViewById(R.id.img_modifier_add);
            //    re_modifier_num=(RelativeLayout)itemView.findViewById(R.id.re_modifier_num);
            num = (TextView) itemView.findViewById(R.id.tv_modifier_num);


        }


        @Override
        public void bindHolder(ItemDetail itemDetail, final int position) {
            int itemViewType = MenuDetailAdapter.this.getItemViewType(position);

            tvName.setText(itemDetail.getItemName());
            Glide.with(mContext)
                    .load(itemDetail.getImgUrl())
                    .placeholder(R.drawable.logo_icon)
                    .error(R.drawable.logo_icon)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(img);

            tvPrice.setText("S$" + BH.getBD(itemDetail.getPrice()).toString());

   count_view.setIsCanClick(getOrderDetailStatus(itemDetail));
                    count_view.setInitCount(getItemNum(itemDetail));
                    count_view.setTag(itemDetail);
                  //  count_view.setParam(itemDetail,setItemCountWindow);
                    count_view.setOnCountChange((CountViewMod.OnCountChange) onCountChange);

        }

        @Override
        public void bindHolderItem(ItemDetail itemDetail, int position) {



        }
    }


    private int getItemNum(ItemDetail itemDetail) {
        int itemNum = 0;
        for (OrderDetail orderDetail : orderDetails) {
            if (orderDetail.getItemId().intValue() == itemDetail.getId()
                    .intValue()
                    ) {
                itemNum += orderDetail.getItemNum();
            }
        }
        return itemNum;
    }

    private boolean getOrderDetailStatus(ItemDetail itemDetail) {
        for (OrderDetail orderDetail : orderDetails) {
            if (orderDetail.getItemId().intValue() == itemDetail.getId()
                    .intValue()
                    ) {
                if (orderDetail.getOrderDetailStatus() == ParamConst.ORDERDETAIL_STATUS_KOTPRINTERD) {
                    return false;
                } else {
                    return true;
                }
            }
        }
        return true;
    }
}
