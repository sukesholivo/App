package in.olivo.patientcare.main.utility;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;

import org.apache.http.NameValuePair;

import java.util.List;

import in.olivo.patientcare.main.R;
import in.olivo.patientcare.main.services.HTTPServiceHandler;

/**
 * Created by Satya Madala on 8/3/16.
 * email : satya.madala@olivo.in
 * <p/>
 * OfflineCacheAsyncTask can use for GET AsyncTasks to provide smooth UI
 * it updates UI with previous cached response for GET URL
 *
 */
public abstract class OfflineCacheAsyncTask extends AsyncTask<Void, Void, String> {

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

    private void changeStateForLoadingPanel(int state){
        if(context instanceof Activity){
            Activity activity = (Activity) context;
            View loadingView = activity.findViewById(R.id.loadingPanel);
            if(loadingView != null){
                loadingView.setVisibility(state);
            }
        }
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        changeStateForLoadingPanel(View.VISIBLE);
    }

    @Override
    protected String doInBackground(Void... params) {
        if (saveToLocal) {
            publishProgress(null);
        }
        HTTPServiceHandler httpServiceHandler = new HTTPServiceHandler(context);
        return httpServiceHandler.makeServiceCall(url, HTTPServiceHandler.HTTPMethod.GET, getParams, null);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
        changeStateForLoadingPanel(View.GONE);
        if (context != null && url != null) { // check only when save to local is true

            OfflineCacheUtil.ResponseDetails responseDetails = OfflineCacheUtil.getResponse(context, url);
            if (responseDetails != null && responseDetails.getResponse() != null) {
                onResponseReceived(responseDetails.getResponse());
            }
        } else {
            Logger.e(TAG, String.format("Can't update from offline db due to url or context is null url = %s, content = %s ", url, context));
        }

    }

    protected abstract void onResponseReceived(String response);

    @Override
    protected void onPostExecute(String response) {

        if (response!= null && context != null && url != null) {
            if (saveToLocal) {
                OfflineCacheUtil.saveResponse(context, url, response);
            }
        } else {
            Logger.e(TAG, String.format("Can't update from offline db due to url or context is null url = %s, content = %s, response = %s ", url, context, response));
        }
        changeStateForLoadingPanel(View.GONE);
        onResponseReceived(response);
    }
}
