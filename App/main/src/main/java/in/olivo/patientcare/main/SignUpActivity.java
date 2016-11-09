package in.olivo.patientcare.main;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import in.olivo.patientcare.main.om.UserSignup;
import in.olivo.patientcare.main.services.HTTPServiceHandler;
import in.olivo.patientcare.main.utility.Constants;
import in.olivo.patientcare.main.utility.Logger;
import in.olivo.patientcare.main.utility.Utils;


public class SignUpActivity extends ActionBarActivity {
    private static final String TAG = "SignUpActivity";
    private static final String role = "PATIENT";
    UserSignup userSignup;
    TextView _loginLink;
    Button _signupButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        // ButterKnife.bind(this);


        _signupButton = (Button)findViewById(R.id.btn_signup);
        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
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



    public void signup(){
        Log.d(TAG, "Signup");
        setUserSignupData();

        if (!validate()) {
            onSignupFailed();
            return;
        }
        // write logic for storing of Data
        saveProfile();
    }


    public void setUserSignupData( ){

    String extra ="";
        String name = ((EditText)SignUpActivity.this.findViewById(R.id.input_name)).getText().toString().trim();

        String email = ((EditText)SignUpActivity.this.findViewById(R.id.input_email)).getText().toString().trim();
        String mobile = ((EditText)SignUpActivity.this.findViewById(R.id.input_mobile)).getText().toString().trim();


        userSignup =   new UserSignup(name,mobile,role,email,extra);

    }
    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "SignUp Failed", Toast.LENGTH_LONG).show();
        _signupButton.setEnabled(true);
    }



    public boolean validate() {
        boolean valid = true;



        String mobile = userSignup.getMobile();
        String email = userSignup.getEmail();

        String name = userSignup.getName();


        EditText _mobileText = (EditText)SignUpActivity.this.findViewById(R.id.input_mobile);
        EditText  _emailText = (EditText)SignUpActivity.this.findViewById(R.id.input_email);


        EditText _nameText = (EditText) SignUpActivity.this.findViewById(R.id.input_name);

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }


        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (mobile.isEmpty() || mobile.length()!=10) {
            _mobileText.setError("Enter Valid Mobile Number");
            valid = false;
        } else {
            _mobileText.setError(null);
        }

        return valid;
    }

    private void saveProfile(){


        if (Utils.isNetworkAvailable(this)){
            String url = Constants.SIGNUP_DETAIL_URL;
            JSONObject data = null;
            try {
                data = userSignup.getDataToPatch();


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


            String url = (String)arg0[0];
            JSONObject data= (JSONObject)arg0[1];
            HTTPServiceHandler serviceHandler = new HTTPServiceHandler(SignUpActivity.this);
            String response = serviceHandler.makeServiceCall(url, HTTPServiceHandler.HTTPMethod.POST, null, data,true);

            Log.d(TAG, "The response is : +"+response);

            if (response != null && !response.isEmpty()) {

                Utils.showToastOnUiThread(SignUpActivity.this, "Profile Saved");

                Intent _intent=new Intent(SignUpActivity.this ,StartPageActivity.class);

                String email = userSignup.getEmail();
                _intent.putExtra("Email","Thank You! Password will be sent to your Email: "+ email);

                startActivity(_intent);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

}