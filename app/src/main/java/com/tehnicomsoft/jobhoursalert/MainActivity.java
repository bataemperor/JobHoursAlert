package com.tehnicomsoft.jobhoursalert;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.tehnicomsoft.jobhoursalert.utility.SettingsManager;
import com.tehnicomsoft.jobhoursalert.view.CircularProgressBar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * NotificationManager : Allows us to notify the user that something happened in the background
 * AlarmManager : Allows you to schedule for your application to do something at a later date
 * even if it is in the background
 */

public class MainActivity extends AppCompatActivity {
    public static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm", Locale.getDefault());
    @BindView(R.id.tvFrom)
    TextView tvFrom;
    @BindView(R.id.tvTo)
    TextView tvTo;
    @BindView(R.id.swNotif)
    SwitchCompat swNotif;
    @BindView(R.id.spInterval)
    AppCompatSpinner spinner;
    @BindView(R.id.cpb)
    CircularProgressBar cpb;
    TimePickerDialog timePickerDialogStart, timePickerDialogEnd;
    Calendar startCalendar, endCalendar;
    Handler handler;
    Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        startCalendar = Calendar.getInstance(Locale.getDefault());
        endCalendar = Calendar.getInstance(Locale.getDefault());
        if (SettingsManager.getInstance().getStartTime() != -1) {
            startCalendar.setTimeInMillis(SettingsManager.getInstance().getStartTime());
        }
        if (SettingsManager.getInstance().getEndTime() != -1) {
            endCalendar.setTimeInMillis(SettingsManager.getInstance().getEndTime());
        }
        if (SettingsManager.getInstance().getInterval() != -1) {
        }
        if (SettingsManager.getInstance().getNotifications()) {
        }

        tvFrom.setText(getString(R.string.time_from, TIME_FORMAT.format(startCalendar.getTime())));
        tvTo.setText(getString(R.string.time_to, TIME_FORMAT.format(endCalendar.getTime())));
        cpb.setProgress(60);
        cpb.setTitle("40%");
        cpb.setTitleTextSize(80);
//        cpb.setSubTitle("done");
//        cpb.setSubtitleTextSize(60);
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                calculateProgress();
                handler.post(this);
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.post(runnable);
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacks(runnable);
    }

    @OnClick(R.id.tvFrom)
    public void onClickFrom() {
        timePickerDialogStart = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                startCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                startCalendar.set(Calendar.MINUTE, minute);
                tvFrom.setText(getString(R.string.time_from, TIME_FORMAT.format(startCalendar.getTime())));
                SettingsManager.getInstance().setStartTime(startCalendar.getTimeInMillis());
            }
        }, startCalendar.get(Calendar.HOUR_OF_DAY), startCalendar.get(Calendar.MINUTE), true);
        timePickerDialogStart.setTitle("Choose start time");
        timePickerDialogStart.show();
    }

    @OnClick(R.id.tvTo)
    public void onClickTo() {
        timePickerDialogEnd = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                endCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                endCalendar.set(Calendar.MINUTE, minute);
                tvTo.setText(getString(R.string.time_to, TIME_FORMAT.format(endCalendar.getTime())));
                SettingsManager.getInstance().setEndTime(endCalendar.getTimeInMillis());

            }
        }, endCalendar.get(Calendar.HOUR_OF_DAY), endCalendar.get(Calendar.MINUTE), true);
        timePickerDialogEnd.setTitle("Choose end time");
        timePickerDialogEnd.show();
    }

    public void setAlarm(View view) {

        // Define our intention of executing AlertReceiver
        Intent alertIntent = new Intent(this, AlertReceiver.class);

        // Allows you to schedule for your application to do something at a later date
        // even if it is in he background or isn't active
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // set() schedules an alarm to trigger
        // Trigger for alertIntent to fire in 5 seconds
        // FLAG_UPDATE_CURRENT : Update the Intent if active
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + 1);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Long alertTime = calendar.getTimeInMillis();
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, alertTime, AlarmManager.INTERVAL_HOUR,
                PendingIntent.getBroadcast(getApplicationContext(), 1, alertIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT));
        Toast.makeText(this, "Notifications started", Toast.LENGTH_SHORT).show();
    }

    private void calculateProgress() {
        Calendar currentTime = Calendar.getInstance(Locale.getDefault());
        Calendar endTime = Calendar.getInstance(Locale.getDefault());
        endTime.set(Calendar.HOUR_OF_DAY, endCalendar.get(Calendar.HOUR_OF_DAY));
        endTime.set(Calendar.MINUTE, 0);
        endTime.set(Calendar.SECOND, 0);
        long remainingTime = endTime.getTime().getTime() - currentTime.getTime().getTime();
        if (remainingTime > 0) {
            int seconds = (int) (remainingTime / 1000) % 60;
            int minutes = (int) ((remainingTime / (1000 * 60)) % 60);
            int hours = (int) ((remainingTime / (1000 * 60 * 60)) % 24);
            cpb.setProgress(100 - (int) (((double) remainingTime / (8 * 60 * 60 * 1000) * 100)));
            cpb.setTitle("Jos :\n" + hours + ":" + minutes + ":" + seconds);
        } else {

        }
    }
}
