package in.olivo.patientcare.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.cocosw.bottomsheet.BottomSheet;

import java.util.ArrayList;
import java.util.List;

import in.olivo.patientcare.main.activities.DocumentsActivity;
import in.olivo.patientcare.main.activities.ThreadListActivity;
import in.olivo.patientcare.main.measure.activity.MeasureSchemaListActivity;
import in.olivo.patientcare.main.om.vitals.VitalTask;
import in.olivo.patientcare.main.utility.AsyncTaskUtils;
import in.olivo.patientcare.main.utility.Constants;
import in.olivo.patientcare.main.utility.Logger;
import in.olivo.patientcare.main.utility.Utils;
import in.olivo.patientcare.main.visit.VisitListActivity;

/**
 * Created by Administrator on 8/11/2014.
 */
public class BaseActivityWithNavigation extends BaseActivity implements ListView.OnItemClickListener {
    Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private RelativeLayout mDrawerListLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private List<DrawerItem> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
//            public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
//                Logger.e("Error:" + Thread.currentThread().getStackTrace()[2], paramThrowable.getLocalizedMessage());
//                Logger.e("Error:" + Thread.currentThread().getStackTrace()[2], paramThrowable.getClass().getName());
//                runOnUiThread(new Runnable() {
//                    public void run() {
//                        Toast.makeText(getApplicationContext(), "Some Error occurred", Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        MainApplication.activityResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MainApplication.activityPaused();
    }

    @Override
    protected void onStart() {
        super.onStart();
        invalidateOptionsMenu();
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle != null && mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            // Something else
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (dataList.get(position).getTitle() == null) {
            selectItem(dataList.get(position).getId());
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(Gravity.START | Gravity.START)) {
            mDrawerLayout.closeDrawers();
            return;
        }
        super.onBackPressed();
    }

    protected void setupNavigationDrawer() {
        mToolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(mToolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerListLayout = (RelativeLayout) findViewById(R.id.left_drawer_layout);
        mDrawerList = (ListView) mDrawerListLayout.findViewById(R.id.left_drawer);
        CustomDrawerAdapter adapter = new CustomDrawerAdapter(this, getDrawerItemList());
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(this);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
            }

            /** Called when a drawer has settled in a completely closed state. */
            @Override
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
                syncState();
            }

            /** Called when a drawer has settled in a completely open state. */
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
                syncState();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }

    /**
     * Swaps fragments in the main content view
     */
    private void selectItem(int position) {
        boolean closeDrawer = true;
        Intent intent;
        switch (position) {
            case 0:
                Logger.d("BaseActivityWithNavigation: ", "Profile Clicked");
                intent = new Intent(this, ChangeProfileActivity.class);
                this.startActivity(intent);
                break;
            case 1:
                Logger.d("BaseActivityWithNavigation: ", "Home Clicked");
                if (!MainActivity.active) {
                    intent = new Intent(this, MainActivity.class);
                    this.startActivity(intent);
                }
                break;
            case 2:
                Logger.d("BaseActivityWithNavigation: ", "Prescription Clicked");
                if (!MedicineDetailActivity.active) {
                    intent = new Intent(this, MedicineDetailActivity.class);
                    this.startActivity(intent);
                }
                break;
            case 3:
                Logger.d("BaseActivityWithNavigation: ", "Vital Clicked");
                closeDrawer = false;
                break;
            case 4:
                Logger.d("BaseActivityWithNavigation: ", "Blood Sugar Clicked");
                intent = new Intent(this, VitalDetailActivity.class);
                intent.putExtra("vitalType", "sugar");
                intent.putExtra("vitalName", "Blood Sugar");
                this.startActivity(intent);
                break;
            case 5:
                Logger.d("BaseActivityWithNavigation: ", "Blood Pressure Clicked");
                intent = new Intent(this, VitalDetailActivity.class);
                intent.putExtra("vitalType", "bp");
                intent.putExtra("vitalName", "Blood Pressure");
                this.startActivity(intent);
                break;
            case 6:
                Logger.d("BaseActivityWithNavigation: ", "Temperature Clicked");
                intent = new Intent(this, VitalDetailActivity.class);
                intent.putExtra("vitalType", "temperature");
                intent.putExtra("vitalName", "Temperature");
                this.startActivity(intent);
                break;
            case 7:
                Logger.d("BaseActivityWithNavigation: ", "Pulse Clicked");
                intent = new Intent(this, VitalDetailActivity.class);
                intent.putExtra("vitalType", "pulse");
                intent.putExtra("vitalName", "Pulse");
                this.startActivity(intent);
                break;
            case 8:
                Logger.d("BaseActivityWithNavigation: ", "Weight Clicked");
                intent = new Intent(this, VitalDetailActivity.class);
                intent.putExtra("vitalType", "weight");
                intent.putExtra("vitalName", "Weight");
                this.startActivity(intent);
                break;
            case 9:
                Logger.d("BaseActivityWithNavigation: ", "Account Clicked");
                closeDrawer = false;
                break;
            case 10:
                Logger.d("BaseActivityWithNavigation: ", "Rewards Clicked");
                intent = new Intent(this, RewardsActivity.class);
                this.startActivity(intent);
                break;
            case 11:
                Logger.d("BaseActivityWithNavigation: ", "Signout Clicked");
                signoutUser();
                break;
            case 12:
                Logger.d("BaseActivityWithNavigation: ", "Document Clicked");
                intent = new Intent(this, DocumentsActivity.class);
                this.startActivity(intent);
                break;
            case 13:
                Logger.d("BaseActivityWithNavigation: ", "Thread Clicked");
                intent = new Intent(this, ThreadListActivity.class);
                this.startActivity(intent);
                break;
            case 14:
                Logger.d("BaseActivityWithNavigation: ", "Visits Clicked");
                intent = new Intent(this, VisitListActivity.class);
                this.startActivity(intent);
                break;
            case 15:
                Logger.d("BaseActivityWithNavigation: ", "Measure Clicked");
//                intent = new Intent(this, MeasureSchemaListActivity.class);
//                this.startActivity(intent);
                handleVitalClick();
                break;
            default:
                break;
        }

        if (closeDrawer) {
            mDrawerList.setItemChecked(position, true);
//            Toast.makeText(this, "Item Selected: " + position, Toast.LENGTH_LONG).show();
            mDrawerLayout.closeDrawer(mDrawerListLayout);
        }
    }

    private void signoutUser() {
        final Activity ctx = this;
        new AlertDialog.Builder(this)
                .setIcon(0)
                .setTitle(R.string.signout_title)
                .setMessage(R.string.signout_body)
                .setPositiveButton(R.string.signout_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new AsyncTaskUtils.DeleteGCMRegistrationId(ctx).execute();
                    }
                })
                .setNegativeButton(R.string.signout_no, null)
                .show();
    }

    private List<DrawerItem> getDrawerItemList() {
        dataList = new ArrayList<>();

        dataList.add(new DrawerItem(0, true));
        dataList.add(new DrawerItem(1, getResources().getString(R.string.nav_item_home), R.drawable.ic_home_black_24dp));
        dataList.add(new DrawerItem(2, getResources().getString(R.string.nav_item_medicine), R.drawable.ic_prescription_black_24dp));
        dataList.add(new DrawerItem(12, getResources().getString(R.string.nav_item_documents), R.drawable.ic_action_storage));
        dataList.add(new DrawerItem(13, getResources().getString(R.string.nav_item_threads), R.drawable.ic_question_answer_black_24dp));
        dataList.add(new DrawerItem(14, getResources().getString(R.string.nav_item_visits), R.drawable.ic_person_black_24dp));
        dataList.add(new DrawerItem(15, getResources().getString(R.string.nav_item_measures), R.drawable.ic_person_black_24dp));
        ArrayList<VitalTask.VitalData> vitals = Utils.getVitalDataFromSharedPreference(this);
        if (!vitals.isEmpty()) {
            dataList.add(new DrawerItem(3, getResources().getString(R.string.nav_item_vital)));
            for (VitalTask.VitalData vital : vitals) {
                String vitalType = vital.getType();
                if (vitalType.toLowerCase().equals("sugar_after_meal") || vitalType.toLowerCase().equals("sugar_before_meal") || vitalType.toLowerCase().equals("sugar_random")) {
                    dataList.add(new DrawerItem(4, getResources().getString(R.string.nav_item_sugar), R.drawable.ic_sugar_black_24dp));
                } else if (vitalType.toLowerCase().equals("blood_pressure")) {
                    dataList.add(new DrawerItem(5, getResources().getString(R.string.nav_item_bp), R.drawable.ic_bloodpressure_black_24dp));
                } else if (vitalType.toLowerCase().equals("temperature")) {
                    dataList.add(new DrawerItem(6, getResources().getString(R.string.nav_item_temperature), R.drawable.ic_temp_black_24dp));
                } else if (vitalType.toLowerCase().equals("pulse")) {
                    dataList.add(new DrawerItem(7, getResources().getString(R.string.nav_item_pulse), R.drawable.ic_pulse_black_24dp));
                } else if (vitalType.toLowerCase().equals("weight")) {
                    dataList.add(new DrawerItem(8, getResources().getString(R.string.nav_item_weight), R.drawable.ic_weight_black_24dp));
                }
            }
        }

        dataList.add(new DrawerItem(9, getResources().getString(R.string.nav_item_accounts)));
        dataList.add(new DrawerItem(10, getResources().getString(R.string.nav_item_rewards), R.drawable.ic_award_black_24dp));
        dataList.add(new DrawerItem(11, getResources().getString(R.string.nav_item_logout), R.drawable.ic_signout_black_24dp));
        return dataList;
    }

    private void handleVitalClick() {
        new BottomSheet.Builder(this).title("Pick vital").sheet(R.menu.vital_list).listener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Context c = BaseActivityWithNavigation.this;
                Intent intent = new Intent(c, VitalDetailActivity.class);
//                intent.putExtra(Constants.ADD_VITAL, true);
                switch (which) {
                    case R.id.blood_sugar:
                        intent.putExtra("vitalType", "sugar");
                        intent.putExtra("vitalName", "Blood Sugar");
                        c.startActivity(intent);
                        break;
                    case R.id.blood_pressure:
                        intent.putExtra("vitalType", "bp");
                        intent.putExtra("vitalName", "Blood Pressure");
                        c.startActivity(intent);
                        break;
                    case R.id.temperature:
                        intent.putExtra("vitalType", "temperature");
                        intent.putExtra("vitalName", "Temperature");
                        c.startActivity(intent);
                        break;
                    case R.id.pulse:
                        intent.putExtra("vitalType", "pulse");
                        intent.putExtra("vitalName", "Pulse");
                        c.startActivity(intent);
                        break;
                    case R.id.weight:
                        intent.putExtra("vitalType", "weight");
                        intent.putExtra("vitalName", "Weight");
                        c.startActivity(intent);
                        break;
                }
            }
        }).show();

    }
}