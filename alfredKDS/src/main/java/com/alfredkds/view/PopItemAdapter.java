package com.alfredkds.view;

import android.content.Context;
import android.graphics.Paint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.alfredbase.ParamConst;
import com.alfredbase.utils.TextTypeFace;
import com.alfredkds.R;
import com.alfredkds.javabean.Kot;

public class PopItemAdapter extends BaseAdapter{

	private Context mContext;
	private LayoutInflater inflater;
	public Kot kot;
	private TextTypeFace textTypeFace;
	
	public PopItemAdapter() {
		
	}
	
	public PopItemAdapter(Context context){
		this.mContext = context;
		inflater = LayoutInflater.from(context);
		initTextTypeFace();
	}

	
	public Kot getKot() {
		return kot;
	}

	public void setKot(Kot kot) {
		this.kot = kot;
	}

	@Override
	public int getCount() {
		return kot.getKotItemDetails().size();
	}

	@Override
	public Object getItem(int position) {
		return kot.getKotItemDetails().get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_in_kot, null);
			holder = new ViewHolder();
			holder.itemNum = (TextView) convertView.findViewById(R.id.tv_order_num);
			holder.itemName = (TextView) convertView.findViewById(R.id.tv_text);
			holder.modifiers = (TextView) convertView.findViewById(R.id.tv_dish_introduce);
			holder.blackLine = convertView.findViewById(R.id.black_line);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
			holder.itemNum.setText(kot.getKotItemDetails().get(position).getUnFinishQty()+"");
			holder.itemName.setText(kot.getKotItemDetails().get(position).getItemName());
			if (kot.getKotItemDetails().get(position).getKotStatus()==ParamConst.KOT_STATUS_DONE) {
				convertView.setBackgroundResource(R.color.bg_complete_item);
				holder.itemName.getPaint().setFlags(0);
			}else if (kot.getKotItemDetails().get(position).getKotStatus()==ParamConst.KOT_STATUS_UPDATE) {
				convertView.setBackgroundResource(R.color.bg_update_item);
				holder.itemName.getPaint().setFlags(0);
			}else if (kot.getKotItemDetails().get(position).getKotStatus()==ParamConst.KOT_STATUS_VOID) {
				convertView.setBackgroundResource(R.color.white);
				holder.itemName.setPaintFlags(holder.itemName.getPaintFlags()|Paint.STRIKE_THRU_TEXT_FLAG);
			}else {
				convertView.setBackgroundResource(R.color.white);
				holder.itemName.getPaint().setFlags(0);
			}
			/*---kotModifier显示---*/
			StringBuffer sBuffer = new StringBuffer();;
			for (int j = 0; j < kot.getKotItemModifiers().size(); j++) {
				if (kot.getKotItemModifiers().get(j) != null && !kot.getKotItemModifiers().equals("")&&
						kot.getKotItemDetails().get(position).getId() == kot.getKotItemModifiers().get(j).getKotItemDetailId()) {
					sBuffer.append(kot.getKotItemModifiers().get(j).getModifierName()+";");
				}
			}
			/* show special instructions */
		    if (!TextUtils.isEmpty(kot.getKotItemDetails().get(position).getSpecialInstractions())) {
					sBuffer.append(kot.getKotItemDetails().get(position).getSpecialInstractions()
							+ "");
			}			
			if (sBuffer !=null && !sBuffer.equals("")) {
				holder.modifiers.setText(sBuffer+"");
			}else {
				holder.modifiers.setText("");
			}
			textTypeFace.setTrajanProBlod(holder.itemName);
			textTypeFace.setTrajanProRegular(holder.itemNum);
			textTypeFace.setTrajanProRegular(holder.modifiers);
		return convertView;
	}
	
	class ViewHolder{
		public TextView itemNum;
		public TextView itemName;
		public View view;
		public TextView modifiers;
		public View blackLine;
	}
	
	private void initTextTypeFace() {
		textTypeFace = TextTypeFace.getInstance();
	}
}
