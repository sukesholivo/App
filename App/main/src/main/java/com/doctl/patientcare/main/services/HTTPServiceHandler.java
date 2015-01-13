package com.doctl.patientcare.main.services;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.doctl.patientcare.main.utility.Utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.List;

/**
 * Created by Administrator on 7/11/2014.
 */
public class HTTPServiceHandler {
    private static final String TAG = HTTPServiceHandler.class.getSimpleName();
    static String response = null;
    private Context context;
    public HTTPServiceHandler(Context c) {
        this.context = c;
    }

    /**
     * Making service call
     * @url - url to make request
     * @method - http request method
     * */
    public String makeServiceCall(String url, HTTPMethod method) {
        return this.makeServiceCall(url, method, null, null);
    }

    /**
     * Making service call
     * @url - url to make request
     * @method - http request method
     * @params - http request params
     * */
    public String makeServiceCall(String url, HTTPMethod method, List<NameValuePair> getParams, JSONObject postParams) {
        try {
            // http client
            int TIMEOUT_MILLISEC = 100000; // = 10 seconds
            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT_MILLISEC);
            HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);

            HttpClient httpClient = new DefaultHttpClient(httpParams);
            HttpEntity httpEntity;
            HttpResponse httpResponse = null;

            //Extract the auth token from user preferences
            String ServerAccessToken = Utils.getAuthTokenFromSharedPreference(context);
            Log.d(TAG, url);

            // Checking http request method type
            if (method == HTTPMethod.POST) {
                Log.d(TAG, "Sending POST");
                HttpPost httpPost = new HttpPost(url);
                // adding post params
                if (postParams != null) {
                    Log.d(TAG, postParams.toString());
                    StringEntity se = new StringEntity(postParams.toString());
                    se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                    httpPost.setEntity(se);
                }
                httpPost.setHeader("Content-type","application/json");
                httpPost.setHeader("Authorization", "Token "+ServerAccessToken);
                httpResponse = httpClient.execute(httpPost);
            } else if (method == HTTPMethod.PATCH) {
                Log.d(TAG, "Sending PATCH");
                HttpPatch httpPatch = new HttpPatch(url);
                // adding post params
                if (postParams != null) {
                    Log.d(TAG, postParams.toString());
                    StringEntity se = new StringEntity(postParams.toString());
                    se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                    httpPatch.setEntity(se);
                }
                httpPatch.setHeader("Content-type","application/json");
                httpPatch.setHeader("Authorization", "Token " + ServerAccessToken);
                httpResponse = httpClient.execute(httpPatch);
            } else if (method == HTTPMethod.GET) {
                // appending params to url
                Log.d(TAG, "Sending GET");
                if (getParams != null) {
                    String paramString = URLEncodedUtils
                            .format(getParams, "utf-8");
                    url += "?" + paramString;
                }
                Log.d(TAG, "URL: " + url);
                HttpGet httpGet = new HttpGet(url);
                httpGet.setHeader("Authorization", "Token "+ServerAccessToken);

                httpResponse = httpClient.execute(httpGet);

            }
            assert httpResponse != null;
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            Log.e(TAG, "actual statusCode: " + statusCode);
            if (statusCode == 403 || statusCode == 401){
                Log.e(TAG, "statusCode: 403");
                Utils.handleUnauthorizedAccess(context);
                return null;
            } else if (statusCode == 400){
                Log.e(TAG, "statusCode: 400");
                final HttpEntity entity = httpResponse.getEntity();
                ((Activity) context).runOnUiThread(new Runnable() {
                    public void run() {
                        String message = "Some error occurred";
                        try {
                            String responseString = EntityUtils.toString(entity);
                            JSONTokener tokener = new JSONTokener(responseString);
                            JSONObject jsonObject =  new JSONObject(tokener);
                            if (jsonObject.has("message")) {
                                message = jsonObject.getString("message");
                            } else if (jsonObject.has("error")) {
                                message = jsonObject.getString("error");
                            }
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                        }
                    }
                });
                return null;
            } else if (statusCode == 500){
                Log.e(TAG, "statusCode: 500");
                final HttpEntity entity = httpResponse.getEntity();
                ((Activity) context).runOnUiThread(new Runnable() {
                    public void run() {
                        String message = "Server error occurred";
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    }
                });
                return null;
            }
            httpEntity = httpResponse.getEntity();
            response = EntityUtils.toString(httpEntity);
        } catch (IOException  e) {
            e.printStackTrace();
        }
        Log.e(TAG, response);
        return response;
    }

    public enum HTTPMethod{
        GET,
        POST,
        PATCH
    }

    public class HttpPatch extends HttpPost {
        public static final String METHOD_PATCH = "PATCH";

        public HttpPatch() {
            super();
        }

        public HttpPatch(final String url) {
            super(url);
            setURI(URI.create(url));
        }

        @Override
        public String getMethod() {
            return METHOD_PATCH;
        }
    }
}
