package com.doctl.patientcare.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.doctl.patientcare.main.utility.Logger;
import com.doctl.patientcare.main.utility.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 8/11/2014.
 */
public class BaseActivity extends ActionBarActivity implements ListView.OnItemClickListener{
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private List<DrawerItem> dataList;
    Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
                Logger.e("Error:" + Thread.currentThread().getStackTrace()[2], paramThrowable.getLocalizedMessage());
                Logger.e("Error:" + Thread.currentThread().getStackTrace()[2], paramThrowable.getClass().getName());
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Some Error occurred", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
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
            selectItem(position);
        }
    }

    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(Gravity.START|Gravity.START)){
            mDrawerLayout.closeDrawers();
            return;
        }
        super.onBackPressed();
    }

    protected void setupNavigationDrawer(){
        mToolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(mToolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        CustomDrawerAdapter adapter = new CustomDrawerAdapter(this, getDrawerItemList());
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(this);
        mDrawerToggle= new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close)
        {
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

    /** Swaps fragments in the main content view */
    private void selectItem(int position) {
        boolean closeDrawer = true;
        Intent intent;
        switch (position) {
            case 0:
                Logger.d("BaseActivity: ", "Profile Clicked");
                closeDrawer = false;
                break;
            case 1:
                Logger.d("BaseActivity: ", "Home Clicked");
                if (!MainActivity.active) {
                    intent = new Intent(this, MainActivity.class);
                    this.startActivity(intent);
                }
                break;
            case 2:
                Logger.d("BaseActivity: ", "Prescription Clicked");
                if (!MedicineDetailActivity.active) {
                    intent = new Intent(this, MedicineDetailActivity.class);
                    this.startActivity(intent);
                }
                break;
            case 3:
                Logger.d("BaseActivity: ", "Vital Clicked");
                closeDrawer = false;
                break;
            case 4:
                Logger.d("BaseActivity: ", "Blood Sugar Clicked");
                intent = new Intent(this, VitalDetailActivity.class);
                intent.putExtra("vitalType", "sugar");
                intent.putExtra("vitalName", "Blood Sugar");
                this.startActivity(intent);
                break;
            case 5:
                Logger.d("BaseActivity: ", "Blood Pressure Clicked");
                intent = new Intent(this, VitalDetailActivity.class);
                intent.putExtra("vitalType", "bp");
                intent.putExtra("vitalName", "Blood Pressure");
                this.startActivity(intent);
                break;
            case 6:
                Logger.d("BaseActivity: ", "Temperature Clicked");
                intent = new Intent(this, VitalDetailActivity.class);
                intent.putExtra("vitalType", "temperature");
                intent.putExtra("vitalName", "Temperature");
                this.startActivity(intent);
                break;
            case 7:
                Logger.d("BaseActivity: ", "Pulse Clicked");
                intent = new Intent(this, VitalDetailActivity.class);
                intent.putExtra("vitalType", "pulse");
                intent.putExtra("vitalName", "Pulse");
                this.startActivity(intent);
                break;
            case 8:
                Logger.d("BaseActivity: ", "Account Clicked");
                closeDrawer = false;
                break;
            case 9:
                Logger.d("BaseActivity: ", "Change Password Clicked");
                intent = new Intent(this, ChangeProfileActivity.class);
                this.startActivity(intent);
                break;
            case 10:
                Logger.d("BaseActivity: ", "Signout Clicked");
                signoutUser();
                break;
            default:
                break;
        }

        if (closeDrawer) {
            mDrawerList.setItemChecked(position, true);
//            Toast.makeText(this, "Item Selected: " + position, Toast.LENGTH_LONG).show();
            mDrawerLayout.closeDrawer(mDrawerList);
        }
    }

    private void signoutUser(){
        final Activity ctx = this;
        new AlertDialog.Builder(this)
                .setIcon(0)
                .setTitle(R.string.signout_title)
                .setMessage(R.string.signout_body)
                .setPositiveButton(R.string.signout_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Utils.cleanupSharedPreference(ctx);
                        Intent intent = new Intent(ctx, StartPageActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        ctx.startActivity(intent);
                    }
                })
                .setNegativeButton(R.string.signout_no, null)
                .show();
    }
    private List<DrawerItem> getDrawerItemList(){
        dataList = new ArrayList<DrawerItem>();

        dataList.add(new DrawerItem(true));
        dataList.add(new DrawerItem(getResources().getString(R.string.nav_item_home), R.drawable.ic_home_black_24dp));
        dataList.add(new DrawerItem(getResources().getString(R.string.nav_item_prescription), R.drawable.ic_prescription_black_24dp));

        dataList.add(new DrawerItem(getResources().getString(R.string.nav_item_vital)));
        dataList.add(new DrawerItem(getResources().getString(R.string.nav_item_sugar), R.drawable.ic_sugar_black_24dp));
        dataList.add(new DrawerItem(getResources().getString(R.string.nav_item_bp), R.drawable.ic_bloodpressure_black_24dp));
        dataList.add(new DrawerItem(getResources().getString(R.string.nav_item_temperature),  R.drawable.ic_temp_black_24dp));
        dataList.add(new DrawerItem(getResources().getString(R.string.nav_item_pulse), R.drawable.ic_pulse_black_24dp));

        dataList.add(new DrawerItem(getResources().getString(R.string.nav_item_accounts)));
        dataList.add(new DrawerItem(getResources().getString(R.string.nav_item_profile), R.drawable.ic_person_black_24dp));
        dataList.add(new DrawerItem(getResources().getString(R.string.nav_item_logout), R.drawable.ic_signout_black_24dp));
        return dataList;
    }
}
