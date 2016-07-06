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
		Cursor dataExternal = null;
		try {
			dataExternal = context.getContentResolver().query(
					MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null,
					null, null);
			List<Music> list=new ArrayList<Music>();
			while (dataExternal.moveToNext()) {
				Music music=new Music();
				music.id=dataExternal.getInt(dataExternal
						.getColumnIndex(MediaStore.Audio.Media._ID));
				music.title = dataExternal.getString(dataExternal
						.getColumnIndex(MediaStore.Audio.Media.TITLE));
				music.name = dataExternal.getString(dataExternal
						.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
				music.mimeType = dataExternal.getString(dataExternal
						.getColumnIndex(MediaStore.Audio.Media.MIME_TYPE));
				list.add(music);

				Log.v("tt", "playSong title: " + music.title + " name: " + music.name
						+ " mimeType: " + music.mimeType);
			}
			if(list.size()==0){
				BackgroundCache.getInstance().setResult(BackgroundCache.Mode.ROBOT,
						"没有找到音乐");
				context.startService(new Intent(context, TalkService.class));
				return;
			}
			Random random=new Random();
			int index = random.nextInt(list.size());
			Intent intent = new Intent(Intent.ACTION_VIEW);
			Uri uri = Uri.parse("content://media/external/audio/media/"+list.get(index).id);
			intent.setDataAndType(uri, list.get(index).mimeType);
			context.startActivity(intent);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (dataExternal != null) {
				dataExternal.close();
			}
		}
	}
	
	static class Music{
		private int id;
		private String title;
		private String name;
		private String mimeType;
	}

}
