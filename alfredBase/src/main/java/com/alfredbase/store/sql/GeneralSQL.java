package com.alfredbase.store.sql;

import java.util.ArrayList;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.alfredbase.ParamConst;
import com.alfredbase.javabean.javabeanforhtml.DashboardItemDetail;
import com.alfredbase.javabean.javabeanforhtml.DashboardSales;
import com.alfredbase.store.SQLExe;
import com.alfredbase.store.TableNames;

/**
 * 本类用于综合查询，多变查询等
 * 
 * @author Alex
 * 
 */
public class GeneralSQL {
	public static ArrayList<DashboardItemDetail> getDashboardItemDetailToCategory(
			long businessDate) {
		ArrayList<DashboardItemDetail> dashboardItemDetails = new ArrayList<DashboardItemDetail>();
		Cursor cursor = null;
		SQLiteDatabase db = SQLExe.getDB();
		try {
			db.beginTransaction();
			cursor = db
					.query(TableNames.ItemCategory + " as a,"
							+ TableNames.ItemDetail + " as b,"
							+ TableNames.OrderDetail + " as c",
							new String[] { " c.itemId,SUM(c.itemNum) as Num, a.itemCategoryName" },
							" a.id = b.itemCategoryId AND b.id=c.itemId AND c.orderId in ( select id from "
									+ TableNames.Order
									+ " where businessDate = ? )",
							new String[] { String.valueOf(businessDate) },
							"a.id", "", "Num desc", "0, 5");
			int count = cursor.getCount();
			if (count < 1) {
				return dashboardItemDetails;
			}
			DashboardItemDetail dashboardItemDetail = null;
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
					.moveToNext()) {
				dashboardItemDetail = new DashboardItemDetail();
				dashboardItemDetail.setItemId(cursor.getInt(0));
				dashboardItemDetail.setQty(cursor.getInt(1));
				dashboardItemDetail.setName(cursor.getString(2));
				dashboardItemDetails.add(dashboardItemDetail);
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
		return dashboardItemDetails;
	}

	public static ArrayList<DashboardItemDetail> getDashboardItemDetailToItem(
			long businessDate) {
		ArrayList<DashboardItemDetail> dashboardItemDetails = new ArrayList<DashboardItemDetail>();
		Cursor cursor = null;
		SQLiteDatabase db = SQLExe.getDB();
		try {
			db.beginTransaction();
			cursor = db
					.query(TableNames.OrderDetail + " as a, "
							+ TableNames.ItemDetail + " as b",
							new String[] { " a.itemId, sum(a.itemNum) as Num, b.itemName" },
							" a.itemId = b.id and a.orderId in ( select id from "
									+ TableNames.Order
									+ " where businessDate = ?)",
							new String[] { String.valueOf(businessDate) },
							"a.itemId", "", "Num desc", "0, 5");
			int count = cursor.getCount();
			if (count < 1) {
				return dashboardItemDetails;
			}
			DashboardItemDetail dashboardItemDetail = null;
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
					.moveToNext()) {
				dashboardItemDetail = new DashboardItemDetail();
				dashboardItemDetail.setItemId(cursor.getInt(0));
				dashboardItemDetail.setQty(cursor.getInt(1));
				dashboardItemDetail.setName(cursor.getString(2));
				dashboardItemDetails.add(dashboardItemDetail);
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
		return dashboardItemDetails;
	}

	public static ArrayList<DashboardSales> getDashboardSales(long businessDate) {
		ArrayList<DashboardSales> dashboardSaless = new ArrayList<DashboardSales>();
		Cursor cursor = null;
		SQLiteDatabase db = SQLExe.getDB();
		try {
			db.beginTransaction();
			cursor = db
					.query(TableNames.Order + " as a, " + TableNames.User
							+ " as b",
							new String[] { " a.userId, sum(a.total) as Num, b.firstName, b.lastName" },
							" a.orderOriginId = "
									+ ParamConst.ORDER_ORIGIN_WAITER
									+ " and a.businessDate = ?",
							new String[] { String.valueOf(businessDate) },
							"a.userId", "", "Num desc", "0, 5");
			int count = cursor.getCount();
			if (count < 1) {
				return dashboardSaless;
			}
			DashboardSales dashboardSales = null;
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
					.moveToNext()) {
				dashboardSales = new DashboardSales();
				dashboardSales.setUserId(cursor.getInt(0));
				dashboardSales.setSales(cursor.getString(1));
				dashboardSales.setWaiterName(cursor.getString(2)
						+ cursor.getString(3));
				dashboardSaless.add(dashboardSales);
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
		return dashboardSaless;
	}

	public static void deleteDataInPosBeforeYesterday(long businessDate) {
		Cursor cursor = null;
		SQLiteDatabase db = SQLExe.getDB();
		// 删除KotSummary等的信息
		String deleteKotItemModifier = "delete from "
				+ TableNames.KotItemModifier
				+ " where kotItemDetailId in ( select id from "
				+ TableNames.KotItemDetail
				+ " where kotSummaryId in (select id from "
				+ TableNames.KotSummary + " where businessDate < ?))";
		String deleteKotItemDetail = "delete  from " + TableNames.KotItemDetail
				+ " where kotSummaryId in (select id from "
				+ TableNames.KotSummary + " where businessDate < ?)";
		String deleteKotSummary = "delete from " + TableNames.KotSummary
				+ " where businessDate < ?";

		// 删除Report的信息
		String deleteReportPluDayComboModifier = "delete from "
				+ TableNames.ReportPluDayComboModifier + " where businessDate < ?";
		String deleteReportPluDayModifier = "delete from "
				+ TableNames.ReportPluDayModifier + " where businessDate < ?";
		String deleteReportPluDayItem = "delete from "
				+ TableNames.ReportPluDayItem + " where businessDate < ?";
		String deleteReportHourly = "delete from " + TableNames.ReportHourly
				+ " where businessDate < ?";
		String deleteReportDiscount = "delete from "
				+ TableNames.ReportDiscount + " where businessDate < ?";
		String deleteReportDayTax = "delete from " + TableNames.ReportDayTax
				+ " where businessDate < ?";
		String deleteReportDaySales = "delete from "
				+ TableNames.ReportDaySales + " where businessDate < ?";
		String deleteUserTimeSheet = "delete  from " + TableNames.UserTimeSheet
				+ " where businessDate < ?";

		// 删除Payment类信息
		String deleteVoidSettlement = "delete from "
				+ TableNames.VoidSettlement
				+ " where paymentId in (select id from " + TableNames.Payment
				+ " where businessDate < ?)";
		String deleteBohHoldSettlement = "delete from "
				+ TableNames.BohHoldSettlement
				+ " where paymentId in (select id from " + TableNames.Payment
				+ " where businessDate < ?)";
		String deleteNonChargableSettlement = "delete from "
				+ TableNames.NonChargableSettlement
				+ " where paymentId in (select id from " + TableNames.Payment
				+ " where businessDate < ?)";
		String deleteNetsSettlement = "delete from "
				+ TableNames.NetsSettlement
				+ " where paymentId in (select id from " + TableNames.Payment
				+ " where businessDate < ?)";
		String deleteCardsSettlement = "delete from "
				+ TableNames.CardsSettlement
				+ " where paymentId in (select id from " + TableNames.Payment
				+ " where businessDate < ?)";
		String deletePaymentSettlement = "delete from "
				+ TableNames.PaymentSettlement
				+ " where paymentId in (select id from " + TableNames.Payment
				+ " where businessDate < ?)";
		String deletePayment = "delete from " + TableNames.Payment
				+ " where businessDate < ?";

		// 删除Order等的信息
		String deleteOrderDetailTax = "delete from "
				+ TableNames.OrderDetailTax
				+ " where orderId in (select id from " + TableNames.Order
				+ " where businessDate < ?)";
		String deleteOrderSplits = "delete from " + TableNames.OrderSplit
				+ " where orderId in (select id from " + TableNames.Order
				+ " where businessDate < ?)";
		String deleteRoundAmount = "delete from " + TableNames.RoundAmount
				+ " where orderId in (select id from " + TableNames.Order
				+ " where businessDate < ?)";
		String deleteOrderBill = "delete from " + TableNames.OrderBill
				+ " where orderId in (select id from " + TableNames.Order
				+ " where businessDate < ?)";
		String deleteOrderModifier = "delete from " + TableNames.OrderModifier
				+ " where orderId in (select id from " + TableNames.Order
				+ " where businessDate < ?)";
		String deleteOrderDetail = "delete from " + TableNames.OrderDetail
				+ " where orderId in (select id from " + TableNames.Order
				+ " where businessDate < ?)";
		String deleteOrder = "delete from " + TableNames.Order
				+ " where businessDate < ?";

		try {
			db.beginTransaction();
			// 删除KotSummary等的信息
			db.execSQL(deleteKotItemModifier, new Object[] { businessDate + "" });
			db.execSQL(deleteKotItemDetail, new Object[] { businessDate + "" });
			db.execSQL(deleteKotSummary, new Object[] { businessDate + "" });
			
			// 删除Report的信息
			db.execSQL(deleteReportPluDayComboModifier, new Object[] { businessDate + "" });
			db.execSQL(deleteReportPluDayModifier, new Object[] { businessDate + "" });
			db.execSQL(deleteReportPluDayItem, new Object[] { businessDate + "" });
			db.execSQL(deleteReportHourly, new Object[] { businessDate + "" });
			db.execSQL(deleteReportDiscount, new Object[] { businessDate + "" });
			db.execSQL(deleteReportDayTax, new Object[] { businessDate + "" });
			db.execSQL(deleteReportDaySales, new Object[] { businessDate + "" });
			db.execSQL(deleteUserTimeSheet, new Object[] { businessDate + "" });

			// 删除Payment类信息
			db.execSQL(deleteVoidSettlement, new Object[] { businessDate + "" });
			db.execSQL(deleteBohHoldSettlement, new Object[] { businessDate + "" });
			db.execSQL(deleteNonChargableSettlement, new Object[] { businessDate + "" });
			db.execSQL(deleteNetsSettlement, new Object[] { businessDate + "" });
			db.execSQL(deleteCardsSettlement, new Object[] { businessDate + "" });
			db.execSQL(deletePaymentSettlement, new Object[] { businessDate + "" });
			db.execSQL(deletePayment, new Object[] { businessDate + "" });

			// 删除Order等的信息
			db.execSQL(deleteOrderDetailTax, new Object[] { businessDate + "" });
			db.execSQL(deleteOrderSplits, new Object[] { businessDate + "" });
			db.execSQL(deleteRoundAmount, new Object[] { businessDate + "" });
			db.execSQL(deleteOrderBill, new Object[] { businessDate + "" });
			db.execSQL(deleteOrderModifier, new Object[] { businessDate + "" });
			db.execSQL(deleteOrderDetail, new Object[] { businessDate + "" });
			db.execSQL(deleteOrder, new Object[] { businessDate + "" });
			
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
			db.endTransaction();
		}

	}
	
	public static void deleteReportDataByBusinessDate(long businessDate) {
		// 删除Report的信息
		String deleteReportPluDayComboModifier = "delete from "
				+ TableNames.ReportPluDayComboModifier
				+ " where businessDate = ?";
		String deleteReportPluDayModifier = "delete from "
				+ TableNames.ReportPluDayModifier + " where businessDate = ?";
		String deleteReportPluDayItem = "delete from "
				+ TableNames.ReportPluDayItem + " where businessDate = ?";
		String deleteReportHourly = "delete from " + TableNames.ReportHourly
				+ " where businessDate = ?";
		String deleteReportDiscount = "delete from "
				+ TableNames.ReportDiscount + " where businessDate = ?";
		String deleteReportDayTax = "delete from " + TableNames.ReportDayTax
				+ " where businessDate = ?";
		String deleteReportDaySales = "delete from "
				+ TableNames.ReportDaySales + " where businessDate = ?";

		Cursor cursor = null;
		SQLiteDatabase db = SQLExe.getDB();

		try {
			db.beginTransaction();

			// 删除Report的信息
			db.execSQL(deleteReportPluDayComboModifier,
					new Object[] { businessDate + "" });
			db.execSQL(deleteReportPluDayModifier, new Object[] { businessDate
					+ "" });
			db.execSQL(deleteReportPluDayItem,
					new Object[] { businessDate + "" });
			db.execSQL(deleteReportHourly, new Object[] { businessDate + "" });
			db.execSQL(deleteReportDiscount, new Object[] { businessDate + "" });
			db.execSQL(deleteReportDayTax, new Object[] { businessDate + "" });
			db.execSQL(deleteReportDaySales, new Object[] { businessDate + "" });
			
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
			db.endTransaction();
		}
	}
}
