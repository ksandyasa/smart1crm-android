package com.sales1crm.ekamant.sales1crm.activities.utilities;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import com.sales1crm.ekamant.sales1crm.activities.services.CheckInService;
import com.sales1crm.ekamant.sales1crm.activities.services.LocationSendService;
import com.sales1crm.ekamant.sales1crm.activities.services.SendTaskDataService;

import java.util.Calendar;

public class WakeAlarm extends BroadcastReceiver {

	// public static WakeAlarm instances = new WakeAlarm();
	private Context context;

	// public WakeAlarm(){
	// }
	//
	// public static WakeAlarm getInstanceof(){
	// return instances;
	// }

	@SuppressLint("NewApi")
	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		PowerManager pm = (PowerManager) context
				.getSystemService(Context.POWER_SERVICE);
		PowerManager.WakeLock wl = pm.newWakeLock(
				PowerManager.PARTIAL_WAKE_LOCK, "");
		wl.acquire();
		Smart1CrmUtils.today_date = Calendar.getInstance().getTime();

		// Put here YOUR code.
		Toast.makeText(context, "Alarm !!!!!!!!!!", Toast.LENGTH_LONG).show(); // For
																				// example
		// SetAlarmForCheckInService(context);
		wl.release();
	}

	public void setAlarm(Context context) {
		long _alarm = 0;
		Calendar now = Calendar.getInstance();
		Calendar alarm = Calendar.getInstance();
		alarm.set(Calendar.HOUR_OF_DAY, 6);
		alarm.set(Calendar.MINUTE, 55);
		alarm.set(Calendar.SECOND, 00);

		if (alarm.getTimeInMillis() <= now.getTimeInMillis()) {
			_alarm = alarm.getTimeInMillis() + (AlarmManager.INTERVAL_DAY + 1);
			Log.i("AAA", "masuk <" + _alarm);
		} else {
			_alarm = alarm.getTimeInMillis();
			Log.i("AAA", "masuk else" + _alarm);
		}
		// Create a new PendingIntent and add it to the AlarmManager
		Intent intent = new Intent(context, CheckInService.class);
		PendingIntent pendingIntent = PendingIntent.getService(context,
				Smart1CrmUtils.PENDINGINTENT_LATEALARM, intent,
				PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		am.cancel(pendingIntent);
		am.set(AlarmManager.RTC_WAKEUP, _alarm, pendingIntent);
		// am.setRepeating(AlarmManager.RTC_WAKEUP, _alarm,
		// AlarmManager.INTERVAL_DAY, pendingIntent);
	}

	public void cancelAlarm(Context context) {
		Log.i("AAA", "cancel alarm : ");
		Intent intent = new Intent(context, CheckInService.class);
		PendingIntent pendingIntent = PendingIntent.getService(context,
				Smart1CrmUtils.PENDINGINTENT_LATEALARM, intent,
				PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);

		am.cancel(pendingIntent);
	}

	public void SetAlarmForGeoService(Context context) {
		Log.i("TAG", "Start Alarm");
		Smart1CrmUtils.service_running = true;
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(context, LocationSendService.class);
		i.putExtra("send_location", true);
		PendingIntent pi = PendingIntent.getService(context,
				Smart1CrmUtils.PENDINGINTENT_LOCATION, i, PendingIntent.FLAG_CANCEL_CURRENT);
		// PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
		am.cancel(pi);
		am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
				1000 * 60 * 5, pi); // Millisec * Second * Minute
	}

	public void sendDataIfConnected(Context context, String customerId) {
		Log.d("TAG", "Start Send Task Data");
		Log.d("TAG", "customerId " + customerId);
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(context, SendTaskDataService.class);
		i.putExtra("send_taskdata", true);
		i.putExtra("customerId", customerId);
		PendingIntent pi = PendingIntent.getService(context,
				Smart1CrmUtils.PENDINGINTENT_TASKDATA, i, PendingIntent.FLAG_CANCEL_CURRENT);
		// PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
		am.cancel(pi);
		am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
				1000 * 1, pi); // Millisec * Second * Minute
	}

	public void stopSendTaskData(Context context) {
		Intent intent = new Intent(context, SendTaskDataService.class);
		PendingIntent pendingIntent = PendingIntent.getService(context,
				Smart1CrmUtils.PENDINGINTENT_TASKDATA, intent,
				PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);

		am.cancel(pendingIntent);
	}
}
