package com.doctl.patientcare.main;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.doctl.patientcare.main.utility.Constants;
import com.doctl.patientcare.main.utility.Logger;
import com.doctl.patientcare.main.utility.Utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
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
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mailtovishal.r on 6/23/2014.
 */

public class WriteGCMRegistrationId extends AsyncTask<String, String, String> {
    private Context c;
    private static final String TAG = WriteGCMRegistrationId.class.getSimpleName();

    public WriteGCMRegistrationId(Context c) {
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
                Logger.e(TAG, " reg id "+ registration + " username "+ username);
                HttpParams httpParams = new BasicHttpParams();
                httpParams.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
                int timeoutConnection = 4000;
                HttpConnectionParams.setConnectionTimeout(httpParams, timeoutConnection);
                int timeoutSocket = 6000;
                HttpConnectionParams.setSoTimeout(httpParams, timeoutSocket);

                List<NameValuePair> nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("gcm_reg_id", registration));
                nameValuePairs.add(new BasicNameValuePair("username", username));

                HttpClient client = new DefaultHttpClient(httpParams);
                String url = Constants.GCM_REGISTER_URL;
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
                    result = "NetError: Please report this error to Olivo team";
                }
                finally {
                    if(response != null) {
                        HttpEntity httpEntity = response.getEntity();
                        result = EntityUtils.toString(httpEntity);
                        Logger.d("DEBUG", result);

                        if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                            //Save the registration id
                            SharedPreferences sp = c.getSharedPreferences(Constants.GCM_SHARED_PREFERENCE_KEY, Activity.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString(Constants.PROPERTY_GCM_REGISTRATION_ID, registration);
                            editor.putInt(Constants.PROPERTY_APP_VERSION, Utils.getAppVersion(c));
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

    }
}
