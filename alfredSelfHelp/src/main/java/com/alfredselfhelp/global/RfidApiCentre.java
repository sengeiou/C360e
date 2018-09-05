package com.alfredselfhelp.global;

import com.alfredbase.utils.CallBack;
import com.alfredbase.utils.LogUtil;
import com.alfredselfhelp.utils.UIHelp;
import com.nordicid.nurapi.NurApi;
import com.nordicid.nurapi.NurApiListener;
import com.nordicid.nurapi.NurEventClientInfo;
import com.nordicid.nurapi.NurEventDeviceInfo;
import com.nordicid.nurapi.NurEventFrequencyHop;
import com.nordicid.nurapi.NurEventIOChange;
import com.nordicid.nurapi.NurEventInventory;
import com.nordicid.nurapi.NurEventNxpAlarm;
import com.nordicid.nurapi.NurEventProgrammingProgress;
import com.nordicid.nurapi.NurEventTraceTag;
import com.nordicid.nurapi.NurEventTriggeredRead;
import com.nordicid.nurapi.NurTag;
import com.nordicid.nurapi.NurTagStorage;

import java.util.HashMap;

public class RfidApiCentre {
    private static final String TAG = RfidApiCentre.class.getSimpleName();
    // RFID API
    private NurApi nurApi;
    public static boolean isleftMoved;
    private NurTagStorage nurTagStorage;
    private boolean mInventoryIsRunning;
    private static RfidApiCentre instance;
    private CallBack callBack;
    private RfidApiCentre(){
    }
    public static RfidApiCentre getInstance() {
        if (instance == null)
            instance = new RfidApiCentre();
        return instance;
    }

    public void setNurTagStorage(NurTagStorage nurTagStorage) {
        this.nurTagStorage = nurTagStorage;
    }

    public boolean isConnected(){
        if(nurApi != null && nurApi.isConnected()){
            return true;
        }else{
            return false;
        }
    }

    public void initApi(){
        nurApi = new NurApi();
        nurTagStorage = new NurTagStorage();
        nurApi.setListener(new NurApiListener() {
            @Override
            public void logEvent(int i, String s) {

            }

            @Override
            public void connectedEvent() {
                LogUtil.d(TAG, "NurApi connected");
                enableItems(true);
            }

            @Override
            public void disconnectedEvent() {
                LogUtil.d(TAG, "NurApi disconnected");
                enableItems(false);
            }

            @Override
            public void bootEvent(String s) {

            }

            @Override
            public void inventoryStreamEvent(NurEventInventory nurEventInventory) {
                if (nurEventInventory.tagsAdded > 0) {
                    inventory();
                    callBack.onSuccess();
                }
                if (nurEventInventory.stopped && mInventoryIsRunning) {
                    try {
                        nurApi.startInventoryStream();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void IOChangeEvent(NurEventIOChange nurEventIOChange) {

            }

            @Override
            public void traceTagEvent(NurEventTraceTag nurEventTraceTag) {

            }

            @Override
            public void triggeredReadEvent(NurEventTriggeredRead nurEventTriggeredRead) {

            }

            @Override
            public void frequencyHopEvent(NurEventFrequencyHop nurEventFrequencyHop) {

            }

            @Override
            public void debugMessageEvent(String s) {

            }

            @Override
            public void inventoryExtendedStreamEvent(NurEventInventory nurEventInventory) {

            }

            @Override
            public void programmingProgressEvent(NurEventProgrammingProgress nurEventProgrammingProgress) {

            }

            @Override
            public void deviceSearchEvent(NurEventDeviceInfo nurEventDeviceInfo) {

            }

            @Override
            public void clientConnectedEvent(NurEventClientInfo nurEventClientInfo) {

            }

            @Override
            public void clientDisconnectedEvent(NurEventClientInfo nurEventClientInfo) {

            }

            @Override
            public void nxpEasAlarmEvent(NurEventNxpAlarm nurEventNxpAlarm) {

            }
        });
    }
    private void enableItems(boolean isConnected){
        if(isConnected){
            UIHelp.showToast(App.instance, "RFID scanner is Connected");
            try {
                nurApi.setSetupOpFlags(NurApi.OPFLAGS_INVSTREAM_ZEROS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            UIHelp.showToast(App.instance, "RFID scanner is Disconnected");
        }

    }

    public void startRFIDScan(CallBack callBack){
        if(nurApi != null && nurApi.isConnected() && !nurApi.isInventoryStreamRunning()){
            try {
                nurApi.startInventoryStream();
                mInventoryIsRunning = true;
                if(callBack != null) {
                    callBack.onSuccess();
                }
            } catch (Exception e) {
                callBack.onError();
                e.printStackTrace();
            }
        }
    }

    public void stopRFIDScan(CallBack callBack){
        if(nurApi != null && nurApi.isConnected() && nurApi.isInventoryStreamRunning()){
            try {
                nurApi.stopInventoryStream();
                mInventoryIsRunning = false;
                nurTagStorage.clear();
                if(callBack != null) {
                    callBack.onSuccess();
                }
            } catch (Exception e) {
                callBack.onError();
                e.printStackTrace();
            }
        }
    }

    public NurTagStorage getNurTagStorage() {
        return nurTagStorage;
    }

    private void inventory(){
        synchronized (nurApi.getStorage()){
            NurTagStorage tagStorage = nurApi.getStorage();
            for (int n = 0; n < tagStorage.size(); n++){
                NurTag tag = tagStorage.get(n);
                if(nurTagStorage.addTag(tag)) {
                    HashMap<String, String> temp = new HashMap<>();
                    temp.put("epc", tag.getEpcString());
                    temp.put("rssi", "" + tag.getRssi());
                    temp.put("timestamp", "" + tag.getTimestamp());
                    temp.put("freq", "" + tag.getFreq() + " kHz (Ch: " + tag.getChannel() + ")");
                    temp.put("found", "1");
                    temp.put("foundpercent", "100");
                    tag.setUserdata(temp);
                }
            }
        }
    }

}
