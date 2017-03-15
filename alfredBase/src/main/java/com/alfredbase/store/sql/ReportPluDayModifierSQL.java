package com.alfredbase.store.sql;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.alfredbase.javabean.ReportPluDayModifier;
import com.alfredbase.store.SQLExe;
import com.alfredbase.store.TableNames;

import java.util.ArrayList;

public class ReportPluDayModifierSQL {
	public static void addReportPluDayModifier(
			ReportPluDayModifier reportPluDayModifier) {
		if (reportPluDayModifier == null)
			return;
		try {
			String sql = "replace into "
					+ TableNames.ReportPluDayModifier
					+ "(id, reportNo, restaurantId, restaurantName, revenueId, revenueName, businessDate, modifierCategoryId, "
					+ "modifierCategoryName, modifierId, modifierName, modifierPrice, modifierCount, billVoidPrice, billVoidCount, " 
					+ "voidModifierPrice, voidModifierCount, bohModifierPrice, bohModifierCount, focModifierPrice, focModifierCount, "
					+ "billFocPrice, billFocCount, comboItemId, modifierItemPrice, realPrice, realCount)"
					+ " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			SQLExe.getDB().execSQL(
					sql,
					new Object[] { reportPluDayModifier.getId(),
							reportPluDayModifier.getReportNo(),
							reportPluDayModifier.getRestaurantId(),
							reportPluDayModifier.getRestaurantName(),
							reportPluDayModifier.getRevenueId(),
							reportPluDayModifier.getRevenueName(),
							reportPluDayModifier.getBusinessDate(),
							reportPluDayModifier.getModifierCategoryId(),
							reportPluDayModifier.getModifierCategoryName(),
							reportPluDayModifier.getModifierId(),
							reportPluDayModifier.getModifierName(),
							reportPluDayModifier.getModifierPrice() == null ? "0.00" : reportPluDayModifier.getModifierPrice(),
							reportPluDayModifier.getModifierCount() == null ? 0 : reportPluDayModifier.getModifierCount(),
							reportPluDayModifier.getBillVoidPrice() == null ? "0.00" : reportPluDayModifier.getBillVoidPrice(),
							reportPluDayModifier.getBillVoidCount() == null ? 0 : reportPluDayModifier.getBillVoidCount(),
							reportPluDayModifier.getVoidModifierPrice() == null ? "0.00" : reportPluDayModifier.getVoidModifierPrice(),
							reportPluDayModifier.getVoidModifierCount() == null ? 0 : reportPluDayModifier.getVoidModifierCount(),
							reportPluDayModifier.getBohModifierPrice() == null ? "0.00" : reportPluDayModifier.getBillVoidPrice(),
							reportPluDayModifier.getBohModifierCount() == null ? 0 : reportPluDayModifier.getBohModifierCount(),
							reportPluDayModifier.getFocModifierPrice() == null ? "0.00" : reportPluDayModifier.getFocModifierPrice(),
							reportPluDayModifier.getFocModifierCount() == null ? 0 : reportPluDayModifier.getFocModifierCount(),
							reportPluDayModifier.getBillFocPrice() == null ? "0.00" : reportPluDayModifier.getFocModifierCount(),
							reportPluDayModifier.getBillFocCount() == null ? 0 : reportPluDayModifier.getFocModifierCount(),
							reportPluDayModifier.getComboItemId(),
							reportPluDayModifier.getModifierItemPrice() == null ? "0.00" : reportPluDayModifier.getModifierItemPrice(),
							reportPluDayModifier.getRealPrice() == null ? "0.00" : reportPluDayModifier.getRealPrice(),
							reportPluDayModifier.getRealCount() == null ? 0 : reportPluDayModifier.getRealCount()
							});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static ReportPluDayModifier getReportPluDayModifier(
			Integer reportPluDayModifierID) {
		ReportPluDayModifier reportPluDayModifier = null;
		String sql = "select * from " + TableNames.ReportPluDayModifier
				+ " where id = ?";
		Cursor cursor = null;
		try {
			cursor = SQLExe.getDB().rawQuery(sql,
					new String[] { reportPluDayModifierID + "" });
			if (cursor.moveToFirst()) {
				reportPluDayModifier = new ReportPluDayModifier();
				reportPluDayModifier.setId(cursor.getInt(0));
				reportPluDayModifier.setReportNo(cursor.getInt(1));
				reportPluDayModifier.setRestaurantId(cursor.getInt(2));
				reportPluDayModifier.setRestaurantName(cursor.getString(3));
				reportPluDayModifier.setRevenueId(cursor.getInt(4));
				reportPluDayModifier.setRevenueName(cursor.getString(5));
				reportPluDayModifier.setBusinessDate(cursor.getLong(6));
				reportPluDayModifier.setModifierCategoryId(cursor.getInt(7));
				reportPluDayModifier.setModifierCategoryName(cursor
						.getString(8));
				reportPluDayModifier.setModifierId(cursor.getInt(9));
				reportPluDayModifier.setModifierName(cursor.getString(10));
				reportPluDayModifier.setModifierPrice(cursor.getString(11));
				reportPluDayModifier.setModifierCount(cursor.getInt(12));
				reportPluDayModifier.setBillVoidPrice(cursor.getString(13));
				reportPluDayModifier.setBillVoidCount(cursor.getInt(14)); 
				reportPluDayModifier.setVoidModifierPrice(cursor.getString(15));
				reportPluDayModifier.setVoidModifierCount(cursor.getInt(16));
				reportPluDayModifier.setBohModifierPrice(cursor.getString(17));
				reportPluDayModifier.setBohModifierCount(cursor.getInt(18));
				reportPluDayModifier.setFocModifierPrice(cursor.getString(19));
				reportPluDayModifier.setFocModifierCount(cursor.getInt(20));
				reportPluDayModifier.setBillFocPrice(cursor.getString(21));
				reportPluDayModifier.setBillFocCount(cursor.getInt(22));
				reportPluDayModifier.setComboItemId(cursor.getInt(23));
				reportPluDayModifier.setModifierItemPrice(cursor.getString(24));
				reportPluDayModifier.setRealPrice(cursor.getString(25));
				reportPluDayModifier.setRealCount(cursor.getInt(26));
				return reportPluDayModifier;
			}
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return reportPluDayModifier;
	}

	public static ArrayList<ReportPluDayModifier> getAllReportPluDayModifier() {
		ArrayList<ReportPluDayModifier> result = new ArrayList<ReportPluDayModifier>();
		String sql = "select * from " + TableNames.ReportPluDayModifier;
		Cursor cursor = null;
		SQLiteDatabase db = SQLExe.getDB();
		try {
			db.beginTransaction();
			cursor = db.rawQuery(sql, new String[] {});
			int count = cursor.getCount();
			if (count < 1) {
				return result;
			}
			ReportPluDayModifier reportPluDayModifier = null;
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
					.moveToNext()) {
				reportPluDayModifier = new ReportPluDayModifier();
				reportPluDayModifier.setId(cursor.getInt(0));
				reportPluDayModifier.setReportNo(cursor.getInt(1));
				reportPluDayModifier.setRestaurantId(cursor.getInt(2));
				reportPluDayModifier.setRestaurantName(cursor.getString(3));
				reportPluDayModifier.setRevenueId(cursor.getInt(4));
				reportPluDayModifier.setRevenueName(cursor.getString(5));
				reportPluDayModifier.setBusinessDate(cursor.getLong(6));
				reportPluDayModifier.setModifierCategoryId(cursor.getInt(7));
				reportPluDayModifier.setModifierCategoryName(cursor
						.getString(8));
				reportPluDayModifier.setModifierId(cursor.getInt(9));
				reportPluDayModifier.setModifierName(cursor.getString(10));
				reportPluDayModifier.setModifierPrice(cursor.getString(11));
				reportPluDayModifier.setModifierCount(cursor.getInt(12));
				reportPluDayModifier.setBillVoidPrice(cursor.getString(13));
				reportPluDayModifier.setBillVoidCount(cursor.getInt(14)); 
				reportPluDayModifier.setVoidModifierPrice(cursor.getString(15));
				reportPluDayModifier.setVoidModifierCount(cursor.getInt(16));
				reportPluDayModifier.setBohModifierPrice(cursor.getString(17));
				reportPluDayModifier.setBohModifierCount(cursor.getInt(18));
				reportPluDayModifier.setFocModifierPrice(cursor.getString(19));
				reportPluDayModifier.setFocModifierCount(cursor.getInt(20));
				reportPluDayModifier.setBillFocPrice(cursor.getString(21));
				reportPluDayModifier.setBillFocCount(cursor.getInt(22));
				reportPluDayModifier.setComboItemId(cursor.getInt(23));
				reportPluDayModifier.setModifierItemPrice(cursor.getString(24));
				reportPluDayModifier.setRealPrice(cursor.getString(25));
				reportPluDayModifier.setRealCount(cursor.getInt(26));
				result.add(reportPluDayModifier);
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

	public static ArrayList<ReportPluDayModifier> getReportPluDayModifiersByTime(long date) {
		ArrayList<ReportPluDayModifier> result = new ArrayList<ReportPluDayModifier>();
		String sql = "select * from "
				+ TableNames.ReportPluDayModifier
				+ " where businessDate = ?";
		Cursor cursor = null;
		SQLiteDatabase db = SQLExe.getDB();
		try {
			db.beginTransaction();
			cursor = db.rawQuery(sql, new String[] {String.valueOf(date)});
			int count = cursor.getCount();
			if (count < 1) {
				return result;
			}
			ReportPluDayModifier reportPluDayModifier = null;
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
					.moveToNext()) {
				reportPluDayModifier = new ReportPluDayModifier();
				reportPluDayModifier.setId(cursor.getInt(0));
				reportPluDayModifier.setReportNo(cursor.getInt(1));
				reportPluDayModifier.setRestaurantId(cursor.getInt(2));
				reportPluDayModifier.setRestaurantName(cursor.getString(3));
				reportPluDayModifier.setRevenueId(cursor.getInt(4));
				reportPluDayModifier.setRevenueName(cursor.getString(5));
				reportPluDayModifier.setBusinessDate(cursor.getLong(6));
				reportPluDayModifier.setModifierCategoryId(cursor.getInt(7));
				reportPluDayModifier.setModifierCategoryName(cursor
						.getString(8));
				reportPluDayModifier.setModifierId(cursor.getInt(9));
				reportPluDayModifier.setModifierName(cursor.getString(10));
				reportPluDayModifier.setModifierPrice(cursor.getString(11));
				reportPluDayModifier.setModifierCount(cursor.getInt(12));
				reportPluDayModifier.setBillVoidPrice(cursor.getString(13));
				reportPluDayModifier.setBillVoidCount(cursor.getInt(14)); 
				reportPluDayModifier.setVoidModifierPrice(cursor.getString(15));
				reportPluDayModifier.setVoidModifierCount(cursor.getInt(16));
				reportPluDayModifier.setBohModifierPrice(cursor.getString(17));
				reportPluDayModifier.setBohModifierCount(cursor.getInt(18));
				reportPluDayModifier.setFocModifierPrice(cursor.getString(19));
				reportPluDayModifier.setFocModifierCount(cursor.getInt(20));
				reportPluDayModifier.setBillFocPrice(cursor.getString(21));
				reportPluDayModifier.setBillFocCount(cursor.getInt(22));
				reportPluDayModifier.setComboItemId(cursor.getInt(23));
				reportPluDayModifier.setModifierItemPrice(cursor.getString(24));
				reportPluDayModifier.setRealPrice(cursor.getString(25));
				reportPluDayModifier.setRealCount(cursor.getInt(26));
				result.add(reportPluDayModifier);
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

	public static void deleteReportPluDayModifier(
			ReportPluDayModifier reportPluDayModifier) {
		String sql = "delete from " + TableNames.ReportPluDayModifier
				+ " where id = ?";
		try {
			SQLExe.getDB().execSQL(sql,
					new Object[] { reportPluDayModifier.getId() });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void deleteReportPluDayModifierByBusinessDate(long businessDate) {
		String sql = "delete from " + TableNames.ReportPluDayModifier
				+ " where businessDate = ?";
		try {
			SQLExe.getDB().execSQL(sql,
					new Object[] { businessDate + "" });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
