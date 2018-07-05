package com.alfredbase.store.sql;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.alfredbase.javabean.SettlementRestaurant;


import com.alfredbase.store.SQLExe;
import com.alfredbase.store.TableNames;
import com.alfredbase.utils.SQLiteStatementHelper;

import java.util.ArrayList;
import java.util.List;
public class SettlementRestaurantSQL {

    public static void addSettlementRestaurant(SettlementRestaurant settlementRestaurant) {
        if (settlementRestaurant == null)
            return;
        try {

            String sql = "replace into "
                    + TableNames.SettlementRestaurant
                    + "(id,restaurantId,mediaId,adjustmentsId,onlineServiceId,type,remarks,discriptionId,otherPaymentId)"
                    + " values (?,?,?,?,?,?,?,?,?)";
            SQLExe.getDB().execSQL(
                    sql,
                    new Object[] { settlementRestaurant.getId(),
                            settlementRestaurant.getRestaurantId(),
                            settlementRestaurant.getMediaId(),
                            settlementRestaurant.getAdjustmentsId(),
                            settlementRestaurant.getOnlineServiceId(),
                            settlementRestaurant.getType(), settlementRestaurant.getRemarks(),
                            settlementRestaurant.getDiscriptionId(), settlementRestaurant.getOtherPaymentId()
                           });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static SettlementRestaurant getAllSettlementRestaurant() {
        SettlementRestaurant result = new SettlementRestaurant();
        String sql = "select * from " + TableNames.SettlementRestaurant;
        Cursor cursor = null;
        SQLiteDatabase db = SQLExe.getDB();
        try {
            cursor = db.rawQuery(sql, new String[] {});
            int count = cursor.getCount();
            if (count < 1) {
                return result;
            }
            SettlementRestaurant settlementRestaurant = null;
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
                    .moveToNext()) {
                settlementRestaurant.setId(cursor.getInt(0));
                settlementRestaurant.setRestaurantId(cursor.getInt(1));
                settlementRestaurant.setMediaId(cursor.getInt(2));
                settlementRestaurant.setAdjustmentsId(cursor.getInt(3));
                settlementRestaurant.setOnlineServiceId(cursor.getInt(4));
                settlementRestaurant.setType(cursor.getInt(5));
                settlementRestaurant.setRemarks(cursor.getString(6));
                settlementRestaurant.setDiscriptionId(cursor.getInt(7));
                settlementRestaurant.setOtherPaymentId(cursor.getInt(8));
                result=settlementRestaurant;
            }
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return result;
    }

    public static void deleteSettlementRestaurant(SettlementRestaurant settlementRestaurant) {
        String sql = "delete from " + TableNames.SettlementRestaurant + " where id = ?";
        try {
            SQLExe.getDB().execSQL(sql, new Object[] { settlementRestaurant.getId() });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteAllSettlementRestaurant() {
        String sql = "delete from " + TableNames.SettlementRestaurant;
        try {
            SQLExe.getDB().execSQL(sql, new Object[] {});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
