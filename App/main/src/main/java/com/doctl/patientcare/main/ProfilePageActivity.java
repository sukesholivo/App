package com.doctl.patientcare.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.doctl.patientcare.main.utility.GetServerAuthTokenAsync;

/**
 * Created by Leak on 12/11/2014.
 */
public class ProfilePageActivity extends Activity  {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilepage);
        setupServerAuthToken();
        PlantMandatoryLabelMarker();
        SetLoginListener();
    }

    private void setupServerAuthToken() {
        Context context = ProfilePageActivity.this;
        SharedPreferences sp = context.getSharedPreferences("auth_prefs", Activity.MODE_PRIVATE);
        String ServerAccessToken = sp.getString("serveraccesstoken", "");
        if(!ServerAccessToken.isEmpty()) {
            Intent intent = new Intent(context, MainActivity.class);
            context.startActivity(intent);
        }

        return;
    }

    private void SetLoginListener(){
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Login into the app and populate the shared preferences
                String username = ((EditText)findViewById(R.id.etUsername)).getText().toString().trim();
                String password = ((EditText)findViewById(R.id.etPassword)).getText().toString();
                //Validate username and password here before proceeding

                findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
                new GetServerAuthTokenAsync(ProfilePageActivity.this).execute(username, password);
            }
        };
        Button loginButton = (Button)findViewById(R.id.LoginButton);
        loginButton.setOnClickListener(clickListener);
    }

    private void PlantMandatoryLabelMarker(){
        String tag = "Mandatory_Field";
        ViewGroup root = (ViewGroup)findViewById(R.id.activityRoot);
        final int childCount = root.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = root.getChildAt(i);
            final Object tagObj = child.getTag();
            if (tagObj != null && tagObj.equals(tag) && child instanceof TextView) {
                TextView tv = (TextView) child;
                String label = (String) tv.getText();
                String colored = "*";
                SpannableStringBuilder builder = new SpannableStringBuilder();

                builder.append(label);
                int start = builder.length();
                builder.append(colored);
                int end = builder.length();

                builder.setSpan(new ForegroundColorSpan(Color.RED), start, end,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                tv.setText(builder);
            }
        }
    }
}
