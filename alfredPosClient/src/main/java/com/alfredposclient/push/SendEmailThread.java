package com.alfredposclient.push;

import android.app.Application;
import android.content.Context;

import com.alfredbase.BaseActivity;
import com.alfredbase.javabean.ReportDaySales;
import com.alfredbase.javabean.ReportDayTax;
import com.alfredbase.javabean.RevenueCenter;
import com.alfredposclient.activity.OpenRestaruant;
import com.alfredposclient.global.App;
import com.alfredposclient.global.ReportObjectFactory;
import com.alfredposclient.global.SyncCentre;
import com.alfredposclient.xmpp.XMPP;
import com.google.gson.Gson;

import java.util.List;

/**
 * Created by Alex on 2017/6/23.
 */

public class SendEmailThread extends Thread {
    Context context;



    @Override
    public void run() {
        super.run();
        while(App.instance.getIndexOfActivity(OpenRestaruant.class) == -1){
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();

            }
        }
        try{
//
        ReportDaySales reportDaySales;
        List<ReportDayTax> reportDayTaxs;
            reportDaySales = ReportObjectFactory.getInstance().loadShowReportDaySales(App.instance.getBusinessDate());
            reportDayTaxs = ReportObjectFactory.getInstance().loadShowReportDayTax(reportDaySales,App.instance.getBusinessDate());

            if(reportDayTaxs != null && reportDayTaxs.size() > 0) {


                SyncCentre.getInstance().syncSendEmail(App.instance, reportDaySales, reportDayTaxs, null);
            }
           // SyncCentre.getInstance().syncMedia(App.instance,  null);

//            RevenueCenter revenueCenter = App.instance.getRevenueCenter();
//            String name = "alfred_" + revenueCenter.getId().intValue() + "_b";
//            String roomName = "alfred" + revenueCenter.getRestaurantId().intValue() + "g";
//            String pass = "amp" + name;
//            XMPP.getInstance().login(name, pass, revenueCenter.getId().toString(), roomName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroy() {

        super.destroy();
    }

}
