package com.doctl.patientcare.main.om.chat;

import com.doctl.patientcare.main.om.UserProfile;

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
    private UserProfile user;
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

    private Integer numOfUnreadMessage;


    public void setLatestMessage(Message latestMessage) {
        this.latestMessage = latestMessage;
    }

    public void setNumOfUnreadMessage(int numOfUnreadMessage) {
        this.numOfUnreadMessage = numOfUnreadMessage;
    }


    public Message getLatestMessage() {
        return latestMessage;
    }

    public Integer getNumOfUnreadMessage() {
        return numOfUnreadMessage;
    }

    public UserProfile getUser() {
        return user;
    }

    public void setUser(UserProfile user) {
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
