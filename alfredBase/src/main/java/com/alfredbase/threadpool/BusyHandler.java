package com.alfredbase.threadpool;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import com.alfredbase.utils.LogUtil;

/**
 * 
 * @author 冯小卫 2014-3-18
 * 
 *         线程池异常处理类
 * 
 */
public class BusyHandler implements RejectedExecutionHandler {
	private static final String TAG = BusyHandler.class.getSimpleName();

	@Override
	public void rejectedExecution(Runnable task, ThreadPoolExecutor executor) {
		LogUtil.d(TAG, "服务器繁忙");
	}
}
