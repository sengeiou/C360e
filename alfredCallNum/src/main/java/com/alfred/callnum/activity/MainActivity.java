package com.alfred.callnum.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
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
import com.alfredbase.utils.AnimatorListenerImpl;

import java.lang.reflect.Field;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends BaseActivity {
    OneFragment oneFragment;
    TwoFragment twoFragment;
    int viewId;
    Timer timer11;
    Timer timer = new Timer();
    MyQueue queue = new MyQueue();
    private int callNumber = 3;
    private int callTime = 2800;
    private ImageView bg;
    public static final int TYPE_AGAIN_CALL = 100;
    public static final int TYPE_ANIMA_END = 101;
    private Boolean animaEnd = true;

    public Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case App.HANDLER_REFRESH_CALL:
                    CallBean callBean = (CallBean) msg.obj;
                    bg.setVisibility(View.GONE);
//                    msg.obj
//                    CallBean callBean = new CallBean();
//                    callBean.setId(0);
                    //    LogUtil.e("HANDLER_REFRESH_CALL",callBean.getCallNumber());
//                     callBean.setType(2);
//                    callBean.setName("A121");

                    if (callBean.getCallType() != App.instance.getMainPageType()) {
                        App.instance.setMainPageType(callBean.getCallType());
                        viewId = callBean.getCallType();
                        reFragment(callBean.getCallType());

                    }


                    queue.enQueue(callBean);

                    if (timer == null) {
                        timer = new Timer();
                        timer.schedule(new MyTimertask(), 1000);
                    }

                    break;
                case TYPE_AGAIN_CALL:

                    timer.schedule(new MyTimertask(), 1000);
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

                    if (!AnimatorListenerImpl.isRunning) {
                        int lon = 1000;
                        try {
                            if (queue.QueueLength() > 0) {

                                CallBean callBean = (CallBean) queue.deQueue();
                                String name = callBean.getCallNumber().toString();
                                if (oneFragment != null) {
                                    oneFragment.addData(0, callBean);
                                }
                                if (twoFragment != null && viewId != 4) {
                                    twoFragment.addData(0, callBean);
                                    twoFragment.getVideoPause(name);
                                }
//                                for (int j = 0; j < callNumber; j++) {
                                    CallNumQueueUtil num1 = new CallNumQueueUtil(name, 1, 0, 1);

                                    CallNumUtil.call(num1);
//                                }

                                animaEnd = false;
                                lon = callNumber * 2500;
                                //   lon=1000;
                            } else {
                                if (twoFragment != null && viewId != 4) {
                                    twoFragment.getVideoAgain();
                                }
                                lon = 1000;
                                timer.schedule(new MyTimertask(), lon);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            //    timer.schedule(new MyTimertask(), lon);
                        }
                    } else {
                        timer.schedule(new MyTimertask(), 500);
                    }
                }
            });
        }

    }


//

    protected void initView() {
        super.initView();
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        viewId = intent.getIntExtra("viewId", 0);
        bg = (ImageView) findViewById(R.id.img_call_bg);
        createFragment();

        CallNumUtil.initVideo(context);
        CallNumUtil.init(context, handler);


        timer.schedule(new MyTimertask(), 1000);
    }



    public void sAnimation(View view) {
        if(view == null){
            return;
        }
        ObjectAnimator oanim1X = ObjectAnimator.ofFloat(
                view, "scaleX", 1.0f, 0.8f).setDuration(750);
        ObjectAnimator oanim1Y = ObjectAnimator.ofFloat(
                view, "scaleY", 1.0f, 0.8f).setDuration(750);
        ObjectAnimator oanim2X = ObjectAnimator.ofFloat(
                view, "scaleX", 0.8f, 1f).setDuration(750);
        ObjectAnimator oanim2Y = ObjectAnimator.ofFloat(
                view, "scaleY", 0.8f, 1f).setDuration(750);

        AnimatorSet aset1 = new AnimatorSet();
        aset1.playTogether(oanim1X, oanim1Y);
        AnimatorSet aset2 = new AnimatorSet();
        aset2.playTogether(oanim2X, oanim2Y);
        AnimatorSet aset3 = new AnimatorSet();
        aset3.playTogether(oanim1X, oanim1Y);
        AnimatorSet aset4 = new AnimatorSet();
        aset4.playTogether(oanim2X, oanim2Y);
        AnimatorSet aset = new AnimatorSet();
        aset.playSequentially(aset1, aset2, aset3, aset4);

        aset.addListener(new AnimatorListenerImpl());
        aset.start();
    }

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
        try {
            if (timer != null) {
                timer.cancel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();

    }
}
