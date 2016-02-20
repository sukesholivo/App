package com.doctl.patientcare.main.om.chat;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.doctl.patientcare.main.R;
import com.doctl.patientcare.main.services.DownloadImageTask;
import com.doctl.patientcare.main.services.image.ImageLoader;
import com.doctl.patientcare.main.utility.Constants;
import com.doctl.patientcare.main.utility.DateUtils;

import java.util.List;

/**
 * Created by Administrator on 5/4/2015.
 */
public class MessageListAdapter  extends ArrayAdapter<Message> {
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
        ViewHolder holder;
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
        String userId = item.getSource() == null?sourceId:item.getSource().getId();
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

        if(item.getLocalUri() != null){
            holder.imgMessage.setImageURI(item.getLocalUri());
            new DownloadImageTask(holder.imgMessage, getContext()).execute(item.getLocalUri().toString());
        }
        else if(item.getThumbnailUrl() != null && !item.getThumbnailUrl().isEmpty()) {
              new DownloadImageTask(holder.imgMessage, getContext()).execute(Constants.SERVER_URL + item.getThumbnailUrl());
         //   imageLoader.DisplayImage(Constants.SERVER_URL + item.getThumbnailUrl(), R.drawable.profile_dummy, holder.imgMessage);
        }else{
            holder.imgMessage.setVisibility(View.GONE);
        } if (item.getText() != null && !item.getText().isEmpty()) {
            holder.txtMessage.setText(item.getText());
        }

//        String timeStr = new SimpleDateFormat("MMM dd, HH:mm").format(item.getTimestamp());
        String status = Message.statusSymbol(item.getStatus());
        holder.txtInfo.setText(DateUtils.messageTimeInThread(item.getTimestamp()) +" "+ status);
        return convertView;
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

    private static class ViewHolder {
        public TextView txtMessage;
        public TextView txtInfo;
        public LinearLayout content;
        public LinearLayout contentWithBG;
        public ImageView imgMessage;
    }
}
