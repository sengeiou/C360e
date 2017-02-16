package com.alfredposclient.adapter;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.alfredbase.javabean.ItemDetail;
import com.alfredbase.store.Store;
import com.alfredbase.utils.TextTypeFace;
import com.alfredposclient.R;

public class ItemDetailAdapter extends BaseAdapter {
	private Context context;
	private List<ItemDetail> itemDetails;

	public static final int ITEM_WIDTH_HEIGHT = 100;
	private TextTypeFace textTypeFace = TextTypeFace.getInstance();
	private LayoutInflater inflater;
	public ItemDetailAdapter(Context context, List<ItemDetail> itemDetails) {
		this.context = context;
		inflater = LayoutInflater.from(context);
		if (itemDetails == null)
			itemDetails = Collections.emptyList();
		this.itemDetails = itemDetails;

	}
	
	public void setItemDetails(List<ItemDetail> itemDetails) {
		this.itemDetails = itemDetails;
		this.notifyDataSetChanged();
	}
	
    public void filter(int subCategoryId) {
    	Iterator<ItemDetail> iter = itemDetails.iterator();

    	while (iter.hasNext()) {
    		ItemDetail item = iter.next();
    	    if (item.getItemCategoryId() != subCategoryId)
    	        iter.remove();
    	}    	
    	this.notifyDataSetChanged();
    }
	@Override
	public int getCount() {
		return itemDetails.size();
	}

	@Override
	public Object getItem(int arg0) {
		return itemDetails.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
//		TextView textView = new TextView(context);
//		ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(ITEM_WIDTH_HEIGHT, ITEM_WIDTH_HEIGHT);
//		lp.setMargins(0, 1000, 0, 0);
//		AbsListView.LayoutParams params = new AbsListView.LayoutParams(lp);
////		params.
//		textView.setLayoutParams(params);
////		textView.setPadding(0, 10, 0, 0);
//		textView.setBackgroundResource(R.drawable.box_menu);
//		textView.setGravity(Gravity.CENTER);
//		textView.setTextColor(Color.parseColor("#000000"));
//		textView.setText(itemDetails.get(arg0).getItemName());
//		textTypeFace.setTrajanProRegular(textView);
//		return textView;
//	}
		ViewHolder holder = null;
		if (arg1 == null) {
			arg1 = inflater.inflate(R.layout.item_name_layout, null);
			holder = new ViewHolder();
			holder.tv_text = (TextView) arg1.findViewById(R.id.tv_item_name);
			textTypeFace.setTrajanProBlod(holder.tv_text);
			arg1.setTag(holder);
		} else {
			holder = (ViewHolder) arg1.getTag();
		}
		int color = Store.getInt(context, Store.COLOR_PICKER, Color.WHITE);
		holder.tv_text.setBackgroundColor(color);
		holder.tv_text.setText(itemDetails.get(arg0).getItemName());
		return arg1;
	}

	class ViewHolder {
		public TextView tv_text;
	}

}
