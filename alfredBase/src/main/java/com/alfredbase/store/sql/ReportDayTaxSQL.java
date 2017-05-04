package com.alfredbase.store.sql;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.alfredbase.javabean.ReportDayTax;
import com.alfredbase.store.SQLExe;
import com.alfredbase.store.TableNames;
import com.alfredbase.utils.SQLiteStatementHelper;

import java.util.ArrayList;
import java.util.List;

public class ReportDayTaxSQL {
	public static void addReportDayTax(ReportDayTax reportDayTax) {
		if (reportDayTax == null)
			return;
		try {
			String sql = "replace into "
					+ TableNames.ReportDayTax
					+ "(id, daySalesId, restaurantId, restaurantName, revenueId, revenueName, businessDate, taxId, taxName, taxPercentage, taxQty, taxAmount)"
					+ " values (?,?,?,?,?,?,?,?,?,?,?,?)";
			SQLExe.getDB().execSQL(
					sql,
					new Object[] { reportDayTax.getId(),
							reportDayTax.getDaySalesId(),
							reportDayTax.getRestaurantId(),
							reportDayTax.getRestaurantName(),
							reportDayTax.getRevenueId(),
							reportDayTax.getRevenueName(),
							reportDayTax.getBusinessDate(),
							reportDayTax.getTaxId(), 
							reportDayTax.getTaxName(),
							reportDayTax.getTaxPercentage() == null ? "0.00" : reportDayTax.getTaxPercentage(),
							reportDayTax.getTaxQty() == null ? 0 : reportDayTax.getTaxQty(),
							reportDayTax.getTaxAmount() == null ? "0.00" : reportDayTax.getTaxAmount()
							});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//save Report data from cloud only
	public static void saveReportDayTaxListFromCloud(List<ReportDayTax> reportDayTaxList) {
		if (reportDayTaxList == null)
			return;
		
		SQLiteDatabase db = SQLExe.getDB();
		try {
			db.beginTransaction();
			String sql = "replace into "
					+ TableNames.ReportDayTax
					+ "(daySalesId, restaurantId, restaurantName, revenueId, revenueName," 
					+ " businessDate, taxId, taxName, taxPercentage, taxQty, taxAmount)"
					+ " values (?,?,?,?,?,?,?,?,?,?,?)";
			SQLiteStatement sqLiteStatement = db.compileStatement(sql);
			for(ReportDayTax reportDayTax: reportDayTaxList) {
				SQLiteStatementHelper.bindLong(sqLiteStatement, 1, reportDayTax.getDaySalesId());
				SQLiteStatementHelper.bindLong(sqLiteStatement, 2, reportDayTax.getRestaurantId());
				SQLiteStatementHelper.bindString(sqLiteStatement, 3, reportDayTax.getRestaurantName());
				SQLiteStatementHelper.bindLong(sqLiteStatement, 4, reportDayTax.getRevenueId());
				SQLiteStatementHelper.bindString(sqLiteStatement, 5, reportDayTax.getRevenueName());
				SQLiteStatementHelper.bindLong(sqLiteStatement, 6, reportDayTax.getBusinessDate());
				SQLiteStatementHelper.bindLong(sqLiteStatement, 7, reportDayTax.getTaxId());
				SQLiteStatementHelper.bindString(sqLiteStatement, 8, reportDayTax.getTaxName());
				SQLiteStatementHelper.bindString(sqLiteStatement, 9, reportDayTax.getTaxPercentage());
				SQLiteStatementHelper.bindLong(sqLiteStatement, 10, reportDayTax.getTaxQty());
				SQLiteStatementHelper.bindString(sqLiteStatement, 11, reportDayTax.getTaxAmount());
				sqLiteStatement.executeInsert();
			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
		}
	}
	public static ReportDayTax getReportDayTax(Integer reportDayTaxID) {
		ReportDayTax reportDayTax = null;
		String sql = "select * from " + TableNames.ReportDayTax
				+ " where id = ?";
		Cursor cursor = null;
		try {
			cursor = SQLExe.getDB().rawQuery(sql,
					new String[] { reportDayTaxID + "" });
			if (cursor.moveToFirst()) {
				reportDayTax = new ReportDayTax();
				reportDayTax.setId(cursor.getInt(0));
				reportDayTax.setDaySalesId(cursor.getInt(1));
				reportDayTax.setRestaurantId(cursor.getInt(2));
				reportDayTax.setRestaurantName(cursor.getString(3));
				reportDayTax.setRevenueId(cursor.getInt(4));
				reportDayTax.setRevenueName(cursor.getString(5));
				reportDayTax.setBusinessDate(cursor.getLong(6));
				reportDayTax.setTaxId(cursor.getInt(7));
				reportDayTax.setTaxName(cursor.getString(8));
				reportDayTax.setTaxPercentage(cursor.getString(9));
				reportDayTax.setTaxQty(cursor.getInt(10));
				reportDayTax.setTaxAmount(cursor.getString(11));
				return reportDayTax;
			}
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return reportDayTax;
	}

	public static ArrayList<ReportDayTax> getAllReportDayTax() {
		ArrayList<ReportDayTax> result = new ArrayList<ReportDayTax>();
		String sql = "select * from " + TableNames.ReportDayTax;
		Cursor cursor = null;
		SQLiteDatabase db = SQLExe.getDB();
		try {
			cursor = db.rawQuery(sql, new String[] {});
			int count = cursor.getCount();
			if (count < 1) {
				return result;
			}
			ReportDayTax reportDayTax = null;
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
					.moveToNext()) {
				reportDayTax = new ReportDayTax();
				reportDayTax.setId(cursor.getInt(0));
				reportDayTax.setDaySalesId(cursor.getInt(1));
				reportDayTax.setRestaurantId(cursor.getInt(2));
				reportDayTax.setRestaurantName(cursor.getString(3));
				reportDayTax.setRevenueId(cursor.getInt(4));
				reportDayTax.setRevenueName(cursor.getString(5));
				reportDayTax.setBusinessDate(cursor.getLong(6));
				reportDayTax.setTaxId(cursor.getInt(7));
				reportDayTax.setTaxName(cursor.getString(8));
				reportDayTax.setTaxPercentage(cursor.getString(9));
				reportDayTax.setTaxQty(cursor.getInt(10));
				reportDayTax.setTaxAmount(cursor.getString(11));
				result.add(reportDayTax);
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
	
	public static ArrayList<ReportDayTax> getAllReportDayTaxByDaySalesId(int daySalesId) {
		ArrayList<ReportDayTax> result = new ArrayList<ReportDayTax>();
		String sql = "select * from " + TableNames.ReportDayTax + " where daySalesId = ?";
		Cursor cursor = null;
		SQLiteDatabase db = SQLExe.getDB();
		try {
			cursor = db.rawQuery(sql, new String[] {String.valueOf(daySalesId)});
			int count = cursor.getCount();
			if (count < 1) {
				return result;
			}
			ReportDayTax reportDayTax = null;
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
					.moveToNext()) {
				reportDayTax = new ReportDayTax();
				reportDayTax.setId(cursor.getInt(0));
				reportDayTax.setDaySalesId(cursor.getInt(1));
				reportDayTax.setRestaurantId(cursor.getInt(2));
				reportDayTax.setRestaurantName(cursor.getString(3));
				reportDayTax.setRevenueId(cursor.getInt(4));
				reportDayTax.setRevenueName(cursor.getString(5));
				reportDayTax.setBusinessDate(cursor.getLong(6));
				reportDayTax.setTaxId(cursor.getInt(7));
				reportDayTax.setTaxName(cursor.getString(8));
				reportDayTax.setTaxPercentage(cursor.getString(9));
				reportDayTax.setTaxQty(cursor.getInt(10));
				reportDayTax.setTaxAmount(cursor.getString(11));
				result.add(reportDayTax);
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

	public static ArrayList<ReportDayTax> getReportDayTaxsByNowTime(long day) {
		ArrayList<ReportDayTax> reportDayTaxs = new ArrayList<ReportDayTax>();
		
		String sql = "select * from "
				+ TableNames.ReportDayTax
				+ " where businessDate = ?";
		Cursor cursor = null;
		SQLiteDatabase db = SQLExe.getDB();
		try {
			ReportDayTax reportDayTax = null;
			cursor = db.rawQuery(sql, new String[] { String.valueOf(day) });
			int count = cursor.getCount();
			if(count < 1){
				return reportDayTaxs;
			}
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
					.moveToNext()) {
				reportDayTax = new ReportDayTax();
				reportDayTax.setId(cursor.getInt(0));
				reportDayTax.setDaySalesId(cursor.getInt(1));
				reportDayTax.setRestaurantId(cursor.getInt(2));
				reportDayTax.setRestaurantName(cursor.getString(3));
				reportDayTax.setRevenueId(cursor.getInt(4));
				reportDayTax.setRevenueName(cursor.getString(5));
				reportDayTax.setBusinessDate(cursor.getLong(6));
				reportDayTax.setTaxId(cursor.getInt(7));
				reportDayTax.setTaxName(cursor.getString(8));
				reportDayTax.setTaxPercentage(cursor.getString(9));
				reportDayTax.setTaxQty(cursor.getInt(10));
				reportDayTax.setTaxAmount(cursor.getString(11));
				reportDayTaxs.add(reportDayTax);
			}
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return reportDayTaxs;
	}

	public static void deleteReportDayTax(ReportDayTax reportDayTax) {
		String sql = "delete from " + TableNames.ReportDayTax + " where id = ?";
		try {
			SQLExe.getDB().execSQL(sql, new Object[] { reportDayTax.getId() });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void deleteReportDayTaxByBusinessDate(long businessDate) {
		String sql = "delete from " + TableNames.ReportDayTax + " where businessDate = ?";
		try {
			SQLExe.getDB().execSQL(sql, new Object[] { businessDate + "" });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
