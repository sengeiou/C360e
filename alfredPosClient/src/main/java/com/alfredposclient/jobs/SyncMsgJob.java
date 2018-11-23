package com.alfredposclient.jobs;

import com.alfredbase.BaseActivity;
import com.alfredbase.ParamConst;
import com.alfredbase.global.CoreData;
import com.alfredbase.javabean.OrderDetail;
import com.alfredbase.javabean.RemainingStock;
import com.alfredbase.javabean.RemainingStockVo;
import com.alfredbase.javabean.SyncMsg;
import com.alfredbase.store.sql.OrderDetailSQL;
import com.alfredbase.store.sql.RemainingStockSQL;
import com.alfredbase.store.sql.SyncMsgSQL;
import com.alfredbase.utils.LogUtil;
import com.alfredposclient.global.App;
import com.alfredposclient.global.SyncCentre;
import com.alfredposclient.http.HttpAPI;
import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private boolean isNoSave = false;
    private int orderId;
    private List<RemainingStockVo> remainingStockVoList=new ArrayList<>();
    
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
     // 修改菜的数量
    public SyncMsgJob(String uuid,int orderId) {
        super(new Params(Priority.MID).requireNetwork().persist().groupBy("sync_no_save"+orderId));
        localId = -System.currentTimeMillis();
        this.isNoSave = true;
        this.orderId = orderId;
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
        if(!isNoSave){
            SyncMsg content = SyncMsgSQL.getSyncMsgById(this.msgUUID, this.revenueId, this.created);
            if (content != null) {
                content.setStatus(ParamConst.SYNC_MSG_QUEUED);
                SyncMsgSQL.add(content);
                LogUtil.d("TEST", "onAdd");
            }
        }
    	LogUtil.d(TAG, "onAdded");
    }

    @Override
    public void onRun() throws Throwable {
    	//LogUtil.d(TAG, payload);
    	BaseActivity context = App.getTopActivity();
    	try {
    	    if(isNoSave){
    	        remainingStockVoList.clear();
                List<OrderDetail> orderDetailList = OrderDetailSQL.getOrderDetails(orderId);

                for(OrderDetail orderDetail: orderDetailList){
                    RemainingStockVo remainingStockVo=new RemainingStockVo();
                    int itemTempId = CoreData.getInstance().getItemDetailById(orderDetail.getItemId()).getItemTemplateId();
                    int qty = RemainingStockSQL.getRemainingStockByitemId(itemTempId).getQty();
                    remainingStockVo.setItemId(itemTempId);
                    remainingStockVo.setNum(qty);
                    remainingStockVoList.add(remainingStockVo);
//                    if(!map.containsKey(itemTempId)) {
//                        int qty = RemainingStockSQL.getRemainingStockByitemId(itemTempId).getQty();
//                        map.put(itemTempId,qty);
//                    }
                }
                Map<String,Object> reMap=new HashMap<String,Object>();
                reMap.put("remainingStockVoList",remainingStockVoList);
               SyncCentre.getInstance().updateReaminingStock(context,reMap,null);

            }else {
                if (this.msgType == HttpAPI.NETWORK_ORDER_STATUS_UPDATE) {
                    SyncMsg syncMsg = SyncMsgSQL.getSyncMsgByAppOrderId(appOrderId, orderStatus);
                    SyncCentre.getInstance().updateAppOrderStatus(context, syncMsg);
                    LogUtil.d(TAG, "updateAppOrderStatus start");
                } else {
                    SyncMsg content = SyncMsgSQL.getSyncMsgById(this.msgUUID, this.revenueId, this.created);
                    if (content == null)
                        return;

                    if (this.msgType == HttpAPI.ORDER_DATA
                            || this.msgType == HttpAPI.LOG_DATA
                            || this.msgType == HttpAPI.SUBPOS_ORDER_DATA) {
                        //sync order data
                        SyncCentre.getInstance().cloudSyncUploadOrderInfo(context, content, null);
                    }
                    if (this.msgType == HttpAPI.REPORT_DATA ||
                            this.msgType == HttpAPI.OPEN_CLOSE_SESSION_RESTAURANT) {
                        SyncCentre.getInstance().cloudSyncUploadReportInfo(context, content, null);
                    }
                    LogUtil.d(TAG, "Cloud MSG SYNC JOB Successful");
                }
            }
    	}catch(Throwable e) {
    		LogUtil.d(TAG, "Cloud MSG SYNC:" + e.getMessage());
    		throw new RuntimeException("Cloud MSG SYNC failed"+e.getMessage());
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
