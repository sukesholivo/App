package com.doctl.patientcare.main.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.doctl.patientcare.main.BaseActivity;
import com.doctl.patientcare.main.MainActivity;
import com.doctl.patientcare.main.R;
import com.doctl.patientcare.main.om.chat.Question;
import com.doctl.patientcare.main.om.chat.QuestionListAdapter;
import com.doctl.patientcare.main.om.vitals.VitalDetailData;
import com.doctl.patientcare.main.services.HTTPServiceHandler;
import com.doctl.patientcare.main.utility.Constants;
import com.doctl.patientcare.main.utility.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 5/4/2015.
 */
public class QuestionListActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_question_list);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setTitle("Questions");
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        refresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.question_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent mainActivityIntent = new Intent(this, MainActivity.class);
                mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mainActivityIntent);
                return true;
            case R.id.action_add_question:
                Intent intent = new Intent(this, QuestionDetailActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void refresh() {
        new GetQuestions().execute();
    }

    private void refreshActivity() {
        String jsonStr = downloadQuestions();
        if (jsonStr != null && !jsonStr.isEmpty()) {
            final Question[] questionList = parseQuestionList(jsonStr);
            runOnUiThread(new Runnable() {
                public void run() {
                    updateList(questionList);
                }
            });
        }
    }

    private String downloadQuestions() {
        String url = Constants.QUESTION_URL;
        HTTPServiceHandler serviceHandler = new HTTPServiceHandler(this);
        return serviceHandler.makeServiceCall(url, HTTPServiceHandler.HTTPMethod.GET, null, null);
    }

    private Question[] parseQuestionList(String jsonStr){
        return new Gson().fromJson(jsonStr, Question[].class);
    }

    private void updateList(Question[] questions){
        List<Question> questionList = new ArrayList<>();
        for (Question q : questions){
            questionList.add(q);
        }
        QuestionListAdapter questionListAdapter = new QuestionListAdapter(this, questionList);
        ListView questionsListView = (ListView) this.findViewById(R.id.questions_list);
        questionsListView.setAdapter(questionListAdapter);

        questionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                final Question item = (Question) parent.getItemAtPosition(position);
                Intent intent = new Intent(QuestionListActivity.this, QuestionDetailActivity.class);
                // sending data to new activity
                intent.putExtra("question_id", item.getId());
                startActivity(intent);
            }
        });
    }

    private class GetQuestions extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            refreshActivity();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }
}
