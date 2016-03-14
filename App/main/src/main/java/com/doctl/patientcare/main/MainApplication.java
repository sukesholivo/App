package com.doctl.patientcare.main;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by Administrator on 1/29/2015.
 */
public class MainApplication extends Application {
    private static boolean activityVisible;
    private Tracker mTracker;
    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityResumed() {
        activityVisible = true;
    }

    public static void activityPaused() {
        activityVisible = false;
    }

    public synchronized void startTracker(){

        if(mTracker == null){
            GoogleAnalytics ga= GoogleAnalytics.getInstance(this);
            mTracker = ga.newTracker(R.xml.track_app);

            ga.enableAutoActivityReports(this);
            ga.getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
        }
    }

    public Tracker getTracker(){
        startTracker();
        return mTracker;
    }

}
