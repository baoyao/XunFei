package com.xunfei.robot.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

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
			"weibo", "openQA" };

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

	// app operation
	private final String LAUNCH = "LAUNCH";
	private final String SEARCH = "SEARCH";
	private final String UNINSTALL = "UNINSTALL";
	private final String INSTALL = "INSTALL";
	private final String EXIT = "EXIT";

	// map
	private final String ROUTE = "ROUTE";
	private final String POSITION = "POSITION";
	
	// video
	private final String PLAY = "PLAY";
	private final String QUERY = "QUERY";
	
	//website
	private final String OPEN="OPEN";


	private final String DEFAULT_RESULT = "该功能等待后续开放";
	private final String ERROR_RESULT = "您的这个问题可把我难倒了";

	public ResultAction analyzeResult(String result) {
		ResultAction ra = new ResultAction();
		ra.setIntercept(false);
		ra.setShowErrorMessage(false);
		try {
			BaseService service = mGson.fromJson(result, BaseService.class);
			if (service != null) {
				mService = -1;
				String serviceName = service.getService();
				String operation = service.getOperation();
				Log.v(TAG, "analyzeResult service: " + serviceName
						+ " operation: " + operation);
				for (int i = 0; i < SERVICE_LIST.length; i++) {
					if (SERVICE_LIST[i].equals(serviceName)) {
						mService = i;
						break;
					}
				}
				switch (mService) {
				case APP:
					if (LAUNCH.equals(service.getOperation())) {
						ra.setIntercept(true);
						String appName = service.getSemantic().getSlots()
								.getName();
						OpenAppUtils.getInstance(mContext).openApp(appName);
					} else{
						String searchAppCategory = service.getSemantic()
								.getSlots().getCategory();
						// 打开网页搜索
						search(ra, searchAppCategory);
					}
					break;
				case BAIKE:
				case CALC:
					questionAnswerText(service, ra);
					break;
				case COOKBOOK:
					List<Result> cookBookResult = service.getData().getResult();
					String cookBookStr = "\n";
					if (cookBookResult != null) {
						for (int i=0;i<cookBookResult.size();i++) {
							if(cookBookResult.size()>1){
								cookBookStr+="第"+(i+1)+"条\n";
							}
							cookBookStr += cookBookResult.get(i).getAccessory() + "\n";
						}
					}
					ra.setResult(ac(service.getData().getHeader()) + cookBookStr);
					// 预留扩展 询问是否打开网页cb.getData().getResult().getUrl()

					break;
				case DATETIME:
				case FAQ:
					questionAnswerText(service, ra);
					break;
				case FLIGHT:// 航班查询
					if (isError(service)) {
						ra.setResult("缺少必要的查询条件。您可以这么说:我想查一下明天海航的从合肥骆岗到北京首都机场的航班");
					} else {
						Slots flightSlots = service.getSemantic().getSlots();
						String flightStr = "查询"
								+ ac(flightSlots.getStartLoc().getCity())
								+ ac(flightSlots.getStartLoc().getPoi())
								+ "到"
								+ ac(flightSlots.getEndLoc().getCity()
										+ ac(flightSlots.getEndLoc().getPoi()))
								+ ac(flightSlots.getStartDate().getDate())
								+ "的" + ac(flightSlots.getAirline());
						search(ra, flightStr);
					}
					break;
				case HOTEL:
					search(ra, service.getText());
					break;
				case MAP:
					if (POSITION.equals(service.getOperation())) {
						Location mapL = service.getSemantic().getSlots()
								.getLocation();
						ra.setResult(mapL.getCity() + mapL.getPoi());
					} else if (ROUTE.equals(service.getOperation())) {// 导航
						search(ra,service.getText());
					}
					break;
				case MUSIC:
					// Music music=mGson.fromJson(result, Music.class);
					// ra.setResult(music.getSemantic().getSlots().getSong());
					if(isError(service)){
						ra.setResult("缺少必要的查询条件。您可以这么说:随便播放一首歌，或者说帮我查找刘德华的忘情水");
					}else{
						if(SEARCH.equals(service.getOperation())){
							search(ra,ac(service.getSemantic().getSlots().getArtist())
									+ac(service.getSemantic().getSlots().getSong()));
						}else{
							ra.setIntercept(true);
							SongUtils.playSong(mContext);
						}
					}
					break;
				case RADIO:
					String radioStr = "";
					radioStr += ac(service.getSemantic().getSlots().getName());
					radioStr += ac(service.getSemantic().getSlots().getWaveband());
					radioStr += ac(service.getSemantic().getSlots().getCode());
					search(ra, radioStr);
					break;
				case RESTAURANT:
					String restaurantStr="";
					Location restaurantLocation=service.getSemantic().getSlots().getLocation();
					if(checkEmpty(restaurantLocation.getPoi())){
						restaurantStr=restaurantLocation.getPoi()
								+ac(service.getSemantic().getSlots().getPrice())
								+ac(service.getSemantic().getSlots().getCategory());
					}else{
						restaurantStr=ac(restaurantLocation.getCity())
								+ac(restaurantLocation.getArea())
								+ac(service.getSemantic().getSlots().getPrice())
								+ac(service.getSemantic().getSlots().getCategory());
					}
					if(!"".equals(restaurantStr)){
						search(ra,restaurantStr);
					}else{
						ra.setResult("缺少必要的查询条件。您可以这么说:合肥包河区女人街有什么实惠的川菜馆");
					}
					break;
				case SCHEDULE:
					// 等待后续开放
					ra.setResult(DEFAULT_RESULT);
					break;
				case STOCK:
					if(isError(service)){
						ra.setResult("缺少必要的查询条件。您可以这么说:查询小米科技的股票价格");
					}else{
						ra.setResult(ac(service.getSemantic().getSlots().getName())
								+ ac(service.getSemantic().getSlots().getCode()));
					}
					break;
				case TRAIN:
					search(ra,service.getText());
					break;
				case TRANSLATION:
					String translationStr="";
					if("en".equals(service.getSemantic().getSlots().getTarget())){
						translationStr=service.getSemantic().getSlots().getContent()+"英文怎么说";
					}
					search(ra,translationStr);
					break;
				case TV:
					List<Result> tvResult = service.getData().getResult();
					String tvStr = "\n";
					if (tvResult != null) {
						for (int i=0;i<tvResult.size();i++) {
							if(tvResult.size()>1){
								tvStr+="第"+(i+1)+"条\n";
							}
							Result r=tvResult.get(i);
							tvStr += r.getTvName() + r.getStartTime()
									+ r.getProgramType() + r.getProgramName()
									+ "\n";
						}
					}
					ra.setResult(ac(service.getData().getHeader()) + tvStr);
					break;
				case VIDEO:
					String videoStr="";
					if(PLAY.equals(service.getOperation())){
						videoStr=service.getSemantic().getSlots().getKeywords();
					}else{
						videoStr=ac(service.getSemantic().getSlots().getActor())
								+ac(service.getSemantic().getSlots().getPopularity())
								+ac(service.getSemantic().getSlots().getVideoTag())
								+ac(service.getSemantic().getSlots().getVideoCategory());
					}
					search(ra,videoStr);
					break;
				case WEATHER:
					Result weatherResult=service.getData().getResult().get(0);
					String weatherStr=ac(weatherResult.getCity())
							+ac(service.getSemantic().getSlots().getDatetime()==null
							?"":service.getSemantic().getSlots().getDatetime().getDateOrig())
							+ac(weatherResult.getWeather())
							+ac(weatherResult.getTempRange())
							+"，"
							+ac(weatherResult.getWind());
					ra.setResult(weatherStr);
					break;
				case WEBSEARCH:
					String websearchStr=ac(service.getSemantic().getSlots().getKeywords());
					if("".equals(websearchStr)){
						websearchStr = service.getText();
					}
					ra.setResult(websearchStr);
					break;
				case WEBSITE:
					if(OPEN.equals(service.getOperation())){
						if(checkEmpty(service.getSemantic().getSlots().getUrl())){
							open(ra,service.getSemantic().getSlots().getUrl());
							break;
						}
						search(ra,ac(service.getSemantic().getSlots().getName()));
					}else{
						search(ra,service.getText());
					}
					break;
				case WEIBO:
					if(checkEmpty(service.getSemantic().getSlots().getChannel())){
						search(ra,service.getSemantic().getSlots().getChannel());
					}else if(checkEmpty(service.getSemantic().getSlots().getContent())){
						search(ra,service.getSemantic().getSlots().getContent());
					}else{
						search(ra,service.getText());
					}
					break;
				case OPENQA:
					questionAnswerText(service, ra);
					break;
				default:
					break;
				}

				if (!ra.isIntercept()
						&& !ra.isShowErrorMessage()
						&& (ra.getResult() == null || "".equals(ra.getResult())||
						"null".equals(ra.getResult().trim()))) {
					if (isError(service)) {
						ra.setResult(service.getError().getMessage());
					} else {
						ra.setShowErrorMessage(true);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.v(TAG, "analyzeResult Exception: " + e);
			ra.setIntercept(false);
			ra.setShowErrorMessage(false);
			ra.setResult(ERROR_RESULT);
		}
		Log.v(TAG, "analyzeResult end: " + ra.isIntercept() + " " + ra.isShowErrorMessage() + " " + ra.getResult());
		return ra;
	}

	private boolean isError(BaseService service) {
		return service.getError() != null;
	}

	private boolean needShowErrorMessage(BaseService service) {
		// return service.getError() != null && mService != MUSIC;
		return false;
	}

	private void questionAnswerText(BaseService service, ResultAction ra) {
		ra.setResult(service.getAnswer().getText());
	}
	
	private boolean checkEmpty(String str){
		return str!=null&&!"".equals(str.trim());
	}

	private String ac(String str) {
		return str == null ? "" : str.trim();
	}

	private void search(ResultAction ra, String info) {
		ra.setIntercept(true);
		// 打开网页搜索
		try {
			Uri uri = Uri.parse("https://www.baidu.com/s?wd=" + URLEncoder.encode(info, "utf-8"));
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mContext.startActivity(intent);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void open(ResultAction ra, String info) {
		ra.setIntercept(true);
		Uri uri = Uri.parse(info);
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mContext.startActivity(intent);
	} 

}
