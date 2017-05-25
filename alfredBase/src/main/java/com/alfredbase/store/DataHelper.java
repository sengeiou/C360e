package com.alfredbase.store;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.alfredbase.BaseApplication;
import com.alfredbase.ParamConst;

public class DataHelper {
	private SQLiteDatabase db;
	private static String database_name;
	private static int database_version;

	public DataHelper(Context _context, String name, int version) {
		if (db == null) {
			database_name = name;
			database_version = version;
			OpenHelper openHelper = new OpenHelper(_context);
			this.db = openHelper.getWritableDatabase();
		}
	}

	public SQLiteDatabase getDb() {
		return db;
	}

	private static class OpenHelper extends SQLiteOpenHelper {

		public OpenHelper(Context context) {
			super(context, database_name, null, database_version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.beginTransaction();
			try {
				createTable(db);
				onUpgradeForOldVersion1(db);
				onUpgradeForOldVersion2(db);
				onUpgradeForOldVersion3(db);
				onUpgradeForOldVersion4(db);
				onUpgradeForOldVersion5(db);
				onUpgradeForOldVersion6(db);
				onUpgradeForOldVersion7(db);
				onUpgradeForOldVersion8(db);
				onUpgradeForOldVersion9(db);
				onUpgradeForOldVersion11(db);
				onUpgradeForOldVersion12(db);
				onUpgradeForOldVersion13(db);
				onUpgradeForOldVersion14(db);
				db.setTransactionSuccessful();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				db.endTransaction();
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.beginTransaction();
			try {
				switch (oldVersion) {
					case 1:
						onUpgradeForOldVersion1(db);
						onUpgradeForOldVersion2(db);
						onUpgradeForOldVersion3(db);
						onUpgradeForOldVersion4(db);
						onUpgradeForOldVersion5(db);
						onUpgradeForOldVersion6(db);
						onUpgradeForOldVersion7(db);
						onUpgradeForOldVersion8(db);
						onUpgradeForOldVersion9(db);
						onUpgradeForOldVersion10(db);
						onUpgradeForOldVersion11(db);
						onUpgradeForOldVersion12(db);
						onUpgradeForOldVersion13(db);
						onUpgradeForOldVersion14(db);
						break;
					case 2:
						onUpgradeForOldVersion2(db);
						onUpgradeForOldVersion3(db);
						onUpgradeForOldVersion4(db);
						onUpgradeForOldVersion5(db);
						onUpgradeForOldVersion6(db);
						onUpgradeForOldVersion7(db);
						onUpgradeForOldVersion8(db);
						onUpgradeForOldVersion9(db);
						onUpgradeForOldVersion10(db);
						onUpgradeForOldVersion11(db);
						onUpgradeForOldVersion12(db);
						onUpgradeForOldVersion13(db);
						onUpgradeForOldVersion14(db);
						break;
					case 3:
						onUpgradeForOldVersion3(db);
						onUpgradeForOldVersion4(db);
						onUpgradeForOldVersion5(db);
						onUpgradeForOldVersion6(db);
						onUpgradeForOldVersion7(db);
						onUpgradeForOldVersion8(db);
						onUpgradeForOldVersion9(db);
						onUpgradeForOldVersion10(db);
						onUpgradeForOldVersion11(db);
						onUpgradeForOldVersion12(db);
						onUpgradeForOldVersion13(db);
						onUpgradeForOldVersion14(db);
						break;
					case 4:
						onUpgradeForOldVersion4(db);
						onUpgradeForOldVersion5(db);
						onUpgradeForOldVersion6(db);
						onUpgradeForOldVersion7(db);
						onUpgradeForOldVersion8(db);
						onUpgradeForOldVersion9(db);
						onUpgradeForOldVersion10(db);
						onUpgradeForOldVersion11(db);
						onUpgradeForOldVersion12(db);
						onUpgradeForOldVersion13(db);
						onUpgradeForOldVersion14(db);
						break;
					case 5:
						onUpgradeForOldVersion5(db);
						onUpgradeForOldVersion6(db);
						onUpgradeForOldVersion7(db);
						onUpgradeForOldVersion8(db);
						onUpgradeForOldVersion9(db);
						onUpgradeForOldVersion10(db);
						onUpgradeForOldVersion11(db);
						onUpgradeForOldVersion12(db);
						onUpgradeForOldVersion13(db);
						onUpgradeForOldVersion14(db);
						break;
					case 6:
						onUpgradeForOldVersion6(db);
						onUpgradeForOldVersion7(db);
						onUpgradeForOldVersion8(db);
						onUpgradeForOldVersion9(db);
						onUpgradeForOldVersion10(db);
						onUpgradeForOldVersion11(db);
						onUpgradeForOldVersion12(db);
						onUpgradeForOldVersion13(db);
						onUpgradeForOldVersion14(db);
						break;
					case 7:
						onUpgradeForOldVersion7(db);
						onUpgradeForOldVersion8(db);
						onUpgradeForOldVersion9(db);
						onUpgradeForOldVersion10(db);
						onUpgradeForOldVersion11(db);
						onUpgradeForOldVersion12(db);
						onUpgradeForOldVersion13(db);
						onUpgradeForOldVersion14(db);
						break;
					case 8:
						onUpgradeForOldVersion8(db);
						onUpgradeForOldVersion9(db);
						onUpgradeForOldVersion10(db);
						onUpgradeForOldVersion11(db);
						onUpgradeForOldVersion12(db);
						onUpgradeForOldVersion13(db);
						onUpgradeForOldVersion14(db);
						break;
					case 9:
						onUpgradeForOldVersion9(db);
						onUpgradeForOldVersion10(db);
						onUpgradeForOldVersion11(db);
						onUpgradeForOldVersion12(db);
						onUpgradeForOldVersion13(db);
						onUpgradeForOldVersion14(db);
						break;
					case 10:
						onUpgradeForOldVersion10(db);
						onUpgradeForOldVersion11(db);
						onUpgradeForOldVersion12(db);
						onUpgradeForOldVersion13(db);
						onUpgradeForOldVersion14(db);
						break;
					case 11:
						onUpgradeForOldVersion11(db);
						onUpgradeForOldVersion12(db);
						onUpgradeForOldVersion13(db);
						onUpgradeForOldVersion14(db);
						break;
					case 12:
						onUpgradeForOldVersion12(db);
						onUpgradeForOldVersion13(db);
						onUpgradeForOldVersion14(db);
						break;
					case 13:
						onUpgradeForOldVersion13(db);
						onUpgradeForOldVersion14(db);
						break;
					case 14:
						onUpgradeForOldVersion14(db);
						break;
				default:
					break;
				}
				db.setTransactionSuccessful();
			} catch (Exception e) {
			} finally {
				db.endTransaction();
			}
		}

		private void createTable(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE "
					+ TableNames.Company
					+ " (id INTEGER PRIMARY KEY AUTOINCREMENT,userId INTEGER,companyName TEXT,email TEXT, level INTEGER,address1 TEXT,address2 TEXT,telNo TEXT,country TEXT,state TEXT,city TEXT,postalCode TEXT,createTime LONG,updateTime LONG)");
			db.execSQL("CREATE TABLE "
					+ TableNames.ItemCategory
					+ " (id INTEGER PRIMARY KEY AUTOINCREMENT,itemCategoryName TEXT,superCategoryId INTEGER,color TEXT, itemMainCategoryId INTEGER,restaurantId INTEGER,isActive INTEGER,indexId INTEGER,printerGroupId INTEGER,userId INTEGER,createTime LONG,updateTime LONG)");
			db.execSQL("CREATE TABLE "
					+ TableNames.User
					+ " (id INTEGER PRIMARY KEY AUTOINCREMENT,empId INTEGER,  type INTEGER,  status INTEGER, accountName TEXT,  userName TEXT,  password TEXT,firstName TEXT,  lastName TEXT,  nickName TEXT,companyId INTEGER, createTime LONG,  updateTime LONG)");
			db.execSQL("CREATE TABLE "
					+ TableNames.UserRestaurant
					+ " (id INTEGER PRIMARY KEY AUTOINCREMENT,userId INTEGER,  restaurantId INTEGER,  revenueId INTEGER, kitchenId INTEGER)");
			db.execSQL("CREATE TABLE "
					+ TableNames.RevenueCenter
					+ " (id INTEGER PRIMARY KEY AUTOINCREMENT,restaurantId INTEGER, printId INTEGER,revName TEXT,  isActive INTEGER,  createTime LONG,updateTime LONG ,happy_hour_id INTEGER,happy_start_time LONG,happy_end_time LONG)");
			db.execSQL("CREATE TABLE "
					+ TableNames.Printer
					+ " (id INTEGER PRIMARY KEY AUTOINCREMENT, printerGroupName TEXT, printerName TEXT, printerLocation TEXT,printerType TEXT,  qPrint TEXT,  isCashdrawer INTEGER,companyId INTEGER,  restaurantId INTEGER, type INTEGER, createTime LONG,updateTime LONG)");
			db.execSQL("CREATE TABLE "
					+ TableNames.PrinterGroup
					+ " (id INTEGER PRIMARY KEY AUTOINCREMENT, printerGroupId INTEGER,  printerId INTEGER,companyId INTEGER,  restaurantId INTEGER)");
//			db.execSQL("CREATE TABLE "
//					+ TableNames.Places
//					+ " (id INTEGER PRIMARY KEY AUTOINCREMENT, placeName TEXT,placeDescription TEXT,restaurantId INTEGER,  revenueId INTEGER,  isActive INTEGER)");
			db.execSQL("CREATE TABLE "
					+ TableNames.Restaurant
					+ " (id INTEGER PRIMARY KEY AUTOINCREMENT,  companyId INTEGER,  restaurantName TEXT,type INTEGER,  status INTEGER,  description TEXT,  email TEXT,address1 TEXT,  address2 TEXT,  telNo TEXT,  country TEXT,state TEXT,  city TEXT,  postalCode TEXT,  createTime LONG,updateTime LONG, website TEXT, addressPrint TEXT, logoUrl TEXT, qrPayment INTEGER, restaurantPrint TEXT)");
//			db.execSQL("CREATE TABLE "
//					+ TableNames.Tables
//					+ " (id INTEGER PRIMARY KEY AUTOINCREMENT, restaurantId INTEGER,  revenueId INTEGER,placesId INTEGER,  tableName TEXT,  tablePacks INTEGER,isActive INTEGER, tableStatus INTEGER)");
			db.execSQL("CREATE TABLE "
					+ TableNames.ItemDetail
					+ "(id INTEGER PRIMARY KEY AUTOINCREMENT, restaurantId INTEGER, itemTemplateId INTEGER, revenueId INTEGER, itemName TEXT, itemDesc TEXT, itemCode TEXT, imgUrl TEXT, price TEXT, itemType INTEGER, printerId INTEGER, isModifier INTEGER, itemMainCategoryId INTEGER, itemCategoryId INTEGER, isActive INTEGER, taxCategoryId INTEGER, isPack INTEGER, isTakeout INTEGER, happyHoursId INTEGER, userId INTEGER, createTime LONG, updateTime LONG)");
			db.execSQL("CREATE TABLE "
					+ TableNames.ItemModifier
					+ "(id INTEGER PRIMARY KEY AUTOINCREMENT, restaurantId INTEGER, itemId INTEGER, modifierId INTEGER, modifierCategoryId INTEGER)");
			db.execSQL("CREATE TABLE "
					+ TableNames.ItemMainCategory
					+ "(id INTEGER PRIMARY KEY AUTOINCREMENT,mainCategoryName TEXT, color TEXT,restaurantId INTEGER,isActive INTEGER,indexId INTEGER,userId INTEGER,printerGroupId INTEGER,createTime LONG,updateTime LONG)");
			db.execSQL("CREATE TABLE "
					+ TableNames.Modifier
					+ "(id INTEGER PRIMARY KEY AUTOINCREMENT, restaurantId INTEGER, type INTEGER, categoryId INTEGER, categoryName TEXT, price TEXT, modifierName TEXT, isActive INTEGER, isDefault INTEGER)");
			db.execSQL("CREATE TABLE "
					+ TableNames.Tax
					+ "(id INTEGER PRIMARY KEY AUTOINCREMENT,companyId INTEGER,restaurantId INTEGER,taxName TEXT,taxPercentage TEXT,taxType INTEGER,status INTEGER,createTime LONG,updateTime LONG)");
			db.execSQL("CREATE TABLE "
					+ TableNames.TaxCategory
					+ "(id INTEGER PRIMARY KEY AUTOINCREMENT, companyId INTEGER,restaurantId INTEGER,taxCategoryId INTEGER,taxCategoryName TEXT,taxId INTEGER,taxOn INTEGER,taxOnId INTEGER,indexId INTEGER,status INTEGER)");
			db.execSQL("CREATE TABLE "
					+ TableNames.LocalDevice
					+ "(id INTEGER PRIMARY KEY AUTOINCREMENT, deviceId INTEGER, deviceName TEXT, userName TEXT, deviceType INTEGER, ip TEXT, macAddress TEXT, connected INTEGER,cashierPrinter INTEGER)");
			db.execSQL("CREATE TABLE "
					+ TableNames.Order
					+ "(id INTEGER PRIMARY KEY AUTOINCREMENT, orderOriginId INTEGER, userId INTEGER, persons INTEGER, orderStatus INTEGER, subTotal TEXT, taxAmount TEXT, discountAmount TEXT, total TEXT, sessionStatus INTEGER, restId INTEGER, revenueId INTEGER, placeId INTEGER, tableId INTEGER, createTime LONG, updateTime LONG,orderNo INTEGER,businessDate LONG,discount_rate TEXT,discount_type INTEGER)");
			db.execSQL("CREATE TABLE "
					+ TableNames.OrderDetail
					+ "(id INTEGER PRIMARY KEY AUTOINCREMENT, orderId INTEGER, orderOriginId INTEGER, userId INTEGER, itemId INTEGER, itemName TEXT, itemNum INTEGER, orderDetailStatus INTEGER, orderDetailType INTEGER, reason TEXT, printStatus INTEGER, itemPrice TEXT, taxPrice TEXT, discountPrice TEXT, modifierPrice TEXT, realPrice TEXT, createTime LONG, updateTime LONG,discountRate TEXT,discountType INTEGER,fromOrderDetailId INTEGER,isFree INTEGER,groupId INTEGER,isOpenItem INTEGER, specialInstractions TEXT)");
			db.execSQL("CREATE TABLE "
					+ TableNames.OrderModifier
					+ "(id INTEGER PRIMARY KEY AUTOINCREMENT, orderId INTEGER, orderDetailId INTEGER, orderOriginId INTEGER, userId INTEGER, itemId INTEGER, modifierId INTEGER, modifierNum INTEGER, status INTEGER, modifierPrice TEXT, createTime LONG, updateTime LONG)");
			db.execSQL("CREATE TABLE "
					+ TableNames.OrderDetailTax
					+ "(id INTEGER PRIMARY KEY AUTOINCREMENT, orderId INTEGER, orderDetailId INTEGER, taxId INTEGER, taxName TEXT, taxPercentage TEXT, taxPrice TEXT, taxType INTEGER, createTime LONG, updateTime LONG)");
			db.execSQL("CREATE TABLE "
					+ TableNames.OrderBill
					+ "(id INTEGER PRIMARY KEY AUTOINCREMENT, billNo INTEGER, orderId INTEGER, orderSplitId INTEGER, type INTEGER, restaurantId INTEGER, revenueId INTEGER, userId INTEGER, createTime LONG, updateTime LONG)");
			db.execSQL("CREATE TABLE "
					+ TableNames.HappyHour
					+ "(id INTEGER PRIMARY KEY AUTOINCREMENT, restaurant_id INTEGER, happy_hour_name TEXT, is_active INTEGER, create_time LONG, update_time LONG)");
			db.execSQL("CREATE TABLE "
					+ TableNames.HappyHourWeek
					+ "(id INTEGER PRIMARY KEY AUTOINCREMENT, happy_hour_id INTEGER, week TEXT, start_time LONG, end_time LONG , is_active INTEGER)");
			db.execSQL("CREATE TABLE "
					+ TableNames.ItemHappyHour
					+ "(id INTEGER PRIMARY KEY AUTOINCREMENT, happy_hour_id INTEGER, item_main_category_id INTEGER,item_category_id INTEGER,item_id INTEGER, type INTEGER,  discount_price TEXT, discount_rate TEXT,  free_num INTEGER,item_main_category_name TEXT , item_category_name TEXT , item_name TEXT,free_item_id INTEGER,free_item_name TEXT)");
			db.execSQL("CREATE TABLE "
					+ TableNames.Payment
					+ "(id INTEGER PRIMARY KEY AUTOINCREMENT, billNo INTEGER, orderId INTEGER, orderSplitId INTEGER, businessDate LONG, type INTEGER, restaurantId INTEGER, revenueId INTEGER,  userId INTEGER, paymentAmount TEXT, taxAmount TEXT, discountAmount TEXT, createTime LONG, updateTime LONG)");
			db.execSQL("CREATE TABLE "
					+ TableNames.PaymentSettlement
					+ "(id INTEGER PRIMARY KEY AUTOINCREMENT, billNo INTEGER, paymentId INTEGER, paymentTypeId INTEGER, paidAmount TEXT, totalAmount TEXT, restaurantId INTEGER, revenueId INTEGER, userId INTEGER, createTime LONG, updateTime LONG, cashChange TEXT)");
			db.execSQL("CREATE TABLE "
					+ TableNames.NonChargableSettlement
					+ "(id INTEGER PRIMARY KEY AUTOINCREMENT, orderId INTEGER, billNo INTEGER, paymentId INTEGER, paymentSettId INTEGER, nameOfPerson TEXT, remarks TEXT, authorizedUserId TEXT, amount TEXT, restaurantId INTEGER, revenueId INTEGER, userId INTEGER,  createTime LONG, updateTime LONG)");
			db.execSQL("CREATE TABLE "
					+ TableNames.BohHoldSettlement
					+ "(id INTEGER PRIMARY KEY AUTOINCREMENT, restaurantId INTEGER, revenueId INTEGER, orderId INTEGER, paymentId INTEGER, paymentSettId INTEGER, billNo INTEGER,  nameOfPerson TEXT, phone TEXT, remarks TEXT, authorizedUserId INTEGER,  amount TEXT, status INTEGER, paymentType INTEGER,  paidDate LONG, daysDue LONG)");
			db.execSQL("CREATE TABLE "
					+ TableNames.SyncMsg
					+ "(id TEXT PRIMARY KEY, orderId INTEGER, msg_type INTEGER, data TEXT, status INTEGER, createTime LONG,revenueId INTEGER, businessDate LONG)");
			db.execSQL("CREATE TABLE "
					+ TableNames.OrderSplit
					+ "(id INTEGER PRIMARY KEY AUTOINCREMENT,orderId INTEGER,orderOriginId INTEGER,userId INTEGER,persons INTEGER,orderStatus INTEGER,subTotal TEXT,taxAmount TEXT,discountAmount TEXT,total TEXT,sessionStatus INTEGER,restId INTEGER, revenueId INTEGER,tableId INTEGER,createTime LONG,updateTime LONG,sysCreateTime TEXT, sysUpdateTime TEXT)");
			db.execSQL("CREATE TABLE "
					+ TableNames.VoidSettlement
					+ "(id INTEGER PRIMARY KEY AUTOINCREMENT, orderId INTEGER, billNo INTEGER, paymentId INTEGER, paymentSettId INTEGER, reason TEXT, authorizedUserId INTEGER, amount TEXT, restaurantId INTEGER, revenueId INTEGER, userId INTEGER, createTime LONG, updateTime LONG)");
			db.execSQL("CREATE TABLE "
					+ TableNames.NetsSettlement
					+ "(id INTEGER PRIMARY KEY AUTOINCREMENT,referenceNo INTEGER, paymentId INTEGER, paymentSettId INTEGER, billNo INTEGER, cashAmount TEXT, createTime LONG, updateTime LONG)");
			db.execSQL("CREATE TABLE "
					+ TableNames.CardsSettlement
					+ "(id INTEGER PRIMARY KEY AUTOINCREMENT,paymentId INTEGER, paymentSettId INTEGER, billNo INTEGER, cardNo TEXT, cardType INTEGER, cvvNo INTEGER, cardExpiryDate TEXT, createTime LONG, updateTime LONG)");
			db.execSQL("CREATE TABLE "
					+ TableNames.ReportDaySales
					+ "(id INTEGER PRIMARY KEY AUTOINCREMENT, restaurantId INTEGER, restaurantName TEXT, revenueId INTEGER, revenueName TEXT, businessDate LONG, itemSales TEXT, itemSalesQty INTEGER, discountPer TEXT, discountPerQty INTEGER, discount TEXT, discountQty INTEGER, discountAmt TEXT, focItem TEXT, focItemQty INTEGER, focBill TEXT, focBillQty INTEGER, totalSales TEXT, cash TEXT, cashQty INTEGER, nets TEXT, netsQty INTEGER, visa TEXT, visaQty INTEGER, mc TEXT, mcQty INTEGER, amex TEXT, amexQty INTEGER, jbl TEXT, jblQty INTEGER, unionPay TEXT, unionPayQty INTEGER, diner TEXT, dinerQty INTEGER, holdld TEXT, holdldQty INTEGER, totalCard TEXT, totalCardQty INTEGER, totalCash TEXT, totalCashQty INTEGER, billVoid TEXT, billVoidQty INTEGER, itemVoid TEXT, itemVoidQty INTEGER, nettSales TEXT, totalBills INTEGER, openCount INTEGER, firstReceipt INTEGER, lastReceipt INTEGER, totalTax TEXT, orderQty INTEGER, personQty INTEGER, totalBalancePrice TEXT, cashInAmt TEXT, cashOutAmt TEXT, varianceAmt TEXT)");
			db.execSQL("CREATE TABLE "
					+ TableNames.ReportDayTax
					+ "(id INTEGER PRIMARY KEY AUTOINCREMENT, daySalesId INTEGER, restaurantId INTEGER, restaurantName TEXT, revenueId INTEGER, revenueName TEXT, businessDate LONG, taxId INTEGER, taxName TEXT, taxPercentage TEXT, taxQty INTEGER, taxAmount TEXT)");
			db.execSQL("CREATE TABLE "
					+ TableNames.ReportPluDayItem
					+ "(id INTEGER PRIMARY KEY AUTOINCREMENT, reportNo INTEGER, restaurantId INTEGER, restaurantName TEXT, revenueId INTEGER, revenueName TEXT, businessDate LONG, itemMainCategoryId INTEGER, itemMainCategoryName TEXT, itemCategoryId INTEGER, itemCategoryName TEXT, itemDetailId INTEGER, itemName TEXT, itemPrice TEXT, itemCount INTEGER, itemAmount TEXT, itemVoidQty INTEGER, itemVoidPrice TEXT, itemHoldQty INTEGER, itemHoldPrice TEXT, itemFocQty INTEGER, itemFocPrice TEXT,billVoidQty INTEGER, billVoidPrice TEXT)");
			db.execSQL("CREATE TABLE "
					+ TableNames.ReportPluDayModifier
					+ "(id INTEGER PRIMARY KEY AUTOINCREMENT, reportNo INTEGER, restaurantId INTEGER, restaurantName TEXT, revenueId INTEGER, revenueName TEXT, businessDate LONG, modifierCategoryId INTEGER, modifierCategoryName TEXT, modifierId INTEGER, modifierName TEXT, modifierPrice TEXT, modifierCount INTEGER, billVoidPrice TEXT, billVoidCount INTEGER, voidModifierPrice TEXT, voidModifierCount INTEGER, bohModifierPrice TEXT, bohModifierCount INTEGER, focModifierPrice TEXT,focModifierCount INTEGER)");
			db.execSQL("CREATE TABLE "
					+ TableNames.RoundRule
					+ "(id INTEGER PRIMARY KEY AUTOINCREMENT, country TEXT, status INTEGER, ruleType TEXT)");
			db.execSQL("CREATE TABLE "
					+ TableNames.RoundAmount
					+ "(id INTEGER PRIMARY KEY AUTOINCREMENT, orderId INTEGER, billNo INTEGER, roundBeforePrice TEXT, roundAlfterPrice TEXT, roundBalancePrice DOUBLE,restId INTEGER,revenueId INTEGER,tableId INTEGER,businessDate LONG ,createTime LONG,updateTime LONG)");
			db.execSQL("CREATE TABLE "
					+ TableNames.ReportHourly
					+ "(id INTEGER PRIMARY KEY AUTOINCREMENT, restaurantId INTEGER, revenueId INTEGER, revenueName TEXT, businessDate LONG, hour INTEGER, amountQty INTEGER, amountPrice TEXT)");
			db.execSQL("CREATE TABLE "
					+ TableNames.KotItemModifier
					+ "(id INTEGER PRIMARY KEY AUTOINCREMENT, kotItemDetailId INTEGER, modifierId INTEGER, modifierName TEXT, modifierNum INTEGER, status INTEGER)");
			db.execSQL("CREATE TABLE "
					+ TableNames.KotItemDetail
					+ "(id INTEGER PRIMARY KEY AUTOINCREMENT, restaurantId INTEGER, revenueId INTEGER, orderId INTEGER, orderDetailId INTEGER, printerGroupId INTEGER, kotSummaryId INTEGER, itemName TEXT, itemNum INTEGER, finishQty INTEGER, sessionStatus INTEGER, kotStatus INTEGER, specialInstractions TEXT, version INTEGER,createTime LONG,updateTime LONG)");
			db.execSQL("CREATE TABLE "
					+ TableNames.EmpWorkLog
					+ "(id INTEGER PRIMARY KEY AUTOINCREMENT, userId INTEGER, empId INTEGER, empName TEXT, loginTime INTEGER, logoutTime INTEGER,totalHours INTEGER,status INTEGER)");
			db.execSQL("CREATE TABLE "
					+ TableNames.CashInOut
					+ "(id INTEGER PRIMARY KEY AUTOINCREMENT, restaurantId INTEGER,  revenueId INTEGER, userId INTEGER, empId INTEGER, empName TEXT, businessDate LONG, type INTEGER, comment  TEXT,cash TEXT, createTime LONG)");
			db.execSQL("CREATE TABLE "
					+ TableNames.KotSummary
					+ "(id INTEGER PRIMARY KEY AUTOINCREMENT, orderId INTEGER, revenueCenterId INTEGER, tableId INTEGER, tableName TEXT, revenueCenterName TEXT, status INT, createTime LONG,updateTime LONG, businessDate LONG)");
			db.execSQL("CREATE TABLE "
					+ TableNames.KotNotification
					+ "(id INTEGER PRIMARY KEY AUTOINCREMENT, orderId INTEGER, orderDetailId INTEGER, revenueCenterId INTEGER, tableName TEXT, revenueCenterName TEXT,itemName TEXT, qty INTEGER, session INTEGER, status INTEGER)");
			db.execSQL("CREATE TABLE "
					+ TableNames.UserTimeSheet
					+ "(id INTEGER PRIMARY KEY AUTOINCREMENT, businessDate LONG, restaurantId INTEGER, revenueId INTEGER, userId INTEGER, empId INTEGER, empName TEXT, loginTime LONG, logoutTime LONG, totalHours DOUBLE, status INTEGER)");
			db.execSQL("CREATE TABLE "
					+ TableNames.SettingData
					+ "(id INTEGER PRIMARY KEY AUTOINCREMENT, logoUrl TEXT, logoString TEXT)");
			db.execSQL("CREATE TABLE "
					+ TableNames.ReportDiscount
					+ "(id INTEGER PRIMARY KEY AUTOINCREMENT, restaurantId INTEGER, revenueId INTEGER, userId INTEGER, orderId INTEGER, revenueName TEXT, businessDate LONG, billNumber INTEGER, tableId INTEGER, tableName TEXT, actuallAmount TEXT, discount TEXT,grandTotal TEXT)");
			db.execSQL("CREATE TABLE "
					+ TableNames.RestaurantConfig
					+ "(id INTEGER PRIMARY KEY AUTOINCREMENT, restaurantId INTEGER, paraId INTEGER, paraType INTEGER, paraName TEXT, paraValue1 TEXT, paraValue2 TEXT)");

			// create index
			db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS localDevice_idx1 on "
					+ TableNames.LocalDevice
					+ "(deviceId, deviceType, macAddress)");

			/**
			 * 更新数据库ReportPluDayItem和ReportPluDayModifier
			 */
			db.execSQL("ALTER TABLE " + TableNames.ReportPluDayItem
					+ " ADD COLUMN billFocQty INTEGER");

			db.execSQL("ALTER TABLE " + TableNames.ReportPluDayItem
					+ " ADD COLUMN billFocPrice TEXT");

			db.execSQL("ALTER TABLE " + TableNames.ReportPluDayModifier
					+ " ADD COLUMN billFocPrice TEXT");

			db.execSQL("ALTER TABLE " + TableNames.ReportPluDayModifier
					+ " ADD COLUMN billFocCount INTEGER");
			/*
			 * 更新RevenueCenter的字段 
			 */
			db.execSQL("ALTER TABLE " + TableNames.RevenueCenter
					+ " ADD COLUMN currentBillNo TEXT");
			db.execSQL("ALTER TABLE " + TableNames.RevenueCenter
					+ " ADD COLUMN indexId INTEGER default '1' ");
			db.execSQL("ALTER TABLE " + TableNames.RevenueCenter
					+ " ADD COLUMN currentValue INTEGER default '0' ");
			/*
			 * 更新ItemDetail的字段
			 */
			db.execSQL("ALTER TABLE " + TableNames.ItemDetail
					+ " ADD COLUMN indexId INTEGER");
			/*
			 * OrderSplit 中添加一个GroupId 方便OrderDetail关联使用
			 */
			db.execSQL("ALTER TABLE " + TableNames.OrderSplit
					+ " ADD COLUMN groupId INTEGER");
			/*
			/*
			 * OrderDetail 中添加一个orderSplitId
			 */
			db.execSQL("ALTER TABLE " + TableNames.OrderDetail
					+ " ADD COLUMN orderSplitId INTEGER");
			/*
			 * 更新LocalDevice
			 */
			db.execSQL("ALTER TABLE " + TableNames.LocalDevice
					+ " ADD COLUMN deviceMode TEXT default ''");
			
			/*
			 * KotItemDetail 中添加一个unFinishQty
			 */
			db.execSQL("ALTER TABLE " + TableNames.KotItemDetail
					+ " ADD COLUMN unFinishQty INTEGER default 0");
			/*
			 * KotNotification 中添加一个unFinishQty
			 */
			db.execSQL("ALTER TABLE " + TableNames.KotNotification
					+ " ADD COLUMN unFinishQty INTEGER default 0");

			/*
			 * KotItemDetail 中添加一个categoryId
			 */
			db.execSQL("ALTER TABLE " + TableNames.KotItemDetail
					+ " ADD COLUMN categoryId INTEGER default 0");
			
			db.execSQL("ALTER TABLE " + TableNames.RevenueCenter
					+ " ADD COLUMN isKiosk INTEGER default 0");
			db.execSQL("ALTER TABLE " + TableNames.KotNotification
					+ " ADD COLUMN kotItemDetailId INTEGER");

			/*
			 * Order 中添加一个discountPrice
			 */
			
			db.execSQL("ALTER TABLE " + TableNames.Order
					+ " ADD COLUMN discountPrice TEXT");
			
			db.execSQL("ALTER TABLE " + TableNames.SyncMsg
					+ " ADD COLUMN currCount INTEGER default 0");
			
			db.execSQL("ALTER TABLE " + TableNames.OrderDetail
					+ " ADD COLUMN isTakeAway INTEGER default 0");
			
			db.execSQL("ALTER TABLE " + TableNames.KotItemDetail
					+ " ADD COLUMN isTakeAway INTEGER default 0");
			
			db.execSQL("ALTER TABLE " + TableNames.PaymentSettlement
					+ " ADD COLUMN isActive INTEGER default 0"); 
			
			db.execSQL("ALTER TABLE " + TableNames.CardsSettlement
					+ " ADD COLUMN isActive INTEGER default 0"); 

			db.execSQL("ALTER TABLE " + TableNames.BohHoldSettlement
					+ " ADD COLUMN isActive INTEGER default 0"); 
		
			db.execSQL("ALTER TABLE " + TableNames.VoidSettlement
					+ " ADD COLUMN isActive INTEGER default 0"); 
			
			db.execSQL("ALTER TABLE " + TableNames.NonChargableSettlement
					+ " ADD COLUMN isActive INTEGER default 0"); 
			
			db.execSQL("ALTER TABLE " + TableNames.NetsSettlement
					+ " ADD COLUMN isActive INTEGER default 0"); 
			
			// create index
			db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS daySalesReport_idx1 on "
					+ TableNames.ReportDaySales
					+ "(restaurantId, revenueId, businessDate)");
			
			db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS dayPluReport_idx1 on "
					+ TableNames.ReportPluDayItem
					+ "(restaurantId, revenueId, businessDate, itemDetailId)");
			
			db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS datTaxReport_idx1 on "
					+ TableNames.ReportDayTax
					+ "(restaurantId, revenueId, businessDate, taxId)");
			
			db.execSQL("ALTER TABLE " + TableNames.KotNotification
					+ " ADD COLUMN kotItemNum INTEGER ");
			//Receipt header and footer
			db.execSQL("ALTER TABLE " + TableNames.Restaurant
					+ " ADD COLUMN options TEXT "); 
			db.execSQL("ALTER TABLE " + TableNames.Restaurant
					+ " ADD COLUMN footerOptions TEXT ");
			//Receipt header and footer
			db.execSQL("ALTER TABLE " + TableNames.RoundAmount
					+ " ADD COLUMN orderSplitId INTEGER default 0");

			db.execSQL("ALTER TABLE " + TableNames.OrderDetail
					+ " ADD COLUMN weight TEXT");
			db.execSQL("ALTER TABLE " + TableNames.Order
					+ " ADD COLUMN inclusiveTaxName TEXT");
			db.execSQL("ALTER TABLE " + TableNames.Order
					+ " ADD COLUMN inclusiveTaxPrice TEXT");
			db.execSQL("ALTER TABLE " + TableNames.Order
					+ " ADD COLUMN inclusiveTaxPercentage TEXT");
			db.execSQL("ALTER TABLE " + TableNames.OrderSplit
					+ " ADD COLUMN inclusiveTaxName TEXT");
			db.execSQL("ALTER TABLE " + TableNames.OrderSplit
					+ " ADD COLUMN inclusiveTaxPrice TEXT");
			db.execSQL("ALTER TABLE " + TableNames.OrderSplit
					+ " ADD COLUMN  inclusiveTaxPercentage TEXT");
			
			db.execSQL("ALTER TABLE " + TableNames.ReportDaySales
					+ " ADD COLUMN inclusiveTaxAmt TEXT");
			db.execSQL("CREATE TABLE " + TableNames.TempOrder
					+ " (id INTEGER PRIMARY KEY AUTOINCREMENT, appOrderId INTEGER, total TEXT, custId INTEGER, placeOrderTime LONG, status INTEGER)");
			db.execSQL("CREATE TABLE " + TableNames.TempOrderDetail
					+ " (id INTEGER PRIMARY KEY AUTOINCREMENT, orderDetailId INTEGER, appOrderId INTEGER, itemId INTEGER, specialInstractions TEXT, itemCount INTEGER, itemPrice TEXT)");
			db.execSQL("CREATE TABLE " + TableNames.TempModifierDetail
					+ " (id INTEGER PRIMARY KEY AUTOINCREMENT, orderDetailId INTEGER, itemId INTEGER, modifierName TEXT, modifierPrice TEXT)");
			
			db.execSQL("ALTER TABLE " + TableNames.ItemDetail
					+ " ADD COLUMN isDiscount INTEGER default 1");
			db.execSQL("ALTER TABLE " + TableNames.OrderDetail
					+ " ADD COLUMN isItemDiscount INTEGER default 1");
			db.execSQL("ALTER TABLE " + TableNames.TempOrder
					+ " ADD COLUMN sourceType INTEGER");
			db.execSQL("ALTER TABLE " + TableNames.TempOrderDetail
					+ " ADD COLUMN itemName TEXT");
			db.execSQL("CREATE TABLE "
					+ TableNames.AlipaySettlement
					+ "(id INTEGER PRIMARY KEY AUTOINCREMENT,paymentId INTEGER, paymentSettId INTEGER, billNo INTEGER, tradeNo TEXT, buyerEmail TEXT, createTime LONG, updateTime LONG, isActive INTEGER)");
			
			db.execSQL("ALTER TABLE " + TableNames.Order
					+ " ADD COLUMN appOrderId INTEGER default 0");
			db.execSQL("ALTER TABLE " + TableNames.Modifier
					+ " ADD COLUMN itemId INTEGER default 0");
			db.execSQL("ALTER TABLE " + TableNames.OrderModifier
					+ " ADD COLUMN printerId INTEGER default 0");
			db.execSQL("ALTER TABLE " + TableNames.KotItemModifier
					+ " ADD COLUMN printerId INTEGER default 0");
			db.execSQL("ALTER TABLE " + TableNames.TempOrder
					+ " ADD COLUMN paied INTEGER default 0");
			db.execSQL("ALTER TABLE " + TableNames.TempModifierDetail
					+ " ADD COLUMN modifierId INTEGER");
			db.execSQL("ALTER TABLE " + TableNames.Order
					+ " ADD COLUMN isTakeAway INTEGER default 0");
			db.execSQL("ALTER TABLE "
					+ TableNames.KotSummary
					+ " ADD COLUMN isTakeAway INTEGER default 0");			

			db.execSQL("ALTER TABLE "
					+ TableNames.OrderDetailTax
					+ " ADD COLUMN indexId INTEGER default 0");
			
			db.execSQL("ALTER TABLE " 
					+ TableNames.ReportDaySales
					+ " ADD COLUMN alipay TEXT");
			db.execSQL("ALTER TABLE " 
					+ TableNames.ReportDaySales
					+ " ADD COLUMN alipayQty INTEGER");
			db.execSQL("ALTER TABLE " 
					+ TableNames.ReportDaySales
					+ " ADD COLUMN thirdParty TEXT");
			db.execSQL("ALTER TABLE " 
					+ TableNames.ReportDaySales
					+ " ADD COLUMN thirdPartyQty INTEGER");
			
//			db.execSQL("ALTER TABLE "
//					+ TableNames.Tables
//					+ " ADD COLUMN orders INTEGER default 0");

			db.execSQL("ALTER TABLE " 
					+ TableNames.ReportPluDayModifier
					+ " ADD COLUMN comboItemId INTEGER default 0");

			db.execSQL("CREATE TABLE "
					+ TableNames.ReportPluDayComboModifier
					+ "(id INTEGER PRIMARY KEY AUTOINCREMENT, reportNo INTEGER, restaurantId INTEGER, restaurantName TEXT, revenueId INTEGER, revenueName TEXT, businessDate LONG, modifierCategoryId INTEGER, modifierCategoryName TEXT, modifierId INTEGER, modifierName TEXT, modifierPrice TEXT, modifierCount INTEGER, billVoidPrice TEXT, billVoidCount INTEGER, voidModifierPrice TEXT, voidModifierCount INTEGER, bohModifierPrice TEXT, bohModifierCount INTEGER, focModifierPrice TEXT,focModifierCount INTEGER, billFocPrice TEXT, billFocCount INTEGER, comboItemId INTEGER default 0, itemId INTEGER)");
			
			//Print Queue
			db.execSQL("CREATE TABLE "
					+ TableNames.PrintQueue
					+ "(msgUUID TEXT PRIMARY KEY, msgType TEXT, charSize INTEGER, printerIp TEXT, data TEXT, status INTEGER, created LONG, bizDate LONG)");
			
			db.execSQL("ALTER TABLE " 
					+ TableNames.ReportPluDayComboModifier
					+ " ADD COLUMN itemName TEXT");


			// KOT Summary,和order的流水保持一致，用于显示
			db.execSQL("ALTER TABLE " 
					+ TableNames.KotSummary
					+ " ADD COLUMN orderNo INTEGER default 0");

			db.execSQL("update " + TableNames.ItemDetail + " set isActive = 1 where itemType = 2");

			db.execSQL("ALTER TABLE "
					+ TableNames.OrderDetailTax
					+ " ADD COLUMN orderSplitId INTEGER default 0");
			db.execSQL("ALTER TABLE " 
					+ TableNames.OrderDetailTax
					+ " ADD COLUMN isActive INTEGER default 0");
			db.execSQL("CREATE TABLE "
					+ TableNames.WeixinSettlement
					+ "(id INTEGER PRIMARY KEY AUTOINCREMENT,paymentId INTEGER, paymentSettId INTEGER, billNo INTEGER, tradeNo TEXT, buyerEmail TEXT, createTime LONG, updateTime LONG, isActive INTEGER)");
			db.execSQL("ALTER TABLE " 
					+ TableNames.ReportDaySales
					+ " ADD COLUMN weixinpay TEXT");
			db.execSQL("ALTER TABLE " 
					+ TableNames.ReportDaySales
					+ " ADD COLUMN weixinpayQty INTEGER");
			db.execSQL("ALTER TABLE "
					+ TableNames.OrderModifier
					+ " ADD COLUMN modifierItemPrice TEXT default '0.00' ");
			db.execSQL("ALTER TABLE " 
					+ TableNames.ReportPluDayComboModifier
					+ " ADD COLUMN modifierItemPrice TEXT default '0.00' ");
			db.execSQL("ALTER TABLE " 
					+ TableNames.ReportPluDayModifier
					+ " ADD COLUMN modifierItemPrice TEXT default '0.00' ");
			db.execSQL("ALTER TABLE "
					+ TableNames.Modifier
					+ " ADD COLUMN isSet INTEGER default 0");
			db.execSQL("ALTER TABLE " 
					+ TableNames.Modifier
					+ " ADD COLUMN qty INTEGER default 1");
			db.execSQL("ALTER TABLE " 
					+ TableNames.Modifier
					+ " ADD COLUMN mustDefault INTEGER default 0");
			db.execSQL("ALTER TABLE " 
					+ TableNames.Modifier
					+ " ADD COLUMN optionQty INTEGER default 0");
			/*
			 * 只用于本地使用
			 */
			db.execSQL("ALTER TABLE " 
					+ TableNames.OrderDetail
					+ " ADD COLUMN isSet INTEGER default 0");
		}

		private void onUpgradeForOldVersion1(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE "
					+ TableNames.AppOrder
					+ "(id INTEGER PRIMARY KEY AUTOINCREMENT, orderNo INTEGER, custId INTEGER, restId INTEGER, revenueId INTEGER, sourceType INTEGER, tableId INTEGER, orderStatus INTEGER, subTotal TEXT, taxAmount TEXT, discountAmount TEXT, discountType INTEGER, total TEXT, orderCount INTEGER, createTime LONG, updateTime LONG)");
			db.execSQL("CREATE TABLE "
					+ TableNames.AppOrderDetail
					+ "(id INTEGER PRIMARY KEY AUTOINCREMENT, orderId INTEGER, custId INTEGER, itemId INTEGER, itemName TEXT, itemNum INTEGER, itemPrice TEXT, taxPrice TEXT, discountPrice TEXT, discountRate TEXT, realPrice TEXT, orderDetailStatus INTEGER, discountType INTEGER, modifierPrice TEXT, specialInstractions TEXT, createTime LONG, updateTime LONG)");
			db.execSQL("CREATE TABLE "
					+ TableNames.AppOrderModifier
					+ "(id INTEGER PRIMARY KEY AUTOINCREMENT, orderId INTEGER, orderDetailId INTEGER, custId INTEGER, itemId INTEGER, modifierId INTEGER, modifierName TEXT, modifierNum INTEGER, status INTEGER, modifierPrice TEXT, createTime LONG, updateTime LONG)");
			db.execSQL("CREATE TABLE "
					+ TableNames.AppOrderDetailTax
					+ " (id INTEGER PRIMARY KEY AUTOINCREMENT, orderId INTEGER, orderDetailId INTEGER, taxId INTEGER, taxName TEXT, taxPercentage TEXT, taxPrice TEXT, taxType INTEGER, createTime LONG, updateTime LONG)");
			db.execSQL("ALTER TABLE " + TableNames.SyncMsg
					+ " ADD COLUMN appOrderId INTEGER");
			db.execSQL("ALTER TABLE " + TableNames.SyncMsg
					+ " ADD COLUMN orderStatus INTEGER");

		}
		//tableType

		private void onUpgradeForOldVersion2(SQLiteDatabase db) {
			db.execSQL("ALTER TABLE " + TableNames.AppOrder
					+ " ADD COLUMN tableType INTEGER");
			db.execSQL("ALTER TABLE " + TableNames.AppOrder
					+ " ADD COLUMN tableNo TEXT");
		}
		private void onUpgradeForOldVersion3(SQLiteDatabase db) {
			db.execSQL("ALTER TABLE " + TableNames.AppOrder
					+ " ADD COLUMN bizType INTEGER");
			db.execSQL("ALTER TABLE "
					+ TableNames.ReportDaySales
					+ " ADD COLUMN paypalpay TEXT");
			db.execSQL("ALTER TABLE "
					+ TableNames.ReportDaySales
					+ " ADD COLUMN paypalpayQty INTEGER");
		}
		private void onUpgradeForOldVersion4(SQLiteDatabase db) {
			db.execSQL("ALTER TABLE " + TableNames.SyncMsg
					+ " ADD COLUMN orderNum INTEGER");

		}

		private void onUpgradeForOldVersion5(SQLiteDatabase db) {
			db.execSQL("ALTER TABLE " + TableNames.KotSummary
					+ " ADD COLUMN revenueCenterIndex INTEGER default 1");

		}

		private void onUpgradeForOldVersion6(SQLiteDatabase db) {
			db.execSQL("ALTER TABLE " + TableNames.Order
					+ " ADD COLUMN tableName TEXT default '' ");

		}
		private void onUpgradeForOldVersion7(SQLiteDatabase db) {
			/*
			idint(11) NOT NULL
			namevarchar(20) NULL桌子名称
			restaurant_idint(11) NOT NULL餐厅id
			revenue_idint(11) NOT NULL收银中心id
			x_axisint(11) NULLx轴
			y_axisint(11) NULLy轴
			places_idint(11) NOT NULL区域id
			resolutionint(11) NULL分辨率
			pos_idint(11) NOT NULLpos端id
			shapeint(2) NULL大小
			typeint(2) NULL类型
			statusint(5) NOT NULL桌子状态(0空的，1、占用、2正在结账)
			is_decorateint(1) NOT NULL是否装饰,0:否;1:是
			union_idvarchar(50) NOT NULL唯一id restautId_revenueId_posId +
			is_activeint(11) NULL是否可用(-1删除，0禁用，1正常)
			create_timetimestamp NULL创建时间
			update_timetimestamp NULL更新时间
			 */
			db.execSQL("CREATE TABLE "
					+ TableNames.TableInfo
					+ " (posId INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, imageName TEXT, restaurantId INTEGER, revenueId INTEGER, xAxis INTEGER, yAxis INTEGER, placesId INTEGER, resolution INTEGER, shape INTEGER, type INTEGER, status INTEGER, isDecorate INTEGER, unionId TEXT, isActive INTEGER, packs INTEGER, rotate INTEGER, createTime LONG, updateTime LONG, orders INTEGER, isKiosk INTEGER default 0)");
			db.execSQL("CREATE TABLE "
					+ TableNames.PlaceInfo
					+ " (posId INTEGER PRIMARY KEY AUTOINCREMENT, placeName TEXT,placeDescription TEXT,restaurantId INTEGER,  revenueId INTEGER, unionId INTEGER, isActive INTEGER, isKiosk INTEGER default 0)");
			//TableNames.ReportDaySales
			db.execSQL("ALTER TABLE " + TableNames.ReportDaySales
					+ " ADD COLUMN storedCard TEXT default '0.00'");
			db.execSQL("ALTER TABLE " + TableNames.ReportDaySales
					+ " ADD COLUMN storedCardQty INTEGER default 0");
		}
		private void onUpgradeForOldVersion8(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE "
					+ TableNames.ConsumingRecords
					+ " (id INTEGER PRIMARY KEY AUTOINCREMENT, cardId INTEGER, restId INTEGER, staffId INTEGER, consumingType INTEGER, fromType INTEGER, consumingAmount TEXT, consumingTime LONG, businessDate LONG)");
			//TableNames.ReportDaySales
			db.execSQL("ALTER TABLE " + TableNames.ReportDaySales
					+ " ADD COLUMN topUps TEXT default '0.00'");
			db.execSQL("ALTER TABLE " + TableNames.ReportDaySales
					+ " ADD COLUMN topUpsQty INTEGER default 0");
			db.execSQL("ALTER TABLE " + TableNames.ItemDetail
					+ " ADD COLUMN barcode TEXT");

		}

		private void onUpgradeForOldVersion9(SQLiteDatabase db){
			db.execSQL("CREATE TABLE "
					+ TableNames.UserOpenDrawerRecord
					+ " (id INTEGER PRIMARY KEY AUTOINCREMENT, restaurantId INTEGER, revenueCenterId INTEGER, sessionStatus INTEGER, userId INTEGER, userName TEXT, openTime LONG, loginUserId INTEGER )");
			db.execSQL("ALTER TABLE " + TableNames.ReportDaySales
					+ " ADD COLUMN billRefund TEXT default '0.00'");
			db.execSQL("ALTER TABLE " + TableNames.ReportDaySales
					+ " ADD COLUMN billRefundQty INTEGER default 0");
			db.execSQL("ALTER TABLE " + TableNames.ReportDaySales
					+ " ADD COLUMN refundTax TEXT default '0.00'");
			db.execSQL("ALTER TABLE " + TableNames.VoidSettlement
					+ " ADD COLUMN type INTEGER default 0");
			db.execSQL("ALTER TABLE " + TableNames.AppOrderDetail
					+ " ADD COLUMN totalItemPrice TEXT default '0.00'");
			db.execSQL("ALTER TABLE " + TableNames.AppOrder
					+ " ADD COLUMN orderRemark TEXT");
			db.execSQL("ALTER TABLE " + TableNames.AppOrder
					+ " ADD COLUMN eatType INTEGER default 0");
			db.execSQL("ALTER TABLE " + TableNames.AppOrder
					+ " ADD COLUMN payStatus INTEGER default 0");
			db.execSQL("ALTER TABLE " + TableNames.OrderDetail
					+ " ADD COLUMN appOrderDetailId INTEGER default 0");
			db.execSQL("ALTER TABLE " + TableNames.Order
					+ " ADD COLUMN orderRemark TEXT");
			db.execSQL("ALTER TABLE " + TableNames.KotSummary
					+ " ADD COLUMN orderRemark TEXT");
		}
		private void onUpgradeForOldVersion10(SQLiteDatabase db){
			db.execSQL("update " + TableNames.SyncMsg + " set status = " + ParamConst.SYNC_MSG_UN_SEND + " where status = " + ParamConst.SYNC_MSG_MALDATA + " and msg_type <> 1001");
		}

		private void onUpgradeForOldVersion11(SQLiteDatabase db){
			db.execSQL("ALTER TABLE " + TableNames.ReportDaySales
					+ " ADD COLUMN startDrawerAmount TEXT default '0.00'");
			db.execSQL("ALTER TABLE " + TableNames.ReportDaySales
					+ " ADD COLUMN expectedAmount TEXT default '0.00'");
			db.execSQL("ALTER TABLE " + TableNames.ReportDaySales
					+ " ADD COLUMN waiterAmount TEXT default '0.00'");
			db.execSQL("ALTER TABLE " + TableNames.ReportDaySales
					+ " ADD COLUMN difference TEXT default '0.00'");
			db.execSQL("ALTER TABLE " + TableNames.ReportDaySales
					+ " ADD COLUMN cashTopUp TEXT default '0.00'");
			db.execSQL("ALTER TABLE " + TableNames.ConsumingRecords
					+ " ADD COLUMN payTypeId INTEGER default -1");

			db.execSQL("CREATE TABLE "
					+ TableNames.ReportSessionSales
					+ " (id INTEGER PRIMARY KEY AUTOINCREMENT, sessionName TEXT, startDrawer TEXT, cash TEXT, cashTopup TEXT, expectedAmount TEXT, actualAmount TEXT, difference TEXT, businessDate LONG)");
			db.execSQL("ALTER TABLE " + TableNames.OrderDetail
					+ " ADD COLUMN mainCategoryId INTEGER default 0");
			db.execSQL("update " + TableNames.SyncMsg + " set status = " + ParamConst.SYNC_MSG_UN_SEND + " where (status = " + ParamConst.SYNC_MSG_QUEUED + " or status = " + ParamConst.SYNC_MSG_MALDATA + ") and msg_type <> 1001");
			try {
				Store.remove(BaseApplication.instance, Store.PUSH_MESSAGE);
			}catch (Exception e){
				e.printStackTrace();
			}
		}

		private void onUpgradeForOldVersion12(SQLiteDatabase db){
			db.execSQL("ALTER TABLE " + TableNames.ReportDaySales
					+ " ADD COLUMN takeawaySales TEXT default '0.00'");
			db.execSQL("ALTER TABLE " + TableNames.ReportDaySales
					+ " ADD COLUMN takeawayTax TEXT default '0.00'");
			db.execSQL("ALTER TABLE " + TableNames.ReportDaySales
					+ " ADD COLUMN takeawayQty INTEGER default 0");
			db.execSQL("ALTER TABLE " + TableNames.ReportDaySales
					+ " ADD COLUMN createTime LONG");
			db.execSQL("ALTER TABLE " + TableNames.ReportDaySales
					+ " ADD COLUMN updateTime LONG");
			db.execSQL("ALTER TABLE " + TableNames.ReportPluDayModifier
					+ " ADD COLUMN realPrice TEXT default '0.00'");
			db.execSQL("ALTER TABLE " + TableNames.ReportPluDayModifier
					+ " ADD COLUMN realCount INTEGER default 0");
			db.execSQL("ALTER TABLE " + TableNames.ReportPluDayComboModifier
					+ " ADD COLUMN realPrice TEXT default '0.00'");
			db.execSQL("ALTER TABLE " + TableNames.ReportPluDayComboModifier
					+ " ADD COLUMN realCount INTEGER default 0");
			db.execSQL("ALTER TABLE " + TableNames.Order
					+ " ADD COLUMN discountCategoryId TEXT");
		}
		private void onUpgradeForOldVersion13(SQLiteDatabase db){

			db.execSQL("ALTER TABLE " + TableNames.ReportDaySales
					+ " ADD COLUMN deliveroo TEXT default '0.00'");
			db.execSQL("ALTER TABLE " + TableNames.ReportDaySales
					+ " ADD COLUMN deliverooQty INTEGER default 0");
			db.execSQL("ALTER TABLE " + TableNames.ReportDaySales
					+ " ADD COLUMN ubereats TEXT default '0.00'");
			db.execSQL("ALTER TABLE " + TableNames.ReportDaySales
					+ " ADD COLUMN ubereatsQty INTEGER default 0");
			db.execSQL("ALTER TABLE " + TableNames.ReportDaySales
					+ " ADD COLUMN foodpanda TEXT default '0.00'");
			db.execSQL("ALTER TABLE " + TableNames.ReportDaySales
					+ " ADD COLUMN foodpandaQty INTEGER default 0");
			db.execSQL("ALTER TABLE " + TableNames.ReportDaySales
					+ " ADD COLUMN voucher TEXT default '0.00'");
			db.execSQL("ALTER TABLE " + TableNames.ReportDaySales
					+ " ADD COLUMN voucherQty INTEGER default 0");
		}
		private void onUpgradeForOldVersion14(SQLiteDatabase db){

			db.execSQL("ALTER TABLE " + TableNames.TableInfo
					+ " ADD COLUMN resolutionWidth INTEGER");
			db.execSQL("ALTER TABLE " + TableNames.TableInfo
					+ " ADD COLUMN resolutionHeight INTEGER");
		}
	}
}
