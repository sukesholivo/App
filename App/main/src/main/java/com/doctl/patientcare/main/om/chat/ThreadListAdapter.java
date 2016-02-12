package com.doctl.patientcare.main.om.chat;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.doctl.patientcare.main.R;
import com.doctl.patientcare.main.om.UserProfile;
import com.doctl.patientcare.main.services.DownloadImageTask;
import com.doctl.patientcare.main.utility.Constants;
import com.doctl.patientcare.main.utility.DateUtils;

import java.util.List;

/**
 * Created by Administrator on 5/4/2015.
 */

public class ThreadListAdapter extends ArrayAdapter<ThreadSummary> {
    String currUserId;
    Context context;
    public ThreadListAdapter(Context context, List<ThreadSummary> objects, String currUserId) {
        super(context, 0, objects);
        this.currUserId=currUserId;
        this.context=context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ThreadSummary item = getItem(position);
        View view = convertView;
        ViewHolder viewHolder =null;
        if (view == null) {
            LayoutInflater li =
                    (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = li.inflate(R.layout.thread_list_item, parent, false);
            viewHolder = new ViewHolder(view);
        }else {
            viewHolder =(ViewHolder) view.getTag();
            if(viewHolder == null){ //todo this should not be null
                viewHolder = new ViewHolder(view);
            }
        }
        viewHolder.updateViewHolder(item);
        return view;
    }

    class ViewHolder{
        ImageView profilePic;
        TextView displayName;
        TextView latestMessageTime;
        TextView latestMessageText;
        ImageView roleImage;
        TextView numOfUnreadMessages;

        public ViewHolder(View view){

            profilePic = (ImageView) view.findViewById(R.id.profile_pic);
            displayName = (TextView) view.findViewById(R.id.display_name);
            latestMessageTime =(TextView) view.findViewById(R.id.latest_message_time);
            latestMessageText =(TextView) view.findViewById(R.id.latest_message_text);
            roleImage = (ImageView) view.findViewById(R.id.role_image);
            numOfUnreadMessages = (TextView) view.findViewById(R.id.num_of_unread_messages);

        }

        public void updateViewHolder( ThreadSummary threadSummary){

            UserProfile userProfile= (threadSummary.getUsers() != null && !threadSummary.getUsers().isEmpty())?threadSummary.getUsers().get(0):null;

            if( userProfile != null) {
                if( userProfile.getProfilePicUrl() != null && !userProfile.getProfilePicUrl().isEmpty()) {
                    new DownloadImageTask(profilePic).execute(Constants.SERVER_URL + userProfile.getProfilePicUrl());
                }
                if(userProfile.getDisplayName() != null && !userProfile.getDisplayName().isEmpty()){
                    displayName.setText(userProfile.getDisplayName());
                }
            }

            Message message = threadSummary.getLatestMessage();

            if(message != null){

                if(message.getText() != null && !message.getText().isEmpty()){
                    latestMessageText.setText(message.getText());
                }

                if( message.getTimestamp() != null ){
                    latestMessageTime.setText(DateUtils.messageTimeForLatestMessage(message.getTimestamp()));
                }
            }

            if(threadSummary.getNumOfUnreadMessage() != null && threadSummary.getNumOfUnreadMessage() != 0){
                numOfUnreadMessages.setText(threadSummary.getNumOfUnreadMessage()+"");
            }else {
                numOfUnreadMessages.setVisibility(View.GONE);
            }

            if(threadSummary.getUsers() != null && !threadSummary.getUsers().isEmpty()){
                UserProfile otherUserProfile = null;
                for(UserProfile userProfile1: threadSummary.getUsers()){
                    if(currUserId != userProfile1.getId()){
                        otherUserProfile=userProfile1;
                        break;
                    }
                }
                if(otherUserProfile != null){

                    String uri = "@drawable/myresource";
                    if("DOCTOR".equalsIgnoreCase(otherUserProfile.getRole())){
                        uri="@drawable/doctor";
                    }else if("LAB".equalsIgnoreCase(otherUserProfile.getRole())){
                        uri="@drawable/lab";
                    }else {
                        uri="@drawable/pharmacy";
                    }
                    int imageResource = context.getResources().getIdentifier(uri, null, context.getPackageName());
                    Drawable res = context.getResources().getDrawable(imageResource);
                    roleImage.setImageDrawable(res);
                }
            }
        }
    }
}
