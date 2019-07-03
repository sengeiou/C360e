package com.alfredbase.store.sql;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.alfredbase.ParamConst;
import com.alfredbase.javabean.KotSummary;
import com.alfredbase.javabean.Order;
import com.alfredbase.store.SQLExe;
import com.alfredbase.store.TableNames;
import com.alfredbase.utils.SQLiteStatementHelper;

import java.util.ArrayList;
import java.util.List;

public class KotSummarySQL {


	//	String sql = "select * from " + TableNames.KotSummary + " where orderId = ?";
//	Cursor cursor = null;
//	SQLiteDatabase db = SQLExe.getDB();
//		try {
//		cursor = db.rawQuery(sql, new String[] {orderId+""});
	public static void update(KotSummary kotSummary) {
		if (kotSummary == null) {
			return;
		}
		try {
			String sql = "replace into "
					+ TableNames.KotSummary
					+ "(id, orderId, revenueCenterId, tableId, tableName, revenueCenterName,status, createTime, updateTime,"
					+ " businessDate,isTakeAway,orderNo, revenueCenterIndex, orderRemark, empName, numTag,eatType,address,contact,mobile,deliveryTime,appOrderId)"
					+ " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			SQLExe.getDB().execSQL(
					sql,
					new Object[]{kotSummary.getId(),
							kotSummary.getOrderId(),
							kotSummary.getRevenueCenterId(),
							kotSummary.getTableId(),
							kotSummary.getTableName(),
							kotSummary.getRevenueCenterName(),
							kotSummary.getStatus(),
							kotSummary.getCreateTime(),
							kotSummary.getUpdateTime(),
							kotSummary.getBusinessDate(),
							kotSummary.getIsTakeAway(),
							kotSummary.getOrderNo(),
							kotSummary.getRevenueCenterIndex(),
							kotSummary.getOrderRemark(),
							kotSummary.getEmpName(),
							kotSummary.getNumTag(),
							kotSummary.getEatType(),
							kotSummary.getAddress(),
							kotSummary.getContact(),
							kotSummary.getMobile(),
							kotSummary.getDeliveryTime(),
							kotSummary.getAppOrderId()
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void addKotSummaryList(List<KotSummary> kotSummarys) {
		if (kotSummarys == null) {
			return;
		}
		SQLiteDatabase db = SQLExe.getDB();
		try {
			db.beginTransaction();
			String sql = "replace into "
					+ TableNames.KotSummary
					+ "(id, orderId, revenueCenterId, tableId, tableName, revenueCenterName,status, createTime, updateTime,"
					+ " businessDate,isTakeAway,orderNo, revenueCenterIndex, orderRemark, empName, numTag,eatType,address,contact,mobile,deliveryTime,appOrderId)"
					+ " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			SQLiteStatement sqLiteStatement = db.compileStatement(
					sql);
			for (KotSummary kotSummary : kotSummarys) {
				SQLiteStatementHelper.bindLong(sqLiteStatement, 1,
						kotSummary.getId());
				SQLiteStatementHelper.bindLong(sqLiteStatement, 2,
						kotSummary.getOrderId());
				SQLiteStatementHelper.bindLong(sqLiteStatement, 3,
						kotSummary.getRevenueCenterId());
				SQLiteStatementHelper.bindLong(sqLiteStatement, 4,
						kotSummary.getTableId());
				SQLiteStatementHelper.bindString(sqLiteStatement, 5,
						kotSummary.getTableName());
				SQLiteStatementHelper.bindString(sqLiteStatement, 6,
						kotSummary.getRevenueCenterName());
				SQLiteStatementHelper.bindLong(sqLiteStatement, 7,
						kotSummary.getStatus());
				SQLiteStatementHelper.bindLong(sqLiteStatement, 8,
						kotSummary.getCreateTime());
				SQLiteStatementHelper.bindLong(sqLiteStatement, 9,
						kotSummary.getUpdateTime());
				SQLiteStatementHelper.bindLong(sqLiteStatement, 10,
						kotSummary.getBusinessDate());
				SQLiteStatementHelper.bindLong(sqLiteStatement, 11,
						kotSummary.getIsTakeAway());
				SQLiteStatementHelper.bindLong(sqLiteStatement, 12,
						kotSummary.getOrderNo());
				SQLiteStatementHelper.bindLong(sqLiteStatement, 13,
						kotSummary.getRevenueCenterIndex());
				SQLiteStatementHelper.bindString(sqLiteStatement,14,
						kotSummary.getOrderRemark());
				SQLiteStatementHelper.bindString(sqLiteStatement,15,
						kotSummary.getEmpName());
				SQLiteStatementHelper.bindString(sqLiteStatement,16,
						kotSummary.getNumTag());
				SQLiteStatementHelper.bindLong(sqLiteStatement,17,
						kotSummary.getEatType());
				SQLiteStatementHelper.bindString(sqLiteStatement,18,
						kotSummary.getAddress());
				SQLiteStatementHelper.bindString(sqLiteStatement,19,
						kotSummary.getContact());
				SQLiteStatementHelper.bindString(sqLiteStatement,20,
						kotSummary.getMobile());
				SQLiteStatementHelper.bindLong(sqLiteStatement,21,
						kotSummary.getDeliveryTime());
				SQLiteStatementHelper.bindLong(sqLiteStatement,22,
						kotSummary.getAppOrderId());

				sqLiteStatement.executeInsert();
			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
		}
	}

	public static ArrayList<KotSummary> getAllKotSummary() {
		ArrayList<KotSummary> result = new ArrayList<KotSummary>();
		String sql = "select * from " + TableNames.KotSummary + " order by id desc";
		Cursor cursor = null;
		SQLiteDatabase db = SQLExe.getDB();
		try {
			cursor = db.rawQuery(sql, new String[] {});
			int count = cursor.getCount();
			if (count < 1) {
				return result;
			}
			KotSummary kotSummary = null;
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
					.moveToNext()) {
				kotSummary = new KotSummary();
				kotSummary.setId(cursor.getInt(0));
				kotSummary.setOrderId(cursor.getInt(1));
				kotSummary.setRevenueCenterId(cursor.getInt(2));
				kotSummary.setTableId(cursor.getInt(3));
				kotSummary.setTableName(cursor.getString(4));
				kotSummary.setRevenueCenterName(cursor.getString(5));
				kotSummary.setStatus(cursor.getInt(6));
				kotSummary.setCreateTime(cursor.getLong(7));
				kotSummary.setUpdateTime(cursor.getLong(8));
				kotSummary.setBusinessDate(cursor.getLong(9));
				kotSummary.setIsTakeAway(cursor.getInt(10));
				kotSummary.setOrderNo(cursor.getInt(11));
				kotSummary.setRevenueCenterIndex(cursor.getInt(12));
				kotSummary.setOrderRemark(cursor.getString(13));
				kotSummary.setEmpName(cursor.getString(14));
				kotSummary.setNumTag(cursor.getString(15));
				kotSummary.setEatType(cursor.getInt(16));
				kotSummary.setAddress(cursor.getString(17));
				kotSummary.setContact(cursor.getString(18));
				kotSummary.setMobile(cursor.getString(19));
				kotSummary.setDeliveryTime(cursor.getLong(20));
				kotSummary.setAppOrderId(cursor.getInt(21));
				result.add(kotSummary);
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

	public static ArrayList<KotSummary> getUndoneKotSummary() {
		ArrayList<KotSummary> result = new ArrayList<KotSummary>();
		String sql = "select * from " + TableNames.KotSummary + " where status = 0 order by updateTime ";
		Cursor cursor = null;
		SQLiteDatabase db = SQLExe.getDB();
		try {
			cursor = db.rawQuery(sql, new String[] {});
			int count = cursor.getCount();
			if (count < 1) {
				return result;
			}
			KotSummary kotSummary = null;
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
					.moveToNext()) {
				kotSummary = new KotSummary();
				kotSummary.setId(cursor.getInt(0));
				kotSummary.setOrderId(cursor.getInt(1));
				kotSummary.setRevenueCenterId(cursor.getInt(2));
				kotSummary.setTableId(cursor.getInt(3));
				kotSummary.setTableName(cursor.getString(4));
				kotSummary.setRevenueCenterName(cursor.getString(5));
				kotSummary.setStatus(cursor.getInt(6));
				kotSummary.setCreateTime(cursor.getLong(7));
				kotSummary.setUpdateTime(cursor.getLong(8));
				kotSummary.setBusinessDate(cursor.getLong(9));
				kotSummary.setIsTakeAway(cursor.getInt(10));
				kotSummary.setOrderNo(cursor.getInt(11));
				kotSummary.setRevenueCenterIndex(cursor.getInt(12));
				kotSummary.setOrderRemark(cursor.getString(13));
				kotSummary.setEmpName(cursor.getString(14));
				kotSummary.setNumTag(cursor.getString(15));
				kotSummary.setEatType(cursor.getInt(16));
				kotSummary.setAddress(cursor.getString(17));
				kotSummary.setContact(cursor.getString(18));
				kotSummary.setMobile(cursor.getString(19));
				kotSummary.setDeliveryTime(cursor.getLong(20));
				kotSummary.setAppOrderId(cursor.getInt(21));
				result.add(kotSummary);
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

	public static ArrayList<KotSummary> getUndoneKotSummaryByBusinessDateAndOrderUnfinish(long businessDate) {
		ArrayList<KotSummary> result = new ArrayList<KotSummary>();
		String sql = "select * from " + TableNames.KotSummary + " where status = " + ParamConst.KOTS_STATUS_UNDONE + " and orderId in ( select id from " + TableNames.Order + " where businessDate = ? and orderStatus <> " + ParamConst.ORDER_STATUS_FINISHED + ")";
		Cursor cursor = null;
		SQLiteDatabase db = SQLExe.getDB();
		try {
			cursor = db.rawQuery(sql, new String[] {businessDate+""});
			int count = cursor.getCount();
			if (count < 1) {
				return result;
			}
			KotSummary kotSummary = null;
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
					.moveToNext()) {
				kotSummary = new KotSummary();
				kotSummary.setId(cursor.getInt(0));
				kotSummary.setOrderId(cursor.getInt(1));
				kotSummary.setRevenueCenterId(cursor.getInt(2));
				kotSummary.setTableId(cursor.getInt(3));
				kotSummary.setTableName(cursor.getString(4));
				kotSummary.setRevenueCenterName(cursor.getString(5));
				kotSummary.setStatus(cursor.getInt(6));
				kotSummary.setCreateTime(cursor.getLong(7));
				kotSummary.setUpdateTime(cursor.getLong(8));
				kotSummary.setBusinessDate(cursor.getLong(9));
				kotSummary.setIsTakeAway(cursor.getInt(10));
				kotSummary.setOrderNo(cursor.getInt(11));
				kotSummary.setRevenueCenterIndex(cursor.getInt(12));
				kotSummary.setOrderRemark(cursor.getString(13));
				kotSummary.setEmpName(cursor.getString(14));
				kotSummary.setNumTag(cursor.getString(15));
				kotSummary.setEatType(cursor.getInt(16));
				kotSummary.setAddress(cursor.getString(17));
				kotSummary.setContact(cursor.getString(18));
				kotSummary.setMobile(cursor.getString(19));
				kotSummary.setDeliveryTime(cursor.getLong(20));
				kotSummary.setAppOrderId(cursor.getInt(21));
				result.add(kotSummary);
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

	public static ArrayList<KotSummary> getUndoneKotSummaryByBusinessDateForKiosk(long businessDate) {
		ArrayList<KotSummary> result = new ArrayList<KotSummary>();
		String sql = "select * from " + TableNames.KotSummary + " where status = " + ParamConst.KOTS_STATUS_UNDONE + " and orderId in ( select id from " + TableNames.Order + " where businessDate = ? )";
		Cursor cursor = null;
		SQLiteDatabase db = SQLExe.getDB();
		try {
			cursor = db.rawQuery(sql, new String[] {businessDate+""});
			int count = cursor.getCount();
			if (count < 1) {
				return result;
			}
			KotSummary kotSummary = null;
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
					.moveToNext()) {
				kotSummary = new KotSummary();
				kotSummary.setId(cursor.getInt(0));
				kotSummary.setOrderId(cursor.getInt(1));
				kotSummary.setRevenueCenterId(cursor.getInt(2));
				kotSummary.setTableId(cursor.getInt(3));
				kotSummary.setTableName(cursor.getString(4));
				kotSummary.setRevenueCenterName(cursor.getString(5));
				kotSummary.setStatus(cursor.getInt(6));
				kotSummary.setCreateTime(cursor.getLong(7));
				kotSummary.setUpdateTime(cursor.getLong(8));
				kotSummary.setBusinessDate(cursor.getLong(9));
				kotSummary.setIsTakeAway(cursor.getInt(10));
				kotSummary.setOrderNo(cursor.getInt(11));
				kotSummary.setRevenueCenterIndex(cursor.getInt(12));
				kotSummary.setOrderRemark(cursor.getString(13));
				kotSummary.setEmpName(cursor.getString(14));
				kotSummary.setNumTag(cursor.getString(15));
				kotSummary.setEatType(cursor.getInt(16));
				kotSummary.setAddress(cursor.getString(17));
				kotSummary.setContact(cursor.getString(18));
				kotSummary.setMobile(cursor.getString(19));
				kotSummary.setDeliveryTime(cursor.getLong(20));
				kotSummary.setAppOrderId(cursor.getInt(21));
				result.add(kotSummary);
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
	public static ArrayList<KotSummary> getUndoneKotSummaryByBusinessDateForSubKiosk(long businessDate) {
		ArrayList<KotSummary> result = new ArrayList<KotSummary>();
		String sql = "select * from " + TableNames.KotSummary + " where status = " + ParamConst.KOTS_STATUS_UNDONE + " and orderId in ( select id from " + TableNames.CPOrder + " where businessDate = ? )";
		Cursor cursor = null;
		SQLiteDatabase db = SQLExe.getDB();
		try {
			cursor = db.rawQuery(sql, new String[] {businessDate+""});
			int count = cursor.getCount();
			if (count < 1) {
				return result;
			}
			KotSummary kotSummary = null;
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
					.moveToNext()) {
				kotSummary = new KotSummary();
				kotSummary.setId(cursor.getInt(0));
				kotSummary.setOrderId(cursor.getInt(1));
				kotSummary.setRevenueCenterId(cursor.getInt(2));
				kotSummary.setTableId(cursor.getInt(3));
				kotSummary.setTableName(cursor.getString(4));
				kotSummary.setRevenueCenterName(cursor.getString(5));
				kotSummary.setStatus(cursor.getInt(6));
				kotSummary.setCreateTime(cursor.getLong(7));
				kotSummary.setUpdateTime(cursor.getLong(8));
				kotSummary.setBusinessDate(cursor.getLong(9));
				kotSummary.setIsTakeAway(cursor.getInt(10));
				kotSummary.setOrderNo(cursor.getInt(11));
				kotSummary.setRevenueCenterIndex(cursor.getInt(12));
				kotSummary.setOrderRemark(cursor.getString(13));
				kotSummary.setEmpName(cursor.getString(14));
				kotSummary.setNumTag(cursor.getString(15));
				kotSummary.setEatType(cursor.getInt(16));
				kotSummary.setAddress(cursor.getString(17));
				kotSummary.setContact(cursor.getString(18));
				kotSummary.setMobile(cursor.getString(19));
				kotSummary.setDeliveryTime(cursor.getLong(20));
				kotSummary.setAppOrderId(cursor.getInt(21));
				result.add(kotSummary);
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

	public static KotSummary getKotSummary(int orderId, String numTag) {
		KotSummary kotSummary = null;
		String sql = "select * from " + TableNames.KotSummary + " where orderId = ? and numTag = ?";
		Cursor cursor = null;
		try {
			cursor = SQLExe.getDB().rawQuery(sql, new String[] {orderId + "", numTag});
			if (cursor.moveToFirst()) {
				kotSummary = new KotSummary();
				kotSummary.setId(cursor.getInt(0));
				kotSummary.setOrderId(cursor.getInt(1));
				kotSummary.setRevenueCenterId(cursor.getInt(2));
				kotSummary.setTableId(cursor.getInt(3));
				kotSummary.setTableName(cursor.getString(4));
				kotSummary.setRevenueCenterName(cursor.getString(5));
				kotSummary.setStatus(cursor.getInt(6));
				kotSummary.setCreateTime(cursor.getLong(7));
				kotSummary.setUpdateTime(cursor.getLong(8));
				kotSummary.setBusinessDate(cursor.getLong(9));
				kotSummary.setIsTakeAway(cursor.getInt(10));
				kotSummary.setOrderNo(cursor.getInt(11));
				kotSummary.setRevenueCenterIndex(cursor.getInt(12));
				kotSummary.setOrderRemark(cursor.getString(13));
				kotSummary.setEmpName(cursor.getString(14));
				kotSummary.setNumTag(cursor.getString(15));
				kotSummary.setEatType(cursor.getInt(16));
				kotSummary.setAddress(cursor.getString(17));
				kotSummary.setContact(cursor.getString(18));
				kotSummary.setMobile(cursor.getString(19));
				kotSummary.setDeliveryTime(cursor.getLong(20));
				kotSummary.setAppOrderId(cursor.getInt(21));
			}
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return kotSummary;
	}

	public static ArrayList<KotSummary> getKotSummaryForTransfer(int orderId, String numTag) {
		ArrayList<KotSummary> result = new ArrayList<KotSummary>();
		String sql = "select * from " + TableNames.KotSummary + " where orderId = ? and numTag = ?";
		Cursor cursor = null;
		SQLiteDatabase db = SQLExe.getDB();
		try {
			cursor = db.rawQuery(sql, new String[] {orderId+"", numTag});
			int count = cursor.getCount();
			if (count < 1) {
				return result;
			}
			KotSummary kotSummary = null;
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
					.moveToNext()) {
				kotSummary = new KotSummary();
				kotSummary.setId(cursor.getInt(0));
				kotSummary.setOrderId(cursor.getInt(1));
				kotSummary.setRevenueCenterId(cursor.getInt(2));
				kotSummary.setTableId(cursor.getInt(3));
				kotSummary.setTableName(cursor.getString(4));
				kotSummary.setRevenueCenterName(cursor.getString(5));
				kotSummary.setStatus(cursor.getInt(6));
				kotSummary.setCreateTime(cursor.getLong(7));
				kotSummary.setUpdateTime(cursor.getLong(8));
				kotSummary.setBusinessDate(cursor.getLong(9));
				kotSummary.setIsTakeAway(cursor.getInt(10));
				kotSummary.setOrderNo(cursor.getInt(11));
				kotSummary.setRevenueCenterIndex(cursor.getInt(12));
				kotSummary.setOrderRemark(cursor.getString(13));
				kotSummary.setEmpName(cursor.getString(14));
				kotSummary.setNumTag(cursor.getString(15));
				kotSummary.setEatType(cursor.getInt(16));
				kotSummary.setAddress(cursor.getString(17));
				kotSummary.setContact(cursor.getString(18));
				kotSummary.setMobile(cursor.getString(19));
				kotSummary.setDeliveryTime(cursor.getLong(20));
				kotSummary.setAppOrderId(cursor.getInt(21));
				result.add(kotSummary);
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

	public static KotSummary getKotSummaryByTable(int tableId) {
		KotSummary kotSummary = null;
		String sql = "select * from " + TableNames.KotSummary + " where tableId = ? and status = " + ParamConst.KOTS_STATUS_UNDONE;
		Cursor cursor = null;
		try {
			cursor = SQLExe.getDB().rawQuery(sql, new String[] {tableId + ""});
			if (cursor.moveToFirst()) {
				kotSummary = new KotSummary();
				kotSummary.setId(cursor.getInt(0));
				kotSummary.setOrderId(cursor.getInt(1));
				kotSummary.setRevenueCenterId(cursor.getInt(2));
				kotSummary.setTableId(cursor.getInt(3));
				kotSummary.setTableName(cursor.getString(4));
				kotSummary.setRevenueCenterName(cursor.getString(5));
				kotSummary.setStatus(cursor.getInt(6));
				kotSummary.setCreateTime(cursor.getLong(7));
				kotSummary.setUpdateTime(cursor.getLong(8));
				kotSummary.setBusinessDate(cursor.getLong(9));
				kotSummary.setIsTakeAway(cursor.getInt(10));
				kotSummary.setOrderNo(cursor.getInt(11));
				kotSummary.setRevenueCenterIndex(cursor.getInt(12));
				kotSummary.setOrderRemark(cursor.getString(13));
				kotSummary.setEmpName(cursor.getString(14));
				kotSummary.setNumTag(cursor.getString(15));
				kotSummary.setEatType(cursor.getInt(16));
				kotSummary.setAddress(cursor.getString(17));
				kotSummary.setContact(cursor.getString(18));
				kotSummary.setMobile(cursor.getString(19));
				kotSummary.setDeliveryTime(cursor.getLong(20));
				kotSummary.setAppOrderId(cursor.getInt(21));
			}
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return kotSummary;
	}

	public static KotSummary getKotSummaryById(int id) {
		KotSummary kotSummary = null;
		String sql = "select * from " + TableNames.KotSummary + " where id = ?";
		Cursor cursor = null;
		try {
			cursor = SQLExe.getDB().rawQuery(sql, new String[] {id + ""});
			if (cursor.moveToFirst()) {
				kotSummary = new KotSummary();
				kotSummary.setId(cursor.getInt(0));
				kotSummary.setOrderId(cursor.getInt(1));
				kotSummary.setRevenueCenterId(cursor.getInt(2));
				kotSummary.setTableId(cursor.getInt(3));
				kotSummary.setTableName(cursor.getString(4));
				kotSummary.setRevenueCenterName(cursor.getString(5));
				kotSummary.setStatus(cursor.getInt(6));
				kotSummary.setCreateTime(cursor.getLong(7));
				kotSummary.setUpdateTime(cursor.getLong(8));
				kotSummary.setBusinessDate(cursor.getLong(9));
				kotSummary.setIsTakeAway(cursor.getInt(10));
				kotSummary.setOrderNo(cursor.getInt(11));
				kotSummary.setRevenueCenterIndex(cursor.getInt(12));
				kotSummary.setOrderRemark(cursor.getString(13));
				kotSummary.setEmpName(cursor.getString(14));
				kotSummary.setNumTag(cursor.getString(15));
				kotSummary.setEatType(cursor.getInt(16));
				kotSummary.setAddress(cursor.getString(17));
				kotSummary.setContact(cursor.getString(18));
				kotSummary.setMobile(cursor.getString(19));
				kotSummary.setDeliveryTime(cursor.getLong(20));
				kotSummary.setAppOrderId(cursor.getInt(21));
			}
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return kotSummary;
	}

	public static void deleteKotSummary(KotSummary kotSummary) {
		String sql = "delete from " + TableNames.KotSummary
				+ " where id = ?";
		try {
			SQLExe.getDB().execSQL(sql, new Object[] { kotSummary.getId() });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void deleteKotSummaryByOrder(Order order) {
		String sql = "delete from " + TableNames.KotSummary
				+ " where orderId = ? and numTag = ?";
		try {
			SQLExe.getDB().execSQL(sql, new Object[] { order.getId(), order.getNumTag() });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void updateKotSummaryStatusById(int status, int kotSummayId){
		String sql = "update " + TableNames.KotSummary + " set status = ? where id = ?";
		try {
			SQLExe.getDB().execSQL(sql, new Object[] {status, kotSummayId});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void updateKotSummaryTimeById(long updateTime, int kotSummayId){
		String sql = "update " + TableNames.KotSummary + " set updateTime = ? where id = ?";
		try {
			SQLExe.getDB().execSQL(sql, new Object[] {updateTime, kotSummayId});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void updateKotSummaryEmpById(String empName, int id){
		String sql = "update " + TableNames.KotSummary + " set empName = ? where id = ?";
		try {
			SQLExe.getDB().execSQL(sql, new Object[]{empName, id});
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	public static void deleteAllKotSummary() {
		String sql = "delete from " + TableNames.KotSummary;
		String sssql = "select * from sqlite_sequence";
		String up = "update sqlite_sequence set seq=0 where name='"+TableNames.KotSummary+"'";
		try {
			SQLExe.getDB().execSQL(sql, new Object[] {});
			SQLExe.getDB().rawQuery(sssql,new String[] {});
			SQLExe.getDB().execSQL(up, new Object[] {});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
