package com.doctl.patientcare.main.utility;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.doctl.patientcare.main.StartPageActivity;
import com.doctl.patientcare.main.activities.ThreadDetailActivity;
import com.doctl.patientcare.main.services.HTTPServiceHandler;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by Satya Madala on 16/3/16.
 * email : satya.madala@olivo.in
 */
public class AsyncTaskUtils {

    private static final String TAG = AsyncTaskUtils.class.getSimpleName();

    public static class CreateThread extends AsyncTask<Void, Void, Void> {


        private String currUserId;
        private String displayName;
        private String profilePicURL;
        private String otherUserId;
        private Context context;

        public CreateThread(Context context, String currUserId, String displayName, String profilePicURL, String otherUserId) {
            this.currUserId = currUserId;
            this.displayName = displayName;
            this.profilePicURL = profilePicURL;
            this.otherUserId = otherUserId;
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            String url = Constants.QUESTION_URL;
            JSONObject data = new JSONObject();

            try {
                data.put("user_ids", currUserId + "," + otherUserId);
                HTTPServiceHandler serviceHandler = new HTTPServiceHandler(context);
                String response = serviceHandler.makeServiceCall(url, HTTPServiceHandler.HTTPMethod.POST, null, data);
                JSONObject jsonObject = new JSONObject(response);
                Logger.d(TAG, " Resopnse " + response);
                if (response == null) {
                    throw new Exception("No response from server");
                }
                String threadId = jsonObject.getInt("id") + "";
                Intent intent = ThreadDetailActivity.createThreadDetailIntent(context, threadId, otherUserId, displayName, profilePicURL);
                context.startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(context, "some error occurred", Toast.LENGTH_SHORT).show();
                Logger.e(TAG, e.getMessage());
            }
            return null;
        }

    }

    public static class DeleteGCMRegistrationId extends AsyncTask<Void, Void, Void> {
        private Context context;

        public DeleteGCMRegistrationId(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... params) {
            String registrationId = Utils.getFromSharedPreference(context, Constants.APP_DATA_FILE, Constants.PROPERTY_GCM_REGISTRATION_ID);
            String username = Utils.getUserNameFromSharedPreference(context);
            HTTPServiceHandler httpServiceHandler = new HTTPServiceHandler(context);
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("username", username);
                jsonObject.put("gcm_reg_id", registrationId);
            } catch (JSONException e) {
                e.printStackTrace();
                Logger.d(TAG, "failed to create json object");
            }
            httpServiceHandler.makeServiceCall(Constants.GCM_REGISTER_URL, HTTPServiceHandler.HTTPMethod.DELETE, null, jsonObject);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Utils.cleanupSharedPreference(context);
            Intent intent = new Intent(context, StartPageActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        }
    }
}
