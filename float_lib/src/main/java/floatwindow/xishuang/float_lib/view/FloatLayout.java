package floatwindow.xishuang.float_lib.view;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;


import floatwindow.xishuang.float_lib.FloatActionController;
import floatwindow.xishuang.float_lib.OnTrainListener;
import floatwindow.xishuang.float_lib.R;

/**
 * Author:xishuang
 * Date:2017.08.01
 * Des:悬浮窗的布局
 */
public class FloatLayout extends FrameLayout {
    private final WindowManager mWindowManager;
    private final ImageView mFloatView;
    //private final DraggableFlagView mDraggableFlagView;
    private long startTime;
    private float mTouchStartX;
    private float mTouchStartY;
    private boolean isclick;
    private WindowManager.LayoutParams mWmParams;
    private Context mContext;
    private long endTime;
    private OnClickListener onClickListener;
    public static final String BROADCAST_ACTION = "com.example.whd_alive";
    private BroadcastReceiver mBroadcastReceiver;
    public FloatLayout(Context context) {
        this(context, null);
        mContext = context;

    }


    private OnTrainListener onTrainListener;


    /**
     * 注册回调接口的方法，供外部调用
     * @param
     */

    /**
     * 增加get()方法，供Activity调用
     * @return 下载进度
     */


    public FloatLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater.from(context).inflate(R.layout.float_littlemonk_layout, this);
        //浮动窗口按钮
        mFloatView = (ImageView) findViewById(R.id.float_id);
//        mDraggableFlagView = (DraggableFlagView) findViewById(R.id.main_dfv);
//        mDraggableFlagView.setOnDraggableFlagViewListener(new DraggableFlagView.OnDraggableFlagViewListener() {
//            @Override
//            public void onFlagDismiss(DraggableFlagView view) {
//                //小红点消失的一些操作
//            }
//        });
        FloatActionController.getInstance().setObtainNumber(1);
//        mFloatView.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(mContext,"dianji",Toast.LENGTH_LONG).show();
//            }
//        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 获取相对屏幕的坐标，即以屏幕左上角为原点
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();
        //下面的这些事件，跟图标的移动无关，为了区分开拖动和点击事件
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                startTime = System.currentTimeMillis();
                mTouchStartX = event.getX();
                mTouchStartY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                //图标移动的逻辑在这里
                float mMoveStartX = event.getX();
                float mMoveStartY = event.getY();
                // 如果移动量大于3才移动
                if (Math.abs(mTouchStartX - mMoveStartX) > 3
                        && Math.abs(mTouchStartY - mMoveStartY) > 3) {
                    // 更新浮动窗口位置参数
                    mWmParams.x = (int) (x - mTouchStartX);
                    mWmParams.y = (int) (y - mTouchStartY);
                    mWindowManager.updateViewLayout(this, mWmParams);
                    return false;
                }
                break;
            case MotionEvent.ACTION_UP:
                endTime = System.currentTimeMillis();
                //当从点击到弹起小于半秒的时候,则判断为点击,如果超过则不响应点击事件
                if ((endTime - startTime) > 0.3 * 1000L) {
                    isclick = false;
                } else {
                    isclick = true;
                }
                break;
        }
        //响应点击事件
        if (isclick) {
            Toast.makeText(mContext, "", Toast.LENGTH_SHORT).show();

                FloatActionController.getInstance().onClick();

        }
        return true;
    }

    /**
     * 将小悬浮窗的参数传入，用于更新小悬浮窗的位置。
     *
     * @param params 小悬浮窗的参数
     */
    public void setParams(WindowManager.LayoutParams params) {
        mWmParams = params;
    }

//    /**
//     * 设置小红点显示
//     */
//    public void setDragFlagViewVisibility(int visibility) {
//        mDraggableFlagView.setVisibility(visibility);
//    }
//
//    /**
//     * 设置小红点数量
//     */
//    public void setDragFlagViewText(int number) {
//        mDraggableFlagView.setText(number + "");
//    }
}
