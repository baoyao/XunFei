package com.xunfei.robot.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.xunfei.robot.VoicesManager;
import com.xunfei.robot.utils.RecordUtils.Mode;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

/**
 * @author houen.bao
 * @date Jul 14, 2016 2:23:45 PM
 */
public class StoryUtils {
	
	private static Context mContext;
	private static StoryUtils mStoryUtils;
	private String mStoryName = "";
	
	private StoryUtils(){}
	
	public static StoryUtils getInstance(Context context){
		if(mStoryUtils == null){
			mStoryUtils = new StoryUtils();
			mContext = context;
		}
		return mStoryUtils;
	}
	
	public void tellStory(String storyName){
		mStoryName = storyName;
		Log.v("tt", "tell story "+storyName);
		Cursor cursorData = null;
		InputStreamReader inputReader = null;
		BufferedReader bufferedReader = null;
		try {
			cursorData = mContext.getContentResolver().query(
					MediaStore.Files.getContentUri("external"), null, null,
					null, null);
			if (cursorData == null) {
				cursorData = mContext.getContentResolver().query(
						MediaStore.Files.getContentUri("internal"), null,
						null, null, null);
			}
			if (cursorData != null) {
				List<Story> list = new ArrayList<Story>();
				while (cursorData.moveToNext()) {
					Story story = new Story();
					story.id = cursorData.getInt(cursorData
							.getColumnIndex(MediaStore.Files.FileColumns._ID));
					story.title = cursorData.getString(cursorData
							.getColumnIndex(MediaStore.Files.FileColumns.TITLE));
					story.name = cursorData
							.getString(cursorData
									.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME));
					story.mimeType = cursorData.getString(cursorData
							.getColumnIndex(MediaStore.Files.FileColumns.MIME_TYPE));
					story.data = cursorData.getString(cursorData
							.getColumnIndex(MediaStore.Files.FileColumns.DATA));
					if(story.data.endsWith(".txt")){
						list.add(story);
						Log.v("tt", "story title: " + story.title + " name: "
								+ story.name + " mimeType: " + story.mimeType+" size: "+list.size());
					}
				}
				if (list.size() == 0) {
					sayNotFound("size=0 ");
					return;
				}
				List<Story> taglist = new ArrayList<Story>();
				if(storyName!=null){
					for(int i=0;i<list.size();i++){
						if(list.get(i).name==null){
							if(list.get(i).title.contains(storyName)){
								taglist.add(list.get(i));
							}
						}else if(list.get(i).name.contains(storyName)){
							taglist.add(list.get(i));
						}
					}
					if (taglist.size() == 0) {
						sayNotFound("taglist size=0 ");
						return;
					}
				}
				Random random = new Random();
				int index = random.nextInt(taglist.size());
				Log.v("tt", "tell story " + index+" list.size(): "+taglist.size());
				
				String storyPath=taglist.get(index).data;
				File file=new File(storyPath);
				Log.v("tt","story file: "+file);
				inputReader = new InputStreamReader(
						new FileInputStream(file),codeString(file));
				bufferedReader = new BufferedReader(inputReader);
				String content = "";
				String lineTxt = "";
				while ((lineTxt = bufferedReader.readLine()) != null) {
					content += lineTxt+"\n";
				}
				Log.v("tt","tell story: "+content);
				if(!"".equals(content)){
					VoicesManager.getInstance(mContext).startTextToVoices(
							Mode.ROBOT, content);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.v("tt","tell story Exception: "+e);
			sayNotFound("error ");
		}finally{
			try {
				if (cursorData != null) {
					cursorData.close();
				}
				if (inputReader != null) {
					inputReader.close();
				}
				if (bufferedReader != null) {
					bufferedReader.close();
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	public String codeString(File file) {
		BufferedInputStream bin = null;
		int p = 0;
		try {
			bin = new BufferedInputStream(new FileInputStream(file));
			p = (bin.read() << 8) + bin.read();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (bin != null) {
					bin.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		String code = null;
		switch (p) {
		case 0xefbb:
			code = "UTF-8";
			break;
		case 0xfffe:
			code = "Unicode";
			break;
		case 0xfeff:
			code = "UTF-16BE";
			break;
		default:
			code = "GBK";
		}
		return code;
	}
	
	private void sayNotFound(String message){
		VoicesManager.getInstance(mContext).startTextToVoices(
				Mode.ROBOT, message+"没有找到故事"+("".equals(mStoryName)?"":
					"《"+mStoryName+"》"));
	}
	
	class Story{
		int id;
		String title;
		String name;
		String mimeType;
		String data;
	}

}
