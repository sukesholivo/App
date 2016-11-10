package in.olivo.patientcare.main;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.text.Html;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.olivo.patientcare.main.om.TreatmentInfo;
import in.olivo.patientcare.main.om.UserSignup;
import in.olivo.patientcare.main.services.HTTPServiceHandler;
import in.olivo.patientcare.main.utility.Constants;
import in.olivo.patientcare.main.utility.GetServerAuthTokenAsync;
import in.olivo.patientcare.main.utility.Logger;
import in.olivo.patientcare.main.utility.Utils;

/**
 * Created by Leak on 12/11/2014.
 */
public class StartPageActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener{
    String mUsernameFromSMS;
    String mPasswordFromSMS;


    TextView _signupLink;

    String _mobileNumber="", mobile="";

    private static final int REQUEST_SIGNUP = 0;


    private static final String TAG = "StartPageActivity";
    private Boolean isClicked = false;
    UserSignup userSignup;
    private static final String role = "PATIENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MainApplication) getApplication()).startTracker();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        setContentView(R.layout.activity_startup);
        setLoginListener();
        setupServerAuthToken();
//        getUsernameAndPasswordFromSms();
//        tryLogin();




        _signupLink = (TextView)findViewById(R.id.link_signup);
        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });


        // Google SignIN onclick action Listener
        SignInButton google_signin = ((SignInButton)StartPageActivity.this.findViewById(R.id.sign_in_button));
        setButtonText(google_signin, "SIGN IN WITH GOOGLE");

        google_signin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                Intent _intent=new Intent(StartPageActivity.this ,GoogleSigninActivity.class);

                startActivity(_intent);

                Log.d(TAG, " Successful Login");
            }
        });


        // Show or Hide Password LOGIC
        final ImageView imagebtt = (ImageView) findViewById(R.id.eyeImage);


        final EditText editText = (EditText) findViewById(R.id.loginPassword);

        imagebtt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isClicked = isClicked ? false : true;
                if (isClicked) {
                    // button.setText("Hide Password");

                    editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    Toast toast= Toast.makeText(getApplicationContext(),
                            "Show Password", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP| Gravity.RIGHT, 0, 0);
                    toast.show();

                } else {

                    Toast toast= Toast.makeText(getApplicationContext(),
                            "Hide password", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP| Gravity.RIGHT, 0, 0);
                    toast.show();
                    editText.setTransformationMethod(PasswordTransformationMethod.getInstance());

                }
            }
        });

        // Calling Intent created in SignUpActivity, After successful Signup, display message to User
        Intent intent=getIntent();
        String intent_email = intent.getStringExtra("Email");

        TextView tvEmail = (TextView)findViewById(R.id.tv_email);
        tvEmail.setText(intent_email);
        tvEmail.setGravity(Gravity.CENTER);

    }

    protected void setButtonText(SignInButton signInButton, String buttonText) {
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            // if the view is instance of TextView then change the text SignInButton
            if (v instanceof TextView) {
                TextView tv = (TextView) v;

                tv.setTextColor(Color.parseColor("#555555"));
                tv.setText(buttonText);

                //tv.setGravity(Gravity.AXIS_PULL_AFTER);

                return;
            }
        }
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
        if (!ServerAccessToken.isEmpty()) {
            this.findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
            this.findViewById(R.id.loginPanel).setVisibility(View.GONE);
            new GetTreatmentSummaryAsync().execute();
        }
    }

    private void launchMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        this.startActivity(intent);
        this.finish();
    }

    private void setLoginListener() {
        TextView textView = (TextView) this.findViewById(R.id.tosLink);
        textView.setClickable(true);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        String text = "By using this app, I agree to the <a href='http://ikonnect.olivo.in/tos'> Terms of Use </a>";
        textView.setText(Html.fromHtml(text));

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Login into the app and populate the shared preferences
                String username = ((EditText) StartPageActivity.this.findViewById(R.id.loginUsername)).getText().toString().trim();
                String password = ((EditText) StartPageActivity.this.findViewById(R.id.loginPassword)).getText().toString();
                Login(username, password);
            }
        };
        Button loginButton = (Button) StartPageActivity.this.findViewById(R.id.loginButton);
        loginButton.setOnClickListener(clickListener);

        View.OnClickListener forgotPasswordClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Login into the app and populate the shared preferences
                String email = ((EditText) StartPageActivity.this.findViewById(R.id.loginUsername)).getText().toString().trim();
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
        nameValuePairs.add(new BasicNameValuePair("role", role));
        //Validate username and password here before proceeding
        StartPageActivity.this.findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
        new GetServerAuthTokenAsync(StartPageActivity.this, Constants.LOGIN_URL, nameValuePairs, username).execute();
    }

    private void getUsernameAndPasswordFromSms() {
        Uri inboxURI = Uri.parse("content://sms/inbox");
        String[] reqCols = new String[]{"address", "body"};
        ContentResolver cr = getContentResolver();
        Cursor cursor = cr.query(inboxURI, reqCols, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(0).contains("MDOCTL")) {
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

    private void tryLogin() {
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

        private void getTreatmentDetail(Context c) {
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
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

        }
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

}