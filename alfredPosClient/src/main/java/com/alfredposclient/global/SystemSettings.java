package com.alfredposclient.global;

import android.content.Context;

import com.alfredbase.store.Store;

public class SystemSettings {

	private Context context;

	private boolean kotPrintTogether = true;
	private boolean doubleKotPrint = false; //2 KOT copies
	private boolean doubleBillPrint = false; //double bill print
	private boolean doubleReceiptPrint = false; //double closing receipt
	private boolean orderSummaryPrint = false; //double closing receipt
	private boolean printWhenCloseSession = true; // session close is report?
	private int maxPrintOrderNo = 98;
    
	public SystemSettings(Context context) {
		super();
		this.context = context;
	}

	public boolean isKotPrintTogether() {
		Integer value = Store.getInt(context,
				Store.PRINT_SETTING_KOT_MODE_TOGETHER);
		if (value != null && value != Store.DEFAULT_INT_TYPE) {
			if (value.intValue() == 1)
				this.kotPrintTogether = true;
			else
				this.kotPrintTogether = false;

			return kotPrintTogether;
		}
		return kotPrintTogether;
	}

	public void setKotPrintTogether(Integer kotPrintTogether) {
		Store.putInt(this.context, Store.PRINT_SETTING_KOT_MODE_TOGETHER,
				kotPrintTogether);
		if (kotPrintTogether.intValue() == 1)
			this.kotPrintTogether = true;
		else
			this.kotPrintTogether = false;
	}
	
	public boolean isKotDoublePrint() {
		Integer value = Store.getInt(context,
				Store.PRINT_SETTING_KOT_MODE_DOUBLE);
		if (value != null && value != Store.DEFAULT_INT_TYPE) {
			if (value.intValue() == 1)
				this.doubleKotPrint = true;
			else
				this.doubleKotPrint = false;

			return doubleKotPrint;
		}
		return doubleKotPrint;
	}
	
	public void setKotDoublePrint(Integer kotCopy) {
		Store.putInt(this.context, Store.PRINT_SETTING_KOT_MODE_DOUBLE,
				kotCopy);
		if (kotCopy.intValue() == 1)
			this.doubleKotPrint = true;
		else
			this.doubleKotPrint = false;
	}
	
	public boolean isDoubleReceiptPrint() {
		Integer value = Store.getInt(context,
				Store.PRINT_SETTING_RECEIPT_MODE_DOUBLE);
		if (value != null && value != Store.DEFAULT_INT_TYPE) {
			if (value.intValue() == 1)
				this.doubleReceiptPrint = true;
			else
				this.doubleReceiptPrint = false;

			return doubleReceiptPrint;
		}
		return doubleReceiptPrint;
	}

	public void setDoubleReceiptPrint(Integer doubleReceiptPrint) {

		Store.putInt(this.context, Store.PRINT_SETTING_RECEIPT_MODE_DOUBLE,
				doubleReceiptPrint);
		if (doubleReceiptPrint.intValue() == 1)
			this.doubleReceiptPrint = true;
		else
			this.doubleReceiptPrint = false;

	}
	public boolean isOrderSummaryPrint() {
		Integer value = Store.getInt(context,
				Store.PRINT_SETTING_ORDER_SUMMARY);
		if (value != null && value != Store.DEFAULT_INT_TYPE) {
			if (value.intValue() == 1)
				this.orderSummaryPrint = true;
			else
				this.orderSummaryPrint = false;
			
			return orderSummaryPrint;
		}
		return orderSummaryPrint;
	}
	
	public void setOrderSummaryPrint(Integer orderSummaryPrint) {
		
		Store.putInt(this.context, Store.PRINT_SETTING_ORDER_SUMMARY,
				orderSummaryPrint);
		if (orderSummaryPrint.intValue() == 1)
			this.orderSummaryPrint = true;
		else
			this.orderSummaryPrint = false;
		
	}
	
	public boolean isDoubleBillPrint() {
		Integer value = Store.getInt(context,
				Store.PRINT_SETTING_BILL_MODE_DOUBLE);
		if (value != null && value != Store.DEFAULT_INT_TYPE) {
			if (value.intValue() == 1)
				this.doubleBillPrint = true;
			else
				this.doubleBillPrint = false;

			return doubleBillPrint;
		}
		return doubleBillPrint;
	}

	public void setDoubleBillPrint(Integer doubleBillPrint) {

		Store.putInt(this.context, Store.PRINT_SETTING_BILL_MODE_DOUBLE,
				doubleBillPrint);
		if (doubleBillPrint.intValue() == 1)
			this.doubleBillPrint = true;
		else
			this.doubleBillPrint = false;

	}

	public void setPrintWhenCloseSession(Integer printWhenCloseSession) {
		Store.putInt(this.context, Store.PRINT_REPORT_WHEN_CLOSE_SESSION,
				printWhenCloseSession);
		if (printWhenCloseSession.intValue() == 1)
			this.printWhenCloseSession = true;
		else
			this.printWhenCloseSession = false;
	}

	public boolean isPrintWhenCloseSession() {

		Integer value = Store.getInt(context,
				Store.PRINT_REPORT_WHEN_CLOSE_SESSION);
		if (value != null && value != Store.DEFAULT_INT_TYPE) {
			if (value.intValue() == 1)
				this.printWhenCloseSession = true;
			else
				this.printWhenCloseSession = false;

			return printWhenCloseSession;
		}
		return printWhenCloseSession;
	}

	public int getMaxPrintOrderNo() {
		return maxPrintOrderNo;
	}

	public void setMaxPrintOrderNo(int maxPrintOrderNo) {
		this.maxPrintOrderNo = maxPrintOrderNo;
	}
	
}
