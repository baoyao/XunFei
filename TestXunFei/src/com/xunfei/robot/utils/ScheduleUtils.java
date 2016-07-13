package com.xunfei.robot.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Reminders;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xunfei.robot.R;
import com.xunfei.robot.activity.ScheduleActivity;
import com.xunfei.robot.entity.BaseService;
import com.xunfei.robot.entity.Schedule;

/**
 * @author houen.bao
 * @date Jul 13, 2016 9:47:02 AM
 */
public class ScheduleUtils {
	
	private static Context mContext;
	private static ScheduleUtils mScheduleUtils;
	
	private ScheduleUtils(){}
	
	public static ScheduleUtils getInstance(Context context){
		if(mScheduleUtils == null){
			mScheduleUtils = new ScheduleUtils();
			mContext = context;
		}
		return mScheduleUtils;
	}
	
	public void createSchedule(BaseService service){
		try{
			showMessageDialog(parseDataToSchedule(service));
		}catch(Exception e){
			e.printStackTrace();
			createScheduleFailed(e);
		}
	}

	private void createScheduleSuccessed(){
		
	}
	
	private void createScheduleFailed(Exception e){
		Log.w("tt", "createScheduleFailed "+e);
	}
	
	public void query(BaseService service){
		Schedule schedule=queryScheduleData(parseDataToSchedule(service));
		if(schedule!=null){
			
		}
	}
	private long getStartMillis(BaseService service){
		String date=service.getSemantic().getSlots().getDatetime().getDate();
		String time=service.getSemantic().getSlots().getDatetime().getTime();
		String[] dateStr=date.split("-");
		String[] timeStr=time.split(":");
		int[] dateInt = new int[dateStr.length];
		int[] timeInt = new int[timeStr.length];

		for(int i = 0;i<dateStr.length;i++){
			dateInt[i]=Integer.parseInt(dateStr[i]);
		}
		for(int i = 0;i<timeStr.length;i++){
			timeInt[i]=Integer.parseInt(timeStr[i]);
		}
		
		long startMillis=0;
		Calendar startTime = Calendar.getInstance();
		startTime.set(dateInt[0], dateInt[1], dateInt[2],
				timeInt[0], timeInt[1]);
		startMillis = startTime.getTimeInMillis();
		Log.v("tt","getStartMillis dateInt: "+Arrays.toString(dateInt)+" timeInt: "+Arrays.toString(timeInt));
		return startMillis;
	}
	
	private void showMessageDialog(final Schedule schedule) {
		if(schedule == null)return;
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle(schedule.getTitle());
		builder.setView(buildDialogContentView(schedule));
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				try{
					addEvent(schedule.getName(),schedule.getDescription(),
							schedule.getEventBeginDate(),schedule.getEventBeginTime(),
							schedule.getEventEndDate(),schedule.getEventEndTime(),
							schedule.getReminderMinutes());
				}catch(Exception e){
					e.printStackTrace();
					createScheduleFailed(e);
				}
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	private Schedule parseDataToSchedule(BaseService service) {
		Schedule s = new Schedule();
		s.setBeginDate(service.getSemantic().getSlots().getDatetime().getDate());
		s.setEndDate(service.getSemantic().getSlots().getDatetime().getDate());
		s.setBeginTime(service.getSemantic().getSlots().getDatetime().getTime());
		s.setEndTime(parseEndTime(service.getSemantic().getSlots().getDatetime().getTime()));
		s.setDescription(service.getSemantic().getSlots().getContent()==null
				?"":service.getSemantic().getSlots().getContent());
		s.setTitle("请确认");
		s.setName("提醒");
		return s;
	}
	
	private String parseEndTime(String endtime){
		String[] tempTime=endtime.split(":");
		int[] time = new int[tempTime.length];
		for(int i = 0;i<tempTime.length;i++){
			time[i]=Integer.parseInt(tempTime[i]);
		}
		String timeStr="";
		for(int i = 0;i<time.length;i++){
			if(i == 1){
				timeStr+=(time[i]+1)+":";//加1分钟
			}else if(i!=(time.length-1)){
				timeStr+=time[i]+":";
			}else{
				timeStr+=time[i];
			}
		}
		return timeStr;
	}
	
	private View buildDialogContentView(Schedule schedule){
		View view = LayoutInflater.from(mContext).inflate(R.layout.add_event, null);
		((EditText)view.findViewById(R.id.event_name)).setText(schedule.getName());
		((EditText)view.findViewById(R.id.event_description)).setText(schedule.getDescription());
		((EditText)view.findViewById(R.id.select_begin_date)).setText(schedule.getBeginDate());
		((EditText)view.findViewById(R.id.select_begin_time)).setText(schedule.getBeginTime());
		((EditText)view.findViewById(R.id.select_end_date)).setText(schedule.getEndDate());
		((EditText)view.findViewById(R.id.select_end_time)).setText(schedule.getEndTime());
		((EditText)view.findViewById(R.id.reminder_minutes)).setText(""+schedule.getReminderMinutes());
		return view;
	}
	
	private void addEvent(String eventName, String eventDescription,
			int eventBeginDate[], int eventBeginTime[], int eventEndDate[],
			int eventEndTime[], int reminderMinutus) {
		long calId = 1;
		long startMillis = 0;
		long endMillis = 0;
		Calendar beginTime = Calendar.getInstance();
		beginTime.set(eventBeginDate[0], eventBeginDate[1], eventBeginDate[2],
				eventBeginTime[0], eventBeginTime[1]); // 注意：月份系统会自动加1
		startMillis = beginTime.getTimeInMillis();
		Calendar endTime = Calendar.getInstance();
		endTime.set(eventEndDate[0], eventEndDate[1], eventEndDate[2],
				eventEndTime[0], eventEndTime[1]);
		endMillis = endTime.getTimeInMillis();
		Log.v("tt","addEvent dateInt: "+Arrays.toString(eventBeginDate)+" timeInt: "+Arrays.toString(eventBeginTime));

		ContentResolver cr = mContext.getContentResolver(); // 添加新event，步骤是固定的
		ContentValues values = new ContentValues();
		values.put(Events.DTSTART, startMillis);
		values.put(Events.DTEND, endMillis);
		values.put(Events.TITLE, eventName);
		values.put(Events.DESCRIPTION, eventDescription);
		values.put(Events.CALENDAR_ID, calId);
		values.put(Events.EVENT_TIMEZONE, "GMT+8");
		Uri uri = cr.insert(Events.CONTENT_URI, values);
		Long myEventsId = Long.parseLong(uri.getLastPathSegment()); // 获取刚才添加的event的Id

		ContentResolver cr1 = mContext.getContentResolver(); // 为刚才新添加的event添加reminder
		ContentValues values1 = new ContentValues();
		values1.put(Reminders.MINUTES, reminderMinutus);
		values1.put(Reminders.EVENT_ID, myEventsId);
		values1.put(Reminders.METHOD, Reminders.METHOD_ALERT);
		cr1.insert(Reminders.CONTENT_URI, values1); // 调用这个方法返回值是一个Uri

		setAlarmDeal(startMillis); // 设置reminder开始的时候，启动另一个activity

		Toast.makeText(mContext, "插入成功", Toast.LENGTH_SHORT).show();

	}

	private void setAlarmDeal(long time) { // 设置全局定时器
		Intent intent = new Intent(mContext, ScheduleActivity.class);
		PendingIntent pi = PendingIntent.getActivity(mContext, 0, intent, 0);
		AlarmManager aManager = (AlarmManager) mContext.getSystemService(Service.ALARM_SERVICE);
		aManager.set(AlarmManager.RTC_WAKEUP, time, pi); // 当系统调用System.currentTimeMillis()方法返回值与time相同时启动pi对应的组件
		createScheduleSuccessed();
	}
	
	private void saveScheduleData(Schedule schedule){
		final String SP_NAME="schedule";
		SharedPreferences sp=mContext.getSharedPreferences(SP_NAME, Context.MODE_WORLD_WRITEABLE);
		Gson gson=new Gson();
		String data=sp.getString(SP_NAME, "");
		List<Schedule> list=null;
		if("".equals(data)){
			list=new ArrayList<Schedule>();
			list.add(schedule);
		}else{
			list = gson.fromJson(data, new TypeToken<List<Schedule>>(){}.getType());
			boolean bool=true;
			for(int i=0;i<list.size();i++){
				Schedule s=list.get(i);
				if(s.getBeginDate().equals(schedule.getBeginDate())
						&&s.getBeginTime().equals(schedule.getBeginTime())){
					bool=false;
					list.set(i, schedule);
				}
			}
			if(bool){
				list.add(schedule);
			}
		}
		String json=gson.toJson(list, new TypeToken<List<Schedule>>(){}.getType());
		Editor edit=sp.edit();
		edit.putString(SP_NAME, json);
		edit.commit();
	}
	
	private Schedule queryScheduleData(Schedule schedule){
		final String SP_NAME="schedule";
		SharedPreferences sp=mContext.getSharedPreferences(SP_NAME, Context.MODE_WORLD_WRITEABLE);
		String data=sp.getString(SP_NAME, "");
		if(!"".equals(data)){
			Gson gson=new Gson();
			List<Schedule> list=gson.fromJson(data, new TypeToken<List<Schedule>>(){}.getType());
			for(int i=0;i<list.size();i++){
				if(list.get(i).getBeginDate().equals(schedule.getBeginDate())
						&&list.get(i).getBeginTime().equals(schedule.getBeginTime())){
					return list.get(i);
				}
			}
		}
		return null;
	}
}
