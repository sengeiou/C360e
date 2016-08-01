package com.alfredbase.javabean;

import com.alfredbase.utils.CommonUtil;

import java.io.Serializable;

public class ReportDaySales implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8531196981865820123L;
	private Integer id;
	private Integer restaurantId;
	/**
	 * 餐厅名称
	 */
	private String restaurantName;
	private Integer revenueId;
	/**
	 * 收银机名称
	 */
	private String revenueName;
	/**
	 * 餐厅营业时间
	 */
	private Long businessDate;
	/**
	 * 菜的金额
	 */
	private String itemSales;
	/**
	 * 菜的销售数量
	 */
	private Integer itemSalesQty;
	/**
	 * 百分比折扣率总金额
	 */
	private String discountPer;
	/**
	 * 百分比折扣率次数
	 */
	private Integer discountPerQty;
	/**
	 * 现金折扣总金额
	 */
	private String discount;
	/**
	 * 现金折扣次数
	 */
	private Integer discountQty;
	/**
	 * 总折扣金额
	 */
	private String discountAmt;
	private String focItem;
	private Integer focItemQty;
	/**
	 * 免费金额
	 */
	private String focBill;
	/**
	 * 免费数量
	 */
	private Integer focBillQty;
	private String totalSales;
	private String cash;
	private Integer cashQty;
	private String nets;
	private Integer netsQty;
	private String visa;
	private Integer visaQty;
	private String mc;
	private Integer mcQty;
	private String amex;
	private Integer amexQty;
	private String jbl;
	private Integer jblQty;
	private String unionPay;
	private Integer unionPayQty;
	private String diner;
	private Integer dinerQty;
	private String holdld;
	private Integer holdldQty;
	private String alipay;
	private Integer alipayQty;
	private String weixinpay;
	private Integer weixinpayQty;
	private String thirdParty;
	private Integer thirdPartyQty;
	private String totalCard;
	private Integer totalCardQty;
	private String totalCash;
	private Integer totalCashQty;
	/**
	 * voidBill结算后退单金额
	 */
	private String billVoid;
	/**
	 * voidBill结算后退单数量
	 */
	private Integer billVoidQty;
	/**
	 * voidItem结算前退单金额
	 */
	private String itemVoid;
	/**
	 * voidItem结算前退单数量
	 */
	private Integer itemVoidQty;
	private String nettSales;
	private Integer totalBills;
	private Integer openCount;
	private Integer firstReceipt;
	private Integer lastReceipt;
	/**
	 * 税的总计
	 */
	private String totalTax;
	
	private Integer orderQty;
	
	private Integer personQty;
	
	private String totalBalancePrice;
	
	private String cashInAmt;
	
	private String cashOutAmt;
	
	private String varianceAmt;
	
	private String inclusiveTaxAmt;// 包含税

	private String paypalpay;
	private Integer paypalpayQty;
	
	public ReportDaySales() {

	}

	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getRestaurantId() {
		return restaurantId;
	}

	public void setRestaurantId(Integer restaurantId) {
		this.restaurantId = restaurantId;
	}

	public String getRestaurantName() {
		return restaurantName;
	}

	public void setRestaurantName(String restaurantName) {
		this.restaurantName = restaurantName;
	}

	public Integer getRevenueId() {
		return revenueId;
	}

	public void setRevenueId(Integer revenueId) {
		this.revenueId = revenueId;
	}

	public String getRevenueName() {
		return revenueName;
	}

	public void setRevenueName(String revenueName) {
		this.revenueName = revenueName;
	}

	public Long getBusinessDate() {
		return businessDate;
	}

	public void setBusinessDate(Long businessDate) {
		this.businessDate = businessDate;
	}

	public String getItemSales() {
		return itemSales;
	}

	public void setItemSales(String itemSales) {
		this.itemSales = itemSales;
	}

	public Integer getItemSalesQty() {
		return itemSalesQty;
	}

	public void setItemSalesQty(Integer itemSalesQty) {
		this.itemSalesQty = itemSalesQty;
	}

	public String getDiscountPer() {
		if(CommonUtil.isNull(discountPer))
			return "0.00";
		return discountPer;
	}

	public void setDiscountPer(String discountPer) {
		this.discountPer = discountPer;
	}

	public Integer getDiscountPerQty() {
		return discountPerQty;
	}

	public void setDiscountPerQty(Integer discountPerQty) {
		this.discountPerQty = discountPerQty;
	}

	public String getDiscount() {
		if(CommonUtil.isNull(discount))
			return "0.00";
		return discount;
	}

	public void setDiscount(String discount) {
		this.discount = discount;
	}

	public Integer getDiscountQty() {
		return discountQty;
	}

	public void setDiscountQty(Integer discountQty) {
		this.discountQty = discountQty;
	}

	public String getDiscountAmt() {
		return discountAmt;
	}

	public void setDiscountAmt(String discountAmt) {
		this.discountAmt = discountAmt;
	}

	public String getFocItem() {
		return focItem;
	}


	public void setFocItem(String focItem) {
		this.focItem = focItem;
	}


	public Integer getFocItemQty() {
		return focItemQty;
	}


	public void setFocItemQty(Integer focItemQty) {
		this.focItemQty = focItemQty;
	}
	
	public String getFocBill() {
		if(CommonUtil.isNull(focBill))
			return "0.00";
		return focBill;
	}

	public void setFocBill(String focBill) {
		this.focBill = focBill;
	}

	public Integer getFocBillQty() {
		return focBillQty;
	}

	public void setFocBillQty(Integer focBillQty) {
		this.focBillQty = focBillQty;
	}

	public String getTotalSales() {
		return totalSales;
	}

	public void setTotalSales(String totalSales) {
		this.totalSales = totalSales;
	}

	public String getCash() {
		return cash;
	}

	public void setCash(String cash) {
		this.cash = cash;
	}

	public Integer getCashQty() {
		return cashQty;
	}

	public void setCashQty(Integer cashQty) {
		this.cashQty = cashQty;
	}

	public String getNets() {
		return nets;
	}

	public void setNets(String nets) {
		this.nets = nets;
	}

	public Integer getNetsQty() {
		return netsQty;
	}

	public void setNetsQty(Integer netsQty) {
		this.netsQty = netsQty;
	}

	public String getVisa() {
		return visa;
	}

	public void setVisa(String visa) {
		this.visa = visa;
	}

	public Integer getVisaQty() {
		return visaQty;
	}

	public void setVisaQty(Integer visaQty) {
		this.visaQty = visaQty;
	}

	public String getMc() {
		return mc;
	}

	public void setMc(String mc) {
		this.mc = mc;
	}

	public Integer getMcQty() {
		return mcQty;
	}

	public void setMcQty(Integer mcQty) {
		this.mcQty = mcQty;
	}

	public String getAmex() {
		return amex;
	}

	public void setAmex(String amex) {
		this.amex = amex;
	}

	public Integer getAmexQty() {
		return amexQty;
	}

	public void setAmexQty(Integer amexQty) {
		this.amexQty = amexQty;
	}

	public String getJbl() {
		return jbl;
	}

	public void setJbl(String jbl) {
		this.jbl = jbl;
	}

	public Integer getJblQty() {
		return jblQty;
	}

	public void setJblQty(Integer jblQty) {
		this.jblQty = jblQty;
	}

	public String getUnionPay() {
		return unionPay;
	}

	public void setUnionPay(String unionPay) {
		this.unionPay = unionPay;
	}

	public Integer getUnionPayQty() {
		return unionPayQty;
	}

	public void setUnionPayQty(Integer unionPayQty) {
		this.unionPayQty = unionPayQty;
	}

	public String getDiner() {
		return diner;
	}

	public void setDiner(String diner) {
		this.diner = diner;
	}

	public Integer getDinerQty() {
		return dinerQty;
	}

	public void setDinerQty(Integer dinerQty) {
		this.dinerQty = dinerQty;
	}

	public String getHoldld() {
		return holdld;
	}

	public void setHoldld(String holdld) {
		this.holdld = holdld;
	}

	public Integer getHoldldQty() {
		return holdldQty;
	}

	public void setHoldldQty(Integer holdldQty) {
		this.holdldQty = holdldQty;
	}

	public String getTotalCard() {
		return totalCard;
	}

	public void setTotalCard(String totalCard) {
		this.totalCard = totalCard;
	}

	public Integer getTotalCardQty() {
		return totalCardQty;
	}

	public void setTotalCardQty(Integer totalCardQty) {
		this.totalCardQty = totalCardQty;
	}

	public String getTotalCash() {
		return totalCash;
	}

	public void setTotalCash(String totalCash) {
		this.totalCash = totalCash;
	}

	public Integer getTotalCashQty() {
		return totalCashQty;
	}

	public void setTotalCashQty(Integer totalCashQty) {
		this.totalCashQty = totalCashQty;
	}
	
	public String getNettSales() {
		return nettSales;
	}

	public void setNettSales(String nettSales) {
		this.nettSales = nettSales;
	}

	public Integer getTotalBills() {
		return totalBills;
	}

	public void setTotalBills(Integer totalBills) {
		this.totalBills = totalBills;
	}

	public Integer getOpenCount() {
		return openCount;
	}

	public void setOpenCount(Integer openCount) {
		this.openCount = openCount;
	}

	public Integer getFirstReceipt() {
		return firstReceipt;
	}

	public void setFirstReceipt(Integer firstReceipt) {
		this.firstReceipt = firstReceipt;
	}

	public Integer getLastReceipt() {
		return lastReceipt;
	}

	public void setLastReceipt(Integer lastReceipt) {
		this.lastReceipt = lastReceipt;
	}
	
	public String getTotalTax() {
		return totalTax;
	}


	public void setTotalTax(String totalTax) {
		this.totalTax = totalTax;
	}

	public Integer getOrderQty() {
		return orderQty;
	}


	public void setOrderQty(Integer orderQty) {
		this.orderQty = orderQty;
	}


	public Integer getPersonQty() {
		return personQty;
	}


	public void setPersonQty(Integer personQty) {
		this.personQty = personQty;
	}

	public String getTotalBalancePrice() {
		return totalBalancePrice;
	}


	public void setTotalBalancePrice(String totalBalancePrice) {
		this.totalBalancePrice = totalBalancePrice;
	}

	public String getCashInAmt() {
		return cashInAmt;
	}


	public void setCashInAmt(String cashInAmt) {
		this.cashInAmt = cashInAmt;
	}


	public String getCashOutAmt() {
		return cashOutAmt;
	}


	public void setCashOutAmt(String cashOutAmt) {
		this.cashOutAmt = cashOutAmt;
	}


	public String getVarianceAmt() {
		return varianceAmt;
	}


	public void setVarianceAmt(String varianceAmt) {
		this.varianceAmt = varianceAmt;
	}

	public String getBillVoid() {
		return billVoid;
	}


	public void setBillVoid(String billVoid) {
		this.billVoid = billVoid;
	}


	public Integer getBillVoidQty() {
		return billVoidQty;
	}


	public void setBillVoidQty(Integer billVoidQty) {
		this.billVoidQty = billVoidQty;
	}


	public String getItemVoid() {
		return itemVoid;
	}


	public void setItemVoid(String itemVoid) {
		this.itemVoid = itemVoid;
	}


	public Integer getItemVoidQty() {
		return itemVoidQty;
	}


	public void setItemVoidQty(Integer itemVoidQty) {
		this.itemVoidQty = itemVoidQty;
	}


	public String getInclusiveTaxAmt() {
		return inclusiveTaxAmt;
	}


	public void setInclusiveTaxAmt(String inclusiveTaxAmt) {
		this.inclusiveTaxAmt = inclusiveTaxAmt;
	}

	public String getAlipay() {
		return alipay;
	}


	public void setAlipay(String alipay) {
		this.alipay = alipay;
	}


	public Integer getAlipayQty() {
		return alipayQty;
	}


	public void setAlipayQty(Integer alipayQty) {
		this.alipayQty = alipayQty;
	}
	
	public String getWeixinpay() {
		return weixinpay;
	}

	public void setWeixinpay(String weixinpay) {
		this.weixinpay = weixinpay;
	}


	public Integer getWeixinpayQty() {
		return weixinpayQty;
	}


	public void setWeixinpayQty(Integer weixinpayQty) {
		this.weixinpayQty = weixinpayQty;
	}

	public String getThirdParty() {
		return thirdParty;
	}


	public void setThirdParty(String thirdParty) {
		this.thirdParty = thirdParty;
	}


	public Integer getThirdPartyQty() {
		return thirdPartyQty;
	}


	public void setThirdPartyQty(Integer thirdPartyQty) {
		this.thirdPartyQty = thirdPartyQty;
	}

	public String getPaypalpay() {
		return paypalpay;
	}

	public void setPaypalpay(String paypalpay) {
		if(paypalpay == null){
			this.paypalpay = "0.00";
		}else {
			this.paypalpay = paypalpay;
		}
	}

	public Integer getPaypalpayQty() {
		return paypalpayQty;
	}

	public void setPaypalpayQty(Integer paypalpayQty) {
		if(paypalpayQty == null){
			this.paypalpayQty = 0;
		}else {
			this.paypalpayQty = paypalpayQty;
		}
	}

	@Override
	public String toString() {
		return "ReportDaySales{" +
				"id=" + id +
				", restaurantId=" + restaurantId +
				", restaurantName='" + restaurantName + '\'' +
				", revenueId=" + revenueId +
				", revenueName='" + revenueName + '\'' +
				", businessDate=" + businessDate +
				", itemSales='" + itemSales + '\'' +
				", itemSalesQty=" + itemSalesQty +
				", discountPer='" + discountPer + '\'' +
				", discountPerQty=" + discountPerQty +
				", discount='" + discount + '\'' +
				", discountQty=" + discountQty +
				", discountAmt='" + discountAmt + '\'' +
				", focItem='" + focItem + '\'' +
				", focItemQty=" + focItemQty +
				", focBill='" + focBill + '\'' +
				", focBillQty=" + focBillQty +
				", totalSales='" + totalSales + '\'' +
				", cash='" + cash + '\'' +
				", cashQty=" + cashQty +
				", nets='" + nets + '\'' +
				", netsQty=" + netsQty +
				", visa='" + visa + '\'' +
				", visaQty=" + visaQty +
				", mc='" + mc + '\'' +
				", mcQty=" + mcQty +
				", amex='" + amex + '\'' +
				", amexQty=" + amexQty +
				", jbl='" + jbl + '\'' +
				", jblQty=" + jblQty +
				", unionPay='" + unionPay + '\'' +
				", unionPayQty=" + unionPayQty +
				", diner='" + diner + '\'' +
				", dinerQty=" + dinerQty +
				", holdld='" + holdld + '\'' +
				", holdldQty=" + holdldQty +
				", alipay='" + alipay + '\'' +
				", alipayQty=" + alipayQty +
				", weixinpay='" + weixinpay + '\'' +
				", weixinpayQty=" + weixinpayQty +
				", thirdParty='" + thirdParty + '\'' +
				", thirdPartyQty=" + thirdPartyQty +
				", totalCard='" + totalCard + '\'' +
				", totalCardQty=" + totalCardQty +
				", totalCash='" + totalCash + '\'' +
				", totalCashQty=" + totalCashQty +
				", billVoid='" + billVoid + '\'' +
				", billVoidQty=" + billVoidQty +
				", itemVoid='" + itemVoid + '\'' +
				", itemVoidQty=" + itemVoidQty +
				", nettSales='" + nettSales + '\'' +
				", totalBills=" + totalBills +
				", openCount=" + openCount +
				", firstReceipt=" + firstReceipt +
				", lastReceipt=" + lastReceipt +
				", totalTax='" + totalTax + '\'' +
				", orderQty=" + orderQty +
				", personQty=" + personQty +
				", totalBalancePrice='" + totalBalancePrice + '\'' +
				", cashInAmt='" + cashInAmt + '\'' +
				", cashOutAmt='" + cashOutAmt + '\'' +
				", varianceAmt='" + varianceAmt + '\'' +
				", inclusiveTaxAmt='" + inclusiveTaxAmt + '\'' +
				", paypalpay='" + paypalpay + '\'' +
				", paypalpayQty=" + paypalpayQty +
				'}';
	}

}
