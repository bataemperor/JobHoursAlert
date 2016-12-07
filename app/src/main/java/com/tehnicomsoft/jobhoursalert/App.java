package com.tehnicomsoft.jobhoursalert;

import android.app.Application;


/**
 * Created by aleksandar on 6.12.16..
 */

public class App extends Application {
    private static App app;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
    }

    public static App getInstance() {
        return app;
    }
}
