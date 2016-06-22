package com.alfred.print.jobs;
import android.util.Log;

import com.alfred.remote.printservice.App;
import com.path.android.jobqueue.log.CustomLogger;



public class AlfredJobLogger implements CustomLogger {

	private String TAG = "JOBS";
	
    public AlfredJobLogger(String tAG) {
		super();
		TAG = tAG;
	}
    
    @Override
    public boolean isDebugEnabled() {
        return App.isOpenLog;
    }

    @Override
    public void d(String text, Object... args) {
        Log.d(TAG, String.format(text, args));
    }

    @Override
    public void e(Throwable t, String text, Object... args) {
        Log.e(TAG, String.format(text, args), t);
    }

    @Override
    public void e(String text, Object... args) {
        Log.e(TAG, String.format(text, args));
    }
}
