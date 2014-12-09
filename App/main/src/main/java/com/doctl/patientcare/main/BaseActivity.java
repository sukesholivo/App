package com.doctl.patientcare.main;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 8/11/2014.
 */
public class BaseActivity extends Activity implements ListView.OnItemClickListener{
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    List<DrawerItem> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
                Log.e("Error:" + Thread.currentThread().getStackTrace()[2], paramThrowable.getLocalizedMessage());
                Log.e("Error:" + Thread.currentThread().getStackTrace()[2], paramThrowable.getClass().getName());
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Some Error occured", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setupNavigationDrawer();
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("BaseActivity: ", "Some menu selected");
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
        Log.e("BaseActivity: ", "Item Selected with position: " + position);
        if (dataList.get(position).getTitle() == null) {
            selectItem(position);
        }
    }

    protected void setupNavigationDrawer(){
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        CustomDrawerAdapter adapter = new CustomDrawerAdapter(this, getDrawerItemList());
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(this);
        mDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout,
                R.drawable.ic_drawer,
                R.string.drawer_open,
                R.string.drawer_close) {

        };
        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }

    /** Swaps fragments in the main content view */
    private void selectItem(int position) {
        boolean closeDrawer = true;
        switch (position) {
            case 0:
                Log.d("BaseActivity: ", "Profile Clicked");
                closeDrawer = false;
                break;
            case 1:
                Log.d("BaseActivity: ", "Home Clicked");
                break;
            case 2:
                Log.d("BaseActivity: ", "Prescription Clicked");
                break;
            case 3:
                Log.d("BaseActivity: ", "Vital Clicked");
                closeDrawer = false;
                break;
            case 4:
                Log.d("BaseActivity: ", "Blood Sugar Clicked");
                break;
            case 5:
                Log.d("BaseActivity: ", "Blood Pressure Clicked");
                break;
            case 6:
                Log.d("BaseActivity: ", "Temperature Clicked");
                break;
            case 7:
                Log.d("BaseActivity: ", "Pulse Clicked");
                break;
            case 8:
                Log.d("BaseActivity: ", "Account Clicked");
                closeDrawer = false;
                break;
            case 9:
                Log.d("BaseActivity: ", "Change Password Clicked");
                break;
            case 10:
                Log.d("BaseActivity: ", "Signout Clicked");
                break;
            default:
                break;
        }

        Log.d("BaseActivity: ", "Item Clicked with position: " + position);
        if (closeDrawer) {
            mDrawerList.setItemChecked(position, true);
            Toast.makeText(this, "Item Selected: " + position, Toast.LENGTH_LONG).show();
            mDrawerLayout.closeDrawer(mDrawerList);
        }
    }

    private List<DrawerItem> getDrawerItemList(){
        dataList = new ArrayList<DrawerItem>();

        dataList.add(new DrawerItem(true));
        dataList.add(new DrawerItem("Home", R.drawable.ic_home));
        dataList.add(new DrawerItem("Prescription", R.drawable.ic_prescription));

        dataList.add(new DrawerItem("Vital"));
        dataList.add(new DrawerItem("Blood Sugar", R.drawable.ic_action_refresh));
        dataList.add(new DrawerItem("Blood Pressure", R.drawable.ic_blood_pressure));
        dataList.add(new DrawerItem("Temperature",  R.drawable.ic_temperature));
        dataList.add(new DrawerItem("Pulse", R.drawable.ic_action_refresh));

        dataList.add(new DrawerItem("Accounts"));
        dataList.add(new DrawerItem("Change Password", R.drawable.ic_password));
        dataList.add(new DrawerItem("Signout", R.drawable.ic_action_refresh));

        return dataList;
    }
}