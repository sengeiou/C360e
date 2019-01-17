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
}
