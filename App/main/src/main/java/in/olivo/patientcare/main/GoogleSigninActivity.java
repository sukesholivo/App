package in.olivo.patientcare.main;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.olivo.patientcare.main.om.UserSignup;
import in.olivo.patientcare.main.services.HTTPServiceHandler;
import in.olivo.patientcare.main.utility.Constants;
import in.olivo.patientcare.main.utility.GetServerAuthTokenAsync;
import in.olivo.patientcare.main.utility.Logger;
import in.olivo.patientcare.main.utility.Utils;

public class GoogleSigninActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener {


    GoogleApiClient mGoogleApiClient;
    TextView _signupLink;


    String _mobileNumber="", mobile="";

    Spinner spSpecialization;
    String specialization;

    private static final int REQUEST_SIGNUP = 0;

    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "StartPageActivity";
    private Boolean isClicked = false;
    UserSignup userSignup;
    private static final String role = "PATIENT";
    TextView _loginLink;
    GoogleSignInResult result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_signin);
        Intent intent = getIntent();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();


        // ATTENTION: This "addApi(AppIndex.API)"was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

        if (result!=null ){
            boolean userLogged =result.isSuccess();

            //Log.d(TAG, " userLogged    is ---->"+""+ userLogged);

        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(AppIndex.API).build();


        mGoogleApiClient.connect();



        SignInButton google_signin = ((SignInButton) this.findViewById(R.id.sign_in_button));
        setButtonText(google_signin, "SIGN IN WITH GOOGLE");

        google_signin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Google SignIn activity



                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);

                System.out.println(signInIntent);
                Log.d(TAG, " Successful Login");
            }
        });

        _loginLink = (TextView)findViewById(R.id.link_login);
        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  return to the Login activity
                Intent intent = new Intent(getApplicationContext(),StartPageActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

            }
        });

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

    // implementing Google sign - in Logic
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);

        Log.d(TAG, " requestCode is "+requestCode);
        if (requestCode == RC_SIGN_IN) {
            //GoogleSigninActivity.this.findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);


        }
    }

    private void handleSignInResult(GoogleSignInResult result) {

        //GoogleSigninActivity.this.findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);

        Log.d(TAG, " In method handleSignInResult()");
        Log.d(TAG,"result.isSuccess() Value"+result.isSuccess());
        if (result.isSuccess()) {

            userSignupLogin(result );


        } else {

            Toast.makeText(this, " Google-Sign In Failed ", Toast.LENGTH_LONG).show();
            //Log.d(TAG, " In Else Block");
            // Signed out, show unauthenticated UI.
            // updateUI(false);
        }
    }
    private void googleSignOut() {

        // StartPageActivity.this.findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {

                    }
                });
        Log.d(TAG, " Successful Logout");

        finish();
    }

    private void saveProfile(){


        if (Utils.isNetworkAvailable(this)){
            String url = Constants.GOOGLE_SIGNUP;
            JSONObject data = null;
            try {
                data = new JSONObject();
                data.put("name", userSignup.getName());
                data.put("phone_number",_mobileNumber );
                data.put("email",userSignup.getEmail());
                data.put("UID",userSignup.getPassword());
                data.put("role",userSignup.getRole());


            } catch (JSONException e){
                Logger.e(TAG, e.getMessage());
            }
            new SaveProfile().execute(url, data);

        } else {
            Toast.makeText(this, "No Network Connection", Toast.LENGTH_LONG).show();
        }
    }

    private class SaveProfile extends AsyncTask<Object, Void, Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Object... arg0) {


            String url = (String) arg0[0];
            JSONObject data = (JSONObject) arg0[1];
            HTTPServiceHandler serviceHandler = new HTTPServiceHandler(GoogleSigninActivity.this);
            String response = serviceHandler.makeServiceCall(url, HTTPServiceHandler.HTTPMethod.POST, null, data, true);

            // Log.d(TAG, "The response is : +"+response);

            if (response != null && !response.isEmpty()) {


//                Utils.showToastOnUiThread(GoogleSigninActivity.this, " Google Sign in Succesful");


            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }



    public  void userSignupLogin(final GoogleSignInResult result){



         _mobileNumber = ((EditText)GoogleSigninActivity.this.findViewById(R.id.input_mobile)).getText().toString().trim();

        Log.d(TAG, " _mobileNumber : "+_mobileNumber);

        // Signed in successfully, show authenticated UI.
        GoogleSignInAccount acct = result.getSignInAccount();


        String _username = acct.getEmail().toString();
        String password = acct.getId().toString();


       // Log.d(TAG, " Name : "+acct.getDisplayName().toString());
        //Log.d(TAG, " Email : "+acct.getEmail().toString());
        //Log.d(TAG, " ID : "+acct.getId().toString());
        // Log.d(TAG, " IDTOKEN : "+ acct.getIdToken().toString());
        //Log.d(TAG, " Server Authenticate code: "+acct.getServerAuthCode().toString());


        // show dialog to enter user Mobile Number

        String name = acct.getDisplayName().toString();
        String email = _username;



        userSignup =   new UserSignup(name,role,email,password);


        if (!validate()) {
            onSignupFailed();
            return;
        }

        saveProfile();

        List<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("name", name));
        nameValuePairs.add(new BasicNameValuePair("email", _username));
        nameValuePairs.add(new BasicNameValuePair("phone_number", _mobileNumber));

        nameValuePairs.add(new BasicNameValuePair("UID", password));
        nameValuePairs.add(new BasicNameValuePair("role", role));




        new GetServerAuthTokenAsync(GoogleSigninActivity.this, Constants.GOOGLE_LOGIN, nameValuePairs, _username).execute();

        Log.d(TAG, " Successful Login as : "+acct.getDisplayName().toString());

    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "SignUp Failed", Toast.LENGTH_LONG).show();
        //_signupButton.setEnabled(true);
    }



    public boolean validate() {
        boolean valid = true;


        //String mobile = userSignup.getMobile();

        EditText _mobileText = (EditText)GoogleSigninActivity.this.findViewById(R.id.input_mobile);

        String mobile = _mobileText.getText().toString().trim();

        if (mobile.isEmpty() || mobile.length()!=10) {
            _mobileText.setError("Enter Valid Mobile Number");
            valid = false;
        } else {
            _mobileText.setError(null);
        }

        return valid;
    }







    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}