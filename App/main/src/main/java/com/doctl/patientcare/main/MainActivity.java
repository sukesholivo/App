package com.doctl.patientcare.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.doctl.patientcare.main.controls.ProgressWheel;
import com.doctl.patientcare.main.fragments.BaseFragment;
import com.doctl.patientcare.main.fragments.CardListFragment;
import com.doctl.patientcare.main.om.dashboard.Dashboard;
import com.doctl.patientcare.main.services.HTTPServiceHandler;
import com.doctl.patientcare.main.utility.Constants;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Date;

import it.gmariotti.cardslib.library.utils.BitmapUtils;

public class MainActivity extends BaseActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    static boolean active = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setupNavigationDrawer();
        new GetProgress().execute();
        setCards();
        setupGCMRegistration();
    }

    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }

    public void setupGCMRegistration() {
        SharedPreferences sp = this.getSharedPreferences(Constants.GCM_SHARED_PREFERERENCE_KEY, Activity.MODE_PRIVATE);
        String gcm_registration_id = sp.getString("gcm_registration_id", "");
        if(gcm_registration_id.isEmpty()) {
            Context appContext = this.getApplicationContext();
            Intent registrationIntent = new Intent("com.google.android.c2dm.intent.REGISTER");
            registrationIntent.putExtra("app", PendingIntent.getBroadcast(appContext, 0, new Intent(), 0));
            registrationIntent.putExtra("sender", "258383232963");
            appContext.startService(registrationIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        TODO: Removing menu as of now
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private Intent getDefaultIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, "I have been 80% adhered to my diabetes treatment");
        intent.setType("text/plain");
        return intent;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_share:
//                Toast.makeText(this,"Shared pressed: " + item.getTitle(), Toast.LENGTH_LONG).show();
//                Intent intent = new Intent(Intent.ACTION_SEND);
//                intent.putExtra(Intent.EXTRA_TEXT, "I have been 80% adhered to my diabetes treatment");
//                intent.setType("text/plain");
//                startActivity(Intent.createChooser(intent, "Share your adherence"));

                View view = findViewById(R.id.treatment_dashboard_layout);
                Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas c = new Canvas(bitmap);
                view.draw(c);
                File photofile= BitmapUtils.createFileFromBitmap(bitmap);
                Intent shareIntent = BitmapUtils.createIntentFromImage(photofile);
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My treatment progress");
                startActivity(Intent.createChooser(shareIntent, "Share your progress"));
                return true;
            case R.id.action_settings:
                Toast.makeText(this,"Settings pressed: " + item.getTitle(), Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_add_vital:
                new AlertDialog.Builder(this)
                        .setTitle("Add Sugar")
                        .setIcon(0)
                        .setView(getLayoutInflater().inflate(R.layout.dialog_inner_content_vital_entry, null))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(MainActivity.this, "Value submitted", Toast.LENGTH_LONG).show();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();
                return true;
            case R.id.action_add_symptom:
                Toast.makeText(this,"Add Symptom", Toast.LENGTH_LONG).show();
                new AlertDialog.Builder(this)
                        .setTitle("Record Symptom" +
                                "")
                        .setIcon(0)
                        .setView(getLayoutInflater().inflate(R.layout.diaolg_inner_content_symptom_entry, null))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(MainActivity.this, "Symptom submitted", Toast.LENGTH_LONG).show();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void toggleUpdateSpinner(int b) {
//        ProgressBar progress = (ProgressBar)findViewById(R.id.loadingIndicator);
//        progress.setVisibility(b);
    }

    private void getProgress(){
        String progressData = downloadProgressData();
        final Dashboard dashboard = parseProgressData(progressData);
        runOnUiThread(new Runnable() {
            public void run() {
                updateProgress(dashboard);
            }
        });
    }

    private String downloadProgressData() {
        HTTPServiceHandler serviceHandler = new HTTPServiceHandler(this);
        return serviceHandler.makeServiceCall(Constants.PROGRESS_DATA_URL, HTTPServiceHandler.HTTPMethod.GET, null, null);
    }

    private Dashboard parseProgressData(String progressData){
        JsonParser parser = new JsonParser();
        JsonObject progressJson = parser.parse(progressData).getAsJsonObject();
        return new Gson().fromJson(progressJson, Dashboard.class);
    }

    private void updateProgress(Dashboard dashboardData) {
        updateAdherencePercentage(dashboardData.getAdherence().intValue());

        updateTotalPoints(dashboardData.getPoints());

        long milliSecondsInDay = 24 * 60 * 60 * 1000;
        long treatmentDays = 0;
        long completedDays = 0;
        Dashboard.Progress progress = dashboardData.getProgress();
        if (progress != null) {
            Date start = dashboardData.getProgress().getStartTime();
            Date end = dashboardData.getProgress().getEndTime();
            treatmentDays = (end.getTime() - start.getTime())/ milliSecondsInDay;
            completedDays = (new Date().getTime() - start.getTime())/ milliSecondsInDay;
            if (completedDays < 0 ) {
                completedDays = 0;
            } else if (completedDays > treatmentDays ){
                completedDays = treatmentDays;
            }
        }
        setTreatmentProgress((int) completedDays, (int) treatmentDays);

        Dashboard.Vital vital = dashboardData.getVital();
        if (vital != null) {
            setVitalValue(dashboardData.getVital().getTimeStamp(),
                    dashboardData.getVital().getValue1(),
                    dashboardData.getVital().getValue2(),
                    dashboardData.getVital().getUnit1());
        }
    }

    private void updateAdherencePercentage(int per){
        ProgressWheel pw = (ProgressWheel)findViewById(R.id.adherenceProgress);
        int val = per * 360 / 100;
        pw.setProgress(val);
        pw.setText(per+ "%");
    }

    private void updateTotalPoints(int points){
        TextView totalPoints = (TextView)findViewById(R.id.points);
        totalPoints.setText("" + points);
    }

    private void setTreatmentProgress(int progress, int max){
        ProgressBar progressBar = (ProgressBar)findViewById(R.id.treatmentProgress);
        progressBar.setMax(max);
        progressBar.setProgress(progress);

        TextView progressText = (TextView)findViewById(R.id.progressText);
        progressText.setText(progress + "/" + max + " DAYS");
    }

    private void setVitalValue(Date timestamp, Double value1, Double value2, String unit){
        DecimalFormat df = new DecimalFormat("###.#");
        TextView vitalValue1 = (TextView) findViewById(R.id.vitalValue1);
        vitalValue1.setText(df.format(value1));

        TextView vitalValue2 = (TextView) findViewById(R.id.vitalValue2);
        TextView vitalValueSeparator = (TextView) findViewById(R.id.vitalValueSeparator);
        if (value2 != null) {
            vitalValue2.setVisibility(View.VISIBLE);
            vitalValueSeparator.setVisibility(View.VISIBLE);
            vitalValue2.setText(df.format(value2));
        } else {
            vitalValue2.setVisibility(View.GONE);
            vitalValueSeparator.setVisibility(View.GONE);
        }

        TextView vitalValueUnit = (TextView) findViewById(R.id.vitalValueUnit);
        vitalValueUnit.setText(unit);

        TextView vitalLastEntryDate = (TextView) findViewById(R.id.vitalLastEntryDate);
        String lastVitalEntryTimeString = "";
        long totalSecond = (new Date().getTime() - timestamp.getTime())/ 1000;
        if (totalSecond < 60){ // Less than one min
            lastVitalEntryTimeString = totalSecond + (totalSecond == 1 ? " SEC AGO" : " SECS AGO");
        } else if (totalSecond < 60 * 60){ // less than one hour
            long mins = totalSecond / 60;
            lastVitalEntryTimeString = mins + (mins == 1 ? " MIN AGO" : " MINS AGO");
        } else if (totalSecond < 60 * 60 * 24){ // less than one day
            long hour = totalSecond / (60 * 60);
            lastVitalEntryTimeString = hour + (hour == 1 ? " HOUR AGO" :  " HOURS AGO");
        } else { // More than one day
            long day = totalSecond / (60 * 60 * 24);
            lastVitalEntryTimeString = day + (day == 1 ? " DAY AGO" :  " DAYS AGO");
        }
        vitalLastEntryDate.setText(lastVitalEntryTimeString);
    }

    private void setCards(){
        BaseFragment mBaseFragment = selectFragment();
        openFragment(mBaseFragment);
    }

    private BaseFragment selectFragment() {
        BaseFragment baseFragment;
        baseFragment = new CardListFragment();
        return baseFragment;
    }

    private void openFragment(BaseFragment baseFragment) {
        if (baseFragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_main, baseFragment);
            //fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }

    private class GetProgress extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            getProgress();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }
}
