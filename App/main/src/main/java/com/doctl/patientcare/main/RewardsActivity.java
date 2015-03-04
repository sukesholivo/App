package com.doctl.patientcare.main;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.doctl.patientcare.main.om.rewards.RewardArrayAdapter;
import com.doctl.patientcare.main.om.rewards.Rewards;
import com.doctl.patientcare.main.services.HTTPServiceHandler;
import com.doctl.patientcare.main.utility.Constants;
import com.doctl.patientcare.main.utility.Utils;
import com.google.gson.Gson;


/**
 * Created by Administrator on 2/26/2015.
 */
public class RewardsActivity extends ActionBarActivity {
    private static final String TAG = RewardsActivity.class.getSimpleName();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_rewards);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setTitle(getResources().getString(R.string.nav_item_rewards));
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        refresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.rewards_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            case R.id.action_refresh_rewards:
                refresh();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void refresh(){
        if (Utils.isNetworkAvailable(this)){
            new GetRewards().execute();
        } else {
            Toast.makeText(this, "No Network Connection", Toast.LENGTH_LONG).show();
        }
    }

    private void getRewards(){
        String data = downloadRewardsData();
        if (data != null && !data.isEmpty()) {
            Rewards mRewards = parseRewardsData(data);
            refreshUI(mRewards);
        }
    }

    private String downloadRewardsData(){
        HTTPServiceHandler serviceHandler = new HTTPServiceHandler(this);
        return serviceHandler.makeServiceCall(
                Constants.REWARDS_URL, HTTPServiceHandler.HTTPMethod.GET, null, null);
    }

    private Rewards parseRewardsData(String data){
        return new Gson().fromJson(data, Rewards.class);
    }

    private void refreshUI(final Rewards rewards){
        this.runOnUiThread(new Runnable() {
            public void run() {
                TextView pointsTextView = (TextView) findViewById(R.id.totalPointsText);
                pointsTextView.setText("" + rewards.getTotalPoints());
                ListView mRewardsList = (ListView) RewardsActivity.this.findViewById(R.id.clinic_rewards_list);
                RewardArrayAdapter mRewardArrayAdapter = new RewardArrayAdapter(RewardsActivity.this, rewards.getByClinic(), rewards.getTotalPoints());
                mRewardsList.setAdapter(mRewardArrayAdapter);
            }
        });
    }

    private class GetRewards extends AsyncTask<Object, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Object... arg0) {
            getRewards();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }
}
