package com.alfred.callnum.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alfred.callnum.R;

import java.util.List;


public class MycallAdapter extends RvAdapter<CallBean> {

    public MycallAdapter(Context context, List<CallBean> list, RvListener listener) {
        super(context, list, listener);
    }


    @Override
    protected int getLayoutId(int viewType) {
        return viewType == 0 ? R.layout.item_mycall : R.layout.item_mycall_small;
    }

    @Override
    public int getItemViewType(int position) {

        if(position==0){
             return 0;
        }else {
            return 1;
        }

    }

    @Override
    protected RvHolder getHolder(View view, int viewType) {
        return new MyCallHolder(view, viewType, listener);
    }

    public class MyCallHolder extends RvHolder<CallBean> {
        TextView tvName;

        TextView tvSmallName;

        public MyCallHolder(View itemView, int type, RvListener listener) {
            super(itemView, type, listener);
            switch (type) {
                case 0:
                    tvName = (TextView) itemView.findViewById(R.id.tv_name);
                    break;
                case 1:
                    tvSmallName = (TextView) itemView.findViewById(R.id.tv_small_name);

                    break;
            }

        }

        @Override
        public void bindHolder(CallBean callBean, int position) {
            int itemViewType = MycallAdapter.this.getItemViewType(position);


            switch (itemViewType) {
                case 0:
                    tvName.setText(callBean.getName());
                    //    tvTitle.setText(sortBean.getName());
                    break;
                case 1:
                    //   tvCity.setText(sortBean.getName());
                    tvSmallName.setText(callBean.getName());
                    break;
            }
        }


    }
}
