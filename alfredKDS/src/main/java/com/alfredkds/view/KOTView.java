package com.alfredkds.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alfredbase.ParamConst;
import com.alfredbase.javabean.KotItemDetail;
import com.alfredbase.javabean.KotItemModifier;
import com.alfredbase.javabean.model.MainPosInfo;
import com.alfredbase.store.sql.KotSummarySQL;
import com.alfredbase.utils.AnimatorListenerImpl;
import com.alfredbase.utils.ButtonClickTimer;
import com.alfredbase.utils.IntegerUtils;
import com.alfredbase.utils.TextTypeFace;
import com.alfredbase.utils.TimeUtil;
import com.alfredkds.R;
import com.alfredkds.activity.KitchenOrder;
import com.alfredkds.global.App;
import com.alfredkds.javabean.Kot;

import java.util.ArrayList;
import java.util.List;

/**
 * KOT信息，每桌菜品展示ScrollView
 *
 * @author
 */
public class KOTView extends LinearLayout implements AnimationListener,
        OnClickListener {

    private KitchenOrder parent;

    private Animation animCenterOpen;
    private Animation animCenterClose;
    private TextView timer;
    private Kot kot;
    private Context context;
    private TextView kotId, tv_kds_delivery;
    private TextView orderId;
    private TextView table;
    private TextView posName;
    private TextView date;
    private TextView time;
    private TextView tv_kiosk_order_id, tv_kiosk_app_order_id, tv_order_type;
    private TextView tv_orderremark;
    private TextView tv_remark;
    private LinearLayout ll_orderRemark, ll_type;
    private View kotView;
    public Chronometer tv_progress;

    private TextView complete_all_tv;
    private TextView call_num_tv;

    private ListView lv_dishes;
    private KotItemDetailAdapter adapter;
    private List<KotItemDetail> kotItemDetails = new ArrayList<KotItemDetail>();
    private List<KotItemModifier> kotItemModifiers = new ArrayList<KotItemModifier>();
    private TextTypeFace textTypeFace;
    private Handler handler;
    private int hour;
    private MainPosInfo mainPosInfo;
    private TextView tvNext;

    public KOTView(Context context) {
        super(context);
        this.parent = (KitchenOrder) context;
        this.context = context;
        init();
    }

    public KOTView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.parent = (KitchenOrder) context;
        this.context = context;
        init();

    }

//	public Long getTime(){
//		return tv_progress.getBase();
//	}

    public void setParams(Context context, Handler handler) {
        this.parent = (KitchenOrder) context;
        this.context = context;
        this.handler = handler;
    }

    public void init() {
        mainPosInfo = App.instance.getCurrentConnectedMainPos();
        kotView = View.inflate(context, R.layout.kot_view, this);
        /*---kotTop显示---*/
        kotId = (TextView) kotView.findViewById(R.id.tv_kotId);

        orderId = (TextView) kotView.findViewById(R.id.tv_order_id);
        table = (TextView) kotView.findViewById(R.id.tv_table);
        posName = (TextView) kotView.findViewById(R.id.tv_pos);
        date = (TextView) kotView.findViewById(R.id.tv_createDate);
        time = (TextView) kotView.findViewById(R.id.tv_createTime);
        tv_progress = (Chronometer) kotView.findViewById(R.id.tv_progress);
        lv_dishes = (ListView) kotView.findViewById(R.id.lv_dishes);
        tv_kiosk_order_id = (TextView) kotView.findViewById(R.id.tv_kiosk_order_id);
        tv_kiosk_app_order_id = (TextView) kotView.findViewById(R.id.tv_kiosk_app_order_id);
        tv_order_type = (TextView) kotView.findViewById(R.id.tv_order_type);
        tv_orderremark = (TextView) kotView.findViewById(R.id.tv_orderremark);
        ll_orderRemark = (LinearLayout) kotView.findViewById(R.id.ll_orderRemark);
        ll_type = (LinearLayout) kotView.findViewById(R.id.ll_type);
        call_num_tv = (TextView) kotView.findViewById(R.id.call_num_tv);
        complete_all_tv = (TextView) kotView.findViewById(R.id.complete_all_tv);
        tvNext = (TextView) kotView.findViewById(R.id.tvNext);

        if ("0".equals(App.instance.getKdsDevice().getKdsType())) {
            call_num_tv.setVisibility(GONE);
            complete_all_tv.setVisibility(GONE);
            tvNext.setVisibility(VISIBLE);
        }

        tv_orderremark.setMovementMethod(ScrollingMovementMethod.getInstance());

        adapter = new KotItemDetailAdapter();
//		initTextTypeFace();
        if (mainPosInfo.getIsKiosk() == ParamConst.MAINPOSINFO_IS_KIOSK) {
            tv_kiosk_order_id.setVisibility(View.VISIBLE);
            orderId.setVisibility(View.GONE);
        } else {
            tv_kiosk_order_id.setVisibility(View.GONE);
            orderId.setVisibility(View.VISIBLE);
        }

    }


    public class KotItemDetailAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return kotItemDetails.size();
        }

        @Override
        public Object getItem(int position) {
            return kotItemDetails.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.item_in_kot, null);
                holder = new ViewHolder();
                holder.tv_order_num = (TextView) convertView.findViewById(R.id.tv_order_num);
                holder.tv_text = (TextView) convertView.findViewById(R.id.tv_text);
                holder.tv_dish_introduce = (TextView) convertView.findViewById(R.id.tv_dish_introduce);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            KotItemDetail kotItemDetail = kotItemDetails.get(position);

            if (kotItemDetail.getKotStatus() == ParamConst.KOT_STATUS_DONE) {
                convertView.setBackgroundResource(R.color.bg_complete_item);
            } else if (kotItemDetail.getFireStatus() == 1) {
                convertView.setBackgroundResource(R.color.viewfinder_laser);
            } else if (kotItemDetail.getKotStatus() == ParamConst.KOT_STATUS_VOID) {
                convertView.setBackgroundResource(R.color.possible_result_points);
                holder.tv_text.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.FAKE_BOLD_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
            } else if (kotItemDetail.getKotStatus() == ParamConst.KOT_STATUS_TEMPORER) {//temporary item
                convertView.setBackgroundResource(R.color.gray);
            } else {
                if (kotItemDetail.getKotStatus() == ParamConst.KOT_STATUS_UPDATE) {
                    convertView.setBackgroundResource(R.color.bg_update_item);
                } else {
                    convertView.setBackgroundResource(R.color.white);
                }
            }
            StringBuffer sBuffer = new StringBuffer();
            for (int j = 0; j < kotItemModifiers.size(); j++) {
                KotItemModifier kotItemModifier = kotItemModifiers.get(j);
                if (kotItemModifier != null
                        && kotItemDetail.getId().intValue() == kotItemModifier.getKotItemDetailId().intValue()) {
                    sBuffer.append("--" + kotItemModifier.getModifierName() + "\n");
                }
            }
            if (!TextUtils.isEmpty(kotItemDetail.getSpecialInstractions())) {
                sBuffer.append("*" + kotItemDetail.getSpecialInstractions() + "*");
            }
            if (sBuffer.toString().endsWith("\n")) {
                sBuffer.deleteCharAt(sBuffer.length() - 1);
            }
            holder.tv_order_num.setText(kotItemDetail.getUnFinishQty() + "");
            holder.tv_text.setText(kotItemDetail.getItemName());
            holder.tv_dish_introduce.setText(sBuffer);
//			textTypeFace.setTrajanProBlod(holder.tv_text);
//			textTypeFace.setTrajanProRegular(holder.tv_order_num);
//			textTypeFace.setTrajanProRegular(holder.tv_dish_introduce);
            return convertView;
        }
    }

    class ViewHolder {
        private TextView tv_order_num;
        private TextView tv_text;
        private TextView tv_dish_introduce;
    }


    public void setData(Kot originKot) {
        this.kot = originKot;
        this.kotItemDetails.clear();
        this.kotItemDetails.addAll(kot.getKotItemDetails());
        this.kotItemModifiers.clear();
        this.kotItemModifiers.addAll(kot.getKotItemModifiers());
        kotId.setText(kot.getKotSummary().getId() + "");
        String orderNoStr = context.getResources().getString(R.string.order_id_) + kot.getKotSummary().getNumTag() + kot.getKotSummary().getOrderNo();

        if (!TextUtils.isEmpty(kot.getKotSummary().getEmpName())) {
            orderNoStr = orderNoStr + "(Emp:" + kot.getKotSummary().getEmpName() + ")";
        }
        String kioskOrderNoStr = context.getResources().getString(R.string.order_id_) + kot.getKotSummary().getNumTag() + IntegerUtils.fromat(kot.getKotSummary().getRevenueCenterIndex(), kot.getKotSummary().getOrderNo() + "");
        if (kot.getKotSummary() != null && kot.getKotSummary().getIsTakeAway().intValue() == ParamConst.TAKE_AWAY) {
            orderNoStr = orderNoStr + "(" + context.getResources().getString(R.string.take_away) + ")";
            kioskOrderNoStr = kioskOrderNoStr + "(" + context.getResources().getString(R.string.take_away) + ")";
        }
        if (kot.getKotSummary().getIsTakeAway().intValue() == ParamConst.DINE_IN) {
            orderNoStr = orderNoStr + "(" + context.getResources().getString(R.string.dine_in) + ")";
            kioskOrderNoStr = kioskOrderNoStr + "(" + context.getResources().getString(R.string.dine_in) + ")";
        } else if (kot.getKotSummary().getIsTakeAway().intValue() == ParamConst.APP_DELIVERY) {
            orderNoStr = orderNoStr + "(" + context.getResources().getString(R.string.app_delivery) + ")";
            kioskOrderNoStr = kioskOrderNoStr + "(" + context.getResources().getString(R.string.app_delivery) + ")";
        }

        if (TextUtils.isEmpty(kot.getKotSummary().getTableName())) {
            table.setText(kioskOrderNoStr);
            if (kot.getKotSummary().getAppOrderId() > 0) {
                if (kot.getKotSummary().getEatType() == 3) {
                    orderId.setText("Online App No:" + kot.getKotSummary().getAppOrderId() + "\n" + TimeUtil.getDeliveryDataTime(kot.getKotSummary().getDeliveryTime()));
                    tv_kiosk_order_id.setText("Online App No:" + kot.getKotSummary().getAppOrderId() + "\n" + TimeUtil.getDeliveryDataTime(kot.getKotSummary().getDeliveryTime()));

                } else {
                    orderId.setText("Online App No:" + kot.getKotSummary().getAppOrderId());
                    tv_kiosk_order_id.setText("Online App No:" + kot.getKotSummary().getAppOrderId());

                }
            } else {
                orderId.setText("");
                tv_kiosk_order_id.setText("");
            }
            ll_type.setVisibility(GONE);
        } else {
            table.setText(context.getResources().getString(R.string.table_) + kot.getKotSummary().getTableName());
            orderId.setText(orderNoStr);
            tv_kiosk_order_id.setText(kioskOrderNoStr);
            if (kot.getKotSummary().getAppOrderId() > 0) {
                ll_type.setVisibility(VISIBLE);
                if (kot.getKotSummary().getEatType() == 3) {
                    tv_kiosk_app_order_id.setText("Online App No:" + kot.getKotSummary().getAppOrderId() + "\n" + TimeUtil.getDeliveryDataTime(kot.getKotSummary().getDeliveryTime()));
                } else {
                    tv_kiosk_app_order_id.setText("Online App No:" + kot.getKotSummary().getAppOrderId());
                }

            } else {
                ll_type.setVisibility(GONE);
            }
        }
        //	posName.setText(kot.getKotSummary().getRevenueCenterName() + "");


//		if(kot.getKotSummary().getEatType()==2)
//		{
//			ll_type.setVisibility(GONE);
//		}else {
//			ll_type.setVisibility(VISIBLE);
//			tv_kiosk_app_order_id.setText("Online App No:"+kot.getKotSummary().getAppOrderId());
//			if(kot.getKotSummary().getEatType()==3){
//				//tv_order_type.setText("delivery");
//			}else {
//				//tv_order_type.setText("dine in");
//			}
//		}
        String remark = kot.getKotSummary().getOrderRemark();
        if (TextUtils.isEmpty(remark)) {
            ll_orderRemark.setVisibility(GONE);
        } else {
            if (kot.getKotSummary().getEatType() == 3) {
                ll_orderRemark.setVisibility(GONE);
            } else {
                ll_orderRemark.setVisibility(VISIBLE);
                tv_orderremark.setText("Remark:" + " " + remark);
            }

        }

        date.setText(TimeUtil.getPrintDate(kot.getKotSummary().getCreateTime()));
        time.setText(TimeUtil.getPrintTime(kot.getKotSummary().getCreateTime()));

//		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
//		long currentTime = System.currentTimeMillis();
        long createTime = kot.getKotSummary().getUpdateTime();
//		final String str = sdf.format(new Date(currentTime - createTime));
        tv_progress.setBase(SystemClock.elapsedRealtime() - (System.currentTimeMillis() - createTime));
//		int hour = (int) ((System.currentTimeMillis() - createTime) / 1000 / 60/60);
//		tv_progress.setFormat("0"+String.valueOf(hour)+":%s");
//		tv_progress.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
//			@Override
//			public void onChronometerTick(Chronometer chronometer) {
//				chronometer.setText(str);
//
//			}
//		});
        tv_progress.start();
//		tv_progress.setText(str);


        if (lv_dishes.getAdapter() == null) {
            lv_dishes.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
        lv_dishes.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parentView, View View, int position,
                                    long id) {
                if (!ButtonClickTimer.canClick(View)) {
                    return;
                }
                parent.showOrderItem(kot.getKotSummary());
            }
        });

        if (isComplete()) {
            tv_progress.setText(context.getResources().getString(R.string.item_complete));
            tv_progress.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    kot.getKotSummary().setStatus(ParamConst.KOTS_STATUS_DONE);
                    KotSummarySQL.update(kot.getKotSummary());
                    KOTView.this.dismissKot();
                }
            });
        } else {
            tv_progress.setFocusable(false);
        }

        call_num_tv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ButtonClickTimer.canClick()) {
                    return;
                }
                Message message = new Message();
                message.obj = kot;
                message.arg2 = -1;
                message.what = App.HANDLER_KOT_CALL_NUM_OLD;
                handler.sendMessage(message);
            }
        });

        complete_all_tv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ButtonClickTimer.canClick()) {
                    return;
                }
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putSerializable("kotSummary", kot.getKotSummary());
                message.setData(bundle);
                message.what = App.HANDLER_KOT_COMPLETE_ALL;
                handler.sendMessage(message);
            }
        });

        tvNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!ButtonClickTimer.canClick()) return;

                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putSerializable("kotSummary", kot.getKotSummary());
                message.setData(bundle);
                message.what = App.HANDLER_KOT_NEXT;
                handler.sendMessage(message);
            }
        });

    }

    public void showNewKOT() {
        // this.startAnimation(this.animCenterOpen);
        kotView.post(new Runnable() {
            @Override
            public void run() {
                AnimatorSet set = new AnimatorSet();
                ObjectAnimator animator1 = ObjectAnimator.ofFloat(kotView,
                        "scaleY", 0.3f, 1f).setDuration(500);
                ObjectAnimator animator2 = ObjectAnimator.ofFloat(kotView,
                        "scaleX", 0.3f, 1f).setDuration(500);
                set.playTogether(animator1, animator2);
                set.setInterpolator(new DecelerateInterpolator());
                set.start();
            }
        });
    }

    public void dismissKot() {
        kotView.post(new Runnable() {
            @Override
            public void run() {
                if (AnimatorListenerImpl.isRunning) {
                    return;
                }
                AnimatorSet set = new AnimatorSet();
                ObjectAnimator animator1 = ObjectAnimator.ofFloat(kotView,
                        "scaleY", 1f, 0.05f).setDuration(500);
                ObjectAnimator animator2 = ObjectAnimator.ofFloat(kotView,
                        "scaleX", 1f, 0.05f).setDuration(500);
                set.playTogether(animator1, animator2);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerImpl() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        parent.adapter.setKots(App.instance.getRefreshKots());
                        parent.adapter.notifyDataSetChanged();
                    }
                });
                set.start();
            }
        });
    }

    public boolean isComplete() {
        for (int i = 0; i < kotItemDetails.size(); i++) {
            if (kotItemDetails.get(i).getKotStatus() < ParamConst.KOT_STATUS_DONE) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        if (ButtonClickTimer.canClick(v)) {

        }
    }

    @Override
    public void onAnimationEnd(Animation arg0) {

    }

    @Override
    public void onAnimationRepeat(Animation arg0) {

    }

    @Override
    public void onAnimationStart(Animation arg0) {

    }

    private void initTextTypeFace() {
        textTypeFace = TextTypeFace.getInstance();
        textTypeFace.setTrajanProBlod(kotId);
        textTypeFace.setTrajanProRegular(orderId);
        textTypeFace.setTrajanProRegular(table);
        textTypeFace.setTrajanProRegular(posName);
        textTypeFace.setTrajanProRegular(date);
        textTypeFace.setTrajanProRegular(time);
        textTypeFace.setTrajanProRegular(tv_progress);
        textTypeFace.setTrajanProRegular(tv_kiosk_order_id);
        textTypeFace.setTrajanProRegular(tv_orderremark);
        textTypeFace.setTrajanProRegular(tv_remark);
    }
}
