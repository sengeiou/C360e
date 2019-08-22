package com.alfredkds.activity;

import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.alfredbase.BaseActivity;
import com.alfredbase.KDSLog;
import com.alfredbase.store.Store;
import com.alfredkds.R;
import com.alfredkds.global.App;
import com.google.gson.Gson;

/**
 * Created by Arif S. on 8/21/19
 */
public class LogActivity extends BaseActivity {

    TextView tvLog;

    @Override
    protected void initView() {
        super.initView();
        setContentView(R.layout.activity_log);

        tvLog = (TextView) findViewById(R.id.tvLog);

        updateLog();
    }

    private void updateLog() {
        Gson gson = new Gson();
        KDSLog kdsLog = gson.fromJson(Store.getString(this, Store.KDS_LOGS), KDSLog.class);
        tvLog.setText(Store.getString(this, Store.KDS_LOGS) + "");
    }

    @Override
    public void httpRequestAction(int action, Object obj) {
        handler.sendMessage(handler.obtainMessage(action, obj));
    }

    public Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case App.HANDLER_REFRESH_LOG:
                    updateLog();
                    break;
            }
        }
    };
}
