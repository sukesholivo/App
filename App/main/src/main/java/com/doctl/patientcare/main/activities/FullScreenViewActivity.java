package com.doctl.patientcare.main.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.doctl.patientcare.main.R;
import com.doctl.patientcare.main.controls.TouchImageView;
import com.doctl.patientcare.main.utility.Constants;
import com.squareup.picasso.Picasso;

/**
 * Created by Administrator on 5/11/2015.
 */
public class FullScreenViewActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_fullscreen);

        Bundle bundle = getIntent().getExtras();
        if (bundle == null){
            return;
        }
        String url =  bundle.getString("url");
        String thumbnail_url = Constants.SERVER_URL + url;
        TouchImageView imgDisplay;
        ImageButton btnClose;

        imgDisplay = (TouchImageView) this.findViewById(R.id.imgDisplay);
        btnClose = (ImageButton) this.findViewById(R.id.btnClose);

        Picasso.with(this)
                .load(thumbnail_url)
                .into(imgDisplay);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FullScreenViewActivity.this.finish();
            }
        });
    }
}
