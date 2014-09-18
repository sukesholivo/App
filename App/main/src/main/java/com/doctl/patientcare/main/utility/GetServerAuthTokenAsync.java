package com.doctl.patientcare.main.utility;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import org.apache.http.HttpResponse;
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
import org.json.JSONArray;
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

    public GetServerAuthTokenAsync(Context c) {
        this.c = c;
    }

    @Override
    protected String doInBackground(String... params) {
        String result = null;

        //Extract the auth token from user preferences
        SharedPreferences sp = c.getSharedPreferences("auth_prefs", Activity.MODE_PRIVATE);
        String ServerAccessToken = sp.getString("serveraccesstoken", "");

        if(ServerAccessToken.isEmpty()) {
            try {
                result = "Error: device reg id expected in params";
                if (params.length > 0) {
                    String registration = params[0];
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
                    String url = "http://test.doctl.com/api-token-auth/";
                    HttpPost request = new HttpPost(url);
                    request.setEntity(new UrlEncodedFormEntity(nameValuePairs));
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

                    if (response != null) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "iso-8859-1"), 8);
                        StringBuilder sb = new StringBuilder();
                        sb.append(reader.readLine() + "\n");
                        String line;
                        while ((line = reader.readLine()) != null) {
                            sb.append(line).append("\n");
                        }

                        result = sb.toString();
                        JSONTokener tokener = new JSONTokener(result);
                        JSONObject jsonResponse = new JSONObject(tokener);

                        String ServerAcessToken = jsonResponse.getString("token");

                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("serveraccesstoken", ServerAcessToken);
                        editor.commit();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
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
