package in.olivo.patientcare.main.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;

import org.apache.http.NameValuePair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import in.olivo.patientcare.main.BaseActivity;
import in.olivo.patientcare.main.MainActivity;
import in.olivo.patientcare.main.R;
import in.olivo.patientcare.main.om.UserProfile;
import in.olivo.patientcare.main.om.chat.ThreadListAdapter;
import in.olivo.patientcare.main.om.chat.ThreadSummary;
import in.olivo.patientcare.main.utility.Constants;
import in.olivo.patientcare.main.utility.OfflineCacheAsyncTask;
import in.olivo.patientcare.main.utility.Utils;

/**
 * Created by Administrator on 5/4/2015.
 */
public class ThreadListActivity extends BaseActivity {

    private static final String TAG = ThreadListActivity.class.getSimpleName();

    private UserProfile currUserProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread_list);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Chat");
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        currUserProfile = Utils.getPatientDataFromSharedPreference(this);
        //Log.d(TAG, "database path "+getDatabasePath("offline.db"));
        refresh();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
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
            case R.id.action_search:
                Intent intent = new Intent(this, ContactsActivity.class);
                startActivity(intent);
                return true;
            /*case R.id.action_add_user:
                Intent addUserIntent = new Intent(this, AddPatientActivity.class);
                startActivity(addUserIntent);
                return true;*/
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void refresh() {
        new GetThreads(ThreadListActivity.this, Constants.QUESTION_URL, null).execute();
    }


    private void updateUi(String jsonResponse) {
        if (jsonResponse != null && !jsonResponse.isEmpty()) {
            final ThreadSummary[] threadSummaryList = parseThreadList(jsonResponse);
            runOnUiThread(new Runnable() {
                public void run() {
                    updateList(threadSummaryList);
                }
            });
        }
    }

    private ThreadSummary[] parseThreadList(String jsonStr) {
        return new Gson().fromJson(jsonStr, ThreadSummary[].class);
    }

    private void updateList(ThreadSummary[] threadSummaries) {

        List<ThreadSummary> threadSummaryList = new ArrayList<>();
        for (ThreadSummary q : threadSummaries) {
            threadSummaryList.add(q);
        }
        Collections.sort(threadSummaryList, Collections.reverseOrder(ThreadSummary.ORDER_BY_LATEST_MESSAGE));
        ThreadListAdapter threadListAdapter = new ThreadListAdapter(this, threadSummaryList, currUserProfile.getId());
        ListView threadListView = (ListView) this.findViewById(R.id.thread_list);
        threadListView.setAdapter(threadListAdapter);
        threadListView.setEmptyView(findViewById(R.id.emptyElement));


        threadListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                final ThreadSummary item = (ThreadSummary) parent.getItemAtPosition(position);
                Intent intent = new Intent(ThreadListActivity.this, ThreadDetailActivity.class);
                // sending data to new activity
                intent.putExtra(Constants.THREAD_ID, item.getId());
                if (item.getUsers() != null) {
                    UserProfile userProfile = UserProfile.getOtherUserProfile(currUserProfile.getId(), item.getUsers());
                    if (userProfile != null) {
                        intent.putExtra(Constants.USER_ID, userProfile.getId());
                        intent.putExtra(Constants.PROFILE_PIC_URL, userProfile.getProfilePicUrl());
                        intent.putExtra(Constants.DISPLAY_NAME, userProfile.getDisplayName());
                    }
                }
                startActivity(intent);
            }
        });
    }

    private class GetThreads extends OfflineCacheAsyncTask {

        public GetThreads(Context context, String url, List<NameValuePair> getParams) {
            super(context, url, getParams, true);
        }

        @Override
        protected void onResponseReceived(String response) {
            updateUi(response);
        }
    }
}