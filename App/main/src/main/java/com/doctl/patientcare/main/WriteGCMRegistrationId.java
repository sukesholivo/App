package com.doctl.patientcare.main;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by mailtovishal.r on 6/23/2014.
 */

public class WriteGCMRegistrationId extends AsyncTask<String, String, String> {
    @Override
    protected String doInBackground(String... params) {
        String result = null;
        try {
            result = "Error: device reg id expected in params";
            if(params.length > 0) {
                String registration = params[0];
                HttpParams httpParams = new BasicHttpParams();
                httpParams.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
                int timeoutConnection = 4000;
                HttpConnectionParams.setConnectionTimeout(httpParams, timeoutConnection);
                int timeoutSocket = 6000;
                HttpConnectionParams.setSoTimeout(httpParams, timeoutSocket);

                HttpClient client = new DefaultHttpClient(httpParams);
                String url = "http://doctl.com/gcm/register/" + registration+"/patientcare";
                HttpGet request = new HttpGet(url);
                HttpResponse response = null;

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

                if(response != null)
                {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "iso-8859-1"), 8);
                    StringBuilder sb = new StringBuilder();
                    sb.append(reader.readLine() + "\n");
                    String line = "0";
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }

                    result = sb.toString();
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
