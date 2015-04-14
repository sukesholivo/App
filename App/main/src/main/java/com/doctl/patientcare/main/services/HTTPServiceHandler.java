package com.doctl.patientcare.main.services;

import android.content.Context;

import com.doctl.patientcare.main.utility.Logger;
import com.doctl.patientcare.main.utility.Utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ConnectTimeoutException;
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
import java.net.URI;
import java.net.UnknownHostException;
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

    public String makeServiceCall(String url, HTTPMethod method, List<NameValuePair> getParams, JSONObject postParams) {
        return this.makeServiceCall(url, method, getParams, postParams, false);
    }

    /**
     * Making service call
     * @url - url to make request
     * @method - http request method
     * @params - http request params
     * */
    public String makeServiceCall(String url, HTTPMethod method, List<NameValuePair> getParams, JSONObject postParams, boolean anonymous) {
        if (!Utils.isNetworkAvailable(context)){
            Utils.showToastOnUiThread(context, "No Network Connection");
            return null;
        }
        try {
            // http client
            int CONNECTION_TIMEOUT_MILLISEC = 7000; // = 7 seconds
            int SOCKET_TIMEOUT_MILLISEC = 10000; // = 10 seconds
            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, CONNECTION_TIMEOUT_MILLISEC);
            HttpConnectionParams.setSoTimeout(httpParams, SOCKET_TIMEOUT_MILLISEC);

            HttpClient httpClient = new DefaultHttpClient(httpParams);
            HttpEntity httpEntity;
            HttpResponse httpResponse = null;
            String ServerAccessToken = "";
            //Extract the auth token from user preferences
            if (!anonymous) {
                ServerAccessToken = Utils.getAuthTokenFromSharedPreference(context);
                if (ServerAccessToken == null || ServerAccessToken.isEmpty()) {
                    return null;
                }
            }
            Logger.d(TAG, url);

            // Checking http request method type
            if (method == HTTPMethod.POST) {
                Logger.d(TAG, "Sending POST");
                HttpPost httpPost = new HttpPost(url);
                // adding post params
                if (postParams != null) {
                    Logger.d(TAG, postParams.toString());
                    StringEntity se = new StringEntity(postParams.toString());
                    se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                    httpPost.setEntity(se);
                }
                httpPost.setHeader("Content-type","application/json");
                if (!anonymous) {
                    httpPost.setHeader("Authorization", "Token " + ServerAccessToken);
                }
                httpResponse = httpClient.execute(httpPost);
            } else if (method == HTTPMethod.PATCH) {
                Logger.d(TAG, "Sending PATCH");
                HttpPatch httpPatch = new HttpPatch(url);
                // adding post params
                if (postParams != null) {
                    Logger.d(TAG, postParams.toString());
                    StringEntity se = new StringEntity(postParams.toString());
                    se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                    httpPatch.setEntity(se);
                }
                httpPatch.setHeader("Content-type","application/json");
                if (!anonymous) {
                    httpPatch.setHeader("Authorization", "Token " + ServerAccessToken);
                }
                httpResponse = httpClient.execute(httpPatch);
            } else if (method == HTTPMethod.GET) {
                // appending params to url
                Logger.d(TAG, "Sending GET");
                if (getParams != null) {
                    String paramString = URLEncodedUtils
                            .format(getParams, "utf-8");
                    url += "?" + paramString;
                }
                Logger.d(TAG, "URL: " + url);
                HttpGet httpGet = new HttpGet(url);
                if (!anonymous) {
                    httpGet.setHeader("Authorization", "Token " + ServerAccessToken);
                }

                httpResponse = httpClient.execute(httpGet);

            }
            assert httpResponse != null;
            httpEntity = httpResponse.getEntity();
            response = EntityUtils.toString(httpEntity);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            Logger.d(TAG, "statusCode: " + statusCode);
            Logger.e(TAG, response);
            if (statusCode == 403 || statusCode == 401){
                Utils.handleUnauthorizedAccess(context);
                return null;
            } else if (statusCode == 400){
                final HttpEntity entity = httpResponse.getEntity();
//                ((Activity) context).runOnUiThread(new Runnable() {
//                    public void run() {
//                        String message = "Some error occurred";
//                        try {
//                            String responseString = EntityUtils.toString(entity);
//                            JSONTokener tokener = new JSONTokener(responseString);
//                            JSONObject jsonObject =  new JSONObject(tokener);
//                            if (jsonObject.has("message")) {
//                                message = jsonObject.getString("message");
//                            } else if (jsonObject.has("error")) {
//                                message = jsonObject.getString("error");
//                            }
//                            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
//                        } catch (IOException | JSONException e) {
//                            e.printStackTrace();
//                            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
//                        }
//                    }
//                });
                return null;
            } else if (statusCode == 500){
                Utils.showToastOnUiThread(context, "Some error occurred");
//                ((Activity) context).runOnUiThread(new Runnable() {
//                    public void run() {
//                        String message = "Server error occurred";
//                        try {
//                            String responseString = EntityUtils.toString(entity);
////                            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//
//                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
//                    }
//                });
                return null;
            }

        } catch (UnknownHostException ex){
            Utils.showToastOnUiThread(context, "No Network Connection");
            return null;
        } catch (ConnectTimeoutException e){
            Utils.showToastOnUiThread(context, "No Network Connection");
            return null;
        } catch (IOException  e) {
            Utils.showToastOnUiThread(context, "Some error occurred");
            return null;
        }
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
