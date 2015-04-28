package com.doctl.patientcare.main.om.documents;

import com.doctl.patientcare.main.om.UserProfile;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by Administrator on 4/27/2015.
 */
public class Document {
    @SerializedName("id")
    private String id;

    @SerializedName("title")
    private String title;

    @SerializedName("fileUrl")
    private String fileUrl;

    @SerializedName("description")
    private String description;

    @SerializedName("timeStamp")
    private Date timeStamp;

    @SerializedName("uploadedBy")
    private UserProfile uploadedBy;

    @SerializedName("isFavorite")
    private boolean isFavorite;

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public String getDescription() {
        return description;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public UserProfile getUploadedBy() {
        return uploadedBy;
    }

    public boolean isFavorite() {
        return isFavorite;
    }
}
