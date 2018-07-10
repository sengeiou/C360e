package com.alfredbase.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alfredbase.BaseActivity;
import com.alfredbase.BaseApplication;
import com.alfredbase.R;
import com.alfredbase.javabean.model.PrinterDevice;
import com.alfredbase.javabean.system.VersionUpdate;
import com.alfredbase.javabean.temporaryforapp.AppOrder;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DialogFactory {


	public static void commonTwoBtnDialog(BaseActivity activity,
										  String title, String content,
										  String leftText, String rightText,
										  OnClickListener leftListener,
										  OnClickListener rghtListener){
		commonTwoBtnDialog(activity, title, content, leftText, rightText, leftListener, rghtListener, false);
	}

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
			final OnClickListener rghtListener, final boolean canBack) {
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
				dialog.setCancelable(canBack);
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

	/**
	 * 输入框的dialog
	 * @param activity
	 * @param title
	 * @param content
	 * @param leftText
	 * @param rightText
	 * @param leftListener
     * @param rghtListener
     */
	public static void commonTwoBtnInputDialog(final BaseActivity activity,
										  final String title, final String content, final String leftText, final String rightText,
										  final OnClickListener leftListener,
										  final OnClickListener rghtListener) {
		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				final Dialog dialog = new Dialog(activity, R.style.base_dialog);
				final View view = LayoutInflater.from(activity).inflate(
						R.layout.dialog_input, null);
				((TextView) view.findViewById(R.id.tv_title)).setText(title);
				((TextView) view.findViewById(R.id.tv_content)).setText(content);
				((TextView) view.findViewById(R.id.tv_left)).setText(leftText);
				((TextView) view.findViewById(R.id.tv_right)).setText(rightText);
				final EditText editText = (EditText) view.findViewById(R.id.et_input);
				dialog.setCancelable(false);
				dialog.setCanceledOnTouchOutside(false);
				dialog.setContentView(view);
				view.findViewById(R.id.tv_left).setOnClickListener(
						new OnClickListener() {

							@Override
							public void onClick(final View v) {
								dialog.dismiss();
								CommonUtil.hideSoftkeyBoard(activity);
								BaseApplication.postHandler.postDelayed(new Runnable() {
									@Override
									public void run() {
								if (leftListener != null)
									leftListener.onClick(v);
									}
								},500);
							}
						});
				view.findViewById(R.id.tv_right).setOnClickListener(
						new OnClickListener() {

							@Override
							public void onClick(View v) {

								String num = editText.getText().toString();
								try{
									if(BH.getBD(num).compareTo(BH.getBD(100000)) > 0){
										((EditText) view.findViewById(R.id.et_input)).setText("");
										return;
									}
								}catch (Exception e){
									return;
								}
								dialog.dismiss();
								CommonUtil.hideSoftkeyBoard(activity);
								BaseApplication.postHandler.postDelayed(new Runnable() {
									@Override
									public void run() {
										if (rghtListener != null)
											rghtListener.onClick(editText);
									}
								},500);
							}
						});
				if (activity == null || activity.isFinishing())
					return;
				dialog.show();
			}
		});

	}

	public static void showQrCodeDialog(final BaseActivity activity, String qrCodeText, final String tableName, final OnClickListener printOnClickListener){
		try {
			String content = URLEncoder.encode(qrCodeText, "UTF-8");
			QRCodeWriter writer = new QRCodeWriter();
			Map<EncodeHintType, ErrorCorrectionLevel> map = new HashMap<EncodeHintType, ErrorCorrectionLevel>();
			map.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
			BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE, 400, 400, map);
			final Bitmap bitmap = BitmapUtil.bitMatrix2Bitmap(matrix);
			final Dialog dialog = new Dialog(activity, R.style.qrcode_dialog);
			View view = LayoutInflater.from(activity).inflate(
					R.layout.dialog_qrcode, null);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			dialog.setContentView(view);
			((ImageView)view.findViewById(R.id.iv_qrcode)).setImageBitmap(bitmap);
			((TextView)view.findViewById(R.id.tv_table_name)).setText(tableName);
			view.findViewById(R.id.btn_qrcode_print).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					printOnClickListener.onClick(v);
					dialog.dismiss();
				}
			});
			view.findViewById(R.id.btn_qrcode_back).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			view.findViewById(R.id.btn_qrcode_save).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					String name = tableName;
					if(TextUtils.isEmpty(tableName)){
						name = "Kiosk";
					}
					BitmapUtil.saveImageToGallery(name, activity, bitmap);
				}
			});
			dialog.show();
		} catch (WriterException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}


	public static void showUpdateVersionDialog( final BaseActivity activity, VersionUpdate versionUpdate, final OnClickListener onClickListener, final OnClickListener nextListener){
		try {
			final Dialog dialog = new Dialog(activity, R.style.qrcode_dialog);
			View view = LayoutInflater.from(activity).inflate(
					R.layout.dialog_update, null);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			dialog.setContentView(view);
			TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
			StringBuffer title = new StringBuffer(activity.getString(R.string.has_new_version));
			((TextView)view.findViewById(R.id.tv_version)).setText("New Version:" + versionUpdate.getVersionName());
			((TextView)view.findViewById(R.id.tv_description)).setText(versionUpdate.getDescription());
			view.findViewById(R.id.btn_update_now).setOnClickListener(onClickListener);
			if(versionUpdate.getForceUpdate() == 1){
				title.append("\nPlease update now");
				view.findViewById(R.id.btn_update_later).setVisibility(View.GONE);
			}else{
				view.findViewById(R.id.btn_update_later).setVisibility(View.VISIBLE);
			}
			tv_title.setText(title.toString());
			view.findViewById(R.id.btn_update_later).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
					if(nextListener != null){
						nextListener.onClick(v);
					}

				}
			});
			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
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





	public static void changeDialogOrder(final BaseActivity activity, final String  changeNum,final OnClickListener exitListener) {
		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {

				final Dialog dialog = new Dialog(activity, R.style.change_dialog);
				View view = LayoutInflater.from(activity).inflate(
						R.layout.dialog_item_change, null);
				Window dialogWindow = dialog.getWindow();
				WindowManager.LayoutParams lp = dialogWindow.getAttributes();
				dialogWindow.setGravity(Gravity.CENTER);
				lp.y = ScreenSizeUtil.dip2px(activity, 0); // 新位置Y坐标
				dialogWindow.setAttributes(lp);
				dialog.setCancelable(false);
				dialog.setCanceledOnTouchOutside(true);
				dialog.setContentView(view);
				dialog.setOwnerActivity(activity);
				TextView tv_num = (TextView)view.findViewById(R.id.tv_change_num);
				tv_num.setText(changeNum + "");


				TextTypeFace textTypeFace = TextTypeFace.getInstance();

				textTypeFace.setTrajanProBlod((TextView) view
						.findViewById(R.id.tv_dia_change));
				view.findViewById(R.id.ll_change).setOnClickListener(
						new OnClickListener() {

							@Override
							public void onClick(View v) {
								dialog.dismiss();
								if (exitListener != null)
									exitListener.onClick(v);
							}
						});
				if (activity == null || activity.isFinishing())
					return;
				dialog.show();
				BaseApplication.postHandler.postDelayed(new Runnable() {

					@Override
					public void run() {
						if(dialog != null && dialog.isShowing() && activity != null && !activity.isFinishing())
							dialog.dismiss();
					}
				}, 20*1000);
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
				BaseApplication.postHandler.postDelayed(new Runnable() {

					@Override
					public void run() {
						if(dialog != null && dialog.isShowing() && activity != null && !activity.isFinishing())
							dialog.dismiss();
					}
				}, 5*1000);
			}
		});

	}

	public static void  showSelectPrinterDialog(final BaseActivity activity, final DialogCallBack dialogCallBack, Map<Integer, PrinterDevice> map){
		try {
			final Dialog dialog = new Dialog(activity, R.style.qrcode_dialog);
			View view = LayoutInflater.from(activity).inflate(
					R.layout.dialog_printer_list, null);
			dialog.setCancelable(true);
			dialog.setCanceledOnTouchOutside(false);
			dialog.setContentView(view);

			Collection<PrinterDevice> valueCollection = map.values();
			final List<PrinterDevice> list = new ArrayList<PrinterDevice>(valueCollection);

			ListView listView = (ListView) view.findViewById(R.id.lv_printer);
			PrinterAdapter printerAdapter = new PrinterAdapter(activity, list);
			listView.setAdapter(printerAdapter);
			listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					if(ButtonClickTimer.canClick()) {
						PrinterDevice printerDevice = list.get(position);
						dialogCallBack.callBack(printerDevice);
						dialog.dismiss();
					}
				}
			});
			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public interface DialogCallBack{
		void callBack(PrinterDevice printerDevice);
	}

	private static class PrinterAdapter extends BaseAdapter{
		private List<PrinterDevice> list = new ArrayList<PrinterDevice>();
		private LayoutInflater inflater;
		public PrinterAdapter(Context context, List<PrinterDevice> list){
			this.list = list;
			inflater = LayoutInflater.from(context);
		}
		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.printer_item, null);
				holder = new ViewHolder();
				holder.tv_printer_name = (TextView) convertView.findViewById(R.id.tv_printer_name);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			PrinterDevice printerDevice = list.get(position);
			holder.tv_printer_name.setText(TextUtils.isEmpty(printerDevice.getPrinterName()) ? printerDevice.getName() : printerDevice.getPrinterName() + "\nIP:" + printerDevice.getIP());
			return convertView;
		}

		class ViewHolder{
			TextView tv_printer_name;
		}
	}

}
