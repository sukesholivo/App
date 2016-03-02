package com.doctl.patientcare.main.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

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

    private static LruCache<String, Bitmap> mMemoryCache;

    static {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
    }
    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    private static final String TAG = ImageUtils.class.getSimpleName();

    public static void loadImage(ImageView imageView, String filePath) {
        new LoadImage(imageView, filePath, null, null, null).execute();
    }

    public static void loadImage(ImageView imageView, Context context, Uri uri) {
        new LoadImage(imageView, null, context, uri, null);
    }

    public static void loadImageFromUrl(ImageView imageView, String url) {

        new LoadImage(imageView, null, null, null, url).execute();
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
                File imgFile = new File(filePath);

                if (imgFile.exists()) {
                    bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    Log.d(TAG, "Loading image from path: " + filePath);
                }else{
                    Log.e(TAG, "Not found image path: "+  filePath);
                }
            } else if (uri != null) {
                try {
                    InputStream imageStream = context.getContentResolver().openInputStream(uri);
                    bitmap = BitmapFactory.decodeStream(imageStream);
                    Log.d(TAG, "Loading image from Uri:" + uri);
                } catch (FileNotFoundException e) {
                    Log.e(TAG, "cann't open stream for Uri: " + uri, e);
                }
            } else if (url != null) {
                InputStream in = null;
                try {
                    in = new URL(url).openStream();
                    bitmap = BitmapFactory.decodeStream(in);
                } catch (IOException e) {
                    Log.e(TAG, "failed to load image from:" + url, e);
                }
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
    }

    public static class DownloadFileAndDisplay extends AsyncTask<Void, Void, Void>{

        ImageView imageView;
        String url;
        boolean overwrite;

        public DownloadFileAndDisplay(ImageView imageView, String url, boolean overwrite) {
            this.imageView = imageView;
            this.url = url;
            this.overwrite = overwrite;
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                String localPath = FileUtils.getPathFromUrl(url);
                downloadFile(localPath, url, overwrite);
                ImageUtils.loadImage(imageView, localPath);
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
}
