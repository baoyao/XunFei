package com.xunfei.robot.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

/**
 * @author houen.bao
 * @date Jul 7, 2016 5:52:38 PM
 */
public class OpenAppUtils {
	

	private static final String TAG = "tt";
	private static Context mContext;
	
	private static List<ApplicationInfo> mAppInfoList;
	private static PackageManager mPm;
	
	private static OpenAppUtils mOpenAppUtils;

	private OpenAppUtils(Context context) {
		mContext = context;
	}
	
	public static OpenAppUtils getInstance(Context context){
		if(mOpenAppUtils==null){
			mOpenAppUtils=new OpenAppUtils(context);
			mAppInfoList = getInstallAppInfo();
			mPm = mContext.getPackageManager();
		}
		return mOpenAppUtils;
	}
	
	public static void openApp(String appName){
		for(ApplicationInfo app : mAppInfoList){
			if(app.loadLabel(mPm).toString().contains(appName)){
				Intent intent=mPm.getLaunchIntentForPackage(app.packageName);
				mContext.startActivity(intent);
				break;
			}
		}
	}
	
	
	private static List<ApplicationInfo> getInstallAppInfo() {
		PackageManager mypm = mContext.getPackageManager();
		List<ApplicationInfo> appInfoList = mypm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
		Collections.sort(appInfoList, new ApplicationInfo.DisplayNameComparator(mypm));// 排序  
		
//		for(ApplicationInfo app: appInfoList) {
//			//Log.v(LogTag, "RunningAppInfoParam  getInstallAppInfo app label = " + (String)app.loadLabel(umpm));
//			//Log.v(LogTag, "RunningAppInfoParam  getInstallAppInfo app packageName = " + app.packageName);
//		}
		
		return appInfoList;
	}
	
	//获取第三方应用信息
	private List<ApplicationInfo> getThirdAppInfo() {
		List<ApplicationInfo> appList = getInstallAppInfo();
		List<ApplicationInfo> thirdAppList = new ArrayList<ApplicationInfo>();
		thirdAppList.clear();
		for (ApplicationInfo app : appList) {  
            //非系统程序  
            if ((app.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {  
            	thirdAppList.add(app);
            }   
            //本来是系统程序，被用户手动更新后，该系统程序也成为第三方应用程序了  
            else if ((app.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0){  
            	thirdAppList.add(app);
            }  
        }  
		return thirdAppList;
	}
	
	//获取系统应用信息
	private List<ApplicationInfo> getSystemAppInfo() {
		List<ApplicationInfo> appList = getInstallAppInfo();
		List<ApplicationInfo> sysAppList = new ArrayList<ApplicationInfo>();
		sysAppList.clear();
		for (ApplicationInfo app : appList) {  
			if ((app.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {  
				sysAppList.add(app);
            }  
		}
//		PackageManager mypm = mContext.getPackageManager();
//		ArrayList<String> sysAppNameList = new ArrayList<String>();
//		for(ApplicationInfo app : sysAppList) {
//			Log.v(TAG, "RunningAppInfoParam getThirdAppInfo app label = " + (String)app.loadLabel(mypm));
//			sysAppNameList.add((String)app.loadLabel(mypm));
//		}
		
		return sysAppList;
		
	}
	
}
