package com.doctl.patientcare.main;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Html;

import com.doctl.patientcare.main.om.BaseTask;
import com.doctl.patientcare.main.om.UserProfile;
import com.doctl.patientcare.main.om.education.EducationTask;
import com.doctl.patientcare.main.om.followup.FollowupTask;
import com.doctl.patientcare.main.om.message.MessageTask;
import com.doctl.patientcare.main.utility.Constants;
import com.doctl.patientcare.main.utility.Logger;
import com.doctl.patientcare.main.utility.Utils;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Created by mailtovishal.r on 6/21/2014.
 */
public class MyC2dmIntentService extends IntentService {
    private static final String TAG = MyC2dmIntentService.class.getSimpleName();
    public MyC2dmIntentService() {
        super("MyC2dmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                Logger.e(TAG, "Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                Logger.e(TAG, "Deleted messages on server: " + extras.toString());
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                Logger.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
                sendNotification(extras);
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        MyC2dmReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(Bundle msg) {
        String ServerAccessToken = Utils.getAuthTokenFromSharedPreference(this);
        if(ServerAccessToken.isEmpty()) {
            return;
        }
        UserProfile userProfile = Utils.getPatientDataFromSharedPreference(this);
        String type = msg.getString("type");
        String username = msg.getString("username");
        if (!username.equals(userProfile.getEmail())){
            return;
        }

        String data = msg.getString("data");
        if (type.toLowerCase().equals("card")) {
            BaseTask task = Utils.parseCardData(data);
            switch (task.getType()){
                case MEDICINE:
                case VITAL:
                    if (!MainApplication.isActivityVisible()) {
                        Intent popupIntent = new Intent(this, PopupNotificationActivity.class);
                        popupIntent.putExtra("card", data);
                        popupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        popupIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        popupIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        this.startActivity(popupIntent);
                    }
                    break;
                case SIMPLEREMINDER:
                case GENERICREMINDER:
                    String reminderTitle = ((MessageTask) task).getPayload().getTitle();
                    String reminderMessage = Html.fromHtml(((MessageTask) task).getPayload().getMessage()).toString();
                    Utils.showNotification(this, Constants.SIMPLEREMINDER_NOTIFICATION_ID, reminderTitle, reminderMessage);
                    break;
                case EDUCATION:
                    String educationTitle = getString(R.string.notification_education_title);
                    String educationMessage = ((EducationTask) task).getPayload().getTitle();
                    Utils.showNotification(this, Constants.EDUCATION_NOTIFICATION_ID, educationTitle, educationMessage);
                    break;
                case FOLLOWUP:
                    if (((FollowupTask) task).getPayload().getType().toLowerCase().equals("feedback")) {
                        String feedbackTitle = getString(R.string.notification_feedback_title);
                        String feedbackMessage = ((FollowupTask) task).getPayload().getTitle();
                        Utils.showNotification(this, Constants.FEEDBACK_NOTIFICATION_ID, feedbackTitle, feedbackMessage);
                    } else {
                        String followupTitle = getString(R.string.notification_followup_title);
                        String followupMessage = ((FollowupTask) task).getPayload().getTitle();
                        Utils.showNotification(this, Constants.FOLLOWUP_NOTIFICATION_ID, followupTitle, followupMessage);
                    }
                    break;
                default:
                    break;
            }
        } else if (type.toLowerCase().equals("message")){
            String title = getString(R.string.notification_default_title);
            String message = "";
            try {
                JSONTokener tokener = new JSONTokener(data);
                JSONObject jsonResponse = new JSONObject(tokener);
                if(jsonResponse.has("title")) {
                    title = jsonResponse.getString("title");
                }
                if(jsonResponse.has("message")) {
                    message = jsonResponse.getString("message");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Utils.showNotification(this, Constants.MESSAGE_NOTIFICATION_ID, title, message);
        }
    }
}