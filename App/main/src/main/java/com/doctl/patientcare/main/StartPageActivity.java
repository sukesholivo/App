package com.doctl.patientcare.main;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.doctl.patientcare.main.om.TreatmentInfo;
import com.doctl.patientcare.main.services.HTTPServiceHandler;
import com.doctl.patientcare.main.utility.Constants;
import com.doctl.patientcare.main.utility.GetServerAuthTokenAsync;
import com.doctl.patientcare.main.utility.Logger;
import com.doctl.patientcare.main.utility.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Leak on 12/11/2014.
 */
public class StartPageActivity extends FragmentActivity {
    ViewPager mViewPager;
    AppSectionsPagerAdapter mAppSectionsPagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        ActionBar actionBar = getActionBar();
        if (actionBar != null){
            actionBar.hide();
        }
        setContentView(R.layout.activity_startup);

        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mAppSectionsPagerAdapter);
        setupServerAuthToken();
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

    public static class AppSectionsPagerAdapter extends FragmentPagerAdapter {

        public AppSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    return new LoginSectionFragment();

                case 1:
                    return new RegisterSectionFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "LOGIN";
                case 1:
                    return "REGISTER";
            }
            return null;
        }
    }

    public static class LoginSectionFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_login, container, false);
            setLoginListener(rootView);
            return rootView;
        }

        private void setLoginListener(final View rootView){
            View.OnClickListener clickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Login into the app and populate the shared preferences
                    String username = ((EditText)rootView.findViewById(R.id.loginUsername)).getText().toString().trim();
                    String password = ((EditText)rootView.findViewById(R.id.loginPassword)).getText().toString();
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                    nameValuePairs.add(new BasicNameValuePair("username", username));
                    nameValuePairs.add(new BasicNameValuePair("password", password));
                    //Validate username and password here before proceeding

                    getActivity().findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
                    new GetServerAuthTokenAsync(getActivity(), Constants.LOGIN_URL, nameValuePairs).execute();
                }
            };
            Button loginButton = (Button)rootView.findViewById(R.id.loginButton);
            loginButton.setOnClickListener(clickListener);
        }
    }

    public static class RegisterSectionFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_register, container, false);
            TextView textView =(TextView)rootView.findViewById(R.id.tosLink);
            textView.setClickable(true);
            textView.setMovementMethod(LinkMovementMethod.getInstance());
            String text = "By signing up, I agree to the <a href='http://www.doctl.com/tos'> Terms of Use </a>";
            textView.setText(Html.fromHtml(text));
            setRegisterListener(rootView);
            return  rootView;
        }

        private void setRegisterListener(final View rootView){
            View.OnClickListener clickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Login into the app and populate the shared preferences
                    String username = ((EditText)rootView.findViewById(R.id.registerUsername)).getText().toString().trim();
                    String code = ((EditText)rootView.findViewById(R.id.registerCode)).getText().toString().trim();
                    String password1 = ((EditText)rootView.findViewById(R.id.registerPassword1)).getText().toString().trim();
                    String password2 = ((EditText)rootView.findViewById(R.id.registerPassword2)).getText().toString();
                    if (!password1.equals(password2)){
                        return;
                    }
                    //Validate username and password here before proceeding
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                    nameValuePairs.add(new BasicNameValuePair("username", username));
                    nameValuePairs.add(new BasicNameValuePair("password1", password1));
                    nameValuePairs.add(new BasicNameValuePair("password2", password2));
                    nameValuePairs.add(new BasicNameValuePair("code", code));

                    getActivity().findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
                    new GetServerAuthTokenAsync(getActivity(), Constants.REGISTER_URL, nameValuePairs).execute();
                }
            };
            Button registerButton = (Button)rootView.findViewById(R.id.registerButton);
            registerButton.setOnClickListener(clickListener);
        }
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
            } else {
                StartPageActivity.this.findViewById(R.id.loginPanel).setVisibility(View.VISIBLE);
            }
        }
    }
}
