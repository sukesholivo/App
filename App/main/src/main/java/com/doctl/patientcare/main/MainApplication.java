package com.doctl.patientcare.main;

import android.app.Application;

/**
 * Created by Administrator on 1/29/2015.
 */
public class MainApplication extends Application {
    private static boolean activityVisible;

    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityResumed() {
        activityVisible = true;
    }

    public static void activityPaused() {
        activityVisible = false;
    }
}
