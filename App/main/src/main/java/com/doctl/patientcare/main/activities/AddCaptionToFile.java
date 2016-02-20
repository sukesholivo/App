package com.doctl.patientcare.main.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.doctl.patientcare.main.BaseActivity;
import com.doctl.patientcare.main.R;
import com.doctl.patientcare.main.utility.Constants;

public class AddCaptionToFile extends BaseActivity {

    Uri imageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_caption_to_file);

        ActionBar actionBar= getSupportActionBar();
        if(actionBar != null){
            actionBar.setTitle("Send Photo");
            actionBar.setSubtitle("user name");
        }

        imageUri = getIntent().getData();
        ImageView imagePreview=(ImageView)findViewById(R.id.imagePreview);
        imagePreview.setImageURI(imageUri);

        Button sendButton= (Button) findViewById(R.id.btn_send);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent data= new Intent();
                data.setData(imageUri);
                EditText captionView= (EditText) findViewById(R.id.caption);
                data.putExtra(Constants.CAPTION, captionView.getText().toString());
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

    private void finishActivity(Intent data, int result){
        if(getParent() == null){
            setResult(result, data);
        }else{
            getParent().setResult(result, data);
        }
        finish();

    }
}
