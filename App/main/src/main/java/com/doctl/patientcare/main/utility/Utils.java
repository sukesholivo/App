package com.doctl.patientcare.main.utility;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.util.DisplayMetrics;

import com.doctl.patientcare.main.Cards.CardHeaderInnerView;
import com.doctl.patientcare.main.MainActivity;
import com.doctl.patientcare.main.R;
import com.doctl.patientcare.main.StartPageActivity;
import com.doctl.patientcare.main.om.BaseTask;
import com.doctl.patientcare.main.om.GraphData;
import com.doctl.patientcare.main.om.UserProfile;
import com.doctl.patientcare.main.om.education.EducationTask;
import com.doctl.patientcare.main.om.followup.FollowupTask;
import com.doctl.patientcare.main.om.medicines.MedicineTask;
import com.doctl.patientcare.main.om.message.MessageTask;
import com.doctl.patientcare.main.om.vitals.VitalTask;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

import it.gmariotti.cardslib.library.internal.CardHeader;

/**
 * Created by Administrator on 7/28/2014.
 */
public final class Utils {
    public static Date parseFromString(String iso8601string) {
        try {
            String s = iso8601string.replace("Z", "+00:00");
            try {
                s = s.substring(0, 22) + s.substring(23);
            } catch (IndexOutOfBoundsException e) {
                throw new ParseException("Invalid length", 0);
            }
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").parse(s);
        } catch (ParseException e){
            e.printStackTrace();
        }
        return null;
    }

    public static String getDateString(Date date) {
        return new SimpleDateFormat("dd MMM yyyy").format(date);
    }

    public static String getIsoDateString(Calendar date) {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(date.getTime());
    }

    public static List<NameValuePair> getCardsHTTPGetQueryParam(){
        Calendar startDate = new GregorianCalendar();
        startDate.set(Calendar.HOUR_OF_DAY, 0);
        startDate.set(Calendar.MINUTE, 0);
        startDate.set(Calendar.SECOND, 0);
        startDate.set(Calendar.MILLISECOND, 0);

        Calendar endDate = new GregorianCalendar();
        endDate.set(Calendar.HOUR_OF_DAY, 0);
        endDate.set(Calendar.MINUTE, 0);
        endDate.set(Calendar.SECOND, 0);
        endDate.set(Calendar.MILLISECOND, 0);
        endDate.add(Calendar.DAY_OF_MONTH, 1);

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("startDate", Utils.getIsoDateString(startDate)));
        params.add(new BasicNameValuePair("endDate", Utils.getIsoDateString(endDate)));
        params.add(new BasicNameValuePair("orderBy", "eta"));
        params.add(new BasicNameValuePair("state", "UNSEEN"));
        return params;
    }

    public static CardHeader getCardHeader(Context context, BaseTask task){
        CardHeader cardHeader;
        boolean pastTask = false;
        int seconds  = (int)( (task.getETA().getTime() - new Date().getTime())/1000);
        if (seconds < 0) { // future tasks
            seconds = 0 - seconds;
            pastTask = true;
        }
        if (task.getEtaType() == BaseTask.TypeOfEta.DAYLONG) {
            cardHeader = new CardHeaderInnerView(context, "" + "TODAY", "", "");
        } else {
            if (seconds < 60) { // if time is less than 1 min
                cardHeader = new CardHeaderInnerView(context, "NOW", "", "");
            }else if (seconds < 60 * 60) { // if time is less than 1 hour
                cardHeader = new CardHeaderInnerView(context, "" + seconds / 60, "MINS " + (pastTask ? "AGO" : "FROM NOW"), "");
            } else if (seconds < 60 * 60 * 24) { // if time is less than 1 day
                cardHeader = new CardHeaderInnerView(context, "" + seconds / (60 * 60), "HRS " + (pastTask ? "AGO" : "FROM NOW"), "");
            } else { // if time is more than 1 day
                cardHeader = new CardHeaderInnerView(context, "" + seconds / (60 * 60 * 24), "DAYS " + (pastTask ? "AGO" : ""), "");
            }
        }
        return cardHeader;
    }

    public static String parsonJsonFromFile(Context context,int resourceId){
        InputStream inputStream = context.getResources().openRawResource(resourceId);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int ctr;
        try {
            ctr = inputStream.read();
            while (ctr != -1) {
                byteArrayOutputStream.write(ctr);
                ctr = inputStream.read();
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArrayOutputStream.toString();
    }

    public static GraphicalView getGraph(Context context, ArrayList<GraphData>graph){
        boolean seriesAdded = false;
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
        multiRenderer.setApplyBackgroundColor(true);
        multiRenderer.setBackgroundColor(Color.parseColor("#00010101"));
        multiRenderer.setMarginsColor(Color.parseColor("#00010101"));
        multiRenderer.setShowLegend(false);
        multiRenderer.setShowAxes(false);
        multiRenderer.setXTitle("Days");
        multiRenderer.setShowGrid(true);
        multiRenderer.setGridColor(Color.WHITE);

        multiRenderer.setZoomEnabled(false, false);
        multiRenderer.setPanEnabled(false, false);
        multiRenderer.setLabelsTextSize(24);
        multiRenderer.setAntialiasing(true);
        multiRenderer.setTextTypeface("sans_serif", Typeface.NORMAL);
        multiRenderer.setPointSize(4f);

        for (GraphData g : graph) {
            if (g.getX()!= null) {
                TimeSeries series = new TimeSeries(g.getTitle());
                for (int j = 0; j < g.getX().size(); j++) {
                    series.add(g.getX().get(j), g.getY().get(j));
                }
                dataset.addSeries(series);
                XYSeriesRenderer graphRenderer = new XYSeriesRenderer();
                graphRenderer.setColor(g.getLineColor());
                graphRenderer.setLineWidth(g.getLineWidth());
                graphRenderer.setPointStyle(PointStyle.CIRCLE);
                graphRenderer.setFillPoints(false);
                graphRenderer.setPointStrokeWidth(10);
                multiRenderer.addSeriesRenderer(graphRenderer);
                seriesAdded = true;
            }
        }
        return seriesAdded? ChartFactory.getTimeChartView(context, dataset, multiRenderer, "dd-MMM-yyyy"): null;
    }

    public static String getCardsUrl(Context context){
        String patientId = getUserDataFromSharedPreference(context).getId();
        if (patientId.isEmpty()){
            return null;
        }
        return Constants.SERVER_URL + "/api/card/v1.0/"+ patientId +"/cards/";
    }

    public static UserProfile getUserDataFromSharedPreference(Context context){
        SharedPreferences sp = context.getSharedPreferences(Constants.PERSONAL_DETAIL_SHARED_PREFERENCE_NAME, Activity.MODE_PRIVATE);
        return new UserProfile(
                sp.getString("id", ""),
                sp.getString("displayName", ""),
                sp.getString("profilePicUrl", ""),
                sp.getString("email", ""),
                sp.getString("phone", ""),
                sp.getString("dob", ""),
                sp.getString("sex", ""),
                sp.getString("address", "")
        );
    }

    public static void saveUserDataToSharedPreference(Context context, UserProfile userProfile){
        SharedPreferences sp = context.getSharedPreferences(Constants.PERSONAL_DETAIL_SHARED_PREFERENCE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("id", userProfile.getId());
        editor.putString("displayName", userProfile.getDisplayName());
        editor.putString("email", userProfile.getEmail());
        editor.putString("dob", userProfile.getDob());
        editor.putString("phone", userProfile.getPhone());
        editor.putString("sex", userProfile.getSex());
        editor.putString("profilePicUrl", userProfile.getProfilePicUrl());
        editor.commit();
    }

    public static String getAuthTokenFromSharedPreference(Context context){
        if (context != null) {
            SharedPreferences sp = context.getSharedPreferences(Constants.AUTH_SHARED_PREFERENCE_NAME, Activity.MODE_PRIVATE);
            return sp.getString(Constants.AUTH_SHARED_PREFERENCE_KEY, "");
        }
        return null;
    }

    public static String setAuthTokenFromSharedPreference(Context context, String auth_token){
        if (context != null) {
            SharedPreferences sp = context.getSharedPreferences(Constants.AUTH_SHARED_PREFERENCE_NAME, Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(Constants.AUTH_SHARED_PREFERENCE_KEY, auth_token);
            editor.commit();
            return sp.getString(Constants.AUTH_SHARED_PREFERENCE_KEY, "");
        }
        return null;
    }

    public static void cleanupSharedPreference(Context context){
        if (context != null) {
            context.getSharedPreferences(Constants.AUTH_SHARED_PREFERENCE_NAME, Activity.MODE_PRIVATE).edit().clear().commit();
            context.getSharedPreferences(Constants.PERSONAL_DETAIL_SHARED_PREFERENCE_NAME, Activity.MODE_PRIVATE).edit().clear().commit();
            context.getSharedPreferences(Constants.GCM_SHARED_PREFERENCE_KEY, Activity.MODE_PRIVATE).edit().clear().commit();
        }
    }

    public static String getFolderUrl(){
//        Environment.getD
        File folder = new File(Environment.getExternalStorageDirectory(), "/DOCTL/ProfileImage");
        if (!folder.exists()) {
            Logger.e("TravellerLog :: ", "Creating folder");
            if (!folder.mkdirs()) {
                Logger.e("TravellerLog :: ", "Problem creating Image folder");
            }
        }
        Logger.e("Utils: getAbsolutePath: ", folder.getAbsolutePath());
        return folder.getAbsolutePath();
    }

    public static File getImageUrlForImageSave(){
        Random r = new Random();
        return new File(getFolderUrl(), "profile_pic_" + String.valueOf(System.currentTimeMillis())+ r.nextInt() + ".jpg");
    }

    public static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    public static String getAppVersionName(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    public static void handleUnauthorizedAccess(Context context){
        cleanupSharedPreference(context);
        Intent intent = new Intent(context, StartPageActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        context.startActivity(intent);
    }

    public static void showNotification(Context context, int notificationId, String title, String message){
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(title)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(message))
                        .setContentText(message);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(notificationId, mBuilder.build());
    }

    public static BaseTask parseCardData(String jsonStr) {
        Logger.d("Utils", jsonStr);
        JsonParser parser = new JsonParser();
        JsonObject cardJsonObj = parser.parse(jsonStr).getAsJsonObject();
        switch (BaseTask.CardType.valueOf(cardJsonObj.get("type").getAsString().toUpperCase())) {
            case MEDICINE:
                return new Gson().fromJson(cardJsonObj, MedicineTask.class);
            case VITAL:
                return new Gson().fromJson(cardJsonObj, VitalTask.class);
            case FOLLOWUP:
                return new Gson().fromJson(cardJsonObj, FollowupTask.class);
            case SIMPLEREMINDER:
                return new Gson().fromJson(cardJsonObj, MessageTask.class);
            case EDUCATION:
                return new Gson().fromJson(cardJsonObj, EducationTask.class);
        }
        return null;
    }

    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }
}