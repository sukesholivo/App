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
import com.doctl.patientcare.main.services.image.ImageLoader;
import com.doctl.patientcare.main.utility.Constants;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
            convertView = li.inflate(R.layout.question_message_list_item, null);
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

        if(item.getFileUrl() != null && !item.getFileUrl().isEmpty()) {
//            new DownloadImageTask(holder.imgMessage).execute(Constants.SERVER_URL + item.getFileUrl());
            imageLoader.DisplayImage(Constants.SERVER_URL + item.getFileUrl(), R.drawable.profile_dummy, holder.imgMessage);
        }else if (item.getText() != null && !item.getText().isEmpty()) {
            holder.txtMessage.setText(item.getText());
        }

//        String timeStr = new SimpleDateFormat("MMM dd, HH:mm").format(item.getTimestamp());
        holder.txtInfo.setText(convertToDisplayTime(item.getTimestamp()));
        return convertView;
    }

    private String convertToDisplayTime(Date date){

       String result;
        if(date == null){
            return "";
        }
        if( isSameDateByIgnoringTime(date, new Date())){
            result =  new SimpleDateFormat("hh:mm a").format(date);
        }else {
            result = new SimpleDateFormat("MMM dd, hh:mm a").format(date);
        }
        return result;
    }

    private boolean isSameDateByIgnoringTime(Date date1, Date date2){

        Calendar calendar1= Calendar.getInstance(), calendar2=Calendar.getInstance();
        calendar1.setTime(date1);
        calendar2.setTime(date2);

        if( calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(Calendar.DAY_OF_MONTH)  &&
                calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH) &&
                calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR)){
            return true;
        }
        return false;
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
