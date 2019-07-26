package com.alfredbase.store.sql;

import android.database.Cursor;

import com.alfredbase.store.SQLExe;
import com.alfredbase.store.TableNames;

public class CommonSQL {

    public static int getNextSeq(String table) {
        synchronized (table) {
            int seq = 0;
            String sql = "select seq from " + TableNames.SQLITE_SEQUENCE
                    + " where name = ?";
            Cursor cursor = null;
            try {
                cursor = SQLExe.getDB().rawQuery(sql, new String[]{table});
                if (cursor.moveToFirst()) {
                    seq = cursor.getInt(0);
                }
            } catch (Exception e) {
                e.printStackTrace();

            } finally {
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
            }
            return seq + 1;
        }
    }

    public static int getfakeId(int originId, int count) {
        String fakeId = "-" + originId + "" + count;
        return Integer.parseInt(fakeId);
    }

    public static boolean isFakeId(int id) {
        return id < 0;
    }
}
