package com.alfredbase.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.alfredbase.BaseActivity;
import com.alfredbase.R;
import com.alfredbase.javabean.temporaryforapp.AppOrder;

public class DialogFactory {

	/**
	 * 通用的，两按钮Dialog
	 * 
	 * @param activity
	 * @param title
	 * @param content
	 * @param leftText
	 * @param rightText
	 * @param leftListener
	 * @param rghtListener
	 */
	public static void commonTwoBtnDialog(final BaseActivity activity,
			final String title, final String content, final String leftText, final String rightText,
			final OnClickListener leftListener,
			final OnClickListener rghtListener) {
		activity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				final Dialog dialog = new Dialog(activity, R.style.base_dialog);
				View view = LayoutInflater.from(activity).inflate(
						R.layout.dialog_item_common_two_btn, null);
				((TextView) view.findViewById(R.id.tv_title)).setText(title);
				((TextView) view.findViewById(R.id.tv_content)).setText(content);
				((TextView) view.findViewById(R.id.tv_left)).setText(leftText);
				((TextView) view.findViewById(R.id.tv_right)).setText(rightText);
				dialog.setCancelable(false);
				dialog.setCanceledOnTouchOutside(false);
				dialog.setContentView(view);
				view.findViewById(R.id.tv_left).setOnClickListener(
						new OnClickListener() {

							@Override
							public void onClick(View v) {
								dialog.dismiss();
								if (leftListener != null)
									leftListener.onClick(v);
							}
						});
				view.findViewById(R.id.tv_right).setOnClickListener(
						new OnClickListener() {

							@Override
							public void onClick(View v) {
								dialog.dismiss();
								if (rghtListener != null)
									rghtListener.onClick(v);
							}
						});
				if (activity == null || activity.isFinishing())
					return;
				dialog.show();
			}
		});
		
	}
	
	/**
	 * * 通用的，一个按钮Dialog
	 * @param activity
	 * @param title
	 * @param content
	 * @param buttonListener
     */
	public static void showOneButtonCompelDialog(final BaseActivity activity, final String title, final String content, final OnClickListener buttonListener) {
		activity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				final Dialog oneButtonCompelDialog = new Dialog(activity, R.style.base_dialog);
				View view = LayoutInflater.from(activity).inflate(
						R.layout.dialog_item_common_one_btn, null);
				((TextView) view.findViewById(R.id.tv_title)).setText(title);
				((TextView) view.findViewById(R.id.tv_content)).setText(content);
				((TextView) view.findViewById(R.id.tv_ok)).setText(activity.getResources().getString(R.string.ok));
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
				if (activity == null || activity.isFinishing())
					return;
				oneButtonCompelDialog.show();
			}
		});
		
	}
	private static int showCount = 0;
	public static void commonOneTimeUpdateDialog(final BaseActivity activity,
			final String title, final String content, final String leftText, final String rightText,
			final OnClickListener leftListener,
			final OnClickListener rghtListener) {
		activity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				if (showCount >= 1) {
					return;
				}

				final Dialog dialog = new Dialog(activity, R.style.base_dialog);
				View view = LayoutInflater.from(activity).inflate(
						R.layout.dialog_item_common_two_btn, null);
				((TextView) view.findViewById(R.id.tv_title)).setText(title);
				((TextView) view.findViewById(R.id.tv_content)).setText(content);
				((TextView) view.findViewById(R.id.tv_left)).setText(leftText);
				((TextView) view.findViewById(R.id.tv_right)).setText(rightText);
				dialog.setCancelable(false);
				dialog.setCanceledOnTouchOutside(false);
				dialog.setContentView(view);
				dialog.setOnDismissListener(new OnDismissListener() {
					
					@Override
					public void onDismiss(DialogInterface arg0) {
						showCount--;
					}
				});
				view.findViewById(R.id.tv_left).setOnClickListener(
						new OnClickListener() {

							@Override
							public void onClick(View v) {
								dialog.dismiss();
								if (leftListener != null)
									leftListener.onClick(v);
							}
						});
				view.findViewById(R.id.tv_right).setOnClickListener(
						new OnClickListener() {

							@Override
							public void onClick(View v) {
								dialog.dismiss();
								if (rghtListener != null)
									rghtListener.onClick(v);
							}
						});
				if (activity == null || activity.isFinishing())
					return;
				dialog.show();
				showCount++;
			}
		});
		
	}
	
	private static int compulsoryCount = 0;
	public static void compulsoryUpdateDialog(final BaseActivity activity,
			final String title, final String content,
			final OnClickListener listener) {
		activity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				if (compulsoryCount >= 1) {
					return;
				}

				final Dialog dialog = new Dialog(activity, R.style.base_dialog);
				View view = LayoutInflater.from(activity).inflate(
						R.layout.dialog_item_common_one_btn, null);
				((TextView) view.findViewById(R.id.tv_title)).setText(title);
				((TextView) view.findViewById(R.id.tv_content)).setText(content);
				((TextView) view.findViewById(R.id.tv_ok)).setText(activity.getResources().getString(R.string.ok));
				dialog.setCancelable(false);
				dialog.setCanceledOnTouchOutside(false);
				dialog.setContentView(view);
				dialog.setOnDismissListener(new OnDismissListener() {
					
					@Override
					public void onDismiss(DialogInterface arg0) {
						compulsoryCount--;
					}
				});
				view.findViewById(R.id.tv_ok).setOnClickListener(
						new OnClickListener() {

							@Override
							public void onClick(View v) {
								dialog.dismiss();
								if (listener != null)
									listener.onClick(v);
							}
						});
				if (activity == null || activity.isFinishing())
					return;
				dialog.show();
				compulsoryCount++;
			}
		});
		
	}
	
	
    public static void alertDialog (final Activity activity,final String title, final String content) {
    	activity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				AlertDialog.Builder builder1 = new AlertDialog.Builder(activity);
		    	builder1.setTitle(title);
		    	builder1.setMessage(content);
		    	builder1.setCancelable(true);
		    	builder1.setNeutralButton(android.R.string.ok,
		    	        new DialogInterface.OnClickListener() {
		    				public void onClick(DialogInterface dialog, int id) {
		    					dialog.cancel();
		    	    }
		    	});

		    	AlertDialog alert11 = builder1.create();
				if (activity == null || activity.isFinishing())
					return;
		    	alert11.show(); 
			}
		});
    	   	
    }


	public static void topDialogOrder(final BaseActivity activity, final AppOrder tempAppOrder, final OnClickListener leftListener,
									  final OnClickListener rghtListener) {
		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if(tempAppOrder == null)
					return;
				final Dialog dialog = new Dialog(activity, R.style.top_dialog);
				View view = LayoutInflater.from(activity).inflate(
						R.layout.dialog_item_top_show, null);
				Window dialogWindow = dialog.getWindow();
				WindowManager.LayoutParams lp = dialogWindow.getAttributes();
				dialogWindow.setGravity(Gravity.TOP);
				lp.y = ScreenSizeUtil.dip2px(activity, 25); // 新位置Y坐标
				dialogWindow.setAttributes(lp);
				dialog.setCancelable(false);
				dialog.setCanceledOnTouchOutside(true);
				dialog.setContentView(view);
				dialog.setOwnerActivity(activity);
				TextView tv_msg_info = (TextView)view.findViewById(R.id.tv_msg_info);
				tv_msg_info.setText(activity.getResources().getString(R.string.num) + tempAppOrder.getOrderCount().intValue() + "，" + activity.getResources().getString(R.string.total) + "$" +  BH.getBD(tempAppOrder.getTotal()).toString() + "");
				view.findViewById(R.id.tv_left).setVisibility(View.INVISIBLE);
				view.findViewById(R.id.tv_left).setOnClickListener(
						new OnClickListener() {

							@Override
							public void onClick(View v) {
								dialog.dismiss();
								if (leftListener != null)
									leftListener.onClick(v);
							}
						});
				view.findViewById(R.id.tv_right).setOnClickListener(
						new OnClickListener() {

							@Override
							public void onClick(View v) {
								dialog.dismiss();
								if (rghtListener != null)
									rghtListener.onClick(v);
							}
						});
				if (activity == null || activity.isFinishing())
					return;
				dialog.show();
				view.postDelayed(new Runnable() {

					@Override
					public void run() {
						if(dialog != null && dialog.isShowing() && activity != null && !activity.isFinishing())
							dialog.dismiss();
					}
				}, 5*1000);
			}
		});

	}
}
