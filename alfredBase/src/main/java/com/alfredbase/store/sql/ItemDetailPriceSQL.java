package com.alfredbase.store.sql;

import android.database.Cursor;

import com.alfredbase.javabean.ItemDetailPrice;
import com.alfredbase.store.SQLExe;
import com.alfredbase.store.TableNames;

/**
 * Created by Arif S. on 2019-09-20
 */
public class ItemDetailPriceSQL {
    public static void update(ItemDetailPrice itemDetailPrice) {
        if (itemDetailPrice == null) return;

        try {
            String sql = "replace into "
                    + TableNames.ItemDetailPrice
                    + "(id, itemId, salesTypeId, taxId, paraType, itemPrice, createTime, updateTime)"
                    + " values (?,?,?,?,?,?,?,?)";
            SQLExe.getDB().execSQL(
                    sql,
                    new Object[]{itemDetailPrice.getId(),
                            itemDetailPrice.getId(),
                            itemDetailPrice.getItemId(),
                            itemDetailPrice.getSalesTypeId(),
                            itemDetailPrice.getTaxId(),
                            itemDetailPrice.getParaType(),
                            itemDetailPrice.getItemPrice(),
                            itemDetailPrice.getCreateTime(),
                            itemDetailPrice.getUpdateTime()
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ItemDetailPrice get(int itemId, int salesTypeId) {

        ItemDetailPrice itemDetailPrice = null;
        String query = " where itemId = ? and salesTypeId = ?";
        String sql = "select * from " + TableNames.ItemDetailPrice + query;
        Cursor cursor = null;
        try {
            cursor = SQLExe.getDB().rawQuery(sql, new String[]{String.valueOf(itemId), String.valueOf(salesTypeId)});

            if (cursor.moveToFirst()) {
                itemDetailPrice = new ItemDetailPrice();
                itemDetailPrice.setId(cursor.getInt(0));
                itemDetailPrice.setItemId(cursor.getInt(1));
                itemDetailPrice.setSalesTypeId(cursor.getInt(2));
                itemDetailPrice.setTaxId(cursor.getInt(3));
                itemDetailPrice.setParaType(cursor.getInt(4));
                itemDetailPrice.setItemPrice(cursor.getDouble(5));
                itemDetailPrice.setCreateTime(cursor.getLong(6));
                itemDetailPrice.setUpdateTime(cursor.getLong(7));
            }
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return itemDetailPrice;
    }

    public static void delete() {
        String sql = "delete from " + TableNames.ItemDetailPrice;
        try {
            SQLExe.getDB().execSQL(sql, new Object[]{});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
