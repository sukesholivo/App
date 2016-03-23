package in.olivo.patientcare.main.activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import in.olivo.patientcare.main.BaseActivity;
import in.olivo.patientcare.main.R;
import in.olivo.patientcare.main.om.UserProfile;
import in.olivo.patientcare.main.om.contact.ContactData;
import in.olivo.patientcare.main.services.HTTPServiceHandler;
import in.olivo.patientcare.main.utility.Constants;
import in.olivo.patientcare.main.utility.Logger;
import in.olivo.patientcare.main.utility.OfflineCacheAsyncTask;
import in.olivo.patientcare.main.utility.Utils;

/**
 * Created by satya on 19/1/16.
 */
public class ContactsActivity extends BaseActivity {

    private final String TAG = ContactsActivity.class.getSimpleName();
    UserProfile currUserProfile;
    private ContactListAdapter mAdapter;
    private SearchView msearchView;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_list);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(mToolbar);
        msearchView = new SearchView(this);
        //SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        //msearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        msearchView.setIconifiedByDefault(false);
        msearchView.setFocusable(true);
        msearchView.requestFocusFromTouch();
        SearchView.OnQueryTextListener listener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                new GetContacts(ContactsActivity.this, Constants.CONTACTS_URL, null, query).execute();
                Log.d(TAG, "on QueryTextSubmit");
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                new GetContacts(ContactsActivity.this, Constants.CONTACTS_URL, null, query).execute();
                Log.d(TAG, "on QueryTextChange");
                return false;
            }
        };

        msearchView.setOnQueryTextListener(listener);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setCustomView(msearchView);
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//            actionBar.setTitle("Contacts");
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mAdapter = new ContactListAdapter(this, new ArrayList<UserProfile>(), new HashMap<Integer, String>());
        // newText is text entered by user to SearchView
        new GetContacts(ContactsActivity.this, Constants.CONTACTS_URL, null, null).execute();
        ListView contactListView = (ListView) this.findViewById(R.id.list);
        contactListView.setAdapter(mAdapter);
        contactListView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final UserProfile item = (UserProfile) parent.getItemAtPosition(position);
                new CreateThread().execute(item.getId(), item.getProfilePicUrl(), item.getDisplayName());
            }
        });
        currUserProfile = Utils.getPatientDataFromSharedPreference(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.contact_list, menu);
        // Retrieve the SearchView and plug it into SearchManager
        /*msearchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        msearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        msearchView.setIconifiedByDefault(false);
        msearchView.setFocusable(true);
        msearchView.requestFocusFromTouch();
        SearchView.OnQueryTextListener listener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                new GetContacts().execute(Constants.CONTACTS_URL, query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // newText is text entered by user to SearchView
                new GetContacts().execute(Constants.CONTACTS_URL, newText);
                return false;
            }
        };

        msearchView.setOnQueryTextListener(listener);*/
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent mainActivityIntent = new Intent(this, ThreadListActivity.class);
                mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mainActivityIntent);
                return true;
            /*case R.id.action_add_contact:
                Intent intent = new Intent(this, AddPatientActivity.class);
                startActivity(intent);
                return true;*/
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void updateUI(String jsonResponse, String filter) {
        ContactData contactData = parseContactData(jsonResponse);
        if (contactData != null) {
            mAdapter.updateData(contactData.getAllUserProfiles(), contactData.getListPositions(), filter);
            mAdapter.notifyDataSetChanged();
        }
    }

    private ContactData parseContactData(String jsonStr) {
        if(jsonStr == null) return null;
        return new Gson().fromJson(jsonStr, ContactData.class);
    }

    private class GetContacts extends OfflineCacheAsyncTask{

        private String filter;

        public GetContacts(Context context, String baseUrl, List<NameValuePair> getParams, String filter) {
            super(context, baseUrl, getParams, true);
            this.filter = filter;
            if (filter == null || filter.isEmpty()) {
                this.saveToLocal = true;
            } else {
                this.url = this.url + "?search_str=" + filter;
                this.saveToLocal = false;
            }
        }

        @Override
        protected void onResponseReceived(String response) {
            updateUI(response, filter);
        }
    }

    private class CreateThread extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... arg0) {
            String userId = arg0[0], displayName = arg0[2], profilePicURL = arg0[1];
            String url = Constants.QUESTION_URL;
            JSONObject data = new JSONObject();

            try {
                data.put("user_ids", userId + "," + currUserProfile.getId());
                HTTPServiceHandler serviceHandler = new HTTPServiceHandler(ContactsActivity.this);
                String response = serviceHandler.makeServiceCall(url, HTTPServiceHandler.HTTPMethod.POST, null, data);
                JSONObject jsonObject = new JSONObject(response);
                Logger.d(TAG, " Resopnse " + response);
                if (response == null) {
                    throw new Exception("No response from server");
                }
                String threadId = jsonObject.getInt("id") + "";
                Intent intent = ThreadDetailActivity.createThreadDetailIntent(ContactsActivity.this, threadId, userId, displayName, profilePicURL);
                startActivity(intent);

            } catch (Exception e) {
                Toast.makeText(getBaseContext(), "some error occurred", Toast.LENGTH_SHORT).show();
                Logger.e(TAG, e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }
}
