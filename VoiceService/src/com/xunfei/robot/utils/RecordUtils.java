package com.xunfei.robot.utils;

import android.util.Log;

/**
 * @author houen.bao
 * @date Jul 6, 2016 1:44:44 PM
 */
public class RecordUtils {

	private static final String TAG="tt";
	private static RecordUtils mBackgroundCache;
	private Callback mCallback;
	private String mResult;
	private static String requestResult="";

	private RecordUtils() {
	}

	public static RecordUtils getInstance() {
		if (mBackgroundCache == null) {
			mBackgroundCache = new RecordUtils();
			requestResult="";
		}
		return mBackgroundCache;
	}
	
	public static enum Mode{
		ROBOT,
		PEOPLE
	}

	public void setCallback(Callback callback) {
		mCallback = callback;
		requestResult="";
	}

	public void setResult(Mode mode,String result) {
		Log.v(TAG, "BackgroundCache setResult: "+result);
		mResult = result;
		if(mode == Mode.PEOPLE){
			requestResult+="me: "+result+"\n";
		}else if(mode == Mode.ROBOT){
			requestResult+="robot: "+result+"\n";
		}
		if(mCallback!=null){
			mCallback.onCallback(result);
		}
	}
	
	public String getResult(){
		return mResult;
	}
	
	public String getRequestResult(){
		return requestResult;
	}

	public interface Callback {
		public void onCallback(String result);
	}

}
