package com.alfredbase.utils;

import com.alfredbase.BaseApplication;
import com.alfredbase.global.CoreData;
import com.alfredbase.javabean.Order;
import com.alfredbase.javabean.OrderDetail;
import com.alfredbase.javabean.RemainingStock;
import com.alfredbase.store.sql.OrderDetailSQL;
import com.alfredbase.store.sql.RemainingStockSQL;
import com.moonearly.utils.service.TcpUdpFactory;

import org.json.JSONObject;

import java.util.List;

public class RemainingStockHelper {
    //修改RemainingStock数量
    public static void updateRemainingStockNumByOrder(Order order){
        List<OrderDetail> orderDetailList = OrderDetailSQL.getOrderDetails(order.getId());

        for(OrderDetail orderDetail: orderDetailList) {
            updateRemainingStockNumByOrderDetail(orderDetail);
        }
    }

    public static boolean updateRemainingStockNumByOrderDetail(OrderDetail orderDetail){
        int itemTempId = CoreData.getInstance().getItemDetailById(orderDetail.getItemId()).getItemTemplateId();
        RemainingStock remainingStock = RemainingStockSQL.getRemainingStockByitemId(itemTempId);
        if (remainingStock != null) {
            RemainingStockSQL.updateRemainingById(orderDetail.getItemNum(), itemTempId);
            try {
                RemainingStock remainingStockVO = RemainingStockSQL.getRemainingStockByitemId(itemTempId);
                if(remainingStockVO.getQty() <= remainingStockVO.getDisplayQty()) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("RX", RxBus.RX_GET_STOCK);
                    TcpUdpFactory.sendUdpMsg(BaseApplication.UDP_INDEX_WAITER, TcpUdpFactory.UDP_REQUEST_MSG + jsonObject.toString(), null);
                    TcpUdpFactory.sendUdpMsg(BaseApplication.UDP_INDEX_EMENU, TcpUdpFactory.UDP_REQUEST_MSG + jsonObject.toString(), null);
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }else{
            return false;
        }
    }

}
