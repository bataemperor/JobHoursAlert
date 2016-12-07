package com.tehnicomsoft.jobhoursalert.utility;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.tehnicomsoft.jobhoursalert.App;

/**
 * Created by aleksandar on 6.12.16..
 * Handles all reads and writes to SharedPreferences
 */

public class SettingsManager {
    private static SettingsManager instance = new SettingsManager();
    private static final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(App.getInstance());

    private enum KEY {
        START_TIME, END_TIME, NOTIFICATIONS, INTERVAL
    }

    private SettingsManager() {
    }

    public static SettingsManager getInstance() {
        return instance;
    }

    public void setStartTime(long time) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(KEY.START_TIME.toString(), time);
        editor.apply();
    }

    public long getStartTime() {
        return prefs.getLong(KEY.START_TIME.toString(), -1);
    }

    public void setEndTime(long time) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(KEY.END_TIME.toString(), time);
        editor.apply();
    }

    public long getEndTime() {
        return prefs.getLong(KEY.END_TIME.toString(), -1);
    }

    public void setNotifications(boolean notifications) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY.NOTIFICATIONS.toString(), notifications);
        editor.apply();
    }

    public boolean getNotifications() {
        return prefs.getBoolean(KEY.NOTIFICATIONS.toString(), false);
    }

    public void setInterval(int interval) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY.INTERVAL.toString(), interval);
        editor.apply();
    }

    public int getInterval() {
        return prefs.getInt(KEY.INTERVAL.toString(), -1);
    }

}
