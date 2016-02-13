package com.doctl.patientcare.main.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.doctl.patientcare.main.BaseActivity;
import com.doctl.patientcare.main.R;
import com.doctl.patientcare.main.om.UserProfile;
import com.doctl.patientcare.main.services.HTTPServiceHandler;
import com.doctl.patientcare.main.utility.Constants;
import com.doctl.patientcare.main.utility.Logger;
import com.doctl.patientcare.main.utility.Utils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        if( actionbar != null){
            actionbar.setTitle("Add new patient");
            actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
            actionbar.setDisplayHomeAsUpEnabled(true);
        }
        if(viewHolder == null){
            viewHolder = new ViewHolder(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_user, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                Intent contactActivity = new Intent(this, ContactsActivity.class);
                startActivity(contactActivity);
                return true;
            case R.id.action_save:
                saveData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void saveData(){

        UserProfile userProfile = Utils.getPatientDataFromSharedPreference(this);
        List<NameValuePair> data=new ArrayList<>();
        data.add(new BasicNameValuePair("role", userProfile.getRole()));
        data.add(new BasicNameValuePair("creator_uname", userProfile.getEmail()));
        data.add(new BasicNameValuePair("phone", viewHolder.phone.getText().toString()));
        Logger.d(TAG, viewHolder.name.getText().toString());
        Logger.d(TAG, viewHolder.email.getText().toString());
        Logger.d(TAG, viewHolder.phone.getText().toString());
        new SavePatient().execute(data.toArray(new NameValuePair[data.size()]));
        Intent intent=new Intent(this, ContactsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }
    private class SavePatient extends AsyncTask<NameValuePair,Void, Void>{
        @Override
        protected Void doInBackground(NameValuePair... params) {
            List<NameValuePair> data=new ArrayList<>(Arrays.asList(params));
            JSONObject jsonData=new JSONObject();
            for(NameValuePair pair:data){
                try {
                    jsonData.put(pair.getName(), pair.getValue());
                    HTTPServiceHandler httpServiceHandler=new HTTPServiceHandler(AddPatientActivity.this);
                    String response = httpServiceHandler.makeServiceCall(Constants.ADD_PATIENT_API_URL, HTTPServiceHandler.HTTPMethod.POST, null, jsonData);
                    if(response != null){
                        Toast.makeText(AddPatientActivity.this, "Added Patient Successfully!", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(AddPatientActivity.this, "Adding Patient Failed!", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }
    static class ViewHolder{
        ImageView profilePic;
        ImageButton profilePicAddButton;
        TextView name, email, phone;

        public ViewHolder(Activity activity){
            profilePic = (ImageView)activity.findViewById(R.id.profile_pic);
            profilePicAddButton = (ImageButton) activity.findViewById(R.id.profile_pic_add_button);
            name = (TextView) activity.findViewById(R.id.name);
            email = (TextView) activity.findViewById(R.id.email);
            phone = (TextView) activity.findViewById(R.id.phone);

            profilePicAddButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {

                    
                }
            });
        }

        @Override
        public String toString() {
            return "ViewHolder{" +
                    "profilePic=" + profilePic +
                    ", profilePicAddButton=" + profilePicAddButton +
                    ", name=" + name +
                    ", email=" + email +
                    ", phone=" + phone +
                    '}';
        }
    }
}
