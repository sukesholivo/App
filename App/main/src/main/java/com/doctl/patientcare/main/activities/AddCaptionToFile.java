package com.doctl.patientcare.main.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.doctl.patientcare.main.BaseActivity;
import com.doctl.patientcare.main.R;
import com.doctl.patientcare.main.utility.Constants;

import java.util.ArrayList;
import java.util.List;

public class AddCaptionToFile extends BaseActivity implements AdapterView.OnItemSelectedListener {

    Uri imageUri;
    List<String> categories;
    String docCategory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_caption_to_file);

        ActionBar actionBar= getSupportActionBar();
        if(actionBar != null){
            actionBar.setTitle("Send Photo");
            actionBar.setSubtitle("user name");
        }

        categories = new ArrayList<String>();
        categories.add("Prescription");
        categories.add("Report");
        categories.add("Bill");
        categories.add("Other");

        imageUri = getIntent().getData();
        ImageView imagePreview=(ImageView)findViewById(R.id.imagePreview);
        imagePreview.setImageURI(imageUri);

        Button sendButton= (Button) findViewById(R.id.btn_send);
        Spinner spinner=(Spinner)findViewById(R.id.doc_type);

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(this);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data= new Intent();
                data.setData(imageUri);
                EditText captionView= (EditText) findViewById(R.id.caption);
                data.putExtra(Constants.CAPTION, captionView.getText().toString());
                data.putExtra(Constants.DOC_CATEGORY, docCategory);
                finishActivity(data, RESULT_OK);
            }
        });

        Button cancelButton = (Button) findViewById(R.id.btn_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishActivity(null, RESULT_CANCELED);
            }
        });
    }

    private void finishActivity(Intent data, int result) {
        if (getParent() == null) {
            setResult(result, data);
        } else {
            getParent().setResult(result, data);
        }
        finish();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = categories.get(position);
        docCategory = item;
        // Showing selected spinner item
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
