package com.alfredposclient.jobs;

import com.alfredbase.BaseActivity;
import com.alfredbase.ParamConst;
import com.alfredbase.javabean.SyncMsg;
import com.alfredbase.store.sql.SyncMsgSQL;
import com.alfredbase.utils.LogUtil;
import com.alfredposclient.global.App;
import com.alfredposclient.global.SyncCentre;
import com.alfredposclient.http.HttpAPI;
import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import java.util.HashMap;
import java.util.Map;

public class SyncMsgJob extends Job {
	private String TAG = SyncMsgJob.class.getSimpleName();
    private long localId;
    private String msgUUID; 
    private int msgType;
    private int msgId; //orderid, report data is 0; or others
    private Long created;
    private Long bizDate;
    private int revenueId;
    private int appOrderId;
    private int orderStatus;
    
    public SyncMsgJob(int revenueId, int msgType, String uuid,
    		int msgId, Long bizDate, Long created) {
        super(new Params(Priority.MID).requireNetwork().persist().groupBy("sync_"+bizDate.toString()));
        localId = -System.currentTimeMillis();
        this.revenueId = revenueId;
        this.msgId = msgId;
        this.msgType = msgType;
        this.msgUUID = uuid;
        this.bizDate = bizDate;
        this.created = created;
    }

    // 用作网络订单修改状态
    public SyncMsgJob(String uuid, int revenueId, int msgType, int appOrderId, int orderStatus, Long bizDate, Long created){
        super(new Params(Priority.MID).requireNetwork().persist().groupBy("net_order_"+bizDate.toString()));
        this.msgUUID = uuid;
        this.revenueId = revenueId;
        this.msgType = msgType;
        this.appOrderId = appOrderId;
        this.orderStatus = orderStatus;
        this.created = created;
    }
	
    @Override
    public void onAdded() {
        //job has been secured to disk, add item to database
    	SyncMsg content = SyncMsgSQL.getSyncMsgById(this.msgUUID, this.revenueId, this.created);
    	if (content != null) {
    		content.setStatus(ParamConst.SYNC_MSG_QUEUED);
    		SyncMsgSQL.add(content);
    	}
    	LogUtil.d(TAG, "onAdded");
    }

    @Override
    public void onRun() throws Throwable {
    	//LogUtil.d(TAG, payload);
    	BaseActivity context = App.getTopActivity();
    	try {
    		SyncMsg content = SyncMsgSQL.getSyncMsgById(this.msgUUID, this.revenueId, this.created);
    		if (content == null)
    			return;
    		
    		if(this.msgType == HttpAPI.ORDER_DATA) {
    			//sync order data
    			SyncCentre.getInstance().cloudSyncUploadOrderInfo(context, content, null);    			
    		}
    		if(this.msgType == HttpAPI.REPORT_DATA) {
    			SyncCentre.getInstance().cloudSyncUploadReportInfo(context,content, null);
    		}
            if(this.msgType == HttpAPI.NETWORK_ORDER_STATUS_UPDATE){
                SyncMsg syncMsg = SyncMsgSQL.getSyncMsgByAppOrderId(appOrderId, orderStatus);
                Map<String, Object> parameters = new HashMap<String, Object>();
                parameters.put("appOrderId", appOrderId);
                parameters.put("orderStatus", orderStatus);
                SyncCentre.getInstance().updateAppOrderStatus(context, parameters, syncMsg);
            }

	    	LogUtil.d(TAG, "Cloud MSG SYNC JOB Successful");
    	}catch(Throwable e) {
    		LogUtil.d(TAG, "Cloud MSG SYNC:" + e.getMessage());
    		throw new RuntimeException("Cloud MSG SYNC failed");
    	}
    }

    @Override
    protected void onCancel() {
    	LogUtil.d(TAG, "onCancel");
    	SyncMsg content = SyncMsgSQL.getSyncMsgById(this.msgUUID, this.revenueId, this.created);
    	if (content != null) {
    		content.setStatus(ParamConst.SYNC_MSG_UN_SEND);
    		SyncMsgSQL.add(content);
    	}    	
    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
       return true;
    }
}
