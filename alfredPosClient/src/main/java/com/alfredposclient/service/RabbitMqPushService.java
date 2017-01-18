package com.alfredposclient.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import com.alfredbase.BaseApplication;
import com.alfredbase.ParamConst;
import com.alfredbase.javabean.model.PushMessage;
import com.alfredbase.utils.LogUtil;
import com.alfredposclient.global.App;
import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

public class RabbitMqPushService extends Service {

	private static final String TAG = RabbitMqPushService.class.getName();
	private final IBinder mBinder = new Binder();
	private boolean mShutDown = false;
	private PushService.PushListener mListener;
	private Handler mHandler;
	private ConnectionFactory factory;
	private static final String  PUSH_USERNAME = "alfred_B_A";
	private static final String  PUSH_PASSWORD = "alfrednew2016";
	private static final String  PUSH_VIRTUAL_HOST = "/alfred";
	private static final String  QUEUE_ANDROID = "Alfred_Pos";
	private static final String  EXCHANGE_NAME = BaseApplication.isOpenLog ? "ALFRED_EXCHANGES_TEXT" : "ALFRED_EXCHANGES";
	private static final int PUSH_PORT = 5672;
	private Thread pushThread;
	private Runnable runnable;
	private boolean stopThread = false;

	public static Intent startIntent(Context context, int restId) {
		Intent i = new Intent(context, RabbitMqPushService.class);
		i.putExtra("rKey", "B." + restId);
		return i;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
//		mHandler = new Handler();
//		mListener = new PushListenerClient(App.instance);
//		LogUtil.d(TAG, "Creating Alfred Push Service " + this.toString());
//		if (factory == null) {
//			factory = new ConnectionFactory();
//		}
//		factory.setHost(getPushServerIp());
//		factory.setPort(PUSH_PORT);
//		factory.setUsername(PUSH_USERNAME);
//		factory.setPassword(PUSH_PASSWORD);
//		factory.setVirtualHost(PUSH_VIRTUAL_HOST);
//		PushMessage pushMessage = new PushMessage();
	}

	@Override
	public void onDestroy() {
//		stopThread();
//		pushThread = null;
//		App.instance.getPushThread().stopThread();
		super.onDestroy();
		LogUtil.d(TAG, "Destroying Alfred Push Service " + this.toString() + "stopThread = " + stopThread);
	}

//	public void stopThread(){
//		if(pushThread.isAlive()) {
//			pushThread.currentThread().interrupt();
//			stopThread = true;
//		}
//	}

	@Override
	public int onStartCommand(final Intent intent, int flags, int startId) {
//		if(intent == null){
//			App.instance.getPushThread().stopThread();
//		}else{
//			App.instance.getPushThread().start(intent.getStringExtra("rKey"));
////			App.instance.getPushThread().setStopThread(false);
//		}
//		stopThread = false;
//		LogUtil.d(TAG, "开启服务 stopThread = " + stopThread);
//		if(runnable == null){
//			runnable = new Runnable() {
//				@Override
//				public void run() {
//					try {
//						if(intent == null){
//							stopThread = true;
//							return;
//						}
//						connect(intent.getStringExtra("rKey"));
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//						Thread.currentThread().interrupt();
//					}
//				}
//			};
//		}
//		if(pushThread == null) {
//			pushThread = new Thread(runnable);
//			pushThread.start();
//		}else{
//			pushThread.currentThread().start();
//		}
		return START_STICKY;
	}



	public void connect(String rKey) throws InterruptedException {
		try {
			LogUtil.d(TAG, "111 executed");
			// 使用之前的设置，建立连接
			Connection autorecoveringConnection = factory.newConnection();
//                AutorecoveringConnection autorecoveringConnection = (AutorecoveringConnection) factory.newConnection();
			// 创建一个通道
			final Channel channel = autorecoveringConnection.createChannel();
			// 声明队列，主要为了防止消息接收者先运行此程序，队列还不存在时创建队列。
			channel.queueDeclare(QUEUE_ANDROID + "_" + rKey, false, false, false, null);
			// 绑定队列到交换机
			channel.queueBind(QUEUE_ANDROID + "_" + rKey, EXCHANGE_NAME, rKey);
			channel.basicQos(1);
			// 创建消费者
			QueueingConsumer consumer = new QueueingConsumer(channel);
			channel.basicConsume(QUEUE_ANDROID + "_" + rKey, false, consumer);
			while (true) {
				if(stopThread) {
					if(autorecoveringConnection.isOpen()){
						autorecoveringConnection.close();
					}
					LogUtil.d(TAG, "退出线程 stopThread = " +stopThread);
					return;
				}
				LogUtil.d(TAG, "executed" + autorecoveringConnection.isOpen());
				QueueingConsumer.Delivery delivery = consumer.nextDelivery();
				if(stopThread) {
					LogUtil.d(TAG, "退出线程 stopThread = " +stopThread);
					return;
				}
				String message = new String(delivery.getBody());
				LogUtil.d(TAG, message);
				channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
				// 从message池中获取msg对象更高效
//				Message msg = mHandler.obtainMessage();
//				Bundle bundle = new Bundle();
//				bundle.putString("msg", message);
//				msg.setData(bundle);
//				mHandler.sendMessage(msg);
				if(!"\"LinkStatus\"".equals(message)) {
					try {
						Gson gson = new Gson();
						PushMessage pushMessage = gson.fromJson(message, PushMessage.class);
						if (mListener != null)
							mListener.onPushMessageReceived(pushMessage);
						else
							LogUtil.d(TAG, "回调是空的");
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
			connect(rKey);
		}
	}



	public class Binder extends android.os.Binder {

		public RabbitMqPushService getService() {
			return RabbitMqPushService.this;
		}
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

	public synchronized void setListener(PushService.PushListener listener) {
		mListener = listener;
	}

//	public interface PushListener {
//		public void onPushMessageReceived(PushMessage msg);
//
//		public void onNetworkError();
//
//		public void onNetworkDisconnected();
//
//		public void onNetworkConnected();
//	}

}
