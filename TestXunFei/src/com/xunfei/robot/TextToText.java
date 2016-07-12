package com.xunfei.robot;

import java.util.Random;

import org.json.JSONObject;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
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
import com.xunfei.robot.entity.ResultAction;
import com.xunfei.robot.tools.UnderstanderSettings;
import com.xunfei.robot.utils.RecordUtils;
import com.xunfei.robot.utils.RecordUtils.Mode;
import com.xunfei.robot.utils.AnalyzeResultUtils;
import com.xunfei.robot.utils.Config;

/**
 * @author houen.bao
 * @date Jul 6, 2016 10:07:56 AM
 */
public class TextToText{

	private static String TAG = "tt";

	// 语义理解对象（语音到语义）。
	private SpeechUnderstander mSpeechUnderstander;
	// 语义理解对象（文本到语义）。
	private TextUnderstander mTextUnderstander;
	private Toast mToast;

	private SharedPreferences mSharedPreferences;

	private Context mContext;
	
	public TextToText(Context context){
		mContext = context;
		// 初始化对象
		mSpeechUnderstander = SpeechUnderstander.createUnderstander(mContext,
				speechUnderstanderListener);
		mTextUnderstander = TextUnderstander.createTextUnderstander(mContext,
				textUnderstanderListener);

		mToast = Toast.makeText(mContext, "", Toast.LENGTH_SHORT);
		mSharedPreferences = mContext.getSharedPreferences(UnderstanderSettings.PREFER_NAME, Activity.MODE_PRIVATE);
	}

	private Handler mHandler = new Handler();
	
	int ret = 0;// 函数调用返回值
	private String text="唱首歌";//深圳明天天气怎么样？
	private String mResult="";
	
	/**文字理解*/
	public void start(){
		text = RecordUtils.getInstance().getResult();
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
	
	private void setResult(String result) {
		ResultAction ra = AnalyzeResultUtils.getInstance(mContext).analyzeResult(result);
		if (!ra.isIntercept()) {
			forward(ra.getResult());
		}else if(ra.isShowErrorMessage()){
			doErrorMessage();
		}
	}
	
	private String[] errorMess=new String[]{"知之为知之,不知为不知,是不知也","这种问题我怎么可能知道呢",
			"很抱歉，不能回答您的这个问题","我以为我什么问题都知道了，除了您这个"};
	private void doErrorMessage(){
		int index = new Random().nextInt(errorMess.length);
		forward(errorMess[index]);
	}
	
	private void forward(String text){
		mResult=text;
		VoicesManager.getInstance(mContext).startTextToVoices(Mode.ROBOT, mResult);
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
								showTip("result: "+text);
							}
			            } else {
			                Log.d(TAG, "understander result:null");
			                showTip("识别结果不正确。");
			                doErrorMessage();
			            }
					}
				});
		}
		
		@Override
		public void onError(SpeechError error) {
			// 文本语义不能使用回调错误码14002，请确认您下载sdk时是否勾选语义场景和私有语义的发布
			showTip("onError Code："	+ error.getErrorCode());
			forward("额～～您的网络好像出现了一点点小问题");
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
		mSpeechUnderstander.setParameter(SpeechConstant.ASR_PTT, mSharedPreferences.getString("understander_punc_preference", "0"));
		
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
	
	public void onDestroy() {
        // 退出时释放连接
    	mSpeechUnderstander.cancel();
    	mSpeechUnderstander.destroy();
    	if(mTextUnderstander.isUnderstanding())
    		mTextUnderstander.cancel();
    	mTextUnderstander.destroy();
	}

}
