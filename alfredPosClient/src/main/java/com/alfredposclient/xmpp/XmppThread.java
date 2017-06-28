package com.alfredposclient.xmpp;

import com.alfredbase.javabean.RevenueCenter;
import com.alfredposclient.global.App;

/**
 * Created by Alex on 2017/6/23.
 */

public class XmppThread extends Thread {
    @Override
    public void run() {
        super.run();
        RevenueCenter revenueCenter = App.instance.getRevenueCenter();
        String name = "alfred_" + revenueCenter.getId().intValue() + "_B";
        String roomName = "alfred" + revenueCenter.getRestaurantId().intValue() + "G";
        String pass = "AMP" + name;
        try {
            XMPP.getInstance().login(name, pass, revenueCenter.getId().toString(), roomName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroy() {
        XMPP.getInstance().close();
        super.destroy();
    }
}
