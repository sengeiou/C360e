package com.alfredkds.view;

import android.content.Context;

import com.alfredbase.store.Store;
import com.alfredkds.activity.Setting;

public class SystemSettings {
    private Context context;

    public static final int MODE_BALANCE = 0;
    public static final int MODE_STACK = 1;
    private boolean islandscape = false;

    public SystemSettings(Context context) {
        super();
        this.context = context;
    }

    public void setKdsLan(Integer kdslan) {
        Store.putInt(this.context, Store.PRINT_LABLE,
                kdslan.intValue());
        if (kdslan.intValue() == 1)
            this.islandscape = true;
        else
            this.islandscape = false;
    }

    public boolean isKdsLan() {
        Integer value = Store.getInt(context,
                Store.PRINT_LABLE);
        if (value != null && value != Store.DEFAULT_INT_TYPE) {
            if (value.intValue() == 1) {
                this.islandscape = true;
            } else {
                this.islandscape = false;
            }
        }
        return islandscape;
    }

    public void setBalancerMode(Integer mode) {
        Store.putInt(this.context, Store.MODE_BALANCER, mode);
    }

    public int getBalancerMode() {
        return Store.getInt(this.context, Store.MODE_BALANCER);
    }

    public void setPendingList(boolean value) {
        Store.putBoolean(this.context, Store.PENDING_LIST, value);
    }

    public boolean isPendingList() {
        return Store.getBoolean(this.context, Store.PENDING_LIST, false);
    }

}
