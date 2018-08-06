package com.alfredposclient.global;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.SyncHttpClient;

import java.util.Timer;
import java.util.TimerTask;

public class SubPosSyncCentre {

	private Timer timer;

	private TimerTask timerTask;

	private static AsyncHttpClient httpClient;

	private static SubPosSyncCentre instance;
	private static SyncHttpClient syncHttpClient;
	/**
	 * 大数据用
	 */
	private static SyncHttpClient bigSyncHttpClient;
	public static final int MODE_FIRST_SYNC = 1;
	public static final int MODE_PUSH_SYNC = 2;

	private SubPosSyncCentre() {

	}

	public static SubPosSyncCentre getInstance() {
		init();
		return instance;
	}

	private static void init() {
		if (instance == null) {
			instance = new SubPosSyncCentre();

			httpClient = new AsyncHttpClient();
			httpClient.addHeader("Connection", "close");
			httpClient.setMaxRetriesAndTimeout(0, 5 * 1000);
			httpClient.setTimeout(20 * 1000);
			syncHttpClient = new SyncHttpClient();
			syncHttpClient.addHeader("Connection", "close");
			syncHttpClient.setTimeout(20 * 1000);
			syncHttpClient.setMaxRetriesAndTimeout(0, 5 * 1000);
			bigSyncHttpClient = new SyncHttpClient();
			bigSyncHttpClient.addHeader("Connection", "close");
			bigSyncHttpClient.setTimeout(100 * 1000);
			bigSyncHttpClient.setMaxRetriesAndTimeout(0, 1 * 1000);
		}
	}



}
