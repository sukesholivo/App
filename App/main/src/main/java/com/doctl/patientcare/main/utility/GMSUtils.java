package com.doctl.patientcare.main.utility;

import android.app.Application;

import com.doctl.patientcare.main.MainApplication;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by Satya Madala on 14/3/16.
 * email : satya.madala@olivo.in
 */
public class GMSUtils {

    public static void sendScreenView(Application application, String screenName){
        if(application instanceof MainApplication){
            MainApplication mainApplication = (MainApplication)application;
            Tracker tracker = mainApplication.getTracker();
            tracker.setScreenName(screenName);
            tracker.send(new HitBuilders.ScreenViewBuilder().build());
        }
    }
}
