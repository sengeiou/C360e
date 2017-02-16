package com.alfredposclient.adapter;

import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.alfredbase.BaseActivity;
import com.alfredbase.javabean.ItemCategory;
import com.alfredbase.utils.TextTypeFace;
import com.alfredposclient.R;

import java.util.List;

/**
 * Created by Zun on 2017/2/15 0015.
 */

public class DiscountAdapter extends BaseAdapter {

    private BaseActivity activity;
    private List<ItemCategory> list;
    private SparseBooleanArray sparseBooleanArray = new SparseBooleanArray();

    public DiscountAdapter(BaseActivity activity, List<ItemCategory> list) {
        this.activity = activity;
        this.list = list;
        if (list != null && list.size() != 0){
            for (int i = 0; i < list.size(); i++){
                sparseBooleanArray.put(i, false);
            }
        }
    }

    private void getSelectedItem(){

    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null){
            convertView = LayoutInflater.from(activity).inflate(R.layout.discount_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.discount_name = (TextView) convertView.findViewById(R.id.discount_name);
            viewHolder.discount_select = (CheckBox) convertView.findViewById(R.id.discount_select);
            convertView.setTag(viewHolder);

        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
            if (list != null){
                if (list.size() != 0){
                    ItemCategory itemCategory = list.get(position);
                    TextTypeFace textTypeFace = TextTypeFace.getInstance();
                    textTypeFace.setTrajanProRegular(viewHolder.discount_name);
                    viewHolder.discount_name.setText(itemCategory.getItemCategoryName());
                    viewHolder.discount_select.setTag(position);
                    viewHolder.discount_select.setChecked(sparseBooleanArray.get(position));
                    viewHolder.discount_select.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            boolean boo = sparseBooleanArray.get(position);
                            sparseBooleanArray.put((Integer) buttonView.getTag(), !boo);
                        }
                    });
                }
            }

        return convertView;
    }

    class ViewHolder{
        TextView discount_name;
        CheckBox discount_select;
    }
}
