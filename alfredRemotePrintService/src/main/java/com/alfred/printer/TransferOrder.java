package com.alfred.printer;

import com.alfred.print.jobs.PrintJob;
import com.alfred.print.jobs.Priority;
import com.alfred.remote.printservice.PrintService;
import com.alfred.remote.printservice.R;
import com.birbit.android.jobqueue.Params;

/**
 * Created by Arif S. on 2019-09-11
 */
public class TransferOrder extends PrintJob {
    public TransferOrder(String uuid, Long bizDate) {
        super(new Params(Priority.MID).requireNetwork().persist().groupBy("transfer"),
                "transfer", uuid, bizDate);
        if (this.charSize == 33) {
            KOTPrint.FIXED_COL2_QTY = 4;
            KOTPrint.FIXED_COL2_SPACE = 1;
        }
    }


    public void addTitle(String tableName) {
        addFeed();
        StringBuilder sbr = new StringBuilder();
        sbr.append(tableName);
        sbr.append(reNext);

        PrintData header = new PrintData();
        header.setDataFormat(PrintData.FORMAT_TXT);
        header.setFontsize(2);
        header.setTextAlign(PrintData.ALIGN_CENTRE);
        header.setText(sbr.toString());
        this.data.add(header);

        addHortionalLine(this.charSize);
    }

    public void addItem(String itemName) {
        PrintData printData = new PrintData();
        printData.setDataFormat(PrintData.FORMAT_TXT);
        printData.setFontsize(2);
        printData.setMarginTop(20);
        printData.setLanguage(PrintData.LANG_EN);
        printData.setText(itemName);
        this.data.add(printData);
    }

    public void addLineSpace(int lines) {
        PrintData kot = new PrintData();
        kot.setDataFormat(PrintData.FORMAT_TXT);
        kot.setFontsize(1);
        kot.setLanguage(PrintData.LANG_CN);
        kot.setText(reNext);
        this.data.add(kot);
    }

    public void addFeed() {
        PrintData kot = new PrintData();
        kot.setDataFormat(PrintData.FORMAT_FEED);
        kot.setMarginTop(3);
        this.data.add(kot);
    }

}
