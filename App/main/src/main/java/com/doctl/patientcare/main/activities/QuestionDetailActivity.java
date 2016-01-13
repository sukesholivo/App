package com.doctl.patientcare.main.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.doctl.patientcare.main.BaseActivity;
import com.doctl.patientcare.main.MainActivity;
import com.doctl.patientcare.main.R;
import com.doctl.patientcare.main.om.chat.Message;
import com.doctl.patientcare.main.om.chat.MessageListAdapter;
import com.doctl.patientcare.main.om.chat.Question;
import com.doctl.patientcare.main.services.HTTPServiceHandler;
import com.doctl.patientcare.main.utility.Constants;
import com.doctl.patientcare.main.utility.HttpFileUpload;
import com.doctl.patientcare.main.utility.Logger;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.util.List;

/**
 * Created by Administrator on 5/4/2015.
 */
public class QuestionDetailActivity extends BaseActivity {
    private static final String TAG = QuestionDetailActivity.class.getSimpleName();
    MessageListAdapter mMessageListAdapter;
    Menu mMenu;
    boolean askMode = false;
    String questionId;

    private static final int IMAGE_ATTACH_BUTTON = 10243;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_question_detail);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setTitle("Questions");
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            askMode = false;
            String questionId = bundle.getString("question_id");
            this.questionId = questionId;
            String questionData = bundle.getString("question_data");
            hideNewQuestionLayout();
            if (questionId != null && !questionId.isEmpty()) {
                new GetQuestionDetail().execute(questionId);
                addActionToAttachButton(questionId);
            } else if (questionData != null && !questionData.isEmpty()){
                final Question question = parseQuestionData(questionData);
                updateQuestionData(question);
            }
        } else {
            askMode = true;
            showNewQuestionLayout();
        }
        final EditText messageEditText = (EditText) findViewById(R.id.etMessage);
        ImageButton sendButton = (ImageButton) findViewById(R.id.btSend);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (messageEditText.getText() != null) {
                    String message = messageEditText.getText().toString();
                    new SendText().execute(questionId, message);
                    System.out.println("Message " + message);
                    messageEditText.setText("");
                }
            }
        });

    }

    private void addActionToAttachButton(final String questionId){

        ImageButton attachItemButton = (ImageButton) findViewById(R.id.attach_button);
        attachItemButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.putExtra("questionId", questionId);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), IMAGE_ATTACH_BUTTON);
            }
        });
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_ATTACH_BUTTON) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    try {
                        //String questionId=data.getExtras().getString("questionId");
                        String questionURL = Constants.QUESTION_URL+questionId+"/";
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
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.question_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        if (!askMode) {
            menu.getItem(0).setVisible(false);
        }
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
            case R.id.action_question_send:
                EditText editText = (EditText) this.findViewById(R.id.ask_question_edit_text);
                String questionText = editText.getText().toString();
                new AskQuestion().execute(questionText);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showNewQuestionLayout(){
        EditText askQuestionEditText = (EditText) this.findViewById(R.id.ask_question_edit_text);
        CardView questionCardview = (CardView) this.findViewById(R.id.question_text_layout);
        CardView chatListLayout = (CardView) this.findViewById(R.id.message_list_layout);
        askQuestionEditText.setVisibility(View.VISIBLE);
        questionCardview.setVisibility(View.GONE);
        chatListLayout.setVisibility(View.GONE);
    }

    private void hideNewQuestionLayout(){
        EditText askQuestionEditText = (EditText) this.findViewById(R.id.ask_question_edit_text);
        CardView questionCardview = (CardView) this.findViewById(R.id.question_text_layout);
        CardView chatListLayout = (CardView) this.findViewById(R.id.message_list_layout);
        askQuestionEditText.setVisibility(View.GONE);
        questionCardview.setVisibility(View.VISIBLE);
        chatListLayout.setVisibility(View.VISIBLE);

    }

    private void refreshActivity(String questionId) {
        String jsonStr = downloadQuestionData(questionId);
        if (jsonStr != null && !jsonStr.isEmpty()) {
            final Question questionData = parseQuestionData(jsonStr);
            runOnUiThread(new Runnable() {
                public void run() {
                    updateQuestionData(questionData);
                }
            });
        }
    }

    private String downloadQuestionData(String questionId) {
        String url = Constants.QUESTION_URL + questionId;
        HTTPServiceHandler serviceHandler = new HTTPServiceHandler(this);
        return serviceHandler.makeServiceCall(url, HTTPServiceHandler.HTTPMethod.GET, null, null);
    }

    private Question parseQuestionData(String jsonStr){
        return new Gson().fromJson(jsonStr, Question.class);
    }

    private void updateQuestionData(Question question){
        TextView questionTextView = (TextView) this.findViewById(R.id.question_text);
        questionTextView.setText(question.getText());

        List<Message> messageList = question.getMessages();
        mMessageListAdapter = new MessageListAdapter(this, messageList);
        ListView messageListView = (ListView) this.findViewById(R.id.message_list);
        messageListView.setAdapter(mMessageListAdapter);
    }

    private class AskQuestion extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... arg0) {
            String url = Constants.QUESTION_URL;
            JSONObject data = new JSONObject();
            try {
                data.put("question", arg0[0]);
                HTTPServiceHandler serviceHandler = new HTTPServiceHandler(QuestionDetailActivity.this);
                String response = serviceHandler.makeServiceCall(url, HTTPServiceHandler.HTTPMethod.POST, null, data);
                Intent intent = new Intent(QuestionDetailActivity.this, QuestionDetailActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("question_data", response);
                startActivity(intent);
            } catch (JSONException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
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
                HttpFileUpload hfu = new HttpFileUpload(QuestionDetailActivity.this, serverUrl);
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
            String questionId= arg0[0];
            String text = arg0[1];
            sendToServer(questionId, text);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }

        public void sendToServer(String questionId, String text){

            try {
                String url = Constants.QUESTION_URL + questionId + "/";
                JSONObject data = new JSONObject();
                data.put("text", text);
                HTTPServiceHandler serviceHandler = new HTTPServiceHandler(QuestionDetailActivity.this);
                String response = serviceHandler.makeServiceCall(url, HTTPServiceHandler.HTTPMethod.POST, null, data);
                System.out.println(" Response " + response);
            }catch (JSONException e){
                Logger.e(TAG, e.getMessage());
            }

        }
    }

    private class GetQuestionDetail extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... arg0) {
            String questionId = arg0[0];
            refreshActivity(questionId);
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
}
