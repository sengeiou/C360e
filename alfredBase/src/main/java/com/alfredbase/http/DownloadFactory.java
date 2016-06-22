package com.alfredbase.http;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import com.alfredbase.R;

public class DownloadFactory {
	public static final int EVENT_DOWNLOAD_INSTALL = 110;
	
	
	public static DownloadManager.Request downloadFile(Context context, String url, String fileName) {
		DownloadManager.Request request = new DownloadManager.Request(
				Uri.parse(url));
		// 设置在什么网络情况下进行下载
		request.setAllowedNetworkTypes(Request.NETWORK_WIFI);
		// 设置通知栏标题
		request.setNotificationVisibility(Request.VISIBILITY_VISIBLE);
		request.setTitle(context.getResources().getString(R.string.downloading));
		request.setDescription(fileName + " is downloading");
		request.setAllowedOverRoaming(false);
		// 设置文件存放目录
		request.setDestinationInExternalFilesDir(context,
				Environment.DIRECTORY_DOWNLOADS, fileName);
		
		return request;
	}
	
	public static void installApk(Activity activity, String apkName) {
//		File apkfile = new File(apkName);
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.setDataAndType(Uri.parse(apkName),
				"application/vnd.android.package-archive");
		activity.startActivity(i);
	}
}
