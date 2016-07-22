package in.olivo.patientcare.main.visit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.apache.http.NameValuePair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import in.olivo.patientcare.main.BaseActivityWithNavigation;
import in.olivo.patientcare.main.MainActivity;
import in.olivo.patientcare.main.R;
import in.olivo.patientcare.main.om.UserProfile;
import in.olivo.patientcare.main.utility.Constants;
import in.olivo.patientcare.main.utility.Logger;
import in.olivo.patientcare.main.utility.OfflineCacheAsyncTask;
import in.olivo.patientcare.main.utility.Utils;
import in.olivo.patientcare.main.visit.om.Visit;

public class VisitListActivity extends BaseActivityWithNavigation {
//    public final static String VISIT_URI = "in.co.example.rmg.myfirstapp.MESSAGE";

    public final static String VISIT_URI = "visit_uri";
    private final static String TAG = VisitListActivity.class.getSimpleName();
    private ListView listView;
    private UserProfile userProfile;
    private List<Visit> visits;
    private VisitListAdapter listAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit_list);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(mToolbar);
        this.setupNavigationDrawer();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Visits");
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        visits = new ArrayList<>();
        listAdapter = new VisitListAdapter(VisitListActivity.this, visits);
        listView = (ListView) findViewById(R.id.visit_list);
        listView.setAdapter(listAdapter);
        listView.setEmptyView(findViewById(R.id.emptyElement));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(VisitListActivity.this, VisitDetailActivity.class);
                Visit visit = (Visit) parent.getItemAtPosition(position);
                if (visit != null) {
                    intent.putExtra(VISIT_URI, visit.getUri());
                    startActivity(intent);
                } else {
                    Logger.e(TAG, "visit is null at position " + position);
                    Toast.makeText(VisitListActivity.this, "visit is null at position " + position, Toast.LENGTH_SHORT).show();
                }
            }
        });


        userProfile = Utils.getPatientDataFromSharedPreference(this);
        refresh();
    }

    private void refresh() {
        String getVisitsUrl = Constants.VISITS_URL;
//        String getVisitsUrl = "http://test.doctl.com/teledos/v1.0/visits/013b7432e6d749129431bef02173f965/";
        new GetVisits(VisitListActivity.this, getVisitsUrl, null).execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(VisitListActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private class GetVisits extends OfflineCacheAsyncTask{

        public GetVisits(Context context, String url, List<NameValuePair> getParams) {
            super(context, url, getParams, false);
        }

        @Override
        protected void onResponseReceived(String response) {
            if (response != null) {
                visits.clear();
                List<Visit> visitList = Arrays.asList(new Gson().fromJson(response, Visit[].class));
                Collections.sort(visitList, Collections.reverseOrder());
                visits.addAll(visitList);
                listAdapter.notifyDataSetChanged();
            }
        }
    }
}