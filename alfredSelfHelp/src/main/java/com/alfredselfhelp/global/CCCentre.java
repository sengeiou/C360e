package com.alfredselfhelp.global;

import android.text.TextUtils;

import com.alfredbase.utils.LogUtil;
import com.alfredselfhelp.utils.UIHelp;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

public class CCCentre {
    private static final String TAG = "TAG";
    private  String HOST;
    private static final int PORT = 8582;
    private BufferedSink mSink;
    private BufferedSource mSource;
    private ExecutorService mExecutorService = null;
    private static CCCentre instance;

    private static final String sales = "C200";
    private static final String amount = "0412%012d";
    private static final String identifier = "5706%06d";
    private static final String trace = "612000109040112121200001";

    public static CCCentre getInstance(){
        if(instance == null){
            instance = new CCCentre();
        }
        return instance;
    }

    private CCCentre() {
        mExecutorService = Executors.newCachedThreadPool();
    }

    public void connect(String ip) {
        if(!TextUtils.isEmpty(ip)) {
            this.HOST = ip;
            mExecutorService.execute(new connectService());
        }
    }
    int i = 0;
    public void startPay(String amount){
        if(mExecutorService != null && !TextUtils.isEmpty(HOST)) {
            StringBuffer str = new StringBuffer(sales);
            str.append(String.format(amount, Integer.parseInt(amount)));
            str.append(String.format(identifier, i++));
            String sendMsg = str.append(trace).toString();
            send(sendMsg);
        }
    }

    public void send(String sendMsg) {
        if(mExecutorService != null && !TextUtils.isEmpty(HOST)) {
            mExecutorService.execute(new sendService(sendMsg));
        }
    }

    public void disconnect() {
        if(mExecutorService != null && !TextUtils.isEmpty(HOST)) {
            mExecutorService.execute(new sendService("0"));
        }
    }

    private class sendService implements Runnable {
        private String msg;

        sendService(String msg) {
            this.msg = msg;
        }

        @Override
        public void run() {
            try {
                if(mSink != null && mSource != null) {
                    mSink.writeUtf8(this.msg + "\n");
                    mSink.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class connectService implements Runnable {
        @Override
        public void run() {
            try {
                Socket socket = new Socket(HOST, PORT);
                socket.setSoTimeout(15*60*1000);
                mSink = Okio.buffer(Okio.sink(socket));
                mSource = Okio.buffer(Okio.source(socket));
                receiveMsg();
            } catch (Exception e) {
                LogUtil.e(TAG, ("connectService:" + e.getMessage()));
            }
        }
    }

    private void receiveMsg() {
        try {
            while (true) {
                for (String receiveMsg; (receiveMsg = mSource.readUtf8Line()) != null; ) {
                    LogUtil.d(TAG, "receiveMsg:" + receiveMsg);
                    final String finalReceiveMsg = receiveMsg;
                    // TODO
                    App.getTopActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            UIHelp.showToast(App.instance, "receiveMsg:" + finalReceiveMsg);
                        }
                    });
                }
            }
        } catch (IOException e) {
            LogUtil.e(TAG, "receiveMsg: ");
            e.printStackTrace();
        }
    }

}
