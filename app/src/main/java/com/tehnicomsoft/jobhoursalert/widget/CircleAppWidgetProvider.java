package com.tehnicomsoft.jobhoursalert.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.RemoteViews;

import com.tehnicomsoft.jobhoursalert.MainActivity;
import com.tehnicomsoft.jobhoursalert.R;
import com.tehnicomsoft.jobhoursalert.utility.SettingsManager;
import com.tehnicomsoft.jobhoursalert.utility.Utility;
import com.tehnicomsoft.jobhoursalert.view.CircularProgressBar;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by aleksandar on 21.12.16..
 */

public class CircleAppWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int i = 0; i < N; i++) {
            int appWidgetId = appWidgetIds[i];

            // Create an Intent to launch ExampleActivity
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            // Get the layout for the App Widget and attach an on-click listener
            // to the button
            final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.circle_appwidget);
            views.setOnClickPendingIntent(R.id.ll, pendingIntent);
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
            views.setTextViewText(R.id.tvPercentage, title + "%");
            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
