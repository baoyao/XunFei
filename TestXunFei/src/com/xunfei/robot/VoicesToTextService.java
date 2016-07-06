package com.xunfei.robot;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.xunfei.robot.tools.IatSettings;
import com.xunfei.robot.tools.JsonParser;
import com.xunfei.robot.utils.BackgroundCache;
import com.xunfei.robot.utils.Config;
import com.xunfei.robot.utils.NetWorkUtil;

/**
 * @author houen.bao
 * @date Jul 5, 2016 3:52:50 PM
 */
public class VoicesToTextService extends Service {

	private static String TAG = "tt";
	
	// 语音听写对象
	private SpeechRecognizer mIat;
	// 语音听写UI
	private RecognizerDialog mIatDialog;
	private SharedPreferences mSharedPreferences;
	private Toast mToast;
	private Context mContext;
	private String mEngineType = "cloud";
	
	// 用HashMap存储听写结果
	private List<String> mResult = new ArrayList<String>();
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		mContext = this;
		// 使用SpeechRecognizer对象，可根据回调消息自定义界面；
		mIat = SpeechRecognizer.createRecognizer(mContext, mInitListener);
		mIatDialog = new RecognizerDialog(this,mInitListener);
		mIatDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		mToast = Toast.makeText(mContext, "", Toast.LENGTH_SHORT);
		mSharedPreferences = getSharedPreferences(IatSettings.PREFER_NAME, Activity.MODE_PRIVATE);
		
		refreshNetworkState();
		
		IntentFilter filter=new IntentFilter();
		filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
		registerReceiver(mNetWorkBroadcastReceiver, filter);

		mHandler.sendEmptyMessageDelayed(1, Config.WAITING_TIME);
		Log.v(TAG,"onCreate");
	}
	
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			startSpeek();
		}
	};

	int ret = 0;// 函数调用返回值
	private void startSpeek(){
		Log.v(TAG,"startSpeek");
		clearResult();
		// 设置参数
		setParam();
		boolean isShowDialog = mSharedPreferences.getBoolean(getString(R.string.pref_key_iat_show), true);
		if (isShowDialog) {
			// 显示听写对话框
			mIatDialog.setListener(mRecognizerDialogListener);
			mIatDialog.show();
			showTip(getString(R.string.text_begin));
		} else {
			// 不显示听写对话框
			ret = mIat.startListening(mRecognizerListener);
			if (ret != ErrorCode.SUCCESS) {
				showTip("听写失败,错误码：" + ret);
			} else {
				showTip(getString(R.string.text_begin));
			}
		}
	}

	/**
	 * 参数设置
	 * @param param
	 * @return 
	 */
	public void setParam(){
		// 清空参数
		mIat.setParameter(SpeechConstant.PARAMS, null);
		String lag = mSharedPreferences.getString("iat_language_preference", "mandarin");
		// 设置引擎
		mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
		// 设置返回结果格式
		mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");

		if (lag.equals("en_us")) {
			// 设置语言
			mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
		}else {
			// 设置语言
			mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
			// 设置语言区域
			mIat.setParameter(SpeechConstant.ACCENT,lag);
		}

		// 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
		mIat.setParameter(SpeechConstant.VAD_BOS, mSharedPreferences.getString("iat_vadbos_preference", "4000"));
		
		// 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
		mIat.setParameter(SpeechConstant.VAD_EOS, mSharedPreferences.getString("iat_vadeos_preference", "1000"));
		
		// 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
		mIat.setParameter(SpeechConstant.ASR_PTT, mSharedPreferences.getString("iat_punc_preference", "1"));
		
		// 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
		// 注：AUDIO_FORMAT参数语记需要更新版本才能生效
		mIat.setParameter(SpeechConstant.AUDIO_FORMAT,"wav");
		mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/msc/iat.wav");
	}
	

	/**
	 * 听写UI监听器
	 */
	private RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() {
		public void onResult(RecognizerResult results, boolean isLast) {
			Log.d(TAG, "recognizer result：" + results.getResultString());
			String text = JsonParser.parseIatResult(results.getResultString());
			setResult(text);
		}

		/**
		 * 识别回调错误.
		 */
		public void onError(SpeechError error) {
			showTip(error.getPlainDescription(true));
		}

	};


	/**
	 * 听写监听器。
	 */
	private RecognizerListener mRecognizerListener = new RecognizerListener() {

		@Override
		public void onBeginOfSpeech() {
			// 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
			showTip("开始说话");
		}

		@Override
		public void onError(SpeechError error) {
			// Tips：
			// 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
			showTip(error.getPlainDescription(true));
		}

		@Override
		public void onEndOfSpeech() {
			// 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
			showTip("结束说话");
		}

		@Override
		public void onResult(RecognizerResult results, boolean isLast) {		
			String text = JsonParser.parseIatResult(results.getResultString());
			setResult(text);
			if(isLast) {
				//TODO 最后的结果
			}
		}

		@Override
		public void onVolumeChanged(int volume, byte[] data) {
			showTip("当前正在说话，音量大小：" + volume);
			Log.d(TAG, "返回音频数据："+data.length);
		}

		@Override
		public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
			// 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
			// 若使用本地能力，会话id为null
			//	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
			//		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
			//		Log.d(TAG, "session id =" + sid);
			//	}
		}
	};
	
	private void setResult(String text){
		mResult.add(text);
		BackgroundCache.getInstance().setResult(BackgroundCache.Mode.PEOPLE,text);
		startService(new Intent(this,TalkService.class));
	}
	
	private void clearResult(){
		mResult.clear();
	}
	
	private void refreshNetworkState(){
		if(NetWorkUtil.isNetworkConnected(this)){
			mEngineType = SpeechConstant.TYPE_CLOUD;
		}else{
			mEngineType =  SpeechConstant.TYPE_LOCAL;
		}
		Log.v(TAG,"refreshNetworkState: "+mEngineType);
	}

	/**
	 * 初始化监听器。
	 */
	private InitListener mInitListener = new InitListener() {

		@Override
		public void onInit(int code) {
			Log.d(TAG, "SpeechRecognizer init() code = " + code);
			if (code != ErrorCode.SUCCESS) {
				showTip("初始化失败，错误码：" + code);
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

	private BroadcastReceiver mNetWorkBroadcastReceiver=new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			refreshNetworkState();
		}
	};
	
	
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		Log.v(TAG,"onDestroy");
		this.unregisterReceiver(mNetWorkBroadcastReceiver);
		// 退出时释放连接
		mIat.cancel();
		mIat.destroy();
		stopService(new Intent(this, TalkService.class));
	}
}
