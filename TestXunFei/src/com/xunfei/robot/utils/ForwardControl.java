package com.xunfei.robot.utils;

import com.xunfei.robot.TalkService;
import com.xunfei.robot.TextToVoicesService;
import com.xunfei.robot.VoicesToTextService;

import android.content.Context;
import android.content.Intent;

public class ForwardControl {
	
	private static ForwardControl mForwardControl;
	private static Context mContext;
	
	public static ForwardControl getInstance(Context context){
		if(mForwardControl==null){
			mForwardControl=new ForwardControl();
			mContext=context;
		}
		return mForwardControl;
	}
	
	private ForwardControl() {}

	/**入口*/
	public void startVoicesToTextService(){
		mContext.startService(new Intent(mContext, VoicesToTextService.class));
	}
	
	public void stopVoicesToTextService(){
		mContext.stopService(new Intent(mContext, VoicesToTextService.class));
	}


	public void startTalkService(BackgroundCache.Mode mode,String text){
		BackgroundCache.getInstance().setResult(mode, text);
		mContext.startService(new Intent(mContext, TalkService.class));
	}
	
	public void stopTalkService(){
		mContext.stopService(new Intent(mContext, TalkService.class));
	}

	public void startTextToVoicesService(BackgroundCache.Mode mode,String text){
		BackgroundCache.getInstance().setResult(mode,text);
		mContext.startService(new Intent(mContext, TextToVoicesService.class));
	}

	public void stopTextToVoicesService(){
		mContext.stopService(new Intent(mContext, TextToVoicesService.class));
	}
	

}
