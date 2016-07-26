package com.alfredbase.store.sql.temporaryforapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.alfredbase.javabean.temporaryforapp.AppOrder;
import com.alfredbase.store.SQLExe;
import com.alfredbase.store.TableNames;

import java.util.ArrayList;
import java.util.List;

public class AppOrderSQL {
	/*
	private id;    //'主键id',
    private Integer orderNo;    //'订单编号',
    private Integer custId;    //'顾客id',
    private Integer restId;        //'餐厅id',
    private Integer revenueId;    //'收银中心id',
    private Integer sourceType;    //'订单来源类型(0外部第三方应用，1手机APP，2微信，3外卖)',
    private Integer tableId;    // '桌子id',
    private Integer orderStatus;    //'订单状态(0未确认，1已确认，2已下单，3已支付, 4已发送到厨房, 5订单完成)',
    private String subTotal;    //'订单总金额',
    private String taxAmount;    //'税收总金额',
    private String discountAmount;    //'打折总金额',
    private Integer discountType;    //'打折类型(0不打折、10主订单按照比率打折、11主订单直接减、12子订单打折)',
    private String total;    //'实收金额=订单总金额+税收总金额-打折总金额',
    private Integer orderCount;    //'订单数量',
    private long createTime;    //'创建时间',
    private long updateTime
					 */
	public static void addAppOrder(AppOrder appOrder){

		if (appOrder == null) {
			return;
		}
		try {
			String sql = "replace into "
					+ TableNames.AppOrder
					+ " (id, orderNo, custId, restId, revenueId, sourceType, tableId, orderStatus, subTotal, taxAmount, "
					+ " discountAmount, discountType, total, orderCount, createTime, updateTime, tableType, tableNo, bizType)"
					+ " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			SQLExe.getDB().execSQL(
					sql,
					new Object[] { appOrder.getId(), appOrder.getOrderNo(),
					appOrder.getCustId(), appOrder.getRestId(),
					appOrder.getRevenueId(), appOrder.getSourceType(),
					appOrder.getTableId(), appOrder.getOrderStatus(),
					appOrder.getSubTotal(), appOrder.getTaxAmount(),
					appOrder.getDiscountAmount(), appOrder.getDiscountType(),
					appOrder.getTotal(), appOrder.getOrderCount(),
					appOrder.getCreateTime(), appOrder.getUpdateTime(),
					appOrder.getTableType(), appOrder.getTableNo(),
					appOrder.getBizType()});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void updateAppOrder(AppOrder appOrder){

		if (appOrder == null) {
			return;
		}
		try {
			String sql = "replace into "
					+ TableNames.AppOrder
					+ " (id, orderNo, custId, restId, revenueId, sourceType, tableId, orderStatus, subTotal, taxAmount, "
					+ " discountAmount, discountType, total, orderCount, createTime, updateTime, tableType, tableNo, bizType)"
					+ " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			SQLExe.getDB().execSQL(
					sql,
					new Object[] { appOrder.getId(), appOrder.getOrderNo(),
							appOrder.getCustId(), appOrder.getRestId(),
							appOrder.getRevenueId(), appOrder.getSourceType(),
							appOrder.getTableId(), appOrder.getOrderStatus(),
							appOrder.getSubTotal(), appOrder.getTaxAmount(),
							appOrder.getDiscountAmount(), appOrder.getDiscountType(),
							appOrder.getTotal(), appOrder.getOrderCount(),
							appOrder.getCreateTime(), appOrder.getUpdateTime(),
							appOrder.getTableType(), appOrder.getTableNo(),
							appOrder.getBizType()});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
		public static List<AppOrder> getAppOrderByOrderStatus(int orderStatus){
		String sql = "select * from " + TableNames.AppOrder + " where orderStatus >= ?";
		Cursor cursor = null;
		List<AppOrder> result = new ArrayList<AppOrder>();
		SQLiteDatabase db = SQLExe.getDB();
		try {
			cursor = db.rawQuery(sql, new String[] {orderStatus + ""});
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
					.moveToNext()) {
				AppOrder appOrder = new AppOrder();
				appOrder.setId(cursor.getInt(0));
				appOrder.setOrderNo(cursor.getInt(1));
				appOrder.setCustId(cursor.getInt(2));
				appOrder.setRestId(cursor.getInt(3));
				appOrder.setRevenueId(cursor.getInt(4));
				appOrder.setSourceType(cursor.getInt(5));
				appOrder.setTableId(cursor.getInt(6));
				appOrder.setOrderStatus(cursor.getInt(7));
				appOrder.setSubTotal(cursor.getString(8));
				appOrder.setTaxAmount(cursor.getString(9));
				appOrder.setDiscountAmount(cursor.getString(10));
				appOrder.setDiscountType(cursor.getInt(11));
				appOrder.setTotal(cursor.getString(12));
				appOrder.setOrderCount(cursor.getInt(13));
				appOrder.setCreateTime(cursor.getLong(14));
				appOrder.setUpdateTime(cursor.getLong(15));
				appOrder.setTableType(cursor.getInt(16));
				appOrder.setTableNo(cursor.getString(17));
				appOrder.setBizType(cursor.getInt(18));
				result.add(appOrder);
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

	public static AppOrder getAppOrderById(int id){
		String sql = "select * from " + TableNames.AppOrder + " where id = ?";
		Cursor cursor = null;
		AppOrder appOrder = null;
		SQLiteDatabase db = SQLExe.getDB();
		try {
			cursor = db.rawQuery(sql, new String[] {id + ""});
			if (cursor.moveToFirst()) {
				appOrder = new AppOrder();
				appOrder.setId(cursor.getInt(0));
				appOrder.setOrderNo(cursor.getInt(1));
				appOrder.setCustId(cursor.getInt(2));
				appOrder.setRestId(cursor.getInt(3));
				appOrder.setRevenueId(cursor.getInt(4));
				appOrder.setSourceType(cursor.getInt(5));
				appOrder.setTableId(cursor.getInt(6));
				appOrder.setOrderStatus(cursor.getInt(7));
				appOrder.setSubTotal(cursor.getString(8));
				appOrder.setTaxAmount(cursor.getString(9));
				appOrder.setDiscountAmount(cursor.getString(10));
				appOrder.setDiscountType(cursor.getInt(11));
				appOrder.setTotal(cursor.getString(12));
				appOrder.setOrderCount(cursor.getInt(13));
				appOrder.setCreateTime(cursor.getLong(14));
				appOrder.setUpdateTime(cursor.getLong(15));
				appOrder.setTableType(cursor.getInt(16));
				appOrder.setTableNo(cursor.getString(17));
				appOrder.setBizType(cursor.getInt(18));
			}
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return appOrder;
	}
	
	public static void deleteTempOrder(){
		String sql = "delete from " + TableNames.AppOrder ;
		try {
			SQLExe.getDB().execSQL(sql, new Object[] { });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
