package com.tehnicomsoft.jobhoursalert.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.tehnicomsoft.jobhoursalert.MainActivity;
import com.tehnicomsoft.jobhoursalert.R;
import com.tehnicomsoft.jobhoursalert.utility.SettingsManager;
import com.tehnicomsoft.jobhoursalert.utility.Utility;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by aleksandar on 21.12.16..
 */

public class CircleAppWidgetProvider extends AppWidgetProvider {
    public static String WIDGET_BUTTON = "com.tehnicomsoft.jobhoursalert.WIDGET_BUTTON";


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int i = 0; i < N; i++) {
            int appWidgetId = appWidgetIds[i];

            // Create an Intent to launch ExampleActivity
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            Intent intent2 = new Intent(WIDGET_BUTTON);
            PendingIntent pendingIntent2 = PendingIntent.getBroadcast(context, 0, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
            // Get the layout for the App Widget and attach an on-click listener
            // to the button
            final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.circle_appwidget);
            views.setOnClickPendingIntent(R.id.ll, pendingIntent);
            views.setOnClickPendingIntent(R.id.tvPercentage, pendingIntent2);
            String title = getString();
            views.setTextViewText(R.id.tvPercentage, title + "%");
            // Tell the AppWidgetManager to perform an update on the current app widget

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }


    }

    @NonNull
    private String getString() {
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTimeInMillis(SettingsManager.getInstance().getEndTime());
        Calendar endTime = Calendar.getInstance(Locale.getDefault());
        endTime.set(Calendar.HOUR_OF_DAY, endCalendar.get(Calendar.HOUR_OF_DAY));
        endTime.set(Calendar.MINUTE, 0);
        endTime.set(Calendar.SECOND, 0);

        long remainingTime = endTime.getTimeInMillis() - System.currentTimeMillis();
        String title;
        if (remainingTime > 0) {
            title = String.valueOf(Utility.getProgress(remainingTime, Utility.calcNumberOfWorkingHours()));
        } else {
            title = "Free time";
        }
        return title;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (WIDGET_BUTTON.equals(intent.getAction())) {
            final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.circle_appwidget);
            final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            final ComponentName componentName = new ComponentName(context, CircleAppWidgetProvider.class);
            Handler handler = new Handler();
            views.setViewVisibility(R.id.pb, View.VISIBLE);
            views.setTextViewText(R.id.tvPercentage, "");
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    views.setViewVisibility(R.id.pb, View.GONE);
                    views.setTextViewText(R.id.tvPercentage, getString() + "%");
                    appWidgetManager.updateAppWidget(componentName, views);
                }
            }, 1000);
            appWidgetManager.updateAppWidget(componentName, views);
        }
    }
}
