package com.xunfei.robot.utils;

import java.util.List;
import org.json.JSONObject;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xunfei.robot.entity.App;

/**
 * @author houen.bao
 * @date Jul 8, 2016 3:19:53 PM
 */
public class AnalyzeTalkResultUtils {

	private static AnalyzeTalkResultUtils mAnalyzeTalkResultUtils;
	private static Gson mGson;

	private AnalyzeTalkResultUtils() {}
	
	public static AnalyzeTalkResultUtils getInstance(){
		if(mAnalyzeTalkResultUtils==null){
			mAnalyzeTalkResultUtils=new AnalyzeTalkResultUtils();
			Gson gson =new Gson();
		}
		return mAnalyzeTalkResultUtils;
	}
	
	public String analyzeResult(String result){
		try{
			App app=mGson.fromJson(result,App.class);
			Log.v("tt","app: "+app);
			if(app!=null){
				Log.v("tt","app: "+app.getText());
			}
//			JSONObject root=new JSONObject(result);
//			JSONObject answer=root.getJSONObject("answer");
//			String text=answer.getString("text");
//			return text;
		}catch(Exception e){
			e.printStackTrace();
		}
		return "";
	}
	

}
