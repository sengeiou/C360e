package com.alfredselfhelp.activity;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.alfredbase.BaseActivity;
import com.alfredbase.BaseApplication;
import com.alfredbase.LoadingDialog;
import com.alfredbase.http.ResultCode;
import com.alfredbase.store.Store;
import com.alfredbase.utils.DialogFactory;
import com.alfredbase.utils.TextTypeFace;
import com.alfredbase.view.Numerickeyboard;
import com.alfredbase.view.Numerickeyboard.KeyBoardClickListener;
import com.alfredselfhelp.R;
import com.alfredselfhelp.global.App;
import com.alfredselfhelp.global.SyncCentre;
import com.alfredselfhelp.utils.UIHelp;

import java.util.HashMap;
import java.util.Map;

public class EmployeeID extends BaseActivity implements KeyBoardClickListener {
//	public static final int CONNECT_FAILED = 0x10;
//	public static final int CONNECT_TIMEOUT = 0x11;
	private TextView tv_login_tips;
	private TextView tv_id_1;
	private TextView tv_id_2;
	private TextView tv_id_3;
	private TextView tv_id_4;
	private TextView tv_id_5;
	private Numerickeyboard employee_id_keyboard;
	private TextTypeFace textTypeFace;
	public static final int SYNC_DATA_TAG = 2015;
	public static final int HANDLER_PAIRING_COMPLETE = 1001;

	public static final int UPDATE_ALL_DATA_SUCCESS = 100;
	public static final int UPDATE_ALL_DATA_FAILURE = -100;
	public static final int GET_ORDER_SUCCESS = 101;

	public static final int HANDLER_GET_PLACE_INFO = 1002;
	private int syncDataCount = 0;
	/**
	 * 当前键盘输入对应的状态，0表示输入的员工ID，1表示输入的密码
	 */

	private String employee_ID;
	private boolean doubleBackToExitPressedOnce = false;

	@Override
	protected void initView() {
		super.initView();
		loadingDialog = new LoadingDialog(context);
		setContentView(R.layout.activity_employee_id);
		employee_id_keyboard = (Numerickeyboard) findViewById(R.id.employee_id_keyboard);
		employee_id_keyboard.setKeyBoardClickListener(this);
		employee_id_keyboard.setParams(context);
		tv_id_1 = (TextView) findViewById(R.id.tv_id_1);
		tv_id_2 = (TextView) findViewById(R.id.tv_id_2);
		tv_id_3 = (TextView) findViewById(R.id.tv_id_3);
		tv_id_4 = (TextView) findViewById(R.id.tv_id_4);
		tv_id_5 = (TextView) findViewById(R.id.tv_id_5);
		tv_login_tips = (TextView) findViewById(R.id.tv_login_tips);
		tv_login_tips.setText(getString(R.string.waiter_login_tips1));
		((TextView)findViewById(R.id.tv_app_version)).setText(context.getResources().getString(R.string.version) + App.instance.VERSION);
		initTextTypeFace();
	}

	private void initTextTypeFace() {
		textTypeFace = TextTypeFace.getInstance();
		textTypeFace.setTrajanProRegular((TextView)findViewById(R.id.tv_app_version));
		textTypeFace.setTrajanProRegular((TextView)findViewById(R.id.tv_login_tips));
		textTypeFace.setTrajanProRegular(tv_id_1);
		textTypeFace.setTrajanProRegular(tv_id_2);
		textTypeFace.setTrajanProRegular(tv_id_3);
		textTypeFace.setTrajanProRegular(tv_id_4);
		textTypeFace.setTrajanProRegular(tv_id_5);
	}

	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what){
				case ResultCode.SUCCESS: {
//					if (needSync) {
						dismissLoadingDialog();
						loadingDialog.setTitle("update all data");
						loadingDialog.show();
						SyncCentre.getInstance().updateAllData(context, handler);
//					}else{
//						App.instance.setSessionStatus(Store.getObject(context, Store.SESSION_STATUS, SessionStatus.class));
//						//TODO startMainPage();
//					}
				}
				break;
				case UPDATE_ALL_DATA_SUCCESS:
//					dismissLoadingDialog();
					// TODO startMainPage();
					App.instance.setPosIp(App.instance.getPairingIp());
					UIHelp.startMain(context);

					break;
				case UPDATE_ALL_DATA_FAILURE:
					dismissLoadingDialog();
					break;
				case ResultCode.SESSION_HAS_CHANGE:
					dismissLoadingDialog();
					DialogFactory.showOneButtonCompelDialog(context, getString(R.string.warning), "Session has changed !", new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							loadingDialog.setTitle("update all data");
							loadingDialog.show();
							SyncCentre.getInstance().updateAllData(context, handler);
						}
					});
					break;

			}
		}
	};
	private static final int KEY_LENGTH = 5;
	private StringBuffer keyBuf = new StringBuffer();

	@Override
	public void onKeyBoardClick(String key) {
		if (keyBuf.length() >= KEY_LENGTH)
			return;
		if (key.equals("X")) {
			if (keyBuf.length() > 0) {
				keyBuf.deleteCharAt(keyBuf.length() - 1);
			}
		} else {
			keyBuf.append(key);
		}
		int key_len = keyBuf.length();
		setPassword(key_len);
		if (key_len == KEY_LENGTH) {
			employee_ID = keyBuf.toString();
			keyBuf.delete(0, key_len);
			setPassword(keyBuf.length());
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("employee_ID", employee_ID);
			loadingDialog.setTitle("Login...");
			loadingDialog.show();
			Store.putString(context, Store.EMPLOYEE_ID, employee_ID);
			SyncCentre.getInstance().login(context, map, handler);
		}
	}


	private void setPassword(int key_len) {
		switch (key_len) {
		case 0: {
			tv_id_1.setText("");
			tv_id_2.setText("");
			tv_id_3.setText("");
			tv_id_4.setText("");
			tv_id_5.setText("");
			break;
		}
		case 1: {
			tv_id_1.setText("*");
			tv_id_2.setText("");
			tv_id_3.setText("");
			tv_id_4.setText("");
			tv_id_5.setText("");
			break;
		}
		case 2: {
			tv_id_1.setText("*");
			tv_id_2.setText("*");
			tv_id_3.setText("");
			tv_id_4.setText("");
			tv_id_5.setText("");
			break;
		}
		case 3: {
			tv_id_1.setText("*");
			tv_id_2.setText("*");
			tv_id_3.setText("*");
			tv_id_4.setText("");
			tv_id_5.setText("");
			break;
		}
		case 4: {
			tv_id_1.setText("*");
			tv_id_2.setText("*");
			tv_id_3.setText("*");
			tv_id_4.setText("*");
			tv_id_5.setText("");
			break;
		}
		case 5: {
			tv_id_1.setText("*");
			tv_id_2.setText("*");
			tv_id_3.setText("*");
			tv_id_4.setText("*");
			tv_id_5.setText("*");
			break;
		}
		default:
			break;
		}
	}

	@Override
	public void onBackPressed() {
		 if (doubleBackToExitPressedOnce) {
		        super.onBackPressed();
		        return;
		    }

		    this.doubleBackToExitPressedOnce = true;
		    UIHelp.showToast(this, context.getResources().getString(R.string.exit_program));

		BaseApplication.postHandler.postDelayed(new Runnable() {

		        @Override
		        public void run() {
		            doubleBackToExitPressedOnce=false;                       
		        }
		    }, 2000);
	}
}
