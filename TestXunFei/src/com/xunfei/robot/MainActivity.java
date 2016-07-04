package com.xunfei.robot;

import com.xunfei.robot.tools.IatDemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		this.startActivity(new Intent(this,IatDemo.class));
		this.finish();
	}
}
