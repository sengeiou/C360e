package com.alfred.printer;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.util.Base64;

import com.alfred.print.jobs.PrintJob;
import com.alfred.print.jobs.Priority;
import com.alfred.remote.printservice.PrintService;
import com.alfred.remote.printservice.R;
import com.alfredbase.utils.BH;
import com.alfredbase.utils.TimeUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.path.android.jobqueue.Params;

public class BillPrint extends PrintJob{

	public static int FIXED_COL4_SPACE = 2;
	public static int FIXED_COL4_PRICE = 10; //in case of 48 dots width, QTY col = 10dots
	public static int FIXED_COL4_QTY = 6; //in case of 48 dots width, QTY col = 10dots
	public static int FIXED_COL4_TOTAL = 10; //in case of 48 dots width, QTY col = 10dots
	
	public static int COL4_ITEMNAME; // Width = CharSize/scale - FIXED_COL2_QTY/scale - 
	                                      // FIXED_COL2_PRICE/scale- FIXED_COL2_TOTAL/scale- FIXED_COL2_SPACE *3

	public BillPrint(String uuid,Long bizDate) {
		super(new Params(Priority.HIGH).requireNetwork().persist().groupBy("bill"), "bill",  uuid, bizDate);
		if (this.charSize == 33) {
			BillPrint.FIXED_COL4_TOTAL = 8;
			BillPrint.FIXED_COL4_PRICE = 7;
			BillPrint.FIXED_COL4_QTY = 4;
			BillPrint.FIXED_COL4_SPACE = 1;
		}
	}
	
	public void AddRestaurantInfo(String logo, String name, String address, String customized) {
		
		if (logo!=null && logo.length()>0) {
			PrintData logoimg = new PrintData();
			logoimg.setDataFormat(PrintData.FORMAT_IMG);
			logoimg.addImage(Base64.decode(logo,  Base64.DEFAULT));
			logoimg.setTextAlign(PrintData.ALIGN_CENTRE);
			this.data.add(logoimg);		
		}
		
		StringBuilder rname = new StringBuilder();
		rname.append(name.trim());

		//name
		PrintData rInfo = new PrintData();
		rInfo.setDataFormat(PrintData.FORMAT_TXT);
		rInfo.setFontsize(2);
		rInfo.setTextAlign(PrintData.ALIGN_CENTRE);
		rInfo.setText(rname.toString()+"\r\n");
		this.data.add(rInfo);	

		//address
		StringBuilder addbuf = new StringBuilder();
		addbuf.append(address);

		PrintData radd = new PrintData();
		radd.setDataFormat(PrintData.FORMAT_TXT);
		radd.setFontsize(1);
		radd.setMarginTop(10);
		radd.setTextAlign(PrintData.ALIGN_CENTRE);
		radd.setText(addbuf.toString()+"\r\n");
		this.data.add(radd);
		
		////customized fields
		if (customized != null) {
			StringBuilder ctbuf = new StringBuilder();
			String[] options = customized.split(";",-1);
			for (int i=0; i<options.length;i++) {
				ctbuf.append(options[i]).append("\r\n");
			}
	
			PrintData ctadd = new PrintData();
			ctadd.setDataFormat(PrintData.FORMAT_TXT);
			ctadd.setFontsize(1);
			ctadd.setTextAlign(PrintData.ALIGN_CENTRE);
			ctadd.setText(ctbuf.toString());
			
			this.data.add(ctadd);
		}

		
		addHortionalLine(this.charSize);
	}
	
	public void AddHeader(int isTakeAway, String table, int pax, String billNo,
					String posNo, String cashier, String dateTime, String orderNo) {
		
		//流水号 NO
		PrintData orderNoPrint = new PrintData();
		String orderNoStr = StringUtil.padRight(PrintService.instance.getResources().getString(R.string.swift_no_), this.FIXED_COL4_TOTAL-1);
		String padorderNo = orderNoStr+":"+orderNo+"\r\n";
		orderNoPrint.setDataFormat(PrintData.FORMAT_TXT);
		orderNoPrint.setTextAlign(PrintData.ALIGN_LEFT);
		orderNoPrint.setText(padorderNo);
		this.data.add(orderNoPrint);
		
        
		//cashier
		PrintData cashierPrint = new PrintData();
		String cashierLabel = StringUtil.padRight(PrintService.instance.getResources().getString(R.string.cashier), this.FIXED_COL4_TOTAL-1);
		String cashierStr = cashierLabel+":"+cashier+"\t\t";
//		cashierPrint.setDataFormat(PrintData.FORMAT_TXT);
//		cashierPrint.setTextAlign(PrintData.ALIGN_LEFT);
//		cashierPrint.setText(cashierStr);
//		this.data.add(cashierPrint);
		
		//POS
		PrintData posPrint = new PrintData();
		String posLabel = StringUtil.padLeft(PrintService.instance.getResources().getString(R.string.pos), this.FIXED_COL4_TOTAL-1);
		String posStr = posLabel+":"+posNo+"\r\n";
		posPrint.setDataFormat(PrintData.FORMAT_TXT);
		posPrint.setTextAlign(PrintData.ALIGN_LEFT);
		posPrint.setText(cashierStr+posStr);
		this.data.add(posPrint);

		//Date
		PrintData datePrint = new PrintData();
		String dateLabel = StringUtil.padRight(PrintService.instance.getResources().getString(R.string.date), this.FIXED_COL4_TOTAL-1);
		String dateStr = dateLabel+":"+dateTime+" ";
		
		//Bill NO
		PrintData billNoPrint = new PrintData();
		String billNoStr = StringUtil.padRight(PrintService.instance.getResources().getString(R.string.bill_no_), this.FIXED_COL4_TOTAL-1);
		String padBillNo = billNoStr+":"+billNo+"\r\n";
		billNoPrint.setDataFormat(PrintData.FORMAT_TXT);
		billNoPrint.setTextAlign(PrintData.ALIGN_LEFT);
		billNoPrint.setText(dateStr+padBillNo);
		this.data.add(billNoPrint);

		
		//Table & PAX
		
		PrintData tabPrint = new PrintData();
		String tabLabel = StringUtil.padRight(PrintService.instance.getResources().getString(R.string.table), this.FIXED_COL4_TOTAL/2);
		String tabStr = tabLabel+":"+table;
		if (isTakeAway==1)
			tabStr = "("+PrintService.instance.getResources().getString(R.string.takeaway_print)+")"+tabStr;
		tabPrint.setDataFormat(PrintData.FORMAT_TXT);
		tabPrint.setTextAlign(PrintData.ALIGN_LEFT);
		tabPrint.setFontsize(2);
		tabPrint.setText(tabStr);
		this.data.add(tabPrint);
		
		PrintData paxPrint = new PrintData();
		
		try {
			int padlen = this.charSize - tabStr.getBytes("GBK").length * 2;
			String paxLabel = StringUtil.padRight(PrintService.instance
					.getResources().getString(R.string.pax),
					this.FIXED_COL4_TOTAL / 2);
			String paxStr = paxLabel + ":" + pax;
			paxStr = StringUtil.padLeft(paxStr, padlen) + "\r\n";
			paxPrint.setDataFormat(PrintData.FORMAT_TXT);
			paxPrint.setTextAlign(PrintData.ALIGN_LEFT);
			paxPrint.setTextBold(1);
			paxPrint.setText(paxStr);
			this.data.add(paxPrint);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//addHortionalLine();
		addHortionaDoublelLine(this.charSize);
	}
	
	/*Kiosk uses only*/
	public void AddKioskHeader(String table, int pax, String billNo,
			String posNo, String cashier, String dateTime) {

		//Bill NO
		PrintData billNoPrint = new PrintData();
		String billNoStr = StringUtil.padRight(PrintService.instance.getResources().getString(R.string.bill_no_), this.FIXED_COL4_TOTAL);
		String padBillNo = billNoStr+":"+billNo+"\r\n";
		billNoPrint.setDataFormat(PrintData.FORMAT_TXT);
		billNoPrint.setTextAlign(PrintData.ALIGN_LEFT);
		billNoPrint.setText(padBillNo);
		this.data.add(billNoPrint);
		
		//cashier
		PrintData cashierPrint = new PrintData();
		String cashierLabel = StringUtil.padRight(PrintService.instance.getResources().getString(R.string.cashier), this.FIXED_COL4_TOTAL);
		String cashierStr = cashierLabel+":"+cashier+"\r\n";
		cashierPrint.setDataFormat(PrintData.FORMAT_TXT);
		cashierPrint.setTextAlign(PrintData.ALIGN_LEFT);
		cashierPrint.setText(cashierStr);
		this.data.add(cashierPrint);
		
		//POS
		PrintData posPrint = new PrintData();
		String posLabel = StringUtil.padRight(PrintService.instance.getResources().getString(R.string.pos), this.FIXED_COL4_TOTAL);
		String posStr = posLabel+":"+posNo+"\r\n";
		posPrint.setDataFormat(PrintData.FORMAT_TXT);
		posPrint.setTextAlign(PrintData.ALIGN_LEFT);
		posPrint.setText(posStr);
		this.data.add(posPrint);
		
		//Date
		PrintData datePrint = new PrintData();
		String dateLabel = StringUtil.padRight(PrintService.instance.getResources().getString(R.string.date), this.FIXED_COL4_TOTAL);
		String dateStr = dateLabel+":"+dateTime+"\r\n";
		datePrint.setDataFormat(PrintData.FORMAT_TXT);
		datePrint.setTextAlign(PrintData.ALIGN_LEFT);
		datePrint.setText(dateStr);
		this.data.add(datePrint);
		
		//addHortionalLine();
		addHortionaDoublelLine(this.charSize);
		}	
	/* Four columns layout (Width = 48dots)
	 * |item Name    38/scale   | QTY 10/scale  |
	 * 
	 **/	
	private String getFourColHeader(String col1Title, String col2Title, 
			String col3Title, String col4Title) {
		
		StringBuffer ret = new StringBuffer();
		BillPrint.COL4_ITEMNAME = this.charSize - BillPrint.FIXED_COL4_PRICE - BillPrint.FIXED_COL4_QTY - BillPrint.FIXED_COL4_TOTAL;
		String title1 = StringUtil.padRight(col1Title, BillPrint.COL4_ITEMNAME);
		String title2 = StringUtil.padRight(col2Title, BillPrint.FIXED_COL4_PRICE);
		String title3 = StringUtil.padRight(col3Title, BillPrint.FIXED_COL4_QTY);
		String title4 = StringUtil.padLeft(col4Title, BillPrint.FIXED_COL4_TOTAL);
		ret.append(title1).append(title2).append(title3).append(title4).append("\r\n");

		return ret.toString();
	}
	
	/* Four columns layout (Width = 48dots)
	 * |item Name   Dynamical | Price |  |2| QTY 10/scale  | Total|
	 * 
	 **/
	private String getFourColContent(String col1Content, String col2Content, 
										String col3Content, String col4Content,int charScale) {
		
		StringBuffer result = new StringBuffer();

		int col1Lines = 1;
		int col2Lines = 1;
		int col3Lines = 1;
		int col4Lines = 1;
		
		BillPrint.COL4_ITEMNAME = (this.charSize -BillPrint.FIXED_COL4_TOTAL - BillPrint.FIXED_COL4_QTY - BillPrint.FIXED_COL4_PRICE)/charScale - 3*BillPrint.FIXED_COL4_SPACE;
		
		int ln1 = 1;
		String [] splitedcontents ={col1Content};
		
		try {
			//ln1 = (col1Content.getBytes("GBK").length)/(BillPrint.COL4_ITEMNAME*1.0);
			splitedcontents = StringUtil.formatLn(BillPrint.COL4_ITEMNAME*1, col1Content);
			ln1 = splitedcontents.length;			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		col1Lines = splitedcontents.length;
		//String col1PadContent = StringUtil.padRight(col1Content, col1Lines*BillPrint.COL4_ITEMNAME);
		//ArrayList<String> splittedCol1Content = StringUtil.splitEqually(col1PadContent, BillPrint.COL4_ITEMNAME);
		
		double ln2 = (col2Content.length())/(BillPrint.FIXED_COL4_PRICE*1.0/charScale);
		col2Lines = StringUtil.nearestTen(ln2);
		String col2PadContent = StringUtil.padLeft(col2Content, col2Lines*BillPrint.FIXED_COL4_PRICE/charScale);
		ArrayList<String> splittedCol2Content = StringUtil.splitEqually(col2PadContent, BillPrint.FIXED_COL4_PRICE/charScale);

		double ln3 = (col3Content.length())/(BillPrint.FIXED_COL4_QTY*1.0/charScale);
		col3Lines = StringUtil.nearestTen(ln3);
		String col3PadContent = StringUtil.padLeft(col3Content, col3Lines*BillPrint.FIXED_COL4_QTY/charScale);
		ArrayList<String> splittedCol3Content = StringUtil.splitEqually(col3PadContent, BillPrint.FIXED_COL4_QTY/charScale);

		double ln4 = (col4Content.length())/(BillPrint.FIXED_COL4_TOTAL*1.0/charScale);
		col4Lines = StringUtil.nearestTen(ln4);
		String col4PadContent = StringUtil.padLeft(col4Content, col4Lines*BillPrint.FIXED_COL4_PRICE/charScale);
		ArrayList<String> splittedCol4Content = StringUtil.splitEqually(col4PadContent, BillPrint.FIXED_COL4_PRICE/charScale);

		
		for (int i=0; i< Math.max(Math.max(col1Lines, col2Lines), Math.max(col3Lines, col4Lines)); i++) {
			if (i<col1Lines) {			
				//result.append(splittedCol1Content.get(i));
				result.append(StringUtil.padRight(splitedcontents[i], BillPrint.COL4_ITEMNAME));
			}else{
				result.append(StringUtil.padRight(" ", BillPrint.COL4_ITEMNAME));
			}
			//padding
			result.append(StringUtil.padRight(" ", BillPrint.FIXED_COL4_SPACE));
			
			if (i<col2Lines) {
				result.append(splittedCol2Content.get(i));
			}else {
				result.append(StringUtil.padLeft(" ", (BillPrint.FIXED_COL4_PRICE)/charScale));
			}
			
			//padding
			result.append(StringUtil.padRight(" ", BillPrint.FIXED_COL4_SPACE));
			
			if (i<col3Lines) {
				result.append(splittedCol3Content.get(i));
			}else {
				result.append(StringUtil.padLeft(" ", (BillPrint.FIXED_COL4_QTY)/charScale));
			}

			//padding
			result.append(StringUtil.padRight(" ", BillPrint.FIXED_COL4_SPACE));
			
			if (i<col4Lines) {
				result.append(splittedCol4Content.get(i));
			}else {
				result.append(StringUtil.padLeft(" ", (BillPrint.FIXED_COL4_PRICE)/charScale));
			}			
			result.append("\r\n");
		}
		return result.toString();
	}
	
	public void AddOrderNo(int isTakeAway, String orderNo){
		PrintData header = new PrintData();
		header.setDataFormat(PrintData.FORMAT_TXT);
		header.setFontsize(2);
		StringBuilder sbr = new StringBuilder(String.format("%1$" + this.charSize/2 + "s", ""));
		if (isTakeAway==1) {
			String tkw = PrintService.instance.getResources().getString(R.string.takeaway_print);
			String ord = PrintService.instance.getResources().getString(R.string.order_no_) + orderNo;
			sbr.replace(0, tkw.length()-1, tkw);
			sbr.replace(this.charSize/2-ord.length()*2-1, this.charSize/2-1, ord);
			header.setTextAlign(PrintData.ALIGN_LEFT);
			header.setText(sbr.toString());
		}else{
			String ord = PrintService.instance.getResources().getString(R.string.order_no_) + orderNo;
			sbr.replace(this.charSize/2-ord.length()*2-1, this.charSize/2-1, ord);
			header.setTextAlign(PrintData.ALIGN_RIGHT);
			header.setText(sbr.toString()+"\r\n");
		}
		this.data.add(header);
	}
	
	public void AddContentListHeader(String itemName, String price, String qty, String total){
		PrintData header = new PrintData();
		header.setDataFormat(PrintData.FORMAT_TXT);
		header.setText(this.getFourColHeader(itemName, price, qty, total));
		this.data.add(header);
		addHortionalLine(this.charSize);
	}
	
	public void AddOrderItem(String itemName, String price, String qty, String total, int scale, String weight) {
		PrintData order = new PrintData();
		order.setDataFormat(PrintData.FORMAT_TXT);
		order.setFontsize(scale);
		order.setLanguage(PrintData.LANG_CN);
		order.setMarginTop(20);
		order.setText(this.getFourColContent(itemName, price,qty, total, scale));
		this.data.add(order);
		this.addWeight(weight);
	}
	

	public void addOrderModifier(String itemName, int scale){
		PrintData orderMod = new PrintData();
		orderMod.setDataFormat(PrintData.FORMAT_TXT);
		orderMod.setFontsize(scale);
		orderMod.setLanguage(PrintData.LANG_CN);
		orderMod.setText("  "+itemName+"\r\n");
		orderMod.setTextAlign(PrintData.ALIGN_LEFT);
		this.data.add(orderMod);	
	}	
	public void AddBillSummary(String subtotal, String discount,
				List<Map<String, String>>taxes, String total, String rounding) {

		this.addHortionalLine(this.charSize);
		//subtotal
		PrintData subTotalPrint = new PrintData();
		String subTotal = StringUtil.padLeft(subtotal, this.FIXED_COL4_TOTAL);
		String padSubtotal = PrintService.instance.getResources().getString(R.string.sub_total)+subTotal+"\r\n";
		subTotalPrint.setDataFormat(PrintData.FORMAT_TXT);
		subTotalPrint.setTextAlign(PrintData.ALIGN_RIGHT);
		subTotalPrint.setText(padSubtotal);
		this.data.add(subTotalPrint);
		

		//discount
		PrintData discPrint = new PrintData();
		String discountStr = StringUtil.padLeft(discount, this.FIXED_COL4_TOTAL);
		String padDiscount = PrintService.instance.getResources().getString(R.string.discount)+discountStr+"\r\n";
		discPrint.setDataFormat(PrintData.FORMAT_TXT);
		discPrint.setTextAlign(PrintData.ALIGN_RIGHT);
		discPrint.setText(padDiscount);
		this.data.add(discPrint);
		
		//taxes
		if (taxes != null) {
//			ArrayList<String> taxPriceSUM = taxes.get("taxPriceSum"); 
//			ArrayList<String> taxNames = taxes.get("taxNames");
//			ArrayList<String> taxPercentages = taxes.get("taxPercentages");
			for(Map<String, String> map : taxes){
				PrintData taxPrint = new PrintData();
	            String taxvalue = StringUtil.padLeft(BH.doubleFormat.format(BH.getBD(map.get("taxPriceSum"))), this.FIXED_COL4_TOTAL);
	                
	            String padTax = map.get("taxName")
							+ "("
							+ (int) (Double.parseDouble(map.get("taxPercentage")) * 100)
							+ "%) : $" + taxvalue +"\r\n"; 
	                
	            taxPrint.setDataFormat(PrintData.FORMAT_TXT);
	            taxPrint.setTextAlign(PrintData.ALIGN_RIGHT);
	            taxPrint.setText(padTax);
	        	this.data.add(taxPrint);
			}
//			for (int i = 0; i < taxPriceSUM.size(); i++) {
//				PrintData taxPrint = new PrintData();
//	            String taxvalue = StringUtil.padLeft(BH.doubleFormat.format(BH.getBD(taxPriceSUM
//							.get(i))), this.FIXED_COL4_TOTAL);
//	                
//	            String padTax = taxNames.get(i)
//							+ "("
//							+ (int) (Double.parseDouble(taxPercentages.get(i)) * 100)
//							+ "%) : $" + taxvalue +"\r\n"; 
//	                
//	            taxPrint.setDataFormat(PrintData.FORMAT_TXT);
//	            taxPrint.setTextAlign(PrintData.ALIGN_RIGHT);
//	            taxPrint.setText(padTax);
//	        	this.data.add(taxPrint);
//			}
		}
		
		// total
		Map<String, String> roundMap = new HashMap<String, String>();
		Gson gson = new Gson();
		roundMap = gson.fromJson(rounding,
				new TypeToken<Map<String, String>>() {
				}.getType());
		PrintData totalPrint = new PrintData();
		String totalStr = StringUtil.padLeft(roundMap.get("Total"),
				this.FIXED_COL4_TOTAL);
		String totaling = PrintService.instance.getResources().getString(R.string.total_) + totalStr + "\r\n";
		totalPrint.setDataFormat(PrintData.FORMAT_TXT);
		totalPrint.setTextAlign(PrintData.ALIGN_RIGHT);
		totalPrint.setMarginTop(10);
		totalPrint.setText(totaling);
		this.data.add(totalPrint);
		// rounding
		PrintData roundingPrint = new PrintData();
		String roundingStr = StringUtil.padLeft(roundMap.get("Rounding"),
				this.FIXED_COL4_TOTAL);
		String padRounding = PrintService.instance.getResources().getString(R.string.rounding_print) + roundingStr + "\r\n";
		roundingPrint.setDataFormat(PrintData.FORMAT_TXT);
		roundingPrint.setTextAlign(PrintData.ALIGN_RIGHT);
		roundingPrint.setText(padRounding);
		this.data.add(roundingPrint);	
		
		//grand total
		PrintData gtPrint = new PrintData();
		String gtotalStr = StringUtil.padLeft(total, this.FIXED_COL4_TOTAL);
		String padTotal = PrintService.instance.getResources().getString(R.string.grand_total) + gtotalStr+"\r\n";
		gtPrint.setDataFormat(PrintData.FORMAT_TXT);
		gtPrint.setTextAlign(PrintData.ALIGN_RIGHT);
		gtPrint.setMarginTop(10);
		gtPrint.setFontsize(1);
		gtPrint.setTextBold(1);
		gtPrint.setText(padTotal);
		this.data.add(gtPrint);
		
		this.addHortionaDoublelLine(this.charSize);
	}

	public void AddSettlementDetails(List<LinkedHashMap<String, String>> settlementList) {
		for(LinkedHashMap<String, String> settlement : settlementList){
			//subtotal
			for (Map.Entry<String, String> entry : settlement.entrySet()) {
				PrintData toPrint = new PrintData();
				String lable = StringUtil.padLeft(entry.getValue(), this.FIXED_COL4_TOTAL);
				String toPrintStr = entry.getKey()+" : $"+lable+"\r\n";
				toPrint.setDataFormat(PrintData.FORMAT_TXT);
				toPrint.setTextAlign(PrintData.ALIGN_RIGHT);
				toPrint.setText(toPrintStr);
				this.data.add(toPrint);
			}
		}
	}
	
	public void AddModifierItem(String modifiers) {
		if (modifiers != null) {
			
			PrintData mod = new PrintData();
			mod.setDataFormat(PrintData.FORMAT_TXT);
			mod.setFontsize(1);
			mod.setLanguage(PrintData.LANG_CN);
			modifiers = "( " + modifiers +" )";
			mod.setText(modifiers);
			this.data.add(mod);
			addBlankLine();
		}
	}
	
	public void addWeight(String weight){
		if(BH.getBDThirdFormat(weight).compareTo(BH.getBD("0")) > 0){
			PrintData orderMod = new PrintData();
			orderMod.setDataFormat(PrintData.FORMAT_TXT);
			orderMod.setFontsize(1);
			orderMod.setLanguage(PrintData.LANG_CN);
			orderMod.setText(" * "+weight+"(" + PrintService.instance.getResources().getString(R.string.kg) + ")\r\n");
			orderMod.setTextAlign(PrintData.ALIGN_LEFT);
			this.data.add(orderMod);
		}
	}

	public void getDollarSign(String sign, int lang) {
		PrintData datePrint = new PrintData();
		String dateStr = sign;
		datePrint.setDataFormat(PrintData.FORMAT_TXT);
		datePrint.setTextAlign(PrintData.ALIGN_LEFT);
		datePrint.setLanguage(lang);
		datePrint.setText(dateStr);
		this.data.add(datePrint);		
	}
	
	public void addCloseBillDate() {
		this.addBlankLine();
		this.addSingleLineCenteredTextPaddingWithDash(this.charSize, 
				PrintService.instance.getResources().getString(R.string.check_closed), 0);
		
		Calendar now = Calendar.getInstance();
		String nowstr = TimeUtil.getCloseBillDataTime(now.getTimeInMillis());
		addSingleLineCenteredText(this.charSize, nowstr, 0);
	}
	public void addCustomizedFieldAtFooter(String customized) {
		////customized fields
		if (customized != null && customized.length()>0) {
			StringBuilder ctbuf = new StringBuilder();
			String[] options = customized.split(";",-1);
			for (int i=0; i<options.length;i++) {
				ctbuf.append(options[i]).append("\r\n");
			}

			PrintData ctadd = new PrintData();
			ctadd.setDataFormat(PrintData.FORMAT_TXT);
			ctadd.setFontsize(1);
			ctadd.setTextAlign(PrintData.ALIGN_CENTRE);
			ctadd.setText(ctbuf.toString());
			this.data.add(ctadd);
		}			
	}	
	public void AddFooter(String op, boolean blankline) {
		if (blankline) {
			this.addBlankLine();
			this.addBlankLine();
		}
		addSingleLineCenteredText(this.charSize, op, 0);
		AddCut();		
	}
	
	public void addWelcomeMsg() {
		this.addBlankLine();
		this.addBlankLine();
		addSingleLineCenteredText(this.charSize, PrintService.instance.getResources().getString(R.string.see_you), 0);
	}
	public void AddQRCode(String qr) {
		PrintData qrCode = new PrintData();
		qrCode.setDataFormat(PrintData.FORMAT_QR);
		qrCode.setText(qr+"\r\n");
		qrCode.setTextAlign(PrintData.ALIGN_CENTRE);
		qrCode.setFontsize(2);
		this.data.add(qrCode);		
	}
	
	public void setData(ArrayList<PrintData> data) {
		this.data = data;
	}
}
