package com.doctl.patientcare.main.om.chat;

import com.doctl.patientcare.main.om.UserProfile;

import java.util.Comparator;
import java.util.List;

/**
 * Created by satya on 28/1/16.
 */
public class ThreadSummary {

    /**
     * userId : 3d1e85
     * displayName : name1
     * profilePic : profilePicurl
     * role : role1
     */

    private String id;
    private List<UserProfile> users;
    /**
     * timestamp : 2016-01-14T13:45:42Z
     * fileUrl : null
     * id : 23
     * text : aa
     */

    private Message latestMessage;
    /**
     * user : {"userId":"3d1e85","displayName":"name1","profilePic":"profilePicurl","role":"role1"}
     * latestMessage : {"timestamp":"2016-01-14T13:45:42Z","fileUrl":null,"id":23,"text":"aa"}
     * numOfUnreadMessage : 2
     */

    private List<ReadLog> readLogs;


    public void setLatestMessage(Message latestMessage) {
        this.latestMessage = latestMessage;
    }

    public Message getLatestMessage() {
        return latestMessage;
    }

    public List<UserProfile> getUsers() {
        return users;
    }

    public void setUsers(List<UserProfile> users) {
        this.users = users;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<ReadLog> getReadLogs() {
        return readLogs;
    }

    public void setReadLogs(List<ReadLog> readLogs) {
        this.readLogs = readLogs;
    }

    public class ReadLog{

        /**
         * unreadCount : 14
         * user : {"dob":"1986-02-20","profilePicUrl":"/static/app/img/default_doctor_pic.png","displayName":"Ashwin Venkatesan","id":"f5ebfb6e937d4685bcb33e976d904363","sex":"M"}
         * timestamp : null
         */

        private int unreadCount;

        private UserProfile user;
        private Object timestamp;

        public void setUnreadCount(int unreadCount) {
            this.unreadCount = unreadCount;
        }

        public void setTimestamp(Object timestamp) {
            this.timestamp = timestamp;
        }

        public int getUnreadCount() {
            return unreadCount;
        }

        public Object getTimestamp() {
            return timestamp;
        }

        public UserProfile getUser() {
            return user;
        }

        public void setUser(UserProfile user) {
            this.user = user;
        }
    }

    public static final OrderByLatestMessage ORDER_BY_LATEST_MESSAGE=new OrderByLatestMessage();
    private static class OrderByLatestMessage implements Comparator<ThreadSummary>{
        @Override
        public int compare(ThreadSummary lhs, ThreadSummary rhs) {
            int res=0;
            if( lhs.getLatestMessage() == null && rhs.getLatestMessage() == null){
                return 0;
            }
            if(lhs.getLatestMessage() == null ) {
                return -1;
            }
            if(rhs.getLatestMessage() == null){
                return 1;
            }
            return lhs.getLatestMessage().getTimestamp().compareTo(rhs.getLatestMessage().getTimestamp());
        }
    }
}
