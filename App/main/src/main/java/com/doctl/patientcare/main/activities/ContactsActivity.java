package com.doctl.patientcare.main.activities;

import android.app.SearchManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.doctl.patientcare.main.BaseActivity;
import com.doctl.patientcare.main.MainActivity;
import com.doctl.patientcare.main.R;
import com.doctl.patientcare.main.om.UserProfile;
import com.doctl.patientcare.main.om.contact.ContactData;
import com.doctl.patientcare.main.services.HTTPServiceHandler;
import com.doctl.patientcare.main.utility.Constants;
import com.doctl.patientcare.main.utility.Logger;
import com.doctl.patientcare.main.utility.Utils;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by satya on 19/1/16.
 */
public class ContactsActivity extends BaseActivity {

    private ContactListAdapter mAdapter;
    private final String TAG = ContactsActivity.class.getSimpleName();
    UserProfile currUserProfile;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_list);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setTitle("Contacts");
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mAdapter = new ContactListAdapter(this, new ArrayList<UserProfile>(), new HashMap<Integer, String>());
        new GetContacts().execute(Constants.CONTACTS_URL);
        ListView contactListView = (ListView) this.findViewById(R.id.list);
        contactListView.setAdapter(mAdapter);
        contactListView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final UserProfile item = (UserProfile) parent.getItemAtPosition(position);
               /* Intent intent = new Intent(ContactsActivity.this, ThreadDetailActivity.class);
                // sending data to new activity
                intent.putExtra(Constants.USER_ID, item.getId());
                intent.putExtra(Constants.PROFILE_PIC_URL, item.getProfilePicUrl());
                intent.putExtra(Constants.DISPLAY_NAME, item.getDisplayName());
                startActivity(intent);*/
                new CreateThread().execute(item.getId(), item.getProfilePicUrl(), item.getDisplayName());
            }
        });
        currUserProfile = Utils.getPatientDataFromSharedPreference(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.contact_list, menu);
        // Retrieve the SearchView and plug it into SearchManager
        final SearchView msearchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        msearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        msearchView.setIconifiedByDefault(false);
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

        msearchView.setOnQueryTextListener(listener);
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
            case R.id.action_add_contact:
                Intent intent = new Intent(this, AddPatientActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private String downloadContacts(String baseURL, String filter) {
        String url = baseURL;
        if( filter != null && !filter.isEmpty()){
            url = baseURL+"?search_str=" + filter;
        }
        HTTPServiceHandler serviceHandler = new HTTPServiceHandler(this);
        return serviceHandler.makeServiceCall(url, HTTPServiceHandler.HTTPMethod.GET, null, null);
    }

    private void updateContactList(String baseURL, String filter) {

        ContactData contactData = parseContactData(downloadContacts(baseURL, filter));
        if(contactData!= null) {
            mAdapter.updateData(contactData.getAllUserProfiles(), contactData.getListPositions(), filter);
        }
    }

    private ContactData parseContactData(String jsonStr) {
        System.out.println("Response " + jsonStr);
        return new Gson().fromJson(jsonStr, ContactData.class);
    }

    private class GetContacts extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... arg0) {
            String baseURL = arg0[0];
            String filter = null;
            if(arg0.length ==  2){
                filter = arg0[1];
            }
            updateContactList(baseURL, filter);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mAdapter.notifyDataSetChanged();
        }
    }

    private class CreateThread extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... arg0) {
            String userId=arg0[0], displayName=arg0[2],profilePicURL=arg0[1];
            String url = Constants.QUESTION_URL;
            JSONObject data = new JSONObject();

            try {
                data.put("user_ids", userId+","+currUserProfile.getId());
                HTTPServiceHandler serviceHandler = new HTTPServiceHandler(ContactsActivity.this);
                String response = serviceHandler.makeServiceCall(url, HTTPServiceHandler.HTTPMethod.POST, null, data);
                JSONObject jsonObject=new JSONObject(response);
                Logger.d(TAG, " Resopnse "+response);
                if(response ==null ) {
                    throw new Exception("No response from server");
                }
                String threadId = jsonObject.getInt("id") + "";
                Intent intent = ThreadDetailActivity.createThreadDetailIntent(ContactsActivity.this, threadId, userId, displayName, profilePicURL);
                startActivity(intent);

            } catch (Exception e){
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
