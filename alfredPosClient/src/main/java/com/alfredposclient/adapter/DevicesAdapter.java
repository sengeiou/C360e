package com.alfredposclient.adapter;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alfredbase.javabean.model.PrinterDevice;
import com.alfredbase.utils.ButtonClickTimer;
import com.alfredbase.utils.DialogFactory;
import com.alfredbase.view.CustomAdapter;
import com.alfredposclient.R;
import com.alfredposclient.activity.DevicesActivity;
import com.alfredposclient.global.App;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author 刘秀秀
 * @time 2015-12-1 下午1:59:36
 * @tags 设备Adapter
 */
public class DevicesAdapter extends CustomAdapter {

	//private List<PrinterDBModel> printerDBModelList;
	List<PrinterDevice> printerDBModelList =new ArrayList<PrinterDevice>();
	private Context con;
	private LayoutInflater inflater;
	private Handler handler;
	private boolean isShowUnbund = true;
	private int type = 1;

	public DevicesAdapter(Context context, List<PrinterDevice> printerDBModelList, Handler handler) {
		setPrinterDBModelList(printerDBModelList);
		this.con = context;
		this.handler = handler;
		inflater = LayoutInflater.from(con);
	}
	private void setPrinterDBModelList(List<PrinterDevice> printerDBModelList){
		this.printerDBModelList.clear();
		if (printerDBModelList != null) {
			this.printerDBModelList.addAll(printerDBModelList);
		}
		if(this.type == 1 &&(this.printerDBModelList.size() == 0
				|| this.printerDBModelList.get(0).getDevice_id() == -1)){

			PrinterDevice printerDevice = new PrinterDevice();
			printerDevice.setDevice_id(-100);
			this.printerDBModelList.add(printerDevice);
		}
	}
	public void setList(List<PrinterDevice> printerDBModelList, int type){
		this.type = type;
		setPrinterDBModelList(printerDBModelList);
		notifyDataSetChanged();
	}
	
//	public void setShowUnbund(boolean isShowUnbund) {
//		isShowUnbund = false;
//		this.isShowUnbund = isShowUnbund;
//	}

	@Override
	public int getCount() {
		return printerDBModelList.size();
	}

	@Override
	public Object getItem(int position) {
		return printerDBModelList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
//	public void refreshData(List<PrinterDevice> newData){
//		printerDBModelList.clear();
//		for (int i = 0; i < newData.size(); i++) {
//			printerDBModelList.add(newData.get(i));
//		}
//		notifyDataSetChanged();
//	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.divices_list_item, null);
			holder = new ViewHolder();
			holder.devices_ip_tv = (TextView) convertView.findViewById(R.id.devices_item_ip_tv);
			holder.tv_device_ip = (TextView) convertView.findViewById(R.id.tv_device_ip);
			holder.devices_unbund_tv = (TextView) convertView.findViewById(R.id.devices_item_unbund_tv);
			holder.devices_item_add_tv = (TextView) convertView.findViewById(R.id.devices_item_add_tv);
			holder.devices_item_add_img = (ImageView) convertView.findViewById(R.id.devices_item_add_img);
			holder.ll_auto_add = (LinearLayout) convertView.findViewById(R.id.ll_auto_add);
			holder.ll_manually_add = (LinearLayout) convertView.findViewById(R.id.ll_manually_add);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		PrinterDevice printerDevice = printerDBModelList.get(position);
		holder.devices_unbund_tv.setTag(printerDevice);
		if(printerDevice.getDevice_id() == -100){
			holder.ll_auto_add.setVisibility(View.GONE);
			holder.ll_manually_add.setVisibility(View.VISIBLE);
			holder.ll_manually_add.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					handler.sendEmptyMessage(DevicesActivity.MANUALLY_ADD_PRINTER);
				}
			});
		}else {
			holder.ll_auto_add.setVisibility(View.VISIBLE);
			holder.ll_manually_add.setVisibility(View.GONE);
			if (printerDevice.getDevice_id() != -1) {
				holder.devices_unbund_tv.setOnClickListener(ocl);
				holder.devices_unbund_tv.setText(con.getResources().getString(R.string.unassign));
				holder.devices_ip_tv.setText(printerDevice.getName());
			} else {
				holder.devices_unbund_tv.setOnClickListener(listener);
				holder.devices_unbund_tv.setText(con.getResources().getString(R.string.assign));
				holder.devices_ip_tv.setText(printerDevice.getName());
			}
			if (!TextUtils.isEmpty(printerDevice.getName())) {
				holder.devices_ip_tv.setVisibility(View.VISIBLE);
				holder.devices_ip_tv.setText(printerDevice.getName());
			} else {
				holder.devices_ip_tv.setVisibility(View.GONE);
			}

			if (type == 1) {
				holder.devices_unbund_tv.setVisibility(View.VISIBLE);
			} else if (type == 2) {
				holder.devices_unbund_tv.setVisibility(View.GONE);
			}
			holder.tv_device_ip.setText(printerDevice.getIP());
		}

		return convertView;
	}
	
	private OnClickListener ocl = new OnClickListener() {
		
		@Override
		public void onClick(final View view) {
			DialogFactory.commonTwoBtnDialog(App.getTopActivity(), con.getResources().getString(R.string.warning), "Unassign will disconnect Printer!", con.getResources().getString(R.string.cancel), con.getResources().getString(R.string.ok), null, new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					PrinterDevice device = (PrinterDevice) view.getTag();
					handler.sendMessage(handler.obtainMessage(DevicesActivity.UNASSIGN_PRINTER_DEVICE, device));
				}
			});
			
		}
	};

	private OnClickListener listener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			if(ButtonClickTimer.canClick(view)) {
				PrinterDevice device = (PrinterDevice) view.getTag();
				handler.sendMessage(handler.obtainMessage(DevicesActivity.ASSIGN_PRINTER_DEVICE, device));
			}
		}
	};

	public class ViewHolder {
		public TextView devices_ip_tv;
		public TextView devices_unbund_tv;
		public TextView tv_device_ip;
		TextView devices_item_add_tv;
		ImageView devices_item_add_img;
		LinearLayout ll_auto_add;
		LinearLayout ll_manually_add;
	}
}