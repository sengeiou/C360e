package com.alfredbase.javabean;

import java.io.Serializable;
import java.text.SimpleDateFormat;

public class KotSummary implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3305618260951189578L;
	private Integer id;
	private Integer orderId; // linked to Order Table ID
	private Integer revenueCenterId;
	private Integer tableId;
	private String tableName;
	private String revenueCenterName;
	private int status;//KotSummary的状态为undone和done两种，分别为0和1
	private long createTime;
	private Long updateTime;
	private long businessDate;
	private Integer isTakeAway;
	
	private Integer orderNo; //流水号, 和订单的流水号一致。only for print
	
	
	//不存数据库 临时变量 
	private String description;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getOrderId() {
		return orderId;
	}

	public Integer getRevenueCenterId() {
		return revenueCenterId;
	}
	public void setRevenueCenterId(Integer revenueCenterId) {
		this.revenueCenterId = revenueCenterId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}
	
	public Integer getTableId() {
		return tableId;
	}
	public void setTableId(Integer tableId) {
		this.tableId = tableId;
	}
	
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getRevenueCenterName() {
		return revenueCenterName;
	}
	public void setRevenueCenterName(String revenueCenterName) {
		this.revenueCenterName = revenueCenterName;
	}
	public long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
	public Long getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Long updateTime) {
		this.updateTime = updateTime;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
	//Bob: not use it for print
	public String getOrderIdString() {
		return String.valueOf(this.orderId);
	}
	
	public String getKotIdString() {
		return String.valueOf(this.id);
	}
	
	public String getCreateTimeString() {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		String dateString = formatter.format(this.createTime);	
		return dateString;
	}
	public long getBusinessDate() {
		return businessDate;
	}
	public void setBusinessDate(long businessDate) {
		this.businessDate = businessDate;
	}
	public Integer getIsTakeAway() {
		return isTakeAway;
	}
	public void setIsTakeAway(Integer isTakeAway) {
		this.isTakeAway = isTakeAway;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "KotSummary [id=" + id + ", orderId=" + orderId
				+ ", revenueCenterId=" + revenueCenterId + ", tableId="
				+ tableId + ", tableName=" + tableName + ", revenueCenterName="
				+ revenueCenterName + ", status=" + status + ", createTime="
				+ createTime + ", updateTime=" + updateTime + ", businessDate="
				+ businessDate + ", isTakeAway=" + isTakeAway
				+ ", description=" + description + "]";
	}
	public Integer getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(Integer orderNo) {
		this.orderNo = orderNo;
	}
	
	//流水号
	public String getOrderNoString() {
		return String.valueOf(this.orderNo);
	}
	
}
