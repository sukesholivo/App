package in.olivo.patientcare.main.services;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import in.olivo.patientcare.main.R;
import in.olivo.patientcare.main.utility.Logger;

/**
 * Created by Administrator on 7/29/2014.
 */
public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    final ImageView bmImage;
    final Context context;
    boolean exception = false;

    public DownloadImageTask(ImageView bmImage, Context context) {
        this.bmImage = bmImage;
        this.context = context;
    }

    protected Bitmap doInBackground(String... args) {
        String url = args[0];
        Bitmap bmp = null;
        try {
            InputStream in = new URL(url).openStream();
            bmp = BitmapFactory.decodeStream(in);
        } catch (IOException e) {

            if (context != null) {
                try {
                    InputStream in = context.getContentResolver().openInputStream(Uri.parse(url));
                    bmp = BitmapFactory.decodeStream(in);
                } catch (FileNotFoundException e1) {
                    Logger.e("Error", e1.getMessage());
                    exception = true;
                    e1.printStackTrace();
                }
            } else {
                Logger.e("Error", e.getMessage());
                exception = true;
                e.printStackTrace();
            }
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
