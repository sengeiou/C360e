package com.alfred.printer;

import com.alfred.remote.printservice.App;
import com.alfred.remote.printservice.PrintService;
import com.alfred.remote.printservice.R;
import com.alfredbase.ParamConst;
import com.alfredbase.javabean.ReportDayPayment;
import com.alfredbase.javabean.ReportDaySales;
import com.alfredbase.javabean.ReportDayTax;
import com.alfredbase.javabean.model.ReportSessionSales;
import com.alfredbase.javabean.temporaryforapp.ReportUserOpenDrawer;
import com.alfredbase.store.sql.TaxCategorySQL;
import com.alfredbase.utils.BH;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DaySalesReportPrint extends ReportBasePrint {

    public final static int FIXED_COL3_SPACE = 2;
    public final static int FIXED_COL3_QTY = 10; //in case of 48 dots width, QTY col = 10dots
    public final static int FIXED_COL3_TOTAL = 12; //in case of 48 dots width, QTY col = 12dots

    public static int COL3_ITEMNAME; // Width = CharSize/scale - FIXED_COL3_QTY/scale -
    // FIXED_COL2_TOTAL/scale- FIXED_COL2_SPACE * 2

    private ReportDaySales reportDaySales;
    private ArrayList<ReportDayTax> reportDayTaxs;
    private List<ReportDayPayment> reportDayPayments;
    private List<ReportUserOpenDrawer> reportUserOpenDrawerList;
    private List<ReportSessionSales> reportSessionSalesList;

    private String OP;
    private String reportNo;
    private String date;

    public DaySalesReportPrint(String uuid, Long bizDate) {
        super("daysales", uuid, bizDate);
    }

    public ArrayList<PrintData> getData() {
        return data;
    }

    public void print(ReportDaySales reportData, ArrayList<ReportDayTax> taxData, List<ReportDayPayment> reportDayPayments,
                      List<ReportUserOpenDrawer> reportUserOpenDrawers, List<ReportSessionSales> reportSessionSalesList) {
        this.reportDaySales = reportData;
        this.reportDayTaxs = taxData;
        this.reportDayPayments = reportDayPayments;
        this.reportUserOpenDrawerList = reportUserOpenDrawers;
        this.reportSessionSalesList = reportSessionSalesList;
        GetReportDaySalesText();
    }


    /* Four columns layout (Width = 48dots)
     * |item Name    38/scale   | QTY 10/scale  |
     *
     **/
    private String getThreeColHeader(String col1Title, String col2Title, String col3Title) {

        StringBuffer ret = new StringBuffer();
        DaySalesReportPrint.COL3_ITEMNAME = this.charSize - DaySalesReportPrint.FIXED_COL3_QTY - DaySalesReportPrint.FIXED_COL3_TOTAL;
        String title1 = StringUtil.padRight(col1Title, DaySalesReportPrint.COL3_ITEMNAME);
        String title2 = StringUtil.padRight(col2Title, DaySalesReportPrint.FIXED_COL3_QTY);
        String title3 = StringUtil.padLeft(col3Title, DaySalesReportPrint.FIXED_COL3_TOTAL);
        ret.append(title1).append(title2).append(title3).append(reNext);

        return ret.toString();
    }

    private String getTwoColContent(String col1Content, String col2Content, int charScale) {
        StringBuffer result = new StringBuffer();

        int col1Lines = 1;

        int col2Lines = 1;

//		int qtyLen = KOTPrint.FIXED_COL2_QTY;
//		if(charScale > 1){
        int qtyLen = charScale * col2Content.length();
//		}

        KOTPrint.COL2_ITEMNAME = this.charSize / charScale - qtyLen / charScale - KOTPrint.FIXED_COL2_SPACE;

        //double ln1 = col1Content.length();
        int ln1 = 1;
        String[] splitedcontents = {col1Content};

        try {
            //ln1 = (col1Content.getBytes("GBK").length)/(KOTPrint.COL2_ITEMNAME*1.0);
            splitedcontents = StringUtil.formatLn(KOTPrint.COL2_ITEMNAME * 1, col1Content);
            ln1 = splitedcontents.length;
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        col1Lines = splitedcontents.length;
        //col1Lines = StringUtil.nearestTen(ln1);
        //String col1PadContent = StringUtil.padRight(col1Content, col1Lines*KOTPrint.COL2_ITEMNAME);
        //ArrayList<String> splittedCol1Content = StringUtil.splitEqually(col1PadContent, KOTPrint.COL2_ITEMNAME);

        double ln2 = (col2Content.length()) / (qtyLen * 1.0 / charScale);
        col2Lines = StringUtil.nearestTen(ln2);
        String col2PadContent = StringUtil.padRight(col2Content, col2Lines * qtyLen / charScale);
        ArrayList<String> splittedCol2Content = StringUtil.splitEqually(col2PadContent, qtyLen / charScale);


        for (int i = 0; i < Math.max(col1Lines, col2Lines); i++) {
            if (i < col1Lines) {
                //result.append(splittedCol1Content.get(i));
                result.append(StringUtil.padRight(splitedcontents[i], KOTPrint.COL2_ITEMNAME));
            } else {
                result.append(StringUtil.padRight(" ", KOTPrint.COL2_ITEMNAME));
            }
            //padding
            result.append(StringUtil.padRight(" ", KOTPrint.FIXED_COL2_SPACE));

            if (i < col2Lines) {
                result.append(splittedCol2Content.get(i));
            } else {
                result.append(StringUtil.padRight(" ", (qtyLen) / charScale));
            }

            result.append(reNext);
        }
        return result.toString();
    }

    /* Four columns layout (Width = 48dots)
     * |item Name   Dynamical |2| QTY  scale  | Total|
     *
     **/
    private String GetThreeColContent(String col1Content, String col2Content,
                                      String col3Content, int charScale) {

        StringBuffer result = new StringBuffer();

        int col1Lines = 1;
        int col2Lines = 1;
        int col3Lines = 1;

        DaySalesReportPrint.COL3_ITEMNAME = (this.charSize - DaySalesReportPrint.FIXED_COL3_TOTAL - DaySalesReportPrint.FIXED_COL3_QTY) / charScale - 2 * DaySalesReportPrint.FIXED_COL3_SPACE;

        double ln1 = col1Content.length();
        try {
            ln1 = (col1Content.getBytes("GBK").length) / (DaySalesReportPrint.COL3_ITEMNAME * 1.0);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        col1Lines = StringUtil.nearestTen(ln1);
        String col1PadContent = StringUtil.padRight(col1Content, col1Lines * DaySalesReportPrint.COL3_ITEMNAME);
        ArrayList<String> splittedCol1Content = StringUtil.splitEqually(col1PadContent, DaySalesReportPrint.COL3_ITEMNAME);

        double ln2 = (col2Content.length()) / (DaySalesReportPrint.FIXED_COL3_QTY * 1.0 / charScale);
        col2Lines = StringUtil.nearestTen(ln2);
        String col2PadContent = StringUtil.padLeft(col2Content, col2Lines * DaySalesReportPrint.FIXED_COL3_QTY / charScale);
        ArrayList<String> splittedCol2Content = StringUtil.splitEqually(col2PadContent, DaySalesReportPrint.FIXED_COL3_QTY / charScale);

        double ln3 = (col3Content.length()) / (DaySalesReportPrint.FIXED_COL3_TOTAL * 1.0 / charScale);
        col3Lines = StringUtil.nearestTen(ln3);
        String col3PadContent = StringUtil.padLeft(col3Content, col3Lines * DaySalesReportPrint.FIXED_COL3_TOTAL / charScale);
        ArrayList<String> splittedCol3Content = StringUtil.splitEqually(col3PadContent, DaySalesReportPrint.FIXED_COL3_TOTAL / charScale);


        for (int i = 0; i < Math.max(Math.max(col1Lines, col2Lines), col3Lines); i++) {
            if (i < col1Lines) {
                result.append(splittedCol1Content.get(i));
            } else {
                result.append(StringUtil.padRight(" ", DaySalesReportPrint.COL3_ITEMNAME));
            }
            //padding
            result.append(StringUtil.padRight(" ", DaySalesReportPrint.FIXED_COL3_SPACE));

            if (i < col2Lines) {
                result.append(splittedCol2Content.get(i));
            } else {
                result.append(StringUtil.padLeft(" ", (DaySalesReportPrint.FIXED_COL3_QTY) / charScale));
            }

            //padding
            result.append(StringUtil.padRight(" ", DaySalesReportPrint.FIXED_COL3_SPACE));

            if (i < col3Lines) {
                result.append(splittedCol3Content.get(i));
            } else {
                result.append(StringUtil.padLeft(" ", (DaySalesReportPrint.FIXED_COL3_TOTAL) / charScale));
            }
            result.append(reNext);
        }
        return result.toString();
    }

    public void AddContentListHeader(String itemName, String qty, String total) {
        PrintData header = new PrintData();
        header.setDataFormat(PrintData.FORMAT_TXT);
        header.setText(this.GetThreeColContent(itemName, qty, total, 1));
        this.data.add(header);
        addHortionalLine(this.charSize);
    }

    public void addItemWithLang(String itemName, String qty, String amount, int lan, int scale) {
        PrintData kot = new PrintData();
        kot.setDataFormat(PrintData.FORMAT_TXT);
        kot.setFontsize(scale);
        kot.setLanguage(lan);
        kot.setText(this.GetThreeColContent(itemName, qty, amount, scale));
        this.data.add(kot);
    }

    public void addItem(String itemName, String qty, String amount, int scale) {
        PrintData kot = new PrintData();
        kot.setDataFormat(PrintData.FORMAT_TXT);
        kot.setFontsize(scale);
        kot.setLanguage(PrintData.LANG_CN);
        kot.setText(this.GetThreeColContent(itemName, qty, amount, scale));
        this.data.add(kot);
    }

    public void addItem(String itemName, String amount, int scale) {
        PrintData kot = new PrintData();
        kot.setDataFormat(PrintData.FORMAT_TXT);
        kot.setFontsize(scale);
        kot.setLanguage(PrintData.LANG_CN);
        kot.setText(this.getTwoColContent(itemName, amount, scale));
        this.data.add(kot);
    }


    public void addSectionHeader(String name) {
        String centerName = StringUtil.padCenterWithDash(name, this.charSize);
        PrintData kot = new PrintData();
        kot.setDataFormat(PrintData.FORMAT_TXT);
        kot.setFontsize(1);
        kot.setTextBold(1);
        kot.setTextAlign(PrintData.ALIGN_CENTRE);
        kot.setLanguage(PrintData.LANG_CN);
        kot.setText(centerName + reNext);
        this.data.add(kot);
    }

    /*
     * Day Sales Report*/
    private void GetReportDaySalesText() {
        //StringBuffer sbr = new StringBuffer();
        this.addItem(PrintService.instance.getResources().getString(R.string.item_sales), reportDaySales.getItemSalesQty().toString(),
                reportDaySales.getItemSales(), 1);
        this.addItem(PrintService.instance.getResources().getString(R.string.stored_card_sales), reportDaySales.getTopUpsQty().toString(),
                BH.getBD(reportDaySales.getTopUps()).toString(), 1);
        this.addItem(PrintService.instance.getResources().getString(R.string.ent_items), reportDaySales.getFocItemQty().toString(),
                reportDaySales.getFocItem(), 1);
        this.addItem(PrintService.instance.getResources().getString(R.string.ent_bills), reportDaySales.getFocBillQty().toString(),
                reportDaySales.getFocBill(), 1);
        this.addItem(PrintService.instance.getResources().getString(R.string.void_items), reportDaySales.getItemVoidQty().toString(),
                reportDaySales.getItemVoid(), 1);
        this.addItem(PrintService.instance.getResources().getString(R.string.void_bills), reportDaySales.getBillVoidQty().toString(),
                reportDaySales.getBillVoid(), 1);
        this.addItem(PrintService.instance.getResources().getString(R.string.refund_bills), reportDaySales.getBillRefundQty().toString(),
                reportDaySales.getBillRefund(), 1);
        this.addItem(PrintService.instance.getResources().getString(R.string.refund_taxes), "",
                reportDaySales.getRefundTax(), 1);
        this.addItem(PrintService.instance.getResources().getString(R.string.discount_on_per), reportDaySales.getDiscountPerQty().toString(),
                reportDaySales.getDiscountPer(), 1);
        this.addItemWithLang(PrintService.instance.getResources().getString(R.string.discount_on_pri), reportDaySales.getDiscountQty().toString(),
                reportDaySales.getDiscount(), PrintData.LANG_EN, 1);

        double nSales = Double.parseDouble(reportDaySales.getItemSales()) + Double.parseDouble(reportDaySales.getTopUps()) - Double.parseDouble(reportDaySales.getFocItem()) - Double.parseDouble(reportDaySales.getFocBill()) - Double.parseDouble(reportDaySales.getItemVoid()) - Double.parseDouble(reportDaySales.getBillVoid()) - Double.parseDouble(reportDaySales.getBillRefund()) - Double.parseDouble(reportDaySales.getDiscount()) - Double.parseDouble(reportDaySales.getDiscountPer());
        //this.addItem(PrintService.instance.getResources().getString(R.string.nett_sales), " ", BH.sub( BH.getBD(reportDaySales.getTotalSales()), BH.getBD(reportDaySales.getTotalTax()), true).toString(), 1);
        this.addItem(PrintService.instance.getResources().getString(R.string.nett_sales), " ", BH.getBD(nSales).toString(), 1);
//reportDay
        if (reportDayTaxs != null) {

            BigDecimal taxSvg = BH.getBD("0.00");
            for (int i = 0; i < reportDayTaxs.size(); i++) {
                //服务税
                if (reportDayTaxs.get(i).getTaxType().intValue() == 1) {
                    ReportDayTax reportDayTax = reportDayTaxs.get(i);

                    taxSvg = BH.add(taxSvg, BH.getBD(reportDayTax.getTaxAmount()), true);
                }
            }

            this.addItem(PrintService.instance.getResources().getString(R.string.next_saels), "", BH.add(taxSvg, BH.getBD(nSales), true).toString(), 1);
        } else {
            this.addItem(PrintService.instance.getResources().getString(R.string.next_saels), " ", BH.getBD(nSales).toString(), 1);
        }
        if (App.countryCode != ParamConst.CHINA) {
            this.addItem(PrintService.instance.getResources().getString(R.string.total_tax), " ", BH.getBD(reportDaySales.getTotalTax()).toString(), 1);
            this.addItem(PrintService.instance.getResources().getString(R.string.inclusive_tax), " ", BH.getBD(reportDaySales.getInclusiveTaxAmt()).toString(), 1);
        }

        BigDecimal overPaymentAmount = BH.getBD(ParamConst.DOUBLE_ZERO);
        if (reportDayPayments != null && reportDayPayments.size() > 0) {
            for (ReportDayPayment reportDayPayment : reportDayPayments) {
                BH.add(overPaymentAmount, BH.getBD(reportDayPayment.getOverPaymentAmount()), false);
            }
            if (overPaymentAmount.compareTo(BH.getBD(ParamConst.DOUBLE_ZERO)) > 0) {
                this.addItem("Custom Payment Change", " ", overPaymentAmount.toString(), 1);
            }
        }
        this.addItem(PrintService.instance.getResources().getString(R.string.rounding), " ", reportDaySales.getTotalBalancePrice(), 1);
        double grossTotal = nSales + Double.parseDouble(reportDaySales.getTotalTax()) + Double.parseDouble(reportDaySales.getTotalBalancePrice()) + overPaymentAmount.doubleValue();
        //	this.addItem(PrintService.instance.getResources().getString(R.string.gross_total), " ", BH.add(overPaymentAmount, BH.getBD(reportDaySales.getTotalSales()), true).toString(), 1);

        this.addItem(PrintService.instance.getResources().getString(R.string.gross_total), " ", BH.getBD(grossTotal).toString(), 1);
        this.addSectionHeader(PrintService.instance.getResources().getString(R.string.media));
        if (BH.getBD(reportDaySales.getCash()).compareTo(BH.getBD(ParamConst.DOUBLE_ZERO)) != 0)
            this.addItem(PrintService.instance.getResources().getString(R.string.cash), reportDaySales.getCashQty().toString(),
                    reportDaySales.getCash(), 1);
        if (App.countryCode == ParamConst.CHINA) {
            this.addItem(PrintService.instance.getResources().getString(R.string.alipay), reportDaySales.getAlipayQty().toString(),
                    reportDaySales.getAlipay(), 1);
            this.addItem(PrintService.instance.getResources().getString(R.string.weixin), reportDaySales.getWeixinpayQty().toString(),
                    reportDaySales.getWeixinpay(), 1);
        }
        if (App.countryCode != ParamConst.CHINA) {
            if (BH.getBD(reportDaySales.getPaypalpay()).compareTo(BH.getBD(ParamConst.DOUBLE_ZERO)) != 0)
                this.addItem(PrintService.instance.getResources().getString(R.string.paypal), reportDaySales.getPaypalpayQty() == null ? "0" : reportDaySales.getPaypalpayQty().toString(),
                        BH.getBD(reportDaySales.getPaypalpay()).toString(), 1);
            if (BH.getBD(reportDaySales.getStoredCard()).compareTo(BH.getBD(ParamConst.DOUBLE_ZERO)) != 0)
                this.addItem(PrintService.instance.getResources().getString(R.string.stored_card_use), reportDaySales.getStoredCardQty() == null ? "0" : reportDaySales.getStoredCardQty().toString(),
                        BH.getBD(reportDaySales.getStoredCard()).toString(), 1);
            if (BH.getBD(reportDaySales.getTopUps()).compareTo(BH.getBD(ParamConst.DOUBLE_ZERO)) != 0)
                this.addItem(PrintService.instance.getResources().getString(R.string.stored_card_charge), reportDaySales.getTopUpsQty() == null ? "0" : reportDaySales.getTopUpsQty().toString(),
                        BH.getBD(reportDaySales.getTopUps()).toString(), 1);
            if (BH.getBD(reportDaySales.getNets()).compareTo(BH.getBD(ParamConst.DOUBLE_ZERO)) != 0)
                this.addItem(PrintService.instance.getResources().getString(R.string.nets), reportDaySales.getNetsQty().toString(),
                        reportDaySales.getNets(), 1);
            if (BH.getBD(reportDaySales.getVisa()).compareTo(BH.getBD(ParamConst.DOUBLE_ZERO)) != 0)
                this.addItem(PrintService.instance.getResources().getString(R.string.visa), reportDaySales.getVisaQty().toString(),
                        reportDaySales.getVisa(), 1);
            if (BH.getBD(reportDaySales.getMc()).compareTo(BH.getBD(ParamConst.DOUBLE_ZERO)) != 0)
                this.addItem(PrintService.instance.getResources().getString(R.string.mc), reportDaySales.getMcQty().toString(),
                        reportDaySales.getMc(), 1);
            if (BH.getBD(reportDaySales.getAmex()).compareTo(BH.getBD(ParamConst.DOUBLE_ZERO)) != 0)
                this.addItem(PrintService.instance.getResources().getString(R.string.amex), reportDaySales.getAmexQty().toString(),
                        reportDaySales.getAmex(), 1);
            if (BH.getBD(reportDaySales.getJbl()).compareTo(BH.getBD(ParamConst.DOUBLE_ZERO)) != 0)
                this.addItem(PrintService.instance.getResources().getString(R.string.jbl), reportDaySales.getJblQty().toString(),
                        reportDaySales.getJbl(), 1);
            if (BH.getBD(reportDaySales.getUnionPay()).compareTo(BH.getBD(ParamConst.DOUBLE_ZERO)) != 0)
                this.addItem(PrintService.instance.getResources().getString(R.string.unionpay), reportDaySales.getUnionPayQty().toString(),
                        reportDaySales.getUnionPay(), 1);
            if (BH.getBD(reportDaySales.getDiner()).compareTo(BH.getBD(ParamConst.DOUBLE_ZERO)) != 0)
                this.addItem(PrintService.instance.getResources().getString(R.string.diner), reportDaySales.getDinerQty().toString(),
                        reportDaySales.getDiner(), 1);
            if (BH.getBD(reportDaySales.getHoldld()).compareTo(BH.getBD(ParamConst.DOUBLE_ZERO)) != 0)
                this.addItem(PrintService.instance.getResources().getString(R.string.boh), reportDaySales.getHoldldQty().toString(),
                        reportDaySales.getHoldld(), 1);
        }
        int totalDeliveryQty = 0;
        BigDecimal totalDelivery = BH.getBD(ParamConst.DOUBLE_ZERO);
        if (BH.getBD(reportDaySales.getDeliveroo()).compareTo(BH.getBD(ParamConst.DOUBLE_ZERO)) != 0) {
            this.addItem(PrintService.instance.getResources().getString(R.string.deliveroo), reportDaySales.getDeliverooQty().toString(),
                    BH.getBD(reportDaySales.getDeliveroo()).toString(), 1);
            totalDelivery = BH.add(totalDelivery, BH.getBD(reportDaySales.getDeliveroo()), false);
            totalDeliveryQty += reportDaySales.getDeliverooQty().intValue();
        }
        if (BH.getBD(reportDaySales.getUbereats()).compareTo(BH.getBD(ParamConst.DOUBLE_ZERO)) != 0) {
            this.addItem(PrintService.instance.getResources().getString(R.string.ubereats), reportDaySales.getUbereatsQty().toString(),
                    BH.getBD(reportDaySales.getUbereats()).toString(), 1);
            totalDelivery = BH.add(totalDelivery, BH.getBD(reportDaySales.getUbereats()), false);
            totalDeliveryQty += reportDaySales.getUbereatsQty().intValue();
        }
        if (BH.getBD(reportDaySales.getFoodpanda()).compareTo(BH.getBD(ParamConst.DOUBLE_ZERO)) != 0) {
            this.addItem(PrintService.instance.getResources().getString(R.string.foodpanda), reportDaySales.getFoodpandaQty().toString(),
                    BH.getBD(reportDaySales.getFoodpanda()).toString(), 1);
            totalDelivery = BH.add(totalDelivery, BH.getBD(reportDaySales.getFoodpanda()), false);
            totalDeliveryQty += reportDaySales.getFoodpandaQty().intValue();
        }
        if (BH.getBD(reportDaySales.getVoucher()).compareTo(BH.getBD(ParamConst.DOUBLE_ZERO)) != 0) {
            this.addItem(PrintService.instance.getResources().getString(R.string.voucher), reportDaySales.getVoucherQty().toString(),
                    BH.getBD(reportDaySales.getVoucher()).toString(), 1);
        }

        BigDecimal totalCustomPaymentAmount = BH.getBD(ParamConst.DOUBLE_ZERO);
        int totalCustomPaymentQty = 0;
        if (reportDayPayments != null && reportDayPayments.size() > 0) {
            for (ReportDayPayment reportDayPayment : reportDayPayments) {
                totalCustomPaymentAmount = BH.add(totalCustomPaymentAmount, BH.getBD(reportDayPayment.getPaymentAmount()), true);
                totalCustomPaymentQty += reportDayPayment.getPaymentQty();
                this.addItem(reportDayPayment.getPaymentName(), reportDayPayment.getPaymentQty().toString(),
                        BH.getBD(reportDayPayment.getPaymentAmount()).toString(), 1);
            }
            this.addItem("TOTAL Custom Payment", totalCustomPaymentQty + "",
                    totalCustomPaymentAmount.toString(), 1);
        }

        if (totalDelivery.compareTo(BH.getBD(ParamConst.DOUBLE_ZERO)) != 0) {
            this.addItem("TOTAL DELIVERY", totalDeliveryQty + "",
                    totalDelivery.toString(), 1);
        }
//        this.addItem(PrintService.instance.getResources().getString(R.string.ent), reportDaySales.getFocBillQty().toString(),
//                reportDaySales.getFocBill(), 1);
        if (BH.getBD(reportDaySales.getTotalCard()).compareTo(BH.getBD(ParamConst.DOUBLE_ZERO)) != 0)
            this.addItem(PrintService.instance.getResources().getString(R.string.total_card), reportDaySales.getTotalCardQty().toString(),
                    reportDaySales.getTotalCard(), 1);
        if (App.countryCode != ParamConst.CHINA) {
            if (BH.getBD(reportDaySales.getNets()).compareTo(BH.getBD(ParamConst.DOUBLE_ZERO)) != 0)
                this.addItem(PrintService.instance.getResources().getString(R.string.total_nets), reportDaySales.getNetsQty().toString(),
                        reportDaySales.getNets(), 1);
        }
        if (App.countryCode != ParamConst.CHINA) {
            if (BH.getBD(reportDaySales.getTotalCash()).compareTo(BH.getBD(ParamConst.DOUBLE_ZERO)) != 0)
                this.addItem(PrintService.instance.getResources().getString(R.string.total_cash), reportDaySales.getTotalCashQty().toString(),
                        reportDaySales.getTotalCash(), 1);
            if (BH.getBD(reportDaySales.getHoldld()).compareTo(BH.getBD(ParamConst.DOUBLE_ZERO)) != 0)
                this.addItem(PrintService.instance.getResources().getString(R.string.total_boh), reportDaySales.getHoldldQty().toString(),
                        reportDaySales.getHoldld(), 1);
//	        this.addItem(PrintService.instance.getResources().getString(R.string.total_ent), reportDaySales.getFocBillQty().toString(),
//	                reportDaySales.getFocBill(), 1);
        }
        if (BH.getBD(reportDaySales.getBillVoid()).compareTo(BH.getBD(ParamConst.DOUBLE_ZERO)) != 0
                || BH.getBD(reportDaySales.getItemVoid()).compareTo(BH.getBD(ParamConst.DOUBLE_ZERO)) != 0
                || BH.getBD(reportDaySales.getBillRefund()).compareTo(BH.getBD(ParamConst.DOUBLE_ZERO)) != 0
                || BH.getBD(reportDaySales.getRefundTax()).compareTo(BH.getBD(ParamConst.DOUBLE_ZERO)) != 0) {
            this.addSectionHeader(PrintService.instance.getResources().getString(R.string.vr_summary));
            this.addItem(PrintService.instance.getResources().getString(R.string.void_bills), reportDaySales.getBillVoidQty().toString(),
                    reportDaySales.getBillVoid(), 1);
            this.addItem(PrintService.instance.getResources().getString(R.string.void_items), reportDaySales.getItemVoidQty().toString(),
                    reportDaySales.getItemVoid(), 1);
            this.addItem(PrintService.instance.getResources().getString(R.string.total_void),
                    String.valueOf(reportDaySales.getItemVoidQty() + reportDaySales.getBillVoidQty()),
                    String.valueOf(BH.add(BH.getBD(reportDaySales.getItemVoid()),
                            BH.getBD(reportDaySales.getBillVoid()), true)), 1);
            this.addItem(PrintService.instance.getResources().getString(R.string.refund_bills), reportDaySales.getBillRefundQty().toString(),
                    reportDaySales.getBillRefund(), 1);
            this.addItem(PrintService.instance.getResources().getString(R.string.refund_taxes), "",
                    reportDaySales.getRefundTax(), 1);
            this.addItem(PrintService.instance.getResources().getString(R.string.total_refund), "",
                    BH.add(BH.getBD(reportDaySales.getBillRefund()), BH.getBD(reportDaySales.getRefundTax()), true).toString(), 1);
        }
        if (BH.getBD(reportDaySales.getDiscountPer()).compareTo(BH.getBD(ParamConst.DOUBLE_ZERO)) != 0
                || BH.getBD(reportDaySales.getDiscount()).compareTo(BH.getBD(ParamConst.DOUBLE_ZERO)) != 0) {
            this.addSectionHeader(PrintService.instance.getResources().getString(R.string.disc_summary));
            this.addItem(PrintService.instance.getResources().getString(R.string.discount_on_per), reportDaySales.getDiscountPerQty().toString(),
                    reportDaySales.getDiscountPer(), 1);
            this.addItemWithLang(PrintService.instance.getResources().getString(R.string.discount_on_pri), reportDaySales.getDiscountQty().toString(),
                    reportDaySales.getDiscount(), PrintData.LANG_EN, 1);
            this.addItem(PrintService.instance.getResources().getString(R.string.total_disc),
                    String.valueOf(reportDaySales.getDiscountPerQty() + reportDaySales.getDiscountQty()),
                    String.valueOf(BH.add(BH.getBD(reportDaySales.getDiscountPer()),
                            BH.getBD(reportDaySales.getDiscount()), true)), 1);
        }
        if (reportDaySales.getTakeawayQty() > 0) {
            this.addSectionHeader(PrintService.instance.getResources().getString(R.string.take_away));
            this.addItem(PrintService.instance.getResources().getString(R.string.take_away_sales),
                    BH.getBD(reportDaySales.getTakeawaySales()).toString(), 1);
            this.addItem(PrintService.instance.getResources().getString(R.string.take_away_tax),
                    BH.getBD(reportDaySales.getTakeawayTax()).toString(),
                    1);
            this.addItem(PrintService.instance.getResources().getString(R.string.take_away_qty),
                    String.valueOf(reportDaySales.getTakeawayQty()), 1);
        }
        if (App.countryCode != ParamConst.CHINA) {
            if (reportDayTaxs != null) {
                this.addSectionHeader(PrintService.instance.getResources().getString(R.string.tax_svc));
                BigDecimal taxSvc = BH.getBD("0.00");
                for (int i = 0; i < reportDayTaxs.size(); i++) {
                    ReportDayTax reportDayTax = reportDayTaxs.get(i);
                    this.addItem(reportDayTax.getTaxName(), "", reportDayTax.getTaxAmount(), 1);
                    taxSvc = BH.add(taxSvc, BH.getBD(reportDayTax.getTaxAmount()), true);
                }
                this.addItem(PrintService.instance.getResources().getString(R.string.inclusive_tax), "", reportDaySales.getInclusiveTaxAmt(), 1);
                taxSvc = BH.add(taxSvc, BH.getBD(reportDaySales.getInclusiveTaxAmt()), true);
                this.addItem(PrintService.instance.getResources().getString(R.string.total_taxsvc), "", taxSvc.toString(), 1);
            }
        }

        this.addSectionHeader(PrintService.instance.getResources().getString(R.string.drawer));
        if (reportUserOpenDrawerList != null && reportUserOpenDrawerList.size() > 0) {
            for (int i = 0; i < reportUserOpenDrawerList.size(); i++) {
                ReportUserOpenDrawer reportUserOpenDrawer = reportUserOpenDrawerList.get(i);
                this.addItem(reportUserOpenDrawer.getUserName(), PrintService.instance.getResources().getString(R.string.open_drawer), reportUserOpenDrawer.getTimes() + "", 1);
            }
        }
        this.addItem(PrintService.instance.getResources().getString(R.string.start_drawer), reportDaySales.getStartDrawerAmount(), 1);
        this.addItem(PrintService.instance.getResources().getString(R.string.total_cash), reportDaySales.getTotalCash(), 1);
        this.addItem("Stored-Card Cash Charge", BH.getBD(reportDaySales.getCashTopUp()).toString(), 1);
        this.addItem(PrintService.instance.getResources().getString(R.string.cash_in), reportDaySales.getCashInAmt(), 1);
        this.addItem(PrintService.instance.getResources().getString(R.string.cash_out), reportDaySales.getCashOutAmt(), 1);
        this.addItem(PrintService.instance.getResources().getString(R.string.expected_in_drawer), "", reportDaySales.getExpectedAmount(), 1);
        this.addItem(PrintService.instance.getResources().getString(R.string.actual_in_drawer), "", reportDaySales.getWaiterAmount(), 1);
        this.addItem(PrintService.instance.getResources().getString(R.string.difference), "", reportDaySales.getDifference(), 1);

        this.addBlankLine();
        //this.addItem(PrintService.instance.getResources().getString(R.string.nett_sales),"",  nettSales.toString(), 1);
        this.addItem(PrintService.instance.getResources().getString(R.string.nett_sales), "", reportDaySales.getTotalSales(), 1);

        this.addItem(PrintService.instance.getResources().getString(R.string.total_of_bills), "", String.valueOf(reportDaySales.getTotalBills()), 1);
        this.addItem(PrintService.instance.getResources().getString(R.string.avg_bills), "", BH.div(
                BH.getBD(reportDaySales.getTotalSales()),
                BH.getBD(reportDaySales.getTotalBills()),
                true).toString(), 1);
        this.addItem(PrintService.instance.getResources().getString(R.string.total_of_paxs), "", String.valueOf(reportDaySales.getPersonQty()), 1);
        BigDecimal vagPaxs = BH.getBD(ParamConst.DOUBLE_ZERO);
        if (reportDaySales.getPersonQty().intValue() > 0) {
            vagPaxs = BH.div(
                    BH.getBD(reportDaySales.getTotalSales()),
                    BH.getBD(reportDaySales.getPersonQty()),
                    true);
        }

        this.addItem(PrintService.instance.getResources().getString(R.string.avg_paxs), "", vagPaxs.toString(), 1);
        this.addItem(PrintService.instance.getResources().getString(R.string.total_open_items), "", String.valueOf(reportDaySales.getOpenCount()), 1);


        if (reportSessionSalesList != null && reportSessionSalesList.size() > 0) {
            addHortionalLine(this.charSize);
            for (int i = 0; i < reportSessionSalesList.size(); i++) {
                ReportSessionSales reportSessionSales = reportSessionSalesList.get(i);
                this.addItem("Shift Detail For Shift Number", "" + (i + 1), 1);
                this.addItem(PrintService.instance.getResources().getString(R.string.total_cash), BH.getBD(reportSessionSales.getCash()).toString(), 1);
                this.addItem("Stored-Card Cash Charge", BH.getBD(reportSessionSales.getCashTopup()).toString(), 1);
                this.addItem(PrintService.instance.getResources().getString(R.string.expected_in_drawer), BH.getBD(reportSessionSales.getExpectedAmount()).toString(), 1);
                this.addItem(PrintService.instance.getResources().getString(R.string.actual_in_drawer), BH.getBD(reportSessionSales.getActualAmount()).toString(), 1);
                this.addItem(PrintService.instance.getResources().getString(R.string.difference), BH.getBD(reportSessionSales.getDifference()).toString(), 1);
                if (i < reportSessionSalesList.size() - 1)
                    this.addBlankLine();
            }
        }
    }

    public void setData(ArrayList<PrintData> data) {
        this.data = data;
    }
}
