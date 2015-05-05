package com.doctl.patientcare.main.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.doctl.patientcare.main.BaseActivity;
import com.doctl.patientcare.main.MainActivity;
import com.doctl.patientcare.main.R;
import com.doctl.patientcare.main.om.chat.Message;
import com.doctl.patientcare.main.om.chat.MessageListAdapter;
import com.doctl.patientcare.main.om.chat.Question;
import com.doctl.patientcare.main.om.chat.QuestionListAdapter;
import com.doctl.patientcare.main.services.HTTPServiceHandler;
import com.doctl.patientcare.main.utility.Constants;
import com.doctl.patientcare.main.utility.Logger;
import com.doctl.patientcare.main.utility.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 5/4/2015.
 */
public class QuestionDetailActivity extends BaseActivity {
    MessageListAdapter mMessageListAdapter;
    Menu mMenu;
    boolean askMode = false;
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
            String questionData = bundle.getString("question_data");
            hideNewQuestionLayout();
            if (questionId != null && !questionId.isEmpty()) {
                new GetQuestionDetail().execute(questionId);
            } else if (questionData != null && !questionData.isEmpty()){
                final Question question = parseQuestionData(questionData);
                updateQuestionData(question);
            }
        } else {
            askMode = true;
            showNewQuestionLayout();
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
        TextView questionTextView = (TextView) this.findViewById(R.id.question_text);
        RelativeLayout chatListLayout = (RelativeLayout) this.findViewById(R.id.message_list_layout);
        askQuestionEditText.setVisibility(View.VISIBLE);
        questionTextView.setVisibility(View.GONE);
        chatListLayout.setVisibility(View.GONE);
    }

    private void hideNewQuestionLayout(){
        EditText askQuestionEditText = (EditText) this.findViewById(R.id.ask_question_edit_text);
        TextView questionTextView = (TextView) this.findViewById(R.id.question_text);
        RelativeLayout chatListLayout = (RelativeLayout) this.findViewById(R.id.message_list_layout);
        askQuestionEditText.setVisibility(View.GONE);
        questionTextView.setVisibility(View.VISIBLE);
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
}
