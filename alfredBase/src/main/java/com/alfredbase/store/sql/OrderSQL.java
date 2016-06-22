package com.alfredbase.store.sql;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.alfredbase.ParamConst;
import com.alfredbase.javabean.Order;
import com.alfredbase.javabean.OrderDetail;
import com.alfredbase.javabean.Tables;
import com.alfredbase.javabean.javabeanforhtml.DashboardTotalDetailInfo;
import com.alfredbase.javabean.model.SessionStatus;
import com.alfredbase.store.SQLExe;
import com.alfredbase.store.TableNames;
import com.alfredbase.utils.BH;
import com.alfredbase.utils.OrderHelper;
import com.alfredbase.utils.SQLiteStatementHelper;

public class OrderSQL {

	public static void addOrder(Order order) {
		if (order == null) {
			return;
		}
		calculate(order);
		try {
			String sql = "insert into "
					+ TableNames.Order
					+ "(orderOriginId, userId, persons, orderStatus, subTotal, taxAmount, discountAmount, "
					+ "total, sessionStatus, restId, revenueId, placeId, tableId, createTime, updateTime," 
					+ "orderNo,businessDate,discount_rate,discount_type,discountPrice,inclusiveTaxName,inclusiveTaxPrice,"
					+ "inclusiveTaxPercentage, appOrderId,isTakeAway)"
					+ " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			SQLExe.getDB().execSQL(
					sql,
					new Object[] { order.getOrderOriginId(), order.getUserId(),
							order.getPersons(), order.getOrderStatus(),
							order.getSubTotal(), order.getTaxAmount(),
							order.getDiscountAmount(), order.getTotal(),
							order.getSessionStatus(), order.getRestId(),
							order.getRevenueId(), order.getPlaceId(), order.getTableId(),
							order.getCreateTime(), order.getUpdateTime(),
							order.getOrderNo(), order.getBusinessDate(),
							order.getDiscountRate(), order.getDiscountType(),
							order.getDiscountPrice(),order.getInclusiveTaxName(),
							order.getInclusiveTaxPrice(), order.getInclusiveTaxPercentage(),
							order.getAppOrderId(),order.getIsTakeAway()});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 修改订单详情OrderDetail，调用这个方法
	 * 
	 * @param order
	 */
	public static void updateOrder(Order order) {
		if (order == null) {
			return;
		}
		calculate(order);

		update(order);
	}
	
	/**
	 * 修改订单Order折扣，调用这个方法
	 * 
	 * @param order
	 */
	public static void updateOrderAndOrderDetailByDiscount(Order order) {
		if (order == null) {
			return;
		}
		updateOrderDetail(order);
		calculateByOrderDiscount(order);
		update(order);
	}
	
	

	public static void update(Order order) {
		if (order == null) {
			return;
		}
		try {
			String sql = "replace into "
					+ TableNames.Order
					+ "(id,orderOriginId, userId, persons, orderStatus, subTotal, taxAmount, discountAmount,"
					+ " total, sessionStatus, restId, revenueId, placeId, tableId, createTime, updateTime,"
					+ "orderNo,businessDate,discount_rate,discount_type, discountPrice, inclusiveTaxName, inclusiveTaxPrice,"
					+ "inclusiveTaxPercentage, appOrderId,isTakeAway)"
					+ " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			SQLExe.getDB().execSQL(
					sql,
					new Object[] { order.getId(), order.getOrderOriginId(),
							order.getUserId(), order.getPersons(),
							order.getOrderStatus(), order.getSubTotal(),
							order.getTaxAmount(), order.getDiscountAmount(),
							order.getTotal(), order.getSessionStatus(),
							order.getRestId(), order.getRevenueId(), order.getPlaceId(),
							order.getTableId(), order.getCreateTime(),
							order.getUpdateTime(), order.getOrderNo(),
							order.getBusinessDate(), order.getDiscountRate(),
							order.getDiscountType(), order.getDiscountPrice(),
							order.getInclusiveTaxName(), order.getInclusiveTaxPrice(),
							order.getInclusiveTaxPercentage(), order.getAppOrderId(),
							order.getIsTakeAway()});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void updateUnFinishedOrderFromWaiter(Order order) {
		if (order == null) {
			return;
		}
		try {
			String sql = "update " + TableNames.Order + " set persons = ? ,  orderStatus = ? , updateTime = ? where id = ? and orderStatus <> " + ParamConst.ORDER_STATUS_FINISHED;
			SQLExe.getDB().execSQL(
					sql,
					new Object[] {order.getPersons(), order.getOrderStatus(), order.getUpdateTime(), order.getId()});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void updateOrderIsTakeAway(Order order, int isTakeAway) {
		try {
			String sql = "update " + TableNames.Order + " set isTakeAway = ?  where id = ? ";
			SQLExe.getDB().execSQL(
					sql,
					new Object[] {isTakeAway + "", order.getId().intValue() + ""});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void updateOrderNo(Order order) {
		try {
			String sql = "update " + TableNames.Order + " set orderNo = ?  where id = ? ";
			SQLExe.getDB().execSQL(
					sql,
					new Object[] {order.getOrderNo().intValue() + "", order.getId().intValue() + ""});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void calculate(Order order) {
		
		List<OrderDetail> orderDetails = OrderDetailSQL.getGeneralOrderDetails(order.getId());
		OrderHelper.setOrderSubTotal(order, orderDetails);
		updateOrderDetail(order);
		orderDetails = OrderDetailSQL.getGeneralOrderDetails(order.getId());
		OrderHelper.setOrderDiscount(order, orderDetails);
		OrderHelper.setOrderTax(order, orderDetails);
		OrderHelper.setOrderTotal(order, orderDetails);
		OrderHelper.setOrderInclusiveTaxPrice(order);
	}
	/**
	 * 修改订单Order折扣，调用这个方法
	 * 
	 * @param order
	 */
	private static void calculateByOrderDiscount(Order order){
		List<OrderDetail> orderDetails = OrderDetailSQL.getGeneralOrderDetails(order.getId());
		OrderHelper.setOrderSubTotal(order, orderDetails);
		OrderHelper.setOrderDiscount(order, orderDetails);
		OrderHelper.setOrderTax(order, orderDetails);
		OrderHelper.setOrderTotal(order, orderDetails);
	}
	/**
	 * 修改订单Order折扣，调用这个方法
	 * 
	 * @param order
	 */
	private static void updateOrderDetail(Order order) {
		List<OrderDetail> orderDetails = OrderDetailSQL.getGeneralOrderDetails(order.getId());
		if (order.getDiscountType().intValue() == ParamConst.ORDER_DISCOUNT_TYPE_RATE_BY_ORDER) {
			for (OrderDetail orderDetail : orderDetails) {
				// 本身是送的，不参与打折
				if (orderDetail.getIsFree() == ParamConst.FREE) {
					continue;
				}
				if(orderDetail.getIsItemDiscount() == ParamConst.ITEM_NO_DISCOUNT){
					continue;
				}
				if(orderDetail.getDiscountType().intValue() != ParamConst.ORDERDETAIL_DISCOUNT_TYPE_RATE
						&& orderDetail.getDiscountType().intValue() != ParamConst.ORDERDETAIL_DISCOUNT_TYPE_SUB){
					orderDetail.setDiscountRate(order.getDiscountRate());
					orderDetail
							.setDiscountType(ParamConst.ORDERDETAIL_DISCOUNT_BYORDER_TYPE_RATE);
					orderDetail.setDiscountPrice(BH.mul(BH.getBDNoFormat(order.getDiscountRate()), BH.getBD(orderDetail.getRealPrice()), false).toString());
					OrderDetailSQL.updateOrderDetail(orderDetail);
				}
			}
		} else if (order.getDiscountType().intValue() == ParamConst.ORDER_DISCOUNT_TYPE_SUB_BY_ORDER) {
			String sumRealPrice = OrderDetailSQL.getOrderDetailRealPriceWhenDiscountBySelf(order);
			if(BH.compare(BH.getBD(order.getSubTotal()), BH.getBD(sumRealPrice))){
				String discount_rate = BH.div(BH.getBD(order.getDiscountPrice()),
						BH.sub(BH.getBD(order.getSubTotal()), BH.getBD(sumRealPrice), false), false).toString();
				for (OrderDetail orderDetail : orderDetails) {
					// 本身是送的，不参与打折
					if (orderDetail.getIsFree() == ParamConst.FREE) {
						continue;
					}
					if(orderDetail.getIsItemDiscount() == ParamConst.ITEM_NO_DISCOUNT){
						continue;
					}
					if(orderDetail.getDiscountType().intValue() != ParamConst.ORDERDETAIL_DISCOUNT_TYPE_RATE
							&& orderDetail.getDiscountType().intValue() != ParamConst.ORDERDETAIL_DISCOUNT_TYPE_SUB){
						orderDetail.setDiscountRate(discount_rate);
						orderDetail
								.setDiscountType(ParamConst.ORDERDETAIL_DISCOUNT_BYORDER_TYPE_SUB);
						orderDetail.setDiscountPrice(BH.mul(BH.getBDNoFormat(discount_rate), BH.getBD(orderDetail.getRealPrice()), false).toString());
						OrderDetailSQL.updateOrderDetail(orderDetail);
					}
				}
			}
		}
	}

	public static void addOrderList(List<Order> orderList) {
		if (orderList == null) {
			return;
		}
		SQLiteDatabase db = SQLExe.getDB();
		try {
			db.beginTransaction();
			String sql = "replace into "
					+ TableNames.Order
					+ "(orderOriginId, userId, persons, orderStatus, subTotal, taxAmount, discountAmount,"
					+ " total, sessionStatus, restId, revenueId, placeId, tableId, createTime, updateTime,"
					+ "orderNo,businessDate,discount_rate,discount_type, discountPrice, inclusiveTaxName, inclusiveTaxPrice,"
					+ "inclusiveTaxPercentage, appOrderId,isTakeAway)"
					+ " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			SQLiteStatement sqLiteStatement = db.compileStatement(
					sql);
				for (Order order : orderList) {
					SQLiteStatementHelper.bindLong(sqLiteStatement, 1,
							order.getOrderOriginId());
					SQLiteStatementHelper.bindLong(sqLiteStatement, 2,
							order.getUserId());
					SQLiteStatementHelper.bindLong(sqLiteStatement, 3,
							order.getPersons());
					SQLiteStatementHelper.bindLong(sqLiteStatement, 4,
							order.getOrderStatus());
					SQLiteStatementHelper.bindString(sqLiteStatement, 5,
							order.getSubTotal());
					SQLiteStatementHelper.bindString(sqLiteStatement, 6,
							order.getTaxAmount());
					SQLiteStatementHelper.bindString(sqLiteStatement, 7,
							order.getDiscountAmount());
					SQLiteStatementHelper.bindString(sqLiteStatement, 8,
							order.getTotal());
					SQLiteStatementHelper.bindLong(sqLiteStatement, 9,
							order.getSessionStatus());
					SQLiteStatementHelper.bindLong(sqLiteStatement, 10,
							order.getRestId());
					SQLiteStatementHelper.bindLong(sqLiteStatement, 11,
							order.getRevenueId());
					SQLiteStatementHelper.bindLong(sqLiteStatement, 12,
							order.getPlaceId());
					SQLiteStatementHelper.bindLong(sqLiteStatement, 13,
							order.getTableId());
					SQLiteStatementHelper.bindLong(sqLiteStatement, 14,
							order.getCreateTime());
					SQLiteStatementHelper.bindLong(sqLiteStatement, 15,
							order.getUpdateTime());
					SQLiteStatementHelper.bindLong(sqLiteStatement, 16,
							order.getOrderNo());
					SQLiteStatementHelper.bindLong(sqLiteStatement, 17,
							order.getBusinessDate());
					SQLiteStatementHelper.bindString(sqLiteStatement, 18,
							order.getDiscountRate());
					SQLiteStatementHelper.bindLong(sqLiteStatement, 19,
							order.getDiscountType());
					SQLiteStatementHelper.bindString(sqLiteStatement, 20,
							order.getDiscountPrice());
					SQLiteStatementHelper.bindString(sqLiteStatement, 21,
							order.getInclusiveTaxName());
					SQLiteStatementHelper.bindString(sqLiteStatement, 22,
							order.getInclusiveTaxPrice());
					SQLiteStatementHelper.bindString(sqLiteStatement, 23,
							order.getInclusiveTaxPercentage());
					SQLiteStatementHelper.bindLong(sqLiteStatement, 24,
							order.getAppOrderId());
					SQLiteStatementHelper.bindLong(sqLiteStatement, 25,
							order.getIsTakeAway());
					sqLiteStatement.executeInsert();
				}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
		}
	}

	public static ArrayList<Order> getAllOrder() {
		ArrayList<Order> result = new ArrayList<Order>();
		String sql = "select * from " + TableNames.Order + " order by id desc";
		Cursor cursor = null;
		SQLiteDatabase db = SQLExe.getDB();
		try {
			db.beginTransaction();
			cursor = db.rawQuery(sql, new String[] {});
			int count = cursor.getCount();
			if (count < 1) {
				return result;
			}
			Order order = null;
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
					.moveToNext()) {
				order = new Order();
				order.setId(cursor.getInt(0));
				order.setOrderOriginId(cursor.getInt(1));
				order.setUserId(cursor.getInt(2));
				order.setPersons(cursor.getInt(3));
				order.setOrderStatus(cursor.getInt(4));
				order.setSubTotal(cursor.getString(5));
				order.setTaxAmount(cursor.getString(6));
				order.setDiscountAmount(cursor.getString(7));
				order.setTotal(cursor.getString(8));
				order.setSessionStatus(cursor.getInt(9));
				order.setRestId(cursor.getInt(10));
				order.setRevenueId(cursor.getInt(11));
				order.setPlaceId(cursor.getInt(12));
				order.setTableId(cursor.getInt(13));
				order.setCreateTime(cursor.getLong(14));
				order.setUpdateTime(cursor.getLong(15));
				order.setOrderNo(cursor.getInt(16));
				order.setBusinessDate(cursor.getLong(17));
				order.setDiscountRate(cursor.getString(18));
				order.setDiscountType(cursor.getInt(19));
				order.setDiscountPrice(cursor.getString(20));
				order.setInclusiveTaxName(cursor.getString(21));
				order.setInclusiveTaxPrice(cursor.getString(22));
				order.setInclusiveTaxPercentage(cursor.getString(23));
				order.setAppOrderId(cursor.getInt(24));
				order.setIsTakeAway(cursor.getInt(25));
				result.add(order);
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
	public static ArrayList<Order> getUnpaidOrdersBySession(SessionStatus sessionStatus, long businessDate) {
		ArrayList<Order> result = new ArrayList<Order>();
		String sql = "select * from " + TableNames.Order  + " where sessionStatus = ? and createTime > ? and businessDate = ? and orderStatus <> "+ParamConst.ORDER_STATUS_FINISHED+" ";
		SQLiteDatabase db = SQLExe.getDB();
		Cursor cursor = null;
		try {
			db.beginTransaction();
			cursor = db.rawQuery(sql, new String[] {String.valueOf(sessionStatus.getSession_status()), String.valueOf(sessionStatus.getTime()), String.valueOf(businessDate)});
			int count = cursor.getCount();
			if (count < 1) {
				return result;
			}
			Order order = null;
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
					.moveToNext()) {
				order = new Order();
				order.setId(cursor.getInt(0));
				order.setOrderOriginId(cursor.getInt(1));
				order.setUserId(cursor.getInt(2));
				order.setPersons(cursor.getInt(3));
				order.setOrderStatus(cursor.getInt(4));
				order.setSubTotal(cursor.getString(5));
				order.setTaxAmount(cursor.getString(6));
				order.setDiscountAmount(cursor.getString(7));
				order.setTotal(cursor.getString(8));
				order.setSessionStatus(cursor.getInt(9));
				order.setRestId(cursor.getInt(10));
				order.setRevenueId(cursor.getInt(11));
				order.setPlaceId(cursor.getInt(12));
				order.setTableId(cursor.getInt(13));
				order.setCreateTime(cursor.getLong(14));
				order.setUpdateTime(cursor.getLong(15));
				order.setOrderNo(cursor.getInt(16));
				order.setBusinessDate(cursor.getLong(17));
				order.setDiscountRate(cursor.getString(18));
				order.setDiscountType(cursor.getInt(19));
				order.setDiscountPrice(cursor.getString(20));
				order.setInclusiveTaxName(cursor.getString(21));
				order.setInclusiveTaxPrice(cursor.getString(22));
				order.setInclusiveTaxPercentage(cursor.getString(23));
				order.setAppOrderId(cursor.getInt(24));
				order.setIsTakeAway(cursor.getInt(25));
				result.add(order);
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
	
	public static ArrayList<Order> getFinishedOrdersBySession(SessionStatus sessionStatus, long businessDate) {
		ArrayList<Order> result = new ArrayList<Order>();
		String sql = "select * from " + TableNames.Order  + " where sessionStatus = ? and businessDate = ? and orderStatus = "+ParamConst.ORDER_STATUS_FINISHED+" ";
		SQLiteDatabase db = SQLExe.getDB();
		Cursor cursor = null;
		try {
			db.beginTransaction();
			cursor = db.rawQuery(sql, new String[] {String.valueOf(sessionStatus.getSession_status()), String.valueOf(businessDate)});
			int count = cursor.getCount();
			if (count < 1) {
				return result;
			}
			Order order = null;
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
					.moveToNext()) {
				order = new Order();
				order.setId(cursor.getInt(0));
				order.setOrderOriginId(cursor.getInt(1));
				order.setUserId(cursor.getInt(2));
				order.setPersons(cursor.getInt(3));
				order.setOrderStatus(cursor.getInt(4));
				order.setSubTotal(cursor.getString(5));
				order.setTaxAmount(cursor.getString(6));
				order.setDiscountAmount(cursor.getString(7));
				order.setTotal(cursor.getString(8));
				order.setSessionStatus(cursor.getInt(9));
				order.setRestId(cursor.getInt(10));
				order.setRevenueId(cursor.getInt(11));
				order.setPlaceId(cursor.getInt(12));
				order.setTableId(cursor.getInt(13));
				order.setCreateTime(cursor.getLong(14));
				order.setUpdateTime(cursor.getLong(15));
				order.setOrderNo(cursor.getInt(16));
				order.setBusinessDate(cursor.getLong(17));
				order.setDiscountRate(cursor.getString(18));
				order.setDiscountType(cursor.getInt(19));
				order.setDiscountPrice(cursor.getString(20));
				order.setInclusiveTaxName(cursor.getString(21));
				order.setInclusiveTaxPrice(cursor.getString(22));
				order.setInclusiveTaxPercentage(cursor.getString(23));
				order.setAppOrderId(cursor.getInt(24));
				order.setIsTakeAway(cursor.getInt(25));				
				result.add(order);
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
	
	public static ArrayList<Order> getAllOrderByTime(long businessDate) {
		ArrayList<Order> result = new ArrayList<Order>();
		Cursor cursor = null;
		SQLiteDatabase db = SQLExe.getDB();
		try {
			db.beginTransaction();
			cursor = db.query(TableNames.Order,
					new String[] { " * " }, "businessDate = ? and orderStatus = " + ParamConst.ORDER_STATUS_FINISHED,
					new String[] { String.valueOf(businessDate) }, "", "", "",
					"");
			int count = cursor.getCount();
			if (count < 1) {
				return result;
			}
			Order order = null;
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
					.moveToNext()) {
				order = new Order();
				order.setId(cursor.getInt(0));
				order.setOrderOriginId(cursor.getInt(1));
				order.setUserId(cursor.getInt(2));
				order.setPersons(cursor.getInt(3));
				order.setOrderStatus(cursor.getInt(4));
				order.setSubTotal(cursor.getString(5));
				order.setTaxAmount(cursor.getString(6));
				order.setDiscountAmount(cursor.getString(7));
				order.setTotal(cursor.getString(8));
				order.setSessionStatus(cursor.getInt(9));
				order.setRestId(cursor.getInt(10));
				order.setRevenueId(cursor.getInt(11));
				order.setPlaceId(cursor.getInt(12));
				order.setTableId(cursor.getInt(13));
				order.setCreateTime(cursor.getLong(14));
				order.setUpdateTime(cursor.getLong(15));
				order.setOrderNo(cursor.getInt(16));
				order.setBusinessDate(cursor.getLong(17));
				order.setDiscountRate(cursor.getString(18));
				order.setDiscountType(cursor.getInt(19));
				order.setDiscountPrice(cursor.getString(20));
				order.setInclusiveTaxName(cursor.getString(21));
				order.setInclusiveTaxPrice(cursor.getString(22));
				order.setInclusiveTaxPercentage(cursor.getString(23));
				order.setAppOrderId(cursor.getInt(24));
				order.setIsTakeAway(cursor.getInt(25));
				result.add(order);
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
	
	public static ArrayList<Order> getAllOrderByTime(long businessDate,SessionStatus sessionStatus) {
		ArrayList<Order> result = new ArrayList<Order>();
		Cursor cursor = null;
		SQLiteDatabase db = SQLExe.getDB();
		try {
			db.beginTransaction();
			cursor = db.query(TableNames.Order,
					new String[] { " * " }, "businessDate = ? and sessionStatus = ? and createTime > ? and orderStatus = " + ParamConst.ORDER_STATUS_FINISHED,
					new String[] { String.valueOf(businessDate), String.valueOf(sessionStatus.getSession_status()), String.valueOf(sessionStatus.getTime() )}, "", "", "",
					"");
			int count = cursor.getCount();
			if (count < 1) {
				return result;
			}
			Order order = null;
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
					.moveToNext()) {
				order = new Order();
				order.setId(cursor.getInt(0));
				order.setOrderOriginId(cursor.getInt(1));
				order.setUserId(cursor.getInt(2));
				order.setPersons(cursor.getInt(3));
				order.setOrderStatus(cursor.getInt(4));
				order.setSubTotal(cursor.getString(5));
				order.setTaxAmount(cursor.getString(6));
				order.setDiscountAmount(cursor.getString(7));
				order.setTotal(cursor.getString(8));
				order.setSessionStatus(cursor.getInt(9));
				order.setRestId(cursor.getInt(10));
				order.setRevenueId(cursor.getInt(11));
				order.setPlaceId(cursor.getInt(12));
				order.setTableId(cursor.getInt(13));
				order.setCreateTime(cursor.getLong(14));
				order.setUpdateTime(cursor.getLong(15));
				order.setOrderNo(cursor.getInt(16));
				order.setBusinessDate(cursor.getLong(17));
				order.setDiscountRate(cursor.getString(18));
				order.setDiscountType(cursor.getInt(19));
				order.setDiscountPrice(cursor.getString(20));
				order.setInclusiveTaxName(cursor.getString(21));
				order.setInclusiveTaxPrice(cursor.getString(22));
				order.setInclusiveTaxPercentage(cursor.getString(23));
				order.setAppOrderId(cursor.getInt(24));
				order.setIsTakeAway(cursor.getInt(25));
				result.add(order);
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

	public static List<Tables> getAllTimeOutOrderByTime(long businessDate,SessionStatus sessionStatus,long time) {
		List<Tables> result = new ArrayList<Tables>();
		Cursor cursor = null;
		SQLiteDatabase db = SQLExe.getDB();
		String sql = "select t.* from "
				+ TableNames.Order+" od, "
				+ TableNames.Tables+" t where od.tableId = t.id and od.businessDate = ? " 
				+ " and od.sessionStatus = ? and od.createTime > ? and od.createTime < ? and t.tableStatus = " 
				+ ParamConst.TABLE_STATUS_DINING
				+ " and od.orderStatus <> " 
				+ ParamConst.ORDER_STATUS_FINISHED
				+ " group by t.id";
		try {
			db.beginTransaction();
			cursor = db.rawQuery(sql, new String[] { String.valueOf(businessDate), 
					String.valueOf(sessionStatus.getSession_status()), 
					String.valueOf(sessionStatus.getTime() ),String.valueOf(time)});
			int count = cursor.getCount();
			if (count < 1) {
				return result;
			}
			Tables tables = null;
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
					.moveToNext()) {
				tables = new Tables();
				tables.setId(cursor.getInt(0));
				tables.setRestaurantId(cursor.getInt(1));
				tables.setRevenueId(cursor.getInt(2));
				tables.setPlacesId(cursor.getInt(3));
				tables.setTableName(cursor.getString(4));
				tables.setTablePacks(cursor.getInt(5));
				tables.setIsActive(cursor.getInt(6));
				tables.setTableStatus(cursor.getInt(7));
				result.add(tables);
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
	
	public static Order getLastOrderatTabel(Tables tables) {
		Order order = null;
		String sql = "select * from " + TableNames.Order
				+ " where tableId = ? order by id DESC";
		Cursor cursor = null;
		try {
			cursor = SQLExe.getDB().rawQuery(
					sql,
					new String[] { tables.getId() + "" });
			int count = cursor.getCount();
			if (count < 1) {
				return order;
			}
			if (cursor.moveToFirst()) {
				order = new Order();
				order.setId(cursor.getInt(0));
				order.setOrderOriginId(cursor.getInt(1));
				order.setUserId(cursor.getInt(2));
				order.setPersons(cursor.getInt(3));
				order.setOrderStatus(cursor.getInt(4));
				order.setSubTotal(cursor.getString(5));
				order.setTaxAmount(cursor.getString(6));
				order.setDiscountAmount(cursor.getString(7));
				order.setTotal(cursor.getString(8));
				order.setSessionStatus(cursor.getInt(9));
				order.setRestId(cursor.getInt(10));
				order.setRevenueId(cursor.getInt(11));
				order.setPlaceId(cursor.getInt(12));
				order.setTableId(cursor.getInt(13));
				order.setCreateTime(cursor.getLong(14));
				order.setUpdateTime(cursor.getLong(15));
				order.setOrderNo(cursor.getInt(16));
				order.setBusinessDate(cursor.getLong(17));
				order.setDiscountRate(cursor.getString(18));
				order.setDiscountType(cursor.getInt(19));
				order.setDiscountPrice(cursor.getString(20));
				order.setInclusiveTaxName(cursor.getString(21));
				order.setInclusiveTaxPrice(cursor.getString(22));
				order.setInclusiveTaxPercentage(cursor.getString(23));
				order.setAppOrderId(cursor.getInt(24));
				order.setIsTakeAway(cursor.getInt(25));
				return order;
			}
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return order;		
	}
	
	public static Order getUnfinishedOrderAtTable(Tables tables, Long bizDate) {
		Order order = null;
		String sql = "select * from " + TableNames.Order
				+ " where tableId = ? and orderStatus <> ? and businessDate = ? order by id DESC";
		Cursor cursor = null;
		try {
			cursor = SQLExe.getDB().rawQuery(
					sql,
					new String[] { tables.getId() + "",
							ParamConst.ORDER_STATUS_FINISHED + "", String.valueOf(bizDate) });
			int count = cursor.getCount();
			if (count < 1) {
				return order;
			}
			if (cursor.moveToFirst()) {
				order = new Order();
				order.setId(cursor.getInt(0));
				order.setOrderOriginId(cursor.getInt(1));
				order.setUserId(cursor.getInt(2));
				order.setPersons(cursor.getInt(3));
				order.setOrderStatus(cursor.getInt(4));
				order.setSubTotal(cursor.getString(5));
				order.setTaxAmount(cursor.getString(6));
				order.setDiscountAmount(cursor.getString(7));
				order.setTotal(cursor.getString(8));
				order.setSessionStatus(cursor.getInt(9));
				order.setRestId(cursor.getInt(10));
				order.setRevenueId(cursor.getInt(11));
				order.setPlaceId(cursor.getInt(12));
				order.setTableId(cursor.getInt(13));
				order.setCreateTime(cursor.getLong(14));
				order.setUpdateTime(cursor.getLong(15));
				order.setOrderNo(cursor.getInt(16));
				order.setBusinessDate(cursor.getLong(17));
				order.setDiscountRate(cursor.getString(18));
				order.setDiscountType(cursor.getInt(19));
				order.setDiscountPrice(cursor.getString(20));
				order.setInclusiveTaxName(cursor.getString(21));
				order.setInclusiveTaxPrice(cursor.getString(22));
				order.setInclusiveTaxPercentage(cursor.getString(23));
				order.setAppOrderId(cursor.getInt(24));
				order.setIsTakeAway(cursor.getInt(25));
				return order;
			}
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return order;
	}
	
	
	public static Order getUnfinishedOrder(int orderId) {
		Order order = null;
		String sql = "select * from " + TableNames.Order
				+ " where id = ? and orderStatus <> " + ParamConst.ORDER_STATUS_FINISHED + " order by id desc";
		Cursor cursor = null;
		try {
			cursor = SQLExe.getDB().rawQuery(
					sql,
					new String[] { orderId + ""});
			int count = cursor.getCount();
			if (count < 1) {
				return order;
			}
			if (cursor.moveToFirst()) {
				order = new Order();
				order.setId(cursor.getInt(0));
				order.setOrderOriginId(cursor.getInt(1));
				order.setUserId(cursor.getInt(2));
				order.setPersons(cursor.getInt(3));
				order.setOrderStatus(cursor.getInt(4));
				order.setSubTotal(cursor.getString(5));
				order.setTaxAmount(cursor.getString(6));
				order.setDiscountAmount(cursor.getString(7));
				order.setTotal(cursor.getString(8));
				order.setSessionStatus(cursor.getInt(9));
				order.setRestId(cursor.getInt(10));
				order.setRevenueId(cursor.getInt(11));
				order.setPlaceId(cursor.getInt(12));
				order.setTableId(cursor.getInt(13));
				order.setCreateTime(cursor.getLong(14));
				order.setUpdateTime(cursor.getLong(15));
				order.setOrderNo(cursor.getInt(16));
				order.setBusinessDate(cursor.getLong(17));
				order.setDiscountRate(cursor.getString(18));
				order.setDiscountType(cursor.getInt(19));
				order.setDiscountPrice(cursor.getString(20));
				order.setInclusiveTaxName(cursor.getString(21));
				order.setInclusiveTaxPrice(cursor.getString(22));
				order.setInclusiveTaxPercentage(cursor.getString(23));
				order.setAppOrderId(cursor.getInt(24));
				order.setIsTakeAway(cursor.getInt(25));
				return order;
			}
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return order;
	}

	public static ArrayList<Order> getAllFinishedOrders() {
		ArrayList<Order> result = new ArrayList<Order>();
		String sql = "select * from " + TableNames.Order
				+ " where orderStatus == " + ParamConst.ORDER_STATUS_FINISHED + " order by id desc";
		Cursor cursor = null;
		SQLiteDatabase db = SQLExe.getDB();
		try {
			db.beginTransaction();
			cursor = db.rawQuery(sql, new String[] {});
			int count = cursor.getCount();
			if (count < 1) {
				return result;
			}
			Order order = null;
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
					.moveToNext()) {
				order = new Order();
				order.setId(cursor.getInt(0));
				order.setOrderOriginId(cursor.getInt(1));
				order.setUserId(cursor.getInt(2));
				order.setPersons(cursor.getInt(3));
				order.setOrderStatus(cursor.getInt(4));
				order.setSubTotal(cursor.getString(5));
				order.setTaxAmount(cursor.getString(6));
				order.setDiscountAmount(cursor.getString(7));
				order.setTotal(cursor.getString(8));
				order.setSessionStatus(cursor.getInt(9));
				order.setRestId(cursor.getInt(10));
				order.setRevenueId(cursor.getInt(11));
				order.setPlaceId(cursor.getInt(12));
				order.setTableId(cursor.getInt(13));
				order.setCreateTime(cursor.getLong(14));
				order.setUpdateTime(cursor.getLong(15));
				order.setOrderNo(cursor.getInt(16));
				order.setBusinessDate(cursor.getLong(17));
				order.setDiscountRate(cursor.getString(18));
				order.setDiscountType(cursor.getInt(19));
				order.setDiscountPrice(cursor.getString(20));
				order.setInclusiveTaxName(cursor.getString(21));
				order.setInclusiveTaxPrice(cursor.getString(22));
				order.setInclusiveTaxPercentage(cursor.getString(23));
				order.setAppOrderId(cursor.getInt(24));
				order.setIsTakeAway(cursor.getInt(25));
				result.add(order);
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
	
	public static Order getOrder(Integer orderID) {
		Order order = null;
		String sql = "select * from " + TableNames.Order + " where id = ?";
		Cursor cursor = null;
		try {
			cursor = SQLExe.getDB()
					.rawQuery(sql, new String[] { orderID + "" });
			if (cursor.moveToFirst()) {
				order = new Order();
				order.setId(cursor.getInt(0));
				order.setOrderOriginId(cursor.getInt(1));
				order.setUserId(cursor.getInt(2));
				order.setPersons(cursor.getInt(3));
				order.setOrderStatus(cursor.getInt(4));
				order.setSubTotal(cursor.getString(5));
				order.setTaxAmount(cursor.getString(6));
				order.setDiscountAmount(cursor.getString(7));
				order.setTotal(cursor.getString(8));
				order.setSessionStatus(cursor.getInt(9));
				order.setRestId(cursor.getInt(10));
				order.setRevenueId(cursor.getInt(11));
				order.setPlaceId(cursor.getInt(12));
				order.setTableId(cursor.getInt(13));
				order.setCreateTime(cursor.getLong(14));
				order.setUpdateTime(cursor.getLong(15));
				order.setOrderNo(cursor.getInt(16));
				order.setBusinessDate(cursor.getLong(17));
				order.setDiscountRate(cursor.getString(18));
				order.setDiscountType(cursor.getInt(19));
				order.setDiscountPrice(cursor.getString(20));
				order.setInclusiveTaxName(cursor.getString(21));
				order.setInclusiveTaxPrice(cursor.getString(22));
				order.setInclusiveTaxPercentage(cursor.getString(23));
				order.setAppOrderId(cursor.getInt(24));
				order.setIsTakeAway(cursor.getInt(25));
				return order;
			}
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return order;
	}

	public static void deleteOrder(Order order) {
		String sql = "delete from " + TableNames.Order + " where id = ?";
		try {
			SQLExe.getDB().execSQL(sql, new Object[] { order.getId() });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Order getOrderByUnPlay(SessionStatus sessionStatus) {
		String sql = "select * from " + TableNames.Order
				+ "  where sessionStatus = ? and createTime > ? and orderStatus <> " + ParamConst.ORDER_STATUS_FINISHED;
		Order order = null;
		Cursor cursor = null;
		try {
			cursor = SQLExe.getDB().rawQuery(
					sql,
					new String[] { String.valueOf(sessionStatus.getSession_status()),
							String.valueOf(sessionStatus.getTime()) });
			if (cursor.moveToFirst()) {
				order = new Order();
				order.setId(cursor.getInt(0));
				order.setOrderOriginId(cursor.getInt(1));
				order.setUserId(cursor.getInt(2));
				order.setPersons(cursor.getInt(3));
				order.setOrderStatus(cursor.getInt(4));
				order.setSubTotal(cursor.getString(5));
				order.setTaxAmount(cursor.getString(6));
				order.setDiscountAmount(cursor.getString(7));
				order.setTotal(cursor.getString(8));
				order.setSessionStatus(cursor.getInt(9));
				order.setRestId(cursor.getInt(10));
				order.setRevenueId(cursor.getInt(11));
				order.setPlaceId(cursor.getInt(12));
				order.setTableId(cursor.getInt(13));
				order.setCreateTime(cursor.getLong(14));
				order.setUpdateTime(cursor.getLong(15));
				order.setOrderNo(cursor.getInt(16));
				order.setBusinessDate(cursor.getLong(17));
				order.setDiscountRate(cursor.getString(18));
				order.setDiscountType(cursor.getInt(19));
				order.setDiscountPrice(cursor.getString(20));
				order.setInclusiveTaxName(cursor.getString(21));
				order.setInclusiveTaxPrice(cursor.getString(22));
				order.setInclusiveTaxPercentage(cursor.getString(23));
				order.setAppOrderId(cursor.getInt(24));
				order.setIsTakeAway(cursor.getInt(25));
				return order;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return order;
	}
	
	public static List<Order> getOrderListByUnPlay(SessionStatus sessionStatus) {
		String sql = "select * from " + TableNames.Order
				+ "  where sessionStatus = ? and createTime > ? and orderStatus <> " + ParamConst.ORDER_STATUS_FINISHED;
		ArrayList<Order> result = new ArrayList<Order>();
		Cursor cursor = null;
		try {
			cursor = SQLExe.getDB().rawQuery(
					sql,
					new String[] { String.valueOf(sessionStatus.getSession_status()),
							String.valueOf(sessionStatus.getTime()) });
			int count = cursor.getCount();
			if (count < 1) {
				return result;
			}
			Order order = null;
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
					.moveToNext()) {
				order = new Order();
				order.setId(cursor.getInt(0));
				order.setOrderOriginId(cursor.getInt(1));
				order.setUserId(cursor.getInt(2));
				order.setPersons(cursor.getInt(3));
				order.setOrderStatus(cursor.getInt(4));
				order.setSubTotal(cursor.getString(5));
				order.setTaxAmount(cursor.getString(6));
				order.setDiscountAmount(cursor.getString(7));
				order.setTotal(cursor.getString(8));
				order.setSessionStatus(cursor.getInt(9));
				order.setRestId(cursor.getInt(10));
				order.setRevenueId(cursor.getInt(11));
				order.setPlaceId(cursor.getInt(12));
				order.setTableId(cursor.getInt(13));
				order.setCreateTime(cursor.getLong(14));
				order.setUpdateTime(cursor.getLong(15));
				order.setOrderNo(cursor.getInt(16));
				order.setBusinessDate(cursor.getLong(17));
				order.setDiscountRate(cursor.getString(18));
				order.setDiscountType(cursor.getInt(19));
				order.setDiscountPrice(cursor.getString(20));
				order.setInclusiveTaxName(cursor.getString(21));
				order.setInclusiveTaxPrice(cursor.getString(22));
				order.setInclusiveTaxPercentage(cursor.getString(23));
				order.setAppOrderId(cursor.getInt(24));
				order.setIsTakeAway(cursor.getInt(25));
				result.add(order);
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
	
	public static ArrayList<DashboardTotalDetailInfo> getTotalDetaiInfosForOrder(){
		ArrayList<DashboardTotalDetailInfo> totalDetailInfos = new ArrayList<DashboardTotalDetailInfo>();
		Cursor cursor = null;
		SQLiteDatabase db = SQLExe.getDB();
		try {
			db.beginTransaction();
			cursor = db.query(TableNames.Order,
					new String[] { " sum(subTotal)", "sum(taxAmount)", "sum(discountAmount)", "sum(total)", "businessDate" }, "orderStatus = " + ParamConst.ORDER_STATUS_FINISHED,
					new String[] { }, "businessDate", "", "",
					"");
			int count = cursor.getCount();
			if (count < 1) {
				return totalDetailInfos;
			}
			DashboardTotalDetailInfo totalDetailInfo;
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
					.moveToNext()) {
				 totalDetailInfo = new DashboardTotalDetailInfo();
				totalDetailInfo.setSubTotal(cursor.getString(0));
				totalDetailInfo.setTotalTax(cursor.getString(1));
				totalDetailInfo.setTotalDiscount(cursor.getString(2));
				totalDetailInfo.setTotalAmount(cursor.getString(3));
				totalDetailInfo.setBusinessDateStr(cursor.getLong(4));
				totalDetailInfos.add(totalDetailInfo);
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
		return totalDetailInfos;
	
	}
	
	public static int getSumCountBySessionType(int sessionStatus){
		int sumCount = 0;
		String sql = "select count(*) from "+ TableNames.Order + " where sessionStatus = ?";
		Cursor cursor = null;
		try {
			cursor = SQLExe.getDB().rawQuery(sql, new String[]{String.valueOf(sessionStatus)});
			if (cursor.moveToFirst()) {
				sumCount = cursor.getInt(0);
			}
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return sumCount;
	
	}
	
	/*计算流水*/
	public static int getSumCountByBizDate(long bizDate){
		int sumCount = 0;
		String sql = "select max(orderNo) from "+ TableNames.Order + " where businessDate = ?";
		Cursor cursor = null;
		try {
			cursor = SQLExe.getDB().rawQuery(sql, new String[]{String.valueOf(bizDate)});
			if (cursor.moveToFirst()) {
				sumCount = cursor.getInt(0);
			}
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return sumCount;
	
	}
	
	public static void updateOrderStatus( int orderStatus, int id){

		String sql = "update " + TableNames.Order + " set orderStatus = ? where id = ?" ;
		try {
			SQLExe.getDB().execSQL(sql, new Object[] {orderStatus, id});
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}
	
	public static void updateOrderPersions( int persons, int id){

		String sql = "update " + TableNames.Order + " set persons = ? where id = ?" ;
		try {
			SQLExe.getDB().execSQL(sql, new Object[] {persons, id});
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}
	
	public static void deleteAllOrder(){

		String sql = "delete from " + TableNames.Order ;
		try {
			SQLExe.getDB().execSQL(sql, new Object[] { });
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}
}
