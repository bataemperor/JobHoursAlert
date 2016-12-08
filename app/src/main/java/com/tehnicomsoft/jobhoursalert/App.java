package com.tehnicomsoft.jobhoursalert;

import android.app.AlarmManager;
import android.app.Application;

import com.squareup.leakcanary.LeakCanary;
import com.tehnicomsoft.jobhoursalert.utility.SettingsManager;


/**
 * Created by aleksandar on 6.12.16..
 */

public class App extends Application {
    private static App app;

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        app = this;
        if (SettingsManager.getInstance().getInterval() == -1)
            SettingsManager.getInstance().setInterval(AlarmManager.INTERVAL_HALF_HOUR);
    }

    public static App getInstance() {
        return app;
    }
}
