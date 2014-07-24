package com.doctl.patientcare.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.File;

import it.gmariotti.cardslib.library.utils.BitmapUtils;

/**
 * Created by mailtovishal.r on 6/25/2014.
 */
public class RichTextEducationDetailActivity extends Activity {
    private ShareActionProvider mShareActionProvider;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_education_richtext_detail);
        setupEducationImage();
    }

    private void setupEducationImage() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.education_detail, menu);

        return super.onCreateOptionsMenu(menu);
    }

    private Intent getShareIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_SUBJECT, "How to take Insulin");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("image/jpeg");

        startActivity(Intent.createChooser(intent, "Send Mail"));
        return intent;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.education_action_share:
                View view = findViewById(R.id.educationDetailLayout);
                Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas c = new Canvas(bitmap);
                view.draw(c);
                File photofile = BitmapUtils.createFileFromBitmap(bitmap);
                Intent shareIntent = BitmapUtils.createIntentFromImage(photofile);
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "How to take Insulin");
                startActivity(Intent.createChooser(shareIntent, "Share..."));
                return true;
            case R.id.education_action_settings:
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
