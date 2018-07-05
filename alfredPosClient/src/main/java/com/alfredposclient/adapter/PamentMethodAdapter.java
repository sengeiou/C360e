package com.alfredposclient.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alfredbase.BaseApplication;
import com.alfredbase.javabean.PamentMethod;
import com.alfredposclient.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;
import java.util.Map;

public class PamentMethodAdapter extends BaseAdapter{

    private Context context = null;
    private LayoutInflater inflater = null;

    private String t;
    private String pay;

    List<PamentMethod> list;
    private DisplayImageOptions options;
    public PamentMethodAdapter(Context context, List<PamentMethod> list) {

        this.context = context;
        this.list = list;
        this.inflater = LayoutInflater.from(context);

        options = new DisplayImageOptions.Builder()
                .showImageOnFail(R.drawable.default_itemmenu)
                .showImageForEmptyUri(R.drawable.default_itemmenu)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }
    public int getCount() {
        return this.list.size();
    }

    public Object getItem(int arg0) {
        return this.list.get(arg0);
    }

    public long getItemId(int arg0) {
        return arg0;
    }


    public View getView(int position, View view, ViewGroup arg2) {
        PamentMethodAdapter.ViewHolder holder = null;
        if (holder == null) {
            holder = new PamentMethodAdapter.ViewHolder();
            if (view == null) {
                view = this.inflater.inflate(R.layout.item_pament_method, (ViewGroup)null);
            }
         //   holder.img = (ImageView)view.findViewById(R.id.img_pament_meth);
            holder.tv = (TextView)view.findViewById(R.id.tv_payment_meth);
            holder.tax = (TextView)view.findViewById(R.id.tv_payment_tax);
            holder.admin = (TextView)view.findViewById(R.id.tv_payment_admin);
            holder.pay = (TextView)view.findViewById(R.id.tv_payment_pay);
            holder.symbol = (TextView)view.findViewById(R.id.tv_payment_symbol);
            holder.money = (TextView)view.findViewById(R.id.tv_payment_money);
            holder.img=(ImageView)view.findViewById(R.id.img_payment_left);

            view.setTag(holder);
        } else {
            holder = (PamentMethodAdapter.ViewHolder)view.getTag();
        }

            PamentMethod   p = list.get(position);

      //  ImageLoader.getInstance().displayImage(url, imageViewHolder.item_name_img, options);
            holder.tv.setText(p.getNameCh().toString());

           return view;
    }

    public class ViewHolder {
        public ImageView img ;
        public TextView tv ,tax,pay,admin,symbol,money;

    }
}
