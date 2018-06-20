package com.alfredposclient.popupwindow;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alfredbase.BaseActivity;
import com.alfredbase.ParamConst;
import com.alfredbase.javabean.AlipaySettlement;
import com.alfredbase.javabean.BohHoldSettlement;
import com.alfredbase.javabean.CardsSettlement;
import com.alfredbase.javabean.KotItemDetail;
import com.alfredbase.javabean.KotSummary;
import com.alfredbase.javabean.NetsSettlement;
import com.alfredbase.javabean.NonChargableSettlement;
import com.alfredbase.javabean.Order;
import com.alfredbase.javabean.OrderBill;
import com.alfredbase.javabean.OrderDetail;
import com.alfredbase.javabean.Payment;
import com.alfredbase.javabean.PaymentSettlement;
import com.alfredbase.javabean.RoundAmount;
import com.alfredbase.javabean.TableInfo;
import com.alfredbase.javabean.User;
import com.alfredbase.javabean.VoidSettlement;
import com.alfredbase.javabean.WeixinSettlement;
import com.alfredbase.store.sql.AlipaySettlementSQL;
import com.alfredbase.store.sql.BohHoldSettlementSQL;
import com.alfredbase.store.sql.CardsSettlementSQL;
import com.alfredbase.store.sql.KotItemDetailSQL;
import com.alfredbase.store.sql.KotSummarySQL;
import com.alfredbase.store.sql.NetsSettlementSQL;
import com.alfredbase.store.sql.NonChargableSettlementSQL;
import com.alfredbase.store.sql.OrderDetailSQL;
import com.alfredbase.store.sql.OrderDetailTaxSQL;
import com.alfredbase.store.sql.OrderSQL;
import com.alfredbase.store.sql.PaymentSQL;
import com.alfredbase.store.sql.PaymentSettlementSQL;
import com.alfredbase.store.sql.RoundAmountSQL;
import com.alfredbase.store.sql.TableInfoSQL;
import com.alfredbase.store.sql.VoidSettlementSQL;
import com.alfredbase.store.sql.WeixinSettlementSQL;
import com.alfredbase.utils.AnimatorListenerImpl;
import com.alfredbase.utils.BH;
import com.alfredbase.utils.BitmapUtil;
import com.alfredbase.utils.ButtonClickTimer;
import com.alfredbase.utils.DialogFactory;
import com.alfredbase.utils.LogUtil;
import com.alfredbase.utils.ObjectFactory;
import com.alfredbase.utils.OrderHelper;
import com.alfredbase.utils.RoundUtil;
import com.alfredbase.utils.TextTypeFace;
import com.alfredposclient.R;
import com.alfredposclient.activity.EditSettlementPage;
import com.alfredposclient.activity.MainPage;
import com.alfredposclient.activity.StoredCardActivity;
import com.alfredposclient.activity.kioskactivity.MainPageKiosk;
import com.alfredposclient.adapter.OrderDetailAdapter;
import com.alfredposclient.global.App;
import com.alfredposclient.global.UIHelp;
import com.alfredposclient.view.CloseMoneyKeyboard;
import com.alfredposclient.view.CloseMoneyKeyboard.KeyBoardClickListener;
import com.alfredposclient.view.SettlementDetailItemView;
import com.alfredposclient.view.SettlementDetailItemView.ViewResultCall;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CloseOrderWindow implements OnClickListener, KeyBoardClickListener {
	private String TAG = CloseOrderWindow.class.getSimpleName();
	private static final int DURATION_1 = 300;
	private static final int DURATION_2 = 200;
	private static final int OPEN_DELAY = 400;
	private BaseActivity parent;
	private View parentView;
	private Handler handler;
	private View contentView;
	private PopupWindow popupWindow;
	private RelativeLayout rl_menu;
	private LinearLayout ll_pay;
	private ImageView iv_top;
	private ImageView iv_bottom;
	private Button btn_print_receipt;
	private Button btn_close_bill;
	private CloseMoneyKeyboard moneyKeyboard;
	private View swipe;
	private TextView tv_total_amount_num;
	private TextView tv_change_action_num;
	private TextView tv_cards_amount_paid_num;
	private TextView tv_card_no_num;
	private TextView tv_cards_cvv_num;
	private TextView tv_cards_expiration_date_num;
	private TextView et_special_settlement_remarks_text;
	private BigDecimal remainTotal;
	private BigDecimal settlementNum;
	private BigDecimal cash_num;
	private BigDecimal change_num;
	private BigDecimal payment_amount;
	private int paymentType;
	private String cardNo;
	private Order order;
	private int paymentTypeId;
	private int viewTag = 0; // 当前显示的view的标志
	private TextView selectView;
	private List<OrderDetail> orderDetails = new ArrayList<OrderDetail>();
	private Payment payment;
	private User user; // 授权人
	private List<Map<String, Object>> oldPaymentMapList;
	private List<Map<String, Object>> newPaymentMapList;
	private TextView tv_sub_total_num;
	private TextView tv_discount_num;
	private TextView tv_taxes_num;
	private TextView tv_total_bill_num;
	private TextView tv_rounding_num;
//	private TextView tv_grand_total_bill_num;
	private TextView tv_amount_due_num;

	private TextView tv_special_settlement_title;
	private RelativeLayout rl_special_settlement_person;
	private RelativeLayout rl_special_settlement_phone;
	private LinearLayout ll_settlement_details;
	private LinearLayout ll_bill_summary;
	private TextView tv_special_settlement_amount_due_num;
	private TextView tv_special_settlement_authorize_by_name;
	private TextView tv_cards_name;
	private TextView tv_cards_amount_due_num;
	private TextView tv_nets_amount_due_num;
	private TextView tv_nets_ref_num;
	private TextView tv_nets_amount_paid_num;

	private TextView tv_wechat_ali_ref_num;
	private TextView tv_wechat_ali_amount_due_num;
	private TextView tv_wechat_ali_amount_paid_num;

	private boolean isMenuClose = false;
	private ImageView iv_card_img;
	private LinearLayout ll_all_settlements;
	private LinearLayout ll_bill_layout;
	private TextView tv_change_num;
	private OrderDetailAdapter orderDetailAdapter;
	private OrderBill orderBill;
	private float startX;
	private LinearLayout ll_subtotal_layout;
//	private AlipayWebView web_alipay;
	private Button btn_void_all_closed;
	private String oldTotal;

	private boolean isFirstClickCash = false;
	private List<PaymentSettlement> paymentSettlements = new ArrayList<>();
//	private BigDecimal includTax;
	public CloseOrderWindow(BaseActivity parent, View parentView,
			Handler handler) {
		this.parent = parent;
		this.parentView = parentView;
		this.handler = handler;
		initView();
	}

	public void setUser(User user) {
		this.user = user;
	}

	private void initView() {
		contentView = LayoutInflater.from(parent).inflate(
				R.layout.popup_close_bill, null);
		popupWindow = new PopupWindow(parentView,
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);
//		rl_pay_panel = (RelativeLayout) contentView
//				.findViewById(R.id.rl_pay_panel);
//		web_alipay = (AlipayWebView) contentView.findViewById(R.id.web_alipay);
		ll_subtotal_layout = (LinearLayout) contentView.findViewById(R.id.ll_subtotal_layout);
		tv_change_num = (TextView) contentView
				.findViewById(R.id.tv_change_num);
		rl_menu = (RelativeLayout) contentView.findViewById(R.id.rl_menu);
		ll_pay = (LinearLayout) contentView.findViewById(R.id.ll_pay);
		iv_top = (ImageView) contentView.findViewById(R.id.iv_top);
		iv_bottom = (ImageView) contentView.findViewById(R.id.iv_bottom);

		btn_close_bill = (Button) contentView.findViewById(R.id.btn_close_bill);
		btn_print_receipt = (Button) contentView
				.findViewById(R.id.btn_print_receipt);
		tv_cards_amount_paid_num = (TextView) contentView
				.findViewById(R.id.tv_cards_amount_paid_num);
		tv_card_no_num = (TextView) contentView
				.findViewById(R.id.tv_card_no_num);

		tv_total_amount_num = (TextView) contentView
				.findViewById(R.id.tv_total_amount_num);
		tv_change_action_num = (TextView) contentView
				.findViewById(R.id.tv_change_action_num);

		btn_void_all_closed = (Button) contentView.findViewById(R.id.btn_void_all_closed);
//		tv_item_count_num = (TextView) contentView.findViewById(R.id.tv_item_count_num);
		tv_sub_total_num = (TextView) contentView.findViewById(R.id.tv_sub_total_num);
		tv_discount_num = (TextView) contentView.findViewById(R.id.tv_discount_num);
		tv_taxes_num = (TextView) contentView.findViewById(R.id.tv_taxes_num);
		tv_total_bill_num = (TextView) contentView.findViewById(R.id.tv_total_bill_num);
		tv_rounding_num = (TextView) contentView.findViewById(R.id.tv_rounding_num);
//		tv_grand_total_bill_num = (TextView) contentView.findViewById(R.id.tv_grand_total_bill_num);
//		tv_settled_num = (TextView) contentView.findViewById(R.id.tv_settled_num);


		tv_special_settlement_title = (TextView) contentView
				.findViewById(R.id.tv_special_settlement_title);
		rl_special_settlement_person = (RelativeLayout) contentView
				.findViewById(R.id.rl_special_settlement_person);
		rl_special_settlement_phone = (RelativeLayout) contentView
				.findViewById(R.id.rl_special_settlement_phone);

		ll_settlement_details = (LinearLayout) contentView.findViewById(R.id.ll_settlement_details);
		ll_bill_summary = (LinearLayout) contentView.findViewById(R.id.ll_bill_summary);

		tv_amount_due_num = (TextView) contentView.findViewById(R.id.tv_amount_due_num);

		tv_special_settlement_amount_due_num = (TextView) contentView
				.findViewById(R.id.tv_special_settlement_amount_due_num);
		tv_special_settlement_authorize_by_name = (TextView) contentView
				.findViewById(R.id.tv_special_settlement_authorize_by_name);
		et_special_settlement_remarks_text = (TextView) contentView
				.findViewById(R.id.et_special_settlement_remarks_text);

		tv_nets_amount_paid_num = (TextView) contentView.findViewById(R.id.tv_nets_amount_paid_num);
		tv_cards_name = (TextView) contentView.findViewById(R.id.tv_cards_name);
		tv_cards_amount_due_num = (TextView) contentView.findViewById(R.id.tv_cards_amount_due_num);
		tv_cards_expiration_date_num = (TextView) contentView
				.findViewById(R.id.tv_cards_expiration_date_num);
		tv_cards_cvv_num = (TextView) contentView
				.findViewById(R.id.tv_cards_cvv_num);
		tv_nets_amount_due_num = (TextView) contentView.findViewById(R.id.tv_nets_amount_due_num);
		tv_nets_ref_num = (TextView) contentView.findViewById(R.id.tv_nets_ref_num);
		iv_card_img = (ImageView) contentView.findViewById(R.id.iv_card_img);

		//wechat and alipay
		tv_wechat_ali_amount_due_num = (TextView) contentView.findViewById(R.id.tv_wechat_ali_amount_due_num);
		tv_wechat_ali_ref_num = (TextView) contentView.findViewById(R.id.tv_wechat_ali_ref_num);
		tv_wechat_ali_amount_paid_num = (TextView) contentView.findViewById(R.id.tv_wechat_ali_amount_paid_num);

		ll_all_settlements = (LinearLayout) contentView.findViewById(R.id.ll_all_settlements);
//		iv_shadow_pop = (ImageView) contentView.findViewById(R.id.iv_shadow_pop);
		moneyKeyboard = (CloseMoneyKeyboard) contentView
				.findViewById(R.id.cashKeyboard);
		moneyKeyboard.setKeyBoardClickListener(this);
		moneyKeyboard.setVisibility(View.GONE);
//		swipe = findViewById(R.id.swipe);
//		swipe.setOnClickListener(this);

		contentView.findViewById(R.id.tv_Others).setOnClickListener(this);
		contentView.findViewById(R.id.tv_exact).setOnClickListener(this);
		contentView.findViewById(R.id.iv_VISA).setOnClickListener(this);
		contentView.findViewById(R.id.iv_UnionPay_CN).setOnClickListener(this);
		contentView.findViewById(R.id.tv_cash_200).setOnClickListener(this);
		contentView.findViewById(R.id.tv_cash_150).setOnClickListener(this);
		contentView.findViewById(R.id.tv_cash_100).setOnClickListener(this);
		contentView.findViewById(R.id.tv_cash_50).setOnClickListener(this);
		contentView.findViewById(R.id.tv_cash_20).setOnClickListener(this);
		contentView.findViewById(R.id.tv_cash_10).setOnClickListener(this);
		contentView.findViewById(R.id.tv_cash_5).setOnClickListener(this);
		contentView.findViewById(R.id.iv_MasterCard).setOnClickListener(this);
		contentView.findViewById(R.id.iv_NETS).setOnClickListener(this);
		contentView.findViewById(R.id.iv_UnionPay).setOnClickListener(this);
		contentView.findViewById(R.id.iv_JCB).setOnClickListener(this);
		contentView.findViewById(R.id.iv_AMERICAN).setOnClickListener(this);
		contentView.findViewById(R.id.iv_dinersclub).setOnClickListener(this);
		contentView.findViewById(R.id.tv_BILL_on_HOLD).setOnClickListener(this);
		contentView.findViewById(R.id.tv_VOID).setOnClickListener(this);
		contentView.findViewById(R.id.tv_ENTERTAINMENT)
				.setOnClickListener(this);
		contentView.findViewById(R.id.tv_stored_card).setOnClickListener(this);
		contentView.findViewById(R.id.tv_deliveroo)
				.setOnClickListener(this);
		contentView.findViewById(R.id.tv_ubereats)
				.setOnClickListener(this);
		contentView.findViewById(R.id.tv_foodpanda)
				.setOnClickListener(this);
		contentView.findViewById(R.id.tv_voucher_event)
				.setOnClickListener(this);

		btn_close_bill.setOnClickListener(this);
		btn_print_receipt.setOnClickListener(this);
		btn_void_all_closed.setOnClickListener(this);
		ImageView iv_alipay = (ImageView) contentView.findViewById(R.id.iv_alipay);
		ImageView iv_wechatpay = (ImageView) contentView.findViewById(R.id.iv_wechatpay);
		if(App.countryCode == ParamConst.CHINA){
			contentView.findViewById(R.id.media_keyboard_1).setVisibility(View.GONE);
			contentView.findViewById(R.id.media_keyboard_2).setVisibility(View.VISIBLE);
			iv_alipay.setVisibility(View.VISIBLE);
			iv_alipay.setOnClickListener(this);
			iv_wechatpay.setVisibility(View.VISIBLE);
			iv_wechatpay.setOnClickListener(this);
		}else{

			contentView.findViewById(R.id.media_keyboard_1).setVisibility(View.VISIBLE);
			contentView.findViewById(R.id.media_keyboard_2).setVisibility(View.GONE);
		}

		popupWindow.setContentView(contentView);
		popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
//		popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
//		popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		popupWindow.setFocusable(true);
//		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		initTextTypeFace(contentView);
		ListView lv_list = (ListView) contentView.findViewById(R.id.lv_list);
		orderDetailAdapter = new OrderDetailAdapter(parent, orderDetails, new OrderDetailAdapter.VoidItemCallBack() {
			@Override
			public void callBack(OrderDetail orderDetail) {
				voidItem(orderDetail);
			}
		});
		lv_list.setAdapter(orderDetailAdapter);
	}

	private void init() {
//		Tax tax = App.instance.getLocalRestaurantConfig().getIncludedTax().getTax();
//		if(tax != null){
//			includTax = BH.mul(BH.getBD(tax.getTaxPercentage()), BH.div(BH.sub(BH.getBD(order.getSubTotal()), BH.getBD(order.getDiscountAmount()), false), BH.add(BH.getBD(1), BH.getBD(tax.getTaxPercentage()), false), false), true);
//		} else {
//			includTax = BH.getBD(ParamConst.DOUBLE_ZERO);
//		}
		cash_num = null;
		change_num = null;
		cardNo = null;
		payment = ObjectFactory.getInstance().getPayment(order, orderBill);
		String sumPaidamount = null;
		if (payment != null) {
			sumPaidamount = PaymentSettlementSQL
					.getPaymentSettlementsSumBypaymentId(payment.getId());
		}
		remainTotal = BH.sub(BH.getBD(order.getTotal()),
				BH.getBD(sumPaidamount), true);
		settlementNum = BH.getBD(sumPaidamount);
		// Bob fix bug: If order amount is 0, we restrict it to use CASH
		// settlement
		if (settlementNum.compareTo(BigDecimal.ZERO) == 0) {
			paymentType = ParamConst.SETTLEMENT_TYPE_CASH;
			payment_amount = BH.getBD(ParamConst.DOUBLE_ZERO);
			cash_num = BH.getBD(ParamConst.DOUBLE_ZERO);
			change_num = BH.getBD(ParamConst.DOUBLE_ZERO);
		}
		initBillSummary();
	}


	private void initTextTypeFace(View view) {
		TextTypeFace textTypeFace = TextTypeFace.getInstance();
		textTypeFace.setTrajanProBlod((TextView) view
				.findViewById(R.id.tv_bill_summary));
		textTypeFace.setTrajanProBlod((TextView) view
				.findViewById(R.id.tv_residue_total));
		textTypeFace.setTrajanProBlod((TextView) view
				.findViewById(R.id.tv_residue_total_num));
		textTypeFace.setTrajanProRegular((TextView) view
				.findViewById(R.id.tv_item_name));
		textTypeFace.setTrajanProRegular((TextView) view
				.findViewById(R.id.tv_item_price));
		textTypeFace.setTrajanProRegular((TextView) view
				.findViewById(R.id.tv_item_qty));
		textTypeFace.setTrajanProRegular((TextView) view
				.findViewById(R.id.tv_item_total));
		textTypeFace.setTrajanProBlod((Button) view
				.findViewById(R.id.btn_close_bill));
		textTypeFace.setTrajanProRegular((Button) view
				.findViewById(R.id.btn_print_receipt));
		textTypeFace.setTrajanProRegular((TextView) view
				.findViewById(R.id.tv_total_bill));
		textTypeFace.setTrajanProRegular((TextView) view
				.findViewById(R.id.tv_total_bill_num));
		textTypeFace.setTrajanProRegular((TextView) view
				.findViewById(R.id.tv_rounding));
		textTypeFace.setTrajanProRegular((TextView) view
				.findViewById(R.id.tv_rounding_num));
//		textTypeFace.setTrajanProRegular((TextView) view
//				.findViewById(R.id.tv_grand_total_bill));
//		textTypeFace.setTrajanProRegular((TextView) view
//				.findViewById(R.id.tv_grand_total_bill_num));
		textTypeFace.setTrajanProRegular((TextView) view
				.findViewById(R.id.tv_sub_total));
		textTypeFace.setTrajanProRegular((TextView) view
				.findViewById(R.id.tv_sub_total_num));
		textTypeFace.setTrajanProBlod((TextView) view
				.findViewById(R.id.tv_change));
		textTypeFace.setTrajanProBlod(tv_change_num);
		tv_change_num.setText(App.instance.getLocalRestaurantConfig().getCurrencySymbol() + BH.getBD(0).toString());
		textTypeFace.setTrajanProRegular((TextView) view
				.findViewById(R.id.tv_item_count));
		TextView tv_discount = (TextView) view
				.findViewById(R.id.tv_discount);
		textTypeFace.setTrajanProRegular(tv_discount);
		tv_discount.setText(parent.getResources().getString(R.string.discount_));
		textTypeFace.setTrajanProRegular((TextView) view
				.findViewById(R.id.tv_discount_num));
		TextView tv_taxes = (TextView) view
				.findViewById(R.id.tv_taxes);
		textTypeFace.setTrajanProRegular(tv_taxes);
		tv_taxes.setText(parent.getResources().getString(R.string.taxes_));
		textTypeFace.setTrajanProRegular((TextView) view
				.findViewById(R.id.tv_taxes_num));
		TextView tv_cash_200 = (TextView) view
			.findViewById(R.id.tv_cash_200);
		TextView tv_cash_50 = (TextView) view
				.findViewById(R.id.tv_cash_50);
		TextView tv_cash_10 = (TextView) view
				.findViewById(R.id.tv_cash_10);
		TextView tv_cash_150 = (TextView) view
				.findViewById(R.id.tv_cash_150);
		TextView tv_cash_20 = (TextView) view
				.findViewById(R.id.tv_cash_20);
		TextView tv_cash_5 = (TextView) view
				.findViewById(R.id.tv_cash_5);
		TextView tv_cash_100 = (TextView) view
				.findViewById(R.id.tv_cash_100);

		textTypeFace.setTrajanProRegular((TextView) view
				.findViewById(R.id.tv_cash));
		textTypeFace.setTrajanProRegular(tv_cash_200);
		tv_cash_200.setText(App.instance.getLocalRestaurantConfig().getCurrencySymbol() + "200");
		textTypeFace.setTrajanProRegular(tv_cash_50);
		tv_cash_50.setText(App.instance.getLocalRestaurantConfig().getCurrencySymbol() + "50");
		textTypeFace.setTrajanProRegular(tv_cash_10);
		tv_cash_10.setText(App.instance.getLocalRestaurantConfig().getCurrencySymbol() + "10");
		textTypeFace.setTrajanProRegular(tv_cash_150);
		tv_cash_150.setText(App.instance.getLocalRestaurantConfig().getCurrencySymbol() + "150");
		textTypeFace.setTrajanProRegular(tv_cash_20);
		tv_cash_20.setText(App.instance.getLocalRestaurantConfig().getCurrencySymbol() + "20");
		textTypeFace.setTrajanProRegular(tv_cash_5);
		tv_cash_5.setText(App.instance.getLocalRestaurantConfig().getCurrencySymbol() + "5");
		textTypeFace.setTrajanProRegular(tv_cash_100);
		tv_cash_100.setText(App.instance.getLocalRestaurantConfig().getCurrencySymbol() + "100");
		textTypeFace.setTrajanProRegular((TextView) view
				.findViewById(R.id.tv_Others));
		textTypeFace.setTrajanProRegular((TextView) view
				.findViewById(R.id.tv_exact));

		textTypeFace.setTrajanProRegular((TextView) view
				.findViewById(R.id.tv_media));
		textTypeFace.setTrajanProRegular((TextView) view
				.findViewById(R.id.tv_adjustment));
		textTypeFace.setTrajanProRegular((TextView) view
				.findViewById(R.id.tv_BILL_on_HOLD));
		textTypeFace.setTrajanProRegular((TextView) view
				.findViewById(R.id.tv_VOID));
		textTypeFace.setTrajanProRegular((TextView) view
				.findViewById(R.id.tv_ENTERTAINMENT));
		textTypeFace.setTrajanProRegular((TextView) view
				.findViewById(R.id.tv_stored_card));

		textTypeFace.setTrajanProRegular((TextView) view
				.findViewById(R.id.tv_delivery));
		textTypeFace.setTrajanProRegular((TextView) view
				.findViewById(R.id.tv_deliveroo));
		textTypeFace.setTrajanProRegular((TextView) view
				.findViewById(R.id.tv_ubereats));
		textTypeFace.setTrajanProRegular((TextView) view
				.findViewById(R.id.tv_foodpanda));

		textTypeFace.setTrajanProRegular((TextView) view
				.findViewById(R.id.tv_voucher));
		textTypeFace.setTrajanProRegular((TextView) view
				.findViewById(R.id.tv_voucher_event));

		textTypeFace.setTrajanProRegular((TextView) view
				.findViewById(R.id.tv_cash_settlement));
		textTypeFace.setTrajanProRegular((TextView) view
				.findViewById(R.id.tv_amount_due));
		textTypeFace.setTrajanProRegular((TextView) view
				.findViewById(R.id.tv_amount_due_num));
		textTypeFace.setTrajanProRegular((TextView) view
				.findViewById(R.id.tv_total_amount));
		textTypeFace.setTrajanProRegular((TextView) view
				.findViewById(R.id.tv_total_amount_num));
		textTypeFace.setTrajanProRegular((TextView) view
				.findViewById(R.id.tv_change_action));
		textTypeFace.setTrajanProRegular((TextView) view
				.findViewById(R.id.tv_change_action_num));

		textTypeFace.setTrajanProRegular((TextView) view
				.findViewById(R.id.tv_cards_name));
		textTypeFace.setTrajanProRegular((TextView) view
				.findViewById(R.id.tv_cards_amount_due));
		textTypeFace.setTrajanProRegular((TextView) view
				.findViewById(R.id.tv_cards_amount_due_num));
		textTypeFace.setTrajanProRegular((TextView) view
				.findViewById(R.id.tv_cards_amount_paid));
		textTypeFace.setTrajanProRegular((TextView) view
				.findViewById(R.id.tv_cards_amount_paid_num));
		TextView tv_cards_amount_symbol =  (TextView) view
				.findViewById(R.id.tv_cards_amount_symbol);
		textTypeFace.setTrajanProRegular(tv_cards_amount_symbol);
		tv_cards_amount_symbol.setText(App.instance.getLocalRestaurantConfig().getCurrencySymbol());
		textTypeFace.setTrajanProRegular((TextView) view
				.findViewById(R.id.tv_card_no));
		textTypeFace.setTrajanProRegular((TextView) view
				.findViewById(R.id.tv_card_no_num));
		textTypeFace.setTrajanProRegular((TextView) view
				.findViewById(R.id.tv_cards_cvv));
		textTypeFace.setTrajanProRegular((TextView) view
				.findViewById(R.id.tv_cards_cvv_num));
		textTypeFace.setTrajanProRegular((TextView) view
				.findViewById(R.id.tv_cards_expiration_date));
		textTypeFace.setTrajanProRegular((TextView) view
				.findViewById(R.id.tv_cards_expiration_date_num));

		textTypeFace.setTrajanProRegular((TextView) view
				.findViewById(R.id.tv_special_settlement_title));
		textTypeFace.setTrajanProRegular((TextView) view
				.findViewById(R.id.tv_special_settlement_amount_due));
		textTypeFace.setTrajanProRegular((TextView) view
				.findViewById(R.id.tv_special_settlement_amount_due_num));
		textTypeFace.setTrajanProRegular((TextView) view
				.findViewById(R.id.tv_special_settlement_authorize_by));
		textTypeFace.setTrajanProRegular((TextView) view
				.findViewById(R.id.tv_special_settlement_authorize_by_name));
		textTypeFace.setTrajanProRegular((EditText) view
				.findViewById(R.id.et_special_settlement_person_name));
		textTypeFace.setTrajanProRegular((EditText) view
				.findViewById(R.id.et_special_settlement_phone_text));
		textTypeFace.setTrajanProRegular((TextView) view
				.findViewById(R.id.tv_special_settlement_remarks));
		textTypeFace.setTrajanProRegular((EditText) view
				.findViewById(R.id.et_special_settlement_remarks_text));
		textTypeFace.setTrajanProRegular(btn_void_all_closed);

		textTypeFace.setTrajanProRegular((TextView) view
				.findViewById(R.id.tv_nets_settlement));
		textTypeFace.setTrajanProRegular((TextView) view
				.findViewById(R.id.tv_nets_amount_due));
		textTypeFace.setTrajanProRegular((TextView) view
				.findViewById(R.id.tv_nets_amount_due_num));
		textTypeFace.setTrajanProRegular((TextView) view
				.findViewById(R.id.tv_nets_ref));
		textTypeFace.setTrajanProRegular((TextView) view
				.findViewById(R.id.tv_nets_ref_num));
		textTypeFace.setTrajanProRegular((TextView)view
				.findViewById(R.id.tv_nets_amount_paid));
		textTypeFace.setTrajanProRegular((TextView)view
				.findViewById(R.id.tv_nets_amount_paid_num));
		TextView tv_nets_amount_symbol = (TextView)view
				.findViewById(R.id.tv_nets_amount_symbol);
		textTypeFace.setTrajanProRegular(tv_nets_amount_symbol);
		tv_nets_amount_symbol.setText(App.instance.getLocalRestaurantConfig().getCurrencySymbol());
		textTypeFace.setTrajanProBlod((TextView) view
				.findViewById(R.id.tv_settlement_details));
	}

	/**
	 * 现在还不确定设计稿的样式 先这样做
	 *
	 */
	private void initBillSummary() {
		if (order.getOrderStatus() == ParamConst.ORDER_STATUS_FINISHED
				&& !(parent instanceof EditSettlementPage)) {
			settlementNum = BH.getBD(order.getTotal());
			remainTotal = BH.getBD(ParamConst.DOUBLE_ZERO);

		} else {
			String sumPaidamount = null;
			if (payment != null) {
				sumPaidamount = PaymentSettlementSQL
						.getPaymentSettlementsSumBypaymentId(payment.getId());
			}
			remainTotal = BH.sub(BH.getBD(order.getTotal()),
					BH.getBD(sumPaidamount), true);
			settlementNum = BH.getBD(sumPaidamount);
		}
		((TextView)contentView.findViewById(R.id.tv_residue_total_num)).setText(App.instance.getLocalRestaurantConfig().getCurrencySymbol() + BH.getBD(remainTotal).toString());


//		RoundAmount roundAmount = RoundAmountSQL.getRoundAmount(order);
//		tv_item_count_num.setText(getItemNumSum() + "");
		tv_sub_total_num.setText(App.instance.getLocalRestaurantConfig().getCurrencySymbol() + BH.getBD(order.getSubTotal()).toString());
		tv_discount_num.setText(App.instance.getLocalRestaurantConfig().getCurrencySymbol() + BH.getBD(order.getDiscountAmount()).toString());
		tv_taxes_num.setText(App.instance.getLocalRestaurantConfig().getCurrencySymbol() + BH.getBD(order.getTaxAmount()).toString());
		tv_total_bill_num.setText(App.instance.getLocalRestaurantConfig().getCurrencySymbol() + BH.getBD(order.getTotal()).toString());
//		tv_rounding_num.setText(App.instance.getLocalRestaurantConfig().getCurrencySymbol() + BH.getBD(roundAmount.getRoundBalancePrice()).toString());
//		tv_grand_total_bill_num.setText(App.instance.getLocalRestaurantConfig().getCurrencySymbol() + order.getTotal());
//		tv_settled_num.setText(settlementNum.toString());

		if (settlementNum.compareTo(BH.getBD(order.getTotal())) == 0) {
			order.setOrderStatus(ParamConst.ORDER_STATUS_FINISHED);
			OrderSQL.update(order);
			btn_print_receipt.setVisibility(View.VISIBLE);
            if(!(parent instanceof EditSettlementPage))
			    btn_close_bill.setVisibility(View.GONE);
		} else {
			btn_print_receipt.setVisibility(View.GONE);
			btn_close_bill.setVisibility(View.VISIBLE);
		}
		paymentSettlements = PaymentSettlementSQL
				.getPaymentSettlementsBypaymentId(payment.getId());
		if(parent instanceof EditSettlementPage && paymentSettlements != null && paymentSettlements.size() > 0){
			if(paymentSettlements.size() == 1
					&& (paymentSettlements.get(0).getPaymentTypeId().intValue() == ParamConst.SETTLEMENT_TYPE_CASH
					|| paymentSettlements.get(0).getPaymentTypeId().intValue() == ParamConst.SETTLEMENT_TYPE_AMEX
					|| paymentSettlements.get(0).getPaymentTypeId().intValue() == ParamConst.SETTLEMENT_TYPE_MASTERCARD
					|| paymentSettlements.get(0).getPaymentTypeId().intValue() == ParamConst.SETTLEMENT_TYPE_UNIPAY
					|| paymentSettlements.get(0).getPaymentTypeId().intValue() == ParamConst.SETTLEMENT_TYPE_VISA
					|| paymentSettlements.get(0).getPaymentTypeId().intValue() == ParamConst.SETTLEMENT_TYPE_DINNER_INTERMATIONAL
					|| paymentSettlements.get(0).getPaymentTypeId().intValue() == ParamConst.SETTLEMENT_TYPE_JCB)){
				orderDetailAdapter.setIsShowCheckBox(true);
				btn_void_all_closed.setVisibility(View.VISIBLE);
			}else{
				if(paymentSettlements.get(0).getPaymentTypeId().intValue() != ParamConst.SETTLEMENT_TYPE_VOID
						&& paymentSettlements.get(0).getPaymentTypeId().intValue() != ParamConst.SETTLEMENT_TYPE_REFUND
						&& paymentSettlements.get(0).getPaymentTypeId().intValue() != ParamConst.SETTLEMENT_TYPE_ENTERTAINMENT){
					btn_void_all_closed.setVisibility(View.VISIBLE);
				}else{
					btn_void_all_closed.setVisibility(View.GONE);
				}
				orderDetailAdapter.setIsShowCheckBox(false);
			}
		}else{
			orderDetailAdapter.setIsShowCheckBox(false);
			btn_void_all_closed.setVisibility(View.GONE);
		}
		orderDetailAdapter.setList(orderDetails);
		orderDetailAdapter.notifyDataSetChanged();
		initSettlementDetail();
	}

	private void initSettlementDetail() {
		ll_settlement_details.setVisibility(
				View.VISIBLE);
//		ll_bill_summary.setVisibility(View.GONE);
		ll_all_settlements.removeAllViews();
//		tv_settlement_num.setText(App.instance.getLocalRestaurantConfig().getCurrencySymbol() + settlementNum.toString());

		for(PaymentSettlement paymentSettlement : paymentSettlements){
			SettlementDetailItemView settlementDetailItemView = new SettlementDetailItemView(parent);
			settlementDetailItemView.setParams(paymentSettlement, new ViewResultCall(){

				@Override
				public void call(PaymentSettlement paymentSettlement) {
					int paymentTypeId = paymentSettlement.getPaymentTypeId()
							.intValue();
//					if(paymentTypeId == ParamConst.SETTLEMENT_TYPE_PAYPAL)
//						return;
//					}
					if(paymentTypeId == ParamConst.SETTLEMENT_TYPE_PAYPAL)
						return;
					remainTotal = BH.getBD(paymentSettlement.getTotalAmount());
					settlementNum = BH.sub(settlementNum,
							BH.getBD(paymentSettlement.getPaidAmount()), true);
//					((TextView) contentView.findViewById(R.id.tv_settlement_num))
//							.setText(App.instance.getLocalRestaurantConfig().getCurrencySymbol() + settlementNum.toString());
					if (!(parent instanceof EditSettlementPage)) {
						order.setOrderStatus(ParamConst.ORDER_STATUS_UNPAY);
					}
					OrderSQL.update(order);
					Object subPaymentSettlement = null;

					switch (paymentTypeId) {
					case ParamConst.SETTLEMENT_TYPE_CASH:
						parent.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								tv_change_num.setText(App.instance.getLocalRestaurantConfig().getCurrencySymbol() + BH.getBD(0).toString());
							}
						});
						RoundAmount roundAmount = RoundAmountSQL.getRoundAmount(order);
						if(roundAmount != null && BH.getBD(roundAmount.getRoundBalancePrice()).compareTo(BH.getBD("0.00")) != 0){
							order.setTotal(BH.sub(BH.getBD(order.getTotal()), BH.getBD(roundAmount.getRoundBalancePrice()), true).toString());
							OrderSQL.update(order);
							if(parent instanceof EditSettlementPage){
								roundAmount.setRoundBalancePrice(0.00);
								RoundAmountSQL.update(roundAmount);
							}else{
								RoundAmountSQL.deleteRoundAmount(roundAmount);
							}
							remainTotal = BH.getBD(order.getTotal());
						}
						break;
					case ParamConst.SETTLEMENT_TYPE_MASTERCARD:
					case ParamConst.SETTLEMENT_TYPE_UNIPAY:
					case ParamConst.SETTLEMENT_TYPE_VISA:
					case ParamConst.SETTLEMENT_TYPE_AMEX:
					case ParamConst.SETTLEMENT_TYPE_DINNER_INTERMATIONAL:
					case ParamConst.SETTLEMENT_TYPE_JCB:
						CardsSettlement cardsSettlement = CardsSettlementSQL
								.getCardsSettlementByPament(payment.getId(),
										paymentSettlement.getId());
						subPaymentSettlement = cardsSettlement;
						if(parent instanceof EditSettlementPage){
							cardsSettlement.setIsActive(ParamConst.PAYMENT_SETT_IS_NO_ACTIVE);
							CardsSettlementSQL.addCardsSettlement(cardsSettlement);
						}else{
							CardsSettlementSQL.deleteCardsSettlement(cardsSettlement);
						}
						break;
					case ParamConst.SETTLEMENT_TYPE_BILL_ON_HOLD:
						BohHoldSettlement bohHoldSettlement = BohHoldSettlementSQL
								.getBohHoldSettlementByPament(
										payment.getId(),
										paymentSettlement.getId());
						subPaymentSettlement = bohHoldSettlement;
						if (parent instanceof EditSettlementPage) {
							bohHoldSettlement.setIsActive(ParamConst.PAYMENT_SETT_IS_NO_ACTIVE);
							BohHoldSettlementSQL.addBohHoldSettlement(bohHoldSettlement);
						} else {
							BohHoldSettlementSQL
								.deleteBohHoldSettlement(bohHoldSettlement);
						}
						break;
					case ParamConst.SETTLEMENT_TYPE_COMPANY:
						// TODO
						break;
					case ParamConst.SETTLEMENT_TYPE_HOURS_CHARGE:
						// TODO
						break;
					case ParamConst.SETTLEMENT_TYPE_VOID: {
						addVoidOrEntTax();
						VoidSettlement voidSettlement = VoidSettlementSQL
								.getVoidSettlementByPament(payment.getId(),
										paymentSettlement.getId());
						subPaymentSettlement = voidSettlement;
						if (parent instanceof EditSettlementPage) {
							voidSettlement.setIsActive(ParamConst.PAYMENT_SETT_IS_NO_ACTIVE);
							VoidSettlementSQL.addVoidSettlement(voidSettlement);
						} else {
							VoidSettlementSQL.deleteVoidSettlement(voidSettlement);
						}
					}
						break;
					case ParamConst.SETTLEMENT_TYPE_REFUND: {
//						addVoidOrEntTax();
						VoidSettlement voidSettlement = VoidSettlementSQL
								.getVoidSettlementByPament(payment.getId(),
										paymentSettlement.getId());
						subPaymentSettlement = voidSettlement;
						if (parent instanceof EditSettlementPage) {
							voidSettlement.setIsActive(ParamConst.PAYMENT_SETT_IS_NO_ACTIVE);
							VoidSettlementSQL.addVoidSettlement(voidSettlement);
						} else {
							VoidSettlementSQL.deleteVoidSettlement(voidSettlement);
						}
					}
						break;
					case ParamConst.SETTLEMENT_TYPE_ENTERTAINMENT:
						addVoidOrEntTax();
						NonChargableSettlement nonChargableSettlement = NonChargableSettlementSQL
								.getNonChargableSettlementByPaymentId(payment.getId(),
										paymentSettlement.getId());
						subPaymentSettlement = nonChargableSettlement;
						if (parent instanceof EditSettlementPage) {
							nonChargableSettlement.setIsActive(ParamConst.PAYMENT_SETT_IS_NO_ACTIVE);
							NonChargableSettlementSQL.addNonChargableSettlement(nonChargableSettlement);
						} else {
							NonChargableSettlementSQL
								.deleteNonChargableSettlement(nonChargableSettlement);
						}
						break;
					case ParamConst.SETTLEMENT_TYPE_NETS:
						NetsSettlement netsSettlement = NetsSettlementSQL
								.getNetsSettlementByPament(payment.getId(),
										paymentSettlement.getId());
						subPaymentSettlement = netsSettlement;
						if (parent instanceof EditSettlementPage) {
							netsSettlement.setIsActive(ParamConst.PAYMENT_SETT_IS_NO_ACTIVE);
							NetsSettlementSQL.addNetsSettlement(netsSettlement);
						} else {
							NetsSettlementSQL.deleteNetsSettlement(netsSettlement);
						}
						break;
					case ParamConst.SETTLEMENT_TYPE_ALIPAY:
						AlipaySettlement alipaySettlement = AlipaySettlementSQL
								.getAlipaySettlementByPament(payment.getId(),
										paymentSettlement.getId());
						subPaymentSettlement = alipaySettlement;
						if (parent instanceof EditSettlementPage) {
							alipaySettlement.setIsActive(ParamConst.PAYMENT_SETT_IS_NO_ACTIVE);
							AlipaySettlementSQL.addAlipaySettlement(alipaySettlement);
						} else {
							AlipaySettlementSQL.deleteAlipaySettlement(alipaySettlement);
						}
						break;
					case ParamConst.SETTLEMENT_TYPE_WEIXIN:
						WeixinSettlement weixinSettlement = WeixinSettlementSQL
								.getWeixinSettlementByPament(payment.getId(),
										paymentSettlement.getId());
						subPaymentSettlement = weixinSettlement;
						if (parent instanceof EditSettlementPage) {
							weixinSettlement.setIsActive(ParamConst.PAYMENT_SETT_IS_NO_ACTIVE);
							WeixinSettlementSQL.addWeixinSettlement(weixinSettlement);
						} else {
							WeixinSettlementSQL.deleteWeixinSettlement(weixinSettlement);
						}
						break;
					default:
						break;
					}

					if (oldPaymentMapList != null) {
						Map<String, Object> paymentMap = new HashMap<String, Object>();
						paymentMap.put("paymentSettlement",
								paymentSettlement);
						paymentMap.put("subPaymentSettlement",
								subPaymentSettlement);
						oldPaymentMapList.add(paymentMap);
					}
					if (parent instanceof EditSettlementPage) {
						paymentSettlement.setIsActive(ParamConst.PAYMENT_SETT_IS_NO_ACTIVE);
						PaymentSettlementSQL.addPaymentSettlement(paymentSettlement);
					} else {
						PaymentSettlementSQL.deletePaymentSettlement(paymentSettlement);
					}

					initBillSummary();
				}
				});
			ll_all_settlements.addView(settlementDetailItemView);
		}

	}

	private void initCashSettlement(Order order) {
		initBillSummary();
		ll_subtotal_layout.setVisibility(
				View.INVISIBLE);
		BigDecimal remainTotalAfterRound = RoundUtil.getPriceAfterRound(App.instance.getLocalRestaurantConfig().getRoundType(), remainTotal);
		show.append(remainTotalAfterRound.toString().replace(".", ""));
		tv_amount_due_num.setText(App.instance.getLocalRestaurantConfig().getCurrencySymbol() + BH.getBD(remainTotalAfterRound).toString());
		BigDecimal rounding = BH.sub(remainTotalAfterRound, remainTotal, true);
		String symbol = "";
		if(rounding.compareTo(BH.getBD("0.00")) == -1){
			symbol = "-";
		}
		tv_rounding_num.setText(symbol + App.instance.getLocalRestaurantConfig().getCurrencySymbol() + BH.abs(rounding, true).toString());
		tv_total_amount_num.setText(App.instance.getLocalRestaurantConfig().getCurrencySymbol() + BH.getBD(remainTotalAfterRound).toString());
		tv_change_action_num.setText(App.instance.getLocalRestaurantConfig().getCurrencySymbol() + BH.getBD(0).toString());
	}

	private void initSpecialSettlement(User user, int type) {
		initBillSummary();
		ll_subtotal_layout.setVisibility(
				View.INVISIBLE);
		switch (type) {
		case ParamConst.SETTLEMENT_TYPE_ENTERTAINMENT:
			tv_special_settlement_title.setText("ENTERTAINMENT");
//			remainTotal = BH.sub(remainTotal, BH.add(BH.getBD(order.getTaxAmount()), includTax, false), true);
			rl_special_settlement_person.setVisibility(View.VISIBLE);
			rl_special_settlement_phone.setVisibility(View.GONE);
			break;
		case ParamConst.SETTLEMENT_TYPE_BILL_ON_HOLD:
			tv_special_settlement_title.setText("BILL_ON_HOLD");
			rl_special_settlement_person.setVisibility(View.VISIBLE);
			rl_special_settlement_phone.setVisibility(View.VISIBLE);
			break;
		case ParamConst.SETTLEMENT_TYPE_VOID:
			tv_special_settlement_title.setText("VOID");
//			remainTotal = BH.sub(remainTotal, BH.add(BH.getBD(order.getTaxAmount()), includTax, false), true);
			rl_special_settlement_person.setVisibility(View.GONE);
			rl_special_settlement_phone.setVisibility(View.GONE);
			break;
		case ParamConst.SETTLEMENT_TYPE_REFUND:
			tv_special_settlement_title.setText("REFUND");
//			remainTotal = BH.sub(remainTotal, BH.add(BH.getBD(order.getTaxAmount()), includTax, false), true);
			rl_special_settlement_person.setVisibility(View.GONE);
			rl_special_settlement_phone.setVisibility(View.GONE);
			break;
		default:
			break;
		}

		tv_special_settlement_amount_due_num.setText(App.instance.getLocalRestaurantConfig().getCurrencySymbol() + BH.getBD(remainTotal).toString());
		tv_special_settlement_authorize_by_name.setText(user.getFirstName() + user.getLastName());

	}

	private void initCardsSettlement(String cardsName) {
		initBillSummary();
		ll_subtotal_layout.setVisibility(
				View.INVISIBLE);
		if (cardsName.equals("Visa")) {
			iv_card_img.setImageResource(R.drawable.img_visa);
		}else if (cardsName.equals("AMEX")) {
			iv_card_img.setImageResource(R.drawable.img_american);
		}else if (cardsName.equals("JCB")) {
			iv_card_img.setImageResource(R.drawable.img_jbc);
		}else if (cardsName.equals("MASTERCARD")) {
			iv_card_img.setImageResource(R.drawable.img_master);
		}else if (cardsName.equals("UNIONPAY")) {
			iv_card_img.setImageResource(R.drawable.img_upay);
		}else if (cardsName.equals("Dinners")) {
			iv_card_img.setImageResource(R.drawable.img_diners);
		}
		if (App.instance.countryCode == ParamConst.CHINA) {
		   if (cardsName.equals("UNIONPAY"))
			   tv_cards_name.setText("银联");
		   else if (cardsName.equals("MASTERCARD"))
			   tv_cards_name.setText("Master Card");
		   else
			   tv_cards_name.setText(cardsName);
		}else {
		  tv_cards_name.setText(cardsName);
		}

		tv_card_no_num.setText("");

		tv_cards_cvv_num.setText("");

		tv_cards_expiration_date_num.setText("");
		tv_cards_amount_due_num.setText(App.instance.getLocalRestaurantConfig().getCurrencySymbol() + BH.getBD(remainTotal).toString());
		tv_cards_amount_paid_num.setText(BH.getBD(remainTotal).toString());
		selectView = tv_card_no_num;
		tv_cards_amount_paid_num.setBackgroundColor(parent.getResources().getColor(
				R.color.white));
		tv_card_no_num.setBackgroundColor(parent.getResources()
				.getColor(R.color.default_line_indicator_selected_color));
		tv_cards_cvv_num.setBackgroundColor(parent.getResources()
				.getColor(R.color.white));
		tv_cards_expiration_date_num.setBackgroundColor(parent
				.getResources().getColor(R.color.white));
		contentView.findViewById(R.id.rl_cards_amount_paid_num).setOnClickListener(this);
		contentView.findViewById(R.id.rl_card_no).setOnClickListener(this);
		contentView.findViewById(R.id.ll_cards_cvv_num).setOnClickListener(this);
		contentView.findViewById(R.id.ll_cards_expiration_date_num).setOnClickListener(this);
	}


	private void initNetsSettlement() {
		initBillSummary();
		ll_subtotal_layout.setVisibility(
				View.INVISIBLE);
		tv_nets_amount_due_num.setText(App.instance.getLocalRestaurantConfig().getCurrencySymbol() + BH.getBD(remainTotal).toString());
		tv_nets_ref_num.setText("");
		tv_nets_amount_paid_num.setText(BH.getBD(remainTotal).toString());
		tv_nets_ref_num.setBackgroundColor(parent.getResources()
				.getColor(R.color.default_line_indicator_selected_color));
		tv_nets_amount_paid_num.setBackgroundColor(parent.getResources()
				.getColor(R.color.white));
		selectView = tv_nets_ref_num;
		contentView.findViewById(R.id.rl_nets_amount_paid_num).setOnClickListener(this);
		contentView.findViewById(R.id.rl_nets_ref_num).setOnClickListener(this);
	}

	private void initWeChatAlipaySettlement(int payTypeId) {
		initBillSummary();
		ll_subtotal_layout.setVisibility(
				View.INVISIBLE);
		TextView tv_wechat_ali_settlement = (TextView) contentView.findViewById(R.id.tv_wechat_ali_settlement);
		if (payTypeId == ParamConst.SETTLEMENT_TYPE_ALIPAY) {
			tv_wechat_ali_settlement.setText(parent.getResources().getString(R.string.alipay_settlement));
		} else if (payTypeId == ParamConst.SETTLEMENT_TYPE_WEIXIN){
			tv_wechat_ali_settlement.setText(parent.getResources().getString(R.string.wechat_settlement));
		}
		tv_wechat_ali_amount_due_num.setText(App.instance.getLocalRestaurantConfig().getCurrencySymbol() + BH.getBD(remainTotal).toString());
		tv_wechat_ali_ref_num.setText("");
		tv_wechat_ali_amount_paid_num.setText(BH.getBD(remainTotal).toString());
//		tv_wechat_ali_ref_num.setBackgroundColor(parent.getResources()
//				.getColor(R.color.default_line_indicator_selected_color));
		tv_wechat_ali_amount_paid_num.setBackgroundColor(parent.getResources()
				.getColor(R.color.white));
		selectView = tv_wechat_ali_ref_num;
		contentView.findViewById(R.id.rl_wechat_ali_amount_paid_num).setOnClickListener(this);
		contentView.findViewById(R.id.rl_wechat_ali_ref_num).setOnClickListener(this);
	}

	private int getItemNumSum() {
		orderDetails = OrderDetailSQL.getOrderDetails(order.getId());
		int itemCount = 0;
		if (orderDetails.isEmpty()) {
			return itemCount;
		}
		for (OrderDetail orderDetail : orderDetails) {
			itemCount += orderDetail.getItemNum();
		}
		return itemCount;
	}

	public void show(View view,Order order, float startX, OrderBill orderBill, List<OrderDetail> orderDetailList) {
		if (isShowing()) {
			return;
		}
		App.instance.setClosingOrderId(order.getId());
		this.orderBill = orderBill;
		this.startX = startX;
		tv_change_num.setText(App.instance.getLocalRestaurantConfig().getCurrencySymbol() + BH.getBD(0).toString());
		this.order = order;
		this.oldTotal = this.order.getTotal();
		if (parent instanceof EditSettlementPage) {
			this.newPaymentMapList = new ArrayList<Map<String,Object>>();
			this.oldPaymentMapList = new ArrayList<Map<String,Object>>();
		}
		if (order == null) {
			return;
		}
		this.orderDetails = orderDetailList;
		init();

		if (show.length() > 0) {
			show.delete(0, show.length());
		}
		popupWindow
				.showAtLocation(parentView, Gravity.LEFT | Gravity.TOP, 0, 0);
//		popupWindow
//				.showAtLocation(parentView, Gravity.LEFT | Gravity.TOP, 0, ScreenSizeUtil.getStatusBarHeight(parent));
//		popupWindow.setAnimationStyle(0);
//		popupWindow.showAsDropDown(view,0,0);
		ll_pay.setVisibility(View.VISIBLE);
		moneyKeyboard.setVisibility(View.GONE);
		ll_subtotal_layout.setVisibility(
				View.VISIBLE);
		contentView.findViewById(R.id.ll_cash_settlement).setVisibility(
				View.INVISIBLE);
		contentView.findViewById(R.id.ll_cards_settlement).setVisibility(
				View.INVISIBLE);
		contentView.findViewById(R.id.ll_nets_settlement).setVisibility(
				View.INVISIBLE);
		contentView.findViewById(R.id.ll_wechat_ali_settlement).setVisibility(
				View.INVISIBLE);
		contentView.findViewById(R.id.ll_special_settlement).setVisibility(
				View.INVISIBLE);
		ll_bill_layout = (LinearLayout) contentView.findViewById(R.id.ll_bill_layout);
		ll_bill_layout.setX(startX);
		ll_bill_layout.setVisibility(View.INVISIBLE);
		ll_bill_layout.postDelayed(new Runnable() {

			@Override
			public void run() {
				ll_bill_layout.setVisibility(View.VISIBLE);
			}
		}, 100);
		ll_bill_layout.postDelayed((new Runnable() {
//
			@Override
			public void run() {

				AnimatorSet set = new AnimatorSet();

				ObjectAnimator animator = ObjectAnimator.ofFloat(
						ll_bill_layout,
						"translationX",
						CloseOrderWindow.this.startX,
						0)
						.setDuration(200);
				set.play(animator);
				set.setInterpolator(new DecelerateInterpolator());
				set.addListener(new AnimatorListenerImpl(){
					@Override
					public void onAnimationStart(Animator animation) {
						super.onAnimationStart(animation);

					}
				});
				set.start();
			}
		}),300);
		App.instance.orderInPayment = order;
		isMenuClose = false;
	}

	public void dismiss() {
		if (popupWindow != null && popupWindow.isShowing()) {
			oldPaymentMapList = null;
			newPaymentMapList = null;
			popupWindow.dismiss();
			App.instance.orderInPayment = null;
		}
		App.instance.setClosingOrderId(0);
	}

	public boolean isShowing() {
		if (popupWindow != null) {
			return popupWindow.isShowing();
		}
		return false;
	}

	private void closeWindowAction() {

		AnimatorSet set = new AnimatorSet();
		ObjectAnimator animator = ObjectAnimator.ofFloat(ll_bill_layout,
				"translationX", 0, startX).setDuration(200);
		set.play(animator);
		set.setInterpolator(new DecelerateInterpolator());
		set.addListener(new AnimatorListenerImpl() {
			public void onAnimationEnd(Animator animation) {
				super.onAnimationEnd(animation);
				dismiss();
				handler.sendEmptyMessage(MainPageKiosk.VIEW_EVENT_SET_DATA);
			};
		});
		set.start();
	}

	private void printReceiptAction(int tableId) {
		if(!App.instance.isRevenueKiosk()){
			TableInfo tables = TableInfoSQL.getTableById(tableId);
			tables.setStatus(ParamConst.TABLE_STATUS_IDLE);
			TableInfoSQL.updateTables(tables);
		}
		// }
		AnimatorSet set = new AnimatorSet();
		ObjectAnimator animator = ObjectAnimator.ofFloat(ll_bill_layout,
				"translationX", 0, startX).setDuration(200);
		set.play(animator);
		set.setInterpolator(new DecelerateInterpolator());
		set.addListener(new AnimatorListenerImpl() {
			public void onAnimationEnd(Animator animation) {
				super.onAnimationEnd(animation);
				// popupWindow.dismiss();
				dismiss();
				handler.sendEmptyMessage(MainPage.VIEW_EVENT_SHOW_TABLES_AFTER_CLOSE_BILL);
			};
		});
		set.start();
	}



	private void printBill(final String change){
		final int paidOrderId = order.getId();
		final int tabelId = order.getTableId();
		new Thread(new Runnable() {

			@Override
			public void run() {

				//DONE all KOT SUMMARY and ITEM DETAILS
				KotSummary kotSummary = KotSummarySQL
						.getKotSummary(paidOrderId);
				PaymentSettlementSQL.deleteAllNoActiveSettlement(payment);
				if (!App.instance.isRevenueKiosk() && kotSummary != null) {
					List<KotItemDetail> kotItemDetails = KotItemDetailSQL.getKotItemDetailByKotSummaryIdUndone(kotSummary);

					if(kotItemDetails != null)
						for(KotItemDetail kotItemDetail : kotItemDetails){
							kotItemDetail.setKotStatus(ParamConst.KOT_STATUS_DONE);
							KotItemDetailSQL.update(kotItemDetail);
						}

					kotSummary.setStatus(ParamConst.KOTS_STATUS_DONE);
					KotSummarySQL.update(kotSummary);
				}
				HashMap<String, String> map = new HashMap<String, String>();

				if(TextUtils.isEmpty(change))
				{
					map.put("orderId", String.valueOf(paidOrderId));
					map.put("paymentId", String.valueOf(payment.getId().intValue()));
					map.put("changeNum", String.valueOf(""));
				}else {
					map.put("orderId", String.valueOf(paidOrderId));
					map.put("paymentId", String.valueOf(payment.getId().intValue()));
					map.put("changeNum", String.valueOf(change));
				}

				// to print close receipt
				handler.sendMessage(handler.obtainMessage(
						MainPage.VIEW_EVENT_CLOSE_BILL, map));
				parent.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// show table after settlement is done
						printReceiptAction(tabelId);
					}
				});
			}
		}).start();
	}

	public void voidItem(final OrderDetail orderDetail){
		DialogFactory.commonTwoBtnDialog(parent, "Warring", "This action is irreversible,\n Are you sure ?", "YES", "NO", new OnClickListener() {
			@Override
			public void onClick(View v) {
				OrderDetailSQL.setOrderDetailToVoidOrFreeForClosedOrder(orderDetail, oldTotal);
				order = OrderSQL.getOrder(order.getId());
				orderDetails = OrderDetailSQL.getOrderDetails(order.getId());
				tv_change_num.setText(App.instance.getLocalRestaurantConfig().getCurrencySymbol() + BH.sub(BH.getBD(oldTotal), BH.getBD(order.getTotal()), true).toString());
				initBillSummary();
			}
		}, null);
	}

	@Override
	public void onClick(View v) {
		if (ButtonClickTimer.canClick(v)) {
			// 非支付方式的点击事件
			switch (v.getId()) {
			case R.id.btn_void_all_closed: {
				PaymentSettlementSQL.deleteAllSettlement(payment);
				RoundAmount roundAmount = RoundAmountSQL.getRoundAmount(order);
				if(roundAmount != null && BH.getBD(roundAmount.getRoundBalancePrice()).compareTo(BH.getBD("0.00")) != 0) {
					order.setTotal(BH.sub(BH.getBD(order.getTotal()), BH.getBD(roundAmount.getRoundBalancePrice()), true).toString());
					OrderSQL.update(order);
					RoundAmountSQL.deleteRoundAmount(roundAmount);
				}
				refundTax();
				PaymentSQL.addPayment(payment);
				PaymentSettlement paymentSettlement = ObjectFactory.getInstance()
						.getPaymentSettlement(payment, ParamConst.SETTLEMENT_TYPE_REFUND,
								order.getTotal());
				PaymentSettlementSQL.addPaymentSettlement(paymentSettlement);
				VoidSettlement mVoidSettlement = new VoidSettlement();
				mVoidSettlement.setReason(et_special_settlement_remarks_text
						.getText().toString());
				mVoidSettlement.setAuthorizedUserId(user.getId());
				mVoidSettlement.setAmount(order.getTotal());
				mVoidSettlement.setType(1);
				VoidSettlement voidSettlement = ObjectFactory.getInstance()
						.getVoidSettlementByPayment(payment, paymentSettlement,
								mVoidSettlement);
				VoidSettlementSQL.addVoidSettlement(voidSettlement);
				payment_amount = remainTotal;
				paymentType = viewTag;
				order.setOrderStatus(ParamConst.ORDER_STATUS_FINISHED);
				OrderSQL.update(order);
				initBillSummary();
				printBill(null);

			}
				break;
			case R.id.btn_print_receipt: {
//				v.setVisibility(View.GONE);
				// closeWindowAction();
				printBill(null);

			}

				break;
			case R.id.btn_close_bill: {
				// if (!(parent instanceof EditSettlementHtml)) {
//				if(ll_subtotal_layout.getVisibility() == View.VISIBLE){
//					closeMoneyKeyboard();
//				}
				closeWindowAction();
				if (parent instanceof EditSettlementPage && oldPaymentMapList != null) {
					Map<String, List<Map<String, Object>>> newAndOldPaymentSettlement = new HashMap<String, List<Map<String,Object>>>();
					newAndOldPaymentSettlement.put("oldPaymentMapList", oldPaymentMapList);
					newAndOldPaymentSettlement.put("newPaymentMapList", newPaymentMapList);
					handler.sendMessage(handler.obtainMessage(
							EditSettlementPage.EDIT_SETTLEMENT_CLOSE_BILL,
							newAndOldPaymentSettlement));
				}
				// }
				break;
			}

			default:
				break;
			}

			// 支付方式能否点击的控制
			if (remainTotal.compareTo(BH.getBD(0)) == 0) {
				return;
			}
			// 支付方式的点击事件
			switch (v.getId()) {
			case R.id.tv_Others: {
				openMoneyKeyboard(View.VISIBLE, ParamConst.SETTLEMENT_TYPE_CASH);
				isFirstClickCash = true;
				break;
			}
			case R.id.tv_exact:{
				openMoneyKeyboard(View.VISIBLE, ParamConst.SETTLEMENT_TYPE_CASH);
				isFirstClickCash = true;
				clickEnterAction();
			}
			case R.id.iv_VISA: {
				openMoneyKeyboard(View.GONE, ParamConst.SETTLEMENT_TYPE_VISA);
				break;
			}

			case R.id.tv_cash_200:
				openMoneyKeyboard(View.VISIBLE, ParamConst.SETTLEMENT_TYPE_CASH);
				selectNumberAction(200);
				isFirstClickCash = true;
				break;
			case R.id.tv_cash_150:
				openMoneyKeyboard(View.VISIBLE, ParamConst.SETTLEMENT_TYPE_CASH);
				selectNumberAction(150);
				isFirstClickCash = true;
				break;
			case R.id.tv_cash_100:
				openMoneyKeyboard(View.VISIBLE, ParamConst.SETTLEMENT_TYPE_CASH);
				selectNumberAction(100);
				isFirstClickCash = true;
				break;
			case R.id.tv_cash_50:
				openMoneyKeyboard(View.VISIBLE, ParamConst.SETTLEMENT_TYPE_CASH);
				selectNumberAction(50);
				isFirstClickCash = true;
				break;
			case R.id.tv_cash_20:
				openMoneyKeyboard(View.VISIBLE, ParamConst.SETTLEMENT_TYPE_CASH);
				selectNumberAction(20);
				isFirstClickCash = true;
				break;
			case R.id.tv_cash_10:
				openMoneyKeyboard(View.VISIBLE, ParamConst.SETTLEMENT_TYPE_CASH);
				selectNumberAction(10);
				isFirstClickCash = true;
				break;
			case R.id.tv_cash_5:
				openMoneyKeyboard(View.VISIBLE, ParamConst.SETTLEMENT_TYPE_CASH);
				selectNumberAction(5);
				isFirstClickCash = true;
				break;
			case R.id.iv_MasterCard:
				openMoneyKeyboard(View.GONE,
						ParamConst.SETTLEMENT_TYPE_MASTERCARD);
				break;
			case R.id.iv_NETS:
				openMoneyKeyboard(View.GONE, ParamConst.SETTLEMENT_TYPE_NETS);
				break;
			case R.id.iv_UnionPay_CN:
			case R.id.iv_UnionPay:
				openMoneyKeyboard(View.GONE, ParamConst.SETTLEMENT_TYPE_UNIPAY);
				break;
			case R.id.iv_JCB:
				openMoneyKeyboard(View.GONE, ParamConst.SETTLEMENT_TYPE_JCB);
				break;
			case R.id.iv_AMERICAN:
				openMoneyKeyboard(View.GONE, ParamConst.SETTLEMENT_TYPE_AMEX);
				break;
			case R.id.iv_dinersclub:
				openMoneyKeyboard(View.GONE, ParamConst.SETTLEMENT_TYPE_DINNER_INTERMATIONAL);
				break;
			case R.id.tv_BILL_on_HOLD:
				if (remainTotal.compareTo(BH.getBD(order.getTotal())) != 0) {
					return;
				}
				handler.sendMessage(handler
						.obtainMessage(MainPage.VIEW_EVENT_SHOW_BILL_ON_HOLD));
				break;
			case R.id.tv_stored_card: {

//				String value = ((TextView)findViewById(R.id.tv_residue_total_num)).getText().toString().substring(1);
				if(BH.compare(remainTotal,BH.getBD(ParamConst.DOUBLE_ZERO))){
					handler.sendMessage(handler.obtainMessage(StoredCardActivity.VIEW_EVENT_STORED_CARD_PAY, remainTotal.toString()));
					viewTag = ParamConst.SETTLEMENT_TYPE_STORED_CARD;
					paymentTypeId = ParamConst.SETTLEMENT_TYPE_STORED_CARD;
				}

			}
			break;
			case R.id.tv_deliveroo:
				if (remainTotal.compareTo(BH.getBD(order.getTotal())) != 0) {
					return;
				}
				viewTag = ParamConst.SETTLEMENT_TYPE_DELIVEROO;
				paymentTypeId = ParamConst.SETTLEMENT_TYPE_DELIVEROO;
				clickEnterAction();
				break;
			case R.id.tv_ubereats:
				if (remainTotal.compareTo(BH.getBD(order.getTotal())) != 0) {
					return;
				}
				viewTag = ParamConst.SETTLEMENT_TYPE_UBEREATS;
				paymentTypeId = ParamConst.SETTLEMENT_TYPE_UBEREATS;
				clickEnterAction();
				break;
			case R.id.tv_foodpanda:
				if (remainTotal.compareTo(BH.getBD(order.getTotal())) != 0) {
					return;
				}
				viewTag = ParamConst.SETTLEMENT_TYPE_FOODPANDA;
				paymentTypeId = ParamConst.SETTLEMENT_TYPE_FOODPANDA;
				clickEnterAction();
				break;
			case R.id.tv_voucher_event:


				break;
//			case R.id.iv_alipay:
//				Map<Integer, AlipayPushMsgDto> alipayPush =  App.instance.getAlipayPushMessage();
//				if (alipayPush.containsKey(orderBill.getBillNo())) {
//					AlipayPushMsgDto alipayPushMsgDto = alipayPush.get(orderBill.getBillNo());
//					alipayClickEnterAction(alipayPushMsgDto.getTradeNo(), alipayPushMsgDto.getBuyerEmail(), BH.getBD(alipayPushMsgDto.getTransAmount()));
//				} else {
//					alipayAction(ParamConst.SETTLEMENT_TYPE_ALIPAY);
//				}
//				break;
			case R.id.iv_alipay:
				openMoneyKeyboard(View.GONE, ParamConst.SETTLEMENT_TYPE_ALIPAY);
				break;
			case R.id.iv_wechatpay:
				openMoneyKeyboard(View.GONE, ParamConst.SETTLEMENT_TYPE_WEIXIN);
				break;
			case R.id.tv_VOID:
				if (remainTotal.compareTo(BH.getBD(order.getTotal())) != 0) {
					return;
				}
				handler.sendMessage(handler
						.obtainMessage(MainPage.VIEW_EVENT_SHOW_VOID));
				break;
			case R.id.tv_ENTERTAINMENT:
				if (remainTotal.compareTo(BH.getBD(order.getTotal())) != 0) {
					return;
				}
				handler.sendMessage(handler
						.obtainMessage(MainPage.VIEW_EVENT_SHOW_ENTERTAINMENT));
				break;
			case R.id.rl_cards_amount_paid_num:
				tv_cards_amount_paid_num.setBackgroundColor(parent.getResources().getColor(
						R.color.default_line_indicator_selected_color));
				selectView = tv_cards_amount_paid_num;
				show.delete(0, show.length());
//				show.append(tv_cards_amount_paid_num.getText().toString());
				tv_card_no_num.setBackgroundColor(parent.getResources()
						.getColor(R.color.white));
				tv_cards_cvv_num.setBackgroundColor(parent.getResources()
						.getColor(R.color.white));
				tv_cards_expiration_date_num.setBackgroundColor(parent
						.getResources().getColor(R.color.white));
				break;
			case R.id.rl_card_no:
				tv_card_no_num
						.setBackgroundColor(parent.getResources().getColor(
								R.color.default_line_indicator_selected_color));
				selectView = tv_card_no_num;
				show.delete(0, show.length());
				tv_cards_amount_paid_num.setBackgroundColor(parent.getResources()
						.getColor(R.color.white));
				tv_cards_cvv_num.setBackgroundColor(parent.getResources()
						.getColor(R.color.white));
				tv_cards_expiration_date_num.setBackgroundColor(parent
						.getResources().getColor(R.color.white));
				break;
			case R.id.ll_cards_cvv_num:
				tv_cards_cvv_num
						.setBackgroundColor(parent.getResources().getColor(
								R.color.default_line_indicator_selected_color));
				selectView = tv_cards_cvv_num;
				show.delete(0, show.length());
				tv_cards_amount_paid_num.setBackgroundColor(parent.getResources()
						.getColor(R.color.white));
				tv_card_no_num.setBackgroundColor(parent.getResources()
						.getColor(R.color.white));
				tv_cards_expiration_date_num.setBackgroundColor(parent
						.getResources().getColor(R.color.white));
				break;
			case R.id.ll_cards_expiration_date_num:
				tv_cards_expiration_date_num.setBackgroundColor(parent
						.getResources().getColor(
								R.color.default_line_indicator_selected_color));
				selectView = tv_cards_expiration_date_num;
				show.delete(0, show.length());
				tv_cards_amount_paid_num.setBackgroundColor(parent.getResources()
						.getColor(R.color.white));
				tv_card_no_num.setBackgroundColor(parent.getResources()
						.getColor(R.color.white));
				tv_cards_cvv_num.setBackgroundColor(parent.getResources()
						.getColor(R.color.white));
				break;
			case R.id.rl_nets_amount_paid_num:
				tv_nets_amount_paid_num.setBackgroundColor(parent
						.getResources().getColor(
								R.color.default_line_indicator_selected_color));
				tv_nets_ref_num.setBackgroundColor(parent.getResources()
						.getColor(R.color.white));
				selectView = tv_nets_amount_paid_num;
				show.delete(0, show.length());
				break;
			case R.id.rl_nets_ref_num:
				tv_nets_amount_paid_num.setBackgroundColor(parent
						.getResources().getColor(
								R.color.white));
				tv_nets_ref_num.setBackgroundColor(parent.getResources()
						.getColor(R.color.default_line_indicator_selected_color));
				selectView = tv_nets_ref_num;
				show.delete(0, show.length());
				break;
			default:
				break;
			}
		}

	}

//	public void alipayAction(int payTypeId){
//		viewTag = payTypeId;
//		paymentTypeId = payTypeId;
//		web_alipay.setVisibility(View.VISIBLE);
//		web_alipay.performClick();
//		WebViewConfig.setDefaultConfig(web_alipay);
//		Map<String, Object> parameters = new HashMap<String, Object>();
//		parameters.put("orderId", order.getId());
//		parameters.put("billNo", orderBill.getBillNo());
//		parameters.put("orderCreateTime", orderBill.getCreateTime());
//		parameters.put("amount", BH.doubleFormat.format(remainTotal));
//		parameters.put("appOrderId", order.getAppOrderId());
//		String url = SyncCentre.getInstance().requestAlipayUrl(parameters);
//		web_alipay.loadUrl(url);
//		web_alipay.setWebViewClient(new WebViewClient(){
//			@Override
//			public boolean shouldOverrideUrlLoading(WebView view,
//					String url) {
////				web_alipay.loadUrl(url);
//				if(url.startsWith(SyncCentre.getInstance().getAlipayVerifyErrorNotifyUrl())){
//					DialogFactory.showOneButtonCompelDialog(parent, "注意", "支付失败", null);
//					web_alipay.setVisibility(View.GONE);
//				}else if(url.startsWith(SyncCentre.getInstance().getAlipayVerifyReturnUrl())){
//					final Map<String, String> localBundle = NetUtil.parseUrl(url);
//					String trade_status = localBundle.get("trade_status");
//					final String trade_no = localBundle.get("trade_no");
//					final String extra_common_param = localBundle.get("extra_common_param");
//					String[] extraCommonArray = extra_common_param.split("_");
//					final String buyer_email = localBundle.get("buyer_email");
//					if(!TextUtils.isEmpty(trade_status) && (trade_status.equals("TRADE_FINISHED") || trade_status.equals("TRADE_SUCCESS")) && extraCommonArray[3].equals(orderBill.getBillNo().toString())){
//						alipayClickEnterAction(trade_no, buyer_email, remainTotal);
//						DialogFactory.showOneButtonCompelDialog(parent, "注意", "支付成功", null);
//
//					}else{
//						DialogFactory.showOneButtonCompelDialog(parent, "注意", "支付失败", null);
//					}
////					AlipayNotify.verify(localBundle, new AlipayNetWorkCallBack() {
////
////						@Override
////						public void onSuccess() {
////							String trade_status = localBundle.get("trade_status");
////							final String trade_no = localBundle.get("trade_no");
////							final String buyer_email = localBundle.get("buyer_email");
////							if(!TextUtils.isEmpty(trade_status) && (trade_status.equals("TRADE_FINISHED") || trade_status.equals("TRADE_SUCCESS"))){
////								parent.runOnUiThread(new Runnable() {
////
////									@Override
////									public void run() {
////										alipayClickEnterAction(trade_no, buyer_email);
////										DialogFactory.showOneButtonCompelDialog(parent, "注意", "支付成功", null);
////									}
////								});
////
////							}else{
////								parent.runOnUiThread(new Runnable() {
////
////									@Override
////									public void run() {
////										DialogFactory.showOneButtonCompelDialog(parent, "注意", "支付失败", null);
////									}
////								});
////
////							}
////						}
////
////						@Override
////						public void onFailure() {
////							DialogFactory.showOneButtonCompelDialog(parent, "注意", "支付失败", null);
////						}
////					});
//
//					web_alipay.setVisibility(View.GONE);
//				}
//				return super.shouldOverrideUrlLoading(view, url);
//			}
//
//			@Override
//			public void onPageFinished(WebView view, String url) {
//				super.onPageFinished(view, url);
//				web_alipay.requestFocus();
//			}
//		});
//	}

	public void openMoneyKeyboardByVoidRefund(){
		if(parent instanceof EditSettlementPage){
			openMoneyKeyboard(View.GONE,
					ParamConst.SETTLEMENT_TYPE_REFUND);
		}else{
			openMoneyKeyboard(View.GONE,
					ParamConst.SETTLEMENT_TYPE_VOID);
		}
	}

	/**
	 *
	 * @param visibility
	 * @param payTypeId
	 *            为支付方式的ID 现在用临时 0， 1 代替 等拉取到数据改成静态参数
	 */
	public void openMoneyKeyboard(int visibility, int payTypeId) {
		if (AnimatorListenerImpl.isRunning) {
			return;
		}
		if (show.length() > 0) {
			show.delete(0, show.length());
		}
		viewTag = payTypeId;
		paymentTypeId = payTypeId;
		switch (payTypeId) {
		case ParamConst.SETTLEMENT_TYPE_CASH:
			initCashSettlement(order);
			contentView.findViewById(R.id.ll_cash_settlement).setVisibility(
					View.VISIBLE);
			break;
		case ParamConst.SETTLEMENT_TYPE_AMEX:
			initCardsSettlement("AMEX");
			contentView.findViewById(R.id.ll_cards_settlement).setVisibility(
					View.VISIBLE);
			// show.append(0);
			break;
		case ParamConst.SETTLEMENT_TYPE_DINNER_INTERMATIONAL:
			initCardsSettlement("Dinners");
			contentView.findViewById(R.id.ll_cards_settlement).setVisibility(
					View.VISIBLE);
			// show.append(0);
			break;
		case ParamConst.SETTLEMENT_TYPE_JCB:
			initCardsSettlement("JCB");
			contentView.findViewById(R.id.ll_cards_settlement).setVisibility(
					View.VISIBLE);
			// show.append(0);
			break;
		case ParamConst.SETTLEMENT_TYPE_MASTERCARD:
			initCardsSettlement("MASTERCARD");
			contentView.findViewById(R.id.ll_cards_settlement).setVisibility(
					View.VISIBLE);
			// show.append(0);
			break;
		case ParamConst.SETTLEMENT_TYPE_UNIPAY:
			initCardsSettlement("UNIONPAY");
			contentView.findViewById(R.id.ll_cards_settlement).setVisibility(
					View.VISIBLE);
			// show.append(0);
			break;
		case ParamConst.SETTLEMENT_TYPE_VISA:
			initCardsSettlement("Visa");
			contentView.findViewById(R.id.ll_cards_settlement).setVisibility(
					View.VISIBLE);
			// show.append(0);
			break;
		case ParamConst.SETTLEMENT_TYPE_ENTERTAINMENT:
			initSpecialSettlement(user,
					ParamConst.SETTLEMENT_TYPE_ENTERTAINMENT);
			contentView.findViewById(R.id.ll_special_settlement).setVisibility(
					View.VISIBLE);
			show.append(0);
			break;
		case ParamConst.SETTLEMENT_TYPE_BILL_ON_HOLD:
			initSpecialSettlement(user, ParamConst.SETTLEMENT_TYPE_BILL_ON_HOLD);
			contentView.findViewById(R.id.ll_special_settlement).setVisibility(
					View.VISIBLE);
			show.append(0);
			break;
		case ParamConst.SETTLEMENT_TYPE_VOID:
			initSpecialSettlement(user, ParamConst.SETTLEMENT_TYPE_VOID);
			contentView.findViewById(R.id.ll_special_settlement).setVisibility(
					View.VISIBLE);
			show.append(0);
			break;
		case ParamConst.SETTLEMENT_TYPE_REFUND:
			initSpecialSettlement(user, ParamConst.SETTLEMENT_TYPE_REFUND);
			contentView.findViewById(R.id.ll_special_settlement).setVisibility(
					View.VISIBLE);
			show.append(0);
			break;
		case ParamConst.SETTLEMENT_TYPE_NETS:
			initNetsSettlement();
			contentView.findViewById(R.id.ll_nets_settlement).setVisibility(
					View.VISIBLE);
			// show.append(0);
			break;
		case ParamConst.SETTLEMENT_TYPE_ALIPAY:
			initWeChatAlipaySettlement(payTypeId);
			contentView.findViewById(R.id.ll_wechat_ali_settlement).setVisibility(
					View.VISIBLE);
			// show.append(0);
			break;
		case ParamConst.SETTLEMENT_TYPE_WEIXIN:
			initWeChatAlipaySettlement(payTypeId);
			contentView.findViewById(R.id.ll_wechat_ali_settlement).setVisibility(
					View.VISIBLE);
			// show.append(0);
			break;
		default:
			break;
		}

		moneyKeyboard.setVisibility(View.VISIBLE);
		moneyKeyboard.setMoneyPanel(visibility);
		Bitmap bitmap = BitmapUtil.convertViewToBitmap(ll_pay);
		iv_top.setImageBitmap(Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight() / 2));
		iv_bottom.setImageBitmap(Bitmap.createBitmap(bitmap,
				0, bitmap.getHeight() / 2, bitmap.getWidth(),
				bitmap.getHeight() / 2));
		ll_pay.setVisibility(View.GONE);
		ObjectAnimator animator1 = ObjectAnimator.ofFloat(iv_top, "y",
				iv_top.getY(), iv_top.getY() - iv_top.getHeight())
				.setDuration(OPEN_DELAY);
		ObjectAnimator animator2 = ObjectAnimator.ofFloat(iv_bottom, "y",
				iv_bottom.getY(), iv_bottom.getY() + iv_bottom.getHeight())
				.setDuration(OPEN_DELAY);
		AnimatorSet animSet = new AnimatorSet();
		animSet.playTogether(animator1, animator2);
		animSet.addListener(new AnimatorListenerImpl());
		animSet.start();
	}

	private void closeMoneyKeyboard() {
		if (AnimatorListenerImpl.isRunning) {
			return;
		}
		if (show.length() > 0) {
			show.delete(0, show.length());
		}
		ll_subtotal_layout.setVisibility(
				View.VISIBLE);
		switch (viewTag) {
		case ParamConst.SETTLEMENT_TYPE_CASH:
			contentView.findViewById(R.id.ll_cash_settlement).setVisibility(
					View.INVISIBLE);
			break;
		case ParamConst.SETTLEMENT_TYPE_JCB:
		case ParamConst.SETTLEMENT_TYPE_AMEX:
		case ParamConst.SETTLEMENT_TYPE_DINNER_INTERMATIONAL:
		case ParamConst.SETTLEMENT_TYPE_MASTERCARD:
		case ParamConst.SETTLEMENT_TYPE_UNIPAY:
		case ParamConst.SETTLEMENT_TYPE_VISA:
			contentView.findViewById(R.id.ll_cards_settlement).setVisibility(
					View.INVISIBLE);
			break;
		case ParamConst.SETTLEMENT_TYPE_ENTERTAINMENT:
			contentView.findViewById(R.id.ll_special_settlement).setVisibility(
					View.INVISIBLE);
			break;
		case ParamConst.SETTLEMENT_TYPE_BILL_ON_HOLD:
			contentView.findViewById(R.id.ll_special_settlement).setVisibility(
					View.INVISIBLE);
			break;
		case ParamConst.SETTLEMENT_TYPE_VOID:
			contentView.findViewById(R.id.ll_special_settlement).setVisibility(
					View.INVISIBLE);
			break;
		case ParamConst.SETTLEMENT_TYPE_REFUND:
			contentView.findViewById(R.id.ll_special_settlement).setVisibility(
					View.INVISIBLE);
			break;
		case ParamConst.SETTLEMENT_TYPE_NETS:
			contentView.findViewById(R.id.ll_nets_settlement).setVisibility(
					View.INVISIBLE);
			break;
		case ParamConst.SETTLEMENT_TYPE_ALIPAY:
			contentView.findViewById(R.id.ll_wechat_ali_settlement).setVisibility(
					View.INVISIBLE);
			break;
		case ParamConst.SETTLEMENT_TYPE_WEIXIN:
			contentView.findViewById(R.id.ll_wechat_ali_settlement).setVisibility(
					View.INVISIBLE);
			break;
		default:
			break;
		}
		ObjectAnimator animator1 = ObjectAnimator.ofFloat(iv_top, "y",
				iv_top.getY(), iv_top.getY() + iv_top.getHeight())
				.setDuration(OPEN_DELAY);
		ObjectAnimator animator2 = ObjectAnimator.ofFloat(iv_bottom, "y",
				iv_bottom.getY(), iv_bottom.getY() - iv_bottom.getHeight())
				.setDuration(OPEN_DELAY);
		AnimatorSet animSet = new AnimatorSet();
		animSet.playTogether(animator1, animator2);
		animSet.addListener(new AnimatorListenerImpl() {
			@Override
			public void onAnimationEnd(Animator animation) {
				super.onAnimationEnd(animation);
				ll_pay.post(new Runnable() {
					@Override
					public void run() {
						ll_pay.setVisibility(View.VISIBLE);
						moneyKeyboard.setVisibility(View.GONE);
					}
				});
			}
		});
		animSet.start();
	}

	StringBuffer show = new StringBuffer();

	@Override
	public void onKeyBoardClick(String key) {
		LogUtil.d(TAG, "show" + show.toString());
		if ("X".equals(key)) {
			closeMoneyKeyboard();
		} else if ("Enter".equals(key)) {
			if(ButtonClickTimer.canClick()) {
				clickEnterAction();
			}
		} else if ("C".equals(key)) {
			clickClearAction();

		} else if ("200".equals(key)) {
			selectNumberAction(200);
		} else if ("100".equals(key)) {
			selectNumberAction(100);
		} else if ("50".equals(key)) {
			selectNumberAction(50);
		} else if ("10".equals(key)) {
			selectNumberAction(10);
		} else {
			clickOtherAction(key);
		}
	}

	private void selectNumberAction(int num) {
		if (show.length() > 0) {
			show.delete(0, show.length());
		}
		show.append(num * 100);
		tv_total_amount_num
				.setText(App.instance.getLocalRestaurantConfig().getCurrencySymbol() + BH.getBD(num).toString());
		BigDecimal cashNum = BH.getBD(String.valueOf(Double.parseDouble(show
				.toString()) / 100.0));
		BigDecimal remainTotalAfterRound = RoundUtil.getPriceAfterRound(App.instance.getLocalRestaurantConfig().getRoundType(), remainTotal);
		int change = cashNum.compareTo(remainTotalAfterRound);
		if (change >= 0) {
			BigDecimal changeNum = BH.sub(cashNum, remainTotalAfterRound, true);
			tv_change_action_num.setText(App.instance.getLocalRestaurantConfig().getCurrencySymbol()
					+ BH.getBD(changeNum).toString());
			clickEnterAction();
		} else {
			tv_change_action_num.setText(App.instance.getLocalRestaurantConfig().getCurrencySymbol() + BH.getBD(0).toString());
		}
	}



	private void showCashChange() {
		BigDecimal cashNum = BH.getBD(String.valueOf(Double.parseDouble(show
				.toString()) / 100.0));
		BigDecimal remainTotalAfterRound = RoundUtil.getPriceAfterRound(App.instance.getLocalRestaurantConfig().getRoundType(), remainTotal);
		int change = cashNum.compareTo(remainTotalAfterRound);
		if (change > 0) {
			BigDecimal changeNum = BH.sub(cashNum, remainTotalAfterRound, true);
			tv_change_action_num.setText(App.instance.getLocalRestaurantConfig().getCurrencySymbol()
					+ BH.getBD(changeNum).toString());
		} else {
			tv_change_action_num.setText(App.instance.getLocalRestaurantConfig().getCurrencySymbol() + BH.getBD(0).toString());
		}
	}

	public void clickEnterAction() {
//		if (show.length() <= 0) {
//			return;
//		}
		switch (viewTag) {
		case ParamConst.SETTLEMENT_TYPE_CASH: {
			String showStr = BH.mul(BH.getBD(show.toString()), BH.getBD("0.01"), true).toString();
			BigDecimal showStrBigDecimal = RoundUtil.getPriceAfterRound(App.instance.getLocalRestaurantConfig().getRoundType(), BH.getBD(showStr));
			BigDecimal remainTotalAfterRound = RoundUtil.getPriceAfterRound(App.instance.getLocalRestaurantConfig().getRoundType(), remainTotal);
			if (showStrBigDecimal.compareTo(remainTotalAfterRound) > 0) {
				showStr = remainTotalAfterRound.toString();
			}else{
				showStr = showStrBigDecimal.toString();
			}
			PaymentSQL.addPayment(payment);
			PaymentSettlement paymentSettlement = ObjectFactory.getInstance()
					.getPaymentSettlement(payment, paymentTypeId, showStr);
			((TextView) contentView.findViewById(R.id.tv_change_num))
					.setText(tv_change_action_num.getText());
			if (showStrBigDecimal.compareTo(remainTotalAfterRound) > -1) {
				RoundAmount roundAmount = ObjectFactory.getInstance().getRoundAmount(order, orderBill, remainTotal, App.instance.getLocalRestaurantConfig().getRoundType());
				order.setOrderStatus(ParamConst.ORDER_STATUS_FINISHED);
				OrderHelper.setOrderTotalAlfterRound(order, roundAmount);
				OrderSQL.update(order);
				PaymentSQL.updatePaymentAmount(order.getTotal(), order.getId().intValue());
				paymentSettlement.setCashChange(BH.sub(showStrBigDecimal,
						remainTotalAfterRound, true).toString());
			} else {
				settlementNum = BH.getBD(PaymentSettlementSQL
						.getPaymentSettlementsSumBypaymentId(payment.getId()));
				remainTotal = BH.sub(remainTotalAfterRound, settlementNum, false);
			}
			PaymentSettlementSQL.addPaymentSettlement(paymentSettlement);
			if (newPaymentMapList != null) {
				Map<String, Object> paymentMap = new HashMap<String, Object>();
				paymentMap.put("newPaymentSettlement", paymentSettlement);
				newPaymentMapList.add(paymentMap);
			}
		}
			break;
		case ParamConst.SETTLEMENT_TYPE_AMEX:
		case ParamConst.SETTLEMENT_TYPE_DINNER_INTERMATIONAL:
		case ParamConst.SETTLEMENT_TYPE_JCB:
		case ParamConst.SETTLEMENT_TYPE_MASTERCARD:
		case ParamConst.SETTLEMENT_TYPE_UNIPAY:
		case ParamConst.SETTLEMENT_TYPE_VISA: {
//			if (!verifyCardNo()) {
//				return;
//			} else {
				BigDecimal paidBD = BH.getBD(tv_cards_amount_paid_num.getText().toString());
				if(BH.compare(paidBD, BH.getBD(ParamConst.DOUBLE_ZERO))){
					PaymentSettlement paymentSettlement = ObjectFactory
							.getInstance().getPaymentSettlementForCard(
									payment,
									paymentTypeId,
									paidBD.toString());
				if (paidBD.compareTo(remainTotal) > -1) {
					order.setOrderStatus(ParamConst.ORDER_STATUS_FINISHED);
					OrderSQL.update(order);
				} else {
					settlementNum = BH.getBD(PaymentSettlementSQL
							.getPaymentSettlementsSumBypaymentId(payment.getId()));
					remainTotal = BH.sub(remainTotal, settlementNum, false);
				}
				PaymentSQL.addPayment(payment);
				PaymentSettlementSQL.addPaymentSettlement(paymentSettlement);
				String cvv = tv_cards_cvv_num.getText().toString();
				String expirationDate = tv_cards_expiration_date_num.getText()
						.toString();
				if (TextUtils.isEmpty(cvv)) {
					cvv = tv_cards_cvv_num.getHint().toString();
				}
				if (TextUtils.isEmpty(expirationDate)) {
					expirationDate = tv_cards_expiration_date_num.getHint()
							.toString();
				}
				CardsSettlement cardsSettlement = ObjectFactory.getInstance()
						.getCardsSettlement(payment, paymentSettlement,
								paymentTypeId,
								tv_card_no_num.getText().toString(), cvv,
								expirationDate);
				CardsSettlementSQL.addCardsSettlement(cardsSettlement);
				payment_amount = remainTotal;
				paymentType = viewTag;
				if (newPaymentMapList != null) {
					Map<String, Object> paymentMap = new HashMap<String, Object>();
					paymentMap.put("newPaymentSettlement",
							paymentSettlement);
					paymentMap.put("newSubPaymentSettlement",
							cardsSettlement);
					newPaymentMapList.add(paymentMap);
				}
			}
//			}

		}
			break;
		case ParamConst.SETTLEMENT_TYPE_STORED_CARD: {
//			String amount = ((TextView) findViewById(R.id.tv_residue_total))
//					.getText().toString();
			PaymentSQL.addPayment(payment);
			PaymentSettlement paymentSettlement = ObjectFactory.getInstance()
					.getPaymentSettlement(payment, paymentTypeId,
							remainTotal.toString());
			PaymentSettlementSQL.addPaymentSettlement(paymentSettlement);
			remainTotal = BH.getBD(ParamConst.DOUBLE_ZERO);
			payment_amount = remainTotal;
			paymentType = viewTag;
			if (newPaymentMapList != null) {
				Map<String, Object> paymentMap = new HashMap<String, Object>();
				paymentMap.put("newPaymentSettlement",
						paymentSettlement);
				newPaymentMapList.add(paymentMap);
			}
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					if (isShowing())
						printBill(null);
				}
			}, 100);
		}
			break;
		case ParamConst.SETTLEMENT_TYPE_BILL_ON_HOLD: {
			EditText et_special_settlement_person_name = (EditText) contentView
					.findViewById(R.id.et_special_settlement_person_name);
			EditText et_special_settlement_phone_text = (EditText) contentView
					.findViewById(R.id.et_special_settlement_phone_text);
			PaymentSQL.addPayment(payment);
			PaymentSettlement paymentSettlement = ObjectFactory.getInstance()
					.getPaymentSettlement(payment, paymentTypeId,
							order.getTotal());
			PaymentSettlementSQL.addPaymentSettlement(paymentSettlement);
			BohHoldSettlement mBohHoldSettlement = new BohHoldSettlement();
			mBohHoldSettlement.setNameOfPerson(et_special_settlement_person_name
					.getText().toString());
			mBohHoldSettlement.setPhone(et_special_settlement_phone_text
					.getText().toString());
			mBohHoldSettlement.setRemarks(et_special_settlement_remarks_text
					.getText().toString());
			if (user != null) {
				mBohHoldSettlement.setAuthorizedUserId(user.getId());
			}
			mBohHoldSettlement.setAmount(order.getTotal());
			BohHoldSettlement bohHoldSettlement = ObjectFactory.getInstance()
					.getBohHoldSettlementByPaymentSettlement(paymentSettlement,
							order.getId(), mBohHoldSettlement);
			BohHoldSettlementSQL.addBohHoldSettlement(bohHoldSettlement);
			payment_amount = remainTotal;
			paymentType = viewTag;
			order.setOrderStatus(ParamConst.ORDER_STATUS_FINISHED);
			OrderSQL.update(order);
			if (newPaymentMapList != null) {
				Map<String, Object> paymentMap = new HashMap<String, Object>();
				paymentMap.put("newPaymentSettlement", paymentSettlement);
				paymentMap.put("newSubPaymentSettlement",
						bohHoldSettlement);
				newPaymentMapList.add(paymentMap);

			}
			et_special_settlement_person_name.setText("");
			et_special_settlement_phone_text.setText("");
			et_special_settlement_remarks_text.setText("");
		}
			break;
		case ParamConst.SETTLEMENT_TYPE_ENTERTAINMENT: {
			deleteVoidOrEntTax();
			PaymentSQL.addPayment(payment);
			PaymentSettlement paymentSettlement = ObjectFactory.getInstance()
					.getPaymentSettlement(payment, paymentTypeId,
							order.getTotal());
			PaymentSettlementSQL.addPaymentSettlement(paymentSettlement);
			NonChargableSettlement mNonChargableSettlement = new NonChargableSettlement();
			mNonChargableSettlement.setNameOfPerson(((EditText) contentView
					.findViewById(R.id.et_special_settlement_person_name))
					.getText().toString());
			mNonChargableSettlement
					.setRemarks(et_special_settlement_remarks_text.getText()
							.toString());
			mNonChargableSettlement.setAuthorizedUserId(user.getId());
			mNonChargableSettlement.setAmount(order.getTotal());
			NonChargableSettlement nonChargableSettlement = ObjectFactory
					.getInstance()
					.getNonChargableSettlementByPaymentSettlement(payment,
							paymentSettlement, mNonChargableSettlement);
			NonChargableSettlementSQL
					.addNonChargableSettlement(nonChargableSettlement);
			payment_amount = remainTotal;
			paymentType = viewTag;
			order.setOrderStatus(ParamConst.ORDER_STATUS_FINISHED);
			OrderSQL.update(order);
			if (newPaymentMapList != null) {
				Map<String, Object> paymentMap = new HashMap<String, Object>();
				paymentMap.put("newPaymentSettlement", paymentSettlement);
				paymentMap.put("newSubPaymentSettlement",
						nonChargableSettlement);
				newPaymentMapList.add(paymentMap);
			}
		}
			break;
		case ParamConst.SETTLEMENT_TYPE_DELIVEROO:
		case ParamConst.SETTLEMENT_TYPE_UBEREATS:
		case ParamConst.SETTLEMENT_TYPE_FOODPANDA: {
			PaymentSQL.addPayment(payment);
			PaymentSettlement paymentSettlement = ObjectFactory.getInstance()
					.getPaymentSettlement(payment, paymentTypeId,
							order.getTotal());
			PaymentSettlementSQL.addPaymentSettlement(paymentSettlement);
			payment_amount = remainTotal;
			paymentType = viewTag;
			order.setOrderStatus(ParamConst.ORDER_STATUS_FINISHED);
			OrderSQL.update(order);
			if (newPaymentMapList != null) {
				Map<String, Object> paymentMap = new HashMap<String, Object>();
				paymentMap.put("newPaymentSettlement", paymentSettlement);
				paymentMap.put("newSubPaymentSettlement", null);
				newPaymentMapList.add(paymentMap);
			}
		}
			break;
		case ParamConst.SETTLEMENT_TYPE_VOID: {
			deleteVoidOrEntTax();
			PaymentSQL.addPayment(payment);
			PaymentSettlement paymentSettlement = ObjectFactory.getInstance()
					.getPaymentSettlement(payment, paymentTypeId,
							order.getTotal());
			PaymentSettlementSQL.addPaymentSettlement(paymentSettlement);
			VoidSettlement mVoidSettlement = new VoidSettlement();
			mVoidSettlement.setReason(et_special_settlement_remarks_text
					.getText().toString());
			mVoidSettlement.setAuthorizedUserId(user.getId());
			mVoidSettlement.setAmount(order.getTotal());
			mVoidSettlement.setType(0);
			VoidSettlement voidSettlement = ObjectFactory.getInstance()
					.getVoidSettlementByPayment(payment, paymentSettlement,
							mVoidSettlement);
			VoidSettlementSQL.addVoidSettlement(voidSettlement);
			payment_amount = remainTotal;
			paymentType = viewTag;
			order.setOrderStatus(ParamConst.ORDER_STATUS_FINISHED);
			OrderSQL.update(order);
			if (newPaymentMapList != null) {
				Map<String, Object> paymentMap = new HashMap<String, Object>();
				paymentMap.put("newPaymentSettlement", paymentSettlement);
				paymentMap.put("newSubPaymentSettlement", voidSettlement);
				newPaymentMapList.add(paymentMap);
			}
		}
			break;
			case ParamConst.SETTLEMENT_TYPE_REFUND: {
				refundTax();
				PaymentSQL.addPayment(payment);
				PaymentSettlement paymentSettlement = ObjectFactory.getInstance()
						.getPaymentSettlement(payment, paymentTypeId,
								order.getTotal());
				PaymentSettlementSQL.addPaymentSettlement(paymentSettlement);
				VoidSettlement mVoidSettlement = new VoidSettlement();
				mVoidSettlement.setReason(et_special_settlement_remarks_text
						.getText().toString());
				mVoidSettlement.setAuthorizedUserId(user.getId());
				mVoidSettlement.setAmount(order.getTotal());
				mVoidSettlement.setType(1);
				VoidSettlement voidSettlement = ObjectFactory.getInstance()
						.getVoidSettlementByPayment(payment, paymentSettlement,
								mVoidSettlement);
				VoidSettlementSQL.addVoidSettlement(voidSettlement);
				payment_amount = remainTotal;
				paymentType = viewTag;
				order.setOrderStatus(ParamConst.ORDER_STATUS_FINISHED);
				OrderSQL.update(order);
				if (newPaymentMapList != null) {
					Map<String, Object> paymentMap = new HashMap<String, Object>();
					paymentMap.put("newPaymentSettlement", paymentSettlement);
					paymentMap.put("newSubPaymentSettlement", voidSettlement);
					newPaymentMapList.add(paymentMap);
				}
			}
			break;
		case ParamConst.SETTLEMENT_TYPE_NETS: {
			String cardNo = tv_nets_ref_num.getText().toString();
//			if(TextUtils.isEmpty(cardNo)){
//				UIHelp.showToast(parent, parent.getResources().getString(R.string.ref_id_not_empty));
//				return;
//			}else{
				BigDecimal paidBD = BH.getBD(tv_nets_amount_paid_num.getText().toString());
				if(BH.compare(paidBD, BH.getBD(ParamConst.DOUBLE_ZERO))){
					PaymentSettlement paymentSettlement = ObjectFactory.getInstance()
							.getPaymentSettlementForCard(
									payment,
									paymentTypeId,
									String.valueOf(paidBD.toString()));
					if (paidBD.compareTo(remainTotal) > -1) {
						order.setOrderStatus(ParamConst.ORDER_STATUS_FINISHED);
						OrderSQL.update(order);
					} else {
						settlementNum = BH.getBD(PaymentSettlementSQL
								.getPaymentSettlementsSumBypaymentId(payment.getId()));
						remainTotal = BH.sub(remainTotal, settlementNum, false);
					}
					PaymentSQL.addPayment(payment);
					PaymentSettlementSQL.addPaymentSettlement(paymentSettlement);
                    Integer cardNum = TextUtils.isEmpty(cardNo) ? 0 : Integer.parseInt(cardNo);
					NetsSettlement netsSettlement = ObjectFactory.getInstance()
							.getNetsSettlementByPayment(
									payment,
									paymentSettlement,
                                    cardNum,
											paidBD.toString());
					NetsSettlementSQL.addNetsSettlement(netsSettlement);

					payment_amount = remainTotal;
					paymentType = viewTag;
					if (newPaymentMapList != null) {
						Map<String, Object> paymentMap = new HashMap<String, Object>();
						paymentMap.put("newPaymentSettlement", paymentSettlement);
						paymentMap.put("newSubPaymentSettlement", netsSettlement);
						newPaymentMapList.add(paymentMap);
					}
				}
			}
//		}
			break;
		case ParamConst.SETTLEMENT_TYPE_ALIPAY:{
			BigDecimal paidBD = BH.getBD(tv_wechat_ali_amount_paid_num.getText().toString());
			if(BH.compare(paidBD, BH.getBD(ParamConst.DOUBLE_ZERO))){
				PaymentSettlement paymentSettlement = ObjectFactory.getInstance()
						.getPaymentSettlementForCard(
								payment,
								paymentTypeId,
								String.valueOf(paidBD.toString()));
				if (paidBD.compareTo(remainTotal) > -1) {
					order.setOrderStatus(ParamConst.ORDER_STATUS_FINISHED);
					OrderSQL.update(order);
				} else {
					settlementNum = BH.getBD(PaymentSettlementSQL
							.getPaymentSettlementsSumBypaymentId(payment.getId()));
					remainTotal = BH.sub(remainTotal, settlementNum, false);
				}
				PaymentSQL.addPayment(payment);
				AlipaySettlement alipaySettlement = ObjectFactory.getInstance().getAlipaySettlement(payment, paymentSettlement, "0", "");
				payment_amount = remainTotal;
				paymentType = viewTag;
				if (newPaymentMapList != null) {
					Map<String, Object> paymentMap = new HashMap<String, Object>();
					paymentMap.put("newPaymentSettlement", paymentSettlement);
					paymentMap.put("newSubPaymentSettlement", alipaySettlement);
					newPaymentMapList.add(paymentMap);
				}
			}
		}
			break;

		case ParamConst.SETTLEMENT_TYPE_WEIXIN:{
			BigDecimal paidBD = BH.getBD(tv_wechat_ali_amount_paid_num.getText().toString());
			if(BH.compare(paidBD, BH.getBD(ParamConst.DOUBLE_ZERO))){
				PaymentSettlement paymentSettlement = ObjectFactory.getInstance()
						.getPaymentSettlementForCard(
								payment,
								paymentTypeId,
								String.valueOf(paidBD.toString()));
				if (paidBD.compareTo(remainTotal) > -1) {
					order.setOrderStatus(ParamConst.ORDER_STATUS_FINISHED);
					OrderSQL.update(order);
				} else {
					settlementNum = BH.getBD(PaymentSettlementSQL
							.getPaymentSettlementsSumBypaymentId(payment.getId()));
					remainTotal = BH.sub(remainTotal, settlementNum, false);
				}
				PaymentSQL.addPayment(payment);
				WeixinSettlement weixinSettlement = ObjectFactory.getInstance().getWeixinSettlement(payment, paymentSettlement, "0", "");
				payment_amount = remainTotal;
				paymentType = viewTag;
				if (newPaymentMapList != null) {
					Map<String, Object> paymentMap = new HashMap<String, Object>();
					paymentMap.put("newPaymentSettlement", paymentSettlement);
					paymentMap.put("newSubPaymentSettlement", weixinSettlement);
					newPaymentMapList.add(paymentMap);
				}
			}
		}
			break;
		default:
			break;
		}

		initBillSummary();
		if(viewTag != ParamConst.SETTLEMENT_TYPE_STORED_CARD)
			closeMoneyKeyboard();

        if (settlementNum.compareTo(BH.getBD(order.getTotal())) == 0) {

        	if(viewTag==ParamConst.SETTLEMENT_TYPE_CASH)
			{
				if(TextUtils.isEmpty(tv_change_num.getText().toString())){
					printBill(null);
				}else {
					printBill(tv_change_num.getText().toString());
				}
			}else {
				printBill(null);
			}

        }
	}

	private void alipayClickEnterAction(String tradeNo, String buyerEmail, BigDecimal paidAmount){

		PaymentSQL.addPayment(payment);
		PaymentSettlement paymentSettlement = ObjectFactory.getInstance()
				.getPaymentSettlement(payment, paymentTypeId,
						paidAmount.toString());
		PaymentSettlementSQL.addPaymentSettlement(paymentSettlement);
		AlipaySettlement alipaySettlement = ObjectFactory.getInstance()
				.getAlipaySettlement(payment, paymentSettlement, tradeNo, buyerEmail);
		AlipaySettlementSQL.addAlipaySettlement(alipaySettlement);
		payment_amount = paidAmount;
		remainTotal = paidAmount;
		paymentType = viewTag;
		order.setOrderStatus(ParamConst.ORDER_STATUS_FINISHED);
		OrderSQL.update(order);
		if (newPaymentMapList != null) {
			Map<String, Object> paymentMap = new HashMap<String, Object>();
			paymentMap.put("newPaymentSettlement", paymentSettlement);
			paymentMap.put("newSubPaymentSettlement", alipaySettlement);
			newPaymentMapList.add(paymentMap);
		}
		initBillSummary();
	}

	private void clickOtherAction(String key) {
		show.append(key);
		switch (viewTag) {
		case ParamConst.SETTLEMENT_TYPE_CASH: {
			if(isFirstClickCash){
				if (show.length() > 0)
				show.delete(0, show.length());
				show.append(key);
				isFirstClickCash = false;
			}
			double shownum = Double.parseDouble(show.toString()) / 100.0;
			tv_total_amount_num.setText(App.instance.getLocalRestaurantConfig().getCurrencySymbol() + BH.getBD(shownum).toString());
			showCashChange();
		}
			break;
		case ParamConst.SETTLEMENT_TYPE_AMEX:
		case ParamConst.SETTLEMENT_TYPE_DINNER_INTERMATIONAL:
		case ParamConst.SETTLEMENT_TYPE_JCB:
		case ParamConst.SETTLEMENT_TYPE_UNIPAY:
		case ParamConst.SETTLEMENT_TYPE_MASTERCARD:
		case ParamConst.SETTLEMENT_TYPE_VISA: {
			if(selectView != null && selectView == tv_cards_amount_paid_num){
				selectView.setInputType(InputType.TYPE_CLASS_NUMBER);
				if(TextUtils.isEmpty(show)){
					selectView.setText(BH.getBD(0).toString());
				}else{
					if(!BH.compare(BH.mul(BH.getBD(show.toString()), BH.getBD("0.01"), true), remainTotal)){
						selectView.setText(BH.mul(BH.getBD(show.toString()), BH.getBD("0.01"), true).toString());
					}else{
						show.delete(show.length() - key.length(), show.length());
					}
				}

			}
			if (selectView != null && selectView == tv_card_no_num) {
				selectView.setInputType(InputType.TYPE_CLASS_NUMBER);
				selectView.setText(show);
			}
			if (selectView != null && selectView == tv_cards_cvv_num) {
				selectView.setInputType(InputType.TYPE_CLASS_NUMBER);
				selectView.setText(show);
			}
			if (selectView != null
					&& selectView == tv_cards_expiration_date_num) {
				selectView.setInputType(InputType.TYPE_CLASS_NUMBER);
				if (show.length() == 3) {
					show.insert(2, "/");
				}
				selectView.setText(show);
			}
		}
			break;
		case ParamConst.SETTLEMENT_TYPE_BILL_ON_HOLD: {
		}
			break;
		case ParamConst.SETTLEMENT_TYPE_ENTERTAINMENT: {
		}
			break;
		case ParamConst.SETTLEMENT_TYPE_VOID: {

		}
			break;
		case ParamConst.SETTLEMENT_TYPE_NETS: {
			if(selectView != null && selectView == tv_nets_amount_paid_num){
				selectView.setInputType(InputType.TYPE_CLASS_NUMBER);
				if(TextUtils.isEmpty(show)){
					selectView.setText(BH.getBD(0).toString());
				}else{
					if(!BH.compare(BH.mul(BH.getBD(show.toString()), BH.getBD("0.01"), true), remainTotal)){
						selectView.setText(BH.mul(BH.getBD(show.toString()), BH.getBD("0.01"), true).toString());
					}
				}
			}else if(selectView != null && selectView == tv_nets_ref_num){
				selectView.setInputType(InputType.TYPE_CLASS_NUMBER);
				selectView.setText(show.toString());
			}
		}
			break;
//		case ParamConst.SETTLEMENT_TYPE_ALIPAY: {
//			if(selectView != null && selectView == tv_wechat_ali_amount_paid_num){
//				selectView.setInputType(InputType.TYPE_CLASS_NUMBER);
//				if(TextUtils.isEmpty(show)){
//					selectView.setText("0.00");
//				}else{
//					if(!BH.compare(BH.mul(BH.getBD(show.toString()), BH.getBD("0.01"), true), remainTotal)){
//						selectView.setText(BH.mul(BH.getBD(show.toString()), BH.getBD("0.01"), true).toString());
//					}
//				}
//			}else if(selectView != null && selectView == tv_wechat_ali_ref_num){
//				selectView.setInputType(InputType.TYPE_CLASS_NUMBER);
//				selectView.setText(show.toString());
//			}
//		}
//			break;
		default:
			break;
		}
	}

	private void clickClearAction() {
		cash_num = null;
		change_num = null;
		show.delete(0, show.length());
		switch (viewTag) {
		case ParamConst.SETTLEMENT_TYPE_CASH: {
			tv_total_amount_num.setText(App.instance.getLocalRestaurantConfig().getCurrencySymbol() + BH.getBD(0).toString());
			tv_change_action_num.setText(App.instance.getLocalRestaurantConfig().getCurrencySymbol() + BH.getBD(0).toString());

		}
			break;
		case ParamConst.SETTLEMENT_TYPE_AMEX:
		case ParamConst.SETTLEMENT_TYPE_DINNER_INTERMATIONAL:
		case ParamConst.SETTLEMENT_TYPE_JCB:
		case ParamConst.SETTLEMENT_TYPE_MASTERCARD:
		case ParamConst.SETTLEMENT_TYPE_UNIPAY:
		case ParamConst.SETTLEMENT_TYPE_VISA: {
			if(selectView != null && selectView == tv_cards_amount_paid_num){
				selectView.setText(BH.getBD(0).toString());
			}
			if (selectView != null && selectView == tv_card_no_num) {
				selectView.setText("");
				selectView.setHint("8888");
			}
			if (selectView != null && selectView == tv_cards_cvv_num) {
				selectView.setText("");
				selectView.setHint("888");
			}
			if (selectView != null
					&& selectView == tv_cards_expiration_date_num) {
				selectView.setText("");
				selectView.setHint("11/88");
			}
		}
			break;
		case ParamConst.SETTLEMENT_TYPE_BILL_ON_HOLD: {
			// tv_authentication_amount_num.setText("");
		}
			break;
		case ParamConst.SETTLEMENT_TYPE_ENTERTAINMENT: {
			// ((TextView) contentView
			// .findViewById(R.id.tv_entertainment_amount_num))
			// .setText("");
		}
			break;
		case ParamConst.SETTLEMENT_TYPE_VOID: {

		}
			break;
		case ParamConst.SETTLEMENT_TYPE_NETS: {
			((TextView) contentView.findViewById(R.id.tv_nets_ref_num))
					.setText("");
		}
			break;
//		case ParamConst.SETTLEMENT_TYPE_ALIPAY: {
//			((TextView) findViewById(R.id.tv_wechat_ali_ref_num))
//					.setText("");
//		}
//			break;
		default:
			break;
		}
	}

	public boolean verifyCardNo() {
		if (TextUtils.isEmpty(tv_card_no_num.getText())) {
			UIHelp.showToast(parent, parent.getResources().getString(R.string.card_no_not_empty));
			return false;
		}
		if (tv_card_no_num.getText().toString().trim().length() != 4) {
			UIHelp.showToast(parent, parent.getResources().getString(R.string.card_no_for_four));
			return false;
		}
		if (!TextUtils.isEmpty(tv_card_no_num.getText())
				&& tv_card_no_num.getText().toString().trim().length() == 4
				&& tv_card_no_num.getInputType() == InputType.TYPE_CLASS_NUMBER
				&& !tv_card_no_num.getText().toString().trim()
						.equals("0000")) {
			return true;
		}
		UIHelp.showToast(parent, parent.getResources().getString(R.string.card_no_not_correct));
		return false;
	}

	public boolean verifyCVV() {
		if (TextUtils.isEmpty(tv_cards_cvv_num.getText())) {
			UIHelp.showToast(parent, parent.getResources().getString(R.string.cvv_not_empty));
			return false;
		}
		if (tv_cards_cvv_num.getText().toString().trim().length() != 3) {
			UIHelp.showToast(parent, parent.getResources().getString(R.string.cvv_for_three));
			return false;
		}
		if (!TextUtils.isEmpty(tv_cards_cvv_num.getText())
				&& tv_cards_cvv_num.getText().toString().trim().length() == 3
				&& tv_cards_cvv_num.getInputType() == InputType.TYPE_CLASS_NUMBER) {
			return true;
		}
		return false;
	}

	public boolean verifyExpirationDate() {
		if (TextUtils.isEmpty(tv_cards_cvv_num.getText())) {
			UIHelp.showToast(parent, parent.getResources().getString(R.string.exp_not_empty));
			return false;
		}
		if (!TextUtils.isEmpty(tv_cards_expiration_date_num.getText())
				&& tv_cards_expiration_date_num.getText().toString().trim()
						.length() == 5) {
			return true;
		}
		UIHelp.showToast(parent, parent.getResources().getString(R.string.exp_format));
		return false;
	}
	
	private void addVoidOrEntTax(){
		OrderDetailTaxSQL.updateOrderDetailTaxActiveByOrder(ParamConst.ACTIVE_NOMAL, order.getId().intValue());
		order.setTotal(BH.add(BH.getBDNoFormat(order.getTotal()), BH.getBD(order.getTaxAmount()), true).toString());
		payment.setPaymentAmount(order.getTotal());
		remainTotal = BH.getBD(order.getTotal());
		settlementNum = BH.getBD(ParamConst.DOUBLE_ZERO);
		OrderSQL.update(order);
		PaymentSQL.addPayment(payment);
	}
	
	private void deleteVoidOrEntTax(){
		OrderDetailTaxSQL.updateOrderDetailTaxActiveByOrder(ParamConst.ACTIVE_DELETE, order.getId().intValue());
		order.setTotal(BH.sub(BH.getBD(order.getSubTotal()),
						BH.getBD(order.getDiscountAmount()), false).toString());
		payment.setPaymentAmount(order.getTotal());
		OrderSQL.update(order);
		PaymentSQL.addPayment(payment);
	}

	private void refundTax(){
		OrderDetailTaxSQL.updateOrderDetailTaxActiveByOrder(ParamConst.ACTIVE_REFUND, order.getId().intValue());
//		order.setTotal(BH.sub(BH.getBDNoFormat(order.getTotal()), BH.getBD(order.getTaxAmount()), true).toString());
		payment.setPaymentAmount(order.getTotal());
		remainTotal = BH.getBD(order.getTotal());
		settlementNum = BH.getBD(ParamConst.DOUBLE_ZERO);
		OrderSQL.update(order);
		PaymentSQL.addPayment(payment);
	}
}
