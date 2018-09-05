package com.alfredselfhelp.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.alfredbase.BaseActivity;
import com.alfredbase.ParamConst;
import com.alfredbase.javabean.Order;
import com.alfredbase.javabean.Tax;
import com.alfredbase.store.sql.TableInfoSQL;
import com.alfredbase.utils.ObjectFactory;
import com.alfredbase.utils.TimeUtil;
import com.alfredselfhelp.R;
import com.alfredselfhelp.global.App;
import com.alfredselfhelp.utils.FileDialog;
import com.alfredselfhelp.utils.PictureSwitch;
import com.alfredselfhelp.utils.TvPref;
import com.alfredselfhelp.utils.UIHelp;
import com.alfredselfhelp.utils.VideoResManager;


public class MainActivity extends BaseActivity {


    Handler mPeriodEventHdr = new Handler();
    String mstrPlayFile = null;
    long play_end_timestamp = 0;
    final int VIDEO_STATE_NONE = 0;
    final int VIDEO_STATE_PLAYING = 1;
    final int VIDEO_STATE_AD = 2;
    final int VIDEO_STATE_QR = 3;
    final int VIDEO_STATE_ONE_COMPLETE = 4;

    private int mVideoState = VIDEO_STATE_NONE;
    int mnVideoWidth = 0;
    int mnVideoHeight = 0;
    private int counter = 0;

    private Button btn_video, btn_picture, btn_empty;
    private VideoView videoView;
    private boolean toPlayVideo = true;
    VideoResManager mVideoResManager;  //视频播放器
    private boolean isGameOpenNow = false;
    PictureSwitch picSwitch;
    View mQueClient;
    int queueing_view_mode = 0;// 不显示排队
    private int miLayoutType = 0;
    boolean mbUpdateVideo = false;
    private final static int LOGIN_STATUS_READY = 5;
    private int mVideoDefaultCurPos = 0;// in milisec
    private int mLoginStatus;
    private Button btn_start;

    private LinearLayout li_select;


    protected void initView() {
        super.initView();
        setContentView(R.layout.activity_main);
        btn_start=(Button)findViewById(R.id.btn_start);
        btn_start.setOnClickListener(this);

        videoView = (VideoView) findViewById(R.id.videoView);
        picSwitch = (PictureSwitch) findViewById(R.id.slImages);
        picSwitch.setInAnimation(MainActivity.this, android.R.anim.fade_in);
        picSwitch.setOutAnimation(MainActivity.this, android.R.anim.fade_out);

        btn_video = (Button)findViewById(R.id.btn_video);
        btn_picture = (Button) findViewById(R.id.btn_picture);
        btn_empty = (Button) findViewById(R.id.btn_empty);
        li_select = (LinearLayout) findViewById(R.id.li_select);
        btn_empty.setOnClickListener(this);
        btn_picture.setOnClickListener(this);
        btn_video.setOnClickListener(this);
        mVideoResManager = new VideoResManager(context);
                 Tax tax=new Tax();
        Order order = ObjectFactory.getInstance().addOrderFromKioskDesktop(
                ParamConst.ORDER_ORIGIN_TABLE, 0,
                TableInfoSQL.getKioskTable(),
                App.instance.getRevenueCenter(),
                App.instance.getUser(),
                App.instance.getSessionStatus(),
                TimeUtil.getNewBusinessDate(),
               tax);
    }



    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            counter++;
            //  prepare();

            mPeriodEventHdr.postDelayed(mRunnable, 4000);
        }

    };


    private Runnable mUpdateUiRunnable = new Runnable() {
        @Override
        public void run() {

            mPeriodEventHdr.postDelayed(mUpdateUiRunnable, 800);

            if (mbUpdateVideo) {
                adjustVideoView();
            }
//            if (mbCalling) {
//                flashingCall();// 叫号闪烁提醒
//            }
//            mUiCounter++;
//
//            if (mLoginStatus > LOGIN_STATUS_CHK_IP
//                    && (mUiCounter % CHECK_VER_PERIOD) == 20) {
//                // checkAppVer();
//            }

            // if (mstvShopAd.playState == 0) {
            // if (mShopAdHideCounter == 0) {
            // mShopAdHideCounter = SHOP_AD_HIDE_TIME;
            // } else {
            // mShopAdHideCounter--;
            // if (mShopAdHideCounter == 0) {
            // mstvShopAd.startScroll(); //广告字幕滚动条
            // }
            // }
            // }
            switch (mVideoState) {
                case VIDEO_STATE_PLAYING:
//                    if ((mUiCounter % ADVIDEO_SHOW_PERIOD) == 0) {
//                        // 进入广告时间
//                        boolean bPlayAd = false;
//                        String adfile = TvPref.readADFile();
//                        if (!adfile.isEmpty()) {
//                            File file = new File(adfile);
//                            if (file.exists() && !file.isDirectory()) {
//                                bPlayAd = true;
//                            }
//                        }
//
//                        if (bPlayAd || mQRCodeStatus == 1) {
//                            if (videoView.isPlaying()) {
//                                mVideoDefaultCurPos = videoView
//                                        .getCurrentPosition();
//                                videoView.stopPlayback();
//                            } else {
//                                mVideoDefaultCurPos = 0;
//                            }
//                            if (bPlayAd) {
//                                startPlayAd(adfile);
//                            } else {
//                                startPlayQR();
//                            }
//                        }
//
//                    }
                    break;
                case VIDEO_STATE_ONE_COMPLETE: {
                    if (System.currentTimeMillis() - play_end_timestamp > 1000) {
                        startPlay(false);// 播放下一个文件
                    }
                }
                break;

                case VIDEO_STATE_NONE:
                    break;
            }
//
//            if (mHeader.getVisibility() == View.VISIBLE
//                    && mUiCounter - stamp > 6) {
//                mHeader.setVisibility(View.GONE);// 自动隐藏标题栏
//            }
        }

    };

    @Override
    protected void handlerClickEvent(View v) {
        super.handlerClickEvent(v);
        Intent intent = new Intent(MainActivity.this, FileDialog.class);
        switch (v.getId())
        {
            case R.id.btn_start:
                UIHelp.startMenu(context);
                break;

            case R.id.btn_picture:
                // 选择图片文件

                intent.putExtra(FileDialog.INP_FILE_FILTER, getResources()
                        .getStringArray(R.array.fileEndingImage));
                intent.putExtra(FileDialog.INP_CURRENT_PATH, TvPref.readVideoFile());
                startActivityForResult(intent, 1);
                break;
            case R.id.btn_video:

                intent.putExtra(FileDialog.INP_FILE_FILTER, getResources()
                        .getStringArray(R.array.fileEndingVideo));
                intent.putExtra(FileDialog.INP_CURRENT_PATH, TvPref.readVideoFile());
                startActivityForResult(intent, 2);
                break;

            case R.id.btn_empty:

                if (videoView.isPlaying()) {
                    videoView.stopPlayback();
                }
                if (picSwitch.isPlaying()) {
                    picSwitch.stopPlay();
                }
                TvPref.saveVideoFile("");
                TvPref.saveImageFilePath("");
//                VideoResManager.DelFilesExcept(ShopInfo.getVideoPath(),
////                        new String[0]);
                videoView.setVisibility(View.GONE);
                picSwitch.setVisibility(View.GONE);
                mVideoResManager.UpdateVideo();
                updateView();




                break;

        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == FileDialog.RESULT_SELECT_FILE) {
            String selFile = data
                    .getStringExtra(FileDialog.RESULT_SELECT_FILEPATH);
            if (requestCode == 1) {
                if (selFile != null) {
                    toPlayVideo = false;
                    TvPref.saveImageFilePath(selFile);
                    TvPref.saveVideoFile("");
                    if (videoView != null && videoView.isPlaying())// 视频结束
                    {
                        //  LogFile.v("正在播放视频  暂停视频");
                        videoView.stopPlayback();
                    }
//                    mtvVideoPath.setText(selFile);
                    videoView.setVisibility(View.GONE);
                    mVideoResManager.clear();
                    updateView();
                    this.playSlideImage();


                } else {
                    Toast.makeText(MainActivity.this, "未选择图像文件", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == 2) {
                if (selFile != null) {
                    toPlayVideo = true;
                    TvPref.saveVideoFile(selFile);
                    TvPref.saveImageFilePath("");
                    if (picSwitch.isPlaying()) {
                        picSwitch.stopPlay();
                    }
                    picSwitch.setVisibility(View.GONE);
                    videoView.setVisibility(View.VISIBLE);
                    //mtvVideoPath.setText(selFile);
                    updateVideoView();// 重新开始，可以打断广告视频


                } else {
                    Toast.makeText(MainActivity.this, "未选择视频文件", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    void updateVideoView() {
        mVideoResManager.UpdateVideo();
        updateView();
        startPlay(false);
    }
    private void updateView() {
        String picpath = TvPref.readImageFilePath();
        Log.e("updateView", picpath);
        boolean hasPics = picpath != null && !picpath.isEmpty();

//        if (LOGIN_STATUS_READY == mLoginStatus){  // 在排队

        //   ViewGroup.LayoutParams params = mQueClient.getLayoutParams();
        if (mVideoResManager.hasVideo() || (hasPics && App.instance.getPlayIMGEn())
                || isGameOpenNow) {
            // 需要播放视频图片
            queueing_view_mode = 1;
            if (miLayoutType == 3) {
                //  params.width = TvPref.readQLayoutWidth();// 只显示右侧一部分
            } else {
                // params.height = TvPref.readQLayoutHeight();
            }
//				mQueAdapter.notifyDataSetChanged();
            // orderNoListAdapter.notifyDataSetChanged();
            // queview_all_screen_lyt.setVisibility(View.GONE);
            //queview_rigth_lyt.setVisibility(View.VISIBLE);
            //  falshCount = 0;
        } else {
            //TODO
            queueing_view_mode = 2;// 全屏
            // 全部是排队
            if (miLayoutType == 3) {
                //  params.width = display.getWidth();
            } else {
                // params.height = display.getHeight();
            }

//                queview_all_screen_lyt.setVisibility(View.VISIBLE);
//                queview_rigth_lyt.setVisibility(View.GONE);
//                falshCount = -1;
        }
//            mQueClient.setLayoutParams(params);
//            mQueClient.setVisibility(View.VISIBLE);
//        } else {
//            mQueClient.setVisibility(View.GONE);
//            mstvShopAd.setVisibility(View.INVISIBLE);// 广告
//        }

        // mVideoClient.setVisibility(View.GONE);
        //    picSwitch.setVisibility(View.GONE);
        //  mimLogo.setVisibility(View.GONE);

        if (mVideoResManager.hasVideo()) {
            //  mVideoClient.setVisibility(View.VISIBLE);
            videoView.setVisibility(View.VISIBLE);
            mbUpdateVideo = true;
        } else if (hasPics && (queueing_view_mode == 0 || App.instance.getPlayIMGEn())) {
            picSwitch.setVisibility(View.VISIBLE);
        } else if (isGameOpenNow) {
            picSwitch.setVisibility(View.VISIBLE);
        } else {
            //    mimLogo.setVisibility(View.VISIBLE);
        }

        if (queueing_view_mode != 0) {
            //   calcFont();
        }
//
//        main_scan_barcode_edt.setFocusable(true);
//        main_scan_barcode_edt.requestFocus();

//        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//        // 得到InputMethodManager的实例
//        if (imm != null) {
//            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//        }
    }
    /**
     * 播放视频
     * 1、从暂停状态恢复
     * 2、查找本地存储的视频地址
     * 1、有视频：播放
     * 2、无视频：有图片就播图片，没有就不显示
     *
     * @param resume 视频处于暂停？
     */
    private void startPlay(boolean resume) {
        /***
         * 将播放器关联上一个音频或者视频文件 videoView.setVideoURI(Uri uri)
         * videoView.setVideoPath(String path) 以上两个方法都可以。
         */

        videoView.stopPlayback();

        int pos = 0;
        if (resume) {
            // 从广告视频恢复
            pos = mVideoDefaultCurPos;
        } else {
            mstrPlayFile = mVideoResManager.getNextVideo();
            if (mstrPlayFile == null) {
                String s = TvPref.readImageFilePath();
                if (s.isEmpty()) {
                    mVideoState = VIDEO_STATE_NONE;
                } else {
                    playSlideImage();
                }
                //   LogFile.i("no video");
                return;// 不播放视频
            }
        }
        //   LogFile.i(String.format("playing video file:%s", mstrPlayFile));
        String dir = TvPref.readVideoFile();
        if (!mstrPlayFile.contains(dir)) {
            dir = TvPref.readExstoragePath();
        }
        //     mtvVideoPath.setText(dir);
        String filename = mstrPlayFile.substring(dir.length());
        //  mtvVideoplaying.setText(filename);

        videoView.setVideoPath(mstrPlayFile);

        /**
         * w为其提供一个控制器，控制其暂停、播放……等功能
         */
        // videoView.setMediaController(new MediaController(this));

        /**
         * 视频或者音频到结尾时触发的方法
         */
        videoView
                .setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
//                        LogFile.i(String.format("OnCompletion,file=%s",
//                                mstrPlayFile));
                        mVideoState = VIDEO_STATE_ONE_COMPLETE;
                        play_end_timestamp = System.currentTimeMillis();
                    }
                });

        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
//
//                LogFile.e(String.format("playfile=%s,OnError", mstrPlayFile));
//                LogFile.i(String.format("what=%d,ex=%d", what, extra));

                mVideoState = VIDEO_STATE_ONE_COMPLETE;// 跳过当前文件
                play_end_timestamp = System.currentTimeMillis();
                return true; // 不显示对话框
            }
        });
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {
                mnVideoWidth = mp.getVideoWidth();
                mnVideoHeight = mp.getVideoHeight();

                //    mp.setVolume(6f, 6f);

                adjustVideoView();
            }
        });

        videoView.start();
        if (pos > 0) {
            videoView.seekTo(pos);
        }
        mVideoState = VIDEO_STATE_PLAYING;
    }

    private void playSlideImage() {
        // 判断是否配置允许同时播放幻灯片和排队
        if (!App.instance.getPlayIMGEn() && LOGIN_STATUS_READY == mLoginStatus) {
            return;// 排队时不显示图片
        }

        if (!picSwitch.isPlaying()) {
            String esdpath = TvPref.readExstoragePath();
            String specifypath = TvPref.readImageFilePath();
            boolean bloss = TvPref.readSaveMem();
            if (specifypath.length() > 0 && esdpath.contains(specifypath)) {
                picSwitch.setImageDir(specifypath, bloss);
                // mtvVideoPath.setText(specifypath);
            } else {
                int num = 0;
                if (specifypath.length() > 0) {
                    num = picSwitch.setImageDir(esdpath, bloss);
                    if (num > 1) {
                        //  mtvVideoPath.setText(esdpath);
                    }
                }
                if (num < 2) {
                    if (specifypath.length() == 0) {
                        return;
                    }
                    picSwitch.setImageDir(specifypath, bloss);
                    // mtvVideoPath.setText(specifypath);
                }
            }

            picSwitch.setVisibility(View.VISIBLE);
            picSwitch.startPlay();
        }
    }

    private void adjustVideoView() {
        mbUpdateVideo = false;
        int client_w = videoView.getWidth();
        int client_h = videoView.getHeight();
        if (client_h == 0 || client_w == 0 || mnVideoWidth == 0
                || mnVideoHeight == 0) {
            return;
        }
        int ajust = TvPref.readVideoAdjust();

        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) videoView
                .getLayoutParams();
        if (client_h / mnVideoHeight > client_w / mnVideoWidth)//
        {
            // 等高模式
            int dstW = mnVideoWidth * client_h / mnVideoHeight;
            int margin_left = (client_w - dstW) * ajust / 20;
            mlp.leftMargin = margin_left;
            mlp.rightMargin = margin_left;
            // mlp.leftMargin=-80;
            // mlp.rightMargin=-80;
            videoView.setLayoutParams(mlp);
        } else {
            // 等宽
            int dstH = mnVideoHeight * client_w / mnVideoWidth;
            int margin_top = (client_h - dstH) * ajust / 20;
            mlp.topMargin = margin_top;
            mlp.bottomMargin = margin_top;
            videoView.setLayoutParams(mlp);
        }

    }

    public void onDestroy() {
        //    App.instance.setSave();
        if (picSwitch.isPlaying()) {
            picSwitch.stopPlay();
        }
        if (videoView != null && videoView.isPlaying()) {
            videoView.stopPlayback();
        }
        super.onDestroy();

    }

}
