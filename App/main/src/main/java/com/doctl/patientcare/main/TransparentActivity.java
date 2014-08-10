package com.doctl.patientcare.main;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import static android.view.View.OnClickListener;

/**
 * Created by mailtovishal.r on 6/27/2014.
 */
public class TransparentActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_transparent_activity);

        OnClickListener clickListener = new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        };
        Button button1 = (Button)findViewById(R.id.popup_medicine_taken_button);
        Button button2 = (Button)findViewById(R.id.popup_medicine_snooze_button);
        button1.setOnClickListener(clickListener);
        button2.setOnClickListener(clickListener);
    }
}
