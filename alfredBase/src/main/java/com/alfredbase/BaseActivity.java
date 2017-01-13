package com.alfredbase;

import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.alfredbase.javabean.TableInfo;
import com.alfredbase.utils.ButtonClickTimer;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.umeng.analytics.MobclickAgent;

public class BaseActivity extends FragmentActivity implements OnClickListener {
	protected BaseActivity context;
	protected Dialog compelDialog;
	protected Dialog oneButtonCompelDialog;
	public LoadingDialog loadingDialog;
	protected NotificationManager mNotificationManager;
	protected static DisplayImageOptions display = new DisplayImageOptions.Builder() // 圆角边处理的头像
	.cacheInMemory(true) // 缓存到内存，设置true则缓存到内存
	.cacheOnDisk(true) // 缓存到本地磁盘,设置true则缓存到磁盘
//	.showImageForEmptyUri(R.drawable.image_default) // 设置尚未加载，或者无图片的默认图片
//	.showImageOnFail(R.drawable.image_default) // 设置加载图片失败时的默认图片
	.displayer(new RoundedBitmapDisplayer(0)) // 可以继承BitmapDisplayer接口来实现bitmap的其他特效，再此非0是实现圆角边特效
	.build();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
//		Window window = getWindow();
//		WindowManager.LayoutParams params = window.getAttributes();
//		params.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//				| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//				| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//				| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//				| View.SYSTEM_UI_FLAG_FULLSCREEN
////				| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
//		window.setAttributes(params);
//		//状态栏 @ 顶部
//		window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//A
//		//导航栏 @ 底部
//		window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);//B
		super.onCreate(savedInstanceState);
		BaseApplication.activitys.add(this);
		context = this;
		mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		MobclickAgent.updateOnlineConfig(this);
		initView();
//		getWindow().peekDecorView().setSystemUiVisibility(
//					View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//							| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//							| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//							| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//							| View.SYSTEM_UI_FLAG_FULLSCREEN
//							| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
	}


	protected void initView() {

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		BaseApplication.activitys.remove(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	public void dismissLoadingDialog(){
		if(loadingDialog != null && loadingDialog.isShowing()){
			loadingDialog.dismiss();
		}
	}

	/**
	 * 1.对原始点击事件做了一层封装，在500毫秒内，不应该处理两次或者两次以上的点击
	 *
	 * 2.不应该复写这个方法，而是复写handlerClickEvent
	 */
	@Override
	public void onClick(View v) {
		if (ButtonClickTimer.canClick(v)) {
			handlerClickEvent(v);
		}
	}

	protected void handlerClickEvent(View v) {

	}

	public void selectTable(TableInfo tableInfo){

	}

	/**
	 * 服务器得到数据，非主线程
	 * added by XieJF, 2014-7-23
	 * @param action
	 * @param obj
	 */
	public void httpRequestAction(int action,Object obj){

	}
	public void kotPrintStatus(int action, Object obj) {}



	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if(ButtonClickTimer.canLink())
			BaseApplication.instance.startAD();
		return super.dispatchTouchEvent(ev);
	}

	/**
	 * 特殊的dialog 屏蔽返回按钮
	 * @param title
	 * @param content
	 * @param leftText
	 * @param rightText
	 * @param leftListener
	 * @param rghtListener
	 */
	public void showCompelDialog(String title, String content, String leftText, String rightText,
			final OnClickListener leftListener,
			final OnClickListener rghtListener) {

		compelDialog = new Dialog(context, R.style.base_dialog);
		View view = LayoutInflater.from(context).inflate(
				R.layout.dialog_item_common_two_btn, null);
		((TextView) view.findViewById(R.id.tv_title)).setText(title);
		((TextView) view.findViewById(R.id.tv_content)).setText(content);
		((TextView) view.findViewById(R.id.tv_left)).setText(leftText);
		((TextView) view.findViewById(R.id.tv_right)).setText(rightText);
		compelDialog.setCancelable(false);
		compelDialog.setCanceledOnTouchOutside(false);
		compelDialog.setContentView(view);
		view.findViewById(R.id.tv_left).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						compelDialog.dismiss();
						if (leftListener != null)
							leftListener.onClick(v);
					}
				});
		view.findViewById(R.id.tv_right).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						compelDialog.dismiss();
						if (rghtListener != null)
							rghtListener.onClick(v);
					}
				});
		if (context == null || context.isFinishing())
			return;
		compelDialog.show();

	}

	public void showOneButtonCompelDialog(String title, String content, final OnClickListener buttonListener) {

		oneButtonCompelDialog = new Dialog(context, R.style.base_dialog);
		View view = LayoutInflater.from(context).inflate(
				R.layout.dialog_item_common_one_btn, null);
		((TextView) view.findViewById(R.id.tv_title)).setText(title);
		((TextView) view.findViewById(R.id.tv_content)).setText(content);
		((TextView) view.findViewById(R.id.tv_ok)).setText(context.getResources().getString(R.string.ok));
		oneButtonCompelDialog.setCancelable(false);
		oneButtonCompelDialog.setCanceledOnTouchOutside(false);
		oneButtonCompelDialog.setContentView(view);
		view.findViewById(R.id.tv_ok).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						oneButtonCompelDialog.dismiss();
						if (buttonListener != null)
							buttonListener.onClick(v);
					}
				});
		if (context == null || context.isFinishing())
			return;
		oneButtonCompelDialog.show();

	}

	@Override
	public void onBackPressed() {
		if(oneButtonCompelDialog != null && oneButtonCompelDialog.isShowing()){
			return;
		}
		if(compelDialog != null && compelDialog.isShowing()){
			return;
		}
		super.onBackPressed();
	}
}
