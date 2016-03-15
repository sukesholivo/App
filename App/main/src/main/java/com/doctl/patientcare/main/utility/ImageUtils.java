package com.doctl.patientcare.main.utility;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Satya Madala on 24/2/16.
 * email : satya.madala@olivo.in
 */
public class ImageUtils {


    private static final String TAG = ImageUtils.class.getSimpleName();

    public static void loadImage(ImageView imageView, String filePath, Context context) {
        new LoadImage(imageView, filePath, context, null, null).execute();
    }

    public static void loadImage(ImageView imageView, Context context, Uri uri) {
        new LoadImage(imageView, null, context, uri, null).execute();
    }

    public static void loadImageFromUrl(Context context, ImageView imageView, String url) {

        new LoadImage(imageView, null, context, null, url).execute();
    }


    private static class LoadImage extends AsyncTask<Void, Void, Void> {

        String filePath, url;
        Uri uri;
        Context context;
        ImageView imageView;
        Bitmap bitmap;

        public LoadImage(ImageView imageView, String filePath, Context context, Uri uri, String url) {
            this.filePath = filePath;
            this.uri = uri;
            this.context = context;
            this.imageView = imageView;
            this.url = url;
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (filePath != null) {
                final File imgFile = new File(filePath);

                if (imgFile.exists()) {
//                    bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    Log.v(TAG, "Loading image from path: " + filePath);
                    if( context instanceof Activity) {
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Picasso.with(context)
                                        .load(imgFile)
                                        .fit()
                                        .into(imageView);
                            }
                        });

                    }else{
                        bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    }
                }else{
                    Log.e(TAG, "Not found image path: "+  filePath);
                }
            } else if (uri != null) {
                try {
                    if( context instanceof Activity){
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Picasso.with(context)
                                        .load(uri)
                                        .fit()
                                        .into(imageView);
                            }
                        });
                    }else {
                        InputStream imageStream = context.getContentResolver().openInputStream(uri);
                        bitmap = BitmapFactory.decodeStream(imageStream);
                    }
                    Log.v(TAG, "Loading image from Uri:" + uri);
                } catch (FileNotFoundException e) {
                    Log.e(TAG, "cann't open stream for Uri: " + uri, e);
                }
            } else if (url != null) {
                InputStream in = null;
                try {
                    if(context !=null && context instanceof Activity){
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Picasso.with(context)
                                        .load(url)
                                        .fit()
                                        .into(imageView);
                            }
                        });
                    }else {
                        in = new URL(url).openStream();
                        bitmap = BitmapFactory.decodeStream(in);
                    }
                } catch (IOException e) {
                    Log.e(TAG, "failed to load image from:" + url, e);
                }
            }else{
                Logger.e(TAG, "failed to Load image to: "+ toString() );
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
        }

        @Override
        public String toString() {
            return "LoadImage{" +
                    "filePath='" + filePath + '\'' +
                    ", url='" + url + '\'' +
                    ", uri=" + uri +
                    ", context=" + context +
                    ", imageView=" + imageView +
                    ", bitmap=" + bitmap +
                    '}';
        }
    }

    public static class DownloadFileAndDisplay extends AsyncTask<Void, Void, Void>{

        ImageView imageView;
        String url;
        boolean overwrite;
        Context context;

        public DownloadFileAndDisplay(ImageView imageView, String url, boolean overwrite, Context context) {
            this.imageView = imageView;
            this.url = url;
            this.overwrite = overwrite;
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                String localPath = FileUtils.getPathFromUrl(url);
                downloadFile(localPath, url, overwrite);
                ImageUtils.loadImage(imageView, localPath, context);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static boolean downloadFile(String path, String urlString, boolean overwrite )  {

        File file = new File(path);
        if (file.exists() && !overwrite) {
            return false;
        }
        File parent = file.getParentFile();
        boolean dirCreated=true;
        if (!parent.exists()) {
            dirCreated=parent.mkdirs();
        }

        if(!dirCreated){
            Log.e(TAG, "failed to create folder:" + parent.getAbsolutePath());
            return false;
        }
        InputStream input=null;
        OutputStream output=null;
        try {
            URL url = new URL(urlString);
            URLConnection conection = url.openConnection();
            conection.connect();
            input = new BufferedInputStream(url.openStream(),
                    8192);
            // Output stream
            output = new FileOutputStream(file);
            byte data[] = new byte[1024];
            int count;
            while ((count = input.read(data)) != -1) {
                output.write(data, 0, count);
            }
        }catch (Exception e){
            Log.e(TAG, " error while downloading "+ "path = [" + path + "], urlString = [" + urlString + "], overwrite = [" + overwrite + "]");
        } finally{
            try {
                if(output != null) {
                    output.flush();
                    output.close();
                }
                if(input != null) {
                    input.close();
                }
            } catch (IOException e) {
                Log.e(TAG, " error in closing streams " +"path = [" + path + "], urlString = [" + urlString + "], overwrite = [" + overwrite + "]" );
            }
        }

        return true;
    }
    public static class DownloadFile extends AsyncTask<Void, Void, Void> {

        String path;
        String url;
        boolean overwrite;

        public DownloadFile(String path, String url, boolean overwrite) {
            this.path = path;
            this.url = url;
            this.overwrite = overwrite;
        }

        @Override
        protected Void doInBackground(Void... params) {
            downloadFile(path, url, overwrite);
            return null;
        }
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
    public static Bitmap decodeSampledBitmapFromUri(Context context, Uri uri,
                                                         int reqWidth, int reqHeight) throws FileNotFoundException {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri),null, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, options);
    }

    public static class LoadOnPreDraw implements ViewTreeObserver.OnPreDrawListener{
        private ImageView imageView;
        private Uri imageUri;
        private Context context;

        public LoadOnPreDraw( Context context, ImageView imageView, Uri imageUri) {
            this.imageView = imageView;
            this.imageUri = imageUri;
            this.context = context;
        }

        @Override
        public boolean onPreDraw() {
            imageView.getViewTreeObserver().removeOnPreDrawListener(this);
            int finalHeight = imageView.getMeasuredHeight();
            int finalWidth = imageView.getMeasuredWidth();
            try {
                Logger.d(TAG, String.format("imageview width = %d, height = %d", finalWidth, finalHeight));
                //imageView.setImageBitmap(ImageUtils.decodeSampledBitmapFromUri(context, imageUri, finalWidth, finalHeight));
                Picasso.with(context).load(imageUri).fit().into(imageView);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
    }
}
