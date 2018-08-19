package com.alfred.callnum.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.alfred.callnum.R;
import com.alfred.callnum.fragment.OneFragment;
import com.alfred.callnum.fragment.TwoFragment;
import com.alfred.callnum.global.App;
import com.alfred.callnum.utils.CallNumQueueUtil;
import com.alfred.callnum.utils.CallNumUtil;
import com.alfred.callnum.utils.MyQueue;
import com.alfredbase.BaseActivity;
import com.alfredbase.MyBaseActivity;
import com.alfredbase.ParamConst;
import com.alfredbase.http.ResultCode;
import com.alfredbase.javabean.KotItemDetail;
import com.alfredbase.javabean.KotSummary;
import com.alfredbase.store.sql.KotItemDetailSQL;
import com.alfredbase.utils.BarcodeUtil;
import com.alfredbase.utils.DialogFactory;
import com.alfredbase.utils.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static u.aly.dn.i;

public class MainActivity extends BaseActivity {
	OneFragment oneFragment;
	TwoFragment twoFragment;
	int viewId;
	Timer timer11;
   Timer	timer = new Timer();
	MyQueue queue = new MyQueue();


	public Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case App.HANDLER_REFRESH_CALL:

                         queue.enQueue("A123");
					timer.schedule(new MyTimertask(),1000);


					if(oneFragment!=null){
						oneFragment.addData(0,"");
					}
					if(twoFragment!=null){
						twoFragment.addData(0,"");
					}
					break;


				case App.HANDLER_REFRESH_CALL_ON:

					CallNumQueueUtil num = new CallNumQueueUtil("A123,", 1,0,3);
					CallNumUtil.call(num);

					break;
				default:
					break;
			}
		}

		;
	};



	class MyTimertask extends TimerTask {

		@Override
		public void run() {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {

					if(queue.QueueLength()>0){
					LogUtil.e("qqqqqqqqq-",queue.QueuePeek().toString()+"-"+queue.QueueLength());

					String name=queue.deQueue().toString();
						CallNumQueueUtil num1 = new CallNumQueueUtil(name, 1,0,1);

						CallNumUtil.call(num1);
						if(oneFragment!=null) {
							oneFragment.addData(0, name);
						}
						if(twoFragment!=null){
							twoFragment.addData(0, name);
						}
//
					}else {
                     queue.clear();
						timer.cancel();

					}

				}
			});


			timer.schedule(new MyTimertask(), 1000);

		}

	}
	protected void initView() {
		super.initView();
		setContentView(R.layout.activity_main);
		Intent intent = getIntent();
		viewId = intent.getIntExtra("viewId",0);
		createFragment();

		CallNumUtil.initVideo(context);
		CallNumUtil.init(context, handler);

	//	timer11 = new Timer();
	//	timer11.schedule(new MyTimertask(),1000);
		for (int j = 0; j <5 ; j++) {
			queue.enQueue("A12"+j);
			if(j==4){
				queue.enQueue("A120");
			}


		}
		timer.schedule(new MyTimertask(),1000);
	}

//
//	class MyTimertask extends TimerTask {
//
//		@Override
//		public void run() {
//			runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					for (int i = 0; i < 3; i++) {
//
//						CallNumQueueUtil num1 = new CallNumQueueUtil("A123,", 1,0,1);;
//						CallNumUtil.call(num1);
//					}
//
//				}
//			});
//
//
//			timer11.schedule(new MyTimertask(), 1000);
//		}
//
//	}

	@Override
	public void httpRequestAction(int action, Object obj) {

		handler.sendMessage(handler.obtainMessage(action, null));
	}

	public void createFragment() {
		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
		if(viewId==1||viewId==2) {
			oneFragment = new OneFragment();
			oneFragment.setViewId(viewId,handler);
			fragmentTransaction.add(R.id.one_fragment, oneFragment);
		}else {
			twoFragment = new TwoFragment();
			twoFragment.setViewId(viewId,handler);
			fragmentTransaction.add(R.id.one_fragment, twoFragment);
		}

		fragmentTransaction.commit();
	}
	@Override
	protected void handlerClickEvent(View v) {
		super.handlerClickEvent(v);
	}
}
