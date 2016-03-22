package in.olivo.patientcare.main.om.chat;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import in.olivo.patientcare.main.R;
import in.olivo.patientcare.main.om.UserProfile;
import in.olivo.patientcare.main.utility.Constants;
import in.olivo.patientcare.main.utility.DateUtils;
import in.olivo.patientcare.main.utility.ImageUtils;

/**
 * Created by Administrator on 5/4/2015.
 */

public class ThreadListAdapter extends ArrayAdapter<ThreadSummary> {
    String currUserId;
    Context context;

    public ThreadListAdapter(Context context, List<ThreadSummary> objects, String currUserId) {
        super(context, 0, objects);
        this.currUserId = currUserId;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ThreadSummary item = getItem(position);
        View view = convertView;
        ViewHolder viewHolder = null;
        if (view == null) {
            LayoutInflater li =
                    (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = li.inflate(R.layout.thread_list_item, parent, false);
            viewHolder = new ViewHolder(view);
        } else {
            viewHolder = (ViewHolder) view.getTag();
            if (viewHolder == null) { //todo this should not be null
                viewHolder = new ViewHolder(view);
            }
        }
        viewHolder.updateViewHolder(item);
        return view;
    }

    class ViewHolder {
        ImageView profilePic;
        TextView displayName;
        TextView latestMessageTime;
        TextView latestMessageText;
        ImageView roleImage;
        TextView numOfUnreadMessages;

        public ViewHolder(View view) {

            profilePic = (ImageView) view.findViewById(R.id.profile_pic);
            displayName = (TextView) view.findViewById(R.id.display_name);
            latestMessageTime = (TextView) view.findViewById(R.id.latest_message_time);
            latestMessageText = (TextView) view.findViewById(R.id.latest_message_text);
            roleImage = (ImageView) view.findViewById(R.id.role_image);
            numOfUnreadMessages = (TextView) view.findViewById(R.id.num_of_unread_messages);

        }

        public void updateViewHolder(ThreadSummary threadSummary) {

            UserProfile userProfile = (threadSummary.getUsers() != null && !threadSummary.getUsers().isEmpty()) ? UserProfile.getOtherUserProfile(currUserId, threadSummary.getUsers()) : null;

            if (userProfile != null) {
                if (userProfile.getProfilePicUrl() != null && !userProfile.getProfilePicUrl().isEmpty()) {
                    ImageUtils.loadImageFromUrl(getContext(), profilePic, Constants.SERVER_URL + userProfile.getProfilePicUrl(), true);
//                    new DownloadImageTask(profilePic, null).execute(Constants.SERVER_URL + userProfile.getProfilePicUrl());
                }
                if (userProfile.getDisplayName() != null && !userProfile.getDisplayName().isEmpty()) {
                    displayName.setText(userProfile.getDisplayName());
                } else if (userProfile.getPhone() != null && !userProfile.getPhone().isEmpty()) {
                    displayName.setText(userProfile.getPhone());
                }
            }

            Message message = threadSummary.getLatestMessage();

            if (message != null) {

                if (message.getText() != null && !message.getText().isEmpty()) {
                    latestMessageText.setText(message.getText());
                } else if (message.getFileUrl() != null) {
                    latestMessageText.setText("**image**");
                }

                if (message.getTimestamp() != null) {
                    latestMessageTime.setText(DateUtils.messageTimeForLatestMessage(message.getTimestamp()));
                }
            }

            if (threadSummary.getReadLogs() != null && threadSummary.getReadLogs().size() != 0) {
                List<ThreadSummary.ReadLog> readLogs = threadSummary.getReadLogs();
                boolean foundCurrUser = false;
                for (ThreadSummary.ReadLog readLog : readLogs) {
                    if (currUserId.equals(readLog.getUser().getId())) {
                        if (readLog.getUnreadCount() != 0) {
                            numOfUnreadMessages.setText(Integer.toString(readLog.getUnreadCount()));
                            foundCurrUser = true;
                        }
                        break;
                    }
                }
                if (!foundCurrUser) {
                    numOfUnreadMessages.setVisibility(View.GONE);
                }

            } else {
                numOfUnreadMessages.setVisibility(View.GONE);
            }
            if (threadSummary.getUsers() != null && !threadSummary.getUsers().isEmpty()) {

                UserProfile otherUserProfile = UserProfile.getOtherUserProfile(currUserId, threadSummary.getUsers());
                if (otherUserProfile != null) {

                    String uri = "@drawable/myresource";
                    if ("DOCTOR".equalsIgnoreCase(otherUserProfile.getRole())) {
                        uri = "@drawable/doctor";
                    } else if ("LAB".equalsIgnoreCase(otherUserProfile.getRole())) {
                        uri = "@drawable/lab";
                    } else if ("PATIENT".equalsIgnoreCase(otherUserProfile.getRole())) {
                        uri = "@drawable/profile_dummy";
                    } else {
                        uri = "@drawable/pharmacy";
                    }
                    int imageResource = context.getResources().getIdentifier(uri, null, context.getPackageName());
                    Drawable res = context.getResources().getDrawable(imageResource);
                    roleImage.setImageDrawable(res);
                }
            }
        }
    }
}
