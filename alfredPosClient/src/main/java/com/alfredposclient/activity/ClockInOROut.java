package com.alfredposclient.activity;

import java.util.ArrayList;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alfredbase.BaseActivity;
import com.alfredbase.ParamConst;
import com.alfredbase.global.CoreData;
import com.alfredbase.javabean.User;
import com.alfredbase.javabean.UserTimeSheet;
import com.alfredbase.store.sql.UserTimeSheetSQL;
import com.alfredbase.utils.ObjectFactory;
import com.alfredposclient.R;
import com.alfredposclient.global.App;
import com.alfredposclient.global.UIHelp;
import com.alfredposclient.view.MoneyKeyboardForClock;
import com.alfredposclient.view.MoneyKeyboardForClock.KeyBoardClickListener;

public class ClockInOROut extends BaseActivity implements KeyBoardClickListener {
	private MoneyKeyboardForClock moneyKeyboardForClock;
	private TextView tv_psw_1;
	private TextView tv_psw_2;
	private TextView tv_psw_3;
	private TextView tv_psw_4;
	private TextView tv_psw_5;
	private Button btn_back;
	private StringBuffer keyBuf = new StringBuffer();
	private static final int KEY_LENGTH = 5;

	@Override
	protected void initView() {
		super.initView();
		setContentView(R.layout.activity_clock_inorout);
		moneyKeyboardForClock = (MoneyKeyboardForClock) findViewById(R.id.moneyKeyboardForClock);
		moneyKeyboardForClock.setKeyBoardClickListener(this);
		tv_psw_1 = (TextView) findViewById(R.id.tv_psw_1);
		tv_psw_2 = (TextView) findViewById(R.id.tv_psw_2);
		tv_psw_3 = (TextView) findViewById(R.id.tv_psw_3);
		tv_psw_4 = (TextView) findViewById(R.id.tv_psw_4);
		tv_psw_5 = (TextView) findViewById(R.id.tv_psw_5);
		btn_back = (Button) findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);
	}

	@Override
	public void handlerClickEvent(View v) {
		super.handlerClickEvent(v);
		switch (v.getId()) {
		case R.id.btn_back:
			this.finish();
			break;

		default:
			break;
		}
	}

	@Override
	public void onKeyBoardClick(String key) {
		if (keyBuf.length() > KEY_LENGTH)
			return;
		if (key.equals("Delete")) {
			if (keyBuf.length() > 0) {
				keyBuf.deleteCharAt(keyBuf.length() - 1);
			}
		} else if (key.equals("clock In")) {
			if (keyBuf.length() == KEY_LENGTH) {
				if(clockIn(keyBuf.toString())){
					UIHelp.showToast(context, context.getResources().getString(R.string.clock_in));
				}else{
					UIHelp.showToast(context, context.getResources().getString(R.string.id_error));
				}
				keyBuf.delete(0, keyBuf.length());
			} else {
				UIHelp.showToast(context, context.getResources().getString(R.string.id_error));
			}
		} else if (key.equals("clock Out")) {
			if (keyBuf.length() == KEY_LENGTH) {
				if(clockOut(keyBuf.toString())){
					UIHelp.showToast(context, context.getResources().getString(R.string.clock_out));
				}else{
					UIHelp.showToast(context, context.getResources().getString(R.string.id_error));
				}
				keyBuf.delete(0, keyBuf.length());
			} else {
				UIHelp.showToast(context, context.getResources().getString(R.string.id_error));
			}
		} else if (keyBuf.length() < KEY_LENGTH) {
			keyBuf.append(key);
		}
		setPassword(keyBuf.length());

	}

	private boolean clockIn(String employyeId) {
		User user = CoreData.getInstance().getUserByEmpId(
				Integer.parseInt(employyeId));
		if (user == null) {
			return false;
		}
		ArrayList<UserTimeSheet> mUserTimeSheets = UserTimeSheetSQL.getUserTimeSheetsByEmpId(Integer.parseInt(employyeId), App.instance.getLastBusinessDate());
		for(UserTimeSheet userTimeSheet : mUserTimeSheets){
			if(userTimeSheet != null && userTimeSheet.getStatus() == ParamConst.USERTIMESHEET_STATUS_LOGIN){
				return false;
			}
		}
		
		UserTimeSheet userTimeSheet = ObjectFactory.getInstance()
				.getUserTimeSheet(App.instance.getLastBusinessDate(), user,
						App.instance.getRevenueCenter());
		UserTimeSheetSQL.addUser(userTimeSheet);
		return true;
	}
	
	private boolean clockOut(String employyeId) {
		User user = CoreData.getInstance().getUserByEmpId(
				Integer.parseInt(employyeId));
		if(user == null){
			return false;
		}
		UserTimeSheet userTimeSheet = UserTimeSheetSQL.getUserTimeSheetByEmpId(Integer.parseInt(employyeId), App.instance.getLastBusinessDate());
		if (userTimeSheet == null) {
			return false;
		}
		userTimeSheet.setLogoutTime(System.currentTimeMillis());
		userTimeSheet.setStatus(ParamConst.USERTIMESHEET_STATUS_LOGOUT);
		UserTimeSheetSQL.addUser(userTimeSheet);
		return true;
	}

	private void setPassword(int key_len) {
		switch (key_len) {
		case 0: {
			tv_psw_1.setText("");
			tv_psw_2.setText("");
			tv_psw_3.setText("");
			tv_psw_4.setText("");
			tv_psw_5.setText("");
			break;
		}
		case 1: {
			tv_psw_1.setText(keyBuf.charAt(0) + "");
			tv_psw_2.setText("");
			tv_psw_3.setText("");
			tv_psw_4.setText("");
			tv_psw_5.setText("");
			break;
		}
		case 2: {
			tv_psw_1.setText(keyBuf.charAt(0) + "");
			tv_psw_2.setText(keyBuf.charAt(1) + "");
			tv_psw_3.setText("");
			tv_psw_4.setText("");
			tv_psw_5.setText("");
			break;
		}
		case 3: {
			tv_psw_1.setText(keyBuf.charAt(0) + "");
			tv_psw_2.setText(keyBuf.charAt(1) + "");
			tv_psw_3.setText(keyBuf.charAt(2) + "");
			tv_psw_4.setText("");
			tv_psw_5.setText("");
			break;
		}
		case 4: {
			tv_psw_1.setText(keyBuf.charAt(0) + "");
			tv_psw_2.setText(keyBuf.charAt(1) + "");
			tv_psw_3.setText(keyBuf.charAt(2) + "");
			tv_psw_4.setText(keyBuf.charAt(3) + "");
			tv_psw_5.setText("");
			break;
		}
		case 5: {
			tv_psw_1.setText(keyBuf.charAt(0) + "");
			tv_psw_2.setText(keyBuf.charAt(1) + "");
			tv_psw_3.setText(keyBuf.charAt(2) + "");
			tv_psw_4.setText(keyBuf.charAt(3) + "");
			tv_psw_5.setText(keyBuf.charAt(4) + "");
			break;
		}
		default:
			break;
		}
	}

}
