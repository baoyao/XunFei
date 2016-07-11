package com.xunfei.robot;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xunfei.robot.utils.BackgroundCache;
import com.xunfei.robot.utils.BackgroundCache.Callback;
import com.xunfei.robot.utils.BackgroundCache.Mode;
import com.xunfei.robot.utils.Config;
import com.xunfei.robot.utils.ForwardControl;

public class MainActivity extends Activity {

	private TextView txt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		txt = (TextView) findViewById(R.id.txt);
		BackgroundCache.getInstance().setCallback(new Callback() {
			@Override
			public void onCallback(String result) {
				// TODO Auto-generated method stub
				String str = BackgroundCache.getInstance()
						.getRequestResult();
				txt.setText(str);
			}

		});
		// this.startActivity(new Intent(this, IatDemo.class));
		
		if(Config.DEBUG){
			findViewById(R.id.button11).setVisibility(View.VISIBLE);
			findViewById(R.id.button22).setVisibility(View.VISIBLE);
			((Button)findViewById(R.id.button1)).setText("next support service");
			((Button)findViewById(R.id.button2)).setText("next unsupport service");
			ForwardControl.getInstance(this).startTalkService(Mode.PEOPLE, Config.TEST_MESSAGE);
		}
	}

	private int testCount1=-1;
	private int testCount2=-1;

	public void onButtonClick(View view) {
		switch (view.getId()) {
		case R.id.button11:
			ForwardControl.getInstance(this).startTalkService(Mode.PEOPLE, Config.SUPPORT_SERVICE[testCount1==-1?0:testCount1]);
			break;
		case R.id.button22:
			ForwardControl.getInstance(this).startTalkService(Mode.PEOPLE, Config.UNSUPPORT_SERVICE[testCount2==-1?0:testCount2]);
			break;
		case R.id.button1:
			if(Config.DEBUG){
				testCount1++;
				ForwardControl.getInstance(this).startTalkService(Mode.PEOPLE, Config.SUPPORT_SERVICE[testCount1]);
				if(testCount1>=(Config.SUPPORT_SERVICE.length-1)){
					testCount1=-1;
				}
			}else{
				ForwardControl.getInstance(this).startVoicesToTextService();
			}
			break;
		case R.id.button2:
			if(Config.DEBUG){
				testCount2++;
				ForwardControl.getInstance(this).startTalkService(Mode.PEOPLE, Config.UNSUPPORT_SERVICE[testCount2]);
				if(testCount2>=(Config.SUPPORT_SERVICE.length-1)){
					testCount2=-1;
				}
			}else{
				ForwardControl.getInstance(this).stopVoicesToTextService();
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
		ForwardControl.getInstance(this).stopVoicesToTextService();
		if(Config.DEBUG){
			ForwardControl.getInstance(this).stopTalkService();
		}
	}
}
