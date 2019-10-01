package com.alfredposclient.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.alfredbase.javabean.User;
import com.alfredbase.utils.TextTypeFace;
import com.alfredposclient.R;

import java.util.List;

public class EmployeeAdapter extends BaseAdapter {
    private Context context;
    private List<User> data;
    private LayoutInflater inflater;
    private TextTypeFace textTypeFace = TextTypeFace.getInstance();

    public EmployeeAdapter(Context context, List<User> data){
        this.context =context;
        this.data =data;
        this.context = context;
        inflater =LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public User getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return data.get(i).getEmpId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.employee_item, null);
            holder = new ViewHolder();
            holder.tvTitle = (TextView) view.findViewById(R.id.title);
            holder.tvSubTitle = (TextView) view.findViewById(R.id.subtitle);
            holder.tvBudget = (TextView) view.findViewById(R.id.tv_budget);
            textTypeFace.setTrajanProBlod(holder.tvTitle);
            textTypeFace.setTrajanProBlod(holder.tvSubTitle);
            textTypeFace.setTrajanProBlod(holder.tvBudget);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        User item = data.get(i);
        holder.tvTitle.setText(item.getFirstName());
        holder.tvSubTitle.setText(item.getType().toString());
        holder.tvBudget.setText(item.getBudget());

        return view;
    }

    public class ViewHolder {
        public TextView tvTitle;
        public TextView tvSubTitle;
        public TextView tvBudget;
    }
}
