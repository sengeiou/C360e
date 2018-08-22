package com.alfred.callnum.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.alfred.callnum.R;
import com.alfred.callnum.adapter.CallBean;
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

import java.lang.reflect.Field;
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
    Timer timer = new Timer();
    MyQueue queue = new MyQueue();
    private int callNumber = 3;
    private int callTime = 2800;
    private ImageView  bg;

    public Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case App.HANDLER_REFRESH_CALL:
                    CallBean callBean= (CallBean) msg.obj;
                    bg.setVisibility(View.GONE);
//                    msg.obj
//                    CallBean callBean = new CallBean();
//                    callBean.setId(0);
                //    LogUtil.e("HANDLER_REFRESH_CALL",callBean.getCallNumber());
//                     callBean.setType(2);
//                    callBean.setName("A121");

                    if(callBean.getCallType()!=App.instance.getMainPageType()){
                        App.instance.setMainPageType(callBean.getCallType());
                          viewId=callBean.getCallType();
                        reFragment(callBean.getCallType());

                    }


                    queue.enQueue(callBean);

                    if (timer == null) {
                        timer = new Timer();
                        timer.schedule(new MyTimertask(), 1000);
                    }

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

                    if (queue.QueueLength() > 0) {

                        CallBean callBean = (CallBean) queue.deQueue();
                        String name = callBean.getCallNumber().toString();
                        if (oneFragment != null) {
                            oneFragment.addData(0, callBean);
                        }
                        if (twoFragment != null&&viewId!=4) {
                            twoFragment.addData(0, callBean);
                            twoFragment.getVideoPause(name);
                        }
                        for (int j = 0; j < callNumber; j++) {
                            CallNumQueueUtil num1 = new CallNumQueueUtil(name, 1, 0, 1);

                            CallNumUtil.call(num1);
                        }
                    } else {
                        cancel();
                        queue.clear();
                        if (timer != null) {
                            timer.cancel();
                            timer = null;
                        }
                        if (twoFragment != null&&viewId!=4) {
                            twoFragment.getVideoAgain();
                        }
                    }

                }
            });


            timer.schedule(new MyTimertask(), callNumber * 2500);

        }

    }


    /**
     * @param volume 音量大小
     * @param object VideoView实例
     */
    public void setVolume(float volume, Object object) {
        try {
            Class<?> forName = Class.forName("android.widget.VideoView");
            Field field = forName.getDeclaredField("mMediaPlayer");
            field.setAccessible(true);
            MediaPlayer mMediaPlayer = (MediaPlayer) field.get(object);
            mMediaPlayer.setVolume(volume, volume);
        } catch (Exception e) {
        }
    }

    protected void initView() {
        super.initView();
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        viewId = intent.getIntExtra("viewId", 0);
        bg=(ImageView)findViewById(R.id.img_call_bg);
        createFragment();

        CallNumUtil.initVideo(context);
        CallNumUtil.init(context, handler);

        //	timer11 = new Timer();
        //	timer11.schedule(new MyTimertask(),1000);
//        for (int j = 0; j < 4; j++) {
//            CallBean call = new CallBean();
//            call.setCallNumber("A12" + j);
//            call.setCallTag(j + 1);
//
//
//            queue.enQueue(call);
//            //   queue.enQueue("A12" + j);
////			if(j==4){
////				queue.enQueue("A120");
////			}
//
//
//        }
        timer.schedule(new MyTimertask(), 1000);
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

        handler.sendMessage(handler.obtainMessage(action, obj));
    }




    public void reFragment(int viewId) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (viewId == 4) {
            oneFragment = new OneFragment();
            oneFragment.setViewId(viewId, handler);
            fragmentTransaction.replace(R.id.one_fragment, oneFragment);
        } else {
            twoFragment = new TwoFragment();
            twoFragment.setViewId(viewId, handler);
            fragmentTransaction.replace(R.id.one_fragment, twoFragment);
        }

        fragmentTransaction.commitAllowingStateLoss();
    }

    public void createFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (viewId == 4) {
            oneFragment = new OneFragment();
            oneFragment.setViewId(viewId, handler);
            fragmentTransaction.add(R.id.one_fragment, oneFragment);
        } else {
            twoFragment = new TwoFragment();
            twoFragment.setViewId(viewId, handler);
            fragmentTransaction.add(R.id.one_fragment, twoFragment);
        }

        fragmentTransaction.commit();
    }

    @Override
    protected void handlerClickEvent(View v) {
        super.handlerClickEvent(v);
    }


    @Override
    protected void onPause() {
        super.onPause();
       // App.instance.setSave();

    }

    @Override
    protected void onDestroy() {
     //   App.instance.setSave();
        super.onDestroy();

    }
}
