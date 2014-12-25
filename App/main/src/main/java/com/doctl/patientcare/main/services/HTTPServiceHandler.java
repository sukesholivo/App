package com.doctl.patientcare.main.services;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

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
import org.json.JSONObject;

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
    private Context c;
    public HTTPServiceHandler(Context c) {
        this.c = c;
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
            String ServerAccessToken = Utils.getAuthTokenFromSharedPreference(c);
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
            httpEntity = httpResponse.getEntity();
            response = EntityUtils.toString(httpEntity);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, response);
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
