package com.doctl.patientcare.main;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.doctl.patientcare.main.om.UserProfile;
import com.doctl.patientcare.main.services.HTTPServiceHandler;
import com.doctl.patientcare.main.utility.Constants;
import com.doctl.patientcare.main.utility.Utils;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 12/30/2014.
 */
public class ChangeProfileActivity extends Activity {
    private static final String TAG = ChangeProfileActivity.class.getSimpleName();
    UserProfile userProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_change_profile);
        ActionBar actionBar = getActionBar();
        if (actionBar != null){
            actionBar.setTitle("Change Profile");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        userProfile = Utils.getUserDataFromSharedPreference(this);
        setData(userProfile);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_profile, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            case R.id.action_save_profile:
                saveProfile();
                Toast.makeText(this, "Profile saved", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setData(UserProfile userProfile){
        final ImageView photoView = (ImageView) findViewById(R.id.photo);
        if (userProfile.getProfilePicUrl() != null && !userProfile.getProfilePicUrl().isEmpty()) {
            Picasso.with(this)
                    .load(Constants.SERVER_URL + userProfile.getProfilePicUrl())
                    .into(photoView);
        }
        final EditText nameText = (EditText) findViewById(R.id.name);
        nameText.setText(userProfile.getDisplayName());
        setNameListener(nameText);

        final EditText phoneText = (EditText) findViewById(R.id.phone);
        phoneText.setText(userProfile.getPhone());
        setPhoneListener(phoneText);

        final EditText dobText = (EditText) findViewById(R.id.dob);
        String dob = userProfile.getDob();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date newDate = null;
        try {
            newDate = format.parse(dob);
        } catch (ParseException e){
            Log.e(TAG, e.getMessage());
        }

        format = new SimpleDateFormat("dd-MM-yyyy");
        String date = "";
        if (newDate != null) {
            date = format.format(newDate);
        }
        dobText.setText(date);
        setDOBListener(dobText);

        final EditText sexText = (EditText) findViewById(R.id.sex);
        String sex = userProfile.getSex();
        if (sex.equals("m")){
            sexText.setText("Male");
        }
        if (sex.equals("f")){
            sexText.setText("Female");
        }
        setSexListener(sexText);
    }

    private void setNameListener(final EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                Log.d(TAG, charSequence.toString());
                userProfile.setDisplayName(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void setPhoneListener(final EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                Log.d(TAG, charSequence.toString());
                userProfile.setPhone(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void setDOBListener(final EditText editText){
        String text = editText.getText().toString();
        String[] dates = text.split("/");
        int mDay = 1;
        int mMonth = 1;
        int mYear = 1980;
        if (dates.length == 3){
            mDay = Integer.getInteger(dates[0]);
            mMonth = Integer.getInteger(dates[1]);
            mYear = Integer.getInteger(dates[2]);
        }

        final DatePickerDialog dpd = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        editText.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                    }
                }, mYear, mMonth, mDay);
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b){
                    dpd.show();
                }
            }
        });
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dpd.show();
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                Log.d(TAG, charSequence.toString());
                Date newDate = null;
                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                try {
                    newDate = format.parse(charSequence.toString());
                } catch (ParseException e){
                    Log.e(TAG, e.getMessage());
                }

                format = new SimpleDateFormat("yyyy-MM-dd");
                String date = "";
                if (newDate != null) {
                    date = format.format(newDate);
                }
                userProfile.setDob(date);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void setSexListener(final EditText editText){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Gender")
                .setItems(R.array.sex_array, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        editText.setText(getResources().getStringArray(R.array.sex_array)[which]);
                    }
                });
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b){
                    builder.show();
                }
            }
        });
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.show();
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                Log.d(TAG, charSequence.toString());
                userProfile.setSex(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void saveProfile(){
        String url = Constants.PERSONAL_DETAIL_URL;
        JSONObject data = null;
        try {
            data = userProfile.getDataToPatch();
            EditText password1 = (EditText) findViewById(R.id.password1);
            String p1 = password1.getText().toString();
            EditText password2 = (EditText) findViewById(R.id.password2);
            String p2 = password2.getText().toString();

            if (p1 != null && !p1.isEmpty()){
                if (p2 == null || p2.isEmpty() || !p1.equals(p2)){
                    Toast.makeText(this, "Password doesn't match", Toast.LENGTH_LONG).show();
                    return;
                }
                data.put("password1", p1);
                data.put("password2", p2);
            }
        } catch (JSONException e){
            Log.e(TAG, e.getMessage());
        }
        Log.e(TAG, data.toString());
        new SaveProfile().execute(url, data);
    }

    private class SaveProfile extends AsyncTask<Object, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Object... arg0) {
            String url = (String)arg0[0];
            JSONObject data= (JSONObject)arg0[1];
            HTTPServiceHandler serviceHandler = new HTTPServiceHandler(ChangeProfileActivity.this);
            String response = serviceHandler.makeServiceCall(url, HTTPServiceHandler.HTTPMethod.PATCH, null, data);
            Log.e(TAG, response);
            UserProfile userProfile = new Gson().fromJson(response, UserProfile.class);
            Utils.saveUserDataToSharedPreference(ChangeProfileActivity.this, userProfile);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }
}