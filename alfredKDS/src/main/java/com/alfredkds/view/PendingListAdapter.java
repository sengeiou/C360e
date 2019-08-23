package com.alfredkds.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.alfredbase.javabean.KotItemDetail;
import com.alfredkds.R;

import java.util.List;

/**
 * Created by Arif S. on 8/22/19
 */
public class PendingListAdapter extends RecyclerView.Adapter<PendingListAdapter.ViewHolder> {

    private Context context;
    private List<KotItemDetail> data;

    public PendingListAdapter(Context context, List<KotItemDetail> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = View.inflate(context, R.layout.item_detail_item, null);
        return new ViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(List<KotItemDetail> kotItemDetailList) {
        this.data = kotItemDetailList;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
