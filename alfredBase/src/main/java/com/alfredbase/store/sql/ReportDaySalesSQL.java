package com.alfredbase.store.sql;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.alfredbase.javabean.ReportDaySales;
import com.alfredbase.store.SQLExe;
import com.alfredbase.store.TableNames;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ReportDaySalesSQL {
	public static void addReportDaySales(ReportDaySales reportDaySales) {
		if (reportDaySales == null)
			return;
		try {
			String sql = "replace into "
					+ TableNames.ReportDaySales
					+ "(id, restaurantId, restaurantName, revenueId, revenueName, businessDate, itemSales, itemSalesQty, discountPer, discountPerQty,"
					+ " discount, discountQty, discountAmt, focItem, focItemQty, focBill, focBillQty, totalSales, cash, cashQty, nets, netsQty, visa,"
					+ " visaQty, mc, mcQty, amex, amexQty, jbl, jblQty, unionPay, unionPayQty, diner, dinerQty, holdld, holdldQty, totalCard, totalCardQty,"
					+ " totalCash, totalCashQty, billVoid, billVoidQty, itemVoid, itemVoidQty, nettSales, totalBills, openCount, firstReceipt, lastReceipt,"
					+ " totalTax, orderQty, personQty, totalBalancePrice, cashInAmt, cashOutAmt, varianceAmt, inclusiveTaxAmt, alipay, alipayQty, thirdParty,"
					+ " thirdPartyQty, weixinpay, weixinpayQty, paypalpay, paypalpayQty, storedCard, storedCardQty, topUps, topUpsQty)"
					+ " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			SQLExe.getDB().execSQL(
					sql,
					new Object[] { reportDaySales.getId(),
							reportDaySales.getRestaurantId(),
							reportDaySales.getRestaurantName(),
							reportDaySales.getRevenueId(),
							reportDaySales.getRevenueName(),
							reportDaySales.getBusinessDate(),
							reportDaySales.getItemSales() == null ? "0.00":reportDaySales.getItemSales(),
							reportDaySales.getItemSalesQty() == null ? 0 : reportDaySales.getItemSalesQty(),
							reportDaySales.getDiscountPer() == null ? "0.00" : reportDaySales.getDiscountPer(),
							reportDaySales.getDiscountPerQty() == null ? 0 :reportDaySales.getDiscountPerQty(),
							reportDaySales.getDiscount() == null ? 0 : reportDaySales.getDiscount(),
							reportDaySales.getDiscountQty() == null ? 0 : reportDaySales.getDiscountQty(),
							reportDaySales.getDiscountAmt() == null ? "0.00" : reportDaySales.getDiscountAmt(),
							reportDaySales.getFocItem() == null ? "0.00" : reportDaySales.getFocItem(),
							reportDaySales.getFocItemQty() == null ? 0 : reportDaySales.getFocItemQty(),
							reportDaySales.getFocBill() == null ? "0.00" : reportDaySales.getFocBill(),
							reportDaySales.getFocBillQty() == null ? 0 : reportDaySales.getFocBillQty(),
							reportDaySales.getTotalSales() == null ? "0.00" : reportDaySales.getTotalSales(),
							reportDaySales.getCash() == null ? "0.00" : reportDaySales.getCash(),
							reportDaySales.getCashQty() == null ? 0 : reportDaySales.getCashQty(),
							reportDaySales.getNets() == null ? "0.00" : reportDaySales.getNets(),
							reportDaySales.getNetsQty() == null ? 0 : reportDaySales.getNetsQty(),
							reportDaySales.getVisa() == null ? "0.00" : reportDaySales.getVisa(),
							reportDaySales.getVisaQty() == null ? 0 : reportDaySales.getVisaQty(),
							reportDaySales.getMc() == null ? "0.00" : reportDaySales.getMc(), 
							reportDaySales.getMcQty() == null ? 0 : reportDaySales.getMcQty(),
							reportDaySales.getAmex() == null ? "0.00" : reportDaySales.getAmex(),
							reportDaySales.getAmexQty() == null ? 0 : reportDaySales.getAmexQty(),
							reportDaySales.getJbl() == null ? "0.00" : reportDaySales.getJbl(),
							reportDaySales.getJblQty() == null ? 0 : reportDaySales.getJblQty(),
							reportDaySales.getUnionPay() == null ? "0.00" : reportDaySales.getUnionPay(),
							reportDaySales.getUnionPayQty() == null ? 0 : reportDaySales.getUnionPayQty(),
							reportDaySales.getDiner() == null ? "0.00" : reportDaySales.getDiner(),
							reportDaySales.getDinerQty() == null ? 0 : reportDaySales.getDinerQty(),
							reportDaySales.getHoldld() == null ? "0.00" : reportDaySales.getHoldld(),
							reportDaySales.getHoldldQty() == null ? 0 : reportDaySales.getHoldldQty(),
							reportDaySales.getTotalCard() == null ? "0.00" : reportDaySales.getTotalCard(),
							reportDaySales.getTotalCardQty() == null ? 0 : reportDaySales.getTotalCardQty(),
							reportDaySales.getTotalCash() == null ? "0.00" : reportDaySales.getTotalCash(),
							reportDaySales.getTotalCashQty() == null ? 0 : reportDaySales.getTotalCashQty(),
							reportDaySales.getBillVoid() == null ? "0.00" : reportDaySales.getBillVoid(),
							reportDaySales.getBillVoidQty() == null ? 0 : reportDaySales.getBillVoidQty(),
							reportDaySales.getItemVoid() == null ? "0.00" : reportDaySales.getItemVoid(),
							reportDaySales.getItemVoidQty() == null ? 0 : reportDaySales.getItemVoidQty(),
							reportDaySales.getNettSales() == null ? "0.00" : reportDaySales.getNettSales(),
							reportDaySales.getTotalBills() == null ? "0.00" : reportDaySales.getTotalBills(),
							reportDaySales.getOpenCount() == null ? 0 : reportDaySales.getOpenCount(),
							reportDaySales.getFirstReceipt(),
							reportDaySales.getLastReceipt(),
							reportDaySales.getTotalTax() == null ? "0.00" : reportDaySales.getTotalTax(),
							reportDaySales.getOrderQty() == null ? 0 : reportDaySales.getOrderQty(),
							reportDaySales.getPersonQty() == null ? 0 : reportDaySales.getPersonQty(),
							reportDaySales.getTotalBalancePrice() == null ? 0 : reportDaySales.getTotalBalancePrice(),
							reportDaySales.getCashInAmt() == null ? "0.00" : reportDaySales.getCashInAmt(),
							reportDaySales.getCashOutAmt() == null ? "0.00" : reportDaySales.getCashOutAmt(),
							reportDaySales.getVarianceAmt() == null ? "0.00" : reportDaySales.getVarianceAmt(),
							reportDaySales.getInclusiveTaxAmt() == null ? "0.00" : reportDaySales.getInclusiveTaxAmt(),
							reportDaySales.getAlipay() == null ? "0.00" : reportDaySales.getAlipay(),
							reportDaySales.getAlipayQty() == null ? 0 : reportDaySales.getAlipayQty(),
							reportDaySales.getThirdParty() == null ? "0.00" : reportDaySales.getThirdParty(),
							reportDaySales.getThirdPartyQty() == null ? 0 : reportDaySales.getThirdPartyQty(),
							reportDaySales.getWeixinpay() == null ? "0.00" : reportDaySales.getWeixinpay(),
							reportDaySales.getWeixinpayQty() == null ? 0 : reportDaySales.getWeixinpayQty(),
							reportDaySales.getPaypalpay() == null ? "0.00" : reportDaySales.getPaypalpay(),
							reportDaySales.getPaypalpayQty() == null ? 0 : reportDaySales.getPaypalpayQty(),
							reportDaySales.getStoredCard() == null ? "0.00" : reportDaySales.getStoredCard(),
							reportDaySales.getStoredCardQty() == null ? 0 : reportDaySales.getStoredCardQty(),
							reportDaySales.getTopUps() == null ? "0.00" : reportDaySales.getTopUps(),
							reportDaySales.getTopUpsQty() == null ? 0 : reportDaySales.getTopUpsQty()
							});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/*ONLY use to save report downloaded from cloud*/
	public static void addReportDaySalesFromCloud(ReportDaySales reportDaySales) {
		if (reportDaySales == null)
			return;
		try {
			String sql = "replace into "
					+ TableNames.ReportDaySales
					+ "(restaurantId, restaurantName, revenueId, revenueName, businessDate, itemSales, itemSalesQty, discountPer, discountPerQty,"
					+ " discount, discountQty, discountAmt, focItem, focItemQty, focBill, focBillQty, totalSales, cash, cashQty, nets, netsQty,"
					+ " visa, visaQty, mc, mcQty, amex, amexQty, jbl, jblQty, unionPay, unionPayQty, diner, dinerQty, holdld, holdldQty, totalCard,"
					+ " totalCardQty, totalCash, totalCashQty, billVoid, billVoidQty, itemVoid, itemVoidQty, nettSales, totalBills, openCount,"
					+ " firstReceipt, lastReceipt, totalTax, orderQty, personQty, totalBalancePrice, cashInAmt, cashOutAmt, varianceAmt, inclusiveTaxAmt,"
					+ " alipay, alipayQty, thirdParty, thirdPartyQty, weixinpay, weixinpayQty, paypalpay, paypalpayQty, storedCard, storedCardQty, topUps, topUpsQty)"
					+ " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			SQLExe.getDB().execSQL(
					sql,
					new Object[] {reportDaySales.getRestaurantId(),
							reportDaySales.getRestaurantName(),
							reportDaySales.getRevenueId(),
							reportDaySales.getRevenueName(),
							reportDaySales.getBusinessDate(),
							reportDaySales.getItemSales() == null ? "0.00":reportDaySales.getItemSales(),
							reportDaySales.getItemSalesQty() == null ? 0 : reportDaySales.getItemSalesQty(),
							reportDaySales.getDiscountPer() == null ? "0.00" : reportDaySales.getDiscountPer(),
							reportDaySales.getDiscountPerQty() == null ? 0 :reportDaySales.getDiscountPerQty(),
							reportDaySales.getDiscount() == null ? 0 : reportDaySales.getDiscount(),
							reportDaySales.getDiscountQty() == null ? 0 : reportDaySales.getDiscountQty(),
							reportDaySales.getDiscountAmt() == null ? "0.00" : reportDaySales.getDiscountAmt(),
							reportDaySales.getFocItem() == null ? "0.00" : reportDaySales.getFocItem(),
							reportDaySales.getFocItemQty() == null ? 0 : reportDaySales.getFocItemQty(),
							reportDaySales.getFocBill() == null ? "0.00" : reportDaySales.getFocBill(),
							reportDaySales.getFocBillQty() == null ? 0 : reportDaySales.getFocBillQty(),
							reportDaySales.getTotalSales() == null ? "0.00" : reportDaySales.getTotalSales(),
							reportDaySales.getCash() == null ? "0.00" : reportDaySales.getCash(),
							reportDaySales.getCashQty() == null ? 0 : reportDaySales.getCashQty(),
							reportDaySales.getNets() == null ? "0.00" : reportDaySales.getNets(),
							reportDaySales.getNetsQty() == null ? 0 : reportDaySales.getNetsQty(),
							reportDaySales.getVisa() == null ? "0.00" : reportDaySales.getVisa(),
							reportDaySales.getVisaQty() == null ? 0 : reportDaySales.getVisaQty(),
							reportDaySales.getMc() == null ? "0.00" : reportDaySales.getMc(), 
							reportDaySales.getMcQty() == null ? 0 : reportDaySales.getMcQty(),
							reportDaySales.getAmex() == null ? "0.00" : reportDaySales.getAmex(),
							reportDaySales.getAmexQty() == null ? 0 : reportDaySales.getAmexQty(),
							reportDaySales.getJbl() == null ? "0.00" : reportDaySales.getJbl(),
							reportDaySales.getJblQty() == null ? 0 : reportDaySales.getJblQty(),
							reportDaySales.getUnionPay() == null ? "0.00" : reportDaySales.getUnionPay(),
							reportDaySales.getUnionPayQty() == null ? 0 : reportDaySales.getUnionPayQty(),
							reportDaySales.getDiner() == null ? "0.00" : reportDaySales.getDiner(),
							reportDaySales.getDinerQty() == null ? 0 : reportDaySales.getDinerQty(),
							reportDaySales.getHoldld() == null ? "0.00" : reportDaySales.getHoldld(),
							reportDaySales.getHoldldQty() == null ? 0 : reportDaySales.getHoldldQty(),
							reportDaySales.getTotalCard() == null ? "0.00" : reportDaySales.getTotalCard(),
							reportDaySales.getTotalCardQty() == null ? 0 : reportDaySales.getTotalCardQty(),
							reportDaySales.getTotalCash() == null ? "0.00" : reportDaySales.getTotalCash(),
							reportDaySales.getTotalCashQty() == null ? 0 : reportDaySales.getTotalCashQty(),
							reportDaySales.getBillVoid() == null ? "0.00" : reportDaySales.getBillVoid(),
							reportDaySales.getBillVoidQty() == null ? 0 : reportDaySales.getBillVoidQty(),
							reportDaySales.getItemVoid() == null ? "0.00" : reportDaySales.getItemVoid(),
							reportDaySales.getItemVoidQty() == null ? 0 : reportDaySales.getItemVoidQty(),
							reportDaySales.getNettSales() == null ? "0.00" : reportDaySales.getNettSales(),
							reportDaySales.getTotalBills() == null ? "0.00" : reportDaySales.getTotalBills(),
							reportDaySales.getOpenCount() == null ? 0 : reportDaySales.getOpenCount(),
							reportDaySales.getFirstReceipt(),
							reportDaySales.getLastReceipt(),
							reportDaySales.getTotalTax() == null ? "0.00" : reportDaySales.getTotalTax(),
							reportDaySales.getOrderQty() == null ? 0 : reportDaySales.getOrderQty(),
							reportDaySales.getPersonQty() == null ? 0 : reportDaySales.getPersonQty(),
							reportDaySales.getTotalBalancePrice() == null ? 0 : reportDaySales.getTotalBalancePrice(),
							reportDaySales.getCashInAmt() == null ? "0.00" : reportDaySales.getCashInAmt(),
							reportDaySales.getCashOutAmt() == null ? "0.00" : reportDaySales.getCashOutAmt(),
							reportDaySales.getVarianceAmt() == null ? "0.00" : reportDaySales.getVarianceAmt(),
							reportDaySales.getInclusiveTaxAmt() == null ? "0.00" : reportDaySales.getInclusiveTaxAmt(),
							reportDaySales.getAlipay() == null ? "0.00" : reportDaySales.getAlipay(),
							reportDaySales.getAlipayQty() == null ? 0 : reportDaySales.getAlipayQty(),
							reportDaySales.getThirdParty() == null ? "0.00" : reportDaySales.getThirdParty(),
							reportDaySales.getThirdPartyQty() == null ? 0 : reportDaySales.getThirdPartyQty(),
							reportDaySales.getWeixinpay() == null ? "0.00" : reportDaySales.getWeixinpay(),
							reportDaySales.getWeixinpayQty() == null ? 0 : reportDaySales.getWeixinpayQty(),
							reportDaySales.getPaypalpay() == null ? "0.00" : reportDaySales.getPaypalpay(),
							reportDaySales.getPaypalpayQty() == null ? 0 : reportDaySales.getPaypalpayQty(),
							reportDaySales.getStoredCard() == null ? "0.00" : reportDaySales.getStoredCard(),
							reportDaySales.getStoredCardQty() == null ? 0 : reportDaySales.getStoredCardQty(),
							reportDaySales.getTopUps() == null ? "0.00" : reportDaySales.getTopUps(),
							reportDaySales.getTopUpsQty() == null ? 0 : reportDaySales.getTopUpsQty()
							});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static ReportDaySales getReportDaySales(Integer reportDaySalesID) {
		ReportDaySales reportDaySales = null;
		String sql = "select * from " + TableNames.ReportDaySales
				+ " where id = ?";
		Cursor cursor = null;
		try {
			cursor = SQLExe.getDB().rawQuery(sql,
					new String[] { reportDaySalesID + "" });
			if (cursor.moveToFirst()) {
				reportDaySales = new ReportDaySales();
				reportDaySales.setId(cursor.getInt(0));
				reportDaySales.setRestaurantId(cursor.getInt(1));
				reportDaySales.setRestaurantName(cursor.getString(2));
				reportDaySales.setRevenueId(cursor.getInt(3));
				reportDaySales.setRevenueName(cursor.getString(4));
				reportDaySales.setBusinessDate(cursor.getLong(5));
				reportDaySales.setItemSales(cursor.getString(6));
				reportDaySales.setItemSalesQty(cursor.getInt(7));
				reportDaySales.setDiscountPer(cursor.getString(8));
				reportDaySales.setDiscountPerQty(cursor.getInt(9));
				reportDaySales.setDiscount(cursor.getString(10));
				reportDaySales.setDiscountQty(cursor.getInt(11));
				reportDaySales.setDiscountAmt(cursor.getString(12));
				reportDaySales.setFocItem(cursor.getString(13));
				reportDaySales.setFocItemQty(cursor.getInt(14));
				reportDaySales.setFocBill(cursor.getString(15));
				reportDaySales.setFocBillQty(cursor.getInt(16));
				reportDaySales.setTotalSales(cursor.getString(17));
				reportDaySales.setCash(cursor.getString(18));
				reportDaySales.setCashQty(cursor.getInt(19));
				reportDaySales.setNets(cursor.getString(20));
				reportDaySales.setNetsQty(cursor.getInt(21));
				reportDaySales.setVisa(cursor.getString(22));
				reportDaySales.setVisaQty(cursor.getInt(23));
				reportDaySales.setMc(cursor.getString(24));
				reportDaySales.setMcQty(cursor.getInt(25));
				reportDaySales.setAmex(cursor.getString(26));
				reportDaySales.setAmexQty(cursor.getInt(27));
				reportDaySales.setJbl(cursor.getString(28));
				reportDaySales.setJblQty(cursor.getInt(29));
				reportDaySales.setUnionPay(cursor.getString(30));
				reportDaySales.setUnionPayQty(cursor.getInt(31));
				reportDaySales.setDiner(cursor.getString(32));
				reportDaySales.setDinerQty(cursor.getInt(33));
				reportDaySales.setHoldld(cursor.getString(34));
				reportDaySales.setHoldldQty(cursor.getInt(35));
				reportDaySales.setTotalCard(cursor.getString(36));
				reportDaySales.setTotalCardQty(cursor.getInt(37));
				reportDaySales.setTotalCash(cursor.getString(38));
				reportDaySales.setTotalCashQty(cursor.getInt(39));
				reportDaySales.setBillVoid(cursor.getString(40));
				reportDaySales.setBillVoidQty(cursor.getInt(41));
				reportDaySales.setItemVoid(cursor.getString(42));
				reportDaySales.setItemVoidQty(cursor.getInt(43));
				reportDaySales.setNettSales(cursor.getString(44));
				reportDaySales.setTotalBills(cursor.getInt(45));
				reportDaySales.setOpenCount(cursor.getInt(46));
				reportDaySales.setFirstReceipt(cursor.getInt(47));
				reportDaySales.setLastReceipt(cursor.getInt(48));
				reportDaySales.setTotalTax(cursor.getString(49));
				reportDaySales.setOrderQty(cursor.getInt(50));
				reportDaySales.setPersonQty(cursor.getInt(51));
				reportDaySales.setTotalBalancePrice(cursor.getString(52));
				reportDaySales.setCashInAmt(cursor.getString(53));
				reportDaySales.setCashOutAmt(cursor.getString(54));
				reportDaySales.setVarianceAmt(cursor.getString(55));
				reportDaySales.setInclusiveTaxAmt(cursor.getString(56));
				reportDaySales.setAlipay(cursor.getString(57));
				reportDaySales.setAlipayQty(cursor.getInt(58));
				reportDaySales.setThirdParty(cursor.getString(59));
				reportDaySales.setThirdPartyQty(cursor.getInt(60));
				reportDaySales.setWeixinpay(cursor.getString(61));
				reportDaySales.setWeixinpayQty(cursor.getInt(62));
				reportDaySales.setPaypalpay(cursor.getString(63));
				reportDaySales.setPaypalpayQty(cursor.getInt(64));
				reportDaySales.setStoredCard(cursor.getString(65));
				reportDaySales.setStoredCardQty(cursor.getInt(66));
				reportDaySales.setTopUps(cursor.getString(67));
				reportDaySales.setTopUpsQty(cursor.getInt(68));
				return reportDaySales;
			}
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return reportDaySales;
	}
	
	public static ReportDaySales getReportDaySalesByTime(long day) {
		ReportDaySales reportDaySales = null;
		String sql = "select * from " + TableNames.ReportDaySales
				+ " where businessDate = ?";
		Cursor cursor = null;
		try {
			cursor = SQLExe.getDB().rawQuery(sql,
					new String[] { String.valueOf(day) });
			if (cursor.moveToFirst()) {
				reportDaySales = new ReportDaySales();
				reportDaySales.setId(cursor.getInt(0));
				reportDaySales.setRestaurantId(cursor.getInt(1));
				reportDaySales.setRestaurantName(cursor.getString(2));
				reportDaySales.setRevenueId(cursor.getInt(3));
				reportDaySales.setRevenueName(cursor.getString(4));
				reportDaySales.setBusinessDate(cursor.getLong(5));
				reportDaySales.setItemSales(cursor.getString(6));
				reportDaySales.setItemSalesQty(cursor.getInt(7));
				reportDaySales.setDiscountPer(cursor.getString(8));
				reportDaySales.setDiscountPerQty(cursor.getInt(9));
				reportDaySales.setDiscount(cursor.getString(10));
				reportDaySales.setDiscountQty(cursor.getInt(11));
				reportDaySales.setDiscountAmt(cursor.getString(12));
				reportDaySales.setFocItem(cursor.getString(13));
				reportDaySales.setFocItemQty(cursor.getInt(14));
				reportDaySales.setFocBill(cursor.getString(15));
				reportDaySales.setFocBillQty(cursor.getInt(16));
				reportDaySales.setTotalSales(cursor.getString(17));
				reportDaySales.setCash(cursor.getString(18));
				reportDaySales.setCashQty(cursor.getInt(19));
				reportDaySales.setNets(cursor.getString(20));
				reportDaySales.setNetsQty(cursor.getInt(21));
				reportDaySales.setVisa(cursor.getString(22));
				reportDaySales.setVisaQty(cursor.getInt(23));
				reportDaySales.setMc(cursor.getString(24));
				reportDaySales.setMcQty(cursor.getInt(25));
				reportDaySales.setAmex(cursor.getString(26));
				reportDaySales.setAmexQty(cursor.getInt(27));
				reportDaySales.setJbl(cursor.getString(28));
				reportDaySales.setJblQty(cursor.getInt(29));
				reportDaySales.setUnionPay(cursor.getString(30));
				reportDaySales.setUnionPayQty(cursor.getInt(31));
				reportDaySales.setDiner(cursor.getString(32));
				reportDaySales.setDinerQty(cursor.getInt(33));
				reportDaySales.setHoldld(cursor.getString(34));
				reportDaySales.setHoldldQty(cursor.getInt(35));
				reportDaySales.setTotalCard(cursor.getString(36));
				reportDaySales.setTotalCardQty(cursor.getInt(37));
				reportDaySales.setTotalCash(cursor.getString(38));
				reportDaySales.setTotalCashQty(cursor.getInt(39));
				reportDaySales.setBillVoid(cursor.getString(40));
				reportDaySales.setBillVoidQty(cursor.getInt(41));
				reportDaySales.setItemVoid(cursor.getString(42));
				reportDaySales.setItemVoidQty(cursor.getInt(43));
				reportDaySales.setNettSales(cursor.getString(44));
				reportDaySales.setTotalBills(cursor.getInt(45));
				reportDaySales.setOpenCount(cursor.getInt(46));
				reportDaySales.setFirstReceipt(cursor.getInt(47));
				reportDaySales.setLastReceipt(cursor.getInt(48));
				reportDaySales.setTotalTax(cursor.getString(49));
				reportDaySales.setOrderQty(cursor.getInt(50));
				reportDaySales.setPersonQty(cursor.getInt(51));
				reportDaySales.setTotalBalancePrice(cursor.getString(52));
				reportDaySales.setCashInAmt(cursor.getString(53));
				reportDaySales.setCashOutAmt(cursor.getString(54));
				reportDaySales.setVarianceAmt(cursor.getString(55));
				reportDaySales.setInclusiveTaxAmt(cursor.getString(56));
				reportDaySales.setAlipay(cursor.getString(57));
				reportDaySales.setAlipayQty(cursor.getInt(58));
				reportDaySales.setThirdParty(cursor.getString(59));
				reportDaySales.setThirdPartyQty(cursor.getInt(60));
				reportDaySales.setWeixinpay(cursor.getString(61));
				reportDaySales.setWeixinpayQty(cursor.getInt(62));
				reportDaySales.setPaypalpay(cursor.getString(63));
				reportDaySales.setPaypalpayQty(cursor.getInt(64));
				reportDaySales.setStoredCard(cursor.getString(65));
				reportDaySales.setStoredCardQty(cursor.getInt(66));
				reportDaySales.setTopUps(cursor.getString(67));
				reportDaySales.setTopUpsQty(cursor.getInt(68));
				return reportDaySales;
			}
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return reportDaySales;
	}
	//for X/Z Report Feature only
	/*
	 *  bizDate: {'x':nettsales, "z": nettsales}
	 * 
	 * */
	public static Map<Long, Object>  getReportDaySalesBetweenTime(long currentTime, long oldTime) {
		ArrayList<ReportDaySales> result = new ArrayList<ReportDaySales>();
		String sql = "select nettSales, businessDate from " + TableNames.ReportDaySales
				+ " where businessDate <= ? AND businessDate >=?";
		Cursor cursor = null;
		Map<Long, Object> zReportSummary = new HashMap<Long, Object>();
		try {
			cursor = SQLExe.getDB().rawQuery(sql,
					new String[] { String.valueOf(currentTime),String.valueOf(oldTime) });
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
					.moveToNext()) {
				Map<String, String> summyObj = new HashMap<String, String>();
				summyObj.put("z", cursor.getString(0)); // NETT Sales colomn
				summyObj.put("x", null);
				zReportSummary.put(cursor.getLong(1), summyObj); //Business Date
			}
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return zReportSummary;
	}

	public static ArrayList<ReportDaySales> getAllReportDaySales() {
		ArrayList<ReportDaySales> result = new ArrayList<ReportDaySales>();
		String sql = "select * from " + TableNames.ReportDaySales;
		Cursor cursor = null;
		SQLiteDatabase db =SQLExe.getDB();
		try {
			db.beginTransaction();
			cursor = db.rawQuery(sql, new String[] {});
			int count = cursor.getCount();
			if (count < 1) {
				return result;
			}
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
					.moveToNext()) {
				ReportDaySales reportDaySales = new ReportDaySales();
				reportDaySales.setId(cursor.getInt(0));
				reportDaySales.setRestaurantId(cursor.getInt(1));
				reportDaySales.setRestaurantName(cursor.getString(2));
				reportDaySales.setRevenueId(cursor.getInt(3));
				reportDaySales.setRevenueName(cursor.getString(4));
				reportDaySales.setBusinessDate(cursor.getLong(5));
				reportDaySales.setItemSales(cursor.getString(6));
				reportDaySales.setItemSalesQty(cursor.getInt(7));
				reportDaySales.setDiscountPer(cursor.getString(8));
				reportDaySales.setDiscountPerQty(cursor.getInt(9));
				reportDaySales.setDiscount(cursor.getString(10));
				reportDaySales.setDiscountQty(cursor.getInt(11));
				reportDaySales.setDiscountAmt(cursor.getString(12));
				reportDaySales.setFocItem(cursor.getString(13));
				reportDaySales.setFocItemQty(cursor.getInt(14));
				reportDaySales.setFocBill(cursor.getString(15));
				reportDaySales.setFocBillQty(cursor.getInt(16));
				reportDaySales.setTotalSales(cursor.getString(17));
				reportDaySales.setCash(cursor.getString(18));
				reportDaySales.setCashQty(cursor.getInt(19));
				reportDaySales.setNets(cursor.getString(20));
				reportDaySales.setNetsQty(cursor.getInt(21));
				reportDaySales.setVisa(cursor.getString(22));
				reportDaySales.setVisaQty(cursor.getInt(23));
				reportDaySales.setMc(cursor.getString(24));
				reportDaySales.setMcQty(cursor.getInt(25));
				reportDaySales.setAmex(cursor.getString(26));
				reportDaySales.setAmexQty(cursor.getInt(27));
				reportDaySales.setJbl(cursor.getString(28));
				reportDaySales.setJblQty(cursor.getInt(29));
				reportDaySales.setUnionPay(cursor.getString(30));
				reportDaySales.setUnionPayQty(cursor.getInt(31));
				reportDaySales.setDiner(cursor.getString(32));
				reportDaySales.setDinerQty(cursor.getInt(33));
				reportDaySales.setHoldld(cursor.getString(34));
				reportDaySales.setHoldldQty(cursor.getInt(35));
				reportDaySales.setTotalCard(cursor.getString(36));
				reportDaySales.setTotalCardQty(cursor.getInt(37));
				reportDaySales.setTotalCash(cursor.getString(38));
				reportDaySales.setTotalCashQty(cursor.getInt(39));
				reportDaySales.setBillVoid(cursor.getString(40));
				reportDaySales.setBillVoidQty(cursor.getInt(41));
				reportDaySales.setItemVoid(cursor.getString(42));
				reportDaySales.setItemVoidQty(cursor.getInt(43));
				reportDaySales.setNettSales(cursor.getString(44));
				reportDaySales.setTotalBills(cursor.getInt(45));
				reportDaySales.setOpenCount(cursor.getInt(46));
				reportDaySales.setFirstReceipt(cursor.getInt(47));
				reportDaySales.setLastReceipt(cursor.getInt(48));
				reportDaySales.setTotalTax(cursor.getString(49));
				reportDaySales.setOrderQty(cursor.getInt(50));
				reportDaySales.setPersonQty(cursor.getInt(51));
				reportDaySales.setTotalBalancePrice(cursor.getString(52));
				reportDaySales.setCashInAmt(cursor.getString(53));
				reportDaySales.setCashOutAmt(cursor.getString(54));
				reportDaySales.setVarianceAmt(cursor.getString(55));
				reportDaySales.setInclusiveTaxAmt(cursor.getString(56));
				reportDaySales.setAlipay(cursor.getString(57));
				reportDaySales.setAlipayQty(cursor.getInt(58));
				reportDaySales.setThirdParty(cursor.getString(59));
				reportDaySales.setThirdPartyQty(cursor.getInt(60));
				reportDaySales.setWeixinpay(cursor.getString(61));
				reportDaySales.setWeixinpayQty(cursor.getInt(62));
				reportDaySales.setPaypalpay(cursor.getString(63));
				reportDaySales.setPaypalpayQty(cursor.getInt(64));
				reportDaySales.setStoredCard(cursor.getString(65));
				reportDaySales.setStoredCardQty(cursor.getInt(66));
				reportDaySales.setTopUps(cursor.getString(67));
				reportDaySales.setTopUpsQty(cursor.getInt(68));
				result.add(reportDaySales);
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

	public static ArrayList<ReportDaySales> getReportDaySalessByNowTime() {
		ArrayList<ReportDaySales> result = new ArrayList<ReportDaySales>();
		String sql = "select * from "
				+ TableNames.ReportDaySales
				+ " where createTime < datetime('now','localtime') and paidDate > date('now','start of day')";
		Cursor cursor = null;
		SQLiteDatabase db = SQLExe.getDB();
		try {
			db.beginTransaction();
			cursor = db.rawQuery(sql, new String[] {});
			int count = cursor.getCount();
			if (count < 1) {
				return result;
			}
			ReportDaySales reportDaySales = null;
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
					.moveToNext()) {
				reportDaySales = new ReportDaySales();
				reportDaySales.setId(cursor.getInt(0));
				reportDaySales.setRestaurantId(cursor.getInt(1));
				reportDaySales.setRestaurantName(cursor.getString(2));
				reportDaySales.setRevenueId(cursor.getInt(3));
				reportDaySales.setRevenueName(cursor.getString(4));
				reportDaySales.setBusinessDate(cursor.getLong(5));
				reportDaySales.setItemSales(cursor.getString(6));
				reportDaySales.setItemSalesQty(cursor.getInt(7));
				reportDaySales.setDiscountPer(cursor.getString(8));
				reportDaySales.setDiscountPerQty(cursor.getInt(9));
				reportDaySales.setDiscount(cursor.getString(10));
				reportDaySales.setDiscountQty(cursor.getInt(11));
				reportDaySales.setDiscountAmt(cursor.getString(12));
				reportDaySales.setFocItem(cursor.getString(13));
				reportDaySales.setFocItemQty(cursor.getInt(14));
				reportDaySales.setFocBill(cursor.getString(15));
				reportDaySales.setFocBillQty(cursor.getInt(16));
				reportDaySales.setTotalSales(cursor.getString(17));
				reportDaySales.setCash(cursor.getString(18));
				reportDaySales.setCashQty(cursor.getInt(19));
				reportDaySales.setNets(cursor.getString(20));
				reportDaySales.setNetsQty(cursor.getInt(21));
				reportDaySales.setVisa(cursor.getString(22));
				reportDaySales.setVisaQty(cursor.getInt(23));
				reportDaySales.setMc(cursor.getString(24));
				reportDaySales.setMcQty(cursor.getInt(25));
				reportDaySales.setAmex(cursor.getString(26));
				reportDaySales.setAmexQty(cursor.getInt(27));
				reportDaySales.setJbl(cursor.getString(28));
				reportDaySales.setJblQty(cursor.getInt(29));
				reportDaySales.setUnionPay(cursor.getString(30));
				reportDaySales.setUnionPayQty(cursor.getInt(31));
				reportDaySales.setDiner(cursor.getString(32));
				reportDaySales.setDinerQty(cursor.getInt(33));
				reportDaySales.setHoldld(cursor.getString(34));
				reportDaySales.setHoldldQty(cursor.getInt(35));
				reportDaySales.setTotalCard(cursor.getString(36));
				reportDaySales.setTotalCardQty(cursor.getInt(37));
				reportDaySales.setTotalCash(cursor.getString(38));
				reportDaySales.setTotalCashQty(cursor.getInt(39));
				reportDaySales.setBillVoid(cursor.getString(40));
				reportDaySales.setBillVoidQty(cursor.getInt(41));
				reportDaySales.setItemVoid(cursor.getString(42));
				reportDaySales.setItemVoidQty(cursor.getInt(43));
				reportDaySales.setNettSales(cursor.getString(44));
				reportDaySales.setTotalBills(cursor.getInt(45));
				reportDaySales.setOpenCount(cursor.getInt(46));
				reportDaySales.setFirstReceipt(cursor.getInt(47));
				reportDaySales.setLastReceipt(cursor.getInt(48));
				reportDaySales.setTotalTax(cursor.getString(49));
				reportDaySales.setOrderQty(cursor.getInt(50));
				reportDaySales.setPersonQty(cursor.getInt(51));
				reportDaySales.setTotalBalancePrice(cursor.getString(52));
				reportDaySales.setCashInAmt(cursor.getString(53));
				reportDaySales.setCashOutAmt(cursor.getString(54));
				reportDaySales.setVarianceAmt(cursor.getString(55));
				reportDaySales.setInclusiveTaxAmt(cursor.getString(56));
				reportDaySales.setAlipay(cursor.getString(57));
				reportDaySales.setAlipayQty(cursor.getInt(58));
				reportDaySales.setThirdParty(cursor.getString(59));
				reportDaySales.setThirdPartyQty(cursor.getInt(60));
				reportDaySales.setWeixinpay(cursor.getString(61));
				reportDaySales.setWeixinpayQty(cursor.getInt(62));
				reportDaySales.setPaypalpay(cursor.getString(63));
				reportDaySales.setPaypalpayQty(cursor.getInt(64));
				reportDaySales.setStoredCard(cursor.getString(65));
				reportDaySales.setStoredCardQty(cursor.getInt(66));
				reportDaySales.setTopUps(cursor.getString(67));
				reportDaySales.setTopUpsQty(cursor.getInt(68));
				result.add(reportDaySales);
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

	public static void deleteReportDaySales(ReportDaySales reportDaySales) {
		String sql = "delete from " + TableNames.ReportDaySales
				+ " where id = ?";
		try {
			SQLExe.getDB()
					.execSQL(sql, new Object[] { reportDaySales.getId() });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
