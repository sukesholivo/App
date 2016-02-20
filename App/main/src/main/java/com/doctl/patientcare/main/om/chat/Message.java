package com.doctl.patientcare.main.om.chat;

import android.net.Uri;

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

    private Uri localUri;

    private MessageStatus status=MessageStatus.SENT;

    public Message(UserProfile source, Date timestamp, String text, String fileUrl, String threadId, String thumbnailUrl, MessageStatus status, Uri localUri) {
        this.source = source;
        this.timestamp = timestamp;
        this.text = text;
        this.fileUrl = fileUrl;
        this.threadId=threadId;
        this.thumbnailUrl=thumbnailUrl;
        this.status=status;
        this.localUri=localUri;
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

    public MessageStatus getStatus() {
        return status;
    }

    public void setStatus(MessageStatus status) {
        this.status = status;
    }

    public Uri getLocalUri() {
        return localUri;
    }

    public static Message createMessage(String jsonStringMessage){
        return  new Gson().fromJson(jsonStringMessage, Message.class);
    }


    public enum MessageStatus{
        SENDING, FAILED, SENT
    }

    public static String statusSymbol(MessageStatus status){
        if(status == null || status == MessageStatus.SENT){
            return "";
        }else if( status == MessageStatus.FAILED){
            return "failed";
        }else{
            return "sending";
        }
    }



}
