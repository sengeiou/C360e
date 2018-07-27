package com.alfred.printer;

import android.text.TextUtils;
import android.util.Base64;

import com.alfred.print.jobs.PrintJob;
import com.alfred.print.jobs.Priority;
import com.alfred.remote.printservice.PrintService;
import com.alfred.remote.printservice.R;
import com.alfredbase.utils.BH;
import com.alfredbase.utils.TimeUtil;
import com.birbit.android.jobqueue.Params;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BillTscPrint extends PrintJob {

    public static int FIXED_COL4_SPACE = 2;
    public static int FIXED_COL4_PRICE = 10; //in case of 48 dots width, QTY col = 10dots
    public static int FIXED_COL4_QTY = 6; //in case of 48 dots width, QTY col = 10dots
    public static int FIXED_COL4_TOTAL = 12; //in case of 48 dots width, QTY col = 10dots

    public int nameSize = 17;

    public static int COL4_ITEMNAME; // Width = CharSize/scale - FIXED_COL2_QTY/scale -
    // FIXED_COL2_PRICE/scale- FIXED_COL2_TOTAL/scale- FIXED_COL2_SPACE *3

    public BillTscPrint(String uuid, Long bizDate) {
        super(new Params(Priority.HIGH).requireNetwork().persist().groupBy("lable"), "lable", uuid, bizDate);
//		if (this.charSize == 33) {
//			BillTscPrint.FIXED_COL4_TOTAL = 8;
//			BillTscPrint.FIXED_COL4_PRICE = 7;
//			BillTscPrint.FIXED_COL4_QTY = 4;
//			BillTscPrint.FIXED_COL4_SPACE = 1;
//		}
    }


    public void AddRestaurantInfo(String logo, String name, String id, int num, String tNum, String itemName, String modifier, String price, boolean Identification) {

        if (logo != null && logo.length() > 0) {
            PrintTscData logoimg = new PrintTscData();
            logoimg.setDataFormat(PrintTscData.FORMAT_IMG);
            logoimg.addImage(Base64.decode(logo, Base64.DEFAULT));
//			logoimg.setTextAlign(PrintTscData.ALIGN_CENTRE);
            this.tdata.add(logoimg);
        }


        //address


        String strno;
        StringBuilder addbuf = new StringBuilder();

        int size = 26;
        //  int padlen = size - id.getBytes("GBK").length*2;
        // strnum = StringUtil.padLeft(num + "/" + tNum+" ", padlen/2);
        strno = StringUtil.padLeft(id + "", size / 2 - 1);
        addbuf.append(strno);

        PrintTscData radd = new PrintTscData();
        radd.setDataFormat(PrintTscData.FORMAT_TXT);
        radd.setFontsizeX(2);
        radd.setFontsizeY(1);

        radd.setX(0);
        radd.setY(10);

        radd.setText(addbuf.toString());
        this.tdata.add(radd);


        ++num;
        String strnum;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd ");// HH:mm:ss

        Date date = new Date(System.currentTimeMillis());
        ;

        StringBuilder numbuf = new StringBuilder();
        String dates = simpleDateFormat.format(date).toString().trim();
        numbuf.append(dates);
        int nsize = 52;
        int padlen = 0;
        try {
            padlen = size - dates.getBytes("GBK").length * 2;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        strnum = StringUtil.padLeft(num + "/" + tNum + " ", padlen * 2 + 1);

        numbuf.append(strnum);
        //name
        PrintTscData rInfo = new PrintTscData();
        rInfo.setDataFormat(PrintTscData.FORMAT_TXT);
        rInfo.setFontsizeX(1);
        rInfo.setFontsizeY(1);
        rInfo.setX(25);
        rInfo.setY(45);
        rInfo.setText(numbuf.toString());
        this.tdata.add(rInfo);


        StringBuilder totalbuf = new StringBuilder();

        totalbuf.append(price);

        PrintTscData total = new PrintTscData();
        total.setDataFormat(PrintTscData.FORMAT_TXT);
        total.setFontsizeX(1);
        total.setFontsizeY(1);
        total.setX(230);
        total.setY(80);
        total.setText(totalbuf.toString());
        this.tdata.add(total);
        toMultiLine(itemName, nameSize, 80);
        if (!TextUtils.isEmpty(modifier)) {
            String newmod = modifier.substring(0, modifier.length() - 1);
            modMultiLine(itemName, 19, 175, newmod);
        }
//        ++num;
//        String strnum;
//        StringBuilder numbuf = new StringBuilder();
//        addbuf.append(" ");
//        int size=26;
//        //  int padlen = size - id.getBytes("GBK").length*2;
//        // strnum = StringUtil.padLeft(num + "/" + tNum+" ", padlen/2);
//        strnum = StringUtil.padLeft(id+" ", size/2-5);
//        addbuf.append(strnum);
//
//        PrintTscData radd = new PrintTscData();
//        radd.setDataFormat(PrintTscData.FORMAT_TXT);
//        radd.setFontsizeX(1);
//        radd.setFontsizeY(1);
//
//        radd.setX(25);
//        radd.setY(20);
//
//        radd.setText(addbuf.toString());
//        this.tdata.add(radd);

        ////customized fields
        //  ++num;
//        StringBuilder ctbuf = new StringBuilder();
//        ctbuf.append(num + "/" + tNum);
//        PrintTscData ctadd = new PrintTscData();
//        ctadd.setDataFormat(PrintTscData.FORMAT_TXT);
//        ctadd.setFontsizeX(2);
//        ctadd.setFontsizeY(1);
//        ctadd.setX(200);
//        ctadd.setY(20);
//        //ctadd.setTextAlign(PrintTscData.ALIGN_CENTRE);
//        ctadd.setText(ctbuf.toString() + reNext);
//        this.tdata.add(ctadd);


//        PrintTscData line = new PrintTscData();
//        line.setDataFormat(PrintTscData.FORMAT_BAR);
//        line.setX(0);
//        line.setY(50);
//        line.setText("");
//        this.tdata.add(line);

//        String lstr = new String(new char[14]).replace('\0', '-').concat(reNext);
//        PrintTscData line = new PrintTscData();
//        line.setDataFormat(PrintData.FORMAT_TXT);
//        line.setX(0);
//        line.setY(40);
//        line.setText(lstr);
//        this.tdata.add(line);
//        toMultiLine(itemName, 20);


//if(!TextUtils.isEmpty(modifier)) {
//
//    String newmod=modifier.substring(0,modifier.length()-1);
//    StringBuilder modifiers = new StringBuilder();
//
//    modifiers.append(newmod);
//
//    PrintTscData modif = new PrintTscData();
//    modif.setDataFormat(PrintTscData.FORMAT_TXT);
//    modif.setFontsizeX(1);
//    modif.setFontsizeY(1);
//    modif.setX(25);
//    modif.setY(155);
//    //radd.setTextAlign(PrintTscData.ALIGN_CENTRE);
//    modif.setText(modifiers.toString());
//    this.tdata.add(modif);
//}


        StringBuilder rename = new StringBuilder();

        rename.append(name);

        PrintTscData names = new PrintTscData();
        names.setDataFormat(PrintTscData.FORMAT_TXT);
        names.setFontsizeX(1);
        names.setFontsizeY(1);
        names.setX(25);
        names.setY(210);
        //radd.setTextAlign(PrintTscData.ALIGN_CENTRE);
        names.setText(rename.toString());
        this.tdata.add(names);

//

        PrintTscData reset = new PrintTscData();
        reset.setDataFormat(PrintTscData.FORMAT_RESET);
        this.tdata.add(reset);
//
//		//	addtdata(this.tdata);
//		//addHortionalLine(this.charSize);
    }

    //商品名换行
    private void toMultiLine(String str, int len, int s) {
        String[] splitedcontents = new String[0];
        int size;
        int y;
        y = s;
        try {
            splitedcontents = StringUtil.formatLn(nameSize, str);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (splitedcontents.length >= 3) {
            size = 3;
        } else {
            size = splitedcontents.length;
        }
        for (int i = 0; i < size; i++) {
            StringBuilder itemnbuf = new StringBuilder();
            itemnbuf.append(splitedcontents[i]);
            PrintTscData itemi = new PrintTscData();
            itemi.setDataFormat(PrintTscData.FORMAT_TXT);
            itemi.setFontsizeX(1);
            itemi.setFontsizeY(1);
            itemi.setX(5);
            itemi.setY(y);
            //radd.setTextAlign(PrintTscData.ALIGN_CENTRE);
            itemi.setText(itemnbuf.toString());
            this.tdata.add(itemi);
            y = y + 30;

        }
//
//        char[] chs = str.toCharArray();
//
//        byte[] buff = str.getBytes();
//        int f = buff.length;
//
//        System.out.println(f);
//        StringBuffer sb = new StringBuffer();
//        int y;
//        y = s;
//        int j = 0;
//        int size;
//
//        if (f > len * 3) {
//            size = len * 3;
//        } else {
//            size = f;
//        }
//        for (int i = 0, sum = 0; i < size; i++) {
//
//            ++sum;
//
//            sb.append(chs[i]);
//            if (sum >= len) {
//                //  sb.setLength(0);
//                sum = 0;
//                ++j;
//                StringBuilder itemnbuf = new StringBuilder();
//                itemnbuf.append(sb);
//                PrintTscData itemi = new PrintTscData();
//                itemi.setDataFormat(PrintTscData.FORMAT_TXT);
//                itemi.setFontsizeX(1);
//                itemi.setFontsizeY(1);
//                itemi.setX(5);
//                itemi.setY(y);
//                //radd.setTextAlign(PrintTscData.ALIGN_CENTRE);
//                itemi.setText(itemnbuf.toString());
//                this.tdata.add(itemi);
//                y = y + 30;
//                sb.setLength(0);
//            }
//
//
//            if (i == size - 1 && sum < len && sum > 0) {
//                if (j == 1) {
//
//                    PrintTscData itemi = new PrintTscData();
//                    itemi.setDataFormat(PrintTscData.FORMAT_TXT);
//                    itemi.setFontsizeX(1);
//                    itemi.setFontsizeY(1);
//                    itemi.setX(5);
//                    itemi.setY(y);
//                    //radd.setTextAlign(PrintTscData.ALIGN_CENTRE);
//                    itemi.setText(sb.toString());
//                    this.tdata.add(itemi);
//
//                } else if (j == 2) {
//
//                    PrintTscData itemi = new PrintTscData();
//                    itemi.setDataFormat(PrintTscData.FORMAT_TXT);
//                    itemi.setFontsizeX(1);
//                    itemi.setFontsizeY(1);
//                    itemi.setX(5);
//                    itemi.setY(y);
//                    //radd.setTextAlign(PrintTscData.ALIGN_CENTRE);
//                    itemi.setText(sb.toString());
//                    this.tdata.add(itemi);
//                }
//            }
//
//            if (size < len) {
//                PrintTscData itemi = new PrintTscData();
//                itemi.setDataFormat(PrintTscData.FORMAT_TXT);
//                itemi.setFontsizeX(1);
//                itemi.setFontsizeY(1);
//                itemi.setX(5);
//                itemi.setY(s);
//                //radd.setTextAlign(PrintTscData.ALIGN_CENTRE);
//                itemi.setText(sb.toString());
//                this.tdata.add(itemi);
//            }
//
//
//        }
//        // return sb.toString();
    }

    //配料换行
    private void modMultiLine(String itemname, int len, int s, String mod) {
        String[] splitedmod = new String[0];
        String[] splitedcontents = new String[0];
        try {
            splitedcontents = StringUtil.formatLn(nameSize, itemname);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        int size;
        int y;
        y = s;


        try {
            splitedmod = StringUtil.formatLn(22, mod);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

            size = splitedmod.length;

        if(splitedcontents.length>=3)
        {
            size=1;
        }else if(splitedcontents.length>1&&splitedcontents.length<=2) {
            if(size>=2){
                size=2;
            }
            y=y-30;
        }else {
            if(size>=3){
                size=3;
            }
            y=y-60;

        }

        for (int i = 0; i < size; i++) {
            StringBuilder itemnbuf = new StringBuilder();
                itemnbuf.append(splitedmod[i]);
                PrintTscData itemi = new PrintTscData();
                itemi.setDataFormat(PrintTscData.FORMAT_TXT);
                itemi.setFontsizeX(1);
                itemi.setFontsizeY(1);
                itemi.setX(25);
                itemi.setY(y);
                //radd.setTextAlign(PrintTscData.ALIGN_CENTRE);
                itemi.setText(itemnbuf.toString());
                this.tdata.add(itemi);
                y = y + 30;

        }
//        byte[] chs = itemname.getBytes();
//        int line = 4;
//        int nameLine = 0;
//        char[] mods = mod.toCharArray();
//        byte[] modchs = mod.getBytes();
//        StringBuffer msb = new StringBuffer();
//        int y;
//        y = s;
//        int j = 0;
//        int modsize = 0;
//
//        if (chs.length >= nameSize * 2) {
//            nameLine = 3;
//            //    modsize = len * 1;
//            if (modchs.length > len * 1) {
//                modsize = len * 1;
//
//            } else {
//                modsize = modchs.length;
//            }
//
//        } else if (chs.length <= nameSize) {
//            nameLine = 1;
//            y = y - 60;
//            if (modchs.length > len * 3) {
//                modsize = len * 3;
//
//
//            } else {
//                modsize = modchs.length;
//            }
//            //   modsize = len * 3;
//        } else if (chs.length < nameSize * 2 && chs.length > nameSize * 1) {
//            //   modsize = modchs.length;
//            nameLine = 2;
//
//            y = y - 30;
//            if (modchs.length > len * 2) {
//                modsize = len * 2;
//            } else {
//                modsize = modchs.length;
//            }
//        }
//
//        for (int i = 0, sum = 0; i < modsize; i++) {
//            ++sum;
//            msb.append(mods[i]);
//            if (sum >= len) {
//                //  sb.setLength(0);
//                sum = 0;
//                ++j;
//                StringBuilder itemnbuf = new StringBuilder();
//                itemnbuf.append(msb);
//                PrintTscData itemi = new PrintTscData();
//                itemi.setDataFormat(PrintTscData.FORMAT_TXT);
//                itemi.setFontsizeX(1);
//                itemi.setFontsizeY(1);
//                itemi.setX(25);
//                itemi.setY(y);
//                //radd.setTextAlign(PrintTscData.ALIGN_CENTRE);
//                itemi.setText(itemnbuf.toString());
//                this.tdata.add(itemi);
//                y = y + 30;
//                msb.setLength(0);
//            }
//
//
//            if (i == modsize - 1 && sum < len && sum > 0) {
//                if (j == 1) {
//
//                    PrintTscData itemi = new PrintTscData();
//                    itemi.setDataFormat(PrintTscData.FORMAT_TXT);
//                    itemi.setFontsizeX(1);
//                    itemi.setFontsizeY(1);
//                    itemi.setX(25);
//                    itemi.setY(y);
//                    //radd.setTextAlign(PrintTscData.ALIGN_CENTRE);
//                    itemi.setText(msb.toString());
//                    this.tdata.add(itemi);
//
//                } else if (j == 2) {
//
//                    PrintTscData itemi = new PrintTscData();
//                    itemi.setDataFormat(PrintTscData.FORMAT_TXT);
//                    itemi.setFontsizeX(1);
//                    itemi.setFontsizeY(1);
//                    itemi.setX(25);
//                    itemi.setY(y);
//                    //radd.setTextAlign(PrintTscData.ALIGN_CENTRE);
//                    itemi.setText(msb.toString());
//                    this.tdata.add(itemi);
//                }
//            }
//
//            if (modsize < len) {
//                PrintTscData itemi = new PrintTscData();
//                itemi.setDataFormat(PrintTscData.FORMAT_TXT);
//                itemi.setFontsizeX(1);
//                itemi.setFontsizeY(1);
//                itemi.setX(25);
//                itemi.setY(y);
//                //radd.setTextAlign(PrintTscData.ALIGN_CENTRE);
//                itemi.setText(msb.toString());
//                this.tdata.add(itemi);
//            }
//
//
//        }
//        // return sb.toString();
    }
//
}
