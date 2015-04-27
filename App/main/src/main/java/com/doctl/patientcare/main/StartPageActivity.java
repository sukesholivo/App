package com.doctl.patientcare.main;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.doctl.patientcare.main.om.TreatmentInfo;
import com.doctl.patientcare.main.services.HTTPServiceHandler;
import com.doctl.patientcare.main.utility.Constants;
import com.doctl.patientcare.main.utility.GetServerAuthTokenAsync;
import com.doctl.patientcare.main.utility.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Leak on 12/11/2014.
 */
public class StartPageActivity extends BaseActivity {
    String mUsernameFromSMS;
    String mPasswordFromSMS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.hide();
        }
        setContentView(R.layout.activity_startup);
        setLoginListener();
        setupServerAuthToken();
//        getUsernameAndPasswordFromSms();
//        tryLogin();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MainApplication.activityResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MainApplication.activityPaused();
    }

    private void setupServerAuthToken() {
        Context context = StartPageActivity.this;
        String ServerAccessToken = Utils.getAuthTokenFromSharedPreference(context);
        if(!ServerAccessToken.isEmpty()) {
            this.findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
            this.findViewById(R.id.loginPanel).setVisibility(View.GONE);
            new GetTreatmentSummaryAsync().execute();
        }
    }

    private void launchMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        this.startActivity(intent);
        this.finish();
    }

    private void setLoginListener(){
        TextView textView =(TextView)this.findViewById(R.id.tosLink);
        textView.setClickable(true);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        String text = "By using this app, I agree to the <a href='http://www.doctl.com/tos'> Terms of Use </a>";
        textView.setText(Html.fromHtml(text));

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Login into the app and populate the shared preferences
                String username = ((EditText)StartPageActivity.this.findViewById(R.id.loginUsername)).getText().toString().trim();
                String password = ((EditText)StartPageActivity.this.findViewById(R.id.loginPassword)).getText().toString();
                Login(username, password);
            }
        };
        Button loginButton = (Button)StartPageActivity.this.findViewById(R.id.loginButton);
        loginButton.setOnClickListener(clickListener);

        View.OnClickListener forgotPasswordClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Login into the app and populate the shared preferences
                String email = ((EditText)StartPageActivity.this.findViewById(R.id.loginUsername)).getText().toString().trim();
                //Validate username and password here before proceeding
                new ForgotPasswordAsync().execute(email);
//                StartPageActivity.this.findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
            }
        };
//        Button forgotPasswordButton = (Button)StartPageActivity.this.findViewById(R.id.forgotPasswordButton);
//        forgotPasswordButton.setOnClickListener(forgotPasswordClickListener);
    }

    private void Login(String username, String password) {
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("username", username));
        nameValuePairs.add(new BasicNameValuePair("password", password));
        //Validate username and password here before proceeding
        StartPageActivity.this.findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
        new GetServerAuthTokenAsync(StartPageActivity.this, Constants.LOGIN_URL, nameValuePairs).execute();
    }

    private void getUsernameAndPasswordFromSms(){
        Uri inboxURI = Uri.parse("content://sms/inbox");
        String[] reqCols = new String[] {"address", "body" };
        ContentResolver cr = getContentResolver();
        Cursor cursor = cr.query(inboxURI, reqCols, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(0).contains("MDOCTL")){
                    String msg = cursor.getString(1);
                    msg = msg.substring(msg.indexOf("Email:"));
                    String[] msgData = msg.split(" ");
                    mUsernameFromSMS = msgData[1];
                    mPasswordFromSMS = msgData[3];
                    break;
                }
            } while (cursor.moveToNext());
        }
    }

    private void tryLogin(){
        Login(mUsernameFromSMS, mPasswordFromSMS);
    }

    public class GetTreatmentSummaryAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            getTreatmentDetail(StartPageActivity.this);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            launchMainActivity();
        }

        private void getTreatmentDetail(Context c){
            String response = new HTTPServiceHandler(c).makeServiceCall(Constants.TREATMENT_DETAIL_URL, HTTPServiceHandler.HTTPMethod.GET, null, null);
            if (response != null && !response.isEmpty()) {
                JsonParser parser = new JsonParser();
                JsonObject treatmentDetail = parser.parse(response).getAsJsonObject();
                TreatmentInfo treatmentInfo = new Gson().fromJson(treatmentDetail, TreatmentInfo.class);
                Utils.saveTreatmentDetailToSharedPreference(c, treatmentInfo);
            }
        }
    }

    public class ForgotPasswordAsync extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... arg0) {
            String email = arg0[0];
            try {
                JSONObject data = new JSONObject();
                data.put("email", email);
                new HTTPServiceHandler(StartPageActivity.this).makeServiceCall(Constants.FORGOT_PASSWORD_URL, HTTPServiceHandler.HTTPMethod.POST, null, data, true);
            } catch (JSONException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

        }
    }
}
