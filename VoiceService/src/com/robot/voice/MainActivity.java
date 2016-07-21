package com.robot.voice;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.robot.voice.utils.Config;
import com.robot.voice.utils.RecordUtils;
import com.robot.voice.utils.RecordUtils.Callback;

public class MainActivity extends Activity {

	private Handler mHandler;
	private TextView txt;
	private ScrollView mScrollView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mHandler = new Handler();
		txt = (TextView) findViewById(R.id.txt);
		mScrollView = (ScrollView) findViewById(R.id.scrollview);
		RecordUtils.getInstance().setCallback(new Callback() {
			@Override
			public void onCallback(String result) {
				// TODO Auto-generated method stub
				String str = RecordUtils.getInstance().getRequestResult();
				txt.setText(str);
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
					}
				});
			}
		});
		// this.startActivity(new Intent(this, IatDemo.class));
		
		if(Config.DEBUG){
			findViewById(R.id.button11).setVisibility(View.VISIBLE);
			findViewById(R.id.button22).setVisibility(View.VISIBLE);
			((Button)findViewById(R.id.button1)).setText("next support service");
			((Button)findViewById(R.id.button2)).setText("next unsupport service");
			
//			VoicesManager.getInstance(this).startTextToText(Mode.PEOPLE, Config.TEST_MESSAGE);
			VoicesManager.getInstance(this).getmVoicesToText().setResult(Config.TEST_MESSAGE);
		}
	}

	private int testCount1=-1;
	private int testCount2=-1;

	public void onButtonClick(View view) {
		switch (view.getId()) {
		case R.id.button11:
			VoicesManager.getInstance(this).getmVoicesToText().setResult(Config.SUPPORT_SERVICE[testCount1==-1?0:testCount1]);
			break;
		case R.id.button22:
			VoicesManager.getInstance(this).getmVoicesToText().setResult(Config.UNSUPPORT_SERVICE[testCount2==-1?0:testCount2]);
			break;
		case R.id.button1:
			if(Config.DEBUG){
				testCount1++;
				if(testCount1>=Config.SUPPORT_SERVICE.length){
					testCount1=0;
				}
				VoicesManager.getInstance(this).getmVoicesToText().setResult(Config.SUPPORT_SERVICE[testCount1]);
			}else{
				VoicesManager.getInstance(this).startVoicesToText();
			}
			break;
		case R.id.button2:
			if(Config.DEBUG){
				testCount2++;
				if(testCount2>=Config.SUPPORT_SERVICE.length){
					testCount2=0;
				}
				VoicesManager.getInstance(this).getmVoicesToText().setResult(Config.UNSUPPORT_SERVICE[testCount2]);
			}else{
				VoicesManager.getInstance(this).onDestroy();
			}
			break;
		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		VoicesManager.getInstance(this).onDestroy();
	}
}
