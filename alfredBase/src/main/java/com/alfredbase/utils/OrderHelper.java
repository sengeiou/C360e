package com.alfredbase.utils;

import com.alfredbase.BaseApplication;
import com.alfredbase.ParamConst;
import com.alfredbase.global.CoreData;
import com.alfredbase.javabean.HappyHourWeek;
import com.alfredbase.javabean.ItemDetail;
import com.alfredbase.javabean.ItemHappyHour;
import com.alfredbase.javabean.ItemModifier;
import com.alfredbase.javabean.Modifier;
import com.alfredbase.javabean.Order;
import com.alfredbase.javabean.OrderDetail;
import com.alfredbase.javabean.OrderDetailTax;
import com.alfredbase.javabean.OrderModifier;
import com.alfredbase.javabean.OrderSplit;
import com.alfredbase.javabean.Printer;
import com.alfredbase.javabean.RevenueCenter;
import com.alfredbase.javabean.RoundAmount;
import com.alfredbase.javabean.Tax;
import com.alfredbase.javabean.TaxCategory;
import com.alfredbase.store.Store;
import com.alfredbase.store.sql.OrderDetailSQL;
import com.alfredbase.store.sql.OrderDetailTaxSQL;
import com.alfredbase.store.sql.OrderModifierSQL;
import com.alfredbase.store.sql.OrderSQL;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class OrderHelper {
	/**
	 * 获取订单详情的Tax
	 * 
	 * @param orderDetail
	 * @return
	 */
	public static BigDecimal getOrderDetailTax(Order order,
			OrderDetail orderDetail) {
		BigDecimal preTaxPrice = BH.sub(BH.getBD(orderDetail.getRealPrice()),
				getOrderDetailDiscountPrice(orderDetail), false);

		ItemDetail itemDetail = CoreData.getInstance().getItemDetailById(
				orderDetail.getItemId());
		BigDecimal taxTotal = BH.getBD(ParamConst.DOUBLE_ZERO);
		if(itemDetail == null || itemDetail.getTaxCategoryId() == null)
			return taxTotal;
		List<TaxCategory> taxCategories = CoreData.getInstance()
				.getTaxCategorys(itemDetail.getTaxCategoryId());
		if (taxCategories.size() > 0) {
			for (TaxCategory taxCategory : taxCategories) {
				Tax tax = CoreData.getInstance().getTax(taxCategory.getTaxId());
				if(tax == null){
					continue;
				}
				OrderDetailTax orderDetailTax = ObjectFactory.getInstance()
						.getOrderDetailTax(order, orderDetail, tax, taxCategory.getIndex().intValue());
				if (taxCategory.getTaxOn().intValue() == ParamConst.TAX_ON_TAX_1
						|| taxCategory.getTaxOn().intValue() == ParamConst.TAX_ON_TAX_2) {
					TaxCategory temp = CoreData.getInstance().getTaxCategory(
							taxCategory.getTaxOnId());

					Tax taxOn = CoreData.getInstance().getTax(temp.getTaxId());
					if(order.getIsTakeAway().intValue() == ParamConst.TAKE_AWAY
							|| orderDetail.getIsTakeAway() == ParamConst.TAKE_AWAY){
						if(orderDetailTax.getTaxType().intValue() == ParamConst.TAX_TYPE_SERVICE){
							orderDetailTax.setTaxPrice(ParamConst.DOUBLE_ZERO);
						}else if(taxOn.getTaxType().intValue() == ParamConst.TAX_TYPE_SERVICE
								&& tax.getTaxType().intValue() == ParamConst.TAX_TYPE_SERVICE){
							taxTotal = BH.getBD(ParamConst.DOUBLE_ZERO);
							orderDetailTax.setTaxPrice(ParamConst.DOUBLE_ZERO);
						}else if(taxOn.getTaxType().intValue() == ParamConst.TAX_TYPE_SERVICE){
							taxTotal = BH.add(taxTotal, BH.mul(preTaxPrice,
									BH.getBDNoFormat(tax.getTaxPercentage()), false), false);
							orderDetailTax.setTaxPrice(BH.mul(preTaxPrice,
									BH.getBDNoFormat(tax.getTaxPercentage()), false).toString());
						} else if (tax.getTaxType().intValue() == ParamConst.TAX_TYPE_SERVICE){
							taxTotal = BH.add(taxTotal, BH.mul(preTaxPrice,
									BH.getBDNoFormat(taxOn.getTaxPercentage()), false), false);
							orderDetailTax.setTaxPrice(BH.mul(preTaxPrice,
									BH.getBDNoFormat(taxOn.getTaxPercentage()), false).toString());
						}else {
							BigDecimal priceOntax = BH.add(
									preTaxPrice,
									BH.mul(preTaxPrice,
											BH.getBDNoFormat(taxOn.getTaxPercentage()), false),
									false);
							
							taxTotal = BH.add(taxTotal, BH.mul(priceOntax,
									BH.getBDNoFormat(tax.getTaxPercentage()), false), false);
							orderDetailTax.setTaxPrice(BH.mul(priceOntax,
									BH.getBDNoFormat(tax.getTaxPercentage()), false).toString());
						}
					}else{
						BigDecimal priceOntax = BH.add(
								preTaxPrice,
								BH.mul(preTaxPrice,
										BH.getBDNoFormat(taxOn.getTaxPercentage()), false),
								false);
						
						taxTotal = BH.add(taxTotal, BH.mul(priceOntax,
								BH.getBDNoFormat(tax.getTaxPercentage()), false), false);
						orderDetailTax.setTaxPrice(BH.mul(priceOntax,
								BH.getBDNoFormat(tax.getTaxPercentage()), false).toString());
					}
					
				} else {
					if(order.getIsTakeAway().intValue() == ParamConst.TAKE_AWAY
							|| orderDetail.getIsTakeAway() == ParamConst.TAKE_AWAY){
						if(tax.getTaxType().intValue() == ParamConst.TAX_TYPE_SERVICE){
							orderDetailTax.setTaxPrice(ParamConst.DOUBLE_ZERO);
						}else{
							taxTotal = BH.add(
									taxTotal,
									BH.mul(preTaxPrice,
											BH.getBDNoFormat(tax.getTaxPercentage()), false),
											false);
							orderDetailTax.setTaxPrice(BH.mul(preTaxPrice,
									BH.getBDNoFormat(tax.getTaxPercentage()), false).toString());
						}
					}else{
						taxTotal = BH.add(
								taxTotal,
								BH.mul(preTaxPrice,
										BH.getBDNoFormat(tax.getTaxPercentage()), false),
								false);
						orderDetailTax.setTaxPrice(BH.mul(preTaxPrice,
								BH.getBDNoFormat(tax.getTaxPercentage()), false).toString());
					}
					
				}
				OrderDetailTaxSQL.updateOrderDetailTax(orderDetailTax);
			}
		}
		return taxTotal;
	}

	/**
	 * 获取订单详情的基础价格，计算方式：实收金额=(菜单金额-HappyHour金额+配料金额)*数量，也就是UI中的subTotal
	 * 
	 * @param order
	 * @param orderDetail
	 * @return
	 */
	public static BigDecimal getOrderDetailRealPrice(Order order,
			OrderDetail orderDetail) {
		BigDecimal price = BH.getBD(ParamConst.DOUBLE_ZERO);
		ItemHappyHour itemHappyHour = getItemHappyHour(order, orderDetail);
		if (itemHappyHour != null) {
			if (Double.parseDouble(itemHappyHour.getDiscountPrice()) > 0) {
				price = BH.sub(BH.getBD(orderDetail.getItemPrice()),
						BH.getBDNoFormat(itemHappyHour.getDiscountPrice()), false);
			} else if (Double.parseDouble(itemHappyHour.getDiscountRate()) > 0) {
				orderDetail.setDiscountRate(itemHappyHour.getDiscountRate());
				price = BH.sub(BH.getBD(orderDetail.getItemPrice()), BH
						.mul(BH.getBD(orderDetail.getItemPrice()),
								BH.getBDNoFormat(itemHappyHour.getDiscountRate()),
								false), false);
			} else if (itemHappyHour.getFreeNum().intValue() > 0) {
				price = BH.getBD(orderDetail.getItemPrice());
			}
		} else {
			price = BH.getBD(orderDetail.getItemPrice());
		}
		price = BH.mul(price, BH.getBD(orderDetail.getItemNum()), false);
		if(BH.getBDNoFormat(orderDetail.getWeight()).compareTo(BH.getBD("0")) > 0){
			price = BH.mul(price, BH.getBDNoFormat(orderDetail.getWeight()), false);
		}
		price = BH.add(price, BH.getBD(orderDetail.getModifierPrice()), true);
		return price;
	}

	/**
	 * 得到所有配料的价格
	 * 
	 * @param order
	 * @param orderDetail
	 * @return
	 */
	public static BigDecimal getOrderDetailModifierPrice(final Order order,
			OrderDetail orderDetail) {
		ArrayList<OrderModifier> orderModifiers = OrderModifierSQL
				.getOrderModifiers(order, orderDetail);
		BigDecimal modifierPrice = BH.getBD(ParamConst.DOUBLE_ZERO);
		for (OrderModifier orderModifier : orderModifiers) {
			if (orderModifier.getStatus().intValue() == ParamConst.ORDER_MODIFIER_STATUS_NORMAL) {
				modifierPrice = BH.add(modifierPrice,
						BH.getBD(orderModifier.getModifierPrice()), true);
			}
		}
		return modifierPrice;
	}

	/**
	 * 得到当前orderDetail的折扣信息
	 * 
	 * @param orderDetail
	 * @return
	 */
	public static BigDecimal getOrderDetailDiscountPrice(OrderDetail orderDetail) {
		BigDecimal discountPrice = BH.getBD(ParamConst.DOUBLE_ZERO);
		if (orderDetail.getDiscountType().intValue() == ParamConst.ORDERDETAIL_DISCOUNT_TYPE_RATE
				|| orderDetail.getDiscountType().intValue() == ParamConst.ORDERDETAIL_DISCOUNT_BYORDER_TYPE_SUB
				|| orderDetail.getDiscountType().intValue() == ParamConst.ORDERDETAIL_DISCOUNT_BYORDER_TYPE_RATE
				|| orderDetail.getDiscountType().intValue() == ParamConst.ORDERDETAIL_DISCOUNT_BYCATEGORY_TYPE_RATE
				|| orderDetail.getDiscountType().intValue() == ParamConst.ORDERDETAIL_DISCOUNT_BYCATEGORY_TYPE_SUB) {
			discountPrice = BH.mul(BH.getBD(orderDetail.getRealPrice()),
					BH.getBDNoFormat(orderDetail.getDiscountRate()), true);
			return discountPrice;
		}
		if (orderDetail.getDiscountType() == ParamConst.ORDERDETAIL_DISCOUNT_TYPE_SUB) {
			discountPrice = BH.getBD(orderDetail.getDiscountPrice());
			return discountPrice;
		}
		return discountPrice;
	}
	// 当修改Order的discount的时候才能调用
//	public static void setOrderDetailDiscount(Order order,
//			OrderDetail orderDetail) {
//		// 0不打折、10主订单按照比率打折、11主订单直接减、12子订单打折
//		
//			if (order.getDiscountType() == ParamConst.ORDER_DISCOUNT_TYPE_RATE_BY_ORDER) {
//				orderDetail.setDiscountRate(order.getDiscountRate());
//				orderDetail
//						.setDiscountType(ParamConst.ORDERDETAIL_DISCOUNT_BYORDER_TYPE_RATE);
//			} else if(order.getDiscountType().intValue() == ParamConst.ORDER_DISCOUNT_TYPE_SUB_BY_ORDER){
//				String discount_rate = BH.div(BH.getBD(order.getDiscountAmount()),
//						BH.getBD(order.getSubTotal()), true).toString();
//				orderDetail.setDiscountRate(discount_rate);
//				orderDetail
//						.setDiscountType(ParamConst.ORDERDETAIL_DISCOUNT_BYORDER_TYPE_SUB);
//			} else {
//				orderDetail.setDiscountRate(ParamConst.DOUBLE_ZERO);
//				orderDetail
//						.setDiscountType(ParamConst.ORDERDETAIL_DISCOUNT_TYPE_NULL);
//			}
//	}

	public static BigDecimal getOrderDiscount(Order order,
			List<OrderDetail> orderDetails) {
		BigDecimal discount = BH.getBD(ParamConst.DOUBLE_ZERO);
		for (OrderDetail orderDetail : orderDetails) {
			if (orderDetail.getDiscountType() == ParamConst.ORDERDETAIL_DISCOUNT_TYPE_SUB) {
				discount = BH.add(discount,
						BH.getBD(orderDetail.getDiscountPrice()), true);
			} else if (orderDetail.getDiscountType() == ParamConst.ORDERDETAIL_DISCOUNT_TYPE_RATE
					|| orderDetail.getDiscountType() == ParamConst.ORDERDETAIL_DISCOUNT_BYORDER_TYPE_RATE
					|| orderDetail.getDiscountType() == ParamConst.ORDERDETAIL_DISCOUNT_BYORDER_TYPE_SUB) {
				discount = BH
						.add(discount,
								BH.mul(BH.getBD(orderDetail.getRealPrice()),
										BH.getBDNoFormat(orderDetail.getDiscountRate()),
										false), true);
			}
		}
		return discount;
	}

	public static void setOrderDiscount(Order order,
			List<OrderDetail> orderDetails) {
		BigDecimal discount = BH.getBD(ParamConst.DOUBLE_ZERO);
		if (order.getDiscountType().intValue() == ParamConst.ORDER_DISCOUNT_TYPE_SUB_BY_ORDER
				|| order.getDiscountType().intValue() == ParamConst.ORDER_DISCOUNT_TYPE_SUB_BY_CATEGORY) {
			discount = BH.add(discount, BH.getBD(order.getDiscountPrice()), true);
			for (OrderDetail orderDetail : orderDetails) {
				if (orderDetail.getDiscountType().intValue() == ParamConst.ORDERDETAIL_DISCOUNT_TYPE_RATE){
					discount = BH.add(discount, BH.mul(
							BH.getBD(orderDetail.getRealPrice()),
							BH.getBDNoFormat(orderDetail.getDiscountRate()), false),
							true);
					}else if(orderDetail.getDiscountType().intValue() == ParamConst.ORDERDETAIL_DISCOUNT_TYPE_SUB) {
						discount = BH.add(discount, BH.getBD(orderDetail.getDiscountPrice()), true);
					}
				}
		} else {
				for (OrderDetail orderDetail : orderDetails) {
					if (orderDetail.getDiscountType() == ParamConst.ORDERDETAIL_DISCOUNT_TYPE_SUB) {
						discount = BH.add(discount,
								BH.getBD(orderDetail.getDiscountPrice()), true);
					} else if (orderDetail.getDiscountType() == ParamConst.ORDERDETAIL_DISCOUNT_TYPE_RATE
									|| orderDetail.getDiscountType() == ParamConst.ORDERDETAIL_DISCOUNT_BYORDER_TYPE_RATE
//									|| orderDetail.getDiscountType() == ParamConst.ORDERDETAIL_DISCOUNT_BYORDER_TYPE_SUB
									|| orderDetail.getDiscountType() == ParamConst.ORDERDETAIL_DISCOUNT_BYCATEGORY_TYPE_RATE
									) {
						discount = BH.add(discount, BH.mul(
								BH.getBD(orderDetail.getRealPrice()),
								BH.getBDNoFormat(orderDetail.getDiscountRate()), false),
								true);
					}
				}
			}
			order.setDiscountAmount(BH.getBD(discount).toString());
	}
	// 只有修改orderDiscount的时候才能调用这个方法
	public static void setOrderDiscountByOrder(Order order,
			List<OrderDetail> orderDetails) {
//		if (order.getDiscountType().intValue() == ParamConst.ORDER_DISCOUNT_TYPE_RATE_BY_ORDER) {
//			order.setDiscountAmount(BH.mul(BH.getBD(order.getSubTotal()),
//					BH.getBD(order.getDiscountRate()), true).toString());
//		} else if (order.getDiscountType().intValue() == ParamConst.ORDER_DISCOUNT_TYPE_SUB_BY_ORDER) {
////			order.setDiscountRate(BH.div(BH.getBD(order.getDiscountPrice()),
////					BH.getBD(order.getSubTotal()), true).toString());
//			order.setDiscountAmount(order.getDiscountPrice());
//		} else if (order.getDiscountType().intValue() == ParamConst.ORDER_DISCOUNT_TYPE_NULL) {
//			order.setDiscountRate(ParamConst.DOUBLE_ZERO);
//			order.setDiscountPrice(ParamConst.INT_ZERO);
//			order.setDiscountAmount(ParamConst.INT_ZERO);
//		} else {
			BigDecimal discount = BH.getBD(ParamConst.DOUBLE_ZERO);
			for (OrderDetail orderDetail : orderDetails) {
				if (orderDetail.getDiscountType() == ParamConst.ORDERDETAIL_DISCOUNT_TYPE_SUB) {
					discount = BH.add(discount,
							BH.getBD(orderDetail.getDiscountPrice()), true);
				} else if (orderDetail.getDiscountType() == ParamConst.ORDERDETAIL_DISCOUNT_TYPE_RATE
								|| orderDetail.getDiscountType() == ParamConst.ORDERDETAIL_DISCOUNT_BYORDER_TYPE_RATE
								|| orderDetail.getDiscountType() == ParamConst.ORDERDETAIL_DISCOUNT_BYORDER_TYPE_SUB) {
					discount = BH.add(discount, BH.mul(
							BH.getBD(orderDetail.getRealPrice()),
							BH.getBDNoFormat(orderDetail.getDiscountRate()), false),
							true);
				}
			}
			order.setDiscountAmount(BH.getBD(discount).toString());
//			if (Double.parseDouble(order.getSubTotal()) > 0) {
//				order.setDiscountRate(BH.div(discount,
//						BH.getBD(order.getSubTotal()), true).toString());
//			} else {
//				order.setDiscountRate(ParamConst.INT_ZERO);
//			}
	}

	public static void setOrderSubTotal(Order order,
			List<OrderDetail> orderDetails) {
		BigDecimal subTotal = BH.getBD(ParamConst.DOUBLE_ZERO);
		if (orderDetails.size() > 0) {
			for (OrderDetail orderDetail : orderDetails) {
				subTotal = BH.add(subTotal,
						BH.getBD(orderDetail.getRealPrice()), false);
			}
		}
		order.setSubTotal(BH.getBD(subTotal).toString());
	}

	public static void setOrderTax(Order order, List<OrderDetail> orderDetails) {
		BigDecimal tax = BH.getBD(ParamConst.DOUBLE_ZERO);
		if (orderDetails.size() > 0) {
			for (OrderDetail orderDetail : orderDetails) {
				if(!IntegerUtils.isEmptyOrZero(orderDetail.getAppOrderDetailId())){
					tax = BH.add(tax,
							BH.getBD(orderDetail.getTaxPrice()),
							false);
				}else {
					BigDecimal orderDetailTax = OrderHelper.getOrderDetailTax(order, orderDetail);
					tax = BH.add(tax,
							orderDetailTax,
							false);
					OrderDetailSQL.updateOrderDetailTaxById(orderDetailTax.toString(), orderDetail.getId().intValue());
				}
			}
		}
		order.setTaxAmount(BH.getBD(tax).toString());
	}

	public static void setOrderTotal(Order order, List<OrderDetail> orderDetails) {
		order.setTotal(BH.add(
				BH.sub(BH.getBD(order.getSubTotal()),
						BH.getBD(order.getDiscountAmount()), false),
				BH.getBD(order.getTaxAmount()), true).toString());
	}
	
	public static void setOrderInclusiveTaxPrice(Order order){
		if(order.getInclusiveTaxPercentage() != null){
			order.setInclusiveTaxPrice(BH
					.mul(BH.getBD(order.getInclusiveTaxPercentage()),
							BH.div(BH.sub(BH.getBD(order.getSubTotal()),
									BH.getBD(order.getDiscountAmount()), false),
									BH.add(BH.getBD(1), BH.getBD(order
											.getInclusiveTaxPercentage()),
											false), false), true).toString());
//			order.setInclusiveTaxPrice( BH.mul(
//							BH.getBD(order.getInclusiveTaxPercentage()),
//							BH.sub(BH.getBD(order.getSubTotal()),
//											BH.getBD(order.getDiscountAmount()),
//											false),
//									true).toString());
		}
	}
	
	public static void setOrderTotalAlfterRound(Order order, RoundAmount roundAmount){
		if(order != null && roundAmount != null){
			order.setTotal(BH.add(BH.getBD(order.getTotal()), BH.getBD(roundAmount.getRoundBalancePrice()), true).toString());
		}
		
	}
	
	public static void setOrderSplitTotalAlfterRound(OrderSplit orderSplit, RoundAmount roundAmount){
		if(orderSplit != null && roundAmount != null){
			orderSplit.setTotal(BH.add(BH.getBD(orderSplit.getTotal()), BH.getBD(roundAmount.getRoundBalancePrice()), true).toString());
		}
		
	}
	
	public static void setOrderSplitInclusiveTaxPrice(OrderSplit orderSplit){
		if(orderSplit.getInclusiveTaxPercentage() != null){
			orderSplit.setInclusiveTaxPrice(BH
					.mul(BH.getBD(orderSplit.getInclusiveTaxPercentage()),
							BH.sub(BH.getBD(orderSplit.getSubTotal()),
									BH.getBD(orderSplit.getDiscountAmount()), false)
									, true).toString());
		}
	}
	
	public static void setOrderSplitDiscount(Order order, OrderSplit orderSplit,
			List<OrderDetail> orderDetails) {
//			BigDecimal discount = BH.getBD(ParamConst.DOUBLE_ZERO);
//			for (OrderDetail orderDetail : orderDetails) {
//					if (orderDetail.getDiscountType() == ParamConst.ORDERDETAIL_DISCOUNT_TYPE_SUB) {
//						if(orderSplit.getGroupId().intValue() == orderDetail.getGroupId().intValue()){
//							discount = BH.add(discount, BH.getBD(orderDetail.getDiscountPrice()), false);
//						}
//					} else if (orderDetail.getDiscountType() == ParamConst.ORDERDETAIL_DISCOUNT_TYPE_RATE
//							|| orderDetail.getDiscountType() == ParamConst.ORDERDETAIL_DISCOUNT_BYORDER_TYPE_RATE
//							|| orderDetail.getDiscountType() == ParamConst.ORDERDETAIL_DISCOUNT_BYORDER_TYPE_SUB) {
//						if(orderSplit.getGroupId().intValue() == orderDetail.getGroupId().intValue()){
//							discount = BH.add(discount, BH.mul( BH.getBD(orderDetail.getRealPrice()),
//									BH.getBDNoFormat(orderDetail.getDiscountRate()), false),false);
//						}
//					}
//				}
		BigDecimal discount = BH.getBD(ParamConst.DOUBLE_ZERO);
		if (order.getDiscountType().intValue() == ParamConst.ORDER_DISCOUNT_TYPE_SUB_BY_ORDER
				|| order.getDiscountType().intValue() == ParamConst.ORDER_DISCOUNT_TYPE_SUB_BY_CATEGORY) {
			discount = BH.add(discount, BH.getBD(order.getDiscountPrice()), true);
			for (OrderDetail orderDetail : orderDetails) {
				if (orderDetail.getDiscountType().intValue() == ParamConst.ORDERDETAIL_DISCOUNT_TYPE_RATE){
					if(orderSplit.getGroupId().intValue() == orderDetail.getGroupId().intValue()) {
						discount = BH.add(discount, BH.mul(
								BH.getBD(orderDetail.getRealPrice()),
								BH.getBDNoFormat(orderDetail.getDiscountRate()), false),
								true);
					}
				}else if(orderDetail.getDiscountType().intValue() == ParamConst.ORDERDETAIL_DISCOUNT_TYPE_SUB) {
					if(orderSplit.getGroupId().intValue() == orderDetail.getGroupId().intValue()) {
						discount = BH.add(discount, BH.getBD(orderDetail.getDiscountPrice()), true);
					}
				}
			}
		} else {
			for (OrderDetail orderDetail : orderDetails) {
				if (orderDetail.getDiscountType() == ParamConst.ORDERDETAIL_DISCOUNT_TYPE_SUB) {
					if(orderSplit.getGroupId().intValue() == orderDetail.getGroupId().intValue()) {
						discount = BH.add(discount,
								BH.getBD(orderDetail.getDiscountPrice()), true);
					}
				} else if (orderDetail.getDiscountType() == ParamConst.ORDERDETAIL_DISCOUNT_TYPE_RATE
						|| orderDetail.getDiscountType() == ParamConst.ORDERDETAIL_DISCOUNT_BYORDER_TYPE_RATE
//									|| orderDetail.getDiscountType() == ParamConst.ORDERDETAIL_DISCOUNT_BYORDER_TYPE_SUB
						|| orderDetail.getDiscountType() == ParamConst.ORDERDETAIL_DISCOUNT_BYCATEGORY_TYPE_RATE
						) {
					if(orderSplit.getGroupId().intValue() == orderDetail.getGroupId().intValue()) {
						discount = BH.add(discount, BH.mul(
								BH.getBD(orderDetail.getRealPrice()),
								BH.getBDNoFormat(orderDetail.getDiscountRate()), false),
								true);
					}
				}
			}
		}
			orderSplit.setDiscountAmount(BH.getBD(discount).toString());
	}
	
	public static void setOrderSplitSubTotal(Order order, OrderSplit orderSplit,
			List<OrderDetail> orderDetails) {
		BigDecimal subTotal = BH.getBD(ParamConst.DOUBLE_ZERO);
		if (!orderDetails.isEmpty()) {
			for (OrderDetail orderDetail : orderDetails) {
				if(orderSplit.getGroupId().intValue() == orderDetail.getGroupId().intValue()){
					subTotal = BH.add(subTotal, BH.getBD(orderDetail.getRealPrice()), false);
				}
			}
		}
		orderSplit.setSubTotal(BH.getBD(subTotal).toString());
	}
	
	public static void setOrderSplitTax(Order order, OrderSplit orderSplit, List<OrderDetail> orderDetails){
		/*
		BigDecimal tax = BH.getBD(ParamConst.DOUBLE_ZERO);
		if (orderDetails.size() > 0) {
			for (OrderDetail orderDetail : orderDetails) {
				if(!IntegerUtils.isEmptyOrZero(orderDetail.getAppOrderDetailId())){
					tax = BH.add(tax,
							BH.getBD(orderDetail.getTaxPrice()),
							false);
				}else {
					BigDecimal orderDetailTax = OrderHelper.getOrderDetailTax(order, orderDetail);
					tax = BH.add(tax,
							orderDetailTax,
							false);
					OrderDetailSQL.updateOrderDetailTaxById(orderDetailTax.toString(), orderDetail.getId().intValue());
				}
			}
		}
		order.setTaxAmount(BH.doubleFormat.format(tax));
		 */
		BigDecimal orderSplitTax = BH.getBD(ParamConst.DOUBLE_ZERO);
		if(!orderDetails.isEmpty()){
			for (OrderDetail orderDetail : orderDetails) {
				if(orderSplit.getGroupId().intValue() == orderDetail.getGroupId().intValue()){
					if(!IntegerUtils.isEmptyOrZero(orderDetail.getAppOrderDetailId())){
						orderSplitTax = BH.add(orderSplitTax,
							BH.getBD(orderDetail.getTaxPrice()),false);
					}
//					orderSplitTax = BH.add(orderSplitTax,
//							OrderHelper.getOrderDetailTax(order, orderDetail),
//							false);
					BigDecimal orderDetailTax = OrderHelper.getOrderDetailTax(order, orderDetail);
					orderSplitTax = BH.add(orderSplitTax,
							orderDetailTax,
							false);
					OrderDetailSQL.updateOrderDetailTaxById(orderDetailTax.toString(), orderDetail.getId().intValue());
				}
			}
		}
		orderSplit.setTaxAmount(BH.getBD(orderSplitTax).toString());
	}
	
	public static void setOrderSplitTotal(Order order, OrderSplit orderSplit, List<OrderDetail> orderDetails){
		orderSplit.setTotal(BH.add(
				BH.sub(BH.getBD(orderSplit.getSubTotal()),
						BH.getBD(orderSplit.getDiscountAmount()), false),
				BH.getBD(orderSplit.getTaxAmount()), true).toString());
	}

	public static ItemHappyHour getItemHappyHour(Order order,
			OrderDetail orderDetail) {
		ItemHappyHour itemHappyHour = null;
		RevenueCenter revenueCenter = CoreData.getInstance().getRevenueCenter(
				order);
		// 先从RevenueCenter判断，是否存在合法的HappyHour，如果不存在，则认为没有ItemHappyHour
		if (revenueCenter == null
				|| CommonUtil.isNull(revenueCenter.getHappyHourId())) {
			return itemHappyHour;
		}
		if (CommonUtil.isNull(revenueCenter.getHappyEndTime())
				|| CommonUtil.isNull(revenueCenter.getHappyStartTime())) {
			return itemHappyHour;
		}
		long currentTimeMillis = System.currentTimeMillis();
		if (currentTimeMillis < revenueCenter.getHappyStartTime()
				|| currentTimeMillis > revenueCenter.getHappyEndTime()) {
			return itemHappyHour;
		}

		// 再从HappyHourWeek判断，是否存在合法的HappyHourWeek，如果不存在，则认为没有ItemHappyHour
		boolean isHappy = hasHappyWeek(revenueCenter.getHappyHourId());
		if (!isHappy)
			return itemHappyHour;

		// 最后从ItemHappyHour判断，是否存在合法的ItemHappyHour，如果不存在，则认为没有ItemHappyHour
		ItemDetail itemDetail = CoreData.getInstance().getItemDetailById(
				orderDetail.getItemId());
		itemHappyHour = CoreData.getInstance().getItemHappyHour(revenueCenter,
				itemDetail);
		return itemHappyHour;
	}

	private static boolean hasHappyWeek(Integer happy_hour_id) {
		long time = TimeUtil.getTimeInMillis();
		for (HappyHourWeek happyHourWeek : CoreData.getInstance()
				.getHappyHourWeeks()) {
			if (happyHourWeek.getHappyHourId().intValue() == happy_hour_id
					.intValue()) {
				if (happyHourWeek.getStartTime() < time
						&& happyHourWeek.getEndTime() > time) {
					if (Integer.parseInt(happyHourWeek.getWeek()) == TimeUtil
							.dayForWeek()) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	// 只有添加默认modifier
	public static void addDefaultModifiers(Order order, OrderDetail orderDetail) {
		ItemDetail itemDetail = CoreData.getInstance().getItemDetailById(
				orderDetail.getItemId());
		if (itemDetail == null || itemDetail.getItemTemplateId() == null){
			return;
		}
		for(ItemModifier itemModifier : CoreData.getInstance().getItemModifiers()){
			if (itemModifier == null
					|| itemModifier.getModifierCategoryId() == null) {
				continue;
			}
			if (itemModifier.getItemId().intValue() == itemDetail
					.getItemTemplateId().intValue()) {
				for (Modifier modifier : CoreData.getInstance().getModifierList()) {
					if (modifier == null || modifier.getId() == null){
						continue;
					}
					if (modifier.getCategoryId().intValue() == itemModifier
							.getModifierCategoryId().intValue()
							&& modifier.getType().intValue() == 1
							&& modifier.getIsDefault().intValue() == 1) {
						ItemDetail printItemDetail = CoreData.getInstance().getItemDetailByTemplateId(modifier.getItemId());
						int printId = 0;
						if(printItemDetail != null){
							ArrayList<Printer> prints = CoreData.getInstance().getPrintersInGroup(printItemDetail.getPrinterId().intValue());
							if(prints.size() == 0){
								printId = 0;
							}else{
								printId = prints.get(0).getId().intValue();
							}
						
						}
						OrderModifier orderModifier = ObjectFactory.getInstance().getOrderModifier(order, orderDetail, modifier, printId);
						OrderModifierSQL.addOrderModifier(orderModifier);
					}
				}
			}
		}
		
	}
	

	public static void setOrderModifierPirceAndNum(OrderDetail orderDetail, int num){
		List<OrderModifier> orderModifiers = OrderModifierSQL.getOrderModifiers(orderDetail);
		for(OrderModifier orderModifier : orderModifiers){
			Modifier modifier = CoreData.getInstance().getModifier(orderModifier.getModifierId().intValue());
			int modifierNum = 1;
			if(modifier == null){
				modifierNum = num;
			}else{
				modifierNum = modifier.getQty() * num;
			}
			orderModifier.setModifierNum(modifierNum);
			orderModifier.setModifierPrice(BH.mul(BH.getBD(orderModifier.getModifierItemPrice()), BH.getBD(modifierNum), true).toString());
			OrderModifierSQL.updateOrderModifier(orderModifier);
		}
		
	}
	
	
	/*Bob:加入流水号 */
	public static int calculateOrderNo(long bizDate) {
		int maxOrderNo = Store.getInt(BaseApplication.instance, Store.MAX_ORDER_NO, 0);
		int sid = 0;
		sid = OrderSQL.getSumCountByBizDate(bizDate);
		if(maxOrderNo > 0 && sid >= maxOrderNo){
			return 1;
		}
		return sid+1;
	}
}
