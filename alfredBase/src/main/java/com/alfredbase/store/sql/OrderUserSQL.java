package com.alfredbase.store.sql;

import android.database.Cursor;

import com.alfredbase.javabean.OrderUser;
import com.alfredbase.store.SQLExe;
import com.alfredbase.store.TableNames;

public class OrderUserSQL {
    public static void addOrder(OrderUser orderUser) {
        if (orderUser == null) {
            return;
        }
        try {
            String sql = "insert into "
                    + TableNames.OrderUser
                    + "(id, orderUserId, userId, orderId, businessDate, transactionAmount, createTime, "
                    + "updateTime)"
                    + " values (?,?,?,?,?,?,?,?)";
            SQLExe.getDB().execSQL(
                    sql,
                    new Object[] { orderUser.getId(), orderUser.getOrderUserId(),
                            orderUser.getUserId(), orderUser.getOrderId(),
                            orderUser.getBusinessDate(), orderUser.getTransactionAmount(),
                            orderUser.getCreateTime(), orderUser.getUpdateTime()
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void update(OrderUser orderUser) {
        if (orderUser == null) {
            return;
        }
        try {
            String sql = "replace into "
                    + TableNames.Order
                    + "(id, orderUserId, userId, orderId, businessDate, transactionAmount, createTime, "
                    + "updateTime)"
                    + " values (?,?,?,?,?,?,?,?)";
            SQLExe.getDB().execSQL(
                    sql,
                    new Object[] { orderUser.getId(), orderUser.getOrderUserId(),
                            orderUser.getUserId(), orderUser.getOrderId(),
                            orderUser.getBusinessDate(), orderUser.getTransactionAmount(),
                            orderUser.getCreateTime(), orderUser.getUpdateTime()});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static OrderUser getOrderUserById(int id) {
        OrderUser orderUser = null;
        String sql = "select * from " + TableNames.OrderUser
                + " where id = ?";
        Cursor cursor = null;
        try {
            cursor = SQLExe.getDB().rawQuery(
                    sql,
                    new String[] { id + "" });
            int count = cursor.getCount();
            if (count < 1) {
                return orderUser;
            }
            if (cursor.moveToFirst()) {
                orderUser = new OrderUser();
                orderUser.setId(cursor.getInt(0));
                orderUser.setOrderUserId(cursor.getInt(1));
                orderUser.setUserId(cursor.getInt(2));
                orderUser.setOrderId(cursor.getInt(3));
                orderUser.setBusinessDate(cursor.getLong(4));
                orderUser.setTransactionAmount(cursor.getString(5));
                orderUser.setCreateTime(cursor.getLong(6));
                orderUser.setUpdateTime(cursor.getLong(7));
                return orderUser;
            }
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return orderUser;
    }
    public static OrderUser getOrderUserByOrderId(int id) {
        OrderUser orderUser = null;
        String sql = "select * from " + TableNames.OrderUser
                + " where orderId = ?";
        Cursor cursor = null;
        try {
            cursor = SQLExe.getDB().rawQuery(
                    sql,
                    new String[] { id + "" });
            int count = cursor.getCount();
            if (count < 1) {
                return orderUser;
            }
            if (cursor.moveToFirst()) {
                orderUser = new OrderUser();
                orderUser.setId(cursor.getInt(0));
                orderUser.setOrderUserId(cursor.getInt(1));
                orderUser.setUserId(cursor.getInt(2));
                orderUser.setOrderId(cursor.getInt(3));
                orderUser.setBusinessDate(cursor.getLong(4));
                orderUser.setTransactionAmount(cursor.getString(5));
                orderUser.setCreateTime(cursor.getLong(6));
                orderUser.setUpdateTime(cursor.getLong(7));
                return orderUser;
            }
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return orderUser;
    }

}
