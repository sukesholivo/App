package com.doctl.patientcare.main.activities;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.doctl.patientcare.main.BaseActivity;
import com.doctl.patientcare.main.R;
import com.doctl.patientcare.main.om.UserProfile;
import com.doctl.patientcare.main.om.chat.Message;
import com.doctl.patientcare.main.om.chat.MessageListAdapter;
import com.doctl.patientcare.main.om.chat.Thread;
import com.doctl.patientcare.main.services.DownloadImageTask;
import com.doctl.patientcare.main.services.HTTPServiceHandler;
import com.doctl.patientcare.main.utility.Constants;
import com.doctl.patientcare.main.utility.HttpFileUpload;
import com.doctl.patientcare.main.utility.Logger;
import com.doctl.patientcare.main.utility.Utils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 5/4/2015.
 *
 */
public class ThreadDetailActivity extends BaseActivity {
    private static final String TAG = ThreadDetailActivity.class.getSimpleName();
    MessageListAdapter mMessageListAdapter;
    ListView messageListView;
    String threadId;
    List<Message> messageList;
    UserProfile userProfile, otherUserProfile;

    private static final int IMAGE_ATTACH_BUTTON = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread_detail);
        userProfile = Utils.getPatientDataFromSharedPreference(this);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        Bundle bundle = getIntent().getExtras();
        if (actionBar != null){
            actionBar.setCustomView(R.layout.thread_action_bar);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowCustomEnabled(true);
        }

        if (bundle != null){
            threadId = bundle.getString(Constants.THREAD_ID);
            if (threadId != null && !threadId.isEmpty()) {
                new GetThreadContent().execute(threadId);
            }
        }

        final EditText messageEditText = (EditText) findViewById(R.id.etMessage);
        ImageButton sendButton = (ImageButton) findViewById(R.id.btSend);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (messageEditText.getText() != null && messageEditText.getText().length() != 0 ) {

                    String messageText = messageEditText.getText().toString();
                    Message msg = new Message(userProfile, new Date(), messageText, null, null,null);
                    addMessageToAdapter(msg);
                    new SendText().execute(threadId, messageText);
                    messageEditText.setText("");
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        this.registerReceiver(mMessageReceiver, new IntentFilter(Constants.BROADCAST_INTENT_CHAT_MESSAGE_RECEIVED));
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(mMessageReceiver);
    }

    private void updateActionBarView(View view, UserProfile userProfile){

        ActionBarViewHolder viewHolder=new ActionBarViewHolder(view);
        viewHolder.update(userProfile);
    }

    class ActionBarViewHolder{
        ImageView profilePic;
        TextView displayName;
        TextView lastSeen;

        public ActionBarViewHolder(View view){

            profilePic = (ImageView) view.findViewById(R.id.profile_pic);
            displayName = (TextView) view.findViewById(R.id.display_name);
            lastSeen = (TextView) view.findViewById(R.id.last_seen);
        }

        public void update(UserProfile userProfile){


            if( userProfile == null){
                return;
            }
            if( userProfile.getProfilePicUrl() != null) {
                new DownloadImageTask(profilePic).execute(Constants.SERVER_URL + userProfile.getProfilePicUrl());
            }
            if( userProfile.getDisplayName() != null && !userProfile.getDisplayName().isEmpty()){
                displayName.setText(userProfile.getDisplayName());
            }else if(userProfile.getPhone() != null && !userProfile.getPhone().isEmpty()){
                displayName.setText(userProfile.getPhone());
            }
            //TODO add last seen
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_ATTACH_BUTTON) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    try {
                        //TODO add image to listview before sending
                        new SendFile().execute(data.getData());
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            }else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.thread_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){

        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, ThreadListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            case R.id.attach_button:
                clickedAttachButton(null);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    private void refreshActivity(String userId) {
        String jsonStr = downloadThreadContent(userId);
        if (jsonStr != null && !jsonStr.isEmpty()) {
            final Thread threadData = parseThreadData(jsonStr);
            runOnUiThread(new Runnable() {
                public void run() {
                    updateThreadData(threadData);
                }
            });
        }
    }

    private String downloadThreadContent(String userId) {
        String url = Constants.QUESTION_URL + userId;
        HTTPServiceHandler serviceHandler = new HTTPServiceHandler(this);
        return serviceHandler.makeServiceCall(url, HTTPServiceHandler.HTTPMethod.GET, null, null);
    }

    private Thread parseThreadData(String jsonStr){
        return new Gson().fromJson(jsonStr, Thread.class);
    }

    private void updateThreadData(Thread thread){

        messageList = thread.getMessages();
        Collections.reverse(messageList);
        mMessageListAdapter = new MessageListAdapter(this, messageList, userProfile.getId());
        messageListView = (ListView) this.findViewById(R.id.message_list);
        messageListView.setAdapter(mMessageListAdapter);
        scrollMyListViewToBottom();
        otherUserProfile = UserProfile.getOtherUserProfile(userProfile.getId(), thread.getUserProfiles());
    }


    private class SendFile extends AsyncTask<Uri, Void, Message> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Message doInBackground(Uri... arg0) {
            String serverUrl = Constants.QUESTION_URL+ threadId +"/";
            Uri fileUri = arg0[0];
            InputStream is = null;
            try {
                is = getContentResolver().openInputStream(fileUri);
                return uploadFile(serverUrl, is);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
           return null;
        }

        @Override
        protected void onPostExecute(Message message) {
            super.onPostExecute(message);
            if(message != null) {
                addMessageToAdapter(message);
            }
        }

        public Message uploadFile(String serverUrl, InputStream is){
            try {
                HttpFileUpload hfu = new HttpFileUpload(ThreadDetailActivity.this, serverUrl);
                JSONObject jsonResponse= hfu.Send_Now("msg_attach.jpg", is);
                return new Gson().fromJson(jsonResponse.toString(), Message.class);
            } catch (Exception e) {
                Logger.e(TAG, e.getMessage());
            }
            return null;
        }
    }

    private class SendText extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... arg0) {
            String userId= arg0[0];
            String text = arg0[1];
            sendToServer(userId, text);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }

        public void sendToServer(String userId, String text){

            try {
                String url = Constants.QUESTION_URL + userId + "/";
                JSONObject data = new JSONObject();
                data.put("text", text);
                HTTPServiceHandler serviceHandler = new HTTPServiceHandler(ThreadDetailActivity.this);
                String response = serviceHandler.makeServiceCall(url, HTTPServiceHandler.HTTPMethod.POST, null, data);
                Logger.d(TAG, response);
                /*try
                {
                    int CONNECTION_TIMEOUT_MILLISEC = 7000; // = 7 seconds
                    int SOCKET_TIMEOUT_MILLISEC = 10000; // = 10 seconds
                    HttpParams httpParams = new BasicHttpParams();
                    HttpConnectionParams.setConnectionTimeout(httpParams, CONNECTION_TIMEOUT_MILLISEC);
                    HttpConnectionParams.setSoTimeout(httpParams, SOCKET_TIMEOUT_MILLISEC);

                    HttpClient client = new DefaultHttpClient(httpParams);
                    HttpPost post = new HttpPost(url);

                    String serverAccessToken = Utils.getAuthTokenFromSharedPreference(getBaseContext());
                    Logger.e(TAG, "server access token: " + serverAccessToken);
                    post.setHeader("Authorization", "Token " + serverAccessToken);
                    post.setHeader("Content-type","application/json");

                    MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
                    entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

                    entityBuilder.addTextBody(Constants.TEXT, text);
                    entityBuilder.addTextBody(Constants.RECEIVER_ID, receiverId);

                    *//*if(file != null)
                    {
                        entityBuilder.addBinaryBody(Constants., file);
                    }*//*

                    HttpEntity entity = entityBuilder.build();
                    post.setEntity(entity);
                    HttpResponse response = client.execute(post);
                    HttpEntity httpEntity = response.getEntity();
                    String res = EntityUtils.toString(httpEntity);
                    int statusCode = response.getStatusLine().getStatusCode();
                    Logger.d(TAG, "statusCode: " + statusCode);
                    Logger.e(TAG, res);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }*/
            }catch (JSONException e){
                Logger.e(TAG, e.getMessage());
            }
        }
    }

    private class ReadThreadContent extends AsyncTask<String, Void, Void>{
        @Override
        protected Void doInBackground(String... params) {
            String threadId = params[0];
            String url = Constants.READ_THREAD_CONTENT_URL+threadId+"/";

            HTTPServiceHandler httpServiceHandler=new HTTPServiceHandler(ThreadDetailActivity.this);
            httpServiceHandler.makeServiceCall(url, HTTPServiceHandler.HTTPMethod.POST, null, null);
            return null;
        }
    }
    private class GetThreadContent extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(String... arg0) {
            String userId = arg0[0];
            refreshActivity(userId);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
            if(getSupportActionBar() != null) {
                updateActionBarView(getSupportActionBar().getCustomView(), otherUserProfile);
            }
            new ReadThreadContent().execute(threadId);
        }
    }

    @SuppressLint("NewApi")
    public static String getRealPathFromURI_API19(Context context, Uri uri){
        String filePath = "";
        System.out.println(uri);
        String wholeID = DocumentsContract.getDocumentId(uri);

        // Split at colon, use second item in the array
        System.out.println("Whole id: "+ wholeID);
        String id = wholeID.split(";")[1];

        String[] column = { MediaStore.Images.Media.DATA };

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{ id }, null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }

    public void clickedAttachButton(View v){

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra("threadId", threadId); //
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), IMAGE_ATTACH_BUTTON);
    }
    private void scrollMyListViewToBottom() {
        messageListView.post(new Runnable() {
            @Override
            public void run() {
                messageListView.setSelection(mMessageListAdapter.getCount() - 1);
            }
        });
    }
    private void addMessageToAdapter(Message message){
        if(message == null) return;
        mMessageListAdapter.add(message);
        mMessageListAdapter.notifyDataSetChanged();
        scrollMyListViewToBottom();
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String jsonStringMessage = intent.getStringExtra(Constants.CHAT_MESSAGE);
            Message message = Message.createMessage(jsonStringMessage);
            if( threadId.equals(message.getThreadId())) {
                addMessageToAdapter(message);
            }else{
                showMessageNotification(context, message);
            }
            new ReadThreadContent().execute(threadId);
        }
    };


    public static void showMessageNotification(Context context, Message message){

        String notificationTitle = "Message from ";
        notificationTitle= message.getSource() != null? message.getSource().getDisplayName(): " unknown";
        String notificationMessage = message.getText() != null? message.getText(): "File";
        UserProfile source= message.getSource();
        if(source != null) {
            Utils.showChatNotification(context, Constants.MESSAGE_NOTIFICATION_ID, notificationTitle, notificationMessage, message.getThreadId(), source.getId(), source.getDisplayName(), source.getProfilePicUrl());
        }
    }
    @Override
    protected void onStart() {
        super.onStart();

        // Store our shared preference
        SharedPreferences sp = getSharedPreferences(Constants.IS_THREAD_DETAIL_ACTIVITY_FOREGROUND, MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean("active", true);
        ed.apply();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Store our shared preference
        SharedPreferences sp = getSharedPreferences(Constants.IS_THREAD_DETAIL_ACTIVITY_FOREGROUND, MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean("active", false);
        ed.apply();
    }

    public static Intent createThreadDetailIntent(Context context, String threadId, String userId, String userDisplayName, String userProfilePicURL){

        Intent intent = new Intent(context, ThreadDetailActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(Constants.THREAD_ID, threadId);
        intent.putExtra(Constants.USER_ID, userId);
        intent.putExtra(Constants.DISPLAY_NAME, userDisplayName);
        intent.putExtra(Constants.PROFILE_PIC_URL, userProfilePicURL);
        return intent;
    }

}
