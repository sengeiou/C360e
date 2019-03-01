package com.alfredbase.utils;

import android.os.Build;

public class MachineUtil {
    private final static String TAG = MachineUtil.class.getSimpleName();
    public static boolean isSUNMIShow() {
        String brand = Build.BRAND;
        String model = Build.MODEL;
        String manufacturer = Build.MANUFACTURER;
        LogUtil.d(TAG, brand + "**************" + model);
        if ("SUNMI".equals(brand.toUpperCase())) {
            return true;
        }
        return false;
    }
    public static boolean isHisense() {
        String model = Build.MODEL;
        LogUtil.d(TAG, model + "**************" + model);
        if ("HK716".equals(model.toUpperCase())) {
            return true;
        }
        return false;
    }
}
