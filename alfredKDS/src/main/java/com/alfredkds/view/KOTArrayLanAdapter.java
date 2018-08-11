package com.alfredkds.view;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import com.alfredbase.ParamConst;
import com.alfredbase.javabean.ItemDetail;
import com.alfredbase.javabean.KotItemDetail;
import com.alfredbase.javabean.model.MainPosInfo;
import com.alfredbase.store.sql.KotSummarySQL;
import com.alfredbase.utils.ButtonClickTimer;
import com.alfredbase.utils.IntegerUtils;
import com.alfredkds.R;
import com.alfredbase.javabean.KotItem;
import com.alfredkds.global.App;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class KOTArrayLanAdapter extends RecyclerView.Adapter<KOTArrayLanAdapter.ViewHolder>{

	private Context mContext;
	private List<KotItem> kots = new ArrayList<KotItem>();
	private boolean addFirstItem = false;
	private Handler handler;
	private LayoutInflater inflater;
	private MainPosInfo mainPosInfo;

//	private Map<Integer, Long> times = new HashMap<Integer, Long>();

	public KOTArrayLanAdapter(Context mContext, Handler handler) {
		super();
//		times.clear();
		this.mContext = mContext;
		this.handler = handler;
		inflater=LayoutInflater. from(mContext);
	}

	public void setAddFirstItem(boolean addFirstItem) {
		this.addFirstItem = addFirstItem;
	}



	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View convertView = null;
		ViewHolder viewHolder = null;



	//	View convertView = View.inflate(mContext,R.layout.kot_array_view, null);


		//convertView = inflater.inflate(R.layout. kot_array_view,parent, false);
		if(viewType==0){
			convertView = inflater.inflate(R.layout. kot_array_landscape_title,parent, false);
			//convertView= LayoutInflater.from(parent.getContext()).inflate(R.layout.kot_array_landscape_view, parent, false);
			// convertView = View.inflate(mContext,R.layout.kot_array_landscape_view, null);
			viewHolder= new ViewHolder(convertView);

		}else {
			convertView = inflater.inflate(R.layout. kot_array_landscape_view,parent, false);

			viewHolder= new ViewHolder(convertView);

			viewHolder.table=(TextView)convertView.findViewById(R.id.tv_kot_item_table);

			viewHolder.detail=(TextView)convertView.findViewById(R.id.tv_kot_detail);
			viewHolder.mod=(TextView)convertView.findViewById(R.id.tv_kot_mod);

			viewHolder.status=(TextView)convertView.findViewById(R.id.tv_kot_item_status);
			viewHolder.orderNo=(TextView)convertView.findViewById(R.id.tv_kot_item_orderno);
			viewHolder.tv_lan_progress=(Chronometer)convertView.findViewById(R.id.tv_lan_progress);
			viewHolder.btn_complete=(Button) convertView.findViewById(R.id.btn_kot_complete);
			viewHolder.btn_call=(Button)convertView.findViewById(R.id.btn_kot_call);

		}


		return viewHolder;
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {

		int itemViewType = KOTArrayLanAdapter.this.getItemViewType(position);
		if(itemViewType==1) {
			final KotItem kotItem = kots.get(position);

			holder.orderNo.setText(kotItem.getOrderNo() + "");
			holder.mod.setText(kotItem.getItemModName());


			holder.detail.setText(kotItem.getItemDetailName());
			holder.table.setText(kotItem.getTableName());

			long createTime = kotItem.getUpdateTime();

			holder.tv_lan_progress.setBase(SystemClock.elapsedRealtime() - System.currentTimeMillis() + createTime);
//
			holder.tv_lan_progress.start();
			if (kotItem.getCallType() == 1) {
				holder.btn_call.setClickable(false);
				//	holder.btn_call.setText("void");
				holder.btn_call.setBackgroundColor(Color.GRAY);
			} else {
				holder.btn_call.setClickable(true);
				//	holder.btn_call.setText("void");
				holder.btn_call.setBackgroundColor(mContext.getResources().getColor(R.color.color_kotview));
				holder.btn_call.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (!ButtonClickTimer.canClick()) {
							return;
						}
						mainPosInfo = App.instance.getCurrentConnectedMainPos();
						Message message = new Message();
						if (mainPosInfo.getIsKiosk() == ParamConst.MAINPOSINFO_IS_KIOSK) {
							//	int orderNoStr = IntegerUtils.fromat(kot.getKotSummary().getRevenueCenterIndex(), kotItem.getOrderNo());
							//message.arg1 = -1;
						} else {
							message.arg1 = kotItem.getOrderNo();
							message.arg2 = kotItem.getId();
						}
						message.what = App.HANDLER_KOT_CALL_NUM;
						handler.sendMessage(message);
					}
				});

			}
			holder.btn_complete.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {


					if (!ButtonClickTimer.canClick()) {
						return;
					}

					Gson gson = new Gson();
					KotItemDetail kotItemDetail = gson.fromJson(kotItem.getItemDetail(), KotItemDetail.class);
					Message message = new Message();
					Bundle bundle = new Bundle();
					bundle.putSerializable("kotSummary", KotSummarySQL.getKotSummaryById(kotItem.getSummaryId()));

					bundle.putSerializable("kotItemDetail", kotItemDetail);
					bundle.putInt("id", kotItem.getId());
					message.setData(bundle);
					message.what = App.HANDLER_KOT_COMPLETE;
					handler.sendMessage(message);
				}
			});
		}
//		holder.orderNo.setText(kotItem.getOrderNo());
//		holder.orderNo.setText(kotItem.getOrderNo());


	//	holder.kotLanView.setData(originKot);
//		Kot kot = kots.get(position);
//		//holder.name.setText(kot.getKotItemDetails().get());
////		Kot originKot = kots.get(position);
//		holder.kotLanView.setData(kot);
//		if (addFirstItem && position == 0) {
//			holder.kotView.showNewKOT();
//			addFirstItem = false;
//		}}
	}


	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemCount() {
		return kots.size();
	}

	@Override
	public int getItemViewType(int position) {
		if(position==0) {
			return 0;
		}else {
			return 1;
		}
	}


	
	class ViewHolder extends  RecyclerView.ViewHolder{

		   Chronometer  tv_lan_progress;
           TextView name,status,table,orderNo,detail,mod;
           Button btn_complete,btn_call;
           Button complete,call;
		public ViewHolder(View itemView) {
			super(itemView);
		}
	}

	/**
	 * @return the kots
	 */
	public List<KotItem> getKots() {
		return kots;
	}

	/**
	 * @param
	 *
	 */
	public void setKots(List<KotItem> kotlist) {
		kots.clear();
		this.kots.add(null);
		this.kots.addAll(kotlist);
	//	this.kots = kots;
	}

}