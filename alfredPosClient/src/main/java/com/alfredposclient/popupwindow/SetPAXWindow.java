package com.alfredposclient.popupwindow;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alfredbase.BaseActivity;
import com.alfredbase.javabean.Order;
import com.alfredbase.store.sql.OrderSQL;
import com.alfredbase.utils.AnimatorListenerImpl;
import com.alfredbase.utils.ButtonClickTimer;
import com.alfredbase.utils.ScreenSizeUtil;
import com.alfredbase.utils.TextTypeFace;
import com.alfredposclient.R;
import com.alfredposclient.activity.MainPage;
import com.alfredposclient.global.UIHelp;
import com.alfredposclient.view.MoneyKeyboard;
import com.alfredposclient.view.MoneyKeyboard.KeyBoardClickListener;

public class SetPAXWindow implements OnClickListener, KeyBoardClickListener {
	private static final int DURATION_1 = 300;
	private static final int DURATION_2 = 500;
	public static final int APP_ORDER = 1;
	public static final int GENERAL_ORDER = 0;
	private BaseActivity parent;
	private View parentView;
	private Handler handler;
	private Order order;
	private View contentView;
	private PopupWindow popupWindow;
	private RelativeLayout rl_pax;
	private RelativeLayout rl_pax_panel;
	private MoneyKeyboard paxKeyboard;
	private TextView tv_pax_tips;
	private TextView tv_pax;
	private boolean flag = false;
	private String str;
	// 用于控制是否是APP订单 1为APP订单，0为正常流程
	private int type = 0;
	public SetPAXWindow(BaseActivity parent, View parentView, Handler handler) {
		this.parent = parent;
		this.parentView = parentView;
		this.handler = handler;
	}

	private void init() {
		contentView = LayoutInflater.from(parent).inflate(
				R.layout.popup_set_person_count, null); 

		rl_pax = (RelativeLayout) contentView.findViewById(R.id.rl_pax);
		tv_pax_tips = (TextView) contentView.findViewById(R.id.tv_pax_tips);

		rl_pax_panel = (RelativeLayout) contentView
				.findViewById(R.id.rl_pax_panel);
		tv_pax = (TextView) contentView.findViewById(R.id.tv_pax);
		if (str != null) {
			tv_pax.setText(str + "");
		}
		initTextTypeFace();
		
		paxKeyboard = (MoneyKeyboard) contentView
				.findViewById(R.id.paxKeyboard);
		paxKeyboard.setMoneyPanel(View.GONE);
		paxKeyboard.setKeyBoardClickListener(this);

		popupWindow = new PopupWindow(parentView,
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);

		popupWindow.setContentView(contentView);
		popupWindow.setFocusable(true);
	}

	public void show(int type) {
		if(isShowing()){
			return;
		}
		init();
		flag = false;
		this.type = type;
		popupWindow
				.showAtLocation(parentView, Gravity.LEFT | Gravity.TOP, 0, 0);
		rl_pax_panel.post(new Runnable() {
			@Override
			public void run() {
				AnimatorSet set = new AnimatorSet();
				ObjectAnimator animator1 = ObjectAnimator.ofFloat(rl_pax_panel,
						"y", rl_pax_panel.getY() + rl_pax_panel.getHeight(),
						rl_pax_panel.getY()).setDuration(DURATION_2);

				ObjectAnimator animator2 = ObjectAnimator.ofFloat(
						rl_pax,
						"y",
						rl_pax.getY() + rl_pax.getHeight(),
						rl_pax.getY()
								+ ScreenSizeUtil.getStatusBarHeight(parent))
						.setDuration(DURATION_1);
				set.playTogether(animator1, animator2);
				set.setInterpolator(new DecelerateInterpolator());
				set.start();
			}
		});
	}
	
	public void show(String str, Order order) {
		this.str = str;
		this.order = order;
		show(0);
	}
	

	public void dismiss() {
		if (popupWindow != null && popupWindow.isShowing()) {
			AnimatorSet set = new AnimatorSet();
			ObjectAnimator animator1 = ObjectAnimator.ofFloat(rl_pax_panel,
					"y", rl_pax_panel.getY(),
					rl_pax_panel.getY() + rl_pax_panel.getHeight())
					.setDuration(DURATION_2);

			ObjectAnimator animator2 = ObjectAnimator.ofFloat(rl_pax, "y",
					rl_pax.getY() + ScreenSizeUtil.getStatusBarHeight(parent),
					rl_pax.getY() + rl_pax.getHeight()).setDuration(DURATION_1);
			set.playTogether(animator1, animator2);
			set.setInterpolator(new DecelerateInterpolator());
			set.addListener(new AnimatorListenerImpl() {
				public void onAnimationEnd(Animator animation) {
					super.onAnimationEnd(animation);
					popupWindow.dismiss();
				};
			});
			set.start();
			
			this.str = null;
			this.order = null;
		}
	}

	public boolean isShowing() {
		if (popupWindow != null) {
			return popupWindow.isShowing();
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		if (ButtonClickTimer.canClick(v)) {
			switch (v.getId()) {
			default:
				break;
			}
		}

	}

	@Override
	public void onKeyBoardClick(String key) {
		if ("X".equals(key)) {
			dismiss();
		} else if ("Enter".equals(key)) {
			if (TextUtils.isEmpty(tv_pax.getText().toString())) {
				UIHelp.showToast(parent, parent.getResources().getString(R.string.enter_people));
			}else if (type == APP_ORDER) {
				Message msg = handler.obtainMessage();
				msg = handler.obtainMessage();
				msg.what = MainPage.VIEW_EVENT_SET_APPORDER_TABLE_PACKS;
				msg.obj = tv_pax.getText().toString();
				handler.sendMessage(msg);
				dismiss();
			} else {
				if(order != null){
					if (order != null) {
						order.setPersons(Integer.parseInt((String) tv_pax.getText().toString()));
						OrderSQL.updateOrderPersions(Integer.parseInt((String) tv_pax.getText().toString()),order.getId());
					}
				}
				Message msg = handler.obtainMessage();
				msg = handler.obtainMessage();
				msg.what = MainPage.VIEW_EVENT_SET_TABLE_PACKS;
				msg.obj = tv_pax.getText().toString();
				handler.sendMessage(msg);

				dismiss();
			}
		} else if ("C".equals(key)) {
			tv_pax.setText("");
		} else if (flag) {
			tv_pax.setText(tv_pax.getText().toString() + key);
		} else {
			tv_pax.setText(key);
			flag = !flag;
		}
	}

	private void initTextTypeFace() {
		TextTypeFace textTypeFace = TextTypeFace.getInstance();
		textTypeFace.setTrajanProRegular(tv_pax_tips);
		textTypeFace.setTrajanProRegular(tv_pax);
	}
}
