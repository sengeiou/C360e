package com.alfredposclient.global;

import com.alfredbase.ParamConst;
import com.alfredbase.global.CoreData;
import com.alfredbase.javabean.CashInOut;
import com.alfredbase.javabean.ItemCategory;
import com.alfredbase.javabean.ItemDetail;
import com.alfredbase.javabean.ItemMainCategory;
import com.alfredbase.javabean.Modifier;
import com.alfredbase.javabean.Order;
import com.alfredbase.javabean.OrderDetail;
import com.alfredbase.javabean.PaymentSettlement;
import com.alfredbase.javabean.ReportDaySales;
import com.alfredbase.javabean.ReportDayTax;
import com.alfredbase.javabean.ReportHourly;
import com.alfredbase.javabean.ReportPluDayComboModifier;
import com.alfredbase.javabean.ReportPluDayItem;
import com.alfredbase.javabean.ReportPluDayModifier;
import com.alfredbase.javabean.Restaurant;
import com.alfredbase.javabean.RevenueCenter;
import com.alfredbase.javabean.UserTimeSheet;
import com.alfredbase.javabean.javabeanforhtml.DashboardTotalDetailInfo;
import com.alfredbase.javabean.model.ReportEntItem;
import com.alfredbase.javabean.model.ReportVoidItem;
import com.alfredbase.javabean.model.SessionStatus;
import com.alfredbase.store.TableNames;
import com.alfredbase.store.sql.CashInOutSQL;
import com.alfredbase.store.sql.CommonSQL;
import com.alfredbase.store.sql.ConsumingRecordsSQL;
import com.alfredbase.store.sql.ItemCategorySQL;
import com.alfredbase.store.sql.ItemDetailSQL;
import com.alfredbase.store.sql.ItemMainCategorySQL;
import com.alfredbase.store.sql.ModifierSQL;
import com.alfredbase.store.sql.OrderDetailSQL;
import com.alfredbase.store.sql.OrderDetailTaxSQL;
import com.alfredbase.store.sql.OrderModifierSQL;
import com.alfredbase.store.sql.OrderSQL;
import com.alfredbase.store.sql.OrderSplitSQL;
import com.alfredbase.store.sql.PaymentSQL;
import com.alfredbase.store.sql.PaymentSettlementSQL;
import com.alfredbase.store.sql.ReportDaySalesSQL;
import com.alfredbase.store.sql.ReportDayTaxSQL;
import com.alfredbase.store.sql.ReportHourlySQL;
import com.alfredbase.store.sql.ReportPluDayComboModifierSQL;
import com.alfredbase.store.sql.ReportPluDayItemSQL;
import com.alfredbase.store.sql.ReportPluDayModifierSQL;
import com.alfredbase.store.sql.RoundAmountSQL;
import com.alfredbase.store.sql.UserTimeSheetSQL;
import com.alfredbase.utils.BH;
import com.alfredbase.utils.IntegerUtils;
import com.alfredbase.utils.TimeUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportObjectFactory {
	private static ReportObjectFactory instance;
	private static Restaurant restaurant;
	private static RevenueCenter revenueCenter;

	private ReportObjectFactory() {
	}

	public static ReportObjectFactory getInstance() {
		if (instance == null) {
			instance = new ReportObjectFactory();
			restaurant = CoreData.getInstance().getRestaurant();
			revenueCenter = App.instance.getRevenueCenter();
		}
		return instance;
	}

	// Z-report
	public ReportDaySales loadReportDaySales(long businessDate) {
		ReportDaySales reportDaySales = null;
		if (App.instance.getBusinessDate() != businessDate) {
			reportDaySales = ReportDaySalesSQL
					.getReportDaySalesByTime(businessDate);
			return reportDaySales;
		}
		reportDaySales = ReportDaySalesSQL
				.getReportDaySalesByTime(businessDate);
		if (reportDaySales == null) {
			reportDaySales = new ReportDaySales();
			reportDaySales.setId(CommonSQL
					.getNextSeq(TableNames.ReportDaySales));
		}
		Map<String, Object> taxPriceSumMap = OrderDetailTaxSQL
				.getTaxDetail(businessDate);
		ArrayList<String> taxPriceSum = new ArrayList<String>();
		BigDecimal totalTax = BH.getBD(ParamConst.DOUBLE_ZERO);
		if (taxPriceSumMap != null) {
			taxPriceSum = (ArrayList<String>) taxPriceSumMap.get("taxPriceSum");
		}
		for (int taxPriceIndex = 0; taxPriceIndex < taxPriceSum.size(); taxPriceIndex++) {
			totalTax = BH.add(totalTax,
					BH.getBD(taxPriceSum.get(taxPriceIndex)), true);
		}
		BigDecimal totalSales = BH.getBD(ParamConst.DOUBLE_ZERO);
		BigDecimal itemSalesDicimal = BH.getBD(ParamConst.DOUBLE_ZERO);
		int itemSalesQty = 0;
		BigDecimal discountPer = BH.getBD(ParamConst.DOUBLE_ZERO);
		int discountPerQty = 0;
		BigDecimal discount = BH.getBD(ParamConst.DOUBLE_ZERO);
		int discountQty = 0;
		BigDecimal totalCard = BH.getBD(ParamConst.DOUBLE_ZERO);
		int totalCardQty = 0;

		List<Order> orderList = new ArrayList<Order>();
		List<OrderDetail> orderDetailList = new ArrayList<OrderDetail>();

		Map<String, String> focBillMap = PaymentSettlementSQL
				.getPaymentSettlementSumPaidAndCount(
						ParamConst.SETTLEMENT_TYPE_ENTERTAINMENT, businessDate);
		String focBill = BH.doubleFormat.format(BH.getBD(focBillMap
				.get("sumAmount")));
		String focBillQty = focBillMap.get("count");

		Map<String, String> voidBillsMap = PaymentSettlementSQL
				.getPaymentSettlementSumPaidAndCount(
						ParamConst.SETTLEMENT_TYPE_VOID, businessDate);
		String billVoid = BH.doubleFormat.format(BH.getBD(voidBillsMap
				.get("sumAmount")));
		String billVoidQty = voidBillsMap.get("count");

		Map<String, String> cashMap = PaymentSettlementSQL
				.getPaymentSettlementSumPaidAndCount(
						ParamConst.SETTLEMENT_TYPE_CASH, businessDate);
		String totalCash = BH.doubleFormat.format(BH.getBD(cashMap
				.get("sumAmount")));
		String totalCashQty = cashMap.get("count");

		Map<String, String> netsMap = PaymentSettlementSQL
				.getPaymentSettlementSumPaidAndCount(
						ParamConst.SETTLEMENT_TYPE_NETS, businessDate);
		String nets = BH.doubleFormat
				.format(BH.getBD(netsMap.get("sumAmount")));
		String netsQty = netsMap.get("count");
		
		Map<String, String> alipayMap = PaymentSettlementSQL
				.getPaymentSettlementSumPaidAndCount(
						ParamConst.SETTLEMENT_TYPE_ALIPAY, businessDate);
		String alipay = BH.doubleFormat
				.format(BH.getBD(alipayMap.get("sumAmount")));
		String alipayQty = alipayMap.get("count");
		
		Map<String, String> weixinpayMap = PaymentSettlementSQL
				.getPaymentSettlementSumPaidAndCount(
						ParamConst.SETTLEMENT_TYPE_WEIXIN, businessDate);
		String weixinpay = BH.doubleFormat
				.format(BH.getBD(weixinpayMap.get("sumAmount")));
		String weixinpayQty = weixinpayMap.get("count");

		Map<String, String> paypalpayMap = PaymentSettlementSQL
				.getPaymentSettlementSumPaidAndCount(
						ParamConst.SETTLEMENT_TYPE_PAYPAL, businessDate);
		String paypalpay = BH.doubleFormat
				.format(BH.getBD(paypalpayMap.get("sumAmount")));
		String paypalpayQty = paypalpayMap.get("count");

		Map<String, String> storedCardMap = PaymentSettlementSQL
				.getPaymentSettlementSumPaidAndCount(
						ParamConst.SETTLEMENT_TYPE_STORED_CARD, businessDate);
		String storedCard = BH.doubleFormat
				.format(BH.getBD(storedCardMap.get("sumAmount")));
		String storedCardQty = storedCardMap.get("count");

		Map<String, String> topUpsMap = ConsumingRecordsSQL.getSumTopUPAndRefoundByBusinessDate(businessDate);
		String topUps = BH.doubleFormat
				.format(BH.getBD(topUpsMap.get("sumAmount")));
		String topUpsQty = topUpsMap.get("count");
		
		Map<String, String> visaMap = PaymentSettlementSQL
				.getPaymentSettlementSumPaidAndCount(
						ParamConst.SETTLEMENT_TYPE_VISA, businessDate);
		String visa = BH.doubleFormat
				.format(BH.getBD(visaMap.get("sumAmount")));
		String visaQty = visaMap.get("count");

		Map<String, String> mcMap = PaymentSettlementSQL
				.getPaymentSettlementSumPaidAndCount(
						ParamConst.SETTLEMENT_TYPE_MASTERCARD, businessDate);
		String mc = BH.doubleFormat.format(BH.getBD(mcMap.get("sumAmount")));
		String mcQty = mcMap.get("count");

		Map<String, String> amexMap = PaymentSettlementSQL
				.getPaymentSettlementSumPaidAndCount(
						ParamConst.SETTLEMENT_TYPE_AMEX, businessDate);
		String amex = BH.doubleFormat
				.format(BH.getBD(amexMap.get("sumAmount")));
		String amexQty = amexMap.get("count");

		Map<String, String> jblMap = PaymentSettlementSQL
				.getPaymentSettlementSumPaidAndCount(
						ParamConst.SETTLEMENT_TYPE_JCB, businessDate);
		String jbl = BH.doubleFormat.format(BH.getBD(jblMap.get("sumAmount")));
		String jblQty = jblMap.get("count");

		Map<String, String> unionMap = PaymentSettlementSQL
				.getPaymentSettlementSumPaidAndCount(
						ParamConst.SETTLEMENT_TYPE_UNIPAY, businessDate);
		String unionPay = BH.doubleFormat.format(BH.getBD(unionMap
				.get("sumAmount")));
		String unionPayQty = unionMap.get("count");

		Map<String, String> dinerMap = PaymentSettlementSQL
				.getPaymentSettlementSumPaidAndCount(
						ParamConst.SETTLEMENT_TYPE_DINNER_INTERMATIONAL,
						businessDate);
		String diner = BH.doubleFormat.format(BH.getBD(dinerMap
				.get("sumAmount")));
		String dinerQty = dinerMap.get("count");

		Map<String, String> holdldMap = PaymentSettlementSQL
				.getPaymentSettlementSumPaidAndCount(
						ParamConst.SETTLEMENT_TYPE_BILL_ON_HOLD, businessDate);
		String holdld = BH.doubleFormat.format(BH.getBD(holdldMap
				.get("sumAmount")));
		String holdldQty = holdldMap.get("count");

		Map<String, String> totalVoidMap = OrderDetailSQL
				.getSumOrderDetailDiscountByTimeAndVoidOrFreeType(
						ParamConst.ORDERDETAIL_TYPE_VOID, businessDate);
		String itemVoid = BH.doubleFormat.format(BH.getBD(totalVoidMap
				.get("sumRealPrice")));
		String itemVoidQty = totalVoidMap.get("sumItemNum");
		Map<String, String> focItemMap = OrderDetailSQL
				.getSumOrderDetailDiscountByTimeAndVoidOrFreeType(
						ParamConst.ORDERDETAIL_TYPE_FREE, businessDate);
		String focItem = BH.doubleFormat.format(BH.getBD(focItemMap
				.get("sumRealPrice")));
		String focItemQty = focItemMap.get("sumItemNum");
		orderList = OrderSQL.getAllOrderByTime(businessDate);
		int orderQty = 0;
		int billNoQty = 0;
		int personQty = 0;
		if (orderList.isEmpty()) {
			return null;
		}
		BigDecimal inclusiveTaxAmt = BH.getBD(ParamConst.DOUBLE_ZERO);
		for (int orderIndex = 0; orderIndex < orderList.size(); orderIndex++) {
			Order order = orderList.get(orderIndex);
			// itemSalesDicimal = BH.add(itemSalesDicimal,
			// BH.getBD(order.getSubTotal()), true);
			orderDetailList = OrderDetailSQL.getAllOrderDetailsByOrder(order);
			switch (order.getDiscountType()) {
			case ParamConst.ORDER_DISCOUNT_TYPE_RATE_BY_ORDER: {
				for (int orderDetailIndex = 0; orderDetailIndex < orderDetailList
						.size(); orderDetailIndex++) {
					OrderDetail orderDetail = orderDetailList
							.get(orderDetailIndex);
					itemSalesQty += orderDetail.getItemNum();
					itemSalesDicimal = BH.add(itemSalesDicimal,
							BH.getBD(orderDetail.getRealPrice()), true);
					if(orderDetail.getOrderDetailType() == ParamConst.ORDERDETAIL_TYPE_GENERAL){
						if (orderDetail.getDiscountType() == ParamConst.ORDERDETAIL_DISCOUNT_TYPE_SUB) {
							discount = BH.add(discount, 
									BH.getBD(orderDetail.getDiscountPrice()),
									true);
							discountQty++;
						} else{ 
							if (orderDetail.getDiscountType() == ParamConst.ORDERDETAIL_DISCOUNT_TYPE_RATE) {
								discountPerQty++;
							}
							discountPer = BH.add(discountPer, BH.mul(
									BH.getBD(orderDetail.getRealPrice()),
									BH.getBD(orderDetail.getDiscountRate()), false),
									true);
						}
					}
				}
				discountPerQty++;
			}
				break;
			case ParamConst.ORDER_DISCOUNT_TYPE_SUB_BY_ORDER: {
				for (int orderDetailIndex = 0; orderDetailIndex < orderDetailList
						.size(); orderDetailIndex++) {
					OrderDetail orderDetail = orderDetailList
							.get(orderDetailIndex);
					itemSalesQty += orderDetail.getItemNum();
					itemSalesDicimal = BH.add(itemSalesDicimal,
							BH.getBD(orderDetail.getRealPrice()), true);
					if(orderDetail.getOrderDetailType() == ParamConst.ORDERDETAIL_TYPE_GENERAL){
						if (orderDetail.getDiscountType() == ParamConst.ORDERDETAIL_DISCOUNT_TYPE_SUB) {
							discount = BH.add(discount, 
									BH.getBD(orderDetail.getDiscountPrice()),
									true);
							discountQty++;
						} else if (orderDetail.getDiscountType() == ParamConst.ORDERDETAIL_DISCOUNT_TYPE_RATE) {
							discountPer = BH.add(discountPer, BH.mul(
									BH.getBD(orderDetail.getRealPrice()),
									BH.getBD(orderDetail.getDiscountRate()), false),
									true);
							discountPerQty++;
						}
					}
				}
//				discountPer = BH.add(discountPer, BH.getBD(order.getDiscountPrice()), true);
				discount = BH.add(discount,
						BH.getBD(order.getDiscountPrice()), true);
				discountQty++;
			}
				break;
			case ParamConst.ORDER_DISCOUNT_TYPE_NULL:
			case ParamConst.ORDER_DISCOUNT_TYPE_BY_ORDERDETAIL: {
				for (int orderDetailIndex = 0; orderDetailIndex < orderDetailList
						.size(); orderDetailIndex++) {
					OrderDetail orderDetail = orderDetailList
							.get(orderDetailIndex);
					itemSalesQty += orderDetail.getItemNum();
					itemSalesDicimal = BH.add(itemSalesDicimal,
							BH.getBD(orderDetail.getRealPrice()), true);
					if(orderDetail.getOrderDetailType() == ParamConst.ORDERDETAIL_TYPE_GENERAL){
						if (orderDetail.getDiscountType() == ParamConst.ORDERDETAIL_DISCOUNT_TYPE_RATE) {
							discountPer = BH
									.add(discountPer,
											BH.mul(BH.getBD(orderDetail
													.getRealPrice()), BH
													.getBD(orderDetail
															.getDiscountRate()),
													false), true);
							discountPerQty++;
						} else if (orderDetailList.get(orderDetailIndex)
								.getDiscountType() == ParamConst.ORDERDETAIL_DISCOUNT_TYPE_SUB) {
							discount = BH.add(discount,
									BH.getBD(orderDetail.getDiscountPrice()), true);
							discountQty++;
						}
					}
				}
			}
				break;
//			case ParamConst.ORDER_DISCOUNT_TYPE_NULL: {
//				for (int orderDetailIndex = 0; orderDetailIndex < orderDetailList
//						.size(); orderDetailIndex++) {
//					OrderDetail orderDetail = orderDetailList
//							.get(orderDetailIndex);
//					itemSalesQty += orderDetail.getItemNum();
//					itemSalesDicimal = BH.add(itemSalesDicimal,
//							BH.getBD(orderDetail.getRealPrice()), true);
//				}
//			}
//				break;
			default:
				break;
			}
			orderQty++;
			personQty += order.getPersons();
			int countOrderSplit = OrderSplitSQL.getOrderSplitsCountByOrder(order);
			if (countOrderSplit > 0) {
				billNoQty += countOrderSplit;
				String inclusiveTaxPrice = OrderSplitSQL.getSumOrderSplitInclusiveTaxPrice(order);
				inclusiveTaxAmt = BH.add(inclusiveTaxAmt, BH.getBD(inclusiveTaxPrice), true);
			} else {
				PaymentSettlement paymentSettlement = PaymentSettlementSQL.getPaymentSettlementsByOrderId(order.getId());
				if(paymentSettlement != null 
						&& paymentSettlement.getPaymentTypeId().intValue() != ParamConst.SETTLEMENT_TYPE_ENTERTAINMENT 
						&& paymentSettlement.getPaymentTypeId().intValue() != ParamConst.SETTLEMENT_TYPE_VOID )
				inclusiveTaxAmt = BH.add(BH.getBD(order.getInclusiveTaxPrice()), inclusiveTaxAmt, true);
				billNoQty++;
			}
			
		}

		// itemSalesDicimal = BH.add(itemSalesDicimal, BH.getBD(iVoid),
		// true);
		

		BigDecimal discountAmt = BH.add(discountPer, discount, true);
		BigDecimal totalBalancePrice = BH.getBD(RoundAmountSQL
				.getAllRoundBalancePriceByTime(businessDate));
		totalCard = BH.add(totalCard, BH.getBD(visa), true);
		totalCard = BH.add(totalCard, BH.getBD(mc), true);
		totalCard = BH.add(totalCard, BH.getBD(amex), true);
		totalCard = BH.add(totalCard, BH.getBD(jbl), true);
		totalCard = BH.add(totalCard, BH.getBD(unionPay), true);
		totalCard = BH.add(totalCard, BH.getBD(diner), true);

		totalCardQty = Integer.parseInt(visaQty) + Integer.parseInt(mcQty)
				+ Integer.parseInt(amexQty) + Integer.parseInt(jblQty)
				+ Integer.parseInt(unionPayQty) + Integer.parseInt(dinerQty);

		totalSales = itemSalesDicimal;
		totalSales = BH.sub(totalSales, discountPer, true);
		totalSales = BH.sub(totalSales, discount, true);
		totalSales = BH.sub(totalSales, BH.getBD(focBill), true);
		totalSales = BH.sub(totalSales, BH.getBD(focItem), true);
		totalSales = BH.sub(totalSales, BH.getBD(billVoid), true);
		totalSales = BH.sub(totalSales, BH.getBD(itemVoid), true);
		totalSales = BH.add(totalSales, totalBalancePrice, true);
		totalSales = BH.add(totalSales, totalTax, true);

		BigDecimal nettSales = BH.getBD(ParamConst.DOUBLE_ZERO);
		nettSales = BH.add(nettSales, totalCard, true);
		nettSales = BH.add(nettSales, BH.getBD(totalCash), true);
		nettSales = BH.add(nettSales, BH.getBD(paypalpay), true);
		nettSales = BH.add(nettSales, BH.getBD(storedCard), true);
		nettSales = BH.add(nettSales, BH.getBD(weixinpay), true);
		nettSales = BH.add(nettSales, BH.getBD(alipay), true);
		nettSales = BH.add(nettSales, BH.getBD(nets), true);
		nettSales = BH.add(nettSales, BH.getBD(holdld), true);
		nettSales = BH.add(nettSales, BH.getBD(topUps),true);
//		nettSales = BH.add(nettSales, BH.getBD(focBill), true);

		BigDecimal cashInAmt = BH
				.getBD(CashInOutSQL.getCashInSUM(businessDate));
		BigDecimal cashOutAmt = BH.getBD(CashInOutSQL
				.getCashOutSUM(businessDate));
		// BigDecimal varianceAmt = BH.abs(BH.add(BH.sub(cashInAmt, cashOutAmt,
		// false), BH.getBD(totalCash), false), true); // TODO

		reportDaySales.setRestaurantId(restaurant.getId());
		reportDaySales.setRestaurantName(restaurant.getRestaurantName());
		reportDaySales.setRevenueId(revenueCenter.getId());
		reportDaySales.setRevenueName(revenueCenter.getRevName());
		reportDaySales.setBusinessDate(businessDate);
		reportDaySales.setItemSales(itemSalesDicimal.toString());
		reportDaySales.setItemSalesQty(itemSalesQty);
		reportDaySales.setDiscountPer(discountPer.toString());
		reportDaySales.setDiscountPerQty(discountPerQty);
		reportDaySales.setDiscount(discount.toString());
		reportDaySales.setDiscountQty(discountQty);
		reportDaySales.setDiscountAmt(discountAmt.toString());
		reportDaySales.setFocItem(focItem);
		reportDaySales.setFocItemQty(Integer.parseInt(focItemQty));
		reportDaySales.setFocBill(focBill);
		reportDaySales.setFocBillQty(Integer.parseInt(focBillQty));
		reportDaySales.setBillVoid(billVoid);
		reportDaySales.setBillVoidQty(Integer.parseInt(billVoidQty));
		reportDaySales.setItemVoid(itemVoid);
		reportDaySales.setItemVoidQty(Integer.parseInt(itemVoidQty));
		reportDaySales.setTotalSales(totalSales.toString());
		reportDaySales.setCash(totalCash);
		reportDaySales.setCashQty(Integer.parseInt(totalCashQty));
		reportDaySales.setNets(nets);
		reportDaySales.setNetsQty(Integer.parseInt(netsQty));
		reportDaySales.setAlipay(alipay);
		reportDaySales.setAlipayQty(Integer.parseInt(alipayQty));
		reportDaySales.setWeixinpay(weixinpay);
		reportDaySales.setWeixinpayQty(Integer.parseInt(weixinpayQty));
		reportDaySales.setPaypalpay(paypalpay);
		reportDaySales.setPaypalpayQty(Integer.parseInt(paypalpayQty));
		reportDaySales.setStoredCard(storedCard);
		reportDaySales.setStoredCardQty(Integer.parseInt(storedCardQty));
		reportDaySales.setTopUps(topUps);
		reportDaySales.setTopUpsQty(Integer.parseInt(topUpsQty));
		reportDaySales.setVisa(visa);
		reportDaySales.setVisaQty(Integer.parseInt(visaQty));
		reportDaySales.setMc(mc);
		reportDaySales.setMcQty(Integer.parseInt(mcQty));
		reportDaySales.setAmex(amex);
		reportDaySales.setAmexQty(Integer.parseInt(amexQty));
		reportDaySales.setJbl(jbl);
		reportDaySales.setJblQty(Integer.parseInt(jblQty));
		reportDaySales.setUnionPay(unionPay);
		reportDaySales.setUnionPayQty(Integer.parseInt(unionPayQty));
		reportDaySales.setDiner(diner);
		reportDaySales.setDinerQty(Integer.parseInt(dinerQty));
		reportDaySales.setHoldld(holdld);
		reportDaySales.setHoldldQty(Integer.parseInt(holdldQty));
		reportDaySales.setTotalCard(totalCard.toString());
		reportDaySales.setTotalCardQty(totalCardQty);
		reportDaySales.setTotalCash(totalCash);
		reportDaySales.setTotalCashQty(Integer.parseInt(totalCashQty));
		reportDaySales.setNettSales(nettSales.toString());
		reportDaySales.setTotalBills(billNoQty);
		reportDaySales.setOpenCount(OrderDetailSQL
				.getOrderDetailCountByItemIdAndBusinessDate(businessDate,
						ParamConst.ITEMDETAIL_TEMP_ITEM));
		reportDaySales.setFirstReceipt(orderList.get(0).getOrderNo());
		reportDaySales.setLastReceipt(orderList.get(orderList.size() - 1)
				.getOrderNo());
		reportDaySales.setTotalTax(totalTax.toString());
		reportDaySales.setOrderQty(orderQty);
		reportDaySales.setPersonQty(personQty);
		reportDaySales.setTotalBalancePrice(totalBalancePrice.toString());
		reportDaySales.setCashInAmt(cashInAmt.toString());
		reportDaySales.setCashOutAmt(cashOutAmt.toString());
		// reportDaySales.setVarianceAmt(varianceAmt.toString());
		reportDaySales.setVarianceAmt("0.00");
		reportDaySales.setInclusiveTaxAmt(inclusiveTaxAmt.toString());
		ReportDaySalesSQL.addReportDaySales(reportDaySales);
		return reportDaySales;
	}

	/*
	 * Z report:
	 */
	public ArrayList<ReportDayTax> loadReportDayTax(
			ReportDaySales reportDaySales, long businessDate) {
		ArrayList<ReportDayTax> reportDayTaxs = new ArrayList<ReportDayTax>();
		if (App.instance.getBusinessDate() != businessDate) {
			reportDayTaxs = ReportDayTaxSQL
					.getReportDayTaxsByNowTime(businessDate);
			return reportDayTaxs;
		}
		ReportDayTaxSQL.deleteReportDayTaxByBusinessDate(businessDate);
		if (reportDayTaxs.isEmpty()) {
			ReportDayTax reportDayTax = null;
			Map<String, Object> map = OrderDetailTaxSQL
					.getTaxDetail(businessDate);
			ArrayList<String> taxPriceSum = null;
			ArrayList<String> taxNames = null;
			ArrayList<String> taxPercentages = null;
			ArrayList<Integer> taxIds = null;
			ArrayList<Integer> taxCounts = null;
			if (map != null) {
				taxPriceSum = (ArrayList<String>) map.get("taxPriceSum");
				taxNames = (ArrayList<String>) map.get("taxNames");
				taxPercentages = (ArrayList<String>) map.get("taxPercentages");
				taxIds = (ArrayList<Integer>) map.get("taxIds");
				taxCounts = (ArrayList<Integer>) map.get("taxCounts");
			}
			if (taxPriceSum != null && taxNames != null
					&& taxPercentages != null && taxIds != null
					&& taxCounts != null) {
				for (int i = 0; i < taxCounts.size(); i++) {
					reportDayTax = new ReportDayTax();
					reportDayTax.setId(CommonSQL
							.getNextSeq(TableNames.ReportDayTax));
					reportDayTax.setDaySalesId(reportDaySales.getId());
					reportDayTax.setRestaurantId(restaurant.getId());
					reportDayTax.setRestaurantName(restaurant
							.getRestaurantName());
					reportDayTax.setRevenueId(revenueCenter.getId());
					reportDayTax.setRevenueName(revenueCenter.getRevName());
					reportDayTax.setBusinessDate(businessDate);
					reportDayTax.setTaxId(taxIds.get(i));
					reportDayTax.setTaxName(taxNames.get(i));
					reportDayTax.setTaxPercentage(taxPercentages.get(i));
					reportDayTax.setTaxQty(taxCounts.get(i));
					reportDayTax.setTaxAmount(BH.getBD(taxPriceSum.get(i)).toString());
					reportDayTaxs.add(reportDayTax);
					ReportDayTaxSQL.addReportDayTax(reportDayTax);
				}
			}
		}
		return reportDayTaxs;
	}

	/*
	 * Z Report : Wrong algo. not used
	 */
//	public ArrayList<ReportPluDayItem> loadZZReportPluDayItem(long businessDate) {
//		ArrayList<ReportPluDayItem> reportPluDayItems = new ArrayList<ReportPluDayItem>();
//		if (App.instance.getBusinessDate() != businessDate) {
//			reportPluDayItems = ReportPluDayItemSQL
//					.getReportPluDayItemsByTime(businessDate);
//			return reportPluDayItems;
//		}
//		ReportPluDayItemSQL.deleteReportPluDayItemByBusinessDate(businessDate);
//
//		if (reportPluDayItems.isEmpty()) {
//			ArrayList<ItemDetail> itemDetails = new ArrayList<ItemDetail>();
//			itemDetails = ItemDetailSQL.getAllItemDetailForReport();
//			for (ItemDetail itemDetail : itemDetails) {
//				ItemCategory itemCategory = ItemCategorySQL
//						.getItemCategoryById(itemDetail.getItemCategoryId());
//				ItemMainCategory itemMainCategory = ItemMainCategorySQL
//						.getItemMainCategoryById(itemDetail
//								.getItemMainCategoryId());
//				ArrayList<OrderDetail> orderDetails = OrderDetailSQL
//						.getAllOrderDetailByTime(businessDate);
//				int sumItemNum = 0;
//				BigDecimal itemAmount = BH.getBD(ParamConst.DOUBLE_ZERO);
//				int itemVoidQty = 0;
//				BigDecimal itemVoidPrice = BH.getBD(ParamConst.DOUBLE_ZERO);
//				int billVoidQty = 0;
//				BigDecimal billVoidPrice = BH.getBD(ParamConst.DOUBLE_ZERO);
//				int itemHoldQty = 0;
//				BigDecimal itemHoldPrice = BH.getBD(ParamConst.DOUBLE_ZERO);
//				int itemFocQty = 0;
//				BigDecimal itemFocPrice = BH.getBD(ParamConst.DOUBLE_ZERO);
//				for (int i = 0; i < orderDetails.size(); i++) {
//					OrderDetail orderDetail = orderDetails.get(i);
//					if (orderDetail.getItemId().intValue() == itemDetail
//							.getId().intValue()) {
//						switch (orderDetail.getOrderDetailStatus()) {
//						case ParamConst.ORDERDETAIL_STATUS_VOID_AFTER_SETTLE:
//							billVoidQty += orderDetail.getItemNum();
//							billVoidPrice = BH.add(billVoidPrice,
//									BH.getBD(orderDetail.getRealPrice()), true);
//							break;
//						case ParamConst.ORDERDETAIL_STATUS_VOID_BEFORE_SETTLE:
//							itemVoidQty += orderDetail.getItemNum();
//							itemVoidPrice = BH.add(itemVoidPrice,
//									BH.getBD(orderDetail.getRealPrice()), true);
//							break;
//						case ParamConst.ORDERDETAIL_STATUS_ENT_AFTER_SETTLE:
//							itemFocQty += orderDetail.getItemNum();
//							break;
//						case ParamConst.ORDERDETAIL_STATUS_BOH_AFTER_SETTLE:
//							itemHoldQty += orderDetail.getItemNum();
//							itemHoldPrice = BH.add(itemHoldPrice,
//									BH.getBD(orderDetail.getRealPrice()), true);
//							break;
//						default:
//							break;
//						}
//						sumItemNum += orderDetail.getItemNum();
//						itemAmount = BH.add(itemAmount,
//								BH.getBD(orderDetail.getRealPrice()), true);
//					}
//
//				}
//				if (sumItemNum != 0) {
//					ReportPluDayItem reportPluDayItem = new ReportPluDayItem();
//					reportPluDayItem.setId(CommonSQL
//							.getNextSeq(TableNames.ReportPluDayItem));
//					reportPluDayItem.setReportNo(0); // TODO
//					reportPluDayItem.setRestaurantId(restaurant.getId());
//					reportPluDayItem.setRestaurantName(restaurant
//							.getRestaurantName());
//					reportPluDayItem.setRevenueId(revenueCenter.getId());
//					reportPluDayItem.setRevenueName(revenueCenter.getRevName());
//					reportPluDayItem.setBusinessDate(businessDate);
//					reportPluDayItem.setItemMainCategoryId(itemDetail
//							.getItemMainCategoryId());
//					reportPluDayItem.setItemMainCategoryName(itemMainCategory
//							.getMainCategoryName());
//					reportPluDayItem.setItemCategoryId(itemDetail
//							.getItemCategoryId());
//					reportPluDayItem.setItemCategoryName(itemCategory
//							.getItemCategoryName());
//					reportPluDayItem.setItemDetailId(itemDetail.getId());
//					reportPluDayItem.setItemName(itemDetail.getItemName());
//					reportPluDayItem.setItemPrice(BH.getBD(
//							itemDetail.getPrice()).toString());
//					reportPluDayItem.setItemCount(sumItemNum);
//					reportPluDayItem.setItemAmount(itemAmount.toString());
//					reportPluDayItem.setItemVoidQty(itemVoidQty);
//					reportPluDayItem.setItemVoidPrice(itemVoidPrice.toString());
//					reportPluDayItem.setBillVoidQty(billVoidQty);
//					reportPluDayItem.setBillVoidPrice(billVoidPrice.toString());
//					reportPluDayItem.setItemHoldQty(itemHoldQty);
//					reportPluDayItem.setItemHoldPrice(itemHoldPrice.toString());
//					reportPluDayItem.setItemFocQty(itemFocQty);
//					reportPluDayItem.setItemFocPrice(itemFocPrice.toString());
//					reportPluDayItems.add(reportPluDayItem);
//					ReportPluDayItemSQL.addReportPluDayItem(reportPluDayItem);
//				}
//			}
//		}
//		return reportPluDayItems;
//	}

	/*
	 * Bob ADD
	 */
	public ArrayList<ReportPluDayItem> loadReportPluDayItem(long businessDate) {

		ArrayList<ReportPluDayItem> reportPluDayItems = new ArrayList<ReportPluDayItem>();
		if (App.instance.getBusinessDate() != businessDate) {
			reportPluDayItems = ReportPluDayItemSQL
					.getReportPluDayItemsByTime(businessDate);
			return reportPluDayItems;
		}
		ReportPluDayItemSQL.deleteReportPluDayItemByBusinessDate(businessDate);

		// Get Void Items before settlement 1
		Map<Integer, Map<String, String>> voidItemsBeforeSettlementMap = OrderDetailSQL
				.getVoidItemsByBusinessDate(businessDate);
		// Get Free Items before settlement 2
		Map<Integer, Map<String, String>> focItemsBeforeSettlementMap = OrderDetailSQL
				.getFocItemsByBusinessDate(businessDate);

		// Item in void bill 3
		Map<Integer, Map<String, String>> voidItemsAfterSettlementMap = PaymentSettlementSQL
				.getItemsInVoidBillByBusinessDate(businessDate);
		Map<Integer, Map<String, String>> voidSplitItemsAfterSettlementMap = PaymentSettlementSQL
				.getItemsInVoidSplitBillByBusinessDate(businessDate);

		// Item in Free bill 4
		Map<Integer, Map<String, String>> focItemsAfterSettlementMap = PaymentSettlementSQL
				.getItemsInFocBillByBusinessDate(businessDate);
		Map<Integer, Map<String, String>> focSplitItemsAfterSettlementMap = PaymentSettlementSQL
				.getItemsInFocSplitBillByBusinessDate(businessDate);
		// Item in BOH bill 5
		Map<Integer, Map<String, String>> bohItemAfterSettlement = PaymentSettlementSQL
				.getItemsByBusinessDateAndPaymentType(
						ParamConst.SETTLEMENT_TYPE_BILL_ON_HOLD, businessDate);
		Map<Integer, Map<String, String>> bohSplitItemAfterSettlement = PaymentSettlementSQL
				.getSplitItemsByBusinessDateAndPaymentType(
						ParamConst.SETTLEMENT_TYPE_BILL_ON_HOLD, businessDate);

		
		Map<Integer, Map<String, String>> itemCountAndAmountMap = OrderDetailSQL
				.getItemCountAndItemAmountByBusinessDate( businessDate);
		if (reportPluDayItems.isEmpty()) {
			ArrayList<ItemDetail> itemDetails = new ArrayList<ItemDetail>();
			itemDetails = ItemDetailSQL.getAllItemDetailForReport();
			for (ItemDetail itemDetail : itemDetails) {
				ItemCategory itemCategory = ItemCategorySQL
						.getItemCategoryByIdForReport(itemDetail.getItemCategoryId());
				ItemMainCategory itemMainCategory = ItemMainCategorySQL
						.getItemMainCategoryByIdForReport(itemDetail
								.getItemMainCategoryId());
				// 1
				String itemVoidQty = "0";
				String itemVoidPrice = "0.00";
				if (voidItemsBeforeSettlementMap
						.containsKey(itemDetail.getId())) {
					Map<String, String> map = voidItemsBeforeSettlementMap
							.get(itemDetail.getId());
					itemVoidQty = map.get("sumItemNum");
					itemVoidPrice = map.get("sumRealPrice");
				}
				// 2
				String itemFocQty = "0";
				String itemFocPrice = "0.00";
				if (focItemsBeforeSettlementMap.containsKey(itemDetail.getId())) {
					Map<String, String> map = focItemsBeforeSettlementMap
							.get(itemDetail.getId());
					itemFocQty = map.get("sumItemNum");
					itemFocPrice = map.get("sumRealPrice");
				}
				// 3
				int billVoidQty = 0;
				BigDecimal billVoidPrice = BH.getBD(ParamConst.DOUBLE_ZERO);
				if (voidItemsAfterSettlementMap.containsKey(itemDetail.getId())) {
					Map<String, String> map = voidItemsAfterSettlementMap
							.get(itemDetail.getId());
					billVoidQty += Integer.parseInt(map.get("sumItemNum"));
					billVoidPrice = BH.add(billVoidPrice, BH.getBD(map.get("sumRealPrice")), true);
				}
				if (voidSplitItemsAfterSettlementMap.containsKey(itemDetail.getId())) {
					Map<String, String> map = voidSplitItemsAfterSettlementMap
							.get(itemDetail.getId());
					billVoidQty += Integer.parseInt(map.get("sumItemNum"));
					billVoidPrice = BH.add(billVoidPrice, BH.getBD(map.get("sumRealPrice")), true);
				}
				// 4
				int billFocQty = 0;
				BigDecimal billFocPrice = BH.getBD(ParamConst.DOUBLE_ZERO);
				if (focItemsAfterSettlementMap.containsKey(itemDetail.getId())) {
					Map<String, String> map = focItemsAfterSettlementMap
							.get(itemDetail.getId());
					billFocQty += Integer.parseInt(map.get("sumItemNum"));
					billFocPrice = BH.add(billFocPrice, BH.getBD(map.get("sumRealPrice")), true);
				}
				if (focSplitItemsAfterSettlementMap.containsKey(itemDetail.getId())) {
					Map<String, String> map = focSplitItemsAfterSettlementMap
							.get(itemDetail.getId());
					billFocQty += Integer.parseInt(map.get("sumItemNum"));
					billFocPrice = BH.add(billFocPrice, BH.getBD(map.get("sumRealPrice")), true);
				}
				// 5
				int itemHoldQty = 0;
				BigDecimal itemHoldPrice = BH.getBD(ParamConst.DOUBLE_ZERO);
				if (bohItemAfterSettlement.containsKey(itemDetail.getId())) {
					Map<String, String> map = bohItemAfterSettlement
							.get(itemDetail.getId());
					itemHoldQty += Integer.parseInt(map.get("sumItemNum"));
					itemHoldPrice = BH.add(itemHoldPrice, BH.getBD(map.get("sumRealPrice")), true);
				}
				if (bohSplitItemAfterSettlement.containsKey(itemDetail.getId())) {
					Map<String, String> map = bohSplitItemAfterSettlement
							.get(itemDetail.getId());
					itemHoldQty += Integer.parseInt(map.get("sumItemNum"));
					itemHoldPrice = BH.add(itemHoldPrice, BH.getBD(map.get("sumRealPrice")), true);
				}
				int sumItemNum = 0;
				String itemAmount = "0.00";
				if(itemCountAndAmountMap.containsKey(itemDetail.getId().intValue())){
					Map<String, String> itemCountAndAmount = itemCountAndAmountMap.get(itemDetail.getId().intValue());
					sumItemNum = Integer.parseInt(itemCountAndAmount
							.get("sumItemNum"));
					itemAmount = itemCountAndAmount.get("sumRealPrice");
				}
				
				
				if (sumItemNum != 0) {
					ReportPluDayItem reportPluDayItem = new ReportPluDayItem();
					reportPluDayItem.setId(CommonSQL
							.getNextSeq(TableNames.ReportPluDayItem));
					reportPluDayItem.setReportNo(0); // TODO
					reportPluDayItem.setRestaurantId(restaurant.getId());
					reportPluDayItem.setRestaurantName(restaurant
							.getRestaurantName());
					reportPluDayItem.setRevenueId(revenueCenter.getId());
					reportPluDayItem.setRevenueName(revenueCenter.getRevName());
					reportPluDayItem.setBusinessDate(businessDate);
					reportPluDayItem.setItemMainCategoryId(itemDetail
							.getItemMainCategoryId());
					reportPluDayItem.setItemMainCategoryName(itemMainCategory
							.getMainCategoryName());
					reportPluDayItem.setItemCategoryId(itemDetail
							.getItemCategoryId());
					reportPluDayItem.setItemCategoryName(itemCategory
							.getItemCategoryName());
					reportPluDayItem.setItemDetailId(itemDetail.getId());
					reportPluDayItem.setItemName(itemDetail.getItemName());
					reportPluDayItem.setItemPrice(BH.getBD(
							itemDetail.getPrice()).toString());
					reportPluDayItem.setItemCount(sumItemNum);
					reportPluDayItem.setItemAmount(itemAmount.toString());
					reportPluDayItem.setItemVoidQty(Integer
							.parseInt(itemVoidQty));
					reportPluDayItem.setItemVoidPrice(itemVoidPrice.toString());
					reportPluDayItem.setBillVoidQty(billVoidQty);
					reportPluDayItem.setBillVoidPrice(billVoidPrice.toString());
					reportPluDayItem.setItemHoldQty(itemHoldQty);
					reportPluDayItem.setItemHoldPrice(itemHoldPrice.toString());
					reportPluDayItem
							.setItemFocQty(Integer.parseInt(itemFocQty));
					reportPluDayItem.setItemFocPrice(itemFocPrice.toString());
					reportPluDayItem
							.setBillFocQty(billFocQty);
					reportPluDayItem.setBillFocPrice(billFocPrice.toString());
					reportPluDayItems.add(reportPluDayItem);
					ReportPluDayItemSQL.addReportPluDayItem(reportPluDayItem);
				}
			}
		}
		return reportPluDayItems;
	}

	public ArrayList<ReportPluDayModifier> loadReportPluDayModifier(
			long businessDate) {
		ArrayList<ReportPluDayModifier> reportPluDayModifiers = new ArrayList<ReportPluDayModifier>();
		if (App.instance.getBusinessDate() != businessDate) {
			reportPluDayModifiers = ReportPluDayModifierSQL
					.getReportPluDayModifiersByTime(businessDate);
			return reportPluDayModifiers;
		}
		ReportPluDayModifierSQL
				.deleteReportPluDayModifierByBusinessDate(businessDate);
		if (reportPluDayModifiers.isEmpty()) {
//			Map<Integer, Map<String, String>> voidBeforeSelettmentMap = OrderModifierSQL.getModifiersByBusinessDateAndOrderDetailType(businessDate, ParamConst.ORDERDETAIL_TYPE_VOID);
//			
//			Map<Integer, Map<String, String>> focBeforeSelettmentMap = OrderModifierSQL.getModifiersByBusinessDateAndOrderDetailType(businessDate, ParamConst.ORDERDETAIL_TYPE_FREE);
//			
//			// Item in void bill 3
//			Map<Integer, Map<String, String>> voidModifiersAfterSettlementMap = PaymentSettlementSQL
//					.getModifiersInBillByBusinessDate(ParamConst.SETTLEMENT_TYPE_VOID, businessDate);
//			// Item in Free bill 4
//			Map<Integer, Map<String, String>> focModifiersAfterSettlementMap = PaymentSettlementSQL
//					.getModifiersInBillByBusinessDate(ParamConst.SETTLEMENT_TYPE_ENTERTAINMENT, businessDate);
//			// Item in BOH bill 5
//			Map<Integer, Map<String, String>> bohModifierAfterSettlement = PaymentSettlementSQL
//					.getModifiersInBillByBusinessDate(
//							ParamConst.SETTLEMENT_TYPE_BILL_ON_HOLD, businessDate);
			
			ArrayList<Modifier> modifiers = new ArrayList<Modifier>();
			modifiers = ModifierSQL.getAllModifierForReport();
			for (Modifier modifier : modifiers) {
				if (modifier.getType() == ParamConst.ORDER_MODIFIER_TYPE_CATEGORY) {
					ReportPluDayModifier reportPluDayModifier = new ReportPluDayModifier();
					Map<String, String> modifierPriceMap = OrderModifierSQL
							.getOrderModifierByModifierId(modifier.getId(),
									businessDate);
					String modifierPrice = modifierPriceMap
							.get("sumModifierPrice") == null ? "0.00"
							: modifierPriceMap.get("sumModifierPrice");
					String modifierCount = modifierPriceMap
							.get("sumModifierNum") == null ? "0"
							: modifierPriceMap.get("sumModifierNum");
					if (modifierCount.equals("0")) {
						continue;
					}
					Map<String, String> billVoidPriceMap = OrderModifierSQL
							.getAllOrderModifierByOrderDetailType(
									modifier.getId(),
									businessDate,
									ParamConst.ORDERDETAIL_TYPE_GENERAL);
					String billVoidPrice = billVoidPriceMap
							.get("sumModifierPrice") == null ? "0.00"
							: billVoidPriceMap.get("sumModifierPrice");
					String billVoidCount = billVoidPriceMap
							.get("sumModifierNum") == null ? "0"
							: billVoidPriceMap.get("sumModifierNum");
//					Map<String, String> billSqlpitVoidPriceMap = 
					Map<String, String> voidModifierPriceMap = OrderModifierSQL
							.getAllOrderModifierByOrderDetailType(
									modifier.getId(),
									businessDate,
									ParamConst.ORDERDETAIL_TYPE_VOID);
					String voidModifierPrice = voidModifierPriceMap
							.get("sumModifierPrice") == null ? "0.00"
							: voidModifierPriceMap.get("sumModifierPrice");
					String voidModifierCount = voidModifierPriceMap
							.get("sumModifierNum") == null ? "0"
							: voidModifierPriceMap.get("sumModifierNum");
					Map<String, String> bohModifierPriceMap = OrderModifierSQL
							.getAllOrderModifierByOrderDetailType(
									modifier.getId(),
									businessDate,
									ParamConst.ORDERDETAIL_TYPE_GENERAL);
					String bohModifierPrice = bohModifierPriceMap
							.get("sumModifierPrice") == null ? "0.00"
							: bohModifierPriceMap.get("sumModifierPrice");
					String bohModifierCount = bohModifierPriceMap
							.get("sumModifierNum") == null ? "0"
							: bohModifierPriceMap.get("sumModifierNum");
					Map<String, String> focModifierPriceMap = OrderModifierSQL
							.getAllOrderModifierByOrderDetailType(
									modifier.getId(),
									businessDate,
									ParamConst.ORDERDETAIL_TYPE_FREE);
					
					Map<String, String> billFocPriceMap = OrderModifierSQL
							.getAllOrderModifierByOrderDetailType(
									modifier.getId(),
									businessDate,
									ParamConst.ORDERDETAIL_TYPE_GENERAL);
					String billFocPrice = billFocPriceMap
							.get("sumModifierPrice") == null ? "0.00"
							: billFocPriceMap.get("sumModifierPrice");
					String billFocCount = billFocPriceMap
							.get("sumModifierNum") == null ? "0"
							: billFocPriceMap.get("sumModifierNum");
					String focModifierPrice = focModifierPriceMap
							.get("sumModifierPrice") == null ? "0.00"
							: focModifierPriceMap.get("sumModifierPrice");
					String focModifierCount = focModifierPriceMap
							.get("sumModifierNum") == null ? "0"
							: focModifierPriceMap.get("sumModifierNum");
					reportPluDayModifier.setId(CommonSQL
							.getNextSeq(TableNames.ReportPluDayModifier));
					reportPluDayModifier.setReportNo(0); // TODO
					reportPluDayModifier.setRestaurantId(restaurant.getId());
					reportPluDayModifier.setRestaurantName(restaurant
							.getRestaurantName());
					reportPluDayModifier.setRevenueId(revenueCenter.getId());
					reportPluDayModifier.setRevenueName(revenueCenter
							.getRevName());
					reportPluDayModifier.setBusinessDate(businessDate);
					reportPluDayModifier.setModifierCategoryId(modifier
							.getCategoryId());
					reportPluDayModifier.setModifierCategoryName(modifier
							.getCategoryName());
					reportPluDayModifier.setModifierId(modifier.getId());
					reportPluDayModifier.setModifierName(modifier
							.getModifierName());
					reportPluDayModifier.setModifierPrice(modifierPrice);
					reportPluDayModifier.setModifierCount(Integer
							.parseInt(modifierCount));
					reportPluDayModifier.setBillVoidPrice(billVoidPrice);
					reportPluDayModifier.setBillVoidCount(Integer
							.parseInt(billVoidCount));
					reportPluDayModifier
							.setVoidModifierPrice(voidModifierPrice);
					reportPluDayModifier.setVoidModifierCount(Integer
							.parseInt(voidModifierCount));
					reportPluDayModifier.setBohModifierPrice(bohModifierPrice);
					reportPluDayModifier.setBohModifierCount(Integer
							.parseInt(bohModifierCount));
					reportPluDayModifier.setFocModifierPrice(focModifierPrice);
					reportPluDayModifier.setFocModifierCount(Integer
							.parseInt(focModifierCount));
					reportPluDayModifier.setBillFocPrice(billFocPrice);
					reportPluDayModifier.setBillFocCount(Integer
							.parseInt(billFocCount));
					reportPluDayModifier.setComboItem(modifier.getItemId());
					reportPluDayModifiers.add(reportPluDayModifier);
					ReportPluDayModifierSQL
							.addReportPluDayModifier(reportPluDayModifier);
				}
			}
		}
		return reportPluDayModifiers;
	}
	
	
	public ArrayList<ReportPluDayComboModifier> loadReportPluDayComboModifier(
			long businessDate) {
		
		ArrayList<ReportPluDayComboModifier> reportPluDayComboModifiers = new ArrayList<ReportPluDayComboModifier>();
		if (App.instance.getBusinessDate().longValue() != businessDate) {
			reportPluDayComboModifiers = ReportPluDayComboModifierSQL
					.getReportPluDayComboModifiersByTime(businessDate);
			return reportPluDayComboModifiers;
		}
		ReportPluDayComboModifierSQL
				.deleteReportPluDayComboModifierByBusinessDate(businessDate);
		if (reportPluDayComboModifiers.isEmpty()) {
			
			List<ItemDetail> itemDetails = ItemDetailSQL.getAllItemDetailForReport();
			ArrayList<Modifier> modifiers = new ArrayList<Modifier>();
			modifiers = ModifierSQL.getAllModifierForReport();
			for (ItemDetail itemDetail : itemDetails) {
				if(itemDetail.getItemType().intValue() != ParamConst.ITEMDETAIL_COMBO_ITEM){
					continue;
				}
				Map<Integer, Map<String, String>> voidBeforeSelettmentMap = OrderModifierSQL.getModifiersByBusinessDateAndOrderDetailType(itemDetail.getId().intValue(), businessDate, ParamConst.ORDERDETAIL_TYPE_VOID);
				
				Map<Integer, Map<String, String>> focBeforeSelettmentMap = OrderModifierSQL.getModifiersByBusinessDateAndOrderDetailType(itemDetail.getId().intValue(),businessDate, ParamConst.ORDERDETAIL_TYPE_FREE);
				
				// Item in void bill 3
				Map<Integer, Map<String, String>> voidModifiersAfterSettlementMap = PaymentSettlementSQL
						.getModifiersInBillByBusinessDate(itemDetail.getId().intValue(), ParamConst.SETTLEMENT_TYPE_VOID, businessDate);
				// Item in Free bill 4
				Map<Integer, Map<String, String>> focModifiersAfterSettlementMap = PaymentSettlementSQL
						.getModifiersInBillByBusinessDate(itemDetail.getId().intValue(), ParamConst.SETTLEMENT_TYPE_ENTERTAINMENT, businessDate);
				// Item in BOH bill 5
				Map<Integer, Map<String, String>> bohModifierAfterSettlement = PaymentSettlementSQL
						.getModifiersInBillByBusinessDate(itemDetail.getId().intValue(),
								ParamConst.SETTLEMENT_TYPE_BILL_ON_HOLD, businessDate);
				for(Modifier modifier : modifiers) {
				if (modifier.getType() == ParamConst.ORDER_MODIFIER_TYPE_CATEGORY) {
					ReportPluDayComboModifier reportPluDayComboModifier = new ReportPluDayComboModifier();
					Map<String, String> modifierPriceMap = OrderModifierSQL
							.getOrderModifierByItemAndModifier(itemDetail.getId().intValue(), modifier.getId().intValue(),
									businessDate);
					String modifierPrice = modifierPriceMap
							.get("sumModifierPrice") == null ? "0.00"
							: modifierPriceMap.get("sumModifierPrice");
					String modifierCount = modifierPriceMap
							.get("sumModifierNum") == null ? "0"
							: modifierPriceMap.get("sumModifierNum");
					String modifierItemPrice = modifierPriceMap
							.get("modifierItemPrice") == null ? "0.00"
							: modifierPriceMap.get("modifierItemPrice");
					
					if (modifierCount.equals("0")) {
						continue;
					}
					String billVoidPrice = "0.00";
					String billVoidCount = "0";
					if(voidModifiersAfterSettlementMap.containsKey(modifier.getId().intValue())){
						Map<String, String> billVoidPriceMap = voidModifiersAfterSettlementMap.get(modifier.getId().intValue());
						billVoidPrice = billVoidPriceMap
								.get("sumModifierPrice") == null ? "0.00"
								: billVoidPriceMap.get("sumModifierPrice");
						billVoidCount = billVoidPriceMap
								.get("sumModifierNum") == null ? "0"
								: billVoidPriceMap.get("sumModifierNum");
					}
					String voidModifierPrice = "0.00";
					String voidModifierCount = "0";
					if(voidBeforeSelettmentMap.containsKey(modifier.getId().intValue())){
						Map<String, String> voidModifierPriceMap = voidBeforeSelettmentMap.get(modifier.getId().intValue());
						voidModifierPrice = voidModifierPriceMap
								.get("sumModifierPrice") == null ? "0.00"
								: voidModifierPriceMap.get("sumModifierPrice");
						voidModifierCount = voidModifierPriceMap
								.get("sumModifierNum") == null ? "0"
								: voidModifierPriceMap.get("sumModifierNum");
					}
					
					String bohModifierPrice = "0.00";
					String bohModifierCount = "0";
					if(bohModifierAfterSettlement.containsKey(modifier.getId().intValue())){
						Map<String, String> bohModifierPriceMap = bohModifierAfterSettlement.get(modifier.getId().intValue());
						bohModifierPrice = bohModifierPriceMap
								.get("sumModifierPrice") == null ? "0.00"
								: bohModifierPriceMap.get("sumModifierPrice");
						bohModifierCount = bohModifierPriceMap
								.get("sumModifierNum") == null ? "0"
								: bohModifierPriceMap.get("sumModifierNum");
					}
					
					
					String focModifierPrice = "0.00";
					String focModifierCount = "0";
					if(focBeforeSelettmentMap.containsKey(modifier.getId().intValue())){
						Map<String, String> focModifierPriceMap = focBeforeSelettmentMap.get(modifier.getId().intValue());
						focModifierPrice = focModifierPriceMap
								.get("sumModifierPrice") == null ? "0.00"
								: focModifierPriceMap.get("sumModifierPrice");
						focModifierCount = focModifierPriceMap
								.get("sumModifierNum") == null ? "0"
								: focModifierPriceMap.get("sumModifierNum");
					}
					
					String billFocPrice = "0.00";
					String billFocCount = "0";
					if(focModifiersAfterSettlementMap.containsKey(modifier.getId().intValue())){
						Map<String, String> billFocPriceMap = focModifiersAfterSettlementMap.get(modifier.getId().intValue());
						billFocPrice = billFocPriceMap
								.get("sumModifierPrice") == null ? "0.00"
								: billFocPriceMap.get("sumModifierPrice");
						billFocCount = billFocPriceMap
								.get("sumModifierNum") == null ? "0"
								: billFocPriceMap.get("sumModifierNum");
					}
					
					reportPluDayComboModifier.setId(CommonSQL
							.getNextSeq(TableNames.ReportPluDayComboModifier));
					reportPluDayComboModifier.setReportNo(0); // TODO
					reportPluDayComboModifier.setRestaurantId(restaurant.getId());
					reportPluDayComboModifier.setRestaurantName(restaurant
							.getRestaurantName());
					reportPluDayComboModifier.setRevenueId(revenueCenter.getId());
					reportPluDayComboModifier.setRevenueName(revenueCenter
							.getRevName());
					reportPluDayComboModifier.setBusinessDate(businessDate);
					reportPluDayComboModifier.setModifierCategoryId(modifier
							.getCategoryId());
					reportPluDayComboModifier.setModifierCategoryName(modifier
							.getCategoryName());
					reportPluDayComboModifier.setModifierId(modifier.getId());
					reportPluDayComboModifier.setModifierName(modifier
							.getModifierName());
					reportPluDayComboModifier.setModifierPrice(modifierPrice);
					reportPluDayComboModifier.setModifierCount(Integer
							.parseInt(modifierCount));
					reportPluDayComboModifier.setBillVoidPrice(billVoidPrice);
					reportPluDayComboModifier.setBillVoidCount(Integer
							.parseInt(billVoidCount));
					reportPluDayComboModifier
							.setVoidModifierPrice(voidModifierPrice);
					reportPluDayComboModifier.setVoidModifierCount(Integer
							.parseInt(voidModifierCount));
					reportPluDayComboModifier.setBohModifierPrice(bohModifierPrice);
					reportPluDayComboModifier.setBohModifierCount(Integer
							.parseInt(bohModifierCount));
					reportPluDayComboModifier.setFocModifierPrice(focModifierPrice);
					reportPluDayComboModifier.setFocModifierCount(Integer
							.parseInt(focModifierCount));
					reportPluDayComboModifier.setBillFocPrice(billFocPrice);
					reportPluDayComboModifier.setBillFocCount(Integer
							.parseInt(billFocCount));
					reportPluDayComboModifier.setComboItemId(modifier.getItemId());
					reportPluDayComboModifier.setItemId(itemDetail.getId());
					reportPluDayComboModifier.setItemName(itemDetail.getItemName());
					reportPluDayComboModifier.setModifierItemPrice(modifierItemPrice);
					reportPluDayComboModifiers.add(reportPluDayComboModifier);
					ReportPluDayComboModifierSQL
							.addReportPluDayModifier(reportPluDayComboModifier);
				}
				}
			}
		}
		return reportPluDayComboModifiers;
	}

	public ArrayList<ReportHourly> loadReportHourlys(long businessDate) {
		ArrayList<ReportHourly> reportHourlys = new ArrayList<ReportHourly>();
		if (App.instance.getBusinessDate() != businessDate) {
			reportHourlys = ReportHourlySQL
					.getReportHourlysByTime(businessDate);
			return reportHourlys;
		}
		ReportHourlySQL.deleteReportHourlyByBusinessDate(businessDate);
		// Calendar nextPoint = TimeUtil.getCalendarNextPoint();
		// Calendar zeroPoint = TimeUtil.getCalendarByZero(0);
		// for (int i = zeroPoint.get(Calendar.HOUR_OF_DAY); i < nextPoint
		// .get(Calendar.HOUR_OF_DAY); i++) {
		long nowTime = System.currentTimeMillis();
		for (long i = businessDate; i < nowTime; i = TimeUtil
				.getCalendarNextPoint(i)) {
			// Calendar hourCal = Calendar.getInstance();
			// hourCal.setTimeInMillis(zeroPoint.getTimeInMillis());
			// hourCal.set(Calendar.HOUR_OF_DAY, i);
			// Calendar netHourCal = Calendar.getInstance();
			// netHourCal.setTimeInMillis(zeroPoint.getTimeInMillis());
			// netHourCal.set(Calendar.HOUR_OF_DAY, i + 1);
			Map<String, String> amountMap = PaymentSQL.getAllPaymentSumByTime(
					businessDate, i, TimeUtil.getCalendarNextPoint(i));
			if (amountMap == null || amountMap.size() < 1) {
				continue;
			} else {
				ReportHourly reportHourly = new ReportHourly();
				reportHourly.setId(CommonSQL
						.getNextSeq(TableNames.ReportHourly));
				reportHourly.setRestaurantId(restaurant.getId());
				reportHourly.setRevenueId(revenueCenter.getId());
				reportHourly.setRevenueName(revenueCenter.getRevName());
				reportHourly.setBusinessDate(businessDate);
				reportHourly.setHour(TimeUtil.getTimeHour(i));
				reportHourly.setAmountPrice(amountMap.get("sum"));
				reportHourly.setAmountQty(Integer.parseInt(amountMap
						.get("count")));
				ReportHourlySQL.addReportHourly(reportHourly);
				reportHourlys.add(reportHourly);
			}

		}
		return reportHourlys;
	}

	/*
	 * For report sync
	 * */
	public Map<String, Object> getAllReportInfo(long businessDate) {
		Map<String, Object> map = new HashMap<String, Object>();
		ReportDaySales reportDaySales = null;
		// ReportDaySalesSQL.getReportDaySalesByTime(businessDate);
		ArrayList<ReportDayTax> reportDayTaxs = new ArrayList<ReportDayTax>();
		ArrayList<ReportPluDayItem> reportPluDayItems = new ArrayList<ReportPluDayItem>();
		ArrayList<ReportPluDayModifier> reportPluDayModifiers = new ArrayList<ReportPluDayModifier>();
		ArrayList<ReportHourly> reportHourlys = new ArrayList<ReportHourly>();
		ArrayList<UserTimeSheet> userTimeSheets = new ArrayList<UserTimeSheet>();
		ArrayList<ReportPluDayComboModifier>reportPluDayComboModifiers = new ArrayList<ReportPluDayComboModifier>();
		ArrayList<CashInOut> cashInOuts = CashInOutSQL.getAllCashInOut(businessDate);
		// reportDaySales = loadReportDaySales(businessDate);
		// reportDayTaxs = loadReportDayTax(reportDaySales, businessDate);
		// reportPluDayItems = loadReportPluDayItem(businessDate);
		// reportPluDayModifiers = loadReportPluDayModifier(businessDate);
		// reportHourlys = loadReportHourlys(businessDate);
		userTimeSheets = UserTimeSheetSQL
				.getUserTimeSheetsByBusinessDate(businessDate);
//		reportPluDayComboModifiers = ReportObjectFactory
//				.getInstance().loadReportPluDayComboModifier(businessDate);
		map.put("reportDaySales", reportDaySales);
		map.put("reportDayTaxs", reportDayTaxs);
		map.put("reportPluDayItems", reportPluDayItems);
		map.put("reportPluDayModifiers", reportPluDayModifiers);
		map.put("reportHourlys", reportHourlys);
		map.put("userTimeSheets", userTimeSheets);
		map.put("cashInOuts", cashInOuts);
		map.put("reportPluDayComboModifiers", reportPluDayComboModifiers);
		return map;
	}

	public ArrayList<ReportVoidItem> loadReportVoidItem(long businessDate) {
		List<ItemDetail> itemDetails = ItemDetailSQL.getAllItemDetailForReport();
		ArrayList<ReportVoidItem> reportVoidItems = new ArrayList<ReportVoidItem>();
		Map<Integer, Map<String, String>> voidItemsBeforeSettlementMap = OrderDetailSQL
				.getVoidItemsByBusinessDate(businessDate);
		Map<Integer, Map<String, String>> voidItemsAfterSettlementMap = PaymentSettlementSQL
						.getItemsInVoidBillByBusinessDate(businessDate);
		Map<Integer, Map<String, String>> voidSplitItemsAfterSettlementMap = PaymentSettlementSQL
						.getItemsInVoidSplitBillByBusinessDate(businessDate);
		for (ItemDetail itemDetail : itemDetails) {
			int itemDetailbillVoidQty = 0;
			BigDecimal itemDetailbillVoidPrice = BH.getBD(ParamConst.DOUBLE_ZERO);
			if (voidItemsBeforeSettlementMap
					.containsKey(itemDetail.getId())) {
				Map<String, String> map = voidItemsBeforeSettlementMap
						.get(itemDetail.getId());
				itemDetailbillVoidQty += Integer.parseInt(map.get("sumItemNum"));
				itemDetailbillVoidPrice = BH.add(itemDetailbillVoidPrice, BH.getBD(map.get("sumRealPrice")), true);
			}
			
			if (voidItemsAfterSettlementMap.containsKey(itemDetail.getId())) {
				Map<String, String> map = voidItemsAfterSettlementMap
						.get(itemDetail.getId());
				itemDetailbillVoidQty += Integer.parseInt(map.get("sumItemNum"));
				itemDetailbillVoidPrice = BH.add(itemDetailbillVoidPrice, BH.getBD(map.get("sumRealPrice")), true);
			}
			if (voidSplitItemsAfterSettlementMap.containsKey(itemDetail.getId())) {
				Map<String, String> map = voidSplitItemsAfterSettlementMap
						.get(itemDetail.getId());
				itemDetailbillVoidQty += Integer.parseInt(map.get("sumItemNum"));
				itemDetailbillVoidPrice = BH.add(itemDetailbillVoidPrice, BH.getBD(map.get("sumRealPrice")), true);
			}
			
			if (itemDetailbillVoidQty != 0) {
				Map<Integer, Map<String, String>> voidBeforeSelettmentMap = OrderModifierSQL
						.getModifiersByBusinessDateAndOrderDetailType(itemDetail.getId().intValue(), businessDate, ParamConst.ORDERDETAIL_TYPE_VOID);
				Map<Integer, Map<String, String>> voidModifiersAfterSettlementMap = PaymentSettlementSQL
						.getModifiersInBillByBusinessDate(itemDetail.getId().intValue(), ParamConst.SETTLEMENT_TYPE_VOID, businessDate);
				List<ReportPluDayComboModifier> reportPluDayComboModifiers = new ArrayList<ReportPluDayComboModifier>();
				ArrayList<Modifier> modifiers = new ArrayList<Modifier>();
				modifiers = ModifierSQL.getAllModifierForReport();
				for(Modifier modifier : modifiers) {
				if (modifier.getType() == ParamConst.ORDER_MODIFIER_TYPE_CATEGORY && !IntegerUtils.isEmptyOrZero(modifier.getItemId())) {
					ReportPluDayComboModifier reportPluDayComboModifier = new ReportPluDayComboModifier();
					Map<String, String> modifierPriceMap = OrderModifierSQL
							.getOrderModifierByItemAndModifier(itemDetail.getId().intValue(), modifier.getId().intValue(),
									businessDate);
					String modifierPrice = modifierPriceMap
							.get("sumModifierPrice") == null ? "0.00"
							: modifierPriceMap.get("sumModifierPrice");
					String modifierCount = modifierPriceMap
							.get("sumModifierNum") == null ? "0"
							: modifierPriceMap.get("sumModifierNum");
					String modifierItemPrice = modifierPriceMap
							.get("modifierItemPrice") == null ? "0.00"
									: modifierPriceMap.get("modifierItemPrice");
					if (modifierCount.equals("0")) {
						continue;
					}
					
					String billVoidPrice = "0.00";
					String billVoidCount = "0";
					if(voidModifiersAfterSettlementMap.containsKey(itemDetail.getId().intValue())){
						Map<String, String> billVoidPriceMap = voidModifiersAfterSettlementMap.get(itemDetail.getId().intValue());
						billVoidPrice = billVoidPriceMap
								.get("sumModifierPrice") == null ? "0.00"
								: billVoidPriceMap.get("sumModifierPrice");
						billVoidCount = billVoidPriceMap
								.get("sumModifierNum") == null ? "0"
								: billVoidPriceMap.get("sumModifierNum");
					}
					
					String voidModifierPrice = "0.00";
					String voidModifierCount = "0";
					if(voidBeforeSelettmentMap.containsKey(itemDetail.getId().intValue())){
						Map<String, String> voidModifierPriceMap = voidBeforeSelettmentMap.get(itemDetail.getId().intValue());
						voidModifierPrice = voidModifierPriceMap
								.get("sumModifierPrice") == null ? "0.00"
								: voidModifierPriceMap.get("sumModifierPrice");
						voidModifierCount = voidModifierPriceMap
								.get("sumModifierNum") == null ? "0"
								: voidModifierPriceMap.get("sumModifierNum");
					}
					reportPluDayComboModifier.setBillVoidPrice(billVoidPrice);
					reportPluDayComboModifier.setBillVoidCount(Integer
							.parseInt(billVoidCount));
					reportPluDayComboModifier
							.setVoidModifierPrice(voidModifierPrice);
					reportPluDayComboModifier.setVoidModifierCount(Integer
							.parseInt(voidModifierCount));
					reportPluDayComboModifier.setModifierName(modifier.getModifierName());
					reportPluDayComboModifier.setModifierPrice(modifier.getPrice());
					reportPluDayComboModifier.setModifierItemPrice(modifierItemPrice);
					reportPluDayComboModifiers.add(reportPluDayComboModifier);
					}
				}
			
				
				ReportVoidItem reportVoidItem = new ReportVoidItem();
				reportVoidItem.setItemName(itemDetail.getItemName());
				reportVoidItem.setPrice(itemDetail.getPrice());
				reportVoidItem.setItemQty(itemDetailbillVoidQty);
				reportVoidItem.setAmount(itemDetailbillVoidPrice.toString());
				reportVoidItem.setComboModifiers(reportPluDayComboModifiers);
				reportVoidItems.add(reportVoidItem);
			}
		}
		return reportVoidItems;
	}

	public ArrayList<ReportEntItem> loadReportEntItem(long businessDate) {
		List<ItemDetail> itemDetails = ItemDetailSQL.getAllItemDetailForReport();
		ArrayList<ReportEntItem> reportEntItems = new ArrayList<ReportEntItem>();
		Map<Integer, Map<String, String>> focItemsBeforeSettlementMap = OrderDetailSQL
				.getFocItemsByBusinessDate(businessDate);
		Map<Integer, Map<String, String>> focItemsAfterSettlementMap = PaymentSettlementSQL
						.getItemsInFocBillByBusinessDate(businessDate);
		Map<Integer, Map<String, String>> focSplitItemsAfterSettlementMap = PaymentSettlementSQL
						.getItemsInFocSplitBillByBusinessDate(businessDate);
		for (ItemDetail itemDetail : itemDetails) {
			int itemDetailbillFocQty = 0;
			BigDecimal itemDetailbillFocPrice = BH.getBD(ParamConst.DOUBLE_ZERO);
			if (focItemsBeforeSettlementMap
					.containsKey(itemDetail.getId())) {
				Map<String, String> map = focItemsBeforeSettlementMap
						.get(itemDetail.getId());
				itemDetailbillFocQty += Integer.parseInt(map.get("sumItemNum"));
				itemDetailbillFocPrice = BH.add(itemDetailbillFocPrice, BH.getBD(map.get("sumRealPrice")), true);
			}
			
			if (focItemsAfterSettlementMap.containsKey(itemDetail.getId())) {
				Map<String, String> map = focItemsAfterSettlementMap
						.get(itemDetail.getId());
				itemDetailbillFocQty += Integer.parseInt(map.get("sumItemNum"));
				itemDetailbillFocPrice = BH.add(itemDetailbillFocPrice, BH.getBD(map.get("sumRealPrice")), true);
			}
			if (focSplitItemsAfterSettlementMap.containsKey(itemDetail.getId())) {
				Map<String, String> map = focSplitItemsAfterSettlementMap
						.get(itemDetail.getId());
				itemDetailbillFocQty += Integer.parseInt(map.get("sumItemNum"));
				itemDetailbillFocPrice = BH.add(itemDetailbillFocPrice, BH.getBD(map.get("sumRealPrice")), true);
			}
			
			if (itemDetailbillFocQty != 0) {
				Map<Integer, Map<String, String>> focBeforeSelettmentMap = OrderModifierSQL
						.getModifiersByBusinessDateAndOrderDetailType(itemDetail.getId().intValue(), businessDate, ParamConst.ORDERDETAIL_TYPE_FREE);
				Map<Integer, Map<String, String>> focModifiersAfterSettlementMap = PaymentSettlementSQL
						.getModifiersInBillByBusinessDate(itemDetail.getId().intValue(), ParamConst.SETTLEMENT_TYPE_ENTERTAINMENT, businessDate);
				List<ReportPluDayComboModifier> reportPluDayComboModifiers = new ArrayList<ReportPluDayComboModifier>();
				ArrayList<Modifier> modifiers = new ArrayList<Modifier>();
				modifiers = ModifierSQL.getAllModifierForReport();
				for(Modifier modifier : modifiers) {
				if (modifier.getType() == ParamConst.ORDER_MODIFIER_TYPE_CATEGORY && !IntegerUtils.isEmptyOrZero(modifier.getItemId())) {
					ReportPluDayComboModifier reportPluDayComboModifier = new ReportPluDayComboModifier();
					Map<String, String> modifierPriceMap = OrderModifierSQL
							.getOrderModifierByItemAndModifier(itemDetail.getId().intValue(), modifier.getId().intValue(),
									businessDate);
					String modifierPrice = modifierPriceMap
							.get("sumModifierPrice") == null ? "0.00"
							: modifierPriceMap.get("sumModifierPrice");
					String modifierCount = modifierPriceMap
							.get("sumModifierNum") == null ? "0"
							: modifierPriceMap.get("sumModifierNum");
					String modifierItemPrice = modifierPriceMap
							.get("modifierItemPrice") == null ? "0.00"
									: modifierPriceMap.get("modifierItemPrice");

					if (modifierCount.equals("0")) {
						continue;
					}
					
					String billFocPrice = "0.00";
					String billFocCount = "0";
					if(focModifiersAfterSettlementMap.containsKey(itemDetail.getId().intValue())){
						Map<String, String> billFocPriceMap = focModifiersAfterSettlementMap.get(itemDetail.getId().intValue());
						billFocPrice = billFocPriceMap
								.get("sumModifierPrice") == null ? "0.00"
								: billFocPriceMap.get("sumModifierPrice");
						billFocCount = billFocPriceMap
								.get("sumModifierNum") == null ? "0"
								: billFocPriceMap.get("sumModifierNum");
					}
					
					String focModifierPrice = "0.00";
					String focModifierCount = "0";
					if(focBeforeSelettmentMap.containsKey(itemDetail.getId().intValue())){
						Map<String, String> focModifierPriceMap = focBeforeSelettmentMap.get(itemDetail.getId().intValue());
						focModifierPrice = focModifierPriceMap
								.get("sumModifierPrice") == null ? "0.00"
								: focModifierPriceMap.get("sumModifierPrice");
						focModifierCount = focModifierPriceMap
								.get("sumModifierNum") == null ? "0"
								: focModifierPriceMap.get("sumModifierNum");
					}
					reportPluDayComboModifier.setBillFocPrice(billFocPrice);
					reportPluDayComboModifier.setBillFocCount(Integer
							.parseInt(billFocCount));
					reportPluDayComboModifier
							.setFocModifierPrice(focModifierPrice);
					reportPluDayComboModifier.setFocModifierCount(Integer
							.parseInt(focModifierCount));
					reportPluDayComboModifier.setModifierItemPrice(modifierItemPrice);
					reportPluDayComboModifiers.add(reportPluDayComboModifier);
					}
				}
			
				
				ReportEntItem reportEntItem = new ReportEntItem();
				reportEntItem.setItemName(itemDetail.getItemName());
				reportEntItem.setPrice(itemDetail.getPrice());
				reportEntItem.setItemQty(itemDetailbillFocQty);
				reportEntItem.setAmount(itemDetailbillFocPrice.toString());
				reportEntItem.setComboModifiers(reportPluDayComboModifiers);
				reportEntItems.add(reportEntItem);
			}
		}
		return reportEntItems;
	}

	public ReportDaySales loadXReportDaySales(long businessDate,
			SessionStatus sessionStatus) {
		ReportDaySales reportDaySales = null;
		// if (App.instance.getBusinessDate() != businessDate) {
		// reportDaySales = ReportDaySalesSQL
		// .getReportDaySalesByTime(businessDate);
		// return reportDaySales;
		// }
		// reportDaySales = ReportDaySalesSQL
		// .getReportDaySalesByTime(businessDate);
		// if (reportDaySales == null) {
		reportDaySales = new ReportDaySales();
		reportDaySales.setId(0);
		// }
		Map<String, Object> taxPriceSumMap = OrderDetailTaxSQL.getTaxDetail(
				businessDate, sessionStatus);
		ArrayList<String> taxPriceSum = new ArrayList<String>();
		BigDecimal totalTax = BH.getBD(ParamConst.DOUBLE_ZERO);
		if (taxPriceSumMap != null) {
			taxPriceSum = (ArrayList<String>) taxPriceSumMap.get("taxPriceSum");
		}
		for (int taxPriceIndex = 0; taxPriceIndex < taxPriceSum.size(); taxPriceIndex++) {
			totalTax = BH.add(totalTax,
					BH.getBD(taxPriceSum.get(taxPriceIndex)), true);
		}
		BigDecimal totalSales = BH.getBD(ParamConst.DOUBLE_ZERO);
		BigDecimal itemSalesDicimal = BH.getBD(ParamConst.DOUBLE_ZERO);
		int itemSalesQty = 0;
		BigDecimal discountPer = BH.getBD(ParamConst.DOUBLE_ZERO);
		int discountPerQty = 0;
		BigDecimal discount = BH.getBD(ParamConst.DOUBLE_ZERO);
		int discountQty = 0;
		BigDecimal totalCard = BH.getBD(ParamConst.DOUBLE_ZERO);
		int totalCardQty = 0;

		List<Order> orderList = new ArrayList<Order>();
		List<OrderDetail> orderDetailList = new ArrayList<OrderDetail>();

		Map<String, String> focBillMap = PaymentSettlementSQL
				.getPaymentSettlementSumPaidAndCount(
						ParamConst.SETTLEMENT_TYPE_ENTERTAINMENT, businessDate,
						sessionStatus);
		String focBill = BH.doubleFormat.format(BH.getBD(focBillMap
				.get("sumAmount")));
		String focBillQty = focBillMap.get("count");

		Map<String, String> voidBillsMap = PaymentSettlementSQL
				.getPaymentSettlementSumPaidAndCount(
						ParamConst.SETTLEMENT_TYPE_VOID, businessDate,
						sessionStatus);
		String billVoid = BH.doubleFormat.format(BH.getBD(voidBillsMap
				.get("sumAmount")));
		String billVoidQty = voidBillsMap.get("count");

		Map<String, String> cashMap = PaymentSettlementSQL
				.getPaymentSettlementSumPaidAndCount(
						ParamConst.SETTLEMENT_TYPE_CASH, businessDate,
						sessionStatus);
		String totalCash = BH.doubleFormat.format(BH.getBD(cashMap
				.get("sumAmount")));
		String totalCashQty = cashMap.get("count");



		Map<String, String> netsMap = PaymentSettlementSQL
				.getPaymentSettlementSumPaidAndCount(
						ParamConst.SETTLEMENT_TYPE_NETS, businessDate,
						sessionStatus);
		String nets = BH.doubleFormat
				.format(BH.getBD(netsMap.get("sumAmount")));
		String netsQty = netsMap.get("count");
		
		Map<String, String> alipayMap = PaymentSettlementSQL
				.getPaymentSettlementSumPaidAndCount(
						ParamConst.SETTLEMENT_TYPE_ALIPAY, businessDate,
						sessionStatus);
		String alipay = BH.doubleFormat
				.format(BH.getBD(alipayMap.get("sumAmount")));
		String alipayQty = alipayMap.get("count");
		
		Map<String, String> weixinpayMap = PaymentSettlementSQL
				.getPaymentSettlementSumPaidAndCount(
						ParamConst.SETTLEMENT_TYPE_WEIXIN, businessDate,
						sessionStatus);
		String weixinpay = BH.doubleFormat
				.format(BH.getBD(weixinpayMap.get("sumAmount")));
		String weixinpayQty = weixinpayMap.get("count");

		Map<String, String> paypalpayMap = PaymentSettlementSQL
				.getPaymentSettlementSumPaidAndCount(
						ParamConst.SETTLEMENT_TYPE_PAYPAL, businessDate,
						sessionStatus);
		String paypalpay = BH.doubleFormat
				.format(BH.getBD(paypalpayMap.get("sumAmount")));
		String paypalpayQty = paypalpayMap.get("count");

		Map<String, String> storedCardMap = PaymentSettlementSQL
				.getPaymentSettlementSumPaidAndCount(
						ParamConst.SETTLEMENT_TYPE_STORED_CARD, businessDate,
						sessionStatus);
		String storedCard = BH.doubleFormat
				.format(BH.getBD(storedCardMap.get("sumAmount")));
		String storedCardQty = storedCardMap.get("count");

		Map<String, String> topUpsMap = ConsumingRecordsSQL.getSumTopUPAndRefoundBySession(businessDate, sessionStatus);
		String topUps = BH.doubleFormat
				.format(BH.getBD(topUpsMap.get("sumAmount")));
		String topUpsQty = topUpsMap.get("count");

		Map<String, String> visaMap = PaymentSettlementSQL
				.getPaymentSettlementSumPaidAndCount(
						ParamConst.SETTLEMENT_TYPE_VISA, businessDate,
						sessionStatus);
		String visa = BH.doubleFormat
				.format(BH.getBD(visaMap.get("sumAmount")));
		String visaQty = visaMap.get("count");

		Map<String, String> mcMap = PaymentSettlementSQL
				.getPaymentSettlementSumPaidAndCount(
						ParamConst.SETTLEMENT_TYPE_MASTERCARD, businessDate,
						sessionStatus);
		String mc = BH.doubleFormat.format(BH.getBD(mcMap.get("sumAmount")));
		String mcQty = mcMap.get("count");

		Map<String, String> amexMap = PaymentSettlementSQL
				.getPaymentSettlementSumPaidAndCount(
						ParamConst.SETTLEMENT_TYPE_AMEX, businessDate,
						sessionStatus);
		String amex = BH.doubleFormat
				.format(BH.getBD(amexMap.get("sumAmount")));
		String amexQty = amexMap.get("count");

		Map<String, String> jblMap = PaymentSettlementSQL
				.getPaymentSettlementSumPaidAndCount(
						ParamConst.SETTLEMENT_TYPE_JCB, businessDate,
						sessionStatus);
		String jbl = BH.doubleFormat.format(BH.getBD(jblMap.get("sumAmount")));
		String jblQty = jblMap.get("count");

		Map<String, String> unionMap = PaymentSettlementSQL
				.getPaymentSettlementSumPaidAndCount(
						ParamConst.SETTLEMENT_TYPE_UNIPAY, businessDate,
						sessionStatus);
		String unionPay = BH.doubleFormat.format(BH.getBD(unionMap
				.get("sumAmount")));
		String unionPayQty = unionMap.get("count");

		Map<String, String> dinerMap = PaymentSettlementSQL
				.getPaymentSettlementSumPaidAndCount(
						ParamConst.SETTLEMENT_TYPE_DINNER_INTERMATIONAL,
						businessDate, sessionStatus);
		String diner = BH.doubleFormat.format(BH.getBD(dinerMap
				.get("sumAmount")));
		String dinerQty = dinerMap.get("count");

		Map<String, String> holdldMap = PaymentSettlementSQL
				.getPaymentSettlementSumPaidAndCount(
						ParamConst.SETTLEMENT_TYPE_BILL_ON_HOLD, businessDate,
						sessionStatus);
		String holdld = BH.doubleFormat.format(BH.getBD(holdldMap
				.get("sumAmount")));
		String holdldQty = holdldMap.get("count");

		Map<String, String> totalVoidMap = OrderDetailSQL
				.getSumOrderDetailDiscountByTimeAndVoidOrFreeType(
						ParamConst.ORDERDETAIL_TYPE_VOID, businessDate,
						sessionStatus);
		String itemVoid = BH.doubleFormat.format(BH.getBD(totalVoidMap
				.get("sumRealPrice")));
		String itemVoidQty = totalVoidMap.get("sumItemNum");
		Map<String, String> focItemMap = OrderDetailSQL
				.getSumOrderDetailDiscountByTimeAndVoidOrFreeType(
						ParamConst.ORDERDETAIL_TYPE_FREE, businessDate,
						sessionStatus);
		String focItem = BH.doubleFormat.format(BH.getBD(focItemMap
				.get("sumRealPrice")));
		String focItemQty = focItemMap.get("sumItemNum");
		orderList = OrderSQL.getAllOrderByTime(businessDate, sessionStatus);
		int orderQty = 0;
		int billNoQty = 0;
		int personQty = 0;
		if (orderList.isEmpty()) {
			return null;
		}
		BigDecimal inclusiveTaxAmt = BH.getBD(ParamConst.DOUBLE_ZERO);
		for (int orderIndex = 0; orderIndex < orderList.size(); orderIndex++) {
			Order order = orderList.get(orderIndex);
			// itemSalesDicimal = BH.add(itemSalesDicimal,
			// BH.getBD(order.getSubTotal()), true);
			orderDetailList = OrderDetailSQL.getAllOrderDetailsByOrder(order);
			switch (order.getDiscountType()) {
			case ParamConst.ORDER_DISCOUNT_TYPE_RATE_BY_ORDER: {
				for (int orderDetailIndex = 0; orderDetailIndex < orderDetailList
						.size(); orderDetailIndex++) {
					OrderDetail orderDetail = orderDetailList
							.get(orderDetailIndex);
					itemSalesQty += orderDetail.getItemNum();
					itemSalesDicimal = BH.add(itemSalesDicimal,
							BH.getBD(orderDetail.getRealPrice()), true);
					if(orderDetail.getOrderDetailType() == ParamConst.ORDERDETAIL_TYPE_GENERAL){
						if (orderDetail.getDiscountType() == ParamConst.ORDERDETAIL_DISCOUNT_TYPE_SUB) {
							discount = BH.add(discount, 
									BH.getBD(orderDetail.getDiscountPrice()),
									true);
							discountQty++;
						} else{ 
							if (orderDetail.getDiscountType() == ParamConst.ORDERDETAIL_DISCOUNT_TYPE_RATE) {
								discountPerQty++;
							}
							discountPer = BH.add(discountPer, BH.mul(
									BH.getBD(orderDetail.getRealPrice()),
									BH.getBD(orderDetail.getDiscountRate()), false),
									true);
						}
					}
				}
				discountPerQty++;
			}
				break;
			case ParamConst.ORDER_DISCOUNT_TYPE_SUB_BY_ORDER: {
				for (int orderDetailIndex = 0; orderDetailIndex < orderDetailList
						.size(); orderDetailIndex++) {
					OrderDetail orderDetail = orderDetailList
							.get(orderDetailIndex);
					itemSalesQty += orderDetail.getItemNum();
					itemSalesDicimal = BH.add(itemSalesDicimal,
							BH.getBD(orderDetail.getRealPrice()), true);
					if(orderDetail.getOrderDetailType() == ParamConst.ORDERDETAIL_TYPE_GENERAL){
						if (orderDetail.getDiscountType() == ParamConst.ORDERDETAIL_DISCOUNT_TYPE_SUB) {
							discount = BH.add(discount, 
									BH.getBD(orderDetail.getDiscountPrice()),
									true);
							discountQty++;
						} else if (orderDetail.getDiscountType() == ParamConst.ORDERDETAIL_DISCOUNT_TYPE_RATE) {
							discountPer = BH.add(discountPer, BH.mul(
									BH.getBD(orderDetail.getRealPrice()),
									BH.getBD(orderDetail.getDiscountRate()), false),
									true);
							discountPerQty++;
						}
					}
				}
//				discountPer = BH.add(discountPer, BH.getBD(order.getDiscountPrice()), true);
				discount = BH.add(discount,
						BH.getBD(order.getDiscountPrice()), true);
				discountQty++;
			}
				break;
			case ParamConst.ORDER_DISCOUNT_TYPE_NULL:
			case ParamConst.ORDER_DISCOUNT_TYPE_BY_ORDERDETAIL: {
				for (int orderDetailIndex = 0; orderDetailIndex < orderDetailList
						.size(); orderDetailIndex++) {
					OrderDetail orderDetail = orderDetailList
							.get(orderDetailIndex);
					itemSalesQty += orderDetail.getItemNum();
					itemSalesDicimal = BH.add(itemSalesDicimal,
							BH.getBD(orderDetail.getRealPrice()), true);
					if(orderDetail.getOrderDetailType() == ParamConst.ORDERDETAIL_TYPE_GENERAL){
						if (orderDetail.getDiscountType() == ParamConst.ORDERDETAIL_DISCOUNT_TYPE_RATE) {
							discountPer = BH
									.add(discountPer,
											BH.mul(BH.getBD(orderDetail
													.getRealPrice()), BH
													.getBD(orderDetail
															.getDiscountRate()),
													false), true);
							discountPerQty++;
						} else if (orderDetailList.get(orderDetailIndex)
								.getDiscountType() == ParamConst.ORDERDETAIL_DISCOUNT_TYPE_SUB) {
							discount = BH.add(discount,
									BH.getBD(orderDetail.getDiscountPrice()), true);
							discountQty++;
						}
					}
				}
			}
				break;
//			case ParamConst.ORDER_DISCOUNT_TYPE_NULL: {
//				for (int orderDetailIndex = 0; orderDetailIndex < orderDetailList
//						.size(); orderDetailIndex++) {
//					OrderDetail orderDetail = orderDetailList
//							.get(orderDetailIndex);
//					itemSalesQty += orderDetail.getItemNum();
//					itemSalesDicimal = BH.add(itemSalesDicimal,
//							BH.getBD(orderDetail.getRealPrice()), true);
//				}
//			}
//				break;
			default:
				break;
			}
			int countOrderSplit = OrderSplitSQL.getOrderSplitsCountByOrder(order);
			if (countOrderSplit > 0) {
				billNoQty += countOrderSplit;
				String inclusiveTaxPrice = OrderSplitSQL.getSumOrderSplitInclusiveTaxPrice(order);
				inclusiveTaxAmt = BH.add(inclusiveTaxAmt, BH.getBD(inclusiveTaxPrice), true);
			} else {
				PaymentSettlement paymentSettlement = PaymentSettlementSQL.getPaymentSettlementsByOrderId(order.getId());
				if(paymentSettlement != null 
						&& paymentSettlement.getPaymentTypeId().intValue() != ParamConst.SETTLEMENT_TYPE_ENTERTAINMENT 
						&& paymentSettlement.getPaymentTypeId().intValue() != ParamConst.SETTLEMENT_TYPE_VOID )
				inclusiveTaxAmt = BH.add(BH.getBD(order.getInclusiveTaxPrice()), inclusiveTaxAmt, true);
				billNoQty++;
			}
			orderQty++;
			personQty += order.getPersons();
		}

		BigDecimal discountAmt = BH.add(discountPer, discount, true);
		BigDecimal totalBalancePrice = BH.getBD(RoundAmountSQL
				.getAllRoundBalancePriceByTime(businessDate, sessionStatus));
		totalCard = BH.add(totalCard, BH.getBD(visa), true);
		totalCard = BH.add(totalCard, BH.getBD(mc), true);
		totalCard = BH.add(totalCard, BH.getBD(amex), true);
		totalCard = BH.add(totalCard, BH.getBD(jbl), true);
		totalCard = BH.add(totalCard, BH.getBD(unionPay), true);
		totalCard = BH.add(totalCard, BH.getBD(diner), true);

		totalCardQty = Integer.parseInt(visaQty) + Integer.parseInt(mcQty)
				+ Integer.parseInt(amexQty) + Integer.parseInt(jblQty)
				+ Integer.parseInt(unionPayQty) + Integer.parseInt(dinerQty);

		totalSales = itemSalesDicimal;
		totalSales = BH.sub(totalSales, discountPer, true);
		totalSales = BH.sub(totalSales, discount, true);
		totalSales = BH.sub(totalSales, BH.getBD(focBill), true);
		totalSales = BH.sub(totalSales, BH.getBD(focItem), true);
		totalSales = BH.sub(totalSales, BH.getBD(billVoid), true);
		totalSales = BH.sub(totalSales, BH.getBD(itemVoid), true);
		totalSales = BH.add(totalSales, totalBalancePrice, true);
		totalSales = BH.add(totalSales, totalTax, true);

		BigDecimal nettSales = BH.getBD(ParamConst.DOUBLE_ZERO);
		nettSales = BH.add(nettSales, totalCard, true);
		nettSales = BH.add(nettSales, BH.getBD(totalCash), true);
		nettSales = BH.add(nettSales, BH.getBD(paypalpay), true);
		nettSales = BH.add(nettSales, BH.getBD(storedCard), true);
		nettSales = BH.add(nettSales, BH.getBD(weixinpay), true);
		nettSales = BH.add(nettSales, BH.getBD(alipay), true);
		nettSales = BH.add(nettSales, BH.getBD(nets), true);
		nettSales = BH.add(nettSales, BH.getBD(holdld), true);
		nettSales = BH.add(nettSales, BH.getBD(topUps),true);
//		nettSales = BH.add(nettSales, BH.getBD(focBill), true);

		BigDecimal cashInAmt = BH
				.getBD(CashInOutSQL.getCashInSUM(businessDate));
		BigDecimal cashOutAmt = BH.getBD(CashInOutSQL
				.getCashOutSUM(businessDate));
		BigDecimal varianceAmt = BH.abs(
				BH.add(BH.sub(cashInAmt, cashOutAmt, false),
						BH.getBD(totalCash), false), true);

		reportDaySales.setRestaurantId(restaurant.getId());
		reportDaySales.setRestaurantName(restaurant.getRestaurantName());
		reportDaySales.setRevenueId(revenueCenter.getId());
		reportDaySales.setRevenueName(revenueCenter.getRevName());
		reportDaySales.setBusinessDate(businessDate);
		reportDaySales.setItemSales(itemSalesDicimal.toString());
		reportDaySales.setItemSalesQty(itemSalesQty);
		reportDaySales.setDiscountPer(discountPer.toString());
		reportDaySales.setDiscountPerQty(discountPerQty);
		reportDaySales.setDiscount(discount.toString());
		reportDaySales.setDiscountQty(discountQty);
		reportDaySales.setDiscountAmt(discountAmt.toString());
		reportDaySales.setFocItem(focItem);
		reportDaySales.setFocItemQty(Integer.parseInt(focItemQty));
		reportDaySales.setFocBill(focBill);
		reportDaySales.setFocBillQty(Integer.parseInt(focBillQty));
		reportDaySales.setBillVoid(billVoid);
		reportDaySales.setBillVoidQty(Integer.parseInt(billVoidQty));
		reportDaySales.setItemVoid(itemVoid);
		reportDaySales.setItemVoidQty(Integer.parseInt(itemVoidQty));
		reportDaySales.setTotalSales(totalSales.toString());
		reportDaySales.setCash(totalCash);
		reportDaySales.setCashQty(Integer.parseInt(totalCashQty));
		reportDaySales.setNets(nets);
		reportDaySales.setNetsQty(Integer.parseInt(netsQty));
		reportDaySales.setAlipay(alipay);
		reportDaySales.setAlipayQty(Integer.parseInt(alipayQty));
		reportDaySales.setWeixinpay(weixinpay);
		reportDaySales.setWeixinpayQty(Integer.parseInt(weixinpayQty));
		reportDaySales.setPaypalpay(paypalpay);
		reportDaySales.setPaypalpayQty(Integer.parseInt(paypalpayQty));
		reportDaySales.setStoredCard(storedCard);
		reportDaySales.setStoredCardQty(Integer.parseInt(storedCardQty));
		reportDaySales.setTopUps(topUps);
		reportDaySales.setTopUpsQty(Integer.parseInt(topUpsQty));
		reportDaySales.setVisa(visa);
		reportDaySales.setVisaQty(Integer.parseInt(visaQty));
		reportDaySales.setMc(mc);
		reportDaySales.setMcQty(Integer.parseInt(mcQty));
		reportDaySales.setAmex(amex);
		reportDaySales.setAmexQty(Integer.parseInt(amexQty));
		reportDaySales.setJbl(jbl);
		reportDaySales.setJblQty(Integer.parseInt(jblQty));
		reportDaySales.setUnionPay(unionPay);
		reportDaySales.setUnionPayQty(Integer.parseInt(unionPayQty));
		reportDaySales.setDiner(diner);
		reportDaySales.setDinerQty(Integer.parseInt(dinerQty));
		reportDaySales.setHoldld(holdld);
		reportDaySales.setHoldldQty(Integer.parseInt(holdldQty));
		reportDaySales.setTotalCard(totalCard.toString());
		reportDaySales.setTotalCardQty(totalCardQty);
		reportDaySales.setTotalCash(totalCash);
		reportDaySales.setTotalCashQty(Integer.parseInt(totalCashQty));
		reportDaySales.setNettSales(nettSales.toString());
		reportDaySales.setTotalBills(billNoQty);
		reportDaySales.setOpenCount(OrderDetailSQL
				.getOrderDetailCountByItemIdAndBusinessDate(businessDate,
						sessionStatus, ParamConst.ITEMDETAIL_TEMP_ITEM));
		reportDaySales.setFirstReceipt(orderList.get(0).getOrderNo());
		reportDaySales.setLastReceipt(orderList.get(orderList.size() - 1)
				.getOrderNo());
		reportDaySales.setTotalTax(totalTax.toString());
		reportDaySales.setOrderQty(orderQty);
		reportDaySales.setPersonQty(personQty);
		reportDaySales.setTotalBalancePrice(totalBalancePrice.toString());
		reportDaySales.setCashInAmt(cashInAmt.toString());
		reportDaySales.setCashOutAmt(cashOutAmt.toString());
		// reportDaySales.setVarianceAmt(varianceAmt.toString());
		reportDaySales.setVarianceAmt("0.00");
		reportDaySales.setInclusiveTaxAmt(inclusiveTaxAmt.toString());
		// ReportDaySalesSQL.addReportDaySales(reportDaySales);
		return reportDaySales;
	}

	public ArrayList<ReportDayTax> loadXReportDayTax(
			ReportDaySales reportDaySales, long businessDate,
			SessionStatus sessionStatus) {
		ArrayList<ReportDayTax> reportDayTaxs = new ArrayList<ReportDayTax>();
		// if (App.instance.getBusinessDate() != businessDate) {
		// reportDayTaxs = ReportDayTaxSQL
		// .getReportDayTaxsByNowTime(businessDate);
		// return reportDayTaxs;
		// }
		// ReportDayTaxSQL.deleteReportDayTaxByBusinessDate(businessDate);
		// if (reportDayTaxs.isEmpty()) {
		ReportDayTax reportDayTax = null;
		Map<String, Object> map = OrderDetailTaxSQL.getTaxDetail(businessDate,
				sessionStatus);
		ArrayList<String> taxPriceSum = null;
		ArrayList<String> taxNames = null;
		ArrayList<String> taxPercentages = null;
		ArrayList<Integer> taxIds = null;
		ArrayList<Integer> taxCounts = null;
		if (map != null) {
			taxPriceSum = (ArrayList<String>) map.get("taxPriceSum");
			taxNames = (ArrayList<String>) map.get("taxNames");
			taxPercentages = (ArrayList<String>) map.get("taxPercentages");
			taxIds = (ArrayList<Integer>) map.get("taxIds");
			taxCounts = (ArrayList<Integer>) map.get("taxCounts");
		}
		if (taxPriceSum != null && taxNames != null && taxPercentages != null
				&& taxIds != null && taxCounts != null) {
			for (int i = 0; i < taxCounts.size(); i++) {
				reportDayTax = new ReportDayTax();
				reportDayTax.setId(0);
				reportDayTax.setDaySalesId(reportDaySales.getId());
				reportDayTax.setRestaurantId(restaurant.getId());
				reportDayTax.setRestaurantName(restaurant.getRestaurantName());
				reportDayTax.setRevenueId(revenueCenter.getId());
				reportDayTax.setRevenueName(revenueCenter.getRevName());
				reportDayTax.setBusinessDate(businessDate);
				reportDayTax.setTaxId(taxIds.get(i));
				reportDayTax.setTaxName(taxNames.get(i));
				reportDayTax.setTaxPercentage(taxPercentages.get(i));
				reportDayTax.setTaxQty(taxCounts.get(i));
				reportDayTax.setTaxAmount(BH.getBD(taxPriceSum.get(i)).toString());
				reportDayTaxs.add(reportDayTax);
				// ReportDayTaxSQL.addReportDayTax(reportDayTax);
			}
		}
		// }
		return reportDayTaxs;
	}

	public ArrayList<ReportPluDayItem> loadXReportPluDayItem(long businessDate,
			SessionStatus sessionStatus) {


		ArrayList<ReportPluDayItem> reportPluDayItems = new ArrayList<ReportPluDayItem>();

		// Get Void Items before settlement 1
		Map<Integer, Map<String, String>> voidItemsBeforeSettlementMap = OrderDetailSQL
				.getVoidItemsByBusinessDateAndSession(businessDate,
						sessionStatus);
		// Get Free Items before settlement 2
		Map<Integer, Map<String, String>> focItemsBeforeSettlementMap = OrderDetailSQL
				.getFocItemsByBusinessDateAndSession(businessDate,
						sessionStatus);

		// Item in void bill 3
		Map<Integer, Map<String, String>> voidItemsAfterSettlementMap = PaymentSettlementSQL
				.getItemsInVoidBillByBusinessDateAndSession(businessDate,
						sessionStatus);
		Map<Integer, Map<String, String>> voidItemAflterSplitSettlementMap = PaymentSettlementSQL
				.getItemsInVoidSplitBillByBusinessDateAndSession(businessDate,
						sessionStatus);
		
		// Item in Free bill 4
		Map<Integer, Map<String, String>> focItemsAfterSettlementMap = PaymentSettlementSQL
				.getItemsInFocBillByBusinessDateAndSession(businessDate,
						sessionStatus);
		Map<Integer, Map<String, String>> focItemsAfterSplitSettlementMap = PaymentSettlementSQL
				.getItemsInFocSplitBillByBusinessDateAndSession(businessDate,
						sessionStatus);
		
		// Item in BOH bill 5
		Map<Integer, Map<String, String>> bohItemAfterSettlement = PaymentSettlementSQL
				.getItemsByBusinessDateAndPaymentTypeAndSession(
						ParamConst.SETTLEMENT_TYPE_BILL_ON_HOLD, businessDate,
						sessionStatus);
		Map<Integer, Map<String, String>> bohItemAfterSplitSettlement = PaymentSettlementSQL
				.getItemsByBusinessDateAndPaymentTypeAndSessionAfterSplit(
						ParamConst.SETTLEMENT_TYPE_BILL_ON_HOLD, businessDate,
						sessionStatus);
		
		Map<Integer, Map<String, String>> itemCountAndAmountMap = OrderDetailSQL
				.getItemCountAndItemAmountByBusinessDateAndSession(businessDate,
						sessionStatus);

		// if (reportPluDayItems.isEmpty()) {
		ArrayList<ItemDetail> itemDetails = new ArrayList<ItemDetail>();
		itemDetails = ItemDetailSQL.getAllItemDetailForReport();
		for (ItemDetail itemDetail : itemDetails) {
			ItemCategory itemCategory = ItemCategorySQL
					.getItemCategoryByIdForReport(itemDetail.getItemCategoryId());
			ItemMainCategory itemMainCategory = ItemMainCategorySQL
					.getItemMainCategoryByIdForReport(itemDetail.getItemMainCategoryId());
			// 1
			String itemVoidQty = "0";
			String itemVoidPrice = "0.00";
			if (voidItemsBeforeSettlementMap.containsKey(itemDetail.getId())) {
				Map<String, String> map = voidItemsBeforeSettlementMap
						.get(itemDetail.getId());
				itemVoidQty = map.get("sumItemNum");
				itemVoidPrice = map.get("sumRealPrice");
			}
			// 2
			String itemFocQty = "0";
			String itemFocPrice = "0.00";
			if (focItemsBeforeSettlementMap.containsKey(itemDetail.getId())) {
				Map<String, String> map = focItemsBeforeSettlementMap
						.get(itemDetail.getId());
				itemFocQty = map.get("sumItemNum");
				itemFocPrice = map.get("sumRealPrice");
			}
			// 3
			int billVoidQty = 0;
			BigDecimal billVoidPrice = BH.getBD(ParamConst.DOUBLE_ZERO);
			if (voidItemsAfterSettlementMap.containsKey(itemDetail.getId())) {
				Map<String, String> map = voidItemsAfterSettlementMap
						.get(itemDetail.getId());
				billVoidQty += Integer.parseInt(map.get("sumItemNum"));
				billVoidPrice = BH.add(billVoidPrice, BH.getBD(map.get("sumRealPrice")), true);
			}
			if(voidItemAflterSplitSettlementMap.containsKey(itemDetail.getId())){
				Map<String, String> map = voidItemAflterSplitSettlementMap
						.get(itemDetail.getId());
				billVoidQty += Integer.parseInt(map.get("sumItemNum"));
				billVoidPrice = BH.add(billVoidPrice, BH.getBD(map.get("sumRealPrice")), true);
			}
			// 4
			int billFocQty = 0;
			BigDecimal billFocPrice = BH.getBD(ParamConst.DOUBLE_ZERO);
			if (focItemsAfterSettlementMap.containsKey(itemDetail.getId())) {
				Map<String, String> map = focItemsAfterSettlementMap
						.get(itemDetail.getId());
				billFocQty += Integer.parseInt(map.get("sumItemNum"));
				billFocPrice = BH.add(billFocPrice, BH.getBD(map.get("sumRealPrice")), true);
			}
			if(focItemsAfterSplitSettlementMap.containsKey(itemDetail.getId())){
				Map<String, String> map = focItemsAfterSplitSettlementMap
						.get(itemDetail.getId());
				billFocQty += Integer.parseInt(map.get("sumItemNum"));
				billFocPrice = BH.add(billFocPrice, BH.getBD(map.get("sumRealPrice")), true);
			}
			// 5
			int itemHoldQty = 0;
			BigDecimal itemHoldPrice = BH.getBD(ParamConst.DOUBLE_ZERO);
			if (bohItemAfterSettlement.containsKey(itemDetail.getId())) {
				Map<String, String> map = bohItemAfterSettlement.get(itemDetail
						.getId());
				itemHoldQty += Integer.parseInt(map.get("sumItemNum"));
				itemHoldPrice = BH.add(itemHoldPrice, BH.getBD(map.get("sumRealPrice")), true);
			}
			
			if (bohItemAfterSplitSettlement.containsKey(itemDetail.getId())) {
				Map<String, String> map = bohItemAfterSplitSettlement.get(itemDetail
						.getId());
				itemHoldQty += Integer.parseInt(map.get("sumItemNum"));
				itemHoldPrice = BH.add(itemHoldPrice, BH.getBD(map.get("sumRealPrice")), true);
			}
			int sumItemNum = 0;
			String itemAmount = "0.00";
			if(itemCountAndAmountMap.containsKey(itemDetail.getId().intValue())){
				Map<String, String> itemCountAndAmount = itemCountAndAmountMap.get(itemDetail.getId().intValue());
				sumItemNum = Integer.parseInt(itemCountAndAmount
						.get("sumItemNum"));
				itemAmount = itemCountAndAmount.get("sumRealPrice");
			}
			
			
			if (sumItemNum != 0) {
				ReportPluDayItem reportPluDayItem = new ReportPluDayItem();
				reportPluDayItem.setId(CommonSQL
						.getNextSeq(TableNames.ReportPluDayItem));
				reportPluDayItem.setReportNo(0); // TODO
				reportPluDayItem.setRestaurantId(restaurant.getId());
				reportPluDayItem.setRestaurantName(restaurant
						.getRestaurantName());
				reportPluDayItem.setRevenueId(revenueCenter.getId());
				reportPluDayItem.setRevenueName(revenueCenter.getRevName());
				reportPluDayItem.setBusinessDate(businessDate);
				reportPluDayItem.setItemMainCategoryId(itemDetail
						.getItemMainCategoryId());
				reportPluDayItem.setItemMainCategoryName(itemMainCategory
						.getMainCategoryName());
				reportPluDayItem.setItemCategoryId(itemDetail
						.getItemCategoryId());
				reportPluDayItem.setItemCategoryName(itemCategory
						.getItemCategoryName());
				reportPluDayItem.setItemDetailId(itemDetail.getId());
				reportPluDayItem.setItemName(itemDetail.getItemName());
				reportPluDayItem.setItemPrice(BH.getBD(itemDetail.getPrice())
						.toString());
				reportPluDayItem.setItemCount(sumItemNum);
				reportPluDayItem.setItemAmount(itemAmount.toString());
				reportPluDayItem.setItemVoidQty(Integer.parseInt(itemVoidQty));
				reportPluDayItem.setItemVoidPrice(itemVoidPrice.toString());
				reportPluDayItem.setBillVoidQty(billVoidQty);
				reportPluDayItem.setBillVoidPrice(billVoidPrice.toString());
				reportPluDayItem.setItemHoldQty(itemHoldQty);
				reportPluDayItem.setItemHoldPrice(itemHoldPrice.toString());
				reportPluDayItem.setItemFocQty(Integer.parseInt(itemFocQty));
				reportPluDayItem.setItemFocPrice(itemFocPrice.toString());
				reportPluDayItem.setBillFocQty(billFocQty);
				reportPluDayItem.setBillFocPrice(billFocPrice.toString());
				reportPluDayItems.add(reportPluDayItem);
			}
		}
		return reportPluDayItems;
	}

	public ArrayList<ReportPluDayModifier> loadXReportPluDayModifier(
			long businessDate, SessionStatus sessionStatus) {
		ArrayList<ReportPluDayModifier> reportPluDayModifiers = new ArrayList<ReportPluDayModifier>();
//		ArrayList<Modifier> modifiers = new ArrayList<Modifier>();
//		modifiers = ModifierSQL.getAllModifier();
//		for (Modifier modifier : modifiers) {
//			if (modifier.getType() == ParamConst.ORDER_MODIFIER_TYPE_CATEGORY) {
//				ReportPluDayModifier reportPluDayModifier = new ReportPluDayModifier();
//				Map<String, String> modifierPriceMap = OrderModifierSQL
//						.getOrderModifierByModifierId(modifier.getId(),
//								businessDate, sessionStatus);
//				String modifierPrice = modifierPriceMap.get("sumModifierPrice") == null ? "0.00"
//						: modifierPriceMap.get("sumModifierPrice");
//				String modifierCount = modifierPriceMap.get("sumModifierNum") == null ? "0"
//						: modifierPriceMap.get("sumModifierNum");
//				if (modifierCount.equals("0")) {
//					continue;
//				}
//				Map<String, String> billVoidPriceMap = OrderModifierSQL
//						.getAllOrderModifierByOrderDetailType(modifier.getId(),
//								businessDate, sessionStatus,
//								ParamConst.ORDERDETAIL_TYPE_GENERAL);
//				String billVoidPrice = billVoidPriceMap.get("sumModifierPrice") == null ? "0.00"
//						: billVoidPriceMap.get("sumModifierPrice");
//				String billVoidCount = billVoidPriceMap.get("sumModifierNum") == null ? "0"
//						: billVoidPriceMap.get("sumModifierNum");
//				Map<String, String> voidModifierPriceMap = OrderModifierSQL
//						.getAllOrderModifierByOrderDetailType(
//								modifier.getId(),
//								businessDate,
//								sessionStatus,
//								ParamConst.ORDERDETAIL_TYPE_VOID);
//				String voidModifierPrice = voidModifierPriceMap
//						.get("sumModifierPrice") == null ? "0.00"
//						: voidModifierPriceMap.get("sumModifierPrice");
//				String voidModifierCount = voidModifierPriceMap
//						.get("sumModifierNum") == null ? "0"
//						: voidModifierPriceMap.get("sumModifierNum");
//				Map<String, String> bohModifierPriceMap = OrderModifierSQL
//						.getAllOrderModifierByOrderDetailType(modifier.getId(),
//								businessDate, sessionStatus,
//								ParamConst.ORDERDETAIL_TYPE_GENERAL);
//				String bohModifierPrice = bohModifierPriceMap
//						.get("sumModifierPrice") == null ? "0.00"
//						: bohModifierPriceMap.get("sumModifierPrice");
//				String bohModifierCount = bohModifierPriceMap
//						.get("sumModifierNum") == null ? "0"
//						: bohModifierPriceMap.get("sumModifierNum");
//				Map<String, String> focModifierPriceMap = OrderModifierSQL
//						.getAllOrderModifierByOrderDetailType(modifier.getId(),
//								businessDate, sessionStatus,
//								ParamConst.ORDERDETAIL_TYPE_FREE);
//				String focModifierPrice = focModifierPriceMap
//						.get("sumModifierPrice") == null ? "0.00"
//						: focModifierPriceMap.get("sumModifierPrice");
//				String focModifierCount = focModifierPriceMap
//						.get("sumModifierNum") == null ? "0"
//						: focModifierPriceMap.get("sumModifierNum");
//				
//				Map<String, String> billFocPriceMap = OrderModifierSQL
//						.getAllOrderModifierByOrderDetailType(modifier.getId(),
//								businessDate, sessionStatus,
//								ParamConst.ORDERDETAIL_TYPE_FREE);
//				String billFocPrice = billFocPriceMap
//						.get("sumModifierPrice") == null ? "0.00"
//						: focModifierPriceMap.get("sumModifierPrice");
//				String billFocCount = billFocPriceMap
//						.get("sumModifierNum") == null ? "0"
//						: focModifierPriceMap.get("sumModifierNum");
//				reportPluDayModifier.setId(0);
//				reportPluDayModifier.setReportNo(0); // TODO
//				reportPluDayModifier.setRestaurantId(restaurant.getId());
//				reportPluDayModifier.setRestaurantName(restaurant
//						.getRestaurantName());
//				reportPluDayModifier.setRevenueId(revenueCenter.getId());
//				reportPluDayModifier.setRevenueName(revenueCenter.getRevName());
//				reportPluDayModifier.setBusinessDate(businessDate);
//				reportPluDayModifier.setModifierCategoryId(modifier
//						.getCategoryId());
//				reportPluDayModifier.setModifierCategoryName(modifier
//						.getCategoryName());
//				reportPluDayModifier.setModifierId(modifier.getId());
//				reportPluDayModifier
//						.setModifierName(modifier.getModifierName());
//				reportPluDayModifier.setModifierPrice(modifierPrice);
//				reportPluDayModifier.setModifierCount(Integer
//						.parseInt(modifierCount));
//				reportPluDayModifier.setBillVoidPrice(billVoidPrice);
//				reportPluDayModifier.setBillVoidCount(Integer
//						.parseInt(billVoidCount));
//				reportPluDayModifier.setVoidModifierPrice(voidModifierPrice);
//				reportPluDayModifier.setVoidModifierCount(Integer
//						.parseInt(voidModifierCount));
//				reportPluDayModifier.setBohModifierPrice(bohModifierPrice);
//				reportPluDayModifier.setBohModifierCount(Integer
//						.parseInt(bohModifierCount));
//				reportPluDayModifier.setFocModifierPrice(focModifierPrice);
//				reportPluDayModifier.setFocModifierCount(Integer
//						.parseInt(focModifierCount));
//				reportPluDayModifier.setBillFocPrice(billFocPrice);
//				reportPluDayModifier.setBillFocCount(Integer
//						.parseInt(billFocCount));
//				reportPluDayModifier.setComboItem(modifier.getItemId().intValue());
//				reportPluDayModifiers.add(reportPluDayModifier);
//			}
//		}
		return reportPluDayModifiers;
	}
	
	
	public ArrayList<ReportPluDayComboModifier> loadXReportPluDayComboModifier(
			long businessDate, SessionStatus sessionStatus) {
		
		ArrayList<ReportPluDayComboModifier> reportPluDayComboModifiers = new ArrayList<ReportPluDayComboModifier>();
//		if (App.instance.getBusinessDate() != businessDate) {
//			reportPluDayComboModifiers = ReportPluDayComboModifierSQL
//					.getReportPluDayComboModifiersByTime(businessDate);
//			return reportPluDayComboModifiers;
//		}
//		ReportPluDayComboModifierSQL
//				.deleteReportPluDayComboModifierByBusinessDate(businessDate);
//		if (reportPluDayComboModifiers.isEmpty()) {
			
			List<ItemDetail> itemDetails = ItemDetailSQL.getAllItemDetailForReport();
			ArrayList<Modifier> modifiers = new ArrayList<Modifier>();
			modifiers = ModifierSQL.getAllModifierForReport();
			for (ItemDetail itemDetail : itemDetails) {
				if(itemDetail.getItemType().intValue() != ParamConst.ITEMDETAIL_COMBO_ITEM){
					continue;
				}
				Map<Integer, Map<String, String>> voidBeforeSelettmentMap = OrderModifierSQL.getModifiersBySessionStatusAndOrderDetailType(itemDetail.getId().intValue(), businessDate, ParamConst.ORDERDETAIL_TYPE_VOID, sessionStatus);
				
				Map<Integer, Map<String, String>> focBeforeSelettmentMap = OrderModifierSQL.getModifiersBySessionStatusAndOrderDetailType(itemDetail.getId().intValue(),businessDate, ParamConst.ORDERDETAIL_TYPE_FREE, sessionStatus);
				
				// Item in void bill 3
				Map<Integer, Map<String, String>> voidModifiersAfterSettlementMap = PaymentSettlementSQL
						.getModifiersInBillBySessionStatus(itemDetail.getId().intValue(), ParamConst.SETTLEMENT_TYPE_VOID, businessDate, sessionStatus);
				// Item in Free bill 4
				Map<Integer, Map<String, String>> focModifiersAfterSettlementMap = PaymentSettlementSQL
						.getModifiersInBillBySessionStatus(itemDetail.getId().intValue(), ParamConst.SETTLEMENT_TYPE_ENTERTAINMENT, businessDate, sessionStatus);
				// Item in BOH bill 5
				Map<Integer, Map<String, String>> bohModifierAfterSettlement = PaymentSettlementSQL
						.getModifiersInBillBySessionStatus(itemDetail.getId().intValue(),
								ParamConst.SETTLEMENT_TYPE_BILL_ON_HOLD, businessDate, sessionStatus);
				for(Modifier modifier : modifiers) {
				if (modifier.getType() == ParamConst.ORDER_MODIFIER_TYPE_CATEGORY) {
					ReportPluDayComboModifier reportPluDayComboModifier = new ReportPluDayComboModifier();
					Map<String, String> modifierPriceMap = OrderModifierSQL
							.getOrderModifierByItemAndModifierAndSession(itemDetail.getId().intValue(), modifier.getId().intValue(),
									businessDate, sessionStatus);
					String modifierPrice = modifierPriceMap
							.get("sumModifierPrice") == null ? "0.00"
							: modifierPriceMap.get("sumModifierPrice");
					String modifierCount = modifierPriceMap
							.get("sumModifierNum") == null ? "0"
							: modifierPriceMap.get("sumModifierNum");
					String modifierItemPrice = modifierPriceMap
							.get("modifierItemPrice") == null ? "0.00"
							: modifierPriceMap.get("modifierItemPrice");
					if (modifierCount.equals("0")) {
						continue;
					}
					String billVoidPrice = "0.00";
					String billVoidCount = "0";
					if(voidModifiersAfterSettlementMap.containsKey(modifier.getId().intValue())){
						Map<String, String> billVoidPriceMap = voidModifiersAfterSettlementMap.get(modifier.getId().intValue());
						billVoidPrice = billVoidPriceMap
								.get("sumModifierPrice") == null ? "0.00"
								: billVoidPriceMap.get("sumModifierPrice");
						billVoidCount = billVoidPriceMap
								.get("sumModifierNum") == null ? "0"
								: billVoidPriceMap.get("sumModifierNum");
					}
					String voidModifierPrice = "0.00";
					String voidModifierCount = "0";
					if(voidBeforeSelettmentMap.containsKey(modifier.getId().intValue())){
						Map<String, String> voidModifierPriceMap = voidBeforeSelettmentMap.get(modifier.getId().intValue());
						voidModifierPrice = voidModifierPriceMap
								.get("sumModifierPrice") == null ? "0.00"
								: voidModifierPriceMap.get("sumModifierPrice");
						voidModifierCount = voidModifierPriceMap
								.get("sumModifierNum") == null ? "0"
								: voidModifierPriceMap.get("sumModifierNum");
					}
					
					String bohModifierPrice = "0.00";
					String bohModifierCount = "0";
					if(bohModifierAfterSettlement.containsKey(modifier.getId().intValue())){
						Map<String, String> bohModifierPriceMap = bohModifierAfterSettlement.get(modifier.getId().intValue());
						bohModifierPrice = bohModifierPriceMap
								.get("sumModifierPrice") == null ? "0.00"
								: bohModifierPriceMap.get("sumModifierPrice");
						bohModifierCount = bohModifierPriceMap
								.get("sumModifierNum") == null ? "0"
								: bohModifierPriceMap.get("sumModifierNum");
					}
					
					
					String focModifierPrice = "0.00";
					String focModifierCount = "0";
					if(focBeforeSelettmentMap.containsKey(modifier.getId().intValue())){
						Map<String, String> focModifierPriceMap = focBeforeSelettmentMap.get(modifier.getId().intValue());
						focModifierPrice = focModifierPriceMap
								.get("sumModifierPrice") == null ? "0.00"
								: focModifierPriceMap.get("sumModifierPrice");
						focModifierCount = focModifierPriceMap
								.get("sumModifierNum") == null ? "0"
								: focModifierPriceMap.get("sumModifierNum");
					}
					
					String billFocPrice = "0.00";
					String billFocCount = "0";
					if(focModifiersAfterSettlementMap.containsKey(modifier.getId().intValue())){
						Map<String, String> billFocPriceMap = focModifiersAfterSettlementMap.get(modifier.getId().intValue());
						billFocPrice = billFocPriceMap
								.get("sumModifierPrice") == null ? "0.00"
								: billFocPriceMap.get("sumModifierPrice");
						billFocCount = billFocPriceMap
								.get("sumModifierNum") == null ? "0"
								: billFocPriceMap.get("sumModifierNum");
					}
					
					reportPluDayComboModifier.setId(CommonSQL
							.getNextSeq(TableNames.ReportPluDayComboModifier));
					reportPluDayComboModifier.setReportNo(0); // TODO
					reportPluDayComboModifier.setRestaurantId(restaurant.getId());
					reportPluDayComboModifier.setRestaurantName(restaurant
							.getRestaurantName());
					reportPluDayComboModifier.setRevenueId(revenueCenter.getId());
					reportPluDayComboModifier.setRevenueName(revenueCenter
							.getRevName());
					reportPluDayComboModifier.setBusinessDate(businessDate);
					reportPluDayComboModifier.setModifierCategoryId(modifier
							.getCategoryId());
					reportPluDayComboModifier.setModifierCategoryName(modifier
							.getCategoryName());
					reportPluDayComboModifier.setModifierId(modifier.getId());
					reportPluDayComboModifier.setModifierName(modifier
							.getModifierName());
					reportPluDayComboModifier.setModifierPrice(modifierPrice);
					reportPluDayComboModifier.setModifierCount(Integer
							.parseInt(modifierCount));
					reportPluDayComboModifier.setBillVoidPrice(billVoidPrice);
					reportPluDayComboModifier.setBillVoidCount(Integer
							.parseInt(billVoidCount));
					reportPluDayComboModifier
							.setVoidModifierPrice(voidModifierPrice);
					reportPluDayComboModifier.setVoidModifierCount(Integer
							.parseInt(voidModifierCount));
					reportPluDayComboModifier.setBohModifierPrice(bohModifierPrice);
					reportPluDayComboModifier.setBohModifierCount(Integer
							.parseInt(bohModifierCount));
					reportPluDayComboModifier.setFocModifierPrice(focModifierPrice);
					reportPluDayComboModifier.setFocModifierCount(Integer
							.parseInt(focModifierCount));
					reportPluDayComboModifier.setBillFocPrice(billFocPrice);
					reportPluDayComboModifier.setBillFocCount(Integer
							.parseInt(billFocCount));
					reportPluDayComboModifier.setComboItemId(modifier.getItemId());
					reportPluDayComboModifier.setItemId(itemDetail.getId());
					reportPluDayComboModifier.setItemName(itemDetail.getItemName());
					reportPluDayComboModifier.setModifierItemPrice(modifierItemPrice);
					reportPluDayComboModifiers.add(reportPluDayComboModifier);
//					ReportPluDayComboModifierSQL
//							.addReportPluDayModifier(reportPluDayComboModifier);
//				}
				}
			}
		}
		return reportPluDayComboModifiers;
	}

	public ArrayList<ReportHourly> loadXReportHourlys(long businessDate,
			SessionStatus sessionStatus) {
		ArrayList<ReportHourly> reportHourlys = new ArrayList<ReportHourly>();
		// if (App.instance.getBusinessDate() != businessDate) {
		// reportHourlys = ReportHourlySQL.getReportHourlysByTime(businessDate);
		// return reportHourlys;
		// }
		// ReportHourlySQL.deleteReportHourlyByBusinessDate(businessDate);
		// Calendar nextPoint = TimeUtil.getCalendarNextPoint();
		// Calendar zeroPoint = TimeUtil.getCalendarByZero(0);
		long nowTime = System.currentTimeMillis();
		for (long i = sessionStatus.getTime(); i < nowTime; i = TimeUtil
				.getCalendarNextPoint(i)) {
			// Calendar hourCal = Calendar.getInstance();
			// hourCal.setTimeInMillis(zeroPoint.getTimeInMillis());
			// hourCal.set(Calendar.HOUR_OF_DAY, i);
			// Calendar netHourCal = Calendar.getInstance();
			// netHourCal.setTimeInMillis(zeroPoint.getTimeInMillis());
			// netHourCal.set(Calendar.HOUR_OF_DAY, i + 1);
			Map<String, String> amountMap = PaymentSQL.getAllPaymentSumByTime(
					businessDate, i, TimeUtil.getCalendarNextPoint(i));
			if (amountMap == null || amountMap.size() < 1) {
				continue;
			} else {
				ReportHourly reportHourly = new ReportHourly();
				reportHourly.setId(0);
				reportHourly.setRestaurantId(restaurant.getId());
				reportHourly.setRevenueId(revenueCenter.getId());
				reportHourly.setRevenueName(revenueCenter.getRevName());
				reportHourly.setBusinessDate(businessDate);
				reportHourly.setHour(TimeUtil.getTimeHour(i));
				reportHourly.setAmountPrice(amountMap.get("sum"));
				reportHourly.setAmountQty(Integer.parseInt(amountMap
						.get("count")));
				// ReportHourlySQL.addReportHourly(reportHourly);
				reportHourlys.add(reportHourly);
			}

		}
		return reportHourlys;
	}

	public Map<String, Object> getXReportInfo(long businessDate,
			SessionStatus sessionStatus) {
		Map<String, Object> map = new HashMap<String, Object>();
		ReportDaySales reportDaySales = null;
		// ReportDaySalesSQL.getReportDaySalesByTime(businessDate);
		reportDaySales = loadXReportDaySales(businessDate, sessionStatus);
		ArrayList<ReportDayTax> reportDayTaxs = new ArrayList<ReportDayTax>();
		ArrayList<ReportPluDayItem> reportPluDayItems = new ArrayList<ReportPluDayItem>();
		ArrayList<ReportPluDayModifier> reportPluDayModifiers = new ArrayList<ReportPluDayModifier>();
		ArrayList<ReportHourly> reportHourlys = new ArrayList<ReportHourly>();
		ArrayList<ReportPluDayComboModifier>reportPluDayComboModifiers = new ArrayList<ReportPluDayComboModifier>();
		// ArrayList<UserTimeSheet> userTimeSheets = new
		// ArrayList<UserTimeSheet>();
		// reportDaySales = loadXReportDaySales(businessDate, sessionStatus);
		reportDayTaxs = loadXReportDayTax(reportDaySales, businessDate,
				sessionStatus);
		reportPluDayItems = loadXReportPluDayItem(businessDate, sessionStatus);
		reportPluDayModifiers = loadXReportPluDayModifier(businessDate,
				sessionStatus);
		reportPluDayComboModifiers = loadXReportPluDayComboModifier(businessDate, sessionStatus);
		reportHourlys = loadXReportHourlys(businessDate, sessionStatus);
		// userTimeSheets =
		// UserTimeSheetSQL.getUserTimeSheetsByBusinessDate(businessDate);
		map.put("reportDaySales", reportDaySales);
		map.put("reportDayTaxs", reportDayTaxs);
		map.put("reportPluDayItems", reportPluDayItems);
		map.put("reportPluDayModifiers", reportPluDayModifiers);
		map.put("reportHourlys", reportHourlys);
		map.put("reportPluDayComboModifiers", reportPluDayComboModifiers);
		map.put("sessionStatus", sessionStatus);
		// map.put("userTimeSheets", userTimeSheets);
		return map;
	}
	
	/*Filter void/ENT item in PLUDayItem*/
	public ArrayList<ReportPluDayItem> getPLUItemWithoutVoidEnt(ArrayList<ReportPluDayItem> reportPluDayItems) {
		ArrayList<ReportPluDayItem> filteredPluDayItems = new ArrayList<ReportPluDayItem>();
		if (reportPluDayItems!=null) {
			Gson gson = new Gson();
			String jsonArray = gson.toJson(reportPluDayItems);
			ArrayList<ReportPluDayItem> reportPluDayItemList = gson.fromJson(jsonArray, new TypeToken<ArrayList<ReportPluDayItem>>(){}.getType());
			for (int j = 0; j < reportPluDayItemList.size(); j++) {
				ReportPluDayItem tmpItem = reportPluDayItemList.get(j);
				int totalQty = tmpItem.getItemCount();
				BigDecimal totalAmount = BH.getBD(tmpItem.getItemAmount());
				
				int itemVoidQty = tmpItem.getItemVoidQty();
				if (itemVoidQty>0) {
					totalQty = totalQty - itemVoidQty;
					totalAmount = BH.sub(totalAmount,BH.getBD(tmpItem.getItemVoidPrice()), true);
				}
				
				int billVoidQty = tmpItem.getBillVoidQty();
				if (billVoidQty>0) {
					totalQty = totalQty - billVoidQty;
					totalAmount = BH.sub(totalAmount,BH.getBD(tmpItem.getBillVoidPrice()), true);
				}
	
				int itemFocQty = tmpItem.getItemFocQty();
				if (itemFocQty>0) {
	//				totalQty = totalQty - itemFocQty;
					totalAmount = BH.sub(totalAmount,BH.getBD(tmpItem.getItemFocPrice()), true);
				}			
				int billFocQty = tmpItem.getBillFocQty();
				if (billFocQty>0) {
	//				totalQty = totalQty - billFocQty;
					totalAmount = BH.sub(totalAmount,BH.getBD(tmpItem.getBillFocPrice()), true);
				}
				if (totalQty>0) {
					tmpItem.setItemCount(totalQty);	
					tmpItem.setItemAmount(totalAmount.toString());
					filteredPluDayItems.add(tmpItem);
				}
			}
		}
		return filteredPluDayItems;
	}
	
    // Main POS Dashboard
	public ArrayList<DashboardTotalDetailInfo> loadDaySalesDashBoard(long bizDateNow) {

		ArrayList<DashboardTotalDetailInfo> totalDetailInfos = new ArrayList<DashboardTotalDetailInfo>();
		
		long bizDate[] = new long[3];
		
		//recent 3 days data
		bizDate[0] = TimeUtil.getBusinessDateByDay(bizDateNow, -2);
		bizDate[1] = TimeUtil.getBusinessDateByDay(bizDateNow, -1);
		bizDate[2] = bizDateNow;

		for (int idx = 0; idx <3; idx ++) {
			long businessDate = bizDate[idx];
			DashboardTotalDetailInfo dashboard = new DashboardTotalDetailInfo();
			
			dashboard.setBusinessDateStr(businessDate);

			ReportDaySales reportDaySales = ReportDaySalesSQL
											.getReportDaySalesByTime(businessDate);
			if (reportDaySales != null) {
				dashboard.setTotalTax(reportDaySales.getTotalTax());
				dashboard.setTotalDiscount(reportDaySales.getDiscountAmt());
				dashboard.setSubTotal(reportDaySales.getItemSales());
				dashboard.setTotalAmount(reportDaySales.getNettSales());
				totalDetailInfos.add(dashboard);
			}else {
				Map<String, Object> taxPriceSumMap = OrderDetailTaxSQL
						.getTaxDetail(businessDate);
				ArrayList<String> taxPriceSum = new ArrayList<String>();
				
				//setTotalTax		
				BigDecimal totalTax = BH.getBD(ParamConst.DOUBLE_ZERO);
				if (taxPriceSumMap != null) {
					taxPriceSum = (ArrayList<String>) taxPriceSumMap.get("taxPriceSum");
				}
				
				for (int taxPriceIndex = 0; taxPriceIndex < taxPriceSum.size(); taxPriceIndex++) {
					totalTax = BH.add(totalTax,
							BH.getBD(taxPriceSum.get(taxPriceIndex)), true);
				}
				dashboard.setTotalTax(totalTax.toString());
				
				BigDecimal totalSales = BH.getBD(ParamConst.DOUBLE_ZERO);
				BigDecimal itemSalesDicimal = BH.getBD(ParamConst.DOUBLE_ZERO);
				int itemSalesQty = 0;
				BigDecimal discountPer = BH.getBD(ParamConst.DOUBLE_ZERO);
				int discountPerQty = 0;
				BigDecimal discount = BH.getBD(ParamConst.DOUBLE_ZERO);
				int discountQty = 0;
				BigDecimal totalCard = BH.getBD(ParamConst.DOUBLE_ZERO);
				int totalCardQty = 0;
		
				List<Order> orderList = new ArrayList<Order>();
				List<OrderDetail> orderDetailList = new ArrayList<OrderDetail>();
		
				Map<String, String> focBillMap = PaymentSettlementSQL
						.getPaymentSettlementSumPaidAndCount(
								ParamConst.SETTLEMENT_TYPE_ENTERTAINMENT, businessDate);
				String focBill = BH.doubleFormat.format(BH.getBD(focBillMap
						.get("sumAmount")));
				String focBillQty = focBillMap.get("count");
		
				Map<String, String> voidBillsMap = PaymentSettlementSQL
						.getPaymentSettlementSumPaidAndCount(
								ParamConst.SETTLEMENT_TYPE_VOID, businessDate);
				String billVoid = BH.doubleFormat.format(BH.getBD(voidBillsMap
						.get("sumAmount")));
				String billVoidQty = voidBillsMap.get("count");
		
				Map<String, String> holdldMap = PaymentSettlementSQL
						.getPaymentSettlementSumPaidAndCount(
								ParamConst.SETTLEMENT_TYPE_BILL_ON_HOLD, businessDate);
				String holdld = BH.doubleFormat.format(BH.getBD(holdldMap
						.get("sumAmount")));
				String holdldQty = holdldMap.get("count");
		
				Map<String, String> totalVoidMap = OrderDetailSQL
						.getSumOrderDetailDiscountByTimeAndVoidOrFreeType(
								ParamConst.ORDERDETAIL_TYPE_VOID, businessDate);
				String itemVoid = BH.doubleFormat.format(BH.getBD(totalVoidMap
						.get("sumRealPrice")));
				String itemVoidQty = totalVoidMap.get("sumItemNum");
				Map<String, String> focItemMap = OrderDetailSQL
						.getSumOrderDetailDiscountByTimeAndVoidOrFreeType(
								ParamConst.ORDERDETAIL_TYPE_FREE, businessDate);
				String focItem = BH.doubleFormat.format(BH.getBD(focItemMap
						.get("sumRealPrice")));
				String focItemQty = focItemMap.get("sumItemNum");
				orderList = OrderSQL.getAllOrderByTime(businessDate);
				int orderQty = 0;
				int billNoQty = 0;
				int personQty = 0;
				if (orderList.isEmpty()) {
					dashboard.setTotalDiscount(discount.toString());
					dashboard.setSubTotal(itemSalesDicimal.toString());
					dashboard.setTotalAmount(itemSalesDicimal.toString());
					dashboard.setTotalAmount(totalSales.toString());
					totalDetailInfos.add(dashboard);
					continue;
				}
				for (int orderIndex = 0; orderIndex < orderList.size(); orderIndex++) {
					Order order = orderList.get(orderIndex);
					// itemSalesDicimal = BH.add(itemSalesDicimal,
					// BH.getBD(order.getSubTotal()), true);
					orderDetailList = OrderDetailSQL.getAllOrderDetailsByOrder(order);
					switch (order.getDiscountType()) {
					case ParamConst.ORDER_DISCOUNT_TYPE_RATE_BY_ORDER: {
						for (int orderDetailIndex = 0; orderDetailIndex < orderDetailList
								.size(); orderDetailIndex++) {
							OrderDetail orderDetail = orderDetailList
									.get(orderDetailIndex);
							itemSalesQty += orderDetail.getItemNum();
							itemSalesDicimal = BH.add(itemSalesDicimal,
									BH.getBD(orderDetail.getRealPrice()), true);
							if(orderDetail.getOrderDetailType() == ParamConst.ORDERDETAIL_TYPE_GENERAL){
								if (orderDetail.getDiscountType() == ParamConst.ORDERDETAIL_DISCOUNT_TYPE_SUB) {
									discountPer = BH.add(discountPer, 
											BH.getBD(orderDetail.getDiscountPrice()),
											true);
									discountPerQty++;
								} else {
									discountPer = BH.add(discountPer, BH.mul(
											BH.getBD(orderDetail.getRealPrice()),
											BH.getBD(orderDetail.getDiscountRate()), false),
											true);
									discountPerQty++;
								}
							}
						}
					}
						break;
					case ParamConst.ORDER_DISCOUNT_TYPE_SUB_BY_ORDER: {
						for (int orderDetailIndex = 0; orderDetailIndex < orderDetailList
								.size(); orderDetailIndex++) {
							OrderDetail orderDetail = orderDetailList
									.get(orderDetailIndex);
							itemSalesQty += orderDetail.getItemNum();
							itemSalesDicimal = BH.add(itemSalesDicimal,
									BH.getBD(orderDetail.getRealPrice()), true);
							if(orderDetail.getOrderDetailType() == ParamConst.ORDERDETAIL_TYPE_GENERAL){
								
								discountQty++;
								if (orderDetail.getDiscountType() == ParamConst.ORDERDETAIL_DISCOUNT_TYPE_SUB) {
									discountPer = BH.add(discountPer, 
											BH.getBD(orderDetail.getDiscountPrice()),
											true);
								} else if (orderDetail.getDiscountType() == ParamConst.ORDERDETAIL_DISCOUNT_TYPE_RATE) {
									discountPer = BH.add(discountPer, BH.mul(
											BH.getBD(orderDetail.getRealPrice()),
											BH.getBD(orderDetail.getDiscountRate()), false),
											true);
								}
							}
						}
						discountPer = BH.add(discountPer, BH.getBD(order.getDiscountPrice()), true);
						discount = BH.add(discount,
								BH.getBD(order.getDiscountAmount()), true);
					}
						break;
					case ParamConst.ORDER_DISCOUNT_TYPE_BY_ORDERDETAIL: {
						for (int orderDetailIndex = 0; orderDetailIndex < orderDetailList
								.size(); orderDetailIndex++) {
							OrderDetail orderDetail = orderDetailList
									.get(orderDetailIndex);
							itemSalesQty += orderDetail.getItemNum();
							itemSalesDicimal = BH.add(itemSalesDicimal,
									BH.getBD(orderDetail.getRealPrice()), true);
							if(orderDetail.getOrderDetailType() == ParamConst.ORDERDETAIL_TYPE_GENERAL){
							if (orderDetail.getDiscountType() == ParamConst.ORDERDETAIL_DISCOUNT_TYPE_RATE) {
								discountPer = BH
										.add(discountPer,
												BH.mul(BH.getBD(orderDetail
														.getRealPrice()), BH
														.getBD(orderDetail
																.getDiscountRate()),
														false), true);
								discountPerQty++;
							} else if (orderDetailList.get(orderDetailIndex)
									.getDiscountType() == ParamConst.ORDERDETAIL_DISCOUNT_TYPE_SUB) {
								discount = BH.add(discount,
										BH.getBD(orderDetail.getDiscountPrice()), true);
								discountQty++;
							}
							}
						}
					}
						break;
					case ParamConst.ORDER_DISCOUNT_TYPE_NULL: {
						for (int orderDetailIndex = 0; orderDetailIndex < orderDetailList
								.size(); orderDetailIndex++) {
							OrderDetail orderDetail = orderDetailList
									.get(orderDetailIndex);
							itemSalesQty += orderDetail.getItemNum();
							itemSalesDicimal = BH.add(itemSalesDicimal,
									BH.getBD(orderDetail.getRealPrice()), true);
						}
					}
						break;
					default:
						break;
					}
					orderQty++;
					
					int countOrderSplit = OrderSplitSQL.getOrderSplitsCountByOrder(order);
					if (countOrderSplit > 0) {
						billNoQty += countOrderSplit;
					} else {
						billNoQty++;
					}
					personQty += order.getPersons();
				}
		
		        //Discount
				BigDecimal discountAmt = BH.add(discountPer, discount, true);
				dashboard.setTotalDiscount(discountAmt.toString());
		  
				//Item sales
				dashboard.setSubTotal(itemSalesDicimal.toString());
				
				totalSales = itemSalesDicimal;
				totalSales = BH.sub(totalSales, discountPer, true);
				totalSales = BH.sub(totalSales, discount, true);
				totalSales = BH.sub(totalSales, BH.getBD(focBill), true);
				totalSales = BH.sub(totalSales, BH.getBD(focItem), true);
				totalSales = BH.sub(totalSales, BH.getBD(billVoid), true);
				totalSales = BH.sub(totalSales, BH.getBD(itemVoid), true);
				totalSales = BH.add(totalSales, totalTax, true);
				
				//NETT Sales
				dashboard.setTotalAmount(totalSales.toString());
				totalDetailInfos.add(dashboard);
			}
	    }
		return totalDetailInfos;
	}
	
	public Map<String, Object> loadDaySalesXZReport(long bizDateNow, SessionStatus sessionStatus) {
		long oldtime = bizDateNow;

		Calendar c = Calendar.getInstance();
		Date dt = new Date(bizDateNow);
		c.setTime(dt); 
		c.add(Calendar.DATE, -29); // Adding 5 days
		oldtime = c.getTime().getTime();
		
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put("bizDateNow", bizDateNow);
		
		//get all ZReports stored locally
		Map<Long, Object> reportSummary = (Map<Long, Object>) ReportDaySalesSQL.getReportDaySalesBetweenTime(bizDateNow, oldtime);
		
		//xReport Current session
		ReportDaySales xReportObj = null;
		if (sessionStatus!=null) {
			xReportObj = this.loadXReportDaySales(bizDateNow, sessionStatus);
		}
        if (reportSummary.isEmpty()) {
        	Map<String, Object> summyObj = new HashMap<String, Object>();
        	if (xReportObj!=null) {
        	   Map<Integer, Object> sessionsale = new HashMap<Integer, Object>();
        	   sessionsale.put(sessionStatus.getSession_status(), xReportObj.getNettSales());
        	   summyObj.put("x", sessionsale);
        	}else
        	  summyObj.put("x", null);
        	summyObj.put("z", null);
        	reportSummary.put(bizDateNow, summyObj);
        }else{
        	Map<String, Object> summyObj = (Map<String, Object>) reportSummary.get(bizDateNow);
        	if (summyObj!=null) {
        	//no x/zreport data for today
	        	if (summyObj.get("z")==null) {
	         	  Map<Integer, Object> sessionsale = new HashMap<Integer, Object>();
	         	  sessionsale.put(sessionStatus.getSession_status(), xReportObj.getNettSales());	
	        	  summyObj.put("x", sessionsale);     
	        	}
	        	reportSummary.put(bizDateNow, summyObj);
        	}else {
        		Map<String, Object> newSummyObj = new HashMap<String, Object>(); 
            	if (xReportObj!=null) {
             	   Map<Integer, Object> sessionsale = new HashMap<Integer, Object>();
             	   sessionsale.put(sessionStatus.getSession_status(), xReportObj.getNettSales());
             	   newSummyObj.put("x", sessionsale);
             	}else
             		newSummyObj.put("x", null);
             	newSummyObj.put("z", null);
             	reportSummary.put(bizDateNow, newSummyObj);
        	}
        	
        }
		ret.put("result",reportSummary);
		return ret;
	}
}
