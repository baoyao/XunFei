package com.xunfei.robot.utils;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

/**
 * @author houen.bao
 * @date Jul 6, 2016 1:44:44 PM
 */
public class BackgroundCache {

	private static final String TAG="tt";
	private static BackgroundCache mBackgroundCache;
	private Callback mCallback;
	private String mResult;
	private static List<String> peopleResult=new ArrayList<String>();
	private static List<String> robotResult=new ArrayList<String>();

	private BackgroundCache() {
	}

	public static BackgroundCache getInstance() {
		if (mBackgroundCache == null) {
			mBackgroundCache = new BackgroundCache();
			peopleResult.clear();
			robotResult.clear();
		}
		return mBackgroundCache;
	}
	
	public static enum Mode{
		ROBOT,
		PEOPLE
	}

	public void setCallback(Callback callback) {
		mCallback = callback;
		peopleResult.clear();
		robotResult.clear();
	}

	public void setResult(Mode mode,String result) {
		Log.v(TAG, "BackgroundCache setResult: "+result);
		mResult = result;
		if(mode == Mode.PEOPLE){
			peopleResult.add(result);
		}else if(mode == Mode.ROBOT){
			robotResult.add(result);
		}
		if(mCallback!=null){
			mCallback.onCallback(result);
		}
	}
	
	public String getResult(){
		return mResult;
	}
	
	public List<String> getAllResult(){
		List<String> tempResult=new ArrayList<String>();
		for(int i=0;i<peopleResult.size();i++){
			tempResult.add(peopleResult.get(i));
			if(robotResult.size()==peopleResult.size()){
				tempResult.add(robotResult.get(i));
			}
		}
		return tempResult;
	}

	public List<String> getPeopleResult(){
		return peopleResult;
	}

	public List<String> getRobotResult(){
		return robotResult;
	}

	public interface Callback {
		public void onCallback(String result);
	}

}
