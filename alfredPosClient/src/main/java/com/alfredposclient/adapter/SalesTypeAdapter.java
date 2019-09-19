package com.alfredposclient.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.alfredbase.javabean.RestaurantConfig;
import com.alfredbase.utils.TextTypeFace;
import com.alfredposclient.R;

import java.util.List;

/**
 * Created by Arif S. on 2019-09-19
 */
public class SalesTypeAdapter extends BaseAdapter {
    private Context context;
    private List<RestaurantConfig> data;
    private LayoutInflater inflater;
    private TextTypeFace textTypeFace = TextTypeFace.getInstance();

    public SalesTypeAdapter(Context context, List<RestaurantConfig> data) {
        this.context = context;
        this.data = data;

        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public RestaurantConfig getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.sales_type_item, null);
            holder = new ViewHolder();
            holder.tvTitle = (TextView) view.findViewById(R.id.title);
            textTypeFace.setTrajanProBlod(holder.tvTitle);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        RestaurantConfig item = data.get(i);
        holder.tvTitle.setText(item.getParaValue1());

        return view;
    }

    public class ViewHolder {
        public TextView tvTitle;
    }
}
