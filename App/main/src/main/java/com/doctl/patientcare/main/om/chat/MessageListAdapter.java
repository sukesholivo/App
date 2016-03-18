package com.doctl.patientcare.main.om.chat;

import android.content.Context;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.doctl.patientcare.main.R;
import com.doctl.patientcare.main.services.image.ImageLoader;
import com.doctl.patientcare.main.utility.AWSUtils;
import com.doctl.patientcare.main.utility.Constants;
import com.doctl.patientcare.main.utility.DateUtils;
import com.doctl.patientcare.main.utility.FileUtils;
import com.doctl.patientcare.main.utility.ImageUtils;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by Administrator on 5/4/2015.
 */
public class MessageListAdapter extends ArrayAdapter<Message> {
    private final static String TAG = MessageListAdapter.class.getSimpleName();
    String sourceId;
    ImageLoader imageLoader;

    public MessageListAdapter(Context context, List<Message> objects, String sourceId) {
        super(context, 0, objects);
        this.sourceId = sourceId;
        imageLoader = new ImageLoader(getContext());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        Message item = getItem(position);
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = li.inflate(R.layout.thread_message_list_item, null);
            holder = createViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // change it later to get from OM directly
        String userId = item.getSource() == null ? sourceId : item.getSource().getId();
        boolean isMe = userId.equals(sourceId);

        if (isMe) {
            holder.contentWithBG.setBackgroundResource(R.drawable.in_chat);

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.contentWithBG.getLayoutParams();
            layoutParams.gravity = Gravity.END;
            holder.contentWithBG.setLayoutParams(layoutParams);

            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) holder.content.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            holder.content.setLayoutParams(lp);
            layoutParams = (LinearLayout.LayoutParams) holder.txtMessage.getLayoutParams();
            layoutParams.gravity = Gravity.END;
            holder.txtMessage.setLayoutParams(layoutParams);

            layoutParams = (LinearLayout.LayoutParams) holder.txtInfo.getLayoutParams();
            layoutParams.gravity = Gravity.END;
            holder.txtInfo.setLayoutParams(layoutParams);
        } else {
            holder.contentWithBG.setBackgroundResource(R.drawable.out_chat);

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.contentWithBG.getLayoutParams();
            layoutParams.gravity = Gravity.START;
            holder.contentWithBG.setLayoutParams(layoutParams);

            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) holder.content.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            holder.content.setLayoutParams(lp);
            layoutParams = (LinearLayout.LayoutParams) holder.txtMessage.getLayoutParams();
            layoutParams.gravity = Gravity.START;
            holder.txtMessage.setLayoutParams(layoutParams);

            layoutParams = (LinearLayout.LayoutParams) holder.txtInfo.getLayoutParams();
            layoutParams.gravity = Gravity.START;
            holder.txtInfo.setLayoutParams(layoutParams);
        }
        holder.txtMessage.setText("");
        holder.imgMessage.setImageDrawable(null);

        String status = item.statusSymbol();

        if(item.getLocalUri() != null){
            holder.imgMessage.setImageURI(item.getLocalUri());
//            ImageUtils.loadImage(holder.imgMessage, getContext(), item.getLocalUri(), false);
            /*ViewTreeObserver vto = holder.imgMessage.getViewTreeObserver();
            vto.addOnPreDrawListener(new ImageUtils.LoadOnPreDraw(getContext(), holder.imgMessage, item.getLocalUri()));*/
            holder.imgMessage.setVisibility(View.VISIBLE);
        }
        else if(item.getThumbnailUrl() != null && !item.getThumbnailUrl().isEmpty()) {
//            new DownloadImageTask(holder.imgMessage, null).execute(Constants.SERVER_URL + item.getThumbnailUrl());
            //   imageLoader.DisplayImage(Constants.SERVER_URL + item.getThumbnailUrl(), R.drawable.profile_dummy, holder.imgMessage);
            new ImageUtils.DownloadFileAndDisplay(holder.imgMessage, Constants.SERVER_URL + item.getThumbnailUrl(), false, getContext()).execute();
            holder.imgMessage.setVisibility(View.VISIBLE);
        }else{
            holder.imgMessage.setVisibility(View.GONE);
        }
        /*if( item.getLocalUri() != null ){ aws
            ImageUtils.loadImage(holder.imgMessage, getContext(), item.getLocalUri());
        }
        else if (item.getFileName() != null && !item.getFileName().isEmpty()) {
            new LoadImage(holder.imgMessage, item.getFileName()).execute();
        }else {
            holder.imgMessage.setVisibility(View.GONE);
        }*/
        if (item.getText() != null && !item.getText().isEmpty()) {
            holder.txtMessage.setText(item.getText());
        }

//        String timeStr = new SimpleDateFormat("MMM dd, HH:mm").format(item.getTimestamp());

        holder.txtInfo.setText(DateUtils.messageTimeInThread(item.getTimestamp()) + " " + status);
        return convertView;
    }

    private class LoadImage extends AsyncTask<Void, Void, String>{
        ImageView imageView;
        String filePath;

        public LoadImage(ImageView imageView, String filePath) {
            this.imageView = imageView;
            this.filePath = filePath;
        }

        @Override
        protected String doInBackground(Void... params) {
            return  loadImage(imageView, filePath);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s != null){
                ImageUtils.loadImage(imageView, FileUtils.getExternalStorageAbsolutePath(s), getContext(), true);
            }
        }
    }

    private ViewHolder createViewHolder(View v) {
        ViewHolder holder = new ViewHolder();
        holder.txtMessage = (TextView) v.findViewById(R.id.txtMessage);
        holder.imgMessage = (ImageView) v.findViewById(R.id.imgMessage);
        holder.content = (LinearLayout) v.findViewById(R.id.content);
        holder.contentWithBG = (LinearLayout) v.findViewById(R.id.contentWithBackground);
        holder.txtInfo = (TextView) v.findViewById(R.id.txtInfo);
        return holder;
    }

    private void updateImageUpload(ImageView view, TransferObserver transferObserver, String filePath) {
        if (transferObserver.getState() == TransferState.COMPLETED) {
            //ImageUtils.loadImage(view, filePath);
            notifyDataSetChanged();
        }
    }

    /*
    *  order in which image shows
    * 1.local thumbnail folder
    * 2.aws thumbnail bucket
    * 3.local image folder
    * 4.aws image bucket
    *
    * */
    private String loadImage(final ImageView imageView, String filePath) {

        String localFilePath=null;
        String thumbnailPath = Constants.LOCAL_THUMBNAIL_FOLDER + Constants.PATH_SEPERATOR + filePath;
        if (!FileUtils.isFileExist(thumbnailPath)) {

            if (!downloadFromAWSUpdateImageView(imageView, Constants.AWS_THUMBNAIL_BUCKET, filePath, Constants.LOCAL_THUMBNAIL_FOLDER)) {

                String imagePath = Constants.LOCAL_IMAGE_FOLDER + Constants.PATH_SEPERATOR + filePath;

                if (!FileUtils.isFileExist(imagePath)) {

                    downloadFromAWSUpdateImageView(imageView, Constants.AWS_BUCKET_NAME, filePath, Constants.LOCAL_IMAGE_FOLDER);

                }else {
                    localFilePath = imagePath;
                }
            }
        } else {
            localFilePath=thumbnailPath;
        }
        return localFilePath;
    }

    private boolean downloadFromAWSUpdateImageView(final ImageView imageView, String bucketName, String key, String folder) {

        boolean downloaded = false;

        if (AWSUtils.isValidFile(getContext(), bucketName, key)) {

            final AWSUtils.S3FileDownloadResponse s3FileDownloadResponse = AWSUtils.beginDownload(getContext(), bucketName, key, folder);
            final TransferObserver transferObserver = s3FileDownloadResponse.getTransferObserver();

            transferObserver.setTransferListener(new AWSUtils.UploadListener<Integer>(AWSUtils.UploadListener.ON_STATE_CAHNGE, TAG, new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    updateImageUpload(imageView, transferObserver, s3FileDownloadResponse.getFilePathInDevice());
                    return null;
                }
            }));

            downloaded = true;
        }
        return downloaded;
    }

    private static class ViewHolder {
        public TextView txtMessage;
        public TextView txtInfo;
        public LinearLayout content;
        public LinearLayout contentWithBG;
        public ImageView imgMessage;
    }
}
