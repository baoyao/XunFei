package com.xunfei.robot.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.xunfei.robot.TextToText;
import com.xunfei.robot.TextToVoices;
import com.xunfei.robot.VoicesManager;
import com.xunfei.robot.utils.RecordUtils.Mode;

/**
 * @author houen.bao
 * @date Jul 6, 2016 5:26:46 PM
 */
public class SongUtils {
	
	public static void playSong(Context context) {
		playSong(context,null);
	}

	public static boolean playSong(Context context,String songName) {
		Log.v("tt", "play song "+songName);
		Cursor cursorData = null;
		try {
			cursorData = context.getContentResolver().query(
					MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null,
					null, null);
			if (cursorData == null) {
				cursorData = context.getContentResolver().query(
						MediaStore.Audio.Media.INTERNAL_CONTENT_URI, null,
						null, null, null);
			}

			if (cursorData != null) {
				List<Music> list = new ArrayList<Music>();
				while (cursorData.moveToNext()) {
					Music music = new Music();
					music.id = cursorData.getInt(cursorData
							.getColumnIndex(MediaStore.Audio.Media._ID));
					music.title = cursorData.getString(cursorData
							.getColumnIndex(MediaStore.Audio.Media.TITLE));
					music.name = cursorData
							.getString(cursorData
									.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
					music.mimeType = cursorData.getString(cursorData
							.getColumnIndex(MediaStore.Audio.Media.MIME_TYPE));
					music.data = cursorData.getString(cursorData
							.getColumnIndex(MediaStore.Audio.Media.DATA));
					if(!music.data.endsWith(".ogg")){
						list.add(music);
					}

					Log.v("tt", "playSong title: " + music.title + " name: "
							+ music.name + " mimeType: " + music.mimeType);
				}
				if (list.size() == 0) {
					VoicesManager.getInstance(context).startTextToVoices(
							Mode.ROBOT, "没有找到音乐");
					return false;
				}
				List<Music> taglist = new ArrayList<Music>();
				if(songName!=null){
					for(int i=0;i<list.size();i++){
						if(list.get(i).name==null){
							if(list.get(i).title.contains(songName)){
								taglist.add(list.get(i));
							}
						}else if(list.get(i).name.contains(songName)){
							taglist.add(list.get(i));
						}
					}
					if (taglist.size() == 0) {
						return false;
					}
				}
				Random random = new Random();
				int index = random.nextInt(taglist.size());
				Log.v("tt", "playing song " + index+" list.size(): "+taglist.size());
				Uri uri = Uri.parse("content://media/external/audio/media/"
						+ taglist.get(index).id);
				
				if(uri!=null){
					play(context, uri);
				}
				// Intent intent = new Intent(Intent.ACTION_VIEW);
				// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				// intent.setDataAndType(uri, list.get(index).mimeType);
				// context.startActivity(intent);
				
				return true;
			} else {
				Log.v("tt", "play song not find music");
				if(songName==null){
					VoicesManager.getInstance(context).startTextToVoices(
							Mode.ROBOT, "没有找到音乐");
				}else{
					return false;
				}
			}
		} catch (Exception e) {
			Log.v("tt", "playing song Exception: " + e);
			e.printStackTrace();
			if(songName==null){
				VoicesManager.getInstance(context).startTextToVoices(
						Mode.ROBOT, "很抱歉，没有找到音乐");
			}else{
				return false;
			}
		} finally {
			if (cursorData != null) {
				cursorData.close();
			}
		}
		return false;
	}

	private static MediaPlayer player;

	private static void play(Context context, Uri uri)
			throws IllegalArgumentException, SecurityException,
			IllegalStateException, IOException {
		if (player == null) {
			player = new MediaPlayer();
		}
		stop();
		player = new MediaPlayer();
		player.setDataSource(context, uri);
		player.setOnPreparedListener(new OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
				player.start();
			}
		});
		player.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				stop();
			}
		});
		player.prepareAsync();
	}

	private static void stop() {
		if (player != null) {
			player.stop();
			player.release();
			player = null;
		}
	}

	public static void onDestroy() {
		stop();
	}

	static class Music {
		private int id;
		private String title;
		private String name;
		private String mimeType;
		private String data;
	}

}
