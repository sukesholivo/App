package com.doctl.patientcare.main.services;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.doctl.patientcare.main.R;
import com.doctl.patientcare.main.utility.Logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by Administrator on 7/29/2014.
 */
public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    final ImageView bmImage;
    boolean exception = false;
    public DownloadImageTask(ImageView bmImage) {
        this.bmImage = bmImage;
    }
    protected Bitmap doInBackground(String... args) {
        String url = args[0];
        Bitmap bmp = null;
        try {
            InputStream in = new URL(url).openStream();
            System.out.println("######## URL ##########" + url);
            bmp = BitmapFactory.decodeStream(in);
        } catch (IOException e) {
            Logger.e("Error", e.getMessage());
            exception = true;
        }
        return bmp;
    }

    protected void onPostExecute(Bitmap result) {
        if (!exception) {
            bmImage.setImageBitmap(result);
        } else {
            bmImage.setImageResource(R.drawable.profile_dummy);
        }
    }
}
