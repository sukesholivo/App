package com.doctl.patientcare.main.om.chat;

import com.doctl.patientcare.main.om.UserProfile;

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
}
