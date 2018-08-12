package com.alfredposclient.activity.kioskactivity.subpos;

import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alfredbase.BaseActivity;
import com.alfredbase.BaseApplication;
import com.alfredbase.LoadingDialog;
import com.alfredbase.ParamConst;
import com.alfredbase.global.CoreData;
import com.alfredbase.http.ResultCode;
import com.alfredbase.javabean.Restaurant;
import com.alfredbase.javabean.RevenueCenter;
import com.alfredbase.store.sql.OrderSQL;
import com.alfredbase.store.sql.TableInfoSQL;
import com.alfredbase.utils.TextTypeFace;
import com.alfredbase.view.Numerickeyboard;
import com.alfredbase.view.Numerickeyboard.KeyBoardClickListener;
import com.alfredposclient.R;
import com.alfredposclient.adapter.HomePageViewPagerAdapter;
import com.alfredposclient.global.App;
import com.alfredposclient.global.SubPosSyncCentre;
import com.alfredposclient.global.UIHelp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SubPosLogin extends BaseActivity implements KeyBoardClickListener {

	private static final int KEY_LENGTH = 5;
	private static final int STATE_IN_ENTER_ID = 0;
	private static final int STATE_IN_ENTER_PASSWORD = 1;
	/**
	 * 当前键盘输入对应的状态，0表示输入的员工ID，1表示输入的密码
	 */
	private int state = STATE_IN_ENTER_ID;
	private ViewPager viewPager;
	private ArrayList<View> views = new ArrayList<View>();
	private View login_view_1;
	private View login_view_2;
	private Numerickeyboard numerickeyboard;
	private TextView tv_psw_1;
	private TextView tv_psw_2;
	private TextView tv_psw_3;
	private TextView tv_psw_4;
	private TextView tv_psw_5;
	private StringBuffer keyBuf = new StringBuffer();
	private String employee_ID;
	private String password;
	private boolean doubleBackToExitPressedOnce = false;
	private TextView tv_title_name_one;
	private TextView tv_title_name_two;
	private TextTypeFace textTypeFace;

	public static final int UPDATE_ALL_DATA_SUCCESS = 100;
	public static final int UPDATE_ALL_DATA_FAILURE = -100;
	public static final int GET_ORDER_SUCCESS = 101;

	@Override
	protected void initView() {
		super.initView();
		setContentView(R.layout.activity_login);
		viewPager = (ViewPager) findViewById(R.id.viewPager);
		login_view_1 = getLayoutInflater().inflate(R.layout.login_view_1, null);
		views.add(login_view_1);
		login_view_2 = getLayoutInflater().inflate(R.layout.login_view_2, null);
		views.add(login_view_2);
		viewPager.setAdapter(new HomePageViewPagerAdapter(views));
		login_view_1.findViewById(R.id.btn_sign_up).setOnClickListener(this);
		loadingDialog = new LoadingDialog(context);
		String title = getString(R.string.cashier_login_tips1);
		Restaurant rest = CoreData.getInstance().getRestaurant();
		RevenueCenter revenueCenter = App.instance.getRevenueCenter();
		String name = "";
		if(rest != null){
			name = rest.getRestaurantName();
			if(revenueCenter != null){
				name = name + "\n" + revenueCenter.getRevName();
			}
		}
		((TextView)(login_view_2.findViewById(R.id.tv_rest_name))).setText(name);
		((TextView) (login_view_2.findViewById(R.id.tv_login_tips))).setText(title);

		numerickeyboard = (Numerickeyboard) login_view_2
				.findViewById(R.id.numerickeyboard);
		numerickeyboard.setKeyBoardClickListener(this);
		numerickeyboard.setParams(context);
		tv_psw_1 = (TextView) login_view_2.findViewById(R.id.tv_psw_1);
		tv_psw_2 = (TextView) login_view_2.findViewById(R.id.tv_psw_2);
		tv_psw_3 = (TextView) login_view_2.findViewById(R.id.tv_psw_3);
		tv_psw_4 = (TextView) login_view_2.findViewById(R.id.tv_psw_4);
		tv_psw_5 = (TextView) login_view_2.findViewById(R.id.tv_psw_5);
		App.instance.finishAllActivityExceptOne(getClass());
		initTitle();
		initTextTypeFace(login_view_1,login_view_2);
		
		((TextView)findViewById(R.id.tv_app_version)).setText(context.getResources().getString(R.string.version)+": " + App.instance.VERSION);
		App.instance.showWelcomeToSecondScreen();
	}

	public void initTitle(){
		tv_title_name_one = (TextView) findViewById(R.id.tv_title_name_one);
		tv_title_name_one.setVisibility(View.GONE);
		tv_title_name_two = (TextView) findViewById(R.id.tv_title_name_two);
		tv_title_name_two.setVisibility(View.GONE);
	}
	
	public void initTextTypeFace(View login_view_1, View login_view_2){
		textTypeFace = TextTypeFace.getInstance();
		textTypeFace.setTrajanProRegular((TextView)findViewById(R.id.tv_app_version));
		textTypeFace.setTrajanProRegular((TextView)login_view_1.findViewById(R.id.tv_sign_up_tips));
		textTypeFace.setTrajanProRegular((Button)login_view_1.findViewById(R.id.btn_sign_up));
		textTypeFace.setTrajanProBlod((TextView)login_view_2.findViewById(R.id.tv_rest_name));
		textTypeFace.setTrajanProRegular((TextView)login_view_2.findViewById(R.id.tv_login_tips));
		textTypeFace.setTrajanProRegular(tv_psw_1);
		textTypeFace.setTrajanProRegular(tv_psw_2);
		textTypeFace.setTrajanProRegular(tv_psw_3);
		textTypeFace.setTrajanProRegular(tv_psw_4);
		textTypeFace.setTrajanProRegular(tv_psw_5);
	}
	
	@Override
	public void handlerClickEvent(View v) {
		super.handlerClickEvent(v);
		switch (v.getId()) {
		case R.id.btn_sign_up: {
			viewPager.setCurrentItem(1);
			break;
		}
		default:
			break;
		}
	}


	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what){
				case ResultCode.SUCCESS: {
					dismissLoadingDialog();
					int subPosStatus = (int) msg.obj;
					if (App.instance.getSubPosStatus() == ParamConst.SUB_POS_STATUS_CLOSE) {
						loadingDialog.setTitle("update all data");
						loadingDialog.show();
						SubPosSyncCentre.getInstance().updaetAllData(context, handler);
					}else{
						startMainPage();
					}
					App.instance.setSubPosStatus(subPosStatus);
				}
					break;
				case UPDATE_ALL_DATA_SUCCESS:
					dismissLoadingDialog();
					startMainPage();
					finish();
					break;
				case UPDATE_ALL_DATA_FAILURE:
					dismissLoadingDialog();
					break;
				case GET_ORDER_SUCCESS:
					dismissLoadingDialog();
					UIHelp.startMainPageKiosk(context);
					break;

			}
		}
	};

	private void startMainPage(){
		long businessDate = App.instance.getBusinessDate();
		if(OrderSQL.getUnfinishedOrderAtTable(TableInfoSQL.getKioskTable().getPosId(), businessDate) != null) {
			UIHelp.startMainPageKiosk(context);
		}else{
			loadingDialog.setTitle("loading");
			loadingDialog.show();
			Map<String, Object> parameters = new HashMap<>();
			parameters.put("userId", App.instance.getUser().getId());
			SubPosSyncCentre.getInstance().getOrder(context, parameters, handler);
		}

	}

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
			if (state == STATE_IN_ENTER_ID) {
				String title = getString(R.string.cashier_login_tips1);
				((TextView) (findViewById(R.id.tv_login_tips))).setText(title);
				state = STATE_IN_ENTER_PASSWORD;
				employee_ID = keyBuf.toString();
				keyBuf.delete(0, key_len);
				setPassword(key_len);
			} else if (state == STATE_IN_ENTER_PASSWORD) {
				password = keyBuf.toString();
				keyBuf.delete(0, key_len);
				setPassword(key_len);
				Map<String, Object> parameters = new HashMap<>();
				parameters.put("employeeId", employee_ID);
				parameters.put("password", password);
				loadingDialog.setTitle("Loading");
				loadingDialog.show();
				SubPosSyncCentre.getInstance().login(context, parameters, handler);
			}
		}
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
			tv_psw_1.setText("*");
			tv_psw_2.setText("");
			tv_psw_3.setText("");
			tv_psw_4.setText("");
			tv_psw_5.setText("");
			break;
		}
		case 2: {
			tv_psw_1.setText("*");
			tv_psw_2.setText("*");
			tv_psw_3.setText("");
			tv_psw_4.setText("");
			tv_psw_5.setText("");
			break;
		}
		case 3: {
			tv_psw_1.setText("*");
			tv_psw_2.setText("*");
			tv_psw_3.setText("*");
			tv_psw_4.setText("");
			tv_psw_5.setText("");
			break;
		}
		case 4: {
			tv_psw_1.setText("*");
			tv_psw_2.setText("*");
			tv_psw_3.setText("*");
			tv_psw_4.setText("*");
			tv_psw_5.setText("");
			break;
		}
		case 5: {
			tv_psw_1.setText("*");
			tv_psw_2.setText("*");
			tv_psw_3.setText("*");
			tv_psw_4.setText("*");
			tv_psw_5.setText("*");
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