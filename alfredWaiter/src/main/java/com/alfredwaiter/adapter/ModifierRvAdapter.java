package com.alfredwaiter.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.alfredbase.javabean.ItemDetail;
import com.alfredwaiter.R;
import com.alfredwaiter.javabean.ModifierVariance;
import com.alfredwaiter.popupwindow.ModifierSetItemCountWindow;
import com.alfredwaiter.view.ModifierCountView;
import com.customRecycler.ItemClickListener;
import com.customRecycler.RecyclerBean;
import com.customRecycler.RecyclerViewMenuAdapter;

import java.util.List;

/**
 * Created by zhangcong on 2017/7/25.
 */

public class ModifierRvAdapter extends RecyclerViewMenuAdapter<ModifierVariance> {
    private ItemDetail itemDetail;
    private ModifierSetItemCountWindow setItemCountWindow;
    private ModifierCountView.OnCountChange onCountChange;
    public ModifierRvAdapter(Context context, List<ModifierVariance> list, ItemClickListener listener, ItemDetail itemDetail, ModifierSetItemCountWindow setItemCountWindow, ModifierCountView.OnCountChange onCountChange) {
        super(context, list, listener);
        this.itemDetail = itemDetail;
        this.setItemCountWindow = setItemCountWindow;
        this.onCountChange = onCountChange;
    }

    @Override
    protected RvHolder getHolder(View view, int viewType) {
        return new CityHolder(view,viewType,listener);
    }

    @Override
    public int getItemViewType(int position) {
        return  1;
//        return  list.get(position).isTitle() ? 0 : 1;
    }

    @Override
    protected int getLayoutId(int viewType) {
        return viewType==0 ? R.layout.modifier_item_title:R.layout.modifier_item;
    }
    private class CityHolder extends RvHolder<RecyclerBean>
    {
        private TextView title;
        private ModifierCountView modifierCountView;

        public CityHolder(View itemView, int type,ItemClickListener listener) {
            super(itemView,type, listener);
            switch (type)
            {
                case 0:
                    title= (TextView) itemView.findViewById(R.id.tv_item_title);
                    break;
                case 1:
                    modifierCountView= (ModifierCountView) itemView.findViewById(R.id.countView_item);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void bindHolder(RecyclerBean cityBean, int position) {
            int itemViewTtpe=ModifierRvAdapter.this.getItemViewType(position);
            switch (itemViewTtpe)
            {
                case 0:
//                    title.setText(list.get(position).getModifierName());
                    break;
                case 1:
                    modifierCountView.setParam(itemDetail, list.get(position), setItemCountWindow);
                    modifierCountView.setOnCountChange(onCountChange);
                    break;
                case 2:
                    break;
            }
        }
    }

}
