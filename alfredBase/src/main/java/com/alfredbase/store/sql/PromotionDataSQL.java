package com.alfredbase.store.sql;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.alfredbase.ParamConst;
import com.alfredbase.javabean.OrderDetail;
import com.alfredbase.javabean.PromotionData;
import com.alfredbase.store.SQLExe;
import com.alfredbase.store.TableNames;
import com.alfredbase.utils.SQLiteStatementHelper;

import java.util.ArrayList;
import java.util.List;

public class PromotionDataSQL {

    public static void addPromotionData(PromotionData promotionData) {
        if (promotionData == null) {
            return;
        }
     
        try {
            String sql = "insert into "
                    + TableNames.PromotionData
                    + "(id, promotionId, promotionName, promotionType, promotionAmount,discountPercentage," +
                    "itemId,itemName,freeNum," +
                    "freeItemId,freeItemName,createTime,updateTime,orderId,orderDetailId,discountPrice)"
                    + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

         
            SQLExe.getDB().execSQL(
                    sql,
                    new Object[] { promotionData.getId(), promotionData.getPromotionId(),
                            promotionData.getPromotionName(), promotionData.getPromotionType(),
                            promotionData.getPromotionAmount(), promotionData.getDiscountPercentage(),
                            promotionData.getItemId(), promotionData.getItemName(),
                            promotionData.getFreeNum(), promotionData.getFreeItemId(),
                            promotionData.getFreeItemName(), promotionData.getCreateTime(), promotionData.getUpdateTime(),
                            promotionData.getOrderId(),promotionData.getOrderDetailId(),promotionData.getDiscountPrice()
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public static void addPromotionItem(List<ItemPromotion> itemPromotions) {
//        if (itemPromotions == null) {
//            return;
//        }
//        SQLiteDatabase db = SQLExe.getDB();
//        try {
//            db.beginTransaction();
//
//            String sql = "insert into "
//                    + TableNames.PromotionItem
//                    + "(id, promotionId, itemMainCategoryId, itemCategoryId, itemId,type," +
//                    "discountPrice,discountPercentage,freeNum,freeItemId,itemMainCategoryName," +
//                    "itemCategoryName,itemName,freeItemName,createTime,updateTime)"
//                    + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
//
//
//            SQLiteStatement sqLiteStatement = db.compileStatement(
//                    sql);
//            for (ItemPromotion itemPromotion : itemPromotions) {
//
//                SQLiteStatementHelper.bindLong(sqLiteStatement, 1,
//                        itemPromotion.getId());
//                SQLiteStatementHelper.bindLong(sqLiteStatement, 2,
//                        itemPromotion.getPromotionId());
//                SQLiteStatementHelper.bindLong(sqLiteStatement, 3,
//                        itemPromotion.getItemMainCategoryId());
//                SQLiteStatementHelper.bindLong(sqLiteStatement, 4,
//                        itemPromotion.getItemCategoryId());
//                SQLiteStatementHelper.bindLong(sqLiteStatement, 5,
//                        itemPromotion.getItemId());
//                SQLiteStatementHelper.bindLong(sqLiteStatement, 6,
//                        itemPromotion.getType());
//                SQLiteStatementHelper.bindString(sqLiteStatement, 7,
//                        itemPromotion.getDiscountPrice());
//                SQLiteStatementHelper.bindString(sqLiteStatement, 8,
//                        itemPromotion.getDiscountPercentage());
//
//                SQLiteStatementHelper.bindLong(sqLiteStatement, 9,
//                        itemPromotion.getFreeNum());
//                SQLiteStatementHelper.bindLong(sqLiteStatement, 10,
//                        itemPromotion.getFreeItemId());
//                SQLiteStatementHelper.bindString(sqLiteStatement, 11,
//                        itemPromotion.getItemMainCategoryName());
//                SQLiteStatementHelper.bindString(sqLiteStatement, 12,
//                        itemPromotion.getItemCategoryName());
//                SQLiteStatementHelper.bindString(sqLiteStatement, 13,
//                        itemPromotion.getItemName());
//                SQLiteStatementHelper.bindString(sqLiteStatement, 14,
//                        itemPromotion.getFreeItemName());
//                SQLiteStatementHelper.bindLong(sqLiteStatement, 15,
//                        itemPromotion.getCreateTime());
//                SQLiteStatementHelper.bindLong(sqLiteStatement, 16,
//                        itemPromotion.getUpdateTime());
//
//                sqLiteStatement.executeInsert();
//            }
//            db.setTransactionSuccessful();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            db.endTransaction();
//        }
//    }


    public static ArrayList<PromotionData> getPromotionDataOrOrderid(int orderId)
          {
        ArrayList<PromotionData> result = new ArrayList<PromotionData>();
        String sql = "select * from " + TableNames.PromotionData
                + " where orderId = ? ";
        Cursor cursor = null;
        SQLiteDatabase db = SQLExe.getDB();
        try {
            cursor = db.rawQuery(sql,
                    new String[]{String.valueOf(orderId)});
            int count = cursor.getCount();
            if (count < 1) {
                return result;
            }
            PromotionData promotionData = null;
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
                    .moveToNext()) {
                promotionData = new PromotionData();
                promotionData.setId(cursor.getInt(0));
                promotionData.setPromotionId(cursor.getInt(1));
                promotionData.setPromotionName(cursor.getString(2));
                promotionData.setPromotionAmount(cursor.getString(3));
                promotionData.setDiscountPercentage(cursor.getString(4));
                promotionData.setItemId(cursor.getInt(5));
                promotionData.setItemName(cursor.getString(6));
                promotionData.setFreeNum(cursor.getInt(7));
                promotionData.setFreeItemId(cursor.getInt(8));
                promotionData.setFreeItemName(cursor.getString(9));
                promotionData.setFreeItemId(cursor.getInt(10));
                promotionData.setCreateTime(cursor.getLong(11));
                promotionData.setUpdateTime(cursor.getLong(12));
                promotionData.setOrderId(cursor.getInt(13));
                promotionData.setOrderDetailId(cursor.getInt(14));
                promotionData.setDiscountPrice(cursor.getString(15));
                result.add(promotionData);
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
    public static ArrayList<PromotionData> getAllPromotionData() {
        ArrayList<PromotionData> result = new ArrayList<PromotionData>();
        String sql = "select * from " + TableNames.PromotionItem + " order by id desc";
        Cursor cursor = null;
        SQLiteDatabase db = SQLExe.getDB();
        try {
            cursor = db.rawQuery(sql, new String[] {});
            int count = cursor.getCount();
            if (count < 1) {
                return result;
            }
            PromotionData promotionData = null;
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
                    .moveToNext()) {
                promotionData = new PromotionData();
                promotionData.setId(cursor.getInt(0));
                promotionData.setPromotionId(cursor.getInt(1));
                promotionData.setPromotionName(cursor.getString(2));
                promotionData.setPromotionAmount(cursor.getString(3));
                promotionData.setDiscountPercentage(cursor.getString(4));
                promotionData.setItemId(cursor.getInt(5));
                promotionData.setItemName(cursor.getString(6));
                promotionData.setFreeNum(cursor.getInt(7));
                promotionData.setFreeItemId(cursor.getInt(8));
                promotionData.setFreeItemName(cursor.getString(9));
                promotionData.setFreeItemId(cursor.getInt(10));
                promotionData.setCreateTime(cursor.getLong(11));
                promotionData.setUpdateTime(cursor.getLong(12));
                promotionData.setOrderId(cursor.getInt(13));
                promotionData.setOrderDetailId(cursor.getInt(14));
                promotionData.setDiscountPrice(cursor.getString(15));
                result.add(promotionData);
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




    public static void deletePromotionData(PromotionData promotionData) {
        String sql = "delete from " + TableNames.PromotionData + " where id = ?";
        try {
            SQLExe.getDB().execSQL(sql, new Object[] { promotionData.getId() });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void deleteAllPromotionData() {
        String sql = "delete from " + TableNames.PromotionData;
        try {
            SQLExe.getDB().execSQL(sql, new Object[] {});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
