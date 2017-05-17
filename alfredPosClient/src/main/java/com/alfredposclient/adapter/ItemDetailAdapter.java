package com.alfredposclient.adapter;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.StateListDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alfredbase.javabean.ItemDetail;
import com.alfredbase.store.Store;
import com.alfredbase.utils.ColorSelectedUtils;
import com.alfredbase.utils.CommonUtil;
import com.alfredbase.utils.IntegerUtils;
import com.alfredbase.utils.TextTypeFace;
import com.alfredposclient.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

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
		ImageViewHolder imageViewHolder = null;
		boolean isSet = Store.getBoolean(context, Store.SET_BACKGROUND, false);
		if (!isSet) {

			if (arg1 == null) {
				arg1 = inflater.inflate(R.layout.item_name_layout, null);
				holder = new ViewHolder();
				holder.tv_text = (TextView) arg1.findViewById(R.id.tv_item_name);
				textTypeFace.setTrajanProBlod(holder.tv_text);
				arg1.setTag(holder);
			} else {
				holder = (ViewHolder) arg1.getTag();
			}
			int color = Store.getInt(context, Store.COLOR_PICKER, 0);

			if (IntegerUtils.isEmptyOrZero(color)) {
				holder.tv_text.setTextColor(context.getResources().getColorStateList(R.color.text_color_selector));
				holder.tv_text.setBackgroundResource(R.drawable.box_menu_selector);
			} else {
				int[] rgb = CommonUtil.getRgb(color);
				int color1 = Color.rgb(Math.abs(rgb[0] - 255), Math.abs(rgb[1] - 255), Math.abs(rgb[2] - 255));
				holder.tv_text.setTextColor(ColorSelectedUtils.createColorStateList(color1, color));
				holder.tv_text.setBackgroundDrawable(ColorSelectedUtils.newSelector(color, color1));
			}
			holder.tv_text.setText(itemDetails.get(arg0).getItemName());
		}else {
			if (arg1 == null){
				arg1 = inflater.inflate(R.layout.item_name_image_layout, null);
				imageViewHolder = new ImageViewHolder();
				imageViewHolder.item_name_img = (ImageView) arg1.findViewById(R.id.item_name_img);
				imageViewHolder.item_name_tv = (TextView) arg1.findViewById(R.id.item_name_tv);
				textTypeFace.setTrajanProBlod(imageViewHolder.item_name_tv);
				arg1.setTag(imageViewHolder);
			}else {
				imageViewHolder = (ImageViewHolder) arg1.getTag();
			}
			imageViewHolder.item_name_tv.setText(itemDetails.get(arg0).getItemName());
			String url = itemDetails.get(arg0).getImgUrl();
			if (!TextUtils.isEmpty(url)){
				//显示图片的配置  
				DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.default_itemmenu)
				.showImageOnFail(R.drawable.default_itemmenu)
				.cacheInMemory(true)
				.cacheOnDisk(true)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.build();
				ImageLoader.getInstance().displayImage(url, imageViewHolder.item_name_img, options);
			}else {
				imageViewHolder.item_name_img.setBackgroundResource(R.drawable.default_itemmenu);
			}

		}
		return arg1;
	}

	class ViewHolder {
		public TextView tv_text;
	}

	class ImageViewHolder {
		public TextView item_name_tv;
		public ImageView item_name_img;
	}

}
