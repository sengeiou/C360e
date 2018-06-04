package com.alfredwaiter.popupwindow;

import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.alfredbase.BaseActivity;
import com.alfredbase.global.CoreData;
import com.alfredbase.javabean.ItemDetail;
import com.alfredbase.javabean.ItemModifier;
import com.alfredbase.javabean.Modifier;
import com.alfredwaiter.R;
import com.alfredwaiter.activity.MainPage;
import com.alfredwaiter.adapter.ItemHeaderDecoration;
import com.alfredwaiter.adapter.WaiterModifierAdapter;
import com.alfredwaiter.javabean.ModifierCPVariance;
import com.alfredwaiter.listener.RvItemClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Alex on 2017/4/8.
 */

public class WaiterModifierCPWindow {
    private BaseActivity context;
    private Handler handler;
    private View parentView;
    private View contentView;
    private PopupWindow popupWindow;
    private RecyclerView lv_modifier;
    private WaiterModifierAdapter waiterModifierAdapter;
    private EditText remark_et;
    private Button btn_save_order_detail;
    private ItemDetail itemDetail;
    private List<ModifierCPVariance> modifierVariances = new ArrayList<ModifierCPVariance>();
    private List<Integer> modifierIds = new ArrayList<Integer>();
    private List<ModifierCPVariance> variances = new ArrayList<ModifierCPVariance>();
    private GridLayoutManager gridLayoutManager;
    private ModifierSetItemCountWindow setItemCountWindow;
    private List<Modifier> modifierTitels = new ArrayList<Modifier>();

    public WaiterModifierCPWindow(BaseActivity context, Handler handler, View parentView) {
        this.context = context;
        this.handler = handler;
        this.parentView = parentView;
        init();
    }

    private void init(){
        contentView = View.inflate(context, R.layout.waiter_modifiercp_popwindow, null);
        popupWindow = new PopupWindow(parentView,
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        popupWindow.setAnimationStyle(R.style.bottomInOutStyle);
        popupWindow.setContentView(contentView);
        lv_modifier = (RecyclerView) contentView.findViewById(R.id.lv_modifier);
        remark_et = (EditText) contentView.findViewById(R.id.remark_et);
        remark_et.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        remark_et.setGravity(Gravity.CENTER_VERTICAL);

        remark_et.setHorizontallyScrolling(false);

        btn_save_order_detail = (Button) contentView.findViewById(R.id.btn_save_order_detail);
        contentView.findViewById(R.id.pop_modifier_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                handler.sendEmptyMessage(MainPage.VIEW_ENVENT_GET_ORDERDETAILS);
            }
        });
        btn_save_order_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> map = new HashMap<String, Object>();
                for (ModifierCPVariance modifierVariance : modifierVariances){
                    if (modifierIds.size() > 0 && !modifierVariance.isTitle()) {
                        for (int id : modifierIds) {
                            if (id == modifierVariance.getModifierId()){
                                variances.add(modifierVariance);
                            }
                        }
                    }
                }
//                map.put("itemDetail", itemDetail);
                map.put("variances", variances);
                map.put("description", remark_et.getText().toString());
                handler.sendMessage(handler.obtainMessage(MainPage.VIEW_EVENT_ADD_ORDER_DETAIL_AND_MODIFIER, map));
                popupWindow.dismiss();
            }
        });
    }

    public void show(ItemDetail itemDetail) {
        show(itemDetail, null, "");

    }
    public void show(ItemDetail itemDetail, List<Integer> modifierIdList, String description){
        if(itemDetail == null)
            return;
        this.itemDetail = itemDetail;
        if(!popupWindow.isShowing()) {
            popupWindow.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
            popupWindow.setFocusable(true);
            popupWindow.setOutsideTouchable(true);
            popupWindow.update();
        }
        initData(modifierIdList);
        setItemCountWindow = new ModifierSetItemCountWindow(context, parentView, handler);
        if(modifierVariances != null && modifierVariances.size() > 0) {
            lv_modifier.setVisibility(View.VISIBLE);
            gridLayoutManager = new GridLayoutManager(context, 2);
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {

                @Override
                public int getSpanSize(int position) {
                    return modifierVariances.get(position).isTitle() ? 2 : 1;
                }
            });
            lv_modifier.setLayoutManager(gridLayoutManager);
            waiterModifierAdapter = new WaiterModifierAdapter(context, modifierVariances, this.modifierIds, new RvItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
//                if(view.getId() == R.id.rl_modifier_content){
//
//                }
                    ModifierCPVariance modifierCPVariance = modifierVariances.get(position);
                    if (!modifierCPVariance.isTitle()) {
//                    variances.add(modifierCPVariance);
                        if (modifierIds.contains(modifierCPVariance.getModifierId())) {
                            modifierIds.remove(Integer.valueOf(modifierCPVariance.getModifierId()));
                        } else {
                            modifierIds.add(modifierCPVariance.getModifierId());
                        }
                        waiterModifierAdapter.notifyDataSetChanged();
                    }
                }
            });
            ItemHeaderDecoration decoration = new ItemHeaderDecoration(context, modifierVariances, modifierTitels);
            decoration.setData(modifierVariances);
            lv_modifier.addItemDecoration(decoration);
            lv_modifier.setAdapter(waiterModifierAdapter);
        }else{
            lv_modifier.setVisibility(View.INVISIBLE);
        }
        remark_et.setText(description);
    }

//    public void setList(ModifierVariance modifierVariance){
////        for (ModifierVariance modifierVariance1 : modifierVariances){
////            if (modifierVariance1.getModifierId1() == modifierVariance.getModifierId1()){
////                modifierVariance1.setModQty(modifierVariance.getModQty());
////            }
////        }
////        modifierAdapter.notifyDataSetChanged();
//    }

    private void varianceModifiers(){

        List<ItemModifier> itemModifiers = CoreData.getInstance()
                .getItemModifiers(itemDetail);
        if (!itemModifiers.isEmpty()) {
            for (ItemModifier itemModifier : itemModifiers) {
                modifierTitels.add(CoreData.getInstance().getModifier(
                        itemModifier));
            }
        }
        for (int i = 0; i < modifierTitels.size(); i++) {
            Modifier modifierTitle = modifierTitels.get(i);
            ModifierCPVariance modifierCPVarianceTitle = new ModifierCPVariance();
            modifierCPVarianceTitle.setTitle(true);
            modifierCPVarianceTitle.setModifierId(modifierTitle.getId());
            modifierCPVarianceTitle.setModifierName(modifierTitle.getCategoryName());
            List<Modifier> modifiers = CoreData.getInstance().getModifiers(modifierTitle);
            if(modifiers != null && modifiers.size() > 0) {
                modifierCPVarianceTitle.setTag(String.valueOf(i));
                modifierVariances.add(modifierCPVarianceTitle);
                for (Modifier modifier : modifiers){
                    ModifierCPVariance modifierCPVariance = new ModifierCPVariance();
                    modifierCPVariance.setTitle(false);
                    modifierCPVariance.setModifierId(modifier.getId());
                    modifierCPVariance.setModifierName(modifier.getModifierName());
                    modifierCPVariance.setTag(String.valueOf(i));
                    modifierVariances.add(modifierCPVariance);
                }
            }
           }
    }
    public boolean isShowing(){
        return popupWindow.isShowing();
    }
    private void backgroundAlpha(float alpha) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = alpha;
        context.getWindow().setAttributes(lp);
    }

    private void initData(List<Integer> modifierIdList){
        modifierVariances.clear();
        variances.clear();
        this.modifierIds.clear();
        this.modifierTitels.clear();
        if(modifierIdList != null && modifierIdList.size() > 0){
            this.modifierIds.addAll(modifierIdList);
        }
        varianceModifiers();
    }

}
