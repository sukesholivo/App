package com.doctl.patientcare.main.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
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
import com.doctl.patientcare.main.MainActivity;
import com.doctl.patientcare.main.R;
import com.doctl.patientcare.main.om.chat.Message;
import com.doctl.patientcare.main.om.chat.MessageListAdapter;
import com.doctl.patientcare.main.om.chat.Thread;
import com.doctl.patientcare.main.services.DownloadImageTask;
import com.doctl.patientcare.main.services.HTTPServiceHandler;
import com.doctl.patientcare.main.utility.Constants;
import com.doctl.patientcare.main.utility.HttpFileUpload;
import com.doctl.patientcare.main.utility.Logger;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 5/4/2015.
 */
public class ThreadDetailActivity extends BaseActivity {
    private static final String TAG = ThreadDetailActivity.class.getSimpleName();
    MessageListAdapter mMessageListAdapter;
    ListView messageListView;
    String userId="1";

    private static final int IMAGE_ATTACH_BUTTON = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_detail);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        Bundle bundle = getIntent().getExtras();
        if (actionBar != null){
            actionBar.setCustomView(R.layout.thread_action_bar);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowCustomEnabled(true);
            updateActionBarView(actionBar.getCustomView(), bundle);
        }

        if (bundle != null){
            String userId ="2";// bundle.getString(Constants.USER_ID);
            this.userId = userId;
            if (userId != null && !userId.isEmpty()) {
                new GetThreadContent().execute(userId);
            }
        }
        final EditText messageEditText = (EditText) findViewById(R.id.etMessage);
        ImageButton sendButton = (ImageButton) findViewById(R.id.btSend);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (messageEditText.getText() != null) {
                    String message = messageEditText.getText().toString();
                    Message msg = new Message();
                    msg.setText(message);
                    msg.setTimestamp(new Date());
                    mMessageListAdapter.add(msg);
                    mMessageListAdapter.notifyDataSetChanged();
                    new SendText().execute(userId, message);
                    System.out.println("Message " + message);
                    messageEditText.setText("");
                    scrollMyListViewToBottom();
                }
            }
        });
    }

    private void updateActionBarView(View view, Bundle bundle){

        ActionBarViewHolder viewHolder=new ActionBarViewHolder(view);
        viewHolder.update(bundle);
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

        public void update(Bundle bundle){


            if( bundle == null){
                return;
            }
            if( bundle.containsKey(Constants.PROFILE_PIC_URL)) {
                new DownloadImageTask(profilePic).execute(Constants.SERVER_URL + bundle.getString(Constants.PROFILE_PIC_URL));
            }
            if( bundle.containsKey(Constants.DISPLAY_NAME)){
                displayName.setText(bundle.getString(Constants.DISPLAY_NAME));
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
                        //String userId=data.getExtras().getString("userId");
                        String questionURL = Constants.QUESTION_URL+ userId +"/"; //TODO add userId
                        String filePath = getRealPathFromURI_API19(this,data.getData());
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                        System.out.println("File name"+ filePath + bitmap);
                        new SendFile().execute(questionURL, filePath);

                    }catch (Exception e){
                        e.printStackTrace();
                    }

                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
                }
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
            case R.id.home:
                Intent mainActivityIntent = new Intent(this, MainActivity.class);
                mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mainActivityIntent);
                return true;
            case R.id.attach_button:
                clickedAttachButton(null);
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

        List<Message> messageList = thread.getMessages();
        mMessageListAdapter = new MessageListAdapter(this, messageList);
        messageListView = (ListView) this.findViewById(R.id.message_list);
        messageListView.setAdapter(mMessageListAdapter);
        scrollMyListViewToBottom();
    }


    private class SendFile extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(String... arg0) {
            String serverUrl = arg0[0];
            String imageUrl = arg0[1];
            uploadFile(serverUrl, imageUrl);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }

        public void uploadFile(String serverUrl, String imageFile){
            try {
                FileInputStream fstrm = new FileInputStream(imageFile);
                HttpFileUpload hfu = new HttpFileUpload(ThreadDetailActivity.this, serverUrl);
                JSONObject jsonResponse= hfu.Send_Now("msg_attach.jpg", fstrm);
                if(jsonResponse == null) throw new Exception("upload failed");
                String fileURL = jsonResponse.getString("fileURL");
                System.out.println(fileURL);
            } catch (Exception e) {
                Logger.e(TAG, e.getMessage());
            }
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
                System.out.println(" Response " + response);
            }catch (JSONException e){
                Logger.e(TAG, e.getMessage());
            }

        }
    }

    private class GetThreadContent extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
        }
    }

    @SuppressLint("NewApi")
    public static String getRealPathFromURI_API19(Context context, Uri uri){
        String filePath = "";
        String wholeID = DocumentsContract.getDocumentId(uri);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

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
        intent.putExtra("userId", userId); //
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), IMAGE_ATTACH_BUTTON);
    }
    private void scrollMyListViewToBottom() {
        messageListView.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                messageListView.setSelection(mMessageListAdapter.getCount() - 1);
            }
        });
    }
}
