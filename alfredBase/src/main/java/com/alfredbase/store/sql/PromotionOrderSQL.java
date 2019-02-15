package com.alfredbase.store.sql;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.alfredbase.javabean.PromotionOrder;
import com.alfredbase.store.SQLExe;
import com.alfredbase.store.TableNames;
import com.alfredbase.utils.SQLiteStatementHelper;

import java.util.ArrayList;
import java.util.List;

public class PromotionOrderSQL {
    
    public static void addPromotionOrder(List<PromotionOrder> promotionOrders) {
        if (promotionOrders == null) {
            return;
        }
        SQLiteDatabase db = SQLExe.getDB();
        try {
            db.beginTransaction();

            String sql = "insert into "
                    + TableNames.PromotionOrder
                    + "(id, promotionId, discountPrice, discountPercentage,createTime,updateTime)"
                    + " values (?,?,?,?,?,?)";

            SQLiteStatement sqLiteStatement = db.compileStatement(
                    sql);
            for (PromotionOrder promotionOrder : promotionOrders) {
                SQLiteStatementHelper.bindLong(sqLiteStatement, 1,
                        promotionOrder.getId());
                SQLiteStatementHelper.bindLong(sqLiteStatement, 2,
                        promotionOrder.getPromotionId());
                SQLiteStatementHelper.bindString(sqLiteStatement, 3,
                        promotionOrder.getDiscountPrice());
                SQLiteStatementHelper.bindString(sqLiteStatement, 4,
                        promotionOrder.getDiscountPercentage());

                SQLiteStatementHelper.bindLong(sqLiteStatement, 5,
                        promotionOrder.getCreateTime());
                SQLiteStatementHelper.bindLong(sqLiteStatement, 6,
                        promotionOrder.getUpdateTime());

                sqLiteStatement.executeInsert();
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    public static ArrayList<PromotionOrder> getAllpromotionOrder() {
        ArrayList<PromotionOrder> result = new ArrayList<PromotionOrder>();
        String sql = "select * from " + TableNames.PromotionOrder + " order by id desc";
        Cursor cursor = null;
        SQLiteDatabase db = SQLExe.getDB();
        try {
            cursor = db.rawQuery(sql, new String[] {});
            int count = cursor.getCount();
            if (count < 1) {
                return result;
            }
            PromotionOrder promotionOrder = null;
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
                    .moveToNext()) {

                promotionOrder = new PromotionOrder();
                promotionOrder.setId(cursor.getInt(0));
                promotionOrder.setPromotionId(cursor.getInt(1));
                promotionOrder.setDiscountPrice(cursor.getString(2));
                promotionOrder.setDiscountPercentage(cursor.getString(3));

                promotionOrder.setCreateTime(cursor.getLong(4));
                promotionOrder.setUpdateTime(cursor.getLong(5));
                result.add(promotionOrder);
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




    public static void deletePromotion(PromotionOrder promotionOrder) {
        String sql = "delete from " + TableNames.PromotionOrder + " where id = ?";
        try {
            SQLExe.getDB().execSQL(sql, new Object[] { promotionOrder.getId() });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void deleteAllpromotionOrder() {
        String sql = "delete from " + TableNames.PromotionOrder;
        try {
            SQLExe.getDB().execSQL(sql, new Object[] {});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
