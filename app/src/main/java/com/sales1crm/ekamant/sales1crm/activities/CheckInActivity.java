package com.sales1crm.ekamant.sales1crm.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.sales1crm.ekamant.sales1crm.R;
import com.sales1crm.ekamant.sales1crm.activities.callbacks.CheckInCallback;
import com.sales1crm.ekamant.sales1crm.activities.presenters.CheckInPresenter;
import com.sales1crm.ekamant.sales1crm.activities.services.CheckInService;
import com.sales1crm.ekamant.sales1crm.activities.utilities.ApiParam;
import com.sales1crm.ekamant.sales1crm.activities.utilities.PreferenceUtility;
import com.sales1crm.ekamant.sales1crm.activities.utilities.Smart1CrmUtils;
import com.sales1crm.ekamant.sales1crm.activities.widgets.CustomTextView;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by apridosandyasa on 4/12/17.
 */

public class CheckInActivity extends BaseActivity implements CheckInCallback {

    @InjectView(R.id.llHeader)
    LinearLayout llHeader;

    @InjectView(R.id.tvClock)
    CustomTextView tvClock;

    @InjectView(R.id.tvAM)
    CustomTextView tvAM;

    @InjectView(R.id.tvNotice)
    CustomTextView tvNotice;

    @InjectView(R.id.tvCountdownCheckin)
    CustomTextView tvCountdownCheckin;

    @InjectView(R.id.btnCheckin)
    Button btnCheckin;

    @InjectView(R.id.cbRemindMe)
    CheckBox cbRemindMe;

    @InjectView(R.id.ivLogout)
    ImageView ivLogout;

    private int checkIn = 1;
    private boolean isCheckout = false;
    private int hours, minutes, seconds, currenttime, TimerTime, startTime;
    private PendingIntent pendingIntent;
    private AlarmManager am;
    private MyCountDownTimer countdowntimer = null;
    private Thread currentTimeThread = null;
    private CheckInPresenter checkInPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkin);

        ButterKnife.inject(this);

        this.checkInPresenter = new CheckInPresenter(CheckInActivity.this, this);

        Intent intent = new Intent(CheckInActivity.this, CheckInService.class);
        intent.putExtra("alarm", true);
        this.pendingIntent = PendingIntent.getService(CheckInActivity.this,
                Smart1CrmUtils.PENDINGINTENT_LATEALARM, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        this.am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // alarm = new WakeAlarm();

        this.checkIn = PreferenceUtility.getInstance().loadDataInt(CheckInActivity.this,
                PreferenceUtility.CHECKIN);

        this.isCheckout = getIntent().getExtras().getBoolean("is_checkout");

        initView();
        startClockOfCurrenttime();
        convertTime();
    }

    @Override
    protected void onDestroy() {
        countdowntimer.cancel();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        countdowntimer.cancel();
        super.onBackPressed();
    }

    private void initView() {
        btnCheckin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCheckout || checkIn == Smart1CrmUtils.CHECKOUT_ALREADY) {
                    Toast.makeText(CheckInActivity.this, "You already check out for today",
                            Toast.LENGTH_SHORT).show();
                } else {
                    showLoadingDialog();
                    checkInPresenter.setupDoCheckIn(ApiParam.API_004);
                }
            }
        });

        cbRemindMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cbRemindMe.isChecked()) {
                    PreferenceUtility.getInstance().saveData(CheckInActivity.this,
                            PreferenceUtility.REMINDME, true);
                    // alarm.setAlarm(CheckInActivity.this);
                    setAlarm();
                } else {
                    PreferenceUtility.getInstance().saveData(CheckInActivity.this,
                            PreferenceUtility.REMINDME, false);
                    // alarm.cancelAlarm(CheckInActivity.this);
                    am.cancel(pendingIntent);
                }
            }
        });

        ivLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoadingDialog();
                checkInPresenter.setupDoLogout(ApiParam.API_002);
            }
        });
    }

    public void setAlarm() {
        long _alarm = 0;
        am.cancel(pendingIntent);

        int start_hour = PreferenceUtility.getInstance().loadDataInt(CheckInActivity.this,
                PreferenceUtility.START_HOUR);
        int start_minute = PreferenceUtility.getInstance().loadDataInt(CheckInActivity.this,
                PreferenceUtility.START_MINUTE);
        int new_minute = start_minute - 15;
        if (new_minute < 0) {
            start_hour = start_hour - 1;
            start_minute = new_minute + 60;
        } else {
        }

        GregorianCalendar twopm = new GregorianCalendar();
        twopm.set(GregorianCalendar.HOUR_OF_DAY, start_hour);
        twopm.set(GregorianCalendar.MINUTE, start_minute);
        twopm.set(GregorianCalendar.SECOND, 0);
        twopm.set(GregorianCalendar.MILLISECOND, 0);
        if (twopm.before(new GregorianCalendar())) {
            twopm.add(GregorianCalendar.DAY_OF_MONTH, 1);
        }

        // am.setRepeating(AlarmManager.RTC_WAKEUP, twopm.getTimeInMillis(),
        // DateUtils.DAY_IN_MILLIS, pendingIntent);
        am.set(AlarmManager.RTC_WAKEUP, twopm.getTimeInMillis(), pendingIntent);
    }

    private void startClockOfCurrenttime() {
        Runnable myRunnableThread = new CountDownRunner();
        currentTimeThread = new Thread(myRunnableThread);
        currentTimeThread.start();
    }

    private void convertTime() {
        Date time = Calendar.getInstance().getTime();

        int start_hour = PreferenceUtility.getInstance().loadDataInt(CheckInActivity.this,
                PreferenceUtility.START_HOUR);
        int start_minute = PreferenceUtility.getInstance().loadDataInt(CheckInActivity.this,
                PreferenceUtility.START_MINUTE);
        // time up
        startTime = (start_hour * 3600 * 1000) + (start_minute * 60 * 1000);
        // startTime = 16 * 3600 * 1000+(31*60*1000);
        hours = time.getHours() * 3600;
        minutes = time.getMinutes() * 60;
        seconds = time.getSeconds();
        // current time
        currenttime = (hours + minutes + seconds) * 1000;

        // check whether time is already over 8 oclock or not
        if (currenttime - startTime > 0) {
            // time is already over 8oclock
            TimerTime = (24 * 3600 * 1000) - (currenttime - startTime);
        } else {
            // time havent reach 8oclock
            TimerTime = startTime - currenttime;
        }

        if (checkIn == 1) {
            if (currenttime - startTime > 0) {
                // time is already over 8oclock
                countdowntimer = new MyCountDownTimer(currenttime - startTime,
                        1000);
                countdowntimer.setIs_late(true);
                countdowntimer.start();
            } else {
                // time havent reach 8oclock
                countdowntimer = new MyCountDownTimer(TimerTime, 1000);
                countdowntimer.start();
            }
        } else {
            countdowntimer = new MyCountDownTimer(TimerTime, 1000);
            countdowntimer.start();
        }
    }

    private void doWork() {
        runOnUiThread(new Runnable() {
            @SuppressWarnings("deprecation")
            public void run() {
                try {
                    Calendar c = Calendar.getInstance();
                    int hours = c.get(Calendar.HOUR_OF_DAY);
                    int minutes = c.get(Calendar.MINUTE);

                    String Hours = String.valueOf((hours));
                    if (Hours.length() == 1) {
                        Hours = "0" + Hours;
                    }
                    String Minutes = String.valueOf(minutes);
                    if (Minutes.length() == 1) {
                        Minutes = "0" + Minutes;
                    }

                    String curTime = Hours + ":" + Minutes;

                    int AM_orPM = c.get(Calendar.AM_PM);
                    if (AM_orPM == 0) {
                        tvAM.setText("a.m");
                    } else {
                        tvAM.setText("p.m");
                    }

                    tvClock.setText(curTime);
                } catch (Exception e) {
                }
            }
        });
    }

    private class CountDownRunner implements Runnable {
        // @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    doWork();
                    Thread.sleep(1000); // Pause of 1 Second
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                }
            }
        }
    }

    public class MyCountDownTimer extends CountDownTimer {
        private int CountDown;
        private boolean isrunning = false, is_late = false;

        public MyCountDownTimer(int startTime, int interval) {

            super(startTime, interval);
            String pattern = "HH:mm a";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            Date tryDate = new Date(Long.parseLong(String.valueOf(startTime)));
            String formattedDate = simpleDateFormat.format(tryDate);
            this.CountDown = startTime;//(int) tryDate.getTime();
        }

        @Override
        public void onFinish() {
            tvCountdownCheckin.setText("00:00:00");
            cancel();
            isrunning = false;
            is_late = true;

            int start_hour = PreferenceUtility.getInstance().loadDataInt(CheckInActivity.this,
                    PreferenceUtility.START_HOUR);
            int start_minute = PreferenceUtility.getInstance().loadDataInt(CheckInActivity.this,
                    PreferenceUtility.START_MINUTE);

            Date time = Calendar.getInstance().getTime();
            startTime = (start_hour * 3600 * 1000) + (start_minute * 60 * 1000);
            hours = time.getHours() * 3600;
            minutes = time.getMinutes() * 60;
            seconds = time.getSeconds();
            // current time
            currenttime = (hours + minutes + seconds) * 1000;
            CountDown = Math.abs(currenttime - startTime);
            start();
        }

        @Override
        public void onTick(long millisUntilFinished) {
            if (is_late) {
                tvNotice.setTextColor(getResources().getColor(
                        R.color.login_error_message_red));
                tvNotice.setText("YOU ARE LATE");
                tvCountdownCheckin.setTextColor(getResources().getColor(
                        R.color.login_error_message_red));
            }
            String pattern = "HH:mm a";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            Date tryDate = new Date(Long.parseLong(String.valueOf(CountDown)));
            String formattedDate = simpleDateFormat.format(tryDate);
            String Hours = String.valueOf((CountDown / 1000) / 3600);
            if (Hours.length() == 1) {
                Hours = "0" + Hours;
            }
            int minutes_left = ((CountDown / 1000) - (Integer.parseInt(Hours) * 3600)) / 60;
            String Minutes = String.valueOf(minutes_left);
            if (Minutes.length() == 1) {
                Minutes = "0" + Minutes;
            }
            int seconds_left = (((CountDown / 1000) - (Integer.parseInt(Hours) * 3600)) - minutes_left * 60);
            String Seconds = String.valueOf(seconds_left);
            if (Seconds.length() == 1) {
                Seconds = "0" + Seconds;
            }
            tvCountdownCheckin.setText(Hours + ":" + Minutes + ":" + Seconds);

            if (is_late) {
                CountDown += 1000;
            } else {
                CountDown -= 1000;
            }
        }

        public boolean isRunning() {
            return isrunning;
        }

        public boolean isIs_late() {
            return is_late;
        }

        public void setIs_late(boolean is_late) {
            this.is_late = is_late;
        }
    }

    @Override
    public void finishedDoCheckIn(String result, String json) {
        dismissLoadingDialog();
        if (result.equals("OK")) {
            if (countdowntimer != null) {
                countdowntimer.cancel();
            }
            am.cancel(pendingIntent);
            try {
                Smart1CrmUtils.JSONUtility.saveCheckInItemsFromJSON(CheckInActivity.this, json);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //to cancel the currently set alarm for start working, just a precaution
            Intent intent = new Intent(this, CheckInService.class);
            intent.putExtra("alarm_start", true);
            PendingIntent pendingIntent = PendingIntent.getService(this,
                    Smart1CrmUtils.PENDINGINTENT_STARTALARM, intent,
                    PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager am = (AlarmManager) this
                    .getSystemService(Context.ALARM_SERVICE);
            am.cancel(pendingIntent);

            Intent i = new Intent(CheckInActivity.this, MainActivity.class);
            startActivity(i);
            // close this activity
            CheckInActivity.this.finish();
        }else {
            Toast.makeText(CheckInActivity.this, "FAILED TO CHECK IN, oops",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void finishedDoLogout(String result, String json) {
        dismissLoadingDialog();
        if (result.equals("OK")) {
            if (countdowntimer != null) {
                countdowntimer.cancel();
            }
            // cancelling all alarm, remind me, start work, end work
            Intent intent = new Intent(this, CheckInService.class);
            intent.putExtra("alarm", true);
            PendingIntent pendingIntent = PendingIntent.getService(this,
                    Smart1CrmUtils.PENDINGINTENT_LATEALARM, intent,
                    PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager am = (AlarmManager) this
                    .getSystemService(Context.ALARM_SERVICE);

            am.cancel(pendingIntent);

            Intent intentStart = new Intent(this, CheckInService.class);
            intentStart.putExtra("alarm_start", true);
            PendingIntent pendingIntentStart = PendingIntent.getService(this,
                    Smart1CrmUtils.PENDINGINTENT_LATEALARM, intentStart,
                    PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager amStart = (AlarmManager) this
                    .getSystemService(Context.ALARM_SERVICE);

            amStart.cancel(pendingIntentStart);

            Intent intentEnd = new Intent(this, CheckInService.class);
            intentEnd.putExtra("alarm_end", true);
            PendingIntent pendingIntentEnd = PendingIntent.getService(this,
                    Smart1CrmUtils.PENDINGINTENT_LATEALARM, intentEnd,
                    PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager amEnd = (AlarmManager) this
                    .getSystemService(Context.ALARM_SERVICE);

            amEnd.cancel(pendingIntentEnd);

            Intent i = new Intent(CheckInActivity.this, LoginActivity.class);
            startActivity(i);
            // close this activity
            CheckInActivity.this.finish();
        }
    }

}
