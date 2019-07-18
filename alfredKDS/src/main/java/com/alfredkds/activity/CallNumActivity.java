package com.alfredkds.activity;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.TextView;
import com.alfredbase.BaseActivity;
import com.alfredbase.LoadingDialog;
import com.alfredbase.global.BugseeHelper;
import com.alfredbase.http.ResultCode;
import com.alfredbase.utils.ButtonClickTimer;
import com.alfredkds.R;
import com.alfredkds.global.App;
import com.alfredkds.global.SyncCentre;
import com.alfredkds.global.UIHelp;
import com.alfredkds.view.CallNumboard;

import java.util.HashMap;
import java.util.Map;

public class CallNumActivity extends BaseActivity implements CallNumboard.KeyBoardClickListener {

    private TextView tv_quantity;
    private CallNumboard callNumboard;
    private boolean flag = false;

    @Override
    protected void initView() {
        super.initView();
        setContentView(R.layout.activity_call_num);
        flag = false;
        loadingDialog = new LoadingDialog(context);
        loadingDialog.setTitle(getString(R.string.loading));
        tv_quantity = (TextView) findViewById(R.id.tv_quantity);
        callNumboard = (CallNumboard) findViewById(R.id.quantityKeyboard);
        callNumboard.setKeyBoardClickListener(this);
    }

    @Override
    public void onKeyBoardClick(String key) {

        BugseeHelper.buttonClicked(key);

        if ("X".equals(key)) {
            tv_quantity.setText("");
            finish();
        } else if ("Enter".equals(key)) {
            if(!ButtonClickTimer.canClick()){
                return;
            }
            String str = tv_quantity.getText().toString();
            if (!TextUtils.isEmpty(str)) {
                loadingDialog.show();
                Map<String, Object> parameters = new HashMap<String, Object>();
                parameters.put("callnumber", str);
                parameters.put("numTag", "");
                SyncCentre.getInstance().callSpecifyNum(CallNumActivity.this, App.instance.getCurrentConnectedMainPos(), parameters, handler,-1);
            }else {
                UIHelp.showToast(CallNumActivity.this, getString(R.string.order_number_cannot_empty));
            }
        } else if ("C".equals(key)) {
            tv_quantity.setText("");
        } else if (flag) {
            tv_quantity.setText(tv_quantity.getText().toString() + key);
        } else {
            tv_quantity.setText(key);
            flag = !flag;
        }
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case ResultCode.SUCCESS:
                    tv_quantity.setText("");
                    loadingDialog.dismiss();
                    break;
                case ResultCode.CONNECTION_FAILED:
                    tv_quantity.setText("");
                    loadingDialog.dismiss();
                    UIHelp.showToast(context, ResultCode.getErrorResultStr(context, (Throwable)msg.obj,
                            context.getResources().getString(R.string.revenue_center)));
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loadingDialog != null && loadingDialog.isShowing()){
            loadingDialog.dismiss();
        }
    }
}
