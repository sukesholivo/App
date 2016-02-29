package com.doctl.patientcare.main.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by Satya Madala on 24/2/16.
 * email : satya.madala@olivo.in
 */
public class ImageUtils {

    private static final String TAG = ImageUtils.class.getSimpleName();
    public static void loadImage(ImageView imageView, String filePath){
        new LoadImage(imageView, filePath, null, null).execute();
    }
    public static void loadImage(ImageView imageView, Context context, Uri uri){
        new LoadImage(imageView, null, context, uri);
    }

    private static class LoadImage extends AsyncTask<Void, Void, Void>{
        String filePath;
        Uri uri;
        Context context;
        ImageView imageView;
        Bitmap bitmap;
        public LoadImage(ImageView imageView, String filePath, Context context, Uri uri) {
            this.filePath = filePath;
            this.uri = uri;
            this.context = context;
            this.imageView=imageView;
        }

        @Override
        protected Void doInBackground(Void... params) {
            if( filePath != null){
                File imgFile = new  File(filePath);

                if(imgFile.exists()){
                    bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    Log.d(TAG, "Loading image from path: "+ filePath);
                }
            }else if( uri != null){
                try {
                    InputStream imageStream = context.getContentResolver().openInputStream(uri);
                    bitmap = BitmapFactory.decodeStream(imageStream);
                    Log.d(TAG, "Loading image from Uri:"+uri);
                } catch (FileNotFoundException e) {
                    Log.e(TAG, "cann't open stream for Uri: " + uri, e);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(bitmap != null){
                imageView.setImageBitmap(bitmap);
            }
        }
    }

}
