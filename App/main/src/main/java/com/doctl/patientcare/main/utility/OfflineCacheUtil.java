package com.doctl.patientcare.main.utility;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.doctl.patientcare.main.data.ResponseContract;
import com.doctl.patientcare.main.om.UserProfile;

import java.util.Date;

/**
 * Created by Satya Madala on 5/3/16.
 * email : satya.madala@olivo.in
 */
public class OfflineCacheUtil {

    private final static String TAG = OfflineCacheUtil.class.getSimpleName();

    private final static String SHARED_PREFERENCE_FILE_NAME_PREFIX = "user";
    private final static String DATE_SUFFIX = "date";
    private final static long DEFAULT_TIME_VAL = -1;

    public static class ResponseDetails{
        private String response;
        private Date lastResponse;

        public ResponseDetails(String response, Long lastResponse) {
            this.response = response;
            if(lastResponse != DEFAULT_TIME_VAL) {
                this.lastResponse = new Date(lastResponse);
            }
        }

        public String getResponse() {
            return response;
        }

        public Date getLastResponse() {
            return lastResponse;
        }
    }
    public static ResponseDetails getResponse(Context context, String url){

        UserProfile userProfile = Utils.getPatientDataFromSharedPreference(context);

        if(userProfile == null){
            return null;
        }


       /* String fileName = SHARED_PREFERENCE_FILE_NAME_PREFIX + userProfile.getId();
        SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        return  new ResponseDetails(sp.getString(url, null), sp.getLong(url+DATE_SUFFIX, DEFAULT_TIME_VAL));*/
        String userId = userProfile.getId();

        Uri uri = ResponseContract.ResponseEntry.buildResponseURLWithUserId(url, userId);

        Log.d(TAG, "get response uri " + uri);
        Cursor cursor = context.getContentResolver().query(uri, new String[]{ResponseContract.ResponseEntry.COLUMN_RESPONSE,
                ResponseContract.ResponseEntry.COLUMN_LAST_UPDATED_TIME}, null, null, null);
        if(cursor != null && cursor.moveToFirst()){
            String response = cursor.getString(cursor.getColumnIndex(ResponseContract.ResponseEntry.COLUMN_RESPONSE));
            long date = cursor.getLong(cursor.getColumnIndex(ResponseContract.ResponseEntry.COLUMN_LAST_UPDATED_TIME));
            cursor.close();
            return new ResponseDetails(response, date);
        }
        return null;
    }

    public static boolean saveResponse(Context context, String url, String response){

        UserProfile userProfile = Utils.getPatientDataFromSharedPreference(context);
        if(userProfile == null){
            return false;
        }
       /* String fileName = SHARED_PREFERENCE_FILE_NAME_PREFIX + userProfile.getId();
        SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(url, response).putLong( url + DATE_SUFFIX, new Date().getTime()).apply();
        return true;*/

        String userId = userProfile.getId();
        Date currTime = new Date();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ResponseContract.ResponseEntry.COLUMN_USER_ID, userId);
        contentValues.put(ResponseContract.ResponseEntry.COLUMN_URL, url);
        contentValues.put(ResponseContract.ResponseEntry.COLUMN_RESPONSE, response);
        contentValues.put(ResponseContract.ResponseEntry.COLUMN_LAST_UPDATED_TIME, currTime.getTime());

        Uri insertedUri = context.getContentResolver().insert(ResponseContract.ResponseEntry.CONTENT_URI, contentValues); // updates if url and user_id already exists
        long id =ContentUris.parseId(insertedUri);
        return true;
    }

}
