package com.alfredbase.store.sql;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.alfredbase.ParamConst;
import com.alfredbase.javabean.Order;
import com.alfredbase.javabean.OrderPromotion;
import com.alfredbase.javabean.model.SessionStatus;
import com.alfredbase.store.SQLExe;
import com.alfredbase.store.TableNames;

import java.util.ArrayList;

public class PromotionDataSQL {

    public static void addPromotionData(OrderPromotion promotionData) {
        if (promotionData == null) {
            return;
        }
     
        try {
            String sql = "insert into "
                    + TableNames.PromotionData
                    + "(id, promotionId, promotionName, promotionType, promotionAmount,discountPercentage," +
                    "itemId,itemName,freeNum," +
                    "freeItemId,freeItemName,createTime,updateTime,orderId,orderDetailId,discountPrice,businessDate)"
                    + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

         
            SQLExe.getDB().execSQL(
                    sql,
                    new Object[] { promotionData.getId(), promotionData.getPromotionId(),
                            promotionData.getPromotionName(), promotionData.getPromotionType(),
                            promotionData.getPromotionAmount(), promotionData.getDiscountPercentage(),
                            promotionData.getItemId(), promotionData.getItemName(),
                            promotionData.getFreeNum(), promotionData.getFreeItemId(),
                            promotionData.getFreeItemName(), promotionData.getCreateTime(), promotionData.getUpdateTime(),
                            promotionData.getOrderId(),promotionData.getOrderDetailId(),promotionData.getDiscountPrice(),
                            promotionData.getBusinessDate()
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void updatePromotionData(OrderPromotion promotionData) {
        if (promotionData == null) {
            return;
        }

        try {
            String sql = "replace into "
                    + TableNames.PromotionData
                    + "(id, promotionId, promotionName, promotionType, promotionAmount,discountPercentage," +
                    "itemId,itemName,freeNum," +
                    "freeItemId,freeItemName,createTime,updateTime,orderId,orderDetailId,discountPrice,businessDate)"
                    + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";


            SQLExe.getDB().execSQL(
                    sql,
                    new Object[] { promotionData.getId(), promotionData.getPromotionId(),
                            promotionData.getPromotionName(), promotionData.getPromotionType(),
                            promotionData.getPromotionAmount(), promotionData.getDiscountPercentage(),
                            promotionData.getItemId(), promotionData.getItemName(),
                            promotionData.getFreeNum(), promotionData.getFreeItemId(),
                            promotionData.getFreeItemName(), promotionData.getCreateTime(), promotionData.getUpdateTime(),
                            promotionData.getOrderId(),promotionData.getOrderDetailId(),promotionData.getDiscountPrice(),
                            promotionData.getBusinessDate()
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




    public static String  getPromotionDataSum(Order order)
    {

        String sql = "select sum(promotionAmount) from " + TableNames.PromotionData
                + " where orderId=? ";
        Cursor cursor = null;
        SQLiteDatabase db = SQLExe.getDB();
        String promotionTotal= null;
        try {
            cursor = db.rawQuery(sql,
                    new String[]{String.valueOf(order.getId())});
            int count = cursor.getCount();
//            if (count < 1) {
//                return result;
//            }

            if (cursor.moveToFirst()) {
                promotionTotal=cursor.getString(0);

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return promotionTotal;
    }

    public static String  getPromotionDataXSum(long businessDate,SessionStatus sessionStatus, long nowTime)
    {

        String sql = "select sum(promotionAmount) from " + TableNames.PromotionData
                + " where businessDate=? and createTime > ? and updateTime < ?";
        Cursor cursor = null;
        SQLiteDatabase db = SQLExe.getDB();
        String promotionTotal= null;
        try {
            cursor = db.rawQuery(sql,
                    new String[]{String.valueOf(businessDate), String.valueOf(sessionStatus.getTime()), String.valueOf(nowTime)});
            int count = cursor.getCount();
//            if (count < 1) {
//                return result;
//            }

            if (cursor.moveToFirst()) {
                promotionTotal=cursor.getString(0);

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return promotionTotal;
    }

    public static String  getPromotionDataZSum(long businessDate)
    {

        String sql = "select sum(promotionAmount) from " + TableNames.PromotionData
                + " where businessDate=? " ;
        Cursor cursor = null;
        SQLiteDatabase db = SQLExe.getDB();
        String promotionTotal= null;
        try {
            cursor = db.rawQuery(sql,
                    new String[]{});
            int count = cursor.getCount();
//            if (count < 1) {
//                return result;
//            }

            if (cursor.moveToFirst()) {
                promotionTotal=cursor.getString(0);

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return promotionTotal;
    }



    public static ArrayList<OrderPromotion> getOrderPromotionData(long businessDate, SessionStatus sessionStatus, long nowTime, int type)
          {
        ArrayList<OrderPromotion> result = new ArrayList<OrderPromotion>();
        String sql = "select * from " + TableNames.PromotionData
                + " where businessDate=? and createTime > ? and updateTime < ?";
        Cursor cursor = null;
        SQLiteDatabase db = SQLExe.getDB();
        try {
            cursor = db.rawQuery(sql,
                    new String[]{String.valueOf(businessDate), String.valueOf(sessionStatus.getTime()), String.valueOf(nowTime)});
            int count = cursor.getCount();
            if (count < 1) {
                return result;
            }
            OrderPromotion promotionData = null;
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
                    .moveToNext()) {
                promotionData = new OrderPromotion();
                promotionData.setId(cursor.getInt(0));
                promotionData.setPromotionId(cursor.getInt(1));
                promotionData.setPromotionName(cursor.getString(2));
                promotionData.setPromotionType(cursor.getInt(3));
                promotionData.setPromotionAmount(cursor.getString(4));
                promotionData.setDiscountPercentage(cursor.getString(5));
                promotionData.setItemId(cursor.getInt(6));
                promotionData.setItemName(cursor.getString(7));
                promotionData.setFreeNum(cursor.getInt(8));
                promotionData.setFreeItemId(cursor.getInt(9));
                promotionData.setFreeItemName(cursor.getString(10));
                promotionData.setCreateTime(cursor.getLong(11));
                promotionData.setUpdateTime(cursor.getLong(12));
                promotionData.setOrderId(cursor.getInt(13));
                promotionData.setOrderDetailId(cursor.getInt(14));
                promotionData.setDiscountPrice(cursor.getString(15));
                promotionData.setBusinessDate(cursor.getLong(16));
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

    public static ArrayList<OrderPromotion> getItemPromotionData(long businessDate, SessionStatus sessionStatus, long nowTime)
    {
        ArrayList<OrderPromotion> result = new ArrayList<OrderPromotion>();
        String sql = "select * from " + TableNames.PromotionData
                + " where businessDate=? and createTime > ? and updateTime < ? ";
        Cursor cursor = null;
        SQLiteDatabase db = SQLExe.getDB();
        try {
            cursor = db.rawQuery(sql,
                    new String[]{String.valueOf(businessDate), String.valueOf(sessionStatus.getTime()), String.valueOf(nowTime)});
            int count = cursor.getCount();
            if (count < 1) {
                return result;
            }
            OrderPromotion promotionData = null;
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
                    .moveToNext()) {
                promotionData = new OrderPromotion();
                promotionData.setId(cursor.getInt(0));
                promotionData.setPromotionId(cursor.getInt(1));
                promotionData.setPromotionName(cursor.getString(2));
                promotionData.setPromotionType(cursor.getInt(3));
                promotionData.setPromotionAmount(cursor.getString(4));
                promotionData.setDiscountPercentage(cursor.getString(5));
                promotionData.setItemId(cursor.getInt(6));
                promotionData.setItemName(cursor.getString(7));
                promotionData.setFreeNum(cursor.getInt(8));
                promotionData.setFreeItemId(cursor.getInt(9));
                promotionData.setFreeItemName(cursor.getString(10));
                promotionData.setCreateTime(cursor.getLong(11));
                promotionData.setUpdateTime(cursor.getLong(12));
                promotionData.setOrderId(cursor.getInt(13));
                promotionData.setOrderDetailId(cursor.getInt(14));
                promotionData.setDiscountPrice(cursor.getString(15));
                promotionData.setBusinessDate(cursor.getLong(16));
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


    public static ArrayList<OrderPromotion> getOrderPromotionData(long businessDate, int type)
    {
        ArrayList<OrderPromotion> result = new ArrayList<OrderPromotion>();
        String sql = "select * from " + TableNames.PromotionData
                + " where businessDate=? and createTime > ? and updateTime < ? and  promotionType= ?";
        Cursor cursor = null;
        SQLiteDatabase db = SQLExe.getDB();
        try {
            cursor = db.rawQuery(sql,
                    new String[]{String.valueOf(businessDate),String.valueOf(type)});
            int count = cursor.getCount();
            if (count < 1) {
                return result;
            }
            OrderPromotion promotionData = null;
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
                    .moveToNext()) {
                promotionData = new OrderPromotion();
                promotionData.setId(cursor.getInt(0));
                promotionData.setPromotionId(cursor.getInt(1));
                promotionData.setPromotionName(cursor.getString(2));
                promotionData.setPromotionType(cursor.getInt(3));
                promotionData.setPromotionAmount(cursor.getString(4));
                promotionData.setDiscountPercentage(cursor.getString(5));
                promotionData.setItemId(cursor.getInt(6));
                promotionData.setItemName(cursor.getString(7));
                promotionData.setFreeNum(cursor.getInt(8));
                promotionData.setFreeItemId(cursor.getInt(9));
                promotionData.setFreeItemName(cursor.getString(10));
                promotionData.setCreateTime(cursor.getLong(11));
                promotionData.setUpdateTime(cursor.getLong(12));
                promotionData.setOrderId(cursor.getInt(13));
                promotionData.setOrderDetailId(cursor.getInt(14));
                promotionData.setDiscountPrice(cursor.getString(15));
                promotionData.setBusinessDate(cursor.getLong(16));
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

    public static ArrayList<OrderPromotion> getItemPromotionData(long businessDate, int type)
    {
        ArrayList<OrderPromotion> result = new ArrayList<OrderPromotion>();
        String sql = "select * from " + TableNames.PromotionData
                + " where businessDate=?  and  promotionType= ?";
        Cursor cursor = null;
        SQLiteDatabase db = SQLExe.getDB();
        try {
            cursor = db.rawQuery(sql,
                    new String[]{String.valueOf(businessDate),String.valueOf(type)});
            int count = cursor.getCount();
            if (count < 1) {
                return result;
            }
            OrderPromotion promotionData = null;
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
                    .moveToNext()) {
                promotionData = new OrderPromotion();
                promotionData.setId(cursor.getInt(0));
                promotionData.setPromotionId(cursor.getInt(1));
                promotionData.setPromotionName(cursor.getString(2));
                promotionData.setPromotionType(cursor.getInt(3));
                promotionData.setPromotionAmount(cursor.getString(4));
                promotionData.setDiscountPercentage(cursor.getString(5));
                promotionData.setItemId(cursor.getInt(6));
                promotionData.setItemName(cursor.getString(7));
                promotionData.setFreeNum(cursor.getInt(8));
                promotionData.setFreeItemId(cursor.getInt(9));
                promotionData.setFreeItemName(cursor.getString(10));
                promotionData.setCreateTime(cursor.getLong(11));
                promotionData.setUpdateTime(cursor.getLong(12));
                promotionData.setOrderId(cursor.getInt(13));
                promotionData.setOrderDetailId(cursor.getInt(14));
                promotionData.setDiscountPrice(cursor.getString(15));
                promotionData.setBusinessDate(cursor.getLong(16));
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
    public static ArrayList<OrderPromotion> getPromotionDataOrOrderid(int orderId)
    {
        ArrayList<OrderPromotion> result = new ArrayList<OrderPromotion>();
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
            OrderPromotion promotionData = null;
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
                    .moveToNext()) {
                promotionData = new OrderPromotion();
                promotionData.setId(cursor.getInt(0));
                promotionData.setPromotionId(cursor.getInt(1));
                promotionData.setPromotionName(cursor.getString(2));
                promotionData.setPromotionType(cursor.getInt(3));
                promotionData.setPromotionAmount(cursor.getString(4));
                promotionData.setDiscountPercentage(cursor.getString(5));
                promotionData.setItemId(cursor.getInt(6));
                promotionData.setItemName(cursor.getString(7));
                promotionData.setFreeNum(cursor.getInt(8));
                promotionData.setFreeItemId(cursor.getInt(9));
                promotionData.setFreeItemName(cursor.getString(10));
                promotionData.setCreateTime(cursor.getLong(11));
                promotionData.setUpdateTime(cursor.getLong(12));
                promotionData.setOrderId(cursor.getInt(13));
                promotionData.setOrderDetailId(cursor.getInt(14));
                promotionData.setDiscountPrice(cursor.getString(15));
                promotionData.setBusinessDate(cursor.getLong(16));
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

    public static OrderPromotion getPromotionData(int orderId, int orderDetailId)
    {
       OrderPromotion promotionData = null;
        String sql = "select * from " + TableNames.PromotionData
                + " where orderId = ? and orderDetailId= ? ";
        Cursor cursor = null;
        SQLiteDatabase db = SQLExe.getDB();
        try {
            cursor = db.rawQuery(sql,
                    new String[]{String.valueOf(orderId),String.valueOf(orderDetailId)});
            int count = cursor.getCount();
            if (count < 1) {
                return promotionData;
            }

            if (cursor.moveToFirst()) {
                promotionData = new OrderPromotion();
                promotionData.setId(cursor.getInt(0));
                promotionData.setPromotionId(cursor.getInt(1));
                promotionData.setPromotionName(cursor.getString(2));
                promotionData.setPromotionType(cursor.getInt(3));
                promotionData.setPromotionAmount(cursor.getString(4));
                promotionData.setDiscountPercentage(cursor.getString(5));
                promotionData.setItemId(cursor.getInt(6));
                promotionData.setItemName(cursor.getString(7));
                promotionData.setFreeNum(cursor.getInt(8));
                promotionData.setFreeItemId(cursor.getInt(9));
                promotionData.setFreeItemName(cursor.getString(10));
                promotionData.setCreateTime(cursor.getLong(11));
                promotionData.setUpdateTime(cursor.getLong(12));
                promotionData.setOrderId(cursor.getInt(13));
                promotionData.setOrderDetailId(cursor.getInt(14));
                promotionData.setDiscountPrice(cursor.getString(15));
                promotionData.setBusinessDate(cursor.getLong(16));
                return promotionData;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return promotionData;
    }


    public static OrderPromotion getPromotionDataOrType(int orderId, int type)
    {
        OrderPromotion promotionData = null;
        String sql = "select * from " + TableNames.PromotionData
                + " where orderId = ? and promotionType = ? ";
        Cursor cursor = null;
        SQLiteDatabase db = SQLExe.getDB();
        try {
            cursor = db.rawQuery(sql,
                    new String[]{String.valueOf(orderId),String.valueOf(type)});
            int count = cursor.getCount();
            if (count < 1) {
                return promotionData;
            }

            if (cursor.moveToFirst()) {
                promotionData = new OrderPromotion();
                promotionData.setId(cursor.getInt(0));
                promotionData.setPromotionId(cursor.getInt(1));
                promotionData.setPromotionName(cursor.getString(2));
                promotionData.setPromotionType(cursor.getInt(3));
                promotionData.setPromotionAmount(cursor.getString(4));
                promotionData.setDiscountPercentage(cursor.getString(5));
                promotionData.setItemId(cursor.getInt(6));
                promotionData.setItemName(cursor.getString(7));
                promotionData.setFreeNum(cursor.getInt(8));
                promotionData.setFreeItemId(cursor.getInt(9));
                promotionData.setFreeItemName(cursor.getString(10));
                promotionData.setCreateTime(cursor.getLong(11));
                promotionData.setUpdateTime(cursor.getLong(12));
                promotionData.setOrderId(cursor.getInt(13));
                promotionData.setOrderDetailId(cursor.getInt(14));
                promotionData.setDiscountPrice(cursor.getString(15));
                promotionData.setBusinessDate(cursor.getLong(16));
                return promotionData;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return promotionData;
    }


    public static OrderPromotion getPromotionDataOrId(int orderId, int promotionId)
    {
        OrderPromotion promotionData = null;
        String sql = "select * from " + TableNames.PromotionData
                + " where orderId = ? and promotionId = ? ";
        Cursor cursor = null;
        SQLiteDatabase db = SQLExe.getDB();
        try {
            cursor = db.rawQuery(sql,
                    new String[]{String.valueOf(orderId),String.valueOf(promotionId)});
            int count = cursor.getCount();
            if (count < 1) {
                return promotionData;
            }

            if (cursor.moveToFirst()) {
                promotionData = new OrderPromotion();
                promotionData.setId(cursor.getInt(0));
                promotionData.setPromotionId(cursor.getInt(1));
                promotionData.setPromotionName(cursor.getString(2));
                promotionData.setPromotionType(cursor.getInt(3));
                promotionData.setPromotionAmount(cursor.getString(4));
                promotionData.setDiscountPercentage(cursor.getString(5));
                promotionData.setItemId(cursor.getInt(6));
                promotionData.setItemName(cursor.getString(7));
                promotionData.setFreeNum(cursor.getInt(8));
                promotionData.setFreeItemId(cursor.getInt(9));
                promotionData.setFreeItemName(cursor.getString(10));
                promotionData.setCreateTime(cursor.getLong(11));
                promotionData.setUpdateTime(cursor.getLong(12));
                promotionData.setOrderId(cursor.getInt(13));
                promotionData.setOrderDetailId(cursor.getInt(14));
                promotionData.setDiscountPrice(cursor.getString(15));
                promotionData.setBusinessDate(cursor.getLong(16));
                return promotionData;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return promotionData;
    }


    public static ArrayList<OrderPromotion> getAllPromotionData() {
        ArrayList<OrderPromotion> result = new ArrayList<OrderPromotion>();
        String sql = "select * from " + TableNames.PromotionItem + " order by id desc";
        Cursor cursor = null;
        SQLiteDatabase db = SQLExe.getDB();
        try {
            cursor = db.rawQuery(sql, new String[] {});
            int count = cursor.getCount();
            if (count < 1) {
                return result;
            }
            OrderPromotion promotionData = null;
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
                    .moveToNext()) {
                promotionData = new OrderPromotion();
//                id, promotionId, promotionName, promotionType, promotionAmount,discountPercentage," +
//                "itemId,itemName,freeNum," +
//                        "freeItemId,freeItemName,createTime,updateTime,orderId,orderDetailId,discountPrice,businessDate
                promotionData.setId(cursor.getInt(0));
                promotionData.setPromotionId(cursor.getInt(1));
                promotionData.setPromotionName(cursor.getString(2));
                promotionData.setPromotionType(cursor.getInt(3));
                promotionData.setPromotionAmount(cursor.getString(4));
                promotionData.setDiscountPercentage(cursor.getString(5));
                promotionData.setItemId(cursor.getInt(6));
                promotionData.setItemName(cursor.getString(7));
                promotionData.setFreeNum(cursor.getInt(8));
                promotionData.setFreeItemId(cursor.getInt(9));
                promotionData.setFreeItemName(cursor.getString(10));
                promotionData.setCreateTime(cursor.getLong(11));
                promotionData.setUpdateTime(cursor.getLong(12));
                promotionData.setOrderId(cursor.getInt(13));
                promotionData.setOrderDetailId(cursor.getInt(14));
                promotionData.setDiscountPrice(cursor.getString(15));
                promotionData.setBusinessDate(cursor.getLong(16));
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





    public static void deletePromotionAndFree(OrderPromotion promotionData) {

        String delePromotionData = "delete from " + TableNames.PromotionData + " where id = ?";
        // 删除免费菜的信息
        String deleteFree = "delete from "+ TableNames.OrderDetail + " where id = ? and orderDetailStatus < ? ";
        Cursor cursor = null;
        SQLiteDatabase db = SQLExe.getDB();

        try {
            db.beginTransaction();
            db.execSQL(delePromotionData,
                    new Object[] { String.valueOf(promotionData.getId())});
            db.execSQL(deleteFree,
                    new Object[] { String.valueOf(promotionData.getOrderDetailId()), String.valueOf(ParamConst.ORDERDETAIL_STATUS_KOTPRINTERD)});

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            db.endTransaction();
        }
    }

    public static void deletePromotionDataOrderId(Order order) {
        String sql = "delete from " + TableNames.PromotionData + " where orderId = ?";
        try {
            SQLExe.getDB().execSQL(sql, new Object[] { order.getId() });
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
