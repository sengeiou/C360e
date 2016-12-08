package com.alfredposclient.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alfredbase.ParamConst;
import com.alfredbase.javabean.PaymentSettlement;
import com.alfredbase.utils.BH;
import com.alfredbase.utils.TextTypeFace;
import com.alfredposclient.R;

public class SettlementDetailItemView extends LinearLayout implements OnClickListener {
//	private static final int ROW_COUNT = 4;
	public static final int ITEM_SIZE = 100;
	public static final int MARGIN_SIZE = 10;
	public ImageView iv_settlement_icon;
	public TextView tv_settlement_num;
	public TextView tv_settlement_delicon;
	private TextTypeFace textTypeFace;

	public SettlementDetailItemView(Context context) {
		super(context);
		init(context);
	}

	public SettlementDetailItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	private void init(Context context) {
		View.inflate(context, R.layout.settlement_type_item, this);
		iv_settlement_icon = (ImageView) findViewById(R.id.iv_settlement_icon);
		tv_settlement_num = (TextView) findViewById(R.id.tv_settlement_num);
		tv_settlement_delicon = (TextView) findViewById(R.id.tv_settlement_delicon);
		initTextTypeFace();
	}

	
	public void setParams(final PaymentSettlement paymentSettlement, final ViewResultCall viewResultCall) {
		tv_settlement_num.setText(BH.getBD(paymentSettlement.getPaidAmount()).toString());
		iv_settlement_icon
				.setImageResource(getImageResourceBySettlementType(paymentSettlement
						.getPaymentTypeId().intValue()));
		int paymentTypeId = paymentSettlement.getPaymentTypeId()
				.intValue();
		if(paymentTypeId == ParamConst.SETTLEMENT_TYPE_PAYPAL){
			tv_settlement_delicon.setVisibility(View.INVISIBLE);
			tv_settlement_delicon.setOnClickListener(null);
		}else{
			tv_settlement_delicon.setVisibility(View.VISIBLE);
			tv_settlement_delicon.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					viewResultCall.call(paymentSettlement);
				}
			});
		}

	}
	
	private int getImageResourceBySettlementType(int paymentTypeId) {
		switch (paymentTypeId) {
		case ParamConst.SETTLEMENT_TYPE_CASH:
		case ParamConst.SETTLEMENT_TYPE_PAYPAL:
		case ParamConst.SETTLEMENT_TYPE_STORED_CARD:
			return R.drawable.icon_settle_cash;
		case ParamConst.SETTLEMENT_TYPE_MASTERCARD:
			return R.drawable.icon_master;
		case ParamConst.SETTLEMENT_TYPE_UNIPAY:
			return R.drawable.icon_upay;
		case ParamConst.SETTLEMENT_TYPE_VISA:
			return R.drawable.icon_visa;
		case ParamConst.SETTLEMENT_TYPE_NETS:
			return R.drawable.icon_nets;
		case ParamConst.SETTLEMENT_TYPE_AMEX:
			return R.drawable.icon_amer;
		case ParamConst.SETTLEMENT_TYPE_JCB:
			return R.drawable.icon_jcb;
		case ParamConst.SETTLEMENT_TYPE_DINNER_INTERMATIONAL:
			return R.drawable.icon_d;
		case ParamConst.SETTLEMENT_TYPE_BILL_ON_HOLD:
			return R.drawable.settle_boh;
		case ParamConst.SETTLEMENT_TYPE_COMPANY:
			return R.drawable.settle_com;
		case ParamConst.SETTLEMENT_TYPE_HOURS_CHARGE:
			return R.drawable.settle_hc;
		case ParamConst.SETTLEMENT_TYPE_VOID:
			return R.drawable.settle_void;
		case ParamConst.SETTLEMENT_TYPE_ENTERTAINMENT:
			return R.drawable.settle_ent;
		case ParamConst.SETTLEMENT_TYPE_ALIPAY:
			return R.drawable.icon_alipay;
		case ParamConst.SETTLEMENT_TYPE_WEIXIN:
			return R.drawable.icon_wxpay;		
		default:
			return R.drawable.icon_settle_cash;
		}
	}

	@Override
	public void onClick(View v) {

	}
	
	private void initTextTypeFace(){
		textTypeFace = TextTypeFace.getInstance();
		textTypeFace.setTrajanProBlod(tv_settlement_num);
	}
	
	public interface ViewResultCall {
		public void call(PaymentSettlement paymentSettlement);
	}
}
