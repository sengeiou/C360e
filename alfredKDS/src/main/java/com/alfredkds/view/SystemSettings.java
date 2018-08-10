package com.alfredkds.view;

import android.content.Context;

import com.alfredbase.store.Store;

public class SystemSettings {
	private Context context;

	private boolean islandscape = false;
    
	public SystemSettings(Context context) {
		super();
		this.context = context;
	}







	public void setKdsLan(Integer kdslan) {
		Store.putInt(this.context, Store.PRINT_LABLE,
				kdslan.intValue());
		if(kdslan.intValue() == 1)
			this.islandscape = true;
		else
			this.islandscape = false;
	}



	public boolean isKdsLan() {
		Integer value = Store.getInt(context,
				Store.PRINT_LABLE);
		if(value != null && value != Store.DEFAULT_INT_TYPE){
			if(value.intValue() == 1){
				this.islandscape = true;
			}else{
				this.islandscape = false;
			}
		}
		return islandscape;
	}

}
