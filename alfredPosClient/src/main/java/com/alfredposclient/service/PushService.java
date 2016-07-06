package com.alfredposclient.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

import com.alfredbase.ParamConst;
import com.alfredbase.javabean.model.PushMessage;
import com.alfredbase.utils.LogUtil;
import com.alfredposclient.global.App;
import com.codebutler.android_websockets.WebSocketClient;
import com.codebutler.android_websockets.WebSocketClient.Listener;
import com.google.gson.Gson;

import java.net.URI;

public class PushService extends Service implements Listener {

	public static final String ACTION_PING = "com.alfred.pushservice.ACTION_PING";
	public static final String ACTION_CONNECT = "com.alfred.pushservice.ACTION_CONNECT";
	public static final String ACTION_SHUT_DOWN = "com.alfred.pushservice.ACTION_SHUT_DOWN";
	private static final String TAG = PushService.class.getName();
	private static final int PING_TYPE = -2;
	private WebSocketClient mClient;
	private final IBinder mBinder = new Binder();
	private boolean mShutDown = false;
	private PushListener mListener;
	private Handler mHandler;

	public static Intent startIntent(Context context) {
		Intent i = new Intent(context, PushService.class);
		i.setAction(ACTION_CONNECT);
		return i;
	}

	public static Intent pingIntent(Context context) {
		Intent i = new Intent(context, PushService.class);
		i.setAction(ACTION_PING);
		return i;
	}

	public static Intent closeIntent(Context context) {
		Intent i = new Intent(context, PushService.class);
		i.setAction(ACTION_SHUT_DOWN);
		return i;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mHandler = new Handler();
		LogUtil.d(TAG, "Creating Alfred Push Service " + this.toString());
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		LogUtil.d(TAG, "Destroying Alfred Push Service " + this.toString());

		if (mClient != null && mClient.isConnected())
			mClient.disconnect();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		WakeLock wakelock = ((PowerManager) getSystemService(POWER_SERVICE))
				.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "AlfedPOS");
		wakelock.acquire();
		LogUtil.i(TAG, "PushService start command");

		if (intent != null)
			Log.i(TAG, intent.toUri(0));

		mShutDown = false;
		if (mClient == null) {
			WakeLock clientlock = ((PowerManager) getSystemService(POWER_SERVICE))
					.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "AlfedPOS");
			mClient = new WebSocketClient(
					URI.create(getPushServerIp()), this, null,
					clientlock);
		}

		if (!mClient.isConnected())
			mClient.connect();

		if (intent != null && App.instance.getRevenueCenter() != null) {
			if (ACTION_PING.equals(intent.getAction())) {
				if (mClient.isConnected())
					mClient.send(PushMessage.getPingMsg(App.instance.getRevenueCenter().getRestaurantId(),
							App.instance.getRevenueCenter().getId()));
				// if(mClient.isConnected())
				// mClient.send("{\"type\":0,\"restId\":19,\"revenueId\":1}");
			} else if (ACTION_SHUT_DOWN.equals(intent.getAction())) {
				mShutDown = true;
				if (mClient.isConnected())
					mClient.disconnect();
			}
		}

		if (intent == null || !intent.getAction().equals(ACTION_SHUT_DOWN)) {
			AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
			PendingIntent operation = PendingIntent.getService(this, 0,
					PushService.pingIntent(this), PendingIntent.FLAG_NO_CREATE);
			if (operation == null) {
				operation = PendingIntent.getService(this, 0,
						PushService.pingIntent(this),
						PendingIntent.FLAG_UPDATE_CURRENT);
				am.setInexactRepeating(AlarmManager.RTC_WAKEUP,
						System.currentTimeMillis(),
						AlarmManager.INTERVAL_FIFTEEN_MINUTES, operation);
			}
		}

		wakelock.release();
		return START_STICKY;
	}

	public class Binder extends android.os.Binder {

		public PushService getService() {
			return PushService.this;
		}
	}
	
	private String getPushServerIp(){
		if(App.isDebug){
			return "ws://192.168.1.131:8085/websocket";
		} else if (App.isOpenLog){
			return "ws://218.244.136.120:8085/websocket";
		} else {
			if (App.instance.countryCode == ParamConst.CHINA)
				return "ws://121.40.168.178:8085/websocket";
			else
				return "ws://52.77.208.125:8085/websocket";
		}
	}

	public synchronized void setListener(PushListener listener) {
		mListener = listener;
	}

	public synchronized boolean isConnected() {
		return mClient != null && mClient.isConnected();
	}

	@Override
	public void onConnect() {
		LogUtil.d(TAG, "Connected to websocket");
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				if (mListener != null)
					mListener.onNetworkConnected();
			}
		});
	}

	@Override
	public synchronized void onDisconnect(int code, String reason) {
		LogUtil.d(TAG, String.format("Disconnected! Code: %d Reason: %s", code,
				reason));
		if (!mShutDown) {
			startService(startIntent(this));
		} else {
			stopSelf();
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					if (mListener != null)
						mListener.onNetworkDisconnected();
				}
			});
		}
	}

	@Override
	public synchronized void onError(Exception arg0) {
		LogUtil.e(TAG, "PushService:" + arg0);
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				if (mListener != null)
					mListener.onNetworkError();
			}
		});
		startService(startIntent(this));
	}

	@Override
	public synchronized void onMessage(String msg) {
		WakeLock wakelock = ((PowerManager) getSystemService(POWER_SERVICE))
				.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "AlfedPOS");
		wakelock.acquire();
		final PushMessage response = PushMessage.deserializeList(msg);
		LogUtil.d(TAG, msg);
		
		PushMessage pushMessage = PushMessage.deserializeList(msg);
		if (pushMessage != null && pushMessage.getType() == PING_TYPE) {
			Gson gson = new Gson();
			pushMessage.setRestId(App.instance.getRevenueCenter()
					.getRestaurantId());
			pushMessage.setRevenueId(App.instance.getRevenueCenter().getId());
			mClient.send(gson.toJson(pushMessage));
			LogUtil.d(TAG, gson.toJson(pushMessage));
		}
		
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				if (mListener != null)
					mListener.onPushMessageReceived(response);
			}
		});
		wakelock.release();
	}

	@Override
	public synchronized void onMessage(byte[] arg0) {
		// TODO Auto-generated method stub

	}

	public synchronized void sentMessage(String msg) {
		if (mClient.isConnected()) {
			
			mClient.send(msg);
			LogUtil.d(TAG, "sentMessage" + msg);
		}
	}

	public interface PushListener {
		public void onPushMessageReceived(PushMessage msg);

		public void onNetworkError();

		public void onNetworkDisconnected();

		public void onNetworkConnected();
	}

}
