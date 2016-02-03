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

    public Message(UserProfile source, Date timestamp, String text, String fileUrl) {
        this.source = source;
        this.timestamp = timestamp;
        this.text = text;
        this.fileUrl = fileUrl;
    }

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

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", source=" + source +
                ", timestamp=" + timestamp +
                ", text='" + text + '\'' +
                ", fileUrl='" + fileUrl + '\'' +
                '}';
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setSource(UserProfile source) {
        this.source = source;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
}
