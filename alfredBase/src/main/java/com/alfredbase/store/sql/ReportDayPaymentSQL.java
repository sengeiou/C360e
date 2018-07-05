package com.alfredbase.store.sql;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.alfredbase.javabean.ReportDayPayment;
import com.alfredbase.store.SQLExe;
import com.alfredbase.store.TableNames;
import com.alfredbase.utils.SQLiteStatementHelper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReportDayPaymentSQL {



    public static void addReportDayPayment(List<ReportDayPayment> reportDayPayments) {
        if (reportDayPayments == null) {
            return;
        }


        SQLiteDatabase db = SQLExe.getDB();
        try {
            db.beginTransaction();

            String sql = "replace into "
                    + TableNames.ReportDayPayment
                    + "(id,daySalesId,restaurantId,restaurantName,revenueId,revenueName,businessDate,paymentTypeId,paymentName,paymentQty,paymentAmount,createTime,systemCreateTime)"
                    + " values (?,?,?,?,?,?,?,?,?,?,?,?,?)";
            SQLiteStatement sqLiteStatement = db.compileStatement(
                    sql);
            for (ReportDayPayment reportDayPayment : reportDayPayments) {

                SQLiteStatementHelper.bindLong(sqLiteStatement, 1,
                        reportDayPayment.getId());
                SQLiteStatementHelper.bindLong(sqLiteStatement, 2,
                        reportDayPayment.getDaySalesId());
                SQLiteStatementHelper.bindLong(sqLiteStatement, 3,
                        reportDayPayment.getRestaurantId());
                SQLiteStatementHelper.bindString(sqLiteStatement, 4,
                        reportDayPayment.getRestaurantName());
                SQLiteStatementHelper.bindLong(sqLiteStatement, 5,
                        reportDayPayment.getRevenueId());
                SQLiteStatementHelper.bindString(sqLiteStatement, 6,
                        reportDayPayment.getRevenueName());
                SQLiteStatementHelper.bindLong(sqLiteStatement, 7,
                        reportDayPayment.getBusinessDate());
                SQLiteStatementHelper.bindLong(sqLiteStatement, 8,
                        reportDayPayment.getPaymentTypeId());
                SQLiteStatementHelper.bindString(sqLiteStatement, 9,
                        reportDayPayment.getPaymentName());
                SQLiteStatementHelper.bindLong(sqLiteStatement, 10,
                        reportDayPayment.getPaymentQty());
                SQLiteStatementHelper.bindString(sqLiteStatement, 11,
                        reportDayPayment.getPaymentAmount());
                SQLiteStatementHelper.bindLong(sqLiteStatement, 12,
                        reportDayPayment.getCreateTime());

                SQLiteStatementHelper.bindLong(sqLiteStatement, 13,
                        reportDayPayment.getSystemCreateTime());



                sqLiteStatement.executeInsert();
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }
    public static ArrayList<ReportDayPayment> getAllReportDayPayment() {
        ArrayList<ReportDayPayment> result = new ArrayList<ReportDayPayment>();
        String sql = "select * from " + TableNames.ReportDayPayment;
        Cursor cursor = null;
        SQLiteDatabase db = SQLExe.getDB();
        try {
            cursor = db.rawQuery(sql, new String[] {});
            int count = cursor.getCount();
            if (count < 1) {
                return result;
            }
            ReportDayPayment reportDayPayment = null;
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
                    .moveToNext()) {
                reportDayPayment = new ReportDayPayment();
                reportDayPayment.setId(cursor.getInt(0));

                reportDayPayment.setDaySalesId(cursor.getInt(1));
                reportDayPayment.setRestaurantId(cursor.getInt(2));
                reportDayPayment.setRestaurantName(cursor.getString(3));
                reportDayPayment.setRevenueId(cursor.getInt(4));
                reportDayPayment.setRevenueName(cursor.getString(5));

                reportDayPayment.setBusinessDate(cursor.getLong(6));
                reportDayPayment.setPaymentTypeId(cursor.getInt(7));
                reportDayPayment.setPaymentName(cursor.getString(8));
                reportDayPayment.setPaymentQty(cursor.getInt(9));
                reportDayPayment.setPaymentAmount(cursor.getString(10));
                reportDayPayment.setCreateTime(cursor.getLong(11));
                reportDayPayment.setSystemCreateTime(cursor.getLong(12));
                result.add(reportDayPayment);
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




    public static void deleteReportDayPayment(ReportDayPayment reportDayPayment) {
        String sql = "delete from " + TableNames.ReportDayPayment + " where id = ?";
        try {
            SQLExe.getDB().execSQL(sql, new Object[] { reportDayPayment.getId() });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteAllReportDayPayment() {
        String sql = "delete from " + TableNames.ReportDayPayment;
        try {
            SQLExe.getDB().execSQL(sql, new Object[] {});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
