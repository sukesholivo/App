package in.olivo.patientcare.main.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import in.olivo.patientcare.main.BaseActivity;
import in.olivo.patientcare.main.R;
import in.olivo.patientcare.main.om.UserProfile;
import in.olivo.patientcare.main.services.HTTPServiceHandler;
import in.olivo.patientcare.main.utility.Constants;
import in.olivo.patientcare.main.utility.Logger;
import in.olivo.patientcare.main.utility.Utils;

/**
 * Created by satya on 27/1/16.
 */
public class AddPatientActivity extends BaseActivity {

    private static final String TAG = AddPatientActivity.class.getSimpleName();
    ViewHolder viewHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);
        Toolbar toolbar = (Toolbar) this.findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setTitle("Add new patient");
            actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
            actionbar.setDisplayHomeAsUpEnabled(true);
        }
        if (viewHolder == null) {
            viewHolder = new ViewHolder(this);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                Intent contactActivity = new Intent(this, ContactsActivity.class);
                startActivity(contactActivity);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void saveData() {

        UserProfile userProfile = Utils.getPatientDataFromSharedPreference(this);
        List<NameValuePair> data = new ArrayList<>();
        data.add(new BasicNameValuePair("role", userProfile.getRole()));
        data.add(new BasicNameValuePair("creator_uname", userProfile.getEmail()));
        data.add(new BasicNameValuePair("phone", viewHolder.phone.getText().toString()));
        Logger.d(TAG, viewHolder.phone.getText().toString());
        new SavePatient().execute(data.toArray(new NameValuePair[data.size()]));

    }

    private class ViewHolder {

        TextView phone;
        Button addButton;

        public ViewHolder(Activity activity) {
//            profilePic = (ImageView) activity.findViewById(R.id.profile_pic);
//            profilePicAddButton = (ImageButton) activity.findViewById(R.id.profile_pic_add_button);
//            name = (TextView) activity.findViewById(R.id.name);
//            email = (TextView) activity.findViewById(R.id.email);
            phone = (TextView) activity.findViewById(R.id.phone);
            addButton = (Button) activity.findViewById(R.id.add_button);

            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(AddPatientActivity.this, "Adding patient", Toast.LENGTH_LONG).show();
                    saveData();
                }
            });

           /* profilePicAddButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                }
            });*/
        }

    }

    private class SavePatient extends AsyncTask<NameValuePair, Void, String> {
        @Override
        protected String doInBackground(NameValuePair... params) {
            List<NameValuePair> data = new ArrayList<>(Arrays.asList(params));
            JSONObject jsonData = new JSONObject();
            try {
                for (NameValuePair pair : data) {
                    jsonData.put(pair.getName(), pair.getValue());
                }
                System.out.print("Request " + jsonData.toString());
                HTTPServiceHandler httpServiceHandler = new HTTPServiceHandler(AddPatientActivity.this);
                String response = httpServiceHandler.makeServiceCall(Constants.ADD_PATIENT_API_URL, HTTPServiceHandler.HTTPMethod.POST, null, jsonData);
                Logger.d(TAG, "Add patient Response: " + response);
                return response;

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            if (response != null) {
                Toast.makeText(AddPatientActivity.this, "Added Patient Successfully!", Toast.LENGTH_SHORT).show();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String threadId = jsonObject.getString("threadId");
                    Intent intent = ThreadDetailActivity.createThreadDetailIntent(AddPatientActivity.this, threadId, null, null, null);
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(AddPatientActivity.this, "Adding Patient Failed!", Toast.LENGTH_SHORT).show();
            }

        }
    }
}
