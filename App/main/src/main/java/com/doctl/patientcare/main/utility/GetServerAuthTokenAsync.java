package com.doctl.patientcare.main.utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.view.View;

import com.doctl.patientcare.main.MainActivity;
import com.doctl.patientcare.main.R;
import com.doctl.patientcare.main.om.UserProfile;
import com.doctl.patientcare.main.services.HTTPServiceHandler;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Komal on 9/17/2014.
 */
public class GetServerAuthTokenAsync extends AsyncTask<String, String, String> {

    private Context c;
    private String err = "";
    private int status = 0;
    public GetServerAuthTokenAsync(Context c) {
        this.c = c;
    }

    @Override
    protected String doInBackground(String... params) {
        String result = null;
        //Extract the auth token from user preferences
        String ServerAccessToken = Utils.getAuthTokenFromSharedPreference(c);

        if(ServerAccessToken.isEmpty()) {
            try {
                err = "Error: device reg id expected in params";
                if (params.length > 1)
                {
                    HttpParams httpParams = new BasicHttpParams();
                    httpParams.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
                    int timeoutConnection = 4000;
                    HttpConnectionParams.setConnectionTimeout(httpParams, timeoutConnection);
                    int timeoutSocket = 6000;
                    HttpConnectionParams.setSoTimeout(httpParams, timeoutSocket);

                    String username = params[0];
                    String password = params[1];
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                    nameValuePairs.add(new BasicNameValuePair("username", username));
                    nameValuePairs.add(new BasicNameValuePair("password", password));

                    HttpClient client = new DefaultHttpClient(httpParams);
                    String url = Constants.SERVER_URL + "/api-token-auth/";
                    HttpPost request = new HttpPost(url);
                    request.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = null;

                    try {
                        response = client.execute(request);
                    } catch (ConnectTimeoutException e) {
                        e.printStackTrace();
                        err = "NetError: ConnectionTimedOut";
                    } catch (ClientProtocolException e) {
                        e.printStackTrace();
                        err = "NetError: Some error, please try again later";
                    } catch (IOException e) {
                        e.printStackTrace();
                        err = "NetError: Please report this error to DocTL team";
                    }

                    if (response != null) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "iso-8859-1"), 8);
                        StringBuilder sb = new StringBuilder();
                        sb.append(reader.readLine()).append("\n");
                        String line;
                        while ((line = reader.readLine()) != null) {
                            sb.append(line).append("\n");
                        }

                        String temp = sb.toString();
                        JSONTokener tokener = new JSONTokener(temp);
                        JSONObject jsonResponse = new JSONObject(tokener);

                        if(jsonResponse.has("non_field_errors"))
                        {
                            err = "Error: Unable to login with provided credentials.";
                            status = 1;
                        }
                        else if(jsonResponse.has("token"))
                        {
                            String serverAccessToken = jsonResponse.getString("token");
                            Utils.setAuthTokenFromSharedPreference(c, serverAccessToken);
                            result = serverAccessToken;
                        }
                        else
                        {
                            throw new Exception("Error:Invalid response");
                        }
                    }
                    getPersonalDetail(c);

                }
            } catch (Exception e) {
                e.printStackTrace();
                err = "Error: "+e.toString();
            }
        }

        return result;
    }

    private void getPersonalDetail(Context c){
        String response = new HTTPServiceHandler(c).makeServiceCall(Constants.PERSONAL_DETAIL_URL, HTTPServiceHandler.HTTPMethod.GET, null, null);
        JsonParser parser = new JsonParser();
        JsonObject personalDetail = parser.parse(response).getAsJsonObject();
        UserProfile userProfile = new Gson().fromJson(personalDetail, UserProfile.class);

        SharedPreferences sp = c.getSharedPreferences(Constants.PERSONAL_DETAIL_SHARED_PREFERENCE_NAME, Activity.MODE_PRIVATE);

        SharedPreferences.Editor editor = sp.edit();
        editor.putString("id", userProfile.getId());
        editor.putString("displayName", userProfile.getDisplayName());
        editor.putString("email", userProfile.getEmail());
        editor.putString("dob", userProfile.getDob());
        editor.putString("phone", userProfile.getPhone());
        editor.putString("sex", userProfile.getSex());
        editor.putString("profilePicUrl", userProfile.getProfilePicUrl());
        editor.commit();
    }

    @Override
    protected void onPostExecute(String result) {
        //Unset the loader
        Activity activity = (Activity)c;
        activity.findViewById(R.id.loadingPanel).setVisibility(View.GONE);

        //Check for results
        if (result != null) {
            Intent intent = new Intent(c, MainActivity.class);
            c.startActivity(intent);
            activity.finish();
        } else {
            //show modal for error
            AlertDialog.Builder alert = new AlertDialog.Builder(c);
            String title = "Error occurred: "+err+". Try again after sometime. For repeated error, please write to contact@DOCTL.com.";
            if(status == 1)
            {
                title = "Invalid Credentials Provided. Please try again with correct credentials.";
            }
            alert.setTitle(title);
            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    dialog.cancel();
                }
            });
            alert.show();
        }
    }
}
