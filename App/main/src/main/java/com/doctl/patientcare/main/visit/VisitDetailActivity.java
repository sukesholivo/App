package com.doctl.patientcare.main.visit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.doctl.patientcare.main.BaseActivity;
import com.doctl.patientcare.main.R;

public class VisitDetailActivity extends BaseActivity {

        ListView list;
//    public final static String TAG = VisitDetailActivity.class.getSimpleName();
//    static boolean active = false;


            String[] values = new String[] { "Complaints:", "Diagnosis:", "Examinations:", "Medication:" };
    String[] data = new String[] { "Body Pain\nHeadache\nCold\nCough\nFever",
            "Body Temperature\nPhysical Tests\nBlood Test",
            "Sugar Level\nBlood Pressure\nPrevious Treatment Results", "1. Tablets\n2. Syrup\n3. Capsules" };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit_details);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setTitle("Visit Details");
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        VisitDetailAdapter adapter = new
//                VisitListAdapter(VisitListActivity.this, values, imageId);
                VisitDetailAdapter(VisitDetailActivity.this, values, data);
        list = (ListView) findViewById(R.id.visitview);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Toast.makeText(VisitDetailActivity.this, "You Clicked at " + values[+position], Toast.LENGTH_SHORT).show();

            }
        });

        Intent myintent2 = getIntent();
        String message = myintent2.getStringExtra(VisitListActivity.EXTRA_MESSAGE);
        TextView textView = (TextView) findViewById(R.id.textView6);
        textView.setText(message);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, VisitListActivity.class);
        startActivity(intent);

        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, VisitListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            // Something else
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
