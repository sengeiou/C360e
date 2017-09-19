package com.alfred.printer;

import android.text.TextUtils;

import com.alfred.print.jobs.PrintJob;
import com.alfred.print.jobs.Priority;
import com.alfred.remote.printservice.PrintService;
import com.alfred.remote.printservice.R;
import com.alfredbase.javabean.KotSummary;
import com.alfredbase.utils.IntegerUtils;
import com.birbit.android.jobqueue.Params;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class KOTPrint extends PrintJob{

	public static int FIXED_COL2_SPACE = 2;
	public static int FIXED_COL2_QTY = 10; //in case of 48 dots width, QTY col = 10dots
	public static int COL2_ITEMNAME = 36; // Width = CharSize/scale - FIXED_COL2_SPACE - FIXED_COL2_QTY/scale
	
	private int maxItemNameOnKot = 60;
	
    public KOTPrint(String uuid,Long bizDate) {
		super(new Params(Priority.MID).requireNetwork().persist().groupBy("kot"),
				"kot",  uuid, bizDate);
		if (this.charSize == 33) {
			KOTPrint.FIXED_COL2_QTY = 4;
			KOTPrint.FIXED_COL2_SPACE = 1;
		}
    }
    
	public void AddTitle(String revenueCenter, String tableName) {
		addFeed();
		StringBuilder sbr = new StringBuilder();
		sbr.append(revenueCenter)
				.append(reNext)
				.append(PrintService.instance.getResources().getString(R.string.table_)+ tableName)
				.append(reNext);

		PrintData header = new PrintData();
		header.setDataFormat(PrintData.FORMAT_TXT);
		header.setFontsize(2);
		header.setTextAlign(PrintData.ALIGN_CENTRE);
		header.setText(sbr.toString());
		this.data.add(header);	
		
		addHortionalLine(this.charSize);
	}

	public void AddHeader(int isTakeAway, String orderId) {
		StringBuilder sbr = new StringBuilder();
		if (isTakeAway==1) {
			sbr.append(PrintService.instance.getResources().getString(R.string.takeaway_print))
					.append("         "+PrintService.instance.getResources().getString(R.string.order_no_))
					.append("\t")
					.append(orderId)
					.append(reNext);
		}else{
			sbr.append(PrintService.instance.getResources().getString(R.string.order_no_))
					.append("\t")
					.append(orderId)
					.append(reNext);
		}

		PrintData header = new PrintData();
		header.setDataFormat(PrintData.FORMAT_TXT);
		header.setTextAlign(PrintData.ALIGN_RIGHT);
		header.setFontsize(2);
		header.setText(sbr.toString());
		this.data.add(header);

		addHortionalLine(this.charSize);
	}


	public  void AddFire(){
		StringBuilder sbr = new StringBuilder();
		sbr.append("FIRE");
		sbr.append(reNext);
		PrintData fire = new PrintData();
		fire.setDataFormat(PrintData.FORMAT_TXT);
		fire.setTextAlign(PrintData.ALIGN_CENTRE);
		fire.setFontsize(2);
		fire.setText(sbr.toString());
		this.data.add(fire);
		addHortionalLine(this.charSize);
	}

	public void AddKioskHeader(KotSummary kotSummary, String orderId) {
		addFeed();
		StringBuilder sbr = new StringBuilder();
		int revenueIndex = kotSummary.getRevenueCenterIndex();
		int isTakeAway = kotSummary.getIsTakeAway();
		String orderNo = PrintService.instance.getResources().getString(R.string.order_no_)
				+IntegerUtils.fromat(revenueIndex, orderId);

		String tableName =PrintService.instance.getResources().getString(R.string.table_name)
				+kotSummary.getTableName();
		if(!TextUtils.isEmpty(kotSummary.getTableName())){
			sbr.append(tableName)
					.append(reNext);
		}
		if (isTakeAway==1) {
			sbr.append(PrintService.instance.getResources().getString(R.string.takeaway_print))
					.append("  ")
					.append(orderNo)
					.append(reNext);
		}else{
			sbr.append( "  ")
					.append(orderNo)
					.append(reNext);
		}
		PrintData header = new PrintData();
		header.setDataFormat(PrintData.FORMAT_TXT);
		header.setTextAlign(PrintData.ALIGN_RIGHT);
		header.setFontsize(2);
		header.setText(sbr.toString());
		this.data.add(header);

		addHortionalLine(this.charSize);
	}
	
	public void addCenterLabel(String label) {
		PrintData header = new PrintData();
		header.setDataFormat(PrintData.FORMAT_TXT);
		header.setTextAlign(PrintData.ALIGN_CENTRE);
		header.setFontsize(2);
		header.setText(label+reNext);
		this.data.add(header);	
		
		addHortionalLine(this.charSize);		
	}
	
	public void addCenterLabel(String label,int fontSize) {
		PrintData header = new PrintData();
		header.setDataFormat(PrintData.FORMAT_TXT);
		header.setTextAlign(PrintData.ALIGN_CENTRE);
		header.setFontsize(fontSize);
		try {
			header.setText(StringUtil.getStr(StringUtil.formatLn(this.charSize, label)));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.data.add(header);	
		
		addHortionalLine(this.charSize);		
	}
	/* Two columns layout (Width = 48dots)
	 * |item Name    38/scale   | QTY 10/scale  |
	 * 
	 **/	
	private String getTwoColHeader(String col1Title, String col2Title) {

		String title1 = StringUtil.padRight(col1Title, this.charSize-KOTPrint.FIXED_COL2_QTY);
		String title2 = StringUtil.padRight(col2Title, KOTPrint.FIXED_COL2_QTY);
		String result = title1.concat(title2).concat(reNext);
		return result;
	}
	
	/* Two columns layout (Width = 48dots)
	 * |item Name   Dynamical   |2| QTY 10/scale  |
	 * 
	 **/
	private String getTwoColContent(String col1Content, String col2Content, int charScale) {
		StringBuffer result = new StringBuffer();

		int col1Lines = 1;
	
		int col2Lines = 1;

//		int qtyLen = KOTPrint.FIXED_COL2_QTY;
//		if(charScale > 1){
		int	qtyLen = charScale*col2Content.length();
//		}
		
		KOTPrint.COL2_ITEMNAME = this.charSize/charScale - qtyLen/charScale - KOTPrint.FIXED_COL2_SPACE;
		
		//double ln1 = col1Content.length();
		int ln1 = 1;
		String [] splitedcontents ={col1Content};
		
		try {
			//ln1 = (col1Content.getBytes("GBK").length)/(KOTPrint.COL2_ITEMNAME*1.0);
			splitedcontents = StringUtil.formatLn(KOTPrint.COL2_ITEMNAME*1, col1Content);
			ln1 = splitedcontents.length;			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		col1Lines = splitedcontents.length;
		//col1Lines = StringUtil.nearestTen(ln1);
		//String col1PadContent = StringUtil.padRight(col1Content, col1Lines*KOTPrint.COL2_ITEMNAME);
		//ArrayList<String> splittedCol1Content = StringUtil.splitEqually(col1PadContent, KOTPrint.COL2_ITEMNAME);
		
		double ln2 = (col2Content.length())/(qtyLen*1.0/charScale);
		col2Lines = StringUtil.nearestTen(ln2);
		String col2PadContent = StringUtil.padRight(col2Content, col2Lines*qtyLen/charScale);
		ArrayList<String> splittedCol2Content = StringUtil.splitEqually(col2PadContent, qtyLen/charScale);

		
		for (int i=0; i< Math.max(col1Lines, col2Lines); i++) {
			if (i<col1Lines) {			
				//result.append(splittedCol1Content.get(i));
				result.append(StringUtil.padRight(splitedcontents[i], KOTPrint.COL2_ITEMNAME));
			}else{
				result.append(StringUtil.padRight(" ", KOTPrint.COL2_ITEMNAME));
			}
			//padding
			result.append(StringUtil.padRight(" ", KOTPrint.FIXED_COL2_SPACE));
			
			if (i<col2Lines) {
				result.append(splittedCol2Content.get(i));
			}else {
				result.append(StringUtil.padRight(" ", (qtyLen)/charScale));
			}
			
			result.append(reNext);
		}
		return result.toString();
	}
	
	public void AddContentListHeader2Cols(String col1Name, String col2Name){
		PrintData header = new PrintData();
		header.setDataFormat(PrintData.FORMAT_TXT);
		header.setText(this.getTwoColHeader(col1Name, col2Name));
		this.data.add(header);
		addHortionalLine(this.charSize);
	}
	
	public void AddKotItem(String itemName, int qty, int scale) {
		scale = 2;
		PrintData kot = new PrintData();
		kot.setDataFormat(PrintData.FORMAT_TXT);
		kot.setFontsize(scale);
		kot.setMarginTop(20);
		kot.setLanguage(PrintData.LANG_CN);
		kot.setText(this.getTwoColContent(itemName, new Integer(qty).toString(), scale));
		this.data.add(kot);
	}


	public void AddModifierItem(String modifiers) {
		AddModifierItem(modifiers, 2);
	}
		
	public void AddModifierItem(String modifiers, int kotFontSize) {
		if (modifiers != null) {

			PrintData kot = new PrintData();
			kot.setDataFormat(PrintData.FORMAT_TXT);
			kot.setFontsize(10);
			kot.setUnderline(true);
			kot.setLanguage(PrintData.LANG_CN);
			kot.setTextAlign(PrintData.ALIGN_LEFT);
			kot.setMarginTop(2);
			modifiers = modifiers + reNext;
			kot.setText(modifiers);
			this.data.add(kot);
		}
	}
	public void addLineSpace(int lines) {
		PrintData kot = new PrintData();
		kot.setDataFormat(PrintData.FORMAT_TXT);
		kot.setFontsize(1);
		kot.setLanguage(PrintData.LANG_CN);
		kot.setText(reNext);
		this.data.add(kot);
	}
	
	public void AddFooter(String op, String remark) {
		addHortionalLine(this.charSize);
		if (!TextUtils.isEmpty(remark)) {
			addSingleLineText(0, "Order remark:" + remark, 0);
			addLineSpace(1);
		}
		addSingleLineText(this.charSize,op, 0);
		addHortionalLine(this.charSize);
		AddCut();
		AddSing();

	}

	public void addFeed(){
		PrintData kot = new PrintData();
		kot.setDataFormat(PrintData.FORMAT_FEED);
		kot.setMarginTop(3);
		this.data.add(kot);
	}
	public void setData(ArrayList<PrintData> data) {
		this.data = data;
	}

}
