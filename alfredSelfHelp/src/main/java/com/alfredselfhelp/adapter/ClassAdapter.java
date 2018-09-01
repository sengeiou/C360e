package com.alfredselfhelp.adapter;


import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.alfredselfhelp.R;

import java.util.List;

public class ClassAdapter extends RvAdapter<String> {

    private int checkedPosition;

    public void setCheckedPosition(int checkedPosition) {
        this.checkedPosition = checkedPosition;
        notifyDataSetChanged();
    }

    public ClassAdapter(Context context, List<String> list, RvListener listener) {
        super(context, list, listener);
    }

    @Override
    protected int getLayoutId(int viewType) {
        return R.layout.item_class_list;
    }

    @Override
    protected RvHolder getHolder(View view, int viewType) {
        return new ClassHolder(view, viewType, listener);
    }

    private class ClassHolder extends RvHolder<String> {

        private TextView tvName;
        private View mView;

        ClassHolder(View itemView, int type, RvListener listener) {
            super(itemView, type, listener);
            this.mView = itemView;

        }

        @Override
        public void bindHolder(String string, int position) {
            tvName.setText(string);
            if (position == checkedPosition) {
                mView.setBackgroundColor(Color.parseColor("#f3f3f3"));
                tvName.setTextColor(Color.parseColor("#0068cf"));
            } else {
                mView.setBackgroundColor(Color.parseColor("#FFFFFF"));
                tvName.setTextColor(Color.parseColor("#1e1d1d"));
            }
        }

    }
}
