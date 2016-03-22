package com.doctl.patientcare.main.om.chat;

import android.net.Uri;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
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

    private String fileCategory;

    private String fileName;

    private Uri localUri;

    private TransferObserver transferObserver;

    private MessageStatus status=MessageStatus.SENT;

    public Message(UserProfile source, Date timestamp, String text, String fileUrl, String threadId, String thumbnailUrl, MessageStatus status, Uri localUri, String fileCategory) {
        this.source = source;
        this.timestamp = timestamp;
        this.text = text;
        this.fileUrl = fileUrl;
        this.threadId=threadId;
        this.thumbnailUrl=thumbnailUrl;
        this.status=status;
        this.localUri=localUri;
        this.fileCategory = fileCategory;
    }

    public static Message createMessage(String jsonStringMessage) {
        return new Gson().fromJson(jsonStringMessage, Message.class);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UserProfile getSource() {
        return source;
    }

    public void setSource(UserProfile source) {
        this.source = source;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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

    public void setLocalUri(Uri localUri) {
        this.localUri = localUri;
    }

    public TransferObserver getTransferObserver() {
        return transferObserver;
    }

    public void setTransferObserver(TransferObserver transferObserver) {
        this.transferObserver = transferObserver;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileCategory() {
        return fileCategory;
    }

    public void setFileCategory(String fileCategory) {
        this.fileCategory = fileCategory;
    }

    public String statusSymbol() {

        if (transferObserver != null && status != MessageStatus.SENDING) {
            status = MessageStatus.SENDING;
            return statusSymbol();
        }

        if (status == null || status == MessageStatus.SENT) {
            return "";
        } else if (status == MessageStatus.FAILED) {
            return "failed";
        } else {

            String res = "sending";
            if (transferObserver != null) {

                if (transferObserver.getState() == TransferState.IN_PROGRESS) {
                    double fraction = transferObserver.getBytesTransferred() / (double) transferObserver.getBytesTotal();
                    res += " " + (int) (fraction * 100) + "%";
                } else if (transferObserver.getState() == TransferState.FAILED) {
                    status = MessageStatus.FAILED;
                    res = statusSymbol();
                }
            }
            return res;
        }

    }

    public void override(Message otherMessage){

        this.source = otherMessage.source;
        this.timestamp = otherMessage.timestamp;
        this.text = otherMessage.text;
        this.fileUrl = otherMessage.fileUrl;
        this.threadId= otherMessage.threadId;
        this.thumbnailUrl=otherMessage.thumbnailUrl;
        this.status= otherMessage.status;
        this.localUri= otherMessage.localUri;
        this.transferObserver = otherMessage.transferObserver;
        this.fileName = otherMessage.fileName;

    }

    @Override
    public boolean equals(Object o) {
        boolean e = false;
        if (o instanceof Message) {
            Message other = (Message) o;
            if (id != null && other.getId() != null && id.equals(other.getId())) {
                e = true;
            }
        }
        return e;
    }

    public enum MessageStatus {
        SENDING, FAILED, SENT
    }
}
