package com.doctl.patientcare.main.om.chat;

import com.doctl.patientcare.main.om.UserProfile;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 5/4/2015.
 */
public class Thread {
    @SerializedName("id")
    private String id;

    @SerializedName("timestamp")
    private Date timestamp;

    @SerializedName("text")
    private String text;

    @SerializedName("fileUrl")
    private String fileUrl;

    @SerializedName("source")
    private List<UserProfile> userProfiles;

    @SerializedName("messages")
    private List<Message> messages;

    public String getId() {
        return id;
    }

    public List<UserProfile> getUserProfiles() {
        return userProfiles;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public String getText() {
        return text;
    }

    public List<Message> getMessages() {
        return messages;
    }
}
