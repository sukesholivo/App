package com.doctl.patientcare.main.utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;

import com.doctl.patientcare.main.MainActivity;
import com.doctl.patientcare.main.R;
import com.doctl.patientcare.main.om.UserProfile;
import com.doctl.patientcare.main.services.HTTPServiceHandler;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.List;

/**
 * Created by Komal on 9/17/2014.
 */
public class GetServerAuthTokenAsync extends AsyncTask<Void, String, String> {

    private Context c;
    private String err = "";
    private String url;
    private List<NameValuePair> nameValuePairs;
    public GetServerAuthTokenAsync(Context c) {
        this.c = c;
    }

    public GetServerAuthTokenAsync(Context c, String url, List<NameValuePair> nameValuePairs) {
        this.c = c;
        this.url = url;
        this.nameValuePairs = nameValuePairs;
    }

    @Override
    protected String doInBackground(Void... params) {
        String result = null;
        //Extract the auth token from user preferences
        String ServerAccessToken = Utils.getAuthTokenFromSharedPreference(c);

        if(ServerAccessToken.isEmpty()) {
            try {
                JSONObject jsonResponse = networkCall(url, nameValuePairs);
                if (jsonResponse != null) {
                    if(jsonResponse.has("message")) {
                        err = jsonResponse.getString("message");
                    } else if(jsonResponse.has("token")) {
                        String serverAccessToken = jsonResponse.getString("token");
                        Utils.setAuthTokenFromSharedPreference(c, serverAccessToken);
                        result = serverAccessToken;
                        getPersonalDetail(c);
                    } else {
                        throw new Exception("Error:Invalid response");
                    }
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
        Utils.saveUserDataToSharedPreference(c, userProfile);
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
            String title = err;
            alert.setTitle(title);
            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    dialog.cancel();
                }
            });
            alert.show();
        }
    }

    private JSONObject networkCall(String url, List<NameValuePair> nameValuePairs){
        try {
            err = "Error: device reg id expected in params";

            HttpParams httpParams = new BasicHttpParams();
            httpParams.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
            int timeoutConnection = 4000;
            HttpConnectionParams.setConnectionTimeout(httpParams, timeoutConnection);
            int timeoutSocket = 6000;
            HttpConnectionParams.setSoTimeout(httpParams, timeoutSocket);

            HttpClient client = new DefaultHttpClient(httpParams);
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
            String responseString;
            if (response != null) {
                HttpEntity httpEntity = response.getEntity();
                responseString = EntityUtils.toString(httpEntity);
                JSONTokener tokener = new JSONTokener(responseString);
                return new JSONObject(tokener);
            }
        } catch (Exception e) {
            e.printStackTrace();
            err = "Error: "+e.toString();
        }
        return null;
    }
}
