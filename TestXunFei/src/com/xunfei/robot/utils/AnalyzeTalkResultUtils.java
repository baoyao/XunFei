package com.xunfei.robot.utils;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.xunfei.robot.entity.*;

/**
 * @author houen.bao
 * @date Jul 8, 2016 3:19:53 PM
 */
public class AnalyzeTalkResultUtils {
	private final String TAG = "tt";

	private static AnalyzeTalkResultUtils mAnalyzeTalkResultUtils;
	private static Context mContext;
	private static Gson mGson;

	private final String[] SERVICE_LIST = new String[] { "app", "baike",
			"calc", "cookbook", "datetime", "faq", "flight", "hotel", "map",
			"music", "radio", "restaurant", "schedule", "stock", "train",
			"translation", "tv", "video", "weather", "websearch", "website",
			"weibo","openQA" };

	private int mService = -1;

	private AnalyzeTalkResultUtils() {
	}

	public static AnalyzeTalkResultUtils getInstance(Context context) {
		if (mAnalyzeTalkResultUtils == null) {
			mAnalyzeTalkResultUtils = new AnalyzeTalkResultUtils();
			mContext = context;
			mGson = new Gson();
		}
		return mAnalyzeTalkResultUtils;
	}

	private final int APP = 0;
	private final int BAIKE = 1;
	private final int CALC = 2;
	private final int COOKBOOK = 3;
	private final int DATETIME = 4;
	private final int FAQ = 5;
	private final int FLIGHT = 6;
	private final int HOTEL = 7;
	private final int MAP = 8;
	private final int MUSIC = 9;
	private final int RADIO = 10;
	private final int RESTAURANT = 11;
	private final int SCHEDULE = 12;
	private final int STOCK = 13;
	private final int TRAIN = 14;
	private final int TRANSLATION = 15;
	private final int TV = 16;
	private final int VIDEO = 17;
	private final int WEATHER = 18;
	private final int WEBSEARCH = 19;
	private final int WEBSITE = 20;
	private final int WEIBO = 21;
	private final int OPENQA = 22;

	private final String[] operation = new String[] { "LAUNCH", "UNINSTALL",
			"INSTALL", "EXIT" };

	// app operation
	private final String LAUNCH = "LAUNCH";
	private final String SEARCH = "SEARCH";
	private final String UNINSTALL = "UNINSTALL";
	private final String INSTALL = "INSTALL";
	private final String EXIT = "EXIT";

	public ResultAction analyzeResult(String result) {
		ResultAction ra=new ResultAction();
		ra.setIntercept(false);
		ra.setShowErrorMessage(false);
		try {
			JSONObject root = new JSONObject(result);
			if (root != null) {
				mService = -1;
				String service = root.getString("service");
				String operation = root.getString("operation");
				Log.v(TAG, "analyzeResult service: " + service
						+" operation: "+operation);
				for (int i = 0; i < SERVICE_LIST.length; i++) {
					if (SERVICE_LIST[i].equals(service)) {
						mService = i;
						break;
					}
				}
				switch (mService) {
				case APP:
					App app = mGson.fromJson(result, App.class);
					Log.v(TAG, "app: " + app);
					if (LAUNCH.equals(app.getOperation())) {
						ra.setIntercept(true);
						String appName = app.getSemantic().getSlots().getName();
						OpenAppUtils.getInstance(mContext).openApp(appName);
					} else if (SEARCH.equals(app.getOperation())) {
						ra.setIntercept(true);
						String searchAppCategory = app.getSemantic().getSlots()
								.getCategory();
						// 打开网页搜索
						Uri myBlogUri = Uri
								.parse("https://www.baidu.com/s?wd="
										+ searchAppCategory);
						Intent intent = new Intent(Intent.ACTION_VIEW,
								myBlogUri);
						mContext.startActivity(intent);
					}else{
						ra.setShowErrorMessage(true);
					}
					break;
				case BAIKE:
				case CALC:
					doQuestion(result,ra);
					break;
				case COOKBOOK:
					Cookbook cb=mGson.fromJson(result, Cookbook.class);
					ra.setResult(cb.getData().getHeader()+cb.getData().getResult().getAccessory());
					//预留扩展 询问是否打开网页cb.getData().getResult().getUrl()
					
					break;
				case DATETIME:
				case FAQ:
					doQuestion(result,ra);
					break;
				case FLIGHT:
					break;
				case HOTEL:
					break;
				case MAP:
					break;
				case MUSIC:
//					Music music=mGson.fromJson(result, Music.class);
//					ra.setResult(music.getSemantic().getSlots().getSong());
					SongUtils.playSong(mContext);
					ra.setIntercept(true);
					break;
				case RADIO:
					break;
				case RESTAURANT:
					break;
				case SCHEDULE:
					break;
				case STOCK:
					break;
				case TRAIN:
					break;
				case TRANSLATION:
					break;
				case TV:
					break;
				case VIDEO:
					break;
				case WEATHER:
					break;
				case WEBSEARCH:
					break;
				case WEBSITE:
					break;
				case WEIBO:
					break;
				case OPENQA:
					doQuestion(result,ra);
					break;
				default:
					break;
				}
			}

			// JSONObject root=new JSONObject(result);
			// JSONObject answer=root.getJSONObject("answer");
			// String text=answer.getString("text");
			// return text;
		} catch (Exception e) {
			e.printStackTrace();
			Log.v(TAG, "analyzeResult Exception: " + e);
			ra.setIntercept(false);
			ra.setShowErrorMessage(false);
			ra.setResult("您的这个问题可把我难倒了");
		}
		if(!ra.isIntercept()&&!ra.isShowErrorMessage()
				&&(ra.getResult()==null||"".equals(ra.getResult()))){
			ra.setShowErrorMessage(true);
		}
		Log.v(TAG, "analyzeResult end: " + ra.getResult());
		return ra;
	}
	
	private void doQuestion(String result,ResultAction ra){
		Question question=mGson.fromJson(result, Question.class);
		ra.setResult(question.getAnswer().getText());
	}

}
