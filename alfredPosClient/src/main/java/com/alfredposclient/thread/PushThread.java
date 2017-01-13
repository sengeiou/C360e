package com.alfredposclient.thread;

import android.text.TextUtils;

import com.alfredbase.BaseApplication;
import com.alfredbase.ParamConst;
import com.alfredbase.javabean.model.PushMessage;
import com.alfredbase.utils.LogUtil;
import com.alfredposclient.global.App;
import com.alfredposclient.global.PushListenerClient;
import com.alfredposclient.service.PushService;
import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

import org.json.JSONObject;

/**
 * Created by Alex on 17/1/12.
 */

public class PushThread extends Thread {
    private static final String TAG = PushThread.class.getSimpleName();
    private PushService.PushListener mListener;
    private ConnectionFactory factory;
    private static final String  PUSH_USERNAME = "alfred_B_A";
    private static final String  PUSH_PASSWORD = "alfrednew2016";
    private static final String  PUSH_VIRTUAL_HOST = "/alfred";
    private static final String  QUEUE_ANDROID = "Alfred_Pos";
    private static final String  EXCHANGE_NAME = BaseApplication.isOpenLog ? "ALFRED_EXCHANGES_TEXT" : "ALFRED_EXCHANGES";
    private static final int PUSH_PORT = 5672;
    private boolean stopThread = true;
    private Connection autorecoveringConnection;
    private Channel channel;
    private QueueingConsumer consumer;
    private String key;
    public PushThread(){
        mListener = new PushListenerClient(App.instance);
        LogUtil.d(TAG, "Creating Alfred Push Service " + this.toString());
        if (factory == null) {
            factory = new ConnectionFactory();
        }
        factory.setHost(getPushServerIp());
        factory.setPort(PUSH_PORT);
        factory.setUsername(PUSH_USERNAME);
        factory.setPassword(PUSH_PASSWORD);
        factory.setVirtualHost(PUSH_VIRTUAL_HOST);
    }

    private String getPushServerIp(){
        if(App.isDebug){
            return "60.205.181.250";
        } else if (App.isOpenLog){
            return "60.205.181.250";
//			return "ws://172.16.0.190:8085/websocket";
        } else {
            if (App.instance.countryCode == ParamConst.CHINA)
                return "52.221.245.224";
            else
                return "52.221.245.224";
        }
    }
    @Override
    public void run() {
        super.run();
        try {
            connect();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    public synchronized void start(String key) {
        this.key = key;
    }

    public void setStopThread(boolean stopThread) {
        this.stopThread = stopThread;
    }

    private void newConnection() throws Exception {
        autorecoveringConnection = factory.newConnection();
        // 创建一个通道
        channel = autorecoveringConnection.createChannel();
        // 声明队列，主要为了防止消息接收者先运行此程序，队列还不存在时创建队列。
        channel.queueDeclare(QUEUE_ANDROID + "_" + key, false, false, false, null);
        // 绑定队列到交换机
        channel.queueBind(QUEUE_ANDROID + "_" + key, EXCHANGE_NAME, key);
        channel.basicQos(1);
        // 创建消费者
        consumer = new QueueingConsumer(channel);
        channel.basicConsume(QUEUE_ANDROID + "_" + key, false, consumer);
    }

    private void closeConnection() throws  Exception{
        if(autorecoveringConnection != null && autorecoveringConnection.isOpen()){
            autorecoveringConnection.close();
        }
        Thread.sleep(10000);
    }

    public void connect() throws InterruptedException {
        try {
            LogUtil.d(TAG, "启动线程");
            while (true) {
                if(stopThread) {
                    LogUtil.d(TAG, "关闭连接 进行等待 stopThread = " +stopThread);
                    closeConnection();
                    continue;
                }else{
                    if(autorecoveringConnection == null || !autorecoveringConnection.isOpen()){
                        LogUtil.d(TAG, "创建连接 stopThread = " +stopThread);
                        newConnection();
                    }
                }
                LogUtil.d(TAG, "executed" + autorecoveringConnection.isOpen());
                QueueingConsumer.Delivery delivery = consumer.nextDelivery();
                if(stopThread) {
                    LogUtil.d(TAG, "关闭连接 进行等待 stopThread = " +stopThread);
                    closeConnection();
                    continue;
                }
                String message = new String(delivery.getBody());
                LogUtil.d(TAG, message);
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                if(!"\"LinkStatus\"".equals(message)) {
                    try {
                        Gson gson = new Gson();
//                        PushMessage pushMessage = gson.fromJson(message, PushMessage.class);
                        JSONObject jsonObject = new JSONObject(message);
                        PushMessage pushMessage = new PushMessage();
                        if(jsonObject.has("push")){
                            pushMessage.setMsg(jsonObject.getString("push"));
                        }
                        if(jsonObject.has("content")){
                            pushMessage.setContent(jsonObject.getString("content"));
                        }
                        if(jsonObject.has("restId")){
                            pushMessage.setRestId(jsonObject.getInt("restId"));
                        }
                        if(jsonObject.has("revenueId")){
                            pushMessage.setRestId(jsonObject.getInt("revenueId"));
                        }
                        if(!TextUtils.isEmpty(pushMessage.getMsg())){
                            if (mListener != null)
                                mListener.onPushMessageReceived(pushMessage);
                            else
                                LogUtil.d(TAG, "回调是空的");
                        }
                    } catch (Exception e) {
                        LogUtil.d(TAG, "设置监听出错");
                        e.printStackTrace();
                    }
                }
                Thread.sleep(500);

            }
        } catch (Exception e) {
            LogUtil.d(TAG, "线程出错,可能是关闭");
            e.printStackTrace();
            if(stopThread) {
                LogUtil.d(TAG, "退出线程 stopThread = " +stopThread);
                return;
            }
            LogUtil.i(TAG, "断线30s后重连");
            Thread.sleep(30000);
            connect();
        }
    }
}
