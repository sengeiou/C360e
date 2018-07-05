package com.alfredposclient.view.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alfredbase.BaseActivity;
import com.alfredbase.ParamConst;
import com.alfredbase.VerifyDialog;
import com.alfredbase.global.CoreData;
import com.alfredbase.javabean.PamentMethod;
import com.alfredbase.javabean.User;
import com.alfredbase.utils.TextTypeFace;
import com.alfredbase.utils.ToastUtils;
import com.alfredbase.view.NumerickeyboardOne;
import com.alfredbase.view.NumerickeyboardOne.KeyBoardClickListener;
import com.alfredposclient.R;
import com.alfredposclient.activity.MainPage;
import com.alfredposclient.adapter.PamentMethodAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MediaDialog extends Dialog  {

	private Handler handler;
	private Context context;
	private ListView listView;
	private ListView listview;
	VerifyDialog	verifyDialog;
	List<PamentMethod> plist;

	PamentMethodAdapter adapters;

	public MediaDialog(Context context, Handler handler, List<PamentMethod> pamentMethodslist) {
		super(context, com.alfredbase.R.style.Dialog_verify);
		this.handler = handler;
		this.context = context;
		plist=pamentMethodslist;
		init();
	}

	private void init() {
		final AlertDialog dlg = new AlertDialog.Builder(context).create();
		dlg.show();
		dlg.setCancelable(false);
		dlg.setCanceledOnTouchOutside(true);

		Window window = dlg.getWindow();
		window.setContentView(R.layout.dialog_meadia_layout);
		window.setBackgroundDrawableResource(android.R.color.transparent);

		listview = (ListView) window.findViewById(R.id.
				lv_media);

		adapters = new PamentMethodAdapter(context, plist);

		listview.setAdapter(adapters);


		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//				handler.sendMessage(handler
//						.obtainMessage(MainPage.VIEW_EVENT_SHOW_BILL_ON_HOLD));
				PamentMethodAdapter.ViewHolder holder = (PamentMethodAdapter.ViewHolder) view.getTag();

				Toast.makeText(context,"-"+plist.get(position).getNameCh(),Toast.LENGTH_LONG).show();

				if(plist.get(position).getIsverify()==1)
				{
					verifyDialog = new VerifyDialog(context, handler);
					verifyDialog.show("111",null);
				}
				//  pays = (String) list.get(position).get("id");
				System.out.println("======y优惠券fangs======" + position);

				dlg.cancel();



			}
		});

//		setCanceledOnTouchOutside(false);
	}





}
