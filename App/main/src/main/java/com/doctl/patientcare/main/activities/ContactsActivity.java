package com.doctl.patientcare.main.activities;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import com.doctl.patientcare.main.BaseActivity;
import com.doctl.patientcare.main.MainActivity;
import com.doctl.patientcare.main.R;
import com.doctl.patientcare.main.om.contact.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by satya on 19/1/16.
 */
public class ContactsActivity extends BaseActivity{

    private ContactListAdapter mAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_list);
        List<User> users = User.getDummyUsers();
        Map<Integer, String> categoryPositions = new HashMap<>();

        categoryPositions.put(0,"Doctors");
        categoryPositions.put(3, "Pharmacy Stores");
        categoryPositions.put(5, "Medical labs");

        mAdapter = new ContactListAdapter(this, users, categoryPositions);
        ListView contactListView = (ListView) this.findViewById(R.id.list);
        contactListView.setAdapter(mAdapter);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.contact_list, menu);
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
}
