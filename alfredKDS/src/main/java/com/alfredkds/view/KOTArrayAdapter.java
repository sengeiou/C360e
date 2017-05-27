package com.alfredkds.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.alfredkds.R;
import com.alfredkds.javabean.Kot;

public class KOTArrayAdapter extends BaseAdapter {

	private Context mContext;
	private List<Kot> kots = new ArrayList<Kot>();
	private boolean addFirstItem = false;
	private Handler handler;
	private Map<Integer, Long> times = new HashMap<Integer, Long>();

	public KOTArrayAdapter(Context mContext, Handler handler) {
		super();
		times.clear();
		this.mContext = mContext;
		this.handler = handler;
	}

	public void setAddFirstItem(boolean addFirstItem) {
		this.addFirstItem = addFirstItem;
	}

	@Override
	public int getCount() {
		return kots.size();
	}

	@Override
	public Object getItem(int position) {
		return kots.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
//		KOTView kotView;
		if (convertView == null) {
			convertView = View.inflate(mContext,R.layout.kot_array_view, null);
			holder = new ViewHolder();
			holder.kotView = (KOTView) convertView.findViewById(R.id.kotview);
			holder.kotView.setParams(mContext, handler);
			convertView.setTag(holder);
//			kotView = new KOTView(mContext, mHandler);
//			convertView = kotView;
//			convertView.setTag(kotView);
		} else {
			holder = (ViewHolder) convertView.getTag();
//			kotView = (KOTView) convertView.getTag();
		}
		holder.kotView.setData(kots.get(position));
		if (times.containsKey(position)){
			Long tm = times.get(position);
			tm = holder.kotView.getTime();
		}else {
			times.put(position, holder.kotView.getTime());
		}
		if (addFirstItem && position == 0) {
			holder.kotView.showNewKOT();
			addFirstItem = false;
		}
		return convertView;
	}
	
	class ViewHolder{
		public KOTView kotView;
	}

	/**
	 * @return the kots
	 */
	public List<Kot> getKots() {
		return kots;
	}

	/**
	 * @param kots
	 *            the kots to set
	 */
	public void setKots(List<Kot> kots) {
		this.kots = kots;
	}

}