package com.doctl.patientcare.main;

import java.io.BufferedReader;
import android.os.AsyncTask;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.sax.StartElementListener;
import android.util.Log;
import android.widget.Toast;
import android.support.v4.app.NotificationCompat;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.conn.ConnectTimeoutException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;


/**
 * Created by Komal on 6/21/2014.
 */
public class MyC2dmReceiver extends BroadcastReceiver {

    //private static String KEY = "c2dmPref";
    //private static String REGISTRATION_KEY = "registrationKey";
    CharSequence tickerText;
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        if (intent.getAction().equals("com.google.android.c2dm.intent.REGISTRATION")) {
            handleRegistration(context, intent);
        } else if (intent.getAction().equals("com.google.android.c2dm.intent.RECEIVE")) {
            handleMessage(context, intent);
        }
    }

    private void handleMessage(Context context2, Intent intent) {
        String a = intent.getAction();
        if (a.equals("com.google.android.c2dm.intent.RECEIVE"))
        {
            String service = intent.getStringExtra("service");
            String type = intent.getStringExtra("type");
            String message = intent.getStringExtra("message");

//            Toast.makeText(context2, service+type+message, Toast.LENGTH_SHORT).show();

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context2).setSmallIcon(R.drawable.ic_launcher).setContentTitle(service).setContentText(message);

            Intent notificationIntent = new Intent(context, MainActivity.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pIntent = PendingIntent.getActivity(context, 0, notificationIntent, Intent.FLAG_ACTIVITY_NEW_TASK);
            Notification notification = mBuilder.build();
            notification.contentIntent = pIntent;

            final int notificationId = 1;
            NotificationManager nm = (NotificationManager) context2.getApplicationContext()
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            nm.notify(notificationId, notification);

//            Launch the popup
            Intent popupIntent = new Intent();
            popupIntent.putExtra("message", "REMINDER");
            popupIntent.setClassName("com.doctl.patientcare.main", "com.doctl.patientcare.main.TransparentActivity");
            popupIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context2.startActivity(popupIntent);
        }
    }


    private void handleRegistration(Context context, Intent intent) {
        String registration = intent.getStringExtra("registration_id");
        if (intent.getStringExtra("error") != null) {

            Log.d("c2dm", "registration failed");

            String error = intent.getStringExtra("error");
            Log.d("c2dm Error", error);
            if(error == "SERVICE_NOT_AVAILABLE"){
                Log.d("c2dm", "SERVICE_NOT_AVAILABLE");
            }else if(error == "ACCOUNT_MISSING"){
                Log.d("c2dm", "ACCOUNT_MISSING");
            }else if(error == "AUTHENTICATION_FAILED"){
                Log.d("c2dm", "AUTHENTICATION_FAILED");
            }else if(error == "TOO_MANY_REGISTRATIONS"){
                Log.d("c2dm", "TOO_MANY_REGISTRATIONS");
            }else if(error == "INVALID_SENDER"){
                Log.d("c2dm", "INVALID_SENDER");
            }else if(error == "PHONE_REGISTRATION_ERROR"){
                Log.d("c2dm", "PHONE_REGISTRATION_ERROR");
            }
        } else if (intent.getStringExtra("unregistered") != null) {

            Log.d("c2dm", "unregistered");
//            Toast.makeText(context,"C2DM unregistered sucessfully",Toast.LENGTH_SHORT).show();

        } else if (registration != null) {
            Log.d("c2dm", registration);

            new WriteGCMRegistrationId().execute(registration);
//            Toast.makeText(context,"Registration Id:"+registration, 10000).show();
        }
    }
}

