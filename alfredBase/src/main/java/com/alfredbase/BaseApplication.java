package com.alfredbase;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;

import com.alfredbase.utils.LogUtil;
import com.alfredbase.utils.RxBus;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BaseApplication extends Application {

	public static BaseApplication instance;
	public static List<BaseActivity> activitys;
	/**
	 * 注意 
	 * 当 isDebug == false， isOpenLog == false 为正式服务器，地区服务器通过地区代码表示 SINGAPORE亚马逊 CHINA阿里
	 * 当 isDebug == false， isOpenLog == true 为阿里云服务器
	 * 当 isDebug == true， isOpenLog == true 为本地的服务器
	 */

	public static boolean isDebug = false;	//	Debug开关 release的时候设置为false
	public static boolean isOpenLog = false;	//	release 时设置为false



	private Handler reLoginHandler = new Handler();
	/**x
	 * 国家电话代码
	 * 用于区别不通过的代码逻辑
	 */
	public static int countryCode = ParamConst.SINGAPORE ;
	
	public static boolean sub_thread_running = true;
	/**
	 * 暂时做的假的version 给Singapore的人辨别使用的
	 */
//	public static String fake_version = "0.1.5";
	//network
	public boolean network_connected = false;
    public String ipaddress_str = "";
    public int ipaddress_int = 0;
    
    public String wifi_mac = "";
    public String wifi_ssid = "";
    public int wifissi = 0;
    
    /** ImageLoader
     *  ImageLoader from https://github.com/nostra13/Android-Universal-Image-Loader
     *  @author Alex
     */
    private static final String IMAGE_ENGIN_PATH = "alfred/cache"; // 图片持久化的位置(本地sdcard 和机身ROM)
	public static int IMAGE_ENGINE_CORETHREAD = 5; // 可以同步加载图片的个数
	public static int IMAGE_ENGINE_COMPRESS_QUALITY = 100; // 对图片的压缩比例0~100数值约大越清晰，越小越模糊
															// Can slow
															// ImageLoader, use
															// it carefully
															// (Better don't use
															// it)
	public static int IMAGE_ENGINE_FREQ_LIMITED_MEMECACHE = 4 * 1024 * 1024; // 设置缓存内存的大小,在此设置2M的缓存
	public static int IMAGE_DISK_CACHE_SIZE = 80 * 1024 * 1024;	//	总共的存储的空间
	public static int IMAGE_ENGINE_SCALE_FOR_SDCARD = 1000; // 缓存图片的大小
	public static int IMAGE_ENGINE_SCALE_FOR_MEMECACHE = 1000;
	
	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		activitys = new ArrayList<BaseActivity>();
		MobclickAgent.setDebugMode(isDebug);
		this.registerReceiver(this.myWifiReceiver, 
	    		   new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
	  
	    this.registerReceiver(this.myRssiChangeReceiver, 
	    		    new IntentFilter(WifiManager.RSSI_CHANGED_ACTION));
	    
	    initImageLoader(instance);
	    if(isOpenLog)
	    	MobclickAgent.setCatchUncaughtExceptions(!isOpenLog);

		String device_model = Build.MODEL; // 设备型号 。
		String version_sdk = Build.VERSION.SDK; // 设备SDK版本  。
		String version_release = Build.VERSION.RELEASE; // 设备的系统版本 。
		String brand = Build.BRAND;
		String Manufacturer = Build.MANUFACTURER;
		String priduct = Build.PRODUCT;
		String fingerprint = Build.FINGERPRINT;
		String serial = Build.SERIAL;

		System.out.println("device_model*******" + device_model);
		System.out.println("version_sdk*******" + version_sdk);
		System.out.println("version_release*******" + version_release);
		System.out.println("brand*******" + brand);
		System.out.println("Manufacturer*******" + Manufacturer);
		System.out.println("priduct*******" + priduct);

		System.out.println("fingerprint*******" + fingerprint);
		System.out.println("serial*******" + serial);
	}


	private Runnable runnable = new Runnable() {
		@Override
		public void run() {
			LogUtil.d("BaseActivity", "show");
			RxBus.getInstance().post("showRelogin", null);
		}
	};
	public void startAD() {
		LogUtil.d("BaseActivity", "Remove");
		reLoginHandler.removeCallbacks(runnable);
		LogUtil.d("BaseActivity", "Start");
		reLoginHandler.postDelayed(runnable, 5*60*1000);
	}

	public void stopAD(){
		reLoginHandler.removeCallbacks(runnable);
	}

	@Override
	public void onTerminate() {
		reLoginHandler.removeCallbacks(runnable);
		super.onTerminate();
	}

	public String getAppVersionName() {
  		PackageInfo info;
		String version = "";
  		try {
			info = getPackageManager().getPackageInfo(this.getPackageName(), 0);
			version = info.versionName;
		} catch (NameNotFoundException e1) {
			e1.printStackTrace();
		}
  		return version;
	}

	public int getAppVersionCode() {
		PackageInfo info;
		int version = 1000;
		try {
			info = getPackageManager().getPackageInfo(this.getPackageName(), 0);
			version = info.versionCode;
		} catch (NameNotFoundException e1) {
			e1.printStackTrace();
		}
		return version;
	}
	
	public static void initImageLoader(Context context) {
		File cacheDir = StorageUtils.getOwnCacheDirectory(context, IMAGE_ENGIN_PATH); // 图片持久化位置
		//	部分属性定制 其他都是默认
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
				//	设置缓存图片的时候最大宽高
				.memoryCacheExtraOptions(IMAGE_ENGINE_SCALE_FOR_SDCARD, IMAGE_ENGINE_SCALE_FOR_SDCARD)
				.diskCacheExtraOptions(IMAGE_ENGINE_SCALE_FOR_SDCARD, IMAGE_ENGINE_SCALE_FOR_SDCARD, null)
				// 设置线程优先级
				.denyCacheImageMultipleSizesInMemory()
				//	自动缩放的大小
				.memoryCache(new UsingFreqLimitedMemoryCache(IMAGE_ENGINE_FREQ_LIMITED_MEMECACHE))
				.memoryCacheSize(IMAGE_ENGINE_FREQ_LIMITED_MEMECACHE)
				//	设置硬盘缓存大小  80 Mb
				.diskCache(new UnlimitedDiscCache(cacheDir))
				.diskCacheSize(IMAGE_DISK_CACHE_SIZE)
				// Remove for release app
				.writeDebugLogs() 
				.build();
		ImageLoader.getInstance().init(config);
	}

	private static Handler APPHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case HandlerWhat.NO_NET: {
				BaseActivity baseActivity = getTopActivity();
				if (baseActivity != null) {

				}

				break;
			}
			case ParamConst.APP_QUIT:{
				
			  }
			  break;
			default:
				break;
			}
		};
	};

	public static void sendMessage(int what, Object obj) {
		APPHandler.sendMessage(APPHandler.obtainMessage(what, obj));
	}

	public static BaseActivity getTopActivity() {
		if (activitys == null)
			return null;
		if (activitys.size() == 0)
			return null;
		return activitys.get(activitys.size() - 1);
	}
	
	protected void onAppQuit() {
		
	}
	/*Fire IP Change Event*/
	protected void onIPChanged() {
		
		
	}
	
	/*Connected or Discounted*/
	protected void onNetworkConnectionUpdate () {
		
		
	}

	public static BaseActivity getTopSecondActivity(){
		if (activitys == null)
			return null;
		if (activitys.size() == 0)
			return null;
		return activitys.get(activitys.size() - 2);
	}
	
	public static BaseActivity getLastActivity(){
		if (activitys == null)
			return null;
		if (activitys.size() == 0)
			return null;
		return activitys.get(0);
	}
	
	private  BroadcastReceiver myRssiChangeReceiver  = new BroadcastReceiver(){
			
		 @Override
		 public void onReceive(Context arg0, Intent arg1) {
		    wifissi = arg1.getIntExtra(WifiManager.EXTRA_NEW_RSSI, 0);
		 }
	};

	private  BroadcastReceiver myWifiReceiver = new BroadcastReceiver(){

		@Override
		 public void onReceive(Context arg0, Intent arg1) {
		  ConnectivityManager myConnManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
	      NetworkInfo networkInfo = myConnManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);			
		  if((networkInfo.getType() == ConnectivityManager.TYPE_WIFI) &&
				  networkInfo.isConnected()){
			  onIPChanged();
		  }
		 }
	};
	
	/**
	 * 关闭 ActivityList用 慎用（必须确定目标activity在栈里） 且用，且珍惜！！
	 * added by Alex, 2014-8-25
	 * @param cls 关闭到当前activity为止(从栈顶)
	 */
	public void popAllActivityExceptOne(Class cls) {
        while (true) {
            BaseActivity currentActivity = getTopActivity();
            if (currentActivity == null) {
                break;
            }
            if (currentActivity.getClass().equals(cls)) {
                break;
            }
            currentActivity.finish();
            activitys.remove(currentActivity);
        }
    }
	
	/**
	 * 关闭 ActivityList用 慎用（必须确定目标activity在栈里） 且用，且珍惜！！
	 * added by Alex, 2014-8-25
	 * @param cls 关闭到当前activity为止(从栈底)
	 */
	public void finishAllActivityExceptOne(Class cls) {
        while (true) {
            BaseActivity oldActivity = activitys.get(0);
            if (oldActivity == null) {
                break;
            }
            if (oldActivity.getClass().equals(cls)) {
                break;
            }
            oldActivity.finish();
            activitys.remove(oldActivity);
        }
    }
	
	
	public void finishAllActivity() {
        while (true) {
            BaseActivity oldActivity = activitys.get(0);
            if (oldActivity == null) {
                break;
            }
            oldActivity.finish();
            activitys.remove(oldActivity);
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }
	/**
	 * PS: return -1时 activity不在栈中
	 * @param cls
	 * @return
	 */
	public int getIndexOfActivity(Class cls){
		for(BaseActivity baseActivity : activitys){
			if(baseActivity.getClass().equals(cls)){
				return activitys.indexOf(baseActivity);
			}
		}
		return -1;
	}
}
