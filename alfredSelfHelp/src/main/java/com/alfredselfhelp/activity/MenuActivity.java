package com.alfredselfhelp.activity;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alfredbase.BaseActivity;
import com.alfredbase.LoadingDialog;
import com.alfredbase.ParamConst;
import com.alfredbase.global.CoreData;
import com.alfredbase.javabean.ItemCategory;
import com.alfredbase.javabean.ItemDetail;
import com.alfredbase.javabean.ItemMainCategory;
import com.alfredbase.javabean.Order;
import com.alfredbase.javabean.OrderDetail;
import com.alfredbase.store.sql.ItemCategorySQL;
import com.alfredbase.store.sql.OrderDetailSQL;
import com.alfredbase.store.sql.OrderModifierSQL;
import com.alfredbase.store.sql.OrderSQL;
import com.alfredbase.store.sql.TableInfoSQL;
import com.alfredbase.utils.BH;
import com.alfredbase.utils.IntegerUtils;
import com.alfredbase.utils.LogUtil;
import com.alfredbase.utils.ObjectFactory;
import com.alfredselfhelp.R;
import com.alfredselfhelp.adapter.CartDetailAdapter;
import com.alfredselfhelp.adapter.ClassAdapter;
import com.alfredselfhelp.adapter.MainCategoryAdapter;
import com.alfredselfhelp.adapter.MenuDetailAdapter;
import com.alfredselfhelp.adapter.RvListener;
import com.alfredselfhelp.global.App;
import com.alfredselfhelp.global.RfidApiCentre;
import com.alfredselfhelp.global.SyncCentre;
import com.alfredselfhelp.javabean.ItemDetailDto;
import com.alfredselfhelp.popuwindow.SetItemCountWindow;
import com.alfredselfhelp.utils.CheckListener;
import com.alfredselfhelp.utils.ItemHeaderDecoration;
import com.alfredselfhelp.utils.OrderDetailRFIDHelp;
import com.alfredselfhelp.utils.UIHelp;
import com.alfredselfhelp.view.CountView;
import com.alfredselfhelp.view.CountViewMod;
import com.nordicid.nurapi.NurApi;
import com.nordicid.nurapi.NurApiUiThreadRunner;
import com.nordicid.nurapi.NurRespInventory;
import com.nordicid.nurapi.NurTag;
import com.nordicid.nurapi.NurTagStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MenuActivity extends BaseActivity implements CheckListener {
    public static final int VIEW_EVENT_MODIFY_ITEM_COUNT = 1;
    public static final int VIEW_EVENT_MODIFIER_COUNT = 8;
    public static final int MODIFY_ITEM_COUNT = 2;
    public static final int VIEW_ORDER_DETAIL_MODIFY_ITEM_COUNT = 3;
    private RecyclerView re_main_category;
    private List<ItemMainCategory> itemMainCategories;
    private LinearLayoutManager mLinearLayoutManager;
    MainCategoryAdapter mainCategoryAdapter;
    private LinearLayout ll_grab, ll_menu_details, ll_video, ll_view_cart, ll_view_cart_list, ll_view_pay;
    private RecyclerView re_menu_classify, re_menu_details, re_view_cart;
    private ClassAdapter mClassAdapter;
    private MenuDetailAdapter mDetailAdapter;
    ItemHeaderDecoration mDecoration;
    private TextView total, tv_cart_num, tv_total_price;
    private RelativeLayout rl_cart_num;

    private RelativeLayout rl_cart_total;
    private LinearLayout li_menu;

    List<ItemDetail> itemDetails = new ArrayList<ItemDetail>();

    List<ItemDetail> itemDetailNur = new ArrayList<ItemDetail>();

    List<ItemCategory> itemCategorys = new ArrayList<ItemCategory>();
    private CheckListener checkListener;

    private Boolean isMoved = false;
    private int targetPosition;
    private int mIndex;
    private GridLayoutManager mManager;
    private Boolean move = false;
    private Order nurOrder;
    SetItemCountWindow setItemCountWindow;
    CartDetailAdapter cartAdater;
//    private OrderSelfDialog selfDialog;
    List<OrderDetail> orderDetails = new ArrayList<OrderDetail>();
    private Timer timer = new Timer();
    private boolean isUpdating = false;
//    Dialog fdialog;
    protected void initView() {
        super.initView();
        setContentView(R.layout.activity_menu);
        init();
        timer.schedule(new MyTimertask(), 3000);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case VIEW_EVENT_MODIFY_ITEM_COUNT: {
                    Log.d("444444444444--->", "4444444444444");
                    Map<String, Object> map = (Map<String, Object>) msg.obj;
                    updateOrderDetail((ItemDetail) map.get("itemDetail"),
                            (Integer) map.get("count"));
                    refreshTotal();
                    refreshList();

                    boolean isadd = (boolean) map.get("isAdd");

                }
                break;

                case MODIFY_ITEM_COUNT:

                    Map<String, Object> map = (Map<String, Object>) msg.obj;
                    updateitemOrderDetail((ItemDetail) map.get("itemDetail"),
                            1);
                    refreshTotal();
                    refreshList();

                    break;

                case VIEW_ORDER_DETAIL_MODIFY_ITEM_COUNT:

                    map = (Map<String, Object>) msg.obj;
                    int count = (int) map.get("count");

//                    if (count == 0) {
//
//                    } else {
                    updateCartOrderDetail((OrderDetail) map.get("orderDetail"),
                            count);
//                    }

                    nurOrder = OrderSQL.getOrder(nurOrder.getId());
                    tv_total_price.setText("S" + App.instance.getCurrencySymbol() + BH.getBD(nurOrder.getTotal()));
                    tv_total_price.setTextColor(context.getResources().getColor(R.color.green));
                    break;

                case 1111:
                    try {
                        if (loadingDialog != null && loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    rl_cart_num.setVisibility(View.GONE);
                    UIHelp.showToast(App.instance, "Success");

                    OrderDetailSQL.deleteAllOrderDetail();
                    OrderSQL.deleteAllOrder();
                    nurOrder = ObjectFactory.getInstance().getOrder(
                            ParamConst.ORDER_ORIGIN_POS, 0, TableInfoSQL.getKioskTable(),
                            App.instance.getRevenueCenter(), App.instance.getUser(),
                            App.instance.getSessionStatus(),
                            App.instance.getBusinessDate(),
                            App.instance.getIndexOfRevenueCenter(),
                            ParamConst.ORDER_STATUS_OPEN_IN_POS,
                            App.instance.getLocalRestaurantConfig()
                                    .getIncludedTax().getTax(), 0);
//                    ll_grab.performClick();
                    MenuActivity.this.finish();

                    break;
                case -1111:
                    if (loadingDialog != null && loadingDialog.isShowing()) {
                        loadingDialog.dismiss();
                        ;
                    }
                    UIHelp.showToast(App.instance, "failed");
                    break;


                //  case  success

            }

        }
    };

    private void init() {
        loadingDialog = new LoadingDialog(MenuActivity.this);
//        selfDialog = new OrderSelfDialog(MenuActivity.this);
        ll_grab = (LinearLayout) findViewById(R.id.ll_grab);
        ll_video = (LinearLayout) findViewById(R.id.ll_video);
        ll_menu_details = (LinearLayout) findViewById(R.id.ll_menu_details);
        re_menu_classify = (RecyclerView) findViewById(R.id.re_menu_classify);
        re_menu_details = (RecyclerView) findViewById(R.id.re_menu_details);
        re_main_category = (RecyclerView) findViewById(R.id.re_main_category);
        ll_view_cart = (LinearLayout) findViewById(R.id.ll_view_cart);
        ll_view_cart_list = (LinearLayout) findViewById(R.id.ll_view_cart_list);
        re_view_cart = (RecyclerView) findViewById(R.id.re_view_cart);
        ll_view_pay = (LinearLayout) findViewById(R.id.ll_view_pay);
        tv_cart_num = (TextView) findViewById(R.id.tv_cart_num);
        rl_cart_num = (RelativeLayout) findViewById(R.id.rl_cart_num);
        tv_total_price = (TextView) findViewById(R.id.tv_cart_total);
        rl_cart_total = (RelativeLayout) findViewById(R.id.rl_cart_total);
        li_menu = (LinearLayout) findViewById(R.id.li_menu);

        ll_view_pay.setOnClickListener(this);
        total = (TextView) findViewById(R.id.tv_cart_total);
        ll_video.setVisibility(View.VISIBLE);
//        ll_menu_details.setVisibility(View.VISIBLE);
//        ll_video.setVisibility(View.GONE);
        ll_grab.setOnClickListener(this);
        ll_view_cart.setOnClickListener(this);
        itemMainCategories = CoreData.getInstance().getItemMainCategoriesForSelp();
        menuDetail();
        re_main_category = (RecyclerView) findViewById(R.id.re_main_category);
        mLinearLayoutManager = new LinearLayoutManager(context);
        mLinearLayoutManager.setOrientation(OrientationHelper.HORIZONTAL);
        re_main_category.setLayoutManager(mLinearLayoutManager);
//        DividerItemDecoration decoration = new DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL);
//        re_main_category.addItemDecoration(decoration);
        mainCategoryAdapter = new MainCategoryAdapter(context, itemMainCategories, new RvListener() {

            @Override
            public void onItemClick(int id, int position) {
//                RfidApiCentre.getInstance().stopRFIDScan();
                ItemMainCategory itemMainCategory = itemMainCategories.get(position);
                mainCategoryAdapter.setCheckedPosition(position);
                li_menu.setBackgroundResource(R.drawable.bg_mod);
                ll_grab.setBackgroundResource(R.drawable.btn_main_g);
                ll_menu_details.setVisibility(View.VISIBLE);
                ll_video.setVisibility(View.GONE);
                ll_view_cart_list.setVisibility(View.GONE);
                ll_view_pay.setVisibility(View.GONE);
                ll_view_cart.setVisibility(View.VISIBLE);
                getItemCategory(itemMainCategory.getId());
                getItemDetail(itemMainCategory.getMainCategoryName(), itemMainCategory.getId().intValue());


            }
        });
        re_main_category.setAdapter(mainCategoryAdapter);

        setItemCountWindow = new SetItemCountWindow(this, findViewById(R.id.li_menu),
                handler);
        nurOrder = OrderSQL.getAllOrder().get(0);
        RfidApiCentre.getInstance().initApi(new NurApiUiThreadRunner() {
            public void runOnUiThread(Runnable r) {
                MenuActivity.this.runOnUiThread(r);
            }
        });
//        RfidApiCentre.getInstance().setCallBack(new RfidApiCentre.RfidCallBack() {
//            @Override
//            public void inventoryStreamEvent() {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (nurOrder != null && nurOrder.getId() > 0) {
//                            NurTagStorage nurTagStorage = RfidApiCentre.getInstance().getNurTagStorage();
//                            UIHelp.showShortToast(App.instance, "nurTagStorage size" + nurTagStorage.size());
//                            List<String> barCodes = OrderDetailRFIDHelp.getUnChooseItemBarCode(orderDetails, nurTagStorage);
//                            if(!isUpdating) {
//                                LogUtil.e("TAG", "Storage size: =======" + nurTagStorage.size());
//                                if (barCodes.size() > 0) {
//                                    LogUtil.e("TAG", "Add: ======= barCodes size" + barCodes.size());
//                                    isUpdating = true;
//                                    initRfid(barCodes);
//                                } else {
//                                    Map<String, Integer> map = OrderDetailRFIDHelp.getUnScannerItemBarCode(orderDetails, nurTagStorage);
//                                    if (map.size() > 0) {
//                                        LogUtil.e("TAG", "Remove: ======= map size" + map.size());
//                                        isUpdating = true;
//                                        removeRfid(map);
//                                    }
//                                }
//                            }
//                        }
//                        RfidApiCentre.getInstance().startRFIDScan();
//                    }
//
//                });
//
//            }
//        });



    }

    @Override
    protected void onDestroy() {
        try{
            if(timer != null){
                timer.cancel();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        super.onDestroy();
    }

    private void initRfid(List<String> barCodes) {
        Map<Integer, ItemDetailDto> itemDetailNumMap = new HashMap<>();
        if (barCodes != null && barCodes.size() > 0) {
            for (int j = 0; j < barCodes.size(); j++) {
                String barCode = barCodes.get(j);
                ItemDetail itemDetail = CoreData.getInstance().getItemDetailByBarCodeForKPMG(barCode);
                if (itemDetail != null) {
                    int itemDetailId = itemDetail.getId();
                    if (itemDetailNumMap.containsKey(itemDetailId)) {
                        ItemDetailDto itemDetailDto = itemDetailNumMap.get(itemDetailId);
                        itemDetailDto.setItemNum(itemDetailDto.getItemNum() + 1);
                    } else {
                        ItemDetailDto itemDetailDto = new ItemDetailDto();
                        itemDetailDto.setItemId(itemDetailId);
                        itemDetailDto.setItemNum(1);
                        itemDetailDto.setItemName(itemDetail.getItemName());
                        itemDetailDto.setItemPrice(itemDetail.getPrice());
                        itemDetailNumMap.put(itemDetailId, itemDetailDto);
                    }
                }
            }
        }
        if (itemDetailNumMap.size() > 0) {
            final List<ItemDetailDto> itemDetailDtos = new ArrayList<>(itemDetailNumMap.values());
            if (itemDetailDtos != null) {
//                loadingDialog.setTitle("Loading");
//                loadingDialog.show();
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
                        for (ItemDetailDto itemDetailDto : itemDetailDtos) {
                            ItemDetail itemDetail = CoreData.getInstance().getItemDetailById(itemDetailDto.getItemId());
                            OrderDetail orderDetail = ObjectFactory.getInstance()
                                    .createOrderDetailForWaiter(nurOrder, itemDetail,
                                            0, App.instance.getUser());
                            orderDetail.setItemNum(itemDetailDto.getItemNum());
                            OrderDetailSQL.addOrderDetailETCForWaiterFirstAdd(orderDetail);
                        }
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                if (loadingDialog != null && loadingDialog.isShowing()) {
//                                    loadingDialog.dismiss();
//                                }
                                refreshTotal();
                                if(ll_view_cart_list != null && ll_view_cart_list.getVisibility() == View.VISIBLE){
                                    refreshViewCart();
                                }
//                                RfidApiCentre.getInstance().getNurTagStorage().clear();
                                isUpdating = false;
//                            }
//                        });
//                    }
//                }).start();
            }
        }
    }
    private void removeRfid(final Map<String, Integer> map) {
        if (map != null && map.size() > 0) {
//            loadingDialog.setTitle("Loading");
//            loadingDialog.show();
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
                    if(orderDetails != null && orderDetails.size() > 0){
                        for(OrderDetail orderDetail : orderDetails){
                            if(!TextUtils.isEmpty(orderDetail.getBarCode())){
                                String barCode = IntegerUtils.format24(orderDetail.getBarCode());
                                if(map.containsKey(barCode)) {
                                    Integer num = map.get(barCode);
                                    OrderDetailSQL.deleteOrderDetail(orderDetail);
                                    if (orderDetail.getItemNum() < num.intValue()) {
                                        map.put(barCode, num.intValue() - orderDetail.getItemNum());
                                    } else {
                                        map.remove(barCode);
                                    }
                                }
                            }
                        }
                    }
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (loadingDialog != null && loadingDialog.isShowing()) {
//                                loadingDialog.dismiss();
//                            }
//                            UIHelp.showToast(MenuActivity.this, "Remove  on UI");
                            refreshTotal();
                            if (ll_view_cart_list != null && ll_view_cart_list.getVisibility() == View.VISIBLE) {
                                refreshViewCart();
                            }
                            if(orderDetails.size() == 0){
                                ll_grab.performClick();
                            }
//                            RfidApiCentre.getInstance().getNurTagStorage().clear();
                            isUpdating = false;
//                        }
//                    });
//                }
//            }).start();
        }
    }


    private void updateCartOrderDetail(OrderDetail orderDetail, int count) {
//        OrderDetail orderDetail = OrderDetailSQL.getUnFreeOrderDetail(
//                nurOrder, itemDetail, 0,
//                ParamConst.ORDERDETAIL_STATUS_WAITER_ADD);
//		int oldCount = OrderDetailSQL.getUnFreeOrderDetailsNumInKOTOrPOS(
//				currentOrder, itemDetail, currentGroupId);
        orderDetails.clear();
        if (count == 0) {// 删除
            OrderDetailSQL.deleteOrderDetail(orderDetail);
            OrderModifierSQL.deleteOrderModifierByOrderDetail(orderDetail);

        } else {// 添加
            nurOrder.setOrderStatus(ParamConst.ORDER_STATUS_OPEN_IN_WAITER);
            OrderSQL.update(nurOrder);
            orderDetail.setItemNum(count);
            orderDetail.setUpdateTime(System.currentTimeMillis());
            OrderDetailSQL.updateOrderDetailAndOrderForWaiter(orderDetail);
        }

        orderDetails.addAll(OrderDetailSQL.getUnFreeOrderDetailsForWaiter(nurOrder));
        cartAdater.notifyDataSetChanged();
        if(orderDetails.size() == 0){
            View view = re_main_category.getChildAt(0);//获取到第一个Item的View
            view.performClick();
        }
    }

    private void updateOrderDetail(ItemDetail itemDetail, int count) {
        OrderDetail orderDetail = OrderDetailSQL.getUnFreeOrderDetail(
                nurOrder, itemDetail, 0,
                ParamConst.ORDERDETAIL_STATUS_WAITER_ADD);
//		int oldCount = OrderDetailSQL.getUnFreeOrderDetailsNumInKOTOrPOS(
//				currentOrder, itemDetail, currentGroupId);
        if (count == 0) {// 删除
            OrderDetailSQL.deleteOrderDetail(orderDetail);
            OrderModifierSQL.deleteOrderModifierByOrderDetail(orderDetail);
        } else {// 添加
//			count = count - oldCount;
            nurOrder.setOrderStatus(ParamConst.ORDER_STATUS_OPEN_IN_WAITER);
            OrderSQL.update(nurOrder);
            if (orderDetail == null) {
                orderDetail = ObjectFactory.getInstance()
                        .createOrderDetailForWaiter(nurOrder, itemDetail,
                                0, App.instance.getUser());
                orderDetail.setItemNum(count);
                OrderDetailSQL.addOrderDetailETCForWaiterFirstAdd(orderDetail);
            } else {
                orderDetail.setItemNum(count);
                orderDetail.setUpdateTime(System.currentTimeMillis());
                OrderDetailSQL.updateOrderDetailAndOrderForWaiter(orderDetail);
            }
        }
    }

    private void updateitemOrderDetail(ItemDetail itemDetail, int count) {
        OrderDetail orderDetail = OrderDetailSQL.getUnFreeOrderDetail(
                nurOrder, itemDetail, 0,
                ParamConst.ORDERDETAIL_STATUS_WAITER_ADD);
        if (orderDetail != null) {
            count = count + orderDetail.getItemNum();

        } else {
            count = 1;
        }
        if (count == 0) {// 删除
            OrderDetailSQL.deleteOrderDetail(orderDetail);
            OrderModifierSQL.deleteOrderModifierByOrderDetail(orderDetail);
        } else {// 添加
//			count = count - oldCount;
            nurOrder.setOrderStatus(ParamConst.ORDER_STATUS_OPEN_IN_WAITER);
            OrderSQL.update(nurOrder);
            if (orderDetail == null) {
                orderDetail = ObjectFactory.getInstance()
                        .createOrderDetailForWaiter(nurOrder, itemDetail,
                                0, App.instance.getUser());
                orderDetail.setItemNum(count);
                OrderDetailSQL.addOrderDetailETCForWaiterFirstAdd(orderDetail);
            } else {
                orderDetail.setItemNum(count);
                orderDetail.setUpdateTime(System.currentTimeMillis());
                OrderDetailSQL.updateOrderDetailAndOrderForWaiter(orderDetail);
            }
        }
    }


    private void menuDetail() {

        mLinearLayoutManager = new LinearLayoutManager(context);
        mLinearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        re_menu_classify.setLayoutManager(mLinearLayoutManager);
        mClassAdapter = new ClassAdapter(context, itemCategorys, new RvListener() {
            @Override
            public void onItemClick(int id, int position) {
                isMoved = true;
                App.isleftMoved = true;
                targetPosition = position;
                setChecked(position, true, 0);

            }
        });

        re_menu_classify.setAdapter(mClassAdapter);


    }


    private List<ItemCategory> getItemCategory(int id) {

        List<ItemCategory> itemCategorielist = new ArrayList<ItemCategory>();

        List<ItemCategory> itemCategorylist = ItemCategorySQL.getAllItemCategory();
        itemCategorys.clear();
        if (itemCategorylist != null || itemCategorielist.size() > 0) {


            for (int i = 0; i < itemCategorylist.size(); i++) {
                ItemCategory itemCategory = itemCategorylist.get(i);
                int cid;
                cid = itemCategorylist.get(i).getItemMainCategoryId();
                if (id == cid) {
//
                    itemCategorys.add(itemCategory);

                }
            }
        }
        mClassAdapter.notifyDataSetChanged();
        return itemCategorielist;

    }

    private List<ItemDetail> getItemDetail(String mainCategoryName, int id
    ) {

        itemDetails.clear();
        re_menu_details.addOnScrollListener(new RecyclerViewListener());
        mManager = new GridLayoutManager(context, 3);
        //通过isTitle的标志来判断是否是title
//        mManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//            @Override
//            public int getSpanSize(int position) {
//                return  1;
//            }
//        });1
        re_menu_details.setLayoutManager(mManager);
        mDetailAdapter = new MenuDetailAdapter(context, itemDetails, new RvListener() {
            @Override
            public void onItemClick(int id, int position) {
                ItemDetail itemDetail = itemDetails.get(position);
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("itemDetail", itemDetail);

                handler.sendMessage(handler.obtainMessage(
                        MODIFY_ITEM_COUNT, map));
            }
        }, new CountViewMod.OnCountChange() {
            @Override
            public void onChange(ItemDetail selectedItemDetail, int count, boolean isAdd) {


                Map<String, Object> map = new HashMap<String, Object>();
                map.put("itemDetail", selectedItemDetail);
                map.put("count", count);
                map.put("isAdd", isAdd);
                handler.sendMessage(handler.obtainMessage(
                        VIEW_EVENT_MODIFY_ITEM_COUNT, map));
            }
        });
        re_menu_details.setAdapter(mDetailAdapter);
//        mDecoration = new ItemHeaderDecoration(context, itemDetails);
//        re_menu_details.addItemDecoration(mDecoration);
//        mDecoration.setCheckListener(this);
        List<ItemDetail> itemDetaillist = new ArrayList<ItemDetail>();

        List<ItemDetail> itemDetailandCate = new ArrayList<ItemDetail>();
//
        List<ItemCategory> itemCategorylist = ItemCategorySQL.getAllItemCategory();
        int tag = 0;
        if (itemCategorylist != null || itemCategorylist.size() > 0) {
            for (int j = 0; j < itemCategorylist.size(); j++) {
                ItemCategory itemCategory = itemCategorylist.get(j);
                int cid;
                cid = itemCategorylist.get(j).getItemMainCategoryId();
                if (id == cid) {
//
                    itemDetaillist.clear();
                    //  itemDetaillist = CoreData.getInstance().getItemDetails(itemCategory);
                    itemDetaillist = CoreData.getInstance().getItemDetails(itemCategorylist.get(j));
                    // itemDetaillist  = ItemDetailSQL.getAllItemDetail();
                    if (itemDetaillist != null || itemDetaillist.size() > 0) {
                        for (int d = 0; d < itemDetaillist.size(); d++) {
                            itemDetaillist.get(d).setItemCategoryName(mainCategoryName);
                            itemDetaillist.get(d).setTag(String.valueOf(tag));
                            itemDetaillist.get(d).setViewType(3);
                            itemDetails.add(itemDetaillist.get(d));
                            // }
                        }
                    }
                    tag++;
                }
            }

        }

        refreshTotal();
        refreshList();
        //  mDetailAdapter.notifyDataSetChanged();
        //  mDecoration.setData(itemDetails);
        return itemDetailandCate;
    }


    @Override
    protected void handlerClickEvent(View v) {
        super.handlerClickEvent(v);

        switch (v.getId()) {
            case R.id.ll_grab:
                mainCategoryAdapter.setCheckedPosition(-1);
                ll_grab.setBackgroundResource(R.drawable.main_btn_b);
                li_menu.setBackgroundResource(R.drawable.bg_grab);
                ll_menu_details.setVisibility(View.GONE);
                ll_video.setVisibility(View.VISIBLE);
                ll_view_cart_list.setVisibility(View.GONE);
                ll_view_pay.setVisibility(View.GONE);
                ll_view_cart.setVisibility(View.VISIBLE);
                refreshTotal();
//                RfidApiCentre.getInstance().startRFIDScan();
//            {// TODO
//                NurTagStorage nurTagStorage = new NurTagStorage();
//                String b = "989292920000000000000000";
//                NurTag tag = new NurTag(0,0,0,0,0,0,0, b.getBytes());
//                if(nurTagStorage.addTag(tag)) {
//                    HashMap<String, String> temp = new HashMap<>();
//                    temp.put("epc", tag.getEpcString());
//                    temp.put("rssi", "" + tag.getRssi());
//                    temp.put("timestamp", "" + tag.getTimestamp());
//                    temp.put("freq", "" + tag.getFreq() + " kHz (Ch: " + tag.getChannel() + ")");
//                    temp.put("found", "1");
//                    temp.put("foundpercent", "100");
//                    tag.setUserdata(temp);
//                }
//                RfidApiCentre.getInstance().setNurTagStorage(nurTagStorage);
//                initRfid();
//            }
                break;


            case R.id.ll_view_cart:
//                Intent intent = new Intent();
//                intent.setClass(MenuActivity.this, DialogActivity.class);
//                startActivity(intent);
                if(orderDetails != null && orderDetails.size() > 0) {
                    ll_menu_details.setVisibility(View.GONE);
                    ll_video.setVisibility(View.GONE);
                    ll_view_cart_list.setVisibility(View.VISIBLE);
                    ll_view_cart.setVisibility(View.GONE);
                    ll_view_pay.setVisibility(View.VISIBLE);
                    li_menu.setBackgroundResource(R.drawable.bg_grab);

                    cartView();
                }else{
                    UIHelp.showToast(App.instance, "Please Choose Menu First !");
                }

                break;
            case R.id.ll_view_pay:
                if(orderDetails == null || orderDetails.size() == 0){
                    UIHelp.showToast(App.instance, "Please Choose Menu First !");
                    return;
                }
                NurTagStorage nurTagStorage = RfidApiCentre.getInstance().getNurTagStorage();
                if(OrderDetailRFIDHelp.getUnScannerItemBarCode(orderDetails, nurTagStorage).size() == 0){
                    paymentAction();
                }else{
                    // TODO 显示等待拿货的Dialog
//                    UIHelp.showToast(App.instance, "Please grab it from the shelf and \nplace it on the sensor plate");
//                    RfidApiCentre.getInstance().startRFIDScan();
//                     fdialog = KpmDialogFactory.kpmFDialog(context,  new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//
//                        }
//                    }, true);


                }


//                     dialog = ToolAlert.MyDialog(DialogActivity.this, "", "", "", new View.OnClickListener() {
//                         @Override
//                         public void onClick(View v) {
//                             dialog.dismiss();
//                         }
//                     }, new View.OnClickListener() {
//                         @Override
//                         public void onClick(View v) {
//                             dialog.dismiss();
//                         }
//                     });

                break;
        }
    }

    private void paymentAction() {
        ll_view_cart.setVisibility(View.GONE);
        ll_view_pay.setVisibility(View.VISIBLE);
        loadingDialog.setTitle("Pay...");
        loadingDialog.show();
        SyncCentre.getInstance().commitOrder(this, nurOrder, handler);

        mainCategoryAdapter.setCheckedPosition(-1);
    }

    private void cartView() {

        orderDetails = OrderDetailSQL.getOrderDetails(nurOrder.getId());
        mLinearLayoutManager = new LinearLayoutManager(context);
        mLinearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        re_view_cart.setLayoutManager(mLinearLayoutManager);
        cartAdater = new CartDetailAdapter(context, orderDetails, setItemCountWindow, new RvListener() {
            @Override
            public void onItemClick(int id, int position) {
//                isMoved = true;
//                App.isleftMoved = true;
//                targetPosition = position;
//                setChecked(position, true, 0);

            }
        }, new CountView.OnCountChange() {
            @Override
            public void onChange(OrderDetail selectedorderDetail, int count, boolean isAdd) {

                Map<String, Object> map = new HashMap<String, Object>();
                map.put("orderDetail", selectedorderDetail);
                map.put("count", count);
//                map.put("isAdd", isAdd);
                handler.sendMessage(handler.obtainMessage(
                        VIEW_ORDER_DETAIL_MODIFY_ITEM_COUNT, map));
            }
        });

        re_view_cart.setAdapter(cartAdater);
        nurOrder = OrderSQL.getOrder(nurOrder.getId());
        tv_total_price.setText("S" + App.instance.getCurrencySymbol() + BH.getBD(nurOrder.getTotal()));
        tv_total_price.setTextColor(context.getResources().getColor(R.color.green));

    }


    private void setChecked(int position, boolean isLeft, int id) {
        Log.d("setChecked-------->", String.valueOf(position));
        if (isLeft) {
            mClassAdapter.setCheckedPosition(position);
            //mSortAdapter.setCheckedPosition(position);
            //此处的位置需要根据每个分类的集合来进行计算
            Log.d("1111111-------->", String.valueOf(position));
            List<ItemDetail> itemDetaillist = new ArrayList<ItemDetail>();
            int count = 0;
            List<ItemCategory> itemCategorylist = ItemCategorySQL.getAllItemCategory();
            int tag = 0;


            for (int i = 0; i < position; i++) {
                itemDetaillist.clear();
                //  itemDetaillist = CoreData.getInstance().getItemDetails(itemCategory);
                itemDetaillist = CoreData.getInstance().getItemDetails(itemCategorys.get(i));
                // itemDetaillist  = ItemDetailSQL.getAllItemDetail();
                for (int d = 0; d < itemDetaillist.size(); d++) {
                    count++;
                    // }
                }
            }

//
            count += position;
            Log.d("count-------->", String.valueOf(count));
            move(count);
            mDecoration.setCurrentTag(String.valueOf(targetPosition));//凡是点击左边，将左边点击的位置作为当前的tag
        } else {

            if (isMoved) {
                isMoved = false;
            } else {
                if (!App.isleftMoved) {
                    mClassAdapter.setCheckedPosition(position);
                }

            }
            ItemHeaderDecoration.setCurrentTag(String.valueOf(position));//如果是滑动右边联动左边，则按照右边传过来的位置作为tag


        }
        if (!App.isleftMoved) {
            moveToCenter(position);
        }

    }

    private void moveToCenter(int position) {
        //将点击的position转换为当前屏幕上可见的item的位置以便于计算距离顶部的高度，从而进行移动居中
        View childAt = re_menu_classify.getChildAt(position - mLinearLayoutManager.findFirstVisibleItemPosition());
        if (childAt != null) {
            int y = (childAt.getTop() - re_main_category.getHeight() / 2);
            re_menu_classify.smoothScrollBy(0, y);
        }

    }

    @Override
    public void check(int position, boolean isScroll) {

        setChecked(position, isScroll, 0);
    }

    public void move(int n) {
        mIndex = n;
        re_menu_details.stopScroll();
        smoothMoveToPosition(n);
    }


    private void smoothMoveToPosition(int n) {
        int firstItem = mManager.findFirstVisibleItemPosition();
        int lastItem = mManager.findLastVisibleItemPosition();
        Log.d("first--->", String.valueOf(firstItem));
        Log.d("last--->", String.valueOf(lastItem));
        if (n <= firstItem) {
            re_menu_details.smoothScrollToPosition(n);
            // ((LinearLayoutManager)mRv.getLayoutManager()).scrollToPositionWithOffset(n,0);
        } else if (n <= lastItem) {
            Log.d("pos---->", String.valueOf(n) + "VS" + firstItem);
            int top = re_menu_details.getChildAt(n - firstItem).getTop();
            Log.d("top---->", String.valueOf(top));
            re_menu_details.scrollBy(0, top);
        } else {
            //((LinearLayoutManager)mRv.getLayoutManager()).scrollToPositionWithOffset(n,0);
            re_menu_details.smoothScrollToPosition(n);
            move = true;
        }
    }


    private class RecyclerViewListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);

            if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                Log.d("SCROLL--->", "拖拽中");
                App.isleftMoved = false;
            }
            if (move && newState == RecyclerView.SCROLL_STATE_IDLE) {
                App.isleftMoved = false;
                move = false;
                int n = mIndex - mManager.findFirstVisibleItemPosition();
                Log.d("n---->", String.valueOf(n));
                if (0 <= n && n < re_menu_details.getChildCount()) {
                    int top = re_menu_details.getChildAt(n).getTop();
                    Log.d("top---Changed>", String.valueOf(top));
                    re_menu_details.smoothScrollBy(0, top);
                }
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

        }
    }


    private void refreshTotal() {
        orderDetails.clear();
        orderDetails.addAll(OrderDetailSQL.getUnFreeOrderDetailsForWaiter(nurOrder));
        int itemCount = OrderDetailSQL.getCreatedOrderDetailCountForKpm(nurOrder.getId().intValue());

        if (itemCount > 0) {
            rl_cart_num.setVisibility(View.VISIBLE);
            tv_cart_num.setText(itemCount + "");
            ll_view_cart.setEnabled(true);
        } else {
            ll_view_cart.setEnabled(false);
            rl_cart_num.setVisibility(View.GONE);
        }
    }

    private void refreshList() {
//        adapter.setParams(currentOrder, orderDetails, currentGroupId);
////        adapter.notifyDataSetChanged();
        if(mDetailAdapter != null) {
            mDetailAdapter.setParams(nurOrder, orderDetails, 0);
            //cartAdater.notifyDataSetChanged();
            mDetailAdapter.notifyDataSetChanged();
        }

    }

    private void refreshViewCart(){
        if(cartAdater != null){
            nurOrder = OrderSQL.getOrder(nurOrder.getId());
            cartAdater.notifyDataSetChanged();
            tv_total_price.setText("S" + App.instance.getCurrencySymbol() + BH.getBD(nurOrder.getTotal()));
        }
    }

    class MyTimertask extends TimerTask {
        @Override
        public void run() {
            try {
                // Clear tag storage
                NurApi api = RfidApiCentre.getInstance().getNurApi();
                api.clearIdBuffer();
                api.clearTagStorage();
                LogUtil.e("TAG", "clear Api : " + api.getStorage().size());
                NurTagStorage nurTagStorage = RfidApiCentre.getInstance().getNurTagStorage();
                nurTagStorage.clear();
                LogUtil.e("TAG", "clear nurTagStorage  : " + nurTagStorage.size());
                NurRespInventory resp = api.inventory();
                System.out.println("inventory numTagsFound: " + resp.numTagsFound);
                LogUtil.e("TAG", "inventory numTagsFound: " + resp.numTagsFound);
                LogUtil.e("TAG", "api.getStorage(): " + api.getStorage());
                if (resp.numTagsFound > 0) {
                    // Fetch and print tags
                    api.fetchTags();
                    for (int n=0; n<resp.numTagsFound; n++) {
                        NurTag tag = api.getStorage().get(n);
                        nurTagStorage.addTag(tag);
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (nurOrder != null && nurOrder.getId() > 0) {
                            NurTagStorage nurTagStorage = RfidApiCentre.getInstance().getNurTagStorage();
//                            UIHelp.showShortToast(App.instance, "nurTagStorage size" + nurTagStorage.size());
                            List<String> barCodes = OrderDetailRFIDHelp.getUnChooseItemBarCode(orderDetails, nurTagStorage);
                            if(!isUpdating) {
                                LogUtil.e("TAG", "Storage size: =======" + nurTagStorage.size());
                                if (barCodes.size() > 0) {

                                    LogUtil.e("TAG", "Add: ======= barCodes size" + barCodes.size());
                                    isUpdating = true;
                                    initRfid(barCodes);
                                } else {
                                    Map<String, Integer> map = OrderDetailRFIDHelp.getUnScannerItemBarCode(orderDetails, nurTagStorage);
                                    if (map.size() > 0) {
                                        LogUtil.e("TAG", "Remove: ======= map size" + map.size());
                                        isUpdating = true;
                                        removeRfid(map);
                                    }
                                }
                            }
                        }
                    }
                });
            }  catch (Exception ex) {
                ex.printStackTrace();
            }finally {
                timer.schedule(new MyTimertask(), 2000);
            }
        }
    }

}

