package com.alfredselfhelp.activity;


import android.app.Dialog;
import android.view.View;

import com.alfredbase.BaseActivity;
import com.alfredselfhelp.R;
import com.alfredselfhelp.utils.DialogFactory;
import com.alfredselfhelp.utils.ToolAlert;


public class DialogActivity extends BaseActivity {

    //    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//    }
    Dialog dialog = null;

    @Override
    protected void initView() {
        super.initView();
        setContentView(R.layout.activity_dialog);

        dialog = ToolAlert.MyDialog(DialogActivity.this, "", "", "", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
//       DialogFactory.QCDialog(context, "KPMG QR Code", "Payment in progress", "", true, new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        }, new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        },true);
//
//    }
    }
}