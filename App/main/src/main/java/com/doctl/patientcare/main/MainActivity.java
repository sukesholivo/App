package com.doctl.patientcare.main;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.doctl.patientcare.main.controls.ProgressWheel;
import com.doctl.patientcare.main.fragments.BaseFragment;
import com.doctl.patientcare.main.fragments.CardListFragment;
import com.doctl.patientcare.main.utility.GetServerAuthTokenAsync;

import java.io.File;

import it.gmariotti.cardslib.library.utils.BitmapUtils;

public class MainActivity extends BaseActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setPoints();
        setCards();
        setupGCMRegistration();
        setupServerAuthToken();
    }

    public void setupServerAuthToken() {
        String username = "doctor@doctl.com";
        String password = "test";
        new GetServerAuthTokenAsync(MainActivity.this).execute(username, password);
        return;
    }

    public void setupGCMRegistration() {
        Context appContext = this.getApplicationContext();
        Intent registrationIntent = new Intent("com.google.android.c2dm.intent.REGISTER");
        registrationIntent.putExtra("app", PendingIntent.getBroadcast(appContext, 0, new Intent(), 0));
        registrationIntent.putExtra("sender","258383232963");
        appContext.startService(registrationIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
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

    private void setPoints() {
        updateAdherencePercentage(80);
        updateTotalPoints(600);
        setTreatmentProgress(40,90);
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
}
