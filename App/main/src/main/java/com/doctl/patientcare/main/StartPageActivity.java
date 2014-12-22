package com.doctl.patientcare.main;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.doctl.patientcare.main.utility.GetServerAuthTokenAsync;
import com.doctl.patientcare.main.utility.Utils;

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

    private void setupServerAuthToken() {
        Context context = StartPageActivity.this;
        String ServerAccessToken = Utils.getAuthTokenFromSharedPreference(context);
        if(!ServerAccessToken.isEmpty()) {
            Intent intent = new Intent(context, MainActivity.class);
            context.startActivity(intent);
            this.finish();
        }
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
                    //Validate username and password here before proceeding

                    getActivity().findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
                    new GetServerAuthTokenAsync(getActivity()).execute(username, password);
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
            return inflater.inflate(R.layout.fragment_register, container, false);
        }
    }
}
