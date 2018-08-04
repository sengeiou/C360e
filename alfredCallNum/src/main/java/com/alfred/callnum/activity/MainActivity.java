package com.alfred.callnum.activity;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;

import com.alfred.callnum.R;
import com.alfred.callnum.fragment.OneFragment;
import com.alfred.callnum.fragment.TwoFragment;
import com.alfredbase.BaseActivity;
import com.alfredbase.MyBaseActivity;

public class MainActivity extends MyBaseActivity {
	OneFragment oneFragment;
	TwoFragment twoFragment;
	int viewId;
	protected void initView() {
		super.initView();
		setContentView(R.layout.activity_main);
		Intent intent = getIntent();
		viewId = intent.getIntExtra("viewId",0);
		createFragment();
	}



	public void createFragment() {
		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
		if(viewId==1||viewId==2) {
			oneFragment = new OneFragment();
			oneFragment.setViewId(viewId);
			fragmentTransaction.add(R.id.one_fragment, oneFragment);
		}else {
			twoFragment = new TwoFragment();
			twoFragment.setViewId(viewId);
			fragmentTransaction.add(R.id.one_fragment, twoFragment);
		}

		fragmentTransaction.commit();
	}
	@Override
	protected void handlerClickEvent(View v) {
		super.handlerClickEvent(v);
	}
}
