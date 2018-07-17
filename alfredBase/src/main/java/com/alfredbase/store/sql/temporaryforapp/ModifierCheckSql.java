package com.alfredbase.store.sql.temporaryforapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.alfredbase.ParamConst;
import com.alfredbase.javabean.ModifierCheck;
import com.alfredbase.javabean.Order;
import com.alfredbase.javabean.Payment;
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
                    + "(id, orderDetailId, orderId, modifierCategoryId, itemName, modifierCategoryName, num)"
                    + " values (?,?,?,?,?,?,?)";
            SQLExe.getDB().execSQL(
                    sql,
                    new Object[] { modifierCheck.getId(),
                            modifierCheck.getOrderDetailId(),
                            modifierCheck.getOrderId(),
                            modifierCheck.getModifierCategoryId(),
                            modifierCheck.getItemName(),
                            modifierCheck.getModifierCategoryName(),
                            modifierCheck.getNum()});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void update(int number,int modifierCategoryId) {

        try {
            String sql = "update " + TableNames.ModifierCheck + " set num = ?  where modifierCategoryId = ? " ;
            SQLExe.getDB().execSQL(
                    sql,
                    new Object[] {number,modifierCategoryId });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<ModifierCheck> getAllModifierCheck() {
        ArrayList<ModifierCheck> result = new ArrayList<ModifierCheck>();
        String sql = "select * from " + TableNames.ModifierCheck;
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

}
