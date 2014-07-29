package com.doctl.patientcare.main.om;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 7/28/2014.
 */
public class UserProfile {
    @SerializedName("id")
    private String id;

    @SerializedName("displayName")
    private String displayName;

    @SerializedName("profilePicUrl")
    private String profilePicUrl;

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }
}
