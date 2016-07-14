package com.xunfei.robot;

import com.xunfei.robot.utils.RecordUtils;
import com.xunfei.robot.utils.Config;
import com.xunfei.robot.utils.RecordUtils.Mode;

import android.content.Context;
import android.content.Intent;

public class VoicesManager {
	
	private static VoicesManager mVoicesManager;
	private static Context mContext;
	private static VoicesToText mVoicesToText;
	private static TextToText mTextToText;
	private static TextToVoices mTextToVoices;
	
	private VoicesManager() {}
	
	public static VoicesManager getInstance(Context context){
		if(mVoicesManager==null){
			mVoicesManager=new VoicesManager();
			mContext=context;

			mVoicesToText = new VoicesToText(context);
			mTextToText = new TextToText(context);
			mTextToVoices = new TextToVoices(context);
		}
		return mVoicesManager;
	}
	
	public void onStart(){
		mVoicesToText.start();
	}

	/**入口*/
	public void startVoicesToText(){
		mVoicesToText.start();
	}
	

	public void startTextToText(RecordUtils.Mode mode,String text){
		RecordUtils.getInstance().setResult(mode, text);
		mTextToText.start();
	}
	
	public void startTextToVoices(RecordUtils.Mode mode,String text){
		RecordUtils.getInstance().setResult(mode,text);
		if(Config.DEBUG){
			return;
		}
		mTextToVoices.start();
	}
	
	
	public VoicesToText getmVoicesToText() {
		return mVoicesToText;
	}

	public void onDestroy() {
		mVoicesManager = null;
		mTextToVoices.onDestroy();
		mTextToText.onDestroy();
		mVoicesToText.onDestroy();
	}

}
