package com.doctl.patientcare.main.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.doctl.patientcare.main.BaseActivity;
import com.doctl.patientcare.main.MainActivity;
import com.doctl.patientcare.main.R;
import com.doctl.patientcare.main.om.chat.ThreadSummary;
import com.doctl.patientcare.main.om.chat.ThreadListAdapter;
import com.doctl.patientcare.main.utility.Constants;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 5/4/2015.
 */
public class ThreadListActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread_list);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setTitle("Chat");
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        refresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.thread_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent mainActivityIntent = new Intent(this, MainActivity.class);
                mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mainActivityIntent);
                return true;
            case R.id.action_add_thread:
                Intent intent = new Intent(this, ContactsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void refresh() {
        new GetThreads().execute();
    }

    private void refreshActivity() {
        String jsonStr = downloadThreads();
        if (jsonStr != null && !jsonStr.isEmpty()) {
            final ThreadSummary[] threadSummaryList = parseThreadList(jsonStr);
            runOnUiThread(new Runnable() {
                public void run() {
                    updateList(threadSummaryList);
                }
            });
        }
    }

    private String downloadThreads() {
        return "[{\"userProfile\":{\"id\":\"3d1e85\",\"displayName\":\"name1\",\"profilePicUrl\":\"/static/files/uploaded_files/1432672583_34_Akansha.jpg\",\"role\":\"role1\"},\"latestMessage\":{\"timestamp\":\"2016-01-14T13:45:42Z\",\"fileUrl\":null,\"id\":23,\"text\":\"aa\"},\"numOfUnreadMessage\":2}, {\"userProfile\":{\"id\":\"3d1e89\",\"displayName\":\"other Name\",\"profilePicUrl\":\"/static/files/uploaded_files/1432672583_34_Akansha.jpg\",\"role\":\"role2\"},\"latestMessage\":{\"timestamp\":\"2016-01-14T15:45:42Z\",\"fileUrl\":null,\"id\":22,\"text\":\"bfdgdf\"},\"numOfUnreadMessage\":1}]";
//        return dummyJSON;
        /*String url = Constants.QUESTION_URL;
        HTTPServiceHandler serviceHandler = new HTTPServiceHandler(this);
        return serviceHandler.makeServiceCall(url, HTTPServiceHandler.HTTPMethod.GET, null, null);*/
    }

    private ThreadSummary[] parseThreadList(String jsonStr){
        return new Gson().fromJson(jsonStr, ThreadSummary[].class);
    }

    private void updateList(ThreadSummary[] threadSummaries){
        List<ThreadSummary> threadSummaryList = new ArrayList<>();
        for (ThreadSummary q : threadSummaries){
            threadSummaryList.add(q);
        }
        ThreadListAdapter threadListAdapter = new ThreadListAdapter(this, threadSummaryList);
        ListView threadListView = (ListView) this.findViewById(R.id.thread_list);
        threadListView.setAdapter(threadListAdapter);

        threadListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                final ThreadSummary item = (ThreadSummary) parent.getItemAtPosition(position);
                Intent intent = new Intent(ThreadListActivity.this, ThreadDetailActivity.class);
                // sending data to new activity
                intent.putExtra(Constants.USER_ID, item.getUserProfile().getId());
                intent.putExtra(Constants.PROFILE_PIC_URL, item.getUserProfile().getProfilePicUrl());
                intent.putExtra(Constants.DISPLAY_NAME, item.getUserProfile().getDisplayName());
                startActivity(intent);
            }
        });
    }

    private class GetThreads extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            refreshActivity();
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }
}
