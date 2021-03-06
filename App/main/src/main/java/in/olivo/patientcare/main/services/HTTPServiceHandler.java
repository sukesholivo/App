package in.olivo.patientcare.main.services;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

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
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.List;

import in.olivo.patientcare.main.utility.Logger;
import in.olivo.patientcare.main.utility.Utils;

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
     *
     * @url - url to make request
     * @method - http request method
     */
    public String makeServiceCall(String url, HTTPMethod method) {
        return this.makeServiceCall(url, method, null, null);
    }

    public String makeServiceCall(String url, HTTPMethod method, List<NameValuePair> getParams, JSONObject postParams) {
        return this.makeServiceCall(url, method, getParams, postParams, false);
    }

    /**
     * Making service call
     *
     * @url - url to make request
     * @method - http request method
     * @params - http request params
     */
    public String makeServiceCall(String url, HTTPMethod method, List<NameValuePair> getParams, JSONObject postParams, boolean anonymous) {
       /* if (!Utils.isNetworkAvailable(context)){
            Utils.showToastOnUiThread(context, "No Network Connection");
            return null;
        }*/
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
            String serverAccessToken = "";
            //Extract the auth token from user preferences
            if (!anonymous) {
                serverAccessToken = Utils.getAuthTokenFromSharedPreference(context);
                if (serverAccessToken == null || serverAccessToken.isEmpty()) {
                    return null;
                }
            }
            url = Utils.encodeURL(url);
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
                httpPost.setHeader("Content-type", "application/json");
                if (!anonymous) {
                    httpPost.setHeader("Authorization", "Token " + serverAccessToken);
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
                httpPatch.setHeader("Content-type", "application/json");
                if (!anonymous) {
                    httpPatch.setHeader("Authorization", "Token " + serverAccessToken);
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
                    httpGet.setHeader("Authorization", "Token " + serverAccessToken);
                }

                httpResponse = httpClient.execute(httpGet);

            } else if (method == HTTPMethod.DELETE) {
                HttpDelete httpDelete = new HttpDelete(url);
                // adding post params
                httpDelete.setHeader("Content-type", "application/json");
                if (!anonymous) {
                    httpDelete.setHeader("Authorization", "Token " + serverAccessToken);
                }
                httpResponse = httpClient.execute(httpDelete);
                return null;
            }
            assert httpResponse != null;
            httpEntity = httpResponse.getEntity();
            response = EntityUtils.toString(httpEntity);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            Logger.d(TAG, "statusCode: " + statusCode);
            Logger.e(TAG, response);
            if (statusCode == 403 || statusCode == 401) {
                Utils.handleUnauthorizedAccess(context);
                return null;
            } else if (statusCode == 400) {
                Log.d(TAG, response);
                ((Activity) context).runOnUiThread(new Runnable() {
                    public void run() {
                        String message = "Some error occurred";
                        try {

                            JSONTokener tokener = new JSONTokener(response);
                            JSONObject jsonObject =  new JSONObject(tokener);
                            if (jsonObject.has("message")) {
                                message = jsonObject.getString("message");
                            } else if (jsonObject.has("error")) {
                                message = jsonObject.getString("error");
                            }

                            //show modal for error
                            AlertDialog.Builder alert = new AlertDialog.Builder(context);
                            String title = message;


                            alert.setTitle(title);
                            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.cancel();
                                }
                            });
                            alert.show();



                            // Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }

                });
                return null;
            } else if (statusCode == 500) {
//                Utils.showToastOnUiThread(context, "Some error occurred");
                Log.e(TAG, response != null ? "Response " + response : " Responose is null with status code:" + statusCode);
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

        } catch (UnknownHostException ex) {
            Log.e(TAG, " UnknownHostException ", ex);
            Utils.showToastOnUiThread(context, "No Network Connection");
            return null;
        } catch (ConnectTimeoutException e) {
            Log.e(TAG, " ConnectionTimeout ", e);
            Utils.showToastOnUiThread(context, "No Network Connection");
            return null;
        } catch (IOException e) {
            Log.e(TAG, " IO Exception ", e);
            Utils.showToastOnUiThread(context, "Some error occurred");
            return null;
        }
        return response;
    }

    public enum HTTPMethod {
        GET,
        POST,
        PATCH,
        DELETE
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

    public class HttpDelete extends HttpPost {
        public static final String METHOD_DELETE = "DELETE";

        public HttpDelete() {
            super();
        }

        public HttpDelete(final String url) {
            super(url);
            setURI(URI.create(url));
        }

        @Override
        public String getMethod() {
            return METHOD_DELETE;
        }
    }
}