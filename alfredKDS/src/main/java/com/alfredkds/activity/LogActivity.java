package com.alfredkds.activity;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alfredbase.BaseActivity;
import com.alfredbase.KDSLog;
import com.alfredbase.store.Store;
import com.alfredbase.utils.TextTypeFace;
import com.alfredkds.R;
import com.alfredkds.global.App;
import com.alfredkds.global.UIHelp;
import com.google.gson.Gson;

/**
 * Created by Arif S. on 8/21/19
 */
public class LogActivity extends BaseActivity {

    TextView tvLog, kitchName;
    ImageView iv_refresh, iv_setting;

    @Override
    protected void initView() {
        super.initView();
        setContentView(R.layout.activity_log);

        tvLog = (TextView) findViewById(R.id.tvLog);
        kitchName = (TextView) findViewById(R.id.title_kitchen);
        iv_refresh = (ImageView) findViewById(R.id.iv_refresh);
        iv_setting = (ImageView) findViewById(R.id.iv_setting);
        kitchName.setText(App.instance.getPrinter().getPrinterName());
        TextTypeFace textTypeFace = TextTypeFace.getInstance();
        textTypeFace.setTrajanProBlod(kitchName);
        iv_refresh.setOnClickListener(this);
        iv_setting.setOnClickListener(this);

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_refresh:
                updateLog();
                break;
            case R.id.iv_setting:
                UIHelp.startSetting(this);
                break;
            default:
                break;
        }
    }
}
