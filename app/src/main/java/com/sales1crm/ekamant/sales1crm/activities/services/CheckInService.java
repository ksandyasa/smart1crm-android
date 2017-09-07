/*
 * Copyright 2017 Matthew Lim
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sales1crm.ekamant.sales1crm.activities.services;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.sales1crm.ekamant.sales1crm.R;
import com.sales1crm.ekamant.sales1crm.activities.CheckInActivity;
import com.sales1crm.ekamant.sales1crm.activities.utilities.PreferenceUtility;
import com.sales1crm.ekamant.sales1crm.activities.utilities.Smart1CrmUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * TODO: Add a class Header comment!
 */

public class CheckInService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        if (intent != null) {
            if (intent.getExtras().containsKey("alarm")) {
                if (intent.getExtras().getBoolean("alarm")) {
                    sendNotification();
                }
            }
            if (intent.getExtras().containsKey("alarm_end")) {
                if (intent.getExtras().getBoolean("alarm_end")) {
                    //sendNotificationEnd();
                }
            }
            if (intent.getExtras().containsKey("alarm_start")) {
                if (intent.getExtras().getBoolean("alarm_start")) {
                    sendNotificationStart();
                }
            }
        }
        // makeAlarm();
        // alarm = new WakeAlarm();
        // makeAlarmNew();
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        Log.i("HUAI", "oncreate service");
    }

    @SuppressLint("NewApi")
    private void sendNotification() {
        Log.i("HUAI", "notif create");
        Intent intent = new Intent(this, CheckInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP); // To open only one activity
        // on launch.
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        // Build notification
        // Actions are just fake
        Notification noti = new Notification.Builder(this)
                .setContentTitle("You are almost late Quickly Check In")
                .setContentText("Click Here")
                .setSmallIcon(R.drawable.ic_msf).setContentIntent(pIntent)
                .build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // hide the notification after its selected
        noti.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(0, noti);
        Log.i("HUAI", "notif sended");

        setAlarm();
        // WakeAlarm alarm = new WakeAlarm();
        // alarm.setAlarm(this);
        // makeAlarm();
        // WakeAlarm.getInstanceof().SetAlarmForCheckInService(this);
    }

    /*@SuppressLint("NewApi")
    private void sendNotificationEnd() {
        Log.i("HUAI", "notif create");
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP); // To open only one activity
        // on launch.
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        // Build notification
        // Actions are just fake
        Notification noti = new Notification.Builder(this)
                .setContentTitle("Time to check out")
                .setContentText("Click Here to check out")
                .setSmallIcon(R.drawable.ic_msf).setContentIntent(pIntent)
                .build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // hide the notification after its selected
        noti.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(0, noti);
        Log.i("HUAI", "notif sended");

        setAlarmEnd();
        // WakeAlarm alarm = new WakeAlarm();
        // alarm.setAlarm(this);
        // makeAlarm();
        // WakeAlarm.getInstanceof().SetAlarmForCheckInService(this);
    }*/

    @SuppressLint("NewApi")
    private void sendNotificationStart() {
        Log.i("HUAI", "notif create");
        Intent intent = new Intent(this, CheckInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP); // To open only one activity
        // on launch.
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        // Build notification
        // Actions are just fake
        Notification noti = new Notification.Builder(this)
                .setContentTitle("Work Time")
                .setContentText("Let's start working!")
                .setSmallIcon(R.drawable.ic_msf).setContentIntent(pIntent)
                .build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // hide the notification after its selected
        noti.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(0, noti);
        Log.i("HUAI", "notif sended");

        setAlarmStart();
        // WakeAlarm alarm = new WakeAlarm();
        // alarm.setAlarm(this);
        // makeAlarm();
        // WakeAlarm.getInstanceof().SetAlarmForCheckInService(this);
    }

    public void setAlarm() {
        Log.i("TAG", "MASUK SET ALARM");
        long _alarm = 0;
        // Calendar now = Calendar.getInstance();
        // Calendar alarm = Calendar.getInstance();
        // alarm.set(Calendar.HOUR_OF_DAY, 7);
        // alarm.set(Calendar.MINUTE, 55);
        // alarm.set(Calendar.SECOND, 00);

        Intent intent = new Intent(this, CheckInService.class);
        intent.putExtra("alarm", true);
        PendingIntent pendingIntent = PendingIntent.getService(this,
                Smart1CrmUtils.PENDINGINTENT_LATEALARM, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager am = (AlarmManager) this
                .getSystemService(Context.ALARM_SERVICE);

        am.cancel(pendingIntent);

        int start_hour = PreferenceUtility.getInstance().loadDataInt(this,
                PreferenceUtility.START_HOUR);
        int start_minute = PreferenceUtility.getInstance().loadDataInt(this,
                PreferenceUtility.START_MINUTE);
        int new_minute = start_minute - 15;
        if (new_minute < 0) {
            start_hour = start_hour - 1;
            start_minute = new_minute + 60;
            Log.i("AAA", "start hour: " + start_hour + "," + start_minute);
        } else {
            Log.i("AAA", "else start hour: " + start_hour + "," + start_minute);
        }

        GregorianCalendar twopm = new GregorianCalendar();
        twopm.set(GregorianCalendar.HOUR_OF_DAY, start_hour);
        twopm.set(GregorianCalendar.MINUTE, start_minute);
        twopm.set(GregorianCalendar.SECOND, 0);
        twopm.set(GregorianCalendar.MILLISECOND, 0);
        if (twopm.before(new GregorianCalendar())) {
            twopm.add(GregorianCalendar.DAY_OF_MONTH, 1);
        }

        Log.i("AAA", "ALARM- timeleft in service : " + twopm.getTimeInMillis());

        am.set(AlarmManager.RTC_WAKEUP, twopm.getTimeInMillis(), pendingIntent);
    }

    public void setAlarmStart() {
        Log.i("TAG", "MASUK SET ALARM");
        long _alarm = 0;
        // Calendar now = Calendar.getInstance();
        // Calendar alarm = Calendar.getInstance();
        // alarm.set(Calendar.HOUR_OF_DAY, 7);
        // alarm.set(Calendar.MINUTE, 55);
        // alarm.set(Calendar.SECOND, 00);

        Intent intent = new Intent(this, CheckInService.class);
        intent.putExtra("alarm_start", true);
        PendingIntent pendingIntent = PendingIntent.getService(this,
                Smart1CrmUtils.PENDINGINTENT_STARTALARM, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager am = (AlarmManager) this
                .getSystemService(Context.ALARM_SERVICE);

        am.cancel(pendingIntent);

        int start_hour = PreferenceUtility.getInstance().loadDataInt(this,
                PreferenceUtility.START_HOUR);
        int start_minute = PreferenceUtility.getInstance().loadDataInt(this,
                PreferenceUtility.START_MINUTE);
        Log.i("AAA", "else start hour: " + start_hour + "," + start_minute);

        GregorianCalendar twopm = new GregorianCalendar();
        twopm.set(GregorianCalendar.HOUR_OF_DAY, start_hour);
        twopm.set(GregorianCalendar.MINUTE, start_minute);
        twopm.set(GregorianCalendar.SECOND, 0);
        twopm.set(GregorianCalendar.MILLISECOND, 0);
        if (twopm.before(new GregorianCalendar())) {
            twopm.add(GregorianCalendar.DAY_OF_MONTH, 1);
        }

        Log.i("AAA", "ALARM- timeleft in service : " + twopm.getTimeInMillis());

        am.set(AlarmManager.RTC_WAKEUP, twopm.getTimeInMillis(), pendingIntent);
    }

    public void setAlarmEnd() {
        Log.i("TAG", "MASUK SET ALARM");
        long _alarm = 0;
        // Calendar now = Calendar.getInstance();
        // Calendar alarm = Calendar.getInstance();
        // alarm.set(Calendar.HOUR_OF_DAY, 7);
        // alarm.set(Calendar.MINUTE, 55);
        // alarm.set(Calendar.SECOND, 00);

        Intent intent = new Intent(this, CheckInService.class);
        intent.putExtra("alarm_end", true);
        PendingIntent pendingIntent = PendingIntent.getService(this,
                Smart1CrmUtils.PENDINGINTENT_ENDALARM, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager am = (AlarmManager) this
                .getSystemService(Context.ALARM_SERVICE);

        am.cancel(pendingIntent);

        int end_hour = PreferenceUtility.getInstance().loadDataInt(this,
                PreferenceUtility.END_HOUR);
        int end_minute = PreferenceUtility.getInstance().loadDataInt(this,
                PreferenceUtility.END_MINUTE);
        Log.i("AAA", "else end hour: " + end_hour + "," + end_minute);

        GregorianCalendar twopm = new GregorianCalendar();
        twopm.set(GregorianCalendar.HOUR_OF_DAY, end_hour);
        twopm.set(GregorianCalendar.MINUTE, end_minute);
        twopm.set(GregorianCalendar.SECOND, 0);
        twopm.set(GregorianCalendar.MILLISECOND, 0);
        if (twopm.before(new GregorianCalendar())) {
            twopm.add(GregorianCalendar.DAY_OF_MONTH, 1);
        }

        Log.i("AAA", "ALARM- timeleft in service : " + twopm.getTimeInMillis());

        am.set(AlarmManager.RTC_WAKEUP, twopm.getTimeInMillis(), pendingIntent);
    }

    @SuppressLint("NewApi")
    private void makeAlarm() {
        Date time = Calendar.getInstance().getTime();

        // time up
        long startTime = ((11 * 3600) + 1320) * 1000;
        long hours = time.getHours() * 3600;
        long minutes = time.getMinutes() * 60;
        long seconds = time.getSeconds();

        Log.i("HUAI", "hours : " + time.getHours());
        // current time
        long currenttime = (hours + minutes + seconds) * 1000;
        Log.i("HUAI", "hours start time : " + startTime);
        Log.i("HUAI", "hours current time : " + currenttime);
        long TimerTime = 0;
        // check whether time is already over 8 oclock or not
        if (currenttime - startTime > 0) {
            // time is already over 8oclock
            TimerTime = (24 * 3600 * 1000) - (currenttime - startTime);
            Log.i("TAG", "timertime + : " + TimerTime);
        } else {
            // time havent reach 8oclock
            TimerTime = startTime - currenttime;
            Log.i("TAG", "timer - : " + TimerTime);
        }
        // return TimerTime;
        AlarmManager am = (AlarmManager) this
                .getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(this, CheckInService.class);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        // PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        // am.setRepeating(AlarmManager.RTC_WAKEUP, calculateRemainingTime(),
        // 3600 * 1000 * 1, pi); // Millisec * Second * Minute
        am.set(AlarmManager.RTC_WAKEUP, TimerTime, pi);
    }

    private long calculateRemainingTime() {
        Calendar calendar = Calendar.getInstance();
        // 7:55 AM
        calendar.set(Calendar.HOUR_OF_DAY, 10);
        calendar.set(Calendar.MINUTE, 42);
        calendar.set(Calendar.SECOND, 00);
        calendar.set(Calendar.MILLISECOND, 00);
        // calendar.set(Calendar.AM, 0);

        Date time = Calendar.getInstance().getTime();

        // time up
        long startTime = 8 * 3600 * 1000;
        long hours = time.getHours() * 3600;
        long minutes = time.getMinutes() * 60;
        long seconds = time.getSeconds();
        Log.i("HUAI", "hours : " + time.getHours());
        // current time
        long currenttime = (hours + minutes + seconds) * 1000;
        long TimerTime = 0;
        // check whether time is already over 8 oclock or not
        if (currenttime - startTime > 0) {
            // time is already over 8oclock
            TimerTime = (24 * 3600 * 1000) - (currenttime - startTime);
            Log.i("TAG", "timertime + : " + TimerTime);
        } else {
            // time havent reach 8oclock
            TimerTime = startTime - currenttime;
            Log.i("TAG", "timer - : " + TimerTime);
        }
        return TimerTime;
    }

}