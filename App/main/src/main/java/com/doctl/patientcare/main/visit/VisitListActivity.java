package com.doctl.patientcare.main.visit;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.doctl.patientcare.main.BaseActivityWithNavigation;
import com.doctl.patientcare.main.R;
import com.doctl.patientcare.main.om.UserProfile;
import com.doctl.patientcare.main.om.medicines.Prescription;
import com.doctl.patientcare.main.services.HTTPServiceHandler;
import com.doctl.patientcare.main.utility.Constants;
import com.doctl.patientcare.main.utility.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.picasso.Picasso;

public class VisitListActivity extends BaseActivityWithNavigation {
//    public final static String EXTRA_MESSAGE = "in.co.example.rmg.myfirstapp.MESSAGE";

    public final static String EXTRA_MESSAGE = VisitListActivity.EXTRA_MESSAGE;
    ListView list;
    public UserProfile doctor;
    public final static String TAG = VisitListActivity.class.getSimpleName();
    static boolean active = false;
    private String contactId = "";

    Integer[] imageId = {

    };
//

   String[] values = new String[] { "01/11/15", "23/11/15", "23/12/15", "12/01/16", "24/01/16", "15/02/16"};
    String[] hospname = new String[] { "Rajini Health Clinics", "Olivo Hospital",
            "Apolo Hospital", "Olivo Hospital", "CHL Apolo", "Sanjivani Hospital"};
    String[] spec = new String[] { "Physiotherapist", "Endocrinologist",
            "Oncologist", "Cardiologist", "Physiotherapist", "Oncologist"};
    String[] docname = new String[] { "Dr. Rajini", "Dr. Ritesh D", "Dr. Kailash S",
            "Dr. Priya Jha", "Dr. Sayali Mahajan", "Dr. Rahul M"};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit_list);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(mToolbar);
        this.setupNavigationDrawer();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Olivo");
//            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
//            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        VisitListAdapter adapter = new VisitListAdapter(VisitListActivity.this, values, imageId, docname, hospname, spec);
        list = (ListView) findViewById(R.id.SubjectList);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Toast.makeText(VisitListActivity.this, "You Clicked at " + values[+position], Toast.LENGTH_SHORT).show();
                Intent intent;
                        intent = new Intent(VisitListActivity.this, VisitDetailActivity.class);
//                        EditText editText = (EditText) findViewById(R.id.edit_message);

                        String message = values[position].toString();
                        intent.putExtra(EXTRA_MESSAGE, message);
                        VisitListActivity.this.startActivity(intent);

            }
        });


        doctor = Utils.getPatientDataFromSharedPreference(this);
        int i = list.getCount();
        contactId = "";
        active = true;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            contactId = bundle.getString("contactId");
        }
//        while (i-- !=0){
//            refresh();
//        }
//        updateUI();
    }

    /*public UserProfile getDoctor() {
        return doctor;
    }*/

//    @Override
//    protected void onStart() {
//        super.onStart();
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.setTitle("Olivo");
//        }
//        contactId = "";
//        active = true;
//        Bundle bundle = getIntent().getExtras();
//        if (bundle != null){
//            contactId = bundle.getString("prescriptionId");
//        }
//        refresh();
//    }



    private String downloadPrescriptionData(String contactId) {
        if (contactId == null){
            contactId = "";
        }
        String url = Constants.PRESCRIPTION_URL + contactId;
        HTTPServiceHandler serviceHandler = new HTTPServiceHandler(this);
        return serviceHandler.makeServiceCall(url, HTTPServiceHandler.HTTPMethod.GET, null, null);
    }

    private Prescription parsePrescriptionData(String jsonStr){
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(jsonStr).getAsJsonObject();

        return new Gson().fromJson(jsonObject, Prescription.class);
    }

    private void refresh(){
        if (Utils.isNetworkAvailable(this)){
            new GetContact().execute(contactId);
        } else {
            Toast.makeText(this, "No Network Connection", Toast.LENGTH_LONG).show();
        }
    }

    private class GetContact extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... arg0) {
            refreshActivity(arg0[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    private void refreshActivity(String contactId) {
        String jsonStr = downloadPrescriptionData(contactId);
        if (jsonStr != null && !jsonStr.isEmpty()) {
            final Prescription prescription = parsePrescriptionData(jsonStr);

            runOnUiThread(new Runnable() {
                public void run() {
                    updateUI(prescription);
                }
            });
        }
    }

    private void updateUI(final Prescription contact) {
    //private void updateUI() {
        LinearLayout doctorLayout = (LinearLayout) findViewById(R.id.doclayout);
        if (contact.getDoctor() != null) {
            doctorLayout.setVisibility(View.VISIBLE);
            TextView doctorName = (TextView) findViewById(R.id.doctorname);
            doctorName.setText(contact.getDoctor().getDisplayName());

            TextView doctorAddress = (TextView) findViewById(R.id.doctorhospital);
            UserProfile.Address address = contact.getDoctor().getAddress();
            if (address != null) {
                doctorAddress.setText(address.getPrintableAddress());
            }

            Picasso.with(this)
                    .load(Constants.SERVER_URL + contact.getDoctor().getProfilePicUrl())
                    .into((ImageView) findViewById(R.id.doctorpic));
        } else {
            doctorLayout.setVisibility(View.GONE);
        }
    }

}