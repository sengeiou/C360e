package com.alfredbase.store.sql.temporaryforapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.alfredbase.ParamConst;
import com.alfredbase.javabean.ModifierCheck;
import com.alfredbase.javabean.Order;
import com.alfredbase.javabean.Payment;
import com.alfredbase.javabean.PaymentMethod;
import com.alfredbase.javabean.PaymentSettlement;
import com.alfredbase.store.SQLExe;
import com.alfredbase.store.TableNames;

import java.util.ArrayList;

public class ModifierCheckSql {

    public static void addModifierCheck(ModifierCheck modifierCheck) {
        if (modifierCheck == null)
            return;
        try {

            String sql = "replace into "
                    + TableNames.ModifierCheck
                    + "(id, orderDetailId, orderId, modifierCategoryId, itemName, modifierCategoryName, num,minNum)"
                    + " values (?,?,?,?,?,?,?,?)";
            SQLExe.getDB().execSQL(
                    sql,
                    new Object[] { modifierCheck.getId(),
                            modifierCheck.getOrderDetailId(),
                            modifierCheck.getOrderId(),
                            modifierCheck.getModifierCategoryId(),
                            modifierCheck.getItemName(),
                            modifierCheck.getModifierCategoryName(),
                            modifierCheck.getNum(),
                            modifierCheck.getMinNum()
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void update(int number,int modifierCategoryId,int detailId) {

        try {
            String sql = "update " + TableNames.ModifierCheck + " set num = ?  where modifierCategoryId = ? and orderDetailId = ? " ;
            SQLExe.getDB().execSQL(
                    sql,
                    new Object[] {number,modifierCategoryId,detailId});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<ModifierCheck> getAllModifierCheck() {
        ArrayList<ModifierCheck> result = new ArrayList<ModifierCheck>();
        String sql = "select * from " + TableNames.ModifierCheck + " order by itemName ";
        Cursor cursor = null;
        SQLiteDatabase db = SQLExe.getDB();
        try {
            cursor = db.rawQuery(sql, new String[] {});
            int count = cursor.getCount();
            if (count < 1) {
                return result;
            }
     
            ModifierCheck modifierCheck = null;
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
                    .moveToNext()) {
                modifierCheck = new ModifierCheck();
                modifierCheck.setId(cursor.getInt(0));
                modifierCheck.setOrderDetailId(cursor.getInt(1));
                modifierCheck.setOrderId(cursor.getInt(2));
                modifierCheck.setModifierCategoryId(cursor.getInt(3));
                modifierCheck.setItemName(cursor.getString(4));
                modifierCheck.setModifierCategoryName(cursor.getString(5));
                modifierCheck.setNum(cursor.getInt(6));
                modifierCheck.setMinNum(cursor.getInt(7));
                result.add(modifierCheck);
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


    public static void deleteModifierCheck(int orderDetailId) {
        String sql = "delete from " + TableNames.ModifierCheck + " where orderDetailId = ?";
        try {
            SQLExe.getDB().execSQL(sql, new Object[] { orderDetailId });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static void deleteAllModifierCheck() {
        String sql = "delete from " + TableNames.ModifierCheck;
        try {
            SQLExe.getDB().execSQL(sql, new Object[] {});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
