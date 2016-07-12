package com.xunfei.robot.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.os.Handler;

/**
 * @author houen.bao
 * @date Jul 12, 2016 2:37:10 PM
 */
public class LocationUtils {

	private static LocationUtils mLocationUtils;
	private Callback mCallback;
	private Handler mHandler = new Handler();

	private LocationUtils() {
	}

	public static LocationUtils getInstance() {
		if (mLocationUtils == null) {
			mLocationUtils = new LocationUtils();
		}
		return mLocationUtils;
	}

	public void getLocation(Callback callback) {
		mCallback = callback;
		getCity();
	}

	interface Callback {
		public void onCallback(String city);
	}

	private void getCity() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String requsetResult = sendRequset("http://ip168.com/json.do?view=myipaddress");
				Pattern pattern = Pattern
						.compile("((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))");
				Matcher matcher = pattern.matcher(requsetResult.toString());
				String cityStr = "";
				if (matcher.find()) {
					String ipLine = "";
					ipLine = matcher.group();
					int startPosition = requsetResult.indexOf(ipLine)
							+ ipLine.length() + 5;
					int endPosition = requsetResult.indexOf("</center>") - 3;
					cityStr = requsetResult.substring(startPosition,
							endPosition);
//					if (cityStr.contains("省")) {
//						cityStr = cityStr.substring(cityStr.indexOf("省") + 1,
//								cityStr.length());
//					}
					android.util.Log.v("tt","requsetResult: "+requsetResult
							+"\n\n\nipLine: "+ipLine
							+"\n\nstartPosition: "+startPosition
							+"\n\nendPosition: "+endPosition);
					final String tempCityStr = cityStr;
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							if (mCallback != null) {
								mCallback.onCallback(tempCityStr);
							}
						}
					});
				}
			}
		}).start();
	}

	private String sendRequset(String url) {
		URL infoUrl = null;
		InputStream inStream = null;
		HttpURLConnection httpConnection = null;
		StringBuilder strber = new StringBuilder();
		try {
			infoUrl = new URL(url);
			URLConnection connection = infoUrl.openConnection();
			httpConnection = (HttpURLConnection) connection;
			int responseCode = httpConnection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				inStream = httpConnection.getInputStream();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(inStream, "utf-8"));
				String line = null;
				while ((line = reader.readLine()) != null) {
					strber.append(line);
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (inStream != null) {
					inStream.close();
				}
				httpConnection.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return strber.toString();
	}

}
