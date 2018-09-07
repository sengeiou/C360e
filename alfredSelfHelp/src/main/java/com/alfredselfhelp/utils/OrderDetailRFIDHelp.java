package com.alfredselfhelp.utils;

import com.alfredbase.javabean.OrderDetail;
import com.alfredbase.utils.IntegerUtils;
import com.nordicid.nurapi.NurTag;
import com.nordicid.nurapi.NurTagStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderDetailRFIDHelp {
    public static List<String> getUnChooseItemBarCode(List<OrderDetail> orderDetails, NurTagStorage nurTagStorage) {
        Map<String, Integer> orderDetailNumMap = new HashMap<>();
        List<String> barCodes = new ArrayList<>();
        if (orderDetails != null) {
            for (OrderDetail orderDetail : orderDetails) {
                String barCode = IntegerUtils.format24(orderDetail.getBarCode());
                if (orderDetailNumMap.containsKey(barCode)) {
                    int orderDetailNum = orderDetailNumMap.get(barCode);
                    orderDetailNumMap.put(barCode, orderDetailNum + orderDetail.getItemNum());
                } else {
                    orderDetailNumMap.put(barCode, orderDetail.getItemNum());
                }
            }
        }
        if (nurTagStorage != null && nurTagStorage.size() > 0) {
            for (int i = 0; i < nurTagStorage.size(); i++) {
                NurTag nurTag = nurTagStorage.get(i);
                String barCode = nurTag.getEpcString();
                if (!orderDetailNumMap.containsKey(nurTag.getEpcString())) {
                    barCodes.add(nurTag.getEpcString());
                } else {
                    int orderDetailNum = orderDetailNumMap.get(barCode);
                    if (orderDetailNum > 1) {
                        orderDetailNumMap.put(barCode, orderDetailNum - 1);
                    } else {
                        orderDetailNumMap.remove(barCode);
                    }
                }

            }
        }
        return barCodes;
    }


    public static Map<String, Integer> getUnScannerItemBarCode(List<OrderDetail> orderDetails, NurTagStorage nurTagStorage) {
        Map<String, Integer> orderDetailNumMap = new HashMap<>();
        if (orderDetails != null) {
            for (OrderDetail orderDetail : orderDetails) {
                String barCode = IntegerUtils.format24(orderDetail.getBarCode());
                if (orderDetailNumMap.containsKey(barCode)) {
                    int orderDetailNum = orderDetailNumMap.get(barCode);
                    orderDetailNumMap.put(barCode, orderDetailNum + orderDetail.getItemNum());
                } else {
                    orderDetailNumMap.put(barCode, orderDetail.getItemNum());
                }
            }
        }
        if (nurTagStorage != null && nurTagStorage.size() > 0) {
            for (int i = 0; i < nurTagStorage.size(); i++) {
                NurTag nurTag = nurTagStorage.get(i);
                String barCode = nurTag.getEpcString();
                if(orderDetailNumMap.containsKey(barCode)) {
                    int orderDetailNum = orderDetailNumMap.get(barCode);
                    if (orderDetailNum > 1) {
                        orderDetailNumMap.put(barCode, orderDetailNum - 1);
                    } else {
                        orderDetailNumMap.remove(barCode);
                    }
                }
            }
        }
        return orderDetailNumMap;
    }

}
