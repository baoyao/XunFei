package com.xunfei.robot.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.xunfei.robot.TalkService;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

/**
 * @author houen.bao
 * @date Jul 6, 2016 5:26:46 PM
 */
public class BgLogic {

	public static void playSong(Context context) {
		Log.v("tt", "play song");
		Cursor dataExternal = null;
		try {
			dataExternal = context.getContentResolver().query(
					MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null,
					null, null);
			if (dataExternal == null) {
				dataExternal = context.getContentResolver().query(
						MediaStore.Audio.Media.INTERNAL_CONTENT_URI, null, null,
						null, null);
			}
			
			if (dataExternal != null) {
				List<Music> list = new ArrayList<Music>();
				while (dataExternal.moveToNext()) {
					Music music = new Music();
					music.id = dataExternal.getInt(dataExternal
							.getColumnIndex(MediaStore.Audio.Media._ID));
					music.title = dataExternal.getString(dataExternal
							.getColumnIndex(MediaStore.Audio.Media.TITLE));
					music.name = dataExternal
							.getString(dataExternal
									.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
					music.mimeType = dataExternal.getString(dataExternal
							.getColumnIndex(MediaStore.Audio.Media.MIME_TYPE));
					list.add(music);

					Log.v("tt", "playSong title: " + music.title + " name: "
							+ music.name + " mimeType: " + music.mimeType);
				}
				if (list.size() == 0) {
					return;
				}
				Random random = new Random();
				int index = random.nextInt(list.size());
				Log.v("tt", "playing song "+index);
				Intent intent = new Intent(Intent.ACTION_VIEW);
				Uri uri = Uri.parse("content://media/external/audio/media/"
						+ list.get(index).id);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setDataAndType(uri, list.get(index).mimeType);
				context.startActivity(intent);
			}else{
				Log.v("tt", "play song not find music");
				BackgroundCache.getInstance().setResult(
						BackgroundCache.Mode.ROBOT, "没有找到音乐");
				context.startService(new Intent(context, TalkService.class));
			}
		} catch (Exception e) {
			Log.v("tt", "playing song Exception: " + e);
			e.printStackTrace();
		} finally {
			if (dataExternal != null) {
				dataExternal.close();
			}
		}
	}

	static class Music {
		private int id;
		private String title;
		private String name;
		private String mimeType;
	}

}
