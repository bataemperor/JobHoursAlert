package com.tehnicomsoft.jobhoursalert;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.SwitchCompat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.tehnicomsoft.jobhoursalert.utility.SettingsManager;
import com.tehnicomsoft.jobhoursalert.utility.Utility;
import com.tehnicomsoft.jobhoursalert.view.CircularProgressBar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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
    Calendar endTime;
    Calendar startTime;
    int numberOfWorkingHours;

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
        endTime = Calendar.getInstance(Locale.getDefault());
        endTime.set(Calendar.HOUR_OF_DAY, endCalendar.get(Calendar.HOUR_OF_DAY));
        endTime.set(Calendar.MINUTE, 0);
        endTime.set(Calendar.SECOND, 0);

        startTime = Calendar.getInstance();
        startTime.set(Calendar.HOUR_OF_DAY, startCalendar.get(Calendar.HOUR_OF_DAY));
        startTime.set(Calendar.MINUTE, startCalendar.get(Calendar.MINUTE));
        startTime.set(Calendar.SECOND, startCalendar.get(Calendar.SECOND));

        tvFrom.setText(getString(R.string.time_from, TIME_FORMAT.format(startCalendar.getTime())));
        tvTo.setText(getString(R.string.time_to, TIME_FORMAT.format(endCalendar.getTime())));
        cpb.setTitleTextSize(20);
        cpb.setSubtitleTextSize(15);

        List<CharSequence> list = new ArrayList<>();
        list.add("30min");
        list.add("1h");
        SpinnerMinutesAdapter dataAdapter = new SpinnerMinutesAdapter(this, 0, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0 && SettingsManager.getInstance().getInterval() == AlarmManager.INTERVAL_HALF_HOUR) {
                    return;
                }
                if (position == 1 && SettingsManager.getInstance().getInterval() == AlarmManager.INTERVAL_HOUR) {
                    return;
                }
                SettingsManager.getInstance().setInterval(position == 0 ? AlarmManager.INTERVAL_HALF_HOUR : AlarmManager.INTERVAL_HOUR);
                if (SettingsManager.getInstance().getNotifications()) {
                    setAlarm(SettingsManager.getInstance().getNotifications());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                calculateProgress();
                handler.postDelayed(this, 1000);
            }
        };
        swNotif.setChecked(SettingsManager.getInstance().getNotifications());
        swNotif.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SettingsManager.getInstance().setNotifications(isChecked);
                setAlarm(isChecked);
                Toast.makeText(MainActivity.this, isChecked ? "Notifications started" : "Notifications stopped", Toast.LENGTH_SHORT).show();
            }
        });
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                DisplayMetrics displaymetrics = new DisplayMetrics();
//                getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
//                int height = displaymetrics.heightPixels;
//                cpb.getLayoutParams().height = height / 4;
//                cpb.getLayoutParams().width = height / 4;
//            }
//        });
        numberOfWorkingHours = Utility.calcNumberOfWorkingHours();
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
                startTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                startTime.set(Calendar.MINUTE, minute);
                tvFrom.setText(getString(R.string.time_from, TIME_FORMAT.format(startCalendar.getTime())));
                SettingsManager.getInstance().setStartTime(startCalendar.getTimeInMillis());
                numberOfWorkingHours = Utility.calcNumberOfWorkingHours();
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
                endTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                endTime.set(Calendar.MINUTE, minute);
                tvTo.setText(getString(R.string.time_to, TIME_FORMAT.format(endCalendar.getTime())));
                SettingsManager.getInstance().setEndTime(endCalendar.getTimeInMillis());
                numberOfWorkingHours = Utility.calcNumberOfWorkingHours();
            }
        }, endCalendar.get(Calendar.HOUR_OF_DAY), endCalendar.get(Calendar.MINUTE), true);
        timePickerDialogEnd.setTitle("Choose end time");
        timePickerDialogEnd.show();
    }

    public void setAlarm(boolean notifications) {
        // Define our intention of executing AlertReceiver
        Intent alertIntent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 1, alertIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        // Allows you to schedule for your application to do something at a later date
        // even if it is in he background or isn't active
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        // set() schedules an alarm to trigger
        // Trigger for alertIntent to fire in 5 seconds
        // FLAG_UPDATE_CURRENT : Update the Intent if active
        if (notifications) {
            Calendar calendar = Calendar.getInstance(Locale.getDefault());
            calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + 1);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            Long alertTime = calendar.getTimeInMillis();
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                    alertTime,
                    getInterval(),
                    pendingIntent
            );
        } else {
            alarmManager.cancel(pendingIntent);
        }
    }

    private long getInterval() {
        long interval = SettingsManager.getInstance().getInterval();
        if (interval != -1) {
            return interval;
        } else return AlarmManager.INTERVAL_HALF_HOUR;
    }

    private void calculateProgress() {
        long currentMilis = System.currentTimeMillis();
        /*
        Ako nije radno vreme prikazi free time
        * */
        if (currentMilis < startTime.getTimeInMillis() || currentMilis > endTime.getTimeInMillis()) {
            cpb.setProgress(0);
            cpb.setTitle("Free time");
            cpb.setSubTitle("");
            return;
        }
        long remainingTime = endTime.getTimeInMillis() - currentMilis;
        if (remainingTime > 0) {
            int seconds = (int) (remainingTime / 1000) % 60;
            int minutes = (int) ((remainingTime / (1000 * 60)) % 60);
            int hours = (int) ((remainingTime / (1000 * 60 * 60)) % 24);
            int progress = getProgress(remainingTime);
            cpb.setProgress(progress);
            cpb.setTitle(getStringRemainingTime(seconds, minutes, hours));
            cpb.setSubTitle(progress + "%");
        } else {
            cpb.setProgress(0);
            cpb.setTitle("Free time");
            cpb.setSubTitle("");
        }
    }

    private int getProgress(long remainingTime) {
        return 100 - (int) (((double) remainingTime / (numberOfWorkingHours * 60 * 60 * 1000) * 100));
    }

    @NonNull
    private String getStringRemainingTime(int seconds, int minutes, int hours) {
        String hoursString = hours < 10 ? "0" + hours : hours + "";
        String minutesString = minutes < 10 ? "0" + minutes : minutes + "";
        String secondsString = seconds < 10 ? "0" + seconds : seconds + "";
        return hoursString + ":" + minutesString + ":" + secondsString;
    }
}

class SpinnerMinutesAdapter extends ArrayAdapter {
    private Context context;
    private List<CharSequence> minutesList;
    private LayoutInflater inflater;

    public SpinnerMinutesAdapter(Context context, int resource, List<CharSequence> itemList) {
        super(context, resource, itemList);
        this.context = context;
        this.minutesList = itemList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.layout_minutes_spinner_item, parent, false);
            holder = new ViewHolder();
            holder.tvMinutes = (TextView) convertView.findViewById(R.id.tvMinutes);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvMinutes.setText(minutesList.get(position));
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.layout_minutes_spinner_item, parent, false);
            holder = new ViewHolder();
            holder.tvMinutes = (TextView) convertView.findViewById(R.id.tvMinutes);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvMinutes.setText(minutesList.get(position));
        return convertView;
    }

    class ViewHolder {
        TextView tvMinutes;
    }
}
