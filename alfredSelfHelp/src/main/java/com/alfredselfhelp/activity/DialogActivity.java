package com.alfredselfhelp.activity;


import android.app.Dialog;
import android.view.View;

import com.alfredbase.BaseActivity;
import com.alfredbase.utils.DialogFactory;
import com.alfredselfhelp.R;
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

        Dialog dialogac =    DialogFactory.kpmTipsDialog(context, "Credit Card Invalid", "", R.drawable.icon_tip_cq, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        },true);

//        Dialog dialogac=    DialogFactory.qcDialog(context, "KPMG QR Code", "", R.drawable.credit_card, false, new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        },true);



//        Dialog dialogac=    DialogFactory.kpmCompleteDialog(context, "Thank You", "aaaaaaaaaaaaaaaaaaaaaaaa", "ssssssssssss",R.drawable.icon_error,
//         true);
//        dialog = ToolAlert.MyDialog(DialogActivity.this, "", "", "", new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        }, new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
////       DialogFactory.QCDialog(context, "KPMG QR Code", "Payment in progress", "", true, new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////
////            }
////        }, new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////
////            }
////        },true);
////
////    }
    }
}