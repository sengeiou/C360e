package com.alfred.print.jobs;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;

public class Utils {
	public static String loadJSONFromAsset(Context context) {
	    String json = null;
	    try {

	        InputStream is = context.getAssets().open("prtconf.json");
	        int size = is.available();
	        byte[] buffer = new byte[size];
	        is.read(buffer);
	        is.close();
	        json = new String(buffer, "UTF-8");
	    } catch (IOException ex) {
	        ex.printStackTrace();
	        return null;
	    }
	    return json;

	}
}
