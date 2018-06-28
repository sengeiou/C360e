package com.alfredposclient.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alfredbase.BaseApplication;
import com.alfredbase.javabean.PamentMethod;
import com.alfredposclient.R;

import java.util.List;
import java.util.Map;

public class PamentMethodAdapter extends BaseAdapter{

    private Context context = null;
    private LayoutInflater inflater = null;

    private String t;
    private String pay;

    List<PamentMethod> list;

    public PamentMethodAdapter(Context context, List<PamentMethod> list) {

        this.context = context;
        this.list = list;
        this.inflater = LayoutInflater.from(context);
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
            holder.tv = (TextView)view.findViewById(R.id.tv_pament_meth);
            view.setTag(holder);
        } else {
            holder = (PamentMethodAdapter.ViewHolder)view.getTag();
        }

            PamentMethod   p = list.get(position);
//            holder.img.setBackgroundResource((Integer)map.get("img"));
            holder.tv.setText(p.getNameCh().toString());
           return view;
    }

    public class ViewHolder {
        public ImageView img ;
        public TextView tv ;


    }
}
