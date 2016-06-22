package com.alfredbase.javabean;

import java.io.Serializable;

public class Printer implements Serializable{

	/**
	 * Printer and PrintGroup name use same DB
	 */
	private static final long serialVersionUID = -4652260147089403168L;

	private Integer id;

    private String printerGroupName; //printer name

    private String printerName; //group name

    private String printerLocation;

    private String printerType; //no used

    private String qPrint;

    private Integer isCashdrawer;

    private Integer companyId;

    private Integer restaurantId;
    /* 0为打印机组 1为打印设备*/
    private Integer type;

    private Long createTime;

    private Long updateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPrinterGroupName() {
        return printerGroupName;
    }

    public void setPrinterGroupName(String printerGroupName) {
        this.printerGroupName = printerGroupName == null ? null : printerGroupName.trim();
    }

    public String getPrinterName() {
        return printerName;
    }

    public void setPrinterName(String printerName) {
        this.printerName = printerName == null ? null : printerName.trim();
    }

    public String getPrinterLocation() {
        return printerLocation;
    }

    public void setPrinterLocation(String printerLocation) {
        this.printerLocation = printerLocation == null ? null : printerLocation.trim();
    }

    public String getPrinterType() {
        return printerType;
    }

    public void setPrinterType(String printerType) {
        this.printerType = printerType == null ? null : printerType.trim();
    }

    public String getqPrint() {
        return qPrint;
    }

    public void setqPrint(String qPrint) {
        this.qPrint = qPrint == null ? null : qPrint.trim();
    }

    public Integer getIsCashdrawer() {
    	if (isCashdrawer==null) {
    		this.isCashdrawer = 0;
    	}
        return isCashdrawer;
    }

    public void setIsCashdrawer(Integer isCashdrawer) {
    	if(isCashdrawer == null)
    		this.isCashdrawer = 0;
    	else
           this.isCashdrawer = isCashdrawer;
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public Integer getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Integer restaurantId) {
        this.restaurantId = restaurantId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }
    
    @Override
	public String toString() {
		return "Printer [id=" + id + ", printerGroupName=" + printerGroupName
				+ ", printerName=" + printerName + ", printerLocation="
				+ printerLocation + ", printerType=" + printerType
				+ ", qPrint=" + qPrint + ", isCashdrawer=" + isCashdrawer
				+ ", companyId=" + companyId + ", restaurantId=" + restaurantId
				+ ", type=" + type + ", createTime=" + createTime
				+ ", updateTime=" + updateTime + "]";
	}
}