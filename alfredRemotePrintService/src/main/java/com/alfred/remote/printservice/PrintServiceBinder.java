package com.alfred.remote.printservice;

import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.alfred.print.jobs.PrintManager;
import com.alfred.printer.BillPrint;
import com.alfred.printer.ComboDetailAnalysisReportPrint;
import com.alfred.printer.DaySalesReportPrint;
import com.alfred.printer.DetailAnalysisReportPrint;
import com.alfred.printer.EntItemReportPrint;
import com.alfred.printer.HourlySalesReportPrint;
import com.alfred.printer.KOTPrint;
import com.alfred.printer.KickDrawerPrint;
import com.alfred.printer.ModifierDetailAnalysisReportPrint;
import com.alfred.printer.MonthlySalesReportPrint;
import com.alfred.printer.StoredCardPrint;
import com.alfred.printer.SummaryAnalysisReportPrint;
import com.alfred.printer.VoidItemReportPrint;
import com.alfredbase.ParamConst;
import com.alfredbase.javabean.ItemCategory;
import com.alfredbase.javabean.ItemMainCategory;
import com.alfredbase.javabean.KotItemDetail;
import com.alfredbase.javabean.KotItemModifier;
import com.alfredbase.javabean.KotSummary;
import com.alfredbase.javabean.Modifier;
import com.alfredbase.javabean.MonthlyPLUReport;
import com.alfredbase.javabean.MonthlySalesReport;
import com.alfredbase.javabean.Order;
import com.alfredbase.javabean.PrinterTitle;
import com.alfredbase.javabean.ReportDaySales;
import com.alfredbase.javabean.ReportDayTax;
import com.alfredbase.javabean.ReportHourly;
import com.alfredbase.javabean.ReportPluDayComboModifier;
import com.alfredbase.javabean.ReportPluDayItem;
import com.alfredbase.javabean.ReportPluDayModifier;
import com.alfredbase.javabean.model.PrintOrderItem;
import com.alfredbase.javabean.model.PrintOrderModifier;
import com.alfredbase.javabean.model.PrintReceiptInfo;
import com.alfredbase.javabean.model.PrinterDevice;
import com.alfredbase.javabean.model.ReportEntItem;
import com.alfredbase.javabean.model.ReportVoidItem;
import com.alfredbase.utils.BH;
import com.alfredbase.utils.IntegerUtils;
import com.alfredbase.utils.TimeUtil;
import com.epson.epsonio.DevType;
import com.epson.epsonio.EpsonIo;
import com.epson.epsonio.EpsonIoException;
import com.epson.epsonio.Finder;
import com.epson.epsonio.IoStatus;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.birbit.android.jobqueue.JobManager;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class PrintServiceBinder extends IAlfredRemotePrintService.Stub{
	private String TAG = PrintServiceBinder.class.getSimpleName();
	private final PrintService service;

    ScheduledExecutorService scheduler = null;
    ScheduledFuture<?> future;

    //get printer name
    static final byte[] CMD_ESC_ENABLE_PRINTER = {
        0x1b, 0x3d, 0x01,    // ESC = 1(Enables printer)
    };

    static final byte[] CMD_GS_I_PRINTER_NAME = {
        0x1d, 0x49, 0x43,    // GS I 67(Printer name)
    };

    static final byte[] CMD_GS_I_ADDITIONAL_FONTS = {
        0x1d, 0x49, 0x45,    // GS I 69(Type of mounted additional fonts)
    };

    static final int RESPONSE_HEADER = 0x5f;
    static final int RESPONSE_TERMINAL = 0x00;
    static final int SEND_RESPONSE_TIMEOUT = 1000;
    static final int RESPONSE_MAXBYTE = 128;

	public PrintServiceBinder(PrintService service) {
		super();
		this.service = service;
	}
	
	@Override
	public void registerCallBack(IAlfredRemotePrintServiceCallback cb)
			throws RemoteException {
		
		synchronized(this.service.mCallbacks) {
			this.service.mCallbacks.add(cb);
		}
		
	}
	
	@Override
	public String getMessage() throws RemoteException {
		return "hello";
	}
	
	@Override
	public void setMessage(String name)  throws RemoteException {
		System.out.println(name);
	}
	
	@Override
	public void kickCashDrawer(String printer) throws RemoteException {
		Gson gson = new Gson();
		PrinterDevice prtDevice =  gson.fromJson(printer, PrinterDevice.class);

		PrintManager printMgr = this.service.getPrintMgr();
		JobManager printJobMgr = printMgr.configureJobManager(prtDevice.getIP());
		PrinterQueueManager pqMgr = this.service.getPqMgr();
		
        if (printJobMgr != null) {	
        	String uuid = pqMgr.getDataUUID("drawer");
			KickDrawerPrint kick = new KickDrawerPrint(uuid,0L);
			kick.setPrinterIp(prtDevice.getIP());
			kick.addKickOut();
			//add queue
			this.service.getPqMgr().queuePrint(kick.getJobForQueue());
			printMgr.addJob(prtDevice.getIP(),kick);
        }
	}
	
	@Override
	public void printDaySalesReport(String xzType, String printer,String title, String report, String tax) 
			throws RemoteException{
		Gson gson = new Gson();
		PrinterDevice prtDevice =  gson.fromJson(printer, PrinterDevice.class);
		PrinterTitle prtTitle =  gson.fromJson(title, PrinterTitle.class);
		ReportDaySales reportData =  gson.fromJson(report, ReportDaySales.class);
		ArrayList<ReportDayTax> taxData = gson.fromJson(tax, new TypeToken<ArrayList<ReportDayTax>>(){}.getType());

		PrintManager printMgr = this.service.getPrintMgr();
		JobManager printJobMgr = printMgr.configureJobManager(prtDevice.getIP());
		PrinterQueueManager pqMgr = this.service.getPqMgr();
		
		if (printJobMgr != null) {
			//int msgType, String uuid,
    		//int msgId, Long bizDate, Long created
			String uuid = pqMgr.getDataUUID(prtTitle.getBill_NO());
			
			DaySalesReportPrint salesPrint 
							= new DaySalesReportPrint(uuid, TimeUtil.getPrintingLongDate(prtTitle.getBizDate()));
			
			String model = prtDevice.getModel();
	    	//set page size
	    	if (this.service.isTMU220(model)) {
	    		salesPrint.setCharSize(33);
	    	} else {
	    		salesPrint.setCharSize(48);
	    	}			
			salesPrint.AddReportHeader(prtTitle.getRestaurantName(), xzType, PrintService.instance.getResources().getString(R.string.sales_analysis));
			salesPrint.AddHeader(prtTitle.getOp(), prtTitle.getBill_NO(), prtTitle.getDate()+" "+prtTitle.getTime(), prtTitle.getBizDate());
			salesPrint.AddContentListHeader(PrintService.instance.getResources().getString(R.string.type), 
					PrintService.instance.getResources().getString(R.string.qty_), 
					PrintService.instance.getResources().getString(R.string.amount));
			salesPrint.setPrinterIp(prtDevice.getIP());
			salesPrint.print(reportData, taxData);
			salesPrint.AddFooter(prtTitle.getDate()+" "+prtTitle.getTime());
			
			pqMgr.queuePrint(salesPrint.getJobForQueue());
			
			printMgr.addJob(prtDevice.getIP(),salesPrint);
		}
	}
	
	@Override
	public void printDetailAnalysisReport(String xzType, String printer,
										String title, String daySaleSummary, String plu, String pluMod,String pluCombo, String category, String items)
			throws RemoteException{
		Gson gson = new Gson();

		PrinterDevice prtDevice =  gson.fromJson(printer, PrinterDevice.class);
		PrinterTitle prtTitle =  gson.fromJson(title, PrinterTitle.class);
		ReportDaySales reportData =  gson.fromJson(daySaleSummary, ReportDaySales.class);
		
		ArrayList<ReportPluDayItem> pluData =  gson.fromJson(plu, new TypeToken<ArrayList<ReportPluDayItem>>(){}.getType());
		ArrayList<ItemMainCategory> categoryData = gson.fromJson(category, new TypeToken<ArrayList<ItemMainCategory>>(){}.getType());
		ArrayList<ItemCategory> itemsData = gson.fromJson(items, new TypeToken<ArrayList<ItemCategory>>(){}.getType());
		ArrayList<ReportPluDayModifier> modifier = gson.fromJson(pluMod, new TypeToken<ArrayList<ReportPluDayModifier>>(){}.getType());
		ArrayList<ReportPluDayComboModifier> comb = gson.fromJson(pluCombo, new TypeToken<ArrayList<ReportPluDayComboModifier>>(){}.getType());

		PrintManager printMgr = this.service.getPrintMgr();
		JobManager printJobMgr = printMgr.configureJobManager(prtDevice.getIP());
		PrinterQueueManager pqMgr = this.service.getPqMgr();

		if (printJobMgr != null) {
			String uuid = pqMgr.getDataUUID(prtTitle.getBill_NO());
			DetailAnalysisReportPrint daPrint 
						= new DetailAnalysisReportPrint(uuid, TimeUtil.getPrintingLongDate(prtTitle.getBizDate()));
			String model = prtDevice.getModel();
	    	//set page size
	    	if (this.service.isTMU220(model)) {
	    		daPrint.setCharSize(33);
	    	} else {
	    		daPrint.setCharSize(48);
	    	}			
			daPrint.AddReportHeader(prtTitle.getRestaurantName(), xzType, PrintService.instance.getResources().getString(R.string.detail_analysis));
			daPrint.AddHeader(prtTitle.getOp(), prtTitle.getBill_NO(), prtTitle.getDate()+" "+prtTitle.getTime(), prtTitle.getBizDate());
			if (App.instance.countryCode == ParamConst.CHINA && reportData!=null) {
				//Bob: Print Sales summary
				daPrint.addSalesSummary(reportData);
			}
			daPrint.AddContentListHeader(PrintService.instance.getResources().getString(R.string.plu_name), 
					PrintService.instance.getResources().getString(R.string.price), 
					PrintService.instance.getResources().getString(R.string.qty), 
					PrintService.instance.getResources().getString(R.string.amount));
			daPrint.print(pluData,modifier,comb,categoryData, itemsData);
			daPrint.setPrinterIp(prtDevice.getIP());
			daPrint.AddFooter(prtTitle.getDate()+" "+prtTitle.getTime());
			pqMgr.queuePrint(daPrint.getJobForQueue());
			printMgr.addJob(prtDevice.getIP(),daPrint);
		}
	}	
	
	@Override
	public void printSummaryAnalysisReport(String xzType, String printer,
										String title, String plu, String pluMod, String category, String items)
			throws RemoteException{
		Gson gson = new Gson();

		PrinterDevice prtDevice =  gson.fromJson(printer, PrinterDevice.class);
		PrinterTitle prtTitle =  gson.fromJson(title, PrinterTitle.class);
		ArrayList<ReportPluDayItem> pluData =  gson.fromJson(plu, new TypeToken<ArrayList<ReportPluDayItem>>(){}.getType());
		ArrayList<ItemMainCategory> categoryData = gson.fromJson(category, new TypeToken<ArrayList<ItemMainCategory>>(){}.getType());
		ArrayList<ItemCategory>itemsData = gson.fromJson(items, new TypeToken<ArrayList<ItemCategory>>(){}.getType());
		ArrayList<ReportPluDayModifier> modifier = gson.fromJson(pluMod, new TypeToken<ArrayList<ReportPluDayModifier>>(){}.getType());

		PrintManager printMgr = this.service.getPrintMgr();
		JobManager printJobMgr = printMgr.configureJobManager(prtDevice.getIP());
		PrinterQueueManager pqMgr = this.service.getPqMgr();
		
		if (printJobMgr != null) {
			String uuid = pqMgr.getDataUUID(prtTitle.getBill_NO());
			SummaryAnalysisReportPrint daPrint
					= new SummaryAnalysisReportPrint(uuid, TimeUtil.getPrintingLongDate(prtTitle.getBizDate()));
			String model = prtDevice.getModel();
	    	//set page size
	    	if (this.service.isTMU220(model)) {
	    		daPrint.setCharSize(33);
	    	} else {
	    		daPrint.setCharSize(48);
	    	}
	    	
			daPrint.AddReportHeader(prtTitle.getRestaurantName(), xzType, PrintService.instance.getResources().getString(R.string.summary_analysis));
			daPrint.AddHeader(prtTitle.getOp(), prtTitle.getBill_NO(), prtTitle.getDate()+" "+prtTitle.getTime(), prtTitle.getBizDate());
			daPrint.AddContentListHeader(PrintService.instance.getResources().getString(R.string.plu_name), 
					PrintService.instance.getResources().getString(R.string.qty), 
					PrintService.instance.getResources().getString(R.string.amount));
			daPrint.setPrinterIp(prtDevice.getIP());
			daPrint.print(pluData, modifier,categoryData, itemsData);
			daPrint.AddFooter(prtTitle.getDate()+" "+prtTitle.getTime());
			pqMgr.queuePrint(daPrint.getJobForQueue());
			printMgr.addJob(prtDevice.getIP(),daPrint);
		}
	}	
	
	public void printHourlyAnalysisReport(String xzType, String printer, String title, String hourly)
			throws RemoteException{
		Gson gson = new Gson();

		PrinterDevice prtDevice =  gson.fromJson(printer, PrinterDevice.class);
		PrinterTitle prtTitle =  gson.fromJson(title, PrinterTitle.class);
		ArrayList<ReportHourly> hourlySales	 =  gson.fromJson(hourly, new TypeToken<ArrayList<ReportHourly>>(){}.getType());

		PrintManager printMgr = this.service.getPrintMgr();
		JobManager printJobMgr = printMgr.configureJobManager(prtDevice.getIP());
		PrinterQueueManager pqMgr = this.service.getPqMgr();
		
		if (printJobMgr != null) {
			String uuid = pqMgr.getDataUUID(prtTitle.getBill_NO());
			HourlySalesReportPrint hsPrint 
					= new HourlySalesReportPrint(uuid, TimeUtil.getPrintingLongDate(prtTitle.getBizDate()));
			String model = prtDevice.getModel();
	    	//set page size
	    	if (this.service.isTMU220(model)) {
	    		hsPrint.setCharSize(33);
	    	} else {
	    		hsPrint.setCharSize(48);
	    	}			
			hsPrint.AddReportHeader(prtTitle.getRestaurantName(), xzType, PrintService.instance.getResources().getString(R.string.hourly_sales));
			hsPrint.AddHeader(prtTitle.getOp(), prtTitle.getBill_NO(), prtTitle.getDate()+" "+prtTitle.getTime(),prtTitle.getBizDate());
			hsPrint.AddContentListHeader(PrintService.instance.getResources().getString(R.string.hour), 
					PrintService.instance.getResources().getString(R.string.tran), 
					PrintService.instance.getResources().getString(R.string.amount));
			hsPrint.setPrinterIp(prtDevice.getIP());
			hsPrint.print(hourlySales);
			hsPrint.AddFooter(prtTitle.getDate()+" "+prtTitle.getTime());
			pqMgr.queuePrint(hsPrint.getJobForQueue());
			printMgr.addJob(prtDevice.getIP(), hsPrint);
		}		
	}
	
	public void printVoidItemAnalysisReport(String xzType, String printer, String title, String voidItems)
			throws RemoteException{
		Gson gson = new Gson();
		PrinterDevice prtDevice =  gson.fromJson(printer, PrinterDevice.class);
		PrinterTitle prtTitle =  gson.fromJson(title, PrinterTitle.class);
		ArrayList<ReportVoidItem> reportVoidItems = gson.fromJson(voidItems, new TypeToken<ArrayList<ReportVoidItem>>(){}.getType());

		PrintManager printMgr = this.service.getPrintMgr();
		JobManager printJobMgr = printMgr.configureJobManager(prtDevice.getIP());
		PrinterQueueManager pqMgr = this.service.getPqMgr();
		
		if (printJobMgr != null) {
			String uuid = pqMgr.getDataUUID(prtTitle.getBill_NO());
			VoidItemReportPrint viPrint 
					= new VoidItemReportPrint(uuid, TimeUtil.getPrintingLongDate(prtTitle.getBizDate()));
			
			String model = prtDevice.getModel();
	    	//set page size
	    	if (this.service.isTMU220(model)) {
	    		viPrint.setCharSize(33);
	    	} else {
	    		viPrint.setCharSize(48);
	    	}
	    	
			viPrint.AddReportHeader(prtTitle.getRestaurantName(), xzType, PrintService.instance.getResources().getString(R.string.void_plu));
			viPrint.AddHeader(prtTitle.getOp(), prtTitle.getBill_NO(), prtTitle.getDate()+" "+prtTitle.getTime(), prtTitle.getBizDate());
			viPrint.AddContentListHeader(PrintService.instance.getResources().getString(R.string.item_name), 
					PrintService.instance.getResources().getString(R.string.qty), 
					PrintService.instance.getResources().getString(R.string.amount));
			viPrint.setPrinterIp(prtDevice.getIP());
			viPrint.print(reportVoidItems);
			viPrint.AddFooter(prtTitle.getPos());
			pqMgr.queuePrint(viPrint.getJobForQueue());
			printMgr.addJob(prtDevice.getIP(), viPrint);
		}		
	}
	
	public void printEntItemAnalysisReport(String xzType, String printer, String title, String voidItems)
			throws RemoteException{
		Gson gson = new Gson();
 
		PrinterDevice prtDevice =  gson.fromJson(printer, PrinterDevice.class);
		PrinterTitle prtTitle =  gson.fromJson(title, PrinterTitle.class);
		ArrayList<ReportEntItem> reportEntItems = gson.fromJson(voidItems, new TypeToken<ArrayList<ReportEntItem>>(){}.getType());

		PrintManager printMgr = this.service.getPrintMgr();
		JobManager printJobMgr = printMgr.configureJobManager(prtDevice.getIP());
		PrinterQueueManager pqMgr = this.service.getPqMgr();		

		if (printJobMgr != null) {
			String uuid = pqMgr.getDataUUID(prtTitle.getBill_NO());
			EntItemReportPrint eiPrint 
				= new EntItemReportPrint(uuid, TimeUtil.getPrintingLongDate(prtTitle.getBizDate()));
			String model = prtDevice.getModel();
	    	//set page size
	    	if (this.service.isTMU220(model)) {
	    		eiPrint.setCharSize(33);
	    	} else {
	    		eiPrint.setCharSize(48);
	    	}
	    	
			eiPrint.AddReportHeader(prtTitle.getRestaurantName(), xzType, PrintService.instance.getResources().getString(R.string.ent_plu));
			eiPrint.AddHeader(prtTitle.getOp(), prtTitle.getBill_NO(), prtTitle.getDate()+" "+prtTitle.getTime(), prtTitle.getBizDate());
			eiPrint.AddContentListHeader(PrintService.instance.getResources().getString(R.string.item_name), 
					PrintService.instance.getResources().getString(R.string.qty), 
					PrintService.instance.getResources().getString(R.string.amount));
			eiPrint.setPrinterIp(prtDevice.getIP());
			eiPrint.print(reportEntItems);
			eiPrint.AddFooter(prtTitle.getPos());
			pqMgr.queuePrint(eiPrint.getJobForQueue());
			printMgr.addJob(prtDevice.getIP(), eiPrint);
		}		
	}

	@Override
	public void printKOT(String printer,String summary,
			 String detail, String modifiers, boolean oneprint, boolean doublePrint, int kotFontSize ) throws RemoteException{

		Gson gson = new Gson();

		PrinterDevice prtDevice =  gson.fromJson(printer, PrinterDevice.class);
		KotSummary kotsummary =  gson.fromJson(summary, KotSummary.class);
		ArrayList<KotItemDetail> itemDetailsList = gson.fromJson(detail, new TypeToken<ArrayList<KotItemDetail>>(){}.getType());
		ArrayList<KotItemModifier> modifiersList = gson.fromJson(modifiers, new TypeToken<ArrayList<KotItemModifier>>(){}.getType());

		PrintManager printMgr = this.service.getPrintMgr();
		JobManager printJobMgr = printMgr.configureJobManager(prtDevice.getIP());
		PrinterQueueManager pqMgr = this.service.getPqMgr();

		String model = prtDevice.getModel();
		int copies = 1;
        if (doublePrint==true)
        	copies = 2;

        for (int i=0; i<copies ;i++) {
        	if (oneprint){
				if(printJobMgr != null){
					  String uuid = pqMgr.getDataUUID(kotsummary.getKotIdString());

						KOTPrint kot = new KOTPrint(uuid, kotsummary.getBusinessDate());
				    	//set page size
				    	if (this.service.isTMU220(model)) {
				    		kot.setCharSize(33);
				    	} else {
				    		kot.setCharSize(48);
				    	}
						kot.addLineSpace(2);
						kot.AddTitle(kotsummary.getRevenueCenterName(),kotsummary.getTableName());
						if(!TextUtils.isEmpty(kotsummary.getDescription())){
							kot.addCenterLabel(kotsummary.getDescription(), kotFontSize);
						}
						if (itemDetailsList.size()>=0) {
							KotItemDetail kotitem = itemDetailsList.get(0);
							if (kotitem.getKotStatus().intValue() == ParamConst.KOT_STATUS_VOID) {
								 kot.addCenterLabel(PrintService.instance.getResources().getString(R.string.void_), kotFontSize);
							}
						}
						kot.AddHeader(kotsummary.getIsTakeAway(), kotsummary.getOrderNoString());
						kot.setPrinterIp(prtDevice.getIP());
						kot.AddContentListHeader2Cols(PrintService.instance.getResources().getString(R.string.item_name),
								PrintService.instance.getResources().getString(R.string.qty));
						boolean canPrint = false;
						for (KotItemDetail item: itemDetailsList) {
						   kot.AddKotItem(item.getItemName(), item.getItemNum(), kotFontSize);
						   ArrayList<KotItemModifier> modList = getModifiersByDetailId(item.getId().intValue(), modifiersList);
						   int size = getModifierSizehavePrintId(modList);
						   if (size != 0){
							   canPrint = false;
							   for (KotItemModifier kotItemModifier : modList){
								   if (IntegerUtils.isEmptyOrZero(kotItemModifier.getPrinterId())
										   || kotItemModifier.getPrinterId().intValue() == prtDevice.getDevice_id()){
									   canPrint = true;
									   if(!IntegerUtils.isEmptyOrZero(kotItemModifier.getModifierNum()) && kotItemModifier.getModifierNum().intValue() > 1){
										   kot.AddModifierItem(kotItemModifier.getModifierName() + "x" + kotItemModifier.getModifierNum().intValue(),1);
									   }else{
										   kot.AddModifierItem(kotItemModifier.getModifierName(),1);
									   }


								   }
							   }
						   }else{
							   canPrint = true;
							   List<String> mods = getModifierNameStr(modList);
							   for (String mod : mods) {
							       kot.AddModifierItem(mod,1);
							   }
						   }
							if (item.getSpecialInstractions() != null) {
								kot.AddModifierItem("*" + item.getSpecialInstractions() + "*",1);
							}
							kot.addLineSpace(1);
						}

						if (i==1) {
				          kot.AddFooter(PrintService.instance.getResources().getString(R.string.kot_copy));
						} else {
					      kot.AddFooter(TimeUtil.getTime());
						}
						if (canPrint) {
							pqMgr.queuePrint(kot.getJobForQueue());
							printMgr.addJob(prtDevice.getIP(), kot);
						}
		        }
        	}else {
				if(printJobMgr != null){
					for (KotItemDetail item: itemDetailsList) {
						ArrayList<KotItemModifier> modList = getModifiersByDetailId(item.getId().intValue(), modifiersList);
						int size = getModifierSizehavePrintId(modList);
						if (size != 0) {
							List<KotItemModifier> comboItems = getComboItemModifier(prtDevice.getDevice_id(), modList);
							List<KotItemModifier> comboGeneralModifiers = getComboGeneralModifier(prtDevice.getDevice_id(), modList);
							 for (KotItemModifier kotItemModifier : comboItems){
								  String uuid = pqMgr.getDataUUID(kotsummary.getKotIdString());

									KOTPrint kot = new KOTPrint(uuid, kotsummary.getBusinessDate());

							    	//set page size
							    	if (this.service.isTMU220(model)) {
							    		kot.setCharSize(33);
							    	} else {
							    		kot.setCharSize(48);
							    	}
									kot.AddTitle(kotsummary.getRevenueCenterName(),kotsummary.getTableName());
									if(!TextUtils.isEmpty(kotsummary.getDescription())){
										kot.addCenterLabel(kotsummary.getDescription(), kotFontSize);
									}
									if (item.getKotStatus().intValue() == ParamConst.KOT_STATUS_VOID) {
										 kot.addCenterLabel(PrintService.instance.getResources().getString(R.string.void_), kotFontSize);
									}
							    	kot.AddHeader(kotsummary.getIsTakeAway(), kotsummary.getOrderNoString());
									kot.setPrinterIp(prtDevice.getIP());
									kot.AddContentListHeader2Cols(PrintService.instance.getResources().getString(R.string.item_name),
											PrintService.instance.getResources().getString(R.string.qty));

									kot.AddKotItem(item.getItemName(), item.getItemNum(), kotFontSize);
									 if(!IntegerUtils.isEmptyOrZero(kotItemModifier.getModifierNum()) && kotItemModifier.getModifierNum().intValue() > 1){
										 kot.AddModifierItem(kotItemModifier.getModifierName() + "x" + kotItemModifier.getModifierNum().intValue(), 1);
									 } else{
										 kot.AddModifierItem(kotItemModifier.getModifierName(), 1);
									 }

									for(KotItemModifier kotGeneralItemModifier : comboGeneralModifiers){
										 if(!IntegerUtils.isEmptyOrZero(kotGeneralItemModifier.getModifierNum()) && kotGeneralItemModifier.getModifierNum().intValue() > 1){
											 kot.AddModifierItem(kotGeneralItemModifier.getModifierName() + "x" + kotGeneralItemModifier.getModifierNum().intValue(), 1);
										 }else{
											 kot.AddModifierItem(kotGeneralItemModifier.getModifierName(), 1);
										 }
									}
									if (item.getSpecialInstractions() != null) {
										kot.AddModifierItem("*" + item.getSpecialInstractions() + "*", 1);
									}
									if (i==1) {
								          kot.AddFooter(PrintService.instance.getResources().getString(R.string.kot_copy));
									} else {
									      kot.AddFooter(TimeUtil.getTime());
									}
									pqMgr.queuePrint(kot.getJobForQueue());
									printMgr.addJob(prtDevice.getIP(),kot);
							   }

						} else {
							String uuid = pqMgr.getDataUUID(kotsummary.getKotIdString());

							KOTPrint kot = new KOTPrint(uuid, kotsummary.getBusinessDate());

					    	//set page size
					    	if (this.service.isTMU220(model)) {
					    		kot.setCharSize(33);
					    	} else {
					    		kot.setCharSize(48);
					    	}
					    	if(!TextUtils.isEmpty(kotsummary.getDescription())){
								kot.addCenterLabel(kotsummary.getDescription(), kotFontSize);
							}
					    	if (itemDetailsList.size()>=0) {
								KotItemDetail kotitem = itemDetailsList.get(0);
								if (kotitem.getKotStatus().intValue() == ParamConst.KOT_STATUS_VOID) {
									 kot.addCenterLabel(PrintService.instance.getResources().getString(R.string.void_), kotFontSize);
								}
							}
							kot.AddTitle(kotsummary.getRevenueCenterName(),kotsummary.getTableName());
					    	kot.AddHeader(kotsummary.getIsTakeAway(), kotsummary.getOrderNoString());
							kot.setPrinterIp(prtDevice.getIP());
							kot.AddContentListHeader2Cols(PrintService.instance.getResources().getString(R.string.item_name),
									PrintService.instance.getResources().getString(R.string.qty));

							kot.AddKotItem(item.getItemName(), item.getItemNum(), kotFontSize);
							List<String> mods = getModifierNameStr(modList);
							for (String mod : mods) {
								kot.AddModifierItem(mod, 1);
							}
							if (item.getSpecialInstractions() != null) {
								kot.AddModifierItem("*" + item.getSpecialInstractions() + "*", 1);
							}
							if (i==1) {
						          kot.AddFooter(PrintService.instance.getResources().getString(R.string.kot_copy));
							} else {
							      kot.AddFooter(TimeUtil.getTime());
							}
							pqMgr.queuePrint(kot.getJobForQueue());
							printMgr.addJob(prtDevice.getIP(),kot);
						}

						//kot.AddFooter(kotsummary.getRevenueCenterName());
//						if (canPrint)

					}
				}
	        }
        }
	}
	
	@Override
	public void printBill(String printer, String title, 
							String order, String orderDetail,
							String modifiers,
							String tax, String payment, boolean doubleprint, boolean doubleReceipts, String rounding) throws RemoteException {
		
		Gson gson = new Gson();
		boolean isCashSettlement = false;

		PrinterDevice prtDevice =  gson.fromJson(printer, PrinterDevice.class);
		PrinterTitle prtTitle =  gson.fromJson(title, PrinterTitle.class);
		Order theOrder =  gson.fromJson(order, Order.class);

		String model = prtDevice.getModel();
        
		ArrayList<PrintOrderItem> printOrderItemList = gson.fromJson(orderDetail, 
				new TypeToken<ArrayList<PrintOrderItem>>(){}.getType());	
		
		ArrayList<PrintOrderModifier> orderModifiers = gson.fromJson(modifiers, 
														new TypeToken<ArrayList<PrintOrderModifier>>(){}.getType());
		
		List<Map<String, String>> taxes = gson.fromJson(tax, 
														new TypeToken<List<Map<String, String>>>(){}.getType());
		
		List<PrintReceiptInfo> settlement = gson.fromJson(payment,
														new TypeToken<List<PrintReceiptInfo>>(){}.getType());
		
		PrintManager printMgr = this.service.getPrintMgr();
		JobManager printJobMgr = printMgr.configureJobManager(prtDevice.getIP());
		PrinterQueueManager pqMgr = this.service.getPqMgr();
		
		if(settlement.isEmpty()){
			//double bill print
			if(printJobMgr != null){
				int printSize = 1;
				if (doubleprint)
					printSize = 2;
				for (int i=0;i < printSize; i++) {
					String uuid = pqMgr.getDataUUID(prtTitle.getBill_NO());
					
			    	BillPrint billPrint = new BillPrint(uuid, Long.valueOf(prtTitle.getBizDate()));
			    	
			    	billPrint.setPrinterIp(prtDevice.getIP());
			    	
			    	//set page size
			    	if (this.service.isTMU220(model)) {
			    		billPrint.setCharSize(33);
			    		//U220 cannot support image print
				    	billPrint.AddRestaurantInfo(null, 
				    								prtTitle.getRestaurantName(), 
				    								prtTitle.getAddressDetail(), prtTitle.getOptions());			    		
			    	} else {
			    		billPrint.setCharSize(48);
				    	billPrint.AddRestaurantInfo(prtTitle.getLogo(), 
				    								prtTitle.getRestaurantName(), 
				    								prtTitle.getAddressDetail(), prtTitle.getOptions());
			    	}
			    	

			    	billPrint.AddHeader(theOrder.getIsTakeAway(),prtTitle.getTableName(), theOrder.getPersons(), 
			    							prtTitle.getBill_NO(), prtTitle.getPos(),
			    							prtTitle.getOp(), prtTitle.getDate()+" "+prtTitle.getTime(), theOrder.getOrderNo().toString());
			    	billPrint.AddContentListHeader(PrintService.instance.getResources().getString(R.string.item),
			    			PrintService.instance.getResources().getString(R.string.price), 
			    			PrintService.instance.getResources().getString(R.string.qty), 
			    			PrintService.instance.getResources().getString(R.string.total));//("Item Name", "QTY");
					
			    	
			    	for(PrintOrderItem item:printOrderItemList) {
						billPrint.AddOrderItem(item.getItemName(), item.getPrice(), 
												item.getQty(), item.getAmount(), 1, item.getWeight());
						//getModifiersByDetailId()
						//// 
						if (orderModifiers !=null) {
							for (int m=0; m<orderModifiers.size(); m++) {
								PrintOrderModifier om =  orderModifiers.get(m);
								if (om.getOrderDetailId() == item.getOrderDetailId()) {
									if(om.getQty() > 1){
										billPrint.addOrderModifier(om.getItemName() + "x" + om.getQty(),1);
									}else{
										billPrint.addOrderModifier(om.getItemName(),1);
									}
								}
							}
						}
					}
	
		////////////// Bill Summary
					String subTotal = BH.doubleFormat.format(BH.getBD(theOrder.getSubTotal()));
		            String discount = BH.doubleFormat.format(BH.getBD(theOrder.getDiscountAmount()));
		            String grandTotal = BH.doubleFormat.format(BH.getBD(theOrder.getTotal()));
					
		            billPrint.AddBillSummary(subTotal, discount, taxes, grandTotal, rounding);
		            billPrint.addCustomizedFieldAtFooter(prtTitle.getFooterOptions());
		            billPrint.AddFooter(PrintService.instance.getResources().getString(R.string.powered_by_alfred), true);
		            pqMgr.queuePrint(billPrint.getJobForQueue());
		            printMgr.addJob(prtDevice.getIP(),billPrint);
				}
			}
		} else {
			if(printJobMgr != null){
				int receiptCopy =1;
				
				if (doubleReceipts)
					receiptCopy =2; 
				
				for (int i=0; i<receiptCopy;i++){
					String uuid = pqMgr.getDataUUID(prtTitle.getBill_NO());
					
			    	BillPrint billPrint = new BillPrint(uuid, Long.valueOf(prtTitle.getBizDate()));
			    	
			    	billPrint.setPrinterIp(prtDevice.getIP());
			    	//set page size
			    	if (this.service.isTMU220(model)) {
			    		billPrint.setCharSize(33);
				    	billPrint.AddRestaurantInfo(null, 
				    								prtTitle.getRestaurantName(), 
				    								prtTitle.getAddressDetail(), prtTitle.getOptions());			    		
			    	} else {
			    		billPrint.setCharSize(48);
				    	billPrint.AddRestaurantInfo(prtTitle.getLogo(), 
				    								prtTitle.getRestaurantName(), 
				    								prtTitle.getAddressDetail(), prtTitle.getOptions());

			    	}
			    	
			    	billPrint.AddHeader(theOrder.getIsTakeAway(),prtTitle.getTableName(), 
			    							theOrder.getPersons(), 
			    							prtTitle.getBill_NO(), prtTitle.getPos(),
			    							prtTitle.getOp(), prtTitle.getDate()+" "+prtTitle.getTime(),theOrder.getOrderNo().toString());
			    	billPrint.AddContentListHeader(PrintService.instance.getResources().getString(R.string.item), 
			    			PrintService.instance.getResources().getString(R.string.price), 
			    			PrintService.instance.getResources().getString(R.string.qty), 
			    			PrintService.instance.getResources().getString(R.string.total));
			    	

			    	for(PrintOrderItem item:printOrderItemList) {
						billPrint.AddOrderItem(item.getItemName(), item.getPrice(), 
												item.getQty(), item.getAmount(), 1, item.getWeight());
						////add modifier print
						if (orderModifiers !=null) {
							for (int m=0; m<orderModifiers.size(); m++) {
								PrintOrderModifier om =  orderModifiers.get(m);
								if (om.getOrderDetailId() == item.getOrderDetailId()) {
									billPrint.addOrderModifier(om.getItemName(),1);
								}
							}	
						}
					}
		
		////////////// Bill Summary
					String subTotal = BH.doubleFormat.format(BH.getBD(theOrder.getSubTotal()));
		            String discount = BH.doubleFormat.format(BH.getBD(theOrder.getDiscountAmount()));
		            String grandTotal = BH.doubleFormat.format(BH.getBD(theOrder.getTotal()));
					
		            billPrint.AddBillSummary(subTotal, discount, taxes, grandTotal, rounding);
		            List<LinkedHashMap<String, String>> stmtList = new ArrayList<LinkedHashMap<String,String>>();
					if (settlement != null) {
						String paymentType = "";
						String cardNo = null;
						for(PrintReceiptInfo printReceiptInfo : settlement){
							 LinkedHashMap<String, String> stmt = new LinkedHashMap<String, String>();
							switch (printReceiptInfo.getPaymentTypeId()) {
							case ParamConst.SETTLEMENT_TYPE_CASH:
								if (!TextUtils.isEmpty(printReceiptInfo.getPaidAmount()) && BH.getBD(printReceiptInfo.getPaidAmount()).compareTo(BH.getBD(ParamConst.DOUBLE_ZERO)) > 0) {
									stmt.put(PrintService.instance.getResources().getString(R.string.cash_), BH.add(BH.getBD(printReceiptInfo.getPaidAmount()), BH.getBD(printReceiptInfo.getCashChange()),true).toString());
									stmt.put(PrintService.instance.getResources().getString(R.string.changes), BH.getBD(printReceiptInfo.getCashChange()).toString());
									isCashSettlement = true;
								}
								if (isCashSettlement && i==0) {
									this.kickCashDrawer(printer);
								}
								break;
							case ParamConst.SETTLEMENT_TYPE_MASTERCARD:
								paymentType = PrintService.instance.getResources().getString(R.string.mastercard);
								break;
							case ParamConst.SETTLEMENT_TYPE_UNIPAY:
								paymentType = PrintService.instance.getResources().getString(R.string.unionpay);
								break;
							case ParamConst.SETTLEMENT_TYPE_VISA:
								paymentType = PrintService.instance.getResources().getString(R.string.visa);
								break;
							case ParamConst.SETTLEMENT_TYPE_AMEX:
								paymentType = PrintService.instance.getResources().getString(R.string.amex_);
								break;
							case ParamConst.SETTLEMENT_TYPE_JCB:
								paymentType = PrintService.instance.getResources().getString(R.string.jcb);
								break;
							case ParamConst.SETTLEMENT_TYPE_DINNER_INTERMATIONAL:
								paymentType = PrintService.instance.getResources().getString(R.string.dinner_intern);
								break;
							case ParamConst.SETTLEMENT_TYPE_BILL_ON_HOLD:
								paymentType = PrintService.instance.getResources().getString(R.string.hold_bill);
								break;
							case ParamConst.SETTLEMENT_TYPE_COMPANY:
								paymentType = PrintService.instance.getResources().getString(R.string.com_credits);
								break;
							case ParamConst.SETTLEMENT_TYPE_HOURS_CHARGE:
								paymentType = PrintService.instance.getResources().getString(R.string.house_charge);
								break;
							case ParamConst.SETTLEMENT_TYPE_VOID:
								paymentType = PrintService.instance.getResources().getString(R.string._void);
								break;
							case ParamConst.SETTLEMENT_TYPE_ENTERTAINMENT:
								paymentType = PrintService.instance.getResources().getString(R.string.ent);
								break;
							case ParamConst.SETTLEMENT_TYPE_NETS:
								paymentType = PrintService.instance.getResources().getString(R.string.nets);
								break;
							case ParamConst.SETTLEMENT_TYPE_ALIPAY:
								paymentType = PrintService.instance.getResources().getString(R.string.alipay);
								break;
							case ParamConst.SETTLEMENT_TYPE_WEIXIN:
								paymentType = PrintService.instance.getResources().getString(R.string.weixin);
								break;
							case ParamConst.SETTLEMENT_TYPE_PAYPAL:
								paymentType = PrintService.instance.getResources().getString(R.string.paypal);
								break;
							case ParamConst.SETTLEMENT_TYPE_STORED_CARD:
								paymentType = PrintService.instance.getResources().getString(R.string.stored_card);
								break;
							}
							if (!TextUtils.isEmpty(paymentType)) {
								stmt.put(paymentType,
										printReceiptInfo.getPaidAmount());
							}
							if (!TextUtils
									.isEmpty(printReceiptInfo.getCardNo())) {
								stmt.put(PrintService.instance.getResources().getString(R.string.card_no),
										"**** " + printReceiptInfo.getCardNo());
							}
							stmtList.add(stmt);
						}
						billPrint.AddSettlementDetails(stmtList);
						billPrint.addCustomizedFieldAtFooter(prtTitle.getFooterOptions());
						//print check clo
						billPrint.addCloseBillDate();
						billPrint.addWelcomeMsg();
					}			
		/////////
		            billPrint.AddFooter(PrintService.instance.getResources().getString(R.string.powered_by_alfred), false);
		            pqMgr.queuePrint(billPrint.getJobForQueue());
		            printMgr.addJob(prtDevice.getIP(),billPrint);
				}
			}
		}
	}
	
	@Override
	public void listPrinters() {
    	final Gson gson = new Gson();
    	
    	if(scheduler == null){
        	scheduler = Executors.newSingleThreadScheduledExecutor();
        }
    	
        //stop old finder
        while(true) {
                try{
                    Finder.stop();
                    break;
                }catch(EpsonIoException e){
                    if(e.getStatus() != IoStatus.ERR_PROCESSING){
                        break;
                    }
                }
         }
            
		//stop find thread
		if(future != null){
		    future.cancel(false);
		    while(!future.isDone()){
		        try{
		            Thread.sleep(500);
		        }catch(Exception e){
		            break;
		        }
		    }
		    future = null;
		}
		
		try {
			Finder.start(this.service.getBaseContext(), DevType.TCP, "255.255.255.255");
		} catch (EpsonIoException e1) {
			e1.printStackTrace();
		}

        //start thread
        future = scheduler.scheduleWithFixedDelay(new Runnable(){
			@Override
			public void run() {
		        String[] deviceList = null;
		        try{
		            deviceList = Finder.getResult();
		            if (deviceList != null && deviceList.length>0) {

		            	future.cancel(false);
		                future = null;
		                scheduler.shutdown();
		                scheduler = null;
		                Map<String, String> ret = new HashMap<String, String>();
		                for (int i=0; i<deviceList.length; i++) {
		                	ret.put(deviceList[i], getPrinterName(deviceList[i]));
		                }
		                synchronized (service.mCallbacks) {
		                	  for (IAlfredRemotePrintServiceCallback listener : service.mCallbacks) {
		                	    try {
		                	      listener.fromService("PRINTS_FOUND", gson.toJson(ret));
		                	    } catch (RemoteException e) {
		                	      Log.w("Printer Lookup", "Failed to notify listener " + listener, e);
		                	    }
		                	  }
		                }
		                
		            }
		        }catch(EpsonIoException e){
		        	e.printStackTrace();
		            return;
		        }					
			}
        }, 0, 500, TimeUnit.MILLISECONDS);
    }
    
    
    private String getPrinterName(String textIp) {
        String fontName = null;
        String printerName = null;
        EpsonIo port = null;
        String method = "";
        try{
        	byte[]		receiveBuffer	= new byte[RESPONSE_MAXBYTE];
        	int[]		receiveSize		= new int[1];
        	String[]	value			= new String[1];
        	Boolean		ret				= false;

            //open
            port = new EpsonIo();
            method = "open";
            port.open(DevType.TCP, textIp, null, this.service);

            clearReceiveBuffer( port );

            //send command(esc/pos)
            // enable printer
            method = "write";
            port.write( CMD_ESC_ENABLE_PRINTER, 0, CMD_ESC_ENABLE_PRINTER.length, SEND_RESPONSE_TIMEOUT );

            {	// printe name
            	method = "write";
            	port.write( CMD_GS_I_PRINTER_NAME, 0, CMD_GS_I_PRINTER_NAME.length, SEND_RESPONSE_TIMEOUT );

            	method = "read";
            	Arrays.fill( receiveBuffer, (byte)0 );

            	ret = receiveResponse( port, receiveBuffer, receiveSize );
            	if ((false != ret) && (0 < receiveSize[0])) {
                	byte[] response = Arrays.copyOf( receiveBuffer, receiveSize[0] );

                	analyzeResponse( response, value );
    
                	printerName = value[0];
            	}
            }
           
            {	// additional fonts
            	method = "write";
            	port.write( CMD_GS_I_ADDITIONAL_FONTS, 0, CMD_GS_I_ADDITIONAL_FONTS.length, SEND_RESPONSE_TIMEOUT );

            	method = "read";
            	Arrays.fill( receiveBuffer, (byte)0 );

            	ret = receiveResponse( port, receiveBuffer, receiveSize );
            	if ((false != ret) && (0 < receiveSize[0])) {
                	byte[] response = Arrays.copyOf( receiveBuffer, receiveSize[0] );

                	analyzeResponse( response, value );
               
                	fontName = value[0];
            	}
            }

            //close
            method = "close";
            port.close();
        }catch(Exception e){
            try{
                if(port != null){
                    port.close();
                    port = null;
                }
            }catch(Exception e1){
                port = null;
            }
            return null;
        }    
        return printerName;
    }

    //receive response
    private void clearReceiveBuffer( EpsonIo port ) throws EpsonIoException{
    	while (true) {
            try {
            	byte[] receiveBuffer = new byte[RESPONSE_MAXBYTE];
            	int readSize = 0;
                readSize = port.read( receiveBuffer, 0, receiveBuffer.length, 100 );
                if (0 == readSize) {
                	break;
                }
            } catch (EpsonIoException e) {
                if(e.getStatus() == IoStatus.ERR_TIMEOUT){
                	return ;
                }else{
                    throw e;
                }
            }
    	}
    }
    
    //receive response
    private Boolean receiveResponse(EpsonIo port, byte[] receiveBuffer, int[] readSize ) throws EpsonIoException{
        if ((null == receiveBuffer) || (0 >= receiveBuffer.length)) {
        	return false;
        }

        if ((null == readSize) || (0 >= readSize.length)) {
        	return false;
        }

        readSize[0] = 0;
        
        //receive
        try {
            readSize[0] = port.read( receiveBuffer, 0, receiveBuffer.length, SEND_RESPONSE_TIMEOUT );
        } catch (EpsonIoException e) {
            if(e.getStatus() == IoStatus.ERR_TIMEOUT){
            	return false;
            }else{
                throw e;
            }
        }

        return true;
    }
    
	private ArrayList<KotItemModifier> getModifiersByDetailId(int detailId, ArrayList<KotItemModifier> modsList) {
		ArrayList<KotItemModifier> result = new ArrayList<KotItemModifier>();
		for (KotItemModifier mod : modsList) {
			if (mod.getKotItemDetailId().intValue() == detailId) {
				result.add(mod);
			}
		}
		return result;
	}
	
	private ArrayList<KotItemModifier> getComboItemModifier(int printId, List<KotItemModifier> modsList) {
		ArrayList<KotItemModifier> result = new ArrayList<KotItemModifier>();
		for (KotItemModifier mod : modsList) {
			if (mod.getPrinterId().intValue() == printId) {
				result.add(mod);
			}
		}
		return result;
	}
	
	private ArrayList<KotItemModifier> getComboGeneralModifier(int printId, List<KotItemModifier> modsList) {
		ArrayList<KotItemModifier> result = new ArrayList<KotItemModifier>();
		for (KotItemModifier mod : modsList) {
			if (IntegerUtils.isEmptyOrZero(mod.getPrinterId().intValue())) {
				result.add(mod);
			}
		}
		return result;
	}
	
	
	
	private List<String> getModifierNameStr(ArrayList<KotItemModifier> modsList){
		ArrayList<String> result = new ArrayList<String>();
		for (KotItemModifier mod : modsList) {
			if(!IntegerUtils.isEmptyOrZero(mod.getModifierNum()) && mod.getModifierNum().intValue() > 1){
				result.add(mod.getModifierName() + "x" + mod.getModifierNum().intValue());
			}else{
				result.add(mod.getModifierName());
			}
			
		}
		return result;
	}
	
	private int getModifierSizehavePrintId(ArrayList<KotItemModifier> modsList){
		int i = 0;
		for(KotItemModifier kotItemModifier : modsList){
			if(!IntegerUtils.isEmptyOrZero(kotItemModifier.getPrinterId())){
				i++;
			}
		}
		return i;
	}
	
    private boolean analyzeResponse(byte[] response, String[] value ){
        int currentPos = 0;

        if ((null == value) || (0 >= value.length)) {
        	return false;
        }
        value[0] = "";

        //search 5f header
        for (currentPos = 0; currentPos < response.length; currentPos++) {
            if (response[currentPos] == RESPONSE_HEADER) {
            	currentPos++;
                break;
            }
        }

        if(currentPos >= response.length){
            return false;
        }

        // terminater check
        int endPos = 0;
        for (endPos = currentPos; endPos < response.length; endPos++) {
            if (response[endPos] == RESPONSE_TERMINAL) {
            	break;
            }
        }

        if (endPos == currentPos) {
        	return true;
        }

        //get response string
        String responseString = null;
        try {
            responseString = new String( response, currentPos, endPos - currentPos, "US-ASCII" );
        } catch (UnsupportedEncodingException e) {
            return false;
        }

        value[0] = responseString;

        return true;
    }	
	public void onStop() {

		if(future != null){
            future.cancel(false);
            while(!future.isDone()){
                try{
                    Thread.sleep(500);
                }catch(Exception e){
                    break;
                }
            }
            future = null;
        }
        
		if (scheduler != null)
		    scheduler.shutdown();		
	}

	@Override
	public void clearPrint() throws RemoteException {
		// TODO Auto-generated method stub
		this.service.getPrintMgr().stop();
		this.service.getPrintMgr().clear();
		this.service.stopSelf();
	}

	@Override
	public void configure(int country,int lang, int dollarsign) throws RemoteException {	
        this.service.configure(country, lang, dollarsign);		
	}

	@Override
	public void printKioskKOT(String printer, String summary, String detail,
			String modifiers, boolean oneprint, boolean doublePrint, String orderNo, int kotFontSize )
			throws RemoteException {
		Gson gson = new Gson();
		PrinterDevice prtDevice =  gson.fromJson(printer, PrinterDevice.class);
		KotSummary kotsummary =  gson.fromJson(summary, KotSummary.class);
		ArrayList<KotItemDetail> itemDetailsList = gson.fromJson(detail, new TypeToken<ArrayList<KotItemDetail>>(){}.getType());
		ArrayList<KotItemModifier> modifiersList = gson.fromJson(modifiers, new TypeToken<ArrayList<KotItemModifier>>(){}.getType());
		String model = prtDevice.getModel();
		
		PrintManager printJobMgr = this.service.getPrintMgr(); 
		PrinterQueueManager pqMgr = this.service.getPqMgr();		
		
		int copies = 1;
        if (doublePrint==true)
        	copies = 2;
        
        for (int i=0; i<copies ;i++) {
        	if (oneprint){
				if(printJobMgr != null){
						String uuid = pqMgr.getDataUUID(kotsummary.getKotIdString());
						
						KOTPrint kot = new KOTPrint(uuid, kotsummary.getBusinessDate());
					
				    	//set page size
				    	if (this.service.isTMU220(model)) {
				    		kot.setCharSize(33);
				    	} else {
				    		kot.setCharSize(48);
				    	}
				    	
						//kot.AddTitle(kotsummary.getRevenueCenterName(),kotsummary.getTableName());
				    	if (!TextUtils.isEmpty(orderNo))
				    		kot.AddKioskHeader(kotsummary, orderNo);
				    	else
				    		kot.AddKioskHeader(kotsummary, kotsummary.getOrderNoString());
				    	
						kot.setPrinterIp(prtDevice.getIP());
						if(!TextUtils.isEmpty(kotsummary.getDescription())){
							kot.addCenterLabel(kotsummary.getDescription(), kotFontSize);
						}
						if (itemDetailsList.size()>=0) {
							KotItemDetail kotitem = itemDetailsList.get(0);
							if (kotitem.getKotStatus().intValue() == ParamConst.KOT_STATUS_VOID) {
								 kot.addCenterLabel(PrintService.instance.getResources().getString(R.string.void_), kotFontSize);
							}
						}
						   
						kot.AddContentListHeader2Cols(PrintService.instance.getResources().getString(R.string.item_name),
								PrintService.instance.getResources().getString(R.string.qty));
						boolean canPrint = false;
						for (KotItemDetail item: itemDetailsList) {
						   kot.AddKotItem(item.getItemName(), item.getItemNum(), kotFontSize);
						   ArrayList<KotItemModifier> modList = getModifiersByDetailId(item.getId().intValue(), modifiersList);
						   int size = getModifierSizehavePrintId(modList);
						   if (size != 0){
							   canPrint = false;
							   for (KotItemModifier kotItemModifier : modList){
								   if (IntegerUtils.isEmptyOrZero(kotItemModifier.getPrinterId()) 
										   || kotItemModifier.getPrinterId().intValue() == prtDevice.getDevice_id()){
									   canPrint = true;
									   if(!IntegerUtils.isEmptyOrZero(kotItemModifier.getModifierNum()) && kotItemModifier.getModifierNum().intValue() > 1){
										   kot.AddModifierItem(kotItemModifier.getModifierName() + "x" + kotItemModifier.getModifierNum().intValue(), 1);
									   } else{
										   kot.AddModifierItem(kotItemModifier.getModifierName(), 1);
									   }
									   
								   }
							   }
						   }else{
							   canPrint = true;
							   List<String> mods = getModifierNameStr(modList);
							   for (String mod : mods) {
							       kot.AddModifierItem(mod, 1);
							   }
						   }
						   if (item.getSpecialInstractions() != null) {
								kot.AddModifierItem("*" + item.getSpecialInstractions() + "*", 1);
							}
						   kot.addLineSpace(1);
						}

						if (i==1) {
				          kot.AddFooter(PrintService.instance.getResources().getString(R.string.kot_copy));
						} else {
					      kot.AddFooter(TimeUtil.getTime());	
						}
						if (canPrint) {
							pqMgr.queuePrint(kot.getJobForQueue());
							printJobMgr.addJob(prtDevice.getIP(),kot);
						}
		        }
        	}else {
				if(printJobMgr != null){
					for (KotItemDetail item: itemDetailsList) {				
						ArrayList<KotItemModifier> modList = getModifiersByDetailId(item.getId().intValue(), modifiersList);
						int size = getModifierSizehavePrintId(modList);
						if (size != 0) {
							List<KotItemModifier> comboItems = getComboItemModifier(prtDevice.getDevice_id(), modList);
							List<KotItemModifier> comboGeneralModifiers = getComboGeneralModifier(prtDevice.getDevice_id(), modList);
							 for (KotItemModifier kotItemModifier : comboItems){
									String uuid = pqMgr.getDataUUID(kotsummary.getKotIdString());
									
									KOTPrint kot = new KOTPrint(uuid, kotsummary.getBusinessDate());								 

							    	//set page size
							    	if (this.service.isTMU220(model)) {
							    		kot.setCharSize(33);
							    	} else {
							    		kot.setCharSize(48);
							    	}						
									//kot.AddTitle(kotsummary.getRevenueCenterName(),kotsummary.getTableName());
							    	if(!TextUtils.isEmpty(kotsummary.getDescription())){
										kot.addCenterLabel(kotsummary.getDescription(), kotFontSize);
									}
							    	if (item.getKotStatus().intValue() == ParamConst.KOT_STATUS_VOID) {
										 kot.addCenterLabel(PrintService.instance.getResources().getString(R.string.void_), kotFontSize);
									}
							    	if (!TextUtils.isEmpty(orderNo))
							    		kot.AddKioskHeader(kotsummary, orderNo);
							    	else
							    		kot.AddKioskHeader(kotsummary, kotsummary.getOrderNoString());
									kot.setPrinterIp(prtDevice.getIP());
									kot.AddContentListHeader2Cols(PrintService.instance.getResources().getString(R.string.item_name), 
											PrintService.instance.getResources().getString(R.string.qty));
					
									kot.AddKotItem(item.getItemName(), item.getItemNum(), kotFontSize);
									if(!IntegerUtils.isEmptyOrZero(kotItemModifier.getModifierNum()) && kotItemModifier.getModifierNum().intValue() > 1){
										kot.AddModifierItem(kotItemModifier.getModifierName() + "x" + kotItemModifier.getModifierNum().intValue(), 1);
									} else {
										kot.AddModifierItem(kotItemModifier.getModifierName(), 1);
									}
									
									for(KotItemModifier kotGeneralItemModifier : comboGeneralModifiers){
										if(!IntegerUtils.isEmptyOrZero(kotGeneralItemModifier.getModifierNum()) && kotGeneralItemModifier.getModifierNum().intValue() > 1){
											kot.AddModifierItem(kotGeneralItemModifier.getModifierName() + "x" + kotGeneralItemModifier.getModifierNum().intValue(), 1);
										}else {
											kot.AddModifierItem(kotGeneralItemModifier.getModifierName(), 1);
										}
										
									}
									if (item.getSpecialInstractions() != null) {
										kot.AddModifierItem("*" + item.getSpecialInstractions() + "*", 1);
									}
									if (i==1) {
								          kot.AddFooter(PrintService.instance.getResources().getString(R.string.kot_copy));
									} else {
									      kot.AddFooter(TimeUtil.getTime());	
									}
									pqMgr.queuePrint(kot.getJobForQueue());
									printJobMgr.addJob(prtDevice.getIP(),kot);
							   }
							 
						} else {
							String uuid = pqMgr.getDataUUID(kotsummary.getKotIdString());
							
							KOTPrint kot = new KOTPrint(uuid, kotsummary.getBusinessDate());								
					    	//set page size
					    	if (this.service.isTMU220(model)) {
					    		kot.setCharSize(33);
					    	} else {
					    		kot.setCharSize(48);
					    	}						
							//kot.AddTitle(kotsummary.getRevenueCenterName(),kotsummary.getTableName());
					    	if(!TextUtils.isEmpty(kotsummary.getDescription())){
								kot.addCenterLabel(kotsummary.getDescription(), kotFontSize);
							}
					    	if (item.getKotStatus().intValue() == ParamConst.KOT_STATUS_VOID) {
								 kot.addCenterLabel(PrintService.instance.getResources().getString(R.string.void_), kotFontSize);
							}
					    	if (!TextUtils.isEmpty(orderNo))
					    		kot.AddKioskHeader(kotsummary, orderNo);
					    	else
					    		kot.AddKioskHeader(kotsummary, kotsummary.getOrderNoString());
							kot.setPrinterIp(prtDevice.getIP());
							kot.AddContentListHeader2Cols(PrintService.instance.getResources().getString(R.string.item_name), 
									PrintService.instance.getResources().getString(R.string.qty));
			
							kot.AddKotItem(item.getItemName(), item.getItemNum(), kotFontSize);
							List<String> mods = getModifierNameStr(modList);
							for (String mod : mods) {
								kot.AddModifierItem(mod);
							}
							if (item.getSpecialInstractions() != null) {
								kot.AddModifierItem("*" + item.getSpecialInstractions() + "*", 1);
							}
							if (i==1) {
						          kot.AddFooter(PrintService.instance.getResources().getString(R.string.kot_copy));
							} else {
							      kot.AddFooter(TimeUtil.getTime());	
							}
							pqMgr.queuePrint(kot.getJobForQueue());
							printJobMgr.addJob(prtDevice.getIP(),kot);
						}
						
						//kot.AddFooter(kotsummary.getRevenueCenterName());
//						if (canPrint)
				        
					}
				}        	
	        }
        }

		
	}

	@Override
	public void printKioskBill(String printer, String title, String order,
			String orderDetail, String modifiers, String tax, String payment,
			boolean doubleprint, boolean doubleReceipts, String rounding, String orderNo) throws RemoteException {
		
		Gson gson = new Gson();
		boolean isCashSettlement = false;

		PrinterDevice prtDevice =  gson.fromJson(printer, PrinterDevice.class);
		PrinterTitle prtTitle =  gson.fromJson(title, PrinterTitle.class);
		Order theOrder =  gson.fromJson(order, Order.class);

		String model = prtDevice.getModel();
        
		ArrayList<PrintOrderItem> printOrderItemList = gson.fromJson(orderDetail, 
				new TypeToken<ArrayList<PrintOrderItem>>(){}.getType());	
		
		ArrayList<PrintOrderModifier> orderModifiers = gson.fromJson(modifiers, 
														new TypeToken<ArrayList<PrintOrderModifier>>(){}.getType());
		
		List<Map<String, String>> taxes = gson.fromJson(tax, 
														new TypeToken<List<Map<String, String>>>(){}.getType());
		
		List<PrintReceiptInfo> settlement = gson.fromJson(payment,
				   										new TypeToken<List<PrintReceiptInfo>>(){}.getType());
		
		PrintManager printMgr = this.service.getPrintMgr();
		JobManager printJobMgr = printMgr.configureJobManager(prtDevice.getIP());
		PrinterQueueManager pqMgr = this.service.getPqMgr();
		
		if (doubleprint && settlement == null) {
			//double bill print
			if(printJobMgr != null){
				for (int i=0;i<2; i++) {
					String uuid = pqMgr.getDataUUID(prtTitle.getBill_NO());
					
			    	BillPrint billPrint = new BillPrint(uuid, Long.valueOf(prtTitle.getBizDate()));					

			    	billPrint.setPrinterIp(prtDevice.getIP());
			    	
			    	//set page size
			    	if (this.service.isTMU220(model)) {
			    		billPrint.setCharSize(33);
			    		//U220 cannot support image print
				    	billPrint.AddRestaurantInfo(null, 
				    								prtTitle.getRestaurantName(), 
				    								prtTitle.getAddressDetail(), null);			    		
			    	} else {
			    		billPrint.setCharSize(48);
				    	billPrint.AddRestaurantInfo(prtTitle.getLogo(), 
				    								prtTitle.getRestaurantName(), 
				    								prtTitle.getAddressDetail(), null);
			    	}
			    	
			    	if(!TextUtils.isEmpty(orderNo))
			    		billPrint.AddOrderNo(theOrder.getIsTakeAway(), orderNo);
			    	billPrint.AddKioskHeader(prtTitle.getTableName(), theOrder.getPersons(), 
			    							prtTitle.getBill_NO(), prtTitle.getPos(),
			    							prtTitle.getOp(), prtTitle.getDate()+" "+prtTitle.getTime(), prtTitle.getOrderNo());
			    	
			    	billPrint.AddContentListHeader(PrintService.instance.getResources().getString(R.string.item), 
			    			PrintService.instance.getResources().getString(R.string.price), 
			    			PrintService.instance.getResources().getString(R.string.qty), 
			    			PrintService.instance.getResources().getString(R.string.total));//("Item Name", "QTY");
					
			    	
			    	for(PrintOrderItem item:printOrderItemList) {
						billPrint.AddOrderItem(item.getItemName(), item.getPrice(), 
												item.getQty(), item.getAmount(), 1, item.getWeight());
						//getModifiersByDetailId()
						//// 
						if (orderModifiers !=null) {
							for (int m=0; m<orderModifiers.size(); m++) {
								PrintOrderModifier om =  orderModifiers.get(m);
								if (om.getOrderDetailId() == item.getOrderDetailId()) {
									if(om.getQty() > 1){
										billPrint.addOrderModifier(om.getItemName() + "x" + om.getQty(),1);
									}else{
										billPrint.addOrderModifier(om.getItemName(),1);
									}
								}
							}
						}
					}
	
		////////////// Bill Summary
					String subTotal = BH.doubleFormat.format(BH.getBD(theOrder.getSubTotal()));
		            String discount = BH.doubleFormat.format(BH.getBD(theOrder.getDiscountAmount()));
		            String grandTotal = BH.doubleFormat.format(BH.getBD(theOrder.getTotal()));
					
		            billPrint.AddBillSummary(subTotal, discount, taxes, grandTotal, rounding);
		            billPrint.addCustomizedFieldAtFooter(prtTitle.getFooterOptions());
		            billPrint.AddFooter(PrintService.instance.getResources().getString(R.string.powered_by_alfred), true);
		            pqMgr.queuePrint(billPrint.getJobForQueue());
		            printMgr.addJob(prtDevice.getIP(), billPrint);
				}
			}
		} else {
			if(printJobMgr != null){
				int receiptCopy =1;
				
				if (doubleReceipts && (settlement != null))
					receiptCopy =2; 
				
				for (int i=0; i<receiptCopy;i++){
					String uuid = pqMgr.getDataUUID(prtTitle.getBill_NO());
					
			    	BillPrint billPrint = new BillPrint(uuid, Long.valueOf(prtTitle.getBizDate()));
			    	billPrint.setPrinterIp(prtDevice.getIP());
			    	//set page size
			    	if (this.service.isTMU220(model)) {
			    		billPrint.setCharSize(33);
				    	billPrint.AddRestaurantInfo(null, 
				    								prtTitle.getRestaurantName(), 
				    								prtTitle.getAddressDetail(), null);			    		
			    	} else {
			    		billPrint.setCharSize(48);
				    	billPrint.AddRestaurantInfo(prtTitle.getLogo(), 
				    								prtTitle.getRestaurantName(), 
				    								prtTitle.getAddressDetail(), null);

			    	}
			    	if(!TextUtils.isEmpty(orderNo))
			    		billPrint.AddOrderNo(theOrder.getIsTakeAway(), orderNo);
			    	billPrint.AddKioskHeader(prtTitle.getTableName(), theOrder.getPersons(), 
			    							prtTitle.getBill_NO(), prtTitle.getPos(),
			    							prtTitle.getOp(), prtTitle.getDate()+" "+prtTitle.getTime(), prtTitle.getOrderNo());
			    	
			    	billPrint.AddContentListHeader(PrintService.instance.getResources().getString(R.string.item), 
			    			PrintService.instance.getResources().getString(R.string.price), 
			    			PrintService.instance.getResources().getString(R.string.qty), 
			    			PrintService.instance.getResources().getString(R.string.total));
			    	

			    	for(PrintOrderItem item:printOrderItemList) {
						billPrint.AddOrderItem(item.getItemName(), item.getPrice(), 
												item.getQty(), item.getAmount(), 1, item.getWeight());
						////add modifier print
						if (orderModifiers !=null) {
							for (int m=0; m<orderModifiers.size(); m++) {
								PrintOrderModifier om =  orderModifiers.get(m);
								if (om.getOrderDetailId() == item.getOrderDetailId()) {
									billPrint.addOrderModifier(om.getItemName(),1);
								}
							}	
						}
					}
		
		////////////// Bill Summary
					String subTotal = BH.doubleFormat.format(BH.getBD(theOrder.getSubTotal()));
		            String discount = BH.doubleFormat.format(BH.getBD(theOrder.getDiscountAmount()));
		            String grandTotal = BH.doubleFormat.format(BH.getBD(theOrder.getTotal()));
					
		            billPrint.AddBillSummary(subTotal, discount, taxes, grandTotal, rounding);
		            List<LinkedHashMap<String, String>> stmtList = new ArrayList<LinkedHashMap<String,String>>();
					if (settlement != null) {
						String paymentType = "";
						String cardNo = null;
						for(PrintReceiptInfo printReceiptInfo : settlement){
							 LinkedHashMap<String, String> stmt = new LinkedHashMap<String, String>();
							switch (printReceiptInfo.getPaymentTypeId()) {
							case ParamConst.SETTLEMENT_TYPE_CASH:
								if (!TextUtils.isEmpty(printReceiptInfo.getPaidAmount()) && BH.getBD(printReceiptInfo.getPaidAmount()).compareTo(BH.getBD(ParamConst.DOUBLE_ZERO)) > 0) {
									stmt.put(PrintService.instance.getResources().getString(R.string.cash), BH.add(BH.getBD(printReceiptInfo.getPaidAmount()), BH.getBD(printReceiptInfo.getCashChange()),true).toString());
									stmt.put(PrintService.instance.getResources().getString(R.string.changes), BH.getBD(printReceiptInfo.getCashChange()).toString());
									isCashSettlement = true;
								}
								if (isCashSettlement && i==0) {
									this.kickCashDrawer(printer);
								}
								break;
							case ParamConst.SETTLEMENT_TYPE_MASTERCARD:
								paymentType = PrintService.instance.getResources().getString(R.string.mastercard);
								break;
							case ParamConst.SETTLEMENT_TYPE_UNIPAY:
								paymentType = PrintService.instance.getResources().getString(R.string.unionpay);
								break;
							case ParamConst.SETTLEMENT_TYPE_VISA:
								paymentType = PrintService.instance.getResources().getString(R.string.visa);
								break;
							case ParamConst.SETTLEMENT_TYPE_AMEX:
								paymentType = PrintService.instance.getResources().getString(R.string.amex_);
								break;
							case ParamConst.SETTLEMENT_TYPE_JCB:
								paymentType = PrintService.instance.getResources().getString(R.string.jcb);
								break;
							case ParamConst.SETTLEMENT_TYPE_DINNER_INTERMATIONAL:
								paymentType = PrintService.instance.getResources().getString(R.string.dinner_intern);
								break;
							case ParamConst.SETTLEMENT_TYPE_BILL_ON_HOLD:
								paymentType = PrintService.instance.getResources().getString(R.string.hold_bill);
								break;
							case ParamConst.SETTLEMENT_TYPE_COMPANY:
								paymentType = PrintService.instance.getResources().getString(R.string.com_credits);
								break;
							case ParamConst.SETTLEMENT_TYPE_HOURS_CHARGE:
								paymentType = PrintService.instance.getResources().getString(R.string.house_charge);
								break;
							case ParamConst.SETTLEMENT_TYPE_VOID:
								paymentType = PrintService.instance.getResources().getString(R.string._void);
								break;
							case ParamConst.SETTLEMENT_TYPE_ENTERTAINMENT:
								paymentType = PrintService.instance.getResources().getString(R.string.ent);
								break;
							case ParamConst.SETTLEMENT_TYPE_NETS:
								paymentType = PrintService.instance.getResources().getString(R.string.nets);
								break;
							case ParamConst.SETTLEMENT_TYPE_ALIPAY:
								paymentType = PrintService.instance.getResources().getString(R.string.alipay);
								break;
							case ParamConst.SETTLEMENT_TYPE_WEIXIN:
								paymentType = PrintService.instance.getResources().getString(R.string.weixin);
								break;
							case ParamConst.SETTLEMENT_TYPE_PAYPAL:
								paymentType = PrintService.instance.getResources().getString(R.string.paypal);
								break;
							case ParamConst.SETTLEMENT_TYPE_STORED_CARD:
								paymentType = PrintService.instance.getResources().getString(R.string.stored_card);
								break;
							}
							if (!TextUtils.isEmpty(paymentType)) {
								stmt.put(paymentType,
										printReceiptInfo.getPaidAmount());
							}
							if (!TextUtils
									.isEmpty(printReceiptInfo.getCardNo())) {
								stmt.put(PrintService.instance.getResources().getString(R.string.card_no),
										"**** " + printReceiptInfo.getCardNo());
							}
							stmtList.add(stmt);
						}

						billPrint.AddSettlementDetails(stmtList);
						billPrint.addCustomizedFieldAtFooter(prtTitle.getFooterOptions());
						//print check clo
						billPrint.addCloseBillDate();
						billPrint.addWelcomeMsg();
					}			
		/////////
		            billPrint.AddFooter(PrintService.instance.getResources().getString(R.string.powered_by_alfred), false);
		            pqMgr.queuePrint(billPrint.getJobForQueue());
		            printMgr.addJob(prtDevice.getIP(), billPrint);
				}
			}
		}

		
	}

	@Override
	public void printBillSummary(String printer, String summary, String detail,
			String modifiers, int kotFontSize) throws RemoteException {
		// TODO Auto-generated method stub
		Gson gson = new Gson();
		
		PrinterDevice prtDevice =  gson.fromJson(printer, PrinterDevice.class);
		KotSummary kotsummary =  gson.fromJson(summary, KotSummary.class);
		ArrayList<KotItemDetail> itemDetailsList = gson.fromJson(detail, new TypeToken<ArrayList<KotItemDetail>>(){}.getType());
		ArrayList<KotItemModifier> modifiersList = gson.fromJson(modifiers, new TypeToken<ArrayList<KotItemModifier>>(){}.getType());

		PrintManager printMgr = this.service.getPrintMgr();
		JobManager printJobMgr = printMgr.configureJobManager(prtDevice.getIP());
		PrinterQueueManager pqMgr = this.service.getPqMgr();
		
		String model = prtDevice.getModel();
        boolean oneprint = true;
        
        if (oneprint){
				if(printJobMgr != null){			
					  String uuid = pqMgr.getDataUUID(kotsummary.getOrderNoString());
						
						KOTPrint kot = new KOTPrint(uuid, kotsummary.getBusinessDate());
				    	//set page size
				    	if (this.service.isTMU220(model)) {
				    		kot.setCharSize(33);
				    	} else {
				    		kot.setCharSize(48);
				    	}
						kot.AddTitle(kotsummary.getRevenueCenterName(),kotsummary.getTableName());
						kot.addCenterLabel(PrintService.instance.getResources().getString(R.string.print_order_summary), 1);
						if(!TextUtils.isEmpty(kotsummary.getDescription())){
							kot.addCenterLabel(kotsummary.getDescription(), kotFontSize);
						}
						if (itemDetailsList.size()>=0) {
							KotItemDetail kotitem = itemDetailsList.get(0);
							if (kotitem.getKotStatus().intValue() == ParamConst.KOT_STATUS_VOID) {
								 kot.addCenterLabel(PrintService.instance.getResources().getString(R.string.void_), kotFontSize);
							}
						}
						kot.AddHeader(kotsummary.getIsTakeAway(), kotsummary.getOrderNoString());
						kot.setPrinterIp(prtDevice.getIP());
						kot.AddContentListHeader2Cols(PrintService.instance.getResources().getString(R.string.item_name),
								PrintService.instance.getResources().getString(R.string.qty));
						boolean canPrint = false;
						for (KotItemDetail item: itemDetailsList) {
						   kot.AddKotItem(item.getItemName(), item.getItemNum(), kotFontSize);
						   ArrayList<KotItemModifier> modList = getModifiersByDetailId(item.getId().intValue(), modifiersList);
//						   int size = getModifierSizehavePrintId(modList);
//						   if (size != 0){
//							   canPrint = false;
//							   for (KotItemModifier kotItemModifier : modList){
//								   if (IntegerUtils.isEmptyOrZero(kotItemModifier.getPrinterId()) 
//										   || kotItemModifier.getPrinterId().intValue() == prtDevice.getDevice_id()){
//									   canPrint = true;
//									   kot.AddModifierItem(kotItemModifier.getModifierName(),kotFontSize);
//									   
//								   }
//							   }
//						   }else{
							   canPrint = true;
							   List<String> mods = getModifierNameStr(modList);
							   for (String mod : mods) {
							       kot.AddModifierItem(mod,1);
							   }
//						   }
							if (item.getSpecialInstractions() != null) {
								kot.AddModifierItem("*" + item.getSpecialInstractions() + "*",1);
							}	
							kot.addLineSpace(1);
						}

					    kot.AddFooter(TimeUtil.getTime());	
						if (canPrint) {
				            pqMgr.queuePrint(kot.getJobForQueue());
							printMgr.addJob(prtDevice.getIP(), kot);
						}
		        }
        	}	
	}
	
	//  modifier print
	@Override
	public void printModifierDetailAnalysisReport(String xzType, String printer,
										String title, String pluMod,String category)
			throws RemoteException{
		Gson gson = new Gson();

		PrinterDevice prtDevice =  gson.fromJson(printer, PrinterDevice.class);
		PrinterTitle prtTitle =  gson.fromJson(title, PrinterTitle.class);
		ArrayList<Modifier> categoryData = gson.fromJson(category, new TypeToken<ArrayList<Modifier>>(){}.getType());
		ArrayList<ReportPluDayModifier> modifier = gson.fromJson(pluMod, new TypeToken<ArrayList<ReportPluDayModifier>>(){}.getType());

		PrintManager printMgr = this.service.getPrintMgr();
		JobManager printJobMgr = printMgr.configureJobManager(prtDevice.getIP());
		PrinterQueueManager pqMgr = this.service.getPqMgr();
		
		if (printJobMgr != null) {
			String uuid = pqMgr.getDataUUID(prtTitle.getBill_NO());
			
			ModifierDetailAnalysisReportPrint daPrint = new ModifierDetailAnalysisReportPrint(uuid, TimeUtil.getPrintingLongDate(prtTitle.getBizDate()));
			String model = prtDevice.getModel();
	    	//set page size
	    	if (this.service.isTMU220(model)) {
	    		daPrint.setCharSize(33);
	    	} else {
	    		daPrint.setCharSize(48);
	    	}			
			daPrint.AddReportHeader(prtTitle.getRestaurantName(), xzType, PrintService.instance.getResources().getString(R.string.modifier_analysis));
			daPrint.AddHeader(prtTitle.getOp(), prtTitle.getBill_NO(), prtTitle.getDate()+" "+prtTitle.getTime(), prtTitle.getBizDate());
			daPrint.AddContentListHeader(PrintService.instance.getResources().getString(R.string.plu_name), 
					PrintService.instance.getResources().getString(R.string.price), 
					PrintService.instance.getResources().getString(R.string.qty), 
					PrintService.instance.getResources().getString(R.string.amount));
			daPrint.print(modifier, categoryData);
			daPrint.setPrinterIp(prtDevice.getIP());
			daPrint.AddFooter(prtTitle.getDate()+" "+prtTitle.getTime());
			pqMgr.queuePrint(daPrint.getJobForQueue());
			printMgr.addJob(prtDevice.getIP(),daPrint);
		}
	}
	
//  modifier print
	@Override
	public void printComboDetailAnalysisReport(String xzType, String printer,
										String title, String plu, String pluMod)
			throws RemoteException{
		Gson gson = new Gson();

		PrinterDevice prtDevice =  gson.fromJson(printer, PrinterDevice.class);
		PrinterTitle prtTitle =  gson.fromJson(title, PrinterTitle.class);
		ArrayList<ReportPluDayItem> item = gson.fromJson(plu, new TypeToken<ArrayList<ReportPluDayItem>>(){}.getType());
		ArrayList<ReportPluDayComboModifier> modifier = gson.fromJson(pluMod, new TypeToken<ArrayList<ReportPluDayComboModifier>>(){}.getType());

		PrintManager printMgr = this.service.getPrintMgr();
		JobManager printJobMgr = printMgr.configureJobManager(prtDevice.getIP());
		PrinterQueueManager pqMgr = this.service.getPqMgr();
		
		if (printJobMgr != null) {
			String uuid = pqMgr.getDataUUID(prtTitle.getBill_NO());
			ComboDetailAnalysisReportPrint daPrint = new ComboDetailAnalysisReportPrint(uuid, TimeUtil.getPrintingLongDate(prtTitle.getBizDate()));
			String model = prtDevice.getModel();
	    	//set page size
	    	if (this.service.isTMU220(model)) {
	    		daPrint.setCharSize(33);
	    	} else {
	    		daPrint.setCharSize(48);
	    	}			
			daPrint.AddReportHeader(prtTitle.getRestaurantName(), xzType, PrintService.instance.getResources().getString(R.string.modifier_analysis));
			daPrint.AddHeader(prtTitle.getOp(), prtTitle.getBill_NO(), prtTitle.getDate()+" "+prtTitle.getTime(), prtTitle.getBizDate());
			daPrint.AddContentListHeader(PrintService.instance.getResources().getString(R.string.plu_name), 
					PrintService.instance.getResources().getString(R.string.price), 
					PrintService.instance.getResources().getString(R.string.qty), 
					PrintService.instance.getResources().getString(R.string.amount));
			daPrint.print(item, modifier);
			daPrint.setPrinterIp(prtDevice.getIP());
			daPrint.AddFooter(prtTitle.getDate()+" "+prtTitle.getTime());
			pqMgr.queuePrint(daPrint.getJobForQueue());
			printMgr.addJob(prtDevice.getIP(),daPrint);
		}
	}

	@Override
	public void printMonthlySaleReport(String printer, String title, int year, int month,
			String saleData) throws RemoteException {
		// TODO Auto-generated method stub
		Gson gson = new Gson();

		PrinterDevice prtDevice =  gson.fromJson(printer, PrinterDevice.class);
		PrinterTitle prtTitle =  gson.fromJson(title, PrinterTitle.class);
		ArrayList<MonthlySalesReport> items = gson.fromJson(saleData, new TypeToken<ArrayList<MonthlySalesReport>>(){}.getType());
		PrintManager printMgr = this.service.getPrintMgr();
		JobManager printJobMgr = printMgr.configureJobManager(prtDevice.getIP());
		PrinterQueueManager pqMgr = this.service.getPqMgr();
		if (printJobMgr != null) {
			int totalDays = TimeUtil.getTotalDaysInMonth(year, month);
			
			String uuid = pqMgr.getDataUUID(prtTitle.getBill_NO());
			MonthlySalesReportPrint daPrint = new MonthlySalesReportPrint(uuid);
			String model = prtDevice.getModel();
	    	//set page size
	    	if (this.service.isTMU220(model)) {
	    		daPrint.setCharSize(33);
	    	} else {
	    		daPrint.setCharSize(48);
	    	}			
			daPrint.AddReportHeader(prtTitle.getRestaurantName(), null, PrintService.instance.getResources().getString(R.string.monthly_sales));
			daPrint.AddHeader(prtTitle.getOp(), year+"-"+month+"-01", year+"-"+month+"-"+totalDays);
			daPrint.print(items);
			daPrint.setPrinterIp(prtDevice.getIP());
			daPrint.AddFooter(prtTitle.getDate()+" "+prtTitle.getTime());
			pqMgr.queuePrint(daPrint.getJobForQueue());
			printMgr.addJob(prtDevice.getIP(),daPrint);
		}
		
	}
	

	@Override
	public void printMonthlyPLUReport(String printer, String title,  int year, int month, String plu)
			throws RemoteException{
		Gson gson = new Gson();

		PrinterDevice prtDevice =  gson.fromJson(printer, PrinterDevice.class);
		PrinterTitle prtTitle =  gson.fromJson(title, PrinterTitle.class);
		ArrayList<MonthlyPLUReport> pluData =  gson.fromJson(plu, new TypeToken<ArrayList<MonthlyPLUReport>>(){}.getType());

		PrintManager printMgr = this.service.getPrintMgr();
		JobManager printJobMgr = printMgr.configureJobManager(prtDevice.getIP());
		PrinterQueueManager pqMgr = this.service.getPqMgr();

		if (printJobMgr != null) {
			int totalDays = TimeUtil.getTotalDaysInMonth(year, month);
			String uuid = pqMgr.getDataUUID(prtTitle.getBill_NO());
			DetailAnalysisReportPrint daPrint 
						= new DetailAnalysisReportPrint(uuid, 0L);
			String model = prtDevice.getModel();
	    	//set page size
	    	if (this.service.isTMU220(model)) {
	    		daPrint.setCharSize(33);
	    	} else {
	    		daPrint.setCharSize(48);
	    	}			
			daPrint.AddReportHeader(prtTitle.getRestaurantName(), null, PrintService.instance.getResources().getString(R.string.monthly_plu));
			daPrint.AddHeader(prtTitle.getOp(), year+"-"+month+"-01", year+"-"+month+"-"+totalDays);
			daPrint.AddContentListHeader(PrintService.instance.getResources().getString(R.string.plu_name), 
					PrintService.instance.getResources().getString(R.string.price), 
					PrintService.instance.getResources().getString(R.string.qty), 
					PrintService.instance.getResources().getString(R.string.amount));
			daPrint.print(pluData);
			daPrint.setPrinterIp(prtDevice.getIP());
			daPrint.AddFooter(prtTitle.getDate()+" "+prtTitle.getTime());
			pqMgr.queuePrint(daPrint.getJobForQueue());
			printMgr.addJob(prtDevice.getIP(),daPrint);
		}
	}


	@Override
	public void printStoredCardConsume(String printer, String title, String time, String cardNo, String action, String actionAmount, String balance) throws RemoteException {
		Gson gson = new Gson();
		PrinterDevice prtDevice =  gson.fromJson(printer, PrinterDevice.class);
		PrintManager printMgr = this.service.getPrintMgr();
		JobManager printJobMgr = printMgr.configureJobManager(prtDevice.getIP());
		PrinterQueueManager pqMgr = this.service.getPqMgr();
		if(printJobMgr != null) {
			String uuid = pqMgr.getDataUUID(cardNo);
			StoredCardPrint storedCardPrint = new StoredCardPrint(uuid, 0L);
			String model = prtDevice.getModel();
			//set page size
			if (this.service.isTMU220(model)) {
				storedCardPrint.setCharSize(33);
			} else {
				storedCardPrint.setCharSize(48);
			}
			storedCardPrint.AddTitle(title);
			storedCardPrint.AddItem("Time:     ", time);
			storedCardPrint.AddItem("Card no.    ******", cardNo);
			storedCardPrint.AddItem(action + " amount :   $", BH.getBD(actionAmount).toString());
			storedCardPrint.AddItem("Balance amount :   $", BH.getBD(balance).toString());
			storedCardPrint.AddFooter("Powered by Alfred");
			storedCardPrint.setPrinterIp(prtDevice.getIP());
			pqMgr.queuePrint(storedCardPrint.getJobForQueue());
			printMgr.addJob(prtDevice.getIP(),storedCardPrint);
		}
	}
}
