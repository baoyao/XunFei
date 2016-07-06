package com.xunfei.robot;

import org.json.JSONObject;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUnderstander;
import com.iflytek.cloud.TextUnderstander;
import com.iflytek.cloud.TextUnderstanderListener;
import com.iflytek.cloud.UnderstanderResult;
import com.xunfei.robot.tools.UnderstanderSettings;
import com.xunfei.robot.utils.BackgroundCache;
import com.xunfei.robot.utils.Config;

/**
 * @author houen.bao
 * @date Jul 6, 2016 10:07:56 AM
 */
public class TalkService extends Service {

	private static String TAG = "tt";

	// 语义理解对象（语音到语义）。
	private SpeechUnderstander mSpeechUnderstander;
	// 语义理解对象（文本到语义）。
	private TextUnderstander mTextUnderstander;
	private Toast mToast;

	private SharedPreferences mSharedPreferences;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

		// 初始化对象
		mSpeechUnderstander = SpeechUnderstander.createUnderstander(this,
				speechUnderstanderListener);
		mTextUnderstander = TextUnderstander.createTextUnderstander(this,
				textUnderstanderListener);

		mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
		mSharedPreferences = getSharedPreferences(UnderstanderSettings.PREFER_NAME, Activity.MODE_PRIVATE);
		
		mHandler.sendEmptyMessageDelayed(1, Config.WAITING_TIME);
	}

	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			startUnderstander();
		}
	};
	
	int ret = 0;// 函数调用返回值
	private String text="你是谁";//深圳明天天气怎么样？
	private String mResult="";
	
	/**文字理解*/
	private void startUnderstander(){
		text = BackgroundCache.getInstance().getResult();
		showTip(text);
		if(mTextUnderstander.isUnderstanding()){
			mTextUnderstander.cancel();
			showTip("取消");
		}else {
			ret = mTextUnderstander.understandText(text, textListener);
			if(ret != 0)
			{
				showTip("语义理解失败,错误码:"+ ret);
			}
		}
	}
	
	private void setResult(String result){
		mResult=analyzeResult(result);
		BackgroundCache.getInstance().setResult(BackgroundCache.Mode.ROBOT,mResult);
		if("".equals(mResult)){
			mResult = "很抱歉，没有识别出来";
		}
		startService(new Intent(this,TextToVoicesService.class));
	}
	
	private String analyzeResult(String result){
		try{
			JSONObject root=new JSONObject(result);
			JSONObject answer=root.getJSONObject("answer");
			String text=answer.getString("text");
			return text;
		}catch(Exception e){
			e.printStackTrace();
		}
		return "";
	}

	
	private TextUnderstanderListener textListener = new TextUnderstanderListener() {
		
		@Override
		public void onResult(final UnderstanderResult result) {
	       	mHandler.post(new Runnable() {
					@Override
					public void run() {
						if (null != result) {
			            	// 显示
							Log.d(TAG, "understander result：" + result.getResultString());
							String text = result.getResultString();
							if (!TextUtils.isEmpty(text)) {
								setResult(text);
								showTip(text);
							}
			            } else {
			                Log.d(TAG, "understander result:null");
			                showTip("识别结果不正确。");
			            }
					}
				});
		}
		
		@Override
		public void onError(SpeechError error) {
			// 文本语义不能使用回调错误码14002，请确认您下载sdk时是否勾选语义场景和私有语义的发布
			showTip("onError Code："	+ error.getErrorCode());
			
		}
	};

	/**
	 * 参数设置
	 * @param param
	 * @return 
	 */
	public void setParam(){
		String lag = mSharedPreferences.getString("understander_language_preference", "mandarin");
		if (lag.equals("en_us")) {
			// 设置语言
			mSpeechUnderstander.setParameter(SpeechConstant.LANGUAGE, "en_us");
		}else {
			// 设置语言
			mSpeechUnderstander.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
			// 设置语言区域
			mSpeechUnderstander.setParameter(SpeechConstant.ACCENT,lag);
		}
		// 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
		mSpeechUnderstander.setParameter(SpeechConstant.VAD_BOS, mSharedPreferences.getString("understander_vadbos_preference", "4000"));
		
		// 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
		mSpeechUnderstander.setParameter(SpeechConstant.VAD_EOS, mSharedPreferences.getString("understander_vadeos_preference", "1000"));
		
		// 设置标点符号，默认：1（有标点）
		mSpeechUnderstander.setParameter(SpeechConstant.ASR_PTT, mSharedPreferences.getString("understander_punc_preference", "1"));
		
		// 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
		// 注：AUDIO_FORMAT参数语记需要更新版本才能生效
		mSpeechUnderstander.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
		mSpeechUnderstander.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/msc/sud.wav");
	}	

    /**
     * 初始化监听器（语音到语义）。
     */
    private InitListener speechUnderstanderListener = new InitListener() {
		@Override
		public void onInit(int code) {
			Log.d(TAG, "speechUnderstanderListener init() code = " + code);
			if (code != ErrorCode.SUCCESS) {
        		showTip("初始化失败,错误码："+code);
        	}		
		}
    };
    
    /**
     * 初始化监听器（文本到语义）。
     */
    private InitListener textUnderstanderListener = new InitListener() {

		@Override
		public void onInit(int code) {
			Log.d(TAG, "textUnderstanderListener init() code = " + code);
			if (code != ErrorCode.SUCCESS) {
        		showTip("初始化失败,错误码："+code);
        	}	
		}
    };

    
	private void showTip(final String str)
	{
		Log.v(TAG,"showTip: "+str);
		mHandler.post(new Runnable(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mToast.setText(str);
				mToast.show();
			}
		});
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
        // 退出时释放连接
    	mSpeechUnderstander.cancel();
    	mSpeechUnderstander.destroy();
    	if(mTextUnderstander.isUnderstanding())
    		mTextUnderstander.cancel();
    	mTextUnderstander.destroy();
    	stopService(new Intent(this,TextToVoicesService.class));
	}

}
