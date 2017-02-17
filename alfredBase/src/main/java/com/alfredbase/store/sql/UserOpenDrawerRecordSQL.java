package com.alfredbase.store.sql;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.alfredbase.javabean.UserOpenDrawerRecord;
import com.alfredbase.javabean.temporaryforapp.ReportUserOpenDrawer;
import com.alfredbase.store.SQLExe;
import com.alfredbase.store.TableNames;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 16/12/28.
 */

public class UserOpenDrawerRecordSQL {
    public static void addUserOpenDrawerRecord(UserOpenDrawerRecord userOpenDrawerRecord) {
        if (userOpenDrawerRecord == null) {
            return;
        }
        try {
            String sql = "replace into "
                    + TableNames.UserOpenDrawerRecord
                    + "(id, restaurantId, revenueCenterId, sessionStatus, userId, userName, openTime, loginUserId)"
                    + " values (?,?,?,?,?,?,?,?)";
            SQLExe.getDB().execSQL(
                    sql,
                    new Object[] {
                            userOpenDrawerRecord.getId(),
                            userOpenDrawerRecord.getRestaurantId(),
                            userOpenDrawerRecord.getRevenueCenterId(),
                            userOpenDrawerRecord.getSessionStatus(),
                            userOpenDrawerRecord.getUserId(),
                            userOpenDrawerRecord.getUserName(),
                            userOpenDrawerRecord.getOpenTime(),
                            userOpenDrawerRecord.getLoginUserId()

                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static List<UserOpenDrawerRecord> getAllUserOpenDrawerRecord(int sessionStatus, long createTime) {
        List<UserOpenDrawerRecord> result = new ArrayList<UserOpenDrawerRecord>();
        String sql = "select * from " + TableNames.UserOpenDrawerRecord + " where sessionStatus = ? and openTime > ?";
        Cursor cursor = null;
        SQLiteDatabase db = SQLExe.getDB();
        try {
            db.beginTransaction();
            cursor = db.rawQuery(sql, new String[] {sessionStatus +"", createTime + ""});
            int count = cursor.getCount();
            if (count < 1) {
                return result;
            }
            UserOpenDrawerRecord userOpenDrawerRecord = null;
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
                    .moveToNext()) {
                userOpenDrawerRecord = new UserOpenDrawerRecord();
                userOpenDrawerRecord.setId(cursor.getInt(0));
                userOpenDrawerRecord.setRestaurantId(cursor.getInt(1));
                userOpenDrawerRecord.setRevenueCenterId(cursor.getInt(2));
                userOpenDrawerRecord.setSessionStatus(cursor.getInt(3));
                userOpenDrawerRecord.setUserId(cursor.getInt(4));
                userOpenDrawerRecord.setUserName(cursor.getString(5));
                userOpenDrawerRecord.setOpenTime(cursor.getLong(6));
                userOpenDrawerRecord.setLoginUserId(cursor.getInt(7));
                result.add(userOpenDrawerRecord);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            db.endTransaction();
        }
        return result;
    }

    public static List<ReportUserOpenDrawer> getReportUserOpenDrawer(int sessionStatus, long createTime) {
        List<ReportUserOpenDrawer> result = new ArrayList<ReportUserOpenDrawer>();
        String sql = "select userName, count(*) from " + TableNames.UserOpenDrawerRecord + " where sessionStatus = ? and openTime > ? group by userId";
        Cursor cursor = null;
        SQLiteDatabase db = SQLExe.getDB();
        try {
            db.beginTransaction();
            cursor = db.rawQuery(sql, new String[] {sessionStatus +"", createTime + ""});
            int count = cursor.getCount();
            if (count < 1) {
                return result;
            }
            ReportUserOpenDrawer reportUserOpenDrawer = null;
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
                    .moveToNext()) {
                reportUserOpenDrawer = new ReportUserOpenDrawer();
                reportUserOpenDrawer.setUserName(cursor.getString(0));
                reportUserOpenDrawer.setTimes(cursor.getInt(1));
                result.add(reportUserOpenDrawer);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            db.endTransaction();
        }
        return result;
    }
    public static List<ReportUserOpenDrawer> getReportUserOpenDrawerByTime(long createTime) {
        List<ReportUserOpenDrawer> result = new ArrayList<ReportUserOpenDrawer>();
        String sql = "select userName, count(*) from " + TableNames.UserOpenDrawerRecord + " where openTime > ? group by userId";
        Cursor cursor = null;
        SQLiteDatabase db = SQLExe.getDB();
        try {
            db.beginTransaction();
            cursor = db.rawQuery(sql, new String[] {createTime + ""});
            int count = cursor.getCount();
            if (count < 1) {
                return result;
            }
            ReportUserOpenDrawer reportUserOpenDrawer = null;
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
                    .moveToNext()) {
                reportUserOpenDrawer = new ReportUserOpenDrawer();
                reportUserOpenDrawer.setUserName(cursor.getString(0));
                reportUserOpenDrawer.setTimes(cursor.getInt(1));
                result.add(reportUserOpenDrawer);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            db.endTransaction();
        }
        return result;
    }

    public static void deleteUserOpenDrawerRecord(UserOpenDrawerRecord userOpenDrawerRecord) {
        String sql = "delete from " + TableNames.UserOpenDrawerRecord + " where id = ?";
        try {
            SQLExe.getDB().execSQL(sql, new Object[] { userOpenDrawerRecord.getId() });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteAllUserOpenDrawerRecord() {
        String sql = "delete from " + TableNames.UserOpenDrawerRecord;
        try {
            SQLExe.getDB().execSQL(sql, new Object[] {});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}