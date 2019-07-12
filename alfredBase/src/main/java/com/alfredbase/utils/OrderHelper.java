package com.alfredbase.utils;

import android.text.TextUtils;

import com.alfredbase.BaseApplication;
import com.alfredbase.ParamConst;
import com.alfredbase.global.CoreData;
import com.alfredbase.javabean.HappyHourWeek;
import com.alfredbase.javabean.ItemDetail;
import com.alfredbase.javabean.ItemHappyHour;
import com.alfredbase.javabean.ItemModifier;
import com.alfredbase.javabean.ItemPromotion;
import com.alfredbase.javabean.Modifier;
import com.alfredbase.javabean.Order;
import com.alfredbase.javabean.OrderDetail;
import com.alfredbase.javabean.OrderDetailTax;
import com.alfredbase.javabean.OrderModifier;
import com.alfredbase.javabean.OrderSplit;
import com.alfredbase.javabean.Printer;
import com.alfredbase.javabean.Promotion;
import com.alfredbase.javabean.PromotionData;
import com.alfredbase.javabean.PromotionOrder;
import com.alfredbase.javabean.PromotionWeek;
import com.alfredbase.javabean.RevenueCenter;
import com.alfredbase.javabean.RoundAmount;
import com.alfredbase.javabean.Tax;
import com.alfredbase.javabean.TaxCategory;
import com.alfredbase.store.Store;
import com.alfredbase.store.sql.OrderDetailSQL;
import com.alfredbase.store.sql.OrderDetailTaxSQL;
import com.alfredbase.store.sql.OrderModifierSQL;
import com.alfredbase.store.sql.OrderSQL;
import com.alfredbase.store.sql.OrderSplitSQL;
import com.alfredbase.store.sql.PromotionDataSQL;
import com.alfredbase.store.sql.PromotionOrderSQL;
import com.alfredbase.store.sql.PromotionSQL;
import com.alfredbase.store.sql.PromotionWeekSQL;
import com.nostra13.universalimageloader.utils.L;

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
              if(tax.getBeforeDiscount()==0){
				if (tax == null) {
					continue;
				}
				OrderDetailTax orderDetailTax = ObjectFactory.getInstance()
						.getOrderDetailTax(order, orderDetail, tax, taxCategory.getIndex().intValue());
				if (taxCategory.getTaxOn().intValue() == ParamConst.TAX_ON_TAX_1
						|| taxCategory.getTaxOn().intValue() == ParamConst.TAX_ON_TAX_2) {
					TaxCategory temp = CoreData.getInstance().getTaxCategory(
							taxCategory.getTaxOnId());

					Tax taxOn = CoreData.getInstance().getTax(temp.getTaxId());
					if(taxOn.getBeforeDiscount()==0){
					if (order.getIsTakeAway().intValue() == ParamConst.TAKE_AWAY
							|| orderDetail.getIsTakeAway() == ParamConst.TAKE_AWAY) {
						if (orderDetailTax.getTaxType().intValue() == ParamConst.TAX_TYPE_SERVICE) {
							orderDetailTax.setTaxPrice(ParamConst.DOUBLE_ZERO);
						} else if (taxOn.getTaxType().intValue() == ParamConst.TAX_TYPE_SERVICE
								&& tax.getTaxType().intValue() == ParamConst.TAX_TYPE_SERVICE) {
							taxTotal = BH.getBD(ParamConst.DOUBLE_ZERO);
							orderDetailTax.setTaxPrice(ParamConst.DOUBLE_ZERO);
						} else if (taxOn.getTaxType().intValue() == ParamConst.TAX_TYPE_SERVICE) {
							taxTotal = BH.add(taxTotal, BH.mul(preTaxPrice,
									BH.getBDNoFormat(tax.getTaxPercentage()), false), false);
							orderDetailTax.setTaxPrice(BH.mul(preTaxPrice,
									BH.getBDNoFormat(tax.getTaxPercentage()), false).toString());
						} else if (tax.getTaxType().intValue() == ParamConst.TAX_TYPE_SERVICE) {
							taxTotal = BH.add(taxTotal, BH.mul(preTaxPrice,
									BH.getBDNoFormat(taxOn.getTaxPercentage()), false), false);
							orderDetailTax.setTaxPrice(BH.mul(preTaxPrice,
									BH.getBDNoFormat(taxOn.getTaxPercentage()), false).toString());
						} else {
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
						BigDecimal priceOntax = BH.add(
								preTaxPrice,
								BH.mul(preTaxPrice,
										BH.getBDNoFormat(taxOn.getTaxPercentage()), false),
								false);

						taxTotal = BH.add(taxTotal, BH.mul(priceOntax,
								BH.getBDNoFormat(tax.getTaxPercentage()), false), false);
						orderDetailTax.setTaxPrice(BH.mul(priceOntax,
								BH.getBDNoFormat(tax.getTaxPercentage()), false).toString());
					}}

				} else {
					if (order.getIsTakeAway().intValue() == ParamConst.TAKE_AWAY
							|| orderDetail.getIsTakeAway() == ParamConst.TAKE_AWAY) {
						if (tax.getTaxType().intValue() == ParamConst.TAX_TYPE_SERVICE) {
							orderDetailTax.setTaxPrice(ParamConst.DOUBLE_ZERO);
						} else {
							taxTotal = BH.add(
									taxTotal,
									BH.mul(preTaxPrice,
											BH.getBDNoFormat(tax.getTaxPercentage()), false),
									false);
							orderDetailTax.setTaxPrice(BH.mul(preTaxPrice,
									BH.getBDNoFormat(tax.getTaxPercentage()), false).toString());
						}
					} else {
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
		}
		return taxTotal;
	}

	/**
	 * 获取订单详情的Tax“
	 *
	 * @param orderDetail
	 * @return
	 */
	public static BigDecimal getOrderDetailBeforTax(Order order,
											   OrderDetail orderDetail) {
		BigDecimal preTaxPrice = BH.getBD(orderDetail.getRealPrice());


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

				if(tax.getBeforeDiscount()==1) {
					if (tax == null) {
						continue;
					}
					OrderDetailTax orderDetailTax = ObjectFactory.getInstance()
							.getOrderDetailTax(order, orderDetail, tax, taxCategory.getIndex().intValue());
					if ((taxCategory.getTaxOn().intValue() == ParamConst.TAX_ON_TAX_1
							|| taxCategory.getTaxOn().intValue() == ParamConst.TAX_ON_TAX_2)) {
						TaxCategory temp = CoreData.getInstance().getTaxCategory(
								taxCategory.getTaxOnId());

						Tax taxOn = CoreData.getInstance().getTax(temp.getTaxId());
						if(taxOn.getBeforeDiscount()==1){


						if (order.getIsTakeAway().intValue() == ParamConst.TAKE_AWAY
								|| orderDetail.getIsTakeAway() == ParamConst.TAKE_AWAY) {
							if (orderDetailTax.getTaxType().intValue() == ParamConst.TAX_TYPE_SERVICE) {
								orderDetailTax.setTaxPrice(ParamConst.DOUBLE_ZERO);
							} else if (taxOn.getTaxType().intValue() == ParamConst.TAX_TYPE_SERVICE
									&& tax.getTaxType().intValue() == ParamConst.TAX_TYPE_SERVICE) {
								taxTotal = BH.getBD(ParamConst.DOUBLE_ZERO);
								orderDetailTax.setTaxPrice(ParamConst.DOUBLE_ZERO);
							} else if (taxOn.getTaxType().intValue() == ParamConst.TAX_TYPE_SERVICE) {
								taxTotal = BH.add(taxTotal, BH.mul(preTaxPrice,
										BH.getBDNoFormat(tax.getTaxPercentage()), false), false);
								orderDetailTax.setTaxPrice(BH.mul(preTaxPrice,
										BH.getBDNoFormat(tax.getTaxPercentage()), false).toString());
							} else if (tax.getTaxType().intValue() == ParamConst.TAX_TYPE_SERVICE) {
								taxTotal = BH.add(taxTotal, BH.mul(preTaxPrice,
										BH.getBDNoFormat(taxOn.getTaxPercentage()), false), false);
								orderDetailTax.setTaxPrice(BH.mul(preTaxPrice,
										BH.getBDNoFormat(taxOn.getTaxPercentage()), false).toString());
							} else {
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
						}

					} else {
						if (order.getIsTakeAway().intValue() == ParamConst.TAKE_AWAY
								|| orderDetail.getIsTakeAway() == ParamConst.TAKE_AWAY) {
							if (tax.getTaxType().intValue() == ParamConst.TAX_TYPE_SERVICE) {
								orderDetailTax.setTaxPrice(ParamConst.DOUBLE_ZERO);
							} else {
								taxTotal = BH.add(
										taxTotal,
										BH.mul(preTaxPrice,
												BH.getBDNoFormat(tax.getTaxPercentage()), false),
										false);
								orderDetailTax.setTaxPrice(BH.mul(preTaxPrice,
										BH.getBDNoFormat(tax.getTaxPercentage()), false).toString());
							}
						} else {
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
			if (BH.getBD(itemHappyHour.getDiscountPrice()).compareTo(BH.getBD(ParamConst.DOUBLE_ZERO)) != 0) {
				price = BH.sub(BH.getBD(orderDetail.getItemPrice()),
						BH.getBDNoFormat(itemHappyHour.getDiscountPrice()), false);
			}  else if (Double.parseDouble(itemHappyHour.getDiscountRate()) > 0) {
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
//		BigDecimal promotionPrice = BH.getBD(ParamConst.DOUBLE_ZERO);
//		ItemPromotion itemPromotion = getItemPromotion(order, orderDetail);
//		if (itemPromotion != null&&hasWeek(itemPromotion.getPromotionId())) {
//			if (BH.getBD(itemPromotion.getDiscountPrice()).compareTo(BH.getBD(ParamConst.DOUBLE_ZERO)) != 0) {
//				price = BH.sub(BH.getBD(orderDetail.getItemPrice()),
//						BH.getBD(itemPromotion.getDiscountPrice()), false);
//                promotionPrice=BH.add(promotionPrice,BH.getBD(itemPromotion.getDiscountPrice()), true);
//			}
//			else if (Double.parseDouble(itemPromotion.getDiscountPercentage()) > 0) {
//				orderDetail.setDiscountRate(itemPromotion.getDiscountPercentage());
//
//				 if(price.compareTo(BH.getBD("0")) > 0) {
//					 price = BH.sub(BH.getBD(orderDetail.getItemPrice()), BH
//							 .mul(BH.getBD(price),
//									 BH.getBD(itemPromotion.getDiscountPercentage()),
//									 false), false);
//				 }else {
//					 price = BH.sub(BH.getBD(orderDetail.getItemPrice()), BH
//							 .mul(BH.getBD(orderDetail.getItemPrice()),
//									 BH.getBD(itemPromotion.getDiscountPercentage()),
//									 false), false);
//				 }
//				promotionPrice=BH.add(promotionPrice,BH.mul(BH.getBD(orderDetail.getItemPrice()),
//                        BH.getBD(itemPromotion.getDiscountPercentage()),
//                        false),false) ;
//			}
//			if (itemPromotion.getFreeNum().intValue() > 0) {
//				//promotionPrice = BH.getBD(orderDetail.getItemPrice());
//			}
//		} else {
//			price = BH.getBD(orderDetail.getItemPrice());
//		}

//		promotionPrice = BH.mul(promotionPrice, BH.getBD(orderDetail.getItemNum()), false);
//		if(promotionPrice.compareTo(BH.getBD(ParamConst.DOUBLE_ZERO)) > 0){
//			long nowTime = System.currentTimeMillis();
//
//			PromotionData promotionData=PromotionDataSQL.getPromotionData(order.getId(),orderDetail.getId());
//			if(promotionData==null){
//				List<Promotion> promotionList= PromotionSQL.getAllPromotion();
//				Promotion promotion= PromotionSQL.getPromotion(itemPromotion.getPromotionId());
//                promotionData=new PromotionData();
//				promotionData.setPromotionType(0);
//				promotionData.setPromotionAmount(promotionPrice.toString());
//				promotionData.setItemId(orderDetail.getItemId());
//				promotionData.setItemName(orderDetail.getItemName());
//				promotionData.setFreeItemId(itemPromotion.getFreeItemId());
//				promotionData.setFreeNum(itemPromotion.getFreeNum());
//				promotionData.setFreeItemName(itemPromotion.getFreeItemName());
//				promotionData.setOrderDetailId(orderDetail.getId());
//				promotionData.setOrderId(orderDetail.getOrderId());
//				promotionData.setItemNum(orderDetail.getItemNum());
//				promotionData.setCreateTime(nowTime);
//				promotionData.setPromotionId(itemPromotion.getPromotionId());
//				promotionData.setPromotionName(promotion.getPromotionName());
//				promotionData.setBusinessDate( order.getBusinessDate());
//				PromotionDataSQL.addPromotionData(promotionData);
//			}else {
//				promotionData.setPromotionAmount(promotionPrice.toString());
//				promotionData.setItemNum(orderDetail.getItemNum());
//				promotionData.setUpdateTime(nowTime);
//				PromotionDataSQL.updatePromotionData(promotionData);
//			}
	//	}

		price = BH.mul(price, BH.getBD(orderDetail.getItemNum()), false);
		if(BH.getBDNoFormat(orderDetail.getWeight()).compareTo(BH.getBD(ParamConst.DOUBLE_ZERO)) > 0){
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

			order.setOrderRound(BH.formatRound(BH.getBD(discount)).toString());
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
      //  PromotionDataSQL.deleteAllPromotionData();
        BigDecimal promotionPrice = BH.getBD(ParamConst.DOUBLE_ZERO);
		BigDecimal maxPrice = BH.getBD(ParamConst.DOUBLE_ZERO);

//        List<PromotionOrder> promotionOrders = getOrderPromotion();
//        if (promotionOrders != null) {
//
//			for (int i = 0; i < promotionOrders.size(); i++) {
//				PromotionOrder promotionOrder = promotionOrders.get(i);
//				if(!hasWeek(promotionOrder.getPromotionId())){
//                         break;
//				}
////				if(maxPrice.compareTo(BH.getBD(promotionOrder.getBasePrice()))>0)
////                {
////                    maxPrice=BH.getBD(promotionOrder.getBasePrice());
////                }
//				if (subTotal.compareTo(BH.getBD(ParamConst.DOUBLE_ZERO)) != 0&&subTotal.compareTo(BH.getBD(promotionOrder.getBasePrice())) >= 0) {
//					if (BH.getBD(promotionOrder.getDiscountPrice()).compareTo(BH.getBD(ParamConst.DOUBLE_ZERO)) > 0) {
//
//						subTotal = BH.sub(BH.getBD(subTotal),
//								BH.getBDNoFormat(promotionOrder.getDiscountPrice()), false);
//
//						if (subTotal.compareTo(BH.getBD(ParamConst.DOUBLE_ZERO)) < 0) {
//							subTotal = BH.getBD(ParamConst.DOUBLE_ZERO);
//						}
//						promotionPrice = BH.add(promotionPrice, BH.getBDNoFormat(promotionOrder.getDiscountPrice()), false);
//					}
//					else if (Double.parseDouble(promotionOrder.getDiscountPercentage()) > 0) {
//
//
//						if (subTotal.compareTo(BH.getBD("0")) > 0) {
//							subTotal = BH.sub(BH.getBD(subTotal), BH
//									.mul(BH.getBD(subTotal),
//											BH.getBDNoFormat(promotionOrder.getDiscountPercentage()),
//											false), false);
//						}
//						promotionPrice = BH.add(promotionPrice, BH.mul(BH.getBD(subTotal),
//								BH.getBDNoFormat(promotionOrder.getDiscountPercentage()),
//								false), false);
//					}
//
//					order.setSubTotal(BH.getBD(subTotal).toString());
//					if (subTotal.compareTo(BH.getBD(promotionOrder.getBasePrice())) >= 0) {
//						//  orderPromotion 获取basePrice进行优惠（满足多个添加多个免费菜）
//
//						if (promotionOrder != null && promotionOrder.getFreeNum().intValue() > 0) {
//							ItemDetail itemDetail = CoreData.getInstance()
//									.getItemDetailByTemplateId(promotionOrder.getFreeItemId());
//							if (itemDetail == null) {
//								return;
//
//							}
//							OrderDetail freeOrderDetail = ObjectFactory.getInstance()
//									.getOrderFreeOrderDetail(order, itemDetail,
//											promotionOrder);
//							OrderDetailSQL.updateOrderDetail(freeOrderDetail);
//						}
//					}
//
//					if(promotionOrder!=null) {
//						long nowTime = System.currentTimeMillis();
//
//						PromotionData promotionData = PromotionDataSQL.getPromotionDataOrType(order.getId(), 1);
//
//						if (promotionData == null) {
//							List<Promotion> promotionList = PromotionSQL.getAllPromotion();
//							Promotion promotion = PromotionSQL.getPromotion(promotionOrder.getPromotionId());
//							promotionData = new PromotionData();
//							promotionData.setPromotionType(1);
//							promotionData.setPromotionAmount(promotionPrice.toString());
//							promotionData.setOrderId(order.getId());
//							promotionData.setCreateTime(nowTime);
//							promotionData.setPromotionId(promotionOrder.getPromotionId());
//							promotionData.setPromotionName(promotion.getPromotionName());
//							promotionData.setBusinessDate(order.getBusinessDate());
//							promotionData.setFreeItemName(promotionOrder.getFreeItemName());
//							promotionOrder.setFreeItemId(promotionOrder.getFreeItemId());
//							promotionData.setFreeNum(promotionOrder.getFreeNum());
//							PromotionDataSQL.addPromotionData(promotionData);
//						} else {
//							promotionData.setPromotionAmount(promotionPrice.toString());
//							promotionData.setUpdateTime(nowTime);
//							PromotionDataSQL.updatePromotionData(promotionData);
//						}
//					}
//				}

//			}
  //  orderPromotion 获取最大的basePrice进行优惠（满足多个选最大的）
//			if(maxPrice.compareTo(BH.getBD(ParamConst.DOUBLE_ZERO))>0) {
//				PromotionOrder promotionOrder = PromotionOrderSQL.getPromotionOrderOrBasePrice(maxPrice.toString());
//
//			}

	//	}

		order.setSubTotal(BH.getBD(subTotal).toString());
		}

	private static BigDecimal promotionAndFree(Order order, List<OrderDetail> orderDetails, Promotion promotion, BigDecimal total) {
		BigDecimal   promotionPrice=BH.getBD(ParamConst.DOUBLE_ZERO);
          int itemNum=0;
          OrderDetail  orderDetailMin = null;
		for(OrderDetail orderDetail : orderDetails){
			ItemDetail itemDetail= CoreData.getInstance()
					.getItemDetailById(orderDetail.getItemId().intValue());


			if (promotion.getItemId()<=0&&promotion.getItemCategoryId()>0) {
				if (promotion.getItemCategoryId().intValue() ==itemDetail.getItemCategoryId()&&orderDetail.getIsFree()!=ParamConst.FREE) {
					CoreData.getInstance().getItemDetails(promotion.getItemCategoryId());
					itemNum = itemNum + orderDetail.getItemNum();
					if(promotionPrice.compareTo(BH.getBD(ParamConst.DOUBLE_ZERO))==0||promotionPrice.compareTo(BH.getBD(orderDetail.getItemPrice()))>0)
					{
						promotionPrice= BH.getBD(orderDetail.getItemPrice());
						orderDetailMin=orderDetail;
					}

				}
			}
			// 最后按照主分类找
			if (promotion.getItemId()<=0&& promotion.getItemCategoryId()<=0) {
				if (promotion.getItemMainCategoryId().intValue() == itemDetail
						.getItemMainCategoryId().intValue()&&orderDetail.getIsFree()!=ParamConst.FREE) {
					itemNum = itemNum + orderDetail.getItemNum();

					if(promotionPrice.compareTo(BH.getBD(ParamConst.DOUBLE_ZERO))==0||promotionPrice.compareTo(BH.getBD(orderDetail.getItemPrice()))>0)
					{
						promotionPrice= BH.getBD(orderDetail.getItemPrice());
						orderDetailMin=orderDetail;
					}
				}

			}
		}
		// 然后按照分类来找
		if(orderDetailMin!=null){

			if(itemNum>=promotion.getItemNum()){
//			subTotal = BH.sub(BH.getBD(subTotal),
//					BH.getBD(promotion.getDiscountPrice()), false);
				// 免费最便宜一个菜
//				OrderDetail freeOrderDetail = ObjectFactory.getInstance()
//						.getItemFreeOrderDetailMin(order, orderDetailMin,true);
//				OrderDetailSQL.updateOrderDetail(freeOrderDetail);
//				if (promotion.getFreeNum() > 0) {
//					ItemDetail itemDetailFree = CoreData.getInstance()
//							.getItemDetailByTemplateId(promotion.getFreeItemId());
//					OrderDetail proOrderDetail = ObjectFactory.getInstance()
//							.getOrderDetailAndPromotion(order, itemDetailFree
//									, 0, promotion);
//					proOrderDetail.setOrderDetailStatus(ParamConst.ORDERDETAIL_STATUS_ADDED);
//					OrderDetailSQL.updateOrderDetail(proOrderDetail);
				promotionPrice=BH.getBD(orderDetailMin.getItemPrice());
					total = BH.sub(BH.getBD(total), promotionPrice
							, false);

					addPromotion(order, promotion, null, promotionPrice.toString());

			}else {

//				OrderDetail freeOrderDetail = ObjectFactory.getInstance()
//						.getItemFreeOrderDetailMin(order, orderDetailMin,false);
//				OrderDetailSQL.updateOrderDetail(freeOrderDetail);
				deletePromotion(order,promotion);

			}
		}



		return total;
	}

	private static BigDecimal promotionAndPax(Order order, List<OrderDetail> orderDetails, Promotion promotion, BigDecimal total, int pax) {

		BigDecimal   promotionPrice=BH.getBD(ParamConst.DOUBLE_ZERO);
		if(total.compareTo(BH.getBD(ParamConst.DOUBLE_ZERO)) >0&&pax>=promotion.getGuestNum()){
//			subTotal = BH.sub(BH.getBD(subTotal),
//					BH.getBD(promotion.getDiscountPrice()), false);

			BigDecimal discount = BH.divThirdFormat(BH.getBD(promotion.getDiscountPercentage()), BH.getBD(100), false);

			promotionPrice = BH.mul(promotionPrice, discount, false);
			total = BH.sub(BH.getBD(total),promotionPrice
					, false);
			addPromotion(order, promotion, null,promotionPrice.toString());
		}else {
			deletePromotion(order,promotion);
		}

		return total;
	}

	private static BigDecimal promotionAndTotalDiscount(Order order, List<OrderDetail> orderDetails, Promotion promotion, BigDecimal total) {

		BigDecimal promotionPrice = BH.getBD(ParamConst.DOUBLE_ZERO);

	    	BigDecimal  promotionTotal=BH.getBD(ParamConst.DOUBLE_ZERO);
		for(OrderDetail orderDetail : orderDetails){
			ItemDetail itemDetail= CoreData.getInstance()
					.getItemDetailById(orderDetail.getItemId().intValue());


			if (promotion.getItemId()>0) {
				if (promotion.getItemId().intValue() ==itemDetail.getItemTemplateId().intValue()&&orderDetail.getIsFree()!=ParamConst.FREE) {
				//	CoreData.getInstance().getItemDetails(promotion.getItemCategoryId());
					promotionTotal=BH.add(promotionPrice,BH.getBD(orderDetail.getRealPrice()),false);
					//itemNum = itemNum + orderDetail.getItemNum();
//						if(promotionPrice.compareTo(BH.getBD(ParamConst.DOUBLE_ZERO))==0||promotionPrice.compareTo(BH.getBD(orderDetail.getItemPrice()))>0)
//						{
//							promotionPrice= BH.getBD(orderDetail.getItemPrice());
//						//orderDetailMin=orderDetail;
//						}
				}
			}


			if (promotion.getItemId()<=0&&promotion.getItemCategoryId()>0) {
				if (promotion.getItemCategoryId().intValue() ==itemDetail.getItemCategoryId()&&orderDetail.getIsFree()!=ParamConst.FREE) {
					CoreData.getInstance().getItemDetails(promotion.getItemCategoryId());
					promotionTotal=BH.add(promotionPrice,BH.getBD(orderDetail.getRealPrice()),false);
				//	itemNum = itemNum + orderDetail.getItemNum();
//						if(promotionPrice.compareTo(BH.getBD(ParamConst.DOUBLE_ZERO))==0||promotionPrice.compareTo(BH.getBD(orderDetail.getItemPrice()))>0)
//						{
//							promotionPrice= BH.getBD(orderDetail.getItemPrice());
//						//orderDetailMin=orderDetail;
//						}

				}
			}
			// 最后按照主分类找
			if (promotion.getItemId()<=0&& promotion.getItemCategoryId()<=0) {
				if (promotion.getItemMainCategoryId().intValue() == itemDetail
						.getItemMainCategoryId().intValue()&&orderDetail.getIsFree()!=ParamConst.FREE) {
					promotionTotal=BH.add(promotionPrice,BH.getBD(orderDetail.getRealPrice()),false);
				//	itemNum = itemNum + orderDetail.getItemNum();

//						if(promotionPrice.compareTo(BH.getBD(ParamConst.DOUBLE_ZERO))==0||promotionPrice.compareTo(BH.getBD(orderDetail.getItemPrice()))>0)
//						{
//							promotionPrice= BH.getBD(orderDetail.getItemPrice());
//							//orderDetailMin=orderDetail;
//						}
				}

			}

//
		}

		if(promotionTotal.compareTo(BH.getBD(ParamConst.DOUBLE_ZERO)) == 0 ) {
			if (total.compareTo(BH.getBD(ParamConst.DOUBLE_ZERO)) != 0 && total.compareTo(BH.getBD(promotion.getBasePrice())) >= 0) {
				if (BH.getBD(promotion.getDiscountPercentage()).compareTo(BH.getBD(ParamConst.DOUBLE_ZERO)) > 0&&promotion.getItemMainCategoryId()==0) {

					BigDecimal discount = BH.divThirdFormat(BH.getBD(promotion.getDiscountPercentage()), BH.getBD(100), false);

					promotionPrice = BH.mul(BH.getBD(promotionTotal),
							BH.getBD(discount),
							false);

				     total = BH.sub(BH.getBD(total),promotionPrice
						, false);


					promotionPrice = BH.add(promotionPrice, promotionPrice, false);
					addPromotion(order, promotion, null, promotionPrice.toString());
				}


			} else {
				deletePromotion(order, promotion);
			}
		}else {
			if ( promotionTotal.compareTo(BH.getBD(promotion.getBasePrice())) >= 0) {
				if (BH.getBD(promotion.getDiscountPercentage()).compareTo(BH.getBD(ParamConst.DOUBLE_ZERO)) > 0) {

					BigDecimal discount = BH.divThirdFormat(BH.getBD(promotion.getDiscountPercentage()), BH.getBD(100), false);

					promotionPrice = BH.mul(BH.getBD(promotionTotal),
							BH.getBD(discount),
							false);

				total = BH.sub(BH.getBD(total),promotionPrice
						, false);


					promotionPrice = BH.add(promotionPrice, promotionPrice, false);
					addPromotion(order, promotion, null, promotionPrice.toString());
				}

			} else {
				deletePromotion(order, promotion);
			}
		}
		return total;
	}


	private static BigDecimal promotionAndTotalFree(Order order, List<OrderDetail> orderDetails, Promotion promotion, BigDecimal total) {
		BigDecimal promotionTotal = BH.getBD(ParamConst.DOUBLE_ZERO);
		BigDecimal promotionPrice = BH.getBD(ParamConst.DOUBLE_ZERO);
//		if (subTotal.compareTo(BH.getBD(ParamConst.DOUBLE_ZERO)) != 0 && subTotal.compareTo(BH.getBD(promotion.getBasePrice())) >= 0) {
//			if (BH.getBD(promotion.getDiscountPercentage()).compareTo(BH.getBD(ParamConst.DOUBLE_ZERO)) > 0) {
//
////				subTotal = BH.sub(BH.getBD(subTotal),
////						BH.getBD(promotion.getDiscountPrice()), false);
////
////				promotionPrice = BH.add(promotionPrice, BH.getBDNoFormat(promotion.getDiscountPrice()), false);
//				addPromotion(order, promotion,null,promotionPrice.toString());
//			}

		for(OrderDetail orderDetail : orderDetails){
			ItemDetail itemDetail= CoreData.getInstance()
					.getItemDetailById(orderDetail.getItemId().intValue());


			if (promotion.getItemId()>0) {
				if (promotion.getItemId().intValue() ==itemDetail.getItemTemplateId().intValue()&&orderDetail.getIsFree()!=ParamConst.FREE) {
					//	CoreData.getInstance().getItemDetails(promotion.getItemCategoryId());
					promotionTotal=BH.add(promotionTotal,BH.getBD(orderDetail.getRealPrice()),false);
					//itemNum = itemNum + orderDetail.getItemNum();
//						if(promotionPrice.compareTo(BH.getBD(ParamConst.DOUBLE_ZERO))==0||promotionPrice.compareTo(BH.getBD(orderDetail.getItemPrice()))>0)
//						{
//							promotionPrice= BH.getBD(orderDetail.getItemPrice());
//						//orderDetailMin=orderDetail;
//						}
				}
			}


			if (promotion.getItemId()<=0&&promotion.getItemCategoryId()>0) {
				if (promotion.getItemCategoryId().intValue() ==itemDetail.getItemCategoryId()&&orderDetail.getIsFree()!=ParamConst.FREE) {
					CoreData.getInstance().getItemDetails(promotion.getItemCategoryId());
					promotionTotal=BH.add(promotionTotal,BH.getBD(orderDetail.getRealPrice()),false);
					//	itemNum = itemNum + orderDetail.getItemNum();
//						if(promotionPrice.compareTo(BH.getBD(ParamConst.DOUBLE_ZERO))==0||promotionPrice.compareTo(BH.getBD(orderDetail.getItemPrice()))>0)
//						{
//							promotionPrice= BH.getBD(orderDetail.getItemPrice());
//						//orderDetailMin=orderDetail;
//						}

				}
			}
			// 最后按照主分类找
			if (promotion.getItemId()<=0&& promotion.getItemCategoryId()<=0) {
				if (promotion.getItemMainCategoryId().intValue() == itemDetail
						.getItemMainCategoryId().intValue()&&orderDetail.getIsFree()!=ParamConst.FREE) {
					promotionTotal=BH.add(promotionTotal,BH.getBD(orderDetail.getRealPrice()),false);
					//	itemNum = itemNum + orderDetail.getItemNum();

//						if(promotionPrice.compareTo(BH.getBD(ParamConst.DOUBLE_ZERO))==0||promotionPrice.compareTo(BH.getBD(orderDetail.getItemPrice()))>0)
//						{
//							promotionPrice= BH.getBD(orderDetail.getItemPrice());
//							//orderDetailMin=orderDetail;
//						}
				}

			}

//
		}

		if(promotion.getItemMainCategoryId()>0) {


			if (promotionTotal.compareTo(BH.getBD(ParamConst.DOUBLE_ZERO)) != 0 && promotionTotal.compareTo(BH.getBD(promotion.getBasePrice())) >= 0) {

				if (promotion.getFreeNum() > 0) {
					ItemDetail itemDetailFree = CoreData.getInstance()
							.getItemDetailByTemplateId(promotion.getFreeItemId());
					if (itemDetailFree == null) {
						return total;
					}
					OrderDetail proOrderDetail = ObjectFactory.getInstance()
							.getOrderDetailAndPromotion(order, itemDetailFree
									,0,promotion);
					proOrderDetail.setOrderDetailStatus(ParamConst.ORDERDETAIL_STATUS_ADDED);
					OrderDetailSQL.updateOrderDetail(proOrderDetail);


					promotionPrice = BH.mul(BH.getBD(proOrderDetail.getItemPrice()),
							BH.getBD(promotion.getFreeNum()),
							false);

					total = BH.sub(BH.getBD(total),promotionPrice
							, false);


					promotionPrice = BH.add(promotionPrice, promotionPrice, false);
					//addPromotion(order, promotion, null, promotionPrice.toString());
					addPromotion(order, promotion, proOrderDetail, promotionPrice.toString());

				}
			} else {
				deletePromotion(order, promotion);
			}
		}else {
			 if (total.compareTo(BH.getBD(promotion.getBasePrice())) >= 0 ) {

				if (promotion.getFreeNum() > 0) {
					ItemDetail itemDetailFree = CoreData.getInstance()
							.getItemDetailByTemplateId(promotion.getFreeItemId());
					if (itemDetailFree == null) {
						return total;
					}
					OrderDetail proOrderDetail = ObjectFactory.getInstance()
							.getOrderDetailAndPromotion(order, itemDetailFree,0,
									promotion);

					proOrderDetail.setOrderDetailStatus(ParamConst.ORDERDETAIL_STATUS_ADDED);
					OrderDetailSQL.updateOrderDetail(proOrderDetail);


					promotionPrice = BH.mul(BH.getBD(proOrderDetail.getItemPrice()),
							BH.getBD(promotion.getFreeNum()),
							false);

					total = BH.sub(BH.getBD(total),promotionPrice
							, false);

					//OrderDetailSQL.updateOrderDetail(freeOrderDetail);
					addPromotion(order, promotion, proOrderDetail, promotionPrice.toString());

				}
			}else {
				 deletePromotion(order, promotion);
			 }
		}
		return total;
	}

    private static BigDecimal promotionAndItemFree(Order order, List<OrderDetail> orderDetails, Promotion promotion, BigDecimal total) {
//
//        int itemNum = 0;
//        int secondItemNum = 0;
//        BigDecimal promotionPrice = BH.getBD(ParamConst.DOUBLE_ZERO);
//        if (orderDetails.size() > 0) {
//            for (OrderDetail orderDetail : orderDetails) {
//                if (orderDetail.getItemId() == promotion.getItemId()) {
//                    itemNum = itemNum + orderDetail.getItemNum();
//                } else if (orderDetail.getItemId() == promotion.getSecondItemId()) {
//                    secondItemNum = secondItemNum + orderDetail.getItemNum();
//                }
//            }

		int itemNum = 0;

		BigDecimal   promotionPrice=BH.getBD(ParamConst.DOUBLE_ZERO);
		for(OrderDetail orderDetail : orderDetails){
			ItemDetail itemDetail= CoreData.getInstance()
					.getItemDetailById(orderDetail.getItemId().intValue());


			if (promotion.getItemId()>0) {
				if (promotion.getItemId().intValue() ==itemDetail.getItemTemplateId().intValue()&&orderDetail.getIsFree()!=ParamConst.FREE) {
					CoreData.getInstance().getItemDetails(promotion.getItemCategoryId());
					itemNum = itemNum + orderDetail.getItemNum();
//						if(promotionPrice.compareTo(BH.getBD(ParamConst.DOUBLE_ZERO))==0||promotionPrice.compareTo(BH.getBD(orderDetail.getItemPrice()))>0)
//						{
//							promotionPrice= BH.getBD(orderDetail.getItemPrice());
//						//orderDetailMin=orderDetail;
//						}
				}
			}


			if (promotion.getItemId()<=0&&promotion.getItemCategoryId()>0) {
				if (promotion.getItemCategoryId().intValue() ==itemDetail.getItemCategoryId()&&orderDetail.getIsFree()!=ParamConst.FREE) {
					CoreData.getInstance().getItemDetails(promotion.getItemCategoryId());
					itemNum = itemNum + orderDetail.getItemNum();
//						if(promotionPrice.compareTo(BH.getBD(ParamConst.DOUBLE_ZERO))==0||promotionPrice.compareTo(BH.getBD(orderDetail.getItemPrice()))>0)
//						{
//							promotionPrice= BH.getBD(orderDetail.getItemPrice());
//						//orderDetailMin=orderDetail;
//						}

				}
			}
			// 最后按照主分类找
			if (promotion.getItemId()<=0&& promotion.getItemCategoryId()<=0) {
				if (promotion.getItemMainCategoryId().intValue() == itemDetail
						.getItemMainCategoryId().intValue()&&orderDetail.getIsFree()!=ParamConst.FREE) {
					itemNum = itemNum + orderDetail.getItemNum();

//						if(promotionPrice.compareTo(BH.getBD(ParamConst.DOUBLE_ZERO))==0||promotionPrice.compareTo(BH.getBD(orderDetail.getItemPrice()))>0)
//						{
//							promotionPrice= BH.getBD(orderDetail.getItemPrice());
//							//orderDetailMin=orderDetail;
//						}
				}

			}


     //       if (itemNum >= promotion.getItemNum() ) {
                //折扣
//                if (BH.getBD(promotion.getDiscountPrice()).compareTo(BH.getBD(ParamConst.DOUBLE_ZERO)) > 0) {
//
//                    subTotal = BH.sub(BH.getBD(subTotal), BH
//                            .mul(BH.getBD(subTotal),
//                                    BH.getBD(promotion.getDiscountPercentage()),
//                                    false), false);
//
//                    promotionPrice = BH.add(promotionPrice, BH.mul(BH.getBD(subTotal),
//                            BH.getBD(promotion.getDiscountPercentage()),
//                            false), false);
//                    addPromotion(order, promotion, promotionPrice);
//                }
//                    //免费菜
//                    if (promotion != null && promotion.getFreeNum().intValue() > 0) {
////						ItemDetail itemDetails= CoreData.getInstance()
////								.getItemDetailById(promotion.getFreeItemId().intValue());
//                        ItemDetail itemDetails = CoreData.getInstance()
//                                .getItemDetailByTemplateId(promotion.getFreeItemId().intValue());
//                        if (itemDetails == null) {
//
//							OrderDetail freeOrderDetail = ObjectFactory.getInstance()
//									.getPromotionFreeOrderDetail(order, itemDetails,
//											promotion);
//
//							List<OrderDetail>	orderDetaillist = OrderDetailSQL.getOrderDetails(order.getId());
//							// OrderDetailSQL.updateOrderDetail(freeOrderDetail);
//
//
//							addPromotion(order, promotion, freeOrderDetail,null);
//
//						}
//					}else  if (itemNum < promotion.getItemNum() ){
//						deletePromotion(order, promotion);
//					}              return subTotal;
//                        }
//
       }
		if (itemNum >= promotion.getItemNum() ) {
			//折扣
//                if (BH.getBD(promotion.getDiscountPrice()).compareTo(BH.getBD(ParamConst.DOUBLE_ZERO)) > 0) {
//
//                    subTotal = BH.sub(BH.getBD(subTotal), BH
//                            .mul(BH.getBD(subTotal),
//                                    BH.getBD(promotion.getDiscountPercentage()),
//                                    false), false);
//
//                    promotionPrice = BH.add(promotionPrice, BH.mul(BH.getBD(subTotal),
//                            BH.getBD(promotion.getDiscountPercentage()),
//                            false), false);
//                    addPromotion(order, promotion, promotionPrice);
//                }
//                    //免费菜
			if (promotion != null && promotion.getFreeNum().intValue() > 0) {
//						ItemDetail itemDetails= CoreData.getInstance()
//								.getItemDetailById(promotion.getFreeItemId().intValue());
				ItemDetail itemDetails = CoreData.getInstance()
						.getItemDetailByTemplateId(promotion.getFreeItemId().intValue());
				if (itemDetails == null) {
					return total;
				}
				OrderDetail proOrderDetail = ObjectFactory.getInstance()
						.getOrderDetailAndPromotion(order, itemDetails
								,0,promotion);
				proOrderDetail.setOrderDetailStatus(ParamConst.ORDERDETAIL_STATUS_ADDED);
				OrderDetailSQL.updateOrderDetail(proOrderDetail);
				promotionPrice=BH.mul(BH.getBD(proOrderDetail.getItemPrice()),BH.getBD(promotion.getFreeNum()) ,false);
				total = BH.sub(BH.getBD(total),promotionPrice, false);
			  // List<OrderDetail>	orderDetaillist = OrderDetailSQL.getOrderDetails(order.getId());
				// OrderDetailSQL.updateOrderDetail(freeOrderDetail);


				addPromotion(order, promotion, proOrderDetail,promotionPrice.toString());

			}
		}else  if (itemNum < promotion.getItemNum() ){
			deletePromotion(order, promotion);
		}
        return total;
    }

    private static BigDecimal  promotionAndItemPrice(Order order,List<OrderDetail> orderDetails,Promotion promotion,BigDecimal total)
        {

			int itemNum = 0;

			BigDecimal   promotionPrice=BH.getBD(ParamConst.DOUBLE_ZERO);
			for(OrderDetail orderDetail : orderDetails){
				ItemDetail itemDetail= CoreData.getInstance()
						.getItemDetailById(orderDetail.getItemId().intValue());


				if (promotion.getItemId()>0) {
					if (promotion.getItemId().intValue() ==itemDetail.getItemTemplateId().intValue()&&orderDetail.getIsFree()!=ParamConst.FREE) {
						//CoreData.getInstance().getItemDetails(promotion.getItemCategoryId());
						itemNum = itemNum + orderDetail.getItemNum();
//						if(promotionPrice.compareTo(BH.getBD(ParamConst.DOUBLE_ZERO))==0||promotionPrice.compareTo(BH.getBD(orderDetail.getItemPrice()))>0)
//						{
//							promotionPrice= BH.getBD(orderDetail.getItemPrice());
//						//orderDetailMin=orderDetail;
//						}
					}
					}


				if (promotion.getItemId()<=0&&promotion.getItemCategoryId()>0) {
					if (promotion.getItemCategoryId().intValue() ==itemDetail.getItemCategoryId()&&orderDetail.getIsFree()!=ParamConst.FREE) {
						CoreData.getInstance().getItemDetails(promotion.getItemCategoryId());
						itemNum = itemNum + orderDetail.getItemNum();
//						if(promotionPrice.compareTo(BH.getBD(ParamConst.DOUBLE_ZERO))==0||promotionPrice.compareTo(BH.getBD(orderDetail.getItemPrice()))>0)
//						{
//							promotionPrice= BH.getBD(orderDetail.getItemPrice());
//						//orderDetailMin=orderDetail;
//						}

					}
				}
				// 最后按照主分类找
				if (promotion.getItemId()<=0&& promotion.getItemCategoryId()<=0) {
					if (promotion.getItemMainCategoryId().intValue() == itemDetail
							.getItemMainCategoryId().intValue()&&orderDetail.getIsFree()!=ParamConst.FREE) {
						itemNum = itemNum + orderDetail.getItemNum();

//						if(promotionPrice.compareTo(BH.getBD(ParamConst.DOUBLE_ZERO))==0||promotionPrice.compareTo(BH.getBD(orderDetail.getItemPrice()))>0)
//						{
//							promotionPrice= BH.getBD(orderDetail.getItemPrice());
//							//orderDetailMin=orderDetail;
//						}
					}

				}}

				if (itemNum >= promotion.getItemNum() ) {
//////                    //折扣
					if (BH.getBD(promotion.getDiscountPrice()).compareTo(BH.getBD(ParamConst.DOUBLE_ZERO)) > 0) {

						total = BH.sub(BH.getBD(total), BH.getBD(promotion.getDiscountPrice()), false);
						promotionPrice = BH.add(promotionPrice, BH.getBD(promotion.getDiscountPrice()), false);
						addPromotion(order, promotion, null, promotionPrice.toString());
					}
				} else  if (itemNum < promotion.getItemNum() ){
                	deletePromotion(order, promotion);
				}


//            int itemNum = 0;
////            int secondItemNum = 0;
////            BigDecimal promotionPrice = BH.getBD(ParamConst.DOUBLE_ZERO);
////            if (orderDetails.size() > 0) {
////                for (OrderDetail orderDetail : orderDetails) {
////                    if (orderDetail.getItemId() == promotion.getItemId()&&orderDetail.getIsFree()!=ParamConst.FREE) {
////                        itemNum = itemNum + orderDetail.getItemNum();
////                    } else if (orderDetail.getItemId() == promotion.getSecondItemId()&&orderDetail.getIsFree()!=ParamConst.FREE) {
////                        secondItemNum = secondItemNum + orderDetail.getItemNum();
////                    }
////                }
////                if (itemNum == promotion.getItemNum() && secondItemNum == promotion.getSecondItemNum()) {
//////                    //折扣
////                    if (BH.getBD(promotion.getDiscountPrice()).compareTo(BH.getBD(ParamConst.DOUBLE_ZERO)) > 0) {
////
////                        subTotal = BH.sub(BH.getBD(subTotal), BH.getBD(promotion.getDiscountPrice()), false);
////                        promotionPrice = BH.add(promotionPrice, BH.getBD(promotion.getDiscountPrice()), false);
////                        addPromotion(order, promotion, null,promotionPrice.toString());
////                    }
//////                    //免费菜
//////                    if (promotion != null && promotion.getFreeNum().intValue() > 0) {
//////                        ItemDetail itemDetail = CoreData.getInstance()
//////                                .getItemDetailByTemplateId(promotion.getFreeItemId());
//////                        if (itemDetail == null) {
//////                            return subTotal;
//////                        }
//////                        OrderDetail freeOrderDetail = ObjectFactory.getInstance()
//////                                .getPromotionFreeOrderDetail(order, itemDetail,
//////                                        promotion);
//////                        OrderDetailSQL.updateOrderDetail(freeOrderDetail);
//////                        addPromotion(order, promotion,freeOrderDetail);
//////                    }
////                }else {
////                	deletePromotion(order, promotion);
////				}
////
////            }

	    return total;
    }

    private static void addPromotion(Order order,Promotion promotion,OrderDetail freeOrderDetail,String price) {
	                     long nowTime = System.currentTimeMillis();
						 PromotionData promotionData = PromotionDataSQL.getPromotionDataOrId(order.getId(), promotion.getId());
						if (promotionData == null) {
							promotionData = new PromotionData();
							promotionData.setPromotionType(0);
							if(!TextUtils.isEmpty(price)){
								promotionData.setPromotionAmount(price);
							}

							if(freeOrderDetail!=null){
								promotionData.setOrderDetailId(freeOrderDetail.getId());
							}

							promotionData.setOrderId(order.getId());
							promotionData.setCreateTime(nowTime);
							promotionData.setPromotionId(promotion.getId());
							promotionData.setPromotionName(promotion.getPromotionName());
							promotionData.setBusinessDate(order.getBusinessDate());
							promotionData.setFreeItemName(promotion.getFreeItemName());
							promotionData.setFreeItemId(promotion.getFreeItemId());
							promotionData.setFreeNum(promotion.getFreeNum());
							promotionData.setDiscountPrice(promotion.getDiscountPrice());
							promotionData.setDiscountPercentage(promotion.getDiscountPercentage());
							//promotionData.setPromotionAmount(promotion.getDiscountPrice());
							PromotionDataSQL.addPromotionData(promotionData);
						} else {
							if(freeOrderDetail!=null){
								promotionData.setOrderDetailId(freeOrderDetail.getId());
							}
							if(!TextUtils.isEmpty(price)){
								promotionData.setPromotionAmount(price);
							}

							//	promotionData.setPromotionAmount(promotionPrice.toString());
							promotionData.setUpdateTime(nowTime);
							PromotionDataSQL.updatePromotionData(promotionData);
						}
					}



	private static void deletePromotion(Order order,Promotion promotion) {

		PromotionData promotionData = PromotionDataSQL.getPromotionDataOrId(order.getId(), promotion.getId());
		if (promotionData != null) {

			PromotionDataSQL.deletePromotionAndFree(promotionData);
			//PromotionDataSQL.deletePromotionData(promotionData);
		}
	}




	public static void setPromotion(Order order,List<OrderDetail> orderDetails) {
		BigDecimal promotionPrice = BH.getBD(ParamConst.DOUBLE_ZERO);
		BigDecimal total = BH.getBD(ParamConst.DOUBLE_ZERO);
		total=BH.getBD(order.getTotal());

	List<OrderSplit> orderSplits =OrderSplitSQL.getUnFinishedOrderSplits(order.getId());
     PromotionDataSQL.deletePromotionDataOrderId(order);
	if(orderSplits== null||orderSplits.size()==0){
		List<Promotion>  promotionList =PromotionSQL.getAllPromotion();

		if(promotionList!=null&&promotionList.size()>0) {
			for (int i = 0; i < promotionList.size(); i++) {
				Promotion promotion = promotionList.get(i);
				if (hasWeek(promotion.getPromotionDateInfoId())) {
					switch (promotion.getType()) {

						case 1:
							total = promotionAndItemPrice(order, orderDetails, promotion, total);
							break;
						case 2:
							total = promotionAndItemFree(order, orderDetails, promotion, total);
							break;
						case 3:
							total = promotionAndTotalDiscount(order, orderDetails, promotion, total);
							break;
						case 4:
							total = promotionAndTotalFree(order, orderDetails, promotion, total);
							break;
						case 5:
							total = promotionAndFree(order, orderDetails, promotion, total);
							break;
						case 6:
							total = promotionAndPax(order, orderDetails, promotion, total, order.getPersons());
							break;
					}
				}

			}
		}}
//	 List<PromotionData> promotionDatas=PromotionDataSQL.getPromotionDataOrOrderid(order.getId());
//
//		if (promotionDatas.size() > 0) {
//			for (PromotionData promotionData : promotionDatas) {
//				if(promotionData.getPromotionType()!= 5){
//					if(BH.getBD(promotionData.getDiscountPrice()).compareTo(BH.getBD(ParamConst.DOUBLE_ZERO))!=0) {
//						BigDecimal discount = BH.divThirdFormat(BH.getBD(promotionData.getDiscountPercentage()),BH.getBD(100),false);
//
//						total=BH.sub(total,
//								BH.getBD(promotionData.getDiscountPrice()), false);
//						promotionPrice = BH.add(promotionPrice,
//								BH.getBD(promotionData.getDiscountPrice()), false);
//
//					}else {
//
//						BigDecimal discount = BH.divThirdFormat(BH.getBD(promotionData.getDiscountPercentage()),BH.getBD(100),false);
//						BigDecimal price = BH.mul(total,BH.getBD(discount),false);
//
//						total=BH.sub(total,price,false);
//						promotionPrice = BH.add(promotionPrice,
//								price, false);
//						promotionData.setPromotionAmount(BH.getBD(promotionPrice).toString());
//						PromotionDataSQL.updatePromotionData(promotionData);
//					}
//				}
//
//			}
//		}
         String  promotionTotal= PromotionDataSQL.getPromotionDataSum(order);
		order.setPromotion(promotionTotal);

		order.setTotal(total.toString());

	}




	public static void setOrderBeforTax(Order order, List<OrderDetail> orderDetails) {
		BigDecimal tax = BH.getBD(ParamConst.DOUBLE_ZERO);
		if (orderDetails.size() > 0) {
			for (OrderDetail orderDetail : orderDetails) {
				if(!IntegerUtils.isEmptyOrZero(orderDetail.getAppOrderDetailId())){
					tax = BH.add(tax,
							BH.getBD(orderDetail.getTaxPrice()),
							false);
				}else {
					BigDecimal orderDetailTax = OrderHelper.getOrderDetailBeforTax(order, orderDetail);
					tax = BH.add(tax,
							orderDetailTax,
							false);
					OrderDetailSQL.updateOrderDetailTaxById(orderDetailTax.toString(), orderDetail.getId().intValue());
				}
			}
		}

		order.setOrderRound(BH.add(BH.getBD(order.getOrderRound()),BH.formatRound(BH.getBD(tax)),false).toString());
		order.setTaxAmount(BH.getBD(tax).toString());
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
		tax= BH.add(tax,BH.getBD(order.getTaxAmount()),false);
		order.setOrderRound(BH.add(BH.getBD(order.getOrderRound()),BH.formatRound(BH.getBD(tax)),false).toString());
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

	public static ItemPromotion getItemPromotion(Order order,
												 OrderDetail orderDetail) {
		ItemPromotion itemPromotion = null;
		RevenueCenter revenueCenter = CoreData.getInstance().getRevenueCenter(
				order);
		// 先从RevenueCenter判断，是否存在合法的HappyHour，如果不存在，则认为没有ItemHappyHour
		if (revenueCenter == null
				|| CommonUtil.isNull(revenueCenter.getHappyHourId())) {
			return itemPromotion;
		}
		if (CommonUtil.isNull(revenueCenter.getHappyEndTime())
				|| CommonUtil.isNull(revenueCenter.getHappyStartTime())) {
			return itemPromotion;
		}
		long currentTimeMillis = System.currentTimeMillis();
//		if (currentTimeMillis < revenueCenter.getHappyStartTime()
//				|| currentTimeMillis > revenueCenter.getHappyEndTime()) {
//			return itemPromotion;
//		}

		// 再从HappyHourWeek判断，是否存在合法的HappyHourWeek，如果不存在，则认为没有ItemHappyHour
//		boolean isHappy = hasHappyWeek(revenueCenter.getHappyHourId());
//		if (!isHappy)
//			return itemPromotion;

		// 最后从ItemHappyHour判断，是否存在合法的ItemHappyHour，如果不存在，则认为没有ItemHappyHour
		ItemDetail itemDetail = CoreData.getInstance().getItemDetailById(
				orderDetail.getItemId());
		itemPromotion = CoreData.getInstance().getItemPromotion(revenueCenter,
				itemDetail);
		return itemPromotion;
	}

	public static List<PromotionOrder> getOrderPromotion() {
		PromotionOrder promotionOrder = null;
	    List<PromotionOrder> promotionOrders=PromotionOrderSQL.getAllpromotionOrder();
//	    if(promotionOrders!=null&&promotionOrders.size()>0){
//	    	promotionOrder=promotionOrders.get(0);
//		}
		return promotionOrders;
	}

	public static boolean hasWeek(Integer id) {
		long time = System.currentTimeMillis();
		List<PromotionWeek>	promotionWeeks= PromotionWeekSQL.getAllPromotionWeek();
		if(promotionWeeks!=null) {
			for (PromotionWeek promotionWeek : promotionWeeks) {
				if (promotionWeek.getPromotionDateInfoId().intValue() == id
						.intValue()) {

					if (promotionWeek.getWeek() == TimeUtil
							.getWeek(time)) {

						if (time >= getTimes(promotionWeek.getStartTime()) && getTimes(promotionWeek.getEndTime()) >= time) {
							return true;
						}


					}
				}
			}
		}
		return false;
	}

	private static long getTimes(String time) {
		 long hourTime = 60 * 60 * 1000;
		String[] times = time.split(":");
       long timeAll =TimeUtil.getTimeInMillsByZero(0)+ Integer.valueOf(times[0]).intValue()*hourTime+Integer.valueOf(times[1]).intValue()*60*1000;
           return timeAll;
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
	
	
	/*:加入流水号 */
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
