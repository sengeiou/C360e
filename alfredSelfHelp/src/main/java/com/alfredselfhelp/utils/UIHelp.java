package com.alfredselfhelp.utils;

import android.app.Activity;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.alfredbase.BaseActivity;
import com.alfredbase.BaseApplication;
import com.alfredbase.utils.TextTypeFace;
import com.alfredselfhelp.activity.ConnectPOS;
import com.alfredselfhelp.activity.EmployeeID;
import com.alfredselfhelp.activity.Login;
import com.alfredselfhelp.activity.SelectRevenue;

public class UIHelp {

    public static void startSelectRevenue(Activity context){
        Intent intent = new Intent(context, SelectRevenue.class);
        context.startActivity(intent);
    }


    public static void startConnectPOS(BaseActivity context) {
        Intent intent = new Intent(context, ConnectPOS.class);
        context.startActivity(intent);
    }

    public static void startEmployeeID(BaseActivity context) {
        Intent intent = new Intent(context, EmployeeID.class);
        context.startActivity(intent);
        // context.overridePendingTransition(R.anim.slide_bottom_in,
        // R.anim.slide_top_out);
    }

    public static void startLogin(BaseActivity context) {
        Intent intent = new Intent(context, Login.class);
        context.startActivity(intent);
        // context.overridePendingTransition(R.anim.slide_bottom_in,
        // R.anim.slide_top_out);
    }



    public static void showToast(BaseActivity context, String text) {
        Toast toast = new Toast(context);
        LayoutInflater inflater = context.getLayoutInflater();
        View view = inflater.inflate(com.alfredbase.R.layout.toast_view, null);
        TextView tv_toast_view = (TextView) view.findViewById(com.alfredbase.R.id.tv_toast_view);
        tv_toast_view.setText(text);
        TextTypeFace textTypeFace = TextTypeFace.getInstance();
        textTypeFace.setTrajanProRegular(tv_toast_view);
        toast.setGravity(Gravity.BOTTOM, 0, 10);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(view);
        toast.show();
    }

    public static void showToast(BaseApplication context, String text) {
        Toast toast = new Toast(context);
        LayoutInflater inflater = context.getTopActivity().getLayoutInflater();
        View view = inflater.inflate(com.alfredbase.R.layout.toast_view, null);
        TextView tv_toast_view = (TextView) view.findViewById(com.alfredbase.R.id.tv_toast_view);
        tv_toast_view.setText(text);
        TextTypeFace textTypeFace = TextTypeFace.getInstance();
        textTypeFace.setTrajanProRegular(tv_toast_view);
        toast.setGravity(Gravity.BOTTOM, 0, 10);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(view);
        toast.show();
    }
}