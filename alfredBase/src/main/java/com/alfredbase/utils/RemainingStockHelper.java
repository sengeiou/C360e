package com.alfredbase.utils;

import com.alfredbase.global.CoreData;
import com.alfredbase.javabean.Order;
import com.alfredbase.javabean.OrderDetail;
import com.alfredbase.javabean.RemainingStock;
import com.alfredbase.store.sql.OrderDetailSQL;
import com.alfredbase.store.sql.RemainingStockSQL;

import java.util.List;

public class RemainingStockHelper {
    //修改RemainingStock数量
    public static void updateRemainingStockNum(Order order){
        List<OrderDetail> orderDetailList = OrderDetailSQL.getOrderDetails(order.getId());

        for(OrderDetail orderDetail: orderDetailList) {
            int itemTempId = CoreData.getInstance().getItemDetailById(orderDetail.getItemId()).getItemTemplateId();
            RemainingStock remainingStock = RemainingStockSQL.getRemainingStockByitemId(itemTempId);
            if (remainingStock != null) {
                RemainingStockSQL.updateRemainingById(orderDetail.getItemNum(), itemTempId);
            }
        }
    }
}
