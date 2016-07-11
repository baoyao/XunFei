package com.xunfei.robot;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
			ForwardControl.getInstance(this).startTalkService(Mode.PEOPLE, Config.TEST_MESSAGE);
		}
	}

	public void onButtonClick(View view) {
		switch (view.getId()) {
		case R.id.button1:
			ForwardControl.getInstance(this).startVoicesToTextService();
			break;
		case R.id.button2:
			ForwardControl.getInstance(this).stopVoicesToTextService();
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
