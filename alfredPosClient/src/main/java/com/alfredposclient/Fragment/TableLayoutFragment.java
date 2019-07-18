package com.alfredposclient.Fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alfredbase.BaseActivity;
import com.alfredbase.LoadingDialog;
import com.alfredbase.ParamConst;
import com.alfredbase.global.BugseeHelper;
import com.alfredbase.global.CoreData;
import com.alfredbase.http.APIName;
import com.alfredbase.http.ResultCode;
import com.alfredbase.javabean.Order;
import com.alfredbase.javabean.OrderDetail;
import com.alfredbase.javabean.PlaceInfo;
import com.alfredbase.javabean.PrinterTitle;
import com.alfredbase.javabean.TableInfo;
import com.alfredbase.store.sql.OrderDetailSQL;
import com.alfredbase.store.sql.OrderSQL;
import com.alfredbase.store.sql.OrderSplitSQL;
import com.alfredbase.store.sql.PlaceInfoSQL;
import com.alfredbase.store.sql.TableInfoSQL;
import com.alfredbase.utils.BitmapUtil;
import com.alfredbase.utils.ButtonClickTimer;
import com.alfredbase.utils.DialogFactory;
import com.alfredbase.utils.IntegerUtils;
import com.alfredbase.utils.LogUtil;
import com.alfredbase.utils.ObjectFactory;
import com.alfredbase.utils.ScreenSizeUtil;
import com.alfredbase.utils.ViewTouchUtil;
import com.alfredposclient.R;
import com.alfredposclient.activity.MainPage;
import com.alfredposclient.global.App;
import com.alfredposclient.global.SyncCentre;
import com.alfredposclient.global.UIHelp;
import com.alfredposclient.utils.ImageUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Alex on 16/9/22.
 */

public class TableLayoutFragment extends Fragment implements View.OnClickListener {
    private final static String TAG = TableLayoutFragment.class.getSimpleName();
    public final static int UPDATE_PLACE_TABLE_SUCCEED = 10001;
    public final static int UPDATE_PLACE_TABLE_FAILURE = -10001;
    private int name_w = 60;
    private ListView lv_place;
    private ListView lv_table_list;
    private RelativeLayout rl_tables;
    private RelativeLayout rl_create_table;
    private int selectPlaceIndex = -1;
    private PlaceAdapter placeAdapter;
    private TableAdapter tableAdapter;
    private String[] images = {"table_1_1", "table_1_2", "table_2_1", "table_4_1", "table_6_1", "table_6_2", "table_6_3"};
    private boolean canEdit = false;
    private ImageView iv_more_table;
    private TextView tv_table_edit;
    private TextView tv_cancel;
    private List<TableInfo> newTables = new ArrayList<TableInfo>();
    private List<PlaceInfo> places = new ArrayList<PlaceInfo>();
    private BaseActivity mainPage;
    private LoadingDialog loadingDialog;
    private LinearLayout ll_table_left;
    private LinearLayout ll_table_right;

    //    private RelativeLayout rl_table_area;
//    private int width;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        System.out.println("-----------tttttttt");
        mainPage = (BaseActivity) getActivity();
        name_w = ScreenSizeUtil.dip2px(mainPage, 85);
        loadingDialog = new LoadingDialog(mainPage);
        loadingDialog.setTitle(mainPage.getResources().getString(R.string.loading));
        Log.e(TAG, "onCreateView");
        final View view = inflater.inflate(R.layout.table_layout, container, false);
        lv_place = (ListView) view.findViewById(R.id.lv_place);
        lv_table_list = (ListView) view.findViewById(R.id.lv_table_list);
        iv_more_table = (ImageView) view.findViewById(R.id.iv_more_table);
        tv_table_edit = (TextView) view.findViewById(R.id.tv_table_edit);
        tv_cancel = (TextView) view.findViewById(R.id.tv_cancel);
        rl_tables = (RelativeLayout) view.findViewById(R.id.rl_tables);
        rl_create_table = (RelativeLayout) view.findViewById(R.id.rl_create_table);
        ll_table_left = (LinearLayout) view.findViewById(R.id.ll_table_left);
//        rl_table_area = (RelativeLayout) view.findViewById(R.id.rl_table_area);
//        ViewTreeObserver vto = rl_table_area.getViewTreeObserver();
//        width = (int) (ScreenSizeUtil.height - ScreenSizeUtil.dip2px(mainPage, 40.0f))*3/2;
//        RelativeLayout.LayoutParams ps2 = new RelativeLayout.LayoutParams(
//                (int) ScreenSizeUtil.width / 5,
//                RelativeLayout.LayoutParams.MATCH_PARENT);
//        width = (int) ScreenSizeUtil.width * 4 / 5;
//        ll_table_left.setLayoutParams(ps2);
        ll_table_right = (LinearLayout) view.findViewById(R.id.ll_table_right);

//        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//            @Override
//            public boolean onPreDraw() {
//                int height = rl_table_area.getMeasuredHeight();
//                int width = rl_table_area.getMeasuredWidth();
//                int widthNew = height * 3/2;
//                TableLayoutFragment.this.width = width;
//                if(width + 10 < widthNew || width - 10 > widthNew){
//
//                }
////
//                return true;
//            }
//        });
        refreshPlace();
        tableAdapter = new TableAdapter();
        lv_table_list.setAdapter(tableAdapter);
        if (places.size() > 0) {
            selectPlaceIndex = 0;
            refreshTableLayout();
        } else {
        }
        lv_table_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if(lv_table_list.getLastVisiblePosition() == position){
//                    return;
//                }
                TableInfo newTable = ObjectFactory.getInstance().addNewTable(images[position],
                        App.instance.getRevenueCenter().getRestaurantId(),
                        App.instance.getRevenueCenter().getId(),
                        places.get(selectPlaceIndex).getId(), ll_table_right.getMeasuredWidth(), ll_table_right.getMeasuredHeight());
                newTables.add(newTable);
                addTable(newTable);
            }
        });


        tv_table_edit.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);
        iv_more_table.setOnClickListener(this);
        view.findViewById(R.id.ll_table_root).setOnClickListener(null);
        view.findViewById(R.id.tv_place).setOnClickListener(this);
        view.findViewById(R.id.tv_summary).setOnClickListener(this);
        return view;
    }

    private void refreshPlace() {
        places.clear();
        places = PlaceInfoSQL.getAllPlaceInfo();
        PlaceInfo place = new PlaceInfo();
        place.setPlaceName("");
        places.add(place);
        if (placeAdapter != null) {
            placeAdapter.notifyDataSetChanged();
        } else {
            placeAdapter = new PlaceAdapter();
            lv_place.setAdapter(placeAdapter);
        }
    }

    private void refreshTableLayout() {
        newTables = TableInfoSQL.getTableInfosBuyPlaces(places.get(selectPlaceIndex));
        changeLayoutStatus();
        rl_tables.removeAllViews();

        for (TableInfo newTable : newTables) {
            addTable(newTable);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            refresh();
        }
    }

    public void refresh() {
        refreshPlace();
        refreshTableLayout();
    }

    private void changeLayoutStatus() {
        if (canEdit) {
            iv_more_table.setVisibility(View.VISIBLE);
            rl_create_table.setVisibility(View.VISIBLE);
            tv_table_edit.setText(mainPage.getResources().getText(R.string.save));
            tv_cancel.setVisibility(View.VISIBLE);
        } else {
            iv_more_table.setVisibility(View.INVISIBLE);
            rl_create_table.setVisibility(View.INVISIBLE);
            tv_table_edit.setText(mainPage.getResources().getText(R.string.edit));
            tv_cancel.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        Log.e(TAG, "onAttach");
        super.onAttach(activity);

    }

    @Override
    public void onStart() {
        Log.e(TAG, "onStart");
        super.onStart();

    }

    private boolean canDelete() {
        long nowTime = System.currentTimeMillis();
        List<Order> orderList = OrderSQL.getUnpaidOrdersBySession(App.instance.getSessionStatus(), App.instance.getBusinessDate(), nowTime);
        if (!orderList.isEmpty()) {
            for (Order order : orderList) {
                List<OrderDetail> orderDetailsUnIncludeVoid = OrderDetailSQL
                        .getOrderDetails(order.getId());
                if (!orderDetailsUnIncludeVoid.isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    private void initTableName(TextView tv_name, TableInfo newTable) {
        int w;
        switch (newTable.getShape()) {
            case 1:
                tv_name.setTextSize(getResources().getDimension(R.dimen.sp10));
                w = 50;
                break;
            case 2:
                tv_name.setTextSize(getResources().getDimension(R.dimen.sp11));
                w = 75;
                break;
            case 3:
                tv_name.setTextSize(getResources().getDimension(R.dimen.text_size_s_small));
                w = 90;
                break;
            default:
                tv_name.setTextSize(getResources().getDimension(R.dimen.text_size_s_small));
                w = 90;
                break;
        }
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(w, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 5 * newTable.getShape(), 0, 0);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        tv_name.setLayoutParams(layoutParams);
        tv_name.setGravity(Gravity.CENTER);
    }

    private void addTable(final TableInfo newTable) {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
//        AbsoluteLayout.LayoutParams l = new AbsoluteLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        final View selfView = LayoutInflater.from(mainPage).inflate(R.layout.table_item_view, null, false);
//        selfView.setX(Float.parseFloat(newTable.getxAxis()));
//        selfView.setY(Float.parseFloat(newTable.getyAxis()));
        selfView.setLayoutParams(layoutParams);
        final ImageView imageView = (ImageView) selfView.findViewById(R.id.iv_table);
        final Bitmap bitmap = BitmapFactory.decodeResource(getResources(), ImageUtils.getImageResourceId(newTable.getStatus().intValue() > ParamConst.TABLE_STATUS_IDLE ? newTable.getImageName() + "used" : newTable.getImageName()));
        if (newTable.getStatus().intValue() > ParamConst.TABLE_STATUS_IDLE) {

        }
        final EditText et_item_table_name = (EditText) selfView.findViewById(R.id.et_item_table_name);
        final Button btn_table_name_ok = (Button) selfView.findViewById(R.id.btn_table_name_ok);
        final TextView tv_table_name = (TextView) selfView.findViewById(R.id.tv_table_name);

        tv_table_name.setText(newTable.getName());
        final LinearLayout ll_table_more_action = (LinearLayout) selfView.findViewById(R.id.ll_table_more_action);
//        imageView.setImageBitmap(BitmapUtil.getResizedBitmap(bitmap, newTable.getPosId()%2 == 0 ? (float)(2.0/3) : (float)(5.0/6)));
        Button btn_table_small = (Button) selfView.findViewById(R.id.btn_table_small);
        Button btn_table_middle = (Button) selfView.findViewById(R.id.btn_table_middle);
        Button btn_table_large = (Button) selfView.findViewById(R.id.btn_table_large);
        imageView.setImageBitmap(BitmapUtil.getTableBitmap(newTable.getRotate(), newTable.getShape(), bitmap));
        initTableName(tv_table_name, newTable);
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!canEdit)
                    return;
                switch (v.getId()) {
                    case R.id.iv_delete:
                        if (!canDelete()) {
                            UIHelp.showShortToast(mainPage, mainPage.getResources().getString(R.string.bill_not_closed));
                            return;
                        }
                        rl_tables.removeView(selfView);
                        newTables.remove(newTable);
                        TableInfoSQL.deleteTableInfo(newTable.getPosId().intValue());
                        break;
                    case R.id.iv_copy:
                        TableInfo copNewTable = ObjectFactory.getInstance().addNewTable(newTable.getImageName(), newTable.getRestaurantId(), newTable.getRevenueId(), newTable.getPlacesId(), newTable.getResolutionWidth(), newTable.getResolutionHeight());
                        addTable(copNewTable);
                        break;
                    case R.id.iv_rotate: {
                        newTable.setRotate(newTable.getRotate() + 90 == 360 ? 0 : newTable.getRotate() + 90);
//                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), ImageUtils.getImageResourceId(newTable.getImageName()));
                        imageView.setImageBitmap(BitmapUtil.getTableBitmap(newTable.getRotate(), newTable.getShape(), bitmap));
                        initTableName(tv_table_name, newTable);
                    }
                    break;
                    case R.id.iv_more:
                        if (ll_table_more_action.getVisibility() == View.VISIBLE) {
                            ll_table_more_action.setVisibility(View.INVISIBLE);
                            et_item_table_name.setVisibility(View.INVISIBLE);
                            btn_table_name_ok.setVisibility(View.INVISIBLE);
                        } else {
                            ll_table_more_action.setVisibility(View.VISIBLE);
                            et_item_table_name.setVisibility(View.VISIBLE);
                            et_item_table_name.setText(newTable.getName());
                            btn_table_name_ok.setVisibility(View.VISIBLE);
                        }
                        break;
                    case R.id.btn_table_small: {
                        newTable.setShape(1);
                        imageView.setImageBitmap(BitmapUtil.getTableBitmap(newTable.getRotate(), newTable.getShape(), bitmap));
                        initTableName(tv_table_name, newTable);
                    }
                    break;
                    case R.id.btn_table_middle: {
                        newTable.setShape(2);
                        imageView.setImageBitmap(BitmapUtil.getTableBitmap(newTable.getRotate(), newTable.getShape(), bitmap));
                        initTableName(tv_table_name, newTable);
                    }
                    break;
                    case R.id.btn_table_large: {
                        newTable.setShape(3);
                        imageView.setImageBitmap(BitmapUtil.getTableBitmap(newTable.getRotate(), newTable.getShape(), bitmap));
                        initTableName(tv_table_name, newTable);
                    }
                    break;
                }
            }
        };
        final ImageView iv_more = (ImageView) selfView.findViewById(R.id.iv_more);
        final ImageView iv_rotate = (ImageView) selfView.findViewById(R.id.iv_rotate);
        final ImageView iv_delete = (ImageView) selfView.findViewById(R.id.iv_delete);
        final ImageView iv_copy = (ImageView) selfView.findViewById(R.id.iv_copy);
        iv_more.setOnClickListener(onClickListener);
        iv_rotate.setOnClickListener(onClickListener);
        iv_delete.setOnClickListener(onClickListener);
        iv_copy.setOnClickListener(onClickListener);
        ViewTouchUtil.expandViewTouchDelegate(iv_more);
        ViewTouchUtil.expandViewTouchDelegate(iv_rotate);
        ViewTouchUtil.expandViewTouchDelegate(iv_delete);
        ViewTouchUtil.expandViewTouchDelegate(iv_copy);
        btn_table_small.setOnClickListener(onClickListener);
        btn_table_middle.setOnClickListener(onClickListener);
        btn_table_large.setOnClickListener(onClickListener);
        btn_table_name_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(et_item_table_name.getText().toString().trim())) {
                    return;
                }
                String name = et_item_table_name.getText().toString().trim();
                TableInfo sqlTable = TableInfoSQL.getTableByName(name);
                if (sqlTable != null) {
                    if (sqlTable.getPosId().intValue() != newTable.getPosId().intValue()) {
                        UIHelp.showShortToast(mainPage, "Table Name already in use. Please use another name.");
                        return;
                    }
                }
                hideInput(et_item_table_name);
                newTable.setName(name);
                tv_table_name.setText(newTable.getName());
                iv_more.setVisibility(View.INVISIBLE);
                iv_rotate.setVisibility(View.INVISIBLE);
                iv_delete.setVisibility(View.INVISIBLE);
                iv_copy.setVisibility(View.INVISIBLE);
                ll_table_more_action.setVisibility(View.INVISIBLE);
                et_item_table_name.setVisibility(View.INVISIBLE);
                btn_table_name_ok.setVisibility(View.INVISIBLE);

                BugseeHelper.buttonClicked(name);
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (canEdit) {
                    BugseeHelper.buttonClicked("Edit table " + newTable.getName());
                    if (iv_more.getVisibility() == View.VISIBLE) {
                        iv_more.setVisibility(View.INVISIBLE);
                        iv_rotate.setVisibility(View.INVISIBLE);
                        iv_delete.setVisibility(View.INVISIBLE);
                        iv_copy.setVisibility(View.INVISIBLE);
                        ll_table_more_action.setVisibility(View.INVISIBLE);
                        et_item_table_name.setVisibility(View.INVISIBLE);
                        btn_table_name_ok.setVisibility(View.INVISIBLE);
                    } else {
                        iv_more.setVisibility(View.VISIBLE);
                        iv_rotate.setVisibility(View.VISIBLE);
                        iv_delete.setVisibility(View.VISIBLE);
                        iv_copy.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (ButtonClickTimer.canClick(v)) {
                        if (mainPage instanceof MainPage) {
                            Order order = OrderSQL.getUnfinishedOrderAtTable(newTable.getPosId(), App.instance.getBusinessDate(), App.instance.getSessionStatus());
                            if (order != null && order.getId() != null) {
                                OrderSplitSQL.deleteOrderSplitPaxByOrderId(order);
                            }
                            ((MainPage) mainPage).tableAction(newTable);
                        } else {
                            if (newTable.getStatus().intValue() == ParamConst.TABLE_STATUS_IDLE) {
                                mainPage.selectTable(newTable);
                            } else {
                                Order order = OrderSQL.getUnfinishedOrderAtTable(newTable.getPosId(), App.instance.getBusinessDate(), App.instance.getSessionStatus());
                                if (OrderDetailSQL.getOrderDetails(order.getId().intValue()).size() > 0) {
                                    UIHelp.showToast(mainPage, mainPage.getResources().getString(R.string.table_dining));
                                } else {
                                    mainPage.selectTable(newTable);
                                }
                            }

                        }

                        BugseeHelper.buttonClicked(newTable.getName());
                    }
                }

            }
        });

        imageView.setOnTouchListener(new View.OnTouchListener() {
            private float initX = -1;
            private float initY = -1;
            float mEventDownX, mEventDownY;
            private boolean isMove = false;

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (!canEdit) {
                    return false;
                }
                if (initX == -1) {
                    initX = selfView.getX();
                }
                if (initY == -1) {
                    initY = selfView.getY();
                }
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mEventDownX = event.getRawX();
                        mEventDownY = event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE: // 移动
                        float dx = event.getRawX() - mEventDownX;
                        float dy = event.getRawY() - mEventDownY;
                        float x = initX + dx;
                        float y = initY + dy;
                        selfView.setX(x > 0 ? x : 0);
                        selfView.setY(y > 0 ? y : 0);
                        newTable.setxAxis(selfView.getX() + "");
                        newTable.setyAxis(selfView.getY() + "");
                        newTable.setResolutionWidth(ll_table_right.getMeasuredWidth());
                        newTable.setResolutionHeight(ll_table_right.getMeasuredHeight());
                        Log.e("TAG", "mx:" + x + ",my:" + y);
                        selfView.invalidate();
                        break;
                    case MotionEvent.ACTION_UP: // 脱离

                        Rect r = new Rect();
                        selfView.getLocalVisibleRect(r);
                        initX = -1;
                        initY = -1;
                        float mx = event.getRawX() - mEventDownX;
                        float my = event.getRawY() - mEventDownY;
                        if (Math.abs(mx) > 10 || Math.abs(my) > 10) {
                            isMove = true;
                        } else {
                            isMove = false;
                        }

                        return isMove;
//                                if(!isMove) {
//                                    UIHelp.showShortToast(mainPage, "显示操作icon");
//                                }
                }
                return false;
            }
        });
        if (!canEdit) {
            if (!TextUtils.isEmpty(newTable.getxAxis()) && !TextUtils.isEmpty(newTable.getyAxis())) {
                selfView.post(new Runnable() {
                    @Override
                    public void run() {
                        selfView.setX(Float.parseFloat(newTable.getxAxis()));
                        selfView.setY(Float.parseFloat(newTable.getyAxis()));
                    }
                });

            }
        } else {
            newTable.setxAxis(selfView.getRotationX() + "");
            newTable.setyAxis(selfView.getRotationY() + "");

        }
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!canEdit) {
//                    Map<String, String> map = new HashMap<String, String>();
//                    map.put("companyId", CoreData.getInstance().getRestaurant().getCompanyId().intValue() + "");
//                    map.put("restaurantId", CoreData.getInstance().getRestaurant().getId().intValue() + "");
//                    map.put("tableId", newTable.getPosId().intValue() + "");
//                    map.put("tableName", newTable.getName());
//                    map.put("type", "1");
//                    final String content = new Gson().toJson(map);
                    String restaurantId = getStringByInt(CoreData.getInstance().getRestaurant().getId().intValue() + "");
                    String tableId = getStringByInt(newTable.getPosId() + "");
                    StringBuffer sb = new StringBuffer();
                    sb.append(getAbsoluteUrl(APIName.QC_DOWNLOAD) + "&" + restaurantId + "&" + tableId);
                    final String content = sb.toString();
                    BugseeHelper.buttonClicked("Long click " + newTable.getName());
                    DialogFactory.showQrCodeDialog(mainPage, content, newTable.getName(), false,
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    PrinterTitle title = ObjectFactory.getInstance()
                                            .getPrinterTitleForQRCode(
                                                    App.instance.getRevenueCenter(),
                                                    App.instance.getUser().getFirstName()
                                                            + App.instance.getUser().getLastName(),
                                                    newTable.getName());
                                    App.instance.remitePrintTableQRCode(App.instance.getCahierPrinter(),
                                            title, newTable.getPosId().intValue() + "", content);
                                    LogUtil.d(TAG, "打印二维码");
                                }
                            });

                    return true;
                } else {

                }
                return false;
            }
        });

        rl_tables.addView(selfView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_table_edit: {
                BugseeHelper.buttonClicked("Table Edit");
                if (places == null || places.size() == 0) {
                    UIHelp.showShortToast(mainPage, "Please add places first!");
                    ll_table_left.setVisibility(View.VISIBLE);
                    return;
                } else if (places.size() == 1) {
                    try {
                        if (places.get(0).getIsKiosk() == ParamConst.REVENUECENTER_ISNOT_KIOSK) {
                            UIHelp.showShortToast(mainPage, "Please add places first!");
                            ll_table_left.setVisibility(View.VISIBLE);
                            return;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (canEdit) {
                    saveTable();
                } else {
                    long nowTime = System.currentTimeMillis();
                    List<Order> orderList = OrderSQL.getUnpaidOrdersBySession(App.instance.getSessionStatus(), App.instance.getBusinessDate(), nowTime);
                    if (!orderList.isEmpty()) {
                        for (Order order : orderList) {
                            List<OrderDetail> orderDetailsUnIncludeVoid = OrderDetailSQL
                                    .getOrderDetails(order.getId());
                            if (!orderDetailsUnIncludeVoid.isEmpty()) {
                                UIHelp.showShortToast(mainPage, "There are some bills not closed yet.");
                                return;
                            } else {
                                OrderSQL.updateOrderStatus(ParamConst.ORDER_STATUS_FINISHED, order.getId().intValue());
                            }
                        }
                    }
                    canEdit = true;
                    changeLayoutStatus();
                }
            }
            break;
            case R.id.iv_more_table:
                BugseeHelper.buttonClicked("More table");
                RelativeLayout rl_table_list = (RelativeLayout) getView().findViewById(R.id.rl_table_list);
                if (rl_table_list.getVisibility() == View.VISIBLE) {
                    rl_table_list.setVisibility(View.GONE);
                } else {
                    rl_table_list.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.tv_cancel:
                BugseeHelper.buttonClicked("cancel edit");
                canEdit = false;
                changeLayoutStatus();
                refresh();
                break;
            case R.id.tv_place: {
                BugseeHelper.buttonClicked("Place");
                if (ll_table_left.getVisibility() == View.VISIBLE) {
                    ll_table_left.setVisibility(View.GONE);
                } else {
                    ll_table_left.setVisibility(View.VISIBLE);
                }
            }
            break;
            case R.id.tv_summary: {
                BugseeHelper.buttonClicked("Summary");
                UIHelp.startTableSummaryActivity(mainPage);
            }
            break;
        }
    }

    private void saveTable() {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("revenueId", App.instance.getRevenueCenter().getId().intValue());
        parameters.put("placeList", PlaceInfoSQL.getAllPlaceInfo());
//        parameters.put("placePosId", places.get(selectPlaceIndex).getId().intValue());
        parameters.put("tableList", newTables);
        SyncCentre.getInstance().updatePlaceTable(mainPage, parameters, handler);
        loadingDialog.show();

    }

    private void dismissDialog() {
        if (loadingDialog != null && loadingDialog.isShowing())
            loadingDialog.dismiss();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_PLACE_TABLE_SUCCEED:
                    TableInfoSQL.addTablesList(newTables);
                    dismissDialog();
                    UIHelp.showShortToast(mainPage, "Save success");
                    canEdit = false;
                    changeLayoutStatus();
                    break;
                case UPDATE_PLACE_TABLE_FAILURE:
                    dismissDialog();
//                    DialogFactory.showOneButtonCompelDialog(mainPage, getActivity().getResources().getString(R.string.warning),
//                            ResultCode.getErrorResultStrByCode(mainPage,(Integer)msg.obj, null)), new Onc);
                    DialogFactory.showOneButtonCompelDialog(mainPage, mainPage.getResources().getString(R.string.warning),
                            ResultCode.getErrorResultStrByCode(mainPage, (Integer) msg.obj, null), null);
//                    TableInfoSQL.addTablesList(newTables);
                    break;
                case ResultCode.CONNECTION_FAILED:
                    dismissDialog();
                    DialogFactory.showOneButtonCompelDialog(mainPage, mainPage.getResources().getString(R.string.warning),
                            ResultCode.getErrorResultStr(mainPage,
                                    (Throwable) msg.obj, mainPage.getResources().getString(R.string.server)), null);
//                    TableInfoSQL.addTablesList(newTables);

                    break;
            }
        }
    };

    /* 隐藏软键盘 */
    private void hideInput(View v) {

        InputMethodManager imm = (InputMethodManager) v.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(v.getApplicationWindowToken(),
                    0);
        }
    }

    /* 显示软键盘 */
    private void showSoftInput(View v) {

        InputMethodManager imm = (InputMethodManager) v.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    class PlaceAdapter extends BaseAdapter {
        LayoutInflater inflater;

        public PlaceAdapter() {
            inflater = LayoutInflater.from(mainPage);
        }

        //        public void updatePlaceArea(){
//            places = PlaceInfoSQL.getAllPlaceInfo();
//            PlaceInfo place = new PlaceInfo();
//            place.setPlaceName("");
//            places.add(place);
//        }
        ViewHolder holder = null;

        @Override
        public int getCount() {
            return places.size();
        }

        @Override
        public Object getItem(int position) {
            return places.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.place_item_layout, null);
                holder.rl_place = (RelativeLayout) convertView.findViewById(R.id.rl_place);
                holder.rl_add_place = (RelativeLayout) convertView.findViewById(R.id.rl_add_place);
                holder.iv_place_edit = (ImageView) convertView.findViewById(R.id.iv_place_edit);
                holder.iv_place_delete = (ImageView) convertView.findViewById(R.id.iv_place_delete);
                holder.tv_place_name = (TextView) convertView.findViewById(R.id.tv_place_name);
                holder.rl_add_place = (RelativeLayout) convertView.findViewById(R.id.rl_add_place);
                holder.rl_add_edit = (RelativeLayout) convertView.findViewById(R.id.rl_add_edit);
                holder.iv_add_place = (ImageView) convertView.findViewById(R.id.iv_add_place);
                holder.tv_add_place = (TextView) convertView.findViewById(R.id.tv_add_place);
                holder.et_add_place = (EditText) convertView.findViewById(R.id.et_add_place);
                holder.et_place_name = (EditText) convertView.findViewById(R.id.et_place_name);
                holder.iv_place_edit.setTag(holder);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final PlaceInfo place = places.get(position);
            holder.tv_place_name.setVisibility(View.VISIBLE);
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (canEdit) {
                        DialogFactory.commonTwoBtnDialog(mainPage,
                                mainPage.getResources().getString(R.string.warning),
                                "Want to save the edited content?",
                                mainPage.getResources().getString(R.string.cancel),
                                mainPage.getResources().getString(R.string.ok),
                                null,
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        canEdit = false;
                                        changeLayoutStatus();
                                        saveTable();
                                    }
                                }
                        );
                        return;
                    }
                    switch (v.getId()) {
                        case R.id.iv_place_edit: {
                            BugseeHelper.buttonClicked("Place Edit");
                            ViewHolder myHolder = (ViewHolder) v.getTag();
                            if (myHolder.et_place_name.getVisibility() == View.VISIBLE) {
                                myHolder.tv_place_name.setVisibility(View.VISIBLE);
                                myHolder.et_place_name.setVisibility(View.GONE);
                                hideInput(myHolder.et_place_name);
                            } else {
                                myHolder.et_place_name.setText(myHolder.tv_place_name.getText());
                                myHolder.tv_place_name.setVisibility(View.GONE);
                                myHolder.et_place_name.setVisibility(View.VISIBLE);
                                myHolder.et_place_name.setFocusable(true);
                                myHolder.et_place_name.requestFocus();
                                showSoftInput(myHolder.et_place_name);
                                canEdit = false;
                            }
                        }
                        break;
                        case R.id.rl_add_place: {
                            BugseeHelper.buttonClicked("Add Place");
                            v.setVisibility(View.GONE);
                            holder.rl_add_edit.setVisibility(View.VISIBLE);
                            holder.et_add_place.setFocusable(true);
                            holder.et_add_place.requestFocus();
                            showSoftInput(holder.et_add_place);
                            canEdit = false;
                        }
                        break;
                        case R.id.tv_add_place_cancel: {
                            BugseeHelper.buttonClicked("Add place cancel");
                            holder.rl_add_edit.setVisibility(View.GONE);
                            if (IntegerUtils.isEmptyOrZero(place.getId())) {
                                holder.rl_add_place.setVisibility(View.VISIBLE);
                            } else {
                                holder.rl_place.setVisibility(View.VISIBLE);
                            }
                            hideInput(holder.et_add_place);
                        }
                        break;
                        case R.id.tv_place_name: {
                            BugseeHelper.buttonClicked("Place Name");
                            selectPlaceIndex = position;
                            canEdit = false;
                            refreshPlace();
                            refreshTableLayout();
                            ll_table_left.setVisibility(View.GONE);
                        }
                        break;
                        case R.id.iv_place_delete: {
                            BugseeHelper.buttonClicked("Place delete");
                            if (!canDelete()) {
                                UIHelp.showShortToast(mainPage, mainPage.getResources().getString(R.string.bill_not_closed));
                                return;
                            }
                            DialogFactory.commonTwoBtnDialog(mainPage,
                                    mainPage.getResources().getString(R.string.warning),
                                    "Are you sure to delete this place?",
                                    mainPage.getResources().getString(R.string.cancel),
                                    mainPage.getResources().getString(R.string.ok),
                                    null,
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            PlaceInfoSQL.deletePlaceInfo(place);
                                            TableInfoSQL.deleteTableInfoByPlaceId(place.getId().intValue());
                                            refreshPlace();
                                            refreshTableLayout();
                                        }
                                    });

                        }
                        break;
                    }
                }
            };
            holder.rl_add_edit.setVisibility(View.GONE);
            holder.et_place_name.setVisibility(View.GONE);
            if (selectPlaceIndex == position) {
                convertView.setBackgroundColor(getResources().getColor(R.color.brownness));
                holder.iv_place_delete.setImageDrawable(getResources().getDrawable(R.drawable.place_delete_selected));
                holder.iv_place_edit.setImageDrawable(getResources().getDrawable(R.drawable.place_edit_selected));
                holder.tv_place_name.setTextColor(getResources().getColor(R.color.white));
            } else {
                convertView.setBackgroundColor(getResources().getColor(R.color.white));
                holder.iv_place_delete.setImageDrawable(getResources().getDrawable(R.drawable.place_delete));
                holder.iv_place_edit.setImageDrawable(getResources().getDrawable(R.drawable.place_edit));
                holder.tv_place_name.setTextColor(getResources().getColor(R.color.black));
            }
            if (IntegerUtils.isEmptyOrZero(place.getId())) {
                holder.rl_place.setVisibility(View.GONE);
                holder.rl_add_place.setVisibility(View.VISIBLE);
                convertView.setBackgroundColor(getResources().getColor(R.color.white));
            } else {
                holder.rl_place.setVisibility(View.VISIBLE);
                holder.rl_add_place.setVisibility(View.GONE);
                holder.tv_place_name.setText(place.getPlaceName());
            }
            holder.rl_add_place.setOnClickListener(onClickListener);


            convertView.findViewById(R.id.tv_add_place_cancel).setOnClickListener(onClickListener);
            holder.tv_place_name.setOnClickListener(onClickListener);
            holder.iv_place_edit.setOnClickListener(onClickListener);
            holder.iv_place_delete.setOnClickListener(onClickListener);
            holder.et_place_name.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (TextUtils.isEmpty(v.getText().toString().trim())) {
                        return false;
                    }
                    if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                        hideInput(v);
                        if (!IntegerUtils.isEmptyOrZero(place.getId())) {
                            place.setPlaceName(v.getText().toString());
                            PlaceInfoSQL.updatePlaceInfo(place);
                            refreshPlace();
                        }
                        return true;
                    }
                    return false;
                }
            });
            holder.et_add_place.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (TextUtils.isEmpty(v.getText().toString().trim())) {
                        return false;
                    }
                    if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                        if (IntegerUtils.isEmptyOrZero(place.getId())) {
                            ObjectFactory.getInstance().addNewPlace(App.instance.getRevenueCenter().getRestaurantId().intValue(),
                                    App.instance.getRevenueCenter().getId().intValue(), v.getText().toString());
                            places = PlaceInfoSQL.getAllPlaceInfo();
//                            notifyDataSetChanged();
                            refreshPlace();
                        }
                        hideInput(v);
                        return true;
                    }
                    return false;
                }
            });


            return convertView;
        }


        class ViewHolder {
            RelativeLayout rl_place;
            ImageView iv_place_edit;
            ImageView iv_place_delete;
            TextView tv_place_name;
            TextView tv_add_place;
            ImageView iv_add_place;
            EditText et_add_place;
            EditText et_place_name;
            RelativeLayout rl_add_place;
            RelativeLayout rl_add_edit;
        }
    }


    private String getStringByInt(String id) {
        final String[] letters = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j"};
        StringBuffer sbf = new StringBuffer();
        char[] c = id.toCharArray();
        for (char ch : c) {
            int intNum = ch - '0';
            sbf.append(letters[intNum]);
        }
        return sbf.toString();
    }


    private String getAbsoluteUrl(String relativeUrl) {
        if (App.instance.isDebug) {
//			return "http://172.16.0.190:8087/alfred-api/" + relativeUrl;
            //  return "http://192.168.104.10:8083/alfred-api/" + relativeUrl;
            return "http://192.168.20.100:8083/alfred-api/" + relativeUrl;
        } else if (App.instance.isOpenLog) {

            return "http://139.224.17.126/" + relativeUrl;
        } else {
            if (App.instance.isCartenzLog) {
                return "http://18.139.110.250/" + relativeUrl;
            } else {
                return "http://www.servedbyalfred.biz/" + relativeUrl;
            }
        }
    }

    class TableAdapter extends BaseAdapter {
        LayoutInflater inflater;

        public TableAdapter() {
            inflater = LayoutInflater.from(mainPage);
        }

        @Override
        public int getCount() {
            return images.length;
        }

        @Override
        public Object getItem(int position) {
            return images[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.table_item_layout, null);
                holder.iv_table = (ImageView) convertView.findViewById(R.id.iv_table);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            String imageName = images[position];
            holder.iv_table.setImageDrawable(getResources().getDrawable(ImageUtils.getImageResourceId(imageName)));
            return convertView;
        }

        class ViewHolder {
            ImageView iv_table;
        }
    }
}
