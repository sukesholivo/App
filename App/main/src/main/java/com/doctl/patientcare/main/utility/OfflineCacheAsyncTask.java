package com.doctl.patientcare.main.utility;

import android.content.Context;
import android.os.AsyncTask;

import com.doctl.patientcare.main.services.HTTPServiceHandler;

import org.apache.http.NameValuePair;

import java.util.List;

/**
 * Created by Satya Madala on 8/3/16.
 * email : satya.madala@olivo.in
 *
 * OfflineCacheAsyncTask can use for GET AsyncTasks to provide smooth UI
 * it updates UI with previous cached response for GET URL
 * @param <T1>
 * @param <T2>
 */
public abstract class OfflineCacheAsyncTask<T1,T2> extends AsyncTask<T1, T2, String> {

    private static final String TAG = OfflineCacheAsyncTask.class.getSimpleName();
    protected String url; //url for getting data from sqllite from response table in offline.db
    protected Context context;
    protected List<NameValuePair> getParams;
    protected boolean saveToLocal;

    public OfflineCacheAsyncTask(Context context, String url, List<NameValuePair> getParams, boolean saveToLocal) {
        this.url = url;
        this.context = context;
        this.getParams = getParams;
        this.saveToLocal = saveToLocal;
    }

    @Override
    protected String doInBackground(T1... params) {
        if(saveToLocal) {
            publishProgress(null);
        }
        HTTPServiceHandler httpServiceHandler = new HTTPServiceHandler(context);
        return httpServiceHandler.makeServiceCall(url, HTTPServiceHandler.HTTPMethod.GET, getParams, null);
    }

    @Override
    protected void onProgressUpdate(T2... values) {
        super.onProgressUpdate(values);
        if(saveToLocal && context != null && url != null ){ // check only when save to local is true

            OfflineCacheUtil.ResponseDetails responseDetails = OfflineCacheUtil.getResponse(context, url);
            if(responseDetails != null && responseDetails.getResponse() != null){
                onResponseReceived(responseDetails.getResponse());
            }

        }else{
            Logger.e(TAG, String.format("Can't update from offline db due to url or context is null url = %s, content = %s ", url, context));
        }

    }

    protected abstract void onResponseReceived(String response);
    @Override
    protected void onPostExecute(String response) {
        if(response == null ) return;
        if( context != null && url != null){
            if(saveToLocal) {
                OfflineCacheUtil.saveResponse(context, url, response);
            }
        }else{
            Logger.e(TAG, String.format("Can't update from offline db due to url or context is null url = %s, content = %s, response = %s ", url, context, response));
        }
        onResponseReceived(response);
    }
}
