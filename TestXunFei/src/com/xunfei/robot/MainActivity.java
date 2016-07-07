package com.xunfei.robot;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.xunfei.robot.utils.BackgroundCache;
import com.xunfei.robot.utils.SongUtils;
import com.xunfei.robot.utils.BackgroundCache.Callback;

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
		SongUtils.playSong(this);
	}

	public void onButtonClick(View view) {
		switch (view.getId()) {
		case R.id.button1:
			this.startService(new Intent(this, VoicesToTextService.class));
			// this.startService(new Intent(this, TalkService.class));
			// this.startService(new Intent(this,TextToVoicesService.class));
			break;
		case R.id.button2:
			this.stopService(new Intent(this, VoicesToTextService.class));
			// this.stopService(new Intent(this, TalkService.class));
			// this.stopService(new Intent(this,TextToVoicesService.class));
			break;
		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		this.stopService(new Intent(this, VoicesToTextService.class));
	}
}
