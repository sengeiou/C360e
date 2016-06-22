package com.alfredbase.javabean.javabeanforhtml;

public class EditSettlementInfo {

	private int paymentId;
	private int orderId;
	private int billNo;
	private String totalAmount;
	private String placeName;
	private int tableId;
	private String paymentCreateTime;
	private String userName;
	
	private Integer type; // 是否拆单的类型  通过此类型 判断打CloseBillWindow的类型
	public EditSettlementInfo(int paymentId, int orderId, int billNo, String totalAmount, String placeName, int tableId, String paymentCreateTime, String userName, Integer type) {
		this.paymentId = paymentId;
		this.orderId = orderId;
		this.billNo = billNo;
		this.totalAmount = totalAmount;
		this.placeName = placeName;
		this.tableId = tableId;
		this.paymentCreateTime = paymentCreateTime;
		this.userName = userName;
		this.type = type;
	}

	public EditSettlementInfo() {

	}

	public int getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(int paymentId) {
		this.paymentId = paymentId;
	}

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public int getBillNo() {
		return billNo;
	}

	public void setBillNo(int billNo) {
		this.billNo = billNo;
	}

	public String getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getPlaceName() {
		return placeName;
	}

	public void setPlaceName(String placeName) {
		this.placeName = placeName;
	}

	public int getTableId() {
		return tableId;
	}

	public void setTableId(int tableId) {
		this.tableId = tableId;
	}

	public String getPaymentCreateTime() {
		return paymentCreateTime;
	}

	public void setPaymentCreateTime(String paymentCreateTime) {
		this.paymentCreateTime = paymentCreateTime;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "EditSettlementInfo [paymentId=" + paymentId + ", orderId="
				+ orderId + ", billNo=" + billNo + ", totalAmount="
				+ totalAmount + ", placeName=" + placeName + ", tableId="
				+ tableId + ", paymentCreateTime=" + paymentCreateTime
				+ ", userName=" + userName + ", type=" + type + "]";
	}

}