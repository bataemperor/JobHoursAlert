package com.tehnicomsoft.jobhoursalert.utility;

import java.util.Calendar;

/**
 * Created by aleksandar on 8.12.16..
 */

public class Utility {
    public static int calcNumberOfWorkingHours() {
        Calendar calendarEnd = Calendar.getInstance();
        Calendar calenderStart = Calendar.getInstance();
        calenderStart.setTimeInMillis(SettingsManager.getInstance().getStartTime());
        calendarEnd.setTimeInMillis(SettingsManager.getInstance().getEndTime());
        return calendarEnd.get(Calendar.HOUR_OF_DAY) - calenderStart.get(Calendar.HOUR_OF_DAY);
    }

    public static boolean isWeekend(Calendar calendarCurrent) {
        return calendarCurrent.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
                || calendarCurrent.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
    }
}
