package com.doctl.patientcare.main.utility;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;

import java.io.File;
import java.util.Date;
import java.util.concurrent.Callable;

/**
 * Created by Satya Madala on 24/2/16.
 * email : satya.madala@Olivo.in
 */
public class AWSUtils {
    private static final String TAG = AWSUtils.class.getSimpleName();

    // We only need one instance of the clients and credentials provider
    private static AmazonS3Client sS3Client;
    private static CognitoCachingCredentialsProvider sCredProvider;
    private static TransferUtility sTransferUtility;

    /**
     * Gets an instance of CognitoCachingCredentialsProvider which is
     * constructed using the given Context.
     *
     * @param context An Context instance.
     * @return A default credential provider.
     */
    private static CognitoCachingCredentialsProvider getCredProvider(Context context) {
        if (sCredProvider == null) {
            sCredProvider = new CognitoCachingCredentialsProvider(
                    context.getApplicationContext(),
                    Constants.COGNITO_POOL_ID,
                    Regions.US_EAST_1);
        }
        return sCredProvider;
    }

    /**
     * Gets an instance of a S3 client which is constructed using the given
     * Context.
     *
     * @param context An Context instance.
     * @return A default S3 client.
     */
    public static AmazonS3Client getS3Client(Context context) {
        if (sS3Client == null) {
            sS3Client = new AmazonS3Client(getCredProvider(context.getApplicationContext()));
        }
        return sS3Client;
    }

    /**
     * Gets an instance of the TransferUtility which is constructed using the
     * given Context
     *
     * @param context
     * @return a TransferUtility instance
     */
    public static TransferUtility getTransferUtility(Context context) {
        if (sTransferUtility == null) {
            sTransferUtility = new TransferUtility(getS3Client(context.getApplicationContext()),
                    context.getApplicationContext());
        }

        return sTransferUtility;
    }

    public static S3FileUploadResponse beginUpload(Context context, String bucketName, String filePath) {
        if (filePath == null) {
            Toast.makeText(context, "Could not find the filepath of the selected file",
                    Toast.LENGTH_LONG).show();
            return null;
        }
        File file = new File(filePath);
        TransferUtility transferUtility = getTransferUtility(context);
        String fileNameInBucket = new Date().getTime() + "-" + file.getName();

        TransferObserver observer = transferUtility.upload(bucketName, fileNameInBucket,
                file);
        return new S3FileUploadResponse(observer, fileNameInBucket);
       /* observers.add(observer);
        HashMap<String, Object> map = new HashMap<String, Object>();
        Util.fillMap(map, observer, false);
        transferRecordMaps.add(map);
        observer.setTransferListener(new UploadListener());
        simpleAdapter.notifyDataSetChanged();*/
    }

    public static S3FileDownloadResponse beginDownload(Context context, String bucketName, String key, String folder) {
        // Location to download files from S3 to. You can choose any accessible
        // file.
        String fileNameInDevice = FileUtils.getExternalStorageAbsolutePath(folder+Constants.PATH_SEPERATOR+key);
        File file = new File(fileNameInDevice);
        File parentDir = file.getParentFile();

        if(!parentDir.exists()){
            if( !parentDir.mkdirs()){
                Log.d(TAG, "can not create dir: "+ parentDir.getAbsolutePath());
            }
        }

        TransferObserver observer = null;
        TransferUtility transferUtility = getTransferUtility(context);
        ;
        observer = transferUtility.download(bucketName, key, file);
        // Initiate the download

        return new S3FileDownloadResponse(observer, fileNameInDevice);
       /* // Add the new download to our list of TransferObservers
        observers.add(observer);
        HashMap<String, Object> map = new HashMap<String, Object>();
        // Fill the map with the observers data
        Util.fillMap(map, observer, false);
        // Add the filled map to our list of maps which the simple adapter uses
        transferRecordMaps.add(map);
        observer.setTransferListener(new DownloadListener());
        simpleAdapter.notifyDataSetChanged();*/
    }

    public static boolean isValidFile(Context context,
                                      String bucketName,
                                      String key) throws AmazonClientException, AmazonServiceException {
        AmazonS3 s3 = getS3Client(context);

        boolean isValidFile = true;
        try {
            ObjectMetadata objectMetadata = s3.getObjectMetadata(bucketName, key);
        } catch (AmazonS3Exception s3e) {
            if (s3e.getStatusCode() == 404) {
                // i.e. 404: NoSuchKey - The specified key does not exist
                isValidFile = false;
            } else {
                Log.e(TAG, String.format(" failed to check: validFile key: %s bucket %s", key, bucketName), s3e);
                //throw s3e;    // rethrow all S3 exceptions other than 404
            }
        }
        Log.d(TAG, String.format("key: %s in bucket: %s found: "+ isValidFile, key, bucketName));

        return isValidFile;
    }

    public static class S3FileUploadResponse {
        private TransferObserver transferObserver;
        private String filePathInBucket;

        public S3FileUploadResponse(TransferObserver transferObserver, String filePathInBucket) {
            this.transferObserver = transferObserver;
            this.filePathInBucket = filePathInBucket;
        }

        public TransferObserver getTransferObserver() {
            return transferObserver;
        }

        public String getFilePathInBucket() {
            return filePathInBucket;
        }
    }

    public static class S3FileDownloadResponse {
        private TransferObserver transferObserver;
        private String filePathInDevice;

        public S3FileDownloadResponse(TransferObserver transferObserver, String filePathInDevice) {
            this.transferObserver = transferObserver;
            this.filePathInDevice = filePathInDevice;
        }

        public TransferObserver getTransferObserver() {
            return transferObserver;
        }

        public String getFilePathInDevice() {
            return filePathInDevice;
        }
    }

    public static class UploadListener<V> implements TransferListener {

        private final String TAG;
        private Callable<V> updateFunction;
        int callback;
        public static final int ON_ERROR = 0x1, ON_STATE_CAHNGE = 0x2, ON_PREGRESS_CHANGE = 0x4;

        public UploadListener(int callback, String TAG, Callable<V> updateFunction) {
            this.TAG = TAG;
            this.updateFunction = updateFunction;
            this.callback=callback;
        }

        // Simply updates the UI list when notified.
        private void update() {
            try {
                updateFunction.call();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

        @Override
        public void onError(int id, Exception e) {
            Log.e(TAG, "Error during : " + id, e);
            if((callback & ON_ERROR) != 0) {
                update();
            }
        }

        @Override
        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
            Log.d(TAG, String.format("onProgressChanged: %d, total: %d, current: %d",
                    id, bytesTotal, bytesCurrent));
            if( (callback & ON_PREGRESS_CHANGE) != 0) {
                update();
            }
        }

        @Override
        public void onStateChanged(int id, TransferState newState) {
            Log.d(TAG, "onStateChanged: " + id + ", " + newState);
            if( (callback & ON_STATE_CAHNGE) != 0) {
                update();
            }
        }
    }
}
