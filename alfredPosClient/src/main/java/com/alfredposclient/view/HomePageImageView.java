package com.alfredposclient.view;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


import com.alfredbase.store.Store;
import com.alfredbase.utils.CommonUtil;
import com.alfredposclient.R;
import com.alfredposclient.global.App;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by Zun on 2016/12/16.
 */

public class HomePageImageView extends LinearLayout {

    private ImageView home_page_img;
    private RelativeLayout home_page_sv;
    private SurfaceView sv;
    private List<String> list = new ArrayList<>();
    private Context context;
    private MediaPlayer mediaPlayer;
    private SurfaceHolder surfaceHolder;
    private int curPosition = 0;
    private boolean isSurfaceCreated = false; //surface是否已经创建好  
    private File file;

    public HomePageImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public HomePageImageView(Context context, List<String> list) {
        super(context);
        this.list = list;
        this.context = context;
        initView(context);
    }

    public void refresh(List<String> list){
        this.list = list;
        refreshUI();
    }

    /**
     * 释放播放器资源
     */
    public void releasePlayer(){
        if (mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void restart(){
        if (!isSurfaceCreated){
            CreateSurface();
        }
    }

    /**
     * 视频暂停
     */
    public void pause(){
        if (mediaPlayer != null && mediaPlayer.isPlaying()){
            curPosition = mediaPlayer.getCurrentPosition();
            mediaPlayer.pause();
        }
    }

    public void resume(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (file != null) {
                    playMedia(file, curPosition);
                }
            }
        }, 10);
    }

    private void initView(Context context){
        View view = inflate(context, R.layout.home_page_img, this);
        home_page_img = (ImageView) view.findViewById(R.id.home_page_img);
        home_page_sv = (RelativeLayout) view.findViewById(R.id.home_page_sv);
        sv = (SurfaceView) findViewById(R.id.sv);
        mediaPlayer = new MediaPlayer();
        timeTask();
        refreshUI();
    }

    int index = 0;
    private void refreshUI(){
        boolean flag = Store.getBoolean(context, Store.VIDEO_IMAGE, true);

        if (Store.getInt(App.getTopActivity(), Store.SUNMI_STYLE)!=Store.SUNMI_VIDEO_TEXT) {
            home_page_img.setVisibility(VISIBLE);
            home_page_sv.setVisibility(GONE);
            if (list != null && list.size() > 0) {
                if (list.size() == 1) {
                    home_page_img.setImageURI(Uri.parse(list.get(0)));
                }
            } else {
                home_page_img.setBackgroundResource(R.drawable.picture);
            }
        }else   if (Store.getInt(App.getTopActivity(), Store.SUNMI_STYLE)!=Store.SUNMI_VIDEO_TEXT) {
            home_page_img.setVisibility(GONE);
            home_page_sv.setVisibility(VISIBLE);
            if (!CommonUtil.isNull(list.get(0))){
                file = new File(list.get(0));
                if (file.exists()) {
                    CreateSurface();
                    playMedia(file, curPosition);
                }
            }
        }
    }

    /**
     * 创建视频展示页面
     */
    private void CreateSurface(){
        surfaceHolder = sv.getHolder();
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceHolder.addCallback(callback);
    }

    SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            isSurfaceCreated = true;
            mediaPlayer.setDisplay(surfaceHolder);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            isSurfaceCreated = false;
            if (mediaPlayer.isPlaying()){
                curPosition = mediaPlayer.getCurrentPosition();
                mediaPlayer.stop();
            }
        }
    };

    /**
     * mediaPlayer播放
     */
    private void playMedia(File file, final int currentPosition){
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(file.getAbsolutePath());
//            mediaPlayer.setDisplay(sv.getHolder());
            mediaPlayer.setLooping(true);
            mediaPlayer.prepare();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    int w = mediaPlayer.getVideoWidth();
                    int h = mediaPlayer.getVideoHeight();
                    Log.d("^^^^^^^^^^^^^^^^", w + "" + h + "");
                    if (w != 0 && h != 0){
                        surfaceHolder.setFixedSize(w / 2, h);
                    }
                    mediaPlayer.seekTo(currentPosition);
                    mediaPlayer.start();
                }
            });

            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    return false;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    Observable<Long> observeOn;
    private void timeTask(){
        observeOn = Observable.timer(3, TimeUnit.SECONDS);
        observeOn.observeOn(AndroidSchedulers.mainThread())
                .subscribe(action);
    }

    Action1 action = new Action1<Long>() {
        @Override
        public void call(Long aLong) {
            if(list!=null && list.size() > 1) {
                home_page_img.setVisibility(VISIBLE);
                home_page_sv.setVisibility(GONE);
                index = (index + 1) % list.size();
                home_page_img.setImageURI(Uri.parse(list.get(index)));
            }
            timeTask();
        }
    };

}
