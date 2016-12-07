package com.tehnicomsoft.jobhoursalert;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import com.tehnicomsoft.jobhoursalert.utility.SettingsManager;

import java.util.Calendar;

/**
 * Created by aleksandar on 22.11.16..
 */

public class AlertReceiver extends BroadcastReceiver {
    Calendar calendarCurrent, calenderStart, calendarEnd;
    int difference;

    // Called when a broadcast is made targeting this class
    @Override
    public void onReceive(Context context, Intent intent) {
        if (conditions()) {
            createNotification(context, "Vreme na poslu", "Jos " + difference + "h " + "do kraja radnog vremena", "Alert");
        }
    }

    /**
     * Only between working hours
     * Not Saturday , Not Sunday
     */
    private boolean conditions() {
        calendarCurrent = Calendar.getInstance();
        calenderStart = Calendar.getInstance();
        calenderStart.setTimeInMillis(SettingsManager.getInstance().getStartTime());
        calendarEnd = Calendar.getInstance();
        calendarEnd.setTimeInMillis(SettingsManager.getInstance().getEndTime());
        difference = calendarEnd.get(Calendar.HOUR_OF_DAY) - calendarCurrent.get(Calendar.HOUR_OF_DAY);
        int numberOfWorkingHoursInOneDay = calendarEnd.get(Calendar.HOUR_OF_DAY) - calenderStart.get(Calendar.HOUR_OF_DAY);
        return difference >= 0 && difference <= numberOfWorkingHoursInOneDay
                && calendarCurrent.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY
                && calendarCurrent.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY;
    }

    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.ic_notifications_white_24dp : R.drawable.ic_notifications_white_24dp;
    }

    private int getLargeIcon() {
        switch (difference) {
            case 8:
                return R.drawable.bored;
            case 7:
                return R.drawable.very_angry;
            case 6:
                return R.drawable.angry;
            case 5:
                return R.drawable.mad;
            case 4:
                return R.drawable.sad;
            case 3:
                return R.drawable.happy;
            case 2:
                return R.drawable.so_hapy;
            case 1:
                return R.drawable.very_happy;
            case 0:
                return R.drawable.in_love;
            default:
                return R.drawable.happy;
        }
    }

    public void createNotification(Context context, String msg, String msgText, String msgAlert) {

        // Define an Intent and an action to perform with it by another application
        PendingIntent notificIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, MainActivity.class), 0);

        // Builds a notification
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(getNotificationIcon())
                        .setContentTitle(msg)
                        .setTicker(msgAlert)
                        .setContentText(msgText)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), getLargeIcon()));


        // Defines the Intent to fire when the notification is clicked
        mBuilder.setContentIntent(notificIntent);

        // Set the default notification option
        // DEFAULT_SOUND : Make sound
        // DEFAULT_VIBRATE : Vibrate
        // DEFAULT_LIGHTS : Use the default light notification
        mBuilder.setDefaults(Notification.DEFAULT_SOUND);

        // Auto cancels the notification when clicked on in the task bar
        mBuilder.setAutoCancel(true);

        // Gets a NotificationManager which is used to notify the user of the background event
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Post the notification
        mNotificationManager.notify(1, mBuilder.build());

    }
}
