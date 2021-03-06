package com.robot.voice;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
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
import com.robot.voice.tools.IatSettings;
import com.robot.voice.tools.JsonParser;
import com.robot.voice.utils.NetWorkUtil;
import com.robot.voice.utils.OpenAppUtils;
import com.robot.voice.utils.RecordUtils;
import com.robot.voice.utils.RecordUtils.Mode;
import com.robot.voice.utils.SongUtils;
import com.robot.voice.utils.StoryUtils;

/**
 * @author houen.bao
 * @date Jul 5, 2016 3:52:50 PM
 */
public class VoicesToText{

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
	
	private Handler mHandler = new Handler();
	
	public VoicesToText(Context context){
		mContext = context;
		// 使用SpeechRecognizer对象，可根据回调消息自定义界面；
		mIat = SpeechRecognizer.createRecognizer(mContext, mInitListener);
		mIatDialog = new RecognizerDialog(mContext, mInitListener);
		mIatDialog.getWindow().setType(
				WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		mToast = Toast.makeText(mContext, "", Toast.LENGTH_SHORT);
		mSharedPreferences = mContext.getSharedPreferences(IatSettings.PREFER_NAME,
				Activity.MODE_PRIVATE);

		refreshNetworkState();

		IntentFilter filter = new IntentFilter();
		filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
		mContext.registerReceiver(mNetWorkBroadcastReceiver, filter);
		Log.v(TAG, "onCreate");
	}

	int ret = 0;// 函数调用返回值

	public void start() {
		Log.v(TAG, "startSpeek");
		clearResult();
		// 设置参数
		setParam();
		boolean isShowDialog = mSharedPreferences.getBoolean(
				mContext.getString(R.string.pref_key_iat_show), true);
		if (isShowDialog) {
			// 显示听写对话框
			mIatDialog.setListener(mRecognizerDialogListener);
			mIatDialog.show();
			showTip(mContext.getString(R.string.text_begin));
		} else {
			// 不显示听写对话框
			ret = mIat.startListening(mRecognizerListener);
			if (ret != ErrorCode.SUCCESS) {
				showTip("听写失败,错误码：" + ret);
			} else {
				showTip(mContext.getString(R.string.text_begin));
			}
		}
	}

	/**
	 * 参数设置
	 * 
	 * @param param
	 * @return
	 */
	public void setParam() {
		// 清空参数
		mIat.setParameter(SpeechConstant.PARAMS, null);
		String lag = mSharedPreferences.getString("iat_language_preference",
				"mandarin");
		// 设置引擎
		mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
		// 设置返回结果格式
		mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");

		if (lag.equals("en_us")) {
			// 设置语言
			mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
		} else {
			// 设置语言
			mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
			// 设置语言区域
			mIat.setParameter(SpeechConstant.ACCENT, lag);
		}

		// 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
		mIat.setParameter(SpeechConstant.VAD_BOS,
				mSharedPreferences.getString("iat_vadbos_preference", "4000"));

		// 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
		mIat.setParameter(SpeechConstant.VAD_EOS,
				mSharedPreferences.getString("iat_vadeos_preference", "1000"));

		// 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
		mIat.setParameter(SpeechConstant.ASR_PTT,
				mSharedPreferences.getString("iat_punc_preference", "0"));

		// 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
		// 注：AUDIO_FORMAT参数语记需要更新版本才能生效
		mIat.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
		mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH,
				Environment.getExternalStorageDirectory() + "/msc/iat.wav");
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
			if (isLast) {
				// TODO 最后的结果
			}
		}

		@Override
		public void onVolumeChanged(int volume, byte[] data) {
			showTip("当前正在说话，音量大小：" + volume);
			Log.d(TAG, "返回音频数据：" + data.length);
		}

		@Override
		public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
			// 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
			// 若使用本地能力，会话id为null
			// if (SpeechEvent.EVENT_SESSION_ID == eventType) {
			// String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
			// Log.d(TAG, "session id =" + sid);
			// }
		}
	};

	public synchronized void setResult(String text) {
		if (text != null && !text.trim().equals("")) {
			mResult.add(text);
			if (!interceptResult(text))
			VoicesManager.getInstance(mContext).startTextToText(Mode.PEOPLE,text);
		}
	}
	
	private List<String[]> mInterceptTags = initTag();
	private final int PLAY_SONG = 0;
	private final int PLAY_SONG2 = 1;
	private final int PLAY_SONG3 = 2;
	private final int TELL_STORY = 3;
	private final int OPEN_APP = -1;

	private List<String[]> initTag() {
		List<String[]> tagets = new ArrayList<String[]>();
		
		String[] tag1 = new String[] { "唱", "首歌" };
		tagets.add(tag1);

		String[] tag2 = new String[] { "唱首"};
		tagets.add(tag2);

		String[] tag3 = new String[] { "唱个"};
		tagets.add(tag3);
		
		String[] tag4 = new String[] { "讲", "故事" };
		tagets.add(tag4);

		return tagets;
	}

	private boolean interceptResult(String text) {
		Log.v("tt", "interceptResult000");
		for (int i = 0; i < mInterceptTags.size(); i++) {
			String[] tag = mInterceptTags.get(i);
			boolean bool = true;
			for (String t : tag) {
				if (!text.contains(t)) {
					Log.v("tt", "interceptResult111");
					bool = false;
					break;
				}
			}
			if (bool) {
				Log.v("tt", "interceptResult222");
				RecordUtils.getInstance().setResult(
						RecordUtils.Mode.PEOPLE, text);
				doIntercept(i,text);
				return true;
			}
		}
		return false;
	}

	private void doIntercept(int index,String text) {
		switch (index) {
		case PLAY_SONG: {
			SongUtils.playSong(mContext);
		}
			break;
		case PLAY_SONG2: {
			String text1="唱首";
			String songName=text.substring(text.indexOf(text1)+text1.length());
			SongUtils.playSong(mContext,songName);
		}
			break;
		case PLAY_SONG3: {
			String text1="唱个";
			String songName=text.substring(text.indexOf(text1)+text1.length());
			SongUtils.playSong(mContext,songName);
		}
			break;
		case TELL_STORY: {
			String text1 = "讲";
			String text2 = "故事";
			String text3 = "讲个";
			String text4 = "的故事";
			int startPosition=0;
			int endPosition=0;
			if(text.contains(text3)){
				startPosition=text.indexOf(text3)+text3.length();
			}else{
				startPosition=text.indexOf(text1)+text1.length();
			}
			
			if(text.contains(text4)){
				endPosition=text.indexOf(text4);
			}else{
				endPosition=text.indexOf(text2);
			}
			String storyName=text.substring(startPosition, endPosition);
			if(startPosition==endPosition){
				storyName=text.substring(text.indexOf(text2)+text2.length());
			}
			StoryUtils.getInstance(mContext).tellStory(storyName);
		}
		break;
		case OPEN_APP: {
			String tagText="打开";
			OpenAppUtils.getInstance(mContext).openApp(text.substring(text.indexOf(tagText)+tagText.length()));
		}
			break;
		default:
			break;
		}

	}

	private void clearResult() {
		mResult.clear();
	}

	private void refreshNetworkState() {
		if (NetWorkUtil.isNetworkConnected(mContext)) {
			mEngineType = SpeechConstant.TYPE_CLOUD;
		} else {
			mEngineType = SpeechConstant.TYPE_LOCAL;
		}
		Log.v(TAG, "refreshNetworkState: " + mEngineType);
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

	private void showTip(final String str) {
		Log.v(TAG, "showTip: " + str);
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mToast.setText(str);
				mToast.show();
			}
		});
	}

	private BroadcastReceiver mNetWorkBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			refreshNetworkState();
		}
	};

	public void onDestroy() {
		// TODO Auto-generated method stub
		Log.v(TAG, "onDestroy");
		mContext.unregisterReceiver(mNetWorkBroadcastReceiver);
		// 退出时释放连接
		mIat.cancel();
		mIat.destroy();
		SongUtils.onDestroy();
		mContext.stopService(new Intent(mContext, TextToText.class));
	}
}
