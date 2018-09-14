package com.alfredselfhelp.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alfredbase.ParamConst;
import com.alfredbase.javabean.ItemDetail;
import com.alfredbase.javabean.Order;
import com.alfredbase.javabean.OrderDetail;
import com.alfredbase.utils.BH;
import com.alfredbase.utils.ScreenSizeUtil;
import com.alfredselfhelp.R;
import com.alfredselfhelp.global.App;
import com.alfredselfhelp.utils.KpmTextTypeFace;
import com.alfredselfhelp.view.CountViewMod;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;


public class MenuDetailAdapter extends RvAdapter<ItemDetail> {

//    CountViewMod.OnCountChange onCountChange;
    private List<OrderDetail> orderDetails = new ArrayList<>();
    private int currentGroupId;
    private Order currentOrder;
    private int WIDTH;

    private KpmTextTypeFace textTypeFace = KpmTextTypeFace.getInstance();

    public MenuDetailAdapter(Context context, List<ItemDetail> list, RvListener listener) {
        super(context, list, listener);
//        this.onCountChange = countViewMod;
        WIDTH = (int) (ScreenSizeUtil.width - ScreenSizeUtil.dip2px((Activity) context, 265));
    }


    public void setParams(Order currentOrder, List<OrderDetail> orderDetails, int currentGroupId) {
        this.currentOrder = currentOrder;
        this.orderDetails.clear();
        this.orderDetails.addAll(orderDetails);
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
        TextView de;
        private View mView;

        public DetailHolder(View itemView, int type, RvListener listener) {
            super(itemView, type, listener);
            this.mView = itemView;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, (WIDTH / 3) / 4 * 5);
            params.setMargins(0, 0, 0, 0);
            mView.setLayoutParams(params);

            tvName = (TextView) itemView.findViewById(R.id.tv_modifier_name);
            img = (ImageView) itemView.findViewById(R.id.img_modifier);
            tvPrice = (TextView) itemView.findViewById(R.id.tv_modifier_price);
            count_view = (CountViewMod) itemView.findViewById(R.id.img_modifier_add);
            //    re_modifier_num=(RelativeLayout)itemView.findViewById(R.id.re_modifier_num);
            num = (TextView) itemView.findViewById(R.id.tv_modifier_num);
            de = (TextView) itemView.findViewById(R.id.tv_modifier_de);


        }


        @Override
        public void bindHolder(ItemDetail itemDetail, final int position) {
            tvName.setText(itemDetail.getItemName());
            textTypeFace.setUbuntuBold(tvName);

            Glide.with(mContext)
                    .load(itemDetail.getImgUrl())
                    .placeholder(R.drawable.img_bg)
                    .error(R.drawable.img_bg)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(img);

            tvPrice.setText("S" + App.instance.getCurrencySymbol() + BH.getBD(itemDetail.getPrice()).toString());
            textTypeFace.setUbuntuBold(tvPrice);
            de.setText(itemDetail.getItemDesc());
            textTypeFace.setUbuntuRegular(de);

            count_view.setIsCanClick(getOrderDetailStatus(itemDetail));
            count_view.setInitCount(getItemNum(itemDetail));
            count_view.setTag(itemDetail);
            count_view.setParam(itemDetail);
            count_view.setEnabled(false);
//            count_view.setOnCountChange(onCountChange);

        }

        @Override
        public void bindHolderItem(ItemDetail itemDetail, int position) {


        }
    }


    private int getItemNum(ItemDetail itemDetail) {
        int itemNum = 0;
        if (orderDetails != null) {
            for (OrderDetail orderDetail : orderDetails) {
                if (orderDetail.getItemId().intValue() == itemDetail.getId()
                        .intValue()
                        ) {
                    itemNum += orderDetail.getItemNum();
                }
            }
        }
        return itemNum;
    }

    private boolean getOrderDetailStatus(ItemDetail itemDetail) {
        if (orderDetails != null) {
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
        }
        return true;
    }
}
