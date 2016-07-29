package com.alfredposclient.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.alfredposclient.R;


public class PayMoneyKeyboard extends LinearLayout implements OnClickListener {
	private KeyBoardClickListener keyBoardClickListener;
	private LinearLayout ll_money;
	private Button btn_1;
	private Button btn_2;
	private Button btn_3;
	private Button btn_4;
	private Button btn_5;
	private Button btn_6;
	private Button btn_7;
	private Button btn_8;
	private Button btn_9;
	private Button btn_0;
	private Button btn_00;
	private Button btn_Clear;
	private Button btn_Enter;
	private Button btn_Cancel;
	private Button btn_500;
	private Button btn_200;
	private Button btn_100;
	private Button btn_50;
	private LinearLayout ll_discount;
	private Button btn_percent_5;
	private Button btn_percent_10;
	private Button btn_percent_15;
	private Button btn_percent_20;
	private Button btn_void_bill;
	private Button btn_foc_bill;
	private Button btn_ticket_check;

	public PayMoneyKeyboard(Context context) {
		super(context);
		init(context);
	}

	public PayMoneyKeyboard(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		View.inflate(context, R.layout.pay_money_keyboard, this);
		ll_money = (LinearLayout) findViewById(R.id.ll_money);
		btn_1 = (Button) findViewById(R.id.btn_1);
		btn_2 = (Button) findViewById(R.id.btn_2);
		btn_3 = (Button) findViewById(R.id.btn_3);
		btn_4 = (Button) findViewById(R.id.btn_4);
		btn_5 = (Button) findViewById(R.id.btn_5);
		btn_6 = (Button) findViewById(R.id.btn_6);
		btn_7 = (Button) findViewById(R.id.btn_7);
		btn_8 = (Button) findViewById(R.id.btn_8);
		btn_9 = (Button) findViewById(R.id.btn_9);
		btn_0 = (Button) findViewById(R.id.btn_0);
		btn_00 = (Button) findViewById(R.id.btn_00);
		btn_Clear = (Button) findViewById(R.id.btn_Clear);
		btn_Enter = (Button) findViewById(R.id.btn_Enter);
		btn_Cancel = (Button) findViewById(R.id.btn_Cancel);
		btn_500 = (Button) findViewById(R.id.btn_500);
		
		btn_200 = (Button) findViewById(R.id.btn_200);
		btn_100 = (Button) findViewById(R.id.btn_100);
		btn_50 = (Button) findViewById(R.id.btn_50);
		ll_discount = (LinearLayout) findViewById(R.id.ll_discount);
		btn_percent_5 = (Button) findViewById(R.id.btn_percent_5);
		btn_percent_10 = (Button) findViewById(R.id.btn_percent_10);
		btn_percent_15 = (Button) findViewById(R.id.btn_percent_15);
		btn_percent_20 = (Button) findViewById(R.id.btn_percent_20);
		btn_void_bill = (Button) findViewById(R.id.btn_void_bill);
		btn_foc_bill = (Button) findViewById(R.id.btn_foc_bill);
		btn_ticket_check = (Button) findViewById(R.id.btn_ticket_check);
		
		
		btn_1.setTag("1");
		btn_1.setOnClickListener(this);
		btn_2.setTag("2");
		btn_2.setOnClickListener(this);
		btn_3.setTag("3");
		btn_3.setOnClickListener(this);
		btn_4.setTag("4");
		btn_4.setOnClickListener(this);
		btn_5.setTag("5");
		btn_5.setOnClickListener(this);
		btn_6.setTag("6");
		btn_6.setOnClickListener(this);
		btn_7.setTag("7");
		btn_7.setOnClickListener(this);
		btn_8.setTag("8");
		btn_8.setOnClickListener(this);
		btn_9.setTag("9");
		btn_9.setOnClickListener(this);
		btn_0.setTag("0");
		btn_0.setOnClickListener(this);
		btn_00.setTag("00");
		btn_00.setOnClickListener(this);
		btn_Clear.setTag("C");
		btn_Clear.setOnClickListener(this);
		btn_Enter.setTag("Enter");
		btn_Enter.setOnClickListener(this);
		btn_Cancel.setTag("X");
		btn_Cancel.setOnClickListener(this);
		btn_500.setTag("500");
		btn_500.setOnClickListener(this);
		btn_200.setTag("200");
		btn_200.setOnClickListener(this);
		btn_100.setTag("100");
		btn_100.setOnClickListener(this);
		btn_50.setTag("50");
		btn_50.setOnClickListener(this);
		btn_percent_5.setTag("5%");
		btn_percent_5.setOnClickListener(this);
		btn_percent_10.setTag("10%");
		btn_percent_10.setOnClickListener(this);
		btn_percent_15.setTag("15%");
		btn_percent_15.setOnClickListener(this);
		btn_percent_20.setTag("20%");
		btn_percent_20.setOnClickListener(this);
		
		btn_void_bill.setTag("void");
		btn_void_bill.setOnClickListener(this);
		btn_foc_bill.setTag("foc");
		btn_foc_bill.setOnClickListener(this);
		btn_ticket_check.setTag("coupons");
		btn_ticket_check.setOnClickListener(this);
	}

	
	public void setKeyBoardClickListener(
			KeyBoardClickListener keyBoardClickListener) {
		this.keyBoardClickListener = keyBoardClickListener;
	}

	public void setMoneyPanel(int visibility) {
		ll_money.setVisibility(visibility);
	}

	public void setDiscountPanel(int visibility){
		ll_discount.setVisibility(visibility);
	}
	
	public interface KeyBoardClickListener {
		void onKeyBoardClick(String key);
	}

	@Override
	public void onClick(View v) {
		Button button = (Button) v;
		if (keyBoardClickListener != null)
			keyBoardClickListener.onKeyBoardClick((String)button.getTag());

	}
	
	

}
