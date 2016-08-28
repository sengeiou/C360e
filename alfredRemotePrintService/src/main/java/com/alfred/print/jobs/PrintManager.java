package com.alfred.print.jobs;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.alfredbase.store.Store;
import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.JobManager;
import com.path.android.jobqueue.config.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class PrintManager {

	//private JobManager printJobManager;
	//each Printer IP has one job Managers
	static Map<String, JobManager> printJobManagers = new ConcurrentHashMap<String, JobManager>();
	private Context context;
	private final Object lock = new Object();
	static final String TAG = "PrintManager";
	
	private static final String STORE_PRINTER_IPS = "PRINTER_IPS";
	private CopyOnWriteArrayList<String> ips = new CopyOnWriteArrayList<String>();
	
    public PrintManager(Context mContext) {
		super();
		this.context = mContext;
		ips = this.getPrinterIpsFromStore();
    }

    public  JobManager configureJobManager(String ip) {
    	JobManager jobManager = null;
    	synchronized(lock) {
	    	jobManager = PrintManager.printJobManagers.get(ip);
		    	if (jobManager == null) {
			        Configuration printjobconfiguration = new Configuration.Builder(context)
			        .customLogger(new AlfredJobLogger("PRINTER_JOBS_"+ip))
			        .id("printer_jobs_"+ip)
			        .minConsumerCount(1)     //always keep at least one consumer alive
			        .maxConsumerCount(4)     //up to 3 consumers at a time
			        .loadFactor(3)           //3 jobs per consumer
			        .consumerKeepAlive(120)   //wait 2 minute
			        .build();
			        jobManager = new JobManager(this.context, printjobconfiguration);
			        PrintManager.printJobManagers.put(ip.trim(),jobManager);
			        addPrinterIpsInStore(ip);
		    	}
	    }
    	return jobManager;
    }
    
    private CopyOnWriteArrayList<String> getPrinterIpsFromStore() {
    	List<String> container = new ArrayList<String>();
    	CopyOnWriteArrayList<String> iplist = new CopyOnWriteArrayList<String>();
    	String ips = Store.getString(context, PrintManager.STORE_PRINTER_IPS);
    	if (!TextUtils.isEmpty(ips)) {
    		container = Arrays.asList(ips.split(";"));
    		for (int i=0;i<container.size(); i++) {
    			iplist.add(container.get(i));
    		}
    	}
    	return iplist;
    }
    
    private void addPrinterIpsInStore(String ip) {
		if (!this.ips.contains(ip)) {
			this.ips.add(ip);
			String date = TextUtils.join(";", ips);
			Store.putString(context, PrintManager.STORE_PRINTER_IPS, date);
		}
    }	
    public synchronized void clear(){

		if (!printJobManagers.isEmpty()) {
			Set<String> key = printJobManagers.keySet();
			for (Iterator<String> it = key.iterator(); it.hasNext();) {
				String ip = it.next();
				JobManager jobMgr =  printJobManagers.get(ip);
				jobMgr.clear();
			}
		}
	}

    public synchronized void start(){
    	for (int i=0; i<this.ips.size();i++) {
    		this.configureJobManager(this.ips.get(i));
    	}
		if (!PrintManager.printJobManagers.isEmpty()) {
			Set<String> key = printJobManagers.keySet();
			for (Iterator<String> it = key.iterator(); it.hasNext();) {
				String ip = it.next();
				JobManager jobMgr =  printJobManagers.get(ip);
				jobMgr.start();
			}
		}
    }
    
    public synchronized void stop(){
		if (!PrintManager.printJobManagers.isEmpty()) {
			Set<String> key = printJobManagers.keySet();
			for (Iterator<String> it = key.iterator(); it.hasNext();) {
				String ip = it.next();
				JobManager jobMgr =  printJobManagers.get(ip);
				jobMgr.stop();
			}
		}
    }
    
//    public synchronized void addPingJob() {
//		if (!printJobManagers.isEmpty()) {
//			Set<String> key = printJobManagers.keySet();
//			for (Iterator<String> it = key.iterator(); it.hasNext();) {
//				String ip = it.next();
//				JobManager jobMgr =  printJobManagers.get(ip);
//				PingJob pjb = new PingJob();
//				pjb.setPrinterIp(ip);
//				//jobMgr.addJob(pjb);
//				this.addJob(ip, pjb);
//			}
//		}
//    }
    
    private  Object joblock = new Object();
    public void addJob(String ip, Job job) {
	    	JobManager jobManager =  PrintManager.printJobManagers.get(ip);
	    	if (jobManager == null) {
	    		jobManager = this.configureJobManager(ip);
	    	}

	    	//init and create network printer connection
//			PrintService srv = ((PrintService)PrintService.instance);
//			WIFIPrinterHandler hdl = srv.getPrinterHandler(ip);
//			ESCPrinter printer = hdl.getPrinter();
//			if (printer == null) {
//		    	   ESCPrinter newPrinter = new ESCPrinter(srv, Print.DEVTYPE_TCP, ip, "P80");
//		    	   hdl.setPrinter(newPrinter);
//			} else if (!printer.isConnected()){
//	    	   ESCPrinter newPrinter = new ESCPrinter(srv, Print.DEVTYPE_TCP, ip, "P80");
//	    	   hdl.setPrinter(newPrinter);
//			} 
			synchronized(joblock) {
	    	   Log.d(TAG, "addJob in to PRINT JOB ("+job+")into: printer_jobs_"+ip);
			   jobManager.addJob(job);
			}
    	}
}
