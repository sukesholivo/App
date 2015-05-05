package com.doctl.patientcare.main.om.chat;

import com.doctl.patientcare.main.om.UserProfile;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by Administrator on 5/4/2015.
 */
public class Message {
    @SerializedName("id")
    private String id;

    @SerializedName("source")
    private UserProfile source;

    @SerializedName("timestamp")
    private Date timestamp;

    @SerializedName("text")
    private String text;

    @SerializedName("fileUrl")
    private String fileUrl;

    public String getId() {
        return id;
    }

    public UserProfile getSource() {
        return source;
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
}
