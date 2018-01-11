package com.alfredbase.store.sql;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.alfredbase.javabean.LocalDevice;
import com.alfredbase.store.SQLExe;
import com.alfredbase.store.TableNames;

import java.util.ArrayList;

public class LocalDeviceSQL {

	public static void addLocalDevice(LocalDevice localDevice) {
		if (localDevice == null) {
			return;
		}
		try {
			String sql = "replace into "
					+ TableNames.LocalDevice
					+ "(id, deviceId, deviceName, userName, deviceType, ip, macAddress, connected, cashierPrinter,deviceMode, printerName)"
					+ " values (?,?,?,?,?,?,?,?,?,?,?)";
			SQLExe.getDB().execSQL(
					sql,
					new Object[] {localDevice.getId(),
							localDevice.getDeviceId(),
							localDevice.getDeviceName(),
							localDevice.getUserName(),
							localDevice.getDeviceType(), localDevice.getIp(),
							localDevice.getMacAddress(),
							localDevice.getConnected(),
							localDevice.getCashierPrinter(),
							localDevice.getDeviceMode(),
							localDevice.getPrinterName()});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	public static void addLocalDeviceList(List<LocalDevice> localDeviceList) {
//		if (localDeviceList == null) {
//			return;
//		}
//		SQLiteDatabase db = SQLExe.getDB();
//		try {
//			db.beginTransaction();
//			String sql = "replace into "
//					+ TableNames.LocalDevice
//					+ "(deviceId, deviceName, userName, deviceType, ip, macAddress, connected,cashierPrinter,deviceMode)"
//					+ " values (?,?,?,?,?,?,?,?,?)";
//			SQLiteStatement sqLiteStatement = db.compileStatement(
//					sql);
//				for (LocalDevice localDevice : localDeviceList) {
//					SQLiteStatementHelper.bindLong(sqLiteStatement, 1,
//							localDevice.getDeviceId());
//					SQLiteStatementHelper.bindString(sqLiteStatement, 2,
//							localDevice.getDeviceName());
//					SQLiteStatementHelper.bindString(sqLiteStatement, 3,
//							localDevice.getUserName());
//					SQLiteStatementHelper.bindLong(sqLiteStatement, 4,
//							localDevice.getDeviceType());
//					SQLiteStatementHelper.bindString(sqLiteStatement, 5,
//							localDevice.getIp());
//					SQLiteStatementHelper.bindString(sqLiteStatement, 6,
//							localDevice.getMacAddress());
//					SQLiteStatementHelper.bindLong(sqLiteStatement, 7,
//							localDevice.getConnected());
//					SQLiteStatementHelper.bindLong(sqLiteStatement, 8,
//							localDevice.getCashierPrinter());
//					SQLiteStatementHelper.bindString(sqLiteStatement, 9,
//							localDevice.getDeviceMode());
//					sqLiteStatement.executeInsert();
//				}
//				db.setTransactionSuccessful();
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			db.endTransaction();
//		}
//	}

	public static ArrayList<LocalDevice> getAllLocalDevice() {
		ArrayList<LocalDevice> result = new ArrayList<LocalDevice>();
		String sql = "select * from " + TableNames.LocalDevice;
		Cursor cursor = null;
		SQLiteDatabase db = SQLExe.getDB();
		try {
			cursor = db.rawQuery(sql, new String[] {});
			int count = cursor.getCount();
			if (count < 1) {
				return result;
			}
			LocalDevice localDevice = null;
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
					.moveToNext()) {
				localDevice = new LocalDevice();
				localDevice.setId(cursor.getInt(0));
				localDevice.setDeviceId(cursor.getInt(1));
				localDevice.setDeviceName(cursor.getString(2));
				localDevice.setUserName(cursor.getString(3));
				localDevice.setDeviceType(cursor.getInt(4));
				localDevice.setIp(cursor.getString(5));
				localDevice.setMacAddress(cursor.getString(6));
				localDevice.setConnected(cursor.getInt(7));
				localDevice.setCashierPrinter(cursor.getInt(8));
				localDevice.setDeviceMode(cursor.getString(9));
				localDevice.setPrinterName(cursor.getString(10));
				result.add(localDevice);
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


	public static LocalDevice getLocalDeviceByDeviceId(int deviceId) {
		String sql = "select * from " + TableNames.LocalDevice + " where deviceId = ?";
		Cursor cursor = null;
		LocalDevice localDevice = null;
		SQLiteDatabase db = SQLExe.getDB();
		try {
			cursor = db.rawQuery(sql, new String[] {deviceId + ""});
			int count = cursor.getCount();
			if (count < 1) {
				return localDevice;
			}

			if (cursor.moveToFirst()) {
				localDevice = new LocalDevice();
				localDevice.setId(cursor.getInt(0));
				localDevice.setDeviceId(cursor.getInt(1));
				localDevice.setDeviceName(cursor.getString(2));
				localDevice.setUserName(cursor.getString(3));
				localDevice.setDeviceType(cursor.getInt(4));
				localDevice.setIp(cursor.getString(5));
				localDevice.setMacAddress(cursor.getString(6));
				localDevice.setConnected(cursor.getInt(7));
				localDevice.setCashierPrinter(cursor.getInt(8));
				localDevice.setDeviceMode(cursor.getString(9));
				localDevice.setPrinterName(cursor.getString(10));
			}
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return localDevice;
	}

	public static void deleteLocalDeviceByPrinterId(LocalDevice localDevice) {
		String sql = "delete from " + TableNames.LocalDevice
				+ " where deviceId = ?";
		try {
			SQLExe.getDB().execSQL(sql,
					new Object[] { localDevice.getDeviceId() });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void deleteLocalDeviceByPrinterIdAndIP(int deviceId, String ip) {
		String sql = "delete from " + TableNames.LocalDevice
				+ " where deviceId = ? and ip=?";
		try {
			SQLExe.getDB().execSQL(sql,
					new Object[] { deviceId, ip });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void deleteLocalDeviceById(int localDeviceId) {
		String sql = "delete from " + TableNames.LocalDevice
				+ " where id = ?";
		try {
			SQLExe.getDB().execSQL(sql,
					new Object[] { localDeviceId });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
