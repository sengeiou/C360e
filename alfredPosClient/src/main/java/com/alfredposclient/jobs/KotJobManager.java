package com.alfredposclient.jobs;

import android.content.Context;

import com.alfredbase.BaseActivity;
import com.alfredbase.ParamConst;
import com.alfredbase.global.CoreData;
import com.alfredbase.javabean.KotItemDetail;
import com.alfredbase.javabean.KotItemModifier;
import com.alfredbase.javabean.KotSummary;
import com.alfredbase.javabean.Order;
import com.alfredbase.javabean.OrderBill;
import com.alfredbase.javabean.OrderDetail;
import com.alfredbase.javabean.OrderModifier;
import com.alfredbase.javabean.Printer;
import com.alfredbase.javabean.Tables;
import com.alfredbase.javabean.model.KDSDevice;
import com.alfredbase.javabean.model.PrinterDevice;
import com.alfredbase.store.sql.KotItemDetailSQL;
import com.alfredbase.store.sql.KotItemModifierSQL;
import com.alfredbase.store.sql.KotSummarySQL;
import com.alfredbase.store.sql.OrderBillSQL;
import com.alfredbase.store.sql.OrderDetailSQL;
import com.alfredbase.store.sql.OrderModifierSQL;
import com.alfredbase.store.sql.OrderSQL;
import com.alfredbase.store.sql.TablesSQL;
import com.alfredbase.utils.ObjectFactory;
import com.alfredposclient.activity.MainPage;
import com.alfredposclient.global.App;
import com.path.android.jobqueue.JobManager;
import com.path.android.jobqueue.config.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KotJobManager {

	private JobManager kotJobManager;
	private Context context;

	public KotJobManager(Context mContext) {
		super();
		this.context = mContext;
		configureJobManager();
	}

	private void configureJobManager() {
		Configuration kotconfiguration = new Configuration.Builder(context)
				.customLogger(new AlfredJobLogger("KOT_JOBS")).id("kot_jobs")
				.minConsumerCount(1) // always keep at least one consumer alive
				.maxConsumerCount(3) // up to 3 consumers at a time
				.loadFactor(3) // 3 jobs per consumer
				.consumerKeepAlive(120) // wait 2 minute
				.build();
		this.kotJobManager = new JobManager(this.context, kotconfiguration);
	}

	/* convert KOT to kot jobs */
	public void tearDownKot(KotSummary kotSummary,
			ArrayList<KotItemDetail> kot, ArrayList<KotItemModifier> modifiers,
			String method, Map<String, Object> orderMap) {

		ArrayList<Integer> printerGrougIds = new ArrayList<Integer>();
		// map printergroudId to Kot: Group ID --> Details
		Map<Integer, ArrayList<KotItemDetail>> kots = new HashMap<Integer, ArrayList<KotItemDetail>>();
		// map printerGroudId to Modifiers
		Map<Integer, ArrayList<KotItemModifier>> mods = new HashMap<Integer, ArrayList<KotItemModifier>>();

		for (KotItemDetail items : kot) {
			Integer pgid = items.getPrinterGroupId();
			int kotItemDetailId = items.getId().intValue();

			// Get all Group ids that KOT blongs to
			if (!printerGrougIds.contains(pgid))
				printerGrougIds.add(pgid);

			// kot
			if (kots.containsKey(pgid)) {
				ArrayList<KotItemDetail> tmp = kots.get(pgid);
				tmp.add(items);
			} else {
				ArrayList<KotItemDetail> tmp = new ArrayList<KotItemDetail>();
				tmp.add(items);
				kots.put(pgid, tmp);
			}

			// modifier
			if (mods.containsKey(pgid)) {
				ArrayList<KotItemModifier> tmp = mods.get(pgid);
				for (KotItemModifier mof : modifiers) {
					if (mof.getKotItemDetailId().intValue() == kotItemDetailId) {
						tmp.add(mof);
					}
				}
			} else {
				ArrayList<KotItemModifier> tmp = new ArrayList<KotItemModifier>();
				for (KotItemModifier mof : modifiers) {
					if (mof.getKotItemDetailId().intValue() == kotItemDetailId) {
						tmp.add(mof);
					}
				}
				mods.put(items.getPrinterGroupId(), tmp);
			}
		}
		if(printerGrougIds !=null && printerGrougIds.size() > 0){
			kotSummary.setStatus(ParamConst.KOTS_STATUS_UNDONE);
			KotSummarySQL.updateKotSummaryStatusById(ParamConst.KOTS_STATUS_UNDONE, kotSummary.getId().intValue());
		}

		// add job to send it to KDS
		for (Integer prgid : printerGrougIds) {
			ArrayList<Printer> printers = CoreData.getInstance()
					.getPrintersInGroup(prgid.intValue());
			for (Printer prnt : printers) {
				// KDS device
				KDSDevice kds1 = App.instance.getKDSDevice(prnt.getId());
				if (kds1 != null) {
					KotJob kotjob = new KotJob(kds1, kotSummary,
							kots.get(prgid), mods.get(prgid), method, orderMap);
					kotJobManager.addJob(kotjob);
				}
				// physical printer
				PrinterDevice prntd = App.instance.getPrinterDeviceById(prnt
						.getId());
				if(prntd != null){
					prntd.setGroupId(prgid.intValue());
					boolean printed = App.instance.remoteKotPrint(prntd,
							kotSummary, kots.get(prgid), mods.get(prgid));
					BaseActivity context = App.getTopActivity();
					if (printed) {
						List<Integer> orderDetailIds = (List<Integer>) orderMap
								.get("orderDetailIds");
						if (orderDetailIds.size() != 0) {
							ArrayList<OrderDetail> orderDetails = new ArrayList<OrderDetail>();
							synchronized (orderDetails) {
								for (int i = 0; i < orderDetailIds.size(); i++) {
									OrderDetail orderDetail = OrderDetailSQL
											.getOrderDetail(orderDetailIds
													.get(i));
									orderDetail
											.setOrderDetailStatus(ParamConst.ORDERDETAIL_STATUS_KOTPRINTERD);
									orderDetails.add(orderDetail);
								}
							}
							OrderDetailSQL.addOrderDetailList(orderDetails);
							context.kotPrintStatus(MainPage.KOT_PRINT_SUCCEED,
									orderMap.get("orderId"));
						}
					} else {
						context.kotPrintStatus(MainPage.KOT_PRINT_FAILED,
								orderMap.get("orderId"));
					}
				}
			}

		}
	}

	/* convert KOT to kot jobs */
	public void transferTableDownKot(String action, KotSummary toKotSummary,
			KotSummary fromKotSummary, Map<String, Object> orderMap) {

		List<Integer> printerGrougIds = KotItemDetailSQL
				.getPrinterGroupIds(fromKotSummary);
		// add job to send it to KDS
		for (Integer prgid : printerGrougIds) {
			ArrayList<Printer> printers = CoreData.getInstance()
					.getPrintersInGroup(prgid.intValue());
			for (Printer prnt : printers) {
				KDSDevice kds1 = App.instance.getKDSDevice(prnt.getId());
				if (kds1 != null) {
					KotJob kotjob = new KotJob(kds1, action, toKotSummary,
							fromKotSummary, orderMap);
					kotJobManager.addJob(kotjob);
				}
				boolean ret = transferItemToPrinter(fromKotSummary,
						toKotSummary, orderMap);
				if (!ret) {
					App.getTopActivity().kotPrintStatus(
							MainPage.KOT_PRINT_FAILED, null);
				}
			}
		}
	}

	/* transfer item to printer */
	private boolean transferItemToPrinter(KotSummary fromKotSummary,
			KotSummary toKotSummary, Map<String, Object> orderMap) {
		BaseActivity context = App.getTopActivity();
		ArrayList<Integer> printerGrougIds = new ArrayList<Integer>();
		KotSummary printKotSummary = null ;
		// map printergroudId to Kot: Group ID --> Details
		Map<Integer, ArrayList<KotItemDetail>> kots = new HashMap<Integer, ArrayList<KotItemDetail>>();
		// map printerGroudId to Modifiers
		Map<Integer, ArrayList<KotItemModifier>> mods = new HashMap<Integer, ArrayList<KotItemModifier>>();
		List<KotItemModifier> kotItemModifiers = new ArrayList<KotItemModifier>();
		List<KotItemDetail> kotItemDetails = new ArrayList<KotItemDetail>();
		String transferAction = (String) orderMap.get("action");
		if (ParamConst.JOB_MERGER_KOT.equals(transferAction)) {

			Order oldOrder = (Order) orderMap.get("fromOrder");

			Tables currentTable = TablesSQL.getTableById((Integer) orderMap
					.get("currentTableId"));
			kotItemDetails = KotItemDetailSQL
					.getKotItemDetailBySummaryId(fromKotSummary.getId());
			kotItemModifiers = new ArrayList<KotItemModifier>();
			for (KotItemDetail kotItemDetail : kotItemDetails) {
				kotItemDetail.setKotSummaryId(toKotSummary.getId());
				KotItemDetailSQL.update(kotItemDetail);
				kotItemModifiers.addAll(KotItemModifierSQL
						.getKotItemModifiersByKotItemDetail(kotItemDetail
								.getId()));
			}
			KotSummarySQL.deleteKotSummary(fromKotSummary);
			Order newOrder = OrderSQL.getUnfinishedOrderAtTable(currentTable, oldOrder.getBusinessDate());
			OrderBill newOrderBill = ObjectFactory.getInstance().getOrderBill(
					newOrder, App.instance.getRevenueCenter());
			List<OrderDetail> orderDetails = OrderDetailSQL
					.getUnFreeOrderDetails(oldOrder);
			if (!orderDetails.isEmpty()) {
				for (OrderDetail orderDetail : orderDetails) {
					OrderDetail newOrderDetail = ObjectFactory.getInstance()
							.getOrderDetailForTransferTable(newOrder,
									orderDetail);
					OrderDetailSQL.addOrderDetailETC(newOrderDetail);
					List<OrderModifier> orderModifiers = OrderModifierSQL
							.getOrderModifiers(orderDetail);
					if (orderModifiers.isEmpty()) {
						continue;
					}
					for (OrderModifier orderModifier : orderModifiers) {
						OrderModifier newOrderModifier = ObjectFactory
								.getInstance().getOrderModifier(
										newOrder,
										newOrderDetail,
										CoreData.getInstance().getModifier(
												orderModifier.getModifierId()),
										orderModifier.getPrinterId().intValue());
						OrderModifierSQL.addOrderModifier(newOrderModifier);
					}
				}
			}

			OrderDetailSQL.deleteOrderDetailByOrder(oldOrder);
			OrderModifierSQL.deleteOrderModifierByOrder(oldOrder);
			OrderBillSQL.deleteOrderBillByOrder(oldOrder);
			OrderBillSQL.add(newOrderBill);
			OrderSQL.deleteOrder(oldOrder);
			context.kotPrintStatus(ParamConst.JOB_TYPE_POS_MERGER_TABLE, null);
			printKotSummary = toKotSummary;
		} else if (ParamConst.JOB_TRANSFER_KOT.equals(transferAction)) {
			KotSummarySQL.update(fromKotSummary);
			Order order = (Order) orderMap.get("fromOrder");
			OrderSQL.update(order);
			kotItemDetails = KotItemDetailSQL
					.getKotItemDetailBySummaryId(fromKotSummary.getId());
			for (KotItemDetail kotItemDetail : kotItemDetails)
				kotItemModifiers.addAll(KotItemModifierSQL
						.getKotItemModifiersByKotItemDetail(kotItemDetail
								.getId()));
			context.kotPrintStatus(ParamConst.JOB_TYPE_POS_TRANSFER_TABLE,
					order);
			printKotSummary = KotSummarySQL.getKotSummary(fromKotSummary.getOrderId());
		}

		for (KotItemDetail items : kotItemDetails) {
			Integer pgid = items.getPrinterGroupId();
			int kotItemDetailId = items.getId().intValue();

			// Get all Group ids that KOT blongs to
			if (!printerGrougIds.contains(pgid))
				printerGrougIds.add(pgid);

			// kot
			if (kots.containsKey(pgid)) {
				ArrayList<KotItemDetail> tmp = kots.get(pgid);
				tmp.add(items);
			} else {
				ArrayList<KotItemDetail> tmp = new ArrayList<KotItemDetail>();
				tmp.add(items);
				kots.put(pgid, tmp);
			}

			// modifier
			if (mods.containsKey(pgid)) {
				ArrayList<KotItemModifier> tmp = mods.get(pgid);
				for (KotItemModifier mof : kotItemModifiers) {
					if (mof.getKotItemDetailId().intValue() == kotItemDetailId) {
						tmp.add(mof);
					}
				}
			} else {
				ArrayList<KotItemModifier> tmp = new ArrayList<KotItemModifier>();
				for (KotItemModifier mof : kotItemModifiers) {
					if (mof.getKotItemDetailId().intValue() == kotItemDetailId) {
						tmp.add(mof);
					}
				}
				mods.put(items.getPrinterGroupId(), tmp);
			}
		}

		boolean printed = false;
		if (App.countryCode == ParamConst.CHINA){
			return true;
		}
		for (Integer prgid : printerGrougIds) {
			ArrayList<Printer> printers = CoreData.getInstance()
					.getPrintersInGroup(prgid.intValue());
			for (Printer prnt : printers) {
				// physical printer
				PrinterDevice prntd = App.instance.getPrinterDeviceById(prnt
						.getId());
				prntd.setGroupId(prgid.intValue());
				if (prntd != null) {
					String fromTableName = (String) orderMap.get("fromTableName");
					printKotSummary.setDescription("*** Transferred from " + fromTableName + " ***");
					printed = App.instance.remoteKotPrint(prntd, printKotSummary,
							kots.get(prgid), mods.get(prgid));

				}
			}
		}
		return printed;
	}

	public void clear() {
		this.kotJobManager.clear();
	}

	public void stop() {
		this.kotJobManager.stop();
	}
}
