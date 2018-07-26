package com.alfredwaiter.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alfredbase.ParamConst;
import com.alfredbase.javabean.ItemDetail;
import com.alfredbase.javabean.Order;
import com.alfredbase.javabean.OrderDetail;
import com.alfredbase.utils.BH;
import com.alfredbase.utils.TextTypeFace;
import com.alfredwaiter.R;
import com.alfredwaiter.global.App;
import com.alfredwaiter.listener.RvItemClickListener;
import com.alfredwaiter.popupwindow.SetItemCountWindow;
import com.alfredwaiter.view.CountView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;


public class ItemDetailAdapter extends RvAdapter<ItemDetail> {
    private DisplayImageOptions options;
    private TextTypeFace textTypeFace = TextTypeFace.getInstance();
    private CountView.OnCountChange onCountChange;
    private SetItemCountWindow setItemCountWindow;
    private List<OrderDetail> orderDetails;
    private int currentGroupId;
    private Order currentOrder;
    public void setParams(Order currentOrder, List<OrderDetail> orderDetails, int currentGroupId) {
        this.currentOrder = currentOrder;
        this.orderDetails = orderDetails;
        this.currentGroupId = currentGroupId;
    }
    public ItemDetailAdapter(Context context, List<ItemDetail> list, SetItemCountWindow setItemCountWindow, RvItemClickListener listener, CountView.OnCountChange onCountChange) {

        super(context, list, listener);
        this.onCountChange = onCountChange;
        this.setItemCountWindow = setItemCountWindow;
        options = new DisplayImageOptions.Builder()
                .showImageOnFail(R.drawable.default_itemmenu)
                .showImageForEmptyUri(R.drawable.default_itemmenu)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }


    @Override
    protected int getLayoutId(int viewType) {
        if(viewType==1){
           return R.layout.kot_mian_item_listview;
        }else if (viewType==2){
           return R.layout.kot_notification_listview;
        }else {
         return R.layout.item_item_detail;

        }
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).getViewType();
       // return TextUtils.isEmpty(list.get(position).getItemName())==true ? 0 : 1;
    }

    @Override
    protected RvHolder getHolder(View view, int viewType) {
        return new ItemHolder(view, viewType, listener);
    }

    public class ItemHolder extends RvHolder<ItemDetail> {
        TextView tvCity;
        ImageView avatar;
        TextView tvTitle;
        TextView title;
        TextView tv_name;

        TextView tv_price;
        ImageView img_icon;
        CountView count_view;
        public ItemHolder(View itemView, int type, RvItemClickListener listener) {
            super(itemView, type, listener);
            switch (type) {
                case 1:
                    title=(TextView) itemView.findViewById(R.id.tv_main_title);
                    textTypeFace.setTrajanProBlod(title);
                    break;
                case 2:
                    tvTitle = (TextView) itemView.findViewById(R.id.title);
                    textTypeFace.setTrajanProBlod(tvTitle);
                    break;
                case 3:
                   tv_name = (TextView) itemView.findViewById(R.id.tv_name);
                   tv_price = (TextView) itemView.findViewById(R.id.tv_price);
                   img_icon = (ImageView) itemView.findViewById(R.id.img_icon);
                     count_view = (CountView) itemView
                            .findViewById(R.id.count_view);
                    break;
            }

        }

        @Override
        public void bindHolder(ItemDetail itemDetail, int position) {
            int itemViewType = ItemDetailAdapter.this.getItemViewType(position);
            switch (itemViewType) {
                case 1:
                    title.setText(itemDetail.getItemCategoryName());
                    break;
                case 2:
                    tvTitle.setText(itemDetail.getItemName()+"");
                  //  title.setTextColor(getResources().getColor(R.color.black));
                    break;
                case 3:
                    tv_name.setText(itemDetail.getItemName());
                    textTypeFace.setTrajanProRegular(tv_name);

                    tv_price.setText(App.instance.getCurrencySymbol() + BH.getBD(itemDetail.getPrice()).toString());
                    textTypeFace.setTrajanProRegular(tv_price);

                    String url = itemDetail.getImgUrl();
                    ImageLoader.getInstance().displayImage(url, img_icon, options);

                    count_view.setIsCanClick(getOrderDetailStatus(itemDetail));
                    count_view.setInitCount(getItemNum(itemDetail));
                    count_view.setTag(itemDetail);
                    count_view.setParam(itemDetail,setItemCountWindow);
                    count_view.setOnCountChange(onCountChange);
                    break;
            }

        }
    }


    private int getItemNum(ItemDetail itemDetail) {
        int itemNum = 0;
        for (OrderDetail orderDetail : orderDetails) {
            if (orderDetail.getItemId().intValue() == itemDetail.getId()
                    .intValue()
                    && orderDetail.getGroupId().intValue() == currentGroupId) {
                itemNum += orderDetail.getItemNum();
            }
        }
        return itemNum;
    }
    private boolean getOrderDetailStatus(ItemDetail itemDetail){
        for (OrderDetail orderDetail : orderDetails) {
            if (orderDetail.getItemId().intValue() == itemDetail.getId()
                    .intValue()
                   ) {
                if(orderDetail.getOrderDetailStatus() == ParamConst.ORDERDETAIL_STATUS_KOTPRINTERD){
                    return false;
                }else{
                    return true;
                }
            }
        }
        return true;
    }
}
