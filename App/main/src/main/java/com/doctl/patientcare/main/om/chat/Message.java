package com.doctl.patientcare.main.om.chat;

import com.doctl.patientcare.main.om.UserProfile;
import com.google.gson.Gson;
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

    @SerializedName("threadId")
    private String threadId;

    @SerializedName("thumbnailUrl")
    private String thumbnailUrl;

    public Message(UserProfile source, Date timestamp, String text, String fileUrl, String threadId, String thumbnailUrl) {
        this.source = source;
        this.timestamp = timestamp;
        this.text = text;
        this.fileUrl = fileUrl;
        this.threadId=threadId;
        this.thumbnailUrl=thumbnailUrl;
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

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getThreadId() {
        return threadId;
    }

    public static Message createMessage(String jsonStringMessage){
        return  new Gson().fromJson(jsonStringMessage, Message.class);
    }
}
