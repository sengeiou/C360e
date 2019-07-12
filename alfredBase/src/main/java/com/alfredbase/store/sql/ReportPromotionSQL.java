package com.alfredbase.store.sql;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.alfredbase.javabean.ReportHourly;
import com.alfredbase.javabean.ReportPromotion;
import com.alfredbase.store.SQLExe;
import com.alfredbase.store.TableNames;
import com.alfredbase.utils.BH;
import com.alfredbase.utils.SQLiteStatementHelper;

import java.util.ArrayList;
import java.util.List;

public class ReportPromotionSQL {
	public static void addReportPromotion(ReportPromotion reportPromotion){
		if(reportPromotion == null){
			return;
		}
		try {
//			private Integer id;
//			private Integer restaurantId;
//			private Integer revenueId;
//			private String revenueName;
//			private Long businessDate;
//
//			private Integer amountQty;
//			private String amountPromotion;
//			private String promotionName;
//			private Integer promotionId;
//			private int daySalesId;
			
			String sql = "replace into " 
					+ TableNames.ReportPromotion
					+ "(id, restaurantId, revenueId, revenueName, businessDate, amountQty,amountPromotion, promotionName,promotionId, daySalesId)"
					+ " values (?,?,?,?,?,?,?,?,?,?)";
			SQLExe.getDB().execSQL(sql, new Object[]{
					reportPromotion.getId(),
					reportPromotion.getRestaurantId(),
					reportPromotion.getRevenueId(),
					reportPromotion.getRevenueName(),
					reportPromotion.getBusinessDate(),
					reportPromotion.getAmountQty(),
					reportPromotion.getAmountPromotion(),
					reportPromotion.getPromotionName(),
					reportPromotion.getPromotionId(),
					reportPromotion.getDaySalesId()
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static void addReportPromotion(List<ReportPromotion> reportPromotions){
		if(reportPromotions == null){
			return;
		}
		SQLiteDatabase db = SQLExe.getDB();
		try {
			db.beginTransaction();
            String sql = "replace into "
                    + TableNames.ReportPromotion
                    + "( restaurantId, revenueId, revenueName, businessDate, amountQty,amountPromotion, promotionName,promotionId, daySalesId)"
                    + " values (?,?,?,?,?,?,?,?,?)";
			SQLiteStatement sqLiteStatement = db.compileStatement(sql);
			for (ReportPromotion reportPromotion: reportPromotions) {
				SQLiteStatementHelper.bindLong(sqLiteStatement, 1,reportPromotion.getRestaurantId());
				SQLiteStatementHelper.bindLong(sqLiteStatement, 2,reportPromotion.getRevenueId());
				SQLiteStatementHelper.bindString(sqLiteStatement, 3,reportPromotion.getRevenueName());
				SQLiteStatementHelper.bindLong(sqLiteStatement, 4,reportPromotion.getBusinessDate());
				SQLiteStatementHelper.bindLong(sqLiteStatement, 5,reportPromotion.getAmountQty());
				SQLiteStatementHelper.bindString(sqLiteStatement, 6,reportPromotion.getAmountPromotion());
				SQLiteStatementHelper.bindString(sqLiteStatement, 7,reportPromotion.getPromotionName());
                SQLiteStatementHelper.bindLong(sqLiteStatement, 8,reportPromotion.getPromotionId());
				SQLiteStatementHelper.bindLong(sqLiteStatement, 9, reportPromotion.getDaySalesId());
				sqLiteStatement.executeInsert();
			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
		}
	}

    public static void addReportPromotion(int daySalesId, SQLiteDatabase db,List<ReportPromotion> reportPromotions){
        if(reportPromotions == null){
            return;
        }

        try {
            db.beginTransaction();
            String sql = "replace into "
                    + TableNames.ReportPromotion
                    + "( restaurantId, revenueId, revenueName, businessDate, amountQty,amountPromotion, promotionName,promotionId, daySalesId)"
                    + " values (?,?,?,?,?,?,?,?,?)";
            SQLiteStatement sqLiteStatement = db.compileStatement(sql);
            for (ReportPromotion reportPromotion: reportPromotions) {
                SQLiteStatementHelper.bindLong(sqLiteStatement, 1,reportPromotion.getRestaurantId());
                SQLiteStatementHelper.bindLong(sqLiteStatement, 2,reportPromotion.getRevenueId());
                SQLiteStatementHelper.bindString(sqLiteStatement, 3,reportPromotion.getRevenueName());
                SQLiteStatementHelper.bindLong(sqLiteStatement, 4,reportPromotion.getBusinessDate());
                SQLiteStatementHelper.bindLong(sqLiteStatement, 5,reportPromotion.getAmountQty());
                SQLiteStatementHelper.bindString(sqLiteStatement, 6,reportPromotion.getAmountPromotion());
                SQLiteStatementHelper.bindString(sqLiteStatement, 7,reportPromotion.getPromotionName());
                SQLiteStatementHelper.bindLong(sqLiteStatement, 8,reportPromotion.getPromotionId());
                SQLiteStatementHelper.bindLong(sqLiteStatement, 9, reportPromotion.getDaySalesId());
                sqLiteStatement.executeInsert();
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

	public static ArrayList<ReportPromotion> getAllReportPromotion(){
		ArrayList<ReportPromotion> reportPromotions = new ArrayList<ReportPromotion>();
		String sql = "select * from " + TableNames.ReportHourly;
		Cursor cursor = null;
		SQLiteDatabase db = SQLExe.getDB();
		try {
			cursor = db.rawQuery(sql, new String[]{});
			int count = cursor.getCount();
			if(count < 1){
				return reportPromotions;
			}
            ReportPromotion reportPromotion = null;
			for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
                reportPromotion = new ReportPromotion();
				reportPromotion.setId(cursor.getInt(0));
				reportPromotion.setRestaurantId(cursor.getInt(1));
				reportPromotion.setRevenueId(cursor.getInt(2));
				reportPromotion.setRevenueName(cursor.getString(3));
				reportPromotion.setBusinessDate(cursor.getLong(4));
				reportPromotion.setAmountQty(cursor.getInt(5));
				reportPromotion.setAmountPromotion(cursor.getString(6));
				reportPromotion.setPromotionName(cursor.getString(7));
				reportPromotion.setPromotionId(cursor.getInt(8));
                reportPromotion.setDaySalesId(cursor.getInt(9));
                reportPromotions.add(reportPromotion);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(cursor != null && !cursor.isClosed()){
				cursor.close();
			}
		}
		return reportPromotions;
	}


	public static ArrayList<ReportPromotion> getReportHourlysByTime(long businessDate){
		ArrayList<ReportPromotion> reportPromotions = new ArrayList<ReportPromotion>();

        String sql = "select id, restaurantId, revenueId, revenueName, businessDate,sum(amountQty), sum(amountPromotion),promotionName,promotionId, daySalesId from "
                + TableNames.ReportPromotion
                + " where businessDate = ? ";
		Cursor cursor = null;
		SQLiteDatabase db = SQLExe.getDB();
		try {
			cursor = db.rawQuery(sql, new String[]{String.valueOf(businessDate)});
			int count = cursor.getCount();
			if(count < 1){
				return reportPromotions;
			}
            ReportPromotion reportPromotion = null;
			for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
                reportPromotion = new ReportPromotion();
                reportPromotion.setId(cursor.getInt(0));
                reportPromotion.setRestaurantId(cursor.getInt(1));
                reportPromotion.setRevenueId(cursor.getInt(2));
                reportPromotion.setRevenueName(cursor.getString(3));
                reportPromotion.setBusinessDate(cursor.getLong(4));
                reportPromotion.setAmountQty(cursor.getInt(5));
                reportPromotion.setAmountPromotion(cursor.getString(6));
                reportPromotion.setPromotionName(cursor.getString(7));
                reportPromotion.setPromotionId(cursor.getInt(8));
                reportPromotion.setDaySalesId(cursor.getInt(9));
                reportPromotions.add(reportPromotion);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(cursor != null && !cursor.isClosed()){
				cursor.close();
			}
		}
		return reportPromotions;
	}
	
	public static void deleteReportHourlyByBusinessDate(long businessDate) {
		String sql = "delete from " + TableNames.ReportPromotion
				+ " where businessDate = ?";
		try {
			SQLExe.getDB().execSQL(sql,
					new Object[] { businessDate + "" });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
