package com.alfredposclient.view;

import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.alfredbase.BaseActivity;
import com.alfredbase.global.CoreData;
import com.alfredbase.javabean.Restaurant;
import com.alfredbase.javabean.RevenueCenter;
import com.alfredbase.javabean.User;
import com.alfredbase.utils.RxBus;
import com.alfredbase.utils.TextTypeFace;
import com.alfredbase.view.Numerickeyboard;
import com.alfredposclient.R;
import com.alfredposclient.global.App;
import com.alfredposclient.global.UIHelp;

/**
 * Created by Alex on 16/12/12.
 */

public class ReloginDialog implements View.OnClickListener, Numerickeyboard.KeyBoardClickListener{
    private static final int KEY_LENGTH = 5;
    private static final int STATE_IN_ENTER_ID = 0;
    private static final int STATE_IN_ENTER_PASSWORD = 1;
    private TextView tv_logout;
    private TextView tv_rest_name;
    private TextView tv_login_tips;
    private TextView tv_psw_1;
    private TextView tv_psw_2;
    private TextView tv_psw_3;
    private TextView tv_psw_4;
    private TextView tv_psw_5;
    private String employee_ID;
    private String password;
    private StringBuffer keyBuf = new StringBuffer();
    /**
     * 当前键盘输入对应的状态，0表示输入的员工ID，1表示输入的密码
     */
    private int state = STATE_IN_ENTER_ID;
    public BaseActivity parent;
    public View contentView;
    private Dialog dialog;
    public TextTypeFace textTypeFace;
    private User user;

    public ReloginDialog(BaseActivity parent) {
        this.parent = parent;
        user = App.instance.getUser();
        initView();
    }

    private void initView(){
        dialog = new Dialog(parent, com.alfredbase.R.style.base_dialog);
        contentView = LayoutInflater.from(parent).inflate(
                R.layout.re_login_view, null);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(contentView);
        Numerickeyboard numerickeyboard = (Numerickeyboard) contentView.findViewById(R.id.view_numerickeyboard);
        numerickeyboard.setParams(parent);
        numerickeyboard.setKeyBoardClickListener(this);
        textTypeFace = TextTypeFace.getInstance();
        textTypeFace.init(parent);
        tv_logout = (TextView) contentView.findViewById(R.id.tv_logout);
        tv_rest_name = (TextView) contentView.findViewById(R.id.tv_rest_name);
        tv_login_tips = (TextView) contentView.findViewById(R.id.tv_login_tips);
        textTypeFace.setTrajanProRegular(tv_logout);
        textTypeFace.setTrajanProRegular(tv_rest_name);
        textTypeFace.setTrajanProRegular(tv_login_tips);
        tv_psw_1 = (TextView) contentView.findViewById(R.id.tv_psw_1);
        tv_psw_2 = (TextView) contentView.findViewById(R.id.tv_psw_2);
        tv_psw_3 = (TextView) contentView.findViewById(R.id.tv_psw_3);
        tv_psw_4 = (TextView) contentView.findViewById(R.id.tv_psw_4);
        tv_psw_5 = (TextView) contentView.findViewById(R.id.tv_psw_5);
        tv_logout.setOnClickListener(this);
        Restaurant rest = CoreData.getInstance().getRestaurant();
        if(rest != null)
            tv_rest_name.setText(rest.getRestaurantName()+"");
        refresh();

    }

    public void show(){
        if (parent == null || parent.isFinishing())
            return;
        if(dialog.isShowing())
            return;
        dialog.show();
        tv_logout.setVisibility(View.VISIBLE);
    }

    public void dissmiss(){
        if(dialog != null && dialog.isShowing())
            dialog.dismiss();
        App.instance.startAD();
    }


    private void refresh(){
        String title = parent.getString(R.string.cashier_login_tips1);
        if(state == STATE_IN_ENTER_ID){
            tv_logout.setVisibility(View.INVISIBLE);
        }else{
            title = parent.getString(R.string.cashier_login_tips2) + "(" + user.getFirstName() + "." + user.getLastName() +")";
        }
        tv_login_tips.setText(title);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_logout:
                state = STATE_IN_ENTER_ID;
                user = null;
                refresh();
                break;
        }
    }

    @Override
    public void onKeyBoardClick(String key) {
        if (keyBuf.length() >= KEY_LENGTH)
            return;
        if (key.equals("X")) {
            if (keyBuf.length() > 0) {
                keyBuf.deleteCharAt(keyBuf.length() - 1);
            }
        } else {
            keyBuf.append(key);
        }
        int key_len = keyBuf.length();
        setPassword(key_len);
        if (key_len == KEY_LENGTH) {
            if (state == STATE_IN_ENTER_ID) {
                String title = parent.getString(R.string.cashier_login_tips1);
                ((TextView) (contentView.findViewById(R.id.tv_login_tips))).setText(title);

//                state = STATE_IN_ENTER_PASSWORD;
                employee_ID = keyBuf.toString();
                keyBuf.delete(0, key_len);
                setPassword(keyBuf.length());
//            } else if (state == STATE_IN_ENTER_PASSWORD) {
                password = keyBuf.toString();
                if(user == null) {
//                     user = CoreData.getInstance().getUser(employee_ID,
//                            password);
                    user = CoreData.getInstance().getUserByEmpId(Integer.parseInt(employee_ID));
                }
                boolean cashierAccess = false;
                if (user != null) {
                    RevenueCenter revenueCenter = App.instance
                            .getRevenueCenter();
                    cashierAccess = CoreData.getInstance()
                            .checkUserCashierAccessInRevcenter(user,
                                    revenueCenter.getRestaurantId(),
                                    revenueCenter.getId());

                    if (cashierAccess) {
                        User appUser = App.instance.getUser();
                        App.instance.setUser(user);
                        if(appUser != null){
                            if(appUser.getId().intValue() != user.getId().intValue()){
                                RxBus.getInstance().post(RxBus.RX_MSG_1, new Integer(1));
                            }
                        }

//                        parent.overridePendingTransition(
//                                R.anim.slide_bottom_in, R.anim.slide_top_out);
//                        RxBus.getInstance()
                        dissmiss();
                        return;
                    } else {
                        UIHelp.showToast(parent, parent.getResources().getString(R.string.login_required));
//                        String title = parent.getString(R.string.cashier_login_tips1);
//                        ((TextView) (contentView.findViewById(R.id.tv_login_tips))).setText(title);
                        state = STATE_IN_ENTER_ID;
                        refresh();
                        employee_ID = null;
                        password = null;
                    }
                }else{
                    UIHelp.showToast(parent, parent.getResources().getString(R.string.invalid_employee));
//                    String title = parent.getString(R.string.cashier_login_tips1);
//                    ((TextView) (contentView.findViewById(R.id.tv_login_tips))).setText(title);
                    state = STATE_IN_ENTER_ID;
                    refresh();
                    employee_ID = null;
                    password = null;
                }
                keyBuf.delete(0, key_len);
                setPassword(keyBuf.length());
            }
        }
    }
    private void setPassword(int key_len) {
        switch (key_len) {
            case 0: {
                tv_psw_1.setText("");
                tv_psw_2.setText("");
                tv_psw_3.setText("");
                tv_psw_4.setText("");
                tv_psw_5.setText("");
                break;
            }
            case 1: {
                tv_psw_1.setText("*");
                tv_psw_2.setText("");
                tv_psw_3.setText("");
                tv_psw_4.setText("");
                tv_psw_5.setText("");
                break;
            }
            case 2: {
                tv_psw_1.setText("*");
                tv_psw_2.setText("*");
                tv_psw_3.setText("");
                tv_psw_4.setText("");
                tv_psw_5.setText("");
                break;
            }
            case 3: {
                tv_psw_1.setText("*");
                tv_psw_2.setText("*");
                tv_psw_3.setText("*");
                tv_psw_4.setText("");
                tv_psw_5.setText("");
                break;
            }
            case 4: {
                tv_psw_1.setText("*");
                tv_psw_2.setText("*");
                tv_psw_3.setText("*");
                tv_psw_4.setText("*");
                tv_psw_5.setText("");
                break;
            }
            case 5: {
                tv_psw_1.setText("*");
                tv_psw_2.setText("*");
                tv_psw_3.setText("*");
                tv_psw_4.setText("*");
                tv_psw_5.setText("*");
                break;
            }
            default:
                break;
        }
    }
}
