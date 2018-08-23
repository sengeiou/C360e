package com.alfredselfhelp.activity;

import android.view.View;
import android.widget.Button;

import com.alfredbase.BaseActivity;
import com.alfredselfhelp.R;
import com.alfredselfhelp.utils.UIHelp;


public class MainActivity extends BaseActivity {

private Button btn_start;
    protected void initView() {
        super.initView();
        setContentView(R.layout.activity_main);
        btn_start=(Button)findViewById(R.id.btn_start);
        btn_start.setOnClickListener(this);



    }

    @Override
    protected void handlerClickEvent(View v) {
        super.handlerClickEvent(v);
        switch (v.getId())
        {
            case R.id.btn_start:
                UIHelp.startMenu(context);
                break;
        }
    }
}
