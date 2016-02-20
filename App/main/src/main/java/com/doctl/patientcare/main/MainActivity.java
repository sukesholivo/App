package com.doctl.patientcare.main;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.doctl.patientcare.main.activities.AddPatientActivity;
import com.cocosw.bottomsheet.BottomSheet;
import com.doctl.patientcare.main.activities.DocumentsActivity;
import com.doctl.patientcare.main.activities.ThreadListActivity;
import com.doctl.patientcare.main.controls.ProgressWheel;
import com.doctl.patientcare.main.fragments.BaseFragment;
import com.doctl.patientcare.main.fragments.CardListFragment;
import com.doctl.patientcare.main.om.dashboard.Dashboard;
import com.doctl.patientcare.main.services.HTTPServiceHandler;
import com.doctl.patientcare.main.utility.Constants;
import com.doctl.patientcare.main.utility.Logger;
import com.doctl.patientcare.main.utility.Utils;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import it.gmariotti.cardslib.library.utils.BitmapUtils;

public class MainActivity extends BaseActivityWithNavigation {
    private static final String TAG = MainActivity.class.getSimpleName();
    static boolean active = false;
    GoogleCloudMessaging gcm;
    String regid;
    FloatingActionMenu mFabMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setupNavigationDrawer();
        mFabMenu = (FloatingActionMenu)findViewById(R.id.fab_menu);
        mFabMenu.setClosedOnTouchOutside(true);
        FloatingActionButton addPatientButton = (FloatingActionButton) findViewById(R.id.add_patient_btn);
        addPatientButton.setOnClickListener(clickListener);
        FloatingActionButton recordVitalBtn = (FloatingActionButton) findViewById(R.id.upload_report_btn);
        recordVitalBtn.setOnClickListener(clickListener);
        FloatingActionButton uploadReportBtn = (FloatingActionButton) findViewById(R.id.upload_report_btn);
        uploadReportBtn.setOnClickListener(clickListener);

        refresh();
        setupGCMRegistration();
    }

    @Override
    public void onStart() {
        super.onStart();
        dismissAllNotification();
        active = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        TODO: Removing menu as of now
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
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
            case R.id.action_refresh:
                refresh();
                return true;
//            case R.id.action_settings:
//                Toast.makeText(this,"Settings pressed: " + item.getTitle(), Toast.LENGTH_LONG).show();
//                return true;
//            case R.id.action_add_vital:
//                new AlertDialog.Builder(this)
//                        .setTitle("Add Sugar")
//                        .setIcon(0)
//                        .setView(getLayoutInflater().inflate(R.layout.dialog_inner_content_vital_entry, null))
//                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                Toast.makeText(MainActivity.this, "Value submitted", Toast.LENGTH_LONG).show();
//                            }
//                        })
//                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                            }
//                        })
//                        .show();
//                return true;
//            case R.id.action_add_symptom:
//                Toast.makeText(this,"Add Symptom", Toast.LENGTH_LONG).show();
//                new AlertDialog.Builder(this)
//                        .setTitle("Record Symptom" +
//                                "")
//                        .setIcon(0)
//                        .setView(getLayoutInflater().inflate(R.layout.dialog_inner_content_symptom_entry, null))
//                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                Toast.makeText(MainActivity.this, "Symptom submitted", Toast.LENGTH_LONG).show();
//                            }
//                        })
//                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                            }
//                        })
//                        .show();
//                return true;
            case R.id.action_chat:
                Intent intent=new Intent(this, ThreadListActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (mFabMenu.isOpened()){
            mFabMenu.close(true);
        } else {
            super.onBackPressed();
        }
    }

    private void dismissAllNotification(){
        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancelAll();
    }
    private void setupActionBar(String clinicName){
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            if (clinicName != null && !clinicName.isEmpty()) {
                actionBar.setTitle(clinicName);
            } else {
                actionBar.setTitle("DOCTL");
            }
        }
    }

    private void refresh(){
        if (Utils.isNetworkAvailable(this)){
            new GetProgress().execute();
            setCards();
        } else {
            Toast.makeText(this, "No Network Connection", Toast.LENGTH_LONG).show();
        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        Constants.PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Logger.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private String getRegistrationId(Context context) {
        SharedPreferences sp = this.getSharedPreferences(Constants.GCM_SHARED_PREFERENCE_KEY, Activity.MODE_PRIVATE);
        String registrationId = sp.getString(Constants.PROPERTY_GCM_REGISTRATION_ID, "");

        if (registrationId.isEmpty()) {
            Logger.i(TAG, "Registration not found.");
            return "";
        }
        int registeredVersion = sp.getInt(Constants.PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = Utils.getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Logger.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    public void setupGCMRegistration() {
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(this);

            if (regid.isEmpty()) {
                registerInBackground(this);
            }
        } else {
            Logger.i(TAG, "No valid Google Play Services APK found.");
        }
    }

    private void toggleUpdateSpinner(int b) {
//        ProgressBar progress = (ProgressBar)findViewById(R.id.loadingIndicator);
//        progress.setVisibility(b);
    }

    private void getProgress(){
        String progressData = downloadProgressData();
        if (progressData != null && !progressData.isEmpty()) {
            final Dashboard dashboard = parseProgressData(progressData);
            runOnUiThread(new Runnable() {
                public void run() {
                    updateProgress(dashboard);
                }
            });
        }
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
        setupActionBar(dashboardData.getClinic().getDisplayName());
        updateAdherencePercentage(dashboardData.getAdherence().intValue());

        updateTotalPoints(dashboardData.getPoints());

        long milliSecondsInDay = 24 * 60 * 60 * 1000;
        long treatmentDays = 0;
        long completedDays = 0;
        Dashboard.Progress progress = dashboardData.getProgress();
        if (progress != null) {
            Date start = dashboardData.getProgress().getStartTime();
            Date end = dashboardData.getProgress().getEndTime();
            if (start != null && end != null) {
                treatmentDays = (end.getTime() - start.getTime()) / milliSecondsInDay;
                completedDays = (new Date().getTime() - start.getTime()) / milliSecondsInDay;
                if (completedDays < 0) {
                    completedDays = 0;
                } else if (completedDays > treatmentDays) {
                    completedDays = treatmentDays;
                }
                setTreatmentProgress((int) completedDays, (int) treatmentDays);
            } else {
                RelativeLayout progressLayout = (RelativeLayout)findViewById(R.id.progressLayout);
                progressLayout.setVisibility(View.GONE);
            }
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
        int digits = 1;
        if (points > 0) {
            digits = (int) Math.log10(points) + 1;
        }
        TextView pointsText = (TextView)findViewById(R.id.pointsText);
        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, (int) getResources().getDimension(R.dimen.awards_text_height));
        rlp.addRule(RelativeLayout.BELOW, R.id.points);
        switch(digits){
            case 1:
            case 2:
                rlp.setMargins(0, 0, 0, 0);
                break;
            case 3:
                rlp.setMargins(Utils.dpToPx(this, 10), 0, 0, 0);
                break;
            case 4:
            default:
                rlp.setMargins(Utils.dpToPx(this, 20), 0, 0, 0);
                break;
        }
        pointsText.setLayoutParams(rlp);
    }

    private void setTreatmentProgress(int progress, int max){
        RelativeLayout progressLayout = (RelativeLayout)findViewById(R.id.progressLayout);
        progressLayout.setVisibility(View.VISIBLE);
        ProgressBar progressBar = (ProgressBar)findViewById(R.id.treatmentProgress);
        progressBar.setMax(max);
        progressBar.setProgress(progress);

        TextView progressText = (TextView)findViewById(R.id.progressText);
        progressText.setText(progress + "/" + max + " DAYS");
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

    private void handleVitalClick(){
        new BottomSheet.Builder(this).title("Pick vital").sheet(R.menu.vital_list).listener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent;
                Context c = MainActivity.this;
                switch (which) {
                    case R.id.blood_sugar:
                        intent = new Intent(c, VitalDetailActivity.class);
                        intent.putExtra("show_add_dialog", true);
                        intent.putExtra("vitalType", "sugar");
                        intent.putExtra("vitalName", "Blood Sugar");
                        c.startActivity(intent);
                        break;
                    case R.id.blood_pressure:
                        intent = new Intent(c, VitalDetailActivity.class);
                        intent.putExtra("show_add_dialog", true);
                        intent.putExtra("vitalType", "bp");
                        intent.putExtra("vitalName", "Blood Pressure");
                        c.startActivity(intent);
                        break;
                    case R.id.temperature:
                        intent = new Intent(c, VitalDetailActivity.class);
                        intent.putExtra("show_add_dialog", true);
                        intent.putExtra("vitalType", "temperature");
                        intent.putExtra("vitalName", "Temperature");
                        c.startActivity(intent);
                        break;
                    case R.id.pulse:
                        intent = new Intent(c, VitalDetailActivity.class);
                        intent.putExtra("show_add_dialog", true);
                        intent.putExtra("vitalType", "pulse");
                        intent.putExtra("vitalName", "Pulse");
                        c.startActivity(intent);
                        break;
                    case R.id.weight:
                        intent = new Intent(c, VitalDetailActivity.class);
                        intent.putExtra("show_add_dialog", true);
                        intent.putExtra("vitalType", "weight");
                        intent.putExtra("vitalName", "Weight");
                        c.startActivity(intent);
                        break;
                }
            }
        }).show();

    }

    private void handleReportClick(){
        Intent intent = new Intent(this, DocumentsActivity.class);
        intent.putExtra("show_add_dialog", true);
        this.startActivity(intent);
    }

    private void handlePatientAddClick(){
        Intent intent = new Intent(this, AddPatientActivity.class);
        intent.putExtra("show_add_dialog", true);
        this.startActivity(intent);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.record_vital_btn:
                    handleVitalClick();
                    Logger.e("", "Record Vital");
                    break;
                case R.id.upload_report_btn:
                    handleReportClick();
                    Logger.e("", "Upload report");
                    break;
                case R.id.add_patient_btn:
                    handlePatientAddClick();
                    Logger.e("", "Add patient");
                    break;
            }
            mFabMenu.close(true);
        }
    };

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

    private void registerInBackground(final Context context) {

        new AsyncTask<Void, String, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(Constants.SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;
                    SharedPreferences sp = context.getSharedPreferences(Constants.PERSONAL_DETAIL_SHARED_PREFERENCE_NAME, Activity.MODE_PRIVATE);
                    String username = sp.getString("email","");

                    new WriteGCMRegistrationId(context).execute(regid, username);

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
            }
        }.execute(null, null, null);
    }
}
