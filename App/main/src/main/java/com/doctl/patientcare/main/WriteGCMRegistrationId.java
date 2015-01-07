package com.doctl.patientcare.main;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.doctl.patientcare.main.utility.Constants;
import com.doctl.patientcare.main.utility.Utils;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by mailtovishal.r on 6/23/2014.
 */

public class WriteGCMRegistrationId extends AsyncTask<String, String, String> {
    private Context c;

    public WriteGCMRegistrationId(Context c)
    {
        this.c = c;
    }

    @Override
    protected String doInBackground(String... params) {
        String result = null;
        try {
            result = "Error: device reg id expected in params";
            if(params.length > 0) {
                String registration = params[0];
                String username = params[1];
                HttpParams httpParams = new BasicHttpParams();
                httpParams.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
                int timeoutConnection = 4000;
                HttpConnectionParams.setConnectionTimeout(httpParams, timeoutConnection);
                int timeoutSocket = 6000;
                HttpConnectionParams.setSoTimeout(httpParams, timeoutSocket);

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("gcm_reg_id", registration));
                nameValuePairs.add(new BasicNameValuePair("username", username));

                HttpClient client = new DefaultHttpClient(httpParams);
                String url = Constants.SERVER_URL+"/gcm/register/";
                HttpPost request = new HttpPost(url);
                request.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = null;

                //Extract the auth token from user preferences
                String ServerAccessToken = Utils.getAuthTokenFromSharedPreference(c);
                request.setHeader("Authorization", "Token "+ServerAccessToken);

                try {
                    response = client.execute(request);
                } catch (ConnectTimeoutException e) {
                    e.printStackTrace();
                    result = "NetError: ConnectionTimedOut";
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                    result = "NetError: Some error, please try again later";
                } catch (IOException e) {
                    e.printStackTrace();
                    result = "NetError: Please report this error to DocTL team";
                }
                finally {
                    if(response != null)
                    {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "iso-8859-1"), 8);
                        StringBuilder sb = new StringBuilder();
                        sb.append(reader.readLine() + "\n");
                        String line;
                        while ((line = reader.readLine()) != null) {
                            sb.append(line).append("\n");
                        }

                        result = sb.toString();
                        Log.d("DEBUG", result);

                        if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                            //Save the registration id
                            SharedPreferences sp = c.getSharedPreferences(Constants.GCM_SHARED_PREFERERENCE_KEY, Activity.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("gcm_registration_id", registration);
                            editor.commit();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            // do something
        } else {
            // error occured
        }
    }
}
