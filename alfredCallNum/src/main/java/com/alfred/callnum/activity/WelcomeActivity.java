package com.alfred.callnum.activity;

import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;

import com.alfred.callnum.R;
import com.alfred.callnum.fragment.OneFragment;
import com.alfredbase.BaseActivity;
import com.alfredbase.MyBaseActivity;

public class WelcomeActivity extends FragmentActivity implements View.OnClickListener {

OneFragment oneFragment;

private Button one,two,three,four;

//    @Override
//    protected void initView() {
//        super.initView();
//        setContentView(R.layout.activity_welcome);
//        createFragment();
//    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        one=(Button)findViewById(R.id.btn_one);
        two=(Button)findViewById(R.id.btn_two);
        three=(Button)findViewById(R.id.btn_three);
        four=(Button)findViewById(R.id.btn_four);
        one.setOnClickListener(this);
        two.setOnClickListener(this);
        three.setOnClickListener(this);
        four.setOnClickListener(this);

       // createFragment();
    }




    @Override
    public void onClick(View v) {
switch (v.getId())
{
    case R.id.btn_one:
    case R.id.btn_two:
    case R.id.btn_three:
    case R.id.btn_four:
        Intent intent=new Intent();
        intent.setClass(WelcomeActivity.this,MainActivity.class);
        startActivity(intent);

}
    }
}
