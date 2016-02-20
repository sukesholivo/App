package com.doctl.patientcare.main.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.doctl.patientcare.main.BaseActivity;
import com.doctl.patientcare.main.R;
import com.doctl.patientcare.main.services.DownloadImageTask;
import com.doctl.patientcare.main.utility.Constants;

public class ShowImage extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);
        ImageView image = (ImageView) findViewById(R.id.image);
        Intent intent=getIntent();
        Uri imageUri=intent.getData();
        if(imageUri != null) {
            image.setImageURI(imageUri);
        }else{
            String imageUrl = intent.getStringExtra(Constants.IMAGE_URL);
            if(imageUrl != null){
                new DownloadImageTask(image, getBaseContext()).execute(Constants.SERVER_URL+imageUrl);
            }
        }
    }
}
