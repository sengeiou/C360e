package com.alfredbase.http;

public class APIName {
    // 以下是服务器和客户端通用的api
    public static final String LOGIN_LOGINVERIFY = "login/loginVerify";
    public static final String RESTAURANT_GETRESTAURANTINFO = "restaurant/getRestaurantInfo";
    public static final String USER_GETUSER = "user/getUser";
    public static final String ITEM_GETITEMCATEGORY = "item/getItemCategory";
    public static final String ITEM_GETITEM = "item/getItem";
    public static final String ITEM_GETMODIFIER = "item/getModifier";
    public static final String TAX_GETTAX = "tax/getTax";
    public static final String HAPPYHOUR_GETHAPPYHOUR = "happyHour/getHappyHour";
    public static final String  PROMOTIONINFO_GETPROMOTIONINFO = "promotionPossInfo/getPromotionInfo";
    public static final String PROMOTIONPOSSINFO_GETPROMOTIONDATA = "promotionPossInfo/getPromotionData";

    public static final String RESTAURANT_GETPLACEINFO = "restaurant/getPlaceInfo";
    public static final String BOH_GETBOHHOLDUNPAID = "boh/getBohHoldUnpaid";
    public static final String BOH_UPDATEBOHHOLDPAID = "boh/updateBohHoldPaid";
    public static final String LOGIN_LOGOUT = "login/logout";

    public static final String SEND_EMAIL = "sendRealTimeReport/getDataSendEmail";

    public static final String SETTLEMENT_GETOTHERPAYMENT = "settlement/getOtherPayment";
    public static final String RESTAURANT_BINDDEVICEID = "restaurant/bindDeviceId";
    public static final String RESTAURANT_DAYSALES_REPORT = "reportDaySales/getReportDaySales";
    public static final String POSORDER_GETORDERBYQRCODE = "order/getOrderByQrcode";
    public static final String POSORDER_GETPAIEDAPPORDERBYID = "order/getPaiedAppOrderById";
    public static final String POSORDER_GETALLPAIEDAPPORDER = "order/getAllPaiedAppOrder";
    public static final String POSORDER_UPDATEAPPORDERSTATUS = "order/updateAppOrderStatus";
    public static final String UPDATE_MANUALAPPORDERSTATUS = "order/updateManualAppOrderStatus";
    public static final String APP_ORDER_REFUND = "order/appOrderRefund";
    public static final String RESTAURANT_MONTHLY_SALE_REPORT = "reportDaySales/getMonthReportDaySales";
    public static final String RESTAURANT_MONTHLY_PLU_REPORT = "reportPlu/getMonthReport";
    public static final String USER_UPDATEPASSWORD = "user/updatePassword";
    public static final String RESTAURANT_CHANGEPLACE = "restaurant/changePlace";
    public static final String RESTAURANT_GETPLACEINFONEW = "restaurant/getPlaceInfoNew";
    public static final String MEMBERSHIP_ACTIVATECARD = "membership/activateCard";
    public static final String MEMBERSHIP_OPERATEBALANCE = "membership/operateBalance";
    public static final String MEMBERSHIP_REPORTCARD = "membership/reportCard";
    public static final String MEMBERSHIP_REATTENDCARD = "membership/reAttendCard";
    public static final String MEMBERSHIP_QUERYBALANCE = "membership/queryBalance";
    public static final String TABLE_UPDATETABLESTATUS = "table/updateTableStatus";
    public static final String SOFTWARE_GETVERSION = "software/getVersion";
    public static final String CLOCK_CLOCKINOUT = "clock/clockInOut";
    public static final String CLOCK_GETUSERTIMESHEET = "clock/getUserTimeSheet";
    public static final String UPDATE_REAMINING_STOCK = "/remainingStock/updateReaminingStock";
    public static final String UPDATE_REAMINING_STOCK_ITEMID = "/remainingStock/updateReaminingStockByItemId";
    public static final String QC_DOWNLOAD = "download/apk/diner";
    public static final String RESET_RESTAURANT_ITEM_NUM = "remainingStock/resetRestaurantItemNum";
    public static final String GET_REMAINING_STOCK = "/item/getRemainingStock";
    //3rd-party Serivce
    public static final String REQUEST_ALIPAY = "alipay/getAlipayUrl";
    /**
     * 以pos作为服务器
     */
    // 以下是Waiter专用的apiInternet connection not available. Please connect for cloud sync.
    public static final String EMPLOYEE_ID = "employee_id";
    public static final String SELECT_TABLES = "select_tables";
    public static final String COMMIT_ORDER = "commit_Order";
    public static final String WAITER_IP_CHANGE = "waiter_ip_change";
    public static final String PAIRING_COMPLETE = "pairing_complete";
    public static final String GET_KOT_NOTIFICATION = "get_kot_notifications";
    public static final String COLLECT_KOT_ITEM = "collect_kot_item";
    public static final String GET_ORDERDETAILS = "get_orderDetails";
    public static final String GET_BILL = "get_bill";
    public static final String PRINT_BILL = "print_bill";
    public static final String UNSEAT_TABLE = "unseat_table";
    public static final String VOID_ITEM = "void_item";
    public static final String TEMPORARY_DISH = "temporary_dish";
    public static final String GET_PRINTER = "get_printer";

    // 以下是KDS专用
    public static final String GET_PRINTERS = "get_printers";
    public static final String KDS_IP_CHANGE = "kds_ip_change";
    public static final String CONNECT_POS = "connect_pos";
    public static final String UPDATE_EXISTING_KOT = "update_existing_kot";
    public static final String TRANSFER_KOT = "transfer_kot";
    public static final String TRANSFER_ITEM_KOT = "transfer_item_kot";
    public static final String KOT_ITEM_COMPLETE = "kot_item_complete";
    public static final String KOT_COMPLETE = "kot_complete";
    public static final String CANCEL_COMPLETE = "cancel_complete";
    public static final String SUMMARY_COMPLETE = "summary_complete";
    public static final String CALL_SPECIFY_THE_NUMBER = "call_specify_the_number";
    public static final String KOT_OUT_OF_STOCK = "kot_out_of_stock";

    // 以下是桌面设备专用

    public static final String DESKTOP_LOGIN = "desktop/login";
    public static final String DESKTOP_GETTABLE = "desktop/getTable";
    public static final String DESKTOP_SELECTTABLE = "desktop/selectTable";
    public static final String DESKTOP_GETITEM = "desktop/getItem";
    public static final String DESKTOP_COMMITORDER = "desktop/commitOrder";
    public static final String DESKTOP_KIOSKORDER = "desktop/kioskOrder";
    public static final String DESKTOP_GETBILL = "desktop/getBill";
    public static final String DESKTOP_PRINTBILL = "desktop/printBill";


    // subPos专用
    public static final String SUBPOS_LOGIN = "subPos/login";
    public static final String SUBPOS_UPDATE_DATA = "subPos/updateData";
    public static final String SUBPOS_CHOOSEREVENUE = "subPos/chooseRevenue";
    public static final String SUBPOS_SYNCDATA = "subPos/syncData";
    public static final String SUBPOS_SESSIONSTATUS = "subPos/sessionStatus";
    public static final String SUBPOS_COMMIT_ORDER = "subPos/commitOrder";
    public static final String SUBPOS_COMMIT_ORDERLOG = "subPos/commitOrderLog";
    public static final String SUBPOS_COMMIT_REPORT = "subPos/commitReport";
    public static final String SUBPOS_CLOSE_SESSION = "subPos/closeSession";
    // kpmg专用
    public static final String KPMG_LOGIN = "kpmg/login";
    public static final String KPMG_UPDATE_DATA = "kpmg/updateData";
    public static final String KPMG_COMMIT_ORDER = "kpmg/commitOrder";
    public static final String GET_REMAINING_STOCK_KPMG = "kpmg/getRemainingStock";
    public static final String KPMG_CHECK_SOTCK_NUM= "kpmg/checkSotckNum";

    // CallNum
    public static final String CALLNUM_ASSIGNREVENUE = "desktop/callNumassignRevenue";


    //kpm
    public static final String KPM_EMPLOYEE_ID = "kpm_employee_id";

    public static final String kpm_RESTAURANT_GETPLACEINFO = "restaurant/getPlaceInfo";


    /**
     * 以kds作为服务器
     */
    public static final String SUBMIT_NEW_KOT = "submit_new_kot";

    /**
     * 以waiter作为服务器
     */
    public static final String KOT_NOTIFICATION = "kot_notification";
    public static final String SYSTEM_MESSAGE = "system_msg";
    public static final String CLOSE_SESSION = "close_session";
    public static final String TRANSFER_TABLE = "transfer_table";

    /**
     * 给callnum 发送信息
     */
    public static final String CALL_POS_NUM = "call_pos_num";
    public static final String POS_CLOSE_SESSION = "pos_close_session";


    public static final String SET_LANGUAGE = "set_language"; //to server
    public static final String POS_LANGUAGE = "pos_language"; //to client

}
