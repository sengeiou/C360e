package com.alfredkds.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alfredbase.BaseActivity;
import com.alfredbase.ParamConst;
import com.alfredbase.http.ResultCode;
import com.alfredbase.javabean.User;
import com.alfredbase.javabean.model.KDSDevice;
import com.alfredbase.store.Store;
import com.alfredbase.utils.DialogFactory;
import com.alfredbase.utils.LanguageManager;
import com.alfredbase.utils.LogUtil;
import com.alfredkds.R;
import com.alfredkds.global.App;
import com.alfredkds.global.SyncCentre;
import com.alfredkds.global.UIHelp;
import com.alfredkds.view.MyToggleButton;
import com.alfredkds.view.SystemSettings;

import java.util.HashMap;
import java.util.Map;

public class Setting extends BaseActivity implements MyToggleButton.OnToggleStateChangeListeren, LanguageManager.LanguageDialogListener {
    public static final String TAG = Setting.class.getSimpleName();
    public static final int HANDLER_LOGOUT_SUCCESS = 1;
    public static final int HANDLER_LOGOUT_FAILED = 2;


    private TextView tv_kot_history;
    private TextView tv_kot_reset;
    private TextView tv_switch_account;

    private ImageView iv_language;
    private LinearLayout ll_language_setting;
    private AlertDialog alertLanguage;


    private MyToggleButton mt_kot_lan;
    private SystemSettings settings;

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case HANDLER_LOGOUT_SUCCESS:
                    User user = App.instance.getUser();
                    if (user != null) {
                        Store.remove(context, Store.KDS_USER);
                    }
                    App.instance.popAllActivityExceptOne(EmployeeID.class);
                    UIHelp.startEmployeeID(context);
                    break;
                case HANDLER_LOGOUT_FAILED:
                    UIHelp.showToast(context, ResultCode.getErrorResultStr(context, (Throwable) msg.obj,
                            context.getResources().getString(R.string.revenue_center)));
                    break;
                default:
                    break;
            }
        }

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        ((TextView) findViewById(R.id.tv_version)).setText(context.getResources().getString(R.string.version) + App.instance.VERSION);
        settings = App.instance.getSystemSettings();
        findView();
    }

    protected void findView() {
        super.initView();
        this.tv_kot_history = (TextView) findViewById(R.id.tv_history);
        this.tv_kot_reset = (TextView) findViewById(R.id.tv_reset);
        this.tv_switch_account = (TextView) findViewById(R.id.tv_switch_account);
        this.mt_kot_lan = (MyToggleButton) findViewById(R.id.mt_kot_lan);
        this.mt_kot_lan.setOnStateChangeListeren(this);
        this.tv_kot_history.setOnClickListener(this);
        this.tv_kot_reset.setOnClickListener(this);
        this.tv_switch_account.setOnClickListener(this);

        if (settings.isKdsLan()) {
            mt_kot_lan.setChecked(true);
        } else {
            mt_kot_lan.setChecked(false);
        }

        ll_language_setting = (LinearLayout) findViewById(R.id.ll_language_setting);
        ll_language_setting.setOnClickListener(this);

        iv_language = (ImageView) findViewById(R.id.iv_language);
        iv_language.setImageDrawable(LanguageManager.getLanguageFlag(this));


    }

    @Override
    public void handlerClickEvent(View v) {
        final Integer cid = (Integer) Store.getInt(context, Store.CURRENT_MAIN_POS_ID_CONNECTED);
        LogUtil.i(TAG, "" + cid);
        super.handlerClickEvent(v);
        switch (v.getId()) {
            case R.id.tv_history:
                UIHelp.startKotHistory(context);
                break;
            case R.id.tv_reset:
                DialogFactory.commonTwoBtnDialog(context, "", getString(R.string.reset),
                        getString(R.string.cancel), getString(R.string.ok), null,
                        new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                App.instance.finishAllActivityExceptOne(Setting.class);
                                App.instance.getTopActivity().finish();
                                User user = Store.getObject(context, Store.KDS_USER, User.class);
                                if (user != null) {
                                    Store.remove(context, Store.KDS_USER);
                                }
                                if (cid != null && cid != Store.DEFAULT_INT_TYPE) {
                                    Store.remove(context, Store.CURRENT_MAIN_POS_ID_CONNECTED);
                                }
                                UIHelp.startWelcome(context);
                            }
                        });
                break;
            case R.id.tv_switch_account:
                KDSDevice kdsDevice = App.instance.getKdsDevice();
                if (kdsDevice == null) {
                    return;
                }
                Map<String, Object> parameters = new HashMap<String, Object>();
                parameters.put("device", kdsDevice);
                parameters.put("deviceType", ParamConst.DEVICE_TYPE_KDS);
                SyncCentre.getInstance().Logout(context, App.instance.getCurrentConnectedMainPos(), parameters, handler);
                break;
            case R.id.ll_language_setting:
                alertLanguage = LanguageManager.alertLanguage(this, this);
                break;
            default:
                break;
        }


    }

    @Override
    public void onToggleStateChangeListeren(MyToggleButton Mybutton, Boolean checkState) {


        switch (Mybutton.getId()) {
            case R.id.mt_kot_lan:


                if (checkState) {
                    mt_kot_lan.setChecked(true);
                    settings.setKdsLan(ParamConst.DEFAULT_TRUE);
                } else {
                    mt_kot_lan.setChecked(false);
                    settings.setKdsLan(ParamConst.DEFAULT_FALSE);
                }

                break;
        }
    }

    @Override
    public void setLanguage(final String language) {
        if (alertLanguage != null) {
            alertLanguage.dismiss();
        }
        App.getTopActivity().changeLanguage(language);
    }
}
