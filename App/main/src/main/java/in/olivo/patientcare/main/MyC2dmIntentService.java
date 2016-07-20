package in.olivo.patientcare.main;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Html;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import in.olivo.patientcare.main.activities.ThreadDetailActivity;
import in.olivo.patientcare.main.om.BaseTask;
import in.olivo.patientcare.main.om.chat.Message;
import in.olivo.patientcare.main.om.education.EducationTask;
import in.olivo.patientcare.main.om.followup.FollowupTask;
import in.olivo.patientcare.main.om.message.MessageTask;
import in.olivo.patientcare.main.utility.Constants;
import in.olivo.patientcare.main.utility.Logger;
import in.olivo.patientcare.main.utility.Utils;

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
        if (ServerAccessToken.isEmpty()) {
            return;
        }

        String type = msg.getString("type");
        String username = msg.getString("username");
        String usernameInDevice = Utils.getUserNameFromSharedPreference(this);
        if (!usernameInDevice.equals(username)) {
            return;
        }

        String data = msg.getString("data");
        if (type.toLowerCase().equals("card")) {
            BaseTask task = Utils.parseCardData(data);
            switch (task.getType()) {
                case MEDICINE:
                case VITAL:
                case RECOMMENDATION:
                case OBJECTIVE:
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
        } else if (type.toLowerCase().equals("message")) {
            String title = getString(R.string.notification_default_title);
            String message = "";
            try {
                JSONTokener tokener = new JSONTokener(data);
                JSONObject jsonResponse = new JSONObject(tokener);
                if (jsonResponse.has("title")) {
                    title = jsonResponse.getString("title");
                }
                if (jsonResponse.has("message")) {
                    message = Html.fromHtml(jsonResponse.getString("message")).toString();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Utils.showNotification(this, Constants.MESSAGE_NOTIFICATION_ID, title, message);
        } else if (type.toLowerCase().equals("chat")) {

            //TODO if activity is not live push notification

            try {
                Logger.e(TAG, " Received message " + data);
                JSONTokener tokener = new JSONTokener(data);
                JSONObject jsonResponse = new JSONObject(tokener);

                SharedPreferences sp = getSharedPreferences(Constants.IS_THREAD_DETAIL_ACTIVITY_FOREGROUND, MODE_PRIVATE);
                boolean isActive = sp.getBoolean("active", false);
                if (isActive) {
                    Intent intent = new Intent(Constants.BROADCAST_INTENT_CHAT_MESSAGE_RECEIVED);
                    intent.putExtra(Constants.CHAT_MESSAGE, jsonResponse.getString("message"));
                    this.sendBroadcast(intent);
                } else {
                    ThreadDetailActivity.showMessageNotification(this, Message.createMessage(jsonResponse.getString("message")));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}