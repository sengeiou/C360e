package com.alfredposclient.push;

import android.text.TextUtils;

import com.alfredbase.BaseApplication;
import com.alfredbase.ParamConst;
import com.alfredbase.javabean.model.PushMessage;
import com.alfredbase.utils.LogUtil;
import com.alfredposclient.global.App;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

import org.json.JSONObject;

/**
 * Created by Alex on 17/1/18.
 */

public class PushServer {
    public static final String  TAG = PushServer.class.getSimpleName();
    Thread myThread;
    private PushListener mListener;
    private ConnectionFactory factory;
    private static final String  PUSH_USERNAME = "alfred_B_A";
    private static final String  PUSH_PASSWORD = "alfrednew2016";
    private static final String  PUSH_VIRTUAL_HOST = "/alfred";
    private static final String  QUEUE_ANDROID = "Alfred_Pos";
    private static final String  EXCHANGE_NAME = BaseApplication.isOpenLog ? "ALFRED_EXCHANGES_TEXT" : "ALFRED_EXCHANGES";
    private static final int PUSH_PORT = 5672;
    private Connection autorecoveringConnection;
    private boolean canCheckAppOrder = false;
    private Channel channel;
    QueueingConsumer consumer;
    public PushServer (){
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

    public void setCanCheckAppOrder(boolean canCheckAppOrder) {
        this.canCheckAppOrder = canCheckAppOrder;
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
    public final boolean isAlive() {
        return myThread != null && myThread.isAlive();
    }
    public final void stop(){
        if(channel !=null && channel.isOpen()) {
            try {
                channel.close();
                channel = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void start(final String key) throws Exception{
            myThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    do {
                        try {
                            if(channel == null) {
                                LogUtil.d(TAG, "创建连接");
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
                                QueueingConsumer.Delivery delivery = consumer.nextDelivery();

                                String message = new String(delivery.getBody());
                                LogUtil.d(TAG, message);
                                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                                if(!"\"LinkStatus\"".equals(message)) {
                                    try {
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
                                            pushMessage.setRevenueId(jsonObject.getInt("revenueId"));
                                        }
                                        if(!TextUtils.isEmpty(pushMessage.getMsg())){
                                            if (mListener != null)
                                                mListener.onPushMessageReceived(pushMessage, canCheckAppOrder);
                                            else
                                                LogUtil.d(TAG, "回调是空的");
                                        }
                                    } catch (Exception e) {
                                        LogUtil.d(TAG, "设置监听出错");
                                        e.printStackTrace();
                                    }
                                }
                                Thread.sleep(500);

                        } catch (Exception e) {
                            LogUtil.d(TAG, "出错15秒后重连");
                            try {
                                Thread.sleep(15000);
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }
                        }
                    } while (true);
                }
            });
            myThread.setDaemon(true);
            myThread.setName("PushServer");
            myThread.start();
        }



    public interface PushListener {
        public void onPushMessageReceived(PushMessage msg, boolean canCheck);

        public void onNetworkError();

        public void onNetworkDisconnected();

        public void onNetworkConnected();
    }
}
