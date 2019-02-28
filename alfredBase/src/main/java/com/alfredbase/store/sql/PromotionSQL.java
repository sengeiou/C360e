package com.alfredbase.store.sql;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.alfredbase.javabean.Promotion;
import com.alfredbase.javabean.PromotionAndWeekVo;
import com.alfredbase.javabean.RemainingStock;
import com.alfredbase.store.SQLExe;
import com.alfredbase.store.TableNames;
import com.alfredbase.utils.SQLiteStatementHelper;

import java.util.ArrayList;
import java.util.List;

public class PromotionSQL {

    public static void addPromotion(List<Promotion> Promotions) {
        if (Promotions == null) {
            return;
        }
        SQLiteDatabase db = SQLExe.getDB();
        try {
            db.beginTransaction();

            String sql = "insert into "
                    + TableNames.Promotion
                    + "(id, type, promotionName, restaurantId, isActive)"
                    + " values (?,?,?,?,?)";
            SQLiteStatement sqLiteStatement = db.compileStatement(
                    sql);
            for (Promotion promotion : Promotions) {
                SQLiteStatementHelper.bindLong(sqLiteStatement, 1,
                        promotion.getId());
                SQLiteStatementHelper.bindLong(sqLiteStatement, 2,
                        promotion.getType());
                SQLiteStatementHelper.bindString(sqLiteStatement, 3,
                        promotion.getPromotionName());
                SQLiteStatementHelper.bindLong(sqLiteStatement, 4,
                        promotion.getRestaurantId());
                SQLiteStatementHelper.bindLong(sqLiteStatement, 5,
                        promotion.getIsActive());

                sqLiteStatement.executeInsert();
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    public static ArrayList<Promotion> getAllPromotion() {
        ArrayList<Promotion> result = new ArrayList<Promotion>();
        String sql = "select * from " + TableNames.Promotion + " order by id desc";
        Cursor cursor = null;
        SQLiteDatabase db = SQLExe.getDB();
        try {
            cursor = db.rawQuery(sql, new String[] {});
            int count = cursor.getCount();
            if (count < 1) {
                return result;
            }
            Promotion promotion = null;
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
                    .moveToNext()) {

                promotion = new Promotion();
                promotion.setId(cursor.getInt(0));
                promotion.setType(cursor.getInt(1));
                promotion.setPromotionName(cursor.getString(2));
                promotion.setRestaurantId(cursor.getInt(3));
                promotion.setIsActive(cursor.getInt(4));
                result.add(promotion);
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

    public static Promotion getPromotion(int proId) {
       Promotion promotion = null;
        String sql = "select * from " + TableNames.Promotion +  " where id = ? ";
        Cursor cursor = null;
        SQLiteDatabase db = SQLExe.getDB();
        try {
            cursor = db.rawQuery(sql, new String[] {String.valueOf(proId)});
            int count = cursor.getCount();
            if (count < 1) {
                return promotion;
            }

            if (cursor.moveToFirst()) {

                promotion = new Promotion();
                promotion.setId(cursor.getInt(0));
                promotion.setType(cursor.getInt(1));
                promotion.setPromotionName(cursor.getString(2));
                promotion.setRestaurantId(cursor.getInt(3));
                promotion.setIsActive(cursor.getInt(4));
               return promotion;
            }
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return promotion;
    }




    public static void deletePromotion(Promotion promotion) {
        String sql = "delete from " + TableNames.Promotion + " where id = ?";
        try {
            SQLExe.getDB().execSQL(sql, new Object[] { promotion.getId() });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void deleteAllPromotion() {
        String sql = "delete from " + TableNames.Promotion;
        try {
            SQLExe.getDB().execSQL(sql, new Object[] {});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
