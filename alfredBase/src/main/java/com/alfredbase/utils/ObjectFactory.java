package com.alfredbase.utils;

import android.text.TextUtils;

import com.alfredbase.ParamConst;
import com.alfredbase.ParamHelper;
import com.alfredbase.global.CoreData;
import com.alfredbase.javabean.AlipaySettlement;
import com.alfredbase.javabean.BohHoldSettlement;
import com.alfredbase.javabean.CardsSettlement;
import com.alfredbase.javabean.CashInOut;
import com.alfredbase.javabean.ItemDetail;
import com.alfredbase.javabean.ItemHappyHour;
import com.alfredbase.javabean.KotItemDetail;
import com.alfredbase.javabean.KotItemModifier;
import com.alfredbase.javabean.KotSummary;
import com.alfredbase.javabean.LocalDevice;
import com.alfredbase.javabean.Modifier;
import com.alfredbase.javabean.NetsSettlement;
import com.alfredbase.javabean.NonChargableSettlement;
import com.alfredbase.javabean.Order;
import com.alfredbase.javabean.OrderBill;
import com.alfredbase.javabean.OrderDetail;
import com.alfredbase.javabean.OrderDetailTax;
import com.alfredbase.javabean.OrderModifier;
import com.alfredbase.javabean.OrderSplit;
import com.alfredbase.javabean.Payment;
import com.alfredbase.javabean.PaymentSettlement;
import com.alfredbase.javabean.PlaceInfo;
import com.alfredbase.javabean.PrinterTitle;
import com.alfredbase.javabean.ReportDiscount;
import com.alfredbase.javabean.Restaurant;
import com.alfredbase.javabean.RevenueCenter;
import com.alfredbase.javabean.RoundAmount;
import com.alfredbase.javabean.TableInfo;
import com.alfredbase.javabean.Tax;
import com.alfredbase.javabean.User;
import com.alfredbase.javabean.UserOpenDrawerRecord;
import com.alfredbase.javabean.UserTimeSheet;
import com.alfredbase.javabean.VoidSettlement;
import com.alfredbase.javabean.WeixinSettlement;
import com.alfredbase.javabean.model.KotNotification;
import com.alfredbase.javabean.model.PrintOrderItem;
import com.alfredbase.javabean.model.PrintOrderModifier;
import com.alfredbase.javabean.model.SessionStatus;
import com.alfredbase.javabean.temporaryforapp.AppOrder;
import com.alfredbase.javabean.temporaryforapp.AppOrderDetail;
import com.alfredbase.javabean.temporaryforapp.AppOrderDetailTax;
import com.alfredbase.javabean.temporaryforapp.AppOrderModifier;
import com.alfredbase.store.TableNames;
import com.alfredbase.store.sql.AlipaySettlementSQL;
import com.alfredbase.store.sql.BohHoldSettlementSQL;
import com.alfredbase.store.sql.CardsSettlementSQL;
import com.alfredbase.store.sql.CashInOutSQL;
import com.alfredbase.store.sql.CommonSQL;
import com.alfredbase.store.sql.ItemDetailSQL;
import com.alfredbase.store.sql.KotItemDetailSQL;
import com.alfredbase.store.sql.KotItemModifierSQL;
import com.alfredbase.store.sql.KotNotificationSQL;
import com.alfredbase.store.sql.KotSummarySQL;
import com.alfredbase.store.sql.LocalDeviceSQL;
import com.alfredbase.store.sql.NetsSettlementSQL;
import com.alfredbase.store.sql.NonChargableSettlementSQL;
import com.alfredbase.store.sql.OrderBillSQL;
import com.alfredbase.store.sql.OrderDetailSQL;
import com.alfredbase.store.sql.OrderDetailTaxSQL;
import com.alfredbase.store.sql.OrderModifierSQL;
import com.alfredbase.store.sql.OrderSQL;
import com.alfredbase.store.sql.OrderSplitSQL;
import com.alfredbase.store.sql.PaymentSQL;
import com.alfredbase.store.sql.PaymentSettlementSQL;
import com.alfredbase.store.sql.PlaceInfoSQL;
import com.alfredbase.store.sql.ReportDiscountSQL;
import com.alfredbase.store.sql.RestaurantSQL;
import com.alfredbase.store.sql.RevenueCenterSQL;
import com.alfredbase.store.sql.RoundAmountSQL;
import com.alfredbase.store.sql.SettingDataSQL;
import com.alfredbase.store.sql.TableInfoSQL;
import com.alfredbase.store.sql.UserOpenDrawerRecordSQL;
import com.alfredbase.store.sql.VoidSettlementSQL;
import com.alfredbase.store.sql.WeixinSettlementSQL;
import com.alfredbase.store.sql.temporaryforapp.AppOrderDetailTaxSQL;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ObjectFactory {
	private static ObjectFactory instance;
	
	private ObjectFactory() {
	}

	public static ObjectFactory getInstance() {
		if (instance == null)
			instance = new ObjectFactory();
		return instance;
	}
	
	Object lock_order = new Object();

	public Order getOrder(Integer orderOriginId, TableInfo tables,
			RevenueCenter revenueCenter, User user,
			SessionStatus sessionStatus, long businessDate, int orderNOTitle,
			int orderStatus, Tax inclusiveTax){
		return getOrder(orderOriginId, tables, revenueCenter, user, sessionStatus, businessDate, orderNOTitle, orderStatus, inclusiveTax, 0);
	}
	
	public Order getOrder(Integer orderOriginId, TableInfo tables,
			RevenueCenter revenueCenter, User user,
			SessionStatus sessionStatus, long businessDate, int orderNOTitle,
			int orderStatus, Tax inclusiveTax, int appOrderId) {
		
		Order order = null;
		synchronized (lock_order) {
			order = OrderSQL.getUnfinishedOrderAtTable(tables.getPosId(), businessDate);
			if (order == null) {
	
					order = new Order();
					order.setId(CommonSQL.getNextSeq(TableNames.Order));
					order.setOrderOriginId(orderOriginId);
					order.setUserId(user.getId());
					order.setPersons(tables.getPacks());
					order.setOrderStatus(orderStatus);
					order.setDiscountRate(ParamConst.DOUBLE_ZERO);
					order.setSessionStatus(sessionStatus.getSession_status());
					order.setRestId(CoreData.getInstance().getRestaurant().getId());
					order.setRevenueId(revenueCenter.getId());
					order.setPlaceId(tables.getPlacesId());
					order.setTableId(tables.getPosId());
					long time = System.currentTimeMillis();
					order.setCreateTime(time);
					order.setUpdateTime(time);
					order.setBusinessDate(businessDate);
//					order.setOrderNo(order.getId());
					order.setOrderNo(OrderHelper.calculateOrderNo(businessDate));//流水号
					order.setDiscountType(ParamConst.ORDER_DISCOUNT_TYPE_NULL);
					order.setAppOrderId(appOrderId);
					if(inclusiveTax != null){
						order.setInclusiveTaxName(inclusiveTax.getTaxName());
						order.setInclusiveTaxPercentage(inclusiveTax.getTaxPercentage());
					}
					OrderSQL.addOrder(order);
				}
		}
		return order;
	}

	public Order getOrderFromAppOrder(AppOrder appOrder, User user,
									  SessionStatus sessionStatus, RevenueCenter revenueCenter,
									  TableInfo tables, long businessDate, Restaurant restaurant,
									  Tax inclusiveTax, boolean isKiosk) {
		Order order = null;
		if (appOrder != null) {
			synchronized (lock_order) {
				order = OrderSQL.getOrderByAppOrderId(appOrder
						.getId().intValue());
				if (order == null) {
					order = new Order();
					order.setId(CommonSQL.getNextSeq(TableNames.Order));
					order.setOrderOriginId(ParamConst.ORDER_ORIGIN_APP);
					order.setUserId(user.getId());
					order.setPersons(appOrder.getPerson() > 0 ? appOrder.getPerson() : 4);
					if(isKiosk)
						order.setOrderStatus(ParamConst.ORDER_STATUS_FINISHED);
					else
						order.setOrderStatus(ParamConst.ORDER_STATUS_OPEN_IN_POS);
					order.setDiscountRate(ParamConst.DOUBLE_ZERO);
					order.setTaxAmount(appOrder.getTaxAmount());
					order.setSessionStatus(sessionStatus.getSession_status());
					order.setRestId(restaurant.getId());
					order.setRevenueId(revenueCenter.getId());
					order.setPlaceId(tables.getPlacesId());
					order.setTableId(tables.getPosId());
					long time = System.currentTimeMillis();
					order.setCreateTime(time);
					order.setUpdateTime(time);
					order.setBusinessDate(businessDate);
					order.setOrderNo(OrderHelper.calculateOrderNo(businessDate));// 流水号
					order.setDiscountType(ParamConst.ORDER_DISCOUNT_TYPE_NULL);
					order.setAppOrderId(appOrder.getId().intValue());
					order.setTotal(appOrder.getTotal());
					order.setSubTotal(appOrder.getSubTotal());
					order.setOrderRemark(appOrder.getOrderRemark());
					if(appOrder.getEatType() == ParamConst.APP_ORDER_TAKE_AWAY){
						order.setIsTakeAway(ParamConst.TAKE_AWAY);
					}else{
						order.setIsTakeAway(ParamConst.NOT_TAKE_AWAY);
					}
					if(inclusiveTax != null){
						order.setInclusiveTaxName(inclusiveTax.getTaxName());
						order.setInclusiveTaxPercentage(inclusiveTax.getTaxPercentage());
					}
					order.setDiscountAmount("0.00");
					OrderHelper.setOrderInclusiveTaxPrice(order);
					OrderSQL.update(order);
				}
			}
		}
		return order;
	}

	Object lock_orderDetail = new Object();
	public OrderDetail getOrderDetail(Order order, ItemDetail itemDetail,
			int groupId) {
		OrderDetail orderDetail = new OrderDetail();
		synchronized (lock_orderDetail) {
			long time = System.currentTimeMillis();
			orderDetail.setCreateTime(time);
			orderDetail.setUpdateTime(time);
			orderDetail.setId(CommonSQL.getNextSeq(TableNames.OrderDetail));
			orderDetail.setOrderId(order.getId());
			orderDetail.setOrderOriginId(ParamConst.ORDER_ORIGIN_POS);
			orderDetail.setUserId(order.getUserId());
			orderDetail.setItemId(itemDetail.getId());
			orderDetail.setItemName(itemDetail.getItemName());
			orderDetail.setItemNum(1);
			orderDetail.setOrderDetailStatus(ParamConst.ORDERDETAIL_STATUS_ADDED);
			orderDetail.setOrderDetailType(ParamConst.ORDERDETAIL_TYPE_GENERAL);
			orderDetail.setReason("");
			orderDetail.setPrintStatus(ParamConst.PRINT_STATUS_UNDONE);
			orderDetail.setItemPrice(itemDetail.getPrice());
			orderDetail.setTaxPrice(ParamConst.DOUBLE_ZERO);
			orderDetail.setFromOrderDetailId(0);
			orderDetail.setIsFree(ParamConst.NOT_FREE);
			orderDetail.setIsItemDiscount(itemDetail.getIsDiscount());
			orderDetail.setAppOrderDetailId(0);
			if (itemDetail.getItemType() == 2) {
				orderDetail.setIsOpenItem(1);
			}
			orderDetail.setGroupId(groupId);
			orderDetail.setIsTakeAway(ParamConst.NOT_TAKE_AWAY);
			orderDetail.setMainCategoryId(itemDetail.getItemMainCategoryId().intValue());
			if (itemDetail.getItemType() == 3)
				orderDetail.setIsSet(1);
		}
		return orderDetail;
	}

	public OrderDetail getOrderDetailFromTempAppOrderDetail(Order order,
															AppOrderDetail appOrderDetail) {
		OrderDetail orderDetail;
		synchronized (lock_orderDetail) {
			long time = System.currentTimeMillis();
			orderDetail = OrderDetailSQL.getOrderDetailByAppOrderDetailId(appOrderDetail.getId());
			if(orderDetail == null) {
				orderDetail = new OrderDetail();
				orderDetail.setCreateTime(time);
				orderDetail.setUpdateTime(time);
				orderDetail.setId(CommonSQL.getNextSeq(TableNames.OrderDetail));
				orderDetail.setOrderId(order.getId());
				orderDetail.setOrderOriginId(ParamConst.ORDER_ORIGIN_APP);
				orderDetail.setUserId(order.getUserId());
				orderDetail.setItemId(appOrderDetail.getItemId().intValue());
				orderDetail.setItemName(appOrderDetail.getItemName());
				orderDetail.setItemNum(appOrderDetail.getItemNum().intValue());
				orderDetail
						.setOrderDetailStatus(ParamConst.ORDERDETAIL_STATUS_ADDED);
				orderDetail.setOrderDetailType(ParamConst.ORDERDETAIL_TYPE_GENERAL);
				orderDetail.setReason("");
				orderDetail.setPrintStatus(ParamConst.PRINT_STATUS_UNDONE);
				orderDetail.setItemPrice(appOrderDetail.getItemPrice());
				String taxPrice = AppOrderDetailTaxSQL.getAppOrderDetailTaxSumByAppOrderDetailId(appOrderDetail.getId().intValue());
				orderDetail.setTaxPrice(taxPrice);
				orderDetail.setFromOrderDetailId(0);
				orderDetail.setIsFree(ParamConst.NOT_FREE);
				orderDetail.setIsItemDiscount(1);
				orderDetail.setRealPrice(appOrderDetail.getTotalItemPrice());
				orderDetail.setGroupId(0);
				orderDetail.setIsTakeAway(ParamConst.NOT_TAKE_AWAY);
				ItemDetail itemDetail = ItemDetailSQL.getItemDetailById(appOrderDetail.getItemId().intValue());
				orderDetail.setMainCategoryId(itemDetail.getItemMainCategoryId().intValue());
				orderDetail.setAppOrderDetailId(appOrderDetail.getId());
			}
		}
		return orderDetail;
	}

	public OrderDetail getOrderDetailForTransferTable(Order order,
			OrderDetail oldOrderDetail) {
		OrderDetail orderDetail = new OrderDetail();
		orderDetail.setId(CommonSQL.getNextSeq(TableNames.OrderDetail));
		orderDetail.setOrderId(order.getId());
		orderDetail.setOrderOriginId(ParamConst.ORDER_ORIGIN_POS);
		orderDetail.setUserId(oldOrderDetail.getUserId());
		orderDetail.setItemName(oldOrderDetail.getItemName());
		orderDetail.setItemId(oldOrderDetail.getItemId());
		orderDetail.setItemNum(oldOrderDetail.getItemNum());
		orderDetail.setOrderDetailStatus(oldOrderDetail.getOrderDetailStatus());
		orderDetail.setOrderDetailType(oldOrderDetail.getOrderDetailType());
		orderDetail.setReason("");
		orderDetail.setPrintStatus(oldOrderDetail.getPrintStatus());
		orderDetail.setItemPrice(oldOrderDetail.getItemPrice());
		orderDetail.setTaxPrice(ParamConst.DOUBLE_ZERO);
//		orderDetail.setDiscountPrice(oldOrderDetail.getDiscountPrice());
//		orderDetail.setModifierPrice(oldOrderDetail.getModifierPrice());
//		orderDetail.setRealPrice(oldOrderDetail.getRealPrice());
		long time = System.currentTimeMillis();
		orderDetail.setCreateTime(time);
		orderDetail.setUpdateTime(time);
		orderDetail.setFromOrderDetailId(0);
		orderDetail.setIsFree(ParamConst.NOT_FREE);
		orderDetail.setIsOpenItem(oldOrderDetail.getIsOpenItem());
		orderDetail.setGroupId(ParamConst.ORDERDETAIL_DEFAULT_GROUP_ID);
		orderDetail.setIsTakeAway(oldOrderDetail.getIsTakeAway());
		orderDetail.setIsItemDiscount(oldOrderDetail.getIsItemDiscount());
		orderDetail.setAppOrderDetailId(oldOrderDetail.getAppOrderDetailId());
		orderDetail.setMainCategoryId(oldOrderDetail.getMainCategoryId());

		return orderDetail;
	}

	public OrderDetail createOrderDetailForWaiter(Order order,
			ItemDetail itemDetail, int groupId, User user) {
		long time = Math.abs(System.currentTimeMillis());
		int orderDetailId = CommonSQL.getNextSeq(TableNames.OrderDetail);
		if (orderDetailId < 1000000) {
			orderDetailId += 1000000;
		}
		OrderDetail orderDetail = new OrderDetail();
		orderDetail.setId(orderDetailId);
		orderDetail.setOrderId(order.getId());
		orderDetail.setOrderOriginId(ParamConst.ORDER_ORIGIN_WAITER);
		orderDetail.setUserId(user.getId());
		orderDetail.setItemId(itemDetail.getId());
		orderDetail.setItemName(itemDetail.getItemName());
		orderDetail.setItemNum(1);
		orderDetail
				.setOrderDetailStatus(ParamConst.ORDERDETAIL_STATUS_WAITER_ADD);
		orderDetail.setOrderDetailType(ParamConst.ORDERDETAIL_TYPE_GENERAL);
		orderDetail.setReason("");
		orderDetail.setDiscountPrice(ParamConst.DOUBLE_ZERO);
		orderDetail.setPrintStatus(ParamConst.PRINT_STATUS_UNDONE);
		orderDetail.setItemPrice(itemDetail.getPrice());
		orderDetail.setTaxPrice(ParamConst.DOUBLE_ZERO);
		orderDetail.setCreateTime(time);
		orderDetail.setUpdateTime(time);
		orderDetail.setFromOrderDetailId(0);
		orderDetail.setIsFree(ParamConst.NOT_FREE);
		orderDetail.setIsItemDiscount(itemDetail.getIsDiscount());
		if (itemDetail.getItemType() == 2) {
			orderDetail.setIsOpenItem(1);
		}
		orderDetail.setGroupId(groupId);
		orderDetail.setIsTakeAway(ParamConst.NOT_TAKE_AWAY);
		orderDetail.setAppOrderDetailId(0);
		orderDetail.setMainCategoryId(itemDetail.getItemMainCategoryId().intValue());
		return orderDetail;
	}


	Object lock_table = new Object();

	public TableInfo addNewTable(String imageName, int restaurantId, int revenueId, int placeId, int width, int height){
		synchronized (lock_table){
			TableInfo newTable = new TableInfo();
			newTable.setPosId(CommonSQL.getNextSeq(TableNames.TableInfo));
			newTable.setImageName(imageName);
			newTable.setRestaurantId(restaurantId);
			newTable.setRevenueId(revenueId);
			newTable.setPlacesId(placeId);
			newTable.setStatus(ParamConst.TABLE_STATUS_IDLE);
			newTable.setShape(3);
			newTable.setIsDecorate(0);
			newTable.setUnionId(restaurantId + "_" + revenueId + "_" + newTable.getPosId());
			newTable.setIsActive(ParamConst.ACTIVE_NOMAL);
			newTable.setResolution(width);
			newTable.setResolutionWidth(width);
			newTable.setResolutionHeight(height);
			newTable.setName("table" + newTable.getPosId().intValue());
//			if(imageName.startsWith("table_1"))
//				newTable.setPacks(1);
//			else if(imageName.startsWith("table_2"))
//				newTable.setPacks(2);
//			else if(imageName.startsWith("table_4"))
//				newTable.setPacks(4);
//			else if(imageName.startsWith("table_6"))
//				newTable.setPacks(6);
//			else
//				newTable.setPacks(8);

			newTable.setRotate(0);
			long time = System.currentTimeMillis();
			newTable.setCreateTime(time);
			newTable.setUpdateTime(time);
			TableInfoSQL.addTables(newTable);
			return newTable;
		}
	}

	Object lock_place = new Object();

	public PlaceInfo addNewPlace(int restaurantId, int revenueId, String placeName){
		synchronized (lock_place){
			PlaceInfo placeInfo = new PlaceInfo();
			placeInfo.setId(CommonSQL.getNextSeq(TableNames.PlaceInfo));
			placeInfo.setIsActive(ParamConst.ACTIVE_NOMAL);
			placeInfo.setRestaurantId(restaurantId);
			placeInfo.setRevenueId(revenueId);
			placeInfo.setPlaceDescription("");
			placeInfo.setPlaceName(placeName);
			placeInfo.setUnionId(restaurantId+"_"+revenueId+"_"+placeInfo.getId());
			PlaceInfoSQL.addPlaceInfo(placeInfo);
			return placeInfo;
		}
	}

	Object lock_getRoundAmount = new Object();
	public RoundAmount getRoundAmount(Order order, OrderBill orderBill, BigDecimal roundBeforePrice, String roundType) {
		RoundAmount roundAmount = null;
		synchronized (lock_getRoundAmount) {	
			roundAmount = RoundAmountSQL.getRoundAmountByOrderAndBill(order,orderBill);
			long time = System.currentTimeMillis();
			if (roundAmount == null) {
					roundAmount = new RoundAmount();
					roundAmount.setId(CommonSQL.getNextSeq(TableNames.RoundAmount));
					roundAmount.setOrderId(order.getId());
					roundAmount.setBillNo(orderBill.getBillNo());
					BigDecimal roundAlfterPrice = RoundUtil.getPriceAfterRound(roundType, roundBeforePrice);
					BigDecimal roundBalancePrice = BH.sub(roundAlfterPrice, roundBeforePrice, false);
					roundAmount.setRoundBeforePrice(roundBeforePrice.toString());
					roundAmount.setRoundAlfterPrice(roundAlfterPrice.toString());
					roundAmount.setRoundBalancePrice(Double.valueOf(roundBalancePrice
							.toString()));
					roundAmount.setRestId(order.getRestId());
					roundAmount.setRevenueId(order.getRevenueId());
					roundAmount.setTableId(order.getTableId());
					roundAmount.setBusinessDate(order.getBusinessDate());
					roundAmount.setCreateTime(time);
					roundAmount.setUpdateTime(time);
					roundAmount.setOrderSplitId(0);
					RoundAmountSQL.update(roundAmount);
			}else {
				BigDecimal roundAlfterPrice = RoundUtil.getPriceAfterRound(roundType, roundBeforePrice);
				BigDecimal roundBalancePrice = BH.sub(roundAlfterPrice, roundBeforePrice, false);
				roundAmount.setRoundBeforePrice(roundBeforePrice.toString());
				roundAmount.setRoundAlfterPrice(roundAlfterPrice.toString());
				roundAmount.setRoundBalancePrice(Double.valueOf(roundBalancePrice
						.toString()));
				roundAmount.setUpdateTime(time);
				RoundAmountSQL.update(roundAmount);
			}
		}
		return roundAmount;
		}
	
	public RoundAmount getRoundAmountByOrderSplit(OrderSplit orderSplit, OrderBill orderBill, BigDecimal roundBeforePrice, String roundType, long businessDate) {
		RoundAmount roundAmount = null;
		synchronized (lock_getRoundAmount) {	
			roundAmount = RoundAmountSQL.getRoundAmountByOrderSplitAndBill(orderSplit,orderBill);
			long time = System.currentTimeMillis();
			if (roundAmount == null) {
					roundAmount = new RoundAmount();
					roundAmount.setId(CommonSQL.getNextSeq(TableNames.RoundAmount));
					roundAmount.setOrderId(orderSplit.getOrderId());
					roundAmount.setOrderSplitId(orderSplit.getId());
					roundAmount.setBillNo(orderBill.getBillNo());
					BigDecimal roundAlfterPrice = RoundUtil.getPriceAfterRound(roundType, roundBeforePrice);
					BigDecimal roundBalancePrice = BH.sub(roundAlfterPrice, roundBeforePrice, false);
					roundAmount.setRoundBeforePrice(roundBeforePrice.toString());
					roundAmount.setRoundAlfterPrice(roundAlfterPrice.toString());
					roundAmount.setRoundBalancePrice(Double.valueOf(roundBalancePrice
							.toString()));
					roundAmount.setRestId(orderSplit.getRestId());
					roundAmount.setRevenueId(orderSplit.getRevenueId());
					roundAmount.setTableId(orderSplit.getTableId());
					roundAmount.setBusinessDate(businessDate);
					roundAmount.setCreateTime(time);
					roundAmount.setUpdateTime(time);
					RoundAmountSQL.update(roundAmount);
			}else {
				BigDecimal roundAlfterPrice = RoundUtil.getPriceAfterRound(roundType, roundBeforePrice);
				BigDecimal roundBalancePrice = BH.sub(roundAlfterPrice,roundBeforePrice, false);
				roundAmount.setRoundBeforePrice(roundBeforePrice.toString());
				roundAmount.setRoundAlfterPrice(roundAlfterPrice.toString());
				roundAmount.setRoundBalancePrice(Double.valueOf(roundBalancePrice
						.toString()));
				roundAmount.setUpdateTime(time);
				RoundAmountSQL.update(roundAmount);
			}
		}
		return roundAmount;
		}

	private static BigDecimal round(String roundType,
			BigDecimal roundBeforePrice) {
		if (roundType == null) {
			return roundBeforePrice;
		}
		if (roundType.equalsIgnoreCase(ParamConst.ROUND_10CENTS)) {
			DecimalFormat doubleFormat = new DecimalFormat("0");
			BigDecimal bigDecimal = BH.div(roundBeforePrice, BH.getBD("0.1"),
					false);
			return BH.mul(BH.getBD(doubleFormat.format(bigDecimal)),
					BH.getBD("0.1"), true);
		} else if (roundType.equalsIgnoreCase(
				ParamConst.ROUND_1DOLLAR)) {
			DecimalFormat doubleFormat = new DecimalFormat("0");
			BigDecimal bigDecimal = BH.div(roundBeforePrice, BH.getBD("1.0"),
					false);
			return BH.mul(BH.getBD(doubleFormat.format(bigDecimal)),
					BH.getBD("1.0"), true);
		} else if (roundType.equalsIgnoreCase(
				ParamConst.ROUND_5CENTS)) {
			DecimalFormat doubleFormat = new DecimalFormat("0");
			BigDecimal bigDecimal = BH.div(roundBeforePrice, BH.getBD("0.05"),
					false);
			return BH.mul(BH.getBD(doubleFormat.format(bigDecimal)),
					BH.getBD("0.05"), true);
		} else if (roundType.equalsIgnoreCase(
				ParamConst.ROUND_10CENTS_DOWN)) {
//			DecimalFormat doubleFormat = new DecimalFormat("0");
			BigDecimal bigDecimal = BH.div(roundBeforePrice, BH.getBD("0.1"), 
					false);
			return BH.mul(BH.getBD(bigDecimal.setScale(0, BigDecimal.ROUND_DOWN).toString()),
					BH.getBD("0.1"), true);
		} else if (roundType.equalsIgnoreCase(
				ParamConst.ROUND_5CENTS_DOWN)) {
			BigDecimal bigDecimal = BH.div(roundBeforePrice, BH.getBD("0.05"), 
					false);
			return BH.mul(BH.getBD(bigDecimal.setScale(0, BigDecimal.ROUND_DOWN).toString()),
					BH.getBD("0.05"), true);
		} else if (roundType.equalsIgnoreCase(
				ParamConst.ROUND_1DOLLAR_DOWN)) {
			BigDecimal bigDecimal = BH.div(roundBeforePrice, BH.getBD("0.1"), 
					false);
			return BH.mul(BH.getBD(bigDecimal.setScale(0, BigDecimal.ROUND_DOWN).toString()),
					BH.getBD("1.0"), true);
		} else{
			return roundBeforePrice;
		}

	}

	//bob add:thread safe
	Object lock_free_order_detail = new Object();
	public OrderDetail getFreeOrderDetail(Order order,
			OrderDetail fromOrderDetail, ItemDetail itemDetail,
			ItemHappyHour itemHappyHour) {
		
		OrderDetail orderDetail= null;
		synchronized (lock_free_order_detail) {		
			orderDetail = OrderDetailSQL.getOrderDetail(order.getId(),
					fromOrderDetail);
			if (orderDetail == null) {
				orderDetail = new OrderDetail();
				orderDetail.setId(CommonSQL.getNextSeq(TableNames.OrderDetail));
				orderDetail.setOrderId(order.getId());
				orderDetail.setOrderOriginId(fromOrderDetail.getOrderOriginId());
				orderDetail.setUserId(order.getUserId());
				orderDetail.setItemId(itemDetail.getId());
				orderDetail.setItemName(itemDetail.getItemName());
				orderDetail.setItemNum(itemHappyHour.getFreeNum()
						* fromOrderDetail.getItemNum());
				orderDetail.setOrderDetailStatus(fromOrderDetail
						.getOrderDetailStatus());
				orderDetail
						.setOrderDetailType(fromOrderDetail.getOrderDetailType());
				orderDetail.setReason("");
				orderDetail.setPrintStatus(ParamConst.PRINT_STATUS_UNDONE);
				orderDetail.setItemPrice(ParamConst.DOUBLE_ZERO);
				orderDetail.setTaxPrice(ParamConst.DOUBLE_ZERO);
				orderDetail.setDiscountPrice(ParamConst.DOUBLE_ZERO);
				orderDetail
						.setDiscountType(ParamConst.ORDERDETAIL_DISCOUNT_TYPE_NULL);
				orderDetail.setDiscountRate(ParamConst.DOUBLE_ZERO);
				long time = System.currentTimeMillis();
				orderDetail.setCreateTime(time);
				orderDetail.setUpdateTime(time);
				orderDetail.setFromOrderDetailId(fromOrderDetail.getId());
				orderDetail.setIsFree(ParamConst.FREE);
				orderDetail.setGroupId(fromOrderDetail.getGroupId());
	
				orderDetail.setModifierPrice(ParamConst.DOUBLE_ZERO);
				orderDetail.setRealPrice(ParamConst.DOUBLE_ZERO);
				orderDetail.setOrderSplitId(null);
				orderDetail.setIsTakeAway(ParamConst.NOT_TAKE_AWAY);
				orderDetail.setAppOrderDetailId(0);
				orderDetail.setMainCategoryId(itemDetail.getItemMainCategoryId().intValue());
			} else {
				orderDetail.setItemNum(itemHappyHour.getFreeNum()
						* fromOrderDetail.getItemNum());
			}
			OrderDetailSQL.updateOrderDetail(orderDetail);
		}
		return orderDetail;
	}

	//bob add:thread safe
	Object lock_free_order_detail_for_waiter = new Object();
	
	public OrderDetail getFreeOrderDetailForWaiter(Order order,
			OrderDetail fromOrderDetail, ItemDetail itemDetail,
			ItemHappyHour itemHappyHour) {
		
		OrderDetail orderDetail = null;
		synchronized(lock_free_order_detail_for_waiter) {
			orderDetail = OrderDetailSQL.getOrderDetail(order.getId(),
					fromOrderDetail);
			if (orderDetail == null) {
				int orderDetailId = CommonSQL.getNextSeq(TableNames.OrderDetail);
				if (orderDetailId < 1000000) {
					orderDetailId += 1000000;
				}
				orderDetail = new OrderDetail();
				orderDetail.setId(orderDetailId);
				orderDetail.setOrderId(order.getId());
				orderDetail.setOrderOriginId(ParamConst.ORDER_ORIGIN_WAITER);
				orderDetail.setUserId(order.getUserId());
				orderDetail.setItemName(itemDetail.getItemName());
				orderDetail.setItemId(itemDetail.getId());
				orderDetail.setItemNum(itemHappyHour.getFreeNum()
						* fromOrderDetail.getItemNum());
				orderDetail
						.setOrderDetailStatus(ParamConst.ORDERDETAIL_STATUS_WAITER_CREATE);
				orderDetail.setOrderDetailType(ParamConst.ORDERDETAIL_TYPE_GENERAL);
				orderDetail.setReason("");
				orderDetail.setPrintStatus(ParamConst.PRINT_STATUS_UNDONE);
				orderDetail.setItemPrice(ParamConst.DOUBLE_ZERO);
				orderDetail.setTaxPrice(ParamConst.DOUBLE_ZERO);
				orderDetail.setDiscountPrice(ParamConst.DOUBLE_ZERO);
				orderDetail
						.setDiscountType(ParamConst.ORDERDETAIL_DISCOUNT_TYPE_NULL);
				orderDetail.setDiscountRate(ParamConst.DOUBLE_ZERO);
				long time = System.currentTimeMillis();
				orderDetail.setCreateTime(time);
				orderDetail.setUpdateTime(time);
				orderDetail.setFromOrderDetailId(fromOrderDetail.getId());
				orderDetail.setIsFree(ParamConst.FREE);
				orderDetail.setGroupId(fromOrderDetail.getGroupId());
	
				orderDetail.setModifierPrice(ParamConst.DOUBLE_ZERO);
				orderDetail.setRealPrice(ParamConst.DOUBLE_ZERO);
				orderDetail.setIsTakeAway(ParamConst.NOT_TAKE_AWAY);
				orderDetail.setAppOrderDetailId(0);
				orderDetail.setMainCategoryId(itemDetail.getItemMainCategoryId().intValue());
			} else {
				orderDetail.setItemNum(itemHappyHour.getFreeNum()
						* fromOrderDetail.getItemNum());
			}
			OrderDetailSQL.updateOrderDetail(orderDetail);
		}
		return orderDetail;
	}

	Object lock_order_modifier = new Object();
	//bob: only call from main POS. not need threadsafe
	public OrderModifier getOrderModifier(Order order, OrderDetail orderDetail,
			Modifier modifier, int printerId) {
		OrderModifier orderModifier = new OrderModifier();
		synchronized (lock_order_modifier) {
			orderModifier.setId(CommonSQL.getNextSeq(TableNames.OrderModifier));
			orderModifier.setOrderId(order.getId());
			orderModifier.setOrderDetailId(orderDetail.getId());
			orderModifier.setOrderOriginId(ParamConst.ORDER_ORIGIN_POS);
			orderModifier.setUserId(order.getUserId());
			orderModifier.setItemId(orderDetail.getItemId());
			orderModifier.setModifierId(modifier.getId());
			orderModifier.setModifierNum(modifier.getQty() * orderDetail.getItemNum().intValue());
			orderModifier.setStatus(ParamConst.ORDER_MODIFIER_STATUS_NORMAL);
			orderModifier.setModifierPrice(BH.mul(BH.getBD(modifier.getPrice()), BH.getBD(modifier.getQty() * orderDetail.getItemNum().intValue()), false).toString());
			long time = System.currentTimeMillis();
			orderModifier.setCreateTime(time);
			orderModifier.setUpdateTime(time);
			orderModifier.setPrinterId(printerId);
			orderModifier.setModifierItemPrice(modifier.getPrice());
		}
		return orderModifier;
	}


	public OrderModifier getOrderModifierFromTempAppOrderModifier(Order order,
																  OrderDetail orderDetail, int printerId,
																  AppOrderModifier appOrderModifier) {
		OrderModifier orderModifier = new OrderModifier();
		synchronized (lock_order_modifier) {
			orderModifier.setId(CommonSQL.getNextSeq(TableNames.OrderModifier));
			orderModifier.setOrderId(order.getId());
			orderModifier.setOrderDetailId(orderDetail.getId());
			orderModifier.setOrderOriginId(ParamConst.ORDER_ORIGIN_POS);
			orderModifier.setUserId(order.getUserId());
			orderModifier.setItemId(orderDetail.getItemId());
			orderModifier.setModifierId(appOrderModifier.getModifierId()
					.intValue());
			orderModifier.setModifierNum(appOrderModifier.getModifierNum()
					.intValue());
			orderModifier.setStatus(ParamConst.ORDER_MODIFIER_STATUS_NORMAL);
			orderModifier.setModifierPrice(appOrderModifier
					.getModifierPrice());
			long time = System.currentTimeMillis();
			orderModifier.setCreateTime(time);
			orderModifier.setUpdateTime(time);
			orderModifier.setPrinterId(printerId);
			orderModifier.setModifierItemPrice(appOrderModifier
					.getModifierPrice());
			OrderModifierSQL
					.addOrderModifierForDiner(orderModifier);
		}
		return orderModifier;
	}

	//bob add:thread safe
	Object lock_get_order_bill = new Object();
	
	public OrderBill getOrderBill(Order order, RevenueCenter revenueCenter) {
		
		OrderBill orderBill = null;
		synchronized(lock_get_order_bill) {
			orderBill = OrderBillSQL.getOrderBillByOrder(order);
			if (orderBill == null) {
				orderBill = new OrderBill();
				orderBill.setId(CommonSQL.getNextSeq(TableNames.OrderBill));
				orderBill.setBillNo(RevenueCenterSQL.getBillNoFromRevenueCenter(revenueCenter.getId()));
				orderBill.setOrderId(order.getId());
				orderBill.setOrderSplitId(0);// TODO
				orderBill.setType(ParamConst.BILL_TYPE_UN_SPLIT);// TODO
				orderBill.setRestaurantId(CoreData.getInstance().getRestaurant()
						.getId());
				orderBill.setRevenueId(revenueCenter.getId());
				orderBill.setUserId(order.getUserId());
				long time = System.currentTimeMillis();
				orderBill.setCreateTime(time);
				orderBill.setUpdateTime(time);
				OrderBillSQL.add(orderBill);
			}
		}
		return orderBill;
	}
	
public OrderBill getOrderBillByOrderSplit(OrderSplit orderSplit, RevenueCenter revenueCenter) {
		
		OrderBill orderBill = null;
		synchronized(lock_get_order_bill) {
			orderBill = OrderBillSQL.getOrderBillByOrderSplit(orderSplit);
			if (orderBill == null) {
				orderBill = new OrderBill();
				orderBill.setId(CommonSQL.getNextSeq(TableNames.OrderBill));
				orderBill.setBillNo(RevenueCenterSQL.getBillNoFromRevenueCenter(revenueCenter.getId()));
				orderBill.setOrderId(orderSplit.getOrderId());
				orderBill.setOrderSplitId(orderSplit.getId());
				orderBill.setType(ParamConst.BILL_TYPE_SPLIT);
				orderBill.setRestaurantId(CoreData.getInstance().getRestaurant()
						.getId());
				orderBill.setRevenueId(revenueCenter.getId());
				orderBill.setUserId(orderSplit.getUserId());
				long time = System.currentTimeMillis();
				orderBill.setCreateTime(time);
				orderBill.setUpdateTime(time);
				OrderBillSQL.add(orderBill);
			}
		}
		return orderBill;
	}

	//bob add:thread safe
	Object lock_get_order_detail_tax = new Object();
	
	public OrderDetailTax getOrderDetailTax(Order order,
			OrderDetail orderDetail, Tax tax, int indexId) {
		OrderDetailTax orderDetailTax = null;
		synchronized (lock_get_order_detail_tax) {
			orderDetailTax = OrderDetailTaxSQL.getOrderDetailTax(
					order, orderDetail, tax);
			if (orderDetailTax == null) {
	
				orderDetailTax = new OrderDetailTax();
				if (orderDetail.getOrderDetailStatus() == ParamConst.ORDERDETAIL_STATUS_WAITER_CREATE) {
					int orderDetailTaxId = CommonSQL
							.getNextSeq(TableNames.OrderDetailTax);
					if (orderDetailTaxId < 1000000) {
						orderDetailTaxId += 1000000;
					}
					orderDetailTax.setId(orderDetailTaxId);
				} else {
					orderDetailTax.setId(CommonSQL
							.getNextSeq(TableNames.OrderDetailTax));
				}
				orderDetailTax.setOrderId(order.getId());
				orderDetailTax.setOrderDetailId(orderDetail.getId());
				orderDetailTax.setTaxId(tax.getId());
				orderDetailTax.setTaxName(tax.getTaxName());
				orderDetailTax.setTaxPercentage(tax.getTaxPercentage());
				orderDetailTax.setTaxType(tax.getTaxType());
				long time = System.currentTimeMillis();
				orderDetailTax.setCreateTime(time);
				orderDetailTax.setUpdateTime(time);
				orderDetailTax.setIndexId(indexId);
				orderDetailTax.setOrderSplitId(orderDetail.getOrderSplitId());
				orderDetailTax.setIsActive(ParamConst.ACTIVE_NOMAL);
				OrderDetailTaxSQL.updateOrderDetailTax(orderDetailTax);
			}
		}
		return orderDetailTax;
	}

	public OrderDetailTax getOrderDetailTaxByOnline(Order order,
													OrderDetail orderDetail, AppOrderDetailTax appOrderDetailTax, int indexId) {
		OrderDetailTax orderDetailTax = null;
		synchronized (lock_get_order_detail_tax) {
			orderDetailTax = OrderDetailTaxSQL.getOrderDetailTaxId(
					order.getId(), orderDetail.getId(), appOrderDetailTax.getTaxId());
			if (orderDetailTax == null) {
				orderDetailTax = new OrderDetailTax();
				orderDetailTax.setId(CommonSQL
						.getNextSeq(TableNames.OrderDetailTax));
			}
				orderDetailTax.setOrderId(order.getId());
				orderDetailTax.setOrderDetailId(orderDetail.getId());
				orderDetailTax.setTaxId(appOrderDetailTax.getTaxId());
				orderDetailTax.setTaxName(appOrderDetailTax.getTaxName());
				orderDetailTax.setTaxPercentage(appOrderDetailTax.getTaxPercentage());
				orderDetailTax.setTaxType(appOrderDetailTax.getTaxType());
				long time = System.currentTimeMillis();
				orderDetailTax.setCreateTime(time);
				orderDetailTax.setUpdateTime(time);
				orderDetailTax.setIndexId(indexId);
				orderDetailTax.setOrderSplitId(orderDetail.getOrderSplitId());
				orderDetailTax.setIsActive(ParamConst.ACTIVE_NOMAL);
				orderDetailTax.setTaxPrice(appOrderDetailTax.getTaxPrice());
				OrderDetailTaxSQL.updateOrderDetailTax(orderDetailTax);
		}
		return orderDetailTax;
	}

	/**
	 * 特殊的调用方式 update by Alex
	 * 
	 * @param order, orderBill 给不拆单的使用
	 * @return
	 */
	//bob add:thread safe
	Object lock_get_payment = new Object();	
	public Payment getPayment(Order order, OrderBill orderBill) {
		
		Payment payment = null;
		synchronized(lock_get_payment) {
			payment = PaymentSQL.getPaymentByOrderId(order.getId());
	
			long time = System.currentTimeMillis();
			if (payment == null) {
				payment = new Payment();
				payment.setId(CommonSQL.getNextSeq(TableNames.Payment));
				payment.setCreateTime(time);
				payment.setOrderSplitId(0); // 不拆单 默认的是0
				payment.setType(ParamConst.BILL_TYPE_UN_SPLIT); // 是否拆单
				payment.setOrderId(order.getId());
				payment.setBusinessDate(order.getBusinessDate());
				payment.setRestaurantId(CoreData.getInstance().getRestaurant().getId());
				payment.setRevenueId(order.getRevenueId());
				payment.setUserId(order.getUserId());
			}
			payment.setBillNo(orderBill.getBillNo());
			payment.setPaymentAmount(order.getTotal()); // 不拆单 填入 Order信息
			payment.setTaxAmount(order.getTaxAmount()); // 不拆单 填入 Order信息
			payment.setDiscountAmount(order.getDiscountAmount()); // 不拆单 填入 Order信息
			payment.setUpdateTime(time);
			PaymentSQL.addPayment(payment);
		}
		return payment;
	}
	
	
	/**
	 * 特殊的调用方式 add by Alex
	 * 
	 * @param businessDate, orderSplit, orderBill 给拆单的使用
	 * @return
	 */
	public Payment getPaymentByOrderSplit(long businessDate ,OrderSplit orderSplit, OrderBill orderBill) {
		
		Payment payment = null;
		synchronized(lock_get_payment) {
			payment = PaymentSQL.getPaymentByOrderSplitId(orderSplit.getId());
	
			long time = System.currentTimeMillis();
			if (payment == null) {
				payment = new Payment();
				payment.setId(CommonSQL.getNextSeq(TableNames.Payment));
				payment.setCreateTime(time);
			}
			payment.setBillNo(orderBill.getBillNo());
			payment.setOrderId(orderSplit.getOrderId());
			payment.setOrderSplitId(orderSplit.getId());
			payment.setBusinessDate(businessDate);
			payment.setType(ParamConst.BILL_TYPE_SPLIT);
			payment.setRestaurantId(CoreData.getInstance().getRestaurant().getId());
			payment.setRevenueId(orderSplit.getRevenueId());
			payment.setUserId(orderSplit.getUserId());
			payment.setPaymentAmount(orderSplit.getTotal());
			payment.setTaxAmount(orderSplit.getTaxAmount());
			payment.setDiscountAmount(orderSplit.getDiscountAmount());
			payment.setUpdateTime(time);
			PaymentSQL.addPayment(payment);
		}
		return payment;
	}

	//bob add:thread safe
	Object lock_get_payment_settlement = new Object();		
	public PaymentSettlement getPaymentSettlement(Payment payment,
			int paymentTypeId, String paidAmount) {
		PaymentSettlement paymentSettlement = null;
		
		synchronized(lock_get_payment_settlement) {
			paymentSettlement =PaymentSettlementSQL
					.getPaymentSettlementByPaymentIdAndTypeId(payment,
							paymentTypeId);
			long time = System.currentTimeMillis();
			if (paymentSettlement == null) {
				paymentSettlement = new PaymentSettlement();
				paymentSettlement.setId(CommonSQL
						.getNextSeq(TableNames.PaymentSettlement));
				paymentSettlement.setBillNo(payment.getBillNo());
				paymentSettlement.setPaymentId(payment.getId());
				paymentSettlement.setPaymentTypeId(paymentTypeId);
				paymentSettlement.setPaidAmount(paidAmount);
				paymentSettlement.setTotalAmount(payment.getPaymentAmount());
				paymentSettlement.setRestaurantId(payment.getRestaurantId());
				paymentSettlement.setRevenueId(payment.getRevenueId());
				paymentSettlement.setUserId(payment.getUserId());
				paymentSettlement.setCreateTime(time);
				paymentSettlement.setIsActive(ParamConst.PAYMENT_SETT_IS_ACTIVE);
			} else {
				paymentSettlement.setPaymentTypeId(paymentTypeId);
				if (paymentTypeId == ParamConst.SETTLEMENT_TYPE_CASH) {
					BigDecimal amount = BH.add(
							BH.getBD(paymentSettlement.getPaidAmount()),
							BH.getBD(paidAmount), true);
					paymentSettlement.setPaidAmount(amount.toString());
				} else {
					paymentSettlement.setPaidAmount(paidAmount);
				}
			}
			paymentSettlement.setUpdateTime(time);
			PaymentSettlementSQL.addPaymentSettlement(paymentSettlement);
		}
		return paymentSettlement;
	}
	public PaymentSettlement getPaymentSettlementForCard(Payment payment,
			int paymentTypeId, String paidAmount) {
		PaymentSettlement paymentSettlement = null;
		
		synchronized(lock_get_payment_settlement) {
//			paymentSettlement =PaymentSettlementSQL
//					.getPaymentSettlementByPaymentIdAndTypeId(payment,
//							paymentTypeId);
			long time = System.currentTimeMillis();
			paymentSettlement = new PaymentSettlement();
			paymentSettlement.setId(CommonSQL
					.getNextSeq(TableNames.PaymentSettlement));
			paymentSettlement.setBillNo(payment.getBillNo());
			paymentSettlement.setPaymentId(payment.getId());
			paymentSettlement.setPaymentTypeId(paymentTypeId);
			paymentSettlement.setPaidAmount(paidAmount);
			paymentSettlement.setTotalAmount(payment.getPaymentAmount());
			paymentSettlement.setRestaurantId(payment.getRestaurantId());
			paymentSettlement.setRevenueId(payment.getRevenueId());
			paymentSettlement.setUserId(payment.getUserId());
			paymentSettlement.setCreateTime(time);
			paymentSettlement.setUpdateTime(time);
			paymentSettlement.setIsActive(ParamConst.PAYMENT_SETT_IS_ACTIVE);
			PaymentSettlementSQL.addPaymentSettlement(paymentSettlement);
		}
		return paymentSettlement;
	}

	//bob add:thread safe
	Object lock_get_getBohHoldSettlementByPaymentSettlement = new Object();			
	public BohHoldSettlement getBohHoldSettlementByPaymentSettlement(
			PaymentSettlement paymentSettlement, int orderId,
			BohHoldSettlement mBohHoldSettlement) {
		
		BohHoldSettlement bohHoldSettlement = null;
		synchronized(lock_get_getBohHoldSettlementByPaymentSettlement) {
			bohHoldSettlement = BohHoldSettlementSQL
					.getBohHoldSettlementByPament(paymentSettlement.getPaymentId(),
							paymentSettlement.getId());
			if (bohHoldSettlement == null) {
				bohHoldSettlement = new BohHoldSettlement();
				bohHoldSettlement.setId(CommonSQL
						.getNextSeq(TableNames.BohHoldSettlement));
				bohHoldSettlement.setRestaurantId(paymentSettlement
						.getRestaurantId());
				bohHoldSettlement.setRevenueId(paymentSettlement.getRevenueId());
				bohHoldSettlement.setPaymentId(paymentSettlement.getPaymentId());
				bohHoldSettlement.setPaymentSettId(paymentSettlement.getId());
				bohHoldSettlement.setOrderId(orderId);
				bohHoldSettlement.setBillNo(paymentSettlement.getBillNo());
				bohHoldSettlement.setNameOfPerson(mBohHoldSettlement
						.getNameOfPerson());
				bohHoldSettlement.setPhone(mBohHoldSettlement.getPhone());
				bohHoldSettlement.setRemarks(mBohHoldSettlement.getRemarks());
				bohHoldSettlement.setAuthorizedUserId(mBohHoldSettlement
						.getAuthorizedUserId());
				bohHoldSettlement.setAmount(mBohHoldSettlement.getAmount());
				bohHoldSettlement.setStatus(ParamConst.BOH_HOLD_STATUS_UNPLAY);
				bohHoldSettlement.setPaidDate(null);
				long time = System.currentTimeMillis();
				bohHoldSettlement.setDaysDue(time);
				bohHoldSettlement.setIsActive(ParamConst.PAYMENT_SETT_IS_ACTIVE);
				BohHoldSettlementSQL.addBohHoldSettlement(bohHoldSettlement);
			}
		}
		return bohHoldSettlement;
	}

	//bob add:thread safe
	Object lock_get_getNonChargableSettlementByPaymentSettlement = new Object();	
	
	public NonChargableSettlement getNonChargableSettlementByPaymentSettlement(
			Payment payment, PaymentSettlement paymentSettlement,
			NonChargableSettlement mNonChargableSettlement) {
		
		NonChargableSettlement nonChargableSettlement = null;
		synchronized(lock_get_getNonChargableSettlementByPaymentSettlement) {
			nonChargableSettlement = NonChargableSettlementSQL
					.getNonChargableSettlementByPaymentId(payment.getId(),paymentSettlement
							.getId());
			if (nonChargableSettlement == null) {
				nonChargableSettlement = new NonChargableSettlement();
				nonChargableSettlement.setId(CommonSQL
						.getNextSeq(TableNames.NonChargableSettlement));
				nonChargableSettlement.setOrderId(payment.getOrderId());
				nonChargableSettlement.setBillNo(paymentSettlement.getBillNo());
				nonChargableSettlement.setPaymentId(paymentSettlement
						.getPaymentId());
				nonChargableSettlement.setPaymentSettId(paymentSettlement.getId());
				nonChargableSettlement.setNameOfPerson(mNonChargableSettlement
						.getNameOfPerson());
				nonChargableSettlement.setRemarks(mNonChargableSettlement
						.getRemarks());
				nonChargableSettlement.setAuthorizedUserId(mNonChargableSettlement
						.getAuthorizedUserId());
				nonChargableSettlement.setAmount(mNonChargableSettlement
						.getAmount());
				nonChargableSettlement.setRestaurantId(mNonChargableSettlement
						.getRestaurantId());
				nonChargableSettlement.setRevenueId(mNonChargableSettlement
						.getRevenueId());
				nonChargableSettlement.setUserId(mNonChargableSettlement
						.getUserId());
				long time = System.currentTimeMillis();
				nonChargableSettlement.setCreateTime(time);
				nonChargableSettlement.setUpdateTime(time);
				nonChargableSettlement.setIsActive(ParamConst.PAYMENT_SETT_IS_ACTIVE);
				NonChargableSettlementSQL.addNonChargableSettlement(nonChargableSettlement);
			}
		}
		return nonChargableSettlement;
	}

	//bob add:thread safe
	Object lock_getVoidSettlementByPayment = new Object();	
	public VoidSettlement getVoidSettlementByPayment(Payment payment,
			PaymentSettlement paymentSettlement, VoidSettlement mVoidSettlement) {
		VoidSettlement voidSettlement = null;
		
		synchronized(lock_getVoidSettlementByPayment) {
			voidSettlement = VoidSettlementSQL
					.getVoidSettlementByPament(payment.getId(),
							paymentSettlement.getId());
			if (voidSettlement == null) {
				voidSettlement = new VoidSettlement();
				voidSettlement.setId(CommonSQL
						.getNextSeq(TableNames.VoidSettlement));
				voidSettlement.setOrderId(payment.getOrderId());
				voidSettlement.setBillNo(payment.getBillNo());
				voidSettlement.setPaymentId(payment.getId());
				voidSettlement.setPaymentSettId(paymentSettlement.getId());
				voidSettlement.setReason(mVoidSettlement.getReason());
				voidSettlement.setAuthorizedUserId(mVoidSettlement
						.getAuthorizedUserId());
				voidSettlement.setAmount(mVoidSettlement.getAmount());
				voidSettlement.setRestaurantId(payment.getRestaurantId());
				voidSettlement.setRevenueId(payment.getRevenueId());
				voidSettlement.setUserId(payment.getUserId());
				long time = System.currentTimeMillis();
				voidSettlement.setCreateTime(time);
				voidSettlement.setUpdateTime(time);
				voidSettlement.setIsActive(ParamConst.PAYMENT_SETT_IS_ACTIVE);
				voidSettlement.setType(mVoidSettlement.getType());
				VoidSettlementSQL.addVoidSettlement(voidSettlement);
			}
		}
		return voidSettlement;
	}

	//bob add:thread safe
	Object lock_getNetsSettlementByPayment = new Object();	
	public NetsSettlement getNetsSettlementByPayment(Payment payment,
			PaymentSettlement paymentSettlement, int referenceNo,
			String cashAmount) {
		NetsSettlement netsSettlement = null;
		
		synchronized(lock_getNetsSettlementByPayment) {
			netsSettlement = NetsSettlementSQL
					.getNetsSettlementByPament(payment.getId(),
							paymentSettlement.getId());
			long time = System.currentTimeMillis();
			if (netsSettlement == null) {
				netsSettlement = new NetsSettlement();
				netsSettlement.setId(CommonSQL
						.getNextSeq(TableNames.NetsSettlement));
				netsSettlement.setPaymentId(payment.getId());
				netsSettlement.setPaymentSettId(paymentSettlement.getId());
				netsSettlement.setBillNo(payment.getBillNo());
				netsSettlement.setReferenceNo(referenceNo);
				netsSettlement.setCashAmount(cashAmount);
				netsSettlement.setCreateTime(time);
				netsSettlement.setUpdateTime(time);
				netsSettlement.setIsActive(ParamConst.PAYMENT_SETT_IS_ACTIVE);
			} else {
				netsSettlement.setReferenceNo(referenceNo);
				netsSettlement.setCashAmount(cashAmount);
				netsSettlement.setUpdateTime(time);
			}
			NetsSettlementSQL.addNetsSettlement(netsSettlement);
		}
		return netsSettlement;
	}

	public PrinterTitle getPrinterTitle(RevenueCenter revenue, Order order,
			String userName, String tableName, int copy) {
		PrinterTitle printerTitle = new PrinterTitle();
		Restaurant restaurant = RestaurantSQL.getRestaurant();
		printerTitle.setRestaurantName(restaurant.getRestaurantPrint());
		printerTitle.setAddressDetail(restaurant.getAddressPrint());
		printerTitle.setTel(restaurant.getTelNo());
		printerTitle.setEmail(restaurant.getEmail());
		printerTitle.setWebAddress(restaurant.getWebsite());
		printerTitle.setOp(userName);
		printerTitle.setPos(revenue.getId().intValue() + "");
		printerTitle.setDate(TimeUtil.getPrintDate(order.getCreateTime()));
		printerTitle.setBill_NO(ParamHelper.getPrintOrderBillNo(OrderBillSQL.getOrderBillByOrder(order).getBillNo()));
		printerTitle.setTime(TimeUtil.getPrintTime(order.getCreateTime()));
		printerTitle.setTableName(tableName);
		printerTitle.setLogo(SettingDataSQL.getSettingDataByUrl(
				restaurant.getLogoUrl()).getLogoString());
		printerTitle.setOptions(restaurant.getOptions());
		printerTitle.setFooterOptions(restaurant.getFooterOptions());
		printerTitle.setIsTakeAway(order.getIsTakeAway());
		printerTitle.setBizDate(order.getBusinessDate().toString());
		printerTitle.setIsKiosk(revenue.getIsKiosk());
		printerTitle.setCopy(copy);
		if(revenue.getIsKiosk() == ParamConst.REVENUECENTER_IS_KIOSK){
			printerTitle.setOrderNo(IntegerUtils.fromat(revenue.getIndexId(), order.getOrderNo().toString()));
		}else{
			printerTitle.setOrderNo(order.getOrderNo().toString());
		}

		return printerTitle;
	}
	
	public PrinterTitle getPrinterTitleByOrderSplit(RevenueCenter revenue, Order order, OrderSplit orderSplit,
			String userName, String tableName, OrderBill orderBill, String businessDate, int copy) {
		PrinterTitle printerTitle = new PrinterTitle();
		Restaurant restaurant = RestaurantSQL.getRestaurant();
		printerTitle.setRestaurantName(restaurant.getRestaurantPrint());
		printerTitle.setAddressDetail(restaurant.getAddressPrint());
		printerTitle.setTel(restaurant.getTelNo());
		printerTitle.setEmail(restaurant.getEmail());
		printerTitle.setWebAddress(restaurant.getWebsite());
		printerTitle.setOp(userName);
		printerTitle.setPos(revenue.getId() + "");
		printerTitle.setDate(TimeUtil.getPrintDate(orderSplit.getCreateTime()));
		printerTitle.setBill_NO(ParamHelper.getPrintOrderBillNo(orderBill.getBillNo()));
		printerTitle.setTime(TimeUtil.getPrintTime(orderSplit.getCreateTime()));
		printerTitle.setTableName(tableName);
		printerTitle.setLogo(SettingDataSQL.getSettingDataByUrl(
				restaurant.getLogoUrl()).getLogoString());
		printerTitle.setOptions(restaurant.getOptions());
		printerTitle.setFooterOptions(restaurant.getFooterOptions());
		printerTitle.setBizDate(businessDate);
		printerTitle.setGroupNum(orderSplit.getGroupId() + "");
		printerTitle.setIsKiosk(revenue.getIsKiosk());
		printerTitle.setCopy(copy);
//		printerTitle.setOrderNo(orderSplit.getOrderId().toString());
		if(revenue.getIsKiosk() == ParamConst.REVENUECENTER_IS_KIOSK){
			printerTitle.setOrderNo(IntegerUtils.fromat(revenue.getIndexId(), order.getOrderNo().toString()));
		}else{
			printerTitle.setOrderNo(order.getOrderNo().toString());
		}
		return printerTitle;
	}

	public PrinterTitle getPrinterTitleForReport(int revenueId, String billNo,
			String userName, String tableName, String businessDate) {
		PrinterTitle printerTitle = new PrinterTitle();
		Restaurant restaurant = new Restaurant();
		restaurant = RestaurantSQL.getRestaurant();
		printerTitle.setRestaurantName(restaurant.getRestaurantPrint());
		printerTitle.setAddressDetail(restaurant.getAddressPrint());

		printerTitle.setAddress(restaurant.getCity() + " "
				+ restaurant.getState() + " " + restaurant.getCountry() + " "
				+ restaurant.getPostalCode());
		printerTitle.setTel(restaurant.getTelNo());
		printerTitle.setEmail(restaurant.getEmail());
		printerTitle.setWebAddress(restaurant.getWebsite());
		printerTitle.setOp(userName);
		long time = System.currentTimeMillis();
		printerTitle.setPos(revenueId + "");
		printerTitle.setDate(TimeUtil.getPrintDate(time));
		printerTitle.setBill_NO(billNo);
		printerTitle.setTime(TimeUtil.getPrintTime(time));
		printerTitle.setTableName(tableName);
		printerTitle.setLogo(SettingDataSQL.getSettingDataByUrl(
				restaurant.getLogoUrl()).getLogoString());
		printerTitle.setBizDate(businessDate);
		printerTitle.setOrderNo(billNo);
		
		return printerTitle;
	}

	//get item list to print
	public ArrayList<PrintOrderItem> getItemList(
			ArrayList<OrderDetail> orderDetails) {
		ArrayList<PrintOrderItem> list = new ArrayList<PrintOrderItem>();
		for (OrderDetail orderDetail : orderDetails) {
			ItemDetail itemDetail = CoreData.getInstance().getItemDetailById(
					orderDetail.getItemId());
			// Double amount = Double.parseDouble(orderDetail.getItemPrice())
			// * orderDetail.getItemNum();
			String price = orderDetail.getItemPrice();
			BigDecimal amountBH = BH.getBD(ParamConst.DOUBLE_ZERO);
			if (orderDetail.getOrderDetailType() == ParamConst.ORDERDETAIL_TYPE_FREE)
				price = ParamConst.DOUBLE_ZERO;
//			BigDecimal amountBH = BH.mul(BH.getBD(price),
//					BH.getBD(orderDetail.getItemNum()), true);
			else
			  amountBH = BH.getBD(orderDetail.getRealPrice());
			list.add(new PrintOrderItem(orderDetail.getId(), itemDetail
					.getItemName(), BH.getBD(orderDetail.getItemPrice())
					.toString(), orderDetail.getItemNum() + "", amountBH
					.toString(), orderDetail.getWeight()));
		}
		return list;
	}



	public PrinterTitle getPrinterTitleForQRCode(RevenueCenter revenue,
										String userName, String tableName) {
		PrinterTitle printerTitle = new PrinterTitle();
		Restaurant restaurant = RestaurantSQL.getRestaurant();
		printerTitle.setRestaurantName(restaurant.getRestaurantPrint());
		printerTitle.setAddressDetail(restaurant.getAddressPrint());
		printerTitle.setTel(restaurant.getTelNo());
		printerTitle.setEmail(restaurant.getEmail());
		printerTitle.setWebAddress(restaurant.getWebsite());
		printerTitle.setOp(userName);
		printerTitle.setPos(revenue.getId().intValue() + "");
		printerTitle.setTableName(tableName);
		printerTitle.setLogo(SettingDataSQL.getSettingDataByUrl(
				restaurant.getLogoUrl()).getLogoString());
		printerTitle.setOptions(restaurant.getOptions());
		printerTitle.setFooterOptions(restaurant.getFooterOptions());
		return printerTitle;
	}

	/* get order modifiers for print */
	public ArrayList<PrintOrderModifier> getItemModifierList(Order order, ArrayList<OrderDetail> orderDetails) {

		ArrayList<PrintOrderModifier> list = new ArrayList<PrintOrderModifier>();
		ArrayList<OrderModifier> orderModifiers = OrderModifierSQL
				.getAllOrderModifierByOrderAndNormal(order);
		for (OrderModifier orm : orderModifiers) {
			Modifier mitem = CoreData.getInstance().getModifier(
					orm.getModifierId());
			// Double amount = Double.parseDouble(mitem.getPrice())
			// * orm.getModifierNum();
			BigDecimal amountBH = BH.mul(BH.getBD(mitem.getPrice()),
					BH.getBD(orm.getModifierNum()), true);
			list.add(new PrintOrderModifier(orm.getOrderDetailId(), mitem
					.getModifierName(), mitem.getPrice(), mitem.getQty()
					, amountBH.toString()));

			// ItemDetail itemDetail = CoreData.getInstance().getItemDetailById(
			// orderDetail.getItemId());
			// Double amount = Double.parseDouble(orderDetail.getRealPrice())
			// * orderDetail.getItemNum();
			// list.add(new
			// PrintOrderItem(orderDetail.getId(),itemDetail.getItemName(),
			// orderDetail
			// .getRealPrice(), orderDetail.getItemNum() + "", Double
			// .toString(amount)));
		}
		for(OrderDetail orderDetail : orderDetails){
			if(!TextUtils.isEmpty(orderDetail.getSpecialInstractions())){
				list.add(new PrintOrderModifier(orderDetail.getId(), orderDetail.getSpecialInstractions(), "0.00", 1, "0.00"));
			}
		}
		return list;
	}
	
	
	/* get order modifiers for print */
	public ArrayList<PrintOrderModifier> getItemModifierListByOrderDetail(List<OrderDetail> orderDetails) {

		ArrayList<PrintOrderModifier> list = new ArrayList<PrintOrderModifier>();
		
		for(OrderDetail orderDetail : orderDetails){
			ArrayList<OrderModifier> orderModifiers = OrderModifierSQL.getOrderModifiers(orderDetail);
			for (OrderModifier orm : orderModifiers) {
				Modifier mitem = CoreData.getInstance().getModifier(
						orm.getModifierId());
			// Double amount = Double.parseDouble(mitem.getPrice())
			// * orm.getModifierNum();
				BigDecimal amountBH = BH.mul(BH.getBD(mitem.getPrice()),
						BH.getBD(orm.getModifierNum()), true);
				list.add(new PrintOrderModifier(orm.getOrderDetailId(), mitem
						.getModifierName(), mitem.getPrice(), orm.getModifierNum().intValue()
						, amountBH.toString()));

			// ItemDetail itemDetail = CoreData.getInstance().getItemDetailById(
			// orderDetail.getItemId());
			// Double amount = Double.parseDouble(orderDetail.getRealPrice())
			// * orderDetail.getItemNum();
			// list.add(new
			// PrintOrderItem(orderDetail.getId(),itemDetail.getItemName(),
			// orderDetail
			// .getRealPrice(), orderDetail.getItemNum() + "", Double
			// .toString(amount)));
			}
		}
		return list;
	}

	Object lock_getKotSummary = new Object();
	public KotSummary getKotSummary(String tableName, Order order,
			RevenueCenter revenueCenter, long businessDate) {
		
		KotSummary kotSummary = null;
		synchronized(lock_getKotSummary) {
			kotSummary =  KotSummarySQL.getKotSummary(order.getId());
			long time = System.currentTimeMillis();
			if (kotSummary == null) {
				kotSummary = new KotSummary();
				kotSummary.setId(CommonSQL.getNextSeq(TableNames.KotSummary));
				kotSummary.setOrderId(order.getId());
				kotSummary.setOrderNo(order.getOrderNo());//流水号
				kotSummary.setRevenueCenterId(revenueCenter.getId());
				kotSummary.setRevenueCenterName(revenueCenter.getRevName());
				if(revenueCenter.getIsKiosk() == ParamConst.REVENUECENTER_IS_KIOSK){
					kotSummary.setTableName(order.getTableName());
				}else{
					kotSummary.setTableName(tableName);
				}
				kotSummary.setCreateTime(time);
				kotSummary.setUpdateTime(time);
				kotSummary.setBusinessDate(businessDate);
				kotSummary.setIsTakeAway(order.getIsTakeAway());
				kotSummary.setRevenueCenterIndex(revenueCenter.getIndexId());
				kotSummary.setOrderRemark(order.getOrderRemark());
				KotSummarySQL.update(kotSummary);
			}
		}
		return kotSummary;
	}


//	Object lock_getKotSummary = new Object();
	public KotSummary getKotSummaryForPlace(String tableName, Order order,
									RevenueCenter revenueCenter, long businessDate) {

		KotSummary kotSummary = null;
		synchronized(lock_getKotSummary) {
			long time = System.currentTimeMillis();
			if (kotSummary == null) {
				kotSummary = new KotSummary();
				kotSummary.setId(CommonSQL.getNextSeq(TableNames.KotSummary));
				kotSummary.setOrderId(order.getId());
				kotSummary.setOrderNo(order.getOrderNo());//流水号
				kotSummary.setRevenueCenterId(revenueCenter.getId());
				kotSummary.setRevenueCenterName(revenueCenter.getRevName());
				if(revenueCenter.getIsKiosk() == ParamConst.REVENUECENTER_IS_KIOSK){
					kotSummary.setTableName(order.getTableName());
				}else{
					kotSummary.setTableName(tableName);
				}
				kotSummary.setCreateTime(time);
				kotSummary.setUpdateTime(time);
				kotSummary.setBusinessDate(businessDate);
				kotSummary.setIsTakeAway(order.getIsTakeAway());
				kotSummary.setRevenueCenterIndex(revenueCenter.getIndexId());
				kotSummary.setOrderRemark(order.getOrderRemark());
				KotSummarySQL.update(kotSummary);
			}
		}
		return kotSummary;
	}

	//bob add:thread safe
	Object lock_getKotItemDetail = new Object();	
	public KotItemDetail getKotItemDetail(Order order, OrderDetail orderDetail,
			ItemDetail itemDetail, KotSummary kotSummary,
			SessionStatus sessionStatus, int categoryId) {
		
		KotItemDetail kotItemDetail = null;
		synchronized(lock_getKotItemDetail) {
			kotItemDetail = KotItemDetailSQL
					.getMainKotItemDetailByOrderDetailId(orderDetail.getId());
			if (kotItemDetail == null) {
				long time = System.currentTimeMillis();
				kotItemDetail = new KotItemDetail();
				kotItemDetail.setId(CommonSQL.getNextSeq(TableNames.KotItemDetail));
				kotItemDetail.setRestaurantId(order.getRestId());
				kotItemDetail.setRevenueId(order.getRevenueId());
				kotItemDetail.setOrderId(orderDetail.getOrderId());
				kotItemDetail.setOrderDetailId(orderDetail.getId());
				kotItemDetail.setPrinterGroupId(itemDetail.getPrinterId());
				kotItemDetail.setKotSummaryId(kotSummary.getId());
				kotItemDetail.setItemName(itemDetail.getItemName());
				kotItemDetail.setItemNum(orderDetail.getItemNum());
				kotItemDetail.setFinishQty(0); // 新创建的都0
				kotItemDetail.setSessionStatus(sessionStatus.getSession_status());
				kotItemDetail.setKotStatus(ParamConst.KOT_STATUS_UNSEND);
				kotItemDetail.setSpecialInstractions(orderDetail
						.getSpecialInstractions());
				kotItemDetail.setVersion(0); // 没用
				kotItemDetail.setCreateTime(time);
				kotItemDetail.setUpdateTime(time);
				kotItemDetail.setUnFinishQty(orderDetail.getItemNum()); // 新创建的都是跟ItemNum一样
				kotItemDetail.setCategoryId(categoryId);
				kotItemDetail.setIsTakeAway(orderDetail.getIsTakeAway());
				KotItemDetailSQL.update(kotItemDetail);
			}else{
				kotItemDetail.setUnFinishQty(orderDetail.getItemNum());
				KotItemDetailSQL.update(kotItemDetail);
			}
		}
		return kotItemDetail;
	}
	
	public KotItemDetail getSubKotItemDetail(KotItemDetail mainKotItemDetail) {
		KotItemDetail kotItemDetail = null;
		synchronized(lock_getKotItemDetail) {
			kotItemDetail = KotItemDetailSQL
					.getSubKotItemDetailByMainKotItemDeail(mainKotItemDetail);
			if (kotItemDetail == null) {
				long time = System.currentTimeMillis();
				kotItemDetail = new KotItemDetail();
				kotItemDetail.setId(CommonSQL.getNextSeq(TableNames.KotItemDetail));
				kotItemDetail.setRestaurantId(mainKotItemDetail.getRestaurantId());
				kotItemDetail.setRevenueId(mainKotItemDetail.getRevenueId());
				kotItemDetail.setOrderId(mainKotItemDetail.getOrderId());
				kotItemDetail.setOrderDetailId(mainKotItemDetail.getOrderDetailId());
				kotItemDetail.setPrinterGroupId(mainKotItemDetail.getPrinterGroupId());
				kotItemDetail.setKotSummaryId(mainKotItemDetail.getKotSummaryId());
				kotItemDetail.setItemName(mainKotItemDetail.getItemName());
				kotItemDetail.setItemNum(mainKotItemDetail.getItemNum());
				kotItemDetail.setFinishQty(mainKotItemDetail.getFinishQty());
				kotItemDetail.setSessionStatus(mainKotItemDetail.getSessionStatus());
				kotItemDetail.setKotStatus(ParamConst.KOT_STATUS_UNSEND);
				kotItemDetail.setSpecialInstractions(mainKotItemDetail
						.getSpecialInstractions());
				kotItemDetail.setVersion(0); // 没用
				kotItemDetail.setCreateTime(time);
				kotItemDetail.setUpdateTime(time);
				kotItemDetail.setUnFinishQty(mainKotItemDetail.getUnFinishQty());
				kotItemDetail.setCategoryId(ParamConst.KOTITEMDETAIL_CATEGORYID_SUB);
				kotItemDetail.setIsTakeAway(ParamConst.TAKE_AWAY);
				kotItemDetail.setFireStatus(kotItemDetail.getFireStatus());
				KotItemDetailSQL.update(kotItemDetail);
			}
		}
		return kotItemDetail;
	}

	//bob add:thread safe
	Object lock_getKotItemModifier = new Object();	
	public KotItemModifier getKotItemModifier(KotItemDetail kotItemDetail,
			OrderModifier orderModifier, Modifier modifier) {
		
		KotItemModifier kotItemModifier = null;
		synchronized(lock_getKotItemModifier) {
			kotItemModifier = KotItemModifierSQL
					.getKotItemModifier(kotItemDetail.getId(), modifier.getId());
			if (kotItemModifier == null) {
				kotItemModifier = new KotItemModifier();
				kotItemModifier.setId(CommonSQL
						.getNextSeq(TableNames.KotItemModifier));
				kotItemModifier.setKotItemDetailId(kotItemDetail.getId());
				kotItemModifier.setModifierId(modifier.getId());
				kotItemModifier.setModifierName(modifier.getModifierName());
				kotItemModifier.setModifierNum(modifier.getQty());
				kotItemModifier.setStatus(ParamConst.KOT_STATUS_UNSEND);
				kotItemModifier.setPrinterId(orderModifier.getPrinterId());
				KotItemModifierSQL.update(kotItemModifier);
			}
		}
		return kotItemModifier;
	}

	//bob add:thread safe
	Object lock_getKotNotification = new Object();	
	public KotNotification getKotNotification(SessionStatus sessionStatus,
			KotSummary kotSummary, KotItemDetail kotItemDetail) {
		KotNotification kotNotification = null;
		
		synchronized(lock_getKotNotification) {
			kotNotification = KotNotificationSQL
					.getKotNotification(kotItemDetail.getOrderDetailId(), kotItemDetail.getId());
			if (kotNotification == null) {
				kotNotification = new KotNotification();
				kotNotification.setId(CommonSQL
						.getNextSeq(TableNames.KotNotification));
				kotNotification.setItemName(kotItemDetail.getItemName());
				kotNotification.setOrderId(kotSummary.getOrderId());
				kotNotification.setOrderDetailId(kotItemDetail.getOrderDetailId());
				kotNotification.setQty(kotItemDetail.getFinishQty());
				kotNotification.setRevenueCenterId(kotSummary.getRevenueCenterId());
				kotNotification.setRevenueCenterName(kotSummary
						.getRevenueCenterName());
				kotNotification.setSession(sessionStatus.getSession_status());
				kotNotification.setTableName(kotSummary.getTableName());
				kotNotification.setStatus(ParamConst.KOTNOTIFICATION_STATUS_NORMAL);
				kotNotification.setUnFinishQty(kotItemDetail.getUnFinishQty());
				kotNotification.setKotItemDetailId(kotItemDetail.getId());
				kotNotification.setKotItemNum(kotItemDetail.getItemNum());
				KotNotificationSQL.update(kotNotification);
			}else {
				kotNotification.setStatus(ParamConst.KOTNOTIFICATION_STATUS_NORMAL);
				KotNotificationSQL.update(kotNotification);
			}
		}
		return kotNotification;
	}

	public UserTimeSheet getUserTimeSheet(long businessDate, User user,
			RevenueCenter revenueCenter) {
		UserTimeSheet userTimeSheet = new UserTimeSheet();
		userTimeSheet.setId(CommonSQL.getNextSeq(TableNames.UserTimeSheet));
		userTimeSheet.setBusinessDate(businessDate);
		userTimeSheet.setRestaurantId(revenueCenter.getRestaurantId());
		userTimeSheet.setRevenueId(revenueCenter.getId());
		userTimeSheet.setUserId(user.getId());
		userTimeSheet.setEmpId(user.getEmpId());
		userTimeSheet.setEmpName(user.getFirstName() + user.getLastName());
		userTimeSheet.setLoginTime(System.currentTimeMillis());
		userTimeSheet.setStatus(ParamConst.USERTIMESHEET_STATUS_LOGIN);
		return userTimeSheet;
	}

	//bob add:thread safe
	Object lock_getReportDiscount = new Object();	
	
	public ReportDiscount getReportDiscount(TableInfo tables, Order order,
			User user, RevenueCenter revenueCenter, long businessDate) {
		
		ReportDiscount reportDiscount = null;
		
		synchronized(lock_getReportDiscount) {
			 reportDiscount = ReportDiscountSQL
					.getReportDiscountByOrderId(order.getId());
			if (reportDiscount == null) {
				reportDiscount = new ReportDiscount();
				reportDiscount.setId(CommonSQL
						.getNextSeq(TableNames.ReportDiscount));
				reportDiscount.setRestaurantId(revenueCenter.getId());
				reportDiscount.setRestaurantId(CoreData.getInstance()
						.getRestaurant().getId());
				reportDiscount.setUserId(user.getId());
				reportDiscount.setOrderId(order.getId());
				reportDiscount.setRevenueName(revenueCenter.getRevName());
				reportDiscount.setBusinessDate(businessDate);
				// reportDiscount.setBillNumber(OrderBillSQL.getOrderBillByOrder(order).getBillNo());
				reportDiscount.setTableId(tables.getPosId());
				reportDiscount.setTableName(tables.getName());
				reportDiscount.setActuallAmount("0");// TODO
				reportDiscount.setDiscount("0.00");// TODO
				reportDiscount.setGrandTotal("0.00");// TODO
				ReportDiscountSQL.update(reportDiscount);
			} else {
				reportDiscount.setUserId(user.getId());
				// reportDiscount.setActuallAmount("0");//TODO
				// reportDiscount.setDiscount("0");//TODO
				// reportDiscount.setGrandTotal("0");//TODO
				ReportDiscountSQL.update(reportDiscount);
			}
		}
		return reportDiscount;
	}

	public CashInOut getCashInOut(RevenueCenter revenueCenter,
			long businessDate, User user, int type, String cash, String comment) {
		CashInOut cashInOut = new CashInOut();
		cashInOut.setId(CommonSQL.getNextSeq(TableNames.CashInOut));
		cashInOut.setRestaurantId(CoreData.getInstance().getRestaurant().getId());
		cashInOut.setRevenueId(revenueCenter.getId());
		cashInOut.setUserId(user.getId());
		cashInOut.setEmpId(user.getId());
		cashInOut.setEmpName(user.getUserName());
		cashInOut.setBusinessDate(businessDate);
		cashInOut.setType(type);
		cashInOut.setComment(comment);
		cashInOut.setCash(cash);
		cashInOut.setCreateTime(System.currentTimeMillis() + "");
		CashInOutSQL.update(cashInOut);
		return cashInOut;
	}
	Object lock_LocalDevice = new Object();
	public LocalDevice getLocalDevice(String name, String model, int type, int deviceId,
			String ip, String mac) {
		LocalDevice localDevice = null;
		synchronized (lock_LocalDevice) {
			localDevice = LocalDeviceSQL.getLocalDeviceByDeviceId(deviceId);
			if (localDevice == null) {
				localDevice = new LocalDevice();
				localDevice.setId(CommonSQL.getNextSeq(TableNames.LocalDevice));
			}
			localDevice.setDeviceName(name);
			localDevice.setDeviceMode(model);
			localDevice.setConnected(1);
			localDevice.setDeviceType(type);
			localDevice.setDeviceId(deviceId);
			localDevice.setIp(ip);
			localDevice.setMacAddress(mac);
		}
		return localDevice;
	}

	//bob add:thread safe
	Object lock_getCardsSettlement = new Object();	
	public CardsSettlement getCardsSettlement(Payment payment,
			PaymentSettlement paymentSettlement, int paymentTypeId,
			String cardNo, String cvvNo, String cardExpiryDateStr) {
		
		CardsSettlement cardsSettlement = null;
		
		synchronized(lock_getCardsSettlement) {
			cardsSettlement = CardsSettlementSQL
					.getCardsSettlementByPament(payment.getId(),
							paymentSettlement.getId());
			long times = System.currentTimeMillis();
			if (cardsSettlement == null) {
				cardsSettlement = new CardsSettlement();
				cardsSettlement.setId(CommonSQL
						.getNextSeq(TableNames.CardsSettlement));
				cardsSettlement.setPaymentId(payment.getId());
				cardsSettlement.setPaymentSettId(paymentSettlement.getId());
				cardsSettlement.setBillNo(payment.getBillNo());
				cardsSettlement.setCardNo(cardNo);
				cardsSettlement.setCardType(paymentTypeId);
				cardsSettlement.setCvvNo(Integer.parseInt(cvvNo));
				cardsSettlement.setCardExpiryDate(cardExpiryDateStr);
				cardsSettlement.setCreateTime(times);
				cardsSettlement.setUpdateTime(times);
				cardsSettlement.setIsActive(ParamConst.PAYMENT_SETT_IS_ACTIVE);
			} else {
				cardsSettlement.setCardNo(cardNo);
				cardsSettlement.setCvvNo(Integer.parseInt(cvvNo));
				cardsSettlement.setCardExpiryDate(cardExpiryDateStr);
				cardsSettlement.setUpdateTime(times);
			}
			CardsSettlementSQL.addCardsSettlement(cardsSettlement);
		}
		return cardsSettlement;
	}
	
	Object lock_getAlipaySettlement = new Object();	
	public AlipaySettlement getAlipaySettlement(Payment payment,
			PaymentSettlement paymentSettlement, String tradeNo, String buyerEmail) {
		
		AlipaySettlement alipaySettlement = null;
		
		synchronized(lock_getAlipaySettlement) {
			alipaySettlement = AlipaySettlementSQL
					.getAlipaySettlementByPament(payment.getId(),
							paymentSettlement.getId());
			long times = System.currentTimeMillis();
			if (alipaySettlement == null) {
				alipaySettlement = new AlipaySettlement();
				alipaySettlement.setId(CommonSQL
						.getNextSeq(TableNames.AlipaySettlement));
				alipaySettlement.setPaymentId(payment.getId());
				alipaySettlement.setPaymentSettId(paymentSettlement.getId());
				alipaySettlement.setBillNo(payment.getBillNo());
				alipaySettlement.setTradeNo(tradeNo);
				alipaySettlement.setBuyerEmail(buyerEmail);
				alipaySettlement.setCreateTime(times);
				alipaySettlement.setUpdateTime(times);
				alipaySettlement.setIsActive(ParamConst.PAYMENT_SETT_IS_ACTIVE);
				AlipaySettlementSQL.addAlipaySettlement(alipaySettlement);
			}
			
		}
		return alipaySettlement;
	}
	
	Object lock_getWeixinSettlement = new Object();	
	public WeixinSettlement getWeixinSettlement(Payment payment,
			PaymentSettlement paymentSettlement, String tradeNo, String buyerEmail) {
		
		WeixinSettlement weixinSettlement = null;
		
		synchronized(lock_getAlipaySettlement) {
			weixinSettlement = WeixinSettlementSQL
					.getWeixinSettlementByPament(payment.getId(),
							paymentSettlement.getId());
			long times = System.currentTimeMillis();
			if (weixinSettlement == null) {
				weixinSettlement = new WeixinSettlement();
				weixinSettlement.setId(CommonSQL
						.getNextSeq(TableNames.WeixinSettlement));
				weixinSettlement.setPaymentId(payment.getId());
				weixinSettlement.setPaymentSettId(paymentSettlement.getId());
				weixinSettlement.setBillNo(payment.getBillNo());
				weixinSettlement.setTradeNo(tradeNo);
				weixinSettlement.setBuyerEmail(buyerEmail);
				weixinSettlement.setCreateTime(times);
				weixinSettlement.setUpdateTime(times);
				weixinSettlement.setIsActive(ParamConst.PAYMENT_SETT_IS_ACTIVE);
				WeixinSettlementSQL.addWeixinSettlement(weixinSettlement);
			}
			
		}
		return weixinSettlement;
	}
	
	Object lock_getOrderSplit = new Object();
	/**
	 * 只有在添加OrderDetail的groupId的时候才调用
	 * @param order
	 * @return
	 */
	public OrderSplit getOrderSplit(Order order,  int groupId, Tax inclusiveTax){
		OrderSplit orderSplit = null;
		synchronized (lock_getOrderSplit) {
			orderSplit = OrderSplitSQL.getOrderSplitByOrderAndGroupId(order, groupId);
			long times = System.currentTimeMillis();
			if(orderSplit == null){
				orderSplit = new OrderSplit();
				orderSplit.setId(CommonSQL.getNextSeq(TableNames.OrderSplit));
				orderSplit.setOrderId(order.getId());
				orderSplit.setOrderOriginId(order.getOrderOriginId());
				orderSplit.setUserId(order.getUserId());
				orderSplit.setPersons(1);
				orderSplit.setOrderStatus(ParamConst.ORDERSPLIT_ORDERSTATUS_OPEN);
				orderSplit.setSessionStatus(order.getSessionStatus());
				orderSplit.setRestId(order.getRestId());
				orderSplit.setRevenueId(order.getRevenueId());
				orderSplit.setTableId(order.getTableId());
				orderSplit.setCreateTime(times);
				orderSplit.setUpdateTime(times);
				orderSplit.setGroupId(groupId);
				if(inclusiveTax != null){
					orderSplit.setInclusiveTaxName(inclusiveTax.getTaxName());
					orderSplit.setInclusiveTaxPercentage(inclusiveTax.getTaxPercentage());
				}
				OrderSplitSQL.add(orderSplit);
			}else {
				orderSplit.setUpdateTime(times);
			}
		}
		return orderSplit;
	}

	public UserOpenDrawerRecord getUserOpenDrawerRecord(int restaurantId, int revenueCenterId, User openUser, int loginUserId, int sessionStatus){
		UserOpenDrawerRecord userOpenDrawerRecord = new UserOpenDrawerRecord();
		userOpenDrawerRecord.setId(CommonSQL.getNextSeq(TableNames.UserOpenDrawerRecord));
		userOpenDrawerRecord.setRestaurantId(restaurantId);
		userOpenDrawerRecord.setRevenueCenterId(revenueCenterId);
		userOpenDrawerRecord.setSessionStatus(sessionStatus);
		userOpenDrawerRecord.setUserId(openUser.getId().intValue());
		userOpenDrawerRecord.setUserName(openUser.getFirstName() + openUser.getLastName());
		userOpenDrawerRecord.setLoginUserId(loginUserId);
		userOpenDrawerRecord.setOpenTime(System.currentTimeMillis());
		UserOpenDrawerRecordSQL.addUserOpenDrawerRecord(userOpenDrawerRecord);
		return userOpenDrawerRecord;
	}
}
