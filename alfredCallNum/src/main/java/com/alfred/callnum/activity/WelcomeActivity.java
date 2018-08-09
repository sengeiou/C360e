package com.alfred.callnum.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;

import com.alfred.callnum.R;
import com.alfred.callnum.fragment.OneFragment;

public class WelcomeActivity extends FragmentActivity implements View.OnClickListener {

    OneFragment oneFragment;

    private Button one, two, three, four;

    int viewId;

    //    @Override
//    protected void initView() {
//        super.initView();
//        setContentView(R.layout.activity_welcome);
//        createFragment();
//    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        one = (Button) findViewById(R.id.btn_one);
        two = (Button) findViewById(R.id.btn_two);
        three = (Button) findViewById(R.id.btn_three);
        four = (Button) findViewById(R.id.btn_four);
        one.setOnClickListener(this);
        two.setOnClickListener(this);
        three.setOnClickListener(this);
        four.setOnClickListener(this);

        // createFragment();
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {

            case R.id.btn_one:
                viewId = 1;
                intent.putExtra("viewId", viewId);
                intent.setClass(WelcomeActivity.this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_two:
                viewId = 2;
                intent.putExtra("viewId", viewId);
                intent.setClass(WelcomeActivity.this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_three:
                viewId = 3;
                intent.putExtra("viewId", viewId);
                intent.setClass(WelcomeActivity.this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_four:
                viewId = 4;
                intent.putExtra("viewId", viewId);
                intent.setClass(WelcomeActivity.this, MainActivity.class);
                startActivity(intent);
                break;


        }
    }
}
