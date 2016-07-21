package com.robot.voice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * @author houen.bao
 * @date Jul 12, 2016 10:04:45 AM
 */
public class MainService extends Service {
	
	private VoicesManager mVoicesManager;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mVoicesManager = VoicesManager.getInstance(this);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		mVoicesManager.onStart();
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mVoicesManager.onDestroy();
	}
	

}
