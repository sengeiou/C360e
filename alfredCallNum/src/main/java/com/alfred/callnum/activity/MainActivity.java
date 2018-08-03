package com.alfred.callnum.activity;

import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;

import com.alfred.callnum.R;
import com.alfred.callnum.fragment.OneFragment;
import com.alfredbase.BaseActivity;
import com.alfredbase.MyBaseActivity;

public class MainActivity extends MyBaseActivity {
	OneFragment oneFragment;
	protected void initView() {
		super.initView();
		setContentView(R.layout.activity_main);
		createFragment();
	}



	public void createFragment() {
		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
		oneFragment = new OneFragment();
//        Bundle bundle = new Bundle();
//        bundle.putParcelableArrayList("right", mSortBean.getCategoryOneArray());
//        mSortDetailFragment.setArguments(bundle);
//        SortDetailFragment.setListener(this);
		// ItemHeaderDecoration.setCheckListener(this);
		fragmentTransaction.add(R.id.one_fragment, oneFragment);
		// fragmentTransaction.add(R.id.one_fragment, oneFragment);
		fragmentTransaction.commit();
	}
	@Override
	protected void handlerClickEvent(View v) {
		super.handlerClickEvent(v);
	}
}
